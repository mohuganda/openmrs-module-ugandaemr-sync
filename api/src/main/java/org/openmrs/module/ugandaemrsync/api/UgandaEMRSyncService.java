/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.ugandaemrsync.api;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.Order;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.ugandaemrsync.model.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * The main service of this module, which is exposed for other modules. See
 * moduleApplicationContext.xml on how it is wired up.
 */
public interface UgandaEMRSyncService extends OpenmrsService {

    /**
     * Getting all sync task types
     * @return List<SyncTaskType> returns all sync task type in a list
     * @throws APIException
     */
    List<SyncTaskType> getAllSyncTaskType() throws APIException;

    /**
     * Get Sync Task Type By uuid
     * @param uuid the uuid of the sync task type to return
     * @return SyncTaskType that matched the uuid parameter
     * @throws APIException
     */
    @Transactional
    SyncTaskType getSyncTaskTypeByUUID(String uuid) throws APIException;

    /**
     * Saves the syn task Type
     * @param syncTaskType the sync task to be saved.
     * @return SyncTaskType saved sync task type
     * @throws APIException
     */
    @Transactional
    SyncTaskType saveSyncTaskType(SyncTaskType syncTaskType) throws APIException;

    /**
     * Gets SyncTask that matches the sync task parameter
     * @param syncTaskId a string containing an id or uuid of the sync task
     * @return SyncTask that matches the sync task id or uuid in the parameter set
     * @throws APIException
     */
    @Transactional
    SyncTask getSyncTaskBySyncTaskId(String syncTaskId) throws APIException;

    /**
     * Saves or updates the sync task set or given
     * @param syncTask the sync task that is to be saved
     * @return SyncTask the sync task that has been saved
     * @throws APIException
     */
    @Transactional
    SyncTask saveSyncTask(SyncTask syncTask) throws APIException;

    /**
     * Gets a list of sync task that require an action yet the action is not complete
     * @param syncTaskTypeIdentifier the sync task type identifier for the sync tasks that need to be checked
     * @return List<SyncTask> that are have an action that is not completed
     * @throws APIException
     */
    @Transactional
    public List<SyncTask> getIncompleteActionSyncTask(String syncTaskTypeIdentifier) throws APIException;

    /**
     * Get all sync task
     * @return List<SyncTask> a list of sync tasks found
     * @throws APIException
     */
    @Transactional
    public List<SyncTask> getAllSyncTask() throws APIException;

    /**
     * @param query
     * @return
     */
    public List getDatabaseRecord(String query);

    /**
     * @param columns
     * @param query
     * @return
     */
    public List getFinalList(List<String> columns, String query);

    /**
     /**
     * Gets patient Identifier by patientId
     * @param patientIdentifier this can be a uuid or patientId
     * @return Patient a patient that matches the patientId
     */
    public Patient getPatientByPatientIdentifier(String patientIdentifier);

    /**
     /**
     * This method is used to validate if the dhis2 code that is in the excel file matches the one set at facility
     * @param facilityDHIS2UUID the dhis2 code that will be validated
     * @return true is it is valid and false if it is invalid
     */
    public boolean validateFacility(String facilityDHIS2UUID);

    /**
     * @param s
     * @return
     */
    public Collection<EncounterType> getEcounterTypes(String s);

    /**
     * This method adds the viral load results to an encounter
     * @param vlQualitative the viral load qualitative value such as detected, not detected, sample rejected
     * @param vlQuantitative the viral load copies.
     * @param vlDate the date of sample collection date for the viral load results that are being returned
     * @param encounter the encounter where the viral load results will be written to
     * @param order the order which was used to order the viral load. this can be null.
     * @return Encounter the encounter where the viral load results have been added.
     */
    public Encounter addVLToEncounter(String vlQualitative, String vlQuantitative, String vlDate, Encounter encounter,
                                      Order order);

    /**
     * @param vlDate
     * @return
     */
    public String getDateFormat(String vlDate);

    /**
     * @param string
     * @param time
     * @param dateFormat
     * @return
     */
    public Date convertStringToDate(String string, String time, String dateFormat);

    /**
     /**
     * This method gets the dhis2 orgunit code for the facility
     * @return returns dhis2 orgunit code
     */
    public String getHealthCenterCode();

    /**
     * This method gets the health center name
     * @return
     */
    public String getHealthCenterName();

    /**
     * This Method gets the patient identifier that is based on patient Identifier type and the patient
     * @param patient the patient whose identifier is being searched
     * @param patientIdentifierTypeUUID the uuid of the patient identifier type that is being search
     * @return the identifier that matches both the patient and the patient identifier type UUID
     */
    public String getPatientIdentifier(Patient patient, String patientIdentifierTypeUUID);


