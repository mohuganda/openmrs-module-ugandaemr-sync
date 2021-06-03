/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.ugandaemrsync.api.dao;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StringType;
import org.openmrs.Patient;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ugandaemrsync.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository("ugandaemrsync.UgandaEMRSyncDao")
public class UgandaEMRSyncDao {

    @Autowired
    DbSessionFactory sessionFactory;

    /**
     * @return
     */
    private DbSession getSession() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * /**
     *
     * @see org.openmrs.module.ugandaemrsync.api.UgandaEMRSyncService#getAllSyncTaskType()
     */
    public List<SyncTaskType> getAllSyncTaskType() {
        return (List<SyncTaskType>) getSession().createCriteria(SyncTaskType.class).list();
    }

    /**
     * @see org.openmrs.module.ugandaemrsync.api.UgandaEMRSyncService#saveSyncTaskType(org.openmrs.module.ugandaemrsync.model.SyncTaskType)
     */
    public SyncTaskType getSyncTaskTypeByUUID(String uuid) {
        return (SyncTaskType) getSession().createCriteria(SyncTaskType.class).add(Restrictions.eq("uuid", uuid))
                .uniqueResult();
    }

    /**
     * @see org.openmrs.module.ugandaemrsync.api.UgandaEMRSyncService#saveSyncTaskType(org.openmrs.module.ugandaemrsync.model.SyncTaskType)
     */
    public SyncTaskType saveSyncTaskType(SyncTaskType syncTaskType) {
        getSession().saveOrUpdate(syncTaskType);
        return syncTaskType;
    }

    /**
     * @see org.openmrs.module.ugandaemrsync.api.UgandaEMRSyncService#getSyncTaskBySyncTaskId(java.lang.String)
     */
    public SyncTask getSyncTask(String syncTask) {
        return (SyncTask) getSession().createCriteria(SyncTask.class).add(Restrictions.eq("syncTask", syncTask))
                .uniqueResult();
    }

    /**
     * /**
     *
     * @see org.openmrs.module.ugandaemrsync.api.UgandaEMRSyncService#getAllSyncTask()
     */
    public List<SyncTask> getAllSyncTask() {
        return (List<SyncTask>) getSession().createCriteria(SyncTask.class).list();
    }


    /**
     * /**
     *
     * @see org.openmrs.module.ugandaemrsync.api.UgandaEMRSyncService#saveSyncTask(org.openmrs.module.ugandaemrsync.model.SyncTask)
     */
    public SyncTask saveSyncTask(SyncTask syncTask) {
        getSession().saveOrUpdate(syncTask);
        return syncTask;
    }

    /**
     * @param query
     * @return
     */
    public List getDatabaseRecord(String query) {
        SQLQuery sqlQuery = getSession().createSQLQuery(query);
        return sqlQuery.list();
    }

    /**
     * @param columns
     * @param finalQuery
     * @return
     */
    public List getFinalResults(List<String> columns, String finalQuery) {
        SQLQuery sqlQuery = getSession().createSQLQuery(finalQuery);
        for (String column : columns) {
            sqlQuery.addScalar(column, StringType.INSTANCE);
        }
        return sqlQuery.list();
    }

    /**
     * /**
     *
     * @see org.openmrs.module.ugandaemrsync.api.UgandaEMRSyncService#getIncompleteActionSyncTask(java.lang.String)
     */
    public List<SyncTask> getIncompleteActionSyncTask(String syncTaskTypeIdentifier) {
        SQLQuery sqlQuery = getSession()
                .createSQLQuery(
                        "select sync_task.* from sync_task inner join sync_task_type on (sync_task_type.sync_task_type_id=sync_task.sync_task_type) where sync_task_type.data_type_id='"
                                + syncTaskTypeIdentifier
                                + "' and  require_action=true and action_completed=false;");
        sqlQuery.addEntity(SyncTask.class);
        return sqlQuery.list();
    }

    /**
     * @see org.openmrs.module.ugandaemrsync.api.UgandaEMRSyncService#getSyncFHIRProfileById(java.lang.Integer)
     */
    public SyncFHIRProfile getSyncFHIRProfileById(Integer id) {
        return (SyncFHIRProfile) getSession().createCriteria(SyncFHIRProfile.class).add(Restrictions.eq("profileId", id))
                .uniqueResult();
    }


