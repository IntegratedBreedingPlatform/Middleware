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
 * @deprecated as we have a new version of the Ontology API now. Use the new API class {@link org.generationcp.middleware.domain.ontology.Method} instead.
 */

@Deprecated
public class Method {

	private Term term;

	public Method() {
	}

	public Method(Term term) {
		this.term = term;
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

	@Override
	public String toString() {

		if (this.term == null) {
			return "";
		}

		StringBuilder builder = new StringBuilder();
		builder.append("Method [id=");
		builder.append(this.term.getId());
		builder.append(", name=");
		builder.append(this.term.getName());
		builder.append(", definition=");
		builder.append(this.term.getDefinition());
		builder.append("]");
		return builder.toString();
	}

	public void print(int indent) {
		Debug.println(indent, "Method: ");
		this.term.print(indent + 3);
	}

}
