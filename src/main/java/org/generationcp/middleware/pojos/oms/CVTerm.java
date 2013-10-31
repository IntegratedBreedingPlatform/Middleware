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

package org.generationcp.middleware.pojos.oms;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * http://gmod.org/wiki/Chado_Tables#Table:_cvterm
 * 
 * A term, class, universal or type within an ontology or controlled vocabulary. 
 * This table is also used for relations and properties. 
 * cvterms constitute nodes in the graph defined by the collection of cvterms and cvterm_relationships.
 * 
 * @author Joyce Avestro
 *
 */
@Entity
@Table(	name = "cvterm", 
		uniqueConstraints = {
			@UniqueConstraint(columnNames = { "name", "cv_id", "is_obsolete" }),
			@UniqueConstraint(columnNames = { "dbxref_id" }) })
public class CVTerm implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@Column(name = "cvterm_id")
	private Integer cvTermId;

	/**
	 * The cv or ontology or namespace to which this cvterm belongs.
	 */
	@Column(name = "cv_id")
	private Integer cvId;

	/**
	 * A concise human-readable name or label for the cvterm. Uniquely
	 * identifies a cvterm within a cv.
	 */
	@Basic(optional = false)
	@Column(name = "name")
	private String name;

	/** A human-readable text definition. */
	@Column(name = "definition")
	private String definition;

	/**
	 * Primary identifier dbxref - The unique global OBO identifier for this
	 * cvterm. Note that a cvterm may have multiple secondary dbxrefs - see also
	 * table: cvterm_dbxref (from http://gmod.org/wiki/Chado_Tables)
	 */
	@Column(name = "dbxref_id")
	private Integer dbxRefId;

	/**
	 * Boolean 0=false,1=true; see GO documentation for details of obsoletion.
	 * Note that two terms with different primary dbxrefs may exist if one is
	 * obsolete. Duplicate names when obsoletes are included: Note that
	 * is_obsolete is an integer and can be incremented to fake uniqueness.
	 */
	@Column(name = "is_obsolete")
	private Integer isObsolete;

	/**
	 * Boolean 0=false,1=true relations or relationship types (also known as
	 * Typedefs in OBO format, or as properties or slots) form a cv/ontology in
	 * themselves. We use this flag to indicate whether this cvterm is an actual
	 * term/class/universal or a relation. Relations may be drawn from the OBO
	 * Relations ontology, but are not exclusively drawn from there.
	 */
	@Column(name = "is_relationshiptype")
	private Integer isRelationshipType;
	
	public CVTerm() {
	}

	public CVTerm(Integer cvTermId, Integer cv, String name, String definition,
			Integer dbxRefId, Integer isObsolete, Integer isRelationshipType) {
		super();
		this.cvTermId = cvTermId;
		this.cvId = cv;
		this.name = name;
		this.definition = definition;
		this.dbxRefId = dbxRefId;
		this.isObsolete = isObsolete;
		this.isRelationshipType = isRelationshipType;
	}

	public Integer getCvTermId() {
		return cvTermId;
	}

	public void setCvTermId(Integer cvTermId) {
		this.cvTermId = cvTermId;
	}

	public Integer getCv() {
		return cvId;
	}

	public void setCv(Integer cv) {
		this.cvId = cv;
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

	public Integer getDbxRefId() {
		return dbxRefId;
	}

	public void setDbxRefId(Integer dbxRefId) {
		this.dbxRefId = dbxRefId;
	}

	public Boolean isObsolete() {
		return isObsolete > 0 ? true : false;
	}

	public void setIsObsolete(Boolean isObsolete) {

		this.isObsolete = (int) (isObsolete ? 1 : 0);
	}

	public Boolean isRelationshipType() {
		return isRelationshipType > 0 ? true : false;
	}

	public void setIsRelationshipType(Boolean isRelationshipType) {
		this.isRelationshipType = (int) (isRelationshipType ? 1 : 0);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cvId == null) ? 0 : cvId.hashCode());
		result = prime * result
				+ ((cvTermId == null) ? 0 : cvTermId.hashCode());
		result = prime * result
				+ ((dbxRefId == null) ? 0 : dbxRefId.hashCode());
		result = prime * result
				+ ((definition == null) ? 0 : definition.hashCode());
		result = prime * result
				+ ((isObsolete == null) ? 0 : isObsolete.hashCode());
		result = prime
				* result
				+ ((isRelationshipType == null) ? 0 : isRelationshipType
						.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		CVTerm other = (CVTerm) obj;
		if (cvId == null) {
			if (other.cvId != null)
				return false;
		} else if (!cvId.equals(other.cvId))
			return false;
		if (cvTermId == null) {
			if (other.cvTermId != null)
				return false;
		} else if (!cvTermId.equals(other.cvTermId))
			return false;
		if (dbxRefId == null) {
			if (other.dbxRefId != null)
				return false;
		} else if (!dbxRefId.equals(other.dbxRefId))
			return false;
		if (definition == null) {
			if (other.definition != null)
				return false;
		} else if (!definition.equals(other.definition))
			return false;
		if (isObsolete == null) {
			if (other.isObsolete != null)
				return false;
		} else if (!isObsolete.equals(other.isObsolete))
			return false;
		if (isRelationshipType == null) {
			if (other.isRelationshipType != null)
				return false;
		} else if (!isRelationshipType.equals(other.isRelationshipType))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CVTerm [cvTermId=");
		builder.append(cvTermId);
		builder.append(", cvId=");
		builder.append(cvId);
		builder.append(", name=");
		builder.append(name);
		builder.append(", definition=");
		builder.append(definition);
		builder.append(", dbxRefId=");
		builder.append(dbxRefId);
		builder.append(", isObsolete=");
		builder.append(isObsolete);
		builder.append(", isRelationshipType=");
		builder.append(isRelationshipType);
		builder.append("]");
		return builder.toString();
	}

}