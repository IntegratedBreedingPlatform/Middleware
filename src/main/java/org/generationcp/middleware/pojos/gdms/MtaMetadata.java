/*******************************************************************************
 * Copyright (c) 2014, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.pojos.gdms;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * POJO for the gdms_mta_metadata table.
 *
 * @author Joyce Avestro
 *
 */
@Entity
@Table(name = "gdms_mta_metadata")
public class MtaMetadata implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "dataset_id")
	private Integer datasetID;

	@Column(name = "project")
	private String project;

	@Column(name = "population")
	private String population;

	@Column(name = "population_size")
	private Integer populationSize;

	@Column(name = "population_units")
	private String populationUnits;

	public MtaMetadata() {
	}

	public MtaMetadata(Integer datasetID, String project, String population, Integer populationSize, String populationUnits) {
		this.datasetID = datasetID;
		this.project = project;
		this.population = population;
		this.populationSize = populationSize;
		this.populationUnits = populationUnits;
	}

	public Integer getDatasetID() {
		return this.datasetID;
	}

	public void setDatasetID(Integer datasetID) {
		this.datasetID = datasetID;
	}

	public String getProject() {
		return this.project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getPopulation() {
		return this.population;
	}

	public void setPopulation(String population) {
		this.population = population;
	}

	public Integer getPopulationSize() {
		return this.populationSize;
	}

	public void setPopulationSize(Integer populationSize) {
		this.populationSize = populationSize;
	}

	public String getPopulationUnits() {
		return this.populationUnits;
	}

	public void setPopulationUnits(String populationUnits) {
		this.populationUnits = populationUnits;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || this.getClass() != o.getClass()) {
			return false;
		}

		MtaMetadata that = (MtaMetadata) o;

		if (this.datasetID != null ? !this.datasetID.equals(that.datasetID) : that.datasetID != null) {
			return false;
		}
		if (this.population != null ? !this.population.equals(that.population) : that.population != null) {
			return false;
		}
		if (this.populationSize != null ? !this.populationSize.equals(that.populationSize) : that.populationSize != null) {
			return false;
		}
		if (this.populationUnits != null ? !this.populationUnits.equals(that.populationUnits) : that.populationUnits != null) {
			return false;
		}
		if (this.project != null ? !this.project.equals(that.project) : that.project != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = this.datasetID != null ? this.datasetID.hashCode() : 0;
		result = 31 * result + (this.project != null ? this.project.hashCode() : 0);
		result = 31 * result + (this.population != null ? this.population.hashCode() : 0);
		result = 31 * result + (this.populationSize != null ? this.populationSize.hashCode() : 0);
		result = 31 * result + (this.populationUnits != null ? this.populationUnits.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "MtaMetadata{" + "datasetID=" + this.datasetID + ", project='" + this.project + '\'' + ", population='" + this.population
				+ '\'' + ", populationSize=" + this.populationSize + ", populationUnits='" + this.populationUnits + '\'' + '}';
	}
}
