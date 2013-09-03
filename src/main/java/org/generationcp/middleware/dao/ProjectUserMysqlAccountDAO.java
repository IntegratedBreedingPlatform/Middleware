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
import org.generationcp.middleware.pojos.workbench.ProjectUserMysqlAccount;
import org.hibernate.Criteria;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
  
/**
 * DAO class for {@link ProjectUserMysqlAccount}.
 * 
 */
public class ProjectUserMysqlAccountDAO extends GenericDAO<ProjectUserMysqlAccount, Integer>{
    public ProjectUserMysqlAccount getByProjectIdAndUserId(Integer projectId, Integer userId) throws MiddlewareQueryException{
        try{
        	if (projectId != null && userId != null){
	            Criteria criteria = getSession().createCriteria(ProjectUserMysqlAccount.class);
	            criteria.createAlias("project", "p");
	            criteria.createAlias("user", "u");
	            criteria.add(Restrictions.eq("p.projectId", Long.valueOf(projectId.longValue())));
	            criteria.add(Restrictions.eq("u.userid", userId));
	            return (ProjectUserMysqlAccount) criteria.uniqueResult();
        	}
        } catch(HibernateException ex){
            logAndThrowException("Error in getByProjectIdAndUserId(projectId = " + projectId
                    + ", userId = " + userId + "):" + ex.getMessage(), ex);
        }
        return null;
    }

 
    @SuppressWarnings("unchecked")
	public List<ProjectUserMysqlAccount> getByProjectId(Integer projectId) throws MiddlewareQueryException{
        try{
        	if (projectId != null ){
	            Criteria criteria = getSession().createCriteria(ProjectUserMysqlAccount.class);
	            criteria.createAlias("project", "p");
	            criteria.createAlias("user", "u");
	            criteria.add(Restrictions.eq("p.projectId", Long.valueOf(projectId.longValue())));
	            return (List<ProjectUserMysqlAccount>) criteria.list();
        	}
        } catch(HibernateException ex){
            logAndThrowException("Error in getByProjectIdAndUserId(projectId = " + projectId
                    + "):" + ex.getMessage(), ex);
        }
        return null;
    }
}
