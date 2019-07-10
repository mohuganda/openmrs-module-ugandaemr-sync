/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.ugandaemrsync;

import org.springframework.stereotype.Component;

/**
 * Contains module's config.
 */
@Component("ugandaemrsync.UgandaemrSyncConfig")
public class UgandaEMRSyncConfig {
	
	public final static String MODULE_PRIVILEGE = "UgandaemrSync Privilege";
	public static final String RECENCY_SERVER_URL = "http://mirth-tcp.globalhealthapp.net:6001";
	public static final String CONNECTIVITY_CHECK_URL = "http://www.google.com";
	public static final String RECENCY_SERVER_TEST_CONNECTION_URL = "http://mirth-tcp.globalhealthapp.net";
	public static final String CONNECTIVITY_CHECK_SUCCESS = "Successful connection to the internet.";
	public static final String RECENCY_SERVER_SUCCESS = "Successfully established connecton to the server.";
	public static final String CONNECTIVITY_CHECK_FAILED = "Cannot establish internet connectivity.";
	public static final String RECENCY_SERVER_FAILED = "Cannot establish connection to the server.";
	public static final String RECENCY_SERVER_USERNAME = "admin";
	public static final String RECENCY_SERVER_PASSWORD = "admin";
	public static final String HEADER_EMR_DATE = "x-emr-date";
	public static final String FACILITY_UUID = "ugandaemr.dhis2.organizationuuid";
	public static final String DHIS2_ORGANIZATION_UUID = "ugandaemr.dhis2.organizationuuid";
	public static String END_POINT = "";
}
