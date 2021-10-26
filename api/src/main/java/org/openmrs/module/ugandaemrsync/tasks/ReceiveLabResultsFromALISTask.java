package org.openmrs.module.ugandaemrsync.tasks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openmrs.*;
import org.openmrs.api.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.ugandaemrsync.api.UgandaEMRHttpURLConnection;
import org.openmrs.module.ugandaemrsync.api.UgandaEMRSyncService;
import org.openmrs.module.ugandaemrsync.model.SyncTask;
import org.openmrs.module.ugandaemrsync.model.SyncTaskType;
import org.openmrs.module.ugandaemrsync.server.SyncGlobalProperties;
import org.openmrs.scheduler.tasks.AbstractTask;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.openmrs.module.ugandaemrsync.UgandaEMRSyncConfig.*;
import static org.openmrs.module.ugandaemrsync.server.SyncConstant.ALIS_LAB_RESULTS_PULL_TYPE_UUID;

public class ReceiveLabResultsFromALISTask extends AbstractTask {
    protected final Log log = LogFactory.getLog(ReceiveLabResultsFromALISTask.class);

    SyncGlobalProperties syncGlobalProperties = new SyncGlobalProperties();
    UgandaEMRSyncService ugandaEMRSyncService = Context.getService(UgandaEMRSyncService.class);

    SyncTaskType syncTaskType = ugandaEMRSyncService.getSyncTaskTypeByUUID(ALIS_LAB_RESULTS_PULL_TYPE_UUID);

    @Override
    public void execute() {
        UgandaEMRHttpURLConnection ugandaEMRHttpURLConnection = new UgandaEMRHttpURLConnection();

        if (!isGpDhis2OrganizationUuidSet()) {
            return;
        }

        if (!ugandaEMRHttpURLConnection.isConnectionAvailable()) {
            return;
        }



        String labResultsALISUrlEndPoint="";
        String results="";
        if(syncTaskType.getUrl()!=null){
            labResultsALISUrlEndPoint = syncTaskType.getUrl();
            labResultsALISUrlEndPoint = addParametersToUrl(labResultsALISUrlEndPoint);

            if (!ugandaEMRHttpURLConnection.isServerAvailable(labResultsALISUrlEndPoint)) {
                log.error("server not available ");
                return;
            }

        }

        try {
            String username = syncTaskType.getUrlUserName();
            String password = syncTaskType.getUrlPassword();
            Map resultMap = ugandaEMRHttpURLConnection.getByWithBasicAuth(labResultsALISUrlEndPoint, username, password, "String");
            results = (String)resultMap.get("result");

        } catch (Exception e) {
            log.error("Failed to fetch results",e);
        }

         if (results != null && !results.isEmpty() ) {
             JSONObject object = new JSONObject(results);
             processData(object);

         }else{
             log.error("Results are empty");
         }
    }

    public boolean isGpDhis2OrganizationUuidSet() {
        if (isBlank(syncGlobalProperties.getGlobalProperty(GP_DHIS2_ORGANIZATION_UUID))) {
            log.info("DHIS2 Organization UUID is not set");
            return false;
        }
        return true;
    }

    public String addParametersToUrl(String url) {
        String uuid  = syncGlobalProperties.getGlobalProperty(GP_DHIS2_ORGANIZATION_UUID);
        String lastSyncDate = syncGlobalProperties.getGlobalProperty(GP_ALIS_LAB_RESULTS_LAST_SYNC_DATE);
        System.out.println(lastSyncDate+"last sync date");

        String uuidParameter  = "&managingOrganisation="+uuid;
        String startDateParameter ="";
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.minusDays(1);

        String newEndDate = endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String endDateParameter = "&%20periodEnd="+ newEndDate+"%20"+"23:59:59";
        if(lastSyncDate!=null&& lastSyncDate!=""){
            startDateParameter = "?periodStart="+lastSyncDate+"%20"+"00:00:00";
        }else{
           startDateParameter=  "?periodStart="+"2021-06-01"+"%20"+"00:00:00";
        }
        String newUrl =url+startDateParameter+endDateParameter+uuidParameter;
        return newUrl;
    }

    private void processData(JSONObject jsonObject){
      JSONArray patientRecords =  jsonObject.getJSONArray("entry");

      if(patientRecords.length()>0 && patientRecords!=null) {
          for (Object o : patientRecords) {
           JSONObject patientRecord = (JSONObject)o;
           JSONObject patientAttributes = patientRecord.getJSONArray("entry").getJSONObject(0);

           JSONObject patientEncounterDetails = patientRecord.getJSONArray("entry").getJSONObject(3);

           String patientARTNo = getIdentifier(patientAttributes);
           Patient patient = ugandaEMRSyncService.getPatientByPatientIdentifier(patientARTNo);
           if(patient!=null){
               System.out.println(patientARTNo);
                processPatientBundle(patientEncounterDetails,patient);
           }

          }
          savedSyncTask();
      }

    }

    private void processPatientBundle(JSONObject jsonObject, Patient patient){
        UserService userService = Context.getUserService();

        try {
            String visit_date = getJSONObjectValue(jsonObject.getJSONObject("0"), "visit_date");
            String dateFormat = ugandaEMRSyncService.getDateFormat(visit_date);
            Date startVisitDate = ugandaEMRSyncService.convertStringToDate(visit_date, "00:00:00", dateFormat);
            Date stopVisitDate = ugandaEMRSyncService.convertStringToDate(visit_date, "23:59:59", dateFormat);

          //  addObsToEncounter(patient,startVisitDate,stopVisitDate,obsList,user);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private String getIdentifier(JSONObject jsonObject){
        String identifier="";
        if(jsonObject!=null) {
            identifier = (String) jsonObject.getJSONObject("resource").getJSONArray("identifier").
                    getJSONObject(0).getJSONObject("type").getJSONObject("coding").getJSONObject("0").get("code");
        }
        return identifier;
    }

    private String getJSONObjectValue(JSONObject jsonObject,String objectName){
        Object value = "";
        if(jsonObject!=null){
          value = jsonObject.getJSONObject(objectName).getJSONObject("coding").get("code");
        }
        try {
            return (String)value;
        }catch (ClassCastException e){
            return null;
        }
    }

    private void savedSyncTask(){
        SyncTask syncTask = new SyncTask();
        syncTask.setActionCompleted(true);
        syncTask.setDateSent(new Date());
        syncTask.setSyncTaskType(syncTaskType);
        syncTask.setCreator(Context.getUserService().getUser(1));
        syncTask.setSentToUrl(syncTaskType.getUrl());
        syncTask.setRequireAction(false);
        syncTask.setSyncTask("ALIS Lab Results receive date as of "+ new Date());
        syncTask.setStatus("SUCCESS");
        ugandaEMRSyncService.saveSyncTask(syncTask);

       syncGlobalProperties.setGlobalProperty(GP_ALIS_LAB_RESULTS_LAST_SYNC_DATE,LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }
}
