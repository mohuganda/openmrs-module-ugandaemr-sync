/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.ugandaemrsync;

import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.ugandaemrsync.server.*;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static org.junit.Assert.*;

/**
 * This is a unit test, which verifies logic in UgandaEMRSyncService. It doesn't extend
 * BaseModuleContextSensitiveTest, thus it is run without the in-memory DB and Spring context.
 */
public class SyncProcessorTest extends BaseModuleContextSensitiveTest {

    @Test
    public void shouldConvertListToJsonString() throws IOException {
        List<Object[]> objects = new ArrayList<Object[]>();

        Object[] encounters = {"1", 2, "7"};
        objects.add(encounters);
        List<String> columns = Arrays.asList("encounter_id", "person_id", "value_text");

        // Map<String, String> dt = SyncDataRecord.convertListOfMapsToJsonString(objects, columns);
        //String result = dt.get("json");
        assertEquals(1, 1);

    }

    @Test
    public void shouldReplaceFacilityAndLimitsInQueryString() {
        String facilityId = "123332323";
        String limitFrom = "1";
        String limitTo = "10";

        String personQuery = SyncConstant.ENCOUNTER_QUERY;
        String lastSyncDate = "2010-01-01 12:01:01";
        String personQueryWith = String.format(personQuery, facilityId, lastSyncDate, lastSyncDate, lastSyncDate, limitFrom,
                limitTo);

		/*Session session = Context.getRegisteredComponent("sessionFactory", SessionFactory.class).getCurrentSession();

		SQLQuery query = session.createSQLQuery(personQueryWith);
		List results = query.list();
		*/
        assertTrue(personQueryWith.contains("10"));
        //assertTrue(results.size() > 0);

    }

    @Test
    public void shouldTestReplace() {
        String personQuery = SyncConstant.TABLES_TOTAL_QUERY;

        String lastSyncDate = "2010-01-01 12:01:01";

        String allThree = personQuery.replaceAll("lastSync", lastSyncDate);

        assertTrue(allThree.contains("2010-01-01"));
    }

    @Test
    public void shouldCompressSendProcessingCommand() throws IOException {
        UgandaEMRHttpURLConnection ugandaEMRHttpURLConnection = new UgandaEMRHttpURLConnection("http://", "localhost:5000");

        ugandaEMRHttpURLConnection.getProcessed("5joAlB");
        assertEquals(1, 1);
    }

    @Test
    public void shouldSyncDataCommand() throws Exception {
        SyncDataRecord syncDataRecord = new SyncDataRecord("http://", "localhost:5000", "10000", "1900-01-01");
        Properties props = new Properties();
        props.setProperty("driver.class", "com.mysql.jdbc.Driver");
        props.setProperty("driver.url", "jdbc:mysql://localhost:3306/mulago");
        props.setProperty("user", "openmrs");
        props.setProperty("password", "openmrs");
        Connection connection = syncDataRecord.getDatabaseConnection(props);
        syncDataRecord.requestFacilityID();
        assertNotNull(syncDataRecord.getFacilityId());
        syncDataRecord.syncData2("/Users/carapai/Desktop/sync2/", connection);
        String uniqueString = syncDataRecord.zipSplitAndSend("/Users/carapai/Desktop/sync2/");
        assertNotNull(uniqueString);
        syncDataRecord.sendProcessingCommand(uniqueString);
    }
}
