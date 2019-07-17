package org.openmrs.module.ugandaemrsync.server;

/**
 * Created by lubwamasamuel on 11/10/16.
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ugandaemrsync.UgandaEMRSyncConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import static org.openmrs.module.ugandaemrsync.server.SyncConstant.HEALTH_CENTER_SYNC_ID;

public class UgandaEMRHttpURLConnection {
	
	public UgandaEMRHttpURLConnection() {
	}
	
	private final String USER_AGENT = "Mozilla/5.0";
	protected Log log = LogFactory.getLog(getClass());
	
	// HTTP GET request
	public HttpURLConnection sendGet(String content, String protocol) throws Exception {
		
		URL obj = new URL(protocol + content);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		
		// optional default is GET
		con.setRequestMethod("GET");
		
		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);
		
		return con;
		
	}
	
	public int getCheckConnection(String url) throws Exception {
		return sendGet(url, SyncConstant.SERVER_PROTOCOL_PLACE_HOLDER).getResponseCode();
	}
	
	public StringBuffer getResponseString(BufferedReader bufferedReader) throws IOException {
		String inputLine;
		
		StringBuffer response = new StringBuffer();
		
		while ((inputLine = bufferedReader.readLine()) != null) {
			response.append(inputLine);
		}
		return response;
	}
	
	// HTTP POST request
	
	public Map sendPostBy(String contentType, String content, String facilityId, String url) throws Exception {
		
		try {
			URL url1 = new URL(url);
			URLConnection con = url1.openConnection();
			
			// specify that we will send output and accept input
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setConnectTimeout(20000); // long timeout, but not infinite
			con.setReadTimeout(20000);
			con.setUseCaches(false);
			con.setDefaultUseCaches(false);
			
			// tell the web server what we are sending
			con.setRequestProperty("Content-Type", contentType);
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			if (facilityId == null) {
				facilityId = "";
			}
			con.setRequestProperty("Ugandaemr-Sync-Facility-Id", facilityId);
			
			OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
			writer.write(content);
			writer.flush();
			writer.close();
			
			// reading the response
			InputStreamReader reader = new InputStreamReader(con.getInputStream());
			StringBuilder buf = new StringBuilder();
			char[] cbuf = new char[2048];
			int num;
			while (-1 != (num = reader.read(cbuf))) {
				buf.append(cbuf, 0, num);
			}
			String result = buf.toString();
			
			ObjectMapper mapper = new ObjectMapper();
			Map map = mapper.readValue(result, Map.class);
			return map;
		}
		catch (Throwable t) {
			t.printStackTrace(System.out);
		}
		return null;
	}
	
	public Map sendPostBy(String url, String data) throws Exception {
		SyncGlobalProperties syncGlobalProperties = new SyncGlobalProperties();
		String contentTypeJSON = SyncConstant.JSON_CONTENT_TYPE;
		
		String serverIP = syncGlobalProperties.getGlobalProperty(SyncConstant.SERVER_IP);
		String serverProtocol = syncGlobalProperties.getGlobalProperty(SyncConstant.SERVER_PROTOCOL);
		String facilitySyncId = syncGlobalProperties.getGlobalProperty(HEALTH_CENTER_SYNC_ID);
		String facilityURL = serverProtocol + serverIP + "/" + url;
		
		return sendPostBy(contentTypeJSON, data, facilitySyncId, facilityURL);
	}
	
	/*Request for facility Id*/
	
	public String requestFacilityId() throws Exception {
		LocationService service = Context.getLocationService();
		SyncGlobalProperties syncGlobalProperties = new SyncGlobalProperties();
		
		Location location = service.getLocation(Integer.valueOf(2));
		
		Facility facility = new Facility(location.getName());
		
		ObjectMapper mapper = new ObjectMapper();
		
		String jsonInString = mapper.writeValueAsString(facility);
		
		Map facilityMap = sendPostBy("api/facility", jsonInString);
		
		String uuid = String.valueOf(facilityMap.get("uuid"));
		
		if (uuid != null) {
			syncGlobalProperties.setGlobalProperty(HEALTH_CENTER_SYNC_ID, uuid);
			return "Facility ID Generated Successfully";
			
		}
		return "Could not generate Facility ID";
	}

	public boolean isConnectionAvailable() {
		try {
			final URL url = new URL(UgandaEMRSyncConfig.CONNECTIVITY_CHECK_URL);
			final URLConnection conn = url.openConnection();
			conn.connect();
			conn.getInputStream().close();
			log.info(UgandaEMRSyncConfig.CONNECTIVITY_CHECK_SUCCESS);
			return true;
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			log.info(UgandaEMRSyncConfig.CONNECTIVITY_CHECK_FAILED);
			return false;
		}
	}
	public boolean isServerAvailable(String strUrl) {
		try {
			final URL url = new URL(strUrl);
			final URLConnection conn = url.openConnection();
			conn.connect();
			conn.getInputStream().close();
			log.info(UgandaEMRSyncConfig.RECENCY_SERVER_SUCCESS);
			return true;
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			log.info(UgandaEMRSyncConfig.RECENCY_SERVER_FAILED);
			return false;
		}
	}

}
