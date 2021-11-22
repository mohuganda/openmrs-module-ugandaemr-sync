/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.ugandaemrsync.api.impl;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hl7.fhir.r4.model.EpisodeOfCare;
import org.openmrs.PatientProgram;
import org.openmrs.module.fhir2.FhirConstants;
import org.openmrs.module.fhir2.api.impl.BaseFhirService;
import org.openmrs.module.fhir2.api.impl.FhirVisitServiceImpl;
import org.openmrs.module.fhir2.api.search.SearchQuery;
import org.openmrs.module.fhir2.api.search.SearchQueryInclude;
import org.openmrs.module.fhir2.api.search.param.SearchParameterMap;
import org.openmrs.module.ugandaemrsync.api.FhirEpisodeOfCareService;
import org.openmrs.module.ugandaemrsync.api.dao.FhirEpisodeOfCareDao;
import org.openmrs.module.ugandaemrsync.api.translators.EpisodeOfCareTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.util.HashSet;

@Component
@Transactional
@Setter(AccessLevel.PACKAGE)
@Getter(AccessLevel.PROTECTED)
public class FhirEpisodeOfCareServiceImpl extends BaseFhirService<EpisodeOfCare, PatientProgram> implements FhirEpisodeOfCareService {
	
	@Autowired
	private FhirEpisodeOfCareDao dao;
	
	@Autowired
	private EpisodeOfCareTranslator<PatientProgram> translator;
	
	@Autowired
	private SearchQueryInclude<EpisodeOfCare> searchQueryInclude;
	
	@Autowired
	private SearchQuery<PatientProgram, EpisodeOfCare, FhirEpisodeOfCareDao, EpisodeOfCareTranslator<PatientProgram, SearchQueryInclude<EpisodeOfCare>> searchQuery;
	
	@Override
	public EpisodeOfCare get(@Nonnull String uuid) {
		
		EpisodeOfCare result;
		try {
			result = super.get(uuid);
		}
		catch (ResourceNotFoundException e) {

		}
		
		return result;
	}
	
	@Override
	public EpisodeOfCare create(@Nonnull EpisodeOfCare episodeOfCare) {
		
		if (episodeOfCare == null) {
			throw new InvalidRequestException("EpisodeOfCare cannot be null");
		}
		
		FhirUtils.Program result = FhirUtils.getOpenmrsEpisodeOfCareType(episodeOfCare).orElse(null);
		
		if (result == null) {
			throw new InvalidRequestException("Invalid type of request");
		}
		
		if (result.equals(FhirUtils.OpenmrsEpisodeOfCareType.ENCOUNTER)) {
			return super.create(episodeOfCare);
		}
		
		if (result.equals(FhirUtils.OpenmrsEpisodeOfCareType.VISIT)) {
			return visitService.create(episodeOfCare);
		}
		
		throw new InvalidRequestException("Invalid type of request");
	}
	
	@Override
	public EpisodeOfCare update(@Nonnull String uuid, @Nonnull EpisodeOfCare episodeOfCare) {
		
		if (uuid == null) {
			throw new InvalidRequestException("Uuid cannot be null.");
		}
		
		FhirUtils.OpenmrsEpisodeOfCareType result = FhirUtils.getOpenmrsEpisodeOfCareType(episodeOfCare).orElse(null);
		
		if (result == null) {
			throw new InvalidRequestException("Invalid type of request");
		}
		
		if (result.equals(FhirUtils.OpenmrsEpisodeOfCareType.ENCOUNTER)) {
			return super.update(uuid, episodeOfCare);
		}
		
		if (result.equals(FhirUtils.OpenmrsEpisodeOfCareType.VISIT)) {
			return visitService.update(uuid, episodeOfCare);
		}
		
		throw new InvalidRequestException("Invalid type of request");
	}
	
	@Override
	public EpisodeOfCare delete(@Nonnull String uuid) {
		
		if (uuid == null) {
			throw new InvalidRequestException("Uuid cannot be null.");
		}
		
		EpisodeOfCare result;
		try {
			result = super.delete(uuid);
		}
		catch (ResourceNotFoundException e) {
			result = visitService.delete(uuid);
		}
		
		return result;
	}
	
	@Override
	@Transactional(readOnly = true)
	public IBundleProvider searchForEpisodeOfCares(DateRangeParam date,
	        ReferenceAndListParam participant, ReferenceAndListParam subject, TokenAndListParam id,
	        DateRangeParam lastUpdated, HashSet<Include> includes, HashSet<Include> revIncludes) {
		SearchParameterMap theParams = new SearchParameterMap().addParameter(FhirConstants.DATE_RANGE_SEARCH_HANDLER, date)
		        .addParameter(FhirConstants.PARTICIPANT_REFERENCE_SEARCH_HANDLER, participant)
		        .addParameter(FhirConstants.PATIENT_REFERENCE_SEARCH_HANDLER, subject)
		        .addParameter(FhirConstants.COMMON_SEARCH_HANDLER, FhirConstants.ID_PROPERTY, id)
		        .addParameter(FhirConstants.COMMON_SEARCH_HANDLER, FhirConstants.LAST_UPDATED_PROPERTY, lastUpdated)
		        .addParameter(FhirConstants.INCLUDE_SEARCH_HANDLER, includes)
		        .addParameter(FhirConstants.REVERSE_INCLUDE_SEARCH_HANDLER, revIncludes);
		return searchQuery.getQueryResults(theParams, dao, translator, searchQueryInclude);
	}
}
