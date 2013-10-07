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

import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.operation.parser.WorkbookParser;
import org.generationcp.middleware.operation.parser.WorkbookParserException;
import org.generationcp.middleware.service.api.DataImportService;
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
	public Workbook parseWorkbook(File file) throws WorkbookParserException{
		return new WorkbookParser().parseFile(file);
	}
}
