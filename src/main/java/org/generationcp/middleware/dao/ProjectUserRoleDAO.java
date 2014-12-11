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
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserRole;
import org.generationcp.middleware.pojos.workbench.Role;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for {@link ProjectUserRole}.
 * 
 * @author Joyce Avestro
 * 
 */
public class ProjectUserRoleDAO extends GenericDAO<ProjectUserRole, Integer>{

    public ProjectUserRole saveOrUpdate(ProjectUserRole projectUser) throws MiddlewareQueryException {

        if (projectUser.getProject() == null || projectUser.getProject().getProjectId() == null) {
            throw new IllegalArgumentException("Project cannot be null");
        }
        if (projectUser.getUserId() == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        return super.saveOrUpdate(projectUser);
    }

    public List<ProjectUserRole> getByProject(Project project) throws MiddlewareQueryException {
        try {
            List<Criterion> criteria = new ArrayList<Criterion>();
            criteria.add(Restrictions.eq("project", project));
            return (List<ProjectUserRole>) super.getByCriteria(criteria);
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error in getByProject(project=" + project + ") query from ProjectUser: " + e.getMessage(),
                    e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<User> getUsersByProjectId(Long projectId) throws MiddlewareQueryException {
        List<User> users = new ArrayList<User>();
        try {
        	if (projectId != null){
	            SQLQuery query = getSession().createSQLQuery(ProjectUserRole.GET_USERS_BY_PROJECT_ID);
	            query.setParameter("projectId", projectId);
	            List<Object> results = query.list();
	            for (Object o : results) {
	                Object[] user = (Object[]) o;
	                Integer userId = (Integer) user[0];
	                Integer instalId = (Integer) user[1];
	                Integer uStatus = (Integer) user[2];
	                Integer uAccess = (Integer) user[3];
	                Integer uType = (Integer) user[4];
	                String uName = (String) user[5];
	                String upswd = (String) user[6];
	                Integer personId = (Integer) user[7];
	                Integer aDate = (Integer) user[8];
	                Integer cDate = (Integer) user[9];
	                User u = new User(userId, instalId, uStatus, uAccess, uType, uName, upswd, personId, aDate, cDate);
	                users.add(u);
	            }
        	}
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error in getUsersByProjectId(projectId=" + projectId + ") query from ProjectUser: "
                    + e.getMessage(), e);
        }
        return users;
    }

    public long countUsersByProjectId(Long projectId) throws MiddlewareQueryException {
        try {
        	if (projectId != null){
	            SQLQuery query = getSession().createSQLQuery(ProjectUserRole.COUNT_USERS_BY_PROJECT_ID);
	            query.setParameter("projectId", projectId);
	            return ((BigInteger) query.uniqueResult()).longValue();
        	}
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error in countUsersByProjectId(projectId=" + projectId + ") query from ProjectUser: "
                    + e.getMessage(), e);
        }
        return 0;
    }
    
    @SuppressWarnings("unchecked")
    public List<Role> getRolesByProjectAndUser(Project project, User user) throws MiddlewareQueryException{
        List<Role> roles = new ArrayList<Role>();
        try{
        	if (project != null && user != null){
	            Criteria criteria = getSession().createCriteria(ProjectUserRole.class);
	            criteria.add(Restrictions.eq("project", project));
	            criteria.add(Restrictions.eq("userId", user.getUserid()));
	            List<ProjectUserRole> projectUsers = criteria.list();
	            
	            for (ProjectUserRole projectUser : projectUsers){
	                roles.add(projectUser.getRole());
	            }
        	}
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error in getByProjectAndUser(project=" + project + ", user=" + user
                + ") query from Role: " + e.getMessage(), e);
        }
        return roles;
    }
    
    @SuppressWarnings("unchecked")
    public List<Project> getProjectsByUser(User user) throws MiddlewareQueryException {
        try {
        	if (user != null){
	            Criteria criteria = getSession().createCriteria(ProjectUserRole.class);
	            criteria.add(Restrictions.eq("userId", user.getUserid()));
	            criteria.setProjection(Projections.distinct(Projections.property("project")));
	            return criteria.list();
        	}
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error in getProjectsByUser(user=" + user
                + ") query from Project: " + e.getMessage(), e);
        }
        return new ArrayList<Project>();
    }
}
