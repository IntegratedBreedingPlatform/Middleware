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

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
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
     * @throws MiddlewareQueryException the MiddlewareQueryException
     */
    @SuppressWarnings("unchecked")
    public List<Long> getLocationIdsByProjectId(Long projectId, int start, int numOfRows) throws MiddlewareQueryException {

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
            throw new MiddlewareQueryException("Error with getLocationIdsByProjectId(projectId=" + projectId
                    + ") query from ProjectLocationMap: " + e.getMessage(), e);
        }
    }

    /**
     * Returns the number of {@link Location} ids by project id.
     *
     * @param projectId - the project id
     * @return the number of {@link Location} ids 
     * @throws MiddlewareQueryException the MiddlewareQueryException
     */
    public long countLocationIdsByProjectId(Long projectId) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(ProjectLocationMap.class);
            Project p = new Project();
            p.setProjectId(projectId);
            criteria.add(Restrictions.eq("project", p));
            criteria.setProjection(Projections.rowCount());

            return ((Long) criteria.uniqueResult()).longValue();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with countLocationIdsByProjectId(projectId=" + projectId
                    + ") query from ProjectLocationMap: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<ProjectLocationMap> getByProjectId(Long projectId, int start, int numOfRows) throws MiddlewareQueryException {

        if (projectId == null) {
            return new ArrayList<ProjectLocationMap>();
        }

        try {
            Criteria criteria = getSession().createCriteria(ProjectLocationMap.class);
            Project p = new Project();
            p.setProjectId(projectId);
            criteria.add(Restrictions.eq("project", p));
            criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);
            return criteria.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getProjectLocationMapByProjectId(projectId=" + projectId
                    + ") query from ProjectLocationMap: " + e.getMessage(), e);
        }
    }
}
