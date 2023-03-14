
package org.generationcp.middleware.dao.mbdt;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.mbdt.MBDTProjectData;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 5/20/2014 Time: 4:12 AM
 */
@Transactional
public class MBDTProjectDAO extends GenericDAO<MBDTProjectData, Integer> {

	public MBDTProjectDAO(final Session session) {
		super(session);
	}

	@Override
	public MBDTProjectData getById(Integer integer) throws MiddlewareQueryException {
		return super.getById(integer);
	}

	@Override
	public MBDTProjectData save(MBDTProjectData entity) throws MiddlewareQueryException {
		try {
			MBDTProjectData returnVal = super.save(entity);
			return returnVal;
		} catch (HibernateException e) {

			throw e;
		} catch (MiddlewareQueryException e) {

			throw e;
		}
	}

	public MBDTProjectData getByName(String name) throws MiddlewareQueryException {
		try {
			MBDTProjectData data = null;

			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("projectName", name));

			Object obj = criteria.uniqueResult();
			if (obj == null) {
				return null;
			} else {
				data = (MBDTProjectData) obj;
			}

			return data;
		} catch (HibernateException e) {
			this.logAndThrowException("Error at getByName=" + name + " query on MBDTProjectDAO: " + e.getMessage(), e);
			return null;
		}
	}
}
