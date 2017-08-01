package org.generationcp.middleware.domain.plant;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.generationcp.middleware.domain.dms.Experiment;

import java.io.Serializable;
import java.util.Date;

public class PlantDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer plantId;

	private Integer plantNumber;

	private Experiment experiment;

	private Date createdDate;

	private String plantBusinessKey;

	public PlantDTO() {

	}

	public PlantDTO(Integer plantNumber, Experiment experiment, Date createdDate, String plantBusinessKey) {
		this.plantNumber = plantNumber;
		this.experiment = experiment;
		this.createdDate = createdDate;
		this.plantBusinessKey = plantBusinessKey;
	}

	public Integer getPlantId() {
		return plantId;
	}

	public void setPlantId(Integer plantId) {
		this.plantId = plantId;
	}

	public Integer getPlantNumber() {
		return plantNumber;
	}

	public void setPlantNumber(Integer plantNumber) {
		this.plantNumber = plantNumber;
	}

	public Experiment getExperiment() {
		return experiment;
	}

	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getPlantBusinessKey() {
		return plantBusinessKey;
	}

	public void setPlantBusinessKey(String plantBusinessKey) {
		this.plantBusinessKey = plantBusinessKey;
	}

	@Override public boolean equals(final Object other) {
		if (!(other instanceof PlantDTO)) {
			return false;
		}
		final PlantDTO castOther = (PlantDTO) other;
		return new EqualsBuilder().append(this.plantId, castOther.plantId).isEquals();
	}

	@Override public int hashCode() {

		return new HashCodeBuilder().append(this.plantId).hashCode();
	}

	@Override public String toString() {

		return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}
}
