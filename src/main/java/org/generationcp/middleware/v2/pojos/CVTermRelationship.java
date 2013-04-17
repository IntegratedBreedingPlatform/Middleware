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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.WhereJoinTable;

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
    private Integer typeId;
    
    /**
     * The subject of the subj-predicate-obj sentence. The cvterm_relationship is about the subject. 
     * In a graph, this typically corresponds to the child node.
     * References cvterm
     */
    @Column(name="subject_id")
    private Integer subjectId;
    
    /**
     * The object of the subj-predicate-obj sentence. The cvterm_relationship refers to the object. 
     * In a graph, this typically corresponds to the parent node.
     * References cvterm
     */
    @ManyToOne(targetEntity = CVTerm.class)
	@JoinColumn(name = "object_id", nullable = false, referencedColumnName = "cvterm_id")
    @WhereJoinTable(clause = "is_obsolete = 0")
    private CVTerm object;
    
	public CVTermRelationship() {
	}

	public CVTermRelationship(Long cvTermRelationshipId, Integer typeId,
			Integer subjectId, CVTerm object) {
		super();
		this.cvTermRelationshipId = cvTermRelationshipId;
		this.typeId = typeId;
		this.subjectId = subjectId;
		this.object = object;
	}

	public Long getCvTermRelationshipId() {
		return cvTermRelationshipId;
	}

	public void setCvTermRelationshipId(Long cvTermRelationshipId) {
		this.cvTermRelationshipId = cvTermRelationshipId;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public Integer getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(Integer subjectId) {
		this.subjectId = subjectId;
	}

	public CVTerm getObject() {
		return object;
	}

	public void setObject(CVTerm object) {
		this.object = object;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((cvTermRelationshipId == null) ? 0 : cvTermRelationshipId
						.hashCode());
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
		return true;
	}

	@Override
	public String toString() {
		return "CVTermRelationship [cvTermRelationshipId="
				+ cvTermRelationshipId + ", typeId=" + typeId + ", subjectId="
				+ subjectId + ", object=" + object + "]";
	}

}