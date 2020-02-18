package org.openmrs.module.ugandaemrsync.tasks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.openmrs.module.ugandaemrsync.server.SyncGlobalProperties;
import org.openmrs.module.ugandaemrsync.server.UgandaEMRHttpURLConnection;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.openmrs.module.ugandaemrsync.UgandaEMRSyncConfig.*;

/**
 * Posts Analytics data to the central server
 */

@Component
public class SendAnalyticsDataToCentralServerTask extends AbstractTask {
	
	protected Log log = LogFactory.getLog(getClass());
	
	UgandaEMRHttpURLConnection ugandaEMRHttpURLConnection = new UgandaEMRHttpURLConnection();
	
	SyncGlobalProperties syncGlobalProperties = new SyncGlobalProperties();
	
	@Autowired
	@Qualifier("reportingReportDefinitionService")
	protected ReportDefinitionService reportingReportDefinitionService;
	
	@Override
	public void execute() {
		Date todayDate = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		if (!isGpAnalyticsServerUrlSet()) {
			return;
		}
		if (!isGpDhis2OrganizationUuidSet()) {
			return;
		}

		if (!isGpAnalyticsServerPasswordSet()) {
			return;
		}

		String analyticsServerUrlEndPoint = syncGlobalProperties.getGlobalProperty(GP_ANALYTICS_SERVER_URL);
		String analyticsBaseUrl = ugandaEMRHttpURLConnection.getBaseURL(analyticsServerUrlEndPoint);

		String strSubmissionDate = Context.getAdministrationService()
		        .getGlobalPropertyObject(GP_ANALYTICS_TASK_LAST_SUCCESSFUL_SUBMISSION_DATE).getPropertyValue();

		String strSubmitOnceDaily = Context.getAdministrationService()
		        .getGlobalPropertyObject(GP_SUBMIT_RECENCY_DATA_ONCE_DAILY).getPropertyValue();

		if (!isBlank(strSubmissionDate)) {
			Date gpSubmissionDate = null;
			try {
				gpSubmissionDate = new SimpleDateFormat("yyyy-MM-dd").parse(strSubmissionDate);
			}
			catch (ParseException e) {
				log.error("Error parsing last successful submission date " + strSubmissionDate + e);
				e.printStackTrace();
			}
			if (dateFormat.format(gpSubmissionDate).equals(dateFormat.format(todayDate))
			        && strSubmitOnceDaily.equals("true")) {
				log.error("Last successful submission was on" + strSubmissionDate
				        + " and once data submission daily is set as " + strSubmitOnceDaily
				        + "so this task will not run again today. If you need to send data, run the task manually."
				        + System.lineSeparator());
				return;
			}
		}
		//Check internet connectivity
		if (!ugandaEMRHttpURLConnection.isConnectionAvailable()) {
			return;
		}

		//Check destination server availability
		if (!ugandaEMRHttpURLConnection.isServerAvailable(analyticsBaseUrl)) {
			return;
		}
		log.error("Sending analytics data to central server ");
		String bodyText = getAnalyticsDataExport();
		HttpResponse httpResponse = ugandaEMRHttpURLConnection.httpPost(analyticsServerUrlEndPoint, bodyText,"mets.mkaye");
		if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

			ReportUtil.updateGlobalProperty(GP_ANALYTICS_TASK_LAST_SUCCESSFUL_SUBMISSION_DATE,
			    dateTimeFormat.format(todayDate));
			log.error("Analytics data has been sent to central server");
		} else {
			log.error("Http response status code: " + httpResponse.getStatusLine().getStatusCode() + ". Reason: "
			        + httpResponse.getStatusLine().getReasonPhrase());
			ugandaEMRHttpURLConnection.setAlertForAllUsers("Http request has returned a response status: "
			        + httpResponse.getStatusLine().getStatusCode() + " " + httpResponse.getStatusLine().getReasonPhrase()
			        + " error");
		}
	}
	
	private String getAnalyticsDataExport() {
		ReportDefinitionService reportDefinitionService = Context.getService(ReportDefinitionService.class);
		String strOutput = new String();
		
		try {
			ReportDefinition rd = reportDefinitionService.getDefinitionByUuid(ANALYTICS_DATA_EXPORT_REPORT_DEFINITION_UUID);
			if (rd == null) {
				throw new IllegalArgumentException("unable to find Analytics Data Export report with uuid "
				        + ANALYTICS_DATA_EXPORT_REPORT_DEFINITION_UUID);
			}
			String reportRendergingMode = JSON_REPORT_RENDERER_TYPE + "!" + ANALYTIC_REPORT_JSON_DESIGN_UUID;
			RenderingMode renderingMode = new RenderingMode(reportRendergingMode);
			if (!renderingMode.getRenderer().canRender(rd)) {
				throw new IllegalArgumentException("Unable to render Analytics Data Export with " + reportRendergingMode);
			}
			
			EvaluationContext context = new EvaluationContext();
			ReportData reportData = reportDefinitionService.evaluate(rd, context);
			ReportRequest reportRequest = new ReportRequest();
			reportRequest.setReportDefinition(new Mapped<ReportDefinition>(rd, context.getParameterValues()));
			reportRequest.setRenderingMode(renderingMode);
			File file = new File(OpenmrsUtil.getApplicationDataDirectory() + ANALYTICS_JSON_FILE_NAME);
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			renderingMode.getRenderer().render(reportData, renderingMode.getArgument(), fileOutputStream);
			
			strOutput = this.readOutputFile(strOutput);
		}
		catch (Exception e) {
			log.error("Error rendering the contents of the Analytics data export report to"
			        + OpenmrsUtil.getApplicationDataDirectory() +  ANALYTICS_JSON_FILE_NAME + e.toString());
		}
		
		return strOutput;
	}
	
	/*
	Method: readOutputFile
	Pre condition: empty strOutput initialized
	Description:
		Read the analytics exported report file in csv
		Create a string and prefix the dhis2_orgunit_uuid
		and encounter_uuid columns to the final output
	Post condition: strOutput assigned with csv file data prefixed with two additional columns
	* */
	
	public String readOutputFile(String strOutput) throws Exception {
		SyncGlobalProperties syncGlobalProperties = new SyncGlobalProperties();
		FileInputStream fstreamItem = new FileInputStream(OpenmrsUtil.getApplicationDataDirectory() +  ANALYTICS_JSON_FILE_NAME);
		DataInputStream inItem = new DataInputStream(fstreamItem);
		BufferedReader brItem = new BufferedReader(new InputStreamReader(inItem));
		String phraseItem;
		
		if (!(phraseItem = brItem.readLine()).isEmpty()) {
			strOutput = strOutput  + phraseItem + System.lineSeparator();
			while ((phraseItem = brItem.readLine()) != null) {
				strOutput = strOutput +  phraseItem + System.lineSeparator();
			}
		}
		
		fstreamItem.close();
		
		return strOutput;
	}
	
	public boolean isGpAnalyticsServerUrlSet() {
		if (isBlank(syncGlobalProperties.getGlobalProperty(GP_ANALYTICS_SERVER_URL))) {
			log.error("Analytics server URL is not set");
			ugandaEMRHttpURLConnection
			        .setAlertForAllUsers("Analytics server URL is not set please go to admin then Settings then Ugandaemrsync and set it");
			return false;
		}
		return true;
	}
	
	public boolean isGpDhis2OrganizationUuidSet() {
		if (isBlank(syncGlobalProperties.getGlobalProperty(GP_DHIS2_ORGANIZATION_UUID))) {
			log.error("DHIS2 Organization UUID is not set");
			ugandaEMRHttpURLConnection
			        .setAlertForAllUsers("DHIS2 Organization UUID is not set please go to admin then Settings then Ugandaemr and set it");
			return false;
		}
		return true;
	}
	
	public boolean isGpAnalyticsServerPasswordSet() {
		if (isBlank(syncGlobalProperties.getGlobalProperty(GP_ANALYTICS_SERVER_PASSWORD))) {
			log.error("Analytics server URL is not set");
			ugandaEMRHttpURLConnection
			        .setAlertForAllUsers("Analytics server password is not set please go to admin then Settings then Ugandaemrsync and set it");
			return false;
		}
		return true;
	}
}
