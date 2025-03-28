package org.openmrs.module.ugandaemrsync.page.controller;

import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openmrs.api.context.Context;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.ugandaemrsync.api.UgandaEMRSyncService;
import org.openmrs.module.ugandaemrsync.model.SyncFhirProfile;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;

import static org.openmrs.module.ugandaemrsync.server.SyncConstant.FHIR_FILTER_OBJECT_STRING;

public class SyncFhirProfilePageController {

    protected final org.apache.commons.logging.Log log = LogFactory.getLog(SyncTaskPageController.class);

    public SyncFhirProfilePageController() {
    }

    public void controller(@SpringBean PageModel pageModel, @RequestParam(value = "breadcrumbOverride", required = false) String breadcrumbOverride, UiSessionContext sessionContext, PageModel model, UiUtils ui) {
        UgandaEMRSyncService ugandaEMRSyncService = Context.getService(UgandaEMRSyncService.class);

        pageModel.put("syncFhirProfiles", ugandaEMRSyncService.getAllSyncFhirProfile());
        pageModel.put("patientIdentifierType", Context.getPatientService().getAllPatientIdentifierTypes());
        pageModel.put("breadcrumbOverride", breadcrumbOverride);
    }

    public void post(@SpringBean PageModel pageModel, @RequestParam(value = "returnUrl", required = false) String returnUrl,
                     @RequestParam(value = "profileId", required = false) String profileId,
                     @RequestParam(value = "syncFhirProfileName", required = false) String syncFhirProfileName,
                     @RequestParam(value = "profileEnabled", required = false, defaultValue = "false") String profileEnabled,
                     @RequestParam(value = "resourceType", required = false) String resourceType,
                     @RequestParam(value = "dataToSyncStartDate", required = false) Date dataToSyncStartDate,
                     @RequestParam(value = "durationToKeepSyncedResources", required = false) Integer durationToKeepSyncedResources,
                     @RequestParam(value = "generateBundle", required = false, defaultValue = "false") String generateBundle,
                     @RequestParam(value = "syncDataEverSince", required = false, defaultValue = "false") String syncDataEverSince,
                     @RequestParam(value = "isCaseBasedProfile", required = false, defaultValue = "false") String isCaseBasedProfile,
                     @RequestParam(value = "caseBasedPrimaryResourceType", required = false) String caseBasedPrimaryResourceType,
                     @RequestParam(value = "caseBasedPrimaryResourceTypeId", required = false) String caseBasedPrimaryResourceUUID,
                     @RequestParam(value = "patientIdentifierType", required = false) String patientIdentifierType,
                     @RequestParam(value = "noOfResourcesInBundle", required = false) Integer noOfResourcesInBundle,
                     @RequestParam(value = "encounterTypeUUIDS", required = false) String encounterTypeUUIDS,
                     @RequestParam(value = "observationCodeUUIDS", required = false) ArrayList observationCodeUUIDs,
                     @RequestParam(value = "medicationRequestCodeUUIDS", required = false) ArrayList medicationRequestCodeUUIDS,
                     @RequestParam(value = "medicationDispenseCodeUUIDS", required = false) ArrayList medicationDispenseCodeUUIDS,
                     @RequestParam(value = "conditionCodeUUIDS", required = false) ArrayList conditionCodeUUIDS,
                     @RequestParam(value = "diagnosticReportCodeUUIDS", required = false) ArrayList diagnosticReportCodeUUIDS,
                     @RequestParam(value = "episodeOfCareUUIDS", required = false) String episodeOfCareUUIDS,
                     @RequestParam(value = "serviceRequestCodeUUIDS", required = false) ArrayList serviceRequestCodeUUIDS,
                     @RequestParam(value = "url", required = false) String url,
                     @RequestParam(value = "searchable", required = false) String searchable,
                     @RequestParam(value = "searchURL", required = false) String searchURL,
                     @RequestParam(value = "username", required = false) String username,
                     @RequestParam(value = "password", required = false) String password,
                     @RequestParam(value = "token", required = false) String token,
                     @RequestParam(value = "syncLimit", required = false) Integer syncLimit,
                     UiSessionContext uiSessionContext, UiUtils uiUtils, HttpServletRequest request) {
        UgandaEMRSyncService ugandaEMRSyncService = Context.getService(UgandaEMRSyncService.class);

        JSONObject jsonObject=new JSONObject(FHIR_FILTER_OBJECT_STRING);
        if(jsonObject.has("encounterFilter") && !encounterTypeUUIDS.equals("") && encounterTypeUUIDS.split(",").length > 0){
            jsonObject.getJSONObject("encounterFilter").put("type",new JSONArray(encounterTypeUUIDS.split(",")));
        }

        if(jsonObject.has("episodeofcareFilter") && !episodeOfCareUUIDS.equals("") && episodeOfCareUUIDS.split(",").length > 0){
            jsonObject.getJSONObject("episodeofcareFilter").put("type",new JSONArray(episodeOfCareUUIDS.split(",")));
        }

        if(jsonObject.has("observationFilter") && !observationCodeUUIDs.isEmpty()){
            jsonObject.getJSONObject("observationFilter").put("code",new JSONArray(observationCodeUUIDs));
        }

        if(jsonObject.has("medicationdispenseFilter") && !medicationDispenseCodeUUIDS.isEmpty()){
            jsonObject.getJSONObject("medicationdispenseFilter").put("code",new JSONArray(medicationDispenseCodeUUIDS));
        }

        if(jsonObject.has("medicationrequestFilter") && !medicationRequestCodeUUIDS.isEmpty()){
            jsonObject.getJSONObject("medicationrequestFilter").put("code",new JSONArray(medicationRequestCodeUUIDS));
        }

        if(jsonObject.has("diagnosticreportFilter") && !diagnosticReportCodeUUIDS.isEmpty()){
            jsonObject.getJSONObject("diagnosticreportFilter").put("code",new JSONArray(diagnosticReportCodeUUIDS));
        }

        if(jsonObject.has("conditionFilter") && !conditionCodeUUIDS.isEmpty()){
            jsonObject.getJSONObject("conditionFilter").put("code",new JSONArray(conditionCodeUUIDS));
        }

        if(jsonObject.has("servicerequestFilter") && !serviceRequestCodeUUIDS.isEmpty()){
            jsonObject.getJSONObject("servicerequestFilter").put("code",new JSONArray(serviceRequestCodeUUIDS));
        }

        SyncFhirProfile syncFhirProfile;

        if (profileId.equals("")) {
            syncFhirProfile = new SyncFhirProfile();
            syncFhirProfile.setCreator(Context.getAuthenticatedUser());
            syncFhirProfile.setDateCreated(new Date());
        } else {
            syncFhirProfile = Context.getService(UgandaEMRSyncService.class).getSyncFhirProfileByUUID(profileId);
            syncFhirProfile.setDateChanged(new Date());
            syncFhirProfile.setChangedBy(Context.getAuthenticatedUser());
        }

        syncFhirProfile.setName(syncFhirProfileName);
        syncFhirProfile.setProfileEnabled(Boolean.parseBoolean(profileEnabled));
        syncFhirProfile.setGenerateBundle(Boolean.parseBoolean(generateBundle));
        syncFhirProfile.setSyncDataEverSince(Boolean.parseBoolean(syncDataEverSince));
        syncFhirProfile.setDataToSyncStartDate(dataToSyncStartDate);
        syncFhirProfile.setNumberOfResourcesInBundle(noOfResourcesInBundle);
        syncFhirProfile.setResourceTypes(resourceType);
        syncFhirProfile.setDurationToKeepSyncedResources(durationToKeepSyncedResources);
        syncFhirProfile.setIsCaseBasedProfile(Boolean.parseBoolean(isCaseBasedProfile));
        syncFhirProfile.setCaseBasedPrimaryResourceType(caseBasedPrimaryResourceType);
        syncFhirProfile.setCaseBasedPrimaryResourceTypeId(caseBasedPrimaryResourceUUID);
        syncFhirProfile.setPatientIdentifierType(Context.getPatientService().getPatientIdentifierTypeByUuid(patientIdentifierType));
        syncFhirProfile.setResourceSearchParameter(jsonObject.toString());
        syncFhirProfile.setUrl(url);
        syncFhirProfile.setUrlUserName(username);
        syncFhirProfile.setUrlPassword(password);
        syncFhirProfile.setUrlToken(token);
        syncFhirProfile.setSyncLimit(syncLimit);
        syncFhirProfile.setSearchable(Boolean.parseBoolean(searchable));
        syncFhirProfile.setSearchURL(searchURL);
        ugandaEMRSyncService.saveSyncFhirProfile(syncFhirProfile);

        pageModel.put("syncFhirProfiles", ugandaEMRSyncService.getAllSyncFhirProfile());
        pageModel.put("patientIdentifierType", Context.getPatientService().getAllPatientIdentifierTypes());
    }
}
