package org.openmrs.module.ugandaemrsync.server;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class UgandaEMRHttpURLConnectionTest {
	
	@Test
	public void isConnectionAvailable() {
		UgandaEMRHttpURLConnection ugandaEMRHttpURLConnection = new UgandaEMRHttpURLConnection();
		assertTrue(ugandaEMRHttpURLConnection.isConnectionAvailable());
	}
	
	@Test
	public void isServerAvailable() {
		UgandaEMRHttpURLConnection ugandaEMRHttpURLConnection = new UgandaEMRHttpURLConnection();
		assertTrue(ugandaEMRHttpURLConnection.isServerAvailable("http://www.google.com"));
	}
	
	@Test
	public void thisServerDoesNotExist() {
		UgandaEMRHttpURLConnection ugandaEMRHttpURLConnection = new UgandaEMRHttpURLConnection();
		assertFalse(ugandaEMRHttpURLConnection.isServerAvailable("http://no-server.exists.ug"));
	}
}
