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
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for {@link CVTermRelationship}.
 * 
 */
public class CVTermRelationshipDao extends GenericDAO<CVTermRelationship, Integer> {

	@SuppressWarnings("unchecked")
	public List<Integer> getSubjectIdsByTypeAndObject(Integer typeId, Integer objectId) throws MiddlewareQueryException {
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.add(Restrictions.eq("typeId", typeId));
			criteria.add(Restrictions.eq("objectId", objectId));
			criteria.setProjection(Projections.property("subjectId"));
			
			return criteria.list();
			
		} catch (HibernateException e) {
			logAndThrowException("Error with getSubjectIdsByTypeAndObject=" + typeId + ", " + objectId 
					+ ") query from CVTermRelationship: " + e.getMessage(), e);
		}
		return new ArrayList<>();
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getObjectIdByTypeAndSubject(Integer typeId, Integer subjectId) throws MiddlewareQueryException {
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.add(Restrictions.eq("typeId", typeId));
			criteria.add(Restrictions.eq("subjectId", subjectId));
			criteria.setProjection(Projections.property("objectId"));
			
			return criteria.list();
			
		} catch (HibernateException e) {
			logAndThrowException("Error with getSubjectIdsByTypeAndObject=" + typeId + ", " + subjectId 
					+ ") query from CVTermRelationship: " + e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	@SuppressWarnings("unchecked")
	public List<CVTermRelationship> getBySubject(int subjectId) throws MiddlewareQueryException {
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.add(Restrictions.eq("subjectId", subjectId));

			return criteria.list();

		} catch(HibernateException e) {
			logAndThrowException("Error with getBySubject=" + subjectId + ") query from CVTermRelationship: " 
					+ e.getMessage(), e);
			return new ArrayList<>();
		}
	}
	
	@SuppressWarnings("unchecked")
    public CVTermRelationship getRelationshipSubjectIdObjectIdByTypeId(int subjectId, int objectId, int typeId) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(getPersistentClass());
            criteria.add(Restrictions.eq("typeId", typeId));
            criteria.add(Restrictions.eq("subjectId", subjectId));
            criteria.add(Restrictions.eq("objectId", objectId));
            
            
            List<CVTermRelationship> cvList = criteria.list();
            if(cvList == null || cvList.isEmpty()){
                return null;
            }else{
                return cvList.get(0);
            }

        } catch(HibernateException e) {
            logAndThrowException("Error with getBySubject=" + subjectId + ") query from CVTermRelationship: " 
                    + e.getMessage(), e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public CVTermRelationship getRelationshipBySubjectIdAndTypeId(int subjectId, int typeId) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(getPersistentClass());
            criteria.add(Restrictions.eq("typeId", typeId));
            criteria.add(Restrictions.eq("subjectId", subjectId));
            
            List<CVTermRelationship> cvList = criteria.list();
            if(cvList == null || cvList.isEmpty()){
                return null;
            }else{
                return cvList.get(0);
            }

        } catch(HibernateException e) {
            logAndThrowException("Error with getRelationshipBySubjectIdAndTypeId=" + subjectId + ", " + typeId + ") query from CVTermRelationship: " 
                    + e.getMessage(), e);
            return null;
        }
    }

    public List<CVTermRelationship> getBySubjectIdAndTypeId(int subjectId, int typeId) throws MiddlewareQueryException {

        List<CVTermRelationship> relationships = new ArrayList<>();

        try {
            Criteria criteria = getSession().createCriteria(getPersistentClass());
            criteria.add(Restrictions.eq("typeId", typeId));
            criteria.add(Restrictions.eq("subjectId", subjectId));

            List cvList = criteria.list();
            for(Object r : cvList){
                relationships.add((CVTermRelationship) r);
            }

        } catch(HibernateException e) {
            throw new MiddlewareQueryException("Error with getBySubjectIdAndTypeId=" + subjectId + ", " + typeId, e);

        }
        return relationships;
    }

    @SuppressWarnings("unchecked")
    public CVTermRelationship getRelationshipByObjectId(int objectId) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(getPersistentClass());
            criteria.add(Restrictions.eq("objectId", objectId));
            
            List<CVTermRelationship> cvList = criteria.list();
            if(cvList == null || cvList.isEmpty()){
                return null;
            }else{
                return cvList.get(0);
            }

        } catch(HibernateException e) {
            logAndThrowException("Error with getRelationshipByObjectId=" + objectId + ") query from CVTermRelationship: " 
                    + e.getMessage(), e);
            return null;
        }
    }

    public CVTermRelationship saveOrUpdateRelationship(CVTermRelationship cvTermRelationship) throws MiddlewareQueryException {
        try {
            saveOrUpdate(cvTermRelationship);

        } catch(HibernateException e) {
            logAndThrowException("Error with getBySubject=" + cvTermRelationship.getSubjectId() + ") query from CVTermRelationship: "
                    + e.getMessage(), e);
            return null;
        }
        return cvTermRelationship;
    }

    public boolean isTermReferred(int termId) throws MiddlewareQueryException {
        try {

            SQLQuery query = getSession().createSQLQuery("SELECT subject_id FROM cvterm_relationship where object_id = :objectId limit 1;");
            query.setParameter("objectId", termId);
            List list = query.list();
            return list.size() > 0;
        } catch (HibernateException e) {
            logAndThrowException("Error in getAllInventoryScales in CVTermDao: " + e.getMessage(), e);
        }
        return false;
    }
    
    public CVTermRelationship save(Integer subjectId, Integer typeId, Integer objectId) throws MiddlewareQueryException{
        CVTermRelationship relationship = getRelationshipSubjectIdObjectIdByTypeId(subjectId, objectId, typeId);
        if(relationship != null) return relationship;
        CVTermRelationship cvTermRelationship = new CVTermRelationship(getNextId(CVTermRelationship.ID_NAME), typeId, subjectId, objectId);
        return save(cvTermRelationship);
    }
}
