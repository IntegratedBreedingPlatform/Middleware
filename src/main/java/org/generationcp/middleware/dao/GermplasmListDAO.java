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
package org.generationcp.middleware.dao;

import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.GermplasmDataManagerUtil;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for {@link GermplasmList}.
 * 
 */
public class GermplasmListDAO extends GenericDAO<GermplasmList, Integer>{

    private static final Integer STATUS_DELETED = 9;
    protected static final List<SimpleExpression> restrictedList = new ArrayList();
    
    static{
    	restrictedList.add(Restrictions.ne("type", GermplasmListType.NURSERY.toString()));
    	restrictedList.add(Restrictions.ne("type", GermplasmListType.TRIAL.toString()));
    	restrictedList.add(Restrictions.ne("type", GermplasmListType.CHECK.toString()));
    	restrictedList.add(Restrictions.ne("type", GermplasmListType.ADVANCED.toString()));
    	restrictedList.add(Restrictions.ne("type", GermplasmListType.CROSSES.toString()));
    }    
    
    @SuppressWarnings("unchecked")
    public List<GermplasmList> getAllExceptDeleted(int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(GermplasmList.class);
            criteria.add(Restrictions.ne("status", STATUS_DELETED));
            
            hideSnapshotListTypes(criteria);
            
            criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);
            return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getAllExceptDeleted() query from GermplasmList: " + e.getMessage(), e);
        }
        return new ArrayList<GermplasmList>();
    }

    public long countAllExceptDeleted() throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(GermplasmList.class);
            criteria.add(Restrictions.ne("status", STATUS_DELETED));
            
            hideSnapshotListTypes(criteria);
            
            criteria.setProjection(Projections.rowCount());
            Long count = (Long) criteria.uniqueResult();
            
            if (count == null){
                return 0;
            }
            return count.longValue(); 
        } catch (HibernateException e) {
            logAndThrowException("Error with countAllExceptDeleted() query from GermplasmList: " + e.getMessage(), e);
        }
        return 0;
    }
    
    @SuppressWarnings("unchecked")
    public List<GermplasmList> getByGID(Integer gid, int start, int numOfRows) throws MiddlewareQueryException {
		try {
			if (gid != null){
				Criteria criteria = getSession().createCriteria(GermplasmListData.class);
	            criteria.createAlias("list", "l");
	            criteria.setProjection(Projections.distinct(Projections.property("list")));
	            criteria.add(Restrictions.eq("gid", gid));
	            criteria.add(Restrictions.ne("l.status", STATUS_DELETED));	            
	            
	            criteria.setFirstResult(start);
	            criteria.setMaxResults(numOfRows);
	            criteria.addOrder(Order.asc("l.id"));
	            criteria.addOrder(Order.asc("entryId"));
	            return criteria.list();
			}
		} catch (HibernateException e) {
		    logAndThrowException("Error with getByGid(gid=" + gid + ") query from GermplasmList: " + e.getMessage(), e);
		}
		return new ArrayList<GermplasmList>();
    }
    
    public long countByGID(Integer gid) throws MiddlewareQueryException {
	try {
		if (gid != null){
				Criteria criteria = getSession().createCriteria(GermplasmListData.class);				
				criteria.createAlias("list", "l");
				criteria.add(Restrictions.eq("gid", gid));
				criteria.add(Restrictions.ne("l.status", STATUS_DELETED));
				criteria.setProjection(Projections.countDistinct("l.id"));
				return ((Long) criteria.uniqueResult()).longValue(); //count
			}
        } catch (HibernateException e) {
            logAndThrowException("Error with countByGID(gid=" + gid + ") query from GermplasmList " + e.getMessage(), e);
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    public List<GermplasmList> getByName(String name, Operation operation, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(GermplasmList.class);

            hideSnapshotListTypes(criteria);
            
            if (operation == null || operation == Operation.EQUAL) {
                criteria.add(Restrictions.eq("name", name));
            } else if (operation == Operation.LIKE) {
                criteria.add(Restrictions.like("name", name));
            }

            criteria.add(Restrictions.ne("status", STATUS_DELETED));
            criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);
            return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getByName(name=" + name + ") query from GermplasmList: " + e.getMessage(), e);
        }
        return new ArrayList<GermplasmList>();
    }

    public long countByName(String name, Operation operation) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(GermplasmList.class);
            criteria.add(Restrictions.ne("status", STATUS_DELETED));
            criteria.setProjection(Projections.rowCount());
            
            hideSnapshotListTypes(criteria);
            
            if (operation == null || operation == Operation.EQUAL) {
                criteria.add(Restrictions.eq("name", name));
            } else if (operation == Operation.LIKE) {
                criteria.add(Restrictions.like("name", name));
            }
            return ((Long) criteria.uniqueResult()).longValue(); 
        } catch (HibernateException e) {
            logAndThrowException("Error with countByName(name=" + name + ") query from GermplasmList: " + e.getMessage(), e);
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    public List<GermplasmList> getByStatus(Integer status, int start, int numOfRows) throws MiddlewareQueryException {
        try {
        	if (status != null){
	            Criteria criteria = getSession().createCriteria(GermplasmList.class);
	            criteria.add(Restrictions.eq("status", status));
	            criteria.add(Restrictions.ne("status", STATUS_DELETED));
	            
	            hideSnapshotListTypes(criteria);
	            
	            criteria.setFirstResult(start);
	            criteria.setMaxResults(numOfRows);
	            return criteria.list();
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with getByStatus(status=" + status + ") query from GermplasmList: " + e.getMessage(),
                    e);
        }
        return new ArrayList<GermplasmList>();
    }

    public long countByStatus(Integer status) throws MiddlewareQueryException {
        try {
        	if (status != null){
	            Criteria criteria = getSession().createCriteria(GermplasmList.class);
	            criteria.add(Restrictions.eq("status", status));
	            criteria.add(Restrictions.ne("status", STATUS_DELETED));
	            
	            hideSnapshotListTypes(criteria);
	            
	            criteria.setProjection(Projections.rowCount());
	            return ((Long) criteria.uniqueResult()).longValue(); 
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with countByStatus(status=" + status + ") query from GermplasmList: "
                    + e.getMessage(), e);
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    public List<GermplasmList> getAllTopLevelLists(int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Criterion topFolder = Restrictions.eq("parent.id", 0);
            Criterion nullFolder = Restrictions.isNull("parent");
            
            Criteria criteria = getSession().createCriteria(GermplasmList.class);
            criteria.add(Restrictions.or(topFolder, nullFolder));
            criteria.add(Restrictions.ne("status", STATUS_DELETED));
            
            hideSnapshotListTypes(criteria);
            
            criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);
            criteria.addOrder(Order.asc("name"));
            return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getAllTopLevelLists() query from GermplasmList: " + e.getMessage(), e);
        }
        return new ArrayList<GermplasmList>();
    }

    public long countAllTopLevelLists() throws MiddlewareQueryException {
        try {
            Criterion topFolder = Restrictions.eq("parent.id", 0);
            Criterion nullFolder = Restrictions.isNull("parent");
            Criteria criteria = getSession().createCriteria(GermplasmList.class);
            criteria.add(Restrictions.or(topFolder, nullFolder));
            criteria.add(Restrictions.ne("status", STATUS_DELETED));
            
            hideSnapshotListTypes(criteria);
            
            criteria.setProjection(Projections.rowCount());
            return ((Long) criteria.uniqueResult()).longValue();
        } catch (HibernateException e) {
            logAndThrowException("Error with countAllTopLevelLists() query from GermplasmList: " + e.getMessage(), e);
        }
        return 0;
    }

    /**
     * Gets the germplasm list children.
     *
     * @param parentId the parent id
     * @param start the start
     * @param numOfRows the num of rows
     * @return the germplasm list children
     * @throws MiddlewareQueryException the MiddlewareQueryException
     */
    @SuppressWarnings("unchecked")
    public List<GermplasmList> getByParentFolderId(Integer parentId, int start, int numOfRows) throws MiddlewareQueryException {
        try {
        	if (parentId != null){
	            Criteria criteria = getSession().createCriteria(GermplasmList.class);
	            criteria.add(Restrictions.eq("parent", new GermplasmList(parentId)));
	            criteria.add(Restrictions.ne("status", STATUS_DELETED));
	            
	            hideSnapshotListTypes(criteria);
	            
	            criteria.setFirstResult(start);
	            criteria.setMaxResults(numOfRows);
	            criteria.addOrder(Order.asc("name"));
	            return criteria.list();
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with getByParentFolderId(parentId=" + parentId + ") query from GermplasmList: "
                    + e.getMessage(), e);
        }
        return new ArrayList<GermplasmList>();
    }

    /**
     * Count germplasm list children.
     *
     * @param parentId the parent id
     * @return number of germplasm list child records of a parent record
     * @throws MiddlewareQueryException the MiddlewareQueryException
     */
    public long countByParentFolderId(Integer parentId) throws MiddlewareQueryException {
        try {
        	if (parentId != null){
	            Criteria criteria = getSession().createCriteria(GermplasmList.class);
	            criteria.add(Restrictions.eq("parent", new GermplasmList(parentId)));
	            criteria.add(Restrictions.ne("status", STATUS_DELETED));
	            
	            hideSnapshotListTypes(criteria);	            
	            
	            criteria.setProjection(Projections.rowCount());
	            return ((Long) criteria.uniqueResult()).longValue(); 
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with countByParentFolderId(parentId=" + parentId + ") query from GermplasmList: "
                    + e.getMessage(), e);
        }
        return 0;
    }
    
    protected void hideSnapshotListTypes(Criteria criteria){
    	for(SimpleExpression restriction : getRestrictedSnapshopTypes()){
    		criteria.add(restriction);	
    	}    	        
    }
    protected List<SimpleExpression> getRestrictedSnapshopTypes(){
    	return restrictedList;
    }
    
    /**
     * Get Germplasm List Types
     * 
     * Return a List of UserDefinedField POJOs representing records from 
     * the udflds table of IBDB which are the types of germplasm lists.
     * 
     * @return List of germplasm list types
     * @throws MiddlewareQueryException
     */
    @SuppressWarnings("rawtypes")
	public List getGermplasmListTypes() throws MiddlewareQueryException {
        try {
        	Session session = getSession();
        	SQLQuery query = session.createSQLQuery(GermplasmList.GET_GERMPLASM_LIST_TYPES); 
            return query.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getGermplasmListTypes() query from GermplasmList: " + e.getMessage(), e);
        }
        return null;
    }
    
    /**
     * Get Germplasm Name Types
     * 
     * Return a List of UserDefinedField POJOs representing records from 
     * the udflds table of IBDB which are the types of germplasm names.
     * 
     * @return List of germplasm name types
     * @throws MiddlewareQueryException
     */
    @SuppressWarnings("rawtypes")
	public List getGermplasmNameTypes() throws MiddlewareQueryException {
        try {
        	Session session = getSession();
        	SQLQuery query = session.createSQLQuery(GermplasmList.GET_GERMPLASM_NAME_TYPES); 
            return query.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getGermplasmListTypes() query from GermplasmList: " + e.getMessage(), e);
        }    	
        return null;
    }    
    
    /**
     * Get Germplasm Lists with names like Q or germplasms with name like Q or gid equal to Q
     * @param q
     * @param o - like or equal
     * @return List of GermplasmLists
     * @throws MiddlewareQueryException 
     */
    @SuppressWarnings("unchecked")
    public List<GermplasmList> searchForGermplasmLists(String q, Operation o) throws MiddlewareQueryException{
    	q = q.trim();
    	if(q.equals("")){
    		return new ArrayList<GermplasmList>();
    	}
        try {
            SQLQuery query;
            
            if(o.equals(Operation.EQUAL)){
           		query = getSession().createSQLQuery(GermplasmList.SEARCH_FOR_GERMPLASM_LIST_EQUAL);
           		query.setParameter("gidLength", q.length());
            	query.setParameter("q", q);
            	query.setParameter("qNoSpaces", q.replace(" ", ""));
            	query.setParameter("qStandardized", GermplasmDataManagerUtil.standardizeName(q));
            } else {
            	if(q.contains("%") || q.contains("_")){
            		query = getSession().createSQLQuery(GermplasmList.SEARCH_FOR_GERMPLASM_LIST_GID_LIKE);
                	query.setParameter("q", q);
                	query.setParameter("qNoSpaces", q.replace(" ", ""));
                	query.setParameter("qStandardized", GermplasmDataManagerUtil.standardizeName(q));
            	} else {
            		query = getSession().createSQLQuery(GermplasmList.SEARCH_FOR_GERMPLASM_LIST);
            		query.setParameter("gidLength", q.length());
                	query.setParameter("q", "%"+q+"%");
                	query.setParameter("qNoSpaces", "%"+q.replace(" ", "")+"%");
                	query.setParameter("qStandardized", "%"+GermplasmDataManagerUtil.standardizeName(q)+"%");
            	}

            }
        	query.setParameter("gid", q);
        	
        	query.addEntity("listnms", GermplasmList.class);
            List<GermplasmList> germplasmLists = query.list();

            return germplasmLists;

        } catch (Exception e) {
                logAndThrowException("Error with searchGermplasmLists(" + q + ") " + e.getMessage(), e);
                }
        return new ArrayList<GermplasmList>();
    }
 
    @SuppressWarnings("unchecked")
	public List<GermplasmList> getByProjectIdAndType(int projectId, GermplasmListType type) throws MiddlewareQueryException {
    	List<GermplasmList> list = new ArrayList<GermplasmList>();
        try {
            Criteria criteria = getSession().createCriteria(GermplasmList.class);
            criteria.add(Restrictions.eq("projectId", projectId));
            criteria.add(Restrictions.eq("type", type.name()));
            criteria.add(Restrictions.ne("status", STATUS_DELETED));
            
            return criteria.list();

        } catch (HibernateException e) {
            logAndThrowException("Error with getByProjectId(projectId=" + projectId + ") query from GermplasmList: "
                    + e.getMessage(), e);
        }
        return list;    	
    }
}
