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
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.helper.VariableInfo;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.util.TimerWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//ASsumptions - can be added to validations
//Mandatory fields:  workbook.studyDetails.studyName
//template must not contain exact same combo of property-scale-method
public class WorkbookSaver extends Saver {

    private static final Logger LOG = LoggerFactory.getLogger(WorkbookSaver.class);
    
    private static final int DEFAULT_GEOLOCATION_ID = 1;
    
    public WorkbookSaver(HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}
    
	public int save(Workbook workbook) throws Exception {
		
		//TODO: call initial workbook validation such as mandatory fields check, etc.

		List<MeasurementVariable> trialMV = workbook.getTrialVariables();
        VariableTypeList trialVariables = getVariableTypeListTransformer()
        						.transform(workbook.getVariables(workbook.getConditions(), false), false);
        trialVariables.addAll(getVariableTypeListTransformer()
        						.transform(workbook.getVariables(workbook.getConstants(), false), true, trialVariables.size()+1));

        List<MeasurementVariable> effectMV = workbook.getMeasurementDatasetVariables();
        VariableTypeList effectVariables = getVariableTypeListTransformer().transform(workbook.getFactors(), false);
        effectVariables.addAll(getVariableTypeListTransformer().transform(workbook.getVariates(), true, effectVariables.size()+1));
        
		int locationId = createLocationAndSetToObservations(workbook, trialMV, trialVariables);

		int studyId = createStudyIfNecessary(workbook, locationId);
		
   		int trialDatasetId = createTrialDatasetIfNecessary(workbook, studyId, trialMV, trialVariables);
   		createTrialExperiment(trialDatasetId, locationId);
   		
   		int datasetId = createMeasurementEffectDatasetIfNecessary(workbook, studyId, trialMV, effectMV, effectVariables);
   		createStocksIfNecessary(datasetId, workbook, effectVariables);
   		createMeasurementEffectExperiments(datasetId, locationId, effectVariables, workbook);
   		
   		return studyId;
	}
	
	private int createLocationAndSetToObservations(Workbook workbook, List<MeasurementVariable> trialMV, 
	VariableTypeList trialVariables) throws MiddlewareQueryException {
		
		TimerWatch watch = new TimerWatch("transform trial environment", LOG);
		VariableList geolocation = getVariableListTransformer().transformTrialEnvironment(trialMV, trialVariables);
        Integer studyLocationId = null;

        if (geolocation != null && geolocation.size() > 0) {
            watch.restart("save geolocation");
            studyLocationId = getGeolocationSaver().saveGeolocation(geolocation);

        } else if (workbook.isNursery()) {
        	studyLocationId = DEFAULT_GEOLOCATION_ID;
        } //TODO: else raise an exception? trial factor is mandatory, otherwise a default value will be set
        
        watch.restart("set to observations(total)");
    	for(MeasurementRow row : workbook.getObservations()) {
   			row.setLocationId(studyLocationId);
        }
    	watch.stop();

    	return studyLocationId != null ? studyLocationId : DEFAULT_GEOLOCATION_ID;
	}
	
