package org.openmrs.module.ugandaemrsync.server;

import org.apache.poi.hssf.record.formula.functions.True;
import org.junit.Test;

import static org.junit.Assert.*;

public class UgandaEMRHttpURLConnectionTest {

    @Test
    public void netServerIsAvailable() {
        UgandaEMRHttpURLConnection connectionRequest = new UgandaEMRHttpURLConnection();
        UgandaEMRHttpURLConnection ugandaEMRHttpURLConnection = new UgandaEMRHttpURLConnection();
        assertEquals(
                ugandaEMRHttpURLConnection.netServerIsAvailable("http://www.google.com", "Success", "Fail"), true);
    }
}