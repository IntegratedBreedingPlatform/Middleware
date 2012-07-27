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

import org.generationcp.middleware.pojos.workbench.WorkbenchDataset;
import org.generationcp.middleware.exceptions.QueryException;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
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
}
