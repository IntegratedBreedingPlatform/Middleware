package org.generationcp.middleware.api.brapi.v2.observation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import liquibase.util.StringUtils;
import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.service.api.study.SeasonDto;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;
import java.util.List;
import java.util.Map;

@AutoProperty
public class ObservationDto {

	private Map<String, String> additionalInfo;
	private String collector;
	private List<ExternalReferenceDTO> externalReferences;
	private String germplasmDbId;
	private String germplasmName;
	private String observationDbId;

	@JsonSerialize(as = Date.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
	private Date observationTimeStamp;

	private String observationUnitDbId;
	private String observationUnitName;
	private String observationVariableDbId;
	private String observationVariableName;
	private SeasonDto season;
	private String studyDbId;
	private String uploadedBy;
	private String value;

	public Map<String, String> getAdditionalInfo() {
		return this.additionalInfo;
	}

	public void setAdditionalInfo(final Map<String, String> additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public String getCollector() {
		return this.collector;
	}

	public void setCollector(final String collector) {
		this.collector = collector;
	}

	public List<ExternalReferenceDTO> getExternalReferences() {
		return this.externalReferences;
	}

	public void setExternalReferences(final List<ExternalReferenceDTO> externalReferences) {
		this.externalReferences = externalReferences;
	}

	public String getGermplasmDbId() {
		return this.germplasmDbId;
	}

	public void setGermplasmDbId(final String germplasmDbId) {
		this.germplasmDbId = germplasmDbId;
	}

	public String getGermplasmName() {
		return this.germplasmName;
	}

	public void setGermplasmName(final String germplasmName) {
		this.germplasmName = germplasmName;
	}

	public String getObservationDbId() {
		return this.observationDbId;
	}

	public void setObservationDbId(final String observationDbId) {
		this.observationDbId = observationDbId;
	}

	public Date getObservationTimeStamp() {
		return this.observationTimeStamp;
	}

	public void setObservationTimeStamp(final Date observationTimeStamp) {
		this.observationTimeStamp = observationTimeStamp;
	}

	public String getObservationUnitDbId() {
		return this.observationUnitDbId;
	}

	public void setObservationUnitDbId(final String observationUnitDbId) {
		this.observationUnitDbId = observationUnitDbId;
	}

	public String getObservationUnitName() {
		return this.observationUnitName;
	}

	public void setObservationUnitName(final String observationUnitName) {
		this.observationUnitName = observationUnitName;
	}

	public String getObservationVariableDbId() {
		return this.observationVariableDbId;
	}

	public void setObservationVariableDbId(final String observationVariableDbId) {
		this.observationVariableDbId = observationVariableDbId;
	}

	public String getObservationVariableName() {
		return this.observationVariableName;
	}

	public void setObservationVariableName(final String observationVariableName) {
		this.observationVariableName = observationVariableName;
	}

	public SeasonDto getSeason() {
		return this.season;
	}

	public void setSeason(final SeasonDto season) {
		this.season = season;
	}

	public String getStudyDbId() {
		return this.studyDbId;
	}

	public void setStudyDbId(final String studyDbId) {
		this.studyDbId = studyDbId;
	}

	public String getUploadedBy() {
		return this.uploadedBy;
	}

	public void setUploadedBy(final String uploadedBy) {
		this.uploadedBy = uploadedBy;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	@JsonIgnore
	public void setSeasonName(final String seasonName) {
		if(StringUtils.isNotEmpty(seasonName)) {
			if (this.season == null) {
				this.season = new SeasonDto();
			}
			this.season.setSeason(seasonName);
		}
	}

	@JsonIgnore
	public void setSeasonDbId(final String seasonDbId) {
		if(StringUtils.isNotEmpty(seasonDbId)) {
			if (this.season == null) {
				this.season = new SeasonDto();
			}
			this.season.setSeasonDbId(seasonDbId);
		}
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

}
