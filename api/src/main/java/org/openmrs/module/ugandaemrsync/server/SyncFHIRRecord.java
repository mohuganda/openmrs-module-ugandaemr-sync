package org.openmrs.module.ugandaemrsync.server;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Patient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openmrs.EncounterRole;
import org.openmrs.PatientIdentifier;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ServiceContext;
import org.openmrs.module.fhir2.api.*;
import org.openmrs.module.ugandaemrsync.api.UgandaEMRHttpURLConnection;
import org.openmrs.module.ugandaemrsync.api.UgandaEMRSyncService;
import org.openmrs.module.ugandaemrsync.model.SyncFHIRProfile;
import org.openmrs.module.ugandaemrsync.model.SyncTaskType;
import org.openmrs.parameter.EncounterSearchCriteria;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Field;
import java.util.*;

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

        return groupInBundles(resourceType, results, interval, null);
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


    public Collection<String> saveFHIRBundleProfile(SyncFHIRProfile syncFHIRProfile) {
        Collection<String> stringCollection = new ArrayList<>();
        List<org.openmrs.Encounter> encounters = new ArrayList<>();

        String[] resourceTypes = syncFHIRProfile.getResourceTypes().split(",");
        for (String resource : resourceTypes) {
            switch (resource) {
                case "Encounter":
                    List<org.openmrs.EncounterType> encounterTypes = new ArrayList<>();
                    DateRangeParam encounterLastUpdated = new DateRangeParam().setUpperBoundInclusive(new Date()).setLowerBound(getSearchParametersInJsonObject("Encounter", syncFHIRProfile.getResourceSearchParameter()).getJSONObject("lastUpdated").get("lowerBound").toString());
                    JSONArray jsonArray = getSearchParametersInJsonObject("Encounter", syncFHIRProfile.getResourceSearchParameter()).getJSONArray("type");

                    for (Object jsonObject : jsonArray) {
                        encounterTypes.add(Context.getEncounterService().getEncounterTypeByUuid(jsonObject.toString()));
                    }

                    EncounterSearchCriteria encounterSearchCriteria = new EncounterSearchCriteria(null, null, encounterLastUpdated.getLowerBoundAsInstant(), encounterLastUpdated.getUpperBoundAsInstant(), null, null, encounterTypes, null, null, null, false);
                    encounters = Context.getEncounterService().getEncounters(encounterSearchCriteria);

                    stringCollection.addAll(groupInBundles("Encounter", getEncounterResourceBundle(encounters), syncFHIRProfile.getNumberOfResourcesInBundle(), null));
                    break;
                case "Observation":
                    if (encounters.size() > 0) {
                        stringCollection.addAll(groupInBundles("Observation", getObservationResourceBundle(syncFHIRProfile, encounters, getPatientIdentifierFromEncounter(encounters, syncFHIRProfile.getPatientIdentifierType())), syncFHIRProfile.getNumberOfResourcesInBundle(), null));
                    } else {
                        stringCollection.addAll(groupInBundles("Observation", getObservationResourceBundle(syncFHIRProfile, null, null), syncFHIRProfile.getNumberOfResourcesInBundle(), null));
                    }
                    break;
                case "Patient":
                    if (encounters.size() > 0) {
                        stringCollection.addAll(groupInBundles("Patient", getPatientResourceBundle(syncFHIRProfile, getPatientIdentifierFromEncounter(encounters, syncFHIRProfile.getPatientIdentifierType())), syncFHIRProfile.getNumberOfResourcesInBundle(), syncFHIRProfile.getPatientIdentifierType().getName()));
                    } else {
                        stringCollection.addAll(groupInBundles("Patient", getPatientResourceBundle(syncFHIRProfile, null), syncFHIRProfile.getNumberOfResourcesInBundle(), syncFHIRProfile.getPatientIdentifierType().getName()));
                    }
                    break;
                case "Practitioner":
                    if (encounters.size() > 0) {
                        stringCollection.addAll(groupInBundles("Practitioner", getPractitionerResourceBundle(syncFHIRProfile, encounters), syncFHIRProfile.getNumberOfResourcesInBundle(), null));
                    } else {
                        stringCollection.addAll(groupInBundles("Practitioner", getPractitionerResourceBundle(syncFHIRProfile, null), syncFHIRProfile.getNumberOfResourcesInBundle(), null));
                    }
                    break;
                case "Person":
                    if (encounters.size() > 0) {
                        stringCollection.addAll(groupInBundles("Person", getPersonResourceBundle(syncFHIRProfile, getPersonsFromEncounterList(encounters)), syncFHIRProfile.getNumberOfResourcesInBundle(), null));
                    } else {
                        stringCollection.addAll(groupInBundles("Person", getPersonResourceBundle(syncFHIRProfile, null), syncFHIRProfile.getNumberOfResourcesInBundle(), null));
                    }
                    break;
            }
        }

        return stringCollection;
    }


    private List<org.openmrs.PatientIdentifier> getPatientIdentifierFromEncounter(List<org.openmrs.Encounter> encounters, org.openmrs.PatientIdentifierType patientIdentifierType) {
        List<org.openmrs.PatientIdentifier> patientIdentifiers = new ArrayList<>();
        for (org.openmrs.Encounter encounter : encounters) {
            patientIdentifiers.add(encounter.getPatient().getPatientIdentifier(patientIdentifierType));
        }
        return patientIdentifiers;
    }

    private List<String> groupInBundles(String resourceType, IBundleProvider iBundleProvider, Integer interval, String identifierTypeName) {
        List<String> resourceBundles = new ArrayList<>();
        StringBuilder currentBundleString = new StringBuilder();
        Integer currentNumberOfBundlesCollected = 0;
        IParser iParser = FhirContext.forR4().newJsonParser();
        List<IBaseResource> iBaseResources = iBundleProvider.getResources(0, Integer.MAX_VALUE);

        for (IBaseResource iBaseResource : iBaseResources) {
            String jsonString = iParser.encodeResourceToString(iBaseResource);

            if (resourceType.equals("Patient") || resourceType.equals("Practitioner")) {
                addOrganizationToRecord(jsonString);
            }

            if (resourceType.equals("Patient") || resourceType.equals("Practitioner") || resourceType.equals("Person")) {
                JSONObject jsonObject = new JSONObject(jsonString);
                String resourceIdentifier = "";
                if (resourceType.equals("Patient") && identifierTypeName != null) {
                    for (Object identifierObject : jsonObject.getJSONArray("identifier")) {
                        JSONObject identifier = new JSONObject(identifierObject.toString());
                        if (identifier.getJSONObject("type").get("text").equals(identifierTypeName)) {
                            resourceIdentifier = identifier.get("value").toString();
                        }
                    }
                } else {
                    resourceIdentifier = jsonObject.get("id").toString();
                }

                jsonString = wrapResourceInPUTRequest(jsonString, resourceType, resourceIdentifier);
            } else {
                jsonString = wrapResourceInPostRequest(jsonString);
            }


            if (currentNumberOfBundlesCollected < interval) {
                currentBundleString.append(jsonString);
                currentNumberOfBundlesCollected++;
            } else {
                if (!currentBundleString.toString().equals("")) {
                    resourceBundles.add(String.format(FHIR_BUNDLE_RESOURCE_TRANSACTION, currentBundleString.toString()));
                }
                currentNumberOfBundlesCollected = 1;
                currentBundleString = new StringBuilder();
                currentBundleString.append(jsonString);
            }
        }

        if (currentNumberOfBundlesCollected < interval) {
            resourceBundles.add(String.format(FHIR_BUNDLE_RESOURCE_TRANSACTION, currentBundleString.toString()));
        }

        return resourceBundles;
    }

    public String wrapResourceInPostRequest(String payload) {
        if (payload.isEmpty()) {
            return "";
        }

        String wrappedResourceInRequest = String.format(FHIR_BUNDLE_RESOURCE_METHOD_POST, payload);
        return wrappedResourceInRequest;
    }

    public String wrapResourceInPUTRequest(String payload, String resourceType, String identifier) {
        if (payload.isEmpty()) {
            return "";
        }

        String wrappedResourceInRequest = String.format(FHIR_BUNDLE_RESOURCE_METHOD_PUT, payload, resourceType + "/" + identifier);
        return wrappedResourceInRequest;
    }


    private ApplicationContext getApplicationContext() {
        Field serviceContextField = null;
        ApplicationContext applicationContext = null;
        try {
            serviceContextField = Context.class.getDeclaredField("serviceContext");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        serviceContextField.setAccessible(true);

        try {
            applicationContext = ((ServiceContext) serviceContextField.get(null)).getApplicationContext();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return applicationContext;
    }

    private JSONObject getSearchParametersInJsonObject(String resourceType, String searchParameterString) {
        JSONObject jsonObject = new JSONObject(searchParameterString);
        if (jsonObject.isNull(resourceType)) {
            jsonObject = jsonObject.getJSONObject(resourceType.toLowerCase() + "Filter");
        }
        return jsonObject;
    }

    private Provider getProviderFromEncounter(org.openmrs.Encounter encounter) {
        EncounterRole encounterRole = Context.getEncounterService().getEncounterRoleByUuid(ENCOUNTER_ROLE);

        Set<Provider> providers = encounter.getProvidersByRole(encounterRole);
        List<Provider> providerList = new ArrayList<>();
        for (Provider provider : providers) {
            providerList.add(provider);
        }

        if (!providerList.isEmpty()) {
            return providerList.get(0);
        } else {
            return null;
        }
    }

    private List<org.openmrs.Person> getPersonsFromEncounterList(List<org.openmrs.Encounter> encounters) {
        EncounterRole encounterClinicianRole = Context.getEncounterService().getEncounterRoleByUuid(ENCOUNTER_ROLE);
        List<org.openmrs.Person> person = new ArrayList<>();

        for (org.openmrs.Encounter encounter : encounters) {
            person.add(encounter.getPatient().getPerson());
            for (org.openmrs.Provider provider : encounter.getProvidersByRole(encounterClinicianRole)) {
                person.add(provider.getPerson());
            }
        }
        return person;
    }


    private IBundleProvider getPatientResourceBundle(SyncFHIRProfile syncFHIRProfile, List<PatientIdentifier> patientIdentifiers) {

        JSONObject searchParams = getSearchParametersInJsonObject("Patient", syncFHIRProfile.getResourceSearchParameter());

        DateRangeParam lastUpdated = new DateRangeParam().setUpperBoundInclusive(new Date()).setLowerBound(searchParams.getJSONObject("lastUpdated").get("lowerBound").toString());

        TokenAndListParam patientReference = new TokenAndListParam();
        for (org.openmrs.PatientIdentifier patientIdentifier : patientIdentifiers) {
            TokenParam tokenParam = new TokenParam();
            tokenParam.setValue(patientIdentifier.getIdentifier());
            patientReference.addAnd(tokenParam);
        }


        return getApplicationContext().getBean(FhirPatientService.class).searchForPatients(null, null, null, patientReference, null, null, null, null, null,
                null, null, null, null, lastUpdated, null, null);
    }

    private IBundleProvider getPractitionerResourceBundle(SyncFHIRProfile syncFHIRProfile, List<org.openmrs.Encounter> encounterList) {

        JSONObject searchParams = getSearchParametersInJsonObject("Practitioner", syncFHIRProfile.getResourceSearchParameter());

        DateRangeParam lastUpdated = new DateRangeParam().setUpperBoundInclusive(new Date()).setLowerBound(searchParams.getJSONObject("lastUpdated").get("lowerBound").toString());

        TokenAndListParam patientReference = new TokenAndListParam();
        for (org.openmrs.Encounter encounter : encounterList) {
            TokenParam tokenParam = new TokenParam();
            tokenParam.setValue(getProviderFromEncounter(encounter).getUuid());
            patientReference.addAnd(tokenParam);
        }


        return getApplicationContext().getBean(FhirPractitionerService.class).searchForPractitioners(null, null, null, null, null,
                null, null, null, patientReference, lastUpdated, null);

    }

    private IBundleProvider getPersonResourceBundle(SyncFHIRProfile syncFHIRProfile, List<org.openmrs.Person> personList) {

        JSONObject searchParams = getSearchParametersInJsonObject("Person", syncFHIRProfile.getResourceSearchParameter());

        DateRangeParam lastUpdated = new DateRangeParam().setUpperBoundInclusive(new Date()).setLowerBound(searchParams.getJSONObject("lastUpdated").get("lowerBound").toString());

        TokenAndListParam personReference = new TokenAndListParam();
        for (org.openmrs.Person person : personList) {
            TokenParam tokenParam = new TokenParam();
            tokenParam.setValue(person.getUuid());
            personReference.addAnd(tokenParam);
        }

        return getApplicationContext().getBean(FhirPersonService.class).searchForPeople(null, null, null, null,
                null, null, null, personReference, lastUpdated, null, null);
    }


    private IBundleProvider getEncounterResourceBundle(List<org.openmrs.Encounter> encounters) {

        TokenAndListParam encounterReference = new TokenAndListParam();
        for (org.openmrs.Encounter encounter : encounters) {
            TokenParam tokenParam = new TokenParam();
            tokenParam.setValue(encounter.getUuid());
            encounterReference.addAnd(tokenParam);
        }

        return getApplicationContext().getBean(FhirEncounterService.class).searchForEncounters(null,
                null, null, null, encounterReference, null, null, null);
    }

    private IBundleProvider getObservationResourceBundle(SyncFHIRProfile syncFHIRProfile, List<org.openmrs.Encounter> encounterList, List<org.openmrs.PatientIdentifier> patientIdentifiers) {

        JSONObject searchParams = getSearchParametersInJsonObject("Observation", syncFHIRProfile.getResourceSearchParameter());

        JSONArray codes = searchParams.getJSONArray("code");

        DateRangeParam lastUpdated = new DateRangeParam().setUpperBoundInclusive(new Date()).setLowerBound(searchParams.getJSONObject("lastUpdated").get("lowerBound").toString());

        ReferenceAndListParam patientReference = new ReferenceAndListParam();

        for (org.openmrs.PatientIdentifier patientIdentifier : patientIdentifiers) {
            patientReference.addValue(new ReferenceOrListParam().add(new ReferenceParam(Patient.SP_IDENTIFIER, patientIdentifier.getIdentifier())));
        }

        ReferenceAndListParam encounterReference = new ReferenceAndListParam();
        for (org.openmrs.Encounter encounter : encounterList) {
            patientReference.addValue(new ReferenceOrListParam().add(new ReferenceParam(Encounter.SP_IDENTIFIER, encounter.getUuid())));
        }


        TokenAndListParam code = new TokenAndListParam();
        for (Object conceptUID : codes) {
            try {
                TokenParam codingToken = new TokenParam();
                codingToken.setValue(Context.getConceptService().getConcept(conceptUID.toString()).getConceptId().toString());
                code.addAnd(codingToken);
            } catch (Exception e) {
                log.error("Error while adding concept with uuid " + conceptUID, e);
            }

        }
        return getApplicationContext().getBean(FhirObservationService.class).searchForObservations(encounterReference, patientReference, null, null, null, null, null, null, code, null, null, lastUpdated, null, null, null);

    }

}
