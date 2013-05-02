package org.generationcp.middleware.v2.manager;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DataManager;
import org.generationcp.middleware.v2.domain.CvTerm;
import org.generationcp.middleware.v2.manager.api.OntologyDataManager;
import org.hibernate.Session;

public class OntologyDataManagerImpl extends DataManager implements OntologyDataManager {
	
	public OntologyDataManagerImpl() { 		
	}

	public OntologyDataManagerImpl(HibernateSessionProvider sessionProviderForLocal,
			                    HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public OntologyDataManagerImpl(Session sessionForLocal, Session sessionForCentral) {
		super(sessionForLocal, sessionForCentral);
	}

	@Override
	public CvTerm getCvTermById(int cvTermId) throws MiddlewareQueryException {
		return getCvTermBuilder().get(cvTermId);
	}
	

	

}
