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
import org.generationcp.middleware.pojos.Method;
import org.hibernate.HibernateException;
import org.hibernate.Query;

public class MethodDAO extends GenericDAO<Method, Integer>{

    @SuppressWarnings("unchecked")
    public List<Method> getAll() throws QueryException {
        try {
            Query query = getSession().getNamedQuery(Method.GET_ALL);

            List<Method> results = query.list();
            return results;
        } catch (HibernateException ex) {
            throw new QueryException("Error with find all query for Method: " + ex.getMessage());
        }
    }
}
