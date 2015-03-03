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

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.oms.*;
import org.generationcp.middleware.domain.oms.Property;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.operation.builder.TermBuilder;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.*;

import java.util.*;

@SuppressWarnings("unchecked")
public class PropertyDao extends GenericDAO<CVTerm, Integer> {

    /**
     * This will fetch Property by propertyId*
     * @param propertyId
     * @return
     * @throws org.generationcp.middleware.exceptions.MiddlewareQueryException
     */
    public Property getPropertyById(int propertyId) throws MiddlewareQueryException {

        try {
            List<Property> properties = getProperties(new ArrayList<>(Arrays.asList(propertyId)));
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
     * @return
     * @throws org.generationcp.middleware.exceptions.MiddlewareQueryException
     */
    public List<Property> getAllProperties() throws MiddlewareQueryException {

        try {
            return getProperties(new ArrayList<Integer>());
        } catch (HibernateException e) {
            logAndThrowException("Error at getAllProperties :" + e.getMessage(), e);
        }
        return null;
    }
    
    /**
     * This will search properties withing name and description
     * @return
     * @throws org.generationcp.middleware.exceptions.MiddlewareQueryException
     */
    public List<Property> searchProperties(String filter) throws MiddlewareQueryException {
        
        try{
            Criteria criteria = getSession().createCriteria(CVTerm.class)
                    .add(Restrictions.eq("cvId", CvId.PROPERTIES.getId()))
                    .add(Restrictions.or(Restrictions.like("name", filter, MatchMode.ANYWHERE), Restrictions.like("definition", filter, MatchMode.ANYWHERE)))
                    .setProjection(Projections.property("cvTermId"));

            List<Integer> propertyIds = criteria.list();

            return getProperties(propertyIds);

        } catch (HibernateException e) {
            logAndThrowException("Error at getAllPropertiesWithClass :" + e.getMessage(), e);
        }
        return new ArrayList<>();
    }

    /**
     * This will fetch all of properties className
     * @return
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
            
            return getProperties(propertyIds);

        } catch (HibernateException e) {
            logAndThrowException("Error at getAllPropertiesWithClass :" + e.getMessage(), e);
        }
        return new ArrayList<>();
    }

    /**
     * This will fetch all of properties classes
     * @return
     * @throws org.generationcp.middleware.exceptions.MiddlewareQueryException
     */
    public List<Property> getAllPropertiesWithClasses(List<String> classes) throws MiddlewareQueryException {

        try{

            SQLQuery query = getSession().createSQLQuery(
                    "SELECT p.cvterm_id FROM cvterm p join cvterm_relationship cvtr on p.cvterm_id = cvtr.subject_id" +
                            " inner join cvterm dt on dt.cvterm_id = cvtr.object_id" +
                            " where cvtr.type_id = " + TermId.IS_A.getId() + " and p.cv_id = " + CvId.PROPERTIES.getId() + " and p.is_obsolete = 0" +
                            " and dt.name in (:classNames)");

            query.setParameterList("classNames", classes);

            List<Integer> propertyIds = query.list();

            return getProperties(propertyIds);

        } catch (HibernateException e) {
            logAndThrowException("Error at getAllPropertiesWithClass :" + e.getMessage(), e);
        }
        return new ArrayList<>();
    }

    /**
     * This will fetch list of properties by passing propertyIds
     * This method is private and consumed by other methods*
     * @return
     * @throws org.generationcp.middleware.exceptions.MiddlewareQueryException
     */
    private List<Property> getProperties(List<Integer> propertyIds) throws MiddlewareQueryException {
        Map<Integer, Property> map = new HashMap<>();

        try {
            
            String filterClause = "";
            if(propertyIds.size() > 0){
                filterClause = " and p.cvterm_id in (:propertyIds)";
            }

            SQLQuery query = getSession().createSQLQuery(
                    "select {p.*}, {tp.*}, {c.*}  from cvterm p" +
                            " LEFT JOIN cvtermprop tp ON tp.cvterm_id = p.cvterm_id AND tp.type_id = " + TermId.CROP_ONTOLOGY_ID.getId() +
                            " join cvterm_relationship cvtr on p.cvterm_id = cvtr.subject_id inner join cvterm c on c.cvterm_id = cvtr.object_id " +
                            " where cvtr.type_id = " + TermId.IS_A.getId() + " and p.cv_id = " + CvId.PROPERTIES.getId() + " and p.is_obsolete = 0" +
                            filterClause + " Order BY p.name")
                    .addEntity("p", CVTerm.class)
                    .addEntity("tp", CVTermProperty.class)
                    .addEntity("c", CVTerm.class);

            if(propertyIds.size() > 0){
                query.setParameterList("propertyIds", propertyIds);
            }
            
            List<Object[]> result = query.list();


            if (result != null && !result.isEmpty()) {

                Property p;

                for (Object[] row : result) {

                    //Check is row does have objects to access
                    if(row.length == 0) continue;

                    //Getting first value for Property Term
                    Term term = TermBuilder.mapCVTermToTerm((CVTerm) row[0]);

                    //Check if Property term is already added to Map. We are iterating multiple classes for property
                    if(map.containsKey(term.getId())){
                        p = map.get(term.getId());
                    }
                    else {
                        p = new Property(term);
                        map.put(term.getId(), p);

                        //Check if crop has assigned cropOntologyId
                        if(row.length > 1 && row[1] instanceof CVTermProperty){
                            p.setCropOntologyId(((CVTermProperty) row[1]).getValue());
                        }
                    }
                    //Check for dataType for the row with second result. This case is applicable when CropOntologyId is null.
                    if(row.length == 2 && row[1] instanceof CVTerm){
                        p.addClass(TermBuilder.mapCVTermToTerm((CVTerm) row[1]));
                    }

                    //Check for dataType for the row with third result. When CropOntologyId is present we will get class details in third value.
                    if(row.length == 3){
                        p.addClass(TermBuilder.mapCVTermToTerm((CVTerm) row[2]));
                    }
                }
            }

        } catch (HibernateException e) {
            logAndThrowException("Error at getProperties :" + e.getMessage(), e);
        }

        return new ArrayList<>(map.values());
    }
}
