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
package org.generationcp.middleware.domain;

import org.generationcp.middleware.util.Debug;

/** 
 * Contains the details of a trial environment - id and variables.
 */
public class TrialEnvironment {

	private int id;
	private VariableList variables;
	
	public TrialEnvironment(int id, VariableList variables) {
		this.id = id;
		this.variables = variables;
	}
	
	public int getId() {
		return id;
	}

	public boolean containsValueByLocalName(String localName, String value) {
		return variables.containsValueByLocalName(localName, value);
	}
	
	public VariableList getVariables() {
		return variables;
	}
	
	public void print(int indent) {
		Debug.println(indent, "Trial Environment " + id);
		variables.print(indent + 3);
	}
}
