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

import org.generationcp.middleware.pojos.User;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.QueryException;
import org.hibernate.criterion.Restrictions;

public class UserDAO extends GenericDAO<User, Integer>{

    public User findByUsernameAndPassword(String username, String password) {
        try {
            Criteria criteria = getSession().createCriteria(User.class)
                                            .add(Restrictions.eq("name", username))
                                            .add(Restrictions.eq("password", password));
            
            @SuppressWarnings("unchecked")
            List<User> users = criteria.list();
            
            return users.size() > 0 ? users.get(0) : null;
        } catch (HibernateException ex) {
            throw new QueryException(ex);
        }
    }
    
    public boolean isUsernameExists(String userName) {
        Criteria criteria = getSession().createCriteria(User.class);
        criteria.add(Restrictions.eq("name", userName));
        
        //used a List in case of dirty data
        @SuppressWarnings("unchecked")
        List<User> users = criteria.list();
        
        return !users.isEmpty();
    }
	
}
