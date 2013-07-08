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

package org.generationcp.middleware.operation.destroyer;


import org.generationcp.middleware.dao.dms.DataSetDao;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.util.DatabaseBroker;

/**
 * Provides destroyer classes that can be used to delete logical/physical data in IBDBv2.
 * Creates destroyer classes based on the given local/central session parameters.
 * 
 * @author Donald Barre
 */
public abstract class Destroyer extends DatabaseBroker {
	
	public Destroyer(HibernateSessionProvider sessionProviderForLocal, HibernateSessionProvider sessionProviderForCentral) {
        super(sessionProviderForLocal, sessionProviderForCentral);		
	}

	protected final DataSetDao getDataSetDao() {
		DataSetDao dataSetDao = new DataSetDao();
		dataSetDao.setSession(getActiveSession());
	    return dataSetDao;
	}
}
