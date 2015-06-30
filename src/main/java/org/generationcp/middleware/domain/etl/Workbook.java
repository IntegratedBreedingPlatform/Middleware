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

package org.generationcp.middleware.domain.etl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.util.Debug;

public class Workbook {

	public static final String STUDY_LABEL = "STUDY";

	private StudyDetails studyDetails;

	private List<MeasurementVariable> conditions;

	private List<MeasurementVariable> factors;

	private List<MeasurementVariable> constants;

	private List<MeasurementVariable> variates;

	private List<MeasurementRow> observations;
	private List<MeasurementRow> exportArrangedObservations; // for exporting only

	// derived variables used to improve performance
	private List<String> trialHeaders;
	private List<MeasurementVariable> trialVariables;
	private List<MeasurementVariable> measurementDatasetVariables;
	private List<MeasurementVariable> studyVariables;
	private List<MeasurementVariable> nonTrialFactors;
	private List<MeasurementVariable> trialFactors;
	private List<MeasurementVariable> germplasmFactors;
	private List<MeasurementVariable> studyConditions;
	private List<MeasurementVariable> studyConstants;
	private List<MeasurementVariable> trialConditions;
	private List<MeasurementVariable> trialConstants;
	private List<TreatmentVariable> treatmentFactors;
	private ExperimentalDesignVariable experimentalDesignVariables;
	private Map<String, ?> variableMap;

	private boolean isCheckFactorAddedOnly;

	private Integer totalNumberOfInstances;

	private Map<String, MeasurementVariable> measurementDatasetVariablesMap; // added for optimization

	private Integer studyId;
	private Integer trialDatasetId;
	private Integer measurementDatesetId;
	private Integer meansDatasetId;

	private List<MeasurementRow> trialObservations;

	private List<MeasurementRow> originalObservations;

	private List<MeasurementVariable> importConditionsCopy;
	private List<MeasurementVariable> importConstantsCopy;
	private List<MeasurementRow> importTrialObservationsCopy;

	private Integer importType;
	private List<StandardVariable> expDesignVariables;
	private boolean hasExistingDataOverwrite;
	private List<Integer> columnOrderedLists;
	
	public Workbook() {
		this.reset();
	}

	public Workbook(StudyDetails studyDetails, List<MeasurementVariable> conditions, List<MeasurementVariable> factors,
			List<MeasurementVariable> constants, List<MeasurementVariable> variates, List<MeasurementRow> observations) {
		this.studyDetails = studyDetails;
		this.conditions = conditions;
		this.factors = factors;
		this.constants = constants;
		this.variates = variates;
		this.observations = observations;
		this.reset();
	}

	public void reset() {
		this.trialHeaders = null;
		this.trialVariables = null;
		this.measurementDatasetVariables = null;
		this.studyVariables = null;
		this.nonTrialFactors = null;
		this.trialFactors = null;
		this.germplasmFactors = null;
		this.studyConditions = null;
		this.studyConstants = null;
		this.trialConditions = null;
		this.trialConstants = null;
		this.treatmentFactors = null;
		this.hasExistingDataOverwrite = false;
	}

	public StudyDetails getStudyDetails() {
		return this.studyDetails;
	}

	public void setStudyDetails(StudyDetails studyDetails) {
		this.studyDetails = studyDetails;
	}

	public List<MeasurementVariable> getConditions() {
		return this.conditions;
	}

	public void setConditions(List<MeasurementVariable> conditions) {
		this.conditions = conditions;
	}

	public List<MeasurementVariable> getFactors() {
		return this.factors;
	}

	public void setFactors(List<MeasurementVariable> factors) {
		this.factors = factors;
	}

	public List<MeasurementVariable> getVariates() {
		return this.variates;
	}

	public void setVariates(List<MeasurementVariable> variates) {
		this.variates = variates;
	}

	public List<MeasurementVariable> getConstants() {
		return this.constants;
	}

	public void setConstants(List<MeasurementVariable> constants) {
		this.constants = constants;
	}

	public List<MeasurementRow> getObservations() {
		return this.observations;
	}

	public void setObservations(List<MeasurementRow> observations) {
		this.observations = observations;
	}

	public boolean isNursery() {
		return this.studyDetails.isNursery();
	}

	public void setMeasurementDatasetVariables(List<MeasurementVariable> measurementDatasetVariables) {
		this.measurementDatasetVariables = measurementDatasetVariables;
	}

