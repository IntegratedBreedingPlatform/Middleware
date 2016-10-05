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

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * http://gmod.org/wiki/Chado_Natural_Diversity_Module#Table:_nd_experiment_project
 *
 * Used to group together related nd_experiment records. All nd_experiments should be linked to at least one project.
 *
 * @author Darla Ani
 *
 */
@Entity
@Table(name = "nd_experiment_project")
public class ExperimentProject implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "nd_experiment_project_id")
	private Integer experimentProjectId;

	/**
	 * Related Experiment entity
	 */
	@Basic(optional = false)
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "nd_experiment_id")
	@Fetch(FetchMode.SELECT)
	// Batching for this relationship is specified on the entity ExperimentModel, because this is a OneToOne relationship. 
	// This is unlike batching for OneToMany which is specified on the field.
	private ExperimentModel experiment;

	/**
	 * Related Project entity
	 */
	@Basic(optional = false)
	@Column(name = "project_id")
	private Integer projectId;

	public ExperimentProject() {

	}

	public ExperimentProject(Integer id) {
		this.experimentProjectId = id;
	}

	public Integer getExperimentProjectId() {
		return this.experimentProjectId;
	}

	public void setExperimentProjectId(Integer experimentProjectId) {
		this.experimentProjectId = experimentProjectId;
	}

	public ExperimentModel getExperiment() {
		return this.experiment;
	}

	public void setExperiment(ExperimentModel experiment) {
		this.experiment = experiment;
	}

	public Integer getProjectId() {
		return this.projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.experimentProjectId == null ? 0 : this.experimentProjectId.hashCode());
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
		if (!(obj instanceof ExperimentProject)) {
			return false;
		}

		ExperimentProject other = (ExperimentProject) obj;
		if (this.experimentProjectId == null) {
			if (other.experimentProjectId != null) {
				return false;
			}
		} else if (!this.experimentProjectId.equals(other.experimentProjectId)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ExperimentProject [experimentProjectId=");
		builder.append(this.experimentProjectId);
		builder.append(", experiment=");
		builder.append(this.experiment);
		builder.append(", projectId=");
		builder.append(this.projectId);
		builder.append("]");
		return builder.toString();
	}

}
