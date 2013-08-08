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
package org.generationcp.middleware.domain.dms;

import org.generationcp.middleware.util.Debug;

/** 
 * Contains the details of a trial environment - id and variables.
 */
public class TrialEnvironment {

	private int id;
	private VariableList variables;
	private LocationDto location;
	private StudyReference study;
	
	public TrialEnvironment(int id, VariableList variables) {
		this.id = id;
		this.variables = variables;
	}
	
	public TrialEnvironment(int id, LocationDto location, StudyReference study) {
		this.id = id;
		this.location = location;
		this.study = study;
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
	
	public LocationDto getLocation() {
		return location;
	}

	public StudyReference getStudy() {
		return study;
	}

	public void print(int indent) {
		Debug.println(indent, "Trial Environment " + id);
		if (variables != null) {
			variables.print(indent + 3);
		}
		if (location != null) {
			location.print(indent+1);
		}
		if (study != null) {
			study.print(indent+1);
		}
		
	}
}