    /**
     * @see org.openmrs.module.ugandaemrsync.api.UgandaEMRSyncService#getSyncFHIRProfileByUUID(java.lang.String)
     */
    public SyncFHIRProfile getSyncFHIRProfileByUUID(String uuid) {
        return (SyncFHIRProfile) getSession().createCriteria(SyncFHIRProfile.class).add(Restrictions.eq("uuid", uuid))
                .uniqueResult();
    }


    /**
     * @see org.openmrs.module.ugandaemrsync.api.UgandaEMRSyncService#saveSyncFHIRProfile(org.openmrs.module.ugandaemrsync.model.SyncFHIRProfile)
     */
    public SyncFHIRProfile saveSyncFHIRProfile(SyncFHIRProfile syncFHIRProfile) {
        getSession().saveOrUpdate(syncFHIRProfile);
        return syncFHIRProfile;
    }

    /**
     * @see org.openmrs.module.ugandaemrsync.api.UgandaEMRSyncService#saveFHIRResource(SyncFHIRResource)
     */
    public SyncFHIRResource saveSyncFHIRResource(SyncFHIRResource syncFHIRResource) {
        getSession().saveOrUpdate(syncFHIRResource);

        return syncFHIRResource;

    }

    /**
     * @see org.openmrs.module.ugandaemrsync.api.UgandaEMRSyncService#saveFHIRResource(SyncFHIRResource)
     */
    public List<SyncFHIRResource> getSyncResourceBySyncFHIRProfile(SyncFHIRProfile syncFHIRProfile, boolean includeSynced) {

        Criteria criteria = getSession().createCriteria(SyncFHIRResource.class);

        if (syncFHIRProfile != null) {
            criteria.add(Restrictions.eq("generatorProfile", syncFHIRProfile));
        }

        if (!includeSynced) {
            criteria.add(Restrictions.eq("synced", false));
        }

        criteria.addOrder(Order.desc("dateCreated"));

        return criteria.list();
    }

    public SyncFHIRResource getSyncFHIRResourceById(Integer id) {

        return (SyncFHIRResource) getSession().createCriteria(SyncFHIRResource.class).add(Restrictions.eq("resourceId", id))
                .uniqueResult();
    }

    public void purgeExpiredFHIRResource(SyncFHIRResource syncFHIRResource) {
        getSession().delete(syncFHIRResource);
    }

    public List<SyncFHIRResource> getExpiredSyncFHIRResources(Date date) {

        Criteria criteria = getSession().createCriteria(SyncFHIRResource.class).add(Restrictions.le("expiryDate", date));

        criteria.addOrder(Order.desc("expiryDate"));

        return criteria.list();
    }

    public SyncFHIRProfileLog saveSyncFHIRProfileLog(SyncFHIRProfileLog syncFHIRProfileLog) {
        getSession().saveOrUpdate(syncFHIRProfileLog);
        return syncFHIRProfileLog;
    }

    public List<SyncFHIRProfileLog> getSyncFHIRProfileLogByProfileAndResourceName(SyncFHIRProfile syncFHIRProfile, String resourceType) {
        Criteria criteria = getSession().createCriteria(SyncFHIRProfileLog.class);

        if (syncFHIRProfile != null && resourceType != null) {
            criteria.add(Restrictions.eq("resourceType", resourceType));
            criteria.add(Restrictions.eq("profile", syncFHIRProfile));
            criteria.addOrder(Order.desc("dateCreated"));
        }

        return criteria.list();
    }

    public SyncFHIRCase getSyncFHIRCaseBySyncFHIRProfileAndPatient(SyncFHIRProfile syncFHIRProfile, Patient patient,String caseIdentifier) {
        Criteria criteria = getSession().createCriteria(SyncFHIRCase.class);
        criteria.add(Restrictions.eq("profile", syncFHIRProfile));
        criteria.add(Restrictions.eq("patient", patient));
        criteria.add(Restrictions.eq("caseIdentifier", caseIdentifier));

        return (SyncFHIRCase) criteria.uniqueResult();
    }

    public SyncFHIRCase saveSyncFHIRCase(SyncFHIRCase syncFHIRCase) {

        getSession().saveOrUpdate(syncFHIRCase);

        return syncFHIRCase;
    }
}