	public List<MeasurementVariable> getMeasurementDatasetVariables() {
		if (this.measurementDatasetVariables == null) {
			this.measurementDatasetVariables = new ArrayList<MeasurementVariable>();

			this.measurementDatasetVariables.addAll(this.getNonTrialFactors());
			if (this.variates != null && !this.variates.isEmpty()) {
				this.measurementDatasetVariables.addAll(this.variates);
			}
		}
		this.measurementDatasetVariables = this.arrangeMeasurementVariables(this.measurementDatasetVariables);
		return this.measurementDatasetVariables;
	}

	public List<MeasurementRow> arrangeMeasurementObservation(List<MeasurementRow> observations) {

		if (this.columnOrderedLists != null && !this.columnOrderedLists.isEmpty()) {
			for (MeasurementRow row : observations) {
				// we need to arrange each data list
				List<MeasurementData> measureDataList = row.getDataList();
				List<MeasurementData> newMeasureData = new ArrayList<MeasurementData>();
				for (Integer termId : this.columnOrderedLists) {
					int index = 0;
					boolean isFound = false;
					for (index = 0; index < measureDataList.size(); index++) {
						MeasurementData measurementData = measureDataList.get(index);
						if (termId.intValue() == measurementData.getMeasurementVariable().getTermId()) {
							newMeasureData.add(measurementData);
							isFound = true;
							break;
						}
					}
					if (isFound) {
						// we remove it from the list
						measureDataList.remove(index);
					}
				}
				newMeasureData.addAll(measureDataList);
				row.setDataList(newMeasureData);
			}
		}
		return observations;
	}

	public List<MeasurementVariable> arrangeMeasurementVariables(List<MeasurementVariable> varList) {
		List<MeasurementVariable> tempVarList = new ArrayList<MeasurementVariable>();
		List<MeasurementVariable> copyVarList = new ArrayList<MeasurementVariable>();
		copyVarList.addAll(varList);
		if (this.columnOrderedLists != null && !this.columnOrderedLists.isEmpty()) {
			// we order the list based on column orders
			for (Integer termId : this.columnOrderedLists) {
				int index = 0;
				boolean isFound = false;
				for (index = 0; index < copyVarList.size(); index++) {
					MeasurementVariable measurementVar = copyVarList.get(index);
					if (termId.intValue() == measurementVar.getTermId()) {
						tempVarList.add(measurementVar);
						isFound = true;
						break;
					}
				}
				if (isFound) {
					// we remove it from the list
					copyVarList.remove(index);
				}
			}
			// we join the new list with the remaining items
			tempVarList.addAll(copyVarList);
			return tempVarList;
		}
		return varList;
	}

	public List<MeasurementVariable> getMeasurementDatasetVariablesView() {
		List<MeasurementVariable> list = new ArrayList<MeasurementVariable>();
		if (!this.isNursery()) {
			MeasurementVariable trialFactor = null;
			if (this.getTrialFactors() != null) {
				for (MeasurementVariable var : this.getTrialConditions()) {
					if (var.getTermId() == TermId.TRIAL_INSTANCE_FACTOR.getId()) {
						trialFactor = var;
						break;
					}
				}
			}
			if (trialFactor != null) {
				list.add(trialFactor);
			}
		}
		list.addAll(this.getMeasurementDatasetVariables());
		list = this.arrangeMeasurementVariables(list);
		return list;
	}

	public Map<String, MeasurementVariable> getMeasurementDatasetVariablesMap() {
		// we set the id to the map
		if (this.measurementDatasetVariablesMap == null) {
			this.measurementDatasetVariablesMap = new HashMap<String, MeasurementVariable>();
			this.getMeasurementDatasetVariables();
			for (MeasurementVariable var : this.measurementDatasetVariables) {
				this.measurementDatasetVariablesMap.put(Integer.toString(var.getTermId()), var);
			}
		}
		return this.measurementDatasetVariablesMap;
	}

	public List<MeasurementVariable> getNonTrialFactors() {
		if (this.nonTrialFactors == null || this.nonTrialFactors.isEmpty()) {
			this.nonTrialFactors = this.getNonTrialVariables(this.factors);
		}
		return this.nonTrialFactors;
	}

	public List<MeasurementVariable> getStudyVariables() {
		if (this.studyVariables == null) {
			this.studyVariables = this.getConditionsAndConstants(true);
		}
		return this.studyVariables;
	}

