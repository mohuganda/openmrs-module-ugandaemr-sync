package org.openmrs.module.ugandaemrsync.tasks;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.ugandaemrreports.reports.SetUpHTCDataExportReport;
import org.openmrs.module.ugandaemrsync.UgandaEMRSyncConfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.openmrs.module.ugandaemrsync.server.SyncGlobalProperties;
import org.openmrs.module.ugandaemrsync.server.UgandaEMRHttpURLConnection;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.util.Date;

/**
 * Posts recency data to the central server
 */

public class SendRecencyDataToCentralServerTask extends AbstractTask {
	
	protected Log log = LogFactory.getLog(getClass());
	
	UgandaEMRHttpURLConnection ugandaEMRHttpURLConnection = new UgandaEMRHttpURLConnection();
	
	//TODO: use syncGlobalProperties once it has been persisted on ugandaEMR database
	// SyncGlobalProperties syncGlobalProperties = new SyncGlobalProperties();
	
	@Autowired
	@Qualifier("reportingReportDefinitionService")
	protected ReportDefinitionService reportingReportDefinitionService;
	
	@Autowired
	private SetUpHTCDataExportReport reportManager;
	
	@Override
    public void execute() {
        log.info("Sending recency data to central server ");
        SyncGlobalProperties syncGlobalProperties = new SyncGlobalProperties();
        //String recencyServerUrl = syncGlobalProperties.getGlobalProperty(UgandaEMRSyncConfig.RECENCY_UPLOADS_SERVER_URL);
        String recencyServerUrl = UgandaEMRSyncConfig.RECENCY_SERVER_URL;
        try {
            // TODO: Add code to verify if there is internet connection and if MIRTH Server is available (log this if not)
            // Data Successfully uploaded

            //Check internet connectivity
            if (!ugandaEMRHttpURLConnection.isConnectionAvailable()){
                System.out.println("Server is not available!!");
                return;
            }
            //Check destination server availability
            if (!ugandaEMRHttpURLConnection.isServerAvailable(recencyServerUrl.substring(recencyServerUrl.indexOf("https://"), recencyServerUrl.indexOf("recency")))){
                return;
            }

                HttpClient client = HttpClientBuilder.create().build();
                HttpPost post = new HttpPost(recencyServerUrl);
                // HttpPost post = new HttpPost(Str+syncGlobalProperties.getGlobalProperty(UgandaEMRSyncConfig.recencyServerUrl));
                post.addHeader(UgandaEMRSyncConfig.HEADER_EMR_DATE, new Date().toString());

                UsernamePasswordCredentials credentials
                        = new UsernamePasswordCredentials(UgandaEMRSyncConfig.RECENCY_SERVER_USERNAME, UgandaEMRSyncConfig.RECENCY_SERVER_PASSWORD);
                post.addHeader(new BasicScheme().authenticate(credentials, post, null));

                String bodyText = getRecencyData();

                HttpEntity multipart = MultipartEntityBuilder.create()
                        .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                        .addTextBody("facility_uuid", UgandaEMRSyncConfig.DHIS2_ORGANIZATION_UUID)
                        //TODO: Uncomment below to replace above when ready for production
                        //.addTextBody("dhis2_organization_uuid", syncGlobalProperties.getGlobalProperty(UgandaEMRSyncConfig.DHIS2_ORGANIZATION_UUID))
                        .addTextBody("data", bodyText, ContentType.TEXT_PLAIN) // Current implementation uses plain text due to decoding challenges on the receiving server.
                        .build();
                post.setEntity(multipart);

                HttpResponse response = client.execute(post);

                log.info(response.toString());

        } catch (IOException | AuthenticationException e) {
            log.info("Exception sending Recency data "+ e.getMessage());
        }

    }
	
	public String getRecencyData() {
		//return "patient_id, patient_creator, encounter_id, gravida, para$$$248,10,0,0,0$$$334,10,0,0,0$$$336,10,0,0,0$$$401,7,0,0,0$$$232,5,0,0,0$$$248,10,0,0,0$$$334,10,0,0,0$$$336,10,0,0,0$$$401,7,0,0,0$$$232,5,0,0,0$$$248,10,0,0,0$$$334,10,0,0,0$$$336,10,0,0,0$$$401,7,0,0,0$$$232,5,0,0,0$$$248,10,0,0,0$$$334,10,0,0,0$$$336,10,0,0,0$$$401,7,0,0,0$$$232,5,0,0,0$$$248,10,0,0,0$$$334,10,0,0,0$$$336,10,0,0,0$$$401,7,0,0,0";
		ReportDefinition reportDefinition = reportManager.constructReportDefinition();
		ReportData reportData = new ReportData();
		try {
			EvaluationContext context = new EvaluationContext();
			context.addParameterValue("startDate", DateUtil.parseDate("2018-11-01", "yyyy-MM-dd"));
			context.addParameterValue("endDate", DateUtil.parseDate("2018-11-30", "yyyy-MM-dd"));
			
			reportData = reportingReportDefinitionService.evaluate(reportDefinition, context);
			
			System.out.println(reportData.toString());
			return reportData.toString();
			
		}
		catch (Exception e) {
			log.info(e.toString());
		}
		return reportData.toString();
	}
}
