package org.generationcp.middleware.manager.ontology;

import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Property;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyPropertyDataManager;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.*;

public class OntologyPropertyDataManagerImpl extends DataManager implements OntologyPropertyDataManager {

    private static final String PROPERTY_DOES_NOT_EXIST = "Property does not exist with that id";
    private static final String TERM_IS_NOT_PROPERTY = "That term is not a PROPERTY";

    public OntologyPropertyDataManagerImpl(HibernateSessionProvider sessionProvider) {
        super(sessionProvider);
    }

    @Override
    public Property getProperty(int id) throws MiddlewareQueryException, MiddlewareException {

        CVTerm term = getCvTermDao().getById(id);

        if(term == null){
            return null;
        }

        if (term.getCv() != CvId.PROPERTIES.getId()) {
            throw new MiddlewareException(TERM_IS_NOT_PROPERTY);
        }

        try {
            List<Property> properties = getProperties(false, new ArrayList<>(Collections.singletonList(id)));
            if(properties.size() == 0) return null;
            return properties.get(0);
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error at getProperty :" + e.getMessage(), e);
        }
    }

    @Override
    public List<Property> getAllProperties() throws MiddlewareQueryException {
        try {
            return getProperties(true, null);
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error at getAllProperties :" + e.getMessage(), e);
        }
    }

    @Override
    public List<Property> getAllPropertiesWithClass(String className) throws MiddlewareQueryException {
        try{

            SQLQuery query = getActiveSession().createSQLQuery(
                    "SELECT p.cvterm_id FROM cvterm p join cvterm_relationship cvtr on p.cvterm_id = cvtr.subject_id" +
                            " inner join cvterm dt on dt.cvterm_id = cvtr.object_id" +
                            " where cvtr.type_id = " + TermId.IS_A.getId() + " and p.cv_id = " + CvId.PROPERTIES.getId() + " and p.is_obsolete = 0" +
                            " and dt.name = :className");

            query.setParameter("className", className);

            List propertyIds = query.list();

            return getProperties(false, propertyIds);

        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error at getAllPropertiesWithClass :" + e.getMessage(), e);
        }
    }

