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

import org.generationcp.middleware.pojos.Person;
import org.hibernate.SQLQuery;

public class PersonDAO extends GenericDAO<Person, Integer>{

    public boolean isPersonExists(String firstName, String lastName) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(1) FROM PERSONS p ")
           .append("WHERE UPPER(p.fname) = :firstName ")
           .append("AND UPPER(p.lname) = :lastName");
        
        SQLQuery query = getSession().createSQLQuery(sql.toString());
        query.setParameter("firstName", firstName);
        query.setParameter("lastName", lastName);

        BigInteger count =  (BigInteger) query.uniqueResult();
        
        return count.longValue() > 0;
    }
}
