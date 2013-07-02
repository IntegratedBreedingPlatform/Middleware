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

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;


/**
 * DAO class for {@link ProjectUserInfo}.
 * 
 */
public class ProjectUserInfoDAO extends GenericDAO<ProjectUserInfo, Integer>{
    public ProjectUserInfo getByProjectIdAndUserId(Integer projectId, Integer userId) throws MiddlewareQueryException{
        try{
        	if (projectId != null && userId != null){
	            Criteria criteria = getSession().createCriteria(ProjectUserInfo.class);
	            criteria.add(Restrictions.eq("projectId", projectId ));
	            criteria.add(Restrictions.eq("userId", userId));	            
	            return (ProjectUserInfo) criteria.uniqueResult();
        	}
        } catch(HibernateException ex){
            logAndThrowException("Error in getByProjectIdAndUserId(projectId = " + projectId
                    + ", userId = " + userId + "):" + ex.getMessage(), ex);
        }
        return null;
    }
}
