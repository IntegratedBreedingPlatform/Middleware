package org.generationcp.middleware.domain.sample;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Date;

public class SampleDetailsDTO implements Serializable {

	private static final long serialVersionUID = -4175016670661637734L;

	private Integer studyDbId;
	private Integer locationDbId;
	private String plotId;
	private String plantBusinessKey;
	private String sampleBusinessKey;
	private String takenBy;
	private Date sampleDate;
	private String sampleType;
	private String tissueType;
	private String notes;
	private String studyName;
	private String season;
	private String locationName;
	private Integer entryNo;
	private Integer plotNo;
	private Integer gid;
	private String seedingDate;
	private String harvestDate;
	private String sampleName;
	private String designation;
	private Integer plantNo;
	private String displayDate;

	public SampleDetailsDTO() {

	}

	public SampleDetailsDTO(final Integer studyDbId, final String plotId, final String plantBusinessKey, final String SampleBusinessKey) {
		this.setStudyDbId(studyDbId);
		this.setPlotId(plotId);
		this.setPlantBusinessKey(plantBusinessKey);
		this.setSampleBusinessKey(SampleBusinessKey);
	}

	public Integer getStudyDbId() {
		return studyDbId;
	}

	public void setStudyDbId(final Integer studyDbId) {
		this.studyDbId = studyDbId;
	}

	public Integer getLocationDbId() {
		return locationDbId;
	}

	public void setLocationDbId(final Integer locationDbId) {
		this.locationDbId = locationDbId;
	}

	public String getPlotId() {
		return plotId;
	}

	public void setPlotId(final String plotId) {
		this.plotId = plotId;
	}

	public String getPlantBusinessKey() {
		return plantBusinessKey;
	}

	public void setPlantBusinessKey(final String plantBusinessKey) {
		this.plantBusinessKey = plantBusinessKey;
	}

	public String getSampleBusinessKey() {
		return sampleBusinessKey;
	}

	public void setSampleBusinessKey(final String sampleBusinessKey) {
		this.sampleBusinessKey = sampleBusinessKey;
	}

	public String getTakenBy() {
		return takenBy;
	}

	public void setTakenBy(final String takenBy) {
		this.takenBy = takenBy;
	}

	public Date getSampleDate() {
		return sampleDate;
	}

	public void setSampleDate(final Date sampleDate) {
		this.sampleDate = sampleDate;
	}

	public String getSampleType() {
		return sampleType;
	}

	public void setSampleType(final String sampleType) {
		this.sampleType = sampleType;
	}

	public String getTissueType() {
		return tissueType;
	}

	public void setTissueType(final String tissueType) {
		this.tissueType = tissueType;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(final String notes) {
		this.notes = notes;
	}

	public String getStudyName() {
		return studyName;
	}

	public void setStudyName(final String studyName) {
		this.studyName = studyName;
	}

	public String getSeason() {
		return season;
	}

	public void setSeason(final String season) {
		this.season = season;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(final String locationName) {
		this.locationName = locationName;
	}

	public Integer getEntryNo() {
		return entryNo;
	}

	public void setEntryNo(final Integer entryNo) {
		this.entryNo = entryNo;
	}

	public Integer getPlotNo() {
		return plotNo;
	}

	public void setPlotNo(final Integer plotNo) {
		this.plotNo = plotNo;
	}

	public Integer getGid() {
		return gid;
	}

	public void setGid(final Integer gid) {
		this.gid = gid;
	}

	public String getSeedingDate() {
		return seedingDate;
	}

	public void setSeedingDate(final String seedingDate) {
		this.seedingDate = seedingDate;
	}

	public String getHarvestDate() {
		return harvestDate;
	}

	public void setHarvestDate(final String harvestDate) {
		this.harvestDate = harvestDate;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof SampleDetailsDTO)) {
			return false;
		}
		final SampleDetailsDTO sampleDetailsDto = (SampleDetailsDTO) other;
		return new EqualsBuilder().append(this.studyDbId, sampleDetailsDto.studyDbId)
			.append(this.locationDbId, sampleDetailsDto.locationDbId).append(this.plotId, sampleDetailsDto.plotId)
			.append(this.plantBusinessKey, sampleDetailsDto.plantBusinessKey)
			.append(this.sampleBusinessKey, sampleDetailsDto.sampleBusinessKey).append(this.gid, sampleDetailsDto.gid).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.studyDbId).append(this.locationDbId).append(this.plotId).append(this.plantBusinessKey)
			.append(this.sampleBusinessKey).append(this.gid).hashCode();
	}

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}

	public void setSampleName(final String sampleName) {
		this.sampleName = sampleName;
	}

	public void setDesignation(final String designation) {
		this.designation = designation;
	}

	public void setPlantNo(final Integer plantNo) {
		this.plantNo = plantNo;
	}

	public String getSampleName() {
		return sampleName;
	}

	public String getDesignation() {
		return designation;
	}

	public Integer getPlantNo() {
		return plantNo;
	}

	public void setDisplayDate(final String displayDate) {
		this.displayDate = displayDate;
	}

	public String getDisplayDate() {
		return displayDate;
	}
}
