package org.openmrs.module.ugandaemrsync.server;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.DateRangeParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ServiceContext;
import org.openmrs.module.fhir2.api.FhirPatientService;
import org.openmrs.module.fhir2.api.FhirEncounterService;
import org.openmrs.module.fhir2.api.FhirPersonService;
import org.openmrs.module.fhir2.api.FhirObservationService;
import org.openmrs.module.fhir2.api.FhirPractitionerService;
import org.openmrs.module.ugandaemrsync.api.UgandaEMRHttpURLConnection;
import org.openmrs.module.ugandaemrsync.api.UgandaEMRSyncService;
import org.openmrs.module.ugandaemrsync.model.SyncTaskType;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Arrays;

import static org.openmrs.module.ugandaemrsync.server.SyncConstant.*;

/**
 * Created by lubwamasamuel on 07/11/2016.
 */
public class SyncFHIRRecord {

    UgandaEMRHttpURLConnection ugandaEMRHttpURLConnection = new UgandaEMRHttpURLConnection();

    Log log = LogFactory.getLog(SyncFHIRRecord.class);

    String healthCenterIdentifier;
    String lastSyncDate;


    public SyncFHIRRecord() {
        healthCenterIdentifier = Context.getAdministrationService().getGlobalProperty(GP_DHIS2);
        lastSyncDate = Context.getAdministrationService().getGlobalProperty(LAST_SYNC_DATE);
    }

    private List getDatabaseRecordWithOutFacility(String query, String from, String to, int datesToBeReplaced, List<String> columns) {
        SyncGlobalProperties syncGlobalProperties = new SyncGlobalProperties();
        UgandaEMRSyncService ugandaEMRSyncService = Context.getService(UgandaEMRSyncService.class);
        String lastSyncDate = syncGlobalProperties.getGlobalProperty(LAST_SYNC_DATE);

        String finalQuery;
        if (datesToBeReplaced == 1) {
            finalQuery = String.format(query, lastSyncDate, from, to);
        } else if (datesToBeReplaced == 2) {
            finalQuery = String.format(query, lastSyncDate, lastSyncDate, from, to);
        } else if (datesToBeReplaced == 3) {
            finalQuery = String.format(query, lastSyncDate, lastSyncDate, lastSyncDate, from, to);
        } else {
            finalQuery = String.format(query, from, to);
        }
        List list = ugandaEMRSyncService.getFinalList(columns, finalQuery);
        return list;
    }

    private List getDatabaseRecord(String query) {
        Session session = Context.getRegisteredComponent("sessionFactory", SessionFactory.class).getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(query);
        return sqlQuery.list();
    }


