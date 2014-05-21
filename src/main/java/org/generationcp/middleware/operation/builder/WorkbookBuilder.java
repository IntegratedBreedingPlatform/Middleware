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
package org.generationcp.middleware.operation.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.etl.TreatmentVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;

public class WorkbookBuilder extends Builder {
	
	private Map<String, String> labelMap = new HashMap<String, String> ();
	
	public WorkbookBuilder(HibernateSessionProvider sessionProviderForLocal,
			                   HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	} 
	
	public Workbook create(int id) throws MiddlewareQueryException {
		return create(id, StudyType.N);
	}
	
	public Workbook create(int id, StudyType studyType) throws MiddlewareQueryException {
		boolean isTrial = studyType == StudyType.T;
		
		Workbook workbook = new Workbook();
		
		/*
                 * 1. Get the dataset id
                 * 2. Count total no. of experiments of the dataset
                 * 3. getExperiments
                 * 4. Per experiment, transform it to MeasurementRow
                 *  a. MeasurementRow (list of MeasurementData) 
                 *  b. MeasurementData label (Experiment > VariableList > Variable > localName), 
                 *  value (Experiment > VariableList > Variable), 
                 *  datatype (Experiment > VariableList > Variable > VariableType > StandardVariable), 
                 *  iseditable (true for variates, else, false)
                 *  
                 * */
		
		StudyDetails studyDetails = getStudyDataManager().getStudyDetails(Database.LOCAL, studyType, id);
		Study study = getStudyBuilder().createStudy(id);
		
		int dataSetId = getMeasurementDataSetId(id, studyDetails.getStudyName());
		workbook.setMeasurementDatesetId(dataSetId);
		
		long expCount = getStudyDataManager().countExperiments(dataSetId);
		
		List<Experiment> experiments = getStudyDataManager().getExperiments(dataSetId, 0, (int)expCount);
		VariableTypeList variables = getDataSetBuilder().getVariableTypes(dataSetId);
		
		VariableList conditionVariables = null, constantVariables = null;
		if (isTrial) {
			conditionVariables = new VariableList();
			conditionVariables.addAll(study.getConditions());
			conditionVariables.addAll(getSingleRowOfEmptyTrialVariables(workbook, study.getId(), dataSetId));
			
			constantVariables = new VariableList();
			constantVariables.addAll(study.getConstants());
			constantVariables.addAll(getTrialConstants(workbook.getTrialDatasetId()));
			
			variables = removeTrialDatasetVariables(variables, conditionVariables, constantVariables);
		}
		else {
			getSingleRowOfEmptyTrialVariables(workbook, study.getId(), dataSetId);
			conditionVariables = study.getConditions();
			constantVariables = study.getConstants();
		}
		List<MeasurementVariable> conditions = buildStudyMeasurementVariables(conditionVariables, true);
		List<MeasurementVariable> factors = buildFactors(experiments, isTrial);
		List<MeasurementVariable> constants = buildStudyMeasurementVariables(constantVariables, false);
		List<MeasurementVariable> variates = buildVariates(variables, constants); //buildVariates(experiments);
		
		//set possible values of breeding method
		for (MeasurementVariable variable : variates) {
		    if (variable.getTermId() == TermId.BREEDING_METHOD_VARIATE.getId()) {
		        variable.setPossibleValues(getAllBreedingMethods());
		        break;
		    }
		}
		
		List<MeasurementRow> observations = buildObservations(experiments, variables.getVariates(), factors, variates, isTrial);
		List<TreatmentVariable> treatmentFactors = buildTreatmentFactors(variables);
		
		workbook.setStudyDetails(studyDetails);
		workbook.setFactors(factors);
		workbook.setVariates(variates);
		workbook.setConditions(conditions);
		workbook.setConstants(constants);
		workbook.setObservations(observations);
		workbook.setTreatmentFactors(treatmentFactors);
		
		//if (isTrial) {
		List<MeasurementRow> trialObservations = buildTrialObservations(workbook.getTrialDatasetId(), workbook.getTrialConditions(), workbook.getTrialConstants());
		workbook.setTrialObservations(trialObservations);
		//}
		
		return workbook;
	}
	
