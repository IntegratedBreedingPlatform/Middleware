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

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.SecurityQuestion;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link SecurityQuestion}.
 *
 * @author Mark Agarrado
 *
 */
public class SecurityQuestionDAO extends GenericDAO<SecurityQuestion, Integer> {

	@SuppressWarnings("unchecked")
	public List<SecurityQuestion> getByUserId(Integer userId) throws MiddlewareQueryException {
		try {
			if (userId != null) {
				Criteria criteria = this.getSession().createCriteria(SecurityQuestion.class);
				criteria.add(Restrictions.eq("userId", userId));
				return criteria.list();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByUserId(userId=" + userId + ") query from SecurityQuestion: " + e.getMessage(), e);
		}
		return new ArrayList<SecurityQuestion>();
	}

}
