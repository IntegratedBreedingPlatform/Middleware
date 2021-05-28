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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;


/**
 *
 * http://gmod.org/wiki/Chado_Tables#Table:_cvterm_relationship
 *
 * A relationship linking two cvterms. Each cvterm_relationship constitutes an edge in the graph defined by the collection of cvterms and
 * cvterm_relationships. The meaning of the cvterm_relationship depends on the definition of the cvterm R refered to by type_id. However, in
 * general the definitions are such that the statement "all SUBJs REL some OBJ" is true. The cvterm_relationship statement is about the
 * subject, not the object. For example "insect wing part_of thorax".
 *
 * @author Joyce Avestro
 *
 */
@Entity
@Table(name = "cvterm_relationship")
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE, region="cvterm_relationship")
public class CVTermRelationship implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String ID_NAME = "cvTermRelationshipId";

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "cvterm_relationship_id")
	private Integer cvTermRelationshipId;

	/**
	 * The nature of the relationship between subject and object. Note that relations are also housed in the cvterm table, typically from
	 * the OBO relationship ontology, although other relationship types are allowed. References cvterm
	 */
	@Column(name = "type_id")
	private Integer typeId;

	/**
	 * The subject of the subj-predicate-obj sentence. The cvterm_relationship is about the subject. In a graph, this typically corresponds
	 * to the child node. References cvterm
	 */
	@Column(name = "subject_id")
	private Integer subjectId;

	/**
	 * The object of the subj-predicate-obj sentence. The cvterm_relationship refers to the object. In a graph, this typically corresponds
	 * to the parent node. References cvterm
	 */
	@Column(name = "object_id")
	private Integer objectId;

	public CVTermRelationship() {
	}

	public CVTermRelationship(final Integer cvTermRelationshipId, final Integer typeId, final Integer subjectId, final Integer objectId) {
		super();
		this.cvTermRelationshipId = cvTermRelationshipId;
		this.typeId = typeId;
		this.subjectId = subjectId;
		this.objectId = objectId;
	}

	public CVTermRelationship(final Integer typeId, final Integer subjectId, final Integer objectId) {
		super();
		this.typeId = typeId;
		this.subjectId = subjectId;
		this.objectId = objectId;
	}

	public Integer getCvTermRelationshipId() {
		return this.cvTermRelationshipId;
	}

	public void setCvTermRelationshipId(final Integer cvTermRelationshipId) {
		this.cvTermRelationshipId = cvTermRelationshipId;
	}

	public Integer getTypeId() {
		return this.typeId;
	}

	public void setTypeId(final Integer typeId) {
		this.typeId = typeId;
	}

	public Integer getSubjectId() {
		return this.subjectId;
	}

	public void setSubjectId(final Integer subjectId) {
		this.subjectId = subjectId;
	}

	public Integer getObjectId() {
		return this.objectId;
	}

	public void setObjectId(final Integer objectId) {
		this.objectId = objectId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.cvTermRelationshipId == null ? 0 : this.cvTermRelationshipId.hashCode());
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
		final CVTermRelationship other = (CVTermRelationship) obj;
		if (this.cvTermRelationshipId == null) {
			if (other.cvTermRelationshipId != null) {
				return false;
			}
		} else if (!this.cvTermRelationshipId.equals(other.cvTermRelationshipId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "CVTermRelationship [cvTermRelationshipId=" + this.cvTermRelationshipId + ", typeId=" + this.typeId + ", subjectId="
				+ this.subjectId + ", objectId=" + this.objectId + "]";
	}

}
