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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Locdes;
import org.generationcp.middleware.pojos.User;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
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
            logAndThrowException("Error with getByUsernameAndPassword(username="+username+") query from User: " + e.getMessage(), e);
        }
        return null;
    }
    public boolean isPasswordSameAsUserName(String username) throws MiddlewareQueryException{
        try {
            Criteria criteria = getSession().createCriteria(User.class)
                                            .add(Restrictions.eq("name", username))
                                            .add(Restrictions.eq("password", username));
            
            @SuppressWarnings("unchecked")
            List<User> users = criteria.list();
            return users.size() > 0 ? true : false;
        } catch (HibernateException e) {
            logAndThrowException("Error with getByUsernameAndPassword(username="+username+") query from User: " + e.getMessage(), e);
        }
        return false;
    }
    
    public boolean changePassword(String userName, String password) throws MiddlewareQueryException {
        try{
            String queryString = "UPDATES users SET upswd = :password WHERE uname LIKE :username";
            Session s = getSession();
            Query q = s.createSQLQuery(queryString);
            q.setString("username", userName);
            q.setString("password", password);
            int success = q.executeUpdate();

            return success > 0;
        }catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
    
    public User getUserDetailsByUsername(String username) throws MiddlewareQueryException{
        try {
            Criteria criteria = getSession().createCriteria(User.class)
                                            .add(Restrictions.eq("name", username));
            
            @SuppressWarnings("unchecked")
            List<User> users = criteria.list();
            
            return users.size() > 0 ? users.get(0) : null;
        } catch (HibernateException e) {
            logAndThrowException("Error with getByUsernameAndPassword(username="+username+") query from User: " + e.getMessage(), e);
        }
        return null;
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
            logAndThrowException("Error with isUsernameExists(username="+userName+") query from User: " + e.getMessage(), e);
        }
        return false;
    }
	
    @SuppressWarnings("unchecked")
    public List<User> getByNameUsingEqual(String name, int start, int numOfRows) throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(User.GET_BY_NAME_USING_EQUAL);
            query.setParameter("name", name);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);

            return (List<User>) query.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getByNameUsingEqual(name="+name+") query from User: " + e.getMessage(), e);
        }
        return new ArrayList<User>();
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
            logAndThrowException("Error with getByNameUsingLike(name="+name+") query from User: " + e.getMessage(), e);
        }
        return new ArrayList<User>();
    }
    
    @SuppressWarnings("unchecked")
    public List<User> getAllUsersSorted() throws MiddlewareQueryException {
        try {
            Query query = getSession().getNamedQuery(User.GET_ALL_USERS_SORTED);
            return query.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getAllUsersSorted() query from User: "
                    + e.getMessage(), e);
        }
        return new ArrayList<User>();
    }
    
    @SuppressWarnings("unchecked")
	public List<Integer> getUserIdsByCountryIds(Collection<Integer> countryIds) throws MiddlewareQueryException {
    	try {
    		Criteria criteria = getSession().createCriteria(Locdes.class);
    		criteria.createAlias("location", "l");
    		criteria.add(Restrictions.in("l.cntryid", countryIds));
    		criteria.setProjection(Projections.distinct(Projections.property("user.userid")));
    		
    		return criteria.list();
    		
    	} catch (HibernateException e) {
    		logAndThrowException("Error with getUserIdsByCountryIds() query from User: " + e.getMessage(), e);
    	}
    	return new ArrayList<Integer>();
    }
    
}
