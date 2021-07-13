package org.openmrs.module.ugandaemrsync.tasks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.ugandaemrsync.api.UgandaEMRHttpURLConnection;
import org.openmrs.module.ugandaemrsync.api.UgandaEMRSyncService;
import org.openmrs.module.ugandaemrsync.model.SyncTaskType;
import org.openmrs.module.ugandaemrsync.server.SyncGlobalProperties;
import org.openmrs.scheduler.tasks.AbstractTask;


import java.util.HashMap;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.openmrs.module.ugandaemrsync.UgandaEMRSyncConfig.*;
import static org.openmrs.module.ugandaemrsync.server.SyncConstant.*;

public class ReceiveVisitsDataFromARTAccessTask extends AbstractTask {
    protected final Log log = LogFactory.getLog(ReceiveVisitsDataFromARTAccessTask.class);

    SyncGlobalProperties syncGlobalProperties = new SyncGlobalProperties();
    UgandaEMRSyncService ugandaEMRSyncService = Context.getService(UgandaEMRSyncService.class);

    @Override
    public void execute() {
        UgandaEMRHttpURLConnection ugandaEMRHttpURLConnection = new UgandaEMRHttpURLConnection();

        if (!isGpDhis2OrganizationUuidSet()) {
            return;
        }

        if (!ugandaEMRHttpURLConnection.isConnectionAvailable()) {
            return;
        }



        SyncTaskType syncTaskType = ugandaEMRSyncService.getSyncTaskTypeByUUID(ART_ACCESS_PULL_TYPE_UUID);

        String ARTAccessServerUrlEndPoint="";
        String results="";
        if(syncTaskType.getUrl()!=null){
            ARTAccessServerUrlEndPoint = syncTaskType.getUrl();
            ARTAccessServerUrlEndPoint = addParametersToUrl(ARTAccessServerUrlEndPoint);

            if (!ugandaEMRHttpURLConnection.isServerAvailable(ugandaEMRHttpURLConnection.getBaseURL(ARTAccessServerUrlEndPoint))) {
                return;
            }

        }

        try {
            results = ugandaEMRHttpURLConnection.getJson(ARTAccessServerUrlEndPoint);
        } catch (Exception e) {
            log.error("Failed to fetch results",e);
        }

         if (results != null && !results.isEmpty() ) {
             JSONObject object = new JSONObject(results);
             processData(object);

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
        String newUrl =url;
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
                processObs(patientEncounterDetails);
           }

          }
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

    private void processObs(JSONObject jsonObject){
        HashMap recordsExpected = getARTAccessRecordsConcepts();
        String visit_date = getJSONObjectValue(jsonObject.getJSONObject("0"),"visit_date");
        String next_visit_date = getJSONObjectValue(jsonObject.getJSONObject("1"),"next_visit_date");
        String medicine_picked = getJSONObjectValue(jsonObject.getJSONObject("2"),"medicine_picked");
        String clinical_status = getJSONObjectValue(jsonObject.getJSONObject("3"),"clinical_status");
        String adherence = getJSONObjectValue(jsonObject.getJSONObject("4"),"adherence");
        String viral_load = getJSONObjectValue(jsonObject.getJSONObject("5"),"viral_load");
        String complaints = getJSONObjectValue(jsonObject.getJSONObject("6"),"complaints");
        String other_complaints = getJSONObjectValue(jsonObject.getJSONObject("7"),"other_complaints");
        String reference_reason = getJSONObjectValue(jsonObject.getJSONObject("8"),"reference_reason");
        String client_representative = getJSONObjectValue(jsonObject.getJSONObject("9"),"client_representative");
        String discontinue_reason = getJSONObjectValue(jsonObject.getJSONObject("10"),"discontinue_reason");
        String admission_since_last_visit = getJSONObjectValue(jsonObject.getJSONObject("11"),"admission_since_last_visit");
        String pharmacist_name = getJSONObjectValue(jsonObject.getJSONObject("12"),"pharmacist_name");
        String other_drugs = getJSONObjectValue(jsonObject.getJSONObject("13"),"other_drugs");
        String next_facility_visit = getJSONObjectValue(jsonObject.getJSONObject("14"),"next_facility_visit");
        String regimen = (String)jsonObject.getJSONObject("regimen").getJSONObject("coding").get("code");
        System.out.println(visit_date+ " "+ next_visit_date+ " " + medicine_picked+" "+ clinical_status+" " + adherence+" "+ viral_load+" "+complaints+" "+
                other_complaints+" "+ reference_reason+" " + client_representative+" "+ discontinue_reason+" " + admission_since_last_visit+ " " + pharmacist_name+" " +
                other_drugs+ " " + next_facility_visit+" " + regimen);
    }

    private HashMap getARTAccessRecordsConcepts(){
        HashMap<String,Integer> map = new HashMap<>();
        map.put("visit_date",null);
        map.put("next_visit_date",5096);
        map.put("medicine_picked",null);
        map.put("clinical_status",null);
        map.put("adherence",90221);
        map.put("viral_load",856);
        map.put("complaints",90227); // side effects
        map.put("other_complaints",99113); // other side effects
        map.put("reference_reason",null);
        map.put("client_representative",null);
        map.put("discontinue_reason",164975); // other reason for next appointment
        map.put("admission_since_last_visit",null);
        map.put("pharmacist_name",null);
        map.put("other_drugs",99035);
        map.put("next_facility_visit",null);
        map.put("regimen",90315);

        return map;
    }

    private String getJSONObjectValue(JSONObject jsonObject,String objectName){
        String value = "";
        if(jsonObject!=null){
          value =(String) jsonObject.getJSONObject(objectName).getJSONObject("coding").get("code");
        }
        return value;
    }

}
