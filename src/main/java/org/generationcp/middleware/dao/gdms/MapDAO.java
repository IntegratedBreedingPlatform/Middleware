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

import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.pojos.gdms.Map;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.QueryException;

/**
 * <b>Description</b>: DAO for Map table
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Michael Blancaflor
 * <br>
 * <b>File Created</b>: Jul 9, 2012.
 */
public class MapDAO extends GenericDAO<Map, Integer>{
    
    /**
     * Returns all Map records.
     *
     * @param start the start
     * @param numOfRows the num of rows
     * @return the list
     */
    @SuppressWarnings("unchecked")
    public List<Map> findAll(Integer start, Integer numOfRows) {
        try {
            Criteria criteria = getSession().createCriteria(Map.class);
            if (start != null) {
                criteria.setFirstResult(start);
            }
            if (numOfRows != null) {
                criteria.setMaxResults(numOfRows);
            }
            
            List<Map> templates = criteria.list();

            return templates;
        } catch (HibernateException ex) {
            throw new QueryException(ex);
        }
    }
    
}
