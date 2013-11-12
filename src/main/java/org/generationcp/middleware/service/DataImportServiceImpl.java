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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
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

    @Override
    public int saveDataset(Workbook workbook) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;
        TimerWatch timerWatch = new TimerWatch("saveDataset (grand total)", LOG);

        try {

            trans = session.beginTransaction();

            int studyId = getWorkbookSaver().save(workbook);

            trans.commit();

            return studyId;

        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered with saveDataset(): " + e.getMessage(), e, LOG);

        } finally {
            timerWatch.stop();
            //session.flush();
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
        
        if (!isEntryExists(ontology,workbook.getFactors())) {
            messages.add(new Message("error.entry.doesnt.exist"));
        }

        if (!workbook.isNursery() && !isTrialEnvironmentExists(ontology,workbook.getTrialVariables())) {
            messages.add(new Message("error.missing.trial.condition"));
        }

        if (messages.size() > 0) {
            throw new WorkbookParserException(messages);
        }
        parser.parseAndSetObservationRows(file, workbook);
        
        //moved checking below as this needs to parse the contents of the observation sheet for multi-locations
        checkForDuplicateStudyName(ontology, workbook, messages);

        return workbook;
    }
    
    private void checkForDuplicateStudyName(OntologyDataManager ontology, Workbook workbook, List<Message> messages) 
    	throws MiddlewareQueryException, WorkbookParserException {
    	
    	String studyName = workbook.getStudyDetails().getStudyName();
        String locationDescription = getLocationDescription(ontology,workbook);
        Integer locationId = getLocationIdByProjectNameAndDescription(studyName,locationDescription);
        if(locationId!=null) {//same location and study
        	messages.add(new Message("error.duplicate.study.name"));
        } else {
        	boolean isExisting = checkIfProjectNameIsExisting(studyName);
        	//existing and is not a valid study
        	if(isExisting && getStudyId(studyName)==null) {
        		messages.add(new Message("error.duplicate.study.name"));
        	}//else we will create a new study or append the data sets to the existing study
        }
        
        if (messages.size() > 0) {
            throw new WorkbookParserException(messages);
        }
	}

	@Override
    public Workbook validateWorkbook(Workbook workbook) throws WorkbookParserException, MiddlewareQueryException {

        // perform validations on the parsed data that require db access
        List<Message> messages = new LinkedList<Message>();
        
        OntologyDataManagerImpl ontology = new OntologyDataManagerImpl(
        		getSessionProviderForLocal(), getSessionProviderForCentral());
        
        if (!isEntryExists(ontology,workbook.getFactors()) && !isEntryExists(ontology,workbook.getConditions())) {
            messages.add(new Message("error.entry.doesnt.exist.wizard"));
        }

        if (!workbook.isNursery() && !isTrialEnvironmentExists(ontology,workbook.getTrialVariables())) {
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
            		TermId.TRIAL_INSTANCE_STORAGE.getId()==svar.getStoredIn().getId()) {
                return mvar.getValue();
            }
        }
    	//check if multi-location (it means the location is defined in the observation sheet)
    	//get first row - should contain the study location
    	MeasurementRow row = workbook.getObservations().get(0);
    	List<MeasurementVariable> trialFactors = workbook.getTrialVariables(workbook.getFactors());
    	for (MeasurementVariable mvar : trialFactors) {
    		StandardVariable svar = ontology.findStandardVariableByTraitScaleMethodNames(mvar.getProperty(), mvar.getScale(), mvar.getMethod());
    		if (svar != null) {
                if (svar.getStoredIn() != null) {
                    if (svar.getStoredIn().getId() == TermId.TRIAL_INSTANCE_STORAGE.getId()) {
                        return row.getMeasurementDataValue(mvar.getName());
                    }
                }
            }
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

            StandardVariable svar = ontology.findStandardVariableByTraitScaleMethodNames(mvar.getProperty(), mvar.getScale(), mvar.getMethod());
            if (svar != null) {
                if (svar.getStoredIn() != null) {
                    if (svar.getStoredIn().getId() == 1041) {
                        return true;
                    }
                }
            }

        }
        return false;
    }

    private Boolean isTrialEnvironmentExists(OntologyDataManager ontology, List<MeasurementVariable> list) throws MiddlewareQueryException {
        for (MeasurementVariable mvar : list) {

            StandardVariable svar = ontology.findStandardVariableByTraitScaleMethodNames(mvar.getProperty(), mvar.getScale(), mvar.getMethod());
            if (svar != null) {
                if (svar.getStoredIn() != null) {

                    if (PhenotypicType.TRIAL_ENVIRONMENT.getTypeStorages().contains(svar.getStoredIn().getId())) {
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
        if (locationId==null) {
            setWorkingDatabase(Database.LOCAL);
            locationId = getGeolocationDao().getLocationIdByProjectNameAndDescription(projectName, locationDescription);
        }
        return locationId;
    }
    
    
	

}
