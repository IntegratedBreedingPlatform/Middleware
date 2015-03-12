package org.generationcp.middleware.manager.ontology;

import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Method;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyMethodDataManager;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class OntologyMethodDataManagerImpl extends DataManager implements OntologyMethodDataManager {

    private static final String METHOD_DOES_NOT_EXIST = "Method does not exist with that id";
    private static final String TERM_IS_NOT_METHOD = "That term is not a METHOD";

    private static final Logger LOG = LoggerFactory.getLogger(OntologyMethodDataManager.class);

    public OntologyMethodDataManagerImpl(HibernateSessionProvider sessionProvider) {
        super(sessionProvider);
    }

    @Override
    public Method getMethod(int id) throws MiddlewareQueryException {
        CVTerm term = getCvTermDao().getById(id);

        if(term == null){
            return null;
        }

        if (term.getCv() != CvId.METHODS.getId()) {
            logAndThrowException(TERM_IS_NOT_METHOD, new MiddlewareException("TERM:" + id), LOG);
        }

        return new Method(Term.fromCVTerm(term));
    }

    @Override
    public List<Method> getAllMethods() throws MiddlewareQueryException {
        List<Method> methods = new ArrayList<>();

        List<CVTerm> methodTerms = getCvTermDao().getAllByCvId(CvId.METHODS);

        for (CVTerm mt : methodTerms){
            methods.add(new Method(Term.fromCVTerm(mt)));
        }

        return methods;
    }

    @Override
    public void addMethod(Method method) throws MiddlewareQueryException {

        CVTerm term = getCvTermDao().getByNameAndCvId(method.getName(), CvId.METHODS.getId());

        if (term != null) {
            logAndThrowException("Method exist with same name");
        }

        //Constant CvId
        method.getTerm().setVocabularyId(CvId.METHODS.getId());

        Session session = getCurrentSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            term = getCvTermDao().save(method.getName(), method.getDefinition(), CvId.METHODS);
            method.setId(term.getCvTermId());
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error at addMethod" + e.getMessage(), e, LOG);
        }
    }

    @Override
    public void updateMethod(Method method) throws MiddlewareQueryException, MiddlewareException {

        CVTerm term = getCvTermDao().getById(method.getId());

        if (term == null) {
            logAndThrowException("Method does not exist with that id");
        }

        assert term != null;

        //Constant CvId
        method.getTerm().setVocabularyId(CvId.METHODS.getId());

        Session session = getCurrentSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();

            term.setName(method.getName());
            term.setDefinition(method.getDefinition());

            getCvTermDao().merge(term);

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error at updateMethod" + e.getMessage(), e, LOG);
        }

    }

    @Override
    public void deleteMethod(int id) throws MiddlewareQueryException {

        CVTerm term = getCvTermDao().getById(id);

        if (term == null || term.getCv() != CvId.METHODS.getId()) {
            logAndThrowException(METHOD_DOES_NOT_EXIST, new MiddlewareException("METHOD:" + id), LOG);
        }

        Session session = getCurrentSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();

            getCvTermDao().makeTransient(term);

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error at deleteMethod" + e.getMessage(), e, LOG);
        }
    }
}
