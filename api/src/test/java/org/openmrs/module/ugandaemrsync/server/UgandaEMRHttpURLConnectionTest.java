package org.openmrs.module.ugandaemrsync.server;

import org.apache.poi.hssf.record.formula.functions.True;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UgandaEMRHttpURLConnectionTest {

    @Test
    public void isInternetConnectionAndRecencyServerAvailable() {
        UgandaEMRHttpURLConnection ugandaEMRHttpURLConnection = new UgandaEMRHttpURLConnection();
        assertEquals(
                ugandaEMRHttpURLConnection.isInternetConnectionAndRecencyServerAvailable("http://www.google.com", "Success", "Fail"), true);
    }
}