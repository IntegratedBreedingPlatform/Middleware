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
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for {@link CVTermProperty}.
 */
public class CvTermPropertyDao extends GenericDAO<CVTermProperty, Integer> {

	@SuppressWarnings("unchecked")
	public List<CVTermProperty> getByCvTermId(int cvTermId) throws MiddlewareQueryException {
		List<CVTermProperty> properties = new ArrayList<>();
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("cvTermId", cvTermId));
			properties = criteria.list();

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getByCvTermId=" + cvTermId + " query on CVTermPropertyDao: " + e.getMessage(), e);
		}
		return properties;
	}

	public List getByCvTermIds(List<Integer> cvTermIds) throws MiddlewareQueryException {
		if (cvTermIds.isEmpty()) {
			return new ArrayList();
		}
		try {
			Criteria criteria =
				this.getSession().createCriteria(this.getPersistentClass()).add(GenericDAO.buildInCriterion("cvTermId", cvTermIds));
			return criteria.list();
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error at getByCvTermIds query on CVTermDao", e);
		}
	}

	public List getByCvId(Integer cvId) throws MiddlewareQueryException {
		try {

			Query query =
				this.getSession()
					.createSQLQuery(
						"select p.* from cvtermprop p inner join cvterm t on p.cvterm_id = t.cvterm_id where t.is_obsolete =0 and t.cv_id = "
							+ cvId).addEntity(CVTermProperty.class);

			return query.list();

		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error at getByCvId", e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<CVTermProperty> getByCvTermAndType(int cvTermId, int typeId) throws MiddlewareQueryException {
		List<CVTermProperty> properties = new ArrayList<>();
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("cvTermId", cvTermId));
			criteria.add(Restrictions.eq("typeId", typeId));
			properties = criteria.list();

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getByCvTermId=" + cvTermId + " query on CVTermPropertyDao: " + e.getMessage(), e);
		}
		return properties;
	}

	public List<CVTermProperty> getByCvTermIdsAndType(final List<Integer> cvTermIds, final int typeId) throws MiddlewareQueryException {
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.in("cvTermId", cvTermIds));
			criteria.add(Restrictions.eq("typeId", typeId));
			return criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				"Error at getByCvTermIdsAndType=" + cvTermIds + " query on CVTermPropertyDao: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public CVTermProperty getOneByCvTermAndType(int cvTermId, int typeId) throws MiddlewareQueryException {
		CVTermProperty property = null;
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("cvTermId", cvTermId));
			criteria.add(Restrictions.eq("typeId", typeId));
			List<CVTermProperty> properties = criteria.list();
			if (properties != null && !properties.isEmpty()) {
				property = properties.get(0);
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getByCvTermId=" + cvTermId + " query on CVTermPropertyDao: " + e.getMessage(), e);
		}
		return property;
	}

	public boolean isTermHasProperties(int termId) throws MiddlewareQueryException {
		try {

			SQLQuery query = this.getSession().createSQLQuery("SELECT value FROM cvtermprop where cvterm_id = :termId limit 1;");
			query.setParameter("termId", termId);
			List list = query.list();
			return list.size() > 0;
		} catch (HibernateException e) {
			this.logAndThrowException("Error in getAllInventoryScales in CVTermDao: " + e.getMessage(), e);
		}
		return false;
	}

	public CVTermProperty save(Integer cvTermId, Integer typeId, String value, Integer rank) throws MiddlewareQueryException {
		CVTermProperty property = this.getOneByCvTermAndType(cvTermId, typeId);

		if (property == null) {
			return this.save(new CVTermProperty(null, cvTermId, typeId, value, rank));
		}

		property.setValue(value);
		property.setRank(rank);
		return this.merge(property);
	}
}
