package org.generationcp.middleware.service.api.study;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.generationcp.middleware.service.api.user.UserDto;

import java.util.List;
import java.util.Map;

public class StudyDetailsDto {

	private StudyMetadata metadata;

	private List<UserDto> contacts;

	private Map<String, String> additionalInfo;

	private transient int hashCode;

	public StudyDetailsDto() {
	}

	public StudyDetailsDto(final StudyMetadata metadata, final List<UserDto> contacts, final Map<String, String> additionalInfo) {
		this.metadata = metadata;
		this.contacts = contacts;
		this.additionalInfo = additionalInfo;
	}

	public StudyMetadata getMetadata() {
		return metadata;
	}

	public StudyDetailsDto setMetadata(final StudyMetadata metadata) {
		this.metadata = metadata;
		return this;
	}

	public List<UserDto> getContacts() {
		return contacts;
	}

	public StudyDetailsDto setContacts(final List<UserDto> contacts) {
		this.contacts = contacts;
		return this;
	}

	public Map<String, String> getAdditionalInfo() {
		return additionalInfo;
	}

	public StudyDetailsDto setAdditionalInfo(final Map<String, String> additionalInfo) {
		this.additionalInfo = additionalInfo;
		return this;
	}

	@Override public boolean equals(final Object other) {
		if (!(other instanceof StudyDetailsDto))
			return false;
		StudyDetailsDto castOther = (StudyDetailsDto) other;
		return new EqualsBuilder().append(this.additionalInfo, castOther.getAdditionalInfo()).append(this.contacts, castOther.getContacts())
				.append(this.metadata, castOther.getMetadata()).isEquals();
	}

	@Override public int hashCode() {
		if (hashCode == 0) {
			hashCode = new HashCodeBuilder().append(additionalInfo).append(contacts).append(metadata).toHashCode();
		}
		return hashCode;
	}

}