	public Workbook createStudyVariableSettings(int id, boolean isNursery) throws MiddlewareQueryException {
            Workbook workbook = new Workbook();
            Study study = getStudyBuilder().createStudy(id);
            int dataSetId = 0;
            //get observation dataset
            if (dataSetId == 0) {
                List<DatasetReference> datasetRefList = getStudyDataManager().getDatasetReferences(id);
                if (datasetRefList != null) {
                	StudyType studyType = StudyType.N;
                	if(!isNursery)
                		studyType = StudyType.T;
                    StudyDetails studyDetails = getStudyDataManager().getStudyDetails(Database.LOCAL, studyType, id);
                    for (DatasetReference datasetRef : datasetRefList) {
                        if (datasetRef.getName().equals("MEASUREMENT EFEC_" + studyDetails.getStudyName()) || 
                                datasetRef.getName().equals("MEASUREMENT EFECT_" + studyDetails.getStudyName())) {
                            dataSetId = datasetRef.getId();
                        }
                    }
                }
            }
            
            //if dataset is not found, get dataset with Plot Data type
            if (dataSetId == 0) {
                dataSetId = getStudyDataManager().findOneDataSetByType(id, DataSetType.PLOT_DATA).getId();
            }
            
            VariableTypeList variables = getDataSetBuilder().getVariableTypes(dataSetId);
            
            List<MeasurementVariable> factors = buildFactors(variables);
            List<MeasurementVariable> variates = buildVariates(variables);
            List<MeasurementVariable> conditions = buildStudyMeasurementVariables(study.getConditions(), true);
            List<MeasurementVariable> constants = buildStudyMeasurementVariables(study.getConstants(), false);
            List<TreatmentVariable> treatmentFactors = buildTreatmentFactors(variables);
            List<ProjectProperty> projectProperties = getDataSetBuilder().getTrialDataset(id, dataSetId).getProperties();
            
            for (ProjectProperty projectProperty : projectProperties) {
                if (projectProperty.getTypeId().equals(TermId.STANDARD_VARIABLE.getId())) {
                    StandardVariable stdVariable = getStandardVariableBuilder().create(Integer.parseInt(projectProperty.getValue()));
                    if (stdVariable.getStoredIn().getId() == TermId.TRIAL_ENVIRONMENT_EXPERIMENT.getId()) {
                        String label = getLabelOfStoredIn(stdVariable.getStoredIn().getId());
                        
                        Double minRange = null, maxRange = null;
                        if (stdVariable.getConstraints() != null) {
                        	minRange = stdVariable.getConstraints().getMaxValue();
                        	maxRange = stdVariable.getConstraints().getMaxValue();
                        }
                        
                        MeasurementVariable measurementVariable = new MeasurementVariable(stdVariable.getId(), getLocalName(projectProperty.getRank(), projectProperties),//projectProperty.getValue(), 
                                stdVariable.getDescription(), stdVariable.getScale().getName(), stdVariable.getMethod().getName(),
                                stdVariable.getProperty().getName(), stdVariable.getDataType().getName(), 
                                getStudyDataManager().getGeolocationPropValue(Database.LOCAL, stdVariable.getId(), id), 
                                label, minRange, maxRange);
                        measurementVariable.setStoredIn(stdVariable.getStoredIn().getId());
                        measurementVariable.setFactor(true);
                        measurementVariable.setDataTypeId(stdVariable.getDataType().getId());
                        
                        conditions.add(measurementVariable);
                    }
                }
            }
            
            workbook.setFactors(factors);
            workbook.setVariates(variates);
            workbook.setConditions(conditions);
            workbook.setConstants(constants);
            workbook.setTreatmentFactors(treatmentFactors);
            return workbook;
	}
	
