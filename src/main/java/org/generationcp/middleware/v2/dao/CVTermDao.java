package org.generationcp.middleware.v2.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.v2.pojos.CVTerm;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

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
}
