/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.domain.oms;

import org.generationcp.middleware.util.Debug;

public class Property {

	private Term term;

	private Term isA;

	private String cropOntologyId;

	public Property() {
	}

	public Property(Term term) {
		this.term = term;
	}

	public Property(Term term, Term isA) {
		this.term = term;
		this.isA = isA;
	}

	public Property(Term term, Term isA, String cropOntologyId) {
		this(term, isA);
		this.cropOntologyId = cropOntologyId;
	}

	public Term getIsA() {
		return this.isA;
	}

	public void setIsA(Term isA) {
		this.isA = isA;
	}

	public Term getTerm() {
		return this.term;
	}

	public void setTerm(Term term) {
		this.term = term;
	}

	public int getId() {
		return this.term.getId();
	}

	public void setId(int id) {
		this.term.setId(id);
	}

	public String getName() {
		return this.term.getName();
	}

	public void setName(String name) {
		this.term.setName(name);
	}

	public String getDefinition() {
		return this.term.getDefinition();
	}

	public void setDefinition(String definition) {
		this.term.setDefinition(definition);
	}

	public int getIsAId() {
		if (this.isA != null) {
			return this.isA.getId();
		} else {
			return -1;
		}

	}

	public void setCropOntologyId(String cropOntologyId) {
		this.cropOntologyId = cropOntologyId;
	}

	public String getCropOntologyId() {
		return this.cropOntologyId;
	}

	@Override
	public String toString() {

		if (this.term == null) {
			return "";
		}

		StringBuilder builder = new StringBuilder();
		builder.append("Property [id=");
		builder.append(this.term.getId());
		builder.append(", name=");
		builder.append(this.term.getName());
		builder.append(", definition=");
		builder.append(this.term.getDefinition());
		builder.append(", IsA=");
		builder.append(this.isA);
		builder.append("]");
		return builder.toString();
	}

	public void print(int indent) {
		Debug.println(indent, "Property: ");
		Debug.println(indent + 3, "term: ");
		this.term.print(indent + 6);
		if (this.isA != null) {
			Debug.println(indent + 3, "IsA: ");
			this.isA.print(indent + 6);
		}
	}

}
