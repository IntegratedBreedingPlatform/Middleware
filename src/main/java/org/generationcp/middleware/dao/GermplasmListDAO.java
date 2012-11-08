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

import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.GermplasmList;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class GermplasmListDAO extends GenericDAO<GermplasmList, Integer>{

    @SuppressWarnings("unchecked")
    public List<GermplasmList> getByName(String name, int start, int numOfRows, Operation operation) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(GermplasmList.class);

            if (operation == null || operation == Operation.EQUAL) {
                criteria.add(Restrictions.eq("name", name));
            } else if (operation == Operation.LIKE) {
                criteria.add(Restrictions.like("name", name));
            }

            criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);
            return criteria.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getByName(name=" + name + ") query from GermplasmList: " + e.getMessage(), e);
        }
    }

    public long countByName(String name, Operation operation) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(GermplasmList.class);
            criteria.setProjection(Projections.rowCount());

            if (operation == null || operation == Operation.EQUAL) {
                criteria.add(Restrictions.eq("name", name));
            } else if (operation == Operation.LIKE) {
                criteria.add(Restrictions.like("name", name));
            }
            return ((Long) criteria.uniqueResult()).longValue(); // count
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with countByName(name=" + name + ") query from GermplasmList: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<GermplasmList> getByStatus(Integer status, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(GermplasmList.class);
            criteria.add(Restrictions.eq("status", status));
            criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);
            return criteria.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getByStatus(status=" + status + ") query from GermplasmList: " + e.getMessage(),
                    e);
        }
    }

    public long countByStatus(Integer status) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(GermplasmList.class);
            criteria.add(Restrictions.eq("status", status));
            criteria.setProjection(Projections.rowCount());
            return ((Long) criteria.uniqueResult()).longValue(); // count
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with countByStatus(status=" + status + ") query from GermplasmList: "
                    + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<GermplasmList> getAllTopLevelLists(int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Criterion topFolder = Restrictions.eq("parent.id", 0);
            Criterion nullFolder = Restrictions.isNull("parent");
            
            Criteria criteria = getSession().createCriteria(GermplasmList.class);
            criteria.add(Restrictions.or(topFolder, nullFolder));
            criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);
            return criteria.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getAllTopLevelLists() query from GermplasmList: " + e.getMessage(), e);
        }
    }

    public long countAllTopLevelLists() throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(GermplasmList.class);
            criteria.add(Restrictions.eq("parent.id", 0));
            criteria.setProjection(Projections.rowCount());
            return ((Long) criteria.uniqueResult()).longValue();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with countAllTopLevelLists() query from GermplasmList: " + e.getMessage(), e);
        }
    }

    public void validateId(GermplasmList germplasmList) throws MiddlewareQueryException {
        // Check if not a local record (has negative ID)
        Integer id = germplasmList.getId();
        if (id != null && id.intValue() > 0) {
            throw new MiddlewareQueryException("Error with validateId(germplasmList=" + germplasmList
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
            criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);
            return criteria.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getByParentFolderId(parentId=" + parentId + ") query from GermplasmList: "
                    + e.getMessage(), e);
        }
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
            criteria.setProjection(Projections.rowCount());
            return ((Long) criteria.uniqueResult()).longValue(); // count
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with countByParentFolderId(parentId=" + parentId + ") query from GermplasmList: "
                    + e.getMessage(), e);
        }
    }
}
