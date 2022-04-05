package org.openmrs.module.ugandaemrsync.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.module.ugandaemrsync.api.UgandaEMRSyncService;
import org.openmrs.module.ugandaemrsync.model.SyncTaskType;

public class TaskType {

	public TaskType() {
	}
	
	protected Log log = LogFactory.getLog(TaskType.class);
	

	
	public GlobalProperty setGlobalProperty(String property, String propertyValue) {
		GlobalProperty globalProperty = new GlobalProperty();
		
		globalProperty.setProperty(property);
		globalProperty.setPropertyValue(propertyValue);
		
		return Context.getAdministrationService().saveGlobalProperty(globalProperty);
	}
	
	public SyncTaskType getSyncTaskType(String uuid) {
		return Context.getService(UgandaEMRSyncService.class).getSyncTaskTypeByUUID(uuid);
	}
	
}
