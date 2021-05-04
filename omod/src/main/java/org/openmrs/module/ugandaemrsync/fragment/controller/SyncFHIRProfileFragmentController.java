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

import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.api.context.Context;
import org.openmrs.module.ugandaemrsync.api.UgandaEMRSyncService;
import org.openmrs.module.ugandaemrsync.model.SyncFHIRProfile;
import org.openmrs.ui.framework.SimpleObject;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

/**
 *  * Controller for a fragment that shows all users  
 */
public class SyncFHIRProfileFragmentController {

	public SimpleObject getSyncFHIRProfile(@RequestParam("syncFHIRProfileId") String syncFHIRProfileId) throws IOException {
		SyncFHIRProfile syncFHIRProfile = Context.getService(UgandaEMRSyncService.class).getSyncFHIRProfileByUUID(syncFHIRProfileId);

		SimpleObject simpleObject = new SimpleObject();

		ObjectMapper objectMapper = new ObjectMapper();
		SimpleObject syncFHIRProfileMapper=SimpleObject.create("syncFHIRProfileId",syncFHIRProfile.getId(),"name",syncFHIRProfile.getName(),"resourceType",syncFHIRProfile.getResourceTypes(),"url",syncFHIRProfile.getUrl()
		,"urlToken",syncFHIRProfile.getUrlToken(),"urlUserName",syncFHIRProfile.getUrlUserName(),"urlPassword",syncFHIRProfile.getUrlPassword());
		simpleObject.put("syncFHIRProfile", objectMapper.writeValueAsString(syncFHIRProfileMapper));
		return simpleObject;
	}
}
