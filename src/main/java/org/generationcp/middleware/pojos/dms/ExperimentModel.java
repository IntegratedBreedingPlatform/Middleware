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
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.JoinColumn;

/**
 * 
 * http://gmod.org/wiki/Chado_Natural_Diversity_Module#Table:_nd_experiment
 * 
 * This is the core table for the natural diversity module, representing each individual assay that is undertaken 
 * (nb this is usually *not* an entire experiment). Each nd_experiment should give rise to a single genotype or 
 * phenotype and be described via 1 (or more) protocols. Collections of assays that relate to each other should be 
 * linked to the same record in the project table.
 * 
 * Experiment.type is a cvterm that will define which records are expected for other tables. Any CV may be used but 
 * it was designed with terms such as: [phenotype_assay, genotype_assay, field_collection, cross_experiment] in mind.
 * 
 * @author Joyce Avestro
 *
 */
@Entity
@Table(	name = "nd_experiment")
public class ExperimentModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@Column(name = "nd_experiment_id")
	private Integer ndExperimentId;

	// Geolocation
	@OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nd_geolocation_id")
	private Geolocation geoLocation;

    // References cvterm
    @Column(name="type_id")
    private Integer typeId;
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name="nd_experiment_id") 
	private List<ExperimentProperty> properties;
    
    @ManyToOne
    @JoinTable(name="nd_experiment_project", 
        joinColumns={@JoinColumn(name="nd_experiment_id", insertable=false,updatable=false)},
        inverseJoinColumns={@JoinColumn(name="project_id", insertable=false,updatable=false)})
    private DmsProject project;
    
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="nd_experiment_id")
    private List<ExperimentStock> experimentStocks;
    
    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name="nd_experiment_phenotype",
            joinColumns = @JoinColumn( name="nd_experiment_id"),
            inverseJoinColumns = @JoinColumn( name="phenotype_id")
    )
    private List<Phenotype> phenotypes;
    

	public ExperimentModel() {
	}

	public ExperimentModel(Integer ndExperimentId, Geolocation geoLocation, Integer typeId) {
		super();
		this.ndExperimentId = ndExperimentId;
		this.geoLocation = geoLocation;
		this.typeId = typeId;
	}

	public Integer getNdExperimentId() {
		return ndExperimentId;
	}

	public void setNdExperimentId(Integer ndExperimentId) {
		this.ndExperimentId = ndExperimentId;
	}

	public Geolocation getGeoLocation() {
		return geoLocation;
	}

	public void setGeoLocation(Geolocation geoLocation) {
		this.geoLocation = geoLocation;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public List<ExperimentProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<ExperimentProperty> properties) {
		this.properties = properties;
	}

	public DmsProject getProject() {
		return project;
	}

	public void setProject(DmsProject project) {
		this.project = project;
	}

	public List<ExperimentStock> getExperimentStocks() {
		return experimentStocks;
	}

	public void setExperimentStocks(List<ExperimentStock> experimentStocks) {
		this.experimentStocks = experimentStocks;
	}

	public List<Phenotype> getPhenotypes() {
		return phenotypes;
	}

	public void setPhenotypes(List<Phenotype> phenotypes) {
		this.phenotypes = phenotypes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((geoLocation == null) ? 0 : geoLocation.hashCode());
		result = prime * result
				+ ((ndExperimentId == null) ? 0 : ndExperimentId.hashCode());
		result = prime * result + ((typeId == null) ? 0 : typeId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExperimentModel other = (ExperimentModel) obj;
		if (geoLocation == null) {
			if (other.geoLocation != null)
				return false;
		} else if (!geoLocation.equals(other.geoLocation))
			return false;
		if (ndExperimentId == null) {
			if (other.ndExperimentId != null)
				return false;
		} else if (!ndExperimentId.equals(other.ndExperimentId))
			return false;
		if (typeId == null) {
			if (other.typeId != null)
				return false;
		} else if (!typeId.equals(other.typeId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Experiment [ndExperimentId=");
		builder.append(ndExperimentId);
		builder.append(", geoLocationId=");
		builder.append(geoLocation);
		builder.append(", typeId=");
		builder.append(typeId);
		builder.append("]");
		return builder.toString();
	}
    


}