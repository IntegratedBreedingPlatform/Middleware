package org.generationcp.middleware.dao.germplasmlist;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.pojos.GermplasmListDataView;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * DAO class for {@link GermplasmListDataView}.
 */
public class GermplasmListDataViewDAO extends GenericDAO<GermplasmListDataView, Integer> {

	public GermplasmListDataViewDAO(final Session session) {
		super(session);
	}

	public List<GermplasmListDataView> getByListId(final Integer listId) {
		final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
		criteria.add(Restrictions.eq("list.id", listId));
		return criteria.list();
	}

}
