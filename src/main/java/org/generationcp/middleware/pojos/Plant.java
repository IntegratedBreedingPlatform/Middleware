
package org.generationcp.middleware.pojos;

import java.io.Serializable;
import java.util.Date;

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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "plant")
public class Plant implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 8106804623760841599L;

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

	@Basic(optional = false)
	@Column(name = "plate_Id")
	private String plateId;

	@Basic(optional = false)
	@Column(name = "well")
	private String well;

	public Plant() {

	}

	public Plant(final Integer plantNumber, final ExperimentModel experiment, final Date createdDate, final String plantBusinessKey) {
		this.plantNumber = plantNumber;
		this.experiment = experiment;
		this.createdDate = createdDate;
		this.plantBusinessKey = plantBusinessKey;
	}

	public Integer getPlantId() {
		return this.plantId;
	}

	public void setPlantId(final Integer plantId) {
		this.plantId = plantId;
	}

	public Integer getPlantNumber() {
		return this.plantNumber;
	}

	public void setPlantNumber(final Integer plantNumber) {
		this.plantNumber = plantNumber;
	}

	public ExperimentModel getExperiment() {
		return this.experiment;
	}

	public void setExperiment(final ExperimentModel experiment) {
		this.experiment = experiment;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(final Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getPlantBusinessKey() {
		return this.plantBusinessKey;
	}

	public void setPlantBusinessKey(final String plantBusinessKey) {
		this.plantBusinessKey = plantBusinessKey;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof Plant)) {
			return false;
		}
		final Plant castOther = (Plant) other;
		return new EqualsBuilder().append(this.plantId, castOther.plantId).isEquals();
	}

	@Override
	public int hashCode() {

		return new HashCodeBuilder().append(this.plantId).hashCode();
	}

	@Override
	public String toString() {

		return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}

	public String getPlateId() {
		return plateId;
	}

	public void setPlateId(String plateId) {
		this.plateId = plateId;
	}

	public String getWell() {
		return well;
	}

	public void setWell(String well) {
		this.well = well;
	}
}
