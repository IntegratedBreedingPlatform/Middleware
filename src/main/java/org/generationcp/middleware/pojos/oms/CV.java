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

package org.generationcp.middleware.pojos.oms;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * http://gmod.org/wiki/Chado_Tables#Table:_cv
 *
 * A controlled vocabulary or ontology. A cv is composed of cvterms (AKA terms, classes, types, universals - relations and properties are
 * also stored in cvterm) and the relationships between them.
 *
 * @author Darla Ani
 *
 */
@Entity
@Table(name = "cv", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
public class CV implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "cv_id")
	private Integer cvId;

	/**
	 * The name of the ontology. In OBO file format, the cv.name is known as the namespace.
	 */
	@Basic(optional = false)
	@Column(name = "name", unique = true)
	private String name;

	/**
	 * A text description of the criteria for membership of this ontology.
	 */
	@Column(name = "definition")
	private String definition;

	public CV() {

	}

	public CV(Integer id) {
		this.cvId = id;
	}

	public Integer getCvId() {
		return this.cvId;
	}

	public void setCvId(Integer id) {
		this.cvId = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDefinition() {
		return this.definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CV [cvId=");
		builder.append(this.cvId);
		builder.append(", name=");
		builder.append(this.name);
		builder.append(", definition=");
		builder.append(this.definition);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.cvId == null ? 0 : this.cvId.hashCode());

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
		if (!(obj instanceof CV)) {
			return false;
		}

		CV other = (CV) obj;
		if (this.cvId == null) {
			if (other.cvId != null) {
				return false;
			}
		} else if (!this.cvId.equals(other.cvId)) {
			return false;
		}

		return true;
	}

}