	private List<MeasurementRow> buildObservations(List<Experiment> experiments, VariableTypeList variateTypes,
			List<MeasurementVariable> factorList, List<MeasurementVariable> variateList, boolean isTrial) {
		
	    List<MeasurementRow> observations = new ArrayList<MeasurementRow>();
	    for (Experiment experiment : experiments) {
	   
	        int experimentId = experiment.getId();
	        VariableList factors = experiment.getFactors();
	        VariableList variates = getCompleteVariatesInExperiment(experiment, variateTypes); //experiment.getVariates();
	        List<MeasurementData> measurementDataList = new ArrayList<MeasurementData>();
	        
	        for (Variable variable : factors.getVariables()) {
	        	
	        	if (isTrial && 
	        			variable.getVariableType().getStandardVariable().getId() == TermId.TRIAL_INSTANCE_FACTOR.getId()
	        			|| !PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().contains(getLabelOfStoredIn(variable.getVariableType().getStandardVariable().getStoredIn().getId()))) {
	        		
	            	MeasurementData measurementData = null;
	            	if (variable.getVariableType().getStandardVariable().getDataType().getId() == TermId.CATEGORICAL_VARIABLE.getId()) {
	            		Integer id = variable.getValue() != null && NumberUtils.isNumber(variable.getValue()) ? Integer.valueOf(variable.getValue()) : null;
                        measurementData = new MeasurementData(variable.getVariableType().getLocalName(), 
                        		variable.getDisplayValue(), false, 
                                getDataType(variable.getVariableType().getStandardVariable().getDataType().getId()),
                                id,
                                getMeasurementVariableByName(variable.getVariableType().getLocalName(), factorList));
	            	}
	            	else {
                        measurementData = new MeasurementData(variable.getVariableType().getLocalName(), 
                                variable.getValue(), false, 
                                getDataType(variable.getVariableType().getStandardVariable().getDataType().getId()),
                                getMeasurementVariableByName(variable.getVariableType().getLocalName(), factorList));
	            	}
                    
	            	measurementDataList.add(measurementData);
	            }
	        }
	        
	        for (Variable variable : variates.getVariables()) {
                    MeasurementData measurementData = new MeasurementData(variable.getVariableType().getLocalName(), 
                            variable.getValue(), true,  
                            getDataType(variable.getVariableType().getStandardVariable().getDataType().getId()),
                            getMeasurementVariableByName(variable.getVariableType().getLocalName(), variateList));
                    measurementData.setPhenotypeId(variable.getPhenotypeId());
                    measurementDataList.add(measurementData);
                }
	        
	        MeasurementRow measurementRow = new MeasurementRow(measurementDataList);
	        measurementRow.setExperimentId(experimentId);
	        measurementRow.setLocationId(experiment.getLocationId());
	        
	        observations.add(measurementRow);
	    }
	    
	    return observations;
	}
	
	private List<ValueReference> getAllBreedingMethods() throws MiddlewareQueryException{
            List<ValueReference> list = new ArrayList<ValueReference>();
            List<Method> methodList = getGermplasmDataManager().getAllMethods();
            
            Collections.sort(methodList, new Comparator<Method>(){

                    @Override
                    public int compare(Method o1, Method o2) {
                             String methodName1 = o1.getMname().toUpperCase();
                          String methodName2 = o2.getMname().toUpperCase();
             
                          //ascending order
                          return methodName1.compareTo(methodName2);
                    }
                    
            });
            
            if (methodList != null && !methodList.isEmpty()) {
                for (Method method : methodList) {
                    if (method != null) {
                        list.add(new ValueReference(method.getMid(), method.getMname(), method.getMname()));
                    }
                }
            }
            return list;
        }
	
	private String getDataType(int dataTypeId) {
	    //datatype ids: 1120, 1125, 1128, 1130
	    if (dataTypeId == TermId.CHARACTER_VARIABLE.getId() || dataTypeId == TermId.TIMESTAMP_VARIABLE.getId() || 
	            dataTypeId == TermId.CHARACTER_DBID_VARIABLE.getId() || dataTypeId == TermId.CATEGORICAL_VARIABLE.getId()) {
	        return "C";
	    } else {
	        return "N";
	    }
	}
	
