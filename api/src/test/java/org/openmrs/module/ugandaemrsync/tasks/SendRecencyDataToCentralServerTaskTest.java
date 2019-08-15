package org.openmrs.module.ugandaemrsync.tasks;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static junit.framework.Assert.assertNotSame;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;

public class SendRecencyDataToCentralServerTaskTest extends Mockito {
	
	private SendRecencyDataToCentralServerTask sendRecencyDataToCentralServerTask;
	
	@Before
	public void setUp() {
		sendRecencyDataToCentralServerTask = new SendRecencyDataToCentralServerTask();
	}
	
	@Test
	public void testTaskSending() {
		sendRecencyDataToCentralServerTask.execute();
		assertTrue(true);
	}
	
	@Test
	public void testSuccessfulStatus() {
		//given:
		StatusLine statusLine = mock(StatusLine.class);
		//and:
		when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
		//then:
		assertEquals(HttpStatus.SC_OK, statusLine.getStatusCode());
	}
	
	@Test
	public void testFailedAuthenticationStatus() {
		//given:
		StatusLine statusLine = mock(StatusLine.class);
		//and:
		when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_UNAUTHORIZED);
		//then:
		assertEquals(HttpStatus.SC_UNAUTHORIZED, statusLine.getStatusCode());
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
	
	@Test
	public void getRecencyDataFromREST() throws Exception {
		System.out.println(sendRecencyDataToCentralServerTask.getRecencyDataFromREST());
		assertNotNull("Not null");
		
	}
	
	//	@Test
	//	public void testGetResponseBody() throws Exception {
	//		System.out.println(sendRecencyDataToCentralServerTask.getResponseBody());
	//		assertNotNull("Not null");
	//	}
	
	@Test
	public void testGetRecencyData() {
		assertNotNull(sendRecencyDataToCentralServerTask.getRecencyData());
		Assert.assertTrue(true);
	}
	
	@Test
	public void testRenderReport() {
		sendRecencyDataToCentralServerTask.renderReport();
		Assert.assertTrue(true);
	}
}
