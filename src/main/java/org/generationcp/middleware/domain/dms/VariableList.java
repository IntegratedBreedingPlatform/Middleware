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

package org.generationcp.middleware.domain.dms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.generationcp.middleware.domain.oms.TermId;

/**
 * List of variables and types.
 */
public class VariableList implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Variable> variables = new ArrayList<Variable>();
	private VariableTypeList variableTypes = null;

	public void add(final Variable variable) {
		this.variables.add(variable);
	}

	public void addAll(final VariableList variableList) {
		this.variables.addAll(variableList.getVariables());
	}

	public Variable findById(final TermId termId) {
		return this.findById(termId.getId());
	}

	public Variable findById(final int id) {
		if (this.variables != null) {
			for (final Variable variable : this.variables) {
				if (variable.getVariableType().getId() == id) {
					return variable;
				}
			}
		}
		return null;
	}

	public List<Variable> getVariables() {
		return this.variables;
	}

	public void setVariables(final List<Variable> variables) {
		this.variables = variables;
	}

	public VariableTypeList getVariableTypes() {
		if (this.variableTypes == null) {
			this.variableTypes = new VariableTypeList();
			for (final Variable variable : this.variables) {
				this.variableTypes.add(variable.getVariableType());
			}
		}
		return this.variableTypes.sort();
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("VariableList [variables=");
		builder.append(this.variables);
		builder.append("]");
		return builder.toString();
	}

	public VariableList sort() {
		Collections.sort(this.variables);
		if (this.variableTypes != null) {
			this.variableTypes.sort();
		}
		return this;
	}

	public boolean containsValueByLocalName(final String localName, final String value) {
		boolean found = false;
		final Variable variable = this.findByLocalName(localName);
		if (variable != null) {
			found = this.strEquals(variable.getValue(), value);
		}
		return found;
	}

	private boolean strEquals(final String s1, final String s2) {
		if (s1 == null && s2 == null) {
			return true;
		}
		if (s1 == s2) {
			return true;
		}
		if (s1 != null && s1.equals(s2)) {
			return true;
		}
		return false;
	}

	public Variable findByLocalName(final String localName) {
		if (this.variables != null && localName != null) {
			for (final Variable variable : this.variables) {
				if (localName.equalsIgnoreCase(variable.getVariableType().getLocalName())) {
					return variable;
				}
			}
		}
		return null;
	}

	public int size() {
		return this.variables != null ? this.variables.size() : 0;
	}

	public boolean isEmpty() {
		return this.variables == null || this.variables.isEmpty();
	}

	public void print(final int indent) {
		if (this.variables != null) {
			for (final Variable variable : this.variables) {
				variable.print(indent);
			}
		}
	}
}
