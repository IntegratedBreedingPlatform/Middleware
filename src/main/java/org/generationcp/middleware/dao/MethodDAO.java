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

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Method;
import org.hibernate.HibernateException;
import org.hibernate.Query;

public class MethodDAO extends GenericDAO<Method, Integer>{

    @SuppressWarnings("unchecked")
    public List<Method> getAllMethod() throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(Method.GET_ALL);

            return (List<Method>) query.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getAllMethod() query from Method: " + e.getMessage(), e);
        }
    }
}
