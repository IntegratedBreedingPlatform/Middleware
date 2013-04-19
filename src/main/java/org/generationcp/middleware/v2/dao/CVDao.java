package org.generationcp.middleware.v2.dao;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.v2.pojos.CV;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class CVDao extends GenericDAO<CV, Integer> {

	public Integer getIdByName(String name) throws MiddlewareQueryException {
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.add(Restrictions.eq("name", name));
			criteria.setProjection(Projections.property("cvId"));
			
			return (Integer) criteria.uniqueResult();
			
		} catch (HibernateException e) {
			logAndThrowException("Error at getIdByName=" + name + " query at CVDao " + e.getMessage(), e);
		}
		return null;
	}
}
