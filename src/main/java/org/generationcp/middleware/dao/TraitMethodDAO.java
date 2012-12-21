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
import org.generationcp.middleware.pojos.TraitMethod;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;

public class TraitMethodDAO extends GenericDAO<TraitMethod, Integer>{

    @SuppressWarnings("unchecked")
    public List<TraitMethod> getByTraitId(Integer traitId) throws MiddlewareQueryException {
        try {
            SQLQuery query = getSession().createSQLQuery(TraitMethod.GET_BY_TRAIT_ID);
            query.addEntity("m", TraitMethod.class);
            query.setParameter("traitid", traitId);
            return query.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException(
                    "Error with getByTraitId(traitId=" + traitId + ") query from TraitMethod: " + e.getMessage(), e);
        }
    }
}
