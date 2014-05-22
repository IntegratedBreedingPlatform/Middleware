package org.generationcp.middleware.dao.mbdt;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.mbdt.SelectedGenotypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.mbdt.SelectedGenotype;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 */

public class SelectedGenotypeDAO extends GenericDAO<SelectedGenotype, Integer> {

    public List<SelectedGenotype> retrieveAllAccessions(Integer projectID, Integer datasetID) throws MiddlewareQueryException {
        Criteria crit = getSession().createCriteria(getPersistentClass());
        crit.createAlias("generation", "g")
                .createAlias("g.project", "p")
                .add(Restrictions.eq("g.generationID", datasetID))
                .add(Restrictions.eq("p.projectID", projectID));

        return crit.list();
    }

    public List<SelectedGenotype> getParentGenotypes(Integer projectID, Integer datasetID) throws MiddlewareQueryException {
        Criteria crit = getSession().createCriteria(getPersistentClass());
        crit.createAlias("generation", "g")
                .createAlias("g.project", "p")
                .add(Restrictions.eq("g.generationID", datasetID))
                .add(Restrictions.eq("p.projectID", projectID))
                .add(Restrictions.disjunction()
                        .add(Restrictions.eq("type", SelectedGenotypeEnum.SR))
                        .add(Restrictions.eq("type", SelectedGenotypeEnum.SD)));

        return crit.list();
    }

    public List<SelectedGenotype> getAccessionsByIds(List<Integer> gids) throws MiddlewareQueryException {
        Integer[] idArray = new Integer[gids.size()];
        idArray = gids.toArray(idArray);
        Criteria criteria = getSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.in("gid", idArray));

        return criteria.list();
    }

}
