package org.openmrs.module.ugandaemrsync.providers.r4;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.annotation.Delete;
import ca.uhn.fhir.rest.annotation.History;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.ResourceParam ;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.openmrs.module.fhir2.providers.util.FhirProviderUtils;
import org.openmrs.module.ugandaemrsync.api.FhirMedicationStatementService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import java.util.List;

public class MedicationStatementProvider implements IResourceProvider {

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return MedicationStatement.class;
    }

    @Autowired
    private FhirMedicationStatementService medicationStatementService;


    @Read
    public MedicationStatement getMedicationStatementByUuid(@IdParam @Nonnull IdType id) {
        MedicationStatement medicationStatement = medicationStatementService.get(id.getIdPart());
        if (medicationStatement == null) {
            throw new ResourceNotFoundException("Could not find medicationStatement with Id " + id.getIdPart());
        }
        return medicationStatement;
    }

    @Create
    @SuppressWarnings("unused")
    public MethodOutcome createMedicationStatement(@ResourceParam MedicationStatement medicationStatement) {
        return FhirProviderUtils.buildCreate(medicationStatementService.create(medicationStatement));
    }

    @Update
    @SuppressWarnings("unused")
    public MethodOutcome updateMedicationStatement(@IdParam IdType id, @ResourceParam MedicationStatement medicationStatement) {
        if (id == null || id.getIdPart() == null) {
            throw new InvalidRequestException("id must be specified to update");
        }

        return FhirProviderUtils.buildUpdate(medicationStatementService.update(id.getIdPart(), medicationStatement));
    }

    @Delete
    @SuppressWarnings("unused")
    public OperationOutcome deleteMedicationStatement(@IdParam @Nonnull IdType id) {
        org.hl7.fhir.r4.model.MedicationStatement medicationStatement = medicationStatementService.delete(id.getIdPart());
        if (medicationStatement == null) {
            throw new ResourceNotFoundException("Could not find medicationStatement to delete with id " + id.getIdPart());
        }
        return FhirProviderUtils.buildDelete(medicationStatement);
    }

    @History
    @SuppressWarnings("unused")
    public List<Resource> getMedicationStatementHistoryById(@IdParam @Nonnull IdType id) {
        MedicationStatement medicationStatement = medicationStatementService.get(id.getIdPart());
        if (medicationStatement == null) {
            throw new ResourceNotFoundException("Could not find medicationStatement with Id " + id.getIdPart());
        }
        return medicationStatement.getContained();
    }

    @Search
    public IBundleProvider searchMedicationStatement(@OptionalParam(name = MedicationStatement.SP_CATEGORY) TokenAndListParam category,  @OptionalParam(name = MedicationStatement.SP_CODE) TokenAndListParam code,@OptionalParam(name = MedicationStatement.SP_PATIENT, chainWhitelist = { "", org.hl7.fhir.r4.model.Patient.SP_IDENTIFIER,
            org.hl7.fhir.r4.model.Patient.SP_GIVEN, org.hl7.fhir.r4.model.Patient.SP_FAMILY,
            org.hl7.fhir.r4.model.Patient.SP_NAME }, targetTypes = org.hl7.fhir.r4.model.Patient.class) ReferenceAndListParam patientReference,  @OptionalParam(name = MedicationStatement.SP_IDENTIFIER) TokenAndListParam identifier,@OptionalParam(name = MedicationStatement.SP_STATUS) TokenAndListParam status) {

        return medicationStatementService.searchForMedicationStatements(category,code ,identifier, patientReference, identifier,status);
    }
}
