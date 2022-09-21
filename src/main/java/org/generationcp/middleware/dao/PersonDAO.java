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

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.workbench.CropPerson;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO class for {@link Person}.
 */
public class PersonDAO extends GenericDAO<Person, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(PersonDAO.class);

	public boolean isPersonWithEmailExists(final String email) throws MiddlewareQueryException {
		try {
			final String sql = "SELECT COUNT(1) FROM persons p WHERE UPPER(p.pemail) = :email";
			final SQLQuery query = this.getSession().createSQLQuery(sql);
			query.setParameter("email", email);

			final BigInteger count = (BigInteger) query.uniqueResult();

			return count.longValue() > 0;

		} catch (final HibernateException e) {
			final String message =
				"Error with isPersonWithEmailExists(email=" + email + ") query from Person: " + e.getMessage();
			PersonDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public boolean isPersonWithUsernameAndEmailExists(final String username, final String email) throws MiddlewareQueryException {
		try {
			final String sql = "SELECT COUNT(1) FROM USERS users join PERSONS persons on users.personid = persons.personid "
				+ "WHERE users.uname = :username and persons.pemail = :email";
			final SQLQuery query = this.getSession().createSQLQuery(sql);
			query.setParameter("email", email);
			query.setParameter("username", username);

			return ((BigInteger) query.uniqueResult()).longValue() > 0;

		} catch (final HibernateException e) {
			final String message =
				"Error with isPersonWithEmailExists(email=" + email + ") query from Person: " + e.getMessage();
			PersonDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, String> getPersonNamesByPersonIds(final List<Integer> personIds) throws MiddlewareQueryException {
		final Map<Integer, String> map = new HashMap<>();
		try {
			final List<Person> persons = this.getSession().createCriteria(Person.class).add(Restrictions.in("id", personIds)).list();
			if (persons != null && !persons.isEmpty()) {
				for (final Person person : persons) {
					map.put(person.getId(), person.getDisplayName());
				}
			}
			return map;
		} catch (final HibernateException e) {
			final String message =
				String.format("Error with getPersonNamesByPersonIds(id=[%s])", StringUtils.join(personIds, ","));
			PersonDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public List<Person> getPersonsByCrop(final CropType cropType) {
		try {
			final Criteria criteria = this.getSession().createCriteria(CropPerson.class);
			criteria.add(Restrictions.eq("cropType.cropName", cropType.getCropName()));
			criteria.setProjection(Projections.property("person"));
			return criteria.list();
		} catch (final HibernateException e) {
			final String message = "Error with getPersonsByCrop(cropType=" + cropType + "";
			LOG.error(message, e);
			throw new MiddlewareQueryException(
				message,
				e);
		}
	}
}
