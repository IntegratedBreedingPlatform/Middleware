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

/**
 * The Trait Class ontology.
 *
 * @author Joyce Avestro
 *
 */
public class TraitClass implements Comparable<TraitClass> {

	private Term term;

	/*
	 * Either TermId.ONTOLOGY_TRAIT_CLASS or TermId.ONTOLOGY_RESEARCH_CLASS for first-level class. For lower-level class, this contains the
	 * parent trait class.
	 */
	private Term isA;

	public TraitClass(Term term, Term isA) {
		this.term = term;
		this.isA = isA;
	}

	public Term getTerm() {
		return this.term;
	}

	public void setTerm(Term term) {
		this.term = term;
	}

	public Term getIsA() {
		return this.isA;
	}

	public void setIsA(Term isA) {
		this.isA = isA;
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

	@Override
	public String toString() {

		if (this.term == null) {
			return "";
		}

		StringBuilder builder = new StringBuilder();
		builder.append("TraitClass [id=");
		builder.append(this.term.getId());
		builder.append(", name=");
		builder.append(this.term.getName());
		builder.append(", definition=");
		builder.append(this.term.getDefinition());
		builder.append(", isA=");
		builder.append(this.isA);
		builder.append("]");
		return builder.toString();
	}

	public void print(int indent) {
		Debug.println(indent, "TraitClass: ");
		Debug.println(indent + 3, "term: ");
		this.term.print(indent + 6);
		if (this.isA != null) {
			Debug.println(indent + 3, "isA: ");
			this.isA.print(indent + 6);
		}
	}

	/*
	 * (non-Javadoc) Sort in ascending order by trait group name
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(TraitClass compareValue) {
		String compareName = compareValue.getName();
		return this.getName().compareToIgnoreCase(compareName);
	}

}
