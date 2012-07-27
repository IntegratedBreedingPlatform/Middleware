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
import org.generationcp.middleware.pojos.ScaleDiscrete;
import org.generationcp.middleware.pojos.ScaleDiscretePK;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

public class ScaleDiscreteDAO extends GenericDAO<ScaleDiscrete, ScaleDiscretePK>{

    @SuppressWarnings("unchecked")
    public List<ScaleDiscrete> getByScaleId(Integer id) throws QueryException{
        try{
        Criteria crit = getSession().createCriteria(ScaleDiscrete.class);
        crit.add(Restrictions.eq("id.scaleId", id));
        return crit.list();
        } catch (HibernateException e) {
            throw new QueryException("Error in getting scale discrete by id " + e.getMessage(), e);
        }
    }
}
