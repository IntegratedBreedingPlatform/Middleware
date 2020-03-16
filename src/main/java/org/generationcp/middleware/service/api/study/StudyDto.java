package org.generationcp.middleware.service.api.study;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;
import java.util.List;
import java.util.Map;

@AutoProperty
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudyDto {

	private String active;

	private String commonCropName;

	private String documentationURL;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date startDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date endDate;

	private String studyDbId;

	private String studyName;

	private String studyType;

	private String studyTypeDbId;

	private String studyTypeName;

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
	public StudyDto addOptionalInfo(final String name, final String value) {
		this.optionalInfo.put(name, value);
		return this;
	}

	/**
	 * @param optionalInfo
	 * @return this
	 */
	public StudyDto setOptionalInfo(final Map<String, String> optionalInfo) {
		this.optionalInfo = optionalInfo;
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
