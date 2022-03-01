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

package org.generationcp.middleware.dao.oms;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for {@link CVTermProperty}.
 */
public class CvTermPropertyDao extends GenericDAO<CVTermProperty, Integer> {

	public static final String CVTERM_ID = "cvTermId";
	public static final String TYPE_ID = "typeId";

	public List<CVTermProperty> getByCvTermId(final int cvTermId) {
		final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
		criteria.add(Restrictions.eq(CVTERM_ID, cvTermId));
		return criteria.list();
	}

	public List<CVTermProperty> getByCvTermIds(final List<Integer> cvTermIds) {
		if (cvTermIds.isEmpty()) {
			return new ArrayList<>();
		}
		final Criteria criteria =
			this.getSession().createCriteria(this.getPersistentClass()).add(GenericDAO.buildInCriterion(CVTERM_ID, cvTermIds));
		return criteria.list();

	}

	public List<CVTermProperty> getByCvId(final Integer cvId) {
		final Query query =
			this.getSession()
				.createSQLQuery(
					"select p.* from cvtermprop p inner join cvterm t on p.cvterm_id = t.cvterm_id where t.is_obsolete =0 and t.cv_id = "
						+ cvId).addEntity(CVTermProperty.class);

		return query.list();
	}

	public List<CVTermProperty> getByCvTermAndType(final int cvTermId, final int typeId) {

		final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
		criteria.add(Restrictions.eq(CVTERM_ID, cvTermId));
		criteria.add(Restrictions.eq(TYPE_ID, typeId));
		return criteria.list();

	}

	public List<CVTermProperty> getByCvTermIdsAndType(final List<Integer> cvTermIds, final int typeId) {
		final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
		criteria.add(Restrictions.in(CVTERM_ID, cvTermIds));
		criteria.add(Restrictions.eq(TYPE_ID, typeId));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public CVTermProperty getOneByCvTermAndType(final int cvTermId, final int typeId) {
		CVTermProperty property = null;
		final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
		criteria.add(Restrictions.eq(CVTERM_ID, cvTermId));
		criteria.add(Restrictions.eq(TYPE_ID, typeId));
		final List<CVTermProperty> properties = criteria.list();
		if (properties != null && !properties.isEmpty()) {
			property = properties.get(0);
		}
		return property;
	}

	public boolean isTermHasProperties(final int termId) {
		final SQLQuery query = this.getSession().createSQLQuery("SELECT value FROM cvtermprop where cvterm_id = :termId limit 1;");
		query.setParameter("termId", termId);
		final List<String> list = query.list();
		return !list.isEmpty();
	}

	public CVTermProperty save(final Integer cvTermId, final Integer typeId, final String value, final Integer rank)
		throws MiddlewareQueryException {
		final CVTermProperty property = this.getOneByCvTermAndType(cvTermId, typeId);

		if (property == null) {
			return this.save(new CVTermProperty(null, cvTermId, typeId, value, rank));
		}

		property.setValue(value);
		property.setRank(rank);
		return this.merge(property);
	}
}
