package org.openmrs.module.ugandaemrsync.tasks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.*;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ugandaemrsync.api.UgandaEMRHttpURLConnection;
import org.openmrs.module.ugandaemrsync.api.UgandaEMRSyncService;
import org.openmrs.module.ugandaemrsync.api.impl.UgandaEMRSyncServiceImpl;
import org.openmrs.module.ugandaemrsync.model.SyncTask;
import org.openmrs.module.ugandaemrsync.model.SyncTaskType;
import org.openmrs.module.ugandaemrsync.server.SyncGlobalProperties;
import org.openmrs.module.ugandaemrsync.util.UgandaEMRSyncUtil;
import org.openmrs.scheduler.tasks.AbstractTask;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

import static org.openmrs.module.ugandaemrsync.UgandaEMRSyncConfig.ALIS_SERVER_URL;
import static org.openmrs.module.ugandaemrsync.server.SyncConstant.*;

/**
 * Send lab request to ALIS
 */

public class SendLabRequestToALIS extends AbstractTask {

    protected Log log = LogFactory.getLog(SendLabRequestToALIS.class);
    SyncGlobalProperties syncGlobalProperties = new SyncGlobalProperties();

    @Override
    public void execute() {
        UgandaEMRHttpURLConnection ugandaEMRHttpURLConnection = new UgandaEMRHttpURLConnection();
        UgandaEMRSyncService ugandaEMRSyncService = Context.getService(UgandaEMRSyncService.class);
        List<Order> orderList = new ArrayList<>();

        if (!ugandaEMRHttpURLConnection.isConnectionAvailable()) {
            return;
        }
        String ALIS_ServerUrlEndPoint = syncGlobalProperties.getGlobalProperty(ALIS_SERVER_URL);
        String ALISBaseUrl = ugandaEMRHttpURLConnection.getBaseURL(ALIS_ServerUrlEndPoint);

        if (!ugandaEMRHttpURLConnection.isServerAvailable(ALISBaseUrl)) {
            return;
        }

        try {
            orderList = getOrders();

        } catch (IOException e) {
            log.error("Failed to get orders", e);
        } catch (ParseException e) {
            log.error("Failed to pass orders to list", e);
        }

        SyncTaskType syncTaskType = ugandaEMRSyncService.getSyncTaskTypeByUUID(ALIS_TESTS_SYNC_TYPE_UUID);

        if (orderList != null){
            for (Order order : orderList) {
                SyncTask syncTask = ugandaEMRSyncService.getSyncTaskBySyncTaskId(order.getAccessionNumber());
                if (syncTask == null) {
                    Map<String, String> dataOutput = generateLabFHIROrderTestRequestBody((TestOrder) order, UGANDAEMR_ALIS_FHIR_PATIANT_JSON_STRING);
                    String json = dataOutput.get("json");
                    try {
                        Map map = ugandaEMRHttpURLConnection.sendPostBy(syncTaskType.getUrl(), syncTaskType.getUrlUserName(), syncTaskType.getUrlPassword(), "", json, false);
                        if ((map != null) && UgandaEMRSyncUtil.getSuccessCodeList().contains(map.get("responseCode"))) {
                            SyncTask newSyncTask = new SyncTask();
                            newSyncTask.setDateSent(new Date());
                            newSyncTask.setCreator(Context.getUserService().getUser(1));
                            newSyncTask.setSentToUrl(syncTaskType.getUrl());
                            newSyncTask.setRequireAction(true);
                            newSyncTask.setActionCompleted(false);
                            newSyncTask.setSyncTask(order.getAccessionNumber());
                            newSyncTask.setStatusCode((Integer) map.get("responseCode"));
                            newSyncTask.setStatus("SUCCESS");
                            newSyncTask.setSyncTaskType(ugandaEMRSyncService.getSyncTaskTypeByUUID(ALIS_TESTS_SYNC_TYPE_UUID));
                            ugandaEMRSyncService.saveSyncTask(newSyncTask);
                        }
                    } catch (Exception e) {
                        log.error("Faied to create sync task",e);
                    }
                }
            }
        }

    }

