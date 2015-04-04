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
package org.generationcp.middleware.dao.oms;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.oms.CVTermAlias;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * DAO class for {@link org.generationcp.middleware.pojos.oms.CVTermAlias}.
 * http://gmod.org/wiki/Chado_Tables#Table:_cvtermalias
 * 
 */
public class CvTermAliasDao extends GenericDAO<CVTermAlias, Integer> {

    public CVTermAlias getByCvTermAndProgram(Integer cvTermId, Integer programId) throws MiddlewareQueryException {
        CVTermAlias alias = null;
        try {
            Criteria criteria = getSession().createCriteria(getPersistentClass());
            criteria.add(Restrictions.eq("cvTermId", cvTermId));
            criteria.add(Restrictions.eq("programId", programId));
            List aliases = criteria.list();
            if (aliases != null && !aliases.isEmpty()) {
                alias = (CVTermAlias) aliases.get(0);
            }
            return alias;

        } catch(HibernateException e) {
            throw new MiddlewareQueryException("Error at getByCvTermId=" + cvTermId + " query on CVTermPropertyDao: " + e.getMessage(), e);
        }
    }

    public CVTermAlias save(Integer cvTermId, Integer programId, String name) throws MiddlewareQueryException{
        CVTermAlias alias = getByCvTermAndProgram(cvTermId, programId);
        if(alias == null) return save(new CVTermAlias(getNextId(CVTermAlias.ID_NAME), cvTermId, programId, name));
        alias.setName(name);
        return merge(alias);
    }
}
