package org.openmrs.module.ugandaemrsync.tasks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.ugandaemrsync.server.SyncGlobalProperties;
import org.openmrs.module.ugandaemrsync.server.UgandaEMRHttpURLConnection;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.openmrs.ui.framework.SimpleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.openmrs.module.ugandaemrsync.UgandaEMRSyncConfig.*;

/**
 * Posts DHIS 2 data data to the central server
 */

@Component
public class SendDHIS2DataToCentralServerTask extends AbstractTask  {

	protected Log log = LogFactory.getLog(getClass());
	SyncGlobalProperties syncGlobalProperties = new SyncGlobalProperties();
	protected byte[] data ;
	SimpleObject serverResponseObject;

	public SimpleObject getServerResponseObject(){
		return serverResponseObject;
	}
	UgandaEMRHttpURLConnection ugandaEMRHttpURLConnection = new UgandaEMRHttpURLConnection();


	public SendDHIS2DataToCentralServerTask() {}

	public SendDHIS2DataToCentralServerTask(byte[] data, SimpleObject simpleObject) {
		this.data = data;
		this.serverResponseObject= simpleObject;
	}

	@Autowired
	@Qualifier("reportingReportDefinitionService")
	protected ReportDefinitionService reportingReportDefinitionService;


	public void execute() {
		Map map = new HashMap();

		String baseUrl = ugandaEMRHttpURLConnection.getBaseURL(syncGlobalProperties.getGlobalProperty(GP_DHIS2_SERVER_URL));
		if (isBlank(syncGlobalProperties.getGlobalProperty(GP_DHIS2_SERVER_URL))) {
			log.error("DHIS 2 server URL is not set");
			ugandaEMRHttpURLConnection
					.setAlertForAllUsers("DHIS 2 server URL is not set please go to admin then Settings then Ugandaemrsync and set it");
			return;
		}

		//Check internet connectivity
		if (!ugandaEMRHttpURLConnection.isConnectionAvailable()) {
			return;
		}

		//Check destination server availability
		if (!ugandaEMRHttpURLConnection.isServerAvailable(baseUrl)) {
			return;
		}

		log.error("Sending DHIS2 data to central server ");
		String bodyText = new String(this.data);
		HttpResponse httpResponse = ugandaEMRHttpURLConnection.httpPost(syncGlobalProperties.getGlobalProperty(GP_DHIS2_SERVER_URL), bodyText,syncGlobalProperties.getGlobalProperty(GP_DHIS2_SERVER_USERNAME),syncGlobalProperties.getGlobalProperty(GP_DHIS2_SERVER_PASSWORD));

		if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			String output1 = httpResponse.getStatusLine().getReasonPhrase();
			map.put("responseCode", output1);
			log.error("DHIS2 data has been sent to central server");
		} else {
			log.error("Data Has not been sent to DHIS2: " + httpResponse.getStatusLine().getStatusCode() + ". Reason: "
					+ httpResponse.getStatusLine().getReasonPhrase());
			ugandaEMRHttpURLConnection.setAlertForAllUsers("Data Has not been sent to DHIS2: "
					+ httpResponse.getStatusLine().getStatusCode() + " " + httpResponse.getStatusLine().getReasonPhrase()
					+ " error");
			map.put("responseCode", httpResponse.getStatusLine().getStatusCode());
		}
		serverResponseObject=SimpleObject.create("message",httpResponse.getStatusLine().getReasonPhrase());
		//serverResponseObject.put("responsedata",responseCode);
	}
}