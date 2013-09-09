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
package org.generationcp.middleware.domain.etl;

import java.util.List;

import org.generationcp.middleware.util.Debug;


public class Workbook {
	
	private StudyDetails studyDetails;
	
	private List<MeasurementVariable> conditions; 
	
	private List<MeasurementVariable> factors; 
	
	private List<MeasurementVariable> constants; 
	
	private List<MeasurementVariable> variates; 
	
	private List<MeasurementRow> observations; 
	
	public Workbook(){
		
	}
	
	public Workbook(StudyDetails studyDetails,
			List<MeasurementVariable> conditions,
			List<MeasurementVariable> factors,
			List<MeasurementVariable> constants,
			List<MeasurementVariable> variates,
			List<MeasurementRow> observations) {
		this.studyDetails = studyDetails;
		this.conditions = conditions;
		this.factors = factors;
		this.constants = constants;
		this.variates = variates;
		this.observations = observations;
	}

	public StudyDetails getStudyDetails() {
		return studyDetails;
	}

	public void setStudyDetails(StudyDetails studyDetails) {
		this.studyDetails = studyDetails;
	}

	public List<MeasurementVariable> getConditions() {
		return conditions;
	}

	public void setConditions(List<MeasurementVariable> conditions) {
		this.conditions = conditions;
	}

	public List<MeasurementVariable> getFactors() {
		return factors;
	}

	public void setFactors(List<MeasurementVariable> factors) {
		this.factors = factors;
	}

	public List<MeasurementVariable> getVariates() {
		return variates;
	}

	public void setVariates(List<MeasurementVariable> variates) {
		this.variates = variates;
	}

	public List<MeasurementVariable> getConstants() {
		return constants;
	}

	public void setConstants(List<MeasurementVariable> constants) {
		this.constants = constants;
	}

	public List<MeasurementRow> getObservations() {
		return observations;
	}

	public void setObservations(List<MeasurementRow> observations) {
		this.observations = observations;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Workbook [studyDetails=");
		builder.append(studyDetails);
		builder.append(", conditions=");
		builder.append(conditions);
		builder.append(", factors=");
		builder.append(factors);
		builder.append(", constants=");
		builder.append(constants);
		builder.append(", variates=");
		builder.append(variates);
		builder.append(", observations=");
		builder.append(observations);
		builder.append("]");
		return builder.toString();
	}

	public void print(int indent) {
		Debug.println(indent, "Workbook: ");
		studyDetails.print(indent + 3);
		Debug.println(indent + 3, "Conditions: ");
		for (MeasurementVariable variable : conditions){
			variable.print(indent + 6);
		}
		Debug.println(indent + 3, "Factors: ");
		for (MeasurementVariable variable : factors){
			variable.print(indent + 6);
		}
		Debug.println(indent + 3, "Constants: ");
		for (MeasurementVariable variable : constants){
			variable.print(indent + 6);
		}
		Debug.println(indent + 3, "Variates: ");
		for (MeasurementVariable variable : variates){
			variable.print(indent + 6);
		}
		Debug.println(indent + 3, "Conditions: ");
		for (MeasurementRow row : observations){
			row.print(indent + 6);
		}
	}

}
