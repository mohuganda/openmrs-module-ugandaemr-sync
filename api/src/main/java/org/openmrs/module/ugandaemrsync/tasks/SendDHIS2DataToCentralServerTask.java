package org.openmrs.module.ugandaemrsync.tasks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.ugandaemrsync.server.SyncGlobalProperties;
import org.openmrs.module.ugandaemrsync.server.UgandaEMRHttpURLConnection;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.openmrs.ui.framework.SimpleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.openmrs.module.ugandaemrsync.UgandaEMRSyncConfig.GP_DHIS2_SERVER_URL;
import static org.openmrs.module.ugandaemrsync.UgandaEMRSyncConfig.GP_DHIS2_SERVER_USERNAME;

/**
 * Posts DHIS 2 data data to the central server
 */

@Component
public class SendDHIS2DataToCentralServerTask extends AbstractTask {

<<<<<<< HEAD
	protected Log log = LogFactory.getLog(getClass());
	protected byte[] data ;
	SimpleObject simpleObject;

	public SendDHIS2DataToCentralServerTask() {}
=======
    protected Log log = LogFactory.getLog(getClass());
    SyncGlobalProperties syncGlobalProperties = new SyncGlobalProperties();
    protected byte[] data;
    SimpleObject serverResponseObject;
>>>>>>> 5c42d52536617b671d9180a686b090aebe756668

    public SimpleObject getServerResponseObject() {
        return serverResponseObject;
    }

<<<<<<< HEAD
	UgandaEMRHttpURLConnection ugandaEMRHttpURLConnection = new UgandaEMRHttpURLConnection();
=======
    UgandaEMRHttpURLConnection ugandaEMRHttpURLConnection = new UgandaEMRHttpURLConnection();

>>>>>>> 5c42d52536617b671d9180a686b090aebe756668

    public SendDHIS2DataToCentralServerTask() {
    }

    public SendDHIS2DataToCentralServerTask(byte[] data, SimpleObject simpleObject) {
        this.data = data;
        this.serverResponseObject = simpleObject;
    }

    @Autowired
    @Qualifier("reportingReportDefinitionService")
    protected ReportDefinitionService reportingReportDefinitionService;


    public void execute() {
        Map map = new HashMap();
        final String url = "https://ugisl.mets.or.ug/ehmis";

        int responseCode = 0;
        String baseUrl = ugandaEMRHttpURLConnection.getBaseURL(syncGlobalProperties.getGlobalProperty(GP_DHIS2_SERVER_URL));
        String baseUsername = ugandaEMRHttpURLConnection.getBaseURL(syncGlobalProperties.getGlobalProperty(GP_DHIS2_SERVER_USERNAME));
        if (isBlank(url)) {
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
        try {
			HttpResponse httpResponse = ugandaEMRHttpURLConnection.httpPost(url, bodyText, "mets.mkaye");

			responseCode = httpResponse.getStatusLine().getStatusCode();
			String responseMessage = httpResponse.getStatusLine().getReasonPhrase();

			if ((responseCode == 200 || responseCode == 201)) {
				InputStream inputStreamReader = httpResponse.getEntity().getContent();
				map = getMapOfResults(inputStreamReader, responseCode);
			} else {
				map.put("responseCode", responseCode);
				log.info(responseMessage);
			}
			map.put("responseMessage", responseMessage);


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
		}catch (IOException e){
        	log.error(e);
		}
        ObjectMapper objectMapper=new ObjectMapper();
		try {
			serverResponseObject = SimpleObject.create("message", objectMapper.writeValueAsString(map));
		} catch (IOException e) {
			e.printStackTrace();
		}
		serverResponseObject.put("responsedata",responseCode);
    }

	public Map getMapOfResults(InputStream inputStreamReader, int responseCode) throws IOException {
		Map map = new HashMap();
		InputStreamReader reader = new InputStreamReader(inputStreamReader);
		StringBuilder buf = new StringBuilder();
		char[] cbuf = new char[2048];
		int num;
		while (true) {
			if (!(-1 != (num = reader.read(cbuf)))) break;
			buf.append(cbuf, 0, num);
		}
		String result = buf.toString();
		ObjectMapper mapper = new ObjectMapper();
		if (isJSONValid(result)) {
			map = mapper.readValue(result, Map.class);
		}
		map.put("responseCode", responseCode);
		return map;
	}

	public boolean isJSONValid(String test) {
		try {
			new JSONObject(test);
		} catch (JSONException ex) {
			try {
				new JSONArray(test);
			} catch (JSONException ex1) {
				return false;
			}
		}
		return true;
	}
}
