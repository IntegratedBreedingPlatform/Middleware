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
package org.generationcp.middleware.dao.gdms;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.gdms.DatasetUsers;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link DatasetUsers}.
 *
 * <b>Authors</b>: Dennis Billano <br>
 * <b>File Created</b>: Mar 7, 2013
 */

public class DatasetUsersDAO extends GenericDAO<DatasetUsers, Integer>{

	@SuppressWarnings("unchecked")
	public List<DatasetUsers> getByDatasetId(int datasetId) throws MiddlewareQueryException {
    	List<DatasetUsers> list = new ArrayList<DatasetUsers>();
    	try {
    		Criteria criteria = getSession().createCriteria(DatasetUsers.class);
            criteria.add(Restrictions.eq("datasetId", datasetId));
            
            return criteria.list();
    		
        } catch (HibernateException e) {
        	logAndThrowException("Error with getByDatasetId(" + datasetId + ") query from DatasetUsersDAO: " + e.getMessage(), e);    
    	}
    	return list;
    }
	
}
