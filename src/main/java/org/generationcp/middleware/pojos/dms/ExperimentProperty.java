/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.pojos.dms;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;

/**
 * http://gmod.org/wiki/Chado_Natural_Diversity_Module#Table:_nd_experimentprop
 *
 * Tag-value properties - follows standard chado model.
 *
 * @author Aldrin Batac
 *
 */
@Entity
@Table(name = "nd_experimentprop", uniqueConstraints = {@UniqueConstraint(columnNames = {"nd_experiment_id", "type_id", "rank"})})
public class ExperimentProperty implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "nd_experimentprop_id")
	private Integer ndExperimentpropId;

	@Basic(optional = false)
	@Column(name = "type_id")
	private Integer typeId;

	@Column(name = "value", columnDefinition = "default NULL")
	private String value;

	@Basic(optional = false)
	@Column(name = "rank", columnDefinition = "default 0")
	private Integer rank;

	@ManyToOne
	@JoinColumn(name = "nd_experiment_id", nullable = false)
	private ExperimentModel experiment;

	public ExperimentProperty() {
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ExperimentProperties [nd_experimentprop_id=" + this.ndExperimentpropId);
		sb.append(", type_id=" + this.typeId);
		sb.append(", value=" + this.value);
		sb.append(", rank=" + this.rank);
		sb.append("]");

		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.ndExperimentpropId == null ? 0 : this.ndExperimentpropId.hashCode());

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ExperimentProperty)) {
			return false;
		}

		ExperimentProperty other = (ExperimentProperty) obj;
		if (this.ndExperimentpropId == null) {
			if (other.ndExperimentpropId != null) {
				return false;
			}
		} else if (!this.ndExperimentpropId.equals(other.ndExperimentpropId)) {
			return false;
		}

		return true;
	}

	public Integer getNdExperimentpropId() {
		return this.ndExperimentpropId;
	}

	public void setNdExperimentpropId(Integer ndExperimentpropId) {
		this.ndExperimentpropId = ndExperimentpropId;
	}

	public Integer getTypeId() {
		return this.typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getRank() {
		return this.rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public ExperimentModel getExperiment() {
		return this.experiment;
	}

	public void setExperiment(ExperimentModel experiment) {
		this.experiment = experiment;
	}

}
