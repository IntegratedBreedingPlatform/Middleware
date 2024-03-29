package org.generationcp.middleware.dao.germplasmlist;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListDataView;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

/**
 * DAO class for {@link GermplasmListDataView}.
 */
public class GermplasmListDataViewDAO extends GenericDAO<GermplasmListDataView, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(GermplasmListDataViewDAO.class);

	private static final String COPY_LIST_DATA_VIEW = "INSERT INTO list_data_view (listid, static_id, name_fldno, cvterm_id, type_id) "
		+ "      SELECT :destListid, static_id, name_fldno, cvterm_id, type_id "
		+ "      FROM list_data_view "
		+ "      WHERE listid = :srcListid ";

	public GermplasmListDataViewDAO(final Session session) {
		super(session);
	}

	public List<GermplasmListDataView> getByListId(final Integer listId) {
		final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
		criteria.add(Restrictions.eq("list.id", listId));
		return criteria.list();
	}

	// TODO: Added to avoid breaking the germplasm list query doing too many joins IBP-5590.
	//  The query will be checked in IBP-5636
	public long countEntryDetailsNamesAndAttributesAdded(final Integer listId) {
		final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
		criteria.add(Restrictions.eq("list.id", listId));
		criteria.add(Restrictions.isNull("staticId"));
		criteria.setProjection(Projections.rowCount());
		return (long) criteria.uniqueResult();
	}

	public long countListByVariableId(final Integer variableId) {
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.createAlias("list", "l");
			criteria.add(Restrictions.eq("cvtermId", variableId));
			criteria.add(Restrictions.not(Restrictions.eq("l.status", GermplasmList.Status.DELETED.getCode())));
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

	public void copyEntries (final Integer sourceListId, final Integer destListId) {
		try {
			final SQLQuery sqlQuery = this.getSession().createSQLQuery(COPY_LIST_DATA_VIEW);
			sqlQuery.setParameter("srcListid", sourceListId);
			sqlQuery.setParameter("destListid", destListId);
			sqlQuery.executeUpdate();
		} catch (final Exception e) {
			final String message = "Error with copyEntries(sourceListId=" + sourceListId + " ): " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message);
		}
	}

	public long countGermplasmListWithNameType(final Integer nameTypeId) {
		try {
			final String sql = "SELECT count(1) FROM list_data_view WHERE name_fldno = :nameType";
			final SQLQuery query = this.getSession().createSQLQuery(sql);
			query.setParameter("nameType", nameTypeId);
			return ((BigInteger) query.uniqueResult()).longValue();
		} catch (final HibernateException e) {
			final String message = "Error with countGermplasmListWithNameType(nameTypeId=" + nameTypeId + "): " + e.getMessage();
			LOG.error(message);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public void deleteByNameType(final Integer nameTypeId) {
		try {
			final String sql = "DELETE FROM list_data_view WHERE name_fldno = :nameTypeId";
			final Query query =
				this.getSession().createSQLQuery(sql);
			query.setParameter("nameTypeId", nameTypeId);
			query.executeUpdate();
		} catch (final HibernateException e) {
			final String message = "Error with deleteByNameType(nameTypeId=" + nameTypeId + "): " + e.getMessage();
			LOG.error(message);
			throw new MiddlewareQueryException(message, e);
		}

	}
}
