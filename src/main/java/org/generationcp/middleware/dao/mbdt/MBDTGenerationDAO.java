package org.generationcp.middleware.dao.mbdt;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.mbdt.MBDTGeneration;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 */


public class MBDTGenerationDAO extends GenericDAO<MBDTGeneration, Integer> {
    public MBDTGeneration getByProjectAndDatasetID(Integer datasetID, Integer projectID) throws MiddlewareQueryException {

        MBDTGeneration generation = null;

        try {
            Criteria criteria = getSession().createCriteria(getPersistentClass());
            criteria.add(Restrictions.eq("genotypeDatasetID", datasetID));
            criteria.add(Restrictions.eq("project.projectID", projectID));

            generation = (MBDTGeneration) criteria.uniqueResult();

        } catch (HibernateException e) {
            logAndThrowException("Error at getByDatasetID=" + datasetID + " query on MBDTGenerationDAO: " + e.getMessage(), e);
        }

        return generation;
    }

    public List<MBDTGeneration> getByProjectID(Integer projectID) throws MiddlewareQueryException {
        Criteria crit = getSession().createCriteria(getPersistentClass());

        crit.add(Restrictions.eq("project.projectID", projectID));

        return crit.list();
    }

    @Override
    public MBDTGeneration saveOrUpdate(MBDTGeneration entity) throws MiddlewareQueryException {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();

        try {
            MBDTGeneration returnVal = super.saveOrUpdate(entity);
            transaction.commit();
            session.flush();
            session.clear();

            return returnVal;
        } catch (MiddlewareQueryException e) {
            transaction.rollback();
            throw e;
        } catch (HibernateException e) {
            transaction.rollback();
            throw e;
        }
    }
}