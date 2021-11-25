package org.generationcp.middleware.dao.germplasmlist;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.apache.commons.collections.CollectionUtils;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.GermplasmListDataDetail;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class GermplasmListDataDetailDAO extends GenericDAO<GermplasmListDataDetail, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(GermplasmListDataDetailDAO.class);

	public GermplasmListDataDetailDAO() {
	}

	public GermplasmListDataDetailDAO(final Session session) {
		super(session);
	}

	public void deleteByListIdAndVariableIds(final Integer listId, final Set<Integer> variableIds) {
		try {
			final String query =
				"DELETE ldd FROM list_data_details ldd INNER JOIN listdata ld ON (ld.lrecid = ldd.lrecid) WHERE ld.listid = :listId AND ldd.variable_id IN (:variableIds)";
			final SQLQuery sqlQuery = this.getSession().createSQLQuery(query);
			sqlQuery.setParameter("listId", listId);
			sqlQuery.setParameterList("variableIds", variableIds);
			sqlQuery.executeUpdate();
		} catch (final HibernateException e) {
			final String errorMessage =
				"Error with deleteByListIdAndVariableIds(listId=" + listId + ", variableIds=" + variableIds
					+ ") query from GermplasmListDataDetailDAO " + e.getMessage();
			GermplasmListDataDetailDAO.LOG.error(errorMessage);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public Optional<GermplasmListDataDetail> getByListDataIdAndVariableId(final Integer listDataId, final Integer variableId) {
		try {
			final Criteria criteria = this.getSession().createCriteria(GermplasmListDataDetail.class);
			criteria.add(Restrictions.eq("listData.id", listDataId));
			criteria.add(Restrictions.eq("variableId", variableId));
			return Optional.ofNullable((GermplasmListDataDetail) criteria.uniqueResult());
		} catch (final HibernateException e) {
			final String errorMessage =
				"Error with getByListDataIdAndVariableId(" + listDataId + "," + variableId + ") query from GermplasmListDataDetailDAO: " + e
					.getMessage();
			GermplasmListDataDetailDAO.LOG.error(errorMessage);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public List<GermplasmListDataDetail> getByListId(final Integer listId) {
		try {
			final Criteria criteria = this.getSession().createCriteria(GermplasmListDataDetail.class);
			criteria.createAlias("listData", "listData");
			criteria.createAlias("listData.list", "list");
			criteria.add(Restrictions.eq("list.id", listId));
			return criteria.list();
		} catch (final HibernateException e) {
			final String errorMessage =
				"Error with getByListId(" + listId + ") query from GermplasmListDataDetailDAO: " + e
					.getMessage();
			GermplasmListDataDetailDAO.LOG.error(errorMessage);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public Table<Integer, Integer, GermplasmListDataDetail> getTableEntryIdToVariableId(final Integer listId) {
		try {
			final Criteria criteria = this.getSession().createCriteria(GermplasmListDataDetail.class, "listDataDetail");
			criteria.createAlias("listDataDetail.listData", "listData");
			criteria.createAlias("listData.list", "list");
			criteria.add(Restrictions.eq("list.id", listId));
			final List<GermplasmListDataDetail> list = criteria.list();

			final Table<Integer, Integer, GermplasmListDataDetail> table = HashBasedTable.create();
			for (final GermplasmListDataDetail detail : list) {
				table.put(detail.getListData().getEntryId(), detail.getVariableId(), detail);
			}
			return table;
		} catch (final HibernateException e) {
			final String errorMessage = "Error with getTableListDataIdToVariableId(" + listId + "): " + e.getMessage();
			LOG.error(errorMessage);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public long countObservationsByListAndVariables(final Integer listId, final List<Integer> variableIds) {
		if (variableIds.isEmpty()) {
			return 0l;
		}
		final Criteria criteria = this.getSession().createCriteria(GermplasmListDataDetail.class);
		criteria.setProjection(Projections.rowCount());
		criteria.createAlias("listData", "listData");
		criteria.createAlias("listData.list", "list");
		criteria.add(Restrictions.eq("list.id", listId));
		criteria.add(Restrictions.in("variableId", variableIds));
		return (Long) criteria.uniqueResult();
	}

	public void deleteByListDataIds(final Set<Integer> listDataIds) {
		Preconditions.checkArgument(CollectionUtils.isNotEmpty(listDataIds), "listDataIds passed cannot be empty.");
		final String query =
			"DELETE ldd FROM list_data_details ldd WHERE ldd.lrecid IN (:listDataIds)";
		final SQLQuery sqlQuery = this.getSession().createSQLQuery(query);
		sqlQuery.setParameterList("listDataIds", listDataIds);
		sqlQuery.executeUpdate();
	}
}