	public List<MeasurementVariable> getTrialVariables() {
		
		Set<MeasurementVariable> unique = new HashSet<>();
		
		if (this.trialVariables == null) {
			unique.addAll(this.getConditionsAndConstants(false));
			
			List<MeasurementVariable> trialFactors = this.getTrialFactors();
			if (trialFactors != null) {
				
				unique.addAll(trialFactors);

			}
			this.trialVariables = new ArrayList<>(unique);
			
		}
		return this.trialVariables;
	}

	public List<MeasurementVariable> getTrialFactors() {
		if (this.trialFactors == null || this.trialFactors.isEmpty()) {
			this.trialFactors = this.getTrialVariables(this.factors);
		}
		return this.trialFactors;
	}

	public List<MeasurementVariable> getGermplasmFactors() {
		if (this.germplasmFactors == null || this.germplasmFactors.isEmpty()) {
			this.germplasmFactors = this.getGermplasmVariables(this.factors);
		}
		return this.germplasmFactors;
	}

	private List<MeasurementVariable> getConditionsAndConstants(boolean isStudy) {
		List<MeasurementVariable> list = new ArrayList<MeasurementVariable>();
		if (isStudy) {
			if (this.studyConditions == null && this.studyConstants == null) {
				this.studyConditions = this.getStudyConditions();
				this.studyConstants = this.getStudyConstants();
			}
			if (this.studyConditions != null) {
				list.addAll(this.studyConditions);
			}
			if (this.studyConstants != null) {
				list.addAll(this.studyConstants);
			}
		} else {
			if (this.trialConditions == null) {
				this.trialConditions = this.getTrialConditions();
			}
			if (this.trialConstants == null) {
				this.trialConstants = this.getTrialConstants();
			}
			if (this.trialConditions != null) {
				list.addAll(this.trialConditions);
			}
			if (this.trialConstants != null) {
				list.addAll(this.trialConstants);
			}
		}
		return list;
	}

	public List<MeasurementVariable> getStudyConstants() {
		if (this.studyConstants == null) {
			this.studyConstants = this.getVariables(this.constants, true);
		}
		return this.studyConstants;
	}

	public List<MeasurementVariable> getStudyConditions() {
		if (this.studyConditions == null) {
			this.studyConditions = this.getVariables(this.conditions, true);
		}
		return this.studyConditions;
	}

	public List<MeasurementVariable> getTrialConstants() {
		if (this.trialConstants == null) {
			this.trialConstants = this.getVariables(this.constants, false);
		}
		return this.trialConstants;
	}

	public List<MeasurementVariable> getTrialConditions() {
		if (this.trialConditions == null) {
			this.trialConditions = this.getVariables(this.conditions, false);
		}
		return this.trialConditions;
	}

	public List<MeasurementVariable> getVariables(List<MeasurementVariable> variables, boolean isStudy) {
		List<MeasurementVariable> list = new ArrayList<MeasurementVariable>();
		if (variables != null && !variables.isEmpty()) {
			for (MeasurementVariable variable : variables) {
				if (isStudy && variable.getLabel().toUpperCase().startsWith(Workbook.STUDY_LABEL) || !isStudy
						&& !variable.getLabel().toUpperCase().startsWith(Workbook.STUDY_LABEL)) {
					list.add(variable);
				}
			}
		}
		return list;
	}

	private List<MeasurementVariable> getNonTrialVariables(List<MeasurementVariable> variables) {
		List<MeasurementVariable> list = new ArrayList<MeasurementVariable>();
		if (variables != null && !variables.isEmpty()) {
			for (MeasurementVariable variable : variables) {
				if (!PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().contains(variable.getLabel().toUpperCase())) {
					list.add(variable);
				}
			}
		}
		return list;
	}

	private List<MeasurementVariable> getGermplasmVariables(List<MeasurementVariable> variables) {
		List<MeasurementVariable> list = new ArrayList<MeasurementVariable>();
		if (variables != null && !variables.isEmpty()) {
			for (MeasurementVariable variable : variables) {
				if (PhenotypicType.GERMPLASM.getLabelList().contains(variable.getLabel().toUpperCase())) {
					list.add(variable);
				}
			}
		}
		return list;
	}

