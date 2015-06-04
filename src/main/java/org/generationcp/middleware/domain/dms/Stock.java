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

import org.generationcp.middleware.util.Debug;

/**
 * The Stock with id and variables.
 *
 */
public class Stock {

	private final int id;
	private final VariableList variables;

	public Stock(int id, VariableList variables) {
		this.id = id;
		this.variables = variables;
	}

	public int getId() {
		return this.id;
	}

	public boolean containsValueByLocalName(String localName, String value) {
		return this.variables.containsValueByLocalName(localName, value);
	}

	public VariableList getVariables() {
		return this.variables;
	}

	public void print(int indent) {
		Debug.println(indent, "Stock " + this.id);
		this.variables.print(indent + 3);
	}
}
