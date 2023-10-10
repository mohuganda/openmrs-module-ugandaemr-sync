package org.openmrs.module.ugandaemrsync.web.resource;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.BooleanProperty;
import io.swagger.models.properties.IntegerProperty;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;
import org.openmrs.api.context.Context;
import org.openmrs.module.ugandaemrsync.api.UgandaEMRSyncService;
import org.openmrs.module.ugandaemrsync.model.SyncTask;
import org.openmrs.module.ugandaemrsync.model.SyncTaskType;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Resource(name = RestConstants.VERSION_1 + "/syncTask", supportedClass = SyncTask.class, supportedOpenmrsVersions = {
        "1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*", "2.2.*", "2.3.*", "2.4.*", "2.5.*" })
public class SyncTaskResource extends DelegatingCrudResource<SyncTask> {

	@Override
	public SyncTask newDelegate() {
		return new SyncTask();
	}

	@Override
	public SyncTask save(SyncTask SyncTask	) {
		return Context.getService(UgandaEMRSyncService.class).saveSyncTask(SyncTask);
	}

	@Override
	public SyncTask getByUniqueId(String uniqueId) {
		SyncTask SyncTask = null;
		Integer id = null;

		SyncTask = Context.getService(UgandaEMRSyncService.class).getSyncTaskByUUID(uniqueId);
		if (SyncTask == null && uniqueId != null) {
			try {
				id = Integer.parseInt(uniqueId);
			}
			catch (Exception e) {}

			if (id != null) {
				SyncTask = Context.getService(UgandaEMRSyncService.class).getSyncTaskById(id);
			}
		}

		return SyncTask;
	}

	@Override
	public NeedsPaging<SyncTask> doGetAll(RequestContext context) throws ResponseException {
		return new NeedsPaging<SyncTask>(new ArrayList<SyncTask>(Context.getService(UgandaEMRSyncService.class)
		        .getAllSyncTask()), context);
	}

