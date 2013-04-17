package org.generationcp.middleware.v2.dao;

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
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.add(Restrictions.in("subjectId", subjectIds));

			return criteria.list();
			
		} catch(HibernateException e) {
			logAndThrowException("Error with getByCVTermIds=" + subjectIds + ") query from CVTermRelationship: "
                    + e.getMessage(), e);
			return null;
		}
	}
}
