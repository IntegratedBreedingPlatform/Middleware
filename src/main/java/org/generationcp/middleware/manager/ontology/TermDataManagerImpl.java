package org.generationcp.middleware.manager.ontology;

import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DataManager;
import org.generationcp.middleware.manager.ontology.api.TermDataManager;

import java.util.List;

public class TermDataManagerImpl extends DataManager implements TermDataManager {

    public TermDataManagerImpl(HibernateSessionProvider sessionProvider) {
        super(sessionProvider);
    }

    @Override
    public Term getTermById(Integer termId) throws MiddlewareException {
        return Term.fromCVTerm(getCvTermDao().getById(termId));
    }

    @Override
    public Term getTermByNameAndCvId(String name, int cvId) throws MiddlewareException {
        return Term.fromCVTerm(getCvTermDao().getByNameAndCvId(name, cvId));
    }

    @Override
    public List<Term> getTermByCvId(int cvId) throws MiddlewareException {
        return getCvTermDao().getTermByCvId(cvId);
    }

    @Override
    public boolean isTermReferred(int termId) throws MiddlewareException {
        return getCvTermRelationshipDao().isTermReferred(termId);
    }
}
