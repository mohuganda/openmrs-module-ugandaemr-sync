/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.ugandaemrsync.fragment.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.ugandaemrsync.server.SyncConstant;
import org.openmrs.module.ugandaemrsync.server.SyncDataRecord;
import org.openmrs.module.ugandaemrsync.server.SyncGlobalProperties;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.page.PageModel;

import java.util.Date;

import static org.openmrs.module.ugandaemrsync.server.SyncConstant.LAST_SYNC_DATE;

/**
 *  * Controller for a fragment that shows all users  
 */
public class GenerateInitialDataFragmentController {
	
	public void controller(UiSessionContext sessionContext, FragmentModel model) {
	}
	
	public void get(@SpringBean PageModel pageModel) throws Exception {
		
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
				pageModel.put("persons", "The data was processed and sent successfully");
			} else {
				pageModel.put("persons", "A problem occurred, please check your Internet connection");
				
			}
			Context.closeSession();
		}
		catch (Exception e) {
			pageModel.put("persons", "A problem occurred, please check your Internet connection");
			System.out.println("Error occured");
		}
	}
	
}