	private List<MeasurementVariable> getTrialVariables(List<MeasurementVariable> variables) {
		List<MeasurementVariable> list = new ArrayList<MeasurementVariable>();
		if (variables != null && !variables.isEmpty()) {
			for (MeasurementVariable variable : variables) {
				if (PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().contains(variable.getLabel().toUpperCase())) {
					list.add(variable);
				}
			}
		}
		return list;
	}

	public List<String> getTrialHeaders() {
		if (this.trialHeaders == null) {
			this.trialHeaders = new ArrayList<String>();
			List<MeasurementVariable> variables = this.getTrialVariables();
			if (variables != null && !variables.isEmpty()) {
				for (MeasurementVariable variable : variables) {
					if (PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().contains(variable.getLabel().toUpperCase())) {
						this.trialHeaders.add(variable.getName());
					}
				}
			}
		}
		return this.trialHeaders;
	}

	public List<MeasurementVariable> getAllVariables() {
		List<MeasurementVariable> variableList = new ArrayList<MeasurementVariable>();
		if (this.conditions != null) {
			variableList.addAll(this.conditions);
		}
		if (this.constants != null) {
			variableList.addAll(this.constants);
		}
		if (this.factors != null) {
			variableList.addAll(this.factors);
		}
		if (this.variates != null) {
			variableList.addAll(this.variates);
		}

		return variableList;
	}

	public List<MeasurementVariable> getNonVariateVariables() {
		List<MeasurementVariable> variableList = new ArrayList<MeasurementVariable>();
		if (this.conditions != null) {
			variableList.addAll(this.conditions);
		}
		if (this.factors != null) {
			variableList.addAll(this.factors);
		}

		return variableList;
	}

	public List<MeasurementVariable> getVariateVariables() {
		List<MeasurementVariable> variableList = new ArrayList<MeasurementVariable>();
		if (this.constants != null) {
			variableList.addAll(this.constants);
		}
		if (this.variates != null) {
			variableList.addAll(this.variates);
		}

		return variableList;
	}

	public boolean isCheckFactorAddedOnly() {
		return this.isCheckFactorAddedOnly;
	}

