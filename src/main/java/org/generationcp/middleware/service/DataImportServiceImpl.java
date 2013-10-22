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
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.OntologyDataManagerImpl;
import org.generationcp.middleware.operation.parser.WorkbookParser;
import org.generationcp.middleware.exceptions.WorkbookParserException;
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
            e.printStackTrace();
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
        Integer studyId = getStudyId(workbook.getStudyDetails().getStudyName());
        if (studyId != null) {
            messages.add(new Message("error.duplicate.study.name"));
        }

        if (!isEntryExists(workbook.getFactors())) {
            messages.add(new Message("error.entry.doesnt.exist"));
        }

        if (!workbook.isNursery() && !isTrialEnvironmentExists(workbook.getConditions())) {
            messages.add(new Message("error.missing.trial.condition"));
        }

        if (messages.size() > 0) {
            throw new WorkbookParserException(messages);
        }
        parser.parseAndSetObservationRows(file, workbook);

        return workbook;
    }
    
    @Override
    public Workbook validateWorkbook(Workbook workbook) throws WorkbookParserException, MiddlewareQueryException {

        // perform validations on the parsed data that require db access
        List<Message> messages = new LinkedList<Message>();
        Integer studyId = getStudyId(workbook.getStudyDetails().getStudyName());
        if (studyId != null) {
            messages.add(new Message("error.duplicate.study.name"));
        }

        if (!isEntryExists(workbook.getFactors())) {
            messages.add(new Message("error.entry.doesnt.exist"));
        }

        if (!workbook.isNursery() && !isTrialEnvironmentExists(workbook.getConditions())) {
            messages.add(new Message("error.missing.trial.condition"));
        }

        if (messages.size() > 0) {
            throw new WorkbookParserException(messages);
        }

        return workbook;
    }

    // copy pasted from WorkbookSaver
    // TODO refactor
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

    private Boolean isEntryExists(java.util.List<MeasurementVariable> list) throws MiddlewareQueryException {
        OntologyDataManagerImpl ontology = new OntologyDataManagerImpl(getSessionProviderForLocal(), getSessionProviderForCentral());
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

    private Boolean isTrialEnvironmentExists(List<MeasurementVariable> list) throws MiddlewareQueryException {
        OntologyDataManagerImpl ontology = new OntologyDataManagerImpl(getSessionProviderForLocal(), getSessionProviderForCentral());
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

}
