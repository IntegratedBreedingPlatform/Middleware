package org.generationcp.middleware.manager.ontology;

import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyBasicDataManager;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.hibernate.SQLQuery;

import java.math.BigInteger;
import java.util.List;

public class OntologyBasicDataManagerImpl extends DataManager implements OntologyBasicDataManager {

    private static final String SHOULD_VALID_IBDB_TERM = "Term should be of valid IBDB_TERM";

    public OntologyBasicDataManagerImpl(HibernateSessionProvider sessionProvider) {
        super(sessionProvider);
    }

    @Override
    public List<Term> getAllTraitClass() throws MiddlewareException {
        return getCvTermDao().getAllClasses();
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
    public boolean isTermReferred(int termId) throws MiddlewareException {
        return getCvTermRelationshipDao().isTermReferred(termId);
    }

    //TODO: BMS-599
    @Override
    public Integer getVariableObservations(int variableId) throws MiddlewareException {
        SQLQuery query = getActiveSession().createSQLQuery("select (select count(*) from projectprop where type_id = :variableId) " +
                                                                "+ (select count(*) from phenotype where observable_id = :variableId) c");
        query.setParameter("variableId", variableId);
        query.addScalar("c");
        return ((BigInteger) query.uniqueResult()).intValue();
    }

    @Override
    public Term addTraitClass(String childClassName, Integer parentClassId) throws MiddlewareException {
        CVTerm parentClass = getCvTermDao().getById(parentClassId);

        //Validate parent class. Parent class should be from cvId as 1000
        if(parentClass.getCv() != CvId.IBDB_TERMS.getId()) {
            throw new MiddlewareException(SHOULD_VALID_IBDB_TERM);
        }

        CVTerm newClass = getCvTermDao().save(childClassName, childClassName + " of " + parentClass.getName(), CvId.IBDB_TERMS);

        getCvTermRelationshipDao().save(newClass.getCvTermId(), TermId.IS_A.getId(), parentClassId);

        return Term.fromCVTerm(newClass);
    }

    @Override
    public void removeTraitClass(Integer termId) throws MiddlewareException {

        CVTerm term = getCvTermDao().getById(termId);

        //Validate parent class. Parent class should be from cvId as 1000
        if(term.getCv() != CvId.IBDB_TERMS.getId()) {
            throw new MiddlewareException(SHOULD_VALID_IBDB_TERM);
        }

        //Check weather term is referred
        if (getCvTermRelationshipDao().getRelationshipByObjectId(termId) != null) {
            return;
        }

        List<CVTermRelationship> termRelationships = getCvTermRelationshipDao().getBySubject(termId);

        for(CVTermRelationship r : termRelationships) {
            getCvTermRelationshipDao().makeTransient(r);
        }

        getCvTermDao().makeTransient(term);
    }
}
