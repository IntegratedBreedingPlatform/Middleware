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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * http://gmod.org/wiki/Chado_Tables#Table:_cvterm_relationship
 * 
 * A relationship linking two cvterms. 
 * Each cvterm_relationship constitutes an edge in the graph defined by the collection of cvterms and cvterm_relationships. 
 * The meaning of the cvterm_relationship depends on the definition of the cvterm R refered to by type_id. 
 * However, in general the definitions are such that the statement "all SUBJs REL some OBJ" is true. 
 * The cvterm_relationship statement is about the subject, not the object. For example "insect wing part_of thorax".
 * 
 * @author Joyce Avestro
 *
 */
@Entity
@Table(	name = "cvterm_relationship")
public class CVTermRelationship implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @GeneratedValue
    @Column(name = "cvterm_relationship_id")
    private Long cvTermRelationshipId;
    
    /**
     * The nature of the relationship between subject and object. 
     * Note that relations are also housed in the cvterm table, typically from the OBO relationship ontology, 
     * although other relationship types are allowed.
     * References cvterm
     */
    @Column(name="type_id")
    private Long typeId;
    
    /**
     * The subject of the subj-predicate-obj sentence. The cvterm_relationship is about the subject. 
     * In a graph, this typically corresponds to the child node.
     * References cvterm
     */
    @Column(name="subject_id")
    private Long subjectId;
    
    /**
     * The object of the subj-predicate-obj sentence. The cvterm_relationship refers to the object. 
     * In a graph, this typically corresponds to the parent node.
     * References cvterm
     */
    @Column(name="object_id")
    private Long objectId;
    
	public CVTermRelationship() {
	}

	public CVTermRelationship(Long cvTermRelationshipId, Long typeId,
			Long subjectId, Long objectId) {
		super();
		this.cvTermRelationshipId = cvTermRelationshipId;
		this.typeId = typeId;
		this.subjectId = subjectId;
		this.objectId = objectId;
	}

	public Long getCvTermRelationshipId() {
		return cvTermRelationshipId;
	}

	public void setCvTermRelationshipId(Long cvTermRelationshipId) {
		this.cvTermRelationshipId = cvTermRelationshipId;
	}

	public Long getTypeId() {
		return typeId;
	}

	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}

	public Long getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(Long subjectId) {
		this.subjectId = subjectId;
	}

	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((cvTermRelationshipId == null) ? 0 : cvTermRelationshipId
						.hashCode());
		result = prime * result + ((objectId == null) ? 0 : objectId.hashCode());
		result = prime * result + ((subjectId == null) ? 0 : subjectId.hashCode());
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
		CVTermRelationship other = (CVTermRelationship) obj;
		if (cvTermRelationshipId == null) {
			if (other.cvTermRelationshipId != null)
				return false;
		} else if (!cvTermRelationshipId.equals(other.cvTermRelationshipId))
			return false;
		if (objectId == null) {
			if (other.objectId != null)
				return false;
		} else if (!objectId.equals(other.objectId))
			return false;
		if (subjectId == null) {
			if (other.subjectId != null)
				return false;
		} else if (!subjectId.equals(other.subjectId))
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
		builder.append("CVTermRelationship [cvTermRelationshipId=");
		builder.append(cvTermRelationshipId);
		builder.append(", typeId=");
		builder.append(typeId);
		builder.append(", subjectId=");
		builder.append(subjectId);
		builder.append(", objectId=");
		builder.append(objectId);
		builder.append("]");
		return builder.toString();
	}



}