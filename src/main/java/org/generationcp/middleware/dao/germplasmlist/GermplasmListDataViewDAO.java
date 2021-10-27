package org.generationcp.middleware.dao.germplasmlist;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.GermplasmListDataView;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * DAO class for {@link GermplasmListDataView}.
 */
public class GermplasmListDataViewDAO extends GenericDAO<GermplasmListDataView, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(GermplasmListDataViewDAO.class);

	public GermplasmListDataViewDAO(final Session session) {
		super(session);
	}

	public List<GermplasmListDataView> getByListId(final Integer listId) {
		final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
		criteria.add(Restrictions.eq("list.id", listId));
		return criteria.list();
	}

	public long countListByVariableId(final Integer variableId) {
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.createAlias("list", "l");
			criteria.add(Restrictions.eq("cvtermId", variableId));
			criteria.setProjection(Projections.countDistinct("l.id"));
			return ((Long) criteria.uniqueResult()).longValue();
		} catch (final HibernateException e) {
			final String errorMessage =
				"Error with countListByVariableId(variableId=" + variableId + ") query from GermplasmListDataViewDAO " + e.getMessage();
			GermplasmListDataViewDAO.LOG.error(errorMessage);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public void deleteByListIdAndVariableIds(final Integer listId, final Set<Integer> variableIds) {
		try {
			final String query =
				"DELETE FROM list_data_view WHERE listid = :listId AND cvterm_id IN :variableIds";
			final SQLQuery sqlQuery = this.getSession().createSQLQuery(query);
			sqlQuery.setParameter("listId", listId);
			sqlQuery.setParameterList("variableIds", variableIds);
			sqlQuery.executeUpdate();
		} catch (final HibernateException e) {
			final String errorMessage =
				"Error with deleteByListIdAndVariableIds(listId=" + listId + ", variableIds=" + variableIds
					+ ") query from GermplasmListDataViewDAO " + e.getMessage();
			GermplasmListDataViewDAO.LOG.error(errorMessage);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

}
