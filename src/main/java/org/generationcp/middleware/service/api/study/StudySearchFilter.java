package org.generationcp.middleware.service.api.study;

import org.pojomatic.Pojomatic;

import java.util.Date;

public class StudySearchFilter {

	private String commonCropName;
	private String studyTypeDbId;
	private String programDbId;
	private String locationDbId;
	private String seasonDbId;
	private String trialPUI;
	private String trialDbId;
	private String trialName;
	private String contactDbId;
	private String studyDbId;
	private Integer germplasmDbId;
	private Integer observationVariableDbId;
	private Boolean active;
	private Date searchDateRangeStart;
	private Date searchDateRangeEnd;

	public StudySearchFilter() {

	}

	public StudySearchFilter(final String studyTypeDbId, final String programDbId, final String locationDbId, final String seasonDbId,
		final String trialDbId, final String studyDbId, final Boolean active) {
		this.studyTypeDbId = studyTypeDbId;
		this.programDbId = programDbId;
		this.locationDbId = locationDbId;
		this.seasonDbId = seasonDbId;
		this.trialDbId = trialDbId;
		this.studyDbId = studyDbId;
		this.active = active;
	}

	public String getStudyTypeDbId() {
		return this.studyTypeDbId;
	}

	public StudySearchFilter withStudyTypeDbId(final String studyTypeDbId) {
		this.studyTypeDbId = studyTypeDbId;
		return this;
	}

	public String getProgramDbId() {
		return this.programDbId;
	}

	public StudySearchFilter withProgramDbId(final String programDbId) {
		this.programDbId = programDbId;
		return this;
	}

	public String getLocationDbId() {
		return this.locationDbId;
	}

	public StudySearchFilter withLocationDbId(final String locationDbId) {
		this.locationDbId = locationDbId;
		return this;
	}

	public String getSeasonDbId() {
		return this.seasonDbId;
	}

	public StudySearchFilter withSeasonDbId(final String seasonDbId) {
		this.seasonDbId = seasonDbId;
		return this;
	}

	public String getTrialDbId() {
		return this.trialDbId;
	}

	public StudySearchFilter withTrialDbId(final String trialDbId) {
		this.trialDbId = trialDbId;
		return this;
	}

	public String getStudyDbId() {
		return this.studyDbId;
	}

	public StudySearchFilter withStudyDbId(final String studyDbId) {
		this.studyDbId = studyDbId;
		return this;
	}

	public Boolean getActive() {
		return this.active;
	}

	public StudySearchFilter withActive(final Boolean active) {
		this.active = active;
		return this;
	}

	public String getCommonCropName() {
		return this.commonCropName;
	}

	public StudySearchFilter withCommonCropName(final String commonCropName) {
		this.commonCropName = commonCropName;
		return this;
	}

	public String getTrialPUI() {
		return this.trialPUI;
	}

	public StudySearchFilter withTrialPUI(final String trialPUI) {
		this.trialPUI = trialPUI;
		return this;
	}

	public String getTrialName() {
		return this.trialName;
	}

	public StudySearchFilter withTrialName(final String trialName) {
		this.trialName = trialName;
		return this;
	}

	public String getContactDbId() {
		return this.contactDbId;
	}

	public StudySearchFilter withContactDbId(final String contactDbId) {
		this.contactDbId = contactDbId;
		return this;
	}

	public Date getSearchDateRangeStart() {
		return this.searchDateRangeStart;
	}

	public StudySearchFilter withSearchDateRangeStart(final Date searchDateRangeStart) {
		this.searchDateRangeStart = searchDateRangeStart;
		return this;
	}

	public Date getSearchDateRangeEnd() {
		return this.searchDateRangeEnd;
	}

	public StudySearchFilter withSearchDateRangeEnd(final Date searchDateRangeEnd) {
		this.searchDateRangeEnd = searchDateRangeEnd;
		return this;
	}

	public Integer getGermplasmDbId() {
		return germplasmDbId;
	}

	public void setGermplasmDbId(final Integer germplasmDbId) {
		this.germplasmDbId = germplasmDbId;
	}

	public StudySearchFilter withGermplasmDbid(final Integer germplasmDbId) {
		this.germplasmDbId = germplasmDbId;
		return this;
	}

	public Integer getObservationVariableDbId() {
		return observationVariableDbId;
	}

	public void setObservationVariableDbId(final Integer observationVariableDbId) {
		this.observationVariableDbId = observationVariableDbId;
	}

	public StudySearchFilter withObservationVariableDbId(final Integer observationVariableDbId) {
		this.observationVariableDbId = observationVariableDbId;
		return this;
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
