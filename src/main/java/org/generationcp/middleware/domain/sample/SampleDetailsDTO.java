package org.generationcp.middleware.domain.sample;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

public class SampleDetailsDTO implements Serializable {

	private static final long serialVersionUID = 2340381705850740790L;

	private Integer studyDbId;
	private Integer locationDbId;
	private String plotId;
	private String plantId;
	private String sampleId;
	private String takenBy;
	private String sampleDate;
	private String sampleType;
	private String tissueType;
	private String notes;
	private String studyName;
	private String season;
	private String locationName;
	private Integer entryNumber;
	private Integer plotNumber;
	private Integer germplasmDbId;
	private String plantingDate;
	private String harvestDate;

	public SampleDetailsDTO(){

	}

	public SampleDetailsDTO(final Integer studyDbId, final String plotId, final String plantId, final String sampleId) {
		this.studyDbId = studyDbId;
		this.plotId = plotId;
		this.plantId = plantId;
		this.sampleId = sampleId;
	}

	public Integer getStudyDbId() {
		return studyDbId;
	}

	public void setStudyDbId(Integer studyDbId) {
		this.studyDbId = studyDbId;
	}

	public Integer getLocationDbId() {
		return locationDbId;
	}

	public void setLocationDbId(Integer locationDbId) {
		this.locationDbId = locationDbId;
	}

	public String getPlotId() {
		return plotId;
	}

	public void setPlotId(String plotId) {
		this.plotId = plotId;
	}

	public String getPlantId() {
		return plantId;
	}

	public void setPlantId(String plantId) {
		this.plantId = plantId;
	}

	public String getSampleId() {
		return sampleId;
	}

	public void setSampleId(String sampleId) {
		this.sampleId = sampleId;
	}

	public String getTakenBy() {
		return takenBy;
	}

	public void setTakenBy(String takenBy) {
		this.takenBy = takenBy;
	}

	public String getSampleDate() {
		return sampleDate;
	}

	public void setSampleDate(String sampleDate) {
		this.sampleDate = sampleDate;
	}

	public String getSampleType() {
		return sampleType;
	}

	public void setSampleType(String sampleType) {
		this.sampleType = sampleType;
	}

	public String getTissueType() {
		return tissueType;
	}

	public void setTissueType(String tissueType) {
		this.tissueType = tissueType;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getStudyName() {
		return studyName;
	}

	public void setStudyName(String studyName) {
		this.studyName = studyName;
	}

	public String getSeason() {
		return season;
	}

	public void setSeason(String season) {
		this.season = season;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public Integer getEntryNumber() {
		return entryNumber;
	}

	public void setEntryNumber(Integer entryNumber) {
		this.entryNumber = entryNumber;
	}

	public Integer getPlotNumber() {
		return plotNumber;
	}

	public void setPlotNumber(Integer plotNumber) {
		this.plotNumber = plotNumber;
	}

	public Integer getGermplasmDbId() {
		return germplasmDbId;
	}

	public void setGermplasmDbId(Integer germplasmDbId) {
		this.germplasmDbId = germplasmDbId;
	}

	public String getPlantingDate() {
		return plantingDate;
	}

	public void setPlantingDate(String plantingDate) {
		this.plantingDate = plantingDate;
	}

	public String getHarvestDate() {
		return harvestDate;
	}

	public void setHarvestDate(String harvestDate) {
		this.harvestDate = harvestDate;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof SampleDetailsDTO)) {
			return false;
		}
		final SampleDetailsDTO sampleDetailsDto = (SampleDetailsDTO) other;
		return new EqualsBuilder()
			.append(this.studyDbId, sampleDetailsDto.studyDbId)
			.append(this.locationDbId, sampleDetailsDto.locationDbId)
			.append(this.plotId, sampleDetailsDto.plotId)
			.append(this.plantId, sampleDetailsDto.plantId)
			.append(this.sampleId, sampleDetailsDto.sampleId)
			.append(this.germplasmDbId, sampleDetailsDto.germplasmDbId).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.studyDbId)
			.append(this.locationDbId)
			.append(this.plotId)
			.append(this.plantId)
			.append(this.sampleId)
			.append(this.germplasmDbId).hashCode();
	}

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}
}
