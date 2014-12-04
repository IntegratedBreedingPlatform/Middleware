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

import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.domain.dms.*;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.etl.*;
import org.generationcp.middleware.domain.fieldbook.NonEditableFactors;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.ErrorCode;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.ProjectProperty;

import java.util.*;

public class WorkbookBuilder extends Builder {
	
	private Map<String, String> labelMap = new HashMap<String, String> ();
	private static final List<Integer> EXPERIMENTAL_DESIGN_VARIABLES = Arrays.asList(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), 
			TermId.NUMBER_OF_REPLICATES.getId(), TermId.BLOCK_SIZE.getId(), TermId.BLOCKS_PER_REPLICATE.getId(), 
			TermId.REPLICATIONS_MAP.getId(), TermId.NO_OF_REPS_IN_COLS.getId(), TermId.NO_OF_ROWS_IN_REPS.getId(),
			TermId.NO_OF_COLS_IN_REPS.getId(), TermId.NO_OF_CROWS_LATINIZE.getId(), TermId.NO_OF_CCOLS_LATINIZE.getId(), 
			TermId.NO_OF_CBLKS_LATINIZE.getId());
	
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
         */
		
		StudyDetails studyDetails = null;
		if (id < 0) {
		    studyDetails = getStudyDataManager().getStudyDetails(Database.LOCAL, studyType, id);
		} else {
		    studyDetails = getStudyDataManager().getStudyDetails(Database.CENTRAL, studyType, id);
		}
		
		Study study = getStudyBuilder().createStudy(id);
		
		int dataSetId = getMeasurementDataSetId(id, studyDetails.getStudyName());
		checkMeasurementDataset(Integer.valueOf(dataSetId));
		workbook.setMeasurementDatesetId(dataSetId);
		
		long expCount = getStudyDataManager().countExperiments(dataSetId);
		VariableTypeList variables = getDataSetBuilder().getVariableTypes(dataSetId);
		List<Experiment> experiments = getStudyDataManager().getExperiments(dataSetId, 0, (int)expCount, variables);

		VariableList conditionVariables = null, constantVariables = null, trialConstantVariables = null;
		if (isTrial) {
			conditionVariables = new VariableList();
			conditionVariables.addAll(study.getConditions());
			conditionVariables.addAll(getSingleRowOfEmptyTrialVariables(workbook, study.getId(), dataSetId));
			
			constantVariables = study.getConstants();
			trialConstantVariables = getTrialConstants(workbook.getTrialDatasetId());
			
			variables = removeTrialDatasetVariables(variables, conditionVariables, constantVariables);
		} else {
			getSingleRowOfEmptyTrialVariables(workbook, study.getId(), dataSetId);
			conditionVariables = study.getConditions();
			constantVariables = study.getConstants();
			trialConstantVariables = getTrialConstants(workbook.getTrialDatasetId());
		}
		List<MeasurementVariable> conditions = buildStudyMeasurementVariables(conditionVariables, true, true);
		List<MeasurementVariable> factors = buildFactors(variables, isTrial);		
		List<MeasurementVariable> constants = buildStudyMeasurementVariables(constantVariables, false, true);
		constants.addAll(buildStudyMeasurementVariables(trialConstantVariables, false, false));
		List<MeasurementVariable> variates = buildVariates(variables, constants);
		List<MeasurementVariable> expDesignVariables = new ArrayList<MeasurementVariable>();
		
		//set possible values of breeding method
		for (MeasurementVariable variable : variates) {
		    if (getOntologyDataManager().getProperty(variable.getProperty()).getTerm().getId() == TermId.BREEDING_METHOD_PROP.getId()) {
		        variable.setPossibleValues(getAllBreedingMethods());
		    }
		}
		
		if(!isTrial){
			//remove OCC from nursery level conditions for nursery cause its duplicating becuase its being added in conditions and factors
			Iterator<MeasurementVariable> iter = conditions.iterator();
			while(iter.hasNext()) {
				if (iter.next().getTermId() == TermId.TRIAL_INSTANCE_FACTOR.getId()) {
					iter.remove();
				}
			}
		}
		
		List<MeasurementRow> observations = buildObservations(experiments, variables.getVariates(), factors, variates, isTrial, conditions);
		List<TreatmentVariable> treatmentFactors = buildTreatmentFactors(variables);
		List<ProjectProperty> projectProperties = getDataSetBuilder().getTrialDataset(id, dataSetId).getProperties();
		
		for (ProjectProperty projectProperty : projectProperties) {
                if (projectProperty.getTypeId().equals(TermId.STANDARD_VARIABLE.getId())) {
                    StandardVariable stdVariable = getStandardVariableBuilder().create(Integer.parseInt(projectProperty.getValue()));
                    if (!isTrial && PhenotypicType.TRIAL_ENVIRONMENT.getTypeStorages().contains(stdVariable.getStoredIn().getId())) {
                    	
                        String label = getLabelOfStoredIn(stdVariable.getStoredIn().getId());
                        
                        Double minRange = null, maxRange = null;
                        if (stdVariable.getConstraints() != null) {
                                minRange = stdVariable.getConstraints().getMinValue();
                                maxRange = stdVariable.getConstraints().getMaxValue();
                        }
                        
                        String value = null;
                        if (stdVariable.getStoredIn().getId() == TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId()) {
                        	value = getStudyDataManager().getGeolocationPropValue(Database.LOCAL, stdVariable.getId(), id);
                        } else if (!isTrial) { 
                        	//set trial env for nursery studies
                        	setWorkingDatabase(id);
                        	List<Integer> locIds = getExperimentDao().getLocationIdsOfStudy(id);
                        	if (locIds != null && !locIds.isEmpty()) {
                        		Integer locId = locIds.get(0);
                        		Geolocation geolocation = getGeolocationDao().getById(locId);
                        		int storedInId = stdVariable.getStoredIn().getId();
                        		if (geolocation != null) {
                        			if (TermId.TRIAL_INSTANCE_STORAGE.getId() == storedInId) {
                        				value = geolocation.getDescription();
                        				
                        			} else if (TermId.LATITUDE_STORAGE.getId() == storedInId && geolocation.getLatitude() != null) {
                        				value = geolocation.getLatitude().toString();
                        				
                        			} else if (TermId.LONGITUDE_STORAGE.getId() == storedInId && geolocation.getLongitude() != null) {
                        				value = geolocation.getLongitude().toString();
                        				
                        			} else if (TermId.DATUM_STORAGE.getId() == storedInId && geolocation.getGeodeticDatum() != null) {
                        				geolocation.setGeodeticDatum(value);
                        				
                        			} else if (TermId.ALTITUDE_STORAGE.getId() == storedInId && geolocation.getAltitude() != null) {
                        				value = geolocation.getAltitude().toString();
                        			}	
                        		}
                        	}
                        	if (value == null) {
                        		value = "";
                        	}
                        }
                        
                        if (value != null) {
	                        MeasurementVariable measurementVariable = new MeasurementVariable(stdVariable.getId(), getLocalName(projectProperty.getRank(), projectProperties),//projectProperty.getValue(), 
	                                stdVariable.getDescription(), stdVariable.getScale().getName(), stdVariable.getMethod().getName(),
	                                stdVariable.getProperty().getName(), stdVariable.getDataType().getName(), 
	                                value, 
	                                label, minRange, maxRange);
	                        measurementVariable.setStoredIn(stdVariable.getStoredIn().getId());
	                        measurementVariable.setFactor(true);
	                        measurementVariable.setDataTypeId(stdVariable.getDataType().getId());
	                        measurementVariable.setPossibleValues(getMeasurementVariableTransformer().transformPossibleValues(stdVariable.getEnumerations()));
	                        
	                        if (EXPERIMENTAL_DESIGN_VARIABLES.contains(stdVariable.getId())) {
	                        	expDesignVariables.add(measurementVariable);
	                        } else {
	                        	conditions.add(measurementVariable);
	                        }
                        }
                    } else if (isTrial && stdVariable.getStoredIn().getId() == TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId()
                    		&& EXPERIMENTAL_DESIGN_VARIABLES.contains(stdVariable.getId())) {
                    	
                        String label = getLabelOfStoredIn(stdVariable.getStoredIn().getId());
                        String value = getStudyDataManager().getGeolocationPropValue(Database.LOCAL, stdVariable.getId(), id);
                        
                        Double minRange = null, maxRange = null;
                        if (stdVariable.getConstraints() != null) {
                                minRange = stdVariable.getConstraints().getMinValue();
                                maxRange = stdVariable.getConstraints().getMaxValue();
                        }
                        MeasurementVariable measurementVariable = new MeasurementVariable(stdVariable.getId(), getLocalName(projectProperty.getRank(), projectProperties),//projectProperty.getValue(), 
                                stdVariable.getDescription(), stdVariable.getScale().getName(), stdVariable.getMethod().getName(),
                                stdVariable.getProperty().getName(), stdVariable.getDataType().getName(), 
                                value, 
                                label, minRange, maxRange);
                        measurementVariable.setStoredIn(stdVariable.getStoredIn().getId());
                        measurementVariable.setFactor(true);
                        measurementVariable.setDataTypeId(stdVariable.getDataType().getId());
                        measurementVariable.setPossibleValues(getMeasurementVariableTransformer().transformPossibleValues(stdVariable.getEnumerations()));

                        expDesignVariables.add(measurementVariable);
                        setValueInCondition(conditions, value, stdVariable.getId());
                    }
                }
	        }
		
		workbook.setStudyDetails(studyDetails);
		workbook.setFactors(factors);
		workbook.setVariates(variates);
		workbook.setConditions(conditions);
		workbook.setConstants(constants);
		workbook.setObservations(observations);
		workbook.setTreatmentFactors(treatmentFactors);
		workbook.setExperimentalDesignVariables(expDesignVariables);
		
		List<MeasurementRow> trialObservations = getTrialObservations(workbook, isTrial); 
		
		workbook.setTrialObservations(trialObservations);
		return workbook;
	}
	
	private List<MeasurementRow> getTrialObservations(Workbook workbook, boolean isTrial) throws MiddlewareQueryException {
		List<MeasurementRow> trialObservations = null;
		if (!isTrial) {
	        trialObservations = buildTrialObservations(workbook.getTrialDatasetId(), workbook.getTrialConditions(), workbook.getTrialConstants());
		} else {
			trialObservations = getDataSetBuilder().buildCompleteDataset(workbook.getTrialDatasetId(), isTrial).getObservations();
		}
		return trialObservations;
	}

	protected void checkMeasurementDataset(Integer dataSetId) throws MiddlewareQueryException{
		//if study has no measurementDataset, throw an error as it is an invalid template
		if (dataSetId == null || dataSetId.equals(0)) {
        	throw new MiddlewareQueryException(ErrorCode.STUDY_FORMAT_INVALID.getCode(), "The term you entered is invalid");
        }
	}
	
	private void setValueInCondition(List<MeasurementVariable> conditions, String value, int id) {
		if (conditions != null && !conditions.isEmpty()) {
			for (MeasurementVariable condition : conditions) {
				if (condition.getTermId() == id) {
					condition.setValue(value);
					break;
				}
			}
		}
	}
	
	public Workbook createStudyVariableSettings(int id, boolean isNursery) throws MiddlewareQueryException {
            Workbook workbook = new Workbook();
            Study study = getStudyBuilder().createStudy(id);
            Integer dataSetId = null, trialDatasetId = null;
            //get observation dataset
            if (dataSetId == null) {
                List<DatasetReference> datasetRefList = getStudyDataManager().getDatasetReferences(id);
                if (datasetRefList != null) {
                	StudyType studyType = StudyType.N;
                	if(!isNursery) {
                		studyType = StudyType.T;
                	}
                	Database database = id > 0 ? Database.CENTRAL : Database.LOCAL;
                    StudyDetails studyDetails = getStudyDataManager().getStudyDetails(database, studyType, id);
                    workbook.setStudyDetails(studyDetails);
                    for (DatasetReference datasetRef : datasetRefList) {
                        if (datasetRef.getName().equals("MEASUREMENT EFEC_" + studyDetails.getStudyName()) || 
                                datasetRef.getName().equals("MEASUREMENT EFECT_" + studyDetails.getStudyName())) {
                            dataSetId = datasetRef.getId();
                        } else if (datasetRef.getName().equals("TRIAL_" + studyDetails.getStudyName())) {
                        	trialDatasetId = datasetRef.getId();
                        }
                    }
                }
            }
            
            //if dataset is not found, get dataset with Plot Data type
            if (dataSetId == null || dataSetId == 0) {
            	DataSet dataset = getStudyDataManager().findOneDataSetByType(id, DataSetType.PLOT_DATA);
            	if (dataset != null) {
            		dataSetId = dataset.getId();
            	}
            }
            
            if (trialDatasetId == null || trialDatasetId == 0) {
            	DataSet dataset = getStudyDataManager().findOneDataSetByType(id, DataSetType.SUMMARY_DATA);
            	if (dataset != null) {
            		trialDatasetId = dataset.getId();
            	}
            }

            checkMeasurementDataset(dataSetId);
            
            workbook.setMeasurementDatesetId(dataSetId);
            workbook.setTrialDatasetId(trialDatasetId);
            
            VariableTypeList variables = null;
            if (dataSetId != null) {
            	variables = getDataSetBuilder().getVariableTypes(dataSetId);
            }
            
            List<MeasurementVariable> factors = buildFactors(variables, !isNursery);
            List<MeasurementVariable> variates = buildVariates(variables);
            List<MeasurementVariable> conditions = buildStudyMeasurementVariables(study.getConditions(), true, true);
            List<MeasurementVariable> constants = buildStudyMeasurementVariables(study.getConstants(), false, true);
            List<TreatmentVariable> treatmentFactors = buildTreatmentFactors(variables);
            if(dataSetId != null){
            	setTreatmentFactorValues(treatmentFactors, dataSetId);
            }
            DmsProject dmsProject = getDataSetBuilder().getTrialDataset(id, dataSetId != null ? dataSetId : 0);
            List<MeasurementVariable> experimentalDesignVariables = new ArrayList<MeasurementVariable>();
            List<ProjectProperty> projectProperties = dmsProject != null ?  dmsProject.getProperties() : new ArrayList<ProjectProperty>();
            
            for (ProjectProperty projectProperty : projectProperties) {
            	boolean isConstant = false;
                if (projectProperty.getTypeId().equals(TermId.STANDARD_VARIABLE.getId())) {
                    StandardVariable stdVariable = getStandardVariableBuilder().create(Integer.parseInt(projectProperty.getValue()));
                    if (PhenotypicType.TRIAL_ENVIRONMENT.getTypeStorages().contains(stdVariable.getStoredIn().getId())
                    		|| PhenotypicType.VARIATE.getTypeStorages().contains(stdVariable.getStoredIn().getId())) {
                    	
                        String label = getLabelOfStoredIn(stdVariable.getStoredIn().getId());
                        
                        Double minRange = null, maxRange = null;
                        if (stdVariable.getConstraints() != null) {
                        	minRange = stdVariable.getConstraints().getMaxValue();
                        	maxRange = stdVariable.getConstraints().getMaxValue();
                        }
                        
                        String value = null;
                        if (stdVariable.getStoredIn().getId() == TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId()) {
                        	value = getStudyDataManager().getGeolocationPropValue(Database.LOCAL, stdVariable.getId(), id);
                        	if (value == null) {
                        		value = "";
                        	}
                        } else if (PhenotypicType.VARIATE.getTypeStorages().contains(stdVariable.getStoredIn().getId())) {
                        	//constants, no need to retrieve the value if it's a trial study
                        	isConstant = true;
                        	if (isNursery) {
                        		List<Phenotype> phenotypes = getPhenotypeDao().getByProjectAndType(trialDatasetId, stdVariable.getId());
                        		//expects only 1 value for nursery
                        		if (phenotypes != null && !phenotypes.isEmpty()) {
                        			if (phenotypes.get(0).getcValueId() != null) {
                        				//categorical constant
                        				Enumeration enumeration = stdVariable.getEnumeration(phenotypes.get(0).getcValueId());
                        				value = enumeration.getDescription();
                        			} else {
                        				value = phenotypes.get(0).getValue();
                        			}
                        		}
                        		if (value == null) {
                        			value = "";
                        		}
                        	} else {
                        		value = "";
                        	}
                        } else { 
                        	//set trial env for nursery studies
                        	setWorkingDatabase(id);
                        	List<Integer> locIds = getExperimentDao().getLocationIdsOfStudy(id);
                        	if (locIds != null && !locIds.isEmpty()) {
                        		Integer locId = locIds.get(0);
                        		Geolocation geolocation = getGeolocationDao().getById(locId);
                        		int storedInId = stdVariable.getStoredIn().getId();
                        		if (geolocation != null) {
                        			if (TermId.TRIAL_INSTANCE_STORAGE.getId() == storedInId) {
                        				value = geolocation.getDescription();
                        				
                        			} else if (TermId.LATITUDE_STORAGE.getId() == storedInId && geolocation.getLatitude() != null) {
                        				value = geolocation.getLatitude().toString();
                        				
                        			} else if (TermId.LONGITUDE_STORAGE.getId() == storedInId && geolocation.getLongitude() != null) {
                        				value = geolocation.getLongitude().toString();
                        				
                        			} else if (TermId.DATUM_STORAGE.getId() == storedInId && geolocation.getGeodeticDatum() != null) {
                        				geolocation.setGeodeticDatum(value);
                        				
                        			} else if (TermId.ALTITUDE_STORAGE.getId() == storedInId && geolocation.getAltitude() != null) {
                        				value = geolocation.getAltitude().toString();
                        			}	
                        		}
                        	}
                        	if (value == null) {
                        		value = "";
                        	}
                        }

                        if (value != null) {
	                        MeasurementVariable measurementVariable = new MeasurementVariable(stdVariable.getId(), getLocalName(projectProperty.getRank(), projectProperties),//projectProperty.getValue(), 
	                                stdVariable.getDescription(), stdVariable.getScale().getName(), stdVariable.getMethod().getName(),
	                                stdVariable.getProperty().getName(), stdVariable.getDataType().getName(), 
	                                value, 
	                                label, minRange, maxRange);
	                        measurementVariable.setStoredIn(stdVariable.getStoredIn().getId());
	                        measurementVariable.setFactor(true);
	                        measurementVariable.setDataTypeId(stdVariable.getDataType().getId());
	                        measurementVariable.setPossibleValues(getMeasurementVariableTransformer().transformPossibleValues(stdVariable.getEnumerations()));
	                        
	                        if (EXPERIMENTAL_DESIGN_VARIABLES.contains(stdVariable.getId())) {
	                        	experimentalDesignVariables.add(measurementVariable);
	                        } else if (isConstant) {
	                        	constants.add(measurementVariable);
	                        } else {
	                        	conditions.add(measurementVariable);
	                        }
                        }
                    }
                }
            }
            
            variates = removeConstantsFromVariates(variates, constants);
            workbook.setFactors(factors);
            workbook.setVariates(variates);
            workbook.setConditions(conditions);
            workbook.setConstants(constants);
            workbook.setTreatmentFactors(treatmentFactors);
            workbook.setExperimentalDesignVariables(experimentalDesignVariables);
            return workbook;
	}
	
	private List<MeasurementRow> buildObservations(List<Experiment> experiments, VariableTypeList variateTypes,
			List<MeasurementVariable> factorList, List<MeasurementVariable> variateList, boolean isTrial, 
			List<MeasurementVariable> conditionList) {
		
	    List<MeasurementRow> observations = new ArrayList<MeasurementRow>();
	    for (Experiment experiment : experiments) {
	        int experimentId = experiment.getId();
	        VariableList factors = experiment.getFactors();
	        VariableList variates = getCompleteVariatesInExperiment(experiment, variateTypes);
	        List<MeasurementData> measurementDataList = new ArrayList<MeasurementData>();
	        
	        if (isTrial) {
    	        for (MeasurementVariable condition : conditionList) {
    	            for (Variable variable : factors.getVariables()) {
    	                if (condition.getTermId() == variable.getVariableType().getStandardVariable().getId() &&
    	                        variable.getVariableType().getStandardVariable().getId() == TermId.TRIAL_INSTANCE_FACTOR.getId()) {
    	                    boolean isEditable = NonEditableFactors.find(variable.getVariableType().getStandardVariable().getId()) == null ? true : false;
                            MeasurementData measurementData = null;
                            measurementData = new MeasurementData(variable.getVariableType().getLocalName(), 
                                    variable.getValue(), isEditable, 
                                    getDataType(variable.getVariableType().getStandardVariable().getDataType().getId()),
                                    condition);
                            measurementDataList.add(measurementData);
                            break;
    	                }
    	            }
    	        }
	        }
	        for (MeasurementVariable factor : factorList) {
	        	boolean found = false;
		        for (Variable variable : factors.getVariables()) {
		        	if (factor.getTermId() == variable.getVariableType().getStandardVariable().getId()) {
		        		found = true;
			        	if (isTrial && 
			        			variable.getVariableType().getStandardVariable().getId() == TermId.TRIAL_INSTANCE_FACTOR.getId()
			        			|| !PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().contains(getLabelOfStoredIn(variable.getVariableType().getStandardVariable().getStoredIn().getId()))) {
			        		boolean isEditable = NonEditableFactors.find(variable.getVariableType().getStandardVariable().getId()) == null ? true : false;
			            	MeasurementData measurementData = null;
			            	if (variable.getVariableType().getStandardVariable().getDataType().getId() == TermId.CATEGORICAL_VARIABLE.getId()) {
			            		Integer id = variable.getValue() != null && NumberUtils.isNumber(variable.getValue()) ? Integer.valueOf(variable.getValue()) : null;
		                        measurementData = new MeasurementData(variable.getVariableType().getLocalName(), 
		                        		variable.getDisplayValue(), isEditable, 
		                                getDataType(variable.getVariableType().getStandardVariable().getDataType().getId()),
		                                id,
		                                factor);
			            	} else {
		                        measurementData = new MeasurementData(variable.getVariableType().getLocalName(), 
		                                variable.getValue(), isEditable, 
		                                getDataType(variable.getVariableType().getStandardVariable().getDataType().getId()),
		                                factor);
			            	}
			            	measurementDataList.add(measurementData);
			            	break;
			            }
		        	}
		        }
		        if (!found) {
	        		boolean isEditable = NonEditableFactors.find(factor.getTermId()) == null ? true : false;
		        	MeasurementData measurementData = new MeasurementData(factor.getName(), null, isEditable, 
		        			getDataType(factor.getDataTypeId()), factor.getTermId(), factor);
		        	measurementDataList.add(measurementData);
		        }
	        }
	        
	        
	        
	        populateMeasurementData(variateList, variates, measurementDataList);
	        
	        MeasurementRow measurementRow = new MeasurementRow(measurementDataList);
	        measurementRow.setExperimentId(experimentId);
	        measurementRow.setLocationId(experiment.getLocationId());
	        
	        observations.add(measurementRow);
	    }
	    
	    return observations;
	}

	private void populateMeasurementData(List<MeasurementVariable> variateList,
			VariableList variates, List<MeasurementData> measurementDataList) {
		for (MeasurementVariable variate : variateList) {
			boolean found = false;
			
			for (Variable variable : variates.getVariables()) {
				if (variate.getTermId() == variable.getVariableType().getStandardVariable().getId()) {
					found = true;
		            MeasurementData measurementData = new MeasurementData(variable.getVariableType().getLocalName(), 
		                    variable.getValue(), true,  
		                    getDataType(variable.getVariableType().getStandardVariable().getDataType().getId()),
		                    variate);
		            measurementData.setPhenotypeId(variable.getPhenotypeId());
		            if(!variable.isCustomValue() && NumberUtils.isNumber(variable.getValue())){
		            	//we set the cValue id if the isCustomValue flag is false, since this is an id of the valid value
		            	//we check if its a number to be sure
		            	measurementData.setcValueId(variable.getValue());
		            	//measurementData.setValue(null);
		            }
		            measurementDataList.add(measurementData);
		            break;
				}
		    }
			if (!found) {
				MeasurementData measurementData = new MeasurementData(variate.getName(), null, true,
						getDataType(variate.getDataTypeId()), variate);
				measurementDataList.add(measurementData);
			}
		}
	}
	
	private List<ValueReference> getAllBreedingMethods() throws MiddlewareQueryException{
            List<ValueReference> list = new ArrayList<ValueReference>();
            List<Method> methodList = getGermplasmDataManager().getAllMethodsNotGenerative();
            
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
                        list.add(new ValueReference(method.getMid(), method.getMname() + " - " + method.getMcode(), method.getMname() + " - " + method.getMcode()));
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
	
	private List<MeasurementVariable> buildStudyMeasurementVariables(VariableList variableList, boolean isFactor, boolean isStudy) {
		return getMeasurementVariableTransformer().transform(variableList, isFactor, isStudy);
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
            		} else {
            			treatment.setValueVariable(factor);
            		}
            	}
                treatmentFactors.add(treatment);
            }
        }
		
		return treatmentFactors;
	}
	
	private List<MeasurementVariable> buildFactors(VariableTypeList variables, boolean isTrial) {
            List<MeasurementVariable> factors = new ArrayList<MeasurementVariable>();
            VariableTypeList factorList = new VariableTypeList();
            if (variables != null && variables.getFactors() != null && !variables.getFactors().getVariableTypes().isEmpty()) {
                
                for (VariableType variable : variables.getFactors().getVariableTypes()) {
                    if ((isTrial && 
                            variable.getStandardVariable().getId() == TermId.TRIAL_INSTANCE_FACTOR.getId()) ||
                            (PhenotypicType.TRIAL_DESIGN.getLabelList().contains(getLabelOfStoredIn(variable.getStandardVariable().getStoredIn().getId()))
                            || PhenotypicType.GERMPLASM.getLabelList().contains(getLabelOfStoredIn(variable.getStandardVariable().getStoredIn().getId()))
                            || PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().contains(getLabelOfStoredIn(variable.getStandardVariable().getStoredIn().getId())))
                    		) {
                    	
                        factorList.add(variable);
                    }
                }
                factors = getMeasurementVariableTransformer().transform(factorList, true);
            }         
            return factors;
   }

	private List<MeasurementVariable> removeConstantsFromVariates(List<MeasurementVariable> variates, List<MeasurementVariable> constants) {
		List<MeasurementVariable> newVariates = new ArrayList<MeasurementVariable>();
		if (variates != null && !variates.isEmpty()) {
			for (MeasurementVariable variate : variates) {
				boolean found = false;
				if (constants != null && !constants.isEmpty()) {
					for (MeasurementVariable constant : constants) {
						if (variate.getTermId() == constant.getTermId()) {
							found = true;
						}
					}
				}
				if (!found) {
					newVariates.add(variate);
				}
			}
		}
		return newVariates;
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
		    } else {
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

	public List<MeasurementRow> buildTrialObservations(int trialDatasetId, List<MeasurementVariable> factorList, List<MeasurementVariable> variateList)
	throws MiddlewareQueryException {
		
		int totalRows = (int) getStudyDataManager().countExperiments(trialDatasetId);
		List<Experiment> experiments = getStudyDataManager().getExperiments(trialDatasetId, 0, totalRows);
		
		List<MeasurementRow> rows = new ArrayList<MeasurementRow>();
		if (experiments != null) {
			for (Experiment experiment : experiments) {
				List<MeasurementData> dataList = new ArrayList<MeasurementData>();
				for (Variable variable : experiment.getFactors().getVariables()) {
					MeasurementData measurementData = null;
					MeasurementVariable measurementVariable = getMeasurementVariableByName(variable.getVariableType().getLocalName(), factorList); 
	            	if (variable.getVariableType().getStandardVariable().getDataType().getId() == TermId.CATEGORICAL_VARIABLE.getId()) {
	            		Integer id = variable.getValue() != null && NumberUtils.isNumber(variable.getValue()) ? Integer.valueOf(variable.getValue()) : null;
                        measurementData = new MeasurementData(variable.getVariableType().getLocalName(), 
                        		variable.getDisplayValue(), false, 
                                getDataType(variable.getVariableType().getStandardVariable().getDataType().getId()),
                                id,
                                measurementVariable);
	            	} else {
                        measurementData = new MeasurementData(variable.getVariableType().getLocalName(), 
                                variable.getValue(), false, 
                                getDataType(variable.getVariableType().getStandardVariable().getDataType().getId()),
                                measurementVariable);
	            	}
	            	
	            	if (experiments.size() == 1) {
	            		measurementVariable.setValue(variable.getValue());
	            	}
	            	dataList.add(measurementData);
				}
		        for (Variable variable : experiment.getVariates().getVariables()) {
					MeasurementData measurementData = null;
					MeasurementVariable measurementVariable = getMeasurementVariableByName(variable.getVariableType().getLocalName(), variateList); 
					Integer id = null;
	            	if (variable.getVariableType().getStandardVariable().getDataType().getId() == TermId.CATEGORICAL_VARIABLE.getId()) {
	            		id = variable.getValue() != null && NumberUtils.isNumber(variable.getValue()) ? Integer.valueOf(variable.getValue()) : null;
	            	}
                    measurementData = new MeasurementData(variable.getVariableType().getLocalName(), 
                            variable.getValue(), true,  
                            getDataType(variable.getVariableType().getStandardVariable().getDataType().getId()),
                            id,
                            measurementVariable);
                    measurementData.setPhenotypeId(variable.getPhenotypeId());
	            	if (experiments.size() == 1) {
	            		measurementVariable.setValue(variable.getValue());
	            	}
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
		DataSet dataset = getStudyDataManager().findOneDataSetByType(studyId, DataSetType.PLOT_DATA);
		if (dataset != null) {
			return dataset.getId();
		} else {
			return 0;
		}
	}
	
	public int getTrialDataSetId(int studyId, String studyName) throws MiddlewareQueryException {
        List<DatasetReference> datasetRefList = getStudyDataManager().getDatasetReferences(studyId);
        if (datasetRefList != null) {
            for (DatasetReference datasetRef : datasetRefList) {
                if (datasetRef.getName().equals("TRIAL_" + studyName)) {
                    return datasetRef.getId();
                }
            }
        }
        //if not found in the list using the name, get dataset with Summary Data type
        DataSet dataset = getStudyDataManager().findOneDataSetByType(studyId, DataSetType.SUMMARY_DATA);
        if (dataset != null) {
            return dataset.getId();
        } else {
            return 0;
        }
    }

	public List<MeasurementRow> buildDatasetObservations(List<Experiment> experiments, VariableTypeList variateTypes,
			List<MeasurementVariable> factorList, List<MeasurementVariable> variateList) {
		
	    List<MeasurementRow> observations = new ArrayList<MeasurementRow>();
	    for (Experiment experiment : experiments) {
	        int experimentId = experiment.getId();
	        VariableList factors = experiment.getFactors();
	        VariableList variates = getCompleteVariatesInExperiment(experiment, variateTypes);
	        List<MeasurementData> measurementDataList = new ArrayList<MeasurementData>();
	        
	        for (MeasurementVariable factor : factorList) {
	        	boolean found = false;
		        for (Variable variable : factors.getVariables()) {
		        	
		        	if (factor.getTermId() == variable.getVariableType().getStandardVariable().getId()) {
		        		found = true;
		        		
		        		boolean isEditable = NonEditableFactors.find(variable.getVariableType().getStandardVariable().getId()) == null ? true : false;
		            	MeasurementData measurementData = null;
		            	if (variable.getVariableType().getStandardVariable().getDataType().getId() == TermId.CATEGORICAL_VARIABLE.getId()) {
		            		Integer id = variable.getValue() != null && NumberUtils.isNumber(variable.getValue()) ? Integer.valueOf(variable.getValue()) : null;
	                        measurementData = new MeasurementData(variable.getVariableType().getLocalName(), 
	                        		variable.getDisplayValue(), isEditable, 
	                                getDataType(variable.getVariableType().getStandardVariable().getDataType().getId()),
	                                id,
	                                factor);
		            	} else {
	                        measurementData = new MeasurementData(variable.getVariableType().getLocalName(), 
	                                variable.getValue(), isEditable, 
	                                getDataType(variable.getVariableType().getStandardVariable().getDataType().getId()),
	                                factor);
		            	}
		            	measurementDataList.add(measurementData);
		            	break;
		        	}
		        }
		        if (!found) {
	        		boolean isEditable = NonEditableFactors.find(factor.getTermId()) == null ? true : false;
		        	MeasurementData measurementData = new MeasurementData(factor.getName(), null, isEditable, 
		        			getDataType(factor.getDataTypeId()), factor.getTermId(), factor);
		        	measurementDataList.add(measurementData);
		        }
	        }
	        
	        populateMeasurementData(variateList, variates, measurementDataList);
	        
	        MeasurementRow measurementRow = new MeasurementRow(measurementDataList);
	        measurementRow.setExperimentId(experimentId);
	        measurementRow.setLocationId(experiment.getLocationId());
	        
	        observations.add(measurementRow);
	    }
	    
	    return observations;
	}

	public void setTreatmentFactorValues(List<TreatmentVariable> treatmentVariables, int measurementDatasetId)
			throws MiddlewareQueryException {
		
		setWorkingDatabase(measurementDatasetId);
		for (TreatmentVariable treatmentVariable : treatmentVariables) {
			List<String> values = getExperimentPropertyDao().getTreatmentFactorValues(
					treatmentVariable.getLevelVariable().getTermId(), 
					treatmentVariable.getValueVariable().getTermId(), 
					measurementDatasetId);
			treatmentVariable.setValues(values);
		}
	}
}
