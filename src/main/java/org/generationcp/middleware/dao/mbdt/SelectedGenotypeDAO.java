
package org.generationcp.middleware.dao.mbdt;

import java.util.List;
import java.util.Set;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.mbdt.SelectedGenotypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.mbdt.SelectedGenotype;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte
 */

public class SelectedGenotypeDAO extends GenericDAO<SelectedGenotype, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(SelectedMarkerDAO.class);

	public SelectedGenotypeDAO(final Session session) {
		super(session);
	}

	public List<SelectedGenotype> getParentData(Integer generationID) throws MiddlewareQueryException {
		Criteria crit = this.getSession().createCriteria(this.getPersistentClass());
		crit.add(Restrictions.eq("generation.generationID", generationID));

		return crit.list();
	}

	public List<SelectedGenotype> getSelectedAccessions(Integer generationID) throws MiddlewareQueryException {
		Criteria crit = this.getSession().createCriteria(this.getPersistentClass());

		crit.add(Restrictions.eq("generation.generationID", generationID)).add(
				Restrictions.disjunction().add(Restrictions.eq("type", SelectedGenotypeEnum.SR))
						.add(Restrictions.eq("type", SelectedGenotypeEnum.SD)));

		return crit.list();
	}

	public List<SelectedGenotype> getSelectedGenotypeByIds(Set<Integer> gids) throws MiddlewareQueryException {
		Integer[] idArray = new Integer[gids.size()];
		idArray = gids.toArray(idArray);
		Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
		criteria.add(Restrictions.in("gid", idArray));

		return criteria.list();
	}

	// moved transaction to the manager level so as to support batch operations
	@Override
	public SelectedGenotype saveOrUpdate(SelectedGenotype entity) throws MiddlewareQueryException {
		try {
			SelectedGenotype genotype = super.saveOrUpdate(entity);
			return genotype;
		} catch (MiddlewareQueryException e) {
			SelectedGenotypeDAO.LOG.error("Saving or updating was not successful", e);
			throw e;
		}
	}
}
