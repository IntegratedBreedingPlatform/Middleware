
package org.generationcp.middleware.service.api.study;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.generationcp.middleware.domain.study.StudyTypeDto;

/**
 * Summary information about a study (Trials and Nurseries).
 *
 */
public class StudySummary {

	private Integer id;
	private String name;
	private String title;
	private String objective;
	private StudyTypeDto type;
	private String startDate;
	private String endDate;
	private String programUUID;
	private String principalInvestigator;
	private String location;
	private String season;

	public StudySummary() {

	}

	public StudySummary(
		final Integer id, final String name, final String title, final String objective, final StudyTypeDto type, final String startDate, final String endDate,
			final String programUUID, final String principalInvestigator, final String location, final String season) {
		this.id = id;
		this.name = name;
		this.title = title;
		this.objective = objective;
		this.type = type;
		this.startDate = startDate;
		this.endDate = endDate;
		this.programUUID = programUUID;
		this.principalInvestigator = principalInvestigator;
		this.location = location;
		this.season = season;
	}

	public StudySummary(final Integer studyId) {
		this.id = studyId;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public String getObjective() {
		return this.objective;
	}

	public void setObjective(final String objective) {
		this.objective = objective;
	}

	public StudyTypeDto getType() {
		return this.type;
	}

	public void setType(final StudyTypeDto type) {
		this.type = type;
	}

	public String getStartDate() {
		return this.startDate;
	}

	public void setStartDate(final String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return this.endDate;
	}

	public void setEndDate(final String endDate) {
		this.endDate = endDate;
	}

	public String getProgramUUID() {
		return this.programUUID;
	}

	public void setProgramUUID(final String programUUID) {
		this.programUUID = programUUID;
	}

	public String getPrincipalInvestigator() {
		return this.principalInvestigator;
	}

	public void setPrincipalInvestigator(final String principalInvestigator) {
		this.principalInvestigator = principalInvestigator;
	}

	public String getLocation() {
		return this.location;
	}

	public void setLocation(final String location) {
		this.location = location;
	}

	public String getSeason() {
		return this.season;
	}

	public void setSeason(final String season) {
		this.season = season;
	}

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this).toString();
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof StudySummary)) {
			return false;
		}
		final StudySummary castOther = (StudySummary) other;
		return new EqualsBuilder().append(this.id, castOther.id).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id).toHashCode();
	}
}
