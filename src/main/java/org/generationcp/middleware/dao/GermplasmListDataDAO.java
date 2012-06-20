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

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class GermplasmListDataDAO extends GenericDAO<GermplasmListData, Integer>{

    @SuppressWarnings("unchecked")
    public List<GermplasmListData> getByListId(Integer id, int start, int numOfRows) {
        Criteria criteria = getSession().createCriteria(GermplasmListData.class);
        criteria.createAlias("list", "l");
        criteria.add(Restrictions.eq("l.id", id));
        criteria.setFirstResult(start);
        criteria.setMaxResults(numOfRows);
        return criteria.list();
    }

    public Long countByListId(Integer id) {
        Criteria criteria = getSession().createCriteria(GermplasmListData.class);
        criteria.createAlias("list", "l");
        criteria.add(Restrictions.eq("l.id", id));
        criteria.setProjection(Projections.rowCount());
        Long count = (Long) criteria.uniqueResult();
        return count;
    }

    @SuppressWarnings("unchecked")
    public List<GermplasmListData> getByListIdAndGID(Integer listId, Integer gid) {
        Criteria criteria = getSession().createCriteria(GermplasmListData.class);
        criteria.createAlias("list", "l");
        criteria.add(Restrictions.eq("l.id", listId));
        criteria.add(Restrictions.eq("gid", gid));
        return criteria.list();
    }

    public GermplasmListData getByListIdAndEntryId(Integer listId, Integer entryId) {
        Criteria criteria = getSession().createCriteria(GermplasmListData.class);
        criteria.createAlias("list", "l");
        criteria.add(Restrictions.eq("l.id", listId));
        criteria.add(Restrictions.eq("entryId", entryId));
        GermplasmListData result = (GermplasmListData) criteria.uniqueResult();
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<GermplasmListData> getByGID(Integer gid, int start, int numOfRows) {
        Criteria criteria = getSession().createCriteria(GermplasmListData.class);
        criteria.add(Restrictions.eq("gid", gid));
        criteria.setFirstResult(start);
        criteria.setMaxResults(numOfRows);
        return criteria.list();
    }

    public Long countByGID(Integer gid) {
        Criteria criteria = getSession().createCriteria(GermplasmListData.class);
        criteria.add(Restrictions.eq("gid", gid));
        criteria.setProjection(Projections.rowCount());
        Long count = (Long) criteria.uniqueResult();
        return count;
    }

    public int deleteByListId(Integer listId) {
        Query query = getSession().getNamedQuery(GermplasmListData.DELETE_BY_LIST_ID);
        query.setInteger("listId", listId);
        return query.executeUpdate();
    }

    public void validateId(GermplasmListData germplasmListData) throws QueryException {
        // Check if not a local record (has negative ID)
        Integer id = germplasmListData.getId();
        if (id != null && id.intValue() > 0) {
            throw new QueryException("Cannot update a Central Database record. "
                    + "GermplasmListData object to update must be a Local Record (ID must be negative)");
        }
    }
}
