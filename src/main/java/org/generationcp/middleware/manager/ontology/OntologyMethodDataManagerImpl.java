package org.generationcp.middleware.manager.ontology;

import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.ontology.OntologyMethod;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyMethodDataManager;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.util.ISO8601DateParser;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.*;

/**
 * Implements {@link OntologyMethodDataManager}
 */
public class OntologyMethodDataManagerImpl extends DataManager implements OntologyMethodDataManager {

    private static final String METHOD_DOES_NOT_EXIST = "Method does not exist with that id";
    private static final String TERM_IS_NOT_METHOD = "That term is not a METHOD";
    private static final String METHOD_IS_REFERRED_TO_VARIABLE = "Method is referred to variable.";

    public OntologyMethodDataManagerImpl(HibernateSessionProvider sessionProvider) {
        super(sessionProvider);
    }

    @Override
    public OntologyMethod getMethod(int id) throws MiddlewareException {
        CVTerm term = getCvTermDao().getById(id);
        checkTermIsMethod(term);

        try {
            List<OntologyMethod> methods = getMethods(false, new ArrayList<>(Collections.singletonList(id)));

            if(methods.isEmpty()){
                return null;
            }

            return methods.get(0);
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error at getMethod :" + e.getMessage(), e);
        }
    }

    @Override
    public List<OntologyMethod> getAllMethods() throws MiddlewareException {
        try {
            return getMethods(true, null);
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error at getAllMethods :" + e.getMessage(), e);
        }
    }

    /**
     * This will fetch list of methods by passing methodIds
     * @param fetchAll will tell wheather query should get all methods or not.
     * @param methodIds will tell wheather methodIds should be pass to filter result. Combination of these two will give flexible usage.
     * @return List<OntologyMethod>
     * @throws MiddlewareException
     */
    private List<OntologyMethod> getMethods(Boolean fetchAll, List<Integer> methodIds) throws MiddlewareException {

        Map<Integer, OntologyMethod> map = new HashMap<>();
        if(methodIds == null) methodIds = new ArrayList<>();

        if(!fetchAll && methodIds.size() == 0){
            return new ArrayList<>();
        }

        try {

            List<CVTerm> terms = fetchAll ? getCvTermDao().getAllByCvId(CvId.METHODS):getCvTermDao().getAllByCvId(methodIds, CvId.METHODS);

            for(CVTerm m : terms){
                OntologyMethod ontologyMethod = new OntologyMethod(Term.fromCVTerm(m));
                map.put(ontologyMethod.getId(), ontologyMethod);
            }

            //Created, modified from CVTermProperty
            List termProperties = getCvTermPropertyDao().getByCvTermIds(new ArrayList<>(map.keySet()));

            for(Object p : termProperties){
                CVTermProperty property = (CVTermProperty) p;

                OntologyMethod ontologyMethod = map.get(property.getCvTermId());

                if(ontologyMethod == null){
                    continue;
                }

                if(Objects.equals(property.getTypeId(), TermId.CREATION_DATE.getId())){
                    ontologyMethod.setDateCreated(ISO8601DateParser.tryParse(property.getValue()));
                } else if(Objects.equals(property.getTypeId(), TermId.LAST_UPDATE_DATE.getId())){
                    ontologyMethod.setDateLastModified(ISO8601DateParser.tryParse(property.getValue()));
                }
            }

        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error at getProperties :" + e.getMessage(), e);
        }

        ArrayList<OntologyMethod> methods = new ArrayList<>(map.values());

        Collections.sort(methods, new Comparator<OntologyMethod>() {
            @Override
            public int compare(OntologyMethod l, OntologyMethod r) {
                return l.getName().compareToIgnoreCase(r.getName());
            }
        });

        return methods;
    }


    @Override
    public void addMethod(OntologyMethod method) throws MiddlewareException {

        CVTerm term = getCvTermDao().getByNameAndCvId(method.getName(), CvId.METHODS.getId());

        if (term != null) {
            throw new MiddlewareQueryException("Method exist with same name");
        }

        //Constant CvId
        method.setVocabularyId(CvId.METHODS.getId());

        Session session = getCurrentSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            term = getCvTermDao().save(method.getName(), method.getDefinition(), CvId.METHODS);
            method.setId(term.getCvTermId());

            // Save creation time
            getCvTermPropertyDao().save(method.getId(), TermId.CREATION_DATE.getId(), ISO8601DateParser.toString(new Date()), 0);

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException("Error at addMethod" + e.getMessage(), e);
        }
    }

    @Override
    public void updateMethod(OntologyMethod method) throws MiddlewareException {

        CVTerm term = getCvTermDao().getById(method.getId());

        checkTermIsMethod(term);

        //Constant CvId
        method.setVocabularyId(CvId.METHODS.getId());

        Session session = getCurrentSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();

            term.setName(method.getName());
            term.setDefinition(method.getDefinition());

            getCvTermDao().merge(term);

            // Save last modified Time
            getCvTermPropertyDao().save(method.getId(), TermId.LAST_UPDATE_DATE.getId(), ISO8601DateParser.toString(new Date()), 0);

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException("Error at updateMethod" + e.getMessage(), e);
        }

    }

    @Override
    public void deleteMethod(int id) throws MiddlewareException {

        CVTerm term = getCvTermDao().getById(id);

        checkTermIsMethod(term);

        if(getCvTermRelationshipDao().isTermReferred(id)){
            throw new MiddlewareException(METHOD_IS_REFERRED_TO_VARIABLE);
        }

        Session session = getCurrentSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();

            //delete properties
            List<CVTermProperty> properties = getCvTermPropertyDao().getByCvTermId(term.getCvTermId());
            for(CVTermProperty property : properties){
                getCvTermPropertyDao().makeTransient(property);
            }

            getCvTermDao().makeTransient(term);

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException("Error at deleteMethod" + e.getMessage(), e);
        }
    }

    private void checkTermIsMethod(CVTerm term) throws MiddlewareException {

        if(term == null){
            throw new MiddlewareException(METHOD_DOES_NOT_EXIST);
        }

        if (term.getCv() != CvId.METHODS.getId()) {
            throw new MiddlewareException(TERM_IS_NOT_METHOD);
        }
    }
}
