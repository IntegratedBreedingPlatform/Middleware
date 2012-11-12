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
import org.generationcp.middleware.pojos.UserDefinedField;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class UserDefinedFieldDAO extends GenericDAO<UserDefinedField, Integer>{
	
	@SuppressWarnings("unchecked")
    public List<UserDefinedField> getByFieldTableNameAndType(String tableName,String fieldType) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(UserDefinedField.class);
            criteria.add(Restrictions.eq("ftable", tableName));
            criteria.add(Restrictions.eq("ftype", fieldType));
            criteria.addOrder(Order.asc("fname"));
            return criteria.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getByFieldTableNameAndType(name=" + tableName+" type= "+fieldType+ " ) query from Location: "
                    + e.getMessage(), e);
        }
    }

}
