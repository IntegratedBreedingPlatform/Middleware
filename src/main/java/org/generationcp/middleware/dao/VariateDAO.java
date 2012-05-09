package org.generationcp.middleware.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.pojos.Variate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;

public class VariateDAO extends GenericDAO<VariateDAO, Integer>
{
	@SuppressWarnings("unchecked")
	public List<Variate> getByStudyID(Integer studyId) throws QueryException {
		try {
			Query query = getSession().getNamedQuery(Variate.GET_VARIATES_BY_STUDYID);
			query.setParameter("studyId", studyId);
			
			List<Variate> results = query.list();
			return results;
		} catch(HibernateException ex) {
			throw new QueryException("Error with get Variates by Study ID query: " + ex.getMessage());
		}
	}

}
