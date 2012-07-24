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

import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.exceptions.QueryException;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

public class ProjectDAO extends GenericDAO<Project, Long>{

    /**
     * Get the list of {@link Project}s.
     * 
     * @return
     */
    public List<Project> findAll() throws QueryException{
        return findAll(null, null);
    }

    /**
     * Get the list of {@link Project}s.
     * 
     * @param start
     *            the index of the first result to return. This parameter is
     *            ignored if null.
     * @param numOfRows
     *            the number of rows to return. This parameter is ignored if
     *            null.
     * @return
     */
    public List<Project> findAll(Integer start, Integer numOfRows) throws QueryException{
        try {
            Criteria criteria = getSession().createCriteria(Project.class);
            if (start != null) {
                criteria.setFirstResult(start);
            }
            if (numOfRows != null) {
                criteria.setMaxResults(numOfRows);
            }
            @SuppressWarnings("unchecked")
            List<Project> projects = criteria.list();

            return projects;
        } catch (HibernateException e) {
            throw new QueryException(e.getMessage());
        }
    }
    
    public Project getById(Long projectId) throws QueryException{        
        try {
            Criteria criteria = getSession().createCriteria(Project.class)
                    .add(Restrictions.eq("projectId", projectId)).setMaxResults(1);
            return (Project) criteria.uniqueResult();
        } catch (HibernateException e) {
            throw new QueryException("Error with get project by id: " + e.getMessage());
        }
    }
}
