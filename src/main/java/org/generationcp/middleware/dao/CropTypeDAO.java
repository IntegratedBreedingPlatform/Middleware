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
import org.generationcp.middleware.pojos.workbench.CropType;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * DAO class for {@link CropType}.
 *
 * @author Joyce Avestro
 */
public class CropTypeDAO extends GenericDAO<CropType, Long> {

	public CropTypeDAO(final Session session) {
		super(session);
	}

	public CropType getByName(final String cropName) {
		CropType toReturn = null;
		try {
			if (cropName != null) {
				final Criteria criteria = this.getSession().createCriteria(CropType.class);
				criteria.add(Restrictions.eq("cropName", cropName));
				toReturn = (CropType) criteria.uniqueResult();
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getByName(cropName=" + cropName + ") query from CropType: " + e.getMessage(), e);
		}
		return toReturn;
	}

	public List<CropType> getAvailableCropsForUser(final int workbenchUserId) {
		try {
			final Query query = this.getSession().createQuery("select distinct cropType from WorkbenchUser as workbenchUser "
				+ " inner join workbenchUser.person as person "
					+ " inner join person.crops as cropType "
				+ " where workbenchUser.userid = :workbenchUserId ");
			query.setParameter("workbenchUserId", workbenchUserId);
			return query.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				"Error with getAvailableCropsForUser(workbenchUserId=" + workbenchUserId + ") query from CropType: " + e.getMessage(), e);
		}
	}

}
