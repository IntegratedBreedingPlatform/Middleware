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

import org.generationcp.middleware.domain.dms.*;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.exceptions.PhenotypeException;
import org.generationcp.middleware.helper.VariableInfo;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.operation.transformer.etl.ExperimentValuesTransformer;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.util.TimerWatch;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

//ASsumptions - can be added to validations
//Mandatory fields:  workbook.studyDetails.studyName
//template must not contain exact same combo of property-scale-method

//TODO : CONTROL THE SESSION - we need to flush in new Standard Variables as soon as we can - before Datasets and constructed
public class WorkbookSaver extends Saver {

    private static final Logger LOG = LoggerFactory.getLogger(WorkbookSaver.class);
    
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
	public int saveDataset(Workbook workbook, Map<String, ?> variableMap, boolean retainValues, boolean isDeleteObservations) throws Exception {
	    Session session = getCurrentSessionForLocal(); 
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
		
		// TODO : Review code and see whether variable validation and possible dataset creation abort is a good idea (rebecca)

        //GCP-6091 start
        int studyLocationId;
        List<Integer> locationIds = new ArrayList<Integer>();
        Map<Integer,VariableList> trialVariatesMap = new HashMap<Integer,VariableList>();
        
        //get the trial and measurement dataset id to use in deletion of experiments 
        Integer trialDatasetId = workbook.getTrialDatasetId();
        Integer datasetId = workbook.getMeasurementDatesetId();
        int totalRows = 0;
        boolean isDeleteTrialObservations = false;
        if (trialDatasetId == null && workbook.getStudyDetails().getId() != null) {
            trialDatasetId = getWorkbookBuilder().getTrialDataSetId(workbook.getStudyDetails().getId(), workbook.getStudyName());
        }
        if (datasetId == null && workbook.getStudyDetails().getId() != null) {
            datasetId = getWorkbookBuilder().getMeasurementDataSetId(workbook.getStudyDetails().getId(), workbook.getStudyName());
        }
        
        if (trialDatasetId != null) {
            totalRows = (int) getStudyDataManager().countExperiments(trialDatasetId);
        }
        
        if (((workbook.getTrialObservations() != null && totalRows != workbook.getTrialObservations().size() 
                && totalRows > 0) || isDeleteObservations) && trialDatasetId != null) {
            isDeleteTrialObservations = true;
            //delete measurement data
            getExperimentDestroyer().deleteExperimentsByStudy(datasetId);
            session.flush();
            session.clear();
            
            //reset trial observation details such as experimentid, stockid and geolocationid
            resetTrialObservations(workbook.getTrialObservations());
        }
        
        if(trialVariableTypeList!=null && !isDeleteObservations) {//multi-location for data loader
   			studyLocationId = createLocationsAndSetToObservations(locationIds,workbook,trialVariableTypeList,trialHeaders, trialVariatesMap, false);
        } else if (workbook.getTrialObservations() != null && workbook.getTrialObservations().size() > 1) { //also a multi-location
        	studyLocationId = createLocationsAndSetToObservations(locationIds,  workbook,  trialVariables, trialHeaders, trialVariatesMap, isDeleteTrialObservations);
        } else {
        	studyLocationId = createLocationAndSetToObservations(workbook, trialMV, trialVariables, trialVariatesMap, isDeleteTrialObservations);
        }

        //GCP-6091 end
        if (isDeleteTrialObservations) {
            session.flush();
            session.clear();
            requireLocalDatabaseInstance();
            ExperimentModel studyExperiment = getExperimentDao().getExperimentsByProjectIds(Arrays.asList(workbook.getStudyDetails().getId())).get(0);
            studyExperiment.setGeoLocation(getGeolocationDao().getById(studyLocationId));
            getExperimentDao().saveOrUpdate(studyExperiment);
            
            //delete trial observations
            getExperimentDestroyer().deleteTrialExperimentsOfStudy(trialDatasetId);
        }
		
		int studyId = 0;
		if (!(workbook.getStudyDetails() != null && workbook.getStudyDetails().getId() != null)) {
			studyId = createStudyIfNecessary(workbook, studyLocationId, true); 
		}
		else {
			studyId = workbook.getStudyDetails().getId();
		}
   		trialDatasetId = createTrialDatasetIfNecessary(workbook, studyId, trialMV, trialVariables);
   		
   		saveOrUpdateTrialObservations(trialDatasetId, workbook, trialVariableTypeList, locationIds, 
   		        trialVariatesMap, studyLocationId, totalRows, isDeleteObservations);
   		
   		datasetId = createMeasurementEffectDatasetIfNecessary(workbook, studyId, effectMV, effectVariables, trialVariables);
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
	
	public void removeDeletedVariablesAndObservations(Workbook workbook) {
	    deleteDeletedVariablesInObservations(workbook.getFactors(), workbook.getObservations());
	    deleteDeletedVariablesInObservations(workbook.getVariates(), workbook.getObservations());
	    deleteDeletedVariables(workbook.getConditions());
	    deleteDeletedVariables(workbook.getFactors());
	    deleteDeletedVariables(workbook.getVariates());
	    deleteDeletedVariables(workbook.getConstants());
	}
	
    private void deleteDeletedVariablesInObservations(List<MeasurementVariable> variableList, List<MeasurementRow> observations) {
        List<Integer> deletedList = new ArrayList<Integer>();
        if (variableList != null) {
            for (MeasurementVariable var : variableList) {
                if (var.getOperation() != null && var.getOperation().equals(Operation.DELETE)) {
                    deletedList.add(Integer.valueOf(var.getTermId()));
                }
            }
        }
        if (deletedList != null && observations != null) {
            for (Integer termId : deletedList) {
                //remove from measurement rows
                int index = 0;
                int varIndex = 0;
                boolean found = false;
                for (MeasurementRow row : observations) {
                    if (index == 0) {
                        for (MeasurementData var : row.getDataList()) {
                            if (var.getMeasurementVariable().getTermId() == termId.intValue()) {
                            	found = true;
                                break;
                            }
                            varIndex++;
                        }
                    }
                    if (found) {
                    	row.getDataList().remove(varIndex);
                    }
                    else {
                    	break;
                    }
                    index++;
                }
            }
	    }
	}
	
	private void deleteDeletedVariables(List<MeasurementVariable> variableList) {
	    if (variableList != null) {
    	    Iterator<MeasurementVariable> variable = variableList.iterator();
            while (variable.hasNext()) {
            	MeasurementVariable var = variable.next();
                if (var.getOperation() != null && var.getOperation().equals(Operation.DELETE)) {
                    variable.remove();
                }
            }
	    }
	}
	
	public void resetTrialObservations(List<MeasurementRow> trialObservations) {
        for (MeasurementRow row : trialObservations) {
            row.setExperimentId(0);
            row.setLocationId(0);
            row.setStockId(0);
            for (MeasurementData data: row.getDataList()) {
                data.setPhenotypeId(null);
            }
        }
    }
	
	public void saveOrUpdateTrialObservations(int trialDatasetId, Workbook workbook, VariableTypeList trialVariableTypeList, 
	        List<Integer> locationIds, Map<Integer,VariableList> trialVariatesMap, int studyLocationId, int totalRows, boolean isDeleteObservations) 
	                throws MiddlewareQueryException, MiddlewareException {           
        if (workbook.getTrialObservations() != null && totalRows == workbook.getTrialObservations().size() && totalRows > 0 
                && !isDeleteObservations) {
            saveTrialObservations(workbook);
        } else {            
            if(trialVariableTypeList!=null || workbook.getTrialObservations() != null && workbook.getTrialObservations().size() > 1) {//multi-location
                for(Integer locationId : locationIds) {
                    createTrialExperiment(trialDatasetId, locationId, trialVariatesMap.get(locationId));
                }
            } else {
                createTrialExperiment(trialDatasetId, studyLocationId, trialVariatesMap.get(studyLocationId));
            }
        }
	}
	
	public void saveTrialObservations(Workbook workbook) throws MiddlewareQueryException, MiddlewareException {
        setWorkingDatabase(Database.LOCAL);
        if (workbook.getTrialObservations() != null && !workbook.getTrialObservations().isEmpty()) {
            for (MeasurementRow trialObservation : workbook.getTrialObservations()) {
                getGeolocationSaver().updateGeolocationInformation(trialObservation, workbook.isNursery());
            }
        }
    }
	
	public int createLocationAndSetToObservations(Workbook workbook, List<MeasurementVariable> trialMV, 
			VariableTypeList trialVariables, Map<Integer,VariableList> trialVariatesMap, boolean isDeleteTrialObservations) throws MiddlewareQueryException {
		
		TimerWatch watch = new TimerWatch("transform trial environment", LOG);
		if (workbook.getTrialObservations() != null && workbook.getTrialObservations().size() == 1) {
			MeasurementRow trialObs = workbook.getTrialObservations().get(0);
			for (MeasurementVariable mv : trialMV) {
				for (MeasurementData mvrow : trialObs.getDataList()) {
					if (mvrow.getMeasurementVariable().getTermId() == mv.getTermId()) {
						mv.setValue(mvrow.getValue());
						break;
					}
				}
			}
		}
		VariableList geolocation = getVariableListTransformer().transformTrialEnvironment(trialMV, trialVariables);
        Integer studyLocationId = null;

       	//GCP-8092 Nurseries will always have a unique geolocation, no more concept of shared/common geolocation
        if (geolocation == null || geolocation.size() == 0) {
        	geolocation = createDefaultGeolocationVariableList();
        }

        watch.restart("save geolocation");
        Geolocation g = getGeolocationSaver().saveGeolocationOrRetrieveIfExisting(
        		workbook.getStudyDetails().getStudyName(),geolocation, null, workbook.isNursery(), isDeleteTrialObservations);
        studyLocationId = g.getLocationId();
        if(g.getVariates()!=null && g.getVariates().size() > 0) {
        	VariableList trialVariates = new VariableList();
        	trialVariates.addAll(g.getVariates());
        	trialVariatesMap.put(studyLocationId, trialVariates);
        }
        
        watch.restart("set to observations(total)");
		if (workbook.getTrialObservations() != null && !workbook.getTrialObservations().isEmpty()) {
			for (MeasurementRow row : workbook.getTrialObservations()) {
				row.setLocationId(studyLocationId);
			}
		}
	if (workbook.getObservations() != null) {
            for(MeasurementRow row : workbook.getObservations()) {
       		row.setLocationId(studyLocationId);
            }
	}
    	watch.stop();
    	
    	return studyLocationId;
	}
	
	public int createLocationsAndSetToObservations(List<Integer> locationIds, Workbook workbook, VariableTypeList trialFactors, List<String> trialHeaders, Map<Integer,VariableList> trialVariatesMap, boolean isDeleteTrialObservations) throws MiddlewareQueryException {
		
		Set<String> trialInstanceNumbers = new HashSet<String>();
		Integer locationId = null;
		List<MeasurementRow> observations = null;
		Long geolocationId = null;
		boolean hasTrialObservations = false;
		if (workbook.getTrialObservations() != null && !workbook.getTrialObservations().isEmpty()) {
			observations = workbook.getTrialObservations();
			hasTrialObservations = true;
			if (workbook.isNursery()) {
				geolocationId = observations.get(0).getLocationId();
			}
		}
		else {
			observations = workbook.getObservations();
		}
		Map<String, Integer> locationMap = new HashMap<String, Integer>();
		if (observations != null) {
			for (MeasurementRow row : observations) {
			    geolocationId = row.getLocationId();
				if (geolocationId != null && geolocationId != 0) { //if geolocationId already exists, no need to create the geolocation
					row.setLocationId(geolocationId);
				}
				else {
					TimerWatch watch = new TimerWatch("transformTrialEnvironment in createLocationsAndSetToObservations", LOG);
					VariableList geolocation = getVariableListTransformer().transformTrialEnvironment(row, trialFactors, trialHeaders);
					if (geolocation != null && geolocation.size() > 0) {
						String trialInstanceNumber = getTrialInstanceNumber(geolocation);
		                if (LOG.isDebugEnabled()){
		                    LOG.debug("trialInstanceNumber = "+trialInstanceNumber);
		                }
		                if(trialInstanceNumbers.add(trialInstanceNumber)) {//if new location (unique by trial instance number)
				            watch.restart("save geolocation");
				            Geolocation g = getGeolocationSaver().saveGeolocationOrRetrieveIfExisting(
				            		workbook.getStudyDetails().getStudyName(), geolocation, row, workbook.isNursery(), isDeleteTrialObservations);
				            locationId = g.getLocationId();
				            locationIds.add(locationId);
				            if(g.getVariates()!=null && g.getVariates().size() > 0) {
				            	VariableList trialVariates = new VariableList();
				            	trialVariates.addAll(g.getVariates());
				            	trialVariatesMap.put(locationId,trialVariates);
				            }
						}
						row.setLocationId(locationId);
						locationMap.put(trialInstanceNumber, locationId);
			        }
				}
			}
			
			if (hasTrialObservations && workbook.getObservations() != null) {
				for (MeasurementRow row : workbook.getObservations()) {
					String trialInstance = getTrialInstanceNumber(row);
					if (trialInstance != null) {
						Integer locId = locationMap.get(trialInstance);
						row.setLocationId(locId);
					}
					else if (geolocationId != null) {
						row.setLocationId(geolocationId);
					}
				}
			}
	        //return studyLocationId
			if (workbook.getObservations() != null && workbook.getObservations().size() > 0) {
			    return Long.valueOf(workbook.getObservations().get(0).getLocationId()).intValue();
			} 
			else {
			    return Long.valueOf(workbook.getTrialObservations().get(0).getLocationId()).intValue(); 
			}
		}
		
		return 0;
	}
	
	private String getTrialInstanceNumber(MeasurementRow row) {
		for (MeasurementData data : row.getDataList()) {
			if (data.getMeasurementVariable().getTermId() == TermId.TRIAL_INSTANCE_FACTOR.getId()) {
				return data.getValue();
			}
		}
		return null;
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
		Map<String, Variable> variableMap = stockVariables.getVariableMap();
		if(variableMap != null){
			Variable var = variableMap.get(Integer.toString(TermId.ENTRY_NUMBER_STORAGE.getId()));
			if(var != null){
				return var.getValue();
			}
		}
		return null;
	}
	
	private String getTrialInstanceNumber(VariableList trialVariables) {
		Map<String, Variable> variableMap = trialVariables.getVariableMap();
		if(variableMap != null){
			Variable var = variableMap.get(Integer.toString(TermId.TRIAL_INSTANCE_STORAGE.getId()));
			if(var != null){
				return var.getValue();
			}
		}
		return null;
	}
	
	private String generateTrialDatasetName(String studyName, StudyType studyType) {
		return studyName + "-ENVIRONMENT";
	}
	
	private String generateMeasurementEffectDatasetName(String studyName) {
		return studyName + "-PLOTDATA";
	}
	
	private String generateMeansDatasetName(String studyName) {
		return studyName + "-MEANS";
	}
	
	private ExperimentValues createTrialExperimentValues(Integer locationId, VariableList variates) {
        ExperimentValues value = new ExperimentValues();
        value.setLocationId(locationId);
        value.setVariableList(variates);
		return value;
	}
	
	private int createStudyIfNecessary(Workbook workbook, int studyLocationId, boolean saveStudyExperiment) throws Exception {
		TimerWatch watch = new TimerWatch("find study", LOG);
	
		Integer studyId = null;
		if (workbook.getStudyDetails() != null){
		    studyId = getStudyId(workbook.getStudyDetails().getStudyName());
		}

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
		Integer trialDatasetId = null;
 		if(trialName == null || trialName.equals("") ){
 		    List<DatasetReference> datasetRefList = getStudyDataManager().getDatasetReferences(studyId);
 	        if (datasetRefList != null) {
 	            for (DatasetReference datasetRef : datasetRefList) {
 	              if (datasetRef.getName().equals("TRIAL_" + workbook.getStudyDetails().getStudyName())) {
 	                 trialDatasetId = datasetRef.getId();
 	              }
 	            }
 	            if (trialDatasetId == null) {
     	           trialName = generateTrialDatasetName(workbook.getStudyDetails().getStudyName(),workbook.getStudyDetails().getStudyType());
                   trialDatasetId = getDatasetId(trialName, generateTrialDatasetName(workbook.getStudyDetails().getStudyName(),workbook.getStudyDetails().getStudyType()));
 	            }
 	        } else {
 		       trialName = generateTrialDatasetName(workbook.getStudyDetails().getStudyName(),workbook.getStudyDetails().getStudyType());
 		       trialDatasetId = getDatasetId(trialName, generateTrialDatasetName(workbook.getStudyDetails().getStudyName(),workbook.getStudyDetails().getStudyType()));
 	        }
 		}
 		if (trialDatasetId == null) {
			watch.restart("transform trial dataset values");
			DatasetValues trialValues = getDatasetValuesTransformer().transform(trialName, trialName, 
					DataSetType.SUMMARY_DATA, trialMV, trialVariables);
			
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
		getExperimentModelSaver().addExperiment(trialProjectId, ExperimentType.TRIAL_ENVIRONMENT, trialDatasetValues);
		watch.stop();
	}
	
	
	private int createMeasurementEffectDatasetIfNecessary(Workbook workbook, int studyId, 
	List<MeasurementVariable> effectMV, VariableTypeList effectVariables, VariableTypeList trialVariables) throws MiddlewareQueryException, MiddlewareException {

		TimerWatch watch = new TimerWatch("find measurement effect dataset", LOG);
        String datasetName = workbook.getStudyDetails().getMeasurementDatasetName();
        Integer datasetId = null;
		
		if(datasetName == null || datasetName.equals("") ){
		    List<DatasetReference> datasetRefList = getStudyDataManager().getDatasetReferences(studyId);
            if (datasetRefList != null) {
                for (DatasetReference datasetRef : datasetRefList) {
                  if (datasetRef.getName().equals("MEASUREMENT EFEC_" + workbook.getStudyDetails().getStudyName()) || 
                        datasetRef.getName().equals("MEASUREMENT EFECT_" + workbook.getStudyDetails().getStudyName())) {
                      datasetId = datasetRef.getId();
                  }
                }
                if (datasetId == null) {
                    datasetName = generateMeasurementEffectDatasetName(workbook.getStudyDetails().getStudyName());
                    datasetId = getDatasetId(datasetName, generateMeasurementEffectDatasetName(workbook.getStudyDetails().getStudyName()));
                }
            } else {
                datasetName = generateMeasurementEffectDatasetName(workbook.getStudyDetails().getStudyName());
                datasetId = getDatasetId(datasetName, generateMeasurementEffectDatasetName(workbook.getStudyDetails().getStudyName()));
            }
        }

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
	
	public void createStocksIfNecessary(int datasetId, Workbook workbook, VariableTypeList effectVariables,List<String> trialHeaders) throws MiddlewareQueryException {
		Map<String, Integer> stockMap = getStockModelBuilder().getStockMapForDataset(datasetId);

		Session session = getCurrentSessionForLocal();
		int i = 0;
		List<Integer> variableIndexesList = new ArrayList<Integer>();
		//we get the indexes so that in the next rows we dont need to compare anymore per row
		if(workbook.getObservations() != null && workbook.getObservations().size() != 0){
			MeasurementRow row = workbook.getObservations().get(0);
			variableIndexesList  = getVariableListTransformer().transformStockIndexes(row, effectVariables, trialHeaders);
		}
		if (workbook.getObservations() != null) {
        	    for (MeasurementRow row : workbook.getObservations()) {
        	    
        		VariableList stock = getVariableListTransformer().transformStockOptimize(variableIndexesList, row, effectVariables, trialHeaders);
        		String stockFactor = getStockFactor(stock);
        		Integer stockId = stockMap.get(stockFactor);
        		
        		if (stockId == null) {
        			stockId = getStockSaver().saveStock(stock);
        			stockMap.put(stockFactor, stockId);
        		} else {
        			getStockSaver().saveOrUpdateStock(stock, stockId);
        		}
        		row.setStockId(stockId);
        		if ( i % 50 == 0 ) { //to save memory space - http://docs.jboss.org/hibernate/core/3.3/reference/en/html/batch.html#batch-inserts
        		      session.flush();
        		      session.clear();
        		}
        		i++;
        	    }
		}
		
	}
	
	private void createMeasurementEffectExperiments(int datasetId, VariableTypeList effectVariables, 
			List<MeasurementRow> observations, List<String> trialHeaders, Map<Integer,VariableList> trialVariatesMap) throws MiddlewareQueryException {
		
		TimerWatch watch = new TimerWatch("saving stocks and measurement effect data (total)", LOG);
		TimerWatch rowWatch = new TimerWatch("for each row", LOG);
		
		int i = 2;//observation values start at row 2
		Session session = getCurrentSessionForLocal();
		ExperimentValuesTransformer experimentValuesTransformer = getExperimentValuesTransformer();
		ExperimentModelSaver experimentModelSaver = getExperimentModelSaver();
		Map<Integer,PhenotypeExceptionDto> exceptions = null;
		if (observations != null) {
        		for(MeasurementRow row : observations) {
        			rowWatch.restart("saving row "+(i++));
        			ExperimentValues experimentValues = experimentValuesTransformer.transform(row, effectVariables, trialHeaders);
        			VariableList trialVariates = trialVariatesMap.get((int)row.getLocationId());
        			if(trialVariates!=null) {
        				experimentValues.getVariableList().addAll(trialVariates);
        			}
        			try {
        				experimentModelSaver.addExperiment(datasetId, ExperimentType.PLOT, experimentValues);
        			} catch(PhenotypeException e) {
        				if(exceptions==null) {
        					exceptions = e.getExceptions();
        				} else {
        					for (Integer standardVariableId : e.getExceptions().keySet()) {
        						PhenotypeExceptionDto exception = e.getExceptions().get(standardVariableId);
        						if(exceptions.get(standardVariableId)==null) {
        							exceptions.put(standardVariableId, exception);//add exception
        						} else {//add invalid values to the existing map of exceptions for each phenotype
        							for(String invalidValue : exception.getInvalidValues()) {
        								exceptions.get(standardVariableId).getInvalidValues().add(invalidValue);
        							}
        						}
        					}
        				}
        			}
        			if ( i % 50 == 0 ) { //to save memory space - http://docs.jboss.org/hibernate/core/3.3/reference/en/html/batch.html#batch-inserts
        				session.flush();
        				session.clear();
        			}
        		}
		}
		rowWatch.stop();
		watch.stop();
		
		if(exceptions!=null) {
			throw new PhenotypeException(exceptions);
		}
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
		info.setLocalName("TRIAL_INSTANCE");
		info.setLocalDescription("TRIAL_INSTANCE");
		info.setStdVariableId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		info.setRank(rank);
		return getVariableTypeBuilder().create(info);
	}
	
	private VariableTypeList propagateTrialFactorsIfNecessary(VariableTypeList effectVariables, VariableTypeList trialVariables) throws MiddlewareQueryException, MiddlewareException {
		
		VariableTypeList newList = new VariableTypeList();
		
		
		
        if (!isTrialFactorInDataset(effectVariables)) {
            if (trialVariables != null) {
                int index = 1;
                for (VariableType var : trialVariables.getVariableTypes()) {
                    if (var.getId() == TermId.TRIAL_INSTANCE_FACTOR.getId() 
                            || TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId() != var.getStandardVariable().getStoredIn().getId()) {
                        var.setRank(index);
                        newList.add(var);
                        index++;
                    }
                }
            }
            
        	effectVariables.allocateRoom(newList.size());
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
	
	
	private Integer getMeansDataset(Integer studyId) throws MiddlewareQueryException {
		Integer id = null;
		setWorkingDatabase(Database.LOCAL);
		List<DmsProject> datasets = getDmsProjectDao().getDataSetsByStudyAndProjectProperty(
                studyId, TermId.DATASET_TYPE.getId(), String.valueOf(DataSetType.MEANS_DATA.getId()));
		if(datasets!=null && !datasets.isEmpty()) {
			id = datasets.get(0).getProjectId();
		}
		return id;
	}
	
	
	
	/**
     * Saves project ontology creating entries in the following tables:
     * project, projectprop and project_relationship
     * @param workbook
     * @return study id
     * @throws Exception
     */
	@SuppressWarnings("unchecked")
	public int saveProjectOntology(Workbook workbook) throws Exception {
		
		final Map<String, ?> variableMap = saveVariables(workbook);
		workbook.setVariableMap(variableMap);
		
		// unpack maps first level - Maps of Strings, Maps of VariableTypeList , Maps of Lists of MeasurementVariable
		Map<String, VariableTypeList> variableTypeMap = (Map<String, VariableTypeList>) variableMap.get("variableTypeMap");
		Map<String, List<MeasurementVariable>> measurementVariableMap = (Map<String, List<MeasurementVariable>>) variableMap.get("measurementVariableMap");

		// unpack maps
		VariableTypeList trialVariables = new VariableTypeList();
		trialVariables.addAll(variableTypeMap.get("trialVariables"));//addAll instead of assigning directly to avoid changing the state of the object
		VariableTypeList effectVariables = new VariableTypeList();
		effectVariables.addAll(variableTypeMap.get("effectVariables"));//addAll instead of assigning directly to avoid changing the state of the object
		List<MeasurementVariable> trialMV = measurementVariableMap.get("trialMV");
		List<MeasurementVariable> effectMV = measurementVariableMap.get("effectMV");
		
		int studyId = createStudyIfNecessary(workbook, 0, false);//locationId and experiment are not yet needed here
		int trialDatasetId = createTrialDatasetIfNecessary(workbook, studyId, trialMV, trialVariables);
		int measurementDatasetId = 0;
		int meansDatasetId = 0;
		if(workbook.getImportType()!=null && workbook.getImportType().intValue()==DataSetType.MEANS_DATA.getId()) {
			meansDatasetId = createMeansDatasetIfNecessary(workbook, studyId, effectMV, effectVariables, trialVariables);
		} else {
			measurementDatasetId = createMeasurementEffectDatasetIfNecessary(workbook, studyId, effectMV, effectVariables, trialVariables);
		}
		
		workbook.populateStudyAndDatasetIds(studyId, trialDatasetId, measurementDatasetId, meansDatasetId);
		
		if (LOG.isDebugEnabled()){
			LOG.debug("studyId = "+studyId);
            LOG.debug("trialDatasetId = "+trialDatasetId);
            LOG.debug("measurementDatasetId = "+measurementDatasetId);
            LOG.debug("meansDatasetId = "+meansDatasetId);
        }
		
		return studyId;
	}
	
	/**
     * Saves experiments creating entries in the following tables:
     * nd_geolocation, nd_geolocationprop, nd_experiment, nd_experiment_project, nd_experimentprop
     * nd_experiment_stock, stock, stockprop, nd_experiment_phenotype and phenotype
     * @param workbook
     * @throws Exception
     */
	@SuppressWarnings("unchecked")
	public void saveProjectData(Workbook workbook) throws Exception {
		
		int studyId = workbook.getStudyId();
		int trialDatasetId = workbook.getTrialDatasetId();
		int measurementDatasetId = workbook.getMeasurementDatesetId()!=null?workbook.getMeasurementDatesetId():0;
		int meansDatasetId = workbook.getMeansDatasetId()!=null?workbook.getMeansDatasetId():0;
		boolean isMeansDataImport = workbook.getImportType()!=null && workbook.getImportType().intValue()==DataSetType.MEANS_DATA.getId();
		
		Map<String, ?> variableMap = workbook.getVariableMap();
		if(variableMap==null || variableMap.isEmpty()) {
			variableMap = saveVariables(workbook);
		}
		
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
		
       	//GCP-8092 Nurseries will always have a unique geolocation, no more concept of shared/common geolocation
		//create locations (entries to nd_geolocation) and associate to observations
        int studyLocationId/* = DEFAULT_GEOLOCATION_ID*/;
        List<Integer> locationIds = new ArrayList<Integer>();
        Map<Integer,VariableList> trialVariatesMap = new HashMap<Integer,VariableList>();
        if(trialVariableTypeList!=null) {//multi-location
   			studyLocationId = createLocationsAndSetToObservations(locationIds,workbook,trialVariableTypeList,trialHeaders, trialVariatesMap, false);
        } else {
        	studyLocationId = createLocationAndSetToObservations(workbook, trialMV, trialVariables, trialVariatesMap, false);
        }
		
        //create stock and stockprops and associate to observations
        int datasetId = measurementDatasetId;
        if(isMeansDataImport) {
        	datasetId = meansDatasetId;
        }
        createStocksIfNecessary(datasetId, workbook, effectVariables, trialHeaders);
        
        //create trial experiments if not yet existing
        boolean hasExistingStudyExperiment = checkIfHasExistingStudyExperiment(studyId);
        boolean hasExistingTrialExperiments = checkIfHasExistingExperiments(locationIds);
        if(!hasExistingStudyExperiment) {
			//1. study experiment
	        StudyValues values = new StudyValues();
	        values.setLocationId(studyLocationId);
	        getStudySaver().saveStudyExperiment(studyId, values);
        }
        //create trial experiments if not yet existing        
        if(!hasExistingTrialExperiments) {
	        //2. trial experiments
	        if(trialVariableTypeList!=null) {//multi-location
	   			for(Integer locationId : locationIds) {
	   				createTrialExperiment(trialDatasetId, locationId, trialVariatesMap.get(locationId));
	   			}
	   		} else {
	   			createTrialExperiment(trialDatasetId, studyLocationId, trialVariatesMap.get(studyLocationId));
	   		}
        }
        if(isMeansDataImport) {
        	//3. means experiments
        	createMeansExperiments(meansDatasetId, effectVariables,  workbook.getObservations(), trialHeaders, trialVariatesMap);
        } else {
        	//3. measurement experiments
        	createMeasurementEffectExperiments(measurementDatasetId, effectVariables,  workbook.getObservations(), trialHeaders, trialVariatesMap);
        }
	}
	
	private boolean checkIfHasExistingStudyExperiment(int studyId) throws MiddlewareQueryException {
		setWorkingDatabase(Database.LOCAL);
        Integer experimentId = getExperimentProjectDao().getExperimentIdByProjectId(studyId);
        if(experimentId!=null) {
        	return true;
        }
        return false;
	}

	private boolean checkIfHasExistingExperiments(List<Integer> locationIds) throws MiddlewareQueryException {
		setWorkingDatabase(Database.LOCAL);
        List<Integer> experimentIds = getExperimentDao().getExperimentIdsByGeolocationIds(locationIds);
        if(experimentIds!=null && !experimentIds.isEmpty()) {
        	return true;
        }
        return false;
	}

	private VariableList createDefaultGeolocationVariableList() throws MiddlewareQueryException {
		VariableList list = new VariableList();
		
		VariableType variableType = new VariableType(PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().get(0)
				, PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().get(0)
				, getStandardVariableBuilder().create(TermId.TRIAL_INSTANCE_FACTOR.getId())
				, 1);
		Variable variable = new Variable(variableType, "1");
		list.add(variable);
		
		return list;
	}
	
	public void saveWorkbookVariables(Workbook workbook) throws MiddlewareQueryException {		
		setWorkingDatabase(Database.LOCAL);
		getProjectRelationshipSaver().saveOrUpdateStudyToFolder(workbook.getStudyDetails().getId(), 
				Long.valueOf(workbook.getStudyDetails().getParentFolderId()).intValue());
		setWorkingDatabase(Database.LOCAL);
		DmsProject study = getDmsProjectDao().getById(workbook.getStudyDetails().getId());
		Integer trialDatasetId = workbook.getTrialDatasetId(), measurementDatasetId = workbook.getMeasurementDatesetId();
		if (workbook.getTrialDatasetId() == null || workbook.getMeasurementDatesetId() == null) {
			measurementDatasetId = getWorkbookBuilder().getMeasurementDataSetId(study.getProjectId(), workbook.getStudyName());
			List<DmsProject> datasets = getProjectRelationshipDao().getSubjectsByObjectIdAndTypeId(study.getProjectId(), TermId.BELONGS_TO_STUDY.getId());
			if (datasets != null) {
				for (DmsProject dataset : datasets) {
					if (!dataset.getProjectId().equals(measurementDatasetId)) {
						trialDatasetId = dataset.getProjectId();
						break;
					}
				}
			}
		}
		DmsProject trialDataset = getDmsProjectDao().getById(trialDatasetId);
		DmsProject measurementDataset = getDmsProjectDao().getById(measurementDatasetId);
		
		getProjectPropertySaver().saveProjectProperties(study, trialDataset, measurementDataset, workbook.getConditions(), false);
		getProjectPropertySaver().saveProjectProperties(study, trialDataset, measurementDataset, workbook.getConstants(), true);
		getProjectPropertySaver().saveProjectProperties(study, trialDataset, measurementDataset, workbook.getVariates(), false);
		getProjectPropertySaver().saveFactors(measurementDataset, workbook.getFactors());
	}
	
	private int createMeansDatasetIfNecessary(Workbook workbook, int studyId, 
			List<MeasurementVariable> effectMV, VariableTypeList effectVariables, VariableTypeList trialVariables) throws MiddlewareQueryException, MiddlewareException {

		TimerWatch watch = new TimerWatch("find means dataset", LOG);
        Integer datasetId = getMeansDataset(studyId);
		
		if (datasetId == null) {
	        watch.restart("transform means dataset");
	        String datasetName = generateMeansDatasetName(workbook.getStudyDetails().getStudyName());
	        DatasetValues datasetValues = getDatasetValuesTransformer().transform(datasetName, datasetName,
											DataSetType.MEANS_DATA, effectMV, effectVariables);

			watch.restart("save means dataset");
			VariableTypeList datasetVariables = getMeansData(effectVariables, trialVariables);
			DmsProject dataset = getDatasetProjectSaver().addDataSet(studyId, datasetVariables, datasetValues);
			datasetId = dataset.getProjectId();
		}
		
		watch.stop();
		return datasetId;
	}
	
	private VariableTypeList getMeansData(VariableTypeList effectVariables, VariableTypeList trialVariables) throws MiddlewareQueryException, MiddlewareException {
		
		VariableTypeList newList = new VariableTypeList();
		int rank = 1;
		for (VariableType var : trialVariables.getVariableTypes()) {
			var.setRank(rank++);
			newList.add(var);
		}
		for (VariableType var : effectVariables.getVariableTypes()) {
			var.setRank(rank++);
			newList.add(var);
		}
		return newList;
	}
	
	private void createMeansExperiments(int datasetId, VariableTypeList effectVariables, 
			List<MeasurementRow> observations, List<String> trialHeaders, Map<Integer,VariableList> trialVariatesMap) throws MiddlewareQueryException {
		
		TimerWatch watch = new TimerWatch("saving means data (total)", LOG);
		TimerWatch rowWatch = new TimerWatch("for each row", LOG);
		
		int i = 2;//observation values start at row 2
		Session session = getCurrentSessionForLocal();
		ExperimentValuesTransformer experimentValuesTransformer = getExperimentValuesTransformer();
		ExperimentModelSaver experimentModelSaver = getExperimentModelSaver();
		Map<Integer,PhenotypeExceptionDto> exceptions = null;
		if (observations != null) {
        		for(MeasurementRow row : observations) {
        			rowWatch.restart("saving row "+(i++));
        			ExperimentValues experimentValues = experimentValuesTransformer.transform(row, effectVariables, trialHeaders);
        			VariableList trialVariates = trialVariatesMap.get((int)row.getLocationId());
        			if(trialVariates!=null) {
        				experimentValues.getVariableList().addAll(trialVariates);
        			}
        			try {
        				experimentModelSaver.addExperiment(datasetId, ExperimentType.AVERAGE, experimentValues);
        			} catch(PhenotypeException e) {
        				if(exceptions==null) {
        					exceptions = e.getExceptions();
        				} else {
        					for (Integer standardVariableId : e.getExceptions().keySet()) {
        						PhenotypeExceptionDto exception = e.getExceptions().get(standardVariableId);
        						if(exceptions.get(standardVariableId)==null) {
        							exceptions.put(standardVariableId, exception);//add exception
        						} else {//add invalid values to the existing map of exceptions for each phenotype
        							for(String invalidValue : exception.getInvalidValues()) {
        								exceptions.get(standardVariableId).getInvalidValues().add(invalidValue);
        							}
        						}
        					}
        				}
        			}
        			if ( i % 50 == 0 ) { //to save memory space - http://docs.jboss.org/hibernate/core/3.3/reference/en/html/batch.html#batch-inserts
        				session.flush();
        				session.clear();
        			}
        		}
		}
		rowWatch.stop();
		watch.stop();
		
		if(exceptions!=null) {
			throw new PhenotypeException(exceptions);
		}
	}
}
