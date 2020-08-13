package org.generationcp.middleware.service.api.study;

import org.generationcp.middleware.pojos.SortedPageRequest;
import org.pojomatic.Pojomatic;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StudySearchFilter {

	// Study DTO fields
	public static final String ACTIVE = "active";
	public static final String PROGRAM_DB_ID = "programDbId";
	public static final String PROGRAM_NAME = "programName";
	public static final String STUDY_DB_ID = "studyDbId";
	public static final String STUDY_NAME = "studyName";
	public static final String TRIAL_DB_ID = "trialDbId";
	public static final String TRIAL_NAME = "trialName";
	public static final String STUDY_TYPE_DB_ID = "studyTypeDbId";
	public static final String STUDY_TYPE_NAME = "studyTypeName";
	public static final String SEASON_DB_ID = "seasonDbId";
	public static final String SEASON = "season";
	public static final String YEAR = "year";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";
	public static final String LOCATION_DB_ID = "locationDbId";
	public static final String LOCATION_NAME = "locationName";
	public static final String COMMON_CROP_NAME = "commonCropName";

	public static final List<String> SORTABLE_FIELDS = Collections.unmodifiableList(Arrays
		.asList(PROGRAM_DB_ID, PROGRAM_NAME, STUDY_DB_ID, STUDY_NAME, TRIAL_DB_ID, TRIAL_NAME, STUDY_TYPE_DB_ID, STUDY_TYPE_NAME,
			SEASON_DB_ID, SEASON, START_DATE, END_DATE, LOCATION_DB_ID, LOCATION_NAME));

	private String commonCropName;
	private String studyTypeDbId;
	private String programDbId;
	private String locationDbId;
	private String seasonDbId;
	private String trialDbId;
	private String studyDbId;
	private Boolean active;

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
