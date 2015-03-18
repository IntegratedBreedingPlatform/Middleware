package org.generationcp.middleware.manager.ontology;

import org.generationcp.middleware.domain.oms.DataType;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyBasicDataManager;

import java.util.Arrays;
import java.util.List;

public class OntologyBasicDataManagerImpl extends DataManager implements OntologyBasicDataManager {

    public OntologyBasicDataManagerImpl(HibernateSessionProvider sessionProvider) {
        super(sessionProvider);
    }

    @Override
    public List<Term> getAllTraitClass() throws MiddlewareQueryException {
        return getCvTermDao().getAllClasses();
    }

    @Override
    public List<Term> getDataTypes() throws MiddlewareQueryException {
        List<Integer> dataTypeIds = Arrays.asList(DataType.CATEGORICAL_VARIABLE.getId(),
                DataType.NUMERIC_VARIABLE.getId(),
                DataType.CHARACTER_VARIABLE.getId(),
                DataType.DATE_TIME_VARIABLE.getId());

        return getTermBuilder().getTermsByIds(dataTypeIds);
    }

    @Override
    public Term getTermById(Integer termId) throws MiddlewareQueryException {
        return Term.fromCVTerm(getCvTermDao().getById(termId));
    }

    @Override
    public Term getTermByNameAndCvId(String name, int cvId) throws MiddlewareQueryException {
        return Term.fromCVTerm(getCvTermDao().getByNameAndCvId(name, cvId));
    }

    @Override
    public boolean isTermReferred(int termId) throws MiddlewareQueryException {
        return getCvTermRelationshipDao().isTermReferred(termId);
    }
}
