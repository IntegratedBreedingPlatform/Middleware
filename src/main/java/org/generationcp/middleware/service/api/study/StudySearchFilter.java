package org.generationcp.middleware.service.api.study;

import org.generationcp.middleware.pojos.SortedPageRequest;
import org.pojomatic.Pojomatic;

public class StudySearchFilter {

	private String commonCropName;
	private String studyTypeDbId;
	private String programDbId;
	private String locationDbId;
	private String seasonDbId;
	private String trialDbId;
	private String studyDbId;
	private Boolean active;
	private SortedPageRequest sortedRequest = new SortedPageRequest();

	public String getStudyTypeDbId() {
		return this.studyTypeDbId;
	}

	public void setStudyTypeDbId(final String studyTypeDbId) {
		this.studyTypeDbId = studyTypeDbId;
	}

	public String getProgramDbId() {
		return this.programDbId;
	}

	public void setProgramDbId(final String programDbId) {
		this.programDbId = programDbId;
	}

	public String getLocationDbId() {
		return this.locationDbId;
	}

	public void setLocationDbId(final String locationDbId) {
		this.locationDbId = locationDbId;
	}

	public String getSeasonDbId() {
		return this.seasonDbId;
	}

	public void setSeasonDbId(final String seasonDbId) {
		this.seasonDbId = seasonDbId;
	}

	public String getTrialDbId() {
		return this.trialDbId;
	}

	public void setTrialDbId(final String trialDbId) {
		this.trialDbId = trialDbId;
	}

	public String getStudyDbId() {
		return this.studyDbId;
	}

	public void setStudyDbId(final String studyDbId) {
		this.studyDbId = studyDbId;
	}

	public Boolean getActive() {
		return this.active;
	}

	public void setActive(final Boolean active) {
		this.active = active;
	}

	public SortedPageRequest getSortedRequest() {
		return this.sortedRequest;
	}

	public void setSortedRequest(final SortedPageRequest sortedRequest) {
		this.sortedRequest = sortedRequest;
	}

	public String getCommonCropName() {
		return this.commonCropName;
	}

	public void setCommonCropName(final String commonCropName) {
		this.commonCropName = commonCropName;
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
