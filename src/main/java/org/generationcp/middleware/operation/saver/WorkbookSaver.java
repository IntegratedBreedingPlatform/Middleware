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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.DatasetValues;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.ExperimentValues;
import org.generationcp.middleware.domain.dms.StudyValues;
import org.generationcp.middleware.domain.dms.Values;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.helper.VariableInfo;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.operation.transformer.etl.ExperimentValuesTransformer;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.util.TimerWatch;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//ASsumptions - can be added to validations
//Mandatory fields:  workbook.studyDetails.studyName
//template must not contain exact same combo of property-scale-method

//TODO : CONTROL THE SESSION - we need to flush in new Standard Variables as soon as we can - before Datasets and constructed
public class WorkbookSaver extends Saver {

    private static final Logger LOG = LoggerFactory.getLogger(WorkbookSaver.class);
    
    private static final int DEFAULT_GEOLOCATION_ID = 1;
    
    public WorkbookSaver(HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}
    
    /**
     * This method transforms Variable data from a Fieldbook presented as an XLS style Workbook.
     * - Variables new to the ontology are created and persisted
     * - Columns and rows are transformed into entities suitables for persistence
     * 
     * Note : the result of this process is suitable for Dataset Creation
     * 
     * @param workbook
     * @return Map<String>, ?> : a map of 3 sub-maps containing Strings(headers), VariableTypeLists and Lists of MeasurementVariables
     * @throws Exception
     */
    
    @SuppressWarnings("rawtypes")
	public Map saveVariables(Workbook workbook) throws Exception 
    {
    	workbook.reset();//make sure to reset all derived variables
    	
    	// Create Maps, which we will fill with transformed Workbook Variable Data
    	Map<String, Map<String, ?>> variableMap = new HashMap<String, Map<String,?>>();
    	Map<String, List<String>> headerMap = new HashMap<String, List<String>>();
    	Map<String, VariableTypeList> variableTypeMap = new HashMap<String, VariableTypeList>();
    	Map<String, List<MeasurementVariable>> measurementVariableMap = new HashMap<String, List<MeasurementVariable>>();
    	
		//GCP-6091 start
		List<MeasurementVariable> trialMV = workbook.getTrialVariables();
		List<String> trialHeaders = workbook.getTrialHeaders();        
        VariableTypeList trialVariables = getVariableTypeListTransformer()
        						.transform(workbook.getTrialConditions(), false);
        List<MeasurementVariable> trialFactors = workbook.getTrialFactors();
        VariableTypeList trialVariableTypeList = null;
        if(trialFactors!=null && !trialFactors.isEmpty()) {//multi-location
        	trialVariableTypeList = getVariableTypeListTransformer().transform(trialFactors, false, trialVariables.size()+1);
        	trialVariables.addAll(trialVariableTypeList);
        }
        //GCP-6091 end
        trialVariables.addAll(getVariableTypeListTransformer()
        						.transform(workbook.getTrialConstants(), true, trialVariables.size()+1));

        List<MeasurementVariable> effectMV = workbook.getMeasurementDatasetVariables();
        VariableTypeList effectVariables = getVariableTypeListTransformer().transform(workbook.getNonTrialFactors(), false);
        effectVariables.addAll(getVariableTypeListTransformer().transform(workbook.getVariates(), true, effectVariables.size()+1));
        
        // Load Lists into Maps in order to return to the front end (and force Session Flush)
        // -- headers
        headerMap.put("trialHeaders", trialHeaders);
        // -- variableTypeLists
        variableTypeMap.put("trialVariableTypeList", trialVariableTypeList);
        variableTypeMap.put("trialVariables", trialVariables);
        variableTypeMap.put("effectVariables", effectVariables);
        // -- measurementVariables
        measurementVariableMap.put("trialMV", trialMV);
        measurementVariableMap.put("effectMV", effectMV);
        // load 3 maps into a super Map
        variableMap.put("headerMap", headerMap);
        variableMap.put("variableTypeMap", variableTypeMap);
        variableMap.put("measurementVariableMap", measurementVariableMap);
        return variableMap;
        
    }
    
