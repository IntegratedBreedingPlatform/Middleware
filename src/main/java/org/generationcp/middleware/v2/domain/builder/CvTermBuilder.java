package org.generationcp.middleware.v2.domain.builder;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.domain.CvTerm;
import org.generationcp.middleware.v2.pojos.CVTerm;

public class CvTermBuilder extends Builder {

	public CvTermBuilder(HibernateSessionProvider sessionProviderForLocal,
			               HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public CvTerm get(int cvTermId) throws MiddlewareQueryException {
		CvTerm cvTerm = null;
		if (setWorkingDatabase(cvTermId)) {
			cvTerm = mapCVTermToCvTerm(getCvTermDao().getById(cvTermId));
		}
		return cvTerm;
	}
	
	private CvTerm mapCVTermToCvTerm(CVTerm cVTerm){
		CvTerm cvTerm = null;
		
		if (cVTerm != null){
			cvTerm = new CvTerm(cVTerm.getCvTermId(), cVTerm.getName(), cVTerm.getDefinition());
		}
		return cvTerm;
		
	}
}
