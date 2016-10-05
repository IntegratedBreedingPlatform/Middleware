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
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link CropType}.
 *
 * @author Joyce Avestro
 *
 */
public class CropTypeDAO extends GenericDAO<CropType, Long> {

	public CropType getByName(String cropName) throws MiddlewareQueryException {
		CropType toReturn = null;
		try {
			if (cropName != null) {
				Criteria criteria = this.getSession().createCriteria(CropType.class);
				criteria.add(Restrictions.eq("cropName", cropName));
				toReturn = (CropType) criteria.uniqueResult();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByName(cropName=" + cropName + ") query from CropType: " + e.getMessage(), e);
		}
		return toReturn;
	}

}
