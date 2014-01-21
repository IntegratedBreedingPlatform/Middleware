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

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.util.Debug;


public class Workbook {
	
	public static final String STUDY_LABEL = "STUDY";
	
	private StudyDetails studyDetails;
	
	private List<MeasurementVariable> conditions; 
	
	private List<MeasurementVariable> factors; 
	
	private List<MeasurementVariable> constants; 
	
	private List<MeasurementVariable> variates; 
	
	private List<MeasurementRow> observations; 
	
	//derived variables used to improve performance
	private List<String> trialHeaders;
	private List<MeasurementVariable> trialVariables;
	private List<MeasurementVariable> measurementDatasetVariables;
	private List<MeasurementVariable> studyVariables;
	private List<MeasurementVariable> nonTrialFactors;
	private List<MeasurementVariable> trialFactors;
	private List<MeasurementVariable> studyConditions;
	private List<MeasurementVariable> studyConstants;
	private List<MeasurementVariable> trialConditions;
	private List<MeasurementVariable> trialConstants;
	
	public void reset() {
		trialHeaders = null;
		trialVariables = null;
		measurementDatasetVariables = null;
		studyVariables = null;
		nonTrialFactors = null;
		trialFactors = null;
		studyConditions = null;
		studyConstants = null;
		trialConditions = null;
		trialConstants = null;
	}
	
	public Workbook(){
		reset();
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
		reset();
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
	
	public boolean isNursery() {
		return this.studyDetails.isNursery();
	}
	
	public List<MeasurementVariable> getMeasurementDatasetVariables() {
		if(measurementDatasetVariables==null) {
			measurementDatasetVariables = new ArrayList<MeasurementVariable>();
			
			measurementDatasetVariables.addAll(getNonTrialFactors());
			measurementDatasetVariables.addAll(variates);
		}
		return measurementDatasetVariables;
	}

	public List<MeasurementVariable> getNonTrialFactors() {
		if(nonTrialFactors==null) {
			nonTrialFactors = getNonTrialVariables(factors);
		}
		return nonTrialFactors;
	}

	public List<MeasurementVariable> getStudyVariables() {
		if(studyVariables==null) {
			studyVariables = getConditionsAndConstants(true);
		}
		return studyVariables;
	}
	
	public List<MeasurementVariable> getTrialVariables() {
		if(trialVariables==null) {
			trialVariables = getConditionsAndConstants(false);
			trialVariables.addAll(getTrialFactors());
		}
		return trialVariables;
	}
	
	public List<MeasurementVariable> getTrialFactors() {
		if(trialFactors==null) {
			trialFactors = getTrialVariables(factors);
		}
		return trialFactors;
	}

	private List<MeasurementVariable> getConditionsAndConstants(boolean isStudy) {
		List<MeasurementVariable> list = new ArrayList<MeasurementVariable>();
		if(isStudy) {
			if(studyConditions == null && studyConstants == null) {
				studyConditions = getStudyConditions();
				studyConstants = getStudyConstants();
			}
			list.addAll(studyConditions);
			list.addAll(studyConstants);
		} else {
			if(trialConditions == null && trialConstants == null) {
				trialConditions = getTrialConditions();
				trialConstants = getTrialConstants();
			}
			list.addAll(trialConditions);
			list.addAll(trialConstants);
		}
		return list;
	}
	
	public List<MeasurementVariable> getStudyConstants() {
		if(studyConstants == null) {
			studyConstants = getVariables(constants, true);
		}
		return studyConstants;
	}

	public List<MeasurementVariable> getStudyConditions() {
		if(studyConditions == null) {
			studyConditions = getVariables(conditions, true);
		}
		return studyConditions;
	}
	
	public List<MeasurementVariable> getTrialConstants() {
		if(trialConstants==null) {
			trialConstants = getVariables(constants, false);
		}
		return trialConstants;
	}

	public List<MeasurementVariable> getTrialConditions() {
		if(trialConditions == null) {
			trialConditions = getVariables(conditions, false);
		}
		return trialConditions;
	}

	public List<MeasurementVariable> getVariables(List<MeasurementVariable> variables, boolean isStudy) {
		List<MeasurementVariable> list = new ArrayList<MeasurementVariable>();
		if (variables != null && variables.size() > 0) {
			for (MeasurementVariable variable : variables) {
				if (isStudy && variable.getLabel().toUpperCase().startsWith(STUDY_LABEL)
				|| !isStudy && !variable.getLabel().toUpperCase().startsWith(STUDY_LABEL)) {
					list.add(variable);
				}
			}
		}
		return list;
	}
	
	private List<MeasurementVariable> getNonTrialVariables(List<MeasurementVariable> variables) {
		List<MeasurementVariable> list = new ArrayList<MeasurementVariable>();
		if (variables != null && variables.size() > 0) {
			for (MeasurementVariable variable : variables) {
				if (!PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().contains(variable.getLabel().toUpperCase())) {
					list.add(variable);
				}
			}
		}
		return list;
	}
	
	private List<MeasurementVariable> getTrialVariables(List<MeasurementVariable> variables) {
		List<MeasurementVariable> list = new ArrayList<MeasurementVariable>();
		if (variables != null && variables.size() > 0) {
			for (MeasurementVariable variable : variables) {
				if (PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().contains(variable.getLabel().toUpperCase())) {
					list.add(variable);
				}
			}
		}
		return list;
	}
	
	public List<String> getTrialHeaders() {
		if(trialHeaders==null) {
			trialHeaders = new ArrayList<String>();
			List<MeasurementVariable> variables = getTrialVariables();
			if (variables != null && variables.size() > 0) {
				for (MeasurementVariable variable : variables) {
					if (PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().contains(variable.getLabel().toUpperCase())) {
						trialHeaders.add(variable.getName());
					}
				}
			}
		}
		return trialHeaders;
	}

    public List<MeasurementVariable> getAllVariables() {
        List<MeasurementVariable> variableList = new ArrayList<MeasurementVariable>();
        variableList.addAll(conditions);
        variableList.addAll(constants);
        variableList.addAll(factors);
        variableList.addAll(variates);

        return variableList;
    }
	
	
	
//	public List<MeasurementVariable> getTrialFactors() {
//		return getVariables(conditions, false);
//	}

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
		Debug.println(indent + 3, "Observations: ");
		for (MeasurementRow row : observations){
			row.print(indent + 6);
		}
	}

}
