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
import org.generationcp.middleware.pojos.Scale;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

public class ScaleDAO extends GenericDAO<Scale, Integer>{

    @SuppressWarnings("unchecked")
    public List<Scale> getByTraitId(Integer traitId) throws MiddlewareQueryException {
        try {
            Criteria crit = getSession().createCriteria(Scale.class);
            crit.add(Restrictions.eq("traitId", traitId));
            return crit.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getByTraitId(traitId=" + traitId + ") query from Scale: " + e.getMessage(), e);
        }
    }

}
