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
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

public class UserDAO extends GenericDAO<User, Integer>{

    public User getByUsernameAndPassword(String username, String password) throws MiddlewareQueryException{
        try {
            Criteria criteria = getSession().createCriteria(User.class)
                                            .add(Restrictions.eq("name", username))
                                            .add(Restrictions.eq("password", password));
            
            @SuppressWarnings("unchecked")
            List<User> users = criteria.list();
            
            return users.size() > 0 ? users.get(0) : null;
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getByUsernameAndPassword(username="+username+") query from User: " + e.getMessage(), e);
        }
    }
    
    public boolean isUsernameExists(String userName) throws MiddlewareQueryException {
        try{
        Criteria criteria = getSession().createCriteria(User.class);
        criteria.add(Restrictions.eq("name", userName));
        
        //used a List in case of dirty data
        @SuppressWarnings("unchecked")
        List<User> users = criteria.list();
        
        return !users.isEmpty();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with isUsernameExists(username="+userName+") query from User: " + e.getMessage(), e);
        }
    }
	
    public User getByNameUsingEqual(String name, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(User.GET_BY_NAME_USING_EQUAL);
            query.setParameter("name", name);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);

            return (User) query.list().get(0);
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getByNameUsingEqual(name="+name+") query from User: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<User> getByNameUsingLike(String name, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(User.GET_BY_NAME_USING_LIKE);
            query.setParameter("name", name);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);

            return (List<User>) query.list();
        } catch (HibernateException e) {
            throw new MiddlewareQueryException("Error with getByNameUsingLike(name="+name+") query from User: " + e.getMessage(), e);
        }
    }
    
}
