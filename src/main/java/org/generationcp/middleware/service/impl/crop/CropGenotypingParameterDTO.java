package org.generationcp.middleware.service.impl.crop;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class CropGenotypingParameterDTO {

	private int genotypingParameterId;
	private String cropName;
	private String endpoint;
	private String tokenEndpoint;
	private String userName;
	private String password;
	private String programId;
	private String baseUrl;
	private String token;

	public String getCropName() {
		return this.cropName;
	}

	public void setCropName(final String cropName) {
		this.cropName = cropName;
	}

	public String getEndpoint() {
		return this.endpoint;
	}

	public void setEndpoint(final String endpoint) {
		this.endpoint = endpoint;
	}

	public String getTokenEndpoint() {
		return this.tokenEndpoint;
	}

	public void setTokenEndpoint(final String tokenEndpoint) {
		this.tokenEndpoint = tokenEndpoint;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(final String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public String getProgramId() {
		return this.programId;
	}

	public void setProgramId(final String programId) {
		this.programId = programId;
	}

	public int getGenotypingParameterId() {
		return this.genotypingParameterId;
	}

	public void setGenotypingParameterId(final int genotypingParameterId) {
		this.genotypingParameterId = genotypingParameterId;
	}

	public String getBaseUrl() {
		return this.baseUrl;
	}

	public void setBaseUrl(final String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getToken() {
		return this.token;
	}

	public void setToken(final String token) {
		this.token = token;
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

}
