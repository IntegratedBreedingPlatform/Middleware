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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * http://gmod.org/wiki/Chado_Natural_Diversity_Module#Table:_nd_experiment_phenotype
 *
 * Linking table: experiments to the phenotypes they produce. in most cases this will either be a single record, or an alternative
 * (quantitative / qualitative?) description of the same phenotype (e.g. 1: "wing length: 12mm" / "wing length: increased").
 *
 * In rare cases it may suit the user to link a single qualitative phenotype to multiple experiments
 *
 * @author Darla Ani
 *
 */
@Entity
@Table(name = "nd_experiment_phenotype", uniqueConstraints = {@UniqueConstraint(columnNames = {"nd_experiment_id", "phenotype_id"})})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE, region="nd_experiment_phenotype")
public class ExperimentPhenotype implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@TableGenerator(name = "ndExperimentPhenotypeIdGenerator", table = "sequence", pkColumnName = "sequence_name", valueColumnName = "sequence_value",
	pkColumnValue = "nd_experiment_phenotype", allocationSize = 500)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "ndExperimentPhenotypeIdGenerator")
	@Basic(optional = false)
	@Column(name = "nd_experiment_phenotype_id")
	private Integer experimentPhenotypeId;

	/**
	 * The related Experiment entity
	 */
	@Basic(optional = false)
	@Column(name = "nd_experiment_id")
	private Integer experiment;

	/**
	 * The related Phenotype entity
	 */
	@Basic(optional = false)
	@Column(name = "phenotype_id")
	private Integer phenotype;

	public ExperimentPhenotype() {

	}

	public ExperimentPhenotype(Integer id) {
		this.experimentPhenotypeId = id;
	}

	public Integer getExperimentPhenotypeId() {
		return this.experimentPhenotypeId;
	}

	public void setExperimentPhenotypeId(Integer id) {
		this.experimentPhenotypeId = id;
	}

	public Integer getExperiment() {
		return this.experiment;
	}

	public void setExperiment(Integer experiment) {
		this.experiment = experiment;
	}

	public Integer getPhenotype() {
		return this.phenotype;
	}

	public void setPhenotype(Integer phenotype) {
		this.phenotype = phenotype;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.experimentPhenotypeId == null ? 0 : this.experimentPhenotypeId.hashCode());
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
		if (!(obj instanceof ExperimentPhenotype)) {
			return false;
		}

		ExperimentPhenotype other = (ExperimentPhenotype) obj;
		if (this.experimentPhenotypeId == null) {
			if (other.experimentPhenotypeId != null) {
				return false;
			}
		} else if (!this.experimentPhenotypeId.equals(other.experimentPhenotypeId)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ExperimentPhenotype [experimentPhenotypeId=");
		builder.append(this.experimentPhenotypeId);
		builder.append(", experiment=");
		builder.append(this.experiment);
		builder.append(", phenotype=");
		builder.append(this.phenotype);
		builder.append("]");
		return builder.toString();
	}

}
