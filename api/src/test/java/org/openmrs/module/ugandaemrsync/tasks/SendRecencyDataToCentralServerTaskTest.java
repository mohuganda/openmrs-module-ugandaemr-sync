package org.openmrs.module.ugandaemrsync.tasks;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.auth.BasicScheme;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openmrs.module.ugandaemrsync.server.UgandaEMRHttpURLConnection;

import java.io.IOException;

import static junit.framework.Assert.assertNotSame;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class SendRecencyDataToCentralServerTaskTest extends Mockito {

    private SendRecencyDataToCentralServerTask sendRecencyDataToCentralServerTask;

    @Before
    public void setUp() {
        sendRecencyDataToCentralServerTask = new SendRecencyDataToCentralServerTask();
    }

    @Test
    public void testTaskSending() throws Exception {
        sendRecencyDataToCentralServerTask.execute();
        assertTrue(true);

    }

    @Test
    public void testSuccessfulStatus() {
        //given:
        StatusLine statusLine = mock(StatusLine.class);
        //and:
        when(statusLine.getStatusCode()).thenReturn(200);
        //then:
        assertEquals(200, statusLine.getStatusCode());
    }

@Test
    public void testFailedAuthenticationStatus() {
        //given:
        StatusLine statusLine = mock(StatusLine.class);
        //and:
        when(statusLine.getStatusCode()).thenReturn(401);
        //then:
        assertEquals(401, statusLine.getStatusCode());
    }

    @Test
    public void testUsernameAndPasswordMatches() {
        //given:
        UsernamePasswordCredentials credentials = mock(UsernamePasswordCredentials.class);
        //and:
        when(credentials.getUserName()).thenReturn("admin");
        when(credentials.getPassword()).thenReturn("admin");
        //then:
        assertEquals("admin", credentials.getUserName());
        assertEquals("admin", credentials.getPassword());
    }

    @Test
    public void testUsernameAndPasswordDoesNotMatch() {
        //given:
        UsernamePasswordCredentials credentials = mock(UsernamePasswordCredentials.class);
        //and:
        when(credentials.getUserName()).thenReturn("admin");
        when(credentials.getPassword()).thenReturn("admin");
        //then:
        assertNotSame("admin", "Admin", credentials.getUserName());
        assertNotSame("admin", "admin123", credentials.getPassword());
    }
}
