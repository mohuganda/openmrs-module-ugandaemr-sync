package org.openmrs.module.ugandaemrsync.server;

/**
 * Created by lubwamasamuel on 11/10/16.
 */

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;
import java.util.Map;

import static org.openmrs.module.ugandaemrsync.server.SyncConstant.HEALTH_CENTER_SYNC_ID;
import static org.openmrs.module.ugandaemrsync.server.SyncConstant.SERVER_PROTOCOL_PLACE_HOLDER;

public class UgandaEMRHttpURLConnection {
	
	private String serverIP;
	
	private String serverProtocol;
	
	public UgandaEMRHttpURLConnection(String serverProtocol, String serverIP) {
		this.serverIP = serverIP;
		this.serverProtocol = serverProtocol;
	}
	
	public boolean testInternet(String site) {
		Socket sock = new Socket();
		InetSocketAddress addr = new InetSocketAddress(site, 80);
		try {
			sock.connect(addr, 3000);
			return true;
		}
		catch (IOException e) {
			return false;
		}
		finally {
			try {
				sock.close();
			}
			catch (IOException e) {}
		}
	}
	
	/*Request for facility Id*/
	
	public String requestFacilityId() throws Exception {
		LocationService service = Context.getLocationService();
		
		Location location = service.getLocation(Integer.valueOf(2));
		
		Facility facility = new Facility(location.getName());
		
		ObjectMapper mapper = new ObjectMapper();
		
		String jsonInString = mapper.writeValueAsString(facility);
		
		Map facilityMap = postJson("api/facility", jsonInString);
		
		String uuid = String.valueOf(facilityMap.get("uuid"));
		
		if (uuid != null) {
			return uuid;
			
		}
		return null;
	}
	
	public void postFile(byte[] json, String url, String fileName) throws IOException {
		String facilityURL = serverProtocol + serverIP + "/" + url;
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost post = new HttpPost(facilityURL);
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.addBinaryBody("file", json, ContentType.DEFAULT_BINARY, fileName);
		HttpEntity entity = builder.build();
		post.setEntity(entity);
		CloseableHttpResponse response = client.execute(post);
		
		if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 201) {
			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
			String message = IOUtils.toString(br);
			// Result returnResult = mapper.readValue(message, Result.class);
			// return returnResult;
		} else {
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
		}
		client.close();
	}
	
	public Map postJson(String url, String json) throws IOException {
		String facilityURL = serverProtocol + serverIP + "/" + url;
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(facilityURL);
		StringEntity entity = new StringEntity(json);
		httpPost.setEntity(entity);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");
		CloseableHttpResponse response = client.execute(httpPost);
		
		if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 201) {
			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
			String message = IOUtils.toString(br);
			ObjectMapper mapper = new ObjectMapper();
			Map map = mapper.readValue(message, Map.class);
			client.close();
			return map;
		} else {
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
		}
	}
	
	public void getProcessed(String uniqueId) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		URIBuilder builder = new URIBuilder();
		builder.setScheme("http").setHost(this.serverIP).setPath("/api/process").setParameter("file_name", uniqueId);
		try {
			URI uri = builder.build();
			System.out.println(uri);
			HttpGet httpGet = new HttpGet(uri);
			CloseableHttpResponse response1 = httpclient.execute(httpGet);
			response1.close();
		}
		catch (URISyntaxException e) {
			e.printStackTrace();
		}
		catch (ClientProtocolException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