    /**
     * Gererates FHIR MESSAGE Basing On Order To Lab That is refereed to Reference Lab
     *
     * @param testOrder
     * @param jsonFHIRMap
     * @return
     */
    public Map<String, String> generateLabFHIROrderTestRequestBody(TestOrder testOrder, String jsonFHIRMap) {
        Map<String, String> jsonMap = new HashMap<>();
        UgandaEMRSyncService ugandaEMRSyncService = new UgandaEMRSyncServiceImpl();
        String filledJsonFile = "";
        System.out.print("See my orders:-"+testOrder);
        if (testOrder != null) {

            // need to aline these with the servicerequestFHIR json from ALIS
            String requestType = proccessMappings(testOrder.getConcept());

            // subject

            /*authoredOn*/
            String authoredOn = testOrder.getEncounter().getEncounterDatetime().toString();

            //requester
            String clinicianNames = testOrder.getOrderer().getName();
            String ordererContact = "None";


            // patient container stuff
            String patientID = ugandaEMRSyncService.getPatientIdentifier(testOrder.getPatient(),PATIENT_IDENTIFIER_TYPE);
            String patientfamilyName = testOrder.getPatient().getFamilyName();
            String patientgivenName = testOrder.getPatient().getGivenName()+" "+testOrder.getPatient().getMiddleName();
            //String patientNames = patientfamilyName+""+patientgivenName;
            String patientNames = testOrder.getPatient().getNames().toString();
            String patientDOB = testOrder.getPatient().getBirthdate().toString();
            String patientGender = testOrder.getPatient().getGender();
            String patientAddress = testOrder.getPatient().getPersonAddress().getCityVillage();
            //String patientNationality = testOrder.getPatient().getAttribute(24).getValue();
            String patientNationality = "National";
            String patientNationalID = ugandaEMRSyncService.getPatientIdentifier(testOrder.getPatient(),"f0c16a6d-dc5f-4118-a803-616d0075d282");
            //String patientOccupation = testOrder.getPatient().getAttribute(23).getValue();
            String patientOccupation = "ICT";
            String patientAge = testOrder.getPatient().getAge().toString();

            // Practitioner
            String labTechNames = testOrder.getCreator().getPersonName().getFullName();
            String labTechContact = "None";
            String labID = testOrder.getCreator().getPerson().getPersonId().toString();
            String email =  testOrder.getCreator().getPerson().getAddresses().toString();

            if (getProviderAttributeValue(Objects.requireNonNull(getProviderAppributesFromPerson(testOrder.getCreator().getPerson()))) != null) {
                labTechContact = getProviderAttributeValue(Objects.requireNonNull(getProviderAppributesFromPerson(testOrder.getCreator().getPerson())));
            }

            String obsSampleType = testOrder.getSpecimenSource().getName().getName();
            if (getProviderAttributeValue(testOrder.getOrderer().getActiveAttributes()) != null) {
                ordererContact = getProviderAttributeValue(testOrder.getOrderer().getActiveAttributes());
            }

            //filledJsonFile = String.format(jsonFHIRMap, requestType,clinicianNames, authoredOn,obsSampleType,     patientID, patientName,patientDOB, patientGender,patientAddress,patientNationality,patientNationalID,patientOccupation,patientAge,labTechNames, labTechContact);
            filledJsonFile = String.format(jsonFHIRMap, requestType,patientID, patientNames,patientDOB, patientGender,patientAddress,patientNationality,patientNationalID,patientAge,patientOccupation);
            System.out.print(filledJsonFile);
        }
        jsonMap.put("json", filledJsonFile);
        System.out.print(jsonMap);
        return jsonMap;

    }

    private String proccessMappings(Concept concept) {
        for (ConceptMap conceptMap : concept.getConceptMappings()) {
            return conceptMap.getConceptReferenceTerm().getCode();
        }
        return null;
    }

    private String getProviderByEncounterRole(Encounter encounter, String encounterRoleName) {
        for (EncounterProvider provider : encounter.getActiveEncounterProviders()) {
            if (provider.getEncounterRole().getName() == encounterRoleName) {
                return provider.getProvider().getName();
            }
        }
        return null;
    }

    public List<Order> getOrders() throws IOException, ParseException {
        OrderService orderService = Context.getOrderService();
        List<Order> orders = new ArrayList<>();
        List list = Context.getAdministrationService().executeSQL(ALIS_LAB_TEST_QUERY, true);
        if (list.size() > 0) {
            for (Object o : list) {
                Order order = orderService.getOrder(Integer.parseUnsignedInt(((ArrayList) o).get(0).toString()));
                if (order.getAccessionNumber() != null && order.isActive()) {
                    orders.add(order);
                }
            }
        }
        return orders;
    }

    private String getProviderAttributeValue(Collection<ProviderAttribute> providerAttributes) {
        for (ProviderAttribute providerAttribute : providerAttributes) {
            if (providerAttribute.getAttributeType().getName().equals("Phone Number")) {
                return providerAttribute.getValue().toString();
            }

        }
        return null;
    }

    private Collection<ProviderAttribute> getProviderAppributesFromPerson(Person person) {
        List<Provider> providers = (List<Provider>) Context.getProviderService().getProvidersByPerson(person);
        if (providers != null) {
            return providers.get(0).getActiveAttributes();
        }
        return null;
    }

}
