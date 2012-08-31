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

import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.exceptions.QueryException;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

/**
 * The Class CropTypeDAO.
 * 
 * @author Joyce Avestro
 * 
 */
public class CropTypeDAO extends GenericDAO<CropType, Long>{

    /**
     * Gets the CropType by crop name.
     *
     * @param cropName the crop name
     * @return the CropType matching the given name
     * @throws QueryException
     */
    public CropType getByName(String cropName) throws QueryException{
        try {
            Criteria criteria = getSession().createCriteria(CropType.class);
            criteria.add(Restrictions.eq("cropName", cropName));            
            return (CropType) criteria.uniqueResult();
        } catch (HibernateException e) {
            throw new QueryException("Error with get crop type by name: " + e.getMessage(), e);
        }
        
    }
    
    
}
