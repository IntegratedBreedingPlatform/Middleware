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
package org.generationcp.middleware.dao.dms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.ExperimentProject;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link ExperimentProject}.
 * 
 */
public class ExperimentProjectDao extends GenericDAO<ExperimentProject, Integer> {

	@SuppressWarnings("unchecked")
	public List<Integer> getProjectIdsByExperimentIds(Collection<Integer> experimentIds) throws MiddlewareQueryException {
		try {
			if (experimentIds != null && experimentIds.size() > 0) {
				boolean first = true;
				StringBuffer buf = new StringBuffer();
				for (@SuppressWarnings("unused") Integer id : experimentIds) {
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
	
	@SuppressWarnings("unchecked")
	public List<ExperimentProject> getExperimentProjects(int projectId, int typeId, int start, int numOfRows) throws MiddlewareQueryException {
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.add(Restrictions.eq("projectId", projectId));
			criteria.createAlias("experiment", "experiment").add(Restrictions.eq("experiment.typeId", typeId));
			criteria.setMaxResults(numOfRows);
			criteria.setFirstResult(start);
			return criteria.list();
		} 
		catch (HibernateException e) {
			logAndThrowException("Error at getExperimentProjects=" + projectId + ", " + typeId + " query at ExperimentProjectDao: " + e.getMessage(), e);
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<ExperimentProject> getExperimentProjects(int projectId, List<TermId> types, int start, int numOfRows) throws MiddlewareQueryException {
		try {
			/*
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.add(Restrictions.eq("projectId", projectId));
			criteria.createAlias("experiment", "experiment").add(Restrictions.in("experiment.typeId", getIds(types)));
			criteria.setMaxResults(numOfRows);
			criteria.setFirstResult(start);
			return criteria.list();
			*/
			
			List <Integer> lists = new ArrayList<Integer>();
			for (TermId termId : types) {
				lists.add(termId.getId());
			}
			
			StringBuilder queryString = new StringBuilder();
			queryString.append("select distinct ep from ExperimentProject as ep ");
			queryString.append("inner join ep.experiment as exp ");
			queryString.append("left outer join exp.properties as plot with plot.typeId IN (8200,8380) ");
			queryString.append("left outer join exp.properties as rep with rep.typeId = 8210 ");
			queryString.append("left outer join exp.experimentStocks as es ");
			queryString.append("left outer join es.stock as st ");
			queryString.append("where ep.projectId =:p_id and ep.experiment.typeId in (:type_ids) ");
			queryString.append("order by ep.experiment.geoLocation.description ASC, ");
			queryString.append("plot.value ASC, ");
			queryString.append("rep.value ASC, ");
			queryString.append("st.uniqueName ASC, ");
			
			if(projectId < 0){
				queryString.append("ep.experiment.ndExperimentId DESC");
			}
			else{
				queryString.append("ep.experiment.ndExperimentId ASC");
			}

			//Query q = getSession().createQuery("from ExperimentProject as ep where ep.projectId =:p_id and ep.experiment.typeId in (:type_ids)")
			Query q = getSession().createQuery(queryString.toString())
					.setParameter("p_id",projectId)
					.setParameterList("type_ids",lists)
					.setMaxResults(numOfRows)
					.setFirstResult(start)
					;
			
			return q.list();
		} 
		catch (HibernateException e) {
			logAndThrowException("Error at getExperimentProjects=" + projectId + ", " + types + " query at ExperimentProjectDao: " + e.getMessage(), e);
			return null;
		}
	}

	private Integer[] getIds(List<TermId> types) {
		Integer ids[] = new Integer[types.size()];
		int i = 0;
		for (TermId type : types) {
			ids[i++] = type.getId();
		}
		return ids;
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
