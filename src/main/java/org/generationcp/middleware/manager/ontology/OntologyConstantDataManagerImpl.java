package org.generationcp.middleware.manager.ontology;

import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyConstantDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class OntologyConstantDataManagerImpl extends DataManager implements OntologyConstantDataManager {

    private static final Logger LOG = LoggerFactory.getLogger(OntologyConstantDataManagerImpl.class);

    public OntologyConstantDataManagerImpl(HibernateSessionProvider sessionProvider) {
        super(sessionProvider);
    }

    @Override
    public List<Term> getAllTraitClass() throws MiddlewareQueryException {
        return getCvTermDao().getAllClasses();
    }
}
