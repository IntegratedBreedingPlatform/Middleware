package org.generationcp.middleware.manager.ontology;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyScaleDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OntologyScaleDataManagerImpl extends DataManager implements OntologyScaleDataManager {

    private static final Logger LOG = LoggerFactory.getLogger(OntologyScaleDataManagerImpl.class);

    public OntologyScaleDataManagerImpl(HibernateSessionProvider sessionProvider) {
        super(sessionProvider);
    }

}
