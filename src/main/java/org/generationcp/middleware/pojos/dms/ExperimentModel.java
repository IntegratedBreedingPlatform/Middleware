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

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import java.io.Serializable;
import java.util.List;
/**
 *
 * http://gmod.org/wiki/Chado_Natural_Diversity_Module#Table:_nd_experiment
 *
 * This is the core table for the natural diversity module, representing each individual assay that is undertaken (nb this is usually *not*
 * an entire experiment). Each nd_experiment should give rise to a single genotype or phenotype and be described via 1 (or more) protocols.
 * Collections of assays that relate to each other should be linked to the same record in the project table.
 *
 * Experiment.type is a cvterm that will define which records are expected for other tables. Any CV may be used but it was designed with
 * terms such as: [phenotype_assay, genotype_assay, field_collection, cross_experiment] in mind.
 *
 * @author Joyce Avestro
 *
 */
@Entity
@Table(name = "nd_experiment")
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE, region="nd_experiment")
//OneToOne relationship to this entity from ExperimentProject requires batching annotation to be on entity unlike OneToMany which can be on the field.
@BatchSize(size = 5000)
public class ExperimentModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@TableGenerator(name = "ndExperimentIdGenerator", table = "sequence", pkColumnName = "sequence_name", valueColumnName = "sequence_value",
	pkColumnValue = "nd_experiment", allocationSize = 500)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "ndExperimentIdGenerator")
	@Basic(optional = false)
	@Column(name = "nd_experiment_id")
	private Integer ndExperimentId;

	// References cvterm
	@Column(name = "type_id")
	private Integer typeId;

	//OBS_UNIT_ID
	@Basic(optional = true)
	@Column(name = "obs_unit_id")
	private String obsUnitId;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "experiment")
	@BatchSize(size = 5000)
	private List<ExperimentProperty> properties;

	// TODO
	//  - Migrate nd_experimentprop
	//  - use @Convert and Map<String, Object> in jpa 2.1 (hibernate > 4.3)
	@Column(name = "json_props")
	private String jsonProps;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id")
	private DmsProject project;

	@ManyToOne(targetEntity = StockModel.class)
	@JoinColumn(name = "stock_id", nullable = true)
	private StockModel stock;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "experiment")
	@BatchSize(size = 5000)
	private List<Phenotype> phenotypes;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private ExperimentModel parent;

	@Column(name = "observation_unit_no")
	private Integer observationUnitNo;

	public ExperimentModel() {
	}

	public ExperimentModel(final Integer ndExperimentId) {
		super();
		this.ndExperimentId = ndExperimentId;
	}

	public ExperimentModel(final Integer ndExperimentId, final Integer typeId) {
		super();
		this.ndExperimentId = ndExperimentId;
		this.typeId = typeId;
	}

	public ExperimentModel(final Integer typeId, final DmsProject project, final StockModel stock,
			final ExperimentModel parent, final Integer observationUnitNo) {
		this.typeId = typeId;
		this.project = project;
		this.stock = stock;
		this.parent = parent;
		this.observationUnitNo = observationUnitNo;
	}

	public Integer getNdExperimentId() {
		return this.ndExperimentId;
	}

	public void setNdExperimentId(final Integer ndExperimentId) {
		this.ndExperimentId = ndExperimentId;
	}

	public Integer getTypeId() {
		return this.typeId;
	}

	public void setTypeId(final Integer typeId) {
		this.typeId = typeId;
	}

	public List<ExperimentProperty> getProperties() {
		return this.properties;
	}

	public String getJsonProps() {
		return this.jsonProps;
	}

	public void setJsonProps(final String props) {
		this.jsonProps = props;
	}

	public void setProperties(final List<ExperimentProperty> properties) {
		this.properties = properties;
	}

	public DmsProject getProject() {
		return this.project;
	}

	public void setProject(final DmsProject project) {
		this.project = project;
	}

	public StockModel getStock() {
		return this.stock;
	}

	public void setStock(final StockModel stock) {
		this.stock = stock;
	}

	public List<Phenotype> getPhenotypes() {
		return this.phenotypes;
	}

	public void setPhenotypes(final List<Phenotype> phenotypes) {
		this.phenotypes = phenotypes;
	}

	public void setObsUnitId(final String obsUnitId) {
		this.obsUnitId = obsUnitId;
	}

	public String getObsUnitId() {
		return this.obsUnitId;
	}

	public ExperimentModel getParent() {
		return this.parent;
	}

	public void setParent(final ExperimentModel parent) {
		this.parent = parent;
	}

	public Integer getObservationUnitNo() {
		return this.observationUnitNo;
	}

	public void setObservationUnitNo(final Integer observationUnitNo) {
		this.observationUnitNo = observationUnitNo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.ndExperimentId == null ? 0 : this.ndExperimentId.hashCode());
		result = prime * result + (this.typeId == null ? 0 : this.typeId.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final ExperimentModel other = (ExperimentModel) obj;
		if (this.ndExperimentId == null) {
			if (other.ndExperimentId != null) {
				return false;
			}
		} else if (!this.ndExperimentId.equals(other.ndExperimentId)) {
			return false;
		}
		if (this.typeId == null) {
			if (other.typeId != null) {
				return false;
			}
		} else if (!this.typeId.equals(other.typeId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ExperimentModel{" +
			"ndExperimentId=" + this.ndExperimentId +
			", typeId=" + this.typeId +
			", obsUnitId='" + this.obsUnitId + '\'' +
			", properties=" + this.properties +
			", project=" + this.project +
			", stock=" + this.stock +
			", phenotypes=" + this.phenotypes +
			", parent=" + this.parent +
			", observationUnitNo=" + this.observationUnitNo +
			'}';
	}
}
