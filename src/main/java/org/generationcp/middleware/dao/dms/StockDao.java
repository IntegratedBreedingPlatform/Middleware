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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link StockModel}.
 * 
 */
public class StockDao extends GenericDAO<StockModel, Integer> {
	
	@SuppressWarnings("unchecked")
	public List<Integer> getStockIdsByProperty(String columnName, String value) throws MiddlewareQueryException {
		List<Integer> stockIds = new ArrayList<Integer>();
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			if ("dbxrefId".equals(columnName)) {
				criteria.add(Restrictions.eq(columnName, Integer.valueOf(value)));
			} else {
				criteria.add(Restrictions.eq(columnName, value));
			}
			criteria.setProjection(Projections.property("stockId"));
		
			stockIds = criteria.list();
			
		} catch(HibernateException e) {
			logAndThrowException("Error in getStockIdsByProperty=" + value + " in StockDao: " + e.getMessage(), e);
		}
		return stockIds;
	}
	
	public long countStudiesByGidViaStudy(int gid) throws MiddlewareQueryException {
		
		try {
			SQLQuery query = getSession().createSQLQuery("select count(distinct ep.project_id) " +
		                                                 "from stock s, nd_experiment_stock es, nd_experiment e, nd_experiment_project ep " + 
					                                     "where s.dbxref_id = " + gid +
					                                     "  and s.stock_id = es.stock_id " +
					                                     "  and es.nd_experiment_id = e.nd_experiment_id " +
					                                     "  and ep.nd_experiment_id = e.nd_experiment_id " + 
					                                     "  and e.type_id = 1010"+
					                                     "	AND NOT EXISTS (SELECT 1 FROM projectprop pp WHERE pp.type_id = "+ TermId.STUDY_STATUS.getId() +
					                         			 "  AND pp.project_id = ep.project_id AND pp.value = " +
					                         			 "  (SELECT cvterm_id FROM cvterm WHERE name = 9 AND cv_id = "+CvId.STUDY_STATUS.getId()+")) ");
			return ((BigInteger) query.uniqueResult()).longValue();
			
		} catch(HibernateException e) {
			logAndThrowException("Error in countStudiesByGid=" + gid + " in StockDao: " + e.getMessage(), e);
		}
		return 0;
	}
	
   @SuppressWarnings("unchecked")
   public List<StudyReference> getStudiesByGidViaStudy(int gid, int start, int numOfRows) throws MiddlewareQueryException {
		List<StudyReference> studyReferences = new ArrayList<StudyReference>();
		try {
			SQLQuery query = getSession().createSQLQuery("select distinct p.project_id, p.name, p.description " +
		                                                 "from stock s, nd_experiment_stock es, nd_experiment e, nd_experiment_project ep, project p " + 
					                                     "where s.dbxref_id = " + gid +
					                                     "  and s.stock_id = es.stock_id " +
					                                     "  and es.nd_experiment_id = e.nd_experiment_id " +
					                                     "  and ep.nd_experiment_id = e.nd_experiment_id " + 
					                                     "  and e.type_id = 1010 " +
					                                     "  and p.project_id = ep.project_id "+
					                                     "	AND NOT EXISTS (SELECT 1 FROM projectprop pp WHERE pp.type_id = "+ TermId.STUDY_STATUS.getId() +
					                         			 "  AND pp.project_id = p.project_id AND pp.value = " +
					                         			 "  (SELECT cvterm_id FROM cvterm WHERE name = 9 AND cv_id = "+CvId.STUDY_STATUS.getId()+")) ");
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);
			
			List<Object[]> results = (List<Object[]>) query.list();
			for (Object[] row : results) {
				studyReferences.add(new StudyReference((Integer) row[0], (String) row[1], (String) row[2]));
			}
			
		} catch(HibernateException e) {
			logAndThrowException("Error in countStudiesByGid=" + gid + " in StockDao: " + e.getMessage(), e);
		}
		return studyReferences;
	}
	
    public long countStudiesByGidViaPlot(int gid) throws MiddlewareQueryException {
		
		try {
			SQLQuery query = getSession().createSQLQuery("select count(distinct pr.object_project_id) " +
		                                                 "from stock s, nd_experiment_stock es, nd_experiment e, nd_experiment_project ep, project_relationship pr " + 
					                                     "where s.dbxref_id = " + gid +
					                                     "  and s.stock_id = es.stock_id " +
					                                     "  and es.nd_experiment_id = e.nd_experiment_id " +
					                                     "  and ep.nd_experiment_id = e.nd_experiment_id " + 
					                                     "  and e.type_id = 1155 " +
					                                     "  and pr.subject_project_id = ep.project_id"+
					                                     "	AND NOT EXISTS (SELECT 1 FROM projectprop pp WHERE pp.type_id = "+ TermId.STUDY_STATUS.getId() +
					                         			 "  AND pp.project_id = pr.object_project_id AND pp.value = " +
					                         			 "  (SELECT cvterm_id FROM cvterm WHERE name = 9 AND cv_id = "+CvId.STUDY_STATUS.getId()+")) ");
			return ((BigInteger) query.uniqueResult()).longValue();
			
		} catch(HibernateException e) {
			logAndThrowException("Error in countStudiesByGidViaPlot=" + gid + " in StockDao: " + e.getMessage(), e);
		}
		return 0;
	}
    
    @SuppressWarnings("unchecked")
	public List<StudyReference> getStudiesByGidViaPlot(int gid, int start, int numOfRows) throws MiddlewareQueryException {
    	List<StudyReference> studyReferences = new ArrayList<StudyReference>();
		try {
			SQLQuery query = getSession().createSQLQuery("select distinct p.project_id, p.name, p.description " +
		                                                 "from stock s, nd_experiment_stock es, nd_experiment e, nd_experiment_project ep, project_relationship pr, project p " + 
					                                     "where s.dbxref_id = " + gid +
					                                     "  and s.stock_id = es.stock_id " +
					                                     "  and es.nd_experiment_id = e.nd_experiment_id " +
					                                     "  and ep.nd_experiment_id = e.nd_experiment_id " + 
					                                     "  and e.type_id = 1155 " +
					                                     "  and pr.subject_project_id = ep.project_id " +
					                                     "  and pr.object_project_id = p.project_id " +
					                                     "	AND NOT EXISTS (SELECT 1 FROM projectprop pp WHERE pp.type_id = "+ TermId.STUDY_STATUS.getId() +
					                         			 "  AND pp.project_id = p.project_id AND pp.value = " +
					                         			 "  (SELECT cvterm_id FROM cvterm WHERE name = 9 AND cv_id = "+CvId.STUDY_STATUS.getId()+")) ");
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);
			
			List<Object[]> results = (List<Object[]>) query.list();
			for (Object[] row : results) {
				studyReferences.add(new StudyReference((Integer) row[0], (String) row[1], (String) row[2]));
			}
			
		} catch(HibernateException e) {
			logAndThrowException("Error in countStudiesByGidViaPlot=" + gid + " in StockDao: " + e.getMessage(), e);
		}
		return studyReferences;
	}
    
    @SuppressWarnings("unchecked")
	public Set<StockModel> findInDataSet(int datasetId) throws MiddlewareQueryException {
		Set<StockModel> stockModels = new LinkedHashSet<StockModel>();
		try {
			
			String sql = "SELECT DISTINCT es.stock_id"
					+ " FROM nd_experiment_stock es"
					+ " INNER JOIN nd_experiment e ON e.nd_experiment_id = es.nd_experiment_id"
					+ " INNER JOIN nd_experiment_project ep ON ep.nd_experiment_id = e.nd_experiment_id"
					+ " WHERE ep.project_id = :projectId ORDER BY es.stock_id";
			Query query = getSession().createSQLQuery(sql)
								.setParameter("projectId", datasetId);
			List<Integer> ids = query.list();
			for (Integer id : ids) {
				stockModels.add(getById(id));
			}
						
		} catch(HibernateException e) {
			logAndThrowException("Error at findInDataSet=" + datasetId + " at StockDao: " + e.getMessage(), e);
		}
		return stockModels;
	}
    
   	public long countStocks(int datasetId, int trialEnvironmentId, int variateStdVarId) throws MiddlewareQueryException {
   		try {
   			
   			String sql = "select count(distinct nes.stock_id) " +
   			             "from nd_experiment e, nd_experiment_phenotype ep, phenotype p, nd_experiment_stock nes, nd_experiment_project nep " +
   			             "where e.nd_experiment_id = ep.nd_experiment_id  " +
   			             "  and ep.phenotype_id = p.phenotype_id  " +
   			             "  and nes.nd_experiment_id = e.nd_experiment_id " + 
   			             "  and nep.nd_experiment_id = e.nd_experiment_id " +
   			             "  and e.nd_geolocation_id = " + trialEnvironmentId + 
   			             "  and p.observable_id = " + variateStdVarId + 
   			             "  and nep.project_id = " + datasetId;
   			Query query = getSession().createSQLQuery(sql);
   		
   			return ((BigInteger) query.uniqueResult()).longValue();
   						
   		} catch(HibernateException e) {
   			logAndThrowException("Error at countStocks=" + datasetId + " at StockDao: " + e.getMessage(), e);
   		}
   		return 0;
   	}
}
