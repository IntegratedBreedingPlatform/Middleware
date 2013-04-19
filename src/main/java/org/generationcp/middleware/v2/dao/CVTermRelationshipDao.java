package org.generationcp.middleware.v2.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.v2.pojos.CVTermRelationship;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class CVTermRelationshipDao extends GenericDAO<CVTermRelationship, Long> {

	@SuppressWarnings("unchecked")
	public List<CVTermRelationship> getBySubjectIds(Collection<Integer> subjectIds) throws MiddlewareQueryException {
		if (subjectIds != null && subjectIds.size() > 0) {
			try {
				Criteria criteria = getSession().createCriteria(getPersistentClass());
				criteria.add(Restrictions.in("subjectId", subjectIds));
	
				return criteria.list();

			} catch(HibernateException e) {
				logAndThrowException("Error with getByCVTermIds=" + subjectIds + ") query from CVTermRelationship: " 
						+ e.getMessage(), e);
			}
		}
		return new ArrayList<CVTermRelationship>();
	}
	
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
		return new ArrayList<Integer>();
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
		return new ArrayList<Integer>();
	}
}
