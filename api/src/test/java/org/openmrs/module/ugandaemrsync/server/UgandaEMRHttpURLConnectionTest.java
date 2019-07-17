package org.openmrs.module.ugandaemrsync.server;

import org.apache.poi.hssf.record.formula.functions.True;
import org.junit.Test;

import static org.junit.Assert.*;

public class UgandaEMRHttpURLConnectionTest {

    @Test
    public void isConnectionAvailable() {
        UgandaEMRHttpURLConnection ugandaEMRHttpURLConnection = new UgandaEMRHttpURLConnection();
        assertTrue(ugandaEMRHttpURLConnection.isConnectionAvailable());
    }
    @Test
    public void isServerAvailable() {
        UgandaEMRHttpURLConnection ugandaEMRHttpURLConnection = new UgandaEMRHttpURLConnection();
        assertTrue(ugandaEMRHttpURLConnection.isServerAvailable("http://mirth-tcp.globalhealthapp.net"));
    }
    @Test
    public void thisServerDoesNotExist() {
        UgandaEMRHttpURLConnection ugandaEMRHttpURLConnection = new UgandaEMRHttpURLConnection();
        assertFalse(ugandaEMRHttpURLConnection.isServerAvailable("http://no-server.exists.ug"));
    }
}