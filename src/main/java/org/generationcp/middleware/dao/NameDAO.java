/***************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the 
 * GNU General Public License (http://bit.ly/8Ztv8M) and the 
 * provisions of Part F of the Generation Challenge Programme 
 * Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 **************************************************************/
package org.generationcp.middleware.dao;

import java.util.List;

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.pojos.Name;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

public class NameDAO extends GenericDAO<Name, Integer> {
    @SuppressWarnings("unchecked")
    public List<Name> getByGIDWithFilters(Integer gid, Integer status,
	    GermplasmNameType type) throws QueryException {
	try {
	    StringBuilder queryString = new StringBuilder();
	    queryString.append("SELECT {n.*} from NAMES n WHERE n.gid = :gid ");

	    if (status != null && status != 0)
		queryString.append("AND n.nstat = :nstat ");

	    if (type != null)
		queryString.append("AND n.ntype = :ntype ");

	    SQLQuery query = getSession()
		    .createSQLQuery(queryString.toString());
	    query.addEntity("n", Name.class);
	    query.setParameter("gid", gid);

	    if (status != null && status != 0)
		query.setParameter("nstat", status);

	    if (type != null)
		query.setParameter("ntype",
			new Integer(type.getUserDefinedFieldID()));

	    return query.list();

	    /**
	     * List<Criterion> criterions = new ArrayList<Criterion>();
	     * Criterion gidCriterion = Restrictions.eq("germplasmId", gid);
	     * criterions.add(gidCriterion);
	     * 
	     * if(status != null && status != 0) { Criterion statusCriterion =
	     * Restrictions.eq("nstat", status);
	     * criterions.add(statusCriterion); }
	     * 
	     * if(type != null) { Integer typeid = type.getUserDefinedFieldID();
	     * Criterion typeCriterion = Restrictions.eq("type.fldno", typeid);
	     * criterions.add(typeCriterion); }
	     * 
	     * List<Name> results = findByCriteria(criterions); return results;
	     **/
	} catch (HibernateException ex) {
	    throw new QueryException("Error with get Names by GID query: "
		    + ex.getMessage());
	}
    }

    @SuppressWarnings("unchecked")
    public Name getByGIDAndNval(Integer gid, String nval) throws QueryException {
	Criteria crit = getSession().createCriteria(Name.class);
	crit.add(Restrictions.eq("germplasmId", gid));
	crit.add(Restrictions.eq("nval", nval));
	List<Name> names = crit.list();
	if (names.size() == 0) {
	    // return null if no Name objects match
	    return null;
	} else {
	    // return first result in the case of multiple matches
	    return names.get(0);
	}
    }

    public void validateId(Name name) throws QueryException {
	// Check if not a local record (has negative ID)
	Integer id = name.getNid();
	if (id != null && id.intValue() > 0) {
	    throw new QueryException(
		    "Cannot update a Central Database record. "
			    + "Name object to update must be a Local Record (ID must be negative)");
	}
    }
}
