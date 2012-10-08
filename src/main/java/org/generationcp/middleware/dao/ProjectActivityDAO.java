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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

/**
 * The Class ProjectActivityDAO.
 * 
 * @author Joyce Avestro
 * 
 */
public class ProjectActivityDAO extends GenericDAO<ProjectActivity, Integer>{

    /**
     * Returns a list of {@link ProjectActivity} records by project id.
     *
     * @param projectId the project id
     * @param start the first row to retrieve
     * @param numOfRows the number of rows to retrieve
     * @return the list of {@link ProjectActivity} records
     * @throws MiddlewareQueryException the MiddlewareQueryException
     */
    @SuppressWarnings("unchecked")
    public List<ProjectActivity> getByProjectId(Long projectId, int start, int numOfRows) throws MiddlewareQueryException {

        if (projectId == null) {
            return new ArrayList<ProjectActivity>();
        }

        try {
            Criteria criteria = getSession().createCriteria(ProjectActivity.class);
            Project p = new Project();
            p.setProjectId(projectId);
            criteria.add(Restrictions.eq("project", p));
            criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);
            return (List<ProjectActivity>) criteria.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getByProjectId(projectId=" + projectId + ") query from ProjectActivity "
                    + e.getMessage(), e);
        }
    }

    /**
     * Returns the number of {@link ProjectActivity} records by project id.
     *
     * @param projectId the project id
     * @return the number of {@link ProjectActivity} records
     * @throws MiddlewareQueryException the MiddlewareQueryException
     */
    public long countByProjectId(Long projectId) throws MiddlewareQueryException {
        try {
            SQLQuery query = getSession().createSQLQuery(ProjectActivity.COUNT_ACTIVITIES_BY_PROJECT_ID);
            query.setParameter("projectId", projectId.intValue());
            BigInteger result = (BigInteger) query.uniqueResult();
            return result.longValue();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with countByProjectId(projectId=" + projectId + ") query from ProjectActivity "
                    + e.getMessage(), e);
        }
    }
}
