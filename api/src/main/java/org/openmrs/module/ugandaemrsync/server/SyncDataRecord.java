package org.openmrs.module.ugandaemrsync.server;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Date;
import java.util.*;

import static org.openmrs.module.ugandaemrsync.server.SyncConstant.*;

/**
 * Created by lubwamasamuel on 07/11/2016.
 */
public class SyncDataRecord {
	
	private UgandaEMRHttpURLConnection ugandaEMRHttpURLConnection;
	
	private String lastSyncDate;
	
	private String maximumNoOfRows;
	
	private String facilityId;
	
	Log log = LogFactory.getLog(getClass());
	
	public UgandaEMRHttpURLConnection getUgandaEMRHttpURLConnection() {
		return ugandaEMRHttpURLConnection;
	}
	
	public void setUgandaEMRHttpURLConnection(UgandaEMRHttpURLConnection ugandaEMRHttpURLConnection) {
		this.ugandaEMRHttpURLConnection = ugandaEMRHttpURLConnection;
	}
	
	public String getLastSyncDate() {
		return lastSyncDate;
	}
	
	public void setLastSyncDate(String lastSyncDate) {
		this.lastSyncDate = lastSyncDate;
	}
	
	public String getMaximumNoOfRows() {
		return maximumNoOfRows;
	}
	
	public void setMaximumNoOfRows(String maximumNoOfRows) {
		this.maximumNoOfRows = maximumNoOfRows;
	}
	
	public String getFacilityId() {
		return facilityId;
	}
	
	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}
	
	public SyncDataRecord(String serverProtocol, String serverIP, String facilityId, String maximumNoOfRows,
	    String lastSyncDate) {
		this.lastSyncDate = lastSyncDate;
		this.ugandaEMRHttpURLConnection = new UgandaEMRHttpURLConnection(serverProtocol, serverIP);
		this.maximumNoOfRows = maximumNoOfRows;
		this.facilityId = facilityId;
	}
	
	public SyncDataRecord(String serverProtocol, String serverIP, String maximumNoOfRows, String lastSyncDate) {
		this.lastSyncDate = lastSyncDate;
		this.ugandaEMRHttpURLConnection = new UgandaEMRHttpURLConnection(serverProtocol, serverIP);
		this.maximumNoOfRows = maximumNoOfRows;
	}
	
	public java.sql.Connection getDatabaseConnection(Properties props) throws ClassNotFoundException, SQLException {
		
		String driverClassName = props.getProperty("driver.class");
		String driverURL = props.getProperty("driver.url");
		String username = props.getProperty("user");
		String password = props.getProperty("password");
		
		Class.forName(driverClassName);
		return DriverManager.getConnection(driverURL, username, password);
	}
	
	private void dumpTable(java.sql.Connection dbConn, String folder, String filename, String query, int count) throws UnsupportedEncodingException, FileNotFoundException {
        if (count > 0) {
            Integer max = Integer.valueOf(maximumNoOfRows);
            JsonFactory jFactory = new JsonFactory();
            try {
                JsonGenerator jGenerator = jFactory.createJsonGenerator(new File(folder + filename), JsonEncoding.UTF8);
                jGenerator.writeStartArray();

                for (int i = 0; i < count / max + 1; i++) {
                    String offset = String.valueOf(i * max);
                    query = query.replaceAll("#DATE", lastSyncDate);
                    query = String.format(query, facilityId, offset, String.valueOf(max));

                    PreparedStatement stmt = dbConn.prepareStatement(query);
                    ResultSet rs = stmt.executeQuery();

                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    String[] columnNames = new String[columnCount];
                    for (int j = 0; j < columnNames.length; j++) {
                        columnNames[j] = metaData.getColumnLabel(j + 1);
                    }
                    while (rs.next()) {
                        jGenerator.writeStartObject();

                        for (int k = 0; k < columnNames.length; k++) {
                            String col = columnNames[k];
                            jGenerator.writeStringField(col, rs.getString(k + 1));
                        }
                        jGenerator.writeEndObject();
                    }
                    rs.close();
                    stmt.close();
                }
                jGenerator.writeEndArray();
                jGenerator.close();
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }
    }
	
	private Map<String, Integer> convertListToMap(String query, java.sql.Connection dbConn) throws SQLException {

        Map<String, Integer> result = new HashMap<>();

        PreparedStatement stmt = dbConn.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String column = rs.getString(2);
            int number = rs.getInt(1);
            result.put(String.valueOf(column), Integer.valueOf(String.valueOf(number)));
        }
        rs.close();
        stmt.close();

        return result;
    }
	
	public void syncData2(String folder, java.sql.Connection connection) {
		
		if (checkFolderPath(folder)) {
			try {
				FileUtils.cleanDirectory(new File(folder));
				Map<String, Integer> numbers = convertListToMap(
				    SyncConstant.TABLES_TOTAL_QUERY.replaceAll("#DATE", lastSyncDate), connection);
				Integer encounters = numbers.get("encounter");
				Integer obs = numbers.get("obs");
				Integer person_names = numbers.get("person_name");
				Integer person_addresses = numbers.get("person_address");
				Integer person_attributes = numbers.get("person_attribute");
				Integer patients = numbers.get("patient");
				Integer patient_identifiers = numbers.get("patient_identifier");
				Integer visits = numbers.get("visit");
				Integer encounter_providers = numbers.get("encounter_provider");
				Integer providers = numbers.get("provider");
				Integer fingerprints = numbers.get("fingerprint");
				
				dumpTable(connection, folder, "encounter_providers.json", ENCOUNTER_PROVIDER_QUERY, encounter_providers);
				dumpTable(connection, folder, "providers.json", PROVIDER_QUERY, providers);
				dumpTable(connection, folder, "patients.json", PATIENT_QUERY, patients);
				dumpTable(connection, folder, "person_names.json", PERSON_NAME_QUERY, person_names);
				dumpTable(connection, folder, "person_addresses.json", PERSON_ADDRESS_QUERY, person_addresses);
				dumpTable(connection, folder, "person_attributes.json", PERSON_ATTRIBUTE_QUERY, person_attributes);
				dumpTable(connection, folder, "patient_identifiers.json", PATIENT_IDENTIFIER_QUERY, patient_identifiers);
				dumpTable(connection, folder, "fingerprints.json", FINGERPRINT_QUERY, fingerprints);
				dumpTable(connection, folder, "visits.json", VISIT_QUERY, visits);
				dumpTable(connection, folder, "encounters.json", ENCOUNTER_QUERY, encounters);
				dumpTable(connection, folder, "obs.json", OBS_QUERY, obs);
				
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public String zipSplitAndSend(String folder) {
		try {
			File file = new File(folder);
			String zipFileName = folder + "data.zip";
			Zip.zipDirectory(file, zipFileName);
			
			File zipFile = new File(zipFileName);
			final String uniqueString = Zip.splitFile(zipFile);
			final boolean delete = zipFile.delete();
			
			File dir = new File(folder);
			File[] files = dir.listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String name) {
					return name.startsWith(uniqueString + "-data");
				}
			});
			
			if (ugandaEMRHttpURLConnection.testInternet("google.com")) {
				assert files != null;
				for (File currentFile : files) {
					Path path = Paths.get(currentFile.getAbsolutePath());
					byte[] data = Files.readAllBytes(path);
					System.out.println(currentFile.getName());
					ugandaEMRHttpURLConnection.postFile(data, "api/files", currentFile.getName());
				}
				return uniqueString;
			}
			return null;
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String getAbsoluteBackupFolderPath() {
		String folder;
		String appDataDir = OpenmrsUtil.getApplicationDataDirectory();
		if (!appDataDir.endsWith(System.getProperty("file.separator")))
			appDataDir = appDataDir + System.getProperty("file.separator");
		folder = Context.getAdministrationService().getGlobalProperty("ugandaemrsync.folderPath", "sync");
		if (folder.startsWith("./"))
			folder = folder.substring(2);
		if (!folder.startsWith("/") && !folder.contains(":"))
			folder = appDataDir + folder;
		folder = folder.replaceAll("/", "\\" + System.getProperty("file.separator"));
		if (!folder.endsWith(System.getProperty("file.separator")))
			folder = folder + System.getProperty("file.separator");
		return folder;
	}
	
	public void sendProcessingCommand(String unique) {
		if (ugandaEMRHttpURLConnection.testInternet("google.com")) {
			ugandaEMRHttpURLConnection.getProcessed(unique);
		}
	}
	
	private boolean checkFolderPath(String folder) {
		// check if backup path exists (sub folder by sub folder), otherwise create
		String[] folderPath = folder.split("\\" + System.getProperty("file.separator"));
		String s = folderPath[0];
		File f;
		boolean success = true;
		for (int i = 1; i <= folderPath.length - 1 && success; i++) {
			if (!"".equals(folderPath[i]))
				s += System.getProperty("file.separator") + folderPath[i];
			f = new File(s);
			
			System.out.println("check exit folder: " + s + ", " + f.exists());
			
			if (!f.exists()) {
				success = f.mkdir();
			}
			System.out.println("create folder: " + s + ", " + success);
		}
		if (!folder.endsWith("\\" + System.getProperty("file.separator")))
			folder += System.getProperty("file.separator");
		return success;
	}
	
	public java.sql.Connection connection() throws SQLException, ClassNotFoundException {
		Properties props = new Properties();
		props.setProperty("driver.class", "com.mysql.jdbc.Driver");
		props.setProperty("driver.url", Context.getRuntimeProperties().getProperty("connection.url"));
		props.setProperty("user", Context.getRuntimeProperties().getProperty("connection.username"));
		props.setProperty("password", Context.getRuntimeProperties().getProperty("connection.password"));
		return getDatabaseConnection(props);
	}
	
	public void requestFacilityID() throws Exception {
		this.facilityId = ugandaEMRHttpURLConnection.requestFacilityId();
	}
}
