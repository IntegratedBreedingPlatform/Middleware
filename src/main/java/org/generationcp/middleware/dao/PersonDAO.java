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

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Person;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link Person}.
 *
 */
public class PersonDAO extends GenericDAO<Person, Integer> {

	public boolean isPersonExists(final String firstName, final String lastName) throws MiddlewareQueryException {
		try {
			final StringBuilder sql = new StringBuilder();
			sql.append("SELECT COUNT(1) FROM persons p ").append("WHERE UPPER(p.fname) = :firstName ")
					.append("AND UPPER(p.lname) = :lastName");

			final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			query.setParameter("firstName", firstName);
			query.setParameter("lastName", lastName);

			final BigInteger count = (BigInteger) query.uniqueResult();

			return count.longValue() > 0;
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with isPersonExists(firstName=" + firstName + ", lastName=" + lastName
					+ ") query from Person: " + e.getMessage(), e);
		}
		return false;
	}

	public boolean isPersonWithEmailExists(final String email) throws MiddlewareQueryException {
		try {
			final StringBuilder sql = new StringBuilder();
			sql.append("SELECT COUNT(1) FROM persons p ").append("WHERE UPPER(p.pemail) = :email");
			final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			query.setParameter("email", email);

			final BigInteger count = (BigInteger) query.uniqueResult();

			return count.longValue() > 0;

		} catch (final HibernateException e) {
			this.logAndThrowException("Error with isPersonWithEmailExists(email=" + email + ") query from Person: " + e.getMessage(), e);
		}

		return false;
	}

	public Person getPersonByEmail(final String email) throws MiddlewareQueryException {
		try {
			final Criteria criteria = this.getSession().createCriteria(Person.class);

			criteria.add(Restrictions.eq("email", email));

			return (Person) criteria.uniqueResult();
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getPersonByEmail(email=" + email + ") query from Person: " + e.getMessage(), e);
		}

		return null;
	}

	public Person getPersonByEmailAndName(final String email, final String firstName, final String lastName)
			throws MiddlewareQueryException {
		try {
			final Criteria criteria = this.getSession().createCriteria(Person.class);

			criteria.add(Restrictions.eq("email", email)).add(Restrictions.eq("firstName", firstName))
					.add(Restrictions.eq("lastName", lastName));

			final Object result = criteria.uniqueResult();
			if (result != null) {
				return (Person) result;
			}
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getPersonByEmail(email=" + email + ") query from Person: " + e.getMessage(), e);
		}

		return null;
	}

	public boolean isPersonWithUsernameAndEmailExists(final String username, final String email) throws MiddlewareQueryException {
		try {
			final StringBuilder sql = new StringBuilder();
			sql.append("SELECT COUNT(1) FROM USERS users join PERSONS persons on users.personid = persons.personid ")
					.append("WHERE users.uname = :username and persons.pemail = :email");
			final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			query.setParameter("email", email);
			query.setParameter("username", username);

			return ((BigInteger) query.uniqueResult()).longValue() > 0;

		} catch (final HibernateException e) {
			this.logAndThrowException("Error with isPersonWithEmailExists(email=" + email + ") query from Person: " + e.getMessage(), e);
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, String> getPersonNamesByPersonIds(final List<Integer> personIds) throws MiddlewareQueryException {
		final Map<Integer, String> map = new HashMap<Integer, String>();
		try {
			final List<Person> persons = this.getSession().createCriteria(Person.class).add(Restrictions.in("id", personIds)).list();
			if (persons != null && !persons.isEmpty()) {
				for (final Person person : persons) {
					map.put(person.getId(), person.getDisplayName());
				}
			}

		} catch (final HibernateException e) {
			this.logAndThrowException(String.format("Error with getPersonNamesByPersonIds(id=[%s])", StringUtils.join(personIds, ",")), e);
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, String> getPersonNamesByUserIds(final List<Integer> userIds) throws MiddlewareQueryException {
		final Map<Integer, String> map = new HashMap<Integer, String>();
		try {
			final StringBuffer sqlString = new StringBuffer()
					.append("SELECT DISTINCT users.userid, persons.fname, persons.ioname, persons.lname ")
					.append("FROM persons JOIN users ON persons.personid = users.personid ").append("WHERE users.userid = :userIds ");

			final SQLQuery query = this.getSession().createSQLQuery(sqlString.toString());
			query.setParameterList("userIds", userIds);

			final List<Object[]> results = query.list();

			for (final Object[] row : results) {
				final Integer userId = (Integer) row[0];
				final String firstName = (String) row[1];
				final String middleName = (String) row[2];
				final String lastName = (String) row[3];

				map.put(userId, new Person(firstName, middleName, lastName).getDisplayName());
			}

		} catch (final HibernateException e) {
			this.logAndThrowException(String.format("Error with getPersonNamesByUserIds(id=[%s])", StringUtils.join(userIds, ",")), e);
		}
		return map;
	}

	public Person getPersonByName(final String firstName, final String middleName, final String lastName) {
		Person person = null;
		try {
			final Criteria criteria = this.getSession().createCriteria(Person.class);
			criteria.add(Restrictions.eq("firstName", firstName));
			criteria.add(Restrictions.eq("lastName", lastName));
			criteria.add(Restrictions.eq("middleName", middleName));

			person = (Person) criteria.uniqueResult();

		} catch (final HibernateException e) {
			this.logAndThrowException(
					String.format("Error with getPersonByName(firstName=[%s],middleName=[%s],lastName)", firstName, middleName, lastName),
					e);
		}
		return person;
	}

	public Person getPersonByFullName(final String fullname) {
		Person person = null;
		try {
			final Query query = this.getSession().getNamedQuery(Person.GET_BY_FULLNAME);
			query.setParameter("fullname", fullname);
			person = (Person) query.uniqueResult();
		} catch (final HibernateException e) {
			this.logAndThrowException(String.format("Error with getPersonByFullName(fullname=[%s])", StringUtils.join(fullname, ",")), e);
		}
		return person;
	}
}
