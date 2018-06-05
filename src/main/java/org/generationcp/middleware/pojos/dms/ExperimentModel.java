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
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
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

	// Geolocation
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "nd_geolocation_id")

	private Geolocation geoLocation;

	// References cvterm
	@Column(name = "type_id")
	private Integer typeId;

	//plot_id
	@Basic(optional = true)
	@Column(name = "plot_id")
	private String plotId;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "experiment")
	@BatchSize(size = 5000)
	private List<ExperimentProperty> properties;

	@ManyToOne
	@JoinColumn(name = "project_id")
	private DmsProject project;

	//FIXME Should this not be a OneToOne? Can one experiment have multiple stock (germplasm) rows?
	//Collection always contains one item currently.
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "experiment")
	@BatchSize(size = 5000)
	private List<ExperimentStock> experimentStocks;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "nd_experiment_phenotype", joinColumns = @JoinColumn(name = "nd_experiment_id"), inverseJoinColumns = @JoinColumn(
			name = "phenotype_id"))
	@BatchSize(size = 5000)
	private List<Phenotype> phenotypes;

	public ExperimentModel() {
	}

	public ExperimentModel(Integer ndExperimentId) {
		super();
		this.ndExperimentId = ndExperimentId;
	}

	public ExperimentModel(Integer ndExperimentId, Geolocation geoLocation, Integer typeId) {
		super();
		this.ndExperimentId = ndExperimentId;
		this.geoLocation = geoLocation;
		this.typeId = typeId;
	}

	public Integer getNdExperimentId() {
		return this.ndExperimentId;
	}

	public void setNdExperimentId(Integer ndExperimentId) {
		this.ndExperimentId = ndExperimentId;
	}

	public Geolocation getGeoLocation() {
		return this.geoLocation;
	}

	public void setGeoLocation(Geolocation geoLocation) {
		this.geoLocation = geoLocation;
	}

	public Integer getTypeId() {
		return this.typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public List<ExperimentProperty> getProperties() {
		return this.properties;
	}

	public void setProperties(List<ExperimentProperty> properties) {
		this.properties = properties;
	}

	public DmsProject getProject() {
		return this.project;
	}

	public void setProject(DmsProject project) {
		this.project = project;
	}

	public List<ExperimentStock> getExperimentStocks() {
		return this.experimentStocks;
	}

	public void setExperimentStocks(List<ExperimentStock> experimentStocks) {
		this.experimentStocks = experimentStocks;
	}

	public List<Phenotype> getPhenotypes() {
		return this.phenotypes;
	}

	public void setPhenotypes(List<Phenotype> phenotypes) {
		this.phenotypes = phenotypes;
	}

	public void setPlotId(String plotId) {
		this.plotId = plotId;
	}

	public String getPlotId() {
		return plotId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.geoLocation == null ? 0 : this.geoLocation.hashCode());
		result = prime * result + (this.ndExperimentId == null ? 0 : this.ndExperimentId.hashCode());
		result = prime * result + (this.typeId == null ? 0 : this.typeId.hashCode());
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
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		ExperimentModel other = (ExperimentModel) obj;
		if (this.geoLocation == null) {
			if (other.geoLocation != null) {
				return false;
			}
		} else if (!this.geoLocation.equals(other.geoLocation)) {
			return false;
		}
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
		StringBuilder builder = new StringBuilder();
		builder.append("Experiment [ndExperimentId=");
		builder.append(this.ndExperimentId);
		builder.append(", geoLocationId=");
		builder.append(this.geoLocation);
		builder.append(", typeId=");
		builder.append(this.typeId);
		builder.append(", plotId=");
		builder.append(this.plotId);
		builder.append("]");
		return builder.toString();
	}
	
}