    /**
     * Dataset creation and persistence for Fieldbook upload
     * 
     * NOTE IMPORTANT : This step will fail if the Fieldbook has not had new Variables processed 
     * and new ontology terms created.
     * 
     * 
     * @param workbook
     * @param variableMap : a map of 3 sub-maps containing Strings(headers), VariableTypeLists and Lists of MeasurementVariables
     * @return int (success/fail)
     * @throws Exception
     */
	@SuppressWarnings("unchecked")
	public int saveDataset(Workbook workbook, Map<String, ?> variableMap, boolean retainValues) throws Exception {
		
		// unpack maps first level - Maps of Strings, Maps of VariableTypeList , Maps of Lists of MeasurementVariable
		Map<String, List<String>> headerMap = (Map<String, List<String>>) variableMap.get("headerMap");
		Map<String, VariableTypeList> variableTypeMap = (Map<String, VariableTypeList>) variableMap.get("variableTypeMap");
		Map<String, List<MeasurementVariable>> measurementVariableMap = (Map<String, List<MeasurementVariable>>) variableMap.get("measurementVariableMap");

		// unpack maps
		// Strings
		List<String> trialHeaders = headerMap.get("trialHeaders");
		//VariableTypeLists
		VariableTypeList trialVariableTypeList = variableTypeMap.get("trialVariableTypeList");
		VariableTypeList trialVariables = variableTypeMap.get("trialVariables");
		VariableTypeList effectVariables = variableTypeMap.get("effectVariables");
		// Lists of measurementVariables
		List<MeasurementVariable> trialMV = measurementVariableMap.get("trialMV");
		List<MeasurementVariable> effectMV = measurementVariableMap.get("effectMV");
		
		// TODO : Review code and see whether variable validation and possible dataset creation abort
		// is a good idea (rebecca)
//		boolean error = false;
//		for (VariableType ev : effectVariables.getVariableTypes()) {
//			if(ev.getStandardVariable().getStoredIn() == null) {
//				LOG.info("No ROLE for : " + ev.getStandardVariable().getName());
//				error = true;
//			}
//			else LOG.info("Role for : " + ev.getStandardVariable().getName() + " : " + ev.getStandardVariable().getStoredIn().getId());
//		}
//		if(error) return -1;
		
		
        //GCP-6091 start
        int studyLocationId = DEFAULT_GEOLOCATION_ID;
        List<Integer> locationIds = new ArrayList<Integer>();
        Map<Integer,VariableList> trialVariatesMap = new HashMap<Integer,VariableList>();
        if(trialVariableTypeList!=null) {//multi-location
   			studyLocationId = createLocationsAndSetToObservations(locationIds,workbook,trialVariableTypeList,trialHeaders, trialVariatesMap);
        } else {
        	studyLocationId = createLocationAndSetToObservations(workbook, trialMV, trialVariables, trialVariatesMap);
        }
		//GCP-6091 end
		
		int studyId = createStudyIfNecessary(workbook, studyLocationId, true); 
   		int trialDatasetId = createTrialDatasetIfNecessary(workbook, studyId, trialMV, trialVariables);
   		
   		if(trialVariableTypeList!=null) {//multi-location
   			for(Integer locationId : locationIds) {
   				createTrialExperiment(trialDatasetId, locationId, trialVariatesMap.get(locationId));
   			}
   		} else {
   			createTrialExperiment(trialDatasetId, studyLocationId, trialVariatesMap.get(studyLocationId));
   		}
   		
   		int datasetId = createMeasurementEffectDatasetIfNecessary(workbook, studyId, effectMV, effectVariables, trialVariables);
   		createStocksIfNecessary(datasetId, workbook, effectVariables, trialHeaders);
   		
   		if (!retainValues){
       		//clean up some variable references to save memory space before saving the measurement effects
       		variableMap = null;
       		headerMap = null;
       		variableTypeMap = null;
       		measurementVariableMap = null;
       		trialVariableTypeList = null;
       		effectMV = null;
       		workbook.reset();
       		workbook.setConditions(null);
       		workbook.setConstants(null);
       		workbook.setFactors(null);
       		workbook.setStudyDetails(null);
       		workbook.setVariates(null);
   		}
   		
   		createMeasurementEffectExperiments(datasetId, effectVariables,  workbook.getObservations(), trialHeaders, trialVariatesMap);
        
   		return studyId;
	}
	
