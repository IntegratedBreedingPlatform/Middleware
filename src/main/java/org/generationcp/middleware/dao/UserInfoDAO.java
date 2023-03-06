/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
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
public class UserInfoDAO extends GenericDAO<UserInfo, Integer> {

	public UserInfoDAO(final Session session) {
		super(session);
	}

	public UserInfo getUserInfoByToken(String token) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(UserInfo.class).add(Restrictions.eq("resetToken", token));

			@SuppressWarnings("unchecked")
			List<UserInfo> userInfoList = criteria.list();
			return !userInfoList.isEmpty() ? userInfoList.get(0) : null;

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getUserInfoByToken(token=" + token + ") query from User: " + e.getMessage(), e);
		}

		return null;
	}

	public boolean insertOrUpdateUserInfo(UserInfo userInfo) {
		
		// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
		// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
		// statement
		this.getSession().flush();
		if (userInfo != null) {
			String queryString = "REPLACE INTO workbench_user_info (user_id, login_count) VALUES (:userId, :loginCount)";
			Session s = this.getSession();
			Query q = s.createSQLQuery(queryString);
			q.setInteger("userId", userInfo.getUserId());
			q.setInteger("loginCount", userInfo.getLoginCount());
			return q.executeUpdate() > 0;
		}
		return false;
	}

	public UserInfo getUserInfoByUserId(int userId) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(UserInfo.class).add(Restrictions.eq("userId", userId));

			@SuppressWarnings("unchecked")
			List<UserInfo> userInfoList = criteria.list();
			return !userInfoList.isEmpty() ? userInfoList.get(0) : null;
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getUserInfoByUserId(userId=" + userId + ") query from User: " + e.getMessage(), e);
		}

		return null;
	}

}
