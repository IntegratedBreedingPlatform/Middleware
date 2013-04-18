package org.generationcp.middleware.v2.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.v2.pojos.CVTerm;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

public class CVTermDao extends GenericDAO<CVTerm, Integer> {

	
	@SuppressWarnings("unchecked")
	public List<CVTerm> getByIds(Collection<Integer> ids) {
		if (ids != null && ids.size() > 0) {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.add(Restrictions.in("cvTermId", ids));
			
			List<CVTerm> results = criteria.list();
			
			return results != null ? results : new ArrayList<CVTerm>();
		}
		return new ArrayList<CVTerm>();
	}
	
}
