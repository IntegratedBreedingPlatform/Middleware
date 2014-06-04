/*******************************************************************************
 * Copyright (c) 2014, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
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
public class MtaMetadata implements Serializable{

    private static final long serialVersionUID = 1L;   
    
    @Id
    @Column(name = "mta_id")
    private Integer mtaId;
    
	@Column(name = "project")
	private String project;
	
	@Column(name = "population")
	private String population;
	
	@Column(name = "population_size")
	private Integer populationSize;
	
	@Column(name = "population_units")
	private String populationUnits;

	public MtaMetadata(){
	}
	
	public MtaMetadata(Integer mtaId, String project, String population,
			Integer populationSize, String populationUnits) {
		this.mtaId = mtaId;
		this.project = project;
		this.population = population;
		this.populationSize = populationSize;
		this.populationUnits = populationUnits;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mtaId == null) ? 0 : mtaId.hashCode());
		result = prime * result
				+ ((population == null) ? 0 : population.hashCode());
		result = prime * result
				+ ((populationSize == null) ? 0 : populationSize.hashCode());
		result = prime * result
				+ ((populationUnits == null) ? 0 : populationUnits.hashCode());
		result = prime * result + ((project == null) ? 0 : project.hashCode());
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
		MtaMetadata other = (MtaMetadata) obj;
		if (mtaId == null) {
			if (other.mtaId != null)
				return false;
		} else if (!mtaId.equals(other.mtaId))
			return false;
		if (population == null) {
			if (other.population != null)
				return false;
		} else if (!population.equals(other.population))
			return false;
		if (populationSize == null) {
			if (other.populationSize != null)
				return false;
		} else if (!populationSize.equals(other.populationSize))
			return false;
		if (populationUnits == null) {
			if (other.populationUnits != null)
				return false;
		} else if (!populationUnits.equals(other.populationUnits))
			return false;
		if (project == null) {
			if (other.project != null)
				return false;
		} else if (!project.equals(other.project))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MtaMetadata [mtaId=");
		builder.append(mtaId);
		builder.append(", project=");
		builder.append(project);
		builder.append(", population=");
		builder.append(population);
		builder.append(", populationSize=");
		builder.append(populationSize);
		builder.append(", populationUnits=");
		builder.append(populationUnits);
		builder.append("]");
		return builder.toString();
	}
	
}
