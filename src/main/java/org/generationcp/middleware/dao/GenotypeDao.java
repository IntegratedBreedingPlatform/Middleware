package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.Genotype;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenotypeDao extends GenericDAO<Genotype, Integer> {

    private static final Logger LOG = LoggerFactory.getLogger(GenotypeDao.class);

    public GenotypeDao(final Session session) {
        super(session);
    }
}
