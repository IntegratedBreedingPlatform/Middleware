/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.middleware.pojos.dms;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;


/**
 * http://gmod.org/wiki/Chado_Natural_Diversity_Module#Table:_nd_experiment_project
 * 
 * Used to group together related nd_experiment records. 
 * All nd_experiments should be linked to at least one project.
 * 
 * @author Darla Ani
 *
 */

@Entity
@Table(name = "nd_experiment_project")
public class ExperimentProject implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Basic(optional = false)
	@Column(name = "nd_experiment_project_id")
	private Long experimentProjectId;
	
	/**
	 * Related Experiment entity
	 */
	@OneToOne
	@JoinColumn(name = "nd_experiment_id")
	private Experiment experiment;
	
	/**
	 * Related Project entity
	 */
	@OneToOne
	@JoinColumn(name = "project_id")
	private DmsProject project;
	
	public ExperimentProject(){
		
	}
	
	public ExperimentProject(Long id){
		this.experimentProjectId = id;
	}

	public Long getExperimentProjectId() {
		return experimentProjectId;
	}

	public void setExperimentProjectId(Long experimentProjectId) {
		this.experimentProjectId = experimentProjectId;
	}

	public Experiment getExperiment() {
		return experiment;
	}

	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}

	public DmsProject getProject() {
		return project;
	}

	public void setProject(DmsProject project) {
		this.project = project;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((experimentProjectId == null) ? 0 : experimentProjectId
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ExperimentProject))
			return false;
		
		ExperimentProject other = (ExperimentProject) obj;
		if (experimentProjectId == null) {
			if (other.experimentProjectId != null)
				return false;
		} else if (!experimentProjectId.equals(other.experimentProjectId))
			return false;
		
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ExperimentProject [experimentProjectId=");
		builder.append(experimentProjectId);
		builder.append(", experiment=");
		builder.append(experiment);
		builder.append(", project=");
		builder.append(project);
		builder.append("]");
		return builder.toString();
	}
	
	
}
