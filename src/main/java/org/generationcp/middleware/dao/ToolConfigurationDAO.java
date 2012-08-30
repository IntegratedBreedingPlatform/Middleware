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
import org.generationcp.middleware.pojos.workbench.ToolConfiguration;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;


/**
 * <b>Description</b>: DAO class for {@link ToolConfiguration}.
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Michael Blancaflor
 * <br>
 * <b>File Created</b>: Aug 28, 2012
 */
public class ToolConfigurationDAO extends GenericDAO<ToolConfiguration, Long>{
    
    @SuppressWarnings("unchecked")
    public List<ToolConfiguration> getListOfToolConfigurationsByToolId(Long toolId) throws QueryException {
        try {
            Criteria criteria = getSession().createCriteria(ToolConfiguration.class);
            criteria.add(Restrictions.eq("toolId", toolId));
            
            return criteria.list();
            
        } catch (HibernateException e) {
            throw new QueryException("Error with getListOfToolConfigurationsByToolId(): " + e.getMessage(), e);
        }
    }
    
    public ToolConfiguration getToolConfigurationByToolIdAndConfigKey(Long toolId, String configKey) 
        throws QueryException {
        try {
            Criteria criteria = getSession().createCriteria(ToolConfiguration.class);
            criteria.add(Restrictions.eq("toolId", toolId));
            criteria.add(Restrictions.eq("configKey", configKey));
            
            return (ToolConfiguration) criteria.uniqueResult();
            
        } catch (HibernateException e) {
            throw new QueryException("Error with getToolConfigurationByToolIdAndConfigKey(): " + e.getMessage(), e);
        }
    }

}
