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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * http://gmod.org/wiki/Chado_Tables#Table:_cv
 * 
 * A controlled vocabulary or ontology. A cv is composed of cvterms 
 * (AKA terms, classes, types, universals - relations and properties 
 * are also stored in cvterm) and the relationships between them.
 * 
 * @author Darla Ani
 *
 */
@Entity
@Table(name = "cv", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "name" }) })
public class CV implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Basic(optional = false)
	@Column(name = "cv_id")	
	private Long cvId;
	
	/**
	 * The name of the ontology. 
	 * In OBO file format, the cv.name is known as the namespace.
	 */
	@Basic(optional = false)
	@Column(name = "name", unique = true)
	private String name;
	
	/**
	 * A text description of the criteria for membership of this ontology.
	 */
	@Column(name = "definition")
	private String definition;
	
	public CV(){
		
	}
	
	public CV(Long id){
		this.cvId = id;
	}

	public Long getCvId() {
		return cvId;
	}

	public void setCvId(Long id) {
		this.cvId = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}
	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CV [cvId=");
		builder.append(cvId);
		builder.append(", name=");
		builder.append(name);
		builder.append(", definition=");
		builder.append(definition);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cvId == null) ? 0 : cvId.hashCode());
		
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof CV))
			return false;
		
		CV other = (CV) obj;
		if (cvId == null) {
			if (other.cvId != null)
				return false;
		} else if (!cvId.equals(other.cvId))
			return false;
		
		return true;
	}

	
}