    /**
     * This will fetch list of properties by passing propertyIds
     * This method is private and consumed by other methods
     * @param fetchAll will tell weather query should get all properties or not.
     * @param propertyIds will tell weather propertyIds should be pass to filter result. Combination of these two will give flexible usage.
     * @return List<Property>
     * @throws MiddlewareQueryException
     */
    private List<Property> getProperties(Boolean fetchAll, List propertyIds) throws MiddlewareQueryException {

        Map<Integer, Property> map = new HashMap<>();

        if(propertyIds == null) propertyIds = new ArrayList<>();

        if(!fetchAll && propertyIds.size() == 0){
            return new ArrayList<>(map.values());
        }

        try {

            String filterClause = "";
            if(propertyIds.size() > 0){
                filterClause = " and p.cvterm_id in (:propertyIds)";
            }

            SQLQuery query = getActiveSession().createSQLQuery(
                    "select p.cvterm_id pId, p.name pName, p.definition pDescription, p.cv_id pVocabularyId, p.is_obsolete pObsolete" +
                            ", tp.value cropOntologyId" +
                            ", cs.cvterm_id cId, cs.name cName, cs.definition cDescription, cs.cv_id cVocabularyId, cs.is_obsolete cObsolete from cvterm p" +
                            " LEFT JOIN cvtermprop tp ON tp.cvterm_id = p.cvterm_id AND tp.type_id = " + TermId.CROP_ONTOLOGY_ID.getId() +
                            " LEFT JOIN (select cvtr.subject_id PropertyId, o.cv_id, o.cvterm_id, o.name, o.definition, o.is_obsolete " +
                            " from cvTerm o inner join cvterm_relationship cvtr on cvtr.object_id = o.cvterm_id and cvtr.type_id = " + TermId.IS_A.getId() + ")" +
                            " cs on cs.PropertyId = p.cvterm_id" +
                            " where p.cv_id = " + CvId.PROPERTIES.getId() + " and p." + getCvTermDao().SHOULD_NOT_OBSOLETE +
                            filterClause + " Order BY p.name")
                    .addScalar("pId", new org.hibernate.type.IntegerType())
                    .addScalar("pName")
                    .addScalar("pDescription")
                    .addScalar("pVocabularyId", new org.hibernate.type.IntegerType())
                    .addScalar("pObsolete", new org.hibernate.type.IntegerType())
                    .addScalar("cropOntologyId")
                    .addScalar("cId", new org.hibernate.type.IntegerType())
                    .addScalar("cName")
                    .addScalar("cDescription")
                    .addScalar("cVocabularyId", new org.hibernate.type.IntegerType())
                    .addScalar("cObsolete", new org.hibernate.type.IntegerType());

            if(propertyIds.size() > 0){
                query.setParameterList("propertyIds", propertyIds);
            }

            List result = query.list();

            Property p;

            for (Object row : result) {
                Object[] items = (Object[]) row;
                //Check is row does have objects to access
                if(items.length == 0) continue;

                //Check if Property term is already added to Map. We are iterating multiple classes for property
                Integer propertyId = (Integer) items[0];
                if (!map.containsKey(propertyId)) {
                    p = new Property(new Term((Integer) items[0], (String)items[1], (String)items[2], (Integer) items[3], typeSafeObjectToBoolean(items[4])));
                    map.put((Integer) items[0], p);

                    if(items[5] != null)
                        p.setCropOntologyId((String) items[5]);
                } else {
                    p = map.get(propertyId);
                }

                if(items[6] != null){
                    p.addClass(new Term((Integer) items[6], (String)items[7], (String)items[8], (Integer) items[9], typeSafeObjectToBoolean(items[10])));
                }
            }

        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error at getProperties :" + e.getMessage(), e);
        }

        List<Property> properties = new ArrayList<>(map.values());

        properties.sort(new Comparator<Property>() {
            @Override
            public int compare(Property l, Property r) {
                return l.getName().compareTo(r.getName());
            }
        });

        return properties;
    }

