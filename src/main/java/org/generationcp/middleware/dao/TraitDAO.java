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

import org.generationcp.middleware.pojos.Trait;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

public class TraitDAO extends GenericDAO<Trait, Integer>{

    public Trait getByTraitId(Integer id) {
        Criteria crit = getSession().createCriteria(Trait.class);
        crit.add(Restrictions.eq("traitId", id));
        crit.add(Restrictions.eq("nameStatus", new Integer(1)));
        List results = crit.list();

        if (results.isEmpty()) {
            return null;
        } else {
            return (Trait) results.get(0);
        }
    }
}
