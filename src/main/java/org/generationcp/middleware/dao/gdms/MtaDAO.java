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

package org.generationcp.middleware.dao.gdms;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.gdms.Mta;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link Mta}.
 *
 * @author Joyce Avestro
 */

public class MtaDAO extends GenericDAO<Mta, Integer> {

	public static final String GET_MTAS_BY_TRAIT = "SELECT mta_id " + "     ,marker_id " + "     ,dataset_id " + "     ,map_id "
			+ "     ,linkage_group " + "     ,position " + "     ,tid " + "     ,effect " + "     ,CONCAT(hv_allele, '') "
			+ "     ,CONCAT(experiment, '') " + "     ,score_value " + "     ,r_square " + "FROM gdms_mta " + "WHERE tid = :traitId ";

	public MtaDAO(final Session session) {
		super(session);
	}

	@SuppressWarnings("unchecked")
	public List<Mta> getMtasByTrait(Integer traitId) throws MiddlewareQueryException {
		List<Mta> toReturn = new ArrayList<Mta>();

		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("tId", traitId));
			toReturn = criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getMtasByTrait(traitId=" + traitId + ") query from gdms_mta: " + e.getMessage(), e);
		}
		return toReturn;
	}

	public void deleteByDatasetId(int datasetId) throws MiddlewareQueryException {
		try {
			SQLQuery statement =
					this.getSession().createSQLQuery(
							"DELETE FROM gdms_mta_metadata WHERE mta_id in (select mta_id from gdms_mta where dataset_id = " + datasetId
									+ ")");
			statement.executeUpdate();

			statement = this.getSession().createSQLQuery("DELETE FROM gdms_mta where dataset_id = " + datasetId);
			statement.executeUpdate();

		} catch (HibernateException e) {
			this.logAndThrowException("Error in deleteByDatasetId=" + datasetId + " in MtaDAO: " + e.getMessage(), e);
		}
	}

}
