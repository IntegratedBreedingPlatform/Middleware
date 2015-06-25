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

package org.generationcp.middleware.domain.oms;

import org.generationcp.middleware.domain.ontology.TermRelationshipId;

/**
 * Contains the details of term relationship.
 */
public class TermRelationship {

	private int id;

	private Term subjectTerm;

	private Term objectTerm;

	private TermRelationshipId relationshipId;

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Term getSubjectTerm() {
		return this.subjectTerm;
	}

	public void setSubjectTerm(Term subjectTerm) {
		this.subjectTerm = subjectTerm;
	}

	public Term getObjectTerm() {
		return this.objectTerm;
	}

	public void setObjectTerm(Term objectTerm) {
		this.objectTerm = objectTerm;
	}

	public TermRelationshipId getRelationshipId() {
		return this.relationshipId;
	}

	public void setRelationshipId(TermRelationshipId relationshipId) {
		this.relationshipId = relationshipId;
	}

	@Override
	public String toString() {
		return "TermRelationship{" + "id=" + this.id + ", subjectTerm=" + this.subjectTerm + ", objectTerm=" + this.objectTerm
				+ ", relationshipId=" + this.relationshipId + '}';
	}
}
