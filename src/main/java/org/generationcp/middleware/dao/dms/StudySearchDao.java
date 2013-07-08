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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.StudyReference;
import org.generationcp.middleware.domain.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Season;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.util.PlotUtil;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;

/**
 * DAO class for searching studies stored in {@link DmsProject}.
 * 
 * @author Donald Barre
 *
 */
public class StudySearchDao extends GenericDAO<DmsProject, Integer> {

	
	public long countStudiesByName(String name) throws MiddlewareQueryException {
		try {
			SQLQuery query = getSession().createSQLQuery("select count(distinct p.project_id) " +
		                                                 "from project p " + 
					                                     "where p.name = '" + name + "'");
					                                  
			return ((BigInteger) query.uniqueResult()).longValue();
			
		} catch(HibernateException e) {
			logAndThrowException("Error in countStudiesByName=" + name + " in StudyDao: " + e.getMessage(), e);
		}
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	public List<StudyReference> getStudiesByName(String name, int start, int numOfRows) throws MiddlewareQueryException {
		List<StudyReference> studyReferences = new ArrayList<StudyReference>();
		try {
			SQLQuery query = getSession().createSQLQuery("select distinct p.project_id, p.name, p.description " +
		                                                 "from project p " +
					                                     "where p.name = '" + name + "'");
					                                  
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);
			
			List<Object[]> results = (List<Object[]>) query.list();
			for (Object[] row : results) {
				studyReferences.add(new StudyReference((Integer) row[0], (String) row[1], (String) row[2]));
			}
			
		} catch(HibernateException e) {
			logAndThrowException("Error in getStudiesByName=" + name + " in StudyDao: " + e.getMessage(), e);
		}
		return studyReferences;
	}
	
	public long countStudiesByStartDate(int startDate) throws MiddlewareQueryException {
		try {
			SQLQuery query = getSession().createSQLQuery("select count(distinct pp.project_id) " +
		                                                 "from projectprop pp " + 
					                                     "where pp.type_id = " + TermId.START_DATE.getId() +
					                                     "  and pp.value = '" + startDate + "'");
					                                  
			return ((BigInteger) query.uniqueResult()).longValue();
			
		} catch(HibernateException e) {
			logAndThrowException("Error in countStudiesByStartDate=" + startDate + " in StudyDao: " + e.getMessage(), e);
		}
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	public List<StudyReference> getStudiesByStartDate(int startDate, int start, int numOfRows) throws MiddlewareQueryException {
		
		List<StudyReference> studyReferences = new ArrayList<StudyReference>();
		try {
			SQLQuery query = getSession().createSQLQuery("select distinct p.project_id, p.name, p.description " +
		                                                 "from projectprop pp, project p " + 
					                                     "where pp.type_id = " + TermId.START_DATE.getId() +
					                                     "  and pp.value = '" + startDate + "'" +
					                                     "  and pp.project_id = p.project_id");
					                                  
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);
			
			List<Object[]> results = (List<Object[]>) query.list();
			for (Object[] row : results) {
				StudyReference sr = new StudyReference((Integer) row[0], (String) row[1], (String) row[2]);
				studyReferences.add(sr);
			}
			
		} catch(HibernateException e) {
			logAndThrowException("Error in getStudiesByStartDate=" + startDate + " in StudyDao: " + e.getMessage(), e);
		}
		return studyReferences;
	}
	