    @Override
    public void addProperty(Property property) throws MiddlewareQueryException, MiddlewareException {

        CVTerm term = getCvTermDao().getByNameAndCvId(property.getName(), CvId.METHODS.getId());

        if (term != null) {
            throw new MiddlewareException("Method exist with same name");
        }

        //To avoid null pointer exception while looping for classes if classes does not set by caller
        if(property.getClasses() == null) {
            property.setClasses(new ArrayList<Term>());
        }

        //Constant CvId
        property.getTerm().setVocabularyId(CvId.PROPERTIES.getId());

        List<Term> allClasses = getCvTermDao().getAllClasses();

        Session session = getActiveSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            CVTerm propertyTerm = getCvTermDao().save(property.getName(), property.getDefinition(), CvId.PROPERTIES);
            property.setId(propertyTerm.getCvTermId());

            if (property.getCropOntologyId() != null) {
                getCvTermPropertyDao().save(property.getId(), TermId.CROP_ONTOLOGY_ID.getId(), property.getCropOntologyId(), 0);
            }

            for (Term pClass : property.getClasses()) {
                boolean found = false;
                for (Term tClass : allClasses) {
                    if (pClass.getId() != tClass.getId()) continue;
                    getCvTermRelationshipDao().save(property.getId(), TermId.IS_A.getId(), tClass.getId());
                    found = true;
                    break;
                }
                if(!found) throw new MiddlewareException("Term Class:" + pClass.getName() + "not found");
            }
            transaction.commit();
        } catch (Exception e) {
            rollbackTransaction(transaction);
            throw new MiddlewareQueryException("Error at addProperty :" + e.getMessage(), e);
        }
    }

    @Override
    public void updateProperty(Property property) throws MiddlewareQueryException, MiddlewareException {

        //To avoid null pointer exception while looping for classes if classes does not set by caller
        if(property.getClasses() == null) property.setClasses(new ArrayList<Term>());

        CVTerm propertyTerm = getCvTermDao().getById(property.getId());

        if(propertyTerm == null){
            throw new MiddlewareException(PROPERTY_DOES_NOT_EXIST);
        }

        if (propertyTerm.getCv() != CvId.PROPERTIES.getId()) {
            throw new MiddlewareException(PROPERTY_DOES_NOT_EXIST);
        }

        List<Term> allClasses = getCvTermDao().getAllClasses();

        Session session = getActiveSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            getCvTermDao().merge(property.getTerm().toCVTerm());

            //Save or update crop ontology
            if (property.getCropOntologyId() != null) {
                getCvTermPropertyDao().save(property.getId(), TermId.CROP_ONTOLOGY_ID.getId(), property.getCropOntologyId(), 0);
            } else{
                CVTermProperty cropProperty = getCvTermPropertyDao().getOneByCvTermAndType(property.getId(), TermId.CROP_ONTOLOGY_ID.getId());
                if(cropProperty != null){
                    getCvTermPropertyDao().makeTransient(cropProperty);
                }
            }

            //Prepare list of relations
            Map<Integer, CVTermRelationship> relationsToDelete = new HashMap<>();
            List<CVTermRelationship> relationships = getCvTermRelationshipDao().getBySubjectIdAndTypeId(property.getId(), TermId.IS_A.getId());
            for(CVTermRelationship cl : relationships){
                relationsToDelete.put(cl.getObjectId(), cl);
            }

            for (Term pClass : property.getClasses()) {
                boolean found = false;
                for (Term tClass : allClasses) {
                    if (pClass.getId() != tClass.getId()){
                        continue;
                    }
                    //If relation is marked for delete then do not delete and continue
                    if(relationsToDelete.containsKey(tClass.getId())){
                        relationsToDelete.remove(tClass.getId());
                    } else {
                        getCvTermRelationshipDao().save(property.getId(), TermId.IS_A.getId(), tClass.getId());
                    }

                    found = true;
                    break;
                }
                if(!found) throw new MiddlewareException("Term Class:" + pClass + "not found");
            }

            //Removing old classes which are not in used
            for (CVTermRelationship cl : relationsToDelete.values()){
                getCvTermRelationshipDao().makeTransient(cl);
            }

            transaction.commit();
        } catch (Exception e) {
            rollbackTransaction(transaction);
            throw new MiddlewareQueryException("Error at updateProperty" + e.getMessage(), e);
        }
    }

    @Override
    public void deleteProperty(Integer propertyId) throws MiddlewareQueryException, MiddlewareException {

        CVTerm term = getCvTermDao().getById(propertyId);

        if(term == null){
            throw new MiddlewareException(PROPERTY_DOES_NOT_EXIST);
        }

        if (term.getCv() != CvId.PROPERTIES.getId()) {
            throw new MiddlewareException(PROPERTY_DOES_NOT_EXIST);
        }

        Session session = getActiveSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            //Deleting existing relationships for property
            List<CVTermRelationship> relationships = getCvTermRelationshipDao().getBySubject(propertyId);
            for(CVTermRelationship r : relationships){
                getCvTermRelationshipDao().makeTransient(r);
            }

            //Deleting existing values for property
            List<CVTermProperty> properties = getCvTermPropertyDao().getByCvTermId(propertyId);
            for(CVTermProperty p : properties){
                getCvTermPropertyDao().makeTransient(p);
            }

            getCvTermDao().makeTransient(term);
            transaction.commit();

        } catch (HibernateException e) {
            rollbackTransaction(transaction);
            throw new MiddlewareQueryException("Error at deleteProperty" + e.getMessage(), e);
        }
    }
}
