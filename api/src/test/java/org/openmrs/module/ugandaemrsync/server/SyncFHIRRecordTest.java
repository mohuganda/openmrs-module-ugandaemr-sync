package org.openmrs.module.ugandaemrsync.server;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ServiceContext;
import org.openmrs.module.fhir2.api.FhirPatientService;
import org.openmrs.module.fhir2.api.FhirPersonService;
import org.openmrs.module.ugandaemrsync.api.UgandaEMRSyncService;
import org.openmrs.module.ugandaemrsync.model.SyncFHIRProfile;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Field;
import java.util.List;

public class SyncFHIRRecordTest extends BaseModuleContextSensitiveTest {

    protected static final String UGANDAEMR_GLOBAL_PROPERTY_DATASET_XML = "org/openmrs/module/ugandaemrsync/include/globalPropertiesDataSet.xml";
    protected static final String UGANDAEMR_STANDARD_DATASET_XML = "org/openmrs/module/ugandaemrsync/include/standardTestDataset.xml";


    @Before
    public void setup() throws Exception {
        executeDataSet(UGANDAEMR_GLOBAL_PROPERTY_DATASET_XML);
        executeDataSet(UGANDAEMR_STANDARD_DATASET_XML);
    }

    @Test
    public void addOrganizationToRecord_shouldReturnJsonStringWithAManagingOrganization() {
        SyncFHIRRecord syncFHIRRecord=new SyncFHIRRecord();

        String managingOrganizationJsonString = syncFHIRRecord.addOrganizationToRecord("{}");

        Assert.assertNotEquals(managingOrganizationJsonString,"{}");
        Assert.assertEquals(managingOrganizationJsonString,"{\"managingOrganization\":{\"reference\":\"Organization/7744yxP\"}}");
    }

    @Test
    public void saveFHIRBundleProfile() {
        SyncFHIRRecord syncFHIRRecord=new SyncFHIRRecord();
        UgandaEMRSyncService ugandaEMRSyncService = Context.getService(UgandaEMRSyncService.class);
        SyncFHIRProfile syncFHIRProfile = ugandaEMRSyncService.getSyncFHIRProfileByUUID("c91b12c3-65fe-4b1c-aba4-99e3a7e58cfa");

        syncFHIRRecord.saveFHIRBundleProfile(syncFHIRProfile);
    }
}
