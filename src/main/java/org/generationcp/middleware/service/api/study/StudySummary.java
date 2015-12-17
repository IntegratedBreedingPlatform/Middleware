
package org.generationcp.middleware.service.api.study;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.generationcp.middleware.domain.oms.StudyType;

/**
 * Summary information about a study (Trials and Nurseries).
 *
 */
public class StudySummary {

	private Integer id;
	private String name;
	private String title;
	private String objective;
	private StudyType type;
	private String startDate;
	private String endDate;
	private String programUUID;
	private String principalInvestigator;
	private String location;
	private String season;

	public StudySummary() {

	}

	public StudySummary(Integer id, String name, String title, String objective, StudyType type, String startDate, String endDate,
			String programUUID, String principalInvestigator, String location, String season) {
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

	public StudySummary(Integer studyId) {
		this.id = studyId;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getObjective() {
		return this.objective;
	}

	public void setObjective(String objective) {
		this.objective = objective;
	}

	public StudyType getType() {
		return this.type;
	}

	public void setType(StudyType type) {
		this.type = type;
	}

	public String getStartDate() {
		return this.startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return this.endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getProgramUUID() {
		return this.programUUID;
	}

	public void setProgramUUID(String programUUID) {
		this.programUUID = programUUID;
	}

	public String getPrincipalInvestigator() {
		return this.principalInvestigator;
	}

	public void setPrincipalInvestigator(String principalInvestigator) {
		this.principalInvestigator = principalInvestigator;
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

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this).toString();
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof StudySummary)) {
			return false;
		}
		StudySummary castOther = (StudySummary) other;
		return new EqualsBuilder().append(this.id, castOther.id).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id).toHashCode();
	}
}
