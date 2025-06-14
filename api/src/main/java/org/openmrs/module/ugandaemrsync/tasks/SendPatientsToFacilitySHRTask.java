package org.openmrs.module.ugandaemrsync.tasks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.ugandaemrsync.api.UgandaEMRSyncService;
import org.openmrs.module.ugandaemrsync.model.SyncFhirProfile;
import org.openmrs.module.ugandaemrsync.server.SyncFHIRRecord;
import org.openmrs.scheduler.tasks.AbstractTask;

public class SendPatientsToFacilitySHRTask extends AbstractTask {
    protected final Log log = LogFactory.getLog(SendPatientsToFacilitySHRTask.class);

    @Override
    public void execute() {
        UgandaEMRSyncService ugandaEMRSyncService = Context.getService(UgandaEMRSyncService.class);


        SyncFhirProfile syncFhirProfile = ugandaEMRSyncService.getSyncFhirProfileByScheduledTaskName(this.taskDefinition.getName());
        SyncFHIRRecord syncFHIRRecord = new SyncFHIRRecord();
        if (syncFhirProfile.getProfileEnabled()) {

            log.info("Generating Resources and cases for Profile " + syncFhirProfile.getName());
            syncFHIRRecord.generateCaseBasedFHIRResourceBundles(syncFhirProfile);
            syncFHIRRecord.sendFhirResourcesTo(syncFhirProfile);
        }

    }

    public void transferIn(String patientDataObject) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            UgandaEMRSyncService ugandaEMRSyncService = Context.getService(UgandaEMRSyncService.class);
            JSONObject results = new JSONObject(patientDataObject);
            for (Object jsonObject : results.getJSONArray("entry")) {
                JSONObject patientData = new JSONObject(jsonObject.toString()).getJSONObject("resource");
                Patient patient = null;
                String healthCenterFrom = patientData.getJSONObject("managingOrganization").get("display").toString();
                if (!ugandaEMRSyncService.patientFromFHIRExists(patientData)) {
                    patient = ugandaEMRSyncService.createPatientsFromFHIR(patientData);
                }
                if (patient != null) {
                    log.info("Patient " + patient.getNames() + "Successfully Created");
                }
            }

        } catch (Exception e) {
            log.info(e);
        }
    }
}
