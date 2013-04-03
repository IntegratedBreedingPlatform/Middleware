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

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
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

public class GermplasmListDAO extends GenericDAO<GermplasmList, Integer>{

    private static final Integer STATUS_DELETED = 9;
    
    @SuppressWarnings("unchecked")
    public List<GermplasmList> getAllExceptDeleted(int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(GermplasmList.class);
            criteria.add(Restrictions.ne("status", STATUS_DELETED));
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
            criteria.setProjection(Projections.rowCount());
            return ((Long) criteria.uniqueResult()).longValue(); // count
        } catch (HibernateException e) {
            logAndThrowException("Error with countAllExceptDeleted() query from GermplasmList: " + e.getMessage(), e);
        }
        return 0;
    }
    
    @SuppressWarnings("unchecked")
    public List<GermplasmList> getByGID(Integer gid, int start, int numOfRows) throws MiddlewareQueryException {
	try {
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
	} catch (HibernateException e) {
	    logAndThrowException("Error with getByGid(gid=" + gid + ") query from GermplasmList: " + e.getMessage(), e);
	}
	return new ArrayList<GermplasmList>();
    }
    
    public long countByGID(Integer gid) throws MiddlewareQueryException {
	try {
	    Criteria criteria = getSession().createCriteria(GermplasmListData.class);
	    criteria.createAlias("list", "l");
	    criteria.add(Restrictions.eq("gid", gid));
            criteria.add(Restrictions.ne("l.status", STATUS_DELETED));
            criteria.setProjection(Projections.countDistinct("l.id"));
            return ((Long) criteria.uniqueResult()).longValue(); //count
        } catch (HibernateException e) {
            logAndThrowException("Error with countByGID(gid=" + gid + ") query from GermplasmList " + e.getMessage(), e);
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    public List<GermplasmList> getByName(String name, Operation operation, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(GermplasmList.class);

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

            if (operation == null || operation == Operation.EQUAL) {
                criteria.add(Restrictions.eq("name", name));
            } else if (operation == Operation.LIKE) {
                criteria.add(Restrictions.like("name", name));
            }
            return ((Long) criteria.uniqueResult()).longValue(); // count
        } catch (HibernateException e) {
            logAndThrowException("Error with countByName(name=" + name + ") query from GermplasmList: " + e.getMessage(), e);
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    public List<GermplasmList> getByStatus(Integer status, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(GermplasmList.class);
            criteria.add(Restrictions.eq("status", status));
            criteria.add(Restrictions.ne("status", STATUS_DELETED));
            criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);
            return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getByStatus(status=" + status + ") query from GermplasmList: " + e.getMessage(),
                    e);
        }
        return new ArrayList<GermplasmList>();
    }

    public long countByStatus(Integer status) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(GermplasmList.class);
            criteria.add(Restrictions.eq("status", status));
            criteria.add(Restrictions.ne("status", STATUS_DELETED));
            criteria.setProjection(Projections.rowCount());
            return ((Long) criteria.uniqueResult()).longValue(); // count
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
            criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);
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
            criteria.setProjection(Projections.rowCount());
            return ((Long) criteria.uniqueResult()).longValue();
        } catch (HibernateException e) {
            logAndThrowException("Error with countAllTopLevelLists() query from GermplasmList: " + e.getMessage(), e);
        }
        return 0;
    }

    public void validateId(GermplasmList germplasmList) throws MiddlewareQueryException {
        // Check if not a local record (has negative ID)
        Integer id = germplasmList.getId();
        if (id != null && id.intValue() > 0) {
            logAndThrowException("Error with validateId(germplasmList=" + germplasmList
                    + "): Cannot update a Central Database record. "
                    + "GermplasmList object to update must be a Local Record (ID must be negative)");
        }
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
            Criteria criteria = getSession().createCriteria(GermplasmList.class);
            criteria.add(Restrictions.eq("parent", new GermplasmList(parentId)));
            criteria.add(Restrictions.ne("status", STATUS_DELETED));
            criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);
            return criteria.list();
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
            Criteria criteria = getSession().createCriteria(GermplasmList.class);
            criteria.add(Restrictions.eq("parent", new GermplasmList(parentId)));
            criteria.add(Restrictions.ne("status", STATUS_DELETED));
            criteria.setProjection(Projections.rowCount());
            return ((Long) criteria.uniqueResult()).longValue(); // count
        } catch (HibernateException e) {
            logAndThrowException("Error with countByParentFolderId(parentId=" + parentId + ") query from GermplasmList: "
                    + e.getMessage(), e);
        }
        return 0;
    }
    
    /**
     * Get Germplasm List Types
     * 
     * Return a List of UserDefinedField POJOs representing records from 
     * the udflds table of IBDB which are the types of germplasm lists.
     * 
     * @return
     * @throws MiddlewareQueryException
     */
    
    
    @SuppressWarnings("rawtypes")
	public List getGermplasmListTypes() throws MiddlewareQueryException {
        try {
        	Session session = getSession();
        	SQLQuery query = session.createSQLQuery(GermplasmList.GET_GERMPLASM_LIST_TYPES); 
            List results = query.list();
            return results;

        } catch (HibernateException e) {
            logAndThrowException("Error with getGermplasmListTypes() query from GermplasmList: " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Get Germplasm Name Types
     * 
     * Return a List of UserDefinedField POJOs representing records from 
     * the udflds table of IBDB which are the types of germplasm names.
     * 
     * @return
     * @throws MiddlewareQueryException
     */
    
    @SuppressWarnings("rawtypes")
	public List getGermplasmNameTypes() throws MiddlewareQueryException {
        try {
        	Session session = getSession();
        	SQLQuery query = session.createSQLQuery(GermplasmList.GET_GERMPLASM_NAME_TYPES); 
            List results = query.list();
            return results;

        } catch (HibernateException e) {
            logAndThrowException("Error with getGermplasmListTypes() query from GermplasmList: " + e.getMessage(), e);
            return null;
        }    	
    }    
}
