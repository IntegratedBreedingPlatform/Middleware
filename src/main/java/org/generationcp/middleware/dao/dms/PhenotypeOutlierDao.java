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
package org.generationcp.middleware.dao.dms;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.PhenotypeOutlier;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO class for {@link PhenotypeOutlier}.
 * 
 */
@SuppressWarnings("unchecked")
public class PhenotypeOutlierDao extends GenericDAO<PhenotypeOutlier, Integer> {
    
    private static final Logger LOG = LoggerFactory.getLogger(PhenotypeOutlierDao.class);
    
    
    public PhenotypeOutlier getPhenotypeOutlierByPhenotypeId(Integer phenotypeId) throws MiddlewareQueryException{
    	
    	try{
    		
    		Criteria criteria = getSession().createCriteria(getPersistentClass());
    		criteria.add(Restrictions.eq("phenotypeId", phenotypeId));

    		PhenotypeOutlier result = (PhenotypeOutlier) criteria.uniqueResult();
    		return result;
    		
    	} catch (HibernateException e) {
            logAndThrowException(
                    "Error in getPhenotypeOutlierId(" + phenotypeId + ") in PhenotypeOutlierDao: " + e.getMessage(), e);
		}
    	
    	return null;
    	
    }
	
}
