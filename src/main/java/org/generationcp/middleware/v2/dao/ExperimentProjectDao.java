package org.generationcp.middleware.v2.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.v2.pojos.ExperimentModel;
import org.generationcp.middleware.v2.pojos.ExperimentProject;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class ExperimentProjectDao extends GenericDAO<ExperimentProject, Integer> {

	@SuppressWarnings("unchecked")
	public List<Integer> getProjectIdsByExperimentIds(Collection<Integer> experimentIds) throws MiddlewareQueryException {
		try {
			if (experimentIds != null && experimentIds.size() > 0) {
				boolean first = true;
				StringBuffer buf = new StringBuffer();
				for (Integer id : experimentIds) {
					if (first) {
						first = false;
						buf.append("?");
					}
					else {
						buf.append(",?");
					}
				}
				SQLQuery query = getSession().createSQLQuery("select distinct ep.project_id from nd_experiment_project ep where ep.nd_experiment_id in (" + buf + ")");
				int index = 0;
				for (Integer id : experimentIds) {
				    query.setParameter(index, id); 
				    index++;
				}
				return query.list();
			}
			
		} catch (HibernateException e) {
			logAndThrowException("Error at getProjectIdsByExperimentIds=" + experimentIds + " query at ExperimentDao: " + e.getMessage(), e);
		}
		return new ArrayList<Integer>();
		
	}
	
	public List<ExperimentProject> getExperimentProjects(int dataSetId, int startIndex, int maxResults) throws MiddlewareQueryException {
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.add(Restrictions.eq("projectId", dataSetId));
			criteria.setMaxResults(maxResults);
			criteria.setFirstResult(startIndex);
			return criteria.list();
		} 
		catch (HibernateException e) {
			logAndThrowException("Error at getExperimentProjects=" + dataSetId + " query at ExperimentProjectDao: " + e.getMessage(), e);
			return null;
		}
	}

	public long count(int dataSetId) throws MiddlewareQueryException {
		try {
			return (Long) getSession().createQuery("select count(*) from ExperimentProject where project_id = " + dataSetId).uniqueResult();
		} 
		catch (HibernateException e) {
			logAndThrowException("Error at getExperimentProjects=" + dataSetId + " query at ExperimentProjectDao: " + e.getMessage(), e);
			return 0;
		}
	}
}
