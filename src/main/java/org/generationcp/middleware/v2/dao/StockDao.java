package org.generationcp.middleware.v2.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.v2.domain.StudyReference;
import org.generationcp.middleware.v2.pojos.Stock;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class StockDao extends GenericDAO<Stock, Integer> {
	
	
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
					                                     "  and e.type_id = 1010");
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
					                                     "  and p.project_id = ep.project_id");
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
					                                     "  and pr.subject_project_id = ep.project_id");
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
					                                     "  and pr.object_project_id = p.project_id");
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
}
