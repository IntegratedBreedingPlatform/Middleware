package org.generationcp.middleware.service.api.study;

import org.pojomatic.Pojomatic;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

	public String getTrialPUI() {
		return trialPUI;
	}

	public void setTrialPUI(String trialPUI) {
		this.trialPUI = trialPUI;
	}

	public String getTrialName() {
		return trialName;
	}

	public void setTrialName(String trialName) {
		this.trialName = trialName;
	}

	public String getContactDbId() {
		return contactDbId;
	}

	public void setContactDbId(String contactDbId) {
		this.contactDbId = contactDbId;
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
