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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class GermplasmListDataDetailDAO extends GenericDAO<GermplasmListDataDetail, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(GermplasmListDataDetailDAO.class);

	private static final String COPY_LIST_DATA_DETAILS = "INSERT INTO list_data_details (variable_id, lrecid, value, cvalue_Id, created_by, created_date) "
		+ "      SELECT variable_id, destld.lrecid, value, cvalue_Id, :createdBy, now() "
		+ "      FROM listdata ld, list_data_details ldd, listdata destld "
		+ "      WHERE ld.lrecid = ldd.lrecid "
		+ "          AND ld.entryid = destld.entryid "
		+ "          AND ld.listid = :srcListid "
		+ "          AND destld.listid = :destListid ";

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

	public void copyEntries (final Integer sourceListId, final Integer destListId, final Integer loggedInUser) {
		try {
			final SQLQuery sqlQuery = this.getSession().createSQLQuery(COPY_LIST_DATA_DETAILS);
			sqlQuery.setParameter("srcListid", sourceListId);
			sqlQuery.setParameter("destListid", destListId);
			sqlQuery.setParameter("createdBy", loggedInUser);
			sqlQuery.executeUpdate();
		} catch (final Exception e) {
			final String message = "Error with copyEntries(sourceListId=" + sourceListId + " ): " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message);
		}
	}

	/**
	 *
	 * @param listId
	 * @param variableIds
	 * @return a {@link Map} with listDataId as key and {@link Map} as value which contains the variableId as key and the observation value
	 * as value
	 */
	public Map<Integer, Map<Integer, String>> getObservationValuesByListAndVariableIds(final Integer listId, final Set<Integer> variableIds) {
		final SQLQuery query = this.getSession().createSQLQuery("SELECT ldd.lrecid, ldd.variable_id, ldd.value "
			+ "		FROM list_data_details ldd "
			+ " 		INNER JOIN listdata ld ON ld.lrecid = ldd.lrecid "
			+ " WHERE ld.listid = :listId AND variable_id IN (:variableIds)");
		query.setParameter("listId", listId);
		query.setParameterList("variableIds", variableIds);

		final Map<Integer, Map<Integer, String>> results = new HashMap<>();
		final List<Object[]> queryResults = query.list();
		queryResults.forEach(result -> {
			final Integer listDataId = (Integer) result[0];
			results.putIfAbsent(listDataId, new HashMap<>());
			results.get(listDataId).putIfAbsent((Integer) result[1], (String) result[2]);
		});
		return results;
	}

}
