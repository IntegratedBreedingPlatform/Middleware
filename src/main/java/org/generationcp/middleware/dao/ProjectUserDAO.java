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

import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUser;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Criterion;

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
        criteria.add(Restrictions.eq("user", user));
        List results = super.findByCriteria(criteria);
        return (ProjectUser) results.get(0);
    }
    


}
