package org.generationcp.middleware.domain.sample;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.util.Util;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SampleDetailsDTO implements Serializable {

	private static final long serialVersionUID = -4175016670661637734L;
	private final SimpleDateFormat dateFormat = Util.getSimpleDateFormat(Util.FRONTEND_DATE_FORMAT_3);

	private Integer studyDbId;
	private Integer locationDbId;
	private String obsUnitId;
	private String sampleBusinessKey;
	private String takenBy;
	private Integer takenByUserId;
	private Date sampleDate;
	private String sampleType;
	private String tissueType;
	private String notes;
	private String studyName;
	private String season;
	private String locationName;
	// TODO not in brapi? use another name or remove, to avoid confusion with entryNumber
	private Integer entryNo;
	private Integer plotNo;
	private Integer gid;
	private String germplasmUUID;
	private String seedingDate;
	private String harvestDate;
	private String sampleName;
	private String designation;
	private String displayDate;
	private Integer entryNumber;
	private Integer sampleEntryNo;
	private String plateId;
	private String well;
	private Integer observationUnitNumber;
	private Integer sampleNumber;

	public SampleDetailsDTO() {

	}

	public SampleDetailsDTO(final Integer studyDbId, final String obsUnitId, final String sampleBusinessKey) {
		this.setStudyDbId(studyDbId);
		this.setObsUnitId(obsUnitId);
		this.setSampleBusinessKey(sampleBusinessKey);
	}

	public Integer getEntryNumber() {
		return this.entryNumber;
	}

	public void setEntryNumber(final Integer entryNumber) {
		this.entryNumber = entryNumber;
	}

	public Integer getStudyDbId() {
		return this.studyDbId;
	}

	public void setStudyDbId(final Integer studyDbId) {
		this.studyDbId = studyDbId;
	}

	public Integer getLocationDbId() {
		return this.locationDbId;
	}

	public void setLocationDbId(final Integer locationDbId) {
		this.locationDbId = locationDbId;
	}

	public String getObsUnitId() {
		return this.obsUnitId;
	}

	public void setObsUnitId(final String obsUnitId) {
		this.obsUnitId = obsUnitId;
	}

	public String getSampleBusinessKey() {
		return this.sampleBusinessKey;
	}

	public void setSampleBusinessKey(final String sampleBusinessKey) {
		this.sampleBusinessKey = sampleBusinessKey;
	}

	public String getTakenBy() {
		return this.takenBy;
	}

	public void setTakenBy(final String takenBy) {
		this.takenBy = takenBy != null ? takenBy : "-";
	}

	public Date getSampleDate() {
		return this.sampleDate;
	}

	public void setSampleDate(final Date sampleDate) {
		this.sampleDate = sampleDate;
		this.displayDate = sampleDate != null ? this.dateFormat.format(sampleDate) : "-";
	}

	public String getSampleType() {
		return this.sampleType;
	}

	public void setSampleType(final String sampleType) {
		this.sampleType = sampleType;
	}

	public String getTissueType() {
		return this.tissueType;
	}

	public void setTissueType(final String tissueType) {
		this.tissueType = tissueType;
	}

	public String getNotes() {
		return this.notes;
	}

	public void setNotes(final String notes) {
		this.notes = notes;
	}

	public String getStudyName() {
		return this.studyName;
	}

	public void setStudyName(final String studyName) {
		this.studyName = studyName;
	}

	public String getSeason() {
		return this.season;
	}

	public void setSeason(final String season) {
		this.season = season;
	}

	public String getLocationName() {
		return this.locationName;
	}

	public void setLocationName(final String locationName) {
		this.locationName = locationName;
	}

	// TODO not in brapi? use another name or remove, to avoid confusion with entryNumber
	public Integer getEntryNo() {
		return this.entryNo;
	}

	public void setEntryNo(final Integer entryNo) {
		this.entryNo = entryNo;
	}

	public Integer getPlotNo() {
		return this.plotNo;
	}

	public void setPlotNo(final Object plotNo) {
		if (plotNo instanceof String) {
			this.plotNo = Integer.valueOf((String) plotNo);
		}
	}

	public Integer getGid() {
		return this.gid;
	}

	public void setGid(final Integer gid) {
		this.gid = gid;
	}

	public String getSeedingDate() {
		return this.seedingDate;
	}

	public void setSeedingDate(final String seedingDate) {
		this.seedingDate = seedingDate;
	}

	public String getHarvestDate() {
		return this.harvestDate;
	}

	public void setHarvestDate(final String harvestDate) {
		this.harvestDate = harvestDate;
	}

	public Integer getObservationUnitNumber() {
		return this.observationUnitNumber;
	}

	// TODO move PLOT_NO to nd_exp observation_unit_no
	//  for now we accept string values from nd_exp_prop
	public void setObservationUnitNumber(final Object observationUnitNumber) {
		if (observationUnitNumber instanceof Number) {
			this.observationUnitNumber = (Integer) observationUnitNumber;
		} else if (observationUnitNumber instanceof String) {
			this.observationUnitNumber = Integer.valueOf((String) observationUnitNumber);
		} else {
			throw new MiddlewareException("Invalid observationUnitNumber: " + observationUnitNumber);
		}
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof SampleDetailsDTO)) {
			return false;
		}
		final SampleDetailsDTO sampleDetailsDto = (SampleDetailsDTO) other;
		return new EqualsBuilder().append(this.studyDbId, sampleDetailsDto.studyDbId)
			.append(this.locationDbId, sampleDetailsDto.locationDbId).append(this.obsUnitId, sampleDetailsDto.obsUnitId)
			.append(this.sampleBusinessKey, sampleDetailsDto.sampleBusinessKey).append(this.gid, sampleDetailsDto.gid).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.studyDbId).append(this.locationDbId).append(this.obsUnitId)
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

	public String getSampleName() {
		return this.sampleName;
	}

	public String getDesignation() {
		return this.designation;
	}

	public String getDisplayDate() {
		return this.displayDate;
	}

	public Integer getSampleEntryNo() {
		return this.sampleEntryNo;
	}

	public void setSampleEntryNo(final Integer sampleEntryNo) {
		this.sampleEntryNo = sampleEntryNo;
	}

	public SimpleDateFormat getDateFormat() {
		return this.dateFormat;
	}

	public String getPlateId() {
		return this.plateId;
	}

	public void setPlateId(final String plateId) {
		this.plateId = plateId;
	}

	public String getWell() {
		return this.well;
	}

	public void setWell(final String well) {
		this.well = well;
	}

	public Integer getSampleNumber() {
		return this.sampleNumber;
	}

	public void setSampleNumber(final Integer sampleNumber) {
		this.sampleNumber = sampleNumber;
	}

	public Integer getTakenByUserId() {
		return this.takenByUserId;
	}

	public void setTakenByUserId(final Integer takenByUserId) {
		this.takenByUserId = takenByUserId;
	}

	public String getGermplasmUUID() {
		return this.germplasmUUID;
	}

	public void setGermplasmUUID(final String germplasmUUID) {
		this.germplasmUUID = germplasmUUID;
	}
}
