package org.generationcp.middleware.service.api.study;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StudyMetadata {

	private Integer nurseryOrTrialId;

	private Integer studyDbId;

	private String studyName;

	private String studyDescription;

	private String studyObjective;

	private String studyType;

	private String studyTypeName;

	private List<String> seasons = new ArrayList<>();

	private Integer trialDbId;

	private String trialName;

	private Date startDate;

	private Date endDate;

	private Boolean active;

	private Integer locationId;

	private String experimentalDesign;

	private Date lastUpdate;

	private transient int hashCode;

	public StudyMetadata() {
	}

	public StudyMetadata(final Integer nurseryOrTrialId, final Integer studyDbId, final Integer locationId, final Boolean active,
		final Date endDate, final Date startDate, final Integer trialDbId, final List<String> seasons, final String trialName,
		final String studyType, final String studyTypeName, final String studyName, final String studyDescription, final String experimentalDesign,
		final Date lastUpdate) {
		this.nurseryOrTrialId = nurseryOrTrialId;
		this.studyDbId = studyDbId;
		this.locationId = locationId;
		this.active = active;
		this.endDate = endDate;
		this.startDate = startDate;
		this.trialDbId = trialDbId;
		this.seasons = seasons;
		this.trialName = trialName;
		this.studyType = studyType;
		this.studyTypeName = studyTypeName;
		this.studyName = studyName;
		this.studyDescription = studyDescription;
		this.experimentalDesign = experimentalDesign;
		this.lastUpdate = lastUpdate;
	}

	public Integer getNurseryOrTrialId() {
		return nurseryOrTrialId;
	}

	public StudyMetadata setNurseryOrTrialId(final Integer nurseryOrTrialId) {
		this.nurseryOrTrialId = nurseryOrTrialId;
		return this;
	}

	public Integer getStudyDbId() {
		return studyDbId;
	}

	public StudyMetadata setStudyDbId(final Integer studyDbId) {
		this.studyDbId = studyDbId;
		return this;
	}

	public String getStudyName() {
		return studyName;
	}

	public StudyMetadata setStudyName(final String studyName) {
		this.studyName = studyName;
		return this;
	}

	public String getStudyType() {
		return studyType;
	}

	public StudyMetadata setStudyType(final String studyType) {
		this.studyType = studyType;
		return this;
	}

	public List<String> getSeasons() {
		return seasons;
	}

	public StudyMetadata setSeasons(final List<String> seasons) {
		this.seasons = seasons;
		return this;
	}

	public Integer getTrialDbId() {
		return trialDbId;
	}

	public StudyMetadata setTrialDbId(final Integer trialDbId) {
		this.trialDbId = trialDbId;
		return this;
	}

	public String getTrialName() {
		return trialName;
	}

	public StudyMetadata setTrialName(final String trialName) {
		this.trialName = trialName;
		return this;
	}

	public Date getStartDate() {
		return startDate;
	}

	public StudyMetadata setStartDate(final Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public Date getEndDate() {
		return endDate;
	}

	public StudyMetadata setEndDate(final Date endDate) {
		this.endDate = endDate;
		return this;
	}

	public Boolean getActive() {
		return active;
	}

	public StudyMetadata setActive(final Boolean active) {
		this.active = active;
		return this;
	}

	public Integer getLocationId() {
		return locationId;
	}

	public StudyMetadata setLocationId(final Integer locationId) {
		this.locationId = locationId;
		return this;
	}

	public String getStudyDescription() {
		return studyDescription;
	}

	public StudyMetadata setStudyDescription(final String studyDescription) {
		this.studyDescription = studyDescription;
		return this;
	}

	public String getStudyObjective() {
		return this.studyObjective;
	}

	public StudyMetadata setStudyObjective(final String studyObjective) {
		this.studyObjective = studyObjective;
		return this;
	}


	public String getStudyTypeName() {
		return this.studyTypeName;
	}

	public void setStudyTypeName(final String studyTypeName) {
		this.studyTypeName = studyTypeName;
	}

	public void addSeason(String season) {
		this.seasons.add(season);
	}

	public String getExperimentalDesign() {
		return this.experimentalDesign;
	}

	public StudyMetadata setExperimentalDesign(final String experimentalDesign) {
		this.experimentalDesign = experimentalDesign;
		return this;
	}

	public Date getLastUpdate() {
		return this.lastUpdate;
	}

	public StudyMetadata setLastUpdate(final Date lastUpdate) {
		this.lastUpdate = lastUpdate;
		return this;
	}

	@Override public boolean equals(final Object other) {
		if (!(other instanceof StudyMetadata))
			return false;
		StudyMetadata castOther = (StudyMetadata) other;
		return new EqualsBuilder().append(this.nurseryOrTrialId, castOther.getNurseryOrTrialId())
			.append(this.studyDbId, castOther.getStudyDbId()).append(this.studyName, castOther.getStudyName())
			.append(this.studyDescription, castOther.getStudyDescription())
			.append(this.experimentalDesign, castOther.getExperimentalDesign())
			.append(this.studyType, castOther.getStudyType()).append(this.seasons, castOther.getSeasons())
			.append(this.trialDbId, castOther.getTrialDbId()).append(this.trialName, castOther.getTrialName())
			.append(this.startDate, castOther.getStartDate()).append(this.endDate, castOther.getEndDate())
			.append(this.active, castOther.getActive()).append(this.locationId, castOther.getLocationId()).isEquals();
	}

	@Override public int hashCode() {
		if (hashCode == 0) {
			hashCode = new HashCodeBuilder().append(nurseryOrTrialId).append(studyDbId).append(studyName).append(studyType).append(seasons)
				.append(trialDbId).append(trialName).append(startDate).append(endDate).append(active).append(locationId).toHashCode();
		}
		return hashCode;
	}

}