	public void setCheckFactorAddedOnly(boolean isCheckFactorAddedOnly) {
		this.isCheckFactorAddedOnly = isCheckFactorAddedOnly;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Workbook [studyDetails=");
		builder.append(this.studyDetails);
		builder.append(", conditions=");
		builder.append(this.conditions);
		builder.append(", factors=");
		builder.append(this.factors);
		builder.append(", constants=");
		builder.append(this.constants);
		builder.append(", variates=");
		builder.append(this.variates);
		builder.append(", observations=");
		builder.append(this.observations);
		builder.append(", trialHeaders=");
		builder.append(this.trialHeaders);
		builder.append(", trialVariables=");
		builder.append(this.trialVariables);
		builder.append(", measurementDatasetVariables=");
		builder.append(this.measurementDatasetVariables);
		builder.append(", studyVariables=");
		builder.append(this.studyVariables);
		builder.append(", nonTrialFactors=");
		builder.append(this.nonTrialFactors);
		builder.append(", trialFactors=");
		builder.append(this.trialFactors);
		builder.append(", studyConditions=");
		builder.append(this.studyConditions);
		builder.append(", studyConstants=");
		builder.append(this.studyConstants);
		builder.append(", trialConditions=");
		builder.append(this.trialConditions);
		builder.append(", trialConstants=");
		builder.append(this.trialConstants);
		builder.append(", isCheckFactorAddedOnly=");
		builder.append(this.isCheckFactorAddedOnly);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.conditions == null ? 0 : this.conditions.hashCode());
		result = prime * result + (this.constants == null ? 0 : this.constants.hashCode());
		result = prime * result + (this.factors == null ? 0 : this.factors.hashCode());
		result = prime * result + (this.isCheckFactorAddedOnly ? 1231 : 1237);
		result = prime * result + (this.measurementDatasetVariables == null ? 0 : this.measurementDatasetVariables.hashCode());
		result = prime * result + (this.nonTrialFactors == null ? 0 : this.nonTrialFactors.hashCode());
		result = prime * result + (this.observations == null ? 0 : this.observations.hashCode());
		result = prime * result + (this.studyConditions == null ? 0 : this.studyConditions.hashCode());
		result = prime * result + (this.studyConstants == null ? 0 : this.studyConstants.hashCode());
		result = prime * result + (this.studyDetails == null ? 0 : this.studyDetails.hashCode());
		result = prime * result + (this.studyVariables == null ? 0 : this.studyVariables.hashCode());
		result = prime * result + (this.trialConditions == null ? 0 : this.trialConditions.hashCode());
		result = prime * result + (this.trialConstants == null ? 0 : this.trialConstants.hashCode());
		result = prime * result + (this.trialFactors == null ? 0 : this.trialFactors.hashCode());
		result = prime * result + (this.trialHeaders == null ? 0 : this.trialHeaders.hashCode());
		result = prime * result + (this.trialVariables == null ? 0 : this.trialVariables.hashCode());
		result = prime * result + (this.variates == null ? 0 : this.variates.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		Workbook other = (Workbook) obj;
		if (this.conditions == null) {
			if (other.conditions != null) {
				return false;
			}
		} else if (!this.conditions.equals(other.conditions)) {
			return false;
		}
		if (this.constants == null) {
			if (other.constants != null) {
				return false;
			}
		} else if (!this.constants.equals(other.constants)) {
			return false;
		}
		if (this.factors == null) {
			if (other.factors != null) {
				return false;
			}
		} else if (!this.factors.equals(other.factors)) {
			return false;
		}
		if (this.isCheckFactorAddedOnly != other.isCheckFactorAddedOnly) {
			return false;
		}
		if (this.measurementDatasetVariables == null) {
			if (other.measurementDatasetVariables != null) {
				return false;
			}
		} else if (!this.measurementDatasetVariables.equals(other.measurementDatasetVariables)) {
			return false;
		}
		if (this.nonTrialFactors == null) {
			if (other.nonTrialFactors != null) {
				return false;
			}
		} else if (!this.nonTrialFactors.equals(other.nonTrialFactors)) {
			return false;
		}
		if (this.observations == null) {
			if (other.observations != null) {
				return false;
			}
		} else if (!this.observations.equals(other.observations)) {
			return false;
		}
		if (this.studyConditions == null) {
			if (other.studyConditions != null) {
				return false;
			}
		} else if (!this.studyConditions.equals(other.studyConditions)) {
			return false;
		}
		if (this.studyConstants == null) {
			if (other.studyConstants != null) {
				return false;
			}
		} else if (!this.studyConstants.equals(other.studyConstants)) {
			return false;
		}
		if (this.studyDetails == null) {
			if (other.studyDetails != null) {
				return false;
			}
		} else if (!this.studyDetails.equals(other.studyDetails)) {
			return false;
		}
		if (this.studyVariables == null) {
			if (other.studyVariables != null) {
				return false;
			}
		} else if (!this.studyVariables.equals(other.studyVariables)) {
			return false;
		}
		if (this.trialConditions == null) {
			if (other.trialConditions != null) {
				return false;
			}
		} else if (!this.trialConditions.equals(other.trialConditions)) {
			return false;
		}
		if (this.trialConstants == null) {
			if (other.trialConstants != null) {
				return false;
			}
		} else if (!this.trialConstants.equals(other.trialConstants)) {
			return false;
		}
		if (this.trialFactors == null) {
			if (other.trialFactors != null) {
				return false;
			}
		} else if (!this.trialFactors.equals(other.trialFactors)) {
			return false;
		}
		if (this.trialHeaders == null) {
			if (other.trialHeaders != null) {
				return false;
			}
		} else if (!this.trialHeaders.equals(other.trialHeaders)) {
			return false;
		}
		if (this.trialVariables == null) {
			if (other.trialVariables != null) {
				return false;
			}
		} else if (!this.trialVariables.equals(other.trialVariables)) {
			return false;
		}
		if (this.variates == null) {
			if (other.variates != null) {
				return false;
			}
		} else if (!this.variates.equals(other.variates)) {
			return false;
		}
		return true;
	}

	public void print(int indent) {
		Debug.println(indent, "Workbook: ");

		if (this.studyDetails != null) {
			this.studyDetails.print(indent + 3);
		} else {
			Debug.print(indent + 3, "StudyDetails: null");
		}

		if (this.conditions != null) {
			Debug.println(indent + 3, "Conditions: ");
			for (MeasurementVariable variable : this.conditions) {
				variable.print(indent + 6);
			}
		} else {
			Debug.println(indent + 3, "Conditions: null ");
		}

		if (this.factors != null) {
			Debug.println(indent + 3, "Factors: ");
			for (MeasurementVariable variable : this.factors) {
				variable.print(indent + 6);
			}
		} else {
			Debug.println(indent + 3, "Factors: Null");
		}

		if (this.constants != null) {
			Debug.println(indent + 3, "Constants: ");
			for (MeasurementVariable variable : this.constants) {
				variable.print(indent + 6);
			}
		} else {
			Debug.println(indent + 3, "Constants: Null");
		}

		if (this.variates != null) {
			Debug.println(indent + 3, "Variates: ");
			for (MeasurementVariable variable : this.variates) {
				variable.print(indent + 6);
			}
		} else {
			Debug.println(indent + 3, "Variates: Null");
		}

		if (this.observations != null) {

			Debug.println(indent + 3, "Observations: ");
			for (MeasurementRow row : this.observations) {
				row.print(indent + 6);
			}
		} else {
			Debug.println(indent + 3, "Observations: Null");
		}

	}

	public Map<String, ?> getVariableMap() {
		return this.variableMap;
	}

	public void setVariableMap(Map<String, ?> variableMap) {
		this.variableMap = variableMap;
	}

	public void populateStudyAndDatasetIds(int studyId, int trialDatasetId, int measurementDatasetId, int meansDatasetId) {
		this.studyId = studyId;
		this.trialDatasetId = trialDatasetId;
		this.measurementDatesetId = measurementDatasetId;
		this.meansDatasetId = meansDatasetId;
	}

	public Integer getStudyId() {
		return this.studyId;
	}

	public void setStudyId(Integer studyId) {
		this.studyId = studyId;
	}

	public Integer getTrialDatasetId() {
		return this.trialDatasetId;
	}

	public void setTrialDatasetId(Integer trialDatasetId) {
		this.trialDatasetId = trialDatasetId;
	}

	public Integer getMeasurementDatesetId() {
		return this.measurementDatesetId;
	}

	public void setMeasurementDatesetId(Integer measurementDatesetId) {
		this.measurementDatesetId = measurementDatesetId;
	}

	public Map<Long, List<MeasurementRow>> segregateByTrialInstances() {
		Map<Long, List<MeasurementRow>> map = new HashMap<Long, List<MeasurementRow>>();

		if (this.observations != null) {
			for (MeasurementRow row : this.observations) {
				Long locationId = row.getLocationId();
				List<MeasurementRow> list = map.get(locationId);
				if (list == null) {
					list = new ArrayList<MeasurementRow>();
					map.put(locationId, list);
				}
				list.add(row);
			}
		}

		this.totalNumberOfInstances = map.size();
		return map;
	}

	public int getTotalNumberOfInstances() {
		if (this.totalNumberOfInstances == null) {
			if (this.trialObservations != null && !this.trialObservations.isEmpty()) {
				this.totalNumberOfInstances = this.trialObservations.size();
			} else {
				Map<Long, List<MeasurementRow>> map = this.segregateByTrialInstances();
				this.totalNumberOfInstances = map.size();
			}
		}
		return this.totalNumberOfInstances;
	}

	/**
	 * @return the trialObservations
	 */
	public List<MeasurementRow> getTrialObservations() {
		return this.trialObservations;
	}

	/**
	 * @param trialObservations the trialObservations to set
	 */
	public void setTrialObservations(List<MeasurementRow> trialObservations) {
		this.trialObservations = trialObservations;
	}

	public MeasurementRow getTrialObservation(long locationId) {
		if (this.trialObservations != null) {
			for (MeasurementRow row : this.trialObservations) {
				if (row.getLocationId() == locationId) {
					return row;
				}
			}
		}
		return null;
	}

	public List<MeasurementRow> getSortedTrialObservations() {
		if (this.trialObservations != null) {
			List<MeasurementRow> rows = new ArrayList<MeasurementRow>();
			Map<Long, List<MeasurementRow>> map = this.segregateByTrialInstances();
			List<Long> keys = new ArrayList<Long>(map.keySet());
			Collections.sort(keys);
			for (Long key : keys) {
				rows.addAll(map.get(key));
			}

			return rows;
		}
		return null;
	}

	public void updateTrialObservationsWithReferenceList(List<List<ValueReference>> trialList) {
		// assumes rows are in the same order and size
		if (trialList != null && !trialList.isEmpty() && this.trialObservations != null
				&& this.trialObservations.size() == trialList.size()) {

			int i = 0;
			for (List<ValueReference> trialRow : trialList) {
				List<MeasurementData> dataList = this.trialObservations.get(i).getDataList();

				for (ValueReference trialCell : trialRow) {
					MeasurementData data = this.getMeasurementDataById(dataList, trialCell.getId());
					if (data != null) {
						data.setValue(trialCell.getName());
					}
				}
				i++;
			}
		}
	}

	public MeasurementData getMeasurementDataById(List<MeasurementData> data, int id) {
		if (data != null && !data.isEmpty()) {
			for (MeasurementData cell : data) {
				if (cell.getMeasurementVariable().getTermId() == id) {
					return cell;
				}
			}
		}
		return null;
	}

	public List<TreatmentVariable> getTreatmentFactors() {
		return this.treatmentFactors;
	}

	public void setTreatmentFactors(List<TreatmentVariable> treatmentFactors) {
		this.treatmentFactors = treatmentFactors;
	}

	public List<MeasurementRow> getExportArrangedObservations() {
		return this.exportArrangedObservations;
	}

	public void setExportArrangedObservations(List<MeasurementRow> exportArrangedObservations) {
		this.exportArrangedObservations = exportArrangedObservations;
	}

	public String getStudyName() {
		if (this.getStudyConditions() != null) {
			for (MeasurementVariable condition : this.getStudyConditions()) {
				if (condition.getTermId() == TermId.STUDY_NAME.getId()) {
					return condition.getValue();
				}
			}
		}
		return null;
	}

	/**
	 * @return the originalObservations
	 */
	public List<MeasurementRow> getOriginalObservations() {
		return this.originalObservations;
	}

	/**
	 * @param originalObservations the originalObservations to set
	 */
	public void setOriginalObservations(List<MeasurementRow> originalObservations) {
		this.originalObservations = originalObservations;
	}

	public Integer getImportType() {
		return this.importType;
	}

	public void setImportType(Integer importType) {
		this.importType = importType;
	}

	public Integer getMeansDatasetId() {
		return this.meansDatasetId;
	}

	public void setMeansDatasetId(Integer meansDatasetId) {
		this.meansDatasetId = meansDatasetId;
	}

	public List<StandardVariable> getExpDesignVariables() {
		return this.expDesignVariables;
	}

	public void setExpDesignVariables(List<StandardVariable> expDesignVariables) {
		this.expDesignVariables = expDesignVariables;
	}

	/**
	 * @return the experimentalDesignVariables
	 */
	public ExperimentalDesignVariable getExperimentalDesignVariables() {
		return this.experimentalDesignVariables;
	}

	/**
	 * @param experimentalDesignVariables the experimentalDesignVariables to set
	 */
	public void setExperimentalDesignVariables(List<MeasurementVariable> list) {
		this.experimentalDesignVariables = new ExperimentalDesignVariable(list);
	}

	public List<MeasurementVariable> getImportConditionsCopy() {
		return this.importConditionsCopy;
	}

	public void setImportConditionsCopy(List<MeasurementVariable> importConditionsCopy) {
		this.importConditionsCopy = importConditionsCopy;
	}

	public List<MeasurementVariable> getImportConstantsCopy() {
		return this.importConstantsCopy;
	}

	public void setImportConstantsCopy(List<MeasurementVariable> importConstantsCopy) {
		this.importConstantsCopy = importConstantsCopy;
	}

	public List<MeasurementRow> getImportTrialObservationsCopy() {
		return this.importTrialObservationsCopy;
	}

	public void setImportTrialObservationsCopy(List<MeasurementRow> importTrialObservationsCopy) {
		this.importTrialObservationsCopy = importTrialObservationsCopy;
	}

	public void resetTrialConditions() {
		this.trialConditions = null;
	}

	public boolean hasExistingDataOverwrite() {
		return this.hasExistingDataOverwrite;
	}

	public void setHasExistingDataOverwrite(boolean hasExistingDataOverwrite) {
		this.hasExistingDataOverwrite = hasExistingDataOverwrite;
	}

	public List<Integer> getColumnOrderedLists() {
		return this.columnOrderedLists;
	}

	public void setColumnOrderedLists(List<Integer> columnOrderedLists) {
		this.columnOrderedLists = columnOrderedLists;
	}

}