	public long countStudiesBySeason(Season season) throws MiddlewareQueryException {
		try {
			int valueId = 0;
			if (season == Season.DRY) valueId = 10290;
			else if (season == Season.WET) valueId = 10300;
			
			if (valueId != 0) {
				SQLQuery query = getSession().createSQLQuery("select count(distinct p.project_id) " +
			                                                 "from nd_geolocationprop gp, nd_experiment e, nd_experiment_project ep, project_relationship pr, project p " +
						                                     "where gp.type_id = 8371 " +
						                                     "  and gp.value = '" + valueId + "'" +
						                                     "  and gp.nd_geolocation_id = e.nd_geolocation_id " +
						                                     "  and ((e.nd_experiment_id = ep.nd_experiment_id " + 
						                                     "          and e.type_id = " + TermId.STUDY_EXPERIMENT.getId() + 
						                                     "          and ep.project_id = p.project_id) " + 
						                                     "       or " +
						                                     "       (e.nd_experiment_id = ep.nd_experiment_id" +
						                                     "          and e.type_id in " + PlotUtil.getSqlTypeIds() + 
						                                     "          and ep.project_id = pr.subject_project_id" +
						                                     "          and pr.object_project_id = p.project_id))");
				
				return ((BigInteger) query.uniqueResult()).longValue();
			}
			
		} catch(HibernateException e) {
			logAndThrowException("Error in countStudiesBySeason=" + season + " in StudyDao: " + e.getMessage(), e);
		}
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	public List<StudyReference> getStudiesBySeason(Season season, int start, int numOfRows) throws MiddlewareQueryException {
		
		List<StudyReference> studyReferences = new ArrayList<StudyReference>();
		try {
			int valueId = 0;
			if (season == Season.DRY) valueId = 10290;
			else if (season == Season.WET) valueId = 10300;
			
			if (valueId != 0) {
				SQLQuery query = getSession().createSQLQuery("select distinct p.project_id, p.name, p.description " +
	                                                         "from nd_geolocationprop gp, nd_experiment e, nd_experiment_project ep, project_relationship pr, project p " +
	                                                         "where gp.type_id = 8371 " +
	                                                         "  and gp.value = '" + valueId + "'" +
	                                                         "  and gp.nd_geolocation_id = e.nd_geolocation_id " +
	                                                         "  and ((e.nd_experiment_id = ep.nd_experiment_id " + 
	                                                         "          and e.type_id = " + TermId.STUDY_EXPERIMENT.getId() + 
	                                                         "          and ep.project_id = p.project_id) or " +
	                                                         "       (e.nd_experiment_id = ep.nd_experiment_id" +
	                                                         "          and e.type_id in " + PlotUtil.getSqlTypeIds() + 
	                                                         "          and ep.project_id = pr.subject_project_id" +
	                                                         "          and pr.object_project_id = p.project_id))");
						                                  
				query.setFirstResult(start);
				query.setMaxResults(numOfRows);
				
				
				List<Object[]> results = (List<Object[]>) query.list();
				for (Object[] row : results) {
					StudyReference sr = new StudyReference((Integer) row[0], (String) row[1], (String) row[2]);
					studyReferences.add(sr);
				}
			}
			
		} catch(HibernateException e) {
			logAndThrowException("Error in getStudiesBySeason=" + season + " in StudyDao: " + e.getMessage(), e);
		}
		return studyReferences;
	}
	
	public long countStudiesByLocationIds(List<Integer> locationIds) throws MiddlewareQueryException {
		try {
			SQLQuery query = getSession().createSQLQuery("select count(distinct p.project_id) " +
		                                                 "from nd_geolocationprop gp, nd_experiment e, nd_experiment_project ep, project_relationship pr, project p " +
					                                     "where gp.type_id = 8190 " +
					                                     "  and gp.value in (" + stringify(locationIds) + ")" +
					                                     "  and gp.nd_geolocation_id = e.nd_geolocation_id " +
					                                     "  and ((e.nd_experiment_id = ep.nd_experiment_id " + 
					                                     "          and e.type_id = " + TermId.STUDY_EXPERIMENT.getId() + 
					                                     "          and ep.project_id = p.project_id) " + 
					                                     "       or " +
					                                     "       (e.nd_experiment_id = ep.nd_experiment_id" +
					                                     "          and e.type_id in " + PlotUtil.getSqlTypeIds() + 
					                                     "          and ep.project_id = pr.subject_project_id" +
					                                     "          and pr.object_project_id = p.project_id))");
			
			return ((BigInteger) query.uniqueResult()).longValue();
			
		} catch(HibernateException e) {
			logAndThrowException("Error in countStudiesByLocationIds=" + locationIds + " in StudyDao: " + e.getMessage(), e);
		}
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	public List<StudyReference> getStudiesByLocationIds(List<Integer> locationIds, int start, int numOfRows) throws MiddlewareQueryException {
		List<StudyReference> studyReferences = new ArrayList<StudyReference>();
		try {
			SQLQuery query = getSession().createSQLQuery("select distinct p.project_id, p.name, p.description " +
                                                         "from nd_geolocationprop gp, nd_experiment e, nd_experiment_project ep, project_relationship pr, project p " +
                                                         "where gp.type_id = 8190 " +
                                                         "  and gp.value in (" + stringify(locationIds) + ")" +
                                                         "  and gp.nd_geolocation_id = e.nd_geolocation_id " +
                                                         "  and ((e.nd_experiment_id = ep.nd_experiment_id " + 
                                                         "          and e.type_id = " + TermId.STUDY_EXPERIMENT.getId() + 
                                                         "          and ep.project_id = p.project_id) or " +
                                                         "       (e.nd_experiment_id = ep.nd_experiment_id" +
                                                         "          and e.type_id in " + PlotUtil.getSqlTypeIds() + 
                                                         "          and ep.project_id = pr.subject_project_id" +
                                                         "          and pr.object_project_id = p.project_id))");
					                                  
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);
		
			List<Object[]> results = (List<Object[]>) query.list();
			for (Object[] row : results) {
				studyReferences.add(new StudyReference((Integer) row[0], (String) row[1], (String) row[2]));
			}
			
		} catch(HibernateException e) {
			logAndThrowException("Error in getStudiesByLocationIds=" + locationIds + " in StudyDao: " + e.getMessage(), e);
		}
		return studyReferences;
	}

	private String stringify(List<Integer> locationIds) {
		String ids = new String();
		boolean first = true;
		for (Integer locId : locationIds) {
			if (!first) {
				ids += ",";
			}
		    ids += "'" + locId + "'";
			first = false;
		}
		return ids;
	}
}
