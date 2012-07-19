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
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.QueryException;
import org.hibernate.criterion.Restrictions;

public class DatasetDAO extends GenericDAO<WorkbenchDataset, Long>{

    /**
     * Get the list of {@link WorkbenchDataset}s.
     * 
     * @return
     */
    public List<WorkbenchDataset> findAll() {
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
    public List<WorkbenchDataset> findAll(Integer start, Integer numOfRows) {
        try {
            Criteria criteria = getSession().createCriteria(WorkbenchDataset.class);
            if (start != null) {
                criteria.setFirstResult(start);
            }
            if (numOfRows != null) {
                criteria.setMaxResults(numOfRows);
            }
            return (List<WorkbenchDataset>) criteria.list();
        } catch (HibernateException ex) {
            throw new QueryException(ex);
        }
    }
    
    public WorkbenchDataset getById(Long datasetId){        
        try {
            Criteria criteria = getSession().createCriteria(WorkbenchDataset.class)
                    .add(Restrictions.eq("datasetId", datasetId)).setMaxResults(1);
            return (WorkbenchDataset) criteria.uniqueResult();
        } catch (HibernateException ex) {
            throw new QueryException(ex);
        }
    }
}
