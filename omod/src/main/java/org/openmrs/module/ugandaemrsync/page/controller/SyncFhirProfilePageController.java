package org.openmrs.module.ugandaemrsync.page.controller;

import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.ugandaemrsync.api.UgandaEMRSyncService;
import org.openmrs.module.ugandaemrsync.model.SyncFhirProfile;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static org.openmrs.module.ugandaemrsync.server.SyncConstant.FHIR_FILTER_OBJECT_STRING;

public class SyncFhirProfilePageController {

    protected final org.apache.commons.logging.Log log = LogFactory.getLog(SyncTaskPageController.class);

    public SyncFhirProfilePageController() {
    }

    public void controller(@SpringBean PageModel pageModel, @RequestParam(value = "breadcrumbOverride", required = false) String breadcrumbOverride, UiSessionContext sessionContext, PageModel model, UiUtils ui) {
        UgandaEMRSyncService ugandaEMRSyncService = Context.getService(UgandaEMRSyncService.class);

        pageModel.put("syncFhirProfiles", null);
        pageModel.put("patientIdentifierType", Context.getPatientService().getAllPatientIdentifierTypes());
        pageModel.put("breadcrumbOverride", breadcrumbOverride);
    }

    public void post(@SpringBean PageModel pageModel, @RequestParam(value = "returnUrl", required = false) String returnUrl,
                     @RequestParam(value = "profileId", required = false) String profileId,
                     @RequestParam(value = "syncFhirProfileName", required = false) String syncFhirProfileName,
                     @RequestParam(value = "resourceType", required = false) String resourceType,
                     @RequestParam(value = "durationToKeepSyncedResources", required = false) Integer durationToKeepSyncedResources,
                     @RequestParam(value = "generateBundle", required = false) boolean generateBundle,
                     @RequestParam(value = "isCaseBasedProfile", required = false) boolean isCaseBasedProfile,
                     @RequestParam(value = "caseBasedPrimaryResourceType", required = false) String caseBasedPrimaryResourceType,
                     @RequestParam(value = "caseBasedPrimaryResourceUUID", required = false) String caseBasedPrimaryResourceUUID,
                     @RequestParam(value = "patientIdentifierType", required = false) String patientIdentifierType,
                     @RequestParam(value = "noOfResourcesInBundle", required = false) Integer noOfResourcesInBundle,
                     @RequestParam(value = "encounterTypeUUIDS", required = false) String encounterTypeUUIDS,
                     @RequestParam(value = "observationCodeUUIDs", required = false) String observationCodeUUIDs,
                     @RequestParam(value = "episodeOfCareUUIDS", required = false) String episodeOfCareUUIDS,
                     @RequestParam(value = "url", required = false) String url,
                     @RequestParam(value = "username", required = false) String username,
                     @RequestParam(value = "password", required = false) String password,
                     @RequestParam(value = "token", required = false) String token,
                     UiSessionContext uiSessionContext, UiUtils uiUtils, HttpServletRequest request) {
        UgandaEMRSyncService ugandaEMRSyncService = Context.getService(UgandaEMRSyncService.class);


        if (profileId.equals("")) {
            String resourceSearchParams = FHIR_FILTER_OBJECT_STRING.replace("encounterTypeUUID", encounterTypeUUIDS).replace("conceptQuestionUUID", observationCodeUUIDs).replace("episodeOfCareTypeUUID", episodeOfCareUUIDS);
            SyncFhirProfile newSyncFhirProfile = new SyncFhirProfile();

            newSyncFhirProfile.setName(syncFhirProfileName);
            newSyncFhirProfile.setResourceTypes(resourceType);
            newSyncFhirProfile.setCaseBasedProfile(isCaseBasedProfile);
            newSyncFhirProfile.setDurationToKeepSyncedResources(durationToKeepSyncedResources);
            newSyncFhirProfile.setGenerateBundle(generateBundle);
            newSyncFhirProfile.setCaseBasedPrimaryResourceType(caseBasedPrimaryResourceType);
            newSyncFhirProfile.setCaseBasedPrimaryResourceTypeId(caseBasedPrimaryResourceUUID);
            newSyncFhirProfile.setPatientIdentifierType(Context.getPatientService().getPatientIdentifierTypeByUuid(patientIdentifierType));
            newSyncFhirProfile.setNumberOfResourcesInBundle(noOfResourcesInBundle);
            newSyncFhirProfile.setUrl(url);
            newSyncFhirProfile.setUrlUserName(username);
            newSyncFhirProfile.setUrlPassword(password);
            newSyncFhirProfile.setUrlToken(token);
            newSyncFhirProfile.setCreator(Context.getAuthenticatedUser());
            newSyncFhirProfile.setDateCreated(new Date());
            ugandaEMRSyncService.saveSyncFhirProfile(newSyncFhirProfile);

        } else {
            SyncFhirProfile syncFhirProfile = Context.getService(UgandaEMRSyncService.class).getSyncFhirProfileByUUID(profileId);

            syncFhirProfile.setName(syncFhirProfileName);
            syncFhirProfile.setResourceTypes(resourceType);
            syncFhirProfile.setDurationToKeepSyncedResources(durationToKeepSyncedResources);
            syncFhirProfile.setGenerateBundle(generateBundle);
            syncFhirProfile.setCaseBasedProfile(isCaseBasedProfile);
            syncFhirProfile.setCaseBasedPrimaryResourceType(caseBasedPrimaryResourceType);
            syncFhirProfile.setCaseBasedPrimaryResourceTypeId(caseBasedPrimaryResourceUUID);
            syncFhirProfile.setPatientIdentifierType(Context.getPatientService().getPatientIdentifierTypeByUuid(patientIdentifierType));
            syncFhirProfile.setNumberOfResourcesInBundle(noOfResourcesInBundle);
            syncFhirProfile.setUrl(url);
            syncFhirProfile.setUrlUserName(username);
            syncFhirProfile.setUrlPassword(password);
            syncFhirProfile.setUrlToken(token);
            syncFhirProfile.setCreator(Context.getAuthenticatedUser());
            syncFhirProfile.setDateChanged(new Date());
            syncFhirProfile.setChangedBy(Context.getAuthenticatedUser());
            ugandaEMRSyncService.saveSyncFhirProfile(syncFhirProfile);
        }

        pageModel.put("syncFhirProfile", ugandaEMRSyncService.getAllSyncFhirProfile());
    }
}
