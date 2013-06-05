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

package org.generationcp.middleware.v2.domain.destroyer;


import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.dao.DataSetDao;
import org.generationcp.middleware.v2.dao.ExperimentPropertyDao;
import org.generationcp.middleware.v2.util.DatabaseBroker;

/**
 * The Class Builder (stolen from DataManager).
 * Mainly used for local-central initially.
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
