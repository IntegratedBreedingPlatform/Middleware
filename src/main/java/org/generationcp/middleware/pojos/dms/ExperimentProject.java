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

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;


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
	@Basic(optional = false)
	@Column(name = "nd_experiment_project_id")
	private Integer experimentProjectId;
	
	/**
	 * Related Experiment entity
	 */
	@Basic(optional = false)
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "nd_experiment_id")
	@Fetch (FetchMode.SELECT)
	private ExperimentModel experiment;
	
	/**
	 * Related Project entity
	 */
	@Basic(optional = false)
	@Column(name = "project_id")
	private Integer projectId;
	
	public ExperimentProject(){
		
	}
	
	public ExperimentProject(Integer id){
		this.experimentProjectId = id;
	}

	public Integer getExperimentProjectId() {
		return experimentProjectId;
	}

	public void setExperimentProjectId(Integer experimentProjectId) {
		this.experimentProjectId = experimentProjectId;
	}

	public ExperimentModel getExperiment() {
		return experiment;
	}

	public void setExperiment(ExperimentModel experiment) {
		this.experiment = experiment;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
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
		if (experimentProjectId == null) {
			if (other.experimentProjectId != null) {
                return false;
            }
		} else if (!experimentProjectId.equals(other.experimentProjectId)) {
            return false;
        }
		
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ExperimentProject [experimentProjectId=");
		builder.append(experimentProjectId);
		builder.append(", experiment=");
		builder.append(experiment);
		builder.append(", projectId=");
		builder.append(projectId);
		builder.append("]");
		return builder.toString();
	}
	
	
}
