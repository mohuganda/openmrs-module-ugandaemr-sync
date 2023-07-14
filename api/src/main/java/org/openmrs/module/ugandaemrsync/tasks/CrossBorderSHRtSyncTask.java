package org.openmrs.module.ugandaemrsync.tasks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.ugandaemrsync.api.UgandaEMRHttpURLConnection;
import org.openmrs.module.ugandaemrsync.api.UgandaEMRSyncService;
import org.openmrs.module.ugandaemrsync.model.SyncFhirProfile;
import org.openmrs.module.ugandaemrsync.server.SyncFHIRRecord;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.openmrs.ui.framework.SimpleObject;

import java.util.Map;

import static org.openmrs.module.ugandaemrsync.UgandaEMRSyncConfig.PATIENT_ID_TYPE_CROSS_BORDER_NAME;
import static org.openmrs.module.ugandaemrsync.UgandaEMRSyncConfig.PATIENT_ID_TYPE_CROSS_BORDER_UUID;

public class CrossBorderSHRtSyncTask extends AbstractTask {
    protected final Log log = LogFactory.getLog(CrossBorderSHRtSyncTask.class);

    @Override
    public void execute() {
        UgandaEMRSyncService ugandaEMRSyncService = Context.getService(UgandaEMRSyncService.class);
        UgandaEMRHttpURLConnection ugandaEMRHttpURLConnection = new UgandaEMRHttpURLConnection();
        SyncFhirProfile syncFhirProfile = ugandaEMRSyncService.getSyncFhirProfileByScheduledTaskName(this.taskDefinition.getName());
        SyncFHIRRecord syncFHIRRecord = new SyncFHIRRecord();
        syncFHIRRecord.generateCaseBasedFHIRResourceBundles(syncFhirProfile);
        syncFHIRRecord.sendFhirResourcesTo(syncFhirProfile);
    }
}
