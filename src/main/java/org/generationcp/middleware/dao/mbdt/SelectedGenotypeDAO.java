package org.generationcp.middleware.dao.mbdt;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.mbdt.SelectedGenotypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.mbdt.SelectedGenotype;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 */

public class SelectedGenotypeDAO extends GenericDAO<SelectedGenotype, Integer> {

    public List<SelectedGenotype> getParentData(Integer generationID) throws MiddlewareQueryException {
        Criteria crit = getSession().createCriteria(getPersistentClass());
        crit.add(Restrictions.eq("generation.generationID", generationID));

        return crit.list();
    }

    public List<SelectedGenotype> getSelectedAccessions(Integer generationID) throws MiddlewareQueryException {
        Criteria crit = getSession().createCriteria(getPersistentClass());

        crit.add(Restrictions.eq("type", SelectedGenotypeEnum.SR))
                .add(Restrictions.eq("type", SelectedGenotypeEnum.SD))
                .add(Restrictions.eq("generation.generationID", generationID));

        return crit.list();
    }


    public List<SelectedGenotype> getSelectedGenotypeByIds(List<Integer> gids) throws MiddlewareQueryException {
        Integer[] idArray = new Integer[gids.size()];
        idArray = gids.toArray(idArray);
        Criteria criteria = getSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.in("gid", idArray));

        return criteria.list();
    }

    @Override
    public SelectedGenotype saveOrUpdate(SelectedGenotype entity) throws MiddlewareQueryException {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        try {
            SelectedGenotype genotype = super.saveOrUpdate(entity);
            transaction.commit();
            session.flush();
            session.clear();

            return genotype;
        } catch (MiddlewareQueryException e) {
            e.printStackTrace();
            transaction.rollback();
            throw e;
        }
    }
}