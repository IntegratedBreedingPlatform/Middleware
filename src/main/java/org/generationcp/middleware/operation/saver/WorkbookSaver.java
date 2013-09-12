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
package org.generationcp.middleware.operation.saver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.DatasetValues;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.ExperimentValues;
import org.generationcp.middleware.domain.dms.StudyValues;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.util.TimerWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkbookSaver extends Saver {

    private static final Logger LOG = LoggerFactory.getLogger(WorkbookSaver.class);

    public WorkbookSaver(HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public int save(Workbook workbook) throws Exception {
        List<MeasurementVariable> trialMV = workbook.getTrialVariables();
		VariableTypeList trialVariables = getVariableTypeListTransformer().transform(trialMV);

		Collection<Integer> locationIds = createGeolocationsAndSetToObservations(workbook, trialMV, trialVariables);
		Integer studyLocationId = locationIds.size() == 1 ? locationIds.iterator().next() : null;

		int studyId = createStudy(workbook, studyLocationId);
		
   		createTrialDataset(workbook, studyId, locationIds, trialMV, trialVariables);
   		createMeasurementEffectDataset(workbook, studyId, trialVariables);
   		
   		return studyId;
	}
	
	private Collection<Integer> createGeolocationsAndSetToObservations(Workbook workbook, List<MeasurementVariable> trialMV, VariableTypeList trialVariables) 
	throws MiddlewareQueryException {
		Map<String, Integer> geolocationMap = new HashMap<String, Integer>();
		
		TimerWatch watch = new TimerWatch("transform trial environment", LOG);
        VariableList geolocation = getVariableListTransformer().transformTrialEnvironment(trialMV, trialVariables);
        String trialFactor = getTrialFactor(geolocation);
        Integer studyLocationId = null;
        
        if (trialFactor != null && !"".equals(trialFactor)) {
        	watch.restart("save 1 geolocation");
            studyLocationId = getGeolocationSaver().saveGeolocation(geolocation);
            geolocationMap.put(trialFactor, studyLocationId);
        }
        
        watch.restart("save multiple geolocations or set to observations(total)");
        TimerWatch rowWatch = new TimerWatch("for each row", LOG);
    	for(MeasurementRow row : workbook.getObservations()) {
    		if (studyLocationId == null) {
    			rowWatch.restart("--transform trial environment");
    			geolocation = getVariableListTransformer().transformTrialEnvironment(row, trialVariables);
    			trialFactor = getTrialFactor(geolocation);
    			if (trialFactor != null && !"".equals(trialFactor)) {
    				Integer locationId = geolocationMap.get(trialFactor);
    				if (locationId == null) {
    					rowWatch.restart("--save geolocation");
    					locationId = getGeolocationSaver().saveGeolocation(geolocation);
    					geolocationMap.put(trialFactor, locationId);
    				}
        			row.setLocationId(locationId);
    			}
    	    	rowWatch.stop();
    		} else {
    			row.setLocationId(studyLocationId);
    		}
        }
    	
    	watch.stop();
        return geolocationMap.values();
	}
	
	private String getTrialFactor(VariableList trialVariables) {
		for (Variable variable : trialVariables.getVariables()) {
			if (TermId.TRIAL_INSTANCE_STORAGE.getId() == variable.getVariableType().getStandardVariable().getStoredIn().getId()) {
				return variable.getValue();
			}
		}
		return null;
	}
	
	private String getStockFactor(VariableList stockVariables) {
		for (Variable variable : stockVariables.getVariables()) {
			if (TermId.ENTRY_NUMBER_STORAGE.getId() == variable.getVariableType().getStandardVariable().getStoredIn().getId()) {
				return variable.getValue();
			}
		}
		return null;
	}
	
	private String generateTrialDatasetName(String studyName) {
		return "TRIAL_" + studyName;
	}
	
	private String generateMeasurementEffectDatasetName(String studyName) {
		return "MEASUREMENT EFEC_" + studyName;
	}
	
	private List<ExperimentValues> createTrialExperimentValues(Collection<Integer> locationIds) {
		List<ExperimentValues> list = new ArrayList<ExperimentValues>();
		
		for (Integer locationId : locationIds) {
			ExperimentValues value = new ExperimentValues();
			value.setLocationId(locationId);
			list.add(value);
		}
		
		return list;
		
	}
	
	private int createStudy(Workbook workbook, Integer studyLocationId) throws Exception {
		TimerWatch watch = new TimerWatch("createStudy", LOG);
		
        List<MeasurementVariable> studyMV = workbook.getStudyVariables();
		VariableTypeList studyVariables = getVariableTypeListTransformer().transform(studyMV);

		StudyValues studyValues = getStudyValuesTransformer().transform(null, studyLocationId, workbook.getStudyDetails(), studyMV, studyVariables);
   		DmsProject study = getStudySaver().saveStudy((int) workbook.getStudyDetails().getParentFolderId(), studyVariables, studyValues);
   		
   		watch.stop();
   		return study.getProjectId();
	}
	
	private void createTrialDataset(Workbook workbook, int studyId, Collection<Integer> locationIds, List<MeasurementVariable> trialMV, 
			VariableTypeList trialVariables) throws MiddlewareQueryException {
		
		//create trial dataset
		TimerWatch watch = new TimerWatch("transform trial dataset", LOG);
 		String trialName = generateTrialDatasetName(workbook.getStudyDetails().getStudyName());
		DatasetValues trialValues = getDatasetValuesTransformer().transform(trialName, trialName, 
										DataSetType.PLOT_DATA, trialMV, trialVariables);
		watch.restart("save trial dataset");
		DmsProject trial = getDatasetProjectSaver().addDataSet(studyId, trialVariables, trialValues);
		
		//create trial experiment row(s)
		watch.restart("save trial experiments");
		List<ExperimentValues> trialDatasetValues = createTrialExperimentValues(locationIds);
		for (ExperimentValues trialExperiment : trialDatasetValues) {
			getExperimentModelSaver().addExperiment(trial.getProjectId(), ExperimentType.PLOT, trialExperiment);
		}
		
		watch.stop();
	}
	
	private void createMeasurementEffectDataset(Workbook workbook, int studyId, VariableTypeList trialVariables) throws MiddlewareQueryException {
		TimerWatch watch = new TimerWatch("preparing measurement effect variables", LOG);
        List<MeasurementVariable> datasetMV = workbook.getMeasurementDatasetVariables();
        VariableTypeList origDatasetVariables = getVariableTypeListTransformer().transform(datasetMV);
        VariableTypeList datasetVariables = new VariableTypeList();
        if (!isTrialFactorInDataset(origDatasetVariables)) {
        	datasetVariables.addAll(trialVariables);
        	origDatasetVariables.allocateRoom(trialVariables.size());
        }
        datasetVariables.addAll(origDatasetVariables);
        
		//create measurements dataset
        watch.restart("transform measurement effect dataset");
		String datasetName = generateMeasurementEffectDatasetName(workbook.getStudyDetails().getStudyName());
		DatasetValues datasetValues = getDatasetValuesTransformer().transform(datasetName, datasetName,
										DataSetType.PLOT_DATA, datasetMV, datasetVariables);
		watch.restart("save measurement effect dataset");
		DmsProject dataset = getDatasetProjectSaver().addDataSet(studyId, datasetVariables, datasetValues);

		Map<String, Integer> stockMap = new HashMap<String, Integer>();
		
		//create measurement experiment rows
		watch.restart("saving stocks and measurement effect data (total)");
		TimerWatch rowWatch = new TimerWatch("for each row", LOG);
		for(MeasurementRow row : workbook.getObservations()) {
			//create stock
			rowWatch.restart("--transform 1 stock");
			VariableList stock = getVariableListTransformer().transformStock(row, origDatasetVariables);
			String stockFactor = getStockFactor(stock);
			Integer stockId = stockMap.get(stockFactor);
			if (stockId == null) {
				rowWatch.restart("--save 1 stock");
				stockId = getStockSaver().saveStock(stock);
				stockMap.put(stockFactor, stockId);
			}
			row.setStockId(stockId);
			
			//create experiment row
			rowWatch.restart("--save 1 experiment row");
			ExperimentValues experimentValues = getExperimentValuesTransformer().transform(row, origDatasetVariables);
			getExperimentModelSaver().addExperiment(dataset.getProjectId(), ExperimentType.PLOT, experimentValues);
		}
		rowWatch.stop();
		watch.stop();
	}
	
	private boolean isTrialFactorInDataset(VariableTypeList list) {
		for (VariableType var : list.getVariableTypes()) {
			System.out.println(var);
			if (TermId.TRIAL_INSTANCE_STORAGE.getId() == var.getStandardVariable().getStoredIn().getId()) {
				return true;
			}
		}
		return false;
	}
}