    public List<Map> processFHIRData(List<String> dataToProcess, String dataType, boolean addOrganizationToRecord) throws Exception {
        List<Map> maps = new ArrayList<>();
        SyncTaskType syncTaskType = Context.getService(UgandaEMRSyncService.class).getSyncTaskTypeByUUID(FHIRSERVER_SYNC_TASK_TYPE_UUID);

        FhirPersonService fhirPersonService;
        FhirPatientService fhirPatientService;
        FhirPractitionerService fhirPractitionerService;
        FhirEncounterService fhirEncounterService;
        FhirObservationService fhirObservationService;


        try {
            Field serviceContextField = Context.class.getDeclaredField("serviceContext");
            serviceContextField.setAccessible(true);
            try {
                ApplicationContext applicationContext = ((ServiceContext) serviceContextField.get(null))
                        .getApplicationContext();

                fhirPersonService = applicationContext.getBean(FhirPersonService.class);
                fhirPatientService = applicationContext.getBean(FhirPatientService.class);
                fhirEncounterService = applicationContext.getBean(FhirEncounterService.class);
                fhirObservationService = applicationContext.getBean(FhirObservationService.class);
                fhirPractitionerService = applicationContext.getBean(FhirPractitionerService.class);


            } finally {
                serviceContextField.setAccessible(false);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (String data : dataToProcess) {
            try {

                IParser parser = FhirContext.forR4().newJsonParser();
                String jsonData = "";

                if (dataType == "Patient") {
                    jsonData = parser.encodeResourceToString(fhirPatientService.get(data));
                } else if (dataType.equals("Person")) {
                    jsonData = parser.encodeResourceToString(fhirPersonService.get(data));
                } else if (dataType.equals("Encounter")) {
                    jsonData = parser.encodeResourceToString(fhirEncounterService.get(data));
                } else if (dataType.equals("Observation")) {
                    jsonData = parser.encodeResourceToString(fhirObservationService.get(data));
                } else if (dataType.equals("Practitioner")) {
                    jsonData = parser.encodeResourceToString(fhirPractitionerService.get(data));
                }

                if (!jsonData.equals("")) {
                    if (addOrganizationToRecord) {
                        jsonData = addOrganizationToRecord(jsonData);
                    }
                    Map map = ugandaEMRHttpURLConnection.sendPostBy(syncTaskType.getUrl() + dataType, syncTaskType.getUrlUserName(), syncTaskType.getUrlPassword(), "", jsonData, false);
                    map.put("DataType", dataType);
                    map.put("uuid", data);
                    maps.add(map);
                }

            } catch (Exception e) {
                log.error(e);
            }


        }
        return maps;
    }

    public String addOrganizationToRecord(String payload) {
        if (payload.isEmpty()) {
            return "";
        }

        String managingOrganizationStirng = String.format("{\"reference\": \"Organization/%s\"}", healthCenterIdentifier);
        JSONObject finalPayLoadJson = new JSONObject(payload);
        JSONObject managingOrganizationJson = new JSONObject(managingOrganizationStirng);

        finalPayLoadJson.put("managingOrganization", managingOrganizationJson);
        return finalPayLoadJson.toString();
    }

    public String wrapResourceInPostRequest(String payload) {
        if (payload.isEmpty()) {
            return "";
        }

        String requestString = "{\"resource\":%s,\"request\":{\"method\":\"POST\"}}";

        String wrappedResourceInRequest = String.format(requestString, payload);

        return wrappedResourceInRequest;
    }


    public List<String> proccessBuldeFHIRResources(String resourceType, String lastUpdateOnDate) {

        String finalQuery;

        StringBuilder currentBundleString = new StringBuilder();
        Integer currentNumberOfBundlesCollected = 0;
        Integer interval = 1000;
        List<String> resourceBundles = new ArrayList<>();

        DateRangeParam lastUpdated = new DateRangeParam().setUpperBoundInclusive(new Date()).setLowerBound(lastUpdateOnDate);
        IParser iParser = FhirContext.forR4().newJsonParser();
        IBundleProvider results = null;
        List<String> jsoStrings = new ArrayList<>();

        String bundleWrapperString = "{\"resourceType\":\"Bundle\",\"type\":\"transaction\",\"entry\":%s}";

        FhirPersonService fhirPersonService;
        FhirPatientService fhirPatientService;
        FhirPractitionerService fhirPractitionerService;
        FhirEncounterService fhirEncounterService;
        FhirObservationService fhirObservationService;


        try {
            Field serviceContextField = Context.class.getDeclaredField("serviceContext");
            serviceContextField.setAccessible(true);
            try {
                ApplicationContext applicationContext = ((ServiceContext) serviceContextField.get(null))
                        .getApplicationContext();

                fhirPersonService = applicationContext.getBean(FhirPersonService.class);
                fhirPatientService = applicationContext.getBean(FhirPatientService.class);
                fhirEncounterService = applicationContext.getBean(FhirEncounterService.class);
                fhirObservationService = applicationContext.getBean(FhirObservationService.class);
                fhirPractitionerService = applicationContext.getBean(FhirPractitionerService.class);


            } finally {
                serviceContextField.setAccessible(false);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        if (resourceType == "Patient") {
            results = fhirPatientService.searchForPatients(null, null, null, null, null, null, null, null, null,
                    null, null, null, null, lastUpdated, null, null);
        } else if (resourceType.equals("Person")) {
            results = fhirPersonService.searchForPeople(null, null, null, null,
                    null, null, null, null, lastUpdated, null, null);
        } else if (resourceType.equals("Encounter")) {
            results = fhirEncounterService.searchForEncounters(null,
                    null, null, null, null, lastUpdated, null, null);
        } else if (resourceType.equals("Observation")) {
            results = fhirObservationService.searchForObservations(null,
                    null, null, null,
                    null, null, null,
                    null, null, null, null, lastUpdated, null, null, null);
        } else if (resourceType.equals("Practitioner")) {
            results = fhirPractitionerService.searchForPractitioners(null, null, null, null, null,
                    null, null, null, null, lastUpdated, null);
        }

        List<IBaseResource> iBaseResources = results.getResources(0, Integer.MAX_VALUE);

        for (IBaseResource iBaseResource : iBaseResources) {
            String jsonString = iParser.encodeResourceToString(iBaseResource);
            if (resourceType.equals("Patient") || resourceType.equals("Practitioner")) {
                addOrganizationToRecord(jsonString);
            }
            jsonString = wrapResourceInPostRequest(jsonString);


            if (currentNumberOfBundlesCollected < interval) {
                currentBundleString.append(jsonString);
                currentNumberOfBundlesCollected++;
            } else {
                if (!currentBundleString.toString().equals("")) {
                    resourceBundles.add(String.format(bundleWrapperString, currentBundleString.toString()));
                }
                currentNumberOfBundlesCollected = 1;
                currentBundleString = new StringBuilder();
                currentBundleString.append(jsonString);
            }
        }

        if (currentNumberOfBundlesCollected < interval) {
            resourceBundles.add(String.format(bundleWrapperString, currentBundleString.toString()));
        }

        return resourceBundles;
    }

    public List<Map> sendFHIRBundleObject(String resourceType, JSONObject filterObject) {
        SyncTaskType syncTaskType = Context.getService(UgandaEMRSyncService.class).getSyncTaskTypeByUUID(FHIRSERVER_SYNC_TASK_TYPE_UUID);

        List<Map> maps = new ArrayList<>();
        String globalProperty = Context.getAdministrationService().getGlobalProperty(String.format(LAST_SYNC_DATE_TO_FORMAT, resourceType));
        List<String> rescourceBundles = proccessBuldeFHIRResources(resourceType, globalProperty);

        for (String bundle : rescourceBundles) {
            try {
                Map map = null;
                map = ugandaEMRHttpURLConnection.sendPostBy(syncTaskType.getUrl(), syncTaskType.getUrlUserName(), syncTaskType.getUrlPassword(), "", bundle, false);
                map.put("DataType", resourceType);
                map.put("uuid", "");
                maps.add(map);
            } catch (Exception e) {
                log.error(e);
            }
        }


        return maps;
    }


    public List<Map> syncFHIRData() {

        List<Map> mapList = new ArrayList<>();

        SyncGlobalProperties syncGlobalProperties = new SyncGlobalProperties();

        if (syncGlobalProperties.getGlobalProperty(GP_ENABLE_SYNC_CBS_FHIR_DATA).equals("true")) {

            try {
                mapList.addAll(processFHIRData(getDatabaseRecordWithOutFacility(PERSON_UUID_QUERY, "", "", 3, Arrays.asList("uuid")), "Person", false));

                mapList.addAll(processFHIRData(getDatabaseRecordWithOutFacility(PRACTITIONER_UUID_QUERY, "", "", 3, Arrays.asList("uuid")), "Practitioner", true));

                mapList.addAll(processFHIRData(getDatabaseRecordWithOutFacility(PATIENT_UUID_QUERY, "", "", 3, Arrays.asList("uuid")), "Patient", true));

                mapList.addAll(processFHIRData(getDatabaseRecordWithOutFacility(ENCOUNTER_UUID_QUERY, "", "", 3, Arrays.asList("uuid")), "Encounter", false));

                mapList.addAll(processFHIRData(getDatabaseRecordWithOutFacility(OBSERVATION_UUID_QUERY, "", "", 2, Arrays.asList("uuid")), "Observation", false));

                Date now = new Date();
                if (!mapList.isEmpty()) {
                    String newSyncDate = SyncConstant.DEFAULT_DATE_FORMAT.format(now);

                    syncGlobalProperties.setGlobalProperty(SyncConstant.LAST_SYNC_DATE, newSyncDate);
                }
            } catch (Exception e) {
                log.error("Failed to process sync records central server", e);
            }
        } else {
            Map map = new HashMap();
            map.put("error", "Syncing of CBS Data is not enabled. Please enable it and proceed");
            mapList.add(map);
        }

        return mapList;
    }
}
