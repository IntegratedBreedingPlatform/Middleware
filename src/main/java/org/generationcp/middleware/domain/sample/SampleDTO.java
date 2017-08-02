package org.generationcp.middleware.domain.sample;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.generationcp.middleware.domain.plant.PlantDTO;
import org.generationcp.middleware.domain.samplelist.SampleListDTO;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.service.api.user.UserDto;

import java.io.Serializable;
import java.util.Date;

public class SampleDTO implements Serializable {

	private Integer sampleId;

	private String sampleName;

	private UserDto takenBy;

	private Date samplingDate;

	private Date createdDate;

	private String sampleBusinessKey;

	private PlantDTO plant;

	private SampleListDTO sampleList;

	public SampleDTO() {

	}

	public SampleListDTO getSampleList() {
	  return sampleList;
	}

	public void setSampleList(SampleListDTO sampleList) {
	  this.sampleList = sampleList;
	}

    public Integer getSampleId() {
		return sampleId;
	}

	public void setSampleId(Integer sampleId) {
		this.sampleId = sampleId;
	}

	public String getSampleName() {
		return sampleName;
	}

	public void setSampleName(String sampleName) {
		this.sampleName = sampleName;
	}

	public UserDto getTakenBy() {
		return takenBy;
	}

	public void setTakenBy(UserDto takenBy) {
		this.takenBy = takenBy;
	}

	public Date getSamplingDate() {
		return samplingDate;
	}

	public void setSamplingDate(Date samplingDate) {
		this.samplingDate = samplingDate;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getSampleBusinessKey() {
		return sampleBusinessKey;
	}

	public void setSampleBusinessKey(String sampleBusinessKey) {
		this.sampleBusinessKey = sampleBusinessKey;
	}

	public PlantDTO getPlant() {
		return plant;
	}

	public void setPlant(PlantDTO plant) {
		this.plant = plant;
	}

	@Override public boolean equals(final Object other) {
		if (!(other instanceof SampleDTO)) {
			return false;
		}
		final SampleDTO castOther = (SampleDTO) other;
		return new EqualsBuilder().append(this.sampleId, castOther.sampleId).isEquals();
	}

	@Override public int hashCode() {

		return new HashCodeBuilder().append(this.sampleId).hashCode();
	}

	@Override public String toString() {

		return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}
}
