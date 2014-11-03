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

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.UserInfo;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * DAO class for {@link UserInfo}.
 * 
 */
public class UserInfoDAO extends GenericDAO<UserInfo, Integer>{

    public boolean updateLoginCounter(UserInfo userInfo) {
        int loginCount = 0;
        if (userInfo != null && userInfo.getLoginCount() != null) {
            loginCount = userInfo.getLoginCount().intValue();
        }
        loginCount++;

        String queryString = "UPDATE workbench_user_info SET login_count = :loginCount where user_id = :userId";
        Session s = getSession();
        Query q = s.createSQLQuery(queryString);
        if (userInfo != null){
            q.setInteger("userId", userInfo.getUserId());
        }
        q.setInteger("loginCount", loginCount);
        return q.executeUpdate() > 0;
    }

    public boolean insertOrUpdateUserInfo(UserInfo userInfo) {
    	if (userInfo != null){
	        String queryString = "REPLACE INTO workbench_user_info (user_id, login_count) VALUES (:userId, :loginCount)";
	         Session s = getSession();
	         Query q = s.createSQLQuery(queryString);
	         q.setInteger("userId", userInfo.getUserId());
	         q.setInteger("loginCount", userInfo.getLoginCount());
	         return q.executeUpdate() > 0;
    	}
    	return false;
    }

    public UserInfo getUserInfoByUserId(int userId) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(UserInfo.class).add(Restrictions.eq("userId", userId));

            @SuppressWarnings("unchecked")
            List<UserInfo> userInfoList = criteria.list();
            return userInfoList.size() > 0 ? userInfoList.get(0) : null;
        }
        catch (HibernateException e) {
            logAndThrowException("Error with getUserInfoByUserId(userId=" + userId + ") query from User: " + e.getMessage(), e);
        }
        
        return null;
    }
    
   
    
}
