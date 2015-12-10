
package org.generationcp.middleware.service.impl.study;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class StudyKey {

	private Integer studyId;

	private String crop;

	public StudyKey(Integer studyId, String crop) {
		this.studyId = studyId;
		this.crop = crop;
	}

	public Integer getStudyId() {
		return this.studyId;
	}

	public void setStudyId(final Integer studyId) {
		this.studyId = studyId;
	}

	public String getCrop() {
		return this.crop;
	}

	public void setCrop(final String crop) {
		this.crop = crop;
	}


	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof StudyKey)) {
			return false;
		}
		final StudyKey castOther = (StudyKey) other;
		return new EqualsBuilder().append(this.studyId, castOther.studyId).append(this.crop, castOther.crop).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.studyId).append(this.crop).toHashCode();
	}
}