	private String getLabelOfStoredIn(int storedIn) {
		String key = Integer.toString(storedIn);
    	String label = "";
    	
    	if(labelMap.containsKey(key)){
    		label = labelMap.get(key);
    	}else{
    		label = PhenotypicType.getPhenotypicTypeById(storedIn).getLabelList().get(0);
    		labelMap.put(key, label);
    	}
    	
         return label;   
        }
	
	private List<MeasurementVariable> buildStudyMeasurementVariables(VariableList variableList, boolean isFactor) {
		return getMeasurementVariableTransformer().transform(variableList, isFactor);
	}
	
	private List<MeasurementVariable> buildFactors(List<Experiment> experiments, boolean isTrial) {
	    List<MeasurementVariable> factors = new ArrayList<MeasurementVariable>();
	    VariableTypeList factorList = new VariableTypeList();
            for (Experiment experiment : experiments) {
                for (Variable variable : experiment.getFactors().getVariables()) {
                    if (isTrial && 
    	        			variable.getVariableType().getStandardVariable().getId() == TermId.TRIAL_INSTANCE_FACTOR.getId()
    	        			|| !PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().contains(getLabelOfStoredIn(variable.getVariableType().getStandardVariable().getStoredIn().getId()))) {
                        factorList.add(variable.getVariableType());
                    }
                }
                factors = getMeasurementVariableTransformer().transform(factorList, true);
                break;
            }
	    return factors;
	}
	
	private List<TreatmentVariable> buildTreatmentFactors(VariableTypeList variables) {
		List<TreatmentVariable> treatmentFactors = new ArrayList<TreatmentVariable>();
	    List<MeasurementVariable> factors = new ArrayList<MeasurementVariable>();
	    Map<String, VariableTypeList> treatmentMap = new HashMap<String, VariableTypeList>(); 
        if (variables != null && variables.getFactors() != null && !variables.getFactors().getVariableTypes().isEmpty()) {
            for (VariableType variable : variables.getFactors().getVariableTypes()) {
                if (variable.getStandardVariable().getStoredIn().getId() == TermId.TRIAL_DESIGN_INFO_STORAGE.getId()
                		&& variable.getTreatmentLabel() != null && !variable.getTreatmentLabel().isEmpty()) {
                	
                    VariableTypeList list = treatmentMap.get(variable.getTreatmentLabel());
                    if (list == null) {
                    	list = new VariableTypeList();
                    	treatmentMap.put(variable.getTreatmentLabel(), list);
                    }
                    list.add(variable);
                }
            }
            
            Set<String> keys = treatmentMap.keySet();
            for (String key : keys) {
            	factors = getMeasurementVariableTransformer().transform(treatmentMap.get(key), false);
            	TreatmentVariable treatment = new TreatmentVariable();
            	for (MeasurementVariable factor : factors) {
            		if (factor.getName().equals(key)) {
            			treatment.setLevelVariable(factor);
            		}
            		else {
            			treatment.setValueVariable(factor);
            		}
            	}
                treatmentFactors.add(treatment);
            }
        }
		
		return treatmentFactors;
	}
	
	private List<MeasurementVariable> buildFactors(VariableTypeList variables) {
            List<MeasurementVariable> factors = new ArrayList<MeasurementVariable>();
            VariableTypeList factorList = new VariableTypeList();
            if (variables != null && variables.getFactors() != null && !variables.getFactors().getVariableTypes().isEmpty()) {
                for (VariableType variable : variables.getFactors().getVariableTypes()) {
                    if (!PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().contains(getLabelOfStoredIn(variable.getStandardVariable().getStoredIn().getId()))
                    		&& (variable.getTreatmentLabel() == null || variable.getTreatmentLabel().isEmpty())) {
                    	
                        factorList.add(variable);
                    }
                }
                factors = getMeasurementVariableTransformer().transform(factorList, false);
            }
            
            return factors;
        }
	
