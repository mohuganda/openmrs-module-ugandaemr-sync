package org.openmrs.module.ugandaemrsync.tasks;

import org.openmrs.api.context.Context;
import org.openmrs.module.ugandaemrsync.api.UgandaEMRSyncService;
import org.openmrs.module.ugandaemrsync.model.SyncFhirProfile;
import org.openmrs.module.ugandaemrsync.server.SyncFHIRRecord;
import org.openmrs.scheduler.tasks.AbstractTask;

import java.text.ParseException;

public class HIVCaseBasedSurveillanceTask extends AbstractTask {
    @Override
    public void execute() {
        UgandaEMRSyncService ugandaEMRSyncService = Context.getService(UgandaEMRSyncService.class);
        SyncFhirProfile syncFhirProfile = ugandaEMRSyncService.getSyncFhirProfileByScheduledTaskName("HIV CASE BASED SURVEILLANCE");
        SyncFHIRRecord syncFHIRRecord = new SyncFHIRRecord();
        try {
            syncFHIRRecord.generateCaseBasedFHIRResourceBundles(syncFhirProfile);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
