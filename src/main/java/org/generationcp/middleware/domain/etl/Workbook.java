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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.domain.dms.PhenotypicType;
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
	private List<MeasurementRow> exportArrangedObservations; //for exporting only
	
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
	private List<TreatmentVariable> treatmentFactors; 
	private Map<String, ?> variableMap;
	
	private boolean isCheckFactorAddedOnly;
	
	private Integer totalNumberOfInstances;
	
	private Map<String, MeasurementVariable> measurementDatasetVariablesMap; //added for optimization
	
	private Integer studyId;
	private Integer trialDatasetId;
	private Integer measurementDatesetId;
	
	private List<MeasurementRow> trialObservations;

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
		treatmentFactors = null;
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
			if (variates != null && variates.size() > 0){
			    measurementDatasetVariables.addAll(variates);
			}
		}
		return measurementDatasetVariables;
	}
	
	public List<MeasurementVariable> getMeasurementDatasetVariablesView() {
		List<MeasurementVariable> list = new ArrayList<MeasurementVariable>();
		if (!isNursery()) {
            MeasurementVariable trialFactor = null;
            if (getTrialFactors() != null) {
            	for (MeasurementVariable var : getTrialFactors()) {
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
		list.addAll(getMeasurementDatasetVariables());
		return list;
	}

	public Map<String, MeasurementVariable> getMeasurementDatasetVariablesMap() {
		//we set the id to the map
		if(measurementDatasetVariablesMap == null){
			measurementDatasetVariablesMap = new HashMap<String, MeasurementVariable>();
			this.getMeasurementDatasetVariables();
			for(MeasurementVariable var: measurementDatasetVariables){
				measurementDatasetVariablesMap.put(Integer.toString(var.getTermId()), var);
			}
		}
		return measurementDatasetVariablesMap;
	}

	

	public List<MeasurementVariable> getNonTrialFactors() {
		if(nonTrialFactors==null || nonTrialFactors.isEmpty()) {
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
			List<MeasurementVariable> trialFactors = getTrialFactors();
			if(trialFactors!=null) {
				trialVariables.addAll(trialFactors);
			}
		}
		return trialVariables;
	}
	
	public List<MeasurementVariable> getTrialFactors() {
		if(trialFactors==null || trialFactors.isEmpty()) {
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
			if(studyConditions != null)
				list.addAll(studyConditions);
			if(studyConstants != null)
				list.addAll(studyConstants);
		} else {
			if(trialConditions == null && trialConstants == null) {
				trialConditions = getTrialConditions();
				trialConstants = getTrialConstants();
			}
			if(trialConditions != null)
				list.addAll(trialConditions);
			if(trialConstants != null)
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
        if (conditions != null) variableList.addAll(conditions);
        if (constants != null) variableList.addAll(constants);
        if (factors != null) variableList.addAll(factors);
        if (variates != null) variableList.addAll(variates);

        return variableList;
    }
    
    public List<MeasurementVariable> getNonVariateVariables() {
        List<MeasurementVariable> variableList = new ArrayList<MeasurementVariable>();
        if (conditions != null) variableList.addAll(conditions);
        if (factors != null) variableList.addAll(factors);

        return variableList;
    }
    
    public List<MeasurementVariable> getVariateVariables() {
        List<MeasurementVariable> variableList = new ArrayList<MeasurementVariable>();
        if (constants != null) variableList.addAll(constants);
        if (variates != null) variableList.addAll(variates);

        return variableList;
    }
	
//	public List<MeasurementVariable> getTrialFactors() {
//		return getVariables(conditions, false);
//	}

    public boolean isCheckFactorAddedOnly() {
        return isCheckFactorAddedOnly;
    }

    public void setCheckFactorAddedOnly(boolean isCheckFactorAddedOnly) {
        this.isCheckFactorAddedOnly = isCheckFactorAddedOnly;
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
        builder.append(", trialHeaders=");
        builder.append(trialHeaders);
        builder.append(", trialVariables=");
        builder.append(trialVariables);
        builder.append(", measurementDatasetVariables=");
        builder.append(measurementDatasetVariables);
        builder.append(", studyVariables=");
        builder.append(studyVariables);
        builder.append(", nonTrialFactors=");
        builder.append(nonTrialFactors);
        builder.append(", trialFactors=");
        builder.append(trialFactors);
        builder.append(", studyConditions=");
        builder.append(studyConditions);
        builder.append(", studyConstants=");
        builder.append(studyConstants);
        builder.append(", trialConditions=");
        builder.append(trialConditions);
        builder.append(", trialConstants=");
        builder.append(trialConstants);
        builder.append(", isCheckFactorAddedOnly=");
        builder.append(isCheckFactorAddedOnly);
        builder.append("]");
        return builder.toString();
    }
	
	

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((conditions == null) ? 0 : conditions.hashCode());
        result = prime * result + ((constants == null) ? 0 : constants.hashCode());
        result = prime * result + ((factors == null) ? 0 : factors.hashCode());
        result = prime * result + (isCheckFactorAddedOnly ? 1231 : 1237);
        result = prime * result + ((measurementDatasetVariables == null) ? 0 : measurementDatasetVariables.hashCode());
        result = prime * result + ((nonTrialFactors == null) ? 0 : nonTrialFactors.hashCode());
        result = prime * result + ((observations == null) ? 0 : observations.hashCode());
        result = prime * result + ((studyConditions == null) ? 0 : studyConditions.hashCode());
        result = prime * result + ((studyConstants == null) ? 0 : studyConstants.hashCode());
        result = prime * result + ((studyDetails == null) ? 0 : studyDetails.hashCode());
        result = prime * result + ((studyVariables == null) ? 0 : studyVariables.hashCode());
        result = prime * result + ((trialConditions == null) ? 0 : trialConditions.hashCode());
        result = prime * result + ((trialConstants == null) ? 0 : trialConstants.hashCode());
        result = prime * result + ((trialFactors == null) ? 0 : trialFactors.hashCode());
        result = prime * result + ((trialHeaders == null) ? 0 : trialHeaders.hashCode());
        result = prime * result + ((trialVariables == null) ? 0 : trialVariables.hashCode());
        result = prime * result + ((variates == null) ? 0 : variates.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Workbook other = (Workbook) obj;
        if (conditions == null) {
            if (other.conditions != null)
                return false;
        } else if (!conditions.equals(other.conditions))
            return false;
        if (constants == null) {
            if (other.constants != null)
                return false;
        } else if (!constants.equals(other.constants))
            return false;
        if (factors == null) {
            if (other.factors != null)
                return false;
        } else if (!factors.equals(other.factors))
            return false;
        if (isCheckFactorAddedOnly != other.isCheckFactorAddedOnly)
            return false;
        if (measurementDatasetVariables == null) {
            if (other.measurementDatasetVariables != null)
                return false;
        } else if (!measurementDatasetVariables.equals(other.measurementDatasetVariables))
            return false;
        if (nonTrialFactors == null) {
            if (other.nonTrialFactors != null)
                return false;
        } else if (!nonTrialFactors.equals(other.nonTrialFactors))
            return false;
        if (observations == null) {
            if (other.observations != null)
                return false;
        } else if (!observations.equals(other.observations))
            return false;
        if (studyConditions == null) {
            if (other.studyConditions != null)
                return false;
        } else if (!studyConditions.equals(other.studyConditions))
            return false;
        if (studyConstants == null) {
            if (other.studyConstants != null)
                return false;
        } else if (!studyConstants.equals(other.studyConstants))
            return false;
        if (studyDetails == null) {
            if (other.studyDetails != null)
                return false;
        } else if (!studyDetails.equals(other.studyDetails))
            return false;
        if (studyVariables == null) {
            if (other.studyVariables != null)
                return false;
        } else if (!studyVariables.equals(other.studyVariables))
            return false;
        if (trialConditions == null) {
            if (other.trialConditions != null)
                return false;
        } else if (!trialConditions.equals(other.trialConditions))
            return false;
        if (trialConstants == null) {
            if (other.trialConstants != null)
                return false;
        } else if (!trialConstants.equals(other.trialConstants))
            return false;
        if (trialFactors == null) {
            if (other.trialFactors != null)
                return false;
        } else if (!trialFactors.equals(other.trialFactors))
            return false;
        if (trialHeaders == null) {
            if (other.trialHeaders != null)
                return false;
        } else if (!trialHeaders.equals(other.trialHeaders))
            return false;
        if (trialVariables == null) {
            if (other.trialVariables != null)
                return false;
        } else if (!trialVariables.equals(other.trialVariables))
            return false;
        if (variates == null) {
            if (other.variates != null)
                return false;
        } else if (!variates.equals(other.variates))
            return false;
        return true;
    }

    public void print(int indent) {
		Debug.println(indent, "Workbook: ");
		
		if (studyDetails != null){
		    studyDetails.print(indent + 3);
		} else {
		    Debug.print(indent + 3, "StudyDetails: null");
		}
		
        if (conditions != null){
            Debug.println(indent + 3, "Conditions: ");
            for (MeasurementVariable variable : conditions){
                variable.print(indent + 6);
            }
        } else {
            Debug.println(indent + 3, "Conditions: null ");
        }
        
        if (factors != null){
            Debug.println(indent + 3, "Factors: ");
            for (MeasurementVariable variable : factors){
                variable.print(indent + 6);
            }
        } else {
            Debug.println(indent + 3, "Factors: Null");
        }
        
        if (constants != null){
    		Debug.println(indent + 3, "Constants: ");
    		for (MeasurementVariable variable : constants){
    			variable.print(indent + 6);
    		}
		} else {
            Debug.println(indent + 3, "Constants: Null");
        }
		
        if (variates != null){
    		Debug.println(indent + 3, "Variates: ");
    		for (MeasurementVariable variable : variates){
    			variable.print(indent + 6);
    		}
        } else {
            Debug.println(indent + 3, "Variates: Null");
        }
    
        if (observations != null){
    		
    		Debug.println(indent + 3, "Observations: ");
    		for (MeasurementRow row : observations){
    			row.print(indent + 6);
    		}
        } else {
            Debug.println(indent + 3, "Observations: Null");
        }
    
	}

	public Map<String, ?> getVariableMap() {
		return variableMap;
	}

	public void setVariableMap(Map<String, ?> variableMap) {
		this.variableMap = variableMap;
	}

    public void populateStudyAndDatasetIds(int studyId, int trialDatasetId, int measurementDatasetId) {
    	this.studyId = studyId;
    	this.trialDatasetId = trialDatasetId;
    	this.measurementDatesetId = measurementDatasetId;
    }

	public Integer getStudyId() {
		return studyId;
	}

	public void setStudyId(Integer studyId) {
		this.studyId = studyId;
	}

	public Integer getTrialDatasetId() {
		return trialDatasetId;
	}

	public void setTrialDatasetId(Integer trialDatasetId) {
		this.trialDatasetId = trialDatasetId;
	}

	public Integer getMeasurementDatesetId() {
		return measurementDatesetId;
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
			Map<Long, List<MeasurementRow>> map = segregateByTrialInstances();
			this.totalNumberOfInstances = map.size();
		}
		return this.totalNumberOfInstances;
	}

	/**
	 * @return the trialObservations
	 */
	public List<MeasurementRow> getTrialObservations() {
		return trialObservations;
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
			Map<Long, List<MeasurementRow>> map = segregateByTrialInstances();
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
		//assumes rows are in the same order and size
		if (trialList != null && !trialList.isEmpty() && this.trialObservations != null 
				&& this.trialObservations.size() == trialList.size()) {
			
			int i = 0;
			for (List<ValueReference> trialRow : trialList) {
				List<MeasurementData> dataList = this.trialObservations.get(i).getDataList();

				for (ValueReference trialCell : trialRow) {
					MeasurementData data = getMeasurementDataById(dataList, trialCell.getId());
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
		return treatmentFactors;
	}

	public void setTreatmentFactors(List<TreatmentVariable> treatmentFactors) {
		this.treatmentFactors = treatmentFactors;
	}
	
	public List<MeasurementRow> getExportArrangedObservations() {
		return exportArrangedObservations;
	}

	public void setExportArrangedObservations(
			List<MeasurementRow> exportArrangedObservations) {
		this.exportArrangedObservations = exportArrangedObservations;
	}
	
	public String getStudyName() {
		if (getStudyConditions() != null) {
			for (MeasurementVariable condition : getStudyConditions()) {
				if (condition.getTermId() == TermId.STUDY_NAME.getId()) {
					return condition.getValue();
				}
			}
		}
		return null;
	}
	
}
