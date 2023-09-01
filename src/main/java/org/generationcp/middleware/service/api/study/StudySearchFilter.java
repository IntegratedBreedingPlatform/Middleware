package org.generationcp.middleware.service.api.study;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AutoProperty
public class StudySearchFilter {

	private String commonCropName;
	private String studyTypeDbId;
	private List<String> programDbIds = new ArrayList<>();
	private String locationDbId;
	private String seasonDbId;
	private String trialPUI;
	private String studyPUI;
	private List<String> trialDbIds = new ArrayList<>();
	private String trialName;
	private String contactDbId;
	private List<String> studyDbIds = new ArrayList<>();
	private String germplasmDbId;
	private Integer observationVariableDbId;
	private Boolean active;
	private Date searchDateRangeStart;
	private Date searchDateRangeEnd;
	private String externalReferenceSource;
	private String externalReferenceID;

	public StudySearchFilter() {

	}

	public String getStudyTypeDbId() {
		return this.studyTypeDbId;
	}

	public void setStudyTypeDbId(final String studyTypeDbId) {
		this.studyTypeDbId = studyTypeDbId;
	}

	public List<String> getProgramDbIds() {
		return this.programDbIds;
	}

	public void setProgramDbIds(final List<String> programDbIds) {
		this.programDbIds = programDbIds;
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

	public String getStudyPUI() {
		return this.studyPUI;
	}

	public void setStudyPUI(final String studyPUI) {
		this.studyPUI = studyPUI;
	}

	public List<String> getTrialDbIds() {
		return this.trialDbIds;
	}

	public void setTrialDbIds(final List<String> trialDbIds) {
		this.trialDbIds = trialDbIds;
	}

	public List<String> getStudyDbIds() {
		return this.studyDbIds;
	}

	public void setStudyDbIds(final List<String> studyDbIds) {
		this.studyDbIds = studyDbIds;
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
		return this.trialPUI;
	}

	public void setTrialPUI(final String trialPUI) {
		this.trialPUI = trialPUI;
	}

	public String getTrialName() {
		return this.trialName;
	}

	public void setTrialName(final String trialName) {
		this.trialName = trialName;
	}

	public String getContactDbId() {
		return this.contactDbId;
	}

	public void setContactDbId(final String contactDbId) {
		this.contactDbId = contactDbId;
	}

	public Date getSearchDateRangeStart() {
		return this.searchDateRangeStart;
	}

	public void setSearchDateRangeStart(final Date searchDateRangeStart) {
		this.searchDateRangeStart = searchDateRangeStart;
	}

	public Date getSearchDateRangeEnd() {
		return this.searchDateRangeEnd;
	}

	public void setSearchDateRangeEnd(final Date searchDateRangeEnd) {
		this.searchDateRangeEnd = searchDateRangeEnd;
	}

	public String getGermplasmDbId() {
		return this.germplasmDbId;
	}

	public void setGermplasmDbId(final String germplasmDbId) {
		this.germplasmDbId = germplasmDbId;
	}

	public void setGermplasmDbid(final String germplasmDbId) {
		this.germplasmDbId = germplasmDbId;
	}

	public Integer getObservationVariableDbId() {
		return this.observationVariableDbId;
	}

	public void setObservationVariableDbId(final Integer observationVariableDbId) {
		this.observationVariableDbId = observationVariableDbId;
	}

	public String getExternalReferenceSource() {
		return this.externalReferenceSource;
	}

	public void setExternalReferenceSource(final String externalReferenceSource) {
		this.externalReferenceSource = externalReferenceSource;
	}

	public String getExternalReferenceID() {
		return this.externalReferenceID;
	}

	public void setExternalReferenceID(final String externalReferenceID) {
		this.externalReferenceID = externalReferenceID;
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
