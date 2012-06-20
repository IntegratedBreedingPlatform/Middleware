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
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.GermplasmList;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class GermplasmListDAO extends GenericDAO<GermplasmList, Integer>{

    @SuppressWarnings("unchecked")
    public List<GermplasmList> findByName(String name, int start, int numOfRows, Operation operation) {
        Criteria criteria = getSession().createCriteria(GermplasmList.class);

        if (operation == null || operation == Operation.EQUAL) {
            criteria.add(Restrictions.eq("name", name));
        } else if (operation == Operation.LIKE) {
            criteria.add(Restrictions.like("name", name));
        }

        criteria.setFirstResult(start);
        criteria.setMaxResults(numOfRows);

        return criteria.list();
    }

    public Long countByName(String name, Operation operation) {
        Criteria criteria = getSession().createCriteria(GermplasmList.class);
        criteria.setProjection(Projections.rowCount());

        if (operation == null || operation == Operation.EQUAL) {
            criteria.add(Restrictions.eq("name", name));
        } else if (operation == Operation.LIKE) {
            criteria.add(Restrictions.like("name", name));
        }

        Long count = (Long) criteria.uniqueResult();
        return count;
    }

    @SuppressWarnings("unchecked")
    public List<GermplasmList> findByStatus(Integer status, int start, int numOfRows) {
        Criteria criteria = getSession().createCriteria(GermplasmList.class);

        criteria.add(Restrictions.eq("status", status));

        criteria.setFirstResult(start);
        criteria.setMaxResults(numOfRows);

        return criteria.list();
    }

    public Long countByStatus(Integer status) {
        Criteria criteria = getSession().createCriteria(GermplasmList.class);

        criteria.add(Restrictions.eq("status", status));
        criteria.setProjection(Projections.rowCount());

        Long count = (Long) criteria.uniqueResult();
        return count;
    }

    public void validateId(GermplasmList germplasmList) throws QueryException {
        // Check if not a local record (has negative ID)
        Integer id = germplasmList.getId();
        if (id != null && id.intValue() > 0) {
            throw new QueryException("Cannot update a Central Database record. "
                    + "GermplasmList object to update must be a Local Record (ID must be negative)");
        }
    }
}
