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

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectMethod;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;

/**
 * The Class ProjectMethodDAO.
 * 
 * @author Joyce Avestro
 * 
 */
public class ProjectMethodDAO extends GenericDAO<ProjectMethod, Integer>{


    /**
     * Returns a list of {@link Method} records by project id.
     *
     * @param projectId the project id
     * @param start the first row to retrieve
     * @param numOfRows the number of rows to retrieve
     * @return the list of {@link Method}s
     * @throws QueryException the query exception
     */
    @SuppressWarnings("unchecked")
    public List<Integer> getByProjectId(Long projectId, int start, int numOfRows) 
        throws QueryException {
        
        if (projectId == null){
            return new ArrayList<Integer>();
        }
        
        try {
            SQLQuery query = getSession().createSQLQuery(ProjectMethod.GET_METHODS_BY_PROJECT_ID);
            query.setParameter("projectId", projectId.intValue());
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);
            return (List<Integer>) query.list();
        } catch (HibernateException e) {
            throw new QueryException("Error with getByProjectId query: " + e.getMessage(), e);
        } 
    }
    
    /**
     * Returns the number of {@link Method} records by project id.
     *
     * @param projectId the project id
     * @return the number of {@link Method} records
     * @throws QueryException the query exception
     */
    public Long countByProjectId(Long projectId) throws QueryException {
        try {
            SQLQuery query = getSession().createSQLQuery(ProjectMethod.COUNT_METHODS_BY_PROJECT_ID);
            query.setParameter("projectId", projectId.intValue());
            BigInteger result = (BigInteger) query.uniqueResult();
            return Long.valueOf(result.longValue());
        } catch (HibernateException e) {
            throw new QueryException("Error with countByProjectId query: " + e.getMessage(), e);
        }
    }
    
    @SuppressWarnings("rawtypes")
    public List<ProjectMethod> getProjectMethodByProject(Project project, int start, int numOfRows) 
            throws QueryException {
            
            if (project == null || project.getProjectId() == null){
                return new ArrayList<ProjectMethod>();
            }

            try {
                SQLQuery query = getSession().createSQLQuery(ProjectMethod.GET_PROJECT_METHODS_BY_PROJECT_ID);
                query.setParameter("projectId", project.getProjectId().intValue());
                query.setFirstResult(start);
                query.setMaxResults(numOfRows);
                List results = query.list();
                List<ProjectMethod> toReturn = new ArrayList<ProjectMethod>();
                for (Object o : results) {
                    Object[] result = (Object[]) o;
                    if (result != null) {
                        Long projectMethodId = Long.valueOf((Integer)result[0]);
                        Integer methodId = (Integer) result[2];
                        ProjectMethod projectMethod = new ProjectMethod(projectMethodId, project, methodId);
                        toReturn.add(projectMethod);
                    }
                }
                return toReturn;
                
            } catch (HibernateException e) {
                throw new QueryException("Error with getProjectMethodByProjectId query: " + e.getMessage(), e);
            } 
        }

}