	@Override
	public List<Representation> getAvailableRepresentations() {
		return Arrays.asList(Representation.DEFAULT, Representation.FULL);
	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("syncTaskType");
			description.addProperty("syncTask");
			description.addProperty("status");
			description.addProperty("statusCode");

			description.addSelfLink();
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("syncTaskType");
			description.addProperty("syncTask");
			description.addProperty("status");
			description.addProperty("statusCode");
			description.addProperty("dateSent");
			description.addProperty("requireAction");
			description.addProperty("actionCompleted");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof RefRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("syncTaskType");
			description.addProperty("syncTask");
			description.addProperty("status");
			description.addProperty("statusCode");
			description.addSelfLink();
			return description;
		}
		return null;
	}

	@Override
	protected void delete(SyncTask SyncTask, String s, RequestContext requestContext) throws ResponseException {

	}

	@Override
	public void purge(SyncTask SyncTask, RequestContext requestContext) throws ResponseException {

	}

	@Override
	public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("name", Representation.REF);
		description.addProperty("dataType");
		description.addProperty("url");
		description.addProperty("urlUserName");
		description.addProperty(" urlPassword");

		return description;
	}

	@Override
	protected PageableResult doSearch(RequestContext context) {
		UgandaEMRSyncService ugandaEMRSyncService = Context.getService(UgandaEMRSyncService.class);

		String syncTaskTypeUuid = context.getParameter("synctasktype");
		String uuid = context.getParameter("uuid");

		List<SyncTask> SyncTasksByQuery = null;

		if(syncTaskTypeUuid !=null){
			SyncTaskType type = ugandaEMRSyncService.getSyncTaskTypeByUUID(syncTaskTypeUuid);
			SyncTasksByQuery = ugandaEMRSyncService.getSyncTasksByType(type);
		}

		if(uuid !=null){
			SyncTask SyncTask = ugandaEMRSyncService.getSyncTaskByUUID(uuid);
			SyncTasksByQuery.add(SyncTask);
		}


		return new NeedsPaging<SyncTask>(SyncTasksByQuery, context);
	}

	@Override
	public Model getGETModel(Representation rep) {
		ModelImpl model = (ModelImpl) super.getGETModel(rep);
		if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
			model.property("uuid", new StringProperty()).property("name", new StringProperty())
			        .property("syncTaskType", new StringProperty()).property("profileEnabled", new BooleanProperty())
			        .property("patientIdentifierType", new StringProperty()).property("numberOfResourcesInBundle", new IntegerProperty())
			        .property("durationToKeepSyncedResources", new IntegerProperty()).property("generateBundle", new BooleanProperty())
			        .property("caseBasedProfile", new BooleanProperty()).property("caseBasedPrimaryResourceType", new StringProperty())
                    .property("caseBasedPrimaryResourceTypeId", new StringProperty()) .property("resourceSearchParameter", new StringProperty());
		}
		if (rep instanceof DefaultRepresentation) {
			model.property("syncTaskType", new RefProperty("#/syncTaskType"))
			        .property("conceptSource", new RefProperty("#/definitions/ConceptGetRef"))
			        .property("creator", new RefProperty("#/definitions/UserGetRef"))
			        .property("changedBy", new RefProperty("#/definitions/UserGetRef"))
			        .property("voidedBy", new RefProperty("#/definitions/UserGetRef"));

		} else if (rep instanceof FullRepresentation) {
            model.property("patientIdentifierType", new RefProperty("#/definitions/PatientIdentifierTypeGetRef"))
                    .property("conceptSource", new RefProperty("#/definitions/ConceptGetRef"))
			        .property("creator", new RefProperty("#/definitions/UserGetRef"))
			        .property("changedBy", new RefProperty("#/definitions/UserGetRef"))
			        .property("voidedBy", new RefProperty("#/definitions/UserGetRef"));
		}
		return model;
	}

	@Override
	public Model getCREATEModel(Representation rep) {
		ModelImpl model = (ModelImpl) super.getGETModel(rep);
		if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
            model.property("uuid", new StringProperty()).property("name", new StringProperty())
                    .property("resourceTypes", new StringProperty()).property("profileEnabled", new BooleanProperty())
                    .property("patientIdentifierType", new StringProperty()).property("numberOfResourcesInBundle", new IntegerProperty())
                    .property("durationToKeepSyncedResources", new IntegerProperty()).property("generateBundle", new BooleanProperty())
                    .property("caseBasedProfile", new BooleanProperty()).property("caseBasedPrimaryResourceType", new StringProperty())
                    .property("caseBasedPrimaryResourceTypeId", new StringProperty()) .property("resourceSearchParameter", new StringProperty());
		}
		if (rep instanceof DefaultRepresentation) {
            model.property("patientIdentifierType", new RefProperty("#/definitions/PatientIdentifierTypeGetRef"))
                    .property("conceptSource", new RefProperty("#/definitions/ConceptGetRef"))
                    .property("creator", new RefProperty("#/definitions/UserGetRef"))
                    .property("changedBy", new RefProperty("#/definitions/UserGetRef"))
                    .property("voidedBy", new RefProperty("#/definitions/UserGetRef"));

		} else if (rep instanceof FullRepresentation) {
            model.property("patientIdentifierType", new RefProperty("#/definitions/PatientIdentifierTypeGetRef"))
                    .property("conceptSource", new RefProperty("#/definitions/ConceptGetRef"))
                    .property("creator", new RefProperty("#/definitions/UserGetRef"))
                    .property("changedBy", new RefProperty("#/definitions/UserGetRef"))
                    .property("voidedBy", new RefProperty("#/definitions/UserGetRef"));
		}
		return model;
	}

	@Override
	public Model getUPDATEModel(Representation rep) {
		return new ModelImpl().property("uuid", new StringProperty()).property("name", new StringProperty())
                .property("resourceTypes", new StringProperty()).property("profileEnabled", new BooleanProperty())
                .property("patientIdentifierType", new StringProperty()).property("numberOfResourcesInBundle", new IntegerProperty())
                .property("durationToKeepSyncedResources", new IntegerProperty()).property("generateBundle", new BooleanProperty())
                .property("caseBasedProfile", new BooleanProperty()).property("caseBasedPrimaryResourceType", new StringProperty())
                .property("caseBasedPrimaryResourceTypeId", new StringProperty()) .property("resourceSearchParameter", new StringProperty())
                .property("patientIdentifierType", new RefProperty("#/definitions/PatientIdentifierTypeGetRef"))
                .property("conceptSource", new RefProperty("#/definitions/ConceptGetRef"))
                .property("creator", new RefProperty("#/definitions/UserGetRef"))
                .property("changedBy", new RefProperty("#/definitions/UserGetRef"))
                .property("voidedBy", new RefProperty("#/definitions/UserGetRef"));
	}
}
