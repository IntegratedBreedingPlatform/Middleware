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
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.domain.dms.NameSynonym;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.etl.*;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.exceptions.WorkbookParserException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
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

    public DataImportServiceImpl(
            HibernateSessionProvider sessionProviderForLocal,
            HibernateSessionProvider sessionProviderForCentral) {
        super(sessionProviderForLocal, sessionProviderForCentral);
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
    public int saveDataset(Workbook workbook) throws MiddlewareQueryException {
        return saveDataset(workbook, false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public int saveDataset(Workbook workbook, boolean retainValues) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;
        Map<String, ?> variableMap = null;
        TimerWatch timerWatch = new TimerWatch("saveDataset (grand total)", LOG);

        // Transaction 1 : Transform Variables and save new Ontology Terms
        // Send : xls workbook
        // Return : Map of 3 sub maps with transformed variables (ontology fully loaded) - here is how it was loaded :
        // -- headers : Strings
        //         headerMap.put("trialHeaders", trialHeaders);
        // -- variableTypeLists (VariableTypeList)
        //          variableTypeMap.put("trialVariableTypeList", trialVariableTypeList);
        //          variableTypeMap.put("trialVariables", trialVariables);
        //          variableTypeMap.put("effectVariables", effectVariables);
        // -- measurementVariables (List<MeasurementVariable>)
        //          measurementVariableMap.put("trialMV", trialMV);
        //          measurementVariableMap.put("effectMV", effectMV);

        try {

            trans = session.beginTransaction();

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

            int studyId = getWorkbookSaver().saveDataset(workbook, variableMap, retainValues);

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

        // partially parse the file to parse the description sheet only at first
        Workbook workbook = parser.parseFile(file, true);
        // perform validations on the parsed data that require db access
        List<Message> messages = new LinkedList<Message>();

        OntologyDataManagerImpl ontology = new OntologyDataManagerImpl(
                getSessionProviderForLocal(), getSessionProviderForCentral());

        if (!isEntryExists(ontology, workbook.getFactors())) {
            messages.add(new Message("error.entry.doesnt.exist"));
        }

        if (!isPlotExists(ontology, workbook.getFactors())) {
            messages.add(new Message("error.plot.doesnt.exist"));
        }

        if (!workbook.isNursery() && !isTrialInstanceNumberExists(ontology, workbook.getTrialVariables())) {
            messages.add(new Message("error.missing.trial.condition"));
        }

        if (messages.size() > 0) {
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
        if (locationId != null) {//same location and study
            messages.add(new Message("error.duplicate.study.name"));
        } else {
            boolean isExisting = checkIfProjectNameIsExisting(studyName);
            //existing and is not a valid study
            if (isExisting && getStudyId(studyName) == null) {
                messages.add(new Message("error.duplicate.study.name"));
            }//else we will create a new study or append the data sets to the existing study
        }

        if (messages.size() > 0) {
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

                PhenotypicType type = ((variableList == workbook.getVariates() || variableList == workbook.getConstants()) ?
                        PhenotypicType.VARIATE : PhenotypicType.getPhenotypicTypeForLabel(measurementVariable.getLabel()));
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

                } else {
                    continue;
                }

            }
        }

        if (messages.size() > 0) {
            throw new WorkbookParserException(messages);
        }
    }


    private void checkForDuplicatePSMCombo(Workbook workbook, List<Message> messages) throws MiddlewareQueryException, WorkbookParserException {
        Map<String, List<Message>> errors = new HashMap<String, List<Message>>();
        checkForDuplicatePSMCombo(workbook, errors);

        if (messages == null) {
            messages = new ArrayList<Message>();
        }

        for (List<Message> messageList : errors.values()) {
            messages.addAll(messageList);
        }

        if (messages.size() > 0) {
            throw new WorkbookParserException(messages);
        }
    }

    private void checkForDuplicatePSMCombo(Workbook workbook, Map<String, List<Message>> errors) throws MiddlewareQueryException {

        List<MeasurementVariable> workbookVariables = workbook.getNonVariateVariables();

        Map<Integer, String> stdVarMap = new HashMap<Integer, String>();
        Map<String, String> psmMap = new HashMap<String, String>();

        for (MeasurementVariable measurementVariable : workbookVariables) {
            PhenotypicType type = PhenotypicType.getPhenotypicTypeForLabel(measurementVariable.getLabel());

            Integer standardVariableId = getOntologyDataManager().getStandardVariableIdByPropertyScaleMethodRole(measurementVariable.getProperty(), measurementVariable.getScale(), measurementVariable.getMethod(), type);
            String psmString = measurementVariable.getProperty().toLowerCase() + "-" + measurementVariable.getScale().toLowerCase() + "-" + measurementVariable.getMethod().toLowerCase() + measurementVariable.getLabel();
            String previousFromVarId = null;
            if (standardVariableId != null) {
                previousFromVarId = stdVarMap.put(standardVariableId, measurementVariable.getName());
            }

            String previousFromPSMString = psmMap.put(psmString, measurementVariable.getName());

            if (previousFromPSMString != null) {
                initializeIfNull(errors, measurementVariable.getName() + ":" + measurementVariable.getTermId());
                errors.get(measurementVariable.getName() + ":" + measurementVariable.getTermId()).add(new Message("error.duplicate.psm", previousFromPSMString, measurementVariable.getName()));
            }

            if (previousFromVarId != null) {
                initializeIfNull(errors, measurementVariable.getName() + ":" + measurementVariable.getTermId());
                errors.get(measurementVariable.getName() + ":" + measurementVariable.getTermId()).add(new Message("error.duplicate.psm", previousFromVarId, measurementVariable.getName()));
            }
        }

        workbookVariables = workbook.getVariateVariables();
        stdVarMap = new HashMap<Integer, String>();

        for (MeasurementVariable measurementVariable : workbookVariables) {
            Integer standardVariableId = getOntologyDataManager().getStandardVariableIdByPropertyScaleMethodRole(measurementVariable.getProperty(), measurementVariable.getScale(), measurementVariable.getMethod(), PhenotypicType.VARIATE);
            String psmString = measurementVariable.getProperty().toLowerCase() + "-" + measurementVariable.getScale().toLowerCase() + "-" + measurementVariable.getMethod().toLowerCase() + measurementVariable.getLabel();

            String previousFromVarId = null;
            if (standardVariableId != null) {
                previousFromVarId = stdVarMap.put(standardVariableId, measurementVariable.getName());
            }

            String previousFromPSMString = psmMap.put(psmString, measurementVariable.getName());

            if (previousFromPSMString != null) {
                initializeIfNull(errors, measurementVariable.getName() + ":" + measurementVariable.getTermId());
                errors.get(measurementVariable.getName() + ":" + measurementVariable.getTermId()).add(new Message("error.duplicate.psm", previousFromPSMString, measurementVariable.getName()));
            }

            if (previousFromVarId != null) {
                initializeIfNull(errors, measurementVariable.getName() + ":" + measurementVariable.getTermId());
                errors.get(measurementVariable.getName() + ":" + measurementVariable.getTermId()).add(new Message("error.duplicate.psm", previousFromVarId, measurementVariable.getName()));
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

        if (messages.size() > 0) {
            throw new WorkbookParserException(messages);
        }
    }

    private boolean nameMatches(String name, Term term) throws MiddlewareQueryException {
        String actualTermName = term.getName();
        boolean matches = actualTermName.equalsIgnoreCase(name);
        if (!matches) {
            List<NameSynonym> synonyms = getStandardVariableBuilder().createSynonyms(term.getId());
            for (NameSynonym synonym : synonyms) {
                if (name.equalsIgnoreCase(synonym.getName())) {
                    matches = true;
                    break;
                }
            }
        }

        return matches;
    }


    @Override
    @Deprecated
    // Deprecated in favor of validateProjectOntology
    public Workbook validateWorkbook(Workbook workbook) throws WorkbookParserException, MiddlewareQueryException {

        // perform validations on the parsed data that require db access
        List<Message> messages = new LinkedList<Message>();

        OntologyDataManagerImpl ontology = new OntologyDataManagerImpl(
                getSessionProviderForLocal(), getSessionProviderForCentral());

        if (!isEntryExists(ontology, workbook.getFactors()) && !isEntryExists(ontology, workbook.getConditions())) {
            messages.add(new Message("error.entry.doesnt.exist.wizard"));
        }

        if (!workbook.isNursery() && !isTrialInstanceNumberExists(ontology, workbook.getTrialVariables())) {
            messages.add(new Message("error.missing.trial.condition"));
        }

        if (messages.size() > 0) {
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
                if (svar.getStoredIn() != null) {
                    if (svar.getStoredIn().getId() == TermId.TRIAL_INSTANCE_STORAGE.getId()) {
                        return row.getMeasurementDataValue(mvar.getName());
                    }
                }
            }
        }
        if (workbook.isNursery()) {
            return "1";//GCP-7340, GCP-7346
        }
        return null;
    }

    private Integer getStudyId(String name) throws MiddlewareQueryException {
        return getProjectId(name, TermId.IS_STUDY);
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

    private Boolean isEntryExists(OntologyDataManager ontology, List<MeasurementVariable> list) throws MiddlewareQueryException {
        for (MeasurementVariable mvar : list) {
            PhenotypicType type = PhenotypicType.getPhenotypicTypeForLabel(mvar.getLabel());
            Integer varId = ontology.getStandardVariableIdByPropertyScaleMethodRole(mvar.getProperty(), mvar.getScale(), mvar.getMethod(), type);

            if (varId != null) {
                StandardVariable svar = ontology.getStandardVariable(varId);
                if (svar.getStoredIn() != null) {
                    if (svar.getStoredIn().getId() == TermId.ENTRY_NUMBER_STORAGE.getId()) {
                        mvar.setRequired(true);
                        if (svar.getId() == TermId.PLOT_NO.getId() || svar.getId() == TermId.PLOT_NNO.getId()) {
                            mvar.setRequired(true);
                            return true;
                        }
                    }
                }

            }
        }
        return false;
    }

    private Boolean isPlotExists(OntologyDataManager ontology, List<MeasurementVariable> list) throws MiddlewareQueryException {
        for (MeasurementVariable mvar : list) {
            PhenotypicType type = PhenotypicType.getPhenotypicTypeForLabel(mvar.getLabel());
            Integer varId = ontology.getStandardVariableIdByPropertyScaleMethodRole(mvar.getProperty(), mvar.getScale(), mvar.getMethod(), type);

            if (varId != null) {
                StandardVariable svar = ontology.getStandardVariable(varId);

                if (svar.getId() == TermId.PLOT_NO.getId() || svar.getId() == TermId.PLOT_NNO.getId()) {
                    return true;
                }

            }

        }
        return false;
    }

    private Boolean isTrialInstanceNumberExists(OntologyDataManager ontology, List<MeasurementVariable> list) throws MiddlewareQueryException {
        for (MeasurementVariable mvar : list) {

            StandardVariable svar = ontology.findStandardVariableByTraitScaleMethodNames(mvar.getProperty(), mvar.getScale(), mvar.getMethod());
            if (svar != null) {
                if (svar.getStoredIn() != null) {
                    if (svar.getStoredIn().getId() == TermId.TRIAL_INSTANCE_STORAGE.getId()) {
                        mvar.setRequired(true);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean checkIfProjectNameIsExisting(String name) throws MiddlewareQueryException {
        boolean isExisting = false;
        setWorkingDatabase(Database.CENTRAL);
        isExisting = getDmsProjectDao().checkIfProjectNameIsExisting(name);
        if (!isExisting) {
            setWorkingDatabase(Database.LOCAL);
            isExisting = getDmsProjectDao().checkIfProjectNameIsExisting(name);
        }
        return isExisting;
    }

    @Override
    public Integer getLocationIdByProjectNameAndDescription(String projectName, String locationDescription) throws MiddlewareQueryException {
        Integer locationId = null;
        setWorkingDatabase(Database.CENTRAL);
        locationId = getGeolocationDao().getLocationIdByProjectNameAndDescription(projectName, locationDescription);
        if (locationId == null) {
            setWorkingDatabase(Database.LOCAL);
            locationId = getGeolocationDao().getLocationIdByProjectNameAndDescription(projectName, locationDescription);
        }
        return locationId;
    }

    @Override
    public Map<String, List<Message>> validateProjectOntology(Workbook workbook) throws MiddlewareQueryException {
        Map<String, List<Message>> errors = new HashMap<String, List<Message>>();

        OntologyDataManagerImpl ontology = new OntologyDataManagerImpl(
                getSessionProviderForLocal(), getSessionProviderForCentral());

        if (!isEntryExists(ontology, workbook.getFactors())) {
            initializeIfNull(errors, Constants.MISSING_ENTRY);
            // DMV : TODO change implem so that backend is agnostic to UI when determining messages
            errors.get(Constants.MISSING_ENTRY).add(new Message("error.entry.doesnt.exist.wizard"));
        }

        if (!isPlotExists(ontology, workbook.getFactors())) {
            initializeIfNull(errors, Constants.MISSING_PLOT);
            // DMV : TODO change implem so that backend is agnostic to UI when determining messages
            errors.get(Constants.MISSING_PLOT).add(new Message("error.plot.doesnt.exist.wizard"));
        }

        if (!workbook.isNursery() && !isTrialInstanceNumberExists(ontology, workbook.getTrialVariables())) {
            initializeIfNull(errors, Constants.MISSING_TRIAL);
            errors.get(Constants.MISSING_TRIAL).add(new Message("error.missing.trial.condition"));
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
    public int saveProjectOntology(Workbook workbook)
            throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;
        TimerWatch timerWatch = new TimerWatch("saveProjectOntology (grand total)", LOG);
        int studyId = 0;

        try {

            trans = session.beginTransaction();
            studyId = getWorkbookSaver().saveProjectOntology(workbook);
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
        requireLocalDatabaseInstance();
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
        OntologyDataManagerImpl ontology = new OntologyDataManagerImpl(
                getSessionProviderForLocal(), getSessionProviderForCentral());
        checkForExistingTrialInstance(ontology, workbook, errors);

        // the following code is a workaround versus the current state management in the ETL Wizard
        // to re-set the "required" fields to true for checking later on

        isPlotExists(ontology, workbook.getFactors());
        isEntryExists(ontology, workbook.getFactors());
        if (!workbook.isNursery()) {
            isTrialInstanceNumberExists(ontology, workbook.getTrialVariables());
        }

        List<Message> requiredVariableValueErrors = checkForEmptyRequiredVariables(workbook);

        if (requiredVariableValueErrors.size() > 0) {
            errors.put(Constants.OBSERVATION_DATA_ERRORS, requiredVariableValueErrors);
        }

        return errors;
    }

    private void checkForExistingTrialInstance(
            OntologyDataManager ontology, Workbook workbook, Map<String, List<Message>> errors)
            throws MiddlewareQueryException {

        String studyName = workbook.getStudyDetails().getStudyName();
        String trialInstanceNumber = null;
        if (workbook.isNursery()) {
            trialInstanceNumber = "1";
            Integer locationId = getLocationIdByProjectNameAndDescription(studyName, trialInstanceNumber);
            if (locationId != null) {//same location and study
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
                    if (svar.getStoredIn() != null) {
                        if (svar.getStoredIn().getId() == TermId.TRIAL_INSTANCE_STORAGE.getId()) {
                            trialInstanceHeader = mvar.getName();
                            break;
                        }
                    }
                }
            }
            //get and check if trialInstanceNumber already exists
            Set<String> locationIds = new LinkedHashSet<String>();
            int maxNumOfIterations = 100000;//TODO MODIFY THIS IF NECESSARY
            int observationCount = workbook.getObservations().size();
            if (observationCount < maxNumOfIterations) {
                maxNumOfIterations = observationCount;
            }
            for (int i = 0; i < maxNumOfIterations; i++) {
                MeasurementRow row = workbook.getObservations().get(i);
                trialInstanceNumber = row.getMeasurementDataValue(trialInstanceHeader);
                if (locationIds.add(trialInstanceNumber)) {
                    Integer locationId = getLocationIdByProjectNameAndDescription(studyName, trialInstanceNumber);
                    if (locationId != null) {//same location and study
                        initializeIfNull(errors, Constants.GLOBAL);
                        errors.get(Constants.GLOBAL).add(new Message("error.duplicate.trial.instance", trialInstanceNumber));
                    }
                }
            }
        }
    }

}