	private List<MeasurementVariable> buildVariates(List<Experiment> experiments) {
	    List<MeasurementVariable> variates = new ArrayList<MeasurementVariable>();
            for (Experiment experiment : experiments) {                
                variates = getMeasurementVariableTransformer().transform(experiment.getVariates().getVariableTypes(), false);
                break;
            }
            return variates;
        }

	private List<MeasurementVariable> buildVariates(VariableTypeList variables) {
		return buildVariates(variables, null);
	}
	private List<MeasurementVariable> buildVariates(VariableTypeList variables, List<MeasurementVariable> constants) { 
	    List<MeasurementVariable> variates = new ArrayList<MeasurementVariable>();
	    VariableTypeList filteredVariables = null;
	    
	    if (variables != null && variables.getVariates() != null && !variables.getVariates().getVariableTypes().isEmpty()) {
		    List<String> constantHeaders = new ArrayList<String>();
		    if (constants != null) {
			    for (MeasurementVariable constant : constants) {
			    	constantHeaders.add(constant.getName());
			    }
			    filteredVariables = new VariableTypeList();
			    for (VariableType variable : variables.getVariableTypes()) {
			    	if (!constantHeaders.contains(variable.getLocalName())) {
			    		filteredVariables.add(variable);
			    	}
			    }
		    }
		    else {
		    	filteredVariables = variables;
		    }
		    
	    
		    if (filteredVariables.size() > 0) {
		    	variates = getMeasurementVariableTransformer().transform(filteredVariables.getVariates(), false);
		    }
	    }
	    
	    return variates;
	}
	
	private VariableList getCompleteVariatesInExperiment(Experiment experiment, VariableTypeList variateTypes) {
		VariableList vlist = new VariableList();
		
		for (VariableType vType : variateTypes.getVariableTypes()) {
			boolean found = false;
			/*
			for (Variable v : experiment.getVariates().getVariables()) {
				if (v.getVariableType().getId() == vType.getId()) {
					vlist.add(v);
					found = true;
					break;
				}
			}
			*/
			//added for optimization
			String key = Integer.toString(vType.getId());
			Variable var = experiment.getVariatesMap().get(key);
			if(var != null){
				vlist.add(var);
				found = true;
			}
			if (!found) {
				vlist.add(new Variable(vType, (String) null));
			}
		}
		
		return vlist;
	}
	
	private MeasurementVariable getMeasurementVariableByName(String name, List<MeasurementVariable> list) {
		MeasurementVariable var = null;
		for (MeasurementVariable variable : list) {
			if (variable.getName().equalsIgnoreCase(name)) {
				return variable;
			}
		}
		return var;
	}
	
	private String getLocalName(int rank, List<ProjectProperty> properties) {
		for (ProjectProperty property : properties) {
			if (PhenotypicType.getAllTypeStorages().contains(property.getTypeId()) && rank == property.getRank()) {
				return property.getValue();
			}
		}
		return "";
	}
	
	private VariableList getSingleRowOfEmptyTrialVariables(Workbook workbook, int studyId, int measurementDatasetId) throws MiddlewareQueryException {
		DmsProject trialProject = getDataSetBuilder().getTrialDataset(studyId, measurementDatasetId);
		DataSet dataset = getDataSetBuilder().build(trialProject.getProjectId());
		VariableTypeList typeList = dataset.getFactorsByPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		VariableList list = new VariableList();
		for (VariableType type : typeList.getVariableTypes()) {
			list.add(new Variable(type, (String) null));
		}
		workbook.setTrialDatasetId(dataset.getId());
		return list;
	}
	
	private VariableList getTrialConstants(int trialDatasetId) throws MiddlewareQueryException {
		DataSet dataset = getDataSetBuilder().build(trialDatasetId);
		VariableTypeList typeList = dataset.getVariableTypes().getVariates();
		
		VariableList list = new VariableList();
		for (VariableType type : typeList.getVariableTypes()) {
			list.add(new Variable(type, (String) null));
		}
		return list;
	}

