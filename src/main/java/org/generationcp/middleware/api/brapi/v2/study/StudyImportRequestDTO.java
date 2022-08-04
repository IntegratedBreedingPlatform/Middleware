package org.generationcp.middleware.api.brapi.v2.study;

import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.service.api.study.DataLink;
import org.generationcp.middleware.service.api.study.EnvironmentParameter;
import org.generationcp.middleware.service.api.study.ExperimentalDesign;
import org.generationcp.middleware.api.brapi.v2.observationlevel.ObservationLevel;
import org.generationcp.middleware.service.api.user.ContactDto;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AutoProperty
public class StudyImportRequestDTO {

	private boolean active;
	private Map<String, String> additionalInfo = new HashMap<>();
	private String commonCropName;
	private List<ContactDto> contacts = new ArrayList<>();
	private String culturalPractices;
	private List<DataLink> dataLinks = new ArrayList<>();
	private String documaentationURL;
	private String endDate;
	private List<EnvironmentParameter> environmentParameters;
	private ExperimentalDesign experimentalDesign;
	private List<ExternalReferenceDTO> externalReferences;
	private Map<String, String> growthFacility;
	private Map<String, String> lastUpdate;
	private String license;
	private String locationDbId;
	private String locationName;
	private List<ObservationLevel> observationLevels;
	private String observationUnitsDescription;
	private List<String> seasons;
	private String startDate;
	private String studyCode;
	private String studyDescription;
	private String studyName;
	private String studyPUI;
	private String studyType;
	private String trialDbId;
	private String trialName;

	public StudyImportRequestDTO() {

	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(final boolean active) {
		this.active = active;
	}

	public Map<String, String> getAdditionalInfo() {
		return this.additionalInfo;
	}

	public void setAdditionalInfo(final Map<String, String> additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public String getCommonCropName() {
		return this.commonCropName;
	}

	public void setCommonCropName(final String commonCropName) {
		this.commonCropName = commonCropName;
	}

	public List<ContactDto> getContacts() {
		return this.contacts;
	}

	public void setContacts(final List<ContactDto> contacts) {
		this.contacts = contacts;
	}

	public String getCulturalPractices() {
		return this.culturalPractices;
	}

	public void setCulturalPractices(final String culturalPractices) {
		this.culturalPractices = culturalPractices;
	}

	public List<DataLink> getDataLinks() {
		return this.dataLinks;
	}

	public void setDataLinks(final List<DataLink> dataLinks) {
		this.dataLinks = dataLinks;
	}

	public String getDocumaentationURL() {
		return this.documaentationURL;
	}

	public void setDocumaentationURL(final String documaentationURL) {
		this.documaentationURL = documaentationURL;
	}

	public String getEndDate() {
		return this.endDate;
	}

	public void setEndDate(final String endDate) {
		this.endDate = endDate;
	}

	public List<EnvironmentParameter> getEnvironmentParameters() {
		return this.environmentParameters;
	}

	public void setEnvironmentParameters(final List<EnvironmentParameter> environmentParameters) {
		this.environmentParameters = environmentParameters;
	}

	public ExperimentalDesign getExperimentalDesign() {
		return this.experimentalDesign;
	}

	public void setExperimentalDesign(final ExperimentalDesign experimentalDesign) {
		this.experimentalDesign = experimentalDesign;
	}

	public List<ExternalReferenceDTO> getExternalReferences() {
		return this.externalReferences;
	}

	public void setExternalReferences(final List<ExternalReferenceDTO> externalReferences) {
		this.externalReferences = externalReferences;
	}

	public Map<String, String> getGrowthFacility() {
		return this.growthFacility;
	}

	public void setGrowthFacility(final Map<String, String> growthFacility) {
		this.growthFacility = growthFacility;
	}

	public Map<String, String> getLastUpdate() {
		return this.lastUpdate;
	}

	public void setLastUpdate(final Map<String, String> lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getLicense() {
		return this.license;
	}

	public void setLicense(final String license) {
		this.license = license;
	}

	public String getLocationDbId() {
		return this.locationDbId;
	}

	public void setLocationDbId(final String locationDbId) {
		this.locationDbId = locationDbId;
	}

	public String getLocationName() {
		return this.locationName;
	}

	public void setLocationName(final String locationName) {
		this.locationName = locationName;
	}

	public List<ObservationLevel> getObservationLevels() {
		return this.observationLevels;
	}

	public void setObservationLevels(final List<ObservationLevel> observationLevels) {
		this.observationLevels = observationLevels;
	}

	public String getObservationUnitsDescription() {
		return this.observationUnitsDescription;
	}

	public void setObservationUnitsDescription(final String observationUnitsDescription) {
		this.observationUnitsDescription = observationUnitsDescription;
	}

	public List<String> getSeasons() {
		return this.seasons;
	}

	public void setSeasons(final List<String> seasons) {
		this.seasons = seasons;
	}

	public String getStartDate() {
		return this.startDate;
	}

	public void setStartDate(final String startDate) {
		this.startDate = startDate;
	}

	public String getStudyCode() {
		return this.studyCode;
	}

	public void setStudyCode(final String studyCode) {
		this.studyCode = studyCode;
	}

	public String getStudyDescription() {
		return this.studyDescription;
	}

	public void setStudyDescription(final String studyDescription) {
		this.studyDescription = studyDescription;
	}

	public String getStudyName() {
		return this.studyName;
	}

	public void setStudyName(final String studyName) {
		this.studyName = studyName;
	}

	public String getStudyPUI() {
		return this.studyPUI;
	}

	public void setStudyPUI(final String studyPUI) {
		this.studyPUI = studyPUI;
	}

	public String getStudyType() {
		return this.studyType;
	}

	public void setStudyType(final String studyType) {
		this.studyType = studyType;
	}

	public String getTrialDbId() {
		return this.trialDbId;
	}

	public void setTrialDbId(final String trialDbId) {
		this.trialDbId = trialDbId;
	}

	public String getTrialName() {
		return this.trialName;
	}

	public void setTrialName(final String trialName) {
		this.trialName = trialName;
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
