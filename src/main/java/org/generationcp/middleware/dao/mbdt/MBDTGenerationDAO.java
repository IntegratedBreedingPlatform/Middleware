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

            /*String sql = "SELECT generation_id, gname, project_id, genotypedataset_id FROM mbdt_generations WHERE genotypedataset_id = :datasetID AND project_id = :projectID";

            SQLQuery query = getSession().createSQLQuery(sql);
            query.setParameter("datasetID", datasetID);
            query.setParameter("projectID", projectID);

            List<Object[]> result =  query.list();
            if (result != null && result.size() > 0) {
                // there should only be one item retrieved given the parameters
                assert(result.size() == 1);
                Object[] row = result.get(0);

                Integer id = (Integer) row[0];
                String gname = (String) row[1];
                projectID = (Integer) row[2];
                datasetID = (Integer) row[3];
            }*/

        } catch (HibernateException e) {
            logAndThrowException("Error at getByDatasetID=" + datasetID + " query on MBDTGenerationDAO: " + e.getMessage(), e);
        }

        return generation;
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