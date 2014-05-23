package org.generationcp.middleware.dao.mbdt;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.mbdt.SelectedMarker;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte

 */
public class SelectedMarkerDAO extends GenericDAO<SelectedMarker, Integer> {
    public List<SelectedMarker> getMarkersByProjectAndDatasetID(Integer projectID, Integer datasetID) throws MiddlewareQueryException {
        Criteria criteria = getSession().createCriteria(getPersistentClass());

        criteria.createAlias("generation", "g")
                .createAlias("g.project", "p")
                .add(Restrictions.eq("g.generationID", datasetID))
                .add(Restrictions.eq("p.projectID", projectID));

        return criteria.list();
    }

    @Override
    public SelectedMarker saveOrUpdate(SelectedMarker entity) throws MiddlewareQueryException {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();

        try {
            SelectedMarker marker = super.saveOrUpdate(entity);
            transaction.commit();
            session.flush();
            session.clear();
            return marker;
        } catch (MiddlewareQueryException e) {
            e.printStackTrace();
            transaction.rollback();
            throw e;
        }
    }
}
