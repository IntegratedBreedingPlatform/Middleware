/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/
package org.generationcp.middleware.dao.oms;

import org.generationcp.middleware.dao.OntologyBaseDAO;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Property;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.hibernate.*;

import java.util.*;

@SuppressWarnings("unchecked")
public class PropertyDao extends OntologyBaseDAO {

    /**
     * This will fetch Property by propertyId*
     * @param propertyId select property by propertyId
     * @return Property
     * @throws org.generationcp.middleware.exceptions.MiddlewareQueryException
     */
    public Property getPropertyById(int propertyId) throws MiddlewareQueryException {

        try {
            List<Property> properties = getProperties(false, new ArrayList<>(Arrays.asList(propertyId)));
            if(properties.size() == 0) return null;
            if(properties.size() > 1) throw new MiddlewareQueryException("Property with id:" + propertyId + " expected: 1, found: " + properties.size());
            return properties.get(0);
        } catch (HibernateException e) {
            logAndThrowException("Error at getPropertyById :" + e.getMessage(), e);
        }
        return null;
    }

    /**
     * This will fetch all Properties*
     * @return List<Property>
     * @throws org.generationcp.middleware.exceptions.MiddlewareQueryException
     */
    public List<Property> getAllProperties() throws MiddlewareQueryException {

        try {
            return getProperties(true, null);
        } catch (HibernateException e) {
            logAndThrowException("Error at getAllProperties :" + e.getMessage(), e);
        }
        return null;
    }
    
    /**
     * This will fetch all of properties className
     * @param className Filter all properties having trait class supplied in function
     * @return List<Property>
     * @throws org.generationcp.middleware.exceptions.MiddlewareQueryException
     */
    public List<Property> getAllPropertiesWithClass(String className) throws MiddlewareQueryException {

        try{

            SQLQuery query = getSession().createSQLQuery(
                    "SELECT p.cvterm_id FROM cvterm p join cvterm_relationship cvtr on p.cvterm_id = cvtr.subject_id" +
                            " inner join cvterm dt on dt.cvterm_id = cvtr.object_id" +
                            " where cvtr.type_id = " + TermId.IS_A.getId() + " and p.cv_id = " + CvId.PROPERTIES.getId() + " and p.is_obsolete = 0" +
                            " and dt.name = :className");

            query.setParameter("className", className);

            List<Integer> propertyIds = query.list();
            
            return getProperties(false, propertyIds);

        } catch (HibernateException e) {
            logAndThrowException("Error at getAllPropertiesWithClass :" + e.getMessage(), e);
        }
        return new ArrayList<>();
    }

    /**
     * This will fetch list of properties by passing propertyIds
     * This method is private and consumed by other methods
     * @param fetchAll will tell weather query should get all properties or not.
     * @param propertyIds will tell weather propertyIds should be pass to filter result. Combination of these two will give flexible usage.
     * @return List<Property>
     * @throws org.generationcp.middleware.exceptions.MiddlewareQueryException
     */
    private List<Property> getProperties(Boolean fetchAll, List<Integer> propertyIds) throws MiddlewareQueryException {
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

            SQLQuery query = getSession().createSQLQuery(
                    "select p.cvterm_id pId, p.name pName, p.definition pDescription, p.cv_id pVocabularyId, p.is_obsolete pObsolete" +
                            ", tp.value cropOntologyId" +
                            ", cs.cvterm_id cId, cs.name cName, cs.definition cDescription, cs.cv_id cVocabularyId, cs.is_obsolete cObsolete from cvterm p" +
                            " LEFT JOIN cvtermprop tp ON tp.cvterm_id = p.cvterm_id AND tp.type_id = " + TermId.CROP_ONTOLOGY_ID.getId() +
                            " LEFT JOIN (select cvtr.subject_id PropertyId, o.cv_id, o.cvterm_id, o.name, o.definition, o.is_obsolete " +
                            " from cvTerm o inner join cvterm_relationship cvtr on cvtr.object_id = o.cvterm_id and cvtr.type_id = " + TermId.IS_A.getId() + ")" +
                            " cs on cs.PropertyId = p.cvterm_id" +
                            " where p.cv_id = " + CvId.PROPERTIES.getId() + " and p." + SHOULD_NOT_OBSOLETE +
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

            List<Object[]> result = query.list();

            if (result != null && !result.isEmpty()) {

                Property p;

                for (Object[] row : result) {

                    //Check is row does have objects to access
                    if(row.length == 0) continue;

                    //Check if Property term is already added to Map. We are iterating multiple classes for property
                    Integer propertyId = (Integer) row[0];
                    if (!map.containsKey(propertyId)) {
                        p = new Property(new Term((Integer) row[0], (String)row[1], (String)row[2], (Integer) row[3], typeSafeObjectToBoolean(row[4])));
                        map.put((Integer) row[0], p);

                        if(row[5] != null)
                            p.setCropOntologyId((String) row[5]);
                    } else {
                        p = map.get(propertyId);
                    }

                    if(row[6] != null)
                        p.addClass(new Term((Integer) row[6], (String)row[7], (String)row[8], (Integer) row[9], typeSafeObjectToBoolean(row[10])));
                }
            }

        } catch (HibernateException e) {
            logAndThrowException("Error at getProperties :" + e.getMessage(), e);
        }

        return new ArrayList<>(map.values());
    }

