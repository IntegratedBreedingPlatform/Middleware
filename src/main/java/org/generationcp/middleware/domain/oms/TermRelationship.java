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
package org.generationcp.middleware.domain.oms;

/**
 * Contains the details of term relationship.
 */
public class TermRelationship {

	private int id;

	private Term subjectTerm;

	private Term objectTerm;

	private TermRelationshipId relationshipId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Term getSubjectTerm() {
		return subjectTerm;
	}

	public void setSubjectTerm(Term subjectTerm) {
		this.subjectTerm = subjectTerm;
	}

	public Term getObjectTerm() {
		return objectTerm;
	}

	public void setObjectTerm(Term objectTerm) {
		this.objectTerm = objectTerm;
	}

	public TermRelationshipId getRelationshipId() {
		return relationshipId;
	}

	public void setRelationshipId(TermRelationshipId relationshipId) {
		this.relationshipId = relationshipId;
	}

	@Override
	public String toString() {
		return "TermRelationship{" +
				"id=" + id +
				", subjectTerm=" + subjectTerm +
				", objectTerm=" + objectTerm +
				", relationshipId=" + relationshipId +
				'}';
	}
}
