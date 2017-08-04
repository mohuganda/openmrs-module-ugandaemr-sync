package org.openmrs.module.ugandaemrsync;

import org.openmrs.api.context.Context;
import org.openmrs.module.ugandaemrsync.server.SyncConstant;
import org.openmrs.module.ugandaemrsync.server.SyncDataRecord;
import org.openmrs.module.ugandaemrsync.server.SyncGlobalProperties;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.openmrs.util.OpenmrsUtil;

import java.io.File;
import java.util.Date;

import static org.openmrs.module.ugandaemrsync.server.SyncConstant.LAST_SYNC_DATE;

/**
 * Created by lubwamasamuel on 21/02/2017.
 */
public class SyncTask extends AbstractTask {
	
	public void execute() {
		try {
			Context.openSession();
			SyncGlobalProperties syncGlobalProperties = new SyncGlobalProperties();
			String facilitySyncId = syncGlobalProperties.getGlobalProperty(SyncConstant.HEALTH_CENTER_SYNC_ID);
			String serverProtocol = syncGlobalProperties.getGlobalProperty(SyncConstant.SERVER_PROTOCOL);
			String serverIP = syncGlobalProperties.getGlobalProperty(SyncConstant.SERVER_IP);
			String maxNoOfRows = syncGlobalProperties.getGlobalProperty(SyncConstant.MAX_NUMBER_OF_ROWS);
			String lastSyncDate = syncGlobalProperties.getGlobalProperty(LAST_SYNC_DATE);
			
			SyncDataRecord syncDataRecord = new SyncDataRecord(serverProtocol, serverIP, facilitySyncId, maxNoOfRows,
			        lastSyncDate);
			String folder = syncDataRecord.getAbsoluteBackupFolderPath();
			
			syncDataRecord.syncData2(folder, syncDataRecord.connection());
			String uniqueString = syncDataRecord.zipSplitAndSend(folder);
			if (uniqueString != null) {
				syncDataRecord.sendProcessingCommand(uniqueString);
				Date now = new Date();
				String newSyncDate = SyncConstant.DEFAULT_DATE_FORMAT.format(now);
				syncGlobalProperties.setGlobalProperty(LAST_SYNC_DATE, newSyncDate);
			}
			Context.closeSession();
		}
		catch (Exception e) {
			System.out.println("Error occured");
		}
		
	}
}
