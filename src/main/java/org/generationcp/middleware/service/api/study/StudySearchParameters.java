package org.generationcp.middleware.service.api.study;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


public class StudySearchParameters {

	private String programUniqueId;
	private String location;
	private String season;
	private String principalInvestigator;

	public String getProgramUniqueId() {
		return this.programUniqueId;
	}

	public void setProgramUniqueId(String programUniqueId) {
		this.programUniqueId = programUniqueId;
	}

	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getSeason() {
		return this.season;
	}

	public void setSeason(String season) {
		this.season = season;
	}

	public String getPrincipalInvestigator() {
		return this.principalInvestigator;
	}

	public void setPrincipalInvestigator(String principalInvestigator) {
		this.principalInvestigator = principalInvestigator;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof StudySearchParameters)) {
			return false;
		}
		StudySearchParameters castOther = (StudySearchParameters) other;
		return new EqualsBuilder()
				.append(this.programUniqueId, castOther.programUniqueId)
				.append(this.location, castOther.location)
				.append(this.season, castOther.season)
				.append(this.principalInvestigator, castOther.principalInvestigator)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(this.programUniqueId)
				.append(this.location)
				.append(this.season)
				.append(this.principalInvestigator)
				.toHashCode();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("programUniqueId", this.programUniqueId)
				.append("location", this.location)
				.append("season", this.season)
				.append("principalInvestigator", this.principalInvestigator)
				.toString();
	}

}
