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
import javax.persistence.UniqueConstraint;

/**
 * http://gmod.org/wiki/Chado_Natural_Diversity_Module#Table:_nd_experiment_phenotype
 * 
 * Linking table: experiments to the phenotypes they produce. in most cases this will either 
 * be a single record, or an alternative (quantitative / qualitative?) description of the same 
 * phenotype (e.g. 1: "wing length: 12mm" / "wing length: increased"). 
 * 
 * In rare cases it may suit the user to link a single qualitative phenotype to multiple experiments
 * 
 * @author Darla Ani
 *
 */
@Entity
@Table( name = "nd_experiment_phenotype",
		uniqueConstraints = {
		@UniqueConstraint(columnNames = { "nd_experiment_id", "phenotype_id" }) })
public class ExperimentPhenotype implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Basic(optional = false)
	@Column(name = "nd_experiment_phenotype_id")
	private Long experimentPhenotypeId;

	/**
	 * The related Experiment entity
	 */
	@OneToOne
    @JoinColumn(name = "nd_experiment_id")
	private Experiment experiment;

	/**
	 * The related Phenotype entity
	 */
	
	@OneToOne
	@JoinColumn(name = "phenotype_id")
	private Phenotype phenotype;
	
	
	public ExperimentPhenotype(){
		
	}
	
	public ExperimentPhenotype(Long id){
		this.experimentPhenotypeId = id;
	}

	public Long getExperimentPhenotypeId() {
		return experimentPhenotypeId;
	}

	public void setExperimentPhenotypeId(Long id) {
		this.experimentPhenotypeId = id;
	}

	public Experiment getExperiment() {
		return experiment;
	}

	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}

	public Phenotype getPhenotype() {
		return phenotype;
	}

	public void setPhenotype(Phenotype phenotype) {
		this.phenotype = phenotype;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((experimentPhenotypeId == null) ? 0 : experimentPhenotypeId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ExperimentPhenotype))
			return false;
		
		ExperimentPhenotype other = (ExperimentPhenotype) obj;
		if (experimentPhenotypeId == null) {
			if (other.experimentPhenotypeId != null)
				return false;
		} else if (!experimentPhenotypeId.equals(other.experimentPhenotypeId))
			return false;
		
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ExperimentPhenotype [experimentPhenotypeId=");
		builder.append(experimentPhenotypeId);
		builder.append(", experiment=");
		builder.append(experiment);
		builder.append(", phenotype=");
		builder.append(phenotype);
		builder.append("]");
		return builder.toString();
	}
	
}
