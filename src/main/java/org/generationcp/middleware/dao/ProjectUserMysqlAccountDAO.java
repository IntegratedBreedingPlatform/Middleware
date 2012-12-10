package org.generationcp.middleware.dao;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.ProjectUserMysqlAccount;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;


public class ProjectUserMysqlAccountDAO extends GenericDAO<ProjectUserMysqlAccount, Integer>{
    public ProjectUserMysqlAccount getByProjectIdAndUserId(Integer projectId, Integer userId) throws MiddlewareQueryException{
        try{
            Criteria criteria = getSession().createCriteria(ProjectUserMysqlAccount.class);
            criteria.createAlias("project", "p");
            criteria.createAlias("user", "u");
            criteria.add(Restrictions.eq("p.projectId", Long.valueOf(projectId.longValue())));
            criteria.add(Restrictions.eq("u.userid", userId));
            
            return (ProjectUserMysqlAccount) criteria.uniqueResult();
        } catch(HibernateException ex){
            throw new MiddlewareQueryException("Error in getByProjectIdAndUserId(projectId = " + projectId
                    + ", userId = " + userId + "):" + ex.getMessage(), ex);
        }
    }
}
