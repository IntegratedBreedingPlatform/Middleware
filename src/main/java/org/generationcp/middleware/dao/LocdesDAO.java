package org.generationcp.middleware.dao;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Locdes;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

public class LocdesDAO extends GenericDAO<Locdes, Integer> {

	@SuppressWarnings("unchecked")
	public List<Locdes> getByLocation(Integer locId) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(Locdes.class);

            criteria.add(Restrictions.eq("locationId", locId));

            return criteria.list();
            
        } catch (HibernateException e) {
            logAndThrowException("Error with getByLocation(locId=" + locId + ") query from Location: "
                    + e.getMessage(), e);
        }
        return new ArrayList<Locdes>();
	}

}