	private List<MeasurementRow> buildTrialObservations(int trialDatasetId, List<MeasurementVariable> factorList, List<MeasurementVariable> variateList)
	throws MiddlewareQueryException {
		
		int totalRows = (int) getStudyDataManager().countExperiments(trialDatasetId);
		List<Experiment> experiments = getStudyDataManager().getExperiments(trialDatasetId, 0, totalRows);
		
		List<MeasurementRow> rows = new ArrayList<MeasurementRow>();
		if (experiments != null) {
			for (Experiment experiment : experiments) {
				List<MeasurementData> dataList = new ArrayList<MeasurementData>();
				for (Variable variable : experiment.getFactors().getVariables()) {
					MeasurementData measurementData = null;
	            	if (variable.getVariableType().getStandardVariable().getDataType().getId() == TermId.CATEGORICAL_VARIABLE.getId()) {
	            		Integer id = variable.getValue() != null && NumberUtils.isNumber(variable.getValue()) ? Integer.valueOf(variable.getValue()) : null;
                        measurementData = new MeasurementData(variable.getVariableType().getLocalName(), 
                        		variable.getDisplayValue(), false, 
                                getDataType(variable.getVariableType().getStandardVariable().getDataType().getId()),
                                id,
                                getMeasurementVariableByName(variable.getVariableType().getLocalName(), factorList));
	            	}
	            	else {
                        measurementData = new MeasurementData(variable.getVariableType().getLocalName(), 
                                variable.getValue(), false, 
                                getDataType(variable.getVariableType().getStandardVariable().getDataType().getId()),
                                getMeasurementVariableByName(variable.getVariableType().getLocalName(), factorList));
	            	}
	            	dataList.add(measurementData);
				}
		        for (Variable variable : experiment.getVariates().getVariables()) {
                    MeasurementData measurementData = new MeasurementData(variable.getVariableType().getLocalName(), 
                            variable.getValue(), true,  
                            getDataType(variable.getVariableType().getStandardVariable().getDataType().getId()),
                            variable.getVariableType().getStandardVariable().getDataType().getId(),
                            getMeasurementVariableByName(variable.getVariableType().getLocalName(), variateList));
                    measurementData.setPhenotypeId(variable.getPhenotypeId());
	            	dataList.add(measurementData);
                }
	        
				MeasurementRow row = new MeasurementRow(dataList);
				row.setExperimentId(experiment.getId());
				row.setLocationId(experiment.getLocationId());
				rows.add(row);
			}
		}
		return rows;
	}
	
	private VariableTypeList removeTrialDatasetVariables(VariableTypeList variables, VariableList conditions, VariableList constants) {
		List<String> trialList = new ArrayList<String>();
		if (conditions != null && conditions.size() > 0) {
			for (Variable condition : conditions.getVariables()) {
				trialList.add(condition.getVariableType().getLocalName());
			}
		}
		if (constants != null && constants.size() > 0) {
			for (Variable constant : constants.getVariables()) {
				trialList.add(constant.getVariableType().getLocalName());
			}
		}
		
		VariableTypeList list = new VariableTypeList();
		if (variables != null) {
			for (VariableType type : variables.getVariableTypes()) {
				if (!trialList.contains(type.getLocalName())) {
					list.add(type);
				}
			}
		}
		return list;
	}
	
	public int getMeasurementDataSetId(int studyId, String studyName) throws MiddlewareQueryException {
		List<DatasetReference> datasetRefList = getStudyDataManager().getDatasetReferences(studyId);
		if (datasetRefList != null) {
		    for (DatasetReference datasetRef : datasetRefList) {
		        if (datasetRef.getName().equals("MEASUREMENT EFEC_" + studyName) || 
		                datasetRef.getName().equals("MEASUREMENT EFECT_" + studyName)) {
		            return datasetRef.getId();
		        }
		    }
		}
		//if not found in the list using the name, get dataset with Plot Data type
	    return getStudyDataManager().findOneDataSetByType(studyId, DataSetType.PLOT_DATA).getId();
	}
}
