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
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchDataset;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class WorkbenchDatasetDAO extends GenericDAO<WorkbenchDataset, Long>{

    /**
     * Get the list of {@link WorkbenchDataset}s.
     * 
     * @return
     */
    public List<WorkbenchDataset> findAll() throws QueryException{
        return findAll(null, null);
    }

    /**
     * Get the list of {@link WorkbenchDataset}s.
     * 
     * @param start
     *            the index of the first result to return. This parameter is
     *            ignored if null.
     * @param numOfRows
     *            the number of rows to return. This parameter is ignored if
     *            null.
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<WorkbenchDataset> findAll(Integer start, Integer numOfRows)  throws QueryException{
        try {
            Criteria criteria = getSession().createCriteria(WorkbenchDataset.class);
            if (start != null) {
                criteria.setFirstResult(start);
            }
            if (numOfRows != null) {
                criteria.setMaxResults(numOfRows);
            }
            return (List<WorkbenchDataset>) criteria.list();
        } catch (HibernateException e) {
            throw new QueryException("Error finding all workbench datasets: " + e.getMessage(), e);
        }
    }
    
    public WorkbenchDataset getById(Long datasetId) throws QueryException{        
        try {
            Criteria criteria = getSession().createCriteria(WorkbenchDataset.class)
                    .add(Restrictions.eq("datasetId", datasetId)).setMaxResults(1);
            return (WorkbenchDataset) criteria.uniqueResult();
        } catch (HibernateException e) {
            throw new QueryException("Error finding all workbench datasets: " + e.getMessage(), e);
        }
    }
    
    /**
     * Returns a list of {@link WorkbenchDataset} records by project id.
     *
     * @param projectId the project id
     * @param start the start
     * @param numOfRows the num of rows
     * @return the list of {@link WorkbenchDataset}s
     * @throws QueryException the query exception
     */
    @SuppressWarnings("unchecked")
    public List<WorkbenchDataset> getByProjectId(Long projectId, int start, int numOfRows) 
        throws QueryException {
        
        try {
            Criteria criteria = getSession().createCriteria(WorkbenchDataset.class);
            Project p = new Project();
            p.setProjectId(projectId);
            
            criteria.add(Restrictions.eq("project", p));
            criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);
            
            return (List<WorkbenchDataset>) criteria.list();
        } catch (HibernateException e) {
            throw new QueryException("Error with getByWorkbenchProjectId query: " + e.getMessage(), e);
        } 
    }
    
    /**
     * Returns the number of {@link WorkbenchDataset} records by project id.
     *
     * @param projectId the project id
     * @return the number of {@link WorkbenchDataset} records
     * @throws QueryException the query exception
     */
    public Long countByProjectId(Long projectId) throws QueryException {
        try {
            Criteria criteria = getSession().createCriteria(WorkbenchDataset.class);
            Project p = new Project();
            p.setProjectId(projectId);
            
            criteria.add(Restrictions.eq("project", p));
            criteria.setProjection(Projections.rowCount());
            
            return (Long) criteria.uniqueResult();
        } catch (HibernateException e) {
            throw new QueryException("Error with countByWorkbenchProjectId query: " + e.getMessage(), e);
        }
    }
    
    /**
     * Returns a list of {@link WorkbenchDataset} by name.
     *
     * @param name - the {@link WorkbenchDataset} name
     * @param op - the operator; EQUAL, LIKE
     * @param start - the start
     * @param numOfRows - the num of rows
     * @return the list of {@link WorkbenchDataset}
     * @throws QueryException the query exception
     */
    @SuppressWarnings("unchecked")
    public List<WorkbenchDataset> getByName(String name, Operation op, int start, int numOfRows) 
        throws QueryException {
        
        try {
            Criteria criteria = getSession().createCriteria(WorkbenchDataset.class);
            
            if(Operation.EQUAL.equals(op)) {
                criteria.add(Restrictions.eq("name", name));
            } else if (Operation.LIKE.equals(op)) {
                criteria.add(Restrictions.like("name", name, MatchMode.ANYWHERE));
            } else {
                throw new QueryException("Operation " + op.toString() + " not supported.");
            }
           
            criteria.setFirstResult(start);
            criteria.setMaxResults(numOfRows);
            
            return (List<WorkbenchDataset>) criteria.list();
        } catch (HibernateException e) {
            throw new QueryException("Error with getByName query: " + e.getMessage(), e);
        }
    }
    
    /**
     * Returns the number of {@link WorkbenchDataset} by name.
     *
     * @param name - the {@link WorkbenchDataset} name
     * @param op - the operator; EQUAL, LIKE
     * @return the number of {@link WorkbenchDataset}
     * @throws QueryException the query exception
     */
    public Long countByName(String name, Operation op) throws QueryException {
        try {
            Criteria criteria = getSession().createCriteria(WorkbenchDataset.class);
            
            if(Operation.EQUAL.equals(op)) {
                criteria.add(Restrictions.eq("name", name));
            } else if (Operation.LIKE.equals(op)) {
                criteria.add(Restrictions.like("name", name, MatchMode.ANYWHERE));
            } else {
                throw new QueryException("Operation " + op.toString() + " not supported.");
            }
            
            criteria.setProjection(Projections.rowCount());
            
            return (Long) criteria.uniqueResult();
        } catch (HibernateException e) {
            throw new QueryException("Error with countByName query: " + e.getMessage(), e);
        }
    }
}
