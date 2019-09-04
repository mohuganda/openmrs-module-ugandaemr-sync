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

	public static final String RECENCY_SERVER_URL = "ugandaemrsync.recency.server.url";
	
	public static final String CONNECTIVITY_CHECK_URL = "http://www.google.com";
	
	public static final String CONNECTIVITY_CHECK_SUCCESS = "Successful connection to the internet.";
	
	public static final String SERVER_CONNECTION_SUCCESS = "Successfully established connecton to the server.";
	
	public static final String CONNECTIVITY_CHECK_FAILED = "Cannot establish internet connectivity.";
	
	public static final String SERVER_CONNECTION_FAILED = "Cannot establish connection to the server.";

	public static final String RECENCY_SERVER_USERNAME = "ugandaemrsync.recency.server.username";

	public static final String RECENCY_SERVER_PASSWORD = "ugandaemrsync.recency.server.password";

	public static final String HEADER_EMR_DATE = "x-emr-date";
	
	public static final String DHIS2_ORGANIZATION_UUID = "ugandaemr.dhis2.organizationuuid";
	
	public static final String RECENCY_CSV_FILE_NAME = "HTS_Recency_Client_Card_Data_Export_2019.csv";
	
	public static final String RECENCY_DEFININATION_UUID = "662d4c00-d6bb-4494-8180-48776f415802";
	
	public static final String REPORT_RENDERING_MODE = "org.openmrs.module.reporting.report.renderer.CsvReportRenderer!152a4845-37e1-40c0-8fa8-5ef343e65ba5";
	
	public static final String RECENCY_SEND_DATA_TASK_RUN = "ugandaemrsync.lastsuccessfulsubmissiondate";
}
