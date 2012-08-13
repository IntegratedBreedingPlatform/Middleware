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
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectLocationMap;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;


/**
 * <b>Description</b>: DAO class for ProjectLocationMap.
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Michael Blancaflor
 * <br>
 * <b>File Created</b>: Aug 9, 2012
 */
public class ProjectLocationMapDAO extends GenericDAO<ProjectLocationMap, Long>{
    
    /**
     * Returns a list of {@link Location} ids by project id.
     *
     * @param projectId - the project id
     * @param start - the start
     * @param numOfRows - the num of rows
     * @return the list of {@link Location} ids
     * @throws QueryException the query exception
     */
    @SuppressWarnings("unchecked")
    public List<Long> getLocationIdsByProjectId(Long projectId, int start, int numOfRows) 
        throws QueryException {
        
        try {
            Criteria criteria = getSession().createCriteria(ProjectLocationMap.class);
            Project p = new Project();
            p.setProjectId(projectId);
            criteria.add(Restrictions.eq("project", p));
            criteria.setProjection(Projections.property("locationId"));
            criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);
            
            return criteria.list();
        } catch (HibernateException e) {
            throw new QueryException("Error with getLocationIdsByProjectId(): " + e.getMessage(), e);
        }
    }
    
    /**
     * Returns the number of {@link Location} ids by project id.
     *
     * @param projectId - the project id
     * @return the number of {@link Location} ids 
     * @throws QueryException the query exception
     */
    public Long countLocationIdsByProjectId(Long projectId) 
        throws QueryException {
        try {
            Criteria criteria = getSession().createCriteria(ProjectLocationMap.class);
            Project p = new Project();
            p.setProjectId(projectId);
            criteria.add(Restrictions.eq("project", p));
            criteria.setProjection(Projections.rowCount());
            
            return (Long) criteria.uniqueResult();
        } catch (HibernateException e) {
            throw new QueryException("Error with countLocationIdsByProjectId(): " + e.getMessage(), e);
        }
    }
}
