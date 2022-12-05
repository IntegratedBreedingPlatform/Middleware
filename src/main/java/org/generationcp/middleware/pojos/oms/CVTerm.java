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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

/**
 * http://gmod.org/wiki/Chado_Tables#Table:_cvterm
 *
 * A term, class, universal or type within an ontology or controlled vocabulary. This table is also used for relations and properties.
 * cvterms constitute nodes in the graph defined by the collection of cvterms and cvterm_relationships.
 *
 * @author Joyce Avestro
 *
 */
@Entity
@Table(name = "cvterm", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "cv_id"}),
		@UniqueConstraint(columnNames = {"dbxref_id"})})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE, region="cvterm")
public class CVTerm implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String ID_NAME = "cvTermId";

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "cvterm_id")
	private Integer cvTermId;

	/**
	 * The cv or ontology or namespace to which this cvterm belongs.
	 */
	@Column(name = "cv_id")
	private Integer cvId;

	/**
	 * A concise human-readable name or label for the cvterm. Uniquely identifies a cvterm within a cv.
	 */
	@Basic(optional = false)
	@Column(name = "name")
	private String name;

	/** A human-readable text definition. */
	@Column(name = "definition", length = 1024)
	private String definition;

	/**
	 * Primary identifier dbxref - The unique global OBO identifier for this cvterm. Note that a cvterm may have multiple secondary dbxrefs
	 * - see also table: cvterm_dbxref (from http://gmod.org/wiki/Chado_Tables)
	 */
	@Column(name = "dbxref_id")
	private Integer dbxRefId;

	/**
	 * Boolean 0=false,1=true; see GO documentation for details of obsoletion. Note that two terms with different primary dbxrefs may exist
	 * if one is obsolete. Duplicate names when obsoletes are included: Note that is_obsolete is an integer and can be incremented to fake
	 * uniqueness.
	 */
	@Column(name = "is_obsolete")
	private Integer isObsolete;

	/**
	 * Boolean 0=false,1=true relations or relationship types (also known as Typedefs in OBO format, or as properties or slots) form a
	 * cv/ontology in themselves. We use this flag to indicate whether this cvterm is an actual term/class/universal or a relation.
	 * Relations may be drawn from the OBO Relations ontology, but are not exclusively drawn from there.
	 */
	@Column(name = "is_relationshiptype")
	private Integer isRelationshipType;

	@Type(type = "org.hibernate.type.NumericBooleanType")
	@Basic(optional = false)
	@Column(name = "is_system", columnDefinition = "TINYINT")
	private Boolean isSystem;

	public CVTerm() {
	}

	public CVTerm(final Integer cvTermId, final Integer cv, final String name, final String definition, final Integer dbxRefId,
		final Integer isObsolete,
		final Integer isRelationshipType, final Boolean isSystem) {
		super();
		this.cvTermId = cvTermId;
		this.cvId = cv;
		this.name = name;
		this.definition = definition;
		this.dbxRefId = dbxRefId;
		this.isObsolete = isObsolete;
		this.isRelationshipType = isRelationshipType;
		this.isSystem = isSystem;
	}

	public Integer getCvTermId() {
		return this.cvTermId;
	}

	public void setCvTermId(final Integer cvTermId) {
		this.cvTermId = cvTermId;
	}

	public Integer getCv() {
		return this.cvId;
	}

	public void setCv(final Integer cv) {
		this.cvId = cv;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDefinition() {
		return this.definition;
	}

	public void setDefinition(final String definition) {
		this.definition = definition;
	}

	public Integer getDbxRefId() {
		return this.dbxRefId;
	}

	public void setDbxRefId(final Integer dbxRefId) {
		this.dbxRefId = dbxRefId;
	}

	public Boolean isObsolete() {
		return this.isObsolete != null && this.isObsolete > 0;
	}

	public void setIsObsolete(final Boolean isObsolete) {
		this.isObsolete = isObsolete ? 1 : 0;
	}

	public Boolean isRelationshipType() {
		return this.isRelationshipType > 0;
	}

	public void setIsRelationshipType(final Boolean isRelationshipType) {
		this.isRelationshipType = isRelationshipType ? 1 : 0;
	}

	public Boolean getIsSystem() {
		return this.isSystem;
	}

	public void setIsSystem(final Boolean isSystem) {
		this.isSystem = isSystem;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.cvId == null ? 0 : this.cvId.hashCode());
		result = prime * result + (this.cvTermId == null ? 0 : this.cvTermId.hashCode());
		result = prime * result + (this.dbxRefId == null ? 0 : this.dbxRefId.hashCode());
		result = prime * result + (this.definition == null ? 0 : this.definition.hashCode());
		result = prime * result + (this.isObsolete == null ? 0 : this.isObsolete.hashCode());
		result = prime * result + (this.isRelationshipType == null ? 0 : this.isRelationshipType.hashCode());
		result = prime * result + (this.isSystem == null ? 0 : this.isSystem.hashCode());
		result = prime * result + (this.name == null ? 0 : this.name.hashCode());
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
		final CVTerm other = (CVTerm) obj;
		if (this.cvId == null) {
			if (other.cvId != null) {
				return false;
			}
		} else if (!this.cvId.equals(other.cvId)) {
			return false;
		}
		if (this.cvTermId == null) {
			if (other.cvTermId != null) {
				return false;
			}
		} else if (!this.cvTermId.equals(other.cvTermId)) {
			return false;
		}
		if (this.dbxRefId == null) {
			if (other.dbxRefId != null) {
				return false;
			}
		} else if (!this.dbxRefId.equals(other.dbxRefId)) {
			return false;
		}
		if (this.definition == null) {
			if (other.definition != null) {
				return false;
			}
		} else if (!this.definition.equals(other.definition)) {
			return false;
		}
		if (this.isObsolete == null) {
			if (other.isObsolete != null) {
				return false;
			}
		} else if (!this.isObsolete.equals(other.isObsolete)) {
			return false;
		}
		if (this.isRelationshipType == null) {
			if (other.isRelationshipType != null) {
				return false;
			}
		} else if (!this.isRelationshipType.equals(other.isRelationshipType)) {
			return false;
		}
		if (this.isSystem == null) {
			if (other.isSystem != null) {
				return false;
			}
		} else if (!this.isSystem.equals(other.isSystem)) {
			return false;
		}
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("CVTerm [cvTermId=");
		builder.append(this.cvTermId);
		builder.append(", cvId=");
		builder.append(this.cvId);
		builder.append(", name=");
		builder.append(this.name);
		builder.append(", definition=");
		builder.append(this.definition);
		builder.append(", dbxRefId=");
		builder.append(this.dbxRefId);
		builder.append(", isObsolete=");
		builder.append(this.isObsolete);
		builder.append(", isRelationshipType=");
		builder.append(this.isRelationshipType);
		builder.append(", isSystem=");
		builder.append(this.isSystem);
		builder.append("]");
		return builder.toString();
	}

}
