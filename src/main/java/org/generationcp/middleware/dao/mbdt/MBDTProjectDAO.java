package org.generationcp.middleware.dao.mbdt;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.mbdt.MBDTProjectData;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 * Date: 5/20/2014
 * Time: 4:12 AM
 */
public class MBDTProjectDAO extends GenericDAO<MBDTProjectData, Integer> {

    @Override
    public MBDTProjectData getById(Integer integer) throws MiddlewareQueryException {
        return super.getById(integer);
    }

    @Override
    public MBDTProjectData save(MBDTProjectData entity) throws MiddlewareQueryException {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        try {

            MBDTProjectData returnVal = super.save(entity);
            transaction.commit();
            session.flush();
            session.clear();
            return returnVal;
        } catch (HibernateException e) {
            transaction.rollback();
            throw e;
        } catch (MiddlewareQueryException e) {
            transaction.rollback();
            throw e;
        }
    }


    public Integer getNegativeId() throws MiddlewareQueryException {
        return super.getNegativeId("projectID");
    }
}