    public Property addProperty(String name, String definition, String cropOntologyId, List<String> classes) throws MiddlewareQueryException{
        
        if(classes == null) classes = new ArrayList<>();
        
        Property p;

        Session session = getSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            p = new Property(Term.fromCVTerm(getCvTermDao().save(name, definition, CvId.PROPERTIES)));

            if (cropOntologyId != null) {
                p.setCropOntologyId(cropOntologyId);
                getCvTermPropertyDao().save(p.getId(), TermId.CROP_ONTOLOGY_ID.getId(), cropOntologyId, 0);
            }

            List<Term> allClasses = getCvTermDao().getAllClasses();

            for (String sClass : classes) {
                for (Term tClass : allClasses) {
                    if (!sClass.equals(tClass.getName())) continue;
                    getCvTermRelationshipDao().save(p.getId(), TermId.IS_A.getId(), tClass.getId());
                    p.addClass(tClass);
                    break;
                }
            }

            transaction.commit();
        } catch (Exception e) {
            rollbackTransaction(transaction);
            throw new MiddlewareQueryException("Error in addProperty " + e.getMessage(), e);
        }

        return p;
    }

    public Property updateProperty(Integer id, String name, String definition, String cropOntologyId, List<String> classes) throws MiddlewareQueryException{

        if(classes == null) classes = new ArrayList<>();

        Property p = getPropertyById(id);
        
        if(p == null) throw new MiddlewareQueryException("Property does not exist");
        
        Session session = getSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            p.setName(name);
            p.setDefinition(definition);
            getCvTermDao().update(p.getTerm().toCVTerm());
            
            if(!Objects.equals(p.getCropOntologyId(), cropOntologyId)){
                getCvTermPropertyDao().save(p.getId(), TermId.CROP_ONTOLOGY_ID.getId(), cropOntologyId, 0);
            }

            List<CVTermRelationship> relationships = getCvTermRelationshipDao().getBySubject(id);
            for(CVTermRelationship r : relationships) getCvTermRelationshipDao().makeTransient(r);

            List<Term> allClasses = getCvTermDao().getAllClasses();
            for (String sClass : classes) {
                for (Term tClass : allClasses) {
                    if (!sClass.equals(tClass.getName())) continue;
                    getCvTermRelationshipDao().save(p.getId(), TermId.IS_A.getId(), tClass.getId());
                    p.addClass(tClass);
                    break;
                }
            }

            transaction.commit();
        } catch (Exception e) {
            rollbackTransaction(transaction);
            throw new MiddlewareQueryException("Error in addProperty " + e.getMessage(), e);
        }

        return p;
    }

    public void delete(Integer propertyId) throws MiddlewareQueryException{

        Session session = getSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            
            CVTerm term = getCvTermDao().getByIdForced(propertyId);

            List<CVTermRelationship> relationships = getCvTermRelationshipDao().getBySubject(propertyId);
            for(CVTermRelationship r : relationships) getCvTermRelationshipDao().makeTransient(r);

            List<CVTermProperty> properties = getCvTermPropertyDao().getByCvTermId(propertyId);
            for(CVTermProperty p : properties) getCvTermPropertyDao().makeTransient(p);
            
            getCvTermDao().makeTransient(term);

            transaction.commit();
        } catch (Exception e) {
            rollbackTransaction(transaction);
            throw new MiddlewareQueryException("Error in addProperty " + e.getMessage(), e);
        }
    }
}
