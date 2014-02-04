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
import java.util.List;

import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
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

public class WorkbookBuilder extends Builder {

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
		int dataSetId = 0;
		
		//get observation dataset
		List<DatasetReference> datasetRefList = getStudyDataManager().getDatasetReferences(id);
		if (datasetRefList != null) {
		    for (DatasetReference datasetRef : datasetRefList) {
		        if (datasetRef.getName().equals("MEASUREMENT_EFEC_" + studyDetails.getStudyName())) {
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
		
		List<MeasurementVariable> factors = buildFactors(experiments);		
		List<MeasurementVariable> variates = buildVariates(experiments);
		List<MeasurementRow> observations = buildObservations(experiments);
		
		workbook.setStudyDetails(studyDetails);
		workbook.setFactors(factors);
		workbook.setVariates(variates);
		workbook.setConditions(new ArrayList<MeasurementVariable>());
		workbook.setConstants(new ArrayList<MeasurementVariable>());
		workbook.setObservations(observations);
		
		return workbook;
	}
	
	private List<MeasurementRow> buildObservations(List<Experiment> experiments) {
	    List<MeasurementRow> observations = new ArrayList<MeasurementRow>();
	    
	    for (Experiment experiment : experiments) {
	        int experimentId = experiment.getId();
	        VariableList factors = experiment.getFactors();
	        VariableList variates = experiment.getVariates();
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
            return PhenotypicType.getPhenotypicTypeById(storedIn).getLabelList().get(0);
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
	
	private List<MeasurementVariable> buildVariates(List<Experiment> experiments) {
	    List<MeasurementVariable> variates = new ArrayList<MeasurementVariable>();
            for (Experiment experiment : experiments) {                
                variates = getMeasurementVariableTransformer().transform(experiment.getVariates().getVariableTypes(), false);
                break;
            }
            return variates;
        }
}
