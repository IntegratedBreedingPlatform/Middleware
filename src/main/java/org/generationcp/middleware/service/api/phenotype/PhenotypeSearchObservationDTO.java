package org.generationcp.middleware.service.api.phenotype;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import liquibase.util.StringUtils;
import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.api.brapi.v2.observation.ObservationDto;
import org.generationcp.middleware.service.api.BrapiView;
import org.generationcp.middleware.service.api.study.SeasonDto;
import org.pojomatic.Pojomatic;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class PhenotypeSearchObservationDTO {

	private String observationDbId;
	private String observationVariableDbId;
	private String observationVariableName;

	@JsonSerialize(as = Date.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
	private Date observationTimeStamp;
	private SeasonDto season;
	private String collector;
	private String value;

	@JsonView(BrapiView.BrapiV2.class)
	private Map<String, String> additionalInfo;

	@JsonView(BrapiView.BrapiV2.class)
	private List<ExternalReferenceDTO> externalReferences;

	@JsonView(BrapiView.BrapiV2.class)
	private String germplasmDbId;

	@JsonView(BrapiView.BrapiV2.class)
	private String germplasmName;

	@JsonView(BrapiView.BrapiV2.class)
	private String observationUnitDbId;

	@JsonView(BrapiView.BrapiV2.class)
	private String observationUnitName;

	@JsonView(BrapiView.BrapiV2.class)
	private String studyDbId;

	@JsonView(BrapiView.BrapiV2.class)
	private String uploadedBy;

	public PhenotypeSearchObservationDTO() {

	}

	public PhenotypeSearchObservationDTO(final ObservationDto observationDto) {
		this.observationDbId = observationDto.getObservationDbId();
		this.observationVariableDbId = observationDto.getObservationVariableDbId();
		this.observationVariableName = observationDto.getObservationVariableName();
		this.observationTimeStamp = observationDto.getObservationTimeStamp();
		this.season = observationDto.getSeason();
		this.collector = observationDto.getCollector();
		this.value = observationDto.getValue();
		this.additionalInfo = observationDto.getAdditionalInfo();
		this.externalReferences = observationDto.getExternalReferences();
		this.germplasmDbId = observationDto.getGermplasmDbId();
		this.germplasmName = observationDto.getGermplasmName();
		this.observationUnitDbId = observationDto.getObservationUnitDbId();
		this.observationUnitName = observationDto.getObservationUnitName();
		this.studyDbId = observationDto.getStudyDbId();
		this.uploadedBy = observationDto.getUploadedBy();
	}

	public Map<String, String> getAdditionalInfo() {
		return this.additionalInfo;
	}

	public void setAdditionalInfo(final Map<String, String> additionalInfo) {
		this.additionalInfo = additionalInfo;
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

	public String getObservationDbId() {
		return this.observationDbId;
	}

	public void setObservationDbId(final String observationDbId) {
		this.observationDbId = observationDbId;
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

	public Date getObservationTimeStamp() {
		return this.observationTimeStamp;
	}

	public void setObservationTimeStamp(final Date observationTimeStamp) {
		this.observationTimeStamp = observationTimeStamp;
	}

	public SeasonDto getSeason() {
		return this.season;
	}

	public void setSeason(final SeasonDto season) {
		this.season = season;
	}

	public String getCollector() {
		return this.collector;
	}

	public void setCollector(final String collector) {
		this.collector = collector;
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
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

}