    /**
     * This Method saves a Sync FHIR Profile
     * @param syncFHIRProfile the Sync FHIR Profile to be saved
     * @return the saved Sync FHIR Profile
     */
    public SyncFHIRProfile saveSyncFHIRProfile(SyncFHIRProfile syncFHIRProfile);

    /**
     * This Method returs a Sync FHIR Profile that matches the id given
     * @param id the Id that will be used to match a sync fhir profile
     * @return the matched Sync FHIR Profile
     */
    public SyncFHIRProfile getSyncFHIRProfileById(Integer id);


    /**
     * This Method returs a Sync FHIR Profile that matches the id given
     * @param uuid the uuid that will be used to match a sync fhir profile
     * @return the matched Sync FHIR Profile
     */
    public SyncFHIRProfile getSyncFHIRProfileByUUID(String uuid);


    /**
     * This Method gets a Sync FHIR Profile from a scheduled task
     * @param scheduledTaskName
     * @return the syncFHIRProfile that is associated with a scheduled task that matches the scheduledTaskName
     */

    public SyncFHIRProfile getSyncFHIRProfileByScheduledTaskName(String scheduledTaskName);


    /**
     * This Method saves a Sync FHIR Resource
     * @param syncFHIRResource the resource to be saved
     * @return the saved sync fhir resource
     */
    public SyncFHIRResource saveFHIRResource(SyncFHIRResource syncFHIRResource);


    /**
     * This Method gets a list of sync fhir resources by the profile that generated them
     * @param syncFHIRProfile
     * @param includeSynced the check to determine if it has been sent to the destined server
     * @return
     */
    public List<SyncFHIRResource> getSyncFHIRResourceBySyncFHIRProfile(SyncFHIRProfile syncFHIRProfile, boolean includeSynced);


    /**
     * Gets a Sync FHIR Resource using an id
     * @param id the id that will be used to match the resource
     * @return the resource that matches the id
     */
    public SyncFHIRResource getSyncFHIRResourceById(Integer id);

    /**
     * Marks resource Synced and sets expiry date based on the number of days to keep resource after sync set in profile
     * @param syncFHIRResources the resource to be marked synced
     * @return the resource that is marked synced.
     */
    public SyncFHIRResource markSyncFHIRResourceSynced(SyncFHIRResource syncFHIRResources);


    /**
     * gets all expired resources based on date passed
     * @param date the date which will be used to match expired resources
     * @return a list of expired resources
     */
    public List<SyncFHIRResource> getExpiredSyncFHIRResources(Date date);

    /**
     * Purges all resources that have  expired
     */
    public void purgeExpiredFHIRResource(Date date);

    /**
     * This Saves the Sync Profile Log
     * @param syncFHIRProfileLog the log to be saved
     * @return the SyncFHIRProfileLog that has been saved
     */
    public SyncFHIRProfileLog saveSyncFHIRProfileLog(SyncFHIRProfileLog syncFHIRProfileLog);

    /**
     * This returns the latest sync fhir profile log
     * @param syncFHIRProfile the sync fhir profile to be used search for the syncFHIRProfile
     * @param resourceType a parameter to be used to search for the sync fhir log
     * @return syncFHIRProfileLog that has matched the search
     */
    public List<SyncFHIRProfileLog> getSyncFHIRProfileLogByProfileAndResourceName(SyncFHIRProfile syncFHIRProfile, String resourceType);

    /**
     * This returns the latest sync fhir profile log
     * @param syncFHIRProfile the sync fhir profile to be used search for the syncFHIRProfile
     * @param resourceType a parameter to be used to search for the sync fhir log
     * @return syncFHIRProfileLog that has matched the search
     */
    public SyncFHIRProfileLog getLatestSyncFHIRProfileLogByProfileAndResourceName(SyncFHIRProfile syncFHIRProfile, String resourceType);


    /**
     * This will get a FHIRCase which matches the parameters set
     * @param syncFHIRProfile the profile that was is used to identify the case
     * @param patient the patient who belongs to the case
     * @return the case that matches the parameters
     */
    public SyncFHIRCase getSyncFHIRCaseBySyncFHIRProfileAndPatient(SyncFHIRProfile syncFHIRProfile, Patient patient, String caseIdentifier);



    /**
     * This Method saves a  FHIR Case
     * @param syncFHIRCase the case to be saved
     * @return the saved case
     */
    public SyncFHIRCase saveSyncFHIRCase(SyncFHIRCase syncFHIRCase);
}

