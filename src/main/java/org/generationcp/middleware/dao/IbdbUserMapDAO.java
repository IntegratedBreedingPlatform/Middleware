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
import org.generationcp.middleware.pojos.workbench.IbdbUserMap;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link IbdbUserMap}.
 * 
 */
public class IbdbUserMapDAO extends GenericDAO<IbdbUserMap, Long>{

    public Integer getLocalIbdbUserId(Integer workbenchUserId, Long projectId) throws MiddlewareQueryException {
        try {
        	if (workbenchUserId != null && projectId != null){
	            Query query = getSession().createSQLQuery(IbdbUserMap.GET_LOCAL_IBDB_USER_ID);
	            query.setParameter("workbenchUserId", workbenchUserId);
	            query.setParameter("projectId", projectId);
	            return (Integer) query.uniqueResult();
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with getLocalIbdbUserId(workbenchUserId=" + workbenchUserId + ", projectId="
                    + projectId + ") query from IbdbUserMap: " + e.getMessage(), e);
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
	public List<IbdbUserMap> getIbdbUserMapByID(Long projectId) throws MiddlewareQueryException {
        try {
        	if (projectId != null){
        		 return getSession().createCriteria(IbdbUserMap.class)
                         .add(Restrictions.eq("projectId", projectId))
                         .list();
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with getIbdbUserMapByID( projectId="
                    + projectId + ") query from IbdbUserMap: " + e.getMessage(), e);
        }
        return null;
    }

}
