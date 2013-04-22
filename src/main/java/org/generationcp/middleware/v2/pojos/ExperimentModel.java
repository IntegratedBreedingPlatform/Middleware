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

package org.generationcp.middleware.v2.pojos;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
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

	// References Geolocation
	@Column(name = "nd_geolocation_id")
	private Integer geoLocationId;

    // References cvterm
    @Column(name="type_id")
    private Integer typeId;
    
    @ManyToOne
    @JoinTable(name="nd_experiment_project", 
        joinColumns={@JoinColumn(name="nd_experiment_id", insertable=false,updatable=false)},
        inverseJoinColumns={@JoinColumn(name="project_id", insertable=false,updatable=false)})
    private DmsProject project;

	public ExperimentModel() {
	}

	public ExperimentModel(Integer ndExperimentId, Integer geoLocationId, Integer typeId) {
		super();
		this.ndExperimentId = ndExperimentId;
		this.geoLocationId = geoLocationId;
		this.typeId = typeId;
	}

	public Integer getNdExperimentId() {
		return ndExperimentId;
	}

	public void setNdExperimentId(Integer ndExperimentId) {
		this.ndExperimentId = ndExperimentId;
	}

	public Integer getGeoLocationId() {
		return geoLocationId;
	}

	public void setGeoLocationId(Integer geoLocationId) {
		this.geoLocationId = geoLocationId;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
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
		result = prime * result
				+ ((geoLocationId == null) ? 0 : geoLocationId.hashCode());
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
		if (geoLocationId == null) {
			if (other.geoLocationId != null)
				return false;
		} else if (!geoLocationId.equals(other.geoLocationId))
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
		builder.append(geoLocationId);
		builder.append(", typeId=");
		builder.append(typeId);
		builder.append("]");
		return builder.toString();
	}
    


}