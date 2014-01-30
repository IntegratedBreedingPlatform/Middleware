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
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.StudyType;
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
                 * 1. findOneDataSetByType get datasetid only
                 * 2. countExperiments
                 * 3. getExperiments
                 * 4. per experiment, transform it to MeasurementRow
                 *  a. MeasurementRow stockid, locationid, list of MeasurementData
                 *  b. MeasurementData label (???), 
                 *  value (Experiment > VariableList > Variable), 
                 *  datatype (Experiment > VariableList > Variable > VariableType > StandardVariable), 
                 *  iseditable (true for variates, else, false)
                 *  
                 *  to do: add storedin and phenotypicId
                 * */
		
		StudyDetails studyDetails = getStudyDataManager().getStudyDetails(Database.LOCAL, StudyType.N, id);
		
		//to do: change datasettype
		int dataSetId = getStudyDataManager().findOneDataSetByType(id, DataSetType.PLOT_DATA).getId();
		long expCount = getStudyDataManager().countExperiments(dataSetId);
		
		List<Experiment> experiments = getStudyDataManager().getExperiments(dataSetId, 1, (int)expCount);
		
		List<MeasurementRow> observations = buildObservations(experiments);
		List<MeasurementVariable> factors = buildFactors(experiments);		
		List<MeasurementVariable> variates = buildVariates(experiments);
		
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
	            MeasurementData measurementData = new MeasurementData(variable.getVariableType().getLocalName(), 
	                    variable.getValue(), false, 
	                    variable.getVariableType().getStandardVariable().getDataType().getName());
	            measurementDataList.add(measurementData);
	        }
	        
	        for (Variable variable : variates.getVariables()) {
                    MeasurementData measurementData = new MeasurementData(variable.getVariableType().getLocalName(), 
                            variable.getValue(), false, 
                            variable.getVariableType().getStandardVariable().getDataType().getName());
                    measurementData.setPhenotypeId(variable.getPhenotypeId());
                    measurementDataList.add(measurementData);
                }
	        
	        MeasurementRow measurementRow = new MeasurementRow(measurementDataList);
	        measurementRow.setExperimentId(experimentId);
	        
	        observations.add(measurementRow);
	    }
	    
	    return observations;
	}
	
	private List<MeasurementVariable> buildFactors(List<Experiment> experiments) {
	    List<MeasurementVariable> factors = new ArrayList<MeasurementVariable>();
	    for (Experiment experiment : experiments) {                
                factors = getMeasurementVariableTransformer().transform(experiment.getFactors().getVariableTypes(), true);
                break;
            }
	    return factors;
	}
	
	private List<MeasurementVariable> buildVariates(List<Experiment> experiments) {
	    List<MeasurementVariable> variates = new ArrayList<MeasurementVariable>();
            for (Experiment experiment : experiments) {                
                variates = getMeasurementVariableTransformer().transform(experiment.getVariates().getVariableTypes(), true);
                break;
            }
            return variates;
        }
}
