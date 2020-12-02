package org.generationcp.middleware.service.api.study;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.service.api.user.UserDto;

import java.util.List;
import java.util.Map;

public class StudyDetailsDto {

	private StudyMetadata metadata;

	private List<UserDto> contacts;

	private Map<String, String> additionalInfo;

	private List<MeasurementVariable> environmentParameters;

	private List<DatasetDTO> datasets;

	private transient int hashCode;

	public StudyDetailsDto() {
	}

	public StudyDetailsDto(final StudyMetadata metadata, final List<UserDto> contacts, final Map<String, String> additionalInfo,
		final List<DatasetDTO> datasets) {
		this.metadata = metadata;
		this.contacts = contacts;
		this.additionalInfo = additionalInfo;
		this.datasets = datasets;
	}

	public StudyMetadata getMetadata() {
		return this.metadata;
	}

	public StudyDetailsDto setMetadata(final StudyMetadata metadata) {
		this.metadata = metadata;
		return this;
	}

	public List<MeasurementVariable> getEnvironmentParameters() {
		return this.environmentParameters;
	}

	public StudyDetailsDto setEnvironmentParameters(final List<MeasurementVariable> environmentParameters) {
		this.environmentParameters = environmentParameters;
		return this;
	}

	public List<UserDto> getContacts() {
		return this.contacts;
	}

	public StudyDetailsDto setContacts(final List<UserDto> contacts) {
		this.contacts = contacts;
		return this;
	}

	public Map<String, String> getAdditionalInfo() {
		return this.additionalInfo;
	}

	public StudyDetailsDto setAdditionalInfo(final Map<String, String> additionalInfo) {
		this.additionalInfo = additionalInfo;
		return this;
	}

	public List<DatasetDTO> getDatasets() {
		return this.datasets;
	}

	public void setDatasets(final List<DatasetDTO> datasets) {
		this.datasets = datasets;
	}


	@Override public boolean equals(final Object other) {
		if (!(other instanceof StudyDetailsDto))
			return false;
		final StudyDetailsDto castOther = (StudyDetailsDto) other;
		return new EqualsBuilder().append(this.additionalInfo, castOther.getAdditionalInfo()).append(this.contacts, castOther.getContacts())
				.append(this.metadata, castOther.getMetadata()).isEquals();
	}

	@Override public int hashCode() {
		if (this.hashCode == 0) {
			this.hashCode = new HashCodeBuilder().append(this.additionalInfo).append(this.contacts).append(this.metadata).toHashCode();
		}
		return this.hashCode;
	}

}
