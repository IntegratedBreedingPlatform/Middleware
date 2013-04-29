package org.generationcp.middleware.dao;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;


public class ProjectUserInfoDAO extends GenericDAO<ProjectUserInfo, Integer>{
    public ProjectUserInfo getByProjectIdAndUserId(Integer projectId, Integer userId) throws MiddlewareQueryException{
        try{
            Criteria criteria = getSession().createCriteria(ProjectUserInfo.class);
            criteria.add(Restrictions.eq("projectId", projectId ));
            criteria.add(Restrictions.eq("userId", userId));
            
            ProjectUserInfo ret = (ProjectUserInfo) criteria.uniqueResult();
            
            return ret;
        } catch(HibernateException ex){
            logAndThrowException("Error in getByProjectIdAndUserId(projectId = " + projectId
                    + ", userId = " + userId + "):" + ex.getMessage(), ex);
            return null;
        }
        
    }
}
