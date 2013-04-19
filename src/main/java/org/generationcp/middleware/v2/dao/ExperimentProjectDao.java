package org.generationcp.middleware.v2.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.v2.pojos.ExperimentProject;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class ExperimentProjectDao extends GenericDAO<ExperimentProject, Integer> {

	@SuppressWarnings("unchecked")
	public List<Integer> getProjectIdsByExperimentIds(Collection<Integer> experimentIds) throws MiddlewareQueryException {
		try {
			if (experimentIds != null && experimentIds.size() > 0) {
				Criteria criteria = getSession().createCriteria(getPersistentClass());
				criteria.add(Restrictions.in("experiment", experimentIds));
				criteria.setProjection(Projections.property("project"));
				
				return criteria.list();
			}
			
		} catch (HibernateException e) {
			logAndThrowException("Error at getProjectIdsByExperimentIds=" + experimentIds + " query at ExperimentDao: " + e.getMessage(), e);
		}
		return new ArrayList<Integer>();
		
	}
}
