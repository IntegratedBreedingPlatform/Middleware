package org.generationcp.middleware.v2.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.v2.pojos.CVTermRelationship;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

public class CVTermRelationshipDao extends GenericDAO<CVTermRelationship, Long> {

	@SuppressWarnings("unchecked")
	public List<CVTermRelationship> getBySubjectIds(Collection<Integer> subjectIds) throws MiddlewareQueryException {
		if (subjectIds != null && subjectIds.size() > 0) {
			try {
				Criteria criteria = getSession().createCriteria(getPersistentClass());
				criteria.add(Restrictions.in("subjectId", subjectIds));
	
				List<CVTermRelationship> results = criteria.list();
				if (results != null) {
					return results;
				}
				
			} catch(HibernateException e) {
				logAndThrowException("Error with getByCVTermIds=" + subjectIds + ") query from CVTermRelationship: "
	                    + e.getMessage(), e);
				return null;
			}
		}
		return new ArrayList<CVTermRelationship>();
	}
}
