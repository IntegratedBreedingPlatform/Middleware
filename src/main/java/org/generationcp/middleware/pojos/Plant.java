package org.generationcp.middleware.pojos;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "plant")
public class Plant implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "plant_id")
	private Integer plantId;

	@Basic(optional = false)
	@Column(name = "plant_no")
	private Integer plantNumber;

	@ManyToOne(targetEntity = ExperimentModel.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "nd_experiment_id")
	@NotFound(action = NotFoundAction.IGNORE)
	@Basic(optional = false)
	private ExperimentModel experiment;

	@Basic(optional = false)
	@Column(name = "created_date")
	private Date createdDate;

	@Basic(optional = false)
	@Column(name = "plant_bk")
	private String plantBusinessKey;

	public Plant() {

	}

	public Plant(Integer plantNumber, ExperimentModel experiment, Date createdDate, String plantBusinessKey) {
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

	public ExperimentModel getExperiment() {
		return experiment;
	}

	public void setExperiment(ExperimentModel experiment) {
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
		if (!(other instanceof Plant)) {
			return false;
		}
		final Plant castOther = (Plant) other;
		return new EqualsBuilder().append(this.plantId, castOther.plantId).isEquals();
	}

	@Override public int hashCode() {

		return new HashCodeBuilder().append(this.plantId).hashCode();
	}

	@Override public String toString() {

		return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}
}
