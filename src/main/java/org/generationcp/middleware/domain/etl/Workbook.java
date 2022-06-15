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

import org.apache.commons.collections.CollectionUtils;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.util.Debug;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Workbook {

	public static final String STUDY_LABEL = "STUDY";
	public static final String DEFAULT_LOCATION_ID_VARIABLE_ALIAS = "LOCATION_NAME";

	private StudyDetails studyDetails;

	private List<MeasurementVariable> conditions;

	private List<MeasurementVariable> factors;

	private List<MeasurementVariable> constants;

	private List<MeasurementVariable> variates;

	private List<MeasurementRow> observations = new ArrayList<>();

	// for exporting only
	private List<MeasurementRow> exportArrangedObservations;

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

	// added for optimization
	private Map<String, MeasurementVariable> measurementDatasetVariablesMap;

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
	private boolean hasOutOfBoundsData;
	private List<Integer> columnOrderedLists;

	private Integer plotsIdNotfound;

	private List<MeasurementVariable> entryDetails;

	public Workbook() {
		this.reset();
		this.trialObservations = new ArrayList<>();
	}

	// TODO : rename reset method to something more indicative of its actual purpose : clearDerivedVariables maybe.
	/**
	 * Used by DatasetImporter to clear ONLY the derived variables of the workbook prior to saving.
	 */
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
		this.hasOutOfBoundsData = false;
		this.plotsIdNotfound = 0;
	}

	public StudyDetails getStudyDetails() {
		return this.studyDetails;
	}

	public void setStudyDetails(final StudyDetails studyDetails) {
		this.studyDetails = studyDetails;
	}

	public List<MeasurementVariable> getConditions() {
		return this.conditions;
	}

	public void setConditions(final List<MeasurementVariable> conditions) {
		this.conditions = conditions;
	}

	public List<MeasurementVariable> getFactors() {
		return this.factors;
	}

	public void setFactors(final List<MeasurementVariable> factors) {
		this.factors = factors;
	}

	public List<MeasurementVariable> getVariates() {
		return this.variates;
	}

	public void setVariates(final List<MeasurementVariable> variates) {
		this.variates = variates;
	}

	public List<MeasurementVariable> getConstants() {
		return this.constants;
	}

	public void setConstants(final List<MeasurementVariable> constants) {
		this.constants = constants;
	}

	public List<MeasurementRow> getObservations() {
		return this.observations;
	}

	public void setObservations(final List<MeasurementRow> observations) {
		this.observations = observations;
	}

	public void setMeasurementDatasetVariables(final List<MeasurementVariable> measurementDatasetVariables) {
		this.measurementDatasetVariables = measurementDatasetVariables;
	}

	public List<MeasurementVariable> getMeasurementDatasetVariables() {
		if (this.measurementDatasetVariables == null) {
			this.measurementDatasetVariables = new ArrayList<>();

			this.measurementDatasetVariables.addAll(this.getNonTrialFactors());
			if (this.variates != null && !this.variates.isEmpty()) {
				this.measurementDatasetVariables.addAll(this.variates);
			}

			if (!CollectionUtils.isEmpty(this.entryDetails)) {
				this.measurementDatasetVariables.addAll(this.entryDetails);
			}
		}
		this.measurementDatasetVariables = this.arrangeMeasurementVariables(this.measurementDatasetVariables);
		return this.measurementDatasetVariables;
	}

	public List<MeasurementRow> arrangeMeasurementObservation(final List<MeasurementRow> observations) {

		if (this.columnOrderedLists != null && !this.columnOrderedLists.isEmpty()) {
			for (final MeasurementRow row : observations) {
				// we need to arrange each data list
				final List<MeasurementData> measureDataList = row.getDataList();
				final List<MeasurementData> newMeasureData = new ArrayList<>();
				for (final Integer termId : this.columnOrderedLists) {
					int index;
					boolean isFound = false;
					for (index = 0; index < measureDataList.size(); index++) {
						final MeasurementData measurementData = measureDataList.get(index);
						if (termId == measurementData.getMeasurementVariable().getTermId()) {
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

	public List<MeasurementVariable> arrangeMeasurementVariables(final List<MeasurementVariable> varList) {
		final List<MeasurementVariable> tempVarList = new ArrayList<>();
		final List<MeasurementVariable> copyVarList = new ArrayList<>();
		copyVarList.addAll(varList);
		if (this.columnOrderedLists != null && !this.columnOrderedLists.isEmpty()) {
			// we order the list based on column orders
			for (final Integer termId : this.columnOrderedLists) {
				int index;
				boolean isFound = false;
				for (index = 0; index < copyVarList.size(); index++) {
					final MeasurementVariable measurementVar = copyVarList.get(index);
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
			copyVarList.addAll(tempVarList);
			return copyVarList;
		}
		return varList;
	}

	/**
	 * This method handles the retrieval of the measurement dataset variables which includes the following: TRIAL_INSTANCE, FACTORS,
	 * VARIATES. The order of insertion matters that's why we used LinkedHashSet on this method to preserve the order of insertion.
	 * 
	 * @return measurement dataset variable list
	 */
	public List<MeasurementVariable> getMeasurementDatasetVariablesView() {
		final Set<MeasurementVariable> list = new LinkedHashSet<>();
		MeasurementVariable trialFactor = null;
		if (this.getTrialFactors() != null) {
			for (final MeasurementVariable var : this.getTrialConditions()) {
				if (var.getTermId() == TermId.TRIAL_INSTANCE_FACTOR.getId()) {
					trialFactor = var;
					break;
				}
			}
			if (trialFactor != null) {
				list.add(trialFactor);
			}
		}
		list.addAll(this.getMeasurementDatasetVariables());

		return this.arrangeMeasurementVariables(new ArrayList<>(list));
	}

	public Map<String, MeasurementVariable> getMeasurementDatasetVariablesMap() {
		// we set the id to the map
		if (this.measurementDatasetVariablesMap == null) {
			this.measurementDatasetVariablesMap = new HashMap<>();
			this.getMeasurementDatasetVariables();
			for (final MeasurementVariable var : this.measurementDatasetVariables) {
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

		final Set<MeasurementVariable> unique = new HashSet<>();

		if (this.trialVariables == null) {
			unique.addAll(this.getConditionsAndConstants(false));

			if (this.getTrialFactors() != null) {

				unique.addAll(this.getTrialFactors());

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

	private List<MeasurementVariable> getConditionsAndConstants(final boolean isStudy) {
		final List<MeasurementVariable> list = new ArrayList<>();
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

	public List<MeasurementVariable> getVariables(final List<MeasurementVariable> variables, final boolean isStudy) {
		final List<MeasurementVariable> list = new ArrayList<>();
		if (variables != null && !variables.isEmpty()) {
			for (final MeasurementVariable variable : variables) {
				if (isStudy && variable.getLabel().toUpperCase().startsWith(Workbook.STUDY_LABEL)
						|| !isStudy && !variable.getLabel().toUpperCase().startsWith(Workbook.STUDY_LABEL)) {
					list.add(variable);
				}
			}
		}
		return list;
	}

	private List<MeasurementVariable> getNonTrialVariables(final List<MeasurementVariable> variables) {
		final List<MeasurementVariable> list = new ArrayList<>();
		if (variables != null && !variables.isEmpty()) {
			for (final MeasurementVariable variable : variables) {
				if (!PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().contains(variable.getLabel().toUpperCase())) {
					list.add(variable);
				}
			}
		}
		return list;
	}

	private List<MeasurementVariable> getGermplasmVariables(final List<MeasurementVariable> variables) {
		final List<MeasurementVariable> list = new ArrayList<>();
		if (variables != null && !variables.isEmpty()) {
			for (final MeasurementVariable variable : variables) {
				if (PhenotypicType.GERMPLASM.getLabelList().contains(variable.getLabel().toUpperCase())) {
					list.add(variable);
				}
			}
		}
		return list;
	}

	private List<MeasurementVariable> getEntryDetailVariables(final List<MeasurementVariable> variables) {
		final List<MeasurementVariable> list = new ArrayList<>();
		if (variables != null && !variables.isEmpty()) {
			for (final MeasurementVariable variable : variables) {
				if (PhenotypicType.ENTRY_DETAIL.getLabelList().contains(variable.getLabel().toUpperCase())) {
					list.add(variable);
				}
			}
		}
		return list;
	}

	private List<MeasurementVariable> getTrialVariables(final List<MeasurementVariable> variables) {
		final List<MeasurementVariable> list = new ArrayList<>();
		if (variables != null && !variables.isEmpty()) {
			for (final MeasurementVariable variable : variables) {
				if (PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().contains(variable.getLabel().toUpperCase())) {
					list.add(variable);
				}
			}
		}
		return list;
	}

	public List<String> getTrialHeaders() {
		if (this.trialHeaders == null) {
			this.trialHeaders = new ArrayList<>();
			final List<MeasurementVariable> variables = this.getTrialVariables();
			if (variables != null && !variables.isEmpty()) {
				for (final MeasurementVariable variable : variables) {
					if (PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().contains(variable.getLabel().toUpperCase())) {
						this.trialHeaders.add(variable.getName());
					}
				}
			}
		}
		return this.trialHeaders;
	}

	public List<MeasurementVariable> getAllVariables() {
		final List<MeasurementVariable> variableList = new ArrayList<>();
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
		if (this.entryDetails != null) {
			variableList.addAll(this.entryDetails);
		}
		return variableList;
	}

	public List<MeasurementVariable> getNonVariateVariables() {
		final List<MeasurementVariable> variableList = new ArrayList<>();
		if (this.conditions != null) {
			variableList.addAll(this.conditions);
		}
		if (this.factors != null) {
			variableList.addAll(this.factors);
		}

		return variableList;
	}

	public List<MeasurementVariable> getVariateVariables() {
		final List<MeasurementVariable> variableList = new ArrayList<>();
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

	public void setCheckFactorAddedOnly(final boolean isCheckFactorAddedOnly) {
		this.isCheckFactorAddedOnly = isCheckFactorAddedOnly;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
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
		builder.append(", trialObservations=");
		builder.append(this.trialObservations);
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
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final Workbook other = (Workbook) obj;
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
			return other.variates == null;
		} else
			return this.variates.equals(other.variates);
	}

	public void print(final int indent) {
		Debug.println(indent, "Workbook: ");

		if (this.studyDetails != null) {
			this.studyDetails.print(indent + 3);
		} else {
			Debug.print(indent + 3, "StudyDetails: null");
		}

		if (this.conditions != null) {
			Debug.println(indent + 3, "Conditions: ");
			for (final MeasurementVariable variable : this.conditions) {
				variable.print(indent + 6);
			}
		} else {
			Debug.println(indent + 3, "Conditions: null ");
		}

		if (this.factors != null) {
			Debug.println(indent + 3, "Factors: ");
			for (final MeasurementVariable variable : this.factors) {
				variable.print(indent + 6);
			}
		} else {
			Debug.println(indent + 3, "Factors: Null");
		}

		if (this.constants != null) {
			Debug.println(indent + 3, "Constants: ");
			for (final MeasurementVariable variable : this.constants) {
				variable.print(indent + 6);
			}
		} else {
			Debug.println(indent + 3, "Constants: Null");
		}

		if (this.variates != null) {
			Debug.println(indent + 3, "Variates: ");
			for (final MeasurementVariable variable : this.variates) {
				variable.print(indent + 6);
			}
		} else {
			Debug.println(indent + 3, "Variates: Null");
		}

		if (this.observations != null) {

			Debug.println(indent + 3, "Observations: ");
			for (final MeasurementRow row : this.observations) {
				row.print(indent + 6);
			}
		} else {
			Debug.println(indent + 3, "Observations: Null");
		}

	}

	public Map<String, ?> getVariableMap() {
		return this.variableMap;
	}

	public void setVariableMap(final Map<String, ?> variableMap) {
		this.variableMap = variableMap;
	}

	public void populateDatasetIds(final int trialDatasetId, final int measurementDatasetId, final int meansDatasetId) {
		this.trialDatasetId = trialDatasetId;
		this.measurementDatesetId = measurementDatasetId;
		this.meansDatasetId = meansDatasetId;
	}

	public Integer getTrialDatasetId() {
		return this.trialDatasetId;
	}

	public void setTrialDatasetId(final Integer trialDatasetId) {
		this.trialDatasetId = trialDatasetId;
	}

	public Integer getMeasurementDatesetId() {
		return this.measurementDatesetId;
	}

	public void setMeasurementDatesetId(final Integer measurementDatesetId) {
		this.measurementDatesetId = measurementDatesetId;
	}

	public Map<Long, List<MeasurementRow>> segregateByTrialInstances() {
		final Map<Long, List<MeasurementRow>> map = new HashMap<>();

		if (this.observations != null) {
			for (final MeasurementRow row : this.observations) {
				final Long locationId = row.getLocationId();
				List<MeasurementRow> list = map.get(locationId);
				if (list == null) {
					list = new ArrayList<>();
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
				final Map<Long, List<MeasurementRow>> map = this.segregateByTrialInstances();
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
	 * @return the trialObservation based on their trial instance no
	 */
	public MeasurementRow getTrialObservationByTrialInstanceNo(final Integer trialInstanceNo) {
		MeasurementRow trialObservation = null;

		if (this.trialObservations != null && !this.trialObservations.isEmpty()) {
			for (final MeasurementRow row : this.trialObservations) {
				final MeasurementData measurementData = row.getMeasurementData(TermId.TRIAL_INSTANCE_FACTOR.getId());
				if (Integer.parseInt(measurementData.getValue()) == trialInstanceNo.intValue()) {
					trialObservation = row;
					break;
				}
			}
		}

		return trialObservation;
	}

	/**
	 * @param trialObservations the trialObservations to set
	 */
	public void setTrialObservations(final List<MeasurementRow> trialObservations) {
		this.trialObservations = trialObservations;
	}

	public MeasurementRow getTrialObservation(final long locationId) {
		if (this.trialObservations != null) {
			for (final MeasurementRow row : this.trialObservations) {
				if (row.getLocationId() == locationId) {
					return row;
				}
			}
		}
		return null;
	}

	public List<MeasurementRow> getSortedTrialObservations() {
		if (this.trialObservations != null) {
			final List<MeasurementRow> rows = new ArrayList<>();
			final Map<Long, List<MeasurementRow>> map = this.segregateByTrialInstances();
			final List<Long> keys = new ArrayList<>(map.keySet());
			Collections.sort(keys);
			for (final Long key : keys) {
				rows.addAll(map.get(key));
			}

			return rows;
		}
		return new ArrayList<>();
	}

	public void updateTrialObservationsWithReferenceList(final List<List<ValueReference>> trialList) {
		// assumes rows are in the same order and size
		if (trialList != null && !trialList.isEmpty() && this.trialObservations != null
				&& this.trialObservations.size() == trialList.size()) {

			int i = 0;
			for (final List<ValueReference> trialRow : trialList) {
				final List<MeasurementData> dataList = this.trialObservations.get(i).getDataList();

				for (final ValueReference trialCell : trialRow) {
					final MeasurementData data = this.getMeasurementDataById(dataList, trialCell.getId());
					if (data != null) {
						data.setValue(trialCell.getName());
					}
				}
				i++;
			}
		}
	}

	public MeasurementData getMeasurementDataById(final List<MeasurementData> data, final int id) {
		if (data != null && !data.isEmpty()) {
			for (final MeasurementData cell : data) {
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

	public void setTreatmentFactors(final List<TreatmentVariable> treatmentFactors) {
		this.treatmentFactors = treatmentFactors;
	}

	public List<MeasurementRow> getExportArrangedObservations() {
		return this.exportArrangedObservations;
	}

	public void setExportArrangedObservations(final List<MeasurementRow> exportArrangedObservations) {
		this.exportArrangedObservations = exportArrangedObservations;
	}

	public String getStudyName() {
		return this.studyDetails.getStudyName();
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
	public void setOriginalObservations(final List<MeasurementRow> originalObservations) {
		this.originalObservations = originalObservations;
	}

	public Integer getImportType() {
		return this.importType;
	}

	public void setImportType(final Integer importType) {
		this.importType = importType;
	}

	public Integer getMeansDatasetId() {
		return this.meansDatasetId;
	}

	public void setMeansDatasetId(final Integer meansDatasetId) {
		this.meansDatasetId = meansDatasetId;
	}

	public List<StandardVariable> getExpDesignVariables() {
		return this.expDesignVariables;
	}

	public void setExpDesignVariables(final List<StandardVariable> expDesignVariables) {
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
	public void setExperimentalDesignVariables(final List<MeasurementVariable> list) {
		this.experimentalDesignVariables = new ExperimentalDesignVariable(list);
	}

	public List<MeasurementVariable> getImportConditionsCopy() {
		return this.importConditionsCopy;
	}

	public void setImportConditionsCopy(final List<MeasurementVariable> importConditionsCopy) {
		this.importConditionsCopy = importConditionsCopy;
	}

	public List<MeasurementVariable> getImportConstantsCopy() {
		return this.importConstantsCopy;
	}

	public void setImportConstantsCopy(final List<MeasurementVariable> importConstantsCopy) {
		this.importConstantsCopy = importConstantsCopy;
	}

	public List<MeasurementRow> getImportTrialObservationsCopy() {
		return this.importTrialObservationsCopy;
	}

	public void setImportTrialObservationsCopy(final List<MeasurementRow> importTrialObservationsCopy) {
		this.importTrialObservationsCopy = importTrialObservationsCopy;
	}

	public void resetTrialConditions() {
		this.trialConditions = null;
	}

	public boolean hasExistingDataOverwrite() {
		return this.hasExistingDataOverwrite;
	}

	public void setHasExistingDataOverwrite(final boolean hasExistingDataOverwrite) {
		this.hasExistingDataOverwrite = hasExistingDataOverwrite;
	}

	public boolean hasOutOfBoundsData() {
		return this.hasOutOfBoundsData;
	}

	public void setHasOutOfBoundsData(final boolean hasOutOfBoundsData) {
		this.hasOutOfBoundsData = hasOutOfBoundsData;
	}

	public List<Integer> getColumnOrderedLists() {
		return this.columnOrderedLists;
	}

	public void setColumnOrderedLists(final List<Integer> columnOrderedLists) {
		this.columnOrderedLists = columnOrderedLists;
	}

	public boolean hasExistingExperimentalDesign() {
		final ExperimentalDesignVariable expDesignVar = this.getExperimentalDesignVariables();
		return expDesignVar != null && expDesignVar.getExperimentalDesign() != null
				&& expDesignVar.getExperimentalDesign().getValue() != null;
	}

	public MeasurementVariable findConditionById(final int conditionId) {
		if (this.getConditions() != null) {
			for (final MeasurementVariable mv : this.getConditions()) {
				if (mv.getTermId() == conditionId) {
					return mv;
				}
			}
		}
		return null;
	}

	public Integer getPlotsIdNotfound() {
		return plotsIdNotfound;
	}

	public void setPlotsIdNotfound(final Integer plotsIdNotfound) {
		this.plotsIdNotfound = plotsIdNotfound;
	}

	public List<MeasurementVariable> getEntryDetails() {
		if (this.entryDetails == null) {
			this.entryDetails = this.getEntryDetailVariables(this.factors);
		}
		return this.entryDetails;
	}

	public void setEntryDetails(final List<MeasurementVariable> entryDetails) {
		this.entryDetails = entryDetails;
	}
}
