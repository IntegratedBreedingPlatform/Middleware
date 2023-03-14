
package org.generationcp.middleware.dao.mbdt;

import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.mbdt.MBDTGeneration;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte
 */
@Transactional
public class MBDTGenerationDAO extends GenericDAO<MBDTGeneration, Integer> {

	public MBDTGenerationDAO(final Session session) {
		super(session);
	}

	public MBDTGeneration getByProjectAndDatasetID(Integer datasetID, Integer projectID) throws MiddlewareQueryException {

		MBDTGeneration generation = null;

		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("genotypeDatasetID", datasetID));
			criteria.add(Restrictions.eq("project.projectID", projectID));

			generation = (MBDTGeneration) criteria.uniqueResult();

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getByDatasetID=" + datasetID + " query on MBDTGenerationDAO: " + e.getMessage(), e);
		}

		return generation;
	}

	public MBDTGeneration getByNameAndProjectID(String name, Integer projectID) throws MiddlewareQueryException {

		try {
			MBDTGeneration generation = null;

			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("generationName", name)).add(Restrictions.eq("project.projectID", projectID));

			Object obj = criteria.uniqueResult();

			if (obj == null) {
				return null;
			} else {
				generation = (MBDTGeneration) obj;
			}

			return generation;
		} catch (HibernateException e) {
			this.logAndThrowException("Error at getByNameAndProjectID query on MBDTGenerationDAO: " + e.getMessage(), e);
			return null;
		}
	}

	public List<MBDTGeneration> getByProjectID(Integer projectID) throws MiddlewareQueryException {
		Criteria crit = this.getSession().createCriteria(this.getPersistentClass());

		crit.add(Restrictions.eq("project.projectID", projectID));

		return crit.list();
	}

	@Override
	public MBDTGeneration saveOrUpdate(MBDTGeneration entity) throws MiddlewareQueryException {
		try {
			MBDTGeneration returnVal = super.saveOrUpdate(entity);
			return returnVal;
		} catch (MiddlewareQueryException e) {

			throw e;
		} catch (HibernateException e) {

			throw e;
		}
	}
}
