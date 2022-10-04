package org.generationcp.middleware.service.api.study;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang.StringUtils;
import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.api.brapi.v2.observationlevel.ObservationLevel;
import org.generationcp.middleware.service.api.BrapiView;
import org.generationcp.middleware.service.api.user.ContactDto;
import org.generationcp.middleware.util.serializer.DatePropertySerializer;
import org.generationcp.middleware.util.serializer.SeasonPropertySerializer;
import org.generationcp.middleware.util.serializer.StringToBooleanSerializer;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@AutoProperty
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudyInstanceDto {

	@JsonView(BrapiView.BrapiV2.class)
	private Map<String, String> additionalInfo;

	@JsonView(BrapiView.BrapiV2.class)
	private List<ContactDto> contacts = new ArrayList<>();

	@JsonView(BrapiView.BrapiV2.class)
	private String culturalPractices;

	@JsonView(BrapiView.BrapiV2.class)
	private List<DataLink> dataLinks = new ArrayList<>();

	@JsonView(BrapiView.BrapiV2.class)
	private List<EnvironmentParameter> environmentParameters;

	@JsonView(BrapiView.BrapiV2.class)
	private ExperimentalDesign experimentalDesign;

	@JsonView(BrapiView.BrapiV2.class)
	private List<ExternalReferenceDTO> externalReferences;

	@JsonView(BrapiView.BrapiV2.class)
	private String growthFacility;

	@JsonView(BrapiView.BrapiV2.class)
	private Map<String, String> lastUpdate;

	@JsonView(BrapiView.BrapiV2.class)
	private String license;

	@JsonView(BrapiView.BrapiV2.class)
	private String observationUnitsDescription = StringUtils.EMPTY;

	@JsonView(BrapiView.BrapiV2_1.class)
	private List<String> observationVariableDbIds;

	@JsonView(BrapiView.BrapiV2.class)
	private String studyCode = StringUtils.EMPTY;

	@JsonView(BrapiView.BrapiV2.class)
	private String studyDescription;

	@JsonView(BrapiView.BrapiV2.class)
	private String studyPUI;

	@JsonView(BrapiView.BrapiV2.class)
	private List<ObservationLevel> observationLevels;

	// Use custom serializer to convert string to boolean if active view is for 2.0
	@JsonSerialize(using = StringToBooleanSerializer.class)
	private String active;

	private String commonCropName;

	private String documentationURL;

	// Use custom serializer to format date according to view
	// V1.2-3 - yyyy-MM-dd
	// V2.0 - yyyy-MM-dd'T'HH:mm:ss.SSS'Z
	@JsonSerialize(using = DatePropertySerializer.class)
	private Date startDate;

	// User custom serializer to format date according to view
	// V1.2-3 - yyyy-MM-dd
	// V2.0 - yyyy-MM-dd'T'HH:mm:ss.SSS'Z
	@JsonSerialize(using = DatePropertySerializer.class)
	private Date endDate;

	private String studyDbId;

	private String studyName;

	private String studyType;

	private String studyTypeDbId;

	private String studyTypeName;

	@JsonSerialize(using = SeasonPropertySerializer.class)
	private List<SeasonDto> seasons;

	private String locationDbId;

	private String locationName;

	private String programDbId;

	private String programName;

	private String trialName;

	private String trialDbId;

	private Map<String, String> optionalInfo;

	public String getActive() {
		return this.active;
	}

	public void setActive(final String active) {
		this.active = active;
	}

	public String getCommonCropName() {
		return this.commonCropName;
	}

	public void setCommonCropName(final String commonCropName) {
		this.commonCropName = commonCropName;
	}

	public String getDocumentationURL() {
		return this.documentationURL;
	}

	public void setDocumentationURL(final String documentationURL) {
		this.documentationURL = documentationURL;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return The study db id
	 */
	public String getStudyDbId() {
		return this.studyDbId;
	}

	/**
	 * @param studyDbId
	 */
	public void setStudyDbId(final String studyDbId) {
		this.studyDbId = studyDbId;
	}

	/**
	 * @return The studyName
	 */
	public String getStudyName() {
		return this.studyName;
	}

	/**
	 * @param studyName
	 * @return this
	 */
	public void setStudyName(final String studyName) {
		this.studyName = studyName;
	}

	/**
	 * @return The study type
	 */
	public String getStudyType() {
		return this.studyType;
	}

	/**
	 * @param studyType
	 */
	public void setStudyType(final String studyType) {
		this.studyType = studyType;
	}

	/**
	 * @return The list of seasons
	 */
	public List<SeasonDto> getSeasons() {
		return this.seasons;
	}

	/**
	 * @param seasons
	 */
	public void setSeasons(final List<SeasonDto> seasons) {
		this.seasons = seasons;
	}

	/**
	 * @return The location db id
	 */
	public String getLocationDbId() {
		return this.locationDbId;
	}

	/**
	 * @param locationDbId
	 */
	public void setLocationDbId(final String locationDbId) {
		this.locationDbId = locationDbId;
	}

	public String getLocationName() {
		return this.locationName;
	}

	public void setLocationName(final String locationName) {
		this.locationName = locationName;
	}

	/**
	 * @return The program db id
	 */
	public String getProgramDbId() {
		return this.programDbId;
	}

	/**
	 * @param programDbId
	 */
	public void setProgramDbId(final String programDbId) {
		this.programDbId = programDbId;
	}

	public String getProgramName() {
		return this.programName;
	}

	public void setProgramName(final String programName) {
		this.programName = programName;
	}

	public String getStudyTypeDbId() {
		return this.studyTypeDbId;
	}

	public void setStudyTypeDbId(final String studyTypeDbId) {
		this.studyTypeDbId = studyTypeDbId;
	}

	public String getStudyTypeName() {
		return this.studyTypeName;
	}

	public void setStudyTypeName(final String studyTypeName) {
		this.studyTypeName = studyTypeName;
	}

	public String getTrialName() {
		return this.trialName;
	}

	public void setTrialName(final String trialName) {
		this.trialName = trialName;
	}

	public String getTrialDbId() {
		return this.trialDbId;
	}

	public void setTrialDbId(final String trialDbId) {
		this.trialDbId = trialDbId;
	}

	/**
	 * @return The map with the optional info
	 */
	public Map<String, String> getOptionalInfo() {
		return this.optionalInfo;
	}

	/**
	 * @param name  Key of the optional info
	 * @param value Value of the optional info
	 */
	public void setOptionalInfo(final String name, final String value) {
		this.optionalInfo.put(name, value);
	}

	/**
	 * @param name  Key of the optional info
	 * @param value Value of the optional info
	 * @return this
	 */
	public StudyInstanceDto addOptionalInfo(final String name, final String value) {
		this.optionalInfo.put(name, value);
		return this;
	}

	/**
	 * @param optionalInfo
	 * @return this
	 */
	public StudyInstanceDto setOptionalInfo(final Map<String, String> optionalInfo) {
		this.optionalInfo = optionalInfo;
		return this;
	}

	public Map<String, String> getAdditionalInfo() {
		return this.additionalInfo;
	}

	public void setAdditionalInfo(final Map<String, String> additionalInfo) {
		this.additionalInfo = additionalInfo;
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

	public String getGrowthFacility() {
		return this.growthFacility;
	}

	public void setGrowthFacility(final String growthFacility) {
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

	public String getObservationUnitsDescription() {
		return this.observationUnitsDescription;
	}

	public void setObservationUnitsDescription(final String observationUnitsDescription) {
		this.observationUnitsDescription = observationUnitsDescription;
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

	public String getStudyPUI() {
		return this.studyPUI;
	}

	public void setStudyPUI(final String studyPUI) {
		this.studyPUI = studyPUI;
	}

	public List<ObservationLevel> getObservationLevels() {
		return this.observationLevels;
	}

	public void setObservationLevels(final List<ObservationLevel> observationLevels) {
		this.observationLevels = observationLevels;
	}

	public List<String> getObservationVariableDbIds() {
		return observationVariableDbIds;
	}

	public void setObservationVariableDbIds(List<String> observationVariableDbIds) {
		this.observationVariableDbIds = observationVariableDbIds;
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
