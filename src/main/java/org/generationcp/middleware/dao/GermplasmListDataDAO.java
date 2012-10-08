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
import org.generationcp.middleware.pojos.GermplasmListData;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class GermplasmListDataDAO extends GenericDAO<GermplasmListData, Integer>{

    @SuppressWarnings("unchecked")
    public List<GermplasmListData> getByListId(Integer id, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(GermplasmListData.class);
            criteria.createAlias("list", "l");
            criteria.add(Restrictions.eq("l.id", id));
            criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);
            return criteria.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getByListId(id=" + id + ") query from GermplasmListData " + e.getMessage(), e);
        }
    }

    public long countByListId(Integer id) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(GermplasmListData.class);
            criteria.createAlias("list", "l");
            criteria.add(Restrictions.eq("l.id", id));
            criteria.setProjection(Projections.rowCount());
            return ((Long) criteria.uniqueResult()).longValue(); //count
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with countByListId(id=" + id + ") query from GermplasmListData " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<GermplasmListData> getByListIdAndGID(Integer listId, Integer gid) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(GermplasmListData.class);
            criteria.createAlias("list", "l");
            criteria.add(Restrictions.eq("l.id", listId));
            criteria.add(Restrictions.eq("gid", gid));
            return criteria.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getByListIdAndGID(listId=" + listId + ", gid=" + gid
                    + ") query from GermplasmListData " + e.getMessage(), e);
        }
    }

    public GermplasmListData getByListIdAndEntryId(Integer listId, Integer entryId) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(GermplasmListData.class);
            criteria.createAlias("list", "l");
            criteria.add(Restrictions.eq("l.id", listId));
            criteria.add(Restrictions.eq("entryId", entryId));
            return (GermplasmListData) criteria.uniqueResult();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getByListIdAndEntryId(listId=" + listId + ", entryId=" + entryId
                    + ") query from GermplasmListData " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<GermplasmListData> getByGID(Integer gid, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(GermplasmListData.class);
            criteria.add(Restrictions.eq("gid", gid));
            criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);
            return criteria.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getByGID(gid=" + gid + ") query from GermplasmListData " + e.getMessage(), e);
        }
    }

    public long countByGID(Integer gid) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(GermplasmListData.class);
            criteria.add(Restrictions.eq("gid", gid));
            criteria.setProjection(Projections.rowCount());
            return ((Long) criteria.uniqueResult()).longValue(); //count
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with countByGID(gid=" + gid + ") query from GermplasmListData " + e.getMessage(), e);
        }
    }

    public int deleteByListId(Integer listId) throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(GermplasmListData.DELETE_BY_LIST_ID);
            query.setInteger("listId", listId);
            return query.executeUpdate();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with deleteByListId(listId=" + listId + ")  query from GermplasmListData "
                    + e.getMessage(), e);
        }
    }

    public void validateId(GermplasmListData germplasmListData) throws MiddlewareQueryException {
        // Check if not a local record (has negative ID)
        Integer id = germplasmListData.getId();
        if (id != null && id.intValue() > 0) {
            throw new MiddlewareQueryException("Error with validateId(germplasmListData=" + germplasmListData
                    + "): Cannot update a Central Database record. "
                    + "GermplasmListData object to update must be a Local Record (ID must be negative)");
        }
    }
}
