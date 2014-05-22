package org.generationcp.middleware.dao.mbdt;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.pojos.mbdt.SelectedGenotype;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 */

public class SelectedGenotypeDAO extends GenericDAO<SelectedGenotype, Integer>{

    public List<SelectedGenotype> retrieveAllAccessions(Integer projectID, Integer datasetID) throws Exception {
        Criteria crit = getSession().createCriteria(getPersistentClass());
        crit.add(Restrictions.eq("generation.generationID", datasetID));
        crit.add(Restrictions.eq("generation.project.projectID", projectID));

        return crit.list();
    }

    public List<SelectedGenotype> getParentGenotypes() throws Exception {
        Criteria criteria = getSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.disjunction()
                .add(Restrictions.eq("type", "SR"))
                .add(Restrictions.eq("type", "SD")));

        return criteria.list();
    }

    public List<SelectedGenotype> getAccessionsByIds(List<Integer> gids) throws Exception {
        Integer[] idArray = new Integer[gids.size()];
        idArray = gids.toArray(idArray);
        Criteria criteria = getSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.in("gid", idArray));

        return criteria.list();
    }

}
