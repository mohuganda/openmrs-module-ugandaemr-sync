/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.ugandaemr.sync.api.ugserver;

import org.junit.Test;
import org.openmrs.module.ugandaemr.sync.ugserver.SyncConstant;
import org.openmrs.module.ugandaemr.sync.ugserver.UgandaEMRHttpURLConnection;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link ${UgandaEMRSyncService}}.
 */
public class PostTest extends BaseModuleContextSensitiveTest {

    @Test
    public void shouldSetupContext() throws Exception {
        UgandaEMRHttpURLConnection connectionRequest = new UgandaEMRHttpURLConnection();
        String resource="/api";
        String content="<items><SyncItem containedType=\"org.openmrs.module.metadatasharing.ImportedPackage\" key=\"b79c703a-3a42-41e0-8f48-3c7c49853946\" state=\"UPDATED\"><content><![CDATA[<org.openmrs.module.metadatasharing.ImportedPackage><dateImported type=\"timestamp\">2016-11-10T13:02:57.000+0300</dateImported><dateCreated type=\"timestamp\">2013-08-09T17:52:40.000+0300</dateCreated><groupUuid type=\"string\">5987fa0f-9526-461c-8d43-28cb2e14933e</groupUuid><subscriptionStatus type=\"org.openmrs.module.metadatasharing.SubscriptionStatus\">DISABLED</subscriptionStatus><name type=\"string\">Reference Application Location Tags</name><description type=\"string\">Contains location tags required for proper functioning of some features in the reference application</description><uuid type=\"string\">b79c703a-3a42-41e0-8f48-3c7c49853946</uuid><version type=\"integer\">1</version></org.openmrs.module.metadatasharing.ImportedPackage>]]></content></SyncItem><SyncItem containedType=\"org.openmrs.module.metadatasharing.ImportedPackage\" key=\"eb0feeb7-ed7d-4892-8d09-29638ab62a7e\" state=\"UPDATED\"><content><![CDATA[<org.openmrs.module.metadatasharing.ImportedPackage><dateImported type=\"timestamp\">2016-11-10T13:02:57.000+0300</dateImported><dateCreated type=\"timestamp\">2013-08-02T21:37:21.000+0300</dateCreated><groupUuid type=\"string\">d5fb6341-caca-4742-b230-c9d979911793</groupUuid><subscriptionStatus type=\"org.openmrs.module.metadatasharing.SubscriptionStatus\">DISABLED</subscriptionStatus><name type=\"string\">Reference Application Person Attribute Types</name><description type=\"string\">Contains person attributes types required for proper functioning of some features in the reference application</description><uuid type=\"string\">eb0feeb7-ed7d-4892-8d09-29638ab62a7e</uuid><version type=\"integer\">1</version></org.openmrs.module.metadatasharing.ImportedPackage>]]></content></SyncItem><SyncItem containedType=\"org.openmrs.module.metadatasharing.ImportedPackage\" key=\"824d49e2-c8a4-477f-a403-50324f9854fd\" state=\"UPDATED\"><content><![CDATA[<org.openmrs.module.metadatasharing.ImportedPackage><dateImported type=\"timestamp\">2016-11-10T13:02:57.000+0300</dateImported><dateCreated type=\"timestamp\">2013-08-03T08:21:31.000+0300</dateCreated><groupUuid type=\"string\">a745ec2b-032c-4815-b091-d0339667b919</groupUuid><subscriptionStatus type=\"org.openmrs.module.metadatasharing.SubscriptionStatus\">DISABLED</subscriptionStatus><name type=\"string\">Reference Application Visit Types</name><description type=\"string\">Contains visit types required for proper functioning of some features in the reference application</description><uuid type=\"string\">824d49e2-c8a4-477f-a403-50324f9854fd</uuid><version type=\"integer\">1</version></org.openmrs.module.metadatasharing.ImportedPackage>]]></content></SyncItem><SyncItem containedType=\"org.openmrs.module.metadatasharing.ImportedPackage\" key=\"7830c468-24f6-4689-91cd-6f27fd94dbfc\" state=\"UPDATED\"><content><![CDATA[<org.openmrs.module.metadatasharing.ImportedPackage><dateImported type=\"timestamp\">2016-11-10T13:02:58.000+0300</dateImported><dateCreated type=\"timestamp\">2013-08-20T23:37:14.000+0300</dateCreated><groupUuid type=\"string\">56b191a8-0f71-44e9-b0ea-651f9da955ce</groupUuid><subscriptionStatus type=\"org.openmrs.module.metadatasharing.SubscriptionStatus\">DISABLED</subscriptionStatus><name type=\"string\">Reference Application Encounter Types</name><description type=\"string\">Contains encounter types required for proper functioning of some features in the reference application</description><uuid type=\"string\">7830c468-24f6-4689-91cd-6f27fd94dbfc</uuid><version type=\"integer\">2</version></org.openmrs.module.metadatasharing.ImportedPackage>]]></content></SyncItem><SyncItem containedType=\"org.openmrs.module.metadatasharing.ImportedPackage\" key=\"e12cd04a-0ee9-42bb-afc6-33f82d54ab96\" state=\"UPDATED\"><content><![CDATA[<org.openmrs.module.metadatasharing.ImportedPackage><dateImported type=\"timestamp\">2016-11-10T13:02:58.000+0300</dateImported><dateCreated type=\"timestamp\">2013-08-14T18:55:40.000+0300</dateCreated><groupUuid type=\"string\">390a392b-10d0-40e4-ae3e-ccd0729d4067</groupUuid><subscriptionStatus type=\"org.openmrs.module.metadatasharing.SubscriptionStatus\">DISABLED</subscriptionStatus><name type=\"string\">Reference Application Provider Roles</name><description type=\"string\">Standard set of Provider Roles distributed with the Reference Application</description><uuid type=\"string\">e12cd04a-0ee9-42bb-afc6-33f82d54ab96</uuid><version type=\"integer\">2</version></org.openmrs.module.metadatasharing.ImportedPackage>]]></content></SyncItem><SyncItem containedType=\"org.openmrs.module.metadatasharing.ImportedPackage\" key=\"a70f53a7-6126-499a-9d6f-f068d3b221dc\" state=\"UPDATED\"><content><![CDATA[<org.openmrs.module.metadatasharing.ImportedPackage><dateImported type=\"timestamp\">2016-11-10T13:02:58.000+0300</dateImported><dateCreated type=\"timestamp\">2013-08-02T08:27:13.000+0300</dateCreated><groupUuid type=\"string\">d7c66f64-c3ee-4012-af48-2644909e25f4</groupUuid><subscriptionStatus type=\"org.openmrs.module.metadatasharing.SubscriptionStatus\">DISABLED</subscriptionStatus><name type=\"string\">Reference Application Visit and Encounter Types</name><description type=\"string\">Standard set of Visit Types and Encounter Types distributed with the Reference Application</description><uuid type=\"string\">a70f53a7-6126-499a-9d6f-f068d3b221dc</uuid><version type=\"integer\">3</version></org.openmrs.module.metadatasharing.ImportedPackage>]]></content></SyncItem><SyncItem containedType=\"org.openmrs.module.metadatasharing.ImportedPackage\" key=\"9d0b7d2f-5833-47d3-b986-a53398d3b619\" state=\"UPDATED\"><content><![CDATA[<org.openmrs.module.metadatasharing.ImportedPackage><dateImported type=\"timestamp\">2016-11-10T13:02:59.000+0300</dateImported><dateCreated type=\"timestamp\">2013-08-20T13:14:51.000+0300</dateCreated><groupUuid type=\"string\">3f63eef6-faab-436f-a284-6c0870670542</groupUuid><subscriptionStatus type=\"org.openmrs.module.metadatasharing.SubscriptionStatus\">DISABLED</subscriptionStatus><name type=\"string\">Reference Application Encounter Roles</name><description type=\"string\">Contains encounter roles required for proper functioning of some features in the reference application</description><uuid type=\"string\">9d0b7d2f-5833-47d3-b986-a53398d3b619</uuid><version type=\"integer\">1</version></org.openmrs.module.metadatasharing.ImportedPackage>]]></content></SyncItem></items>";
        connectionRequest.sendPostBy(SyncConstant.XML_CONTENT_TYPE, content, "Kyabalanga HC 3", "", SyncConstant.HTTP_PROTOCOL+SyncConstant.SERVER_IP_PLACE_HOLDER+resource, SyncConstant.FACILITY_ID_REQUEST_TYPE);
        assertEquals(connectionRequest.sendGet("google.com/search?q=Samuel+Lubwama", SyncConstant.HTTP_PROTOCOL).getResponseCode(), 200);
    }


}
