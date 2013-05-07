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
import org.generationcp.middleware.pojos.UserDetails;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class UserDetailsDAO extends GenericDAO<UserDetails, Integer>{

	public boolean updateLoginCounter(UserDetails usersdetails)
	{
		 int ulogincount = (int)usersdetails.getUlogincnt().intValue();
         ulogincount++;
         String queryString = "update userdetails set ulogincnt = "+ ulogincount + " where userdetailsid = "+ usersdetails.getUserdetailsid();
         System.out.println(queryString);
         Session s = getSession();
         Query q = s.createSQLQuery(queryString);
         if(q.executeUpdate() > 0)
        	 return true;
         else
        	 return false;
	}
	
	public boolean addUserDetails(UserDetails usersdetails)
	{
		 String queryString = "insert into userdetails (ulogincnt,uname) values ("+usersdetails.getUlogincnt()+",'"+usersdetails.getName()+"')";
         Session s = getSession();
         Query q = s.createSQLQuery(queryString);
         if(q.executeUpdate() > 0)
        	 return true;
         else
        	 return false;
	}
    public UserDetails getByUsername(String username) throws MiddlewareQueryException{
        try {
            
        	 Criteria criteria = getSession().createCriteria(UserDetails.class)
                     .add(Restrictions.eq("name", username));
        	 
				@SuppressWarnings("unchecked")
				List<UserDetails> usersdetails = criteria.list();
				System.out.println(username +" : usersdetails size " + usersdetails.size());
				
				return usersdetails.size() > 0 ? usersdetails.get(0) : null;
				
	           
        } catch (HibernateException e) {
            logAndThrowException("Error with getByUsername(username="+username+") query from User: " + e.getMessage(), e);
        }
        return null;
    }
    
   
    
}
