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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.dms.ProjectProperty;

public class WorkbookBuilder extends Builder {
	
	private Map<String, String> labelMap = new HashMap();
	
	public WorkbookBuilder(HibernateSessionProvider sessionProviderForLocal,
			                   HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	} 
	
	public Workbook create(int id) throws MiddlewareQueryException {
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
		
		StudyDetails studyDetails = getStudyDataManager().getStudyDetails(Database.LOCAL, StudyType.N, id);
		Study study = getStudyBuilder().createStudy(id);
		
		int dataSetId = 0;
		
		//get observation dataset
		List<DatasetReference> datasetRefList = getStudyDataManager().getDatasetReferences(id);
		if (datasetRefList != null) {
		    for (DatasetReference datasetRef : datasetRefList) {
		        if (datasetRef.getName().equals("MEASUREMENT EFEC_" + studyDetails.getStudyName()) || 
		                datasetRef.getName().equals("MEASUREMENT EFECT_" + studyDetails.getStudyName())) {
		            dataSetId = datasetRef.getId();
		        }
		    }
		}
		
		//if not found in the list using the name, get dataset with Plot Data type
		if (dataSetId == 0) {
		    dataSetId = getStudyDataManager().findOneDataSetByType(id, DataSetType.PLOT_DATA).getId();
		}
		
		long expCount = getStudyDataManager().countExperiments(dataSetId);
		
		List<Experiment> experiments = getStudyDataManager().getExperiments(dataSetId, 0, (int)expCount);
		VariableTypeList variables = getDataSetBuilder().getVariableTypes(dataSetId);
		
		List<MeasurementVariable> conditions = buildStudyMeasurementVariables(study.getConditions(), true);
		List<MeasurementVariable> factors = buildFactors(experiments);
		List<MeasurementVariable> constants = buildStudyMeasurementVariables(study.getConstants(), false);
		List<MeasurementVariable> variates = buildVariates(variables); //buildVariates(experiments);
		List<MeasurementRow> observations = buildObservations(experiments, variables.getVariates());
		
		workbook.setStudyDetails(studyDetails);
		workbook.setFactors(factors);
		workbook.setVariates(variates);
		workbook.setConditions(conditions);
		workbook.setConstants(constants);
		workbook.setObservations(observations);
		
		return workbook;
	}
	
	public Workbook createNurseryVariableSettings(int id) throws MiddlewareQueryException {
            Workbook workbook = new Workbook();
            Study study = getStudyBuilder().createStudy(id);
            int dataSetId = 0;
            //get observation dataset
            if (dataSetId == 0) {
                List<DatasetReference> datasetRefList = getStudyDataManager().getDatasetReferences(id);
                if (datasetRefList != null) {
                    StudyDetails studyDetails = getStudyDataManager().getStudyDetails(Database.LOCAL, StudyType.N, id);
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
            List<ProjectProperty> projectProperties = getDataSetBuilder().getTrialDataset(id, dataSetId).getProperties();
            
            for (ProjectProperty projectProperty : projectProperties) {
                if (projectProperty.getTypeId().equals(TermId.STANDARD_VARIABLE.getId())) {
                    StandardVariable stdVariable = getStandardVariableBuilder().create(Integer.parseInt(projectProperty.getValue()));
                    if (stdVariable.getStoredIn().getId() == TermId.TRIAL_ENVIRONMENT_EXPERIMENT.getId()) {
                        String label = getLabelOfStoredIn(stdVariable.getStoredIn().getId());
                        
                        MeasurementVariable measurementVariable = new MeasurementVariable(stdVariable.getId(), projectProperty.getValue(), 
                                stdVariable.getDescription(), stdVariable.getScale().getName(), stdVariable.getMethod().getName(),
                                stdVariable.getProperty().getName(), stdVariable.getDataType().getName(), 
                                getStudyDataManager().getGeolocationPropValue(Database.LOCAL, stdVariable.getId(), id), 
                                label);
                        measurementVariable.setStoredIn(stdVariable.getStoredIn().getId());
                        measurementVariable.setFactor(true);
                        
                        conditions.add(measurementVariable);
                    }
                }
            }
            
            workbook.setFactors(factors);
            workbook.setVariates(variates);
            workbook.setConditions(conditions);
            return workbook;
	}
	
	private List<MeasurementRow> buildObservations(List<Experiment> experiments, VariableTypeList variateTypes) {
	    List<MeasurementRow> observations = new ArrayList<MeasurementRow>();
	   
	    for (Experiment experiment : experiments) {
	        int experimentId = experiment.getId();
	        VariableList factors = experiment.getFactors();
	        VariableList variates = getCompleteVariatesInExperiment(experiment, variateTypes); //experiment.getVariates();
	        List<MeasurementData> measurementDataList = new ArrayList<MeasurementData>();
	        
	        for (Variable variable : factors.getVariables()) {
	        	
	            if (!PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().contains(getLabelOfStoredIn(variable.getVariableType().getStandardVariable().getStoredIn().getId()))) {
                        MeasurementData measurementData = new MeasurementData(variable.getVariableType().getLocalName(), 
                                variable.getValue(), false, 
                                getDataType(variable.getVariableType().getStandardVariable().getDataType().getId()));
                        measurementDataList.add(measurementData);
                    }
	        }
	        
	        for (Variable variable : variates.getVariables()) {
                    MeasurementData measurementData = new MeasurementData(variable.getVariableType().getLocalName(), 
                            variable.getValue(), true,  
                            getDataType(variable.getVariableType().getStandardVariable().getDataType().getId()));
                    measurementData.setPhenotypeId(variable.getPhenotypeId());
                    measurementDataList.add(measurementData);
                }
	        
	        MeasurementRow measurementRow = new MeasurementRow(measurementDataList);
	        measurementRow.setExperimentId(experimentId);
	        
	        observations.add(measurementRow);
	    }
	    
	    return observations;
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
	
	private List<MeasurementVariable> buildFactors(List<Experiment> experiments) {
	    List<MeasurementVariable> factors = new ArrayList<MeasurementVariable>();
	    VariableTypeList factorList = new VariableTypeList();
            for (Experiment experiment : experiments) {
                for (Variable variable : experiment.getFactors().getVariables()) {
                    if (!PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().contains(getLabelOfStoredIn(variable.getVariableType().getStandardVariable().getStoredIn().getId()))) {
                        factorList.add(variable.getVariableType());
                    }
                }
                factors = getMeasurementVariableTransformer().transform(factorList, true);
                break;
            }
	    return factors;
	}
	
	private List<MeasurementVariable> buildFactors(VariableTypeList variables) {
            List<MeasurementVariable> factors = new ArrayList<MeasurementVariable>();
            VariableTypeList factorList = new VariableTypeList();
            if (variables != null && variables.getFactors() != null && !variables.getFactors().getVariableTypes().isEmpty()) {
                for (VariableType variable : variables.getFactors().getVariableTypes()) {
                    if (!PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().contains(getLabelOfStoredIn(variable.getStandardVariable().getStoredIn().getId()))) {
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
	    List<MeasurementVariable> variates = new ArrayList<MeasurementVariable>();
	    
	    if (variables != null && variables.getVariates() != null && !variables.getVariates().getVariableTypes().isEmpty()) {
	    	variates = getMeasurementVariableTransformer().transform(variables.getVariates(), false);
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
}
