package org.generationcp.middleware.v2.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.v2.pojos.CVTerm;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

public class CVTermDao extends GenericDAO<CVTerm, Integer> {

	
	@SuppressWarnings("unchecked")
	public List<CVTerm> getByIds(Collection<Integer> ids) throws MiddlewareQueryException {
		if (ids != null && ids.size() > 0) {
			try {
				Criteria criteria = getSession().createCriteria(getPersistentClass());
				criteria.add(Restrictions.in("cvTermId", ids));
				
				List<CVTerm> results = criteria.list();
				
				return results != null ? results : new ArrayList<CVTerm>();
				
			} catch (HibernateException e) {
				logAndThrowException("Error at getByIds=" + ids + " query on CVTermDao: " + e.getMessage(), e);
			}
		}
		return new ArrayList<CVTerm>();
	}
	
	
	public CVTerm getByCvIdAndDefinition(Integer cvId, String definition) throws MiddlewareQueryException {
		CVTerm term = null;
		
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.add(Restrictions.eq("cvId", cvId));
			criteria.add(Restrictions.eq("definition", definition));
			
			term = (CVTerm) criteria.uniqueResult();
		
		} catch (HibernateException e) {
			logAndThrowException("Error at getByCvIdAndDefinition=" + cvId + ", " + definition + " query on CVTermDao: " + e.getMessage(), e);
		}
		
		return term;
	}
}
