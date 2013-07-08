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
package org.generationcp.middleware.dao.oms;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link CVTerm}.
 * 
 */
public class CVTermDao extends GenericDAO<CVTerm, Integer> {

	
	public CVTerm getByCvIdAndDefinition(Integer cvId, String definition) throws MiddlewareQueryException {
		CVTerm term = null;
		
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.add(Restrictions.eq("cvId", cvId));
			criteria.add(Restrictions.eq("definition", definition));
			criteria.add(Restrictions.eq("isObsolete", 0));
			
			term = (CVTerm) criteria.uniqueResult();
		
		} catch (HibernateException e) {
			logAndThrowException("Error at getByCvIdAndDefinition=" + cvId + ", " + definition + " query on CVTermDao: " + e.getMessage(), e);
		}
		
		return term;
	}
	
	@SuppressWarnings("unchecked")
	public Set<Integer> findStdVariablesByNameOrSynonym(String nameOrSynonym) throws MiddlewareQueryException {
		Set<Integer> stdVarIds = new HashSet<Integer>();
		try {
			SQLQuery query = getSession().createSQLQuery("select distinct cvterm.cvterm_id " +
	                                                     "from cvterm cvterm, cvtermsynonym syn " +
	                                                     "where cvterm.cv_id = 1040 " +
	                                                     "   and (cvterm.name = '" + nameOrSynonym + "'" +
	                                                     "        or (syn.synonym = '" + nameOrSynonym + "'" +
	                                                     "            and syn.cvterm_id = cvterm.cvterm_id))");
	             
	        List<Object> results = (List<Object>) query.list();
	        for (Object row : results) {
	            stdVarIds.add((Integer) row);
	        }
			
		} catch(HibernateException e) {
			logAndThrowException("Error in findStdVariablesByNameOrSynonym=" + nameOrSynonym + " in CVTermDao: " + e.getMessage(), e);
		}
		return stdVarIds;
	}

	public CVTerm getByNameAndCvId(String name, int cvId) throws MiddlewareQueryException {
        CVTerm term = null;
		
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.add(Restrictions.eq("cvId", cvId));
			criteria.add(Restrictions.eq("name", name));
			
			term = (CVTerm) criteria.uniqueResult();
		
		} catch (HibernateException e) {
			logAndThrowException("Error at getByNameAndCvId=" + name + ", " + cvId + " query on CVTermDao: " + e.getMessage(), e);
		}
		
		return term;
	}
}
