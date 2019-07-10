package org.openmrs.module.ugandaemrsync.server;

import org.apache.poi.hssf.record.formula.functions.True;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UgandaEMRHttpURLConnectionTest {

    @Test
    public void isConnectionAvailable() {
        UgandaEMRHttpURLConnection ugandaEMRHttpURLConnection = new UgandaEMRHttpURLConnection();
        assertEquals(
                ugandaEMRHttpURLConnection.isConnectionAvailable(), true);
    }
    @Test
    public void isServerAvailable() {
        UgandaEMRHttpURLConnection ugandaEMRHttpURLConnection = new UgandaEMRHttpURLConnection();
        assertEquals(
                ugandaEMRHttpURLConnection.isServerAvailable("http://mirth-tcp.globalhealthapp.net"), true);
    }
}