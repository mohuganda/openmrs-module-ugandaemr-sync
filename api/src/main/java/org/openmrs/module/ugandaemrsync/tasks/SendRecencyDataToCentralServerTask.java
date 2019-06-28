package org.openmrs.module.ugandaemrsync.tasks;
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
import org.openmrs.scheduler.tasks.AbstractTask;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

/**
 * Posts recency data to the central server
 */

public class SendRecencyDataToCentralServerTask extends AbstractTask {

    protected Log log = LogFactory.getLog(getClass());
    //TODO: use syncGlobalProperties once it has been persisted on ugandaEMR database
    // SyncGlobalProperties syncGlobalProperties = new SyncGlobalProperties();

    @Override
    public void execute() {
        log.info("Executing");
        System.out.println("Executing");
        try {
            // TODO: Add code to verify if there is internet connection and if MIRTH Server is available (log this if not)
            // Ensuring connectivity
            // Contacting MIRTH server
            // Uploading data....
            // Data Successfully uploaded
            /* Check internet connectivity */
            if (netServerIsAvailable(UgandaEMRSyncConfig.GOOGLE_COM, UgandaEMRSyncConfig.GOOGLE_SUCCESS, UgandaEMRSyncConfig.GOOGLE_FAILED)
                    && netServerIsAvailable(UgandaEMRSyncConfig.SERVER_URL, UgandaEMRSyncConfig.SERVER_SUCCESS, UgandaEMRSyncConfig.SERVER_FAILED)) {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(UgandaEMRSyncConfig.SENDNG_SERVER_URL);
                post.addHeader(UgandaEMRSyncConfig.HEADER_EMR_DATE, new Date().toString());

                UsernamePasswordCredentials credentials
                        = new UsernamePasswordCredentials(UgandaEMRSyncConfig.SERVER_USERNAME, UgandaEMRSyncConfig.SERVER_PASSWORD);
                post.addHeader(new BasicScheme().authenticate(credentials, post, null));

                String bodyText = getRecencyData();

                HttpEntity multipart = MultipartEntityBuilder.create()
                        .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                        .addTextBody("facility_uuid", UgandaEMRSyncConfig.FACILITY_UUID)
                        .addTextBody("data", bodyText, ContentType.TEXT_PLAIN)
                        .build();
                post.setEntity(multipart);

                HttpResponse response = client.execute(post);

                System.out.println(response.toString());
            }

        } catch (IOException | AuthenticationException e) {
            e.printStackTrace();
        }

    }

    private String getRecencyData() {
        return "patient_id, patient_creator, encounter_id, gravida, para$$$248,10,0,0,0$$$334,10,0,0,0$$$336,10,0,0,0$$$401,7,0,0,0$$$232,5,0,0,0$$$248,10,0,0,0$$$334,10,0,0,0$$$336,10,0,0,0$$$401,7,0,0,0$$$232,5,0,0,0$$$248,10,0,0,0$$$334,10,0,0,0$$$336,10,0,0,0$$$401,7,0,0,0$$$232,5,0,0,0$$$248,10,0,0,0$$$334,10,0,0,0$$$336,10,0,0,0$$$401,7,0,0,0$$$232,5,0,0,0$$$248,10,0,0,0$$$334,10,0,0,0$$$336,10,0,0,0$$$401,7,0,0,0";
    }

    private boolean netServerIsAvailable(String strUrl, String strSuccess, String strFail) {
        try {
            final URL url = new URL(strUrl);
            final URLConnection conn = url.openConnection();
            conn.connect();
            conn.getInputStream().close();
            System.out.println(strSuccess);
            return true;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println(strFail);
            return false;
        }
    }
}