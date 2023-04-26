package org.openmrs.module.ugandaemrsync.tasks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openmrs.*;
import org.openmrs.api.ConceptService;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ugandaemrsync.api.UgandaEMRHttpURLConnection;
import org.openmrs.module.ugandaemrsync.api.UgandaEMRSyncService;
import org.openmrs.module.ugandaemrsync.api.impl.UgandaEMRSyncServiceImpl;
import org.openmrs.module.ugandaemrsync.model.SyncFhirProfile;
import org.openmrs.module.ugandaemrsync.model.SyncTask;
import org.openmrs.module.ugandaemrsync.model.SyncTaskType;
import org.openmrs.module.ugandaemrsync.server.SyncFHIRRecord;
import org.openmrs.scheduler.tasks.AbstractTask;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static org.openmrs.module.ugandaemrsync.server.SyncConstant.*;

public class SendLabRequestToALIS_ATask extends AbstractTask {
    Log log = LogFactory.getLog(SendLabRequestToALIS_ATask.class);
    OrderService orderService = Context.getOrderService();

    @Override
    public void execute() {
        UgandaEMRHttpURLConnection ugandaEMRHttpURLConnection = new UgandaEMRHttpURLConnection();
        UgandaEMRSyncService ugandaEMRSyncService = Context.getService(UgandaEMRSyncService.class);

        if (!ugandaEMRHttpURLConnection.isConnectionAvailable()) {
            return;
        }

        SyncTaskType syncTaskType = ugandaEMRSyncService.getSyncTaskTypeByUUID(LAB_TEST_SYNC_TYPE_UUID);

        Set<Patient> patients = null;
        try {
            patients = getOrders().stream().map(Order::getPatient).collect(Collectors.toSet());


        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        if(patients.size()>0){
            for (Patient patient:patients) {
                List<Order> patientOrderList = orderService.getActiveOrders(patient, orderService.getOrderTypeByUuid(OrderType.TEST_ORDER_TYPE_UUID), null, null);


                String dataOutput=  generatePatientTestInfo(patient,patientOrderList.get(0),LAB_SEND_FHIR_JSON_STRING);
                String testOrdersJson = generatePatientOrders(patientOrderList);
                String finalJson = dataOutput + testOrdersJson+ "]}";
                System.out.println("Log FHIR Json String ---> "+finalJson);

                try {
                    Map map = ugandaEMRHttpURLConnection.sendPostBy(syncTaskType.getUrl(), "", "", "", finalJson, false);
                    if (map != null) {
                        SyncTask newSyncTask = new SyncTask();
                        newSyncTask.setDateSent(new Date());
                        newSyncTask.setCreator(Context.getUserService().getUser(1));
                        newSyncTask.setSentToUrl(syncTaskType.getUrl());
                        newSyncTask.setRequireAction(true);
                        newSyncTask.setActionCompleted(false);
                        newSyncTask.setSyncTask(patientOrderList.get(0).getOrderer().getUuid());
                        newSyncTask.setStatusCode((Integer) map.get("responseCode"));
                        newSyncTask.setStatus((String)map.get("responseMessage"));
                        newSyncTask.setSyncTaskType(ugandaEMRSyncService.getSyncTaskTypeByUUID(LAB_TEST_SYNC_TYPE_UUID));
                        ugandaEMRSyncService.saveSyncTask(newSyncTask);

                        newSyncTask.setSyncTaskType(syncTaskType);
                        ugandaEMRSyncService.saveSyncTask(newSyncTask);
                    }
                } catch (Exception e) {
                    log.error("Failed to create sync task",e);
                }
            }
        }
    }


    public String  generatePatientTestInfo(Patient patient,Order order,String json_body){
        //get patient identifiers
        String patientNo = patient.getPatientIdentifier().getIdentifier();
        String patientOPD_IPD_No = String.valueOf(patient.getPatientIdentifier().getId());
        String patientNIN = "none";

        // this is just an example
        String code = patient.getPatientIdentifier().getUuid();
        String patientFamilyName = patient.getFamilyName();
        String patientGivenName = patient.getGivenName();
        String dateOfBirth = patient.getBirthdate().toString().replace(" 00:00:00.0", "");
        String phone = String.valueOf(patient.getAttribute("14d4f066-15f5-102d-96e4-000c29c2a5d7"));
        String email = "none";
        String nationality = "Ugandan";
        String occupation = String.valueOf(patient.getAttribute("b0868a16-4f8e-43da-abfc-6338c9d8f56a"));
        String age = String.valueOf(patient.getAge());
        //process here string for patient body in json payload
        String gender = patient.getGender();
        String village = patient.getPersonAddress().getAddress3();
        String district = patient.getPersonAddress().getAddress4();

        //get Practitioner
        UgandaEMRSyncService ugandaEMRSyncService = new UgandaEMRSyncServiceImpl();
        String cadre = "None";
        String healthCenterName = ugandaEMRSyncService.getHealthCenterName();
        String healthCenterCode = ugandaEMRSyncService.getHealthCenterCode();
        String clinicianNames_ = order.getOrderer().getPerson().getFamilyName();
        String labTechNames_ = order.getOrderer().getPerson().getGivenName();
        String labTechContact_ = "None";
        String orderContact_ = "256771999900";
        String labTechEmail = "None";
        String sourceSystem = "UgandaEMR";

        String json = String.format(json_body,patientNo,patientOPD_IPD_No,patientNIN,patientFamilyName,patientGivenName,dateOfBirth,gender,phone,email,village,district,nationality,age,occupation,cadre,healthCenterCode,clinicianNames_,labTechNames_,orderContact_,labTechEmail);
        return json;

    }


    public String  generatePatientOrders(List<Order> orders){
        String testOrderPayload =" ";
        for (Order order : orders) {
            String orderJson = processJsonTestOrder(order,SEND_LAB_REQUEST_FHIR_JSON_STRING);
            testOrderPayload = testOrderPayload+ orderJson+" , ";
        }
        return testOrderPayload;

    }
    public String processJsonTestOrder(Order order, String testOrderPaylaod){
        ConceptService conceptService = Context.getConceptService();
        String testName = order.getConcept().getName().getName();
        int conceptid = order.getConcept().getConceptId();
        String uuid= order.getConcept().getUuid();
        String display_name = order.getConcept().getDisplayString();
        String conceptName = order.getConcept().getName().getName();

        String conceptQuery ="SELECT cn.name, crt.code FROM concept_name cn inner join concept_reference_map crm on(crm.concept_id=cn.concept_id)\n" +
                "        inner join concept_reference_term crt on crm.concept_reference_term_id = crt.concept_reference_term_id\n" +
                "        where cn.locale='en' and cn.concept_name_type='FULLY_SPECIFIED' and cn.concept_id ="+conceptid;
        List conceptList = Context.getAdministrationService().executeSQL(conceptQuery,true);
        List<Object> listObjConcept = (List<Object>) conceptList.get(0);
        String code = listObjConcept.get(1).toString();
        String authoredOn = order.getConcept().getDateCreated().toString();

        String note ="none";

        //Spacemine details
        String spacemine = "";
        String spaceminCode="";
        String query ="SELECT cn.name, crt.code FROM test_order t inner join concept_name cn on(cn.concept_id=t.specimen_source) inner join concept_reference_map crm on(crm.concept_id=cn.concept_id)  inner join concept_reference_term crt on crm.concept_reference_term_id = crt.concept_reference_term_id where cn.locale='en' and cn.concept_name_type='FULLY_SPECIFIED' and t.order_id ="+order.getOrderId();
        List spacemineList = Context.getAdministrationService().executeSQL(query,true);
        List<Object> listObj = (List<Object>) spacemineList.get(0);
        spacemine = listObj.get(0).toString();
        spaceminCode = listObj.get(1).toString();

        String collectedDateTime = order.getConcept().getDateCreated().toString();

        //MedicationStatement
        String medCode ="";
        String medText ="";
        String medNote ="none";
        String medStatus ="";

        //Observation
        String obsCode ="";
        String obsHospitalAdmission="";
        String obsInterimDiagnosis ="";

        //Encounter
        String enInpatientsCode ="";
        String enWardCode ="";
        String enBedCode ="";

        String locName ="none";

        String json = String.format(testOrderPaylaod,uuid,code,display_name,conceptName,authoredOn,note,spaceminCode,spacemine,spacemine,collectedDateTime,medCode
                ,medText,medNote,medStatus,medCode,medText,medNote,obsCode,obsHospitalAdmission,obsInterimDiagnosis,enInpatientsCode,enWardCode,enBedCode,locName
                ,locName,locName);

        return json;
    }

    public List<Order> getOrders() throws IOException, ParseException {
        OrderService orderService = Context.getOrderService();
        List<Order> orders = new ArrayList<>();
        List list = Context.getAdministrationService().executeSQL(LAB_ORDER_QUERY, true);
        if (list.size() > 0) {
            for (Object o : list) {
                Order order = orderService.getOrder(Integer.parseUnsignedInt(((ArrayList) o).get(0).toString()));
                // if (order.getAccessionNumber() != null && order.isActive() && order.get) { //we look at fixing the active orders
                orders.add(order);
                // }
            }
        }
        return orders;

    }

}
