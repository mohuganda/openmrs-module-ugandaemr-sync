package org.openmrs.module.ugandaemrsync.model;

import org.hibernate.annotations.Type;
import org.openmrs.BaseOpenmrsData;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;

@Entity(name = "ugandaemrsync.SyncFHIRProfile")
@Table(name = "sync_fhir_profile")
public class SyncFHIRProfile extends BaseOpenmrsData {

	@Id
	@GeneratedValue
	@Column(name = "profile_id")
	private int profileId;

	@Column(name = "name", length = 255)
	private String name;

	@Column(name = "resource_types", length = 255)
	private String resourceTypes;

	@Column(name = "resource_search_parameter")
	@Type(type="text")
	private String resourceSearchParameter;

	@Column(name = "generate_bundle")
	private Boolean generateBundle;

	@Column(name = "url_end_point", length = 255)
	private String url;

	@Column(name = "url_token", length = 255)
	private String urlToken;

	@Column(name = "url_username", length = 255)
	private String urlUserName;

	@Column(name = "url_password", length = 255)
	private String urlPassword;

	public int getProfileId() {
		return profileId;
	}

	public void setProfileId(int profileId) {
		this.profileId = profileId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getResourceTypes() {
		return resourceTypes;
	}

	public void setResourceTypes(String resourceTypes) {
		this.resourceTypes = resourceTypes;
	}

	public String getResourceSearchParameter() {
		return resourceSearchParameter;
	}

	public void setResourceSearchParameter(String resourceSearchParameter) {
		this.resourceSearchParameter = resourceSearchParameter;
	}

	public Boolean getGenerateBundle() {
		return generateBundle;
	}

	public void setGenerateBundle(Boolean generateBundle) {
		this.generateBundle = generateBundle;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrlToken() {
		return urlToken;
	}

	public void setUrlToken(String urlToken) {
		this.urlToken = urlToken;
	}

	public String getUrlUserName() {
		return urlUserName;
	}

	public void setUrlUserName(String urlUserName) {
		this.urlUserName = urlUserName;
	}

	public String getUrlPassword() {
		return urlPassword;
	}

	public void setUrlPassword(String urlPassword) {
		this.urlPassword = urlPassword;
	}

	@Override
	public Integer getId() {
		return profileId;
	}

	@Override
	public void setId(Integer id) {

	}
}
