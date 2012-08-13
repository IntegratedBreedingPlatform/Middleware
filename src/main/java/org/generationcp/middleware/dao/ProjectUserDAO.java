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

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUser;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

// TODO: Auto-generated Javadoc
/**
 * The Class ProjectUserDAO.
 * 
 * @author Joyce Avestro
 * 
 */
public class ProjectUserDAO extends GenericDAO<ProjectUser, Integer>{

    /* (non-Javadoc)
     * @see org.generationcp.middleware.dao.GenericDAO#saveOrUpdate(java.lang.Object)
     */
    /**
     * Save or update.
     *
     * @param projectUser the project user
     * @return the project user
     */
    public ProjectUser saveOrUpdate(ProjectUser projectUser) {
        
        if (projectUser.getProject() == null || projectUser.getProject().getProjectId() == null){
            throw new IllegalArgumentException("Project cannot be null");
        }
        if (projectUser.getUserId() == null){
            throw new IllegalArgumentException("User cannot be null");
        }
        
        return super.saveOrUpdate(projectUser);
    }
    
    /**
     * Gets the ProjectUser by id.
     *
     * @param id the ProjectUser id
     * @return the associated ProjectUser
     */
    public ProjectUser getById(Integer id){
        return super.findById(id, false);        
    }

    /**
     * Gets the ProjectUser by project and user.
     *
     * @param project the project
     * @param user the user
     * @return the ProjectUser associated to the given project and user
     */
    @SuppressWarnings("rawtypes")
    public ProjectUser getByProjectAndUser(Project project, User user){
        List<Criterion> criteria = new ArrayList<Criterion>();
        criteria.add(Restrictions.eq("project", project));
        criteria.add(Restrictions.eq("userId", user.getUserid()));
        List results = super.findByCriteria(criteria);
        return (ProjectUser) results.get(0);
    }
    
    
    /**
     * Return a List of {@link User} records associated with a {@link Project}
     *
     * @param projectId - the project id
     * @return the List of {@link User} records
     * @throws QueryException the query exception
     */
    @SuppressWarnings("unchecked")
    public List<User> getUsersByProjectId(Long projectId) throws QueryException {
        try {
            
            SQLQuery query = getSession().createSQLQuery(ProjectUser.GET_USERS_BY_PROJECT_ID);
            query.setParameter("projectId", projectId);
            List<User> users = new ArrayList<User>();
            
            List<Object> results = query.list();
            for(Object o : results) {
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
            
            return users;
        } catch (HibernateException e) {
            throw new QueryException("Error in getUsersByProjectId(): " + e.getMessage(), e);
        }
    }
    
    /**
     * Returns the number of {@link User} records associated with a {@link Project}
     *
     * @param projectId - the project id
     * @return the number of {@link User} records
     * @throws QueryException the query exception
     */
    public Long countUsersByProjectId(Long projectId) throws QueryException {
        try {
            Criteria criteria = getSession().createCriteria(ProjectUser.class);
            Project p = new Project();
            p.setProjectId(projectId);
            criteria.add(Restrictions.eq("project", p));
            criteria.setProjection(Projections.rowCount());
            
            Long result = (Long) criteria.uniqueResult();
            return result;
        } catch (HibernateException e) {
            throw new QueryException("Error in countUsersByProjectId(): " + e.getMessage(), e);
        }
    }

}
