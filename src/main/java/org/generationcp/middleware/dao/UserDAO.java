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
import org.generationcp.middleware.exceptions.QueryException;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

public class UserDAO extends GenericDAO<User, Integer>{

    public User findByUsernameAndPassword(String username, String password) throws QueryException{
        try {
            Criteria criteria = getSession().createCriteria(User.class)
                                            .add(Restrictions.eq("name", username))
                                            .add(Restrictions.eq("password", password));
            
            @SuppressWarnings("unchecked")
            List<User> users = criteria.list();
            
            return users.size() > 0 ? users.get(0) : null;
        } catch (HibernateException e) {
            throw new QueryException("Error with finding user by user name and password: " + e.getMessage(), e);
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
	
    public User findByNameUsingEqual(String name, int start, int numOfRows) throws QueryException {
        try {
            Query query = getSession().getNamedQuery(User.FIND_BY_NAME_USING_EQUAL);
            query.setParameter("name", name);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);

            return (User) query.list().get(0);
        } catch (HibernateException ex) {
            throw new QueryException("Error with find by name query using equal for User: " + ex.getMessage(), ex);
        }
    }

    @SuppressWarnings("unchecked")
    public List<User> findByNameUsingLike(String name, int start, int numOfRows) throws QueryException {
        try {
            Query query = getSession().getNamedQuery(User.FIND_BY_NAME_USING_LIKE);
            query.setParameter("name", name);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);

            return (List<User>) query.list();
        } catch (HibernateException ex) {
            throw new QueryException("Error with find by  name query using like for User: " + ex.getMessage(), ex);
        }
    }
    
}
