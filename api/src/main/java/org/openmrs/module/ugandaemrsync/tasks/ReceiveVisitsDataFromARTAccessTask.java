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
import org.openmrs.module.ugandaemrsync.model.SyncTaskType;
import org.openmrs.module.ugandaemrsync.server.SyncGlobalProperties;
import org.openmrs.scheduler.tasks.AbstractTask;


import java.util.*;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.openmrs.module.ugandaemrsync.UgandaEMRSyncConfig.*;
import static org.openmrs.module.ugandaemrsync.server.SyncConstant.*;

public class ReceiveVisitsDataFromARTAccessTask extends AbstractTask {
    protected final Log log = LogFactory.getLog(ReceiveVisitsDataFromARTAccessTask.class);

    SyncGlobalProperties syncGlobalProperties = new SyncGlobalProperties();
    UgandaEMRSyncService ugandaEMRSyncService = Context.getService(UgandaEMRSyncService.class);
    ConceptService conceptService = Context.getConceptService();
    VisitService visitService = Context.getVisitService();
    EncounterService encounterService =Context.getEncounterService();
    LocationService locationService = Context.getLocationService();
    Location pharmacyLocation = locationService.getLocationByUuid("3ec8ff90-3ec1-408e-bf8c-22e4553d6e17");


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
//            results = ugandaEMRHttpURLConnection.getJson(ARTAccessServerUrlEndPoint);
            results = "{\n" +
                    "    \"resourceType\": \"Bundle\",\n" +
                    "    \"id\": \"Bundle Transaction\",\n" +
                    "    \"type\": \"transaction\",\n" +
                    "    \"meta\": {\n" +
                    "      \"lastUpdated\": \"2021-06-26T02:42:59.000000Z\"\n" +
                    "    },\n" +
                    "    \"entry\": [\n" +
                    "      {\n" +
                    "        \"resourceType\": \"Bundle\",\n" +
                    "        \"id\": \"Bundle Transaction\",\n" +
                    "        \"meta\": {\n" +
                    "          \"lastUpdated\": \"2021-05-31T10:48:57.000000Z\"\n" +
                    "        },\n" +
                    "        \"type\": \"transaction\",\n" +
                    "        \"active\": true,\n" +
                    "        \"entry\": [\n" +
                    "          {\n" +
                    "            \"fullUrl\": \"http:\\/\\/52.12.36.20:8070\",\n" +
                    "            \"resource\": {\n" +
                    "              \"resourceType\": \"Patient\",\n" +
                    "              \"identifier\": [\n" +
                    "                {\n" +
                    "                  \"type\": {\n" +
                    "                    \"coding\": {\n" +
                    "                      \"0\": {\n" +
                    "                        \"system\": \"http:\\/\\/52.12.36.20:8070\",\n" +
                    "                        \"code\": \"KAW\\/CP2\\/04216\",\n" +
                    "                        \"display\": \"ART Number\"\n" +
                    "                      },\n" +
                    "                      \"text\": \"ART Number\"\n" +
                    "                    },\n" +
                    "                    \"value\": \"KAW\\/CP2\\/04216\"\n" +
                    "                  },\n" +
                    "                  \"assigner\": {\n" +
                    "                    \"identifier\": \"KAW\\/CP2\\/04216\",\n" +
                    "                    \"type\": \"Organization\"\n" +
                    "                  }\n" +
                    "                }\n" +
                    "              ],\n" +
                    "              \"name\": [\n" +
                    "                {\n" +
                    "                  \"use\": \"Official\",\n" +
                    "                  \"family\": \"KWIZERA \",\n" +
                    "                  \"given\": [\n" +
                    "                    \"KWIZERA \"\n" +
                    "                  ]\n" +
                    "                }\n" +
                    "              ],\n" +
                    "              \"gender\": \"Male\",\n" +
                    "              \"age\": \"42\"\n" +
                    "            },\n" +
                    "            \"request\": {\n" +
                    "              \"method\": \"POST\",\n" +
                    "              \"url\": \"http:\\/\\/52.12.36.20:8070KAW\\/CP2\\/04216\"\n" +
                    "            }\n" +
                    "          },\n" +
                    "          {\n" +
                    "            \"resourceType\": \"Observation\",\n" +
                    "            \"status\": \"final\",\n" +
                    "            \"code\": {\n" +
                    "              \"coding\": [\n" +
                    "                {\n" +
                    "                  \"system\": \"http:\\/\\/52.12.36.20:8070\",\n" +
                    "                  \"code\": \"KAW\\/CP2\\/04216\",\n" +
                    "                  \"display\": \"ART Number\"\n" +
                    "                }\n" +
                    "              ],\n" +
                    "              \"text\": \"ART Start Date\"\n" +
                    "            },\n" +
                    "            \"subject\": {\n" +
                    "              \"reference\": \"Patient\\/KAW\\/CP2\\/04216\"\n" +
                    "            },\n" +
                    "            \"encounter\": {\n" +
                    "              \"reference\": \"Encounter\\/KAW\\/CP2\\/04216\"\n" +
                    "            },\n" +
                    "            \"issued\": \"2021-05-31 13:50:52\",\n" +
                    "            \"valueDateTime\": \"2021-05-31 13:50:52\"\n" +
                    "          },\n" +
                    "          {\n" +
                    "            \"resourceType\": \"Encounter\",\n" +
                    "            \"id\": 557,\n" +
                    "            \"text\": {\n" +
                    "              \"status\": \"generated\"\n" +
                    "            },\n" +
                    "            \"identifier\": [\n" +
                    "              {\n" +
                    "                \"use\": \"temp\",\n" +
                    "                \"value\": \"Encounter\\/KAW\\/CP2\\/04216\"\n" +
                    "              }\n" +
                    "            ],\n" +
                    "            \"class\": {\n" +
                    "              \"system\": \"Patient\\/KAW\\/CP2\\/04216\",\n" +
                    "              \"code\": \"ART_refill\",\n" +
                    "              \"display\": \"Art refill\"\n" +
                    "            },\n" +
                    "            \"type\": [\n" +
                    "              {\n" +
                    "                \"coding\": {\n" +
                    "                  \"0\": {\n" +
                    "                    \"system\": \"http:\\/\\/52.12.36.20:8070\",\n" +
                    "                    \"code\": \"KAW\\/CP2\\/04216\",\n" +
                    "                    \"display\": \"ART Number\"\n" +
                    "                  },\n" +
                    "                  \"text\": \"ART Number\"\n" +
                    "                }\n" +
                    "              }\n" +
                    "            ],\n" +
                    "            \"priority\": {\n" +
                    "              \"coding\": [\n" +
                    "                {\n" +
                    "                  \"system\": \"http:\\/\\/52.12.36.20:8070\",\n" +
                    "                  \"code\": \"KAW\\/CP2\\/04216\",\n" +
                    "                  \"display\": \"ART Number\"\n" +
                    "                }\n" +
                    "              ]\n" +
                    "            },\n" +
                    "            \"subject\": {\n" +
                    "              \"reference\": \"Patient\\/KAW\\/CP2\\/04216\",\n" +
                    "              \"display\": \"Roel\"\n" +
                    "            },\n" +
                    "            \"reasonCode\": [\n" +
                    "              {\n" +
                    "                \"text\": \"Patient Needs a Refill\"\n" +
                    "              }\n" +
                    "            ],\n" +
                    "            \"serviceProvider\": {\n" +
                    "              \"reference\": \"Organization\\/IDI\"\n" +
                    "            }\n" +
                    "          },\n" +
                    "          {\n" +
                    "            \"resourceType\": \"Encounter\",\n" +
                    "            \"id\": 557,\n" +
                    "            \"text\": {\n" +
                    "              \"status\": \"generated\"\n" +
                    "            },\n" +
                    "            \"identifier\": [\n" +
                    "              {\n" +
                    "                \"use\": \"temp\",\n" +
                    "                \"value\": \"Encounter\\/KAW\\/CP2\\/04216\"\n" +
                    "              }\n" +
                    "            ],\n" +
                    "            \"class\": {\n" +
                    "              \"system\": \"Patient\\/KAW\\/CP2\\/04216\",\n" +
                    "              \"code\": \"ART_refill\",\n" +
                    "              \"display\": \"Art refill\"\n" +
                    "            },\n" +
                    "            \"type\": [\n" +
                    "              {\n" +
                    "                \"coding\": {\n" +
                    "                  \"0\": {\n" +
                    "                    \"system\": \"http:\\/\\/52.12.36.20:8070\",\n" +
                    "                    \"code\": \"KAW\\/CP2\\/04216\",\n" +
                    "                    \"display\": \"ART Number\"\n" +
                    "                  },\n" +
                    "                  \"text\": \"ART Number\"\n" +
                    "                }\n" +
                    "              }\n" +
                    "            ],\n" +
                    "            \"priority\": {\n" +
                    "              \"coding\": [\n" +
                    "                {\n" +
                    "                  \"system\": \"http:\\/\\/52.12.36.20:8070\",\n" +
                    "                  \"code\": \"KAW\\/CP2\\/04216\",\n" +
                    "                  \"display\": \"ART Number\"\n" +
                    "                }\n" +
                    "              ]\n" +
                    "            },\n" +
                    "            \"subject\": {\n" +
                    "              \"reference\": \"Patient\\/KAW\\/CP2\\/04216\",\n" +
                    "              \"display\": \"Roel\"\n" +
                    "            },\n" +
                    "            \"reasonCode\": [\n" +
                    "              {\n" +
                    "                \"text\": \"Patient Needs a Refill\"\n" +
                    "              }\n" +
                    "            ],\n" +
                    "            \"serviceProvider\": {\n" +
                    "              \"reference\": \"Organization\\/IDI\"\n" +
                    "            },\n" +
                    "            \"regimen\": {\n" +
                    "              \"coding\": {\n" +
                    "                \"system\": \"http:\\/\\/52.12.36.20:8070\",\n" +
                    "                \"code\": \"TDF\\/3TC\\/EFV\",\n" +
                    "                \"display\": \"regimen\"\n" +
                    "              },\n" +
                    "              \"subject\": {\n" +
                    "                \"reference\": \"Patient\\/KAW\\/CP2\\/04216\",\n" +
                    "                \"regimen\": \"TDF\\/3TC\\/EFV\"\n" +
                    "              }\n" +
                    "            },\n" +
                    "            \"0\": {\n" +
                    "              \"visit_date\": {\n" +
                    "                \"coding\": {\n" +
                    "                  \"system\": \"http:\\/\\/52.12.36.20:8070\",\n" +
                    "                  \"code\": \"2021-05-31 13:50:52\",\n" +
                    "                  \"display\": \"visit date\"\n" +
                    "                },\n" +
                    "                \"period\": \"2021-05-31 13:50:52\"\n" +
                    "              }\n" +
                    "            },\n" +
                    "            \"1\": {\n" +
                    "              \"next_visit_date\": {\n" +
                    "                \"coding\": {\n" +
                    "                  \"system\": \"http:\\/\\/52.12.36.20:8070\",\n" +
                    "                  \"code\": \"2021-11-22\",\n" +
                    "                  \"display\": \"Next visit date\"\n" +
                    "                },\n" +
                    "                \"period\": \"2021-11-22\"\n" +
                    "              }\n" +
                    "            },\n" +
                    "            \"2\": {\n" +
                    "              \"medicine_picked\": {\n" +
                    "                \"coding\": {\n" +
                    "                  \"system\": \"http:\\/\\/52.12.36.20:8070\",\n" +
                    "                  \"code\": null,\n" +
                    "                  \"display\": \"Medicine Picked\"\n" +
                    "                },\n" +
                    "                \"text\": null\n" +
                    "              }\n" +
                    "            },\n" +
                    "            \"3\": {\n" +
                    "              \"clinical_status\": {\n" +
                    "                \"coding\": {\n" +
                    "                  \"system\": \"http:\\/\\/52.12.36.20:8070\",\n" +
                    "                  \"code\": null,\n" +
                    "                  \"display\": \"Clinical Status\"\n" +
                    "                },\n" +
                    "                \"text\": null\n" +
                    "              }\n" +
                    "            },\n" +
                    "            \"4\": {\n" +
                    "              \"adherence\": {\n" +
                    "                \"coding\": {\n" +
                    "                  \"system\": \"http:\\/\\/52.12.36.20:8070\",\n" +
                    "                  \"code\": null,\n" +
                    "                  \"display\": \"Adherence\"\n" +
                    "                },\n" +
                    "                \"text\": null\n" +
                    "              }\n" +
                    "            },\n" +
                    "            \"5\": {\n" +
                    "              \"viral_load\": {\n" +
                    "                \"coding\": {\n" +
                    "                  \"system\": \"http:\\/\\/52.12.36.20:8070\",\n" +
                    "                  \"code\": null,\n" +
                    "                  \"display\": \"Viral Load\"\n" +
                    "                },\n" +
                    "                \"text\": null\n" +
                    "              }\n" +
                    "            },\n" +
                    "            \"6\": {\n" +
                    "              \"complaints\": {\n" +
                    "                \"coding\": {\n" +
                    "                  \"system\": \"http:\\/\\/52.12.36.20:8070\",\n" +
                    "                  \"code\": \"null\",\n" +
                    "                  \"display\": \"Complaints\"\n" +
                    "                },\n" +
                    "                \"text\": \"null\"\n" +
                    "              }\n" +
                    "            },\n" +
                    "            \"7\": {\n" +
                    "              \"other_complaints\": {\n" +
                    "                \"coding\": {\n" +
                    "                  \"system\": \"http:\\/\\/52.12.36.20:8070\",\n" +
                    "                  \"code\": null,\n" +
                    "                  \"display\": \"Other Complaints\"\n" +
                    "                },\n" +
                    "                \"text\": null\n" +
                    "              }\n" +
                    "            },\n" +
                    "            \"8\": {\n" +
                    "              \"reference_reason\": {\n" +
                    "                \"coding\": {\n" +
                    "                  \"system\": \"http:\\/\\/52.12.36.20:8070\",\n" +
                    "                  \"code\": null,\n" +
                    "                  \"display\": \"Reference reason\"\n" +
                    "                },\n" +
                    "                \"text\": null\n" +
                    "              }\n" +
                    "            },\n" +
                    "            \"9\": {\n" +
                    "              \"client_representative\": {\n" +
                    "                \"coding\": {\n" +
                    "                  \"system\": \"http:\\/\\/52.12.36.20:8070\",\n" +
                    "                  \"code\": null,\n" +
                    "                  \"display\": \"Client representative\"\n" +
                    "                },\n" +
                    "                \"text\": null\n" +
                    "              }\n" +
                    "            },\n" +
                    "            \"10\": {\n" +
                    "              \"discontinue_reason\": {\n" +
                    "                \"coding\": {\n" +
                    "                  \"system\": \"http:\\/\\/52.12.36.20:8070\",\n" +
                    "                  \"code\": \"\",\n" +
                    "                  \"display\": \"Discontinue reason\"\n" +
                    "                },\n" +
                    "                \"text\": \"\"\n" +
                    "              }\n" +
                    "            },\n" +
                    "            \"11\": {\n" +
                    "              \"admission_since_last_visit\": {\n" +
                    "                \"coding\": {\n" +
                    "                  \"system\": \"http:\\/\\/52.12.36.20:8070\",\n" +
                    "                  \"code\": \"2021-11-22\",\n" +
                    "                  \"display\": \"Admission since last visit\"\n" +
                    "                },\n" +
                    "                \"text\": \"2021-11-22\"\n" +
                    "              }\n" +
                    "            },\n" +
                    "            \"12\": {\n" +
                    "              \"pharmacist_name\": {\n" +
                    "                \"coding\": {\n" +
                    "                  \"system\": \"http:\\/\\/52.12.36.20:8070\",\n" +
                    "                  \"code\": \"Test Pharmacy\",\n" +
                    "                  \"display\": \"Pharmacist name\"\n" +
                    "                },\n" +
                    "                \"text\": \"Test Pharmacy\"\n" +
                    "              }\n" +
                    "            },\n" +
                    "            \"13\": {\n" +
                    "              \"other_drugs\": {\n" +
                    "                \"coding\": {\n" +
                    "                  \"system\": \"http:\\/\\/52.12.36.20:8070\",\n" +
                    "                  \"code\": null,\n" +
                    "                  \"display\": \"other drugs\"\n" +
                    "                },\n" +
                    "                \"text\": null\n" +
                    "              }\n" +
                    "            },\n" +
                    "            \"14\": {\n" +
                    "              \"next_facility_visit\": {\n" +
                    "                \"coding\": {\n" +
                    "                  \"system\": \"http:\\/\\/52.12.36.20:8070\",\n" +
                    "                  \"code\": \"2021-11-22\",\n" +
                    "                  \"display\": \"Next facility visit\"\n" +
                    "                },\n" +
                    "                \"text\": \"2021-11-22\"\n" +
                    "              }\n" +
                    "            }\n" +
                    "          }\n" +
                    "        ]\n" +
                    "      }\n" +
                    "    ]\n" +
                    "}";
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
                processARTAccessData(patientEncounterDetails,patient);
           }

          }
      }

    }

    private void processARTAccessData(JSONObject jsonObject,Patient patient){
        HashMap conceptsCaptured = getARTAccessRecordsConcepts();
        UserService userService = Context.getUserService();
        User user = userService.getUserByUuid("9bd6584f-33e0-11e7-9528-1866da16840d");

        Set<Obs> obsList =new HashSet<>();

        try {
            String visit_date = getJSONObjectValue(jsonObject.getJSONObject("0"), "visit_date");
            String dateFormat = ugandaEMRSyncService.getDateFormat(visit_date);
            Date startVisitDate = ugandaEMRSyncService.convertStringToDate(visit_date, "00:00:00", dateFormat);
            Date stopVisitDate = ugandaEMRSyncService.convertStringToDate(visit_date, "23:59:59", dateFormat);

            String next_visit_date = getJSONObjectValue(jsonObject.getJSONObject("1"), "next_visit_date");
            Date return_date = ugandaEMRSyncService.convertStringToDate(next_visit_date, "00:00:00", ugandaEMRSyncService.getDateFormat(next_visit_date));
            if(next_visit_date!=""&&next_visit_date!=null) {
                addObs(obsList, next_visit_date, conceptService.getConcept((int) conceptsCaptured.get("next_visit_date")), null, return_date, null, patient, user, startVisitDate);
            }

            String adherence = getJSONObjectValue(jsonObject.getJSONObject("4"),"adherence");
            Concept adherence_concept = conceptService.getConcept((int)conceptsCaptured.get("adherence"));
            Concept adherence_answer = convertAdherence(adherence);
            addObs(obsList,adherence,adherence_concept, adherence_answer, null, null, patient, user, new Date());

            Visit visit =createVisit(patient,startVisitDate,stopVisitDate,user, pharmacyLocation);
            Encounter encounter = createEncounter(patient,startVisitDate,user, pharmacyLocation);
            encounter.setObs(obsList);
            encounter.setVisit(visit);
            encounterService.saveEncounter(encounter);
            System.out.println("Visit for patient: "+ visit.getPatient().getId()+ "saved successfully");


//            String medicine_picked = getJSONObjectValue(jsonObject.getJSONObject("2"),"medicine_picked");
//        String clinical_status = getJSONObjectValue(jsonObject.getJSONObject("3"),"clinical_status");
////        String adherence = getJSONObjectValue(jsonObject.getJSONObject("4"),"adherence");
//        String viral_load = getJSONObjectValue(jsonObject.getJSONObject("5"),"viral_load");
//        String complaints = getJSONObjectValue(jsonObject.getJSONObject("6"),"complaints");
//        String other_complaints = getJSONObjectValue(jsonObject.getJSONObject("7"),"other_complaints");
//        String reference_reason = getJSONObjectValue(jsonObject.getJSONObject("8"),"reference_reason");
//        String client_representative = getJSONObjectValue(jsonObject.getJSONObject("9"),"client_representative");
//        String discontinue_reason = getJSONObjectValue(jsonObject.getJSONObject("10"),"discontinue_reason");
//        String admission_since_last_visit = getJSONObjectValue(jsonObject.getJSONObject("11"),"admission_since_last_visit");
//        String pharmacist_name = getJSONObjectValue(jsonObject.getJSONObject("12"),"pharmacist_name");
//        String other_drugs = getJSONObjectValue(jsonObject.getJSONObject("13"),"other_drugs");
//        String next_facility_visit = getJSONObjectValue(jsonObject.getJSONObject("14"),"next_facility_visit");
//        String regimen = (String)jsonObject.getJSONObject("regimen").getJSONObject("coding").get("code");
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

    private Visit createVisit(Patient patient, Date startDate,Date stopDate,User creator,Location location){
        Visit visit = new Visit();
        visit.setLocation(location);
        visit.setPatient(patient);
        visit.setStartDatetime(startDate);
        visit.setStopDatetime(stopDate);
        visit.setVisitType(visitService.getVisitTypeByUuid("2ce24f40-8f4c-4bfa-8fde-09d475783468"));
        visit.setCreator(creator);
        visit.setDateCreated(new Date());
        return  visit;

    }

    private Encounter createEncounter(Patient patient,Date visitDate,User creator,Location location){
        FormService formService =Context.getFormService();
        Encounter encounter = new Encounter();
        encounter.setEncounterDatetime(visitDate);
        encounter.setEncounterType(encounterService.getEncounterTypeByUuid("8d5b2be0-c2cc-11de-8d13-0010c6dffd0f"));
        encounter.setForm(formService.getFormByUuid("12de5bc5-352e-4faf-9961-a2125085a75c"));
        encounter.setPatient(patient);
        encounter.setCreator(creator);
        encounter.setLocation(location);
        encounter.setDateCreated(new Date());
        return encounter;

    }

    private Obs processObs(Concept question, Concept valueCoded, Date valueDateTime,String valueText,Patient patient,User creator,Date visitDate){
        Obs newObs = new Obs();
        newObs.setConcept(question);
        newObs.setValueCoded(valueCoded);
        newObs.setValueText(valueText);
        newObs.setValueDatetime(valueDateTime);
        newObs.setCreator(creator);
        newObs.setObsDatetime(visitDate);
        newObs.setPerson(patient);
        newObs.setLocation(pharmacyLocation);
        return newObs;
    }
    private Obs processObs(Concept question,double valueNumeric,Patient patient,User creator,Date created){
        Obs newObs = new Obs();
        newObs.setConcept(question);
        newObs.setValueNumeric(valueNumeric);
        newObs.setCreator(creator);
        newObs.setDateCreated(created);
        newObs.setPerson(patient);
        return newObs;
    }

    private Set<Obs> addObs(Set<Obs> obsList,String artAccessValue,Concept question, Concept valueCoded, Date valueDateTime,String valueText,Patient patient,User creator,Date visitDate){
        if (artAccessValue!=null){
            obsList.add(processObs(question,valueCoded, valueDateTime,valueText,patient,creator,visitDate));
        }
        return obsList;
    }

    private Concept convertAdherence(String adherence){
        int conceptValue =0 ;
        if(adherence!=null) {
            if (adherence.contains("poor")) {
                conceptValue = 90158;
            } else if (adherence.contains("good")) {
                conceptValue = 90156;
            } else if (adherence.contains("fair")) {
                conceptValue =90157;
            } else {
                conceptValue = 90001; // unknown
            }
        }
        if(conceptValue!=0){
            return conceptService.getConcept(conceptValue);
        }else{
            return  null;
        }
    }

}