	private int createLocationAndSetToObservations(Workbook workbook, List<MeasurementVariable> trialMV, 
			VariableTypeList trialVariables, Map<Integer,VariableList> trialVariatesMap) throws MiddlewareQueryException {
		
		TimerWatch watch = new TimerWatch("transform trial environment", LOG);
		VariableList geolocation = getVariableListTransformer().transformTrialEnvironment(trialMV, trialVariables);
        Integer studyLocationId = null;

        if (geolocation != null && geolocation.size() > 0) {
            watch.restart("save geolocation");
            Geolocation g = getGeolocationSaver().saveGeolocation(geolocation, null, workbook.isNursery());
            studyLocationId = g.getLocationId();
            if(g.getVariates()!=null && g.getVariates().size() > 0) {
            	VariableList trialVariates = new VariableList();
            	trialVariates.addAll(g.getVariates());
            	trialVariatesMap.put(studyLocationId, trialVariates);
            }

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
	
	private int createLocationsAndSetToObservations(List<Integer> locationIds, Workbook workbook, VariableTypeList trialFactors, List<String> trialHeaders, Map<Integer,VariableList> trialVariatesMap) throws MiddlewareQueryException {
		
		Set<String> trialInstanceNumbers = new HashSet<String>();
		Integer locationId = null;
		for (MeasurementRow row : workbook.getObservations()) {
			TimerWatch watch = new TimerWatch("transformTrialEnvironment in createLocationsAndSetToObservations", LOG);
			VariableList geolocation = getVariableListTransformer().transformTrialEnvironment(row, trialFactors, trialHeaders);
			if (geolocation != null && geolocation.size() > 0) {
				String trialInstanceNumber = getTrialInstanceNumber(geolocation);
                if (LOG.isDebugEnabled()){
                    LOG.debug("trialInstanceNumber = "+trialInstanceNumber);
                }
                if(trialInstanceNumbers.add(trialInstanceNumber)) {//if new location (unique by trial instance number)
		            watch.restart("save geolocation");
		            Geolocation g = getGeolocationSaver().saveGeolocation(geolocation, row, workbook.isNursery()); 
		            locationId = g.getLocationId();
		            locationIds.add(locationId);
		            if(g.getVariates()!=null && g.getVariates().size() > 0) {
		            	VariableList trialVariates = new VariableList();
		            	trialVariates.addAll(g.getVariates());
		            	trialVariatesMap.put(locationId,trialVariates);
		            }
				}
				row.setLocationId(locationId);
	        }
		}
        //return studyLocationId
		return Long.valueOf(workbook.getObservations().get(0).getLocationId()).intValue();
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
	
	private String getTrialInstanceNumber(VariableList trialVariables) {
		for (Variable variable : trialVariables.getVariables()) {
			if (TermId.TRIAL_INSTANCE_STORAGE.getId() == variable.getVariableType().getStandardVariable().getStoredIn().getId()) {
				return variable.getValue();
			}
		}
		return null;
	}
	
	private String generateTrialDatasetName(String studyName, StudyType studyType) {
		if(studyType == StudyType.N) {
			return "NURSERY_" + studyName;
		}
		return "TRIAL_" + studyName;
	}
	
	private String generateMeasurementEffectDatasetName(String studyName) {
		return "MEASUREMENT EFEC_" + studyName;
	}
	
	private ExperimentValues createTrialExperimentValues(Integer locationId, VariableList variates) {
        ExperimentValues value = new ExperimentValues();
        value.setLocationId(locationId);
        value.setVariableList(variates);
		return value;
	}
	
	private int createStudyIfNecessary(Workbook workbook, int studyLocationId, boolean saveStudyExperiment) throws Exception {
		TimerWatch watch = new TimerWatch("find study", LOG);
		Integer studyId = getStudyId(workbook.getStudyDetails().getStudyName());

		if (studyId == null) {
			watch.restart("transform variables for study");
	        List<MeasurementVariable> studyMV = workbook.getStudyVariables();
	        VariableTypeList studyVariables = getVariableTypeListTransformer()
	        		.transform(workbook.getStudyConditions(), false);
	        studyVariables.addAll(getVariableTypeListTransformer()
	        		.transform(workbook.getStudyConstants(), true, studyVariables.size()+1));
	
			if (workbook.isNursery() && getMainFactor(workbook.getTrialVariables()) == null) {
				studyVariables.add(createOccVariableType(studyVariables.size()+1));
			}
	
			StudyValues studyValues = getStudyValuesTransformer().transform(null, studyLocationId, workbook.getStudyDetails(), studyMV, studyVariables);
			
			watch.restart("save study");
	   		DmsProject study = getStudySaver().saveStudy((int) workbook.getStudyDetails().getParentFolderId(), studyVariables, studyValues, saveStudyExperiment);
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
 			trialName = generateTrialDatasetName(workbook.getStudyDetails().getStudyName(),workbook.getStudyDetails().getStudyType());
 		}
		Integer trialDatasetId = getDatasetId(trialName, generateTrialDatasetName(workbook.getStudyDetails().getStudyName(),workbook.getStudyDetails().getStudyType()));
		
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
		} else {
			if (workbook.isNursery() && (trialMV == null || trialMV.size() == 0 || getMainFactor(trialMV) == null)) {
				trialVariables.add(createOccVariableType(trialVariables.size()+1));
			}
		}
		
		watch.stop();
		return trialDatasetId;
	}
	
	private void createTrialExperiment(int trialProjectId, int locationId, VariableList trialVariates) throws MiddlewareQueryException {
	    
		TimerWatch watch = new TimerWatch("save trial experiments", LOG);
		ExperimentValues trialDatasetValues = createTrialExperimentValues(locationId, trialVariates);
		getExperimentModelSaver().addExperiment(trialProjectId, ExperimentType.PLOT, trialDatasetValues);
		watch.stop();
	}
	
	private int createMeasurementEffectDatasetIfNecessary(Workbook workbook, int studyId, 
	List<MeasurementVariable> effectMV, VariableTypeList effectVariables, VariableTypeList trialVariables) throws MiddlewareQueryException, MiddlewareException {

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
			//fix for GCP-6436 start
			VariableTypeList datasetVariables = propagateTrialFactorsIfNecessary(effectVariables, trialVariables);
			//no need to add occ as it is already added in trialVariables
			//fix for GCP-6436 end			
			DmsProject dataset = getDatasetProjectSaver().addDataSet(studyId, datasetVariables, datasetValues);
			datasetId = dataset.getProjectId();
		}
		
		watch.stop();
		return datasetId;
	}
	
	private void createStocksIfNecessary(int datasetId, Workbook workbook, VariableTypeList effectVariables,List<String> trialHeaders) throws MiddlewareQueryException {
		Map<String, Integer> stockMap = getStockModelBuilder().getStockMapForDataset(datasetId);

		TimerWatch watch = new TimerWatch("fetch stocks", LOG);
		TimerWatch rowWatch = new TimerWatch("for each row", LOG);
		
		for (MeasurementRow row : workbook.getObservations()) {
			VariableList stock = getVariableListTransformer().transformStock(row, effectVariables, trialHeaders);
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
	
	private void createMeasurementEffectExperiments(int datasetId, VariableTypeList effectVariables, 
			List<MeasurementRow> observations, List<String> trialHeaders, Map<Integer,VariableList> trialVariatesMap) throws MiddlewareQueryException {
		
		TimerWatch watch = new TimerWatch("saving stocks and measurement effect data (total)", LOG);
		TimerWatch rowWatch = new TimerWatch("for each row", LOG);
		
		int i = 2;//observation values start at row 2
		Session session = getCurrentSessionForLocal();
		ExperimentValuesTransformer experimentValuesTransformer = getExperimentValuesTransformer();
		ExperimentModelSaver experimentModelSaver = getExperimentModelSaver();
		for(MeasurementRow row : observations) {
			rowWatch.restart("saving row "+(i++));
			ExperimentValues experimentValues = experimentValuesTransformer.transform(row, effectVariables, trialHeaders);
			VariableList trialVariates = trialVariatesMap.get((int)row.getLocationId());
			if(trialVariates!=null) {
				experimentValues.getVariableList().addAll(trialVariates);
			}
			experimentModelSaver.addExperiment(datasetId, ExperimentType.PLOT, experimentValues);
			if ( i % 50 == 0 ) { //to save memory space - http://docs.jboss.org/hibernate/core/3.3/reference/en/html/batch.html#batch-inserts
				session.flush();
				session.clear();
			}
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
	
	private VariableTypeList propagateTrialFactorsIfNecessary(VariableTypeList effectVariables, VariableTypeList trialVariables) throws MiddlewareQueryException, MiddlewareException {
		
		VariableTypeList newList = new VariableTypeList();
		
        if (!isTrialFactorInDataset(effectVariables)) {
    		newList.addAll(trialVariables);
        	effectVariables.allocateRoom(trialVariables.size());
        }
        newList.addAll(effectVariables);
		
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
	
	/**
     * Saves project ontology creating entries in the following tables:
     * project, projectprop and project_relationship
     * @param Workbook
     * @return study id
     * @throws Exception
     */
	@SuppressWarnings("unchecked")
	public int saveProjectOntology(Workbook workbook) throws Exception {
		
		Map<String, ?> variableMap = saveVariables(workbook);
		
		// unpack maps first level - Maps of Strings, Maps of VariableTypeList , Maps of Lists of MeasurementVariable
		Map<String, VariableTypeList> variableTypeMap = (Map<String, VariableTypeList>) variableMap.get("variableTypeMap");
		Map<String, List<MeasurementVariable>> measurementVariableMap = (Map<String, List<MeasurementVariable>>) variableMap.get("measurementVariableMap");

		// unpack maps
		VariableTypeList trialVariables = variableTypeMap.get("trialVariables");
		VariableTypeList effectVariables = variableTypeMap.get("effectVariables");
		List<MeasurementVariable> trialMV = measurementVariableMap.get("trialMV");
		List<MeasurementVariable> effectMV = measurementVariableMap.get("effectMV");
		
		int studyId = createStudyIfNecessary(workbook, 0, false);//locationId and experiment are not yet needed here
		int trialDatasetId = createTrialDatasetIfNecessary(workbook, studyId, trialMV, trialVariables);
		int measurementDatasetId = createMeasurementEffectDatasetIfNecessary(workbook, studyId, effectMV, effectVariables, trialVariables);
		
		if (LOG.isDebugEnabled()){
			LOG.debug("studyId = "+studyId);
            LOG.debug("trialDatasetId = "+trialDatasetId);
            LOG.debug("measurementDatasetId = "+measurementDatasetId);
        }
		
		return studyId;
	}
	
	/**
     * Saves experiments creating entries in the following tables:
     * nd_geolocation, nd_geolocationprop, nd_experiment, nd_experiment_project, nd_experimentprop
     * nd_experiment_stock, stock, stockprop, nd_experiment_phenotype and phenotype
     * @param Workbook
     * @return 
     * @throws Exception
     */
	@SuppressWarnings("unchecked")
	public void saveProjectData(
			int studyId, 
			int trialDatasetId, 
			int measurementDatasetId,
			Workbook workbook) throws Exception {
		
		Map<String, ?> variableMap = saveVariables(workbook);
		
		// unpack maps first level - Maps of Strings, Maps of VariableTypeList , Maps of Lists of MeasurementVariable
		Map<String, List<String>> headerMap = (Map<String, List<String>>) variableMap.get("headerMap");
		Map<String, VariableTypeList> variableTypeMap = (Map<String, VariableTypeList>) variableMap.get("variableTypeMap");
		Map<String, List<MeasurementVariable>> measurementVariableMap = (Map<String, List<MeasurementVariable>>) variableMap.get("measurementVariableMap");

		// unpack maps
		List<String> trialHeaders = headerMap.get("trialHeaders");
		VariableTypeList trialVariableTypeList = variableTypeMap.get("trialVariableTypeList");
		VariableTypeList trialVariables = variableTypeMap.get("trialVariables");
		VariableTypeList effectVariables = variableTypeMap.get("effectVariables");
		List<MeasurementVariable> trialMV = measurementVariableMap.get("trialMV");
		
		//create locations (entries to nd_geolocation) and associate to observations
        int studyLocationId = DEFAULT_GEOLOCATION_ID;
        List<Integer> locationIds = new ArrayList<Integer>();
        Map<Integer,VariableList> trialVariatesMap = new HashMap<Integer,VariableList>();
        if(trialVariableTypeList!=null) {//multi-location
   			studyLocationId = createLocationsAndSetToObservations(locationIds,workbook,trialVariableTypeList,trialHeaders, trialVariatesMap);
        } else {
        	studyLocationId = createLocationAndSetToObservations(workbook, trialMV, trialVariables, trialVariatesMap);
        }
		
        //create stock and stockprops and associate to observations
        createStocksIfNecessary(measurementDatasetId, workbook, effectVariables, trialHeaders);
        
		//create experiments
        //1. study experiment
        StudyValues values = new StudyValues();
        values.setLocationId(studyLocationId);
        getStudySaver().saveStudyExperiment(studyId, values);
        //2. trial experiments
        if(trialVariableTypeList!=null) {//multi-location
   			for(Integer locationId : locationIds) {
   				createTrialExperiment(trialDatasetId, locationId, trialVariatesMap.get(locationId));
   			}
   		} else {
   			createTrialExperiment(trialDatasetId, studyLocationId, trialVariatesMap.get(studyLocationId));
   		}
        //3. measurement experiments
        createMeasurementEffectExperiments(measurementDatasetId, effectVariables,  workbook.getObservations(), trialHeaders, trialVariatesMap);
	}
}
