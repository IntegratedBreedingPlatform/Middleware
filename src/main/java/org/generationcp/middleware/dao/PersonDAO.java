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

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Person;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link Person}.
 * 
 */
public class PersonDAO extends GenericDAO<Person, Integer>{

    public boolean isPersonExists(String firstName, String lastName) throws MiddlewareQueryException {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT COUNT(1) FROM PERSONS p ").append("WHERE UPPER(p.fname) = :firstName ")
                    .append("AND UPPER(p.lname) = :lastName");

            SQLQuery query = getSession().createSQLQuery(sql.toString());
            query.setParameter("firstName", firstName);
            query.setParameter("lastName", lastName);

            BigInteger count = (BigInteger) query.uniqueResult();

            return count.longValue() > 0;
        } catch (HibernateException e) {
            logAndThrowException("Error with isPersonExists(firstName=" + firstName + ", lastName=" + lastName
                    + ") query from Person: " + e.getMessage(), e);
        }
        return false;
    }
    
    @SuppressWarnings("unchecked")
	public Map<Integer, String> getPersonNamesByPersonIds(List<Integer> personIds) throws MiddlewareQueryException {
    	Map<Integer, String> map = new HashMap<Integer, String>();
        try {
            List<Person> persons = getSession().createCriteria(Person.class).add(Restrictions.in("id", personIds)).list();
            if (persons != null && !persons.isEmpty()) {
            	for (Person person: persons) {
            		map.put(person.getId(), person.getDisplayName());
            	}
            }
            
        } catch (HibernateException e) {
            logAndThrowException(String.format("Error with getPersonNamesByPersonIds(id=[%s])", StringUtils.join(personIds, ",")),e);
        }
        return map;
    }
}
