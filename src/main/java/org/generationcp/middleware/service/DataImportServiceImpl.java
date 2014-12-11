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
package org.generationcp.middleware.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.etl.Constants;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.exceptions.WorkbookParserException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.OntologyDataManagerImpl;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.operation.parser.WorkbookParser;
import org.generationcp.middleware.service.api.DataImportService;
import org.generationcp.middleware.util.Message;
import org.generationcp.middleware.util.TimerWatch;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataImportServiceImpl extends Service implements DataImportService {
    private static final Logger LOG = LoggerFactory.getLogger(DataImportServiceImpl.class);
    public static final int MAX_VARIABLE_NAME_LENGTH = 32;
    public static final String ERROR_MISSING_TRIAL_CONDITION = "error.missing.trial.condition";
    public static final String ERROR_PLOT_DOESNT_EXIST = "error.plot.doesnt.exist";
    public static final String ERROR_ENTRY_DOESNT_EXIST = "error.entry.doesnt.exist";
    public static final String ERROR_DUPLICATE_STUDY_NAME = "error.duplicate.study.name";
    public static final String ERROR_DUPLICATE_PSMR = "error.duplicate.psmr";
    public static final String ERROR_INVALID_VARIABLE_NAME_LENGTH = "error.invalid.variable.name.length";
    public static final String ERROR_INVALID_VARIABLE_NAME_CHARACTERS = "error.invalid.variable.name.characters";

    public DataImportServiceImpl(HibernateSessionProvider sessionProviderForLocal) {
        super(sessionProviderForLocal);
    }

    /**
     * Saves a Dataset from a Workbook into the database via
     * 1. Saving new Ontology Variables (column headers)
     * 2. Saving data
     * <p/>
     * The operation is performed in two separate transactions in order to force a Hibernate
     * Session Flush, and thereby persist all required terms to the Ontology Tables
     */
    @Override
    public int saveDataset(Workbook workbook, String programUUID) throws MiddlewareQueryException {
        return saveDataset(workbook, false, false, programUUID);
    }

	/**
	 * Transaction 1 : Transform Variables and save new Ontology Terms
	 *  Send : xls workbook
	 *   Return : Map of 3 sub maps with transformed allVariables (ontology fully loaded) - here is how it was loaded :
	 *    -- headers : Strings
	 *        //         headerMap.put("trialHeaders", trialHeaders);
	 *        // -- variableTypeLists (VariableTypeList)
	 *        //          variableTypeMap.put("trialVariableTypeList", trialVariableTypeList);
	 *        //          variableTypeMap.put("trialVariables", trialVariables);
	 *        //          variableTypeMap.put("effectVariables", effectVariables);
	 *        // -- measurementVariables (List<MeasurementVariable>)
	 *        //          measurementVariableMap.put("trialMV", trialMV);
	 *        //          measurementVariableMap.put("effectMV", effectMV);
	 * @param workbook
	 * @param retainValues if true, values of the workbook items are retained, else they are cleared to conserve memory
	 * @param isDeleteObservations
	 * @return
	 * @throws MiddlewareQueryException
	 */
    @SuppressWarnings("unchecked")
    @Override
    public int saveDataset(Workbook workbook, boolean retainValues, boolean isDeleteObservations, String programUUID) throws MiddlewareQueryException {
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;
        Map<String, ?> variableMap = null;
        TimerWatch timerWatch = new TimerWatch("saveDataset (grand total)", LOG);
        try {
            trans = session.beginTransaction();
            boolean isUpdate = workbook.getStudyDetails() != null && workbook.getStudyDetails().getId() != null;
            if (isUpdate) {
                getWorkbookSaver().saveWorkbookVariables(workbook);
                getWorkbookSaver().removeDeletedVariablesAndObservations(workbook);
            }
            variableMap = getWorkbookSaver().saveVariables(workbook);
            trans.commit();

        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered with saving to database: ", e, LOG);

        } finally {
            timerWatch.stop();
        }

        // Transaction 2 : save data
        // Send : Map of 3 sub maps, with data to create Dataset
        // Receive int (success/fail)
        Transaction trans2 = null;

        try {

            trans2 = session.beginTransaction();

            int studyId = getWorkbookSaver().saveDataset(workbook, variableMap, retainValues, isDeleteObservations, programUUID);

            trans2.commit();

            return studyId;

        } catch (Exception e) {
            rollbackTransaction(trans2);
            logAndThrowException("Error encountered with saving to database: ", e, LOG);

        } finally {
            timerWatch.stop();
        }

        return 0;
    }

    @Override
    public Workbook parseWorkbook(File file) throws WorkbookParserException {
        WorkbookParser parser = new WorkbookParser();

        // partially parse the file to parse the description sheet only at first
        Workbook workbook = parser.parseFile(file, false);


        parser.parseAndSetObservationRows(file, workbook);

        return workbook;
    }

    @Override
    public Workbook strictParseWorkbook(File file) throws WorkbookParserException, MiddlewareQueryException {
        WorkbookParser parser = new WorkbookParser();

        OntologyDataManagerImpl ontology = new OntologyDataManagerImpl(getSessionProviderForLocal());

        // partially parse the file to parse the description sheet only at first
        return strictParseWorkbook(file, parser, parser.parseFile(file, true), ontology);
    }

    protected Workbook strictParseWorkbook(File file, WorkbookParser parser, Workbook workbook,OntologyDataManager ontology) throws MiddlewareQueryException, WorkbookParserException {
        // perform validations on the parsed data that require db access
        List<Message> messages = new LinkedList<Message>();


        if (!isEntryExists(ontology, workbook.getFactors())) {
            messages.add(new Message(ERROR_ENTRY_DOESNT_EXIST));
        }

        if (!isPlotExists(ontology, workbook.getFactors())) {
            messages.add(new Message(ERROR_PLOT_DOESNT_EXIST));
        }

        if (!workbook.isNursery() && !isTrialInstanceNumberExists(ontology, workbook.getTrialVariables())) {
            messages.add(new Message(ERROR_MISSING_TRIAL_CONDITION));
        }

        messages.addAll(validateMeasurementVariableName(workbook.getAllVariables()));

        if (!messages.isEmpty()) {
            throw new WorkbookParserException(messages);
        }

        // this version of the workbookparser method is also capable of throwing a workbookparserexception with a list of messages containing validation errors inside
        parser.parseAndSetObservationRows(file, workbook);

        // separated the validation of observations from the parsing so that it can be used even in other parsing implementations (e.g., the one for Wizard style)
        messages.addAll(checkForEmptyRequiredVariables(workbook));

        //moved checking below as this needs to parse the contents of the observation sheet for multi-locations
        checkForDuplicateStudyName(ontology, workbook, messages);

        //GCP-6253
        checkForDuplicateVariableNames(ontology, workbook, messages);

        checkForDuplicatePSMCombo(workbook, messages);

        checkForInvalidLabel(workbook, messages);

        return workbook;
    }

    protected List<Message> validateMeasurementVariableName(List<MeasurementVariable> allVariables) {
        List<Message> messages = new ArrayList<Message>();

        messages.addAll(validateMeasurmentVariableNameLengths(allVariables));
        messages.addAll(validateMeasurmentVariableNameCharacters(allVariables));

        return messages;
    }

    protected List<Message> validateMeasurmentVariableNameLengths(List<MeasurementVariable> variableList) {
        List<Message> messages = new ArrayList<Message>();

        for (MeasurementVariable mv : variableList) {
            if (mv.getName().length() > MAX_VARIABLE_NAME_LENGTH) {
                messages.add(new Message(ERROR_INVALID_VARIABLE_NAME_LENGTH,mv.getName()));
            }
        }

        return messages;
    }

    protected List<Message> validateMeasurmentVariableNameCharacters(List<MeasurementVariable> variableList) {
        List<Message> messages = new ArrayList<Message>();

        for (MeasurementVariable mv : variableList) {
            if (!mv.getName().matches("^[^0-9][\\w\\d%]*$")) {
                messages.add(new Message(ERROR_INVALID_VARIABLE_NAME_CHARACTERS,mv.getName()));
            }
        }

        return messages;
    }

    private List<Message> checkForEmptyRequiredVariables(Workbook workbook) {
        List<Message> returnVal = new ArrayList<Message>();

        List<MeasurementVariable> requiredMeasurementVariables = retrieveRequiredMeasurementVariables(workbook);

        int i = 1;

        for (MeasurementRow measurementRow : workbook.getObservations()) {
            for (MeasurementData measurementData : measurementRow.getDataList()) {
                for (MeasurementVariable requiredMeasurementVariable : requiredMeasurementVariables) {
                    if (measurementData.getLabel().equals(requiredMeasurementVariable.getName()) && StringUtils.isEmpty(measurementData.getValue())) {
                        returnVal.add(new Message("empty.required.variable", measurementData.getLabel(), Integer.toString(i)));
                    }
                }
            }

            i++;
        }

        return returnVal;
    }

    private List<MeasurementVariable> retrieveRequiredMeasurementVariables(Workbook workbook) {
        // current implem leverages the setting of the required variable in earlier checks
        List<MeasurementVariable> returnVal = new ArrayList<MeasurementVariable>();

        for (MeasurementVariable measurementVariable : workbook.getAllVariables()) {
            if (measurementVariable.isRequired()) {
                returnVal.add(measurementVariable);
            }
        }

        return returnVal;
    }

    private void checkForDuplicateStudyName(OntologyDataManager ontology, Workbook workbook, List<Message> messages)
            throws MiddlewareQueryException, WorkbookParserException {

        String studyName = workbook.getStudyDetails().getStudyName();
        String locationDescription = getLocationDescription(ontology, workbook);
        Integer locationId = getLocationIdByProjectNameAndDescription(studyName, locationDescription);

        //same location and study
        if (locationId != null) {
            messages.add(new Message(ERROR_DUPLICATE_STUDY_NAME));
        } else {
            boolean isExisting = checkIfProjectNameIsExisting(studyName);
            //existing and is not a valid study
            if (isExisting && getStudyId(studyName) == null) {
                messages.add(new Message(ERROR_DUPLICATE_STUDY_NAME));
            }
            //else we will create a new study or append the data sets to the existing study
        }

        if (!messages.isEmpty()) {
            throw new WorkbookParserException(messages);
        }
    }

    private void checkForDuplicateVariableNames(OntologyDataManager ontologyDataManager, Workbook workbook, List<Message> messages) throws MiddlewareQueryException, WorkbookParserException {
        List<List<MeasurementVariable>> workbookVariables = new ArrayList<List<MeasurementVariable>>();
        workbookVariables.add(workbook.getConditions());
        workbookVariables.add(workbook.getFactors());
        workbookVariables.add(workbook.getConstants());
        workbookVariables.add(workbook.getVariates());
        Map<String, MeasurementVariable> variableNameMap = new HashMap<String, MeasurementVariable>();
        for (List<MeasurementVariable> variableList : workbookVariables) {


            for (MeasurementVariable measurementVariable : variableList) {
                if (variableNameMap.containsKey(measurementVariable.getName())) {
                    messages.add(new Message("error.duplicate.local.variable", measurementVariable.getName()));
                } else {
                    variableNameMap.put(measurementVariable.getName(), measurementVariable);
                }

                PhenotypicType type = variableList == workbook.getVariates() || variableList == workbook.getConstants() ?
                        PhenotypicType.VARIATE : PhenotypicType.getPhenotypicTypeForLabel(measurementVariable.getLabel());
                Integer varId = ontologyDataManager.getStandardVariableIdByPropertyScaleMethodRole(measurementVariable.getProperty(),
                        measurementVariable.getScale(), measurementVariable.getMethod(), type);

                if (varId == null) {

                    Set<StandardVariable> variableSet = ontologyDataManager.findStandardVariablesByNameOrSynonym(measurementVariable.getName());

                    for (StandardVariable variable : variableSet) {
                        if (variable.getName().equalsIgnoreCase(measurementVariable.getName())) {
                            messages.add(new Message("error.import.existing.standard.variable.name", measurementVariable.getName(), variable.getProperty().getName(),
                                    variable.getScale().getName(), variable.getMethod().getName(), variable.getPhenotypicType().getGroup()));
                        }
                    }

                }
            }
        }

        if (!messages.isEmpty()) {
            throw new WorkbookParserException(messages);
        }
    }


    private void checkForDuplicatePSMCombo(Workbook workbook, List<Message> messages) throws MiddlewareQueryException, WorkbookParserException {
    	Map<String, List<MeasurementVariable>> stdVarMap = checkForDuplicates(workbook.getNonVariateVariables(),false);
    	addErrorForDuplicates(messages,stdVarMap);
    	stdVarMap = checkForDuplicates(workbook.getVariateVariables(),true);
    	addErrorForDuplicates(messages,stdVarMap);
        if (!messages.isEmpty()) {
            throw new WorkbookParserException(messages);
        }
    }

    private void checkForDuplicatePSMCombo(Workbook workbook, Map<String, List<Message>> errors) throws MiddlewareQueryException {
    	Map<String, List<MeasurementVariable>> stdVarMap = checkForDuplicates(workbook.getNonVariateVariables(),false);
    	addErrorForDuplicates(errors,stdVarMap);
    	stdVarMap = checkForDuplicates(workbook.getVariateVariables(),true);
    	addErrorForDuplicates(errors,stdVarMap);
    }
    
    private Map<String, List<MeasurementVariable>> checkForDuplicates( 
		List<MeasurementVariable> workbookVariables, boolean isVariate) throws MiddlewareQueryException {
    	Map<String, List<MeasurementVariable>> stdVarMap = new LinkedHashMap<String, List<MeasurementVariable>>();
        for (MeasurementVariable measurementVariable : workbookVariables) {
            PhenotypicType type = isVariate?PhenotypicType.VARIATE:PhenotypicType.getPhenotypicTypeForLabel(measurementVariable.getLabel());
            //need to retrieve standard variable because of synonyms
            Integer standardVariableId = getOntologyDataManager().getStandardVariableIdByPropertyScaleMethodRole(
            		measurementVariable.getProperty(), measurementVariable.getScale(), measurementVariable.getMethod(), type);
            String key;
            if (standardVariableId != null) {
                key = Integer.toString(standardVariableId);
            } else {
            	key = measurementVariable.getProperty().toLowerCase() + "-" + 
            		  measurementVariable.getScale().toLowerCase() + "-" + 
            		  measurementVariable.getMethod().toLowerCase() + "-" + 
            		  (type==null?measurementVariable.getLabel().toLowerCase():type.getGroup());
            }
            List<MeasurementVariable> vars = stdVarMap.get(key);
            if(vars==null) {
            	vars = new ArrayList<MeasurementVariable>();
            	stdVarMap.put(key, vars);
            }
            vars.add(measurementVariable);
        }
        return stdVarMap;
	}

	private void addErrorForDuplicates(Map<String, List<Message>> errors, Map<String, List<MeasurementVariable>> map) {
    	for (String key : map.keySet()) {
			List<MeasurementVariable> vars = map.get(key);
			if(vars.size()>1) {
			    //has duplicate
				StringBuilder duplicates = new StringBuilder();
				String delimiter = "";
				for (MeasurementVariable measurementVariable : vars) {
					duplicates.append(delimiter);
					delimiter = ", ";
					duplicates.append(measurementVariable.getName());
				}
				for (MeasurementVariable measurementVariable : vars) {
					initializeIfNull(errors, measurementVariable.getName() + ":" + measurementVariable.getTermId());
					errors.get(measurementVariable.getName() + ":" + measurementVariable.getTermId()).
						add(new Message(ERROR_DUPLICATE_PSMR, duplicates.toString()));
				}
			}
		}
    }
	
	private void addErrorForDuplicates(List<Message> errors, Map<String, List<MeasurementVariable>> map) {
    	for (String key : map.keySet()) {
			List<MeasurementVariable> vars = map.get(key);
			if(vars.size()>1) {
			    //has duplicate
				StringBuilder duplicates = new StringBuilder();
				String delimiter = "";
				for (MeasurementVariable measurementVariable : vars) {
					duplicates.append(delimiter);
					delimiter = ", ";
					duplicates.append(measurementVariable.getName());
				}
				errors.add(new Message("error.duplicate.psmr", duplicates.toString()));
			}
		}
    }

    private void checkForInvalidLabel(Workbook workbook, List<Message> messages) throws MiddlewareQueryException, WorkbookParserException {
        List<MeasurementVariable> variableList = new ArrayList<MeasurementVariable>();
        variableList.addAll(workbook.getFactors());
        variableList.addAll(workbook.getConditions());

        for (MeasurementVariable measurementVariable : variableList) {
            PhenotypicType type = PhenotypicType.getPhenotypicTypeForLabel(measurementVariable.getLabel(), false);
            if (type == PhenotypicType.VARIATE) {
                messages.add(new Message("error.import.invalid.label", measurementVariable.getName(), measurementVariable.getLabel()));
            }
        }

        if (!messages.isEmpty()) {
            throw new WorkbookParserException(messages);
        }
    }

    /**
     * @deprecated
     * @param workbook
     * @return
     * @throws WorkbookParserException
     * @throws MiddlewareQueryException
     */
    @Override
    @Deprecated
    // Deprecated in favor of validateProjectOntology
    public Workbook validateWorkbook(Workbook workbook) throws WorkbookParserException, MiddlewareQueryException {

        // perform validations on the parsed data that require db access
        List<Message> messages = new LinkedList<Message>();

        OntologyDataManagerImpl ontology = new OntologyDataManagerImpl(getSessionProviderForLocal());

        if (!isEntryExists(ontology, workbook.getFactors()) && !isEntryExists(ontology, workbook.getConditions())) {
            messages.add(new Message("error.entry.doesnt.exist.wizard"));
        }

        if (!workbook.isNursery() && !isTrialInstanceNumberExists(ontology, workbook.getTrialVariables())) {
            messages.add(new Message(ERROR_MISSING_TRIAL_CONDITION));
        }

        if (!messages.isEmpty()) {
            throw new WorkbookParserException(messages);
        }

        //moved checking below as this needs to parse the contents of the observation sheet for multi-locations
        checkForDuplicateStudyName(ontology, workbook, messages);


        return workbook;
    }

    //for single location
    private String getLocationDescription(OntologyDataManager ontology, Workbook workbook) throws MiddlewareQueryException {

        //check if single location (it means the location is defined in the description sheet)
        List<MeasurementVariable> list = workbook.getConditions();
        for (MeasurementVariable mvar : list) {
            StandardVariable svar = ontology.findStandardVariableByTraitScaleMethodNames(
                    mvar.getProperty(), mvar.getScale(), mvar.getMethod());
            if (svar != null && svar.getStoredIn() != null &&
                    TermId.TRIAL_INSTANCE_STORAGE.getId() == svar.getStoredIn().getId()) {
                return mvar.getValue();
            }
        }
        //check if multi-location (it means the location is defined in the observation sheet)
        //get first row - should contain the study location
        MeasurementRow row = workbook.getObservations().get(0);
        List<MeasurementVariable> trialFactors = workbook.getTrialFactors();
        for (MeasurementVariable mvar : trialFactors) {
            PhenotypicType type = PhenotypicType.getPhenotypicTypeForLabel(mvar.getLabel());
            Integer varId = ontology.getStandardVariableIdByPropertyScaleMethodRole(mvar.getProperty(), mvar.getScale(), mvar.getMethod(), type);


            if (varId != null) {
                StandardVariable svar = ontology.getStandardVariable(varId);
                if (svar.getStoredIn() != null && svar.getStoredIn().getId() == TermId.TRIAL_INSTANCE_STORAGE.getId()) {
                    return row.getMeasurementDataValue(mvar.getName());
                }
            }
        }
        //GCP-7340, GCP-7346
        if (workbook.isNursery()) {
            return "1";
        }
        return null;
    }

    private Integer getStudyId(String name) throws MiddlewareQueryException {
        return getProjectId(name, TermId.IS_STUDY);
    }

    private Integer getProjectId(String name, TermId relationship) throws MiddlewareQueryException {
    	return getDmsProjectDao().getProjectIdByName(name, relationship);
    }

    protected Boolean isEntryExists(OntologyDataManager ontology, List<MeasurementVariable> list) throws MiddlewareQueryException {
        for (MeasurementVariable mvar : list) {
            PhenotypicType type = PhenotypicType.getPhenotypicTypeForLabel(mvar.getLabel());
            Integer varId = ontology.getStandardVariableIdByPropertyScaleMethodRole(mvar.getProperty(), mvar.getScale(), mvar.getMethod(), type);

            if (varId != null) {
                StandardVariable svar = ontology.getStandardVariable(varId);
                if (svar.getStoredIn() != null && svar.getStoredIn().getId() == TermId.ENTRY_NUMBER_STORAGE.getId()) {
                    mvar.setRequired(true);
                    return true;
                }
            }
        }

        return false;
    }

    protected Boolean isPlotExists(OntologyDataManager ontology, List<MeasurementVariable> list) throws MiddlewareQueryException {
        for (MeasurementVariable mvar : list) {
            PhenotypicType type = PhenotypicType.getPhenotypicTypeForLabel(mvar.getLabel());
            Integer varId = ontology.getStandardVariableIdByPropertyScaleMethodRole(mvar.getProperty(), mvar.getScale(), mvar.getMethod(), type);

            if (varId != null) {
                StandardVariable svar = ontology.getStandardVariable(varId);

                if (svar.getId() == TermId.PLOT_NO.getId() || svar.getId() == TermId.PLOT_NNO.getId()) {
                    mvar.setRequired(true);
                    return true;
                }

            }

        }
        return false;
    }

    protected Boolean isTrialInstanceNumberExists(OntologyDataManager ontology, List<MeasurementVariable> list) throws MiddlewareQueryException {
        for (MeasurementVariable mvar : list) {

            StandardVariable svar = ontology.findStandardVariableByTraitScaleMethodNames(mvar.getProperty(), mvar.getScale(), mvar.getMethod());

            if (svar != null && svar.getStoredIn() != null && svar.getStoredIn().getId() == TermId.TRIAL_INSTANCE_STORAGE.getId()) {
                mvar.setRequired(true);
                return true;
            }

        }
        return false;
    }

    @Override
    public boolean checkIfProjectNameIsExisting(String name) throws MiddlewareQueryException {
        return getDmsProjectDao().checkIfProjectNameIsExisting(name);
    }

    @Override
    public Integer getLocationIdByProjectNameAndDescription(String projectName, String locationDescription) throws MiddlewareQueryException {
        return getGeolocationDao().getLocationIdByProjectNameAndDescription(projectName, locationDescription);
    }

    @Override
    public Map<String, List<Message>> validateProjectOntology(Workbook workbook) throws MiddlewareQueryException {
        Map<String, List<Message>> errors = new HashMap<String, List<Message>>();

        OntologyDataManagerImpl ontology = new OntologyDataManagerImpl(getSessionProviderForLocal());

        if (!isEntryExists(ontology, workbook.getFactors())) {
            initializeIfNull(errors, Constants.MISSING_ENTRY);
            // DMV : TODO change implem so that backend is agnostic to UI when determining messages
            errors.get(Constants.MISSING_ENTRY).add(new Message("error.entry.doesnt.exist.wizard"));
        }

        if( (workbook.getImportType() ==null || workbook.getImportType()==DataSetType.PLOT_DATA.getId()) && !isPlotExists(ontology, workbook.getFactors()) ) {
            initializeIfNull(errors, Constants.MISSING_PLOT);
            // DMV : TODO change implem so that backend is agnostic to UI when determining messages
            errors.get(Constants.MISSING_PLOT).add(new Message("error.plot.doesnt.exist.wizard"));
        }

        if (!workbook.isNursery() && !isTrialInstanceNumberExists(ontology, workbook.getTrialVariables())) {
            initializeIfNull(errors, Constants.MISSING_TRIAL);
            errors.get(Constants.MISSING_TRIAL).add(new Message(ERROR_MISSING_TRIAL_CONDITION));
        }

        checkForDuplicatePSMCombo(workbook, errors);

        return errors;
    }

    private <T> void initializeIfNull(Map<String, List<T>> errors, String key) {
        if (errors.get(key) == null) {
            errors.put(key, new ArrayList<T>());
        }
    }

    @Override
    public int saveProjectOntology(Workbook workbook, String programUUID)
            throws MiddlewareQueryException {
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;
        TimerWatch timerWatch = new TimerWatch("saveProjectOntology (grand total)", LOG);
        int studyId = 0;

        try {

            trans = session.beginTransaction();
            studyId = getWorkbookSaver().saveProjectOntology(workbook, programUUID);
            trans.commit();

        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered with importing project ontology: ", e, LOG);

        } finally {
            timerWatch.stop();
        }

        return studyId;
    }

    @Override
    public int saveProjectData(Workbook workbook) throws MiddlewareQueryException {
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;
        TimerWatch timerWatch = new TimerWatch("saveProjectData (grand total)", LOG);

        try {

            trans = session.beginTransaction();
            getWorkbookSaver().saveProjectData(workbook);
            trans.commit();

        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered in importing observations: ", e, LOG);
            return 0;
        } finally {
            timerWatch.stop();
        }

        return 1;
    }

    @Override
    public Map<String, List<Message>> validateProjectData(Workbook workbook) throws MiddlewareQueryException {
        Map<String, List<Message>> errors = new HashMap<String, List<Message>>();
        OntologyDataManagerImpl ontology = new OntologyDataManagerImpl(getSessionProviderForLocal());
        checkForExistingTrialInstance(ontology, workbook, errors);

        // the following code is a workaround versus the current state management in the ETL Wizard
        // to re-set the "required" fields to true for checking later on

        isPlotExists(ontology, workbook.getFactors());
        isEntryExists(ontology, workbook.getFactors());
        if (!workbook.isNursery()) {
            isTrialInstanceNumberExists(ontology, workbook.getTrialVariables());
        }

        List<Message> requiredVariableValueErrors = checkForEmptyRequiredVariables(workbook);

        if (!requiredVariableValueErrors.isEmpty()) {
            errors.put(Constants.OBSERVATION_DATA_ERRORS, requiredVariableValueErrors);
        }

        return errors;
    }

    private void checkForExistingTrialInstance(
            OntologyDataManager ontology, Workbook workbook, Map<String, List<Message>> errors)
            throws MiddlewareQueryException {

        String studyName = workbook.getStudyDetails().getStudyName();
        String trialInstanceNumber;
        if (workbook.isNursery()) {
            trialInstanceNumber = "1";
            Integer locationId = getLocationIdByProjectNameAndDescription(studyName, trialInstanceNumber);
            //same location and study
            if (locationId != null) {
                initializeIfNull(errors, Constants.GLOBAL);
                errors.get(Constants.GLOBAL).add(new Message("error.duplicate.trial.instance", trialInstanceNumber));
            }
        } else {
            //get local variable name of the trial instance number
            String trialInstanceHeader = null;
            List<MeasurementVariable> trialFactors = workbook.getTrialFactors();
            for (MeasurementVariable mvar : trialFactors) {
                PhenotypicType type = PhenotypicType.getPhenotypicTypeForLabel(mvar.getLabel());
                Integer varId = ontology.getStandardVariableIdByPropertyScaleMethodRole(mvar.getProperty(), mvar.getScale(), mvar.getMethod(), type);
                if (varId != null) {
                    StandardVariable svar = ontology.getStandardVariable(varId);
                    if (svar.getStoredIn() != null && svar.getStoredIn().getId() == TermId.TRIAL_INSTANCE_STORAGE.getId()) {
                        trialInstanceHeader = mvar.getName();
                        break;
                    }
                }
            }
            //get and check if trialInstanceNumber already exists
            Set<String> locationIds = new LinkedHashSet<String>();

            //TODO MODIFY THIS IF NECESSARY
            int maxNumOfIterations = 100000;
            int observationCount = workbook.getObservations().size();
            if (observationCount < maxNumOfIterations) {
                maxNumOfIterations = observationCount;
            }
            List<String> duplicateTrialInstances = new ArrayList<String>();
            boolean isMeansDataImport = 
            		workbook.getImportType() != null &&
            		workbook.getImportType() == DataSetType.MEANS_DATA.getId();
            for (int i = 0; i < maxNumOfIterations; i++) {
                MeasurementRow row = workbook.getObservations().get(i);
                trialInstanceNumber = row.getMeasurementDataValue(trialInstanceHeader);
                if (locationIds.add(trialInstanceNumber)) {
                    Integer locationId = getLocationIdByProjectNameAndDescription(studyName, trialInstanceNumber);
                    //same location and study
                    if (locationId != null) {
                    	duplicateTrialInstances.add(trialInstanceNumber);
                    }
                }
            }
            boolean hasDuplicateTrialInstances = false;
            if(!duplicateTrialInstances.isEmpty() && workbook.getStudyId()!=null) {
            	//check import type first
            	List<Integer> variateIds = new ArrayList<Integer>();
        		//check at least one variate
        		variateIds.add(workbook.getVariates().get(0).getTermId());
        		int numberOfVariatesData = 
        				getPhenotypeDao().countVariatesDataOfStudy(
        						isMeansDataImport?
        								workbook.getMeansDatasetId():
        								workbook.getMeasurementDatesetId(), 
        						variateIds);
        		if(numberOfVariatesData>0) {
        			hasDuplicateTrialInstances = true;
        		}          	
            }
            if(hasDuplicateTrialInstances) {
            	initializeIfNull(errors, Constants.GLOBAL);
            	StringBuilder trialInstanceNumbers = new StringBuilder();
            	for (String trialInstanceNo : duplicateTrialInstances) {
            		trialInstanceNumbers.append(trialInstanceNo);
            		trialInstanceNumbers.append(",");                    
				}
            	errors.get(Constants.GLOBAL).add(new Message("error.duplicate.trial.instance", 
            			trialInstanceNumbers.toString().substring(0, trialInstanceNumbers.toString().length()-1)));
            }
            	
        }
    }

}