	private MeasurementVariable getMainFactor(List<MeasurementVariable> mvars) {
		for (MeasurementVariable mvar : mvars) {
			if (mvar.getName().equals(mvar.getLabel())) {
				return mvar;
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
	
	private ExperimentValues createTrialExperimentValues(Integer locationId) {
		ExperimentValues value = new ExperimentValues();
		value.setLocationId(locationId);
		
		return value;
	}
	
	private int createStudyIfNecessary(Workbook workbook, int studyLocationId) throws Exception {
		TimerWatch watch = new TimerWatch("find study", LOG);
		Integer studyId = getStudyId(workbook.getStudyDetails().getStudyName());

		if (studyId == null) {
			watch.restart("transform variables for study");
	        List<MeasurementVariable> studyMV = workbook.getStudyVariables();
	        VariableTypeList studyVariables = getVariableTypeListTransformer()
	        		.transform(workbook.getVariables(workbook.getConditions(), true), false);
	        studyVariables.addAll(getVariableTypeListTransformer()
	        		.transform(workbook.getVariables(workbook.getConstants(), true), true, studyVariables.size()+1));
	
			if (workbook.isNursery() && getMainFactor(workbook.getTrialFactors()) == null) {
				studyVariables.add(createOccVariableType(studyVariables.size()+1));
			}
	
			StudyValues studyValues = getStudyValuesTransformer().transform(null, studyLocationId, workbook.getStudyDetails(), studyMV, studyVariables);
			
			watch.restart("save study");
	   		DmsProject study = getStudySaver().saveStudy((int) workbook.getStudyDetails().getParentFolderId(), studyVariables, studyValues);
	   		studyId = study.getProjectId();
		}   		
   		watch.stop();
   		
   		return studyId;
	}
	
	private int createTrialDatasetIfNecessary(Workbook workbook, int studyId, 
			List<MeasurementVariable> trialMV, VariableTypeList trialVariables) throws MiddlewareQueryException {
		
		TimerWatch watch = new TimerWatch("find trial dataset", LOG);
		String trialName = workbook.getStudyDetails().getTrialDatasetName();
 		if(trialName == null || trialName.equals("") ){
 			trialName = generateTrialDatasetName(workbook.getStudyDetails().getStudyName());
 		}
		Integer trialDatasetId = getDatasetId(trialName, generateTrialDatasetName(workbook.getStudyDetails().getStudyName()));
		
		if (trialDatasetId == null) {
			watch.restart("transform trial dataset values");
			DatasetValues trialValues = getDatasetValuesTransformer().transform(trialName, trialName, 
					DataSetType.PLOT_DATA, trialMV, trialVariables);
			
			if (workbook.isNursery() && (trialMV == null || trialMV.size() == 0 || getMainFactor(trialMV) == null)) {
				trialVariables.add(createOccVariableType(trialVariables.size()+1));
			}
		
			watch.restart("save trial dataset");
			DmsProject trial = getDatasetProjectSaver().addDataSet(studyId, trialVariables, trialValues);
			trialDatasetId = trial.getProjectId();
		}
		
		watch.stop();
		return trialDatasetId;
	}
	
	private void createTrialExperiment(int trialProjectId, int locationId) throws MiddlewareQueryException {
		TimerWatch watch = new TimerWatch("save trial experiments", LOG);
		ExperimentValues trialDatasetValues = createTrialExperimentValues(locationId);
		getExperimentModelSaver().addExperiment(trialProjectId, ExperimentType.PLOT, trialDatasetValues);
		watch.stop();
	}
	
	private int createMeasurementEffectDatasetIfNecessary(Workbook workbook, int studyId, List<MeasurementVariable> trialMV,
	List<MeasurementVariable> effectMV, VariableTypeList effectVariables) throws MiddlewareQueryException, MiddlewareException {

		TimerWatch watch = new TimerWatch("find measurement effect dataset", LOG);
        String datasetName = workbook.getStudyDetails().getMeasurementDatasetName();
        if(datasetName == null || datasetName.equals("")){
        	datasetName = generateMeasurementEffectDatasetName(workbook.getStudyDetails().getStudyName());
        }
		Integer datasetId = getDatasetId(datasetName, generateMeasurementEffectDatasetName(workbook.getStudyDetails().getStudyName()));
		
		if (datasetId == null) {
	        watch.restart("transform measurement effect dataset");
			DatasetValues datasetValues = getDatasetValuesTransformer().transform(datasetName, datasetName,
											DataSetType.PLOT_DATA, effectMV, effectVariables);

			watch.restart("save measurement effect dataset");
			VariableTypeList datasetVariables = propagateTrialFactorsIfNecessary(workbook.getTrialFactors(), effectVariables, workbook);
			DmsProject dataset = getDatasetProjectSaver().addDataSet(studyId, datasetVariables, datasetValues);
			datasetId = dataset.getProjectId();
		}
		
		watch.stop();
		return datasetId;
	}
	
	private void createStocksIfNecessary(int datasetId, Workbook workbook, VariableTypeList effectVariables) throws MiddlewareQueryException {
		Map<String, Integer> stockMap = getStockModelBuilder().getStockMapForDataset(datasetId);

		TimerWatch watch = new TimerWatch("fetch stocks", LOG);
		TimerWatch rowWatch = new TimerWatch("for each row", LOG);
		
		for (MeasurementRow row : workbook.getObservations()) {
			VariableList stock = getVariableListTransformer().transformStock(row, effectVariables);
			String stockFactor = getStockFactor(stock);
			Integer stockId = stockMap.get(stockFactor);
			if (stockId == null) {
				rowWatch.restart("--save 1 stock");
				stockId = getStockSaver().saveStock(stock);
				stockMap.put(stockFactor, stockId);
			}
			row.setStockId(stockId);
		}
		
		rowWatch.stop();
		watch.stop();
	}
	
	private void createMeasurementEffectExperiments(int datasetId, int locationId, VariableTypeList effectVariables, 
			Workbook workbook) throws MiddlewareQueryException {
		
		TimerWatch watch = new TimerWatch("saving stocks and measurement effect data (total)", LOG);
		TimerWatch rowWatch = new TimerWatch("for each row", LOG);
		for(MeasurementRow row : workbook.getObservations()) {
			rowWatch.restart("--save 1 experiment row");
			ExperimentValues experimentValues = getExperimentValuesTransformer().transform(row, effectVariables);
			getExperimentModelSaver().addExperiment(datasetId, ExperimentType.PLOT, experimentValues);
		}
		rowWatch.stop();
		watch.stop();
	}
	
	private boolean isTrialFactorInDataset(VariableTypeList list) {
		for (VariableType var : list.getVariableTypes()) {
			if (TermId.TRIAL_INSTANCE_STORAGE.getId() == var.getStandardVariable().getStoredIn().getId()) {
				return true;
			}
		}
		return false;
	}
	
	private VariableType createOccVariableType(int rank) throws MiddlewareQueryException {
		VariableInfo info = new VariableInfo();
		info.setLocalName("OCC");
		info.setLocalDescription("OCC");
		info.setStdVariableId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		info.setRank(rank);
		return getVariableTypeBuilder().create(info);
	}
	
	private VariableTypeList propagateTrialFactorsIfNecessary(List<MeasurementVariable> trialFactors, VariableTypeList effectVariables, 
	Workbook workbook) throws MiddlewareQueryException, MiddlewareException {
		
		VariableTypeList newList = new VariableTypeList();
		
        if (!isTrialFactorInDataset(effectVariables)) {
    		VariableTypeList trialVariables = getVariableTypeListTransformer().transform(trialFactors, false);
        	newList.addAll(trialVariables);
        	effectVariables.allocateRoom(trialVariables.size());
        }
        newList.addAll(effectVariables);
		
		if (workbook.isNursery()) {
        	newList.add(createOccVariableType(newList.size()+1));
        }
        
		return newList;
	}
	
	private Integer getStudyId(String name) throws MiddlewareQueryException {
		return getProjectId(name, TermId.IS_STUDY);
	}
	
	private Integer getDatasetId(String name, String generatedName) throws MiddlewareQueryException {
		Integer id = getProjectId(name, TermId.BELONGS_TO_STUDY);
		if (id == null && !name.equals(generatedName)) {
			id = getProjectId(generatedName, TermId.BELONGS_TO_STUDY);
		}
		return id;
	}
	
	private Integer getProjectId(String name, TermId relationship) throws MiddlewareQueryException {
		Integer id = null;
		setWorkingDatabase(Database.CENTRAL);
		id = getDmsProjectDao().getProjectIdByName(name, relationship);
		if (id == null) {
			setWorkingDatabase(Database.LOCAL);
			id = getDmsProjectDao().getProjectIdByName(name, relationship);
		}
		return id;
	}
}
