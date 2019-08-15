package org.openmrs.module.ugandaemrsync.tasks;

import net.sf.ehcache.transaction.xa.EhcacheXAException;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.ugandaemrreports.reports.SetUpHTSClientCardDataExportReport2019;
import org.openmrs.module.ugandaemrsync.UgandaEMRSyncConfig;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

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
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import java.util.ArrayList;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

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
	private SetUpHTSClientCardDataExportReport2019 htsReportManager;
	
	@Override
    public void execute() {
        log.info("Sending recency data to central server ");
        //SyncGlobalProperties syncGlobalProperties = new SyncGlobalProperties();
        //String recencyServerUrl = syncGlobalProperties.getGlobalProperty(UgandaEMRSyncConfig.RECENCY_UPLOADS_SERVER_URL);
        String recencyServerUrl = UgandaEMRSyncConfig.RECENCY_SERVER_URL;
        String testUrl = recencyServerUrl.substring(recencyServerUrl.indexOf("https://"), recencyServerUrl.indexOf("recency"));
        try {
            // TODO: Add code to verify if there is internet connection and if MIRTH Server is available (log this if not)
            // Data Successfully uploaded

            //Check internet connectivity
            if (!ugandaEMRHttpURLConnection.isConnectionAvailable()){
                System.out.println("Server is not available!!");
                return;
            }
            //Check destination server availability
            if (!ugandaEMRHttpURLConnection.isServerAvailable(testUrl)){
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
//                String bodyText = getResponseBody();

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
		
		try {
			EvaluationContext context = new EvaluationContext();
			context.addParameterValue("startDate", DateUtil.parseDate("2018-11-01", "yyyy-MM-dd"));
			context.addParameterValue("endDate", DateUtil.parseDate("2018-11-30", "yyyy-MM-dd"));
			
			ReportDefinition reportDefinition = htsReportManager.constructReportDefinition();
			ReportData reportData = reportingReportDefinitionService.evaluate(reportDefinition, context);
			
			System.out.println(reportData.toString());
			return reportData.toString();
		}
		catch (Exception e) {
			log.info(e.toString());
		}
		return "";
	}
	
	public String getRecencyDataFromREST() throws Exception {
		Context.startup(
		    "jdbc:mysql://localhost:3306/ugandaemr?autoReconnect=true&sessionVariables=storage_engine%3DInnoDB&useUnicode=true&characterEncoding=UTF-8",
		    "openmrs", "openmrs", new Properties());
		Context.openSession();
		Context.authenticate("admin", "Admin123");
		
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet(
		        "http://localhost:8000/openmrs/module/reporting/reports/renderReport.form?reportDefinition=96e0926d-1606-4de6-943f-cb036bdc15ad&renderingMode=org.openmrs.module.reporting.report.renderer.CsvReportRenderer!7c8301e3-439e-4084-a9f1-dad1a2b6e3c3");
		// HttpGet get1 = new HttpGet("http://localhost:8000/openmrs/ws/rest/v1/concept?limit=2");
		UsernamePasswordCredentials getCredentials = new UsernamePasswordCredentials("admin", "Admin123");
		get.addHeader(new BasicScheme().authenticate(getCredentials, get, null));
		HttpResponse response = client.execute(get);
		HttpEntity responseEntity = response.getEntity();
		String respStr = EntityUtils.toString(responseEntity, "UTF-8");
		
		System.out.println(respStr.toString());
		
		Context.closeSession();
		
		return "TEST";
	}
	
	//	public String getResponseBody() {
	//		Context context = new Context();
	//		try {
	//			context.startup(
	//			    "jdbc:mysql://localhost:3306/ugandaemr?autoReconnect=true&sessionVariables=storage_engine%3DInnoDB&useUnicode=true&characterEncoding=UTF-8",
	//			    "openmrs", "openmrs", new Properties());
	//			HttpClient client = HttpClientBuilder.create().build();
	//			HttpGet get = new HttpGet(
	//			        "http://localhost:8000/openmrs/module/reporting/reports/renderReport.form?reportDefinition=96e0926d-1606-4de6-943f-cb036bdc15ad&renderingMode=org.openmrs.module.reporting.report.renderer.CsvReportRenderer!7c8301e3-439e-4084-a9f1-dad1a2b6e3c3");
	//			UsernamePasswordCredentials getCredentials = new UsernamePasswordCredentials("admin", "Admin123");
	//			get.addHeader(new BasicScheme().authenticate(getCredentials, get, null));
	//			context.authenticate("admin", "Admin123");
	//			HttpResponse response = client.execute(get);
	//			HttpEntity entity = response.getEntity();
	//
	//			String responseString = EntityUtils.toString(entity, "UTF-8");
	//			System.out.println(responseString);
	//			return responseString;
	//		}
	//		catch (Exception e) {
	//			log.info(e.toString());
	//			System.out.println("getResponseBody Exception" + e.toString());
	//		}
	//
	//		return "";
	//	}
	//
	public void renderReport() {
		HttpResponse response;
		ReportDefinitionService reportDefinitionService = Context.getService(ReportDefinitionService.class);
		
		try {
			ReportDefinition rd = reportDefinitionService.getDefinitionByUuid("96e0926d-1606-4de6-943f-cb036bdc15ad");
			if (rd == null) {
				throw new IllegalArgumentException(
				        "Unable to find report with passed uuid = 96e0926d-1606-4de6-943f-cb036bdc15ad");
			}
			RenderingMode renderingMode = new RenderingMode(
			        "org.openmrs.module.reporting.report.renderer.CsvReportRenderer!7c8301e3-439e-4084-a9f1-dad1a2b6e3c3");
			if (!renderingMode.getRenderer().canRender(rd)) {
				throw new IllegalArgumentException("Rendering mode chosen cannot render passed report definition");
			}
			
			EvaluationContext context = new EvaluationContext();
			
			// If the report takes in additional parameters, try to retrieve these from the request
			
			//			for (Parameter p : rd.getParameters()) {
			//				String[] parameterValues = request.getParameterValues(p.getName());
			//				if (parameterValues != null && parameterValues.length > 0) {
			//					Object value = null;
			//					if (parameterValues.length == 1) {
			//						value = WidgetUtil.parseInput(parameterValues[0], p.getType(), p.getCollectionType());
			//					} else {
			//						List l = new ArrayList();
			//						for (String v : parameterValues) {
			//							l.add(WidgetUtil.parseInput(parameterValues[0], p.getType()));
			//						}
			//						value = l;
			//					}
			//					context.addParameterValue(p.getName(), value);
			//				}
			//			}
			
			ReportData reportData = reportDefinitionService.evaluate(rd, context);
			
			ReportRequest reportRequest = new ReportRequest();
			reportRequest.setReportDefinition(new Mapped<ReportDefinition>(rd, context.getParameterValues()));
			reportRequest.setRenderingMode(renderingMode);
			String contentType = renderingMode.getRenderer().getRenderedContentType(reportRequest);
			String fileName = renderingMode.getRenderer().getFilename(reportRequest);
			
			//response.setHeader("Content-Type", contentType);
			
			//			if (download) {
			//				response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			//			}
			//
			System.out.println(reportData.toString());
			//renderingMode.getRenderer().render(reportData, renderingMode.getArgument(), response.getEntity());
		}
		catch (Exception e) {
			//			e.printStackTrace(response.getWriter());
		}
	}
}
