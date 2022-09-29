/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.dao.germplasmlist;

import com.google.common.base.Preconditions;
import org.apache.commons.collections.CollectionUtils;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.germplasm.GermplasmParent;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DAO class for {@link GermplasmListData}.
 */
public class GermplasmListDataDAO extends GenericDAO<GermplasmListData, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(GermplasmListDataDAO.class);

	private static final String GERMPLASM_LIST_DATA_LIST_ID_COLUMN = "listId";

	private static final String GERMPLASM_TABLE = "germplasm";

	private static final String GERMPLASM_TABLE_ALIAS = "g";

	private static final String GERMPLASM_LIST_NAME_TABLE = "list";

	private static final String GERMPLASM_LIST_NAME_TABLE_ALIAS = "l";

	private static final String GERMPLASM_LIST_DATA_ID_COLUMN = "id";

	private static final String GERMPLASM_LIST_DATA_GID_COLUMN = "gid";

	private static final String GERMPLASM_LIST_DATA_ENTRY_ID_COLUMN = "entryId";

	private static final String GERMPLASM_LIST_NAME_ID_COLUMN = GermplasmListDataDAO.GERMPLASM_LIST_NAME_TABLE_ALIAS + ".id";

	private static final String GERMPLASM_LIST_DATA_TABLE_STATUS_COLUMN = "status";

	private static final String GERMPLASM_DELETED_COLUMN = GermplasmListDataDAO.GERMPLASM_TABLE_ALIAS + ".deleted";

	public static final Integer STATUS_DELETED = 9;
	public static final Integer STATUS_ACTIVE = 0;
	public static final String SOURCE_UNKNOWN = "Unknown";

	private static final String COPY_LISTDATA_TO_NEW_LIST = "INSERT INTO listdata (listid, gid, entryid, source, grpname, lrstatus, llrecid) "
		+ "      SELECT :destListid, gid, entryid, source, grpname, lrstatus, llrecid "
		+ "      FROM listdata "
		+ "      WHERE listid = :srcListid ";

	@SuppressWarnings("unchecked")
	public List<GermplasmListData> getByListId(final Integer id) {
		// Make sure parameters are not null.
		Preconditions.checkNotNull(id, "List id passed in cannot be null.");

		final Criteria criteria = this.getSession().createCriteria(GermplasmListData.class);
		criteria.createAlias(GermplasmListDataDAO.GERMPLASM_LIST_NAME_TABLE,
			GermplasmListDataDAO.GERMPLASM_LIST_NAME_TABLE_ALIAS);
		criteria.createAlias(GermplasmListDataDAO.GERMPLASM_TABLE, GermplasmListDataDAO.GERMPLASM_TABLE_ALIAS);

		criteria.add(Restrictions.eq(GermplasmListDataDAO.GERMPLASM_LIST_NAME_ID_COLUMN, id));
		criteria.add(Restrictions.ne(GermplasmListDataDAO.GERMPLASM_LIST_DATA_TABLE_STATUS_COLUMN,
			GermplasmListDataDAO.STATUS_DELETED));
		criteria.add(Restrictions.eq(GermplasmListDataDAO.GERMPLASM_DELETED_COLUMN, Boolean.FALSE));
		criteria.addOrder(Order.asc(GermplasmListDataDAO.GERMPLASM_LIST_DATA_ENTRY_ID_COLUMN));
		final List<GermplasmListData> germplasmListDataList = criteria.list();
		for (final GermplasmListData germplasmListData : germplasmListDataList) {
			final Germplasm germplasm = germplasmListData.getGermplasm();
			if (germplasm != null) {
				germplasmListData.setGroupId(germplasm.getMgid());
			}
		}
		return germplasmListDataList;
	}

	public long countByListId(final Integer id) {

		// Make sure parameters are not null.
		Preconditions.checkNotNull(id, "List id passed in cannot be null.");

		final StringBuilder sql = new StringBuilder("select count(1) from listdata l, germplsm g");
		sql.append(" where l.gid = g.gid and l.lrstatus != ");
		sql.append(GermplasmListDataDAO.STATUS_DELETED);
		sql.append(" and  g.deleted = 0 ");
		sql.append(" and l.listid = :listId ");
		final Session session = this.getSession();
		final SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter(GermplasmListDataDAO.GERMPLASM_LIST_DATA_LIST_ID_COLUMN, id);
		return ((BigInteger) query.uniqueResult()).longValue();
	}

	public Map<Integer, List<GermplasmListData>> getGermplasmDataListMapByListIds(final List<Integer> listIds) {
		try {

			final Criteria criteria = this.getSession().createCriteria(GermplasmListData.class);
			criteria.createAlias("list", "l");
			criteria.createAlias("germplasm", "g");

			criteria.add(Restrictions.in("l.id", listIds));
			criteria.add(Restrictions.ne("status", GermplasmListDataDAO.STATUS_DELETED));
			criteria.add(Restrictions.eq("g.deleted", Boolean.FALSE));
			criteria.addOrder(Order.asc("entryId"));
			final List<GermplasmListData> germplasmListDataList = criteria.list();
			final Map<Integer, List<GermplasmListData>> germplasmListDataMap = new HashMap<>();
			for (final GermplasmListData germplasmListData : germplasmListDataList) {
				germplasmListDataMap.putIfAbsent(germplasmListData.getList().getId(), new ArrayList<>());
				germplasmListDataMap.get(germplasmListData.getList().getId()).add(germplasmListData);
			}
			return germplasmListDataMap;
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in getGermplasmDataListMapByListIds=" + listIds + " in GermplasmListDataDAO: "
				+ e.getMessage(), e);
		}
	}

	public Map<Integer, GermplasmListData> getMapByEntryId(final Integer listId) {
		final Criteria criteria = this.getSession().createCriteria(GermplasmListData.class);
		criteria.add(Restrictions.eq("list.id", listId));
		final List<GermplasmListData> list = criteria.list();

		final Map<Integer, GermplasmListData> map = new LinkedHashMap<>();
		for (final GermplasmListData listData : list) {
			map.put(listData.getEntryId(), listData);
		}
		return map;
	}

	public GermplasmListData getByListIdAndLrecId(final Integer listId, final Integer lrecId) {

		// Make sure parameters are not null.
		Preconditions.checkNotNull(listId, "List id passed cannot be null.");
		Preconditions.checkNotNull(lrecId, "List record id's passed in cannot be null.");

		final Criteria criteria = this.getSession().createCriteria(GermplasmListData.class);
		criteria.createAlias(GermplasmListDataDAO.GERMPLASM_LIST_NAME_TABLE,
			GermplasmListDataDAO.GERMPLASM_LIST_NAME_TABLE_ALIAS);
		criteria.createAlias(GermplasmListDataDAO.GERMPLASM_TABLE, GermplasmListDataDAO.GERMPLASM_TABLE_ALIAS);
		criteria.add(Restrictions.eq(GermplasmListDataDAO.GERMPLASM_DELETED_COLUMN, Boolean.FALSE));
		criteria.add(Restrictions.eq(GermplasmListDataDAO.GERMPLASM_LIST_NAME_ID_COLUMN, listId));
		criteria.add(Restrictions.eq(GermplasmListDataDAO.GERMPLASM_LIST_DATA_ID_COLUMN, lrecId));
		criteria.add(Restrictions.ne(GermplasmListDataDAO.GERMPLASM_LIST_DATA_TABLE_STATUS_COLUMN,
			GermplasmListDataDAO.STATUS_DELETED));
		criteria.addOrder(Order.asc(GermplasmListDataDAO.GERMPLASM_LIST_DATA_ID_COLUMN));
		return (GermplasmListData) criteria.uniqueResult();

	}

	public int deleteByListId(final Integer listId) {
		// Make sure parameters are not null.
		Preconditions.checkNotNull(listId, "List id passed cannot be null.");
		final Query query = this.getSession().getNamedQuery(GermplasmListData.DELETE_BY_LIST_ID);
		query.setInteger(GermplasmListDataDAO.GERMPLASM_LIST_DATA_LIST_ID_COLUMN, listId);
		return query.executeUpdate();
	}

	public void deleteByListDataIds(final Set<Integer> listDataIds) {
		Preconditions.checkArgument(CollectionUtils.isNotEmpty(listDataIds), "listDataIds passed cannot be empty.");
		final Query query =
			this.getSession().createQuery("DELETE FROM GermplasmListData WHERE id in (:listDataIds)");
		query.setParameterList("listDataIds", listDataIds);
		query.executeUpdate();

	}

	/**
	 * This will return all items of a cross list along with data of parents.
	 * Note that we're getting the name of the parents from its preferred name which is indicated by name record with nstat = 1
	 */
	public List<GermplasmListData> retrieveGermplasmListDataWithImmediateParents(final Integer listID) {
		Preconditions.checkNotNull(listID, "List id passed cannot be null.");
		final List<GermplasmListData> germplasmListData = new ArrayList<>();

		try {

			final String queryStr = "select  lp.lrecid as lrecid,  lp.entryid as entryid, lp.grpname as grpname, "
				+ " if(g.gpid1 = 0, '" + Name.UNKNOWN + "', femaleParentName.nval) as fnval,  g.gpid1 as fpgid,  if(g.gpid2 = 0, '"
				+ Name.UNKNOWN + "', maleParentName.nval) as mnval,  g.gpid2 as mpgid,  "
				+ " g.gid as gid,  lp.source as source,  m.mname as mname, "
				+ " if(g.gpid2 = 0, '" + Name.UNKNOWN
				+ "', (select nMale.grpName from listdata nMale where nMale.gid = maleParentName.gid limit 1)) as malePedigree, "
				+ " if(g.gpid1 = 0, '" + Name.UNKNOWN
				+ "', (select nFemale.grpName from listdata nFemale where nFemale.gid = femaleParentName.gid limit 1)) as femalePedigree "
				+ "from listdata lp  inner join germplsm g on lp.gid = g.gid  "
				+ "left outer join names maleParentName on g.gpid2 = maleParentName.gid and maleParentName.nstat = :preferredNameNstat  "
				+ "left outer join names femaleParentName on g.gpid1 = femaleParentName.gid and femaleParentName.nstat = :preferredNameNstat  "
				+ "left outer join methods m on m.mid = g.methn " + "where lp.listid = :listId group by entryid";

			final SQLQuery query = this.getSession().createSQLQuery(queryStr);
			query.setParameter("listId", listID);
			query.setParameter("preferredNameNstat", Name.NSTAT_PREFERRED_NAME);

			query.addScalar("lrecid");
			query.addScalar("entryid");
			query.addScalar("grpname");
			query.addScalar("fnval");
			query.addScalar("fpgid");
			query.addScalar("mnval");
			query.addScalar("mpgid");
			query.addScalar("gid");
			query.addScalar("source");
			query.addScalar("mname");
			query.addScalar("malePedigree");
			query.addScalar("femalePedigree");

			this.createCrossListDataRows(germplasmListData, query);

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in retrieveCrossListData=" + listID + " in GermplasmListDataDAO: " + e.getMessage(),
				e);
		}

		return germplasmListData;
	}

	@SuppressWarnings("unchecked")
	private void createCrossListDataRows(final List<GermplasmListData> dataList, final SQLQuery query) {
		final List<Object[]> result = query.list();

		for (final Object[] row : result) {
			final Integer id = (Integer) row[0];
			final Integer entryId = (Integer) row[1];
			final String femaleParent = (String) row[3];
			final Integer fgid = (Integer) row[4];
			final String maleParent = (String) row[5];
			final Integer mgid = (Integer) row[6];
			final Integer gid = (Integer) row[7];
			final String seedSource = (String) row[8];
			final String methodName = (String) row[9];
			final String malePedigree = (String) row[10];
			final String femalePedigree = (String) row[11];

			final GermplasmListData data = new GermplasmListData();
			data.setId(id);
			data.setEntryId(entryId);
			data.setGid(gid);
			data.setFemaleParent(new GermplasmParent(fgid, femaleParent, femalePedigree));
			data.setSeedSource(seedSource);
			data.setBreedingMethodName(methodName);
			data.addMaleParent(new GermplasmParent(mgid, maleParent, malePedigree));

			dataList.add(data);
		}
	}

	public GermplasmListData getByListIdAndGid(final Integer listId, final Integer gid) {

		// Make sure parameters are not null.
		Preconditions.checkNotNull(listId, "List id passed cannot be null.");
		Preconditions.checkNotNull(gid, "Gid passed in cannot be null.");

		final Criteria criteria = this.getSession().createCriteria(GermplasmListData.class);
		criteria.createAlias(GermplasmListDataDAO.GERMPLASM_LIST_NAME_TABLE,
			GermplasmListDataDAO.GERMPLASM_LIST_NAME_TABLE_ALIAS);
		criteria.createAlias(GermplasmListDataDAO.GERMPLASM_TABLE, GermplasmListDataDAO.GERMPLASM_TABLE_ALIAS);
		criteria.add(Restrictions.eq(GermplasmListDataDAO.GERMPLASM_LIST_NAME_ID_COLUMN, listId));
		criteria.add(Restrictions.eq(GermplasmListDataDAO.GERMPLASM_LIST_DATA_GID_COLUMN, gid));
		criteria.add(Restrictions.ne(GermplasmListDataDAO.GERMPLASM_LIST_DATA_TABLE_STATUS_COLUMN,
			GermplasmListDataDAO.STATUS_DELETED));
		final List result = criteria.list();
		return (result != null && result.size() > 0 ? (GermplasmListData) result.get(0) : null);

	}

	public void replaceGermplasm(final List<Integer> gidsToReplace, final Germplasm germplasm, final String crossExpansion) {
		Preconditions.checkNotNull(gidsToReplace, "gidsToReplace passed cannot be null.");
		Preconditions.checkArgument(!gidsToReplace.isEmpty(), "gidsToReplace passed cannot be empty.");
		Preconditions.checkNotNull(germplasm, "germplasm passed in cannot be null.");

		try {
			final Query query =
				this.getSession().createQuery(
					"UPDATE GermplasmListData listData SET listData.gid = :replaceWithGid, listData.groupName = :groupName WHERE listData.gid in (:gidsToReplace)");
			query.setParameter("replaceWithGid", germplasm.getGid());
			query.setParameter("groupName", crossExpansion);
			query.setParameterList("gidsToReplace", gidsToReplace);
			query.executeUpdate();
		} catch (final HibernateException e) {
			final String errorMessage =
				"Error in replaceGermplasm for gidsToReplace=" + gidsToReplace + ", germplasm=" + germplasm + " " + e
					.getMessage();
			throw new MiddlewareQueryException(errorMessage, e);
		}

	}

	public List<Integer> getGidsByListId(final Integer listId) {
		// TODO removed deleted gids
		final String sql = "SELECT gid FROM listdata ld WHERE ld.listid = :listId";
		final SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameter("listId", listId);
		return query.list();
	}

	public void reOrderEntries(final Integer listId, final List<Integer> selectedEntries, final Integer selectedPosition) {

		// Get the value of the minimum and maximum entry number of the selected entries
		final String selectedEntriesIntervalSQL = "SELECT MIN(entryid) AS min, MAX(entryid) AS max "
			+ " FROM listdata "
			+ " WHERE listid = :listId AND lrecid IN ( :selectedEntries )";
		final SQLQuery selectedEntriesIntervalQuery = this.getSession().createSQLQuery(selectedEntriesIntervalSQL);
		selectedEntriesIntervalQuery.addScalar("min");
		selectedEntriesIntervalQuery.addScalar("max");
		selectedEntriesIntervalQuery.setParameter("listId", listId);
		selectedEntriesIntervalQuery.setParameterList("selectedEntries", selectedEntries);
		selectedEntriesIntervalQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		final Map<String, Integer> selectedEntriesResult = (Map<String, Integer>) selectedEntriesIntervalQuery.uniqueResult();

		final int selectionMaxValue = selectedPosition + selectedEntries.size() - 1;
		final int least = Math.min(selectedPosition, selectedEntriesResult.get("min"));
		final int greatest = Math.max(selectionMaxValue, selectedEntriesResult.get("max"));

		// This query has two main parts. The first select of the query obtains the new values of entry numbers for the entries that
		// are going to be modified. The second select obtains which entries are going to be modified. To improve performance, this query is
		// using intervals to define only the collection of entries that must be reordered. Also, take in mind that this query does not update
		// any of the selected entries
		final String updateAffectedEntriesSQL = "UPDATE listdata l\n"
			+ "    JOIN (SELECT @row_number_in \\:= 0) i\n"
			+ "    JOIN (SELECT @row_number_out \\:= 0) o\n"
			+ "    INNER JOIN (\n"
			+ "        SELECT to_modify.lrecid rec_id_mod, new_value.entryid\n"
			+ "        FROM (\n"
			+ "            SELECT entryid, (@row_number_in \\:= @row_number_in + 1) AS row_num\n"
			+ "            FROM listdata\n"
			+ "            WHERE listid = :listId AND\n"
			+ "                (( entryid >= :least AND entryid < :selectedPosition)\n"
			+ "                    OR ( entryid > :selectionMaxValue AND entryid <= :greatest)\n"
			+ "                )\n"
			+ "            ) AS new_value,\n"
			+ "            (\n"
			+ "            SELECT lrecid, (@row_number_out \\:= @row_number_out + 1) AS row_num\n"
			+ "            FROM listdata\n"
			+ "            WHERE listid = :listId AND entryid >= :least\n"
			+ "              AND entryid <= :greatest\n"
			+ "              AND\n"
			+ "                    lrecid NOT IN (:selectedEntries)\n"
			+ "                ORDER BY entryid) AS to_modify\n"
			+ "        WHERE new_value.row_num = to_modify.row_num) m\n"
			+ "    ON m.rec_id_mod = l.lrecid\n"
			+ "        AND l.listid = :listId\n"
			+ "SET l.entryid = m.entryid";
		final SQLQuery updateAffectedEntriesQuery = this.getSession().createSQLQuery(updateAffectedEntriesSQL);
		updateAffectedEntriesQuery.setParameter("listId", listId);
		updateAffectedEntriesQuery.setParameter("least", least);
		updateAffectedEntriesQuery.setParameter("greatest", greatest);
		updateAffectedEntriesQuery.setParameter("selectionMaxValue", selectionMaxValue);
		updateAffectedEntriesQuery.setParameter("selectedPosition", selectedPosition);
		updateAffectedEntriesQuery.setParameterList("selectedEntries", selectedEntries);
		updateAffectedEntriesQuery.executeUpdate();

		// Finally, we update the entry numbers of the selected entries.
		final String updateSelectedEntriesSQL = "UPDATE listdata ld \n"
			+ "    JOIN (SELECT @position \\:= :selectedPosition) r\n"
			+ "    INNER JOIN (\n"
			+ "        SELECT lrecid, entryid\n"
			+ "        FROM listdata innerListData\n"
			+ "        WHERE innerlistdata.listid = :listId AND innerlistdata.lrecid IN (:selectedEntries)\n"
			+ "        ORDER BY innerlistdata.entryid ASC) AS tmp\n"
			+ "    ON ld.lrecid = tmp.lrecid\n"
			+ "SET ld.entryid = @position \\:= @position + 1\n"
			+ "WHERE listid = :listId";
		final SQLQuery updateSelectedEntriesQuery = this.getSession().createSQLQuery(updateSelectedEntriesSQL);
		updateSelectedEntriesQuery.setParameter("listId", listId);
		updateSelectedEntriesQuery.setParameter("selectedPosition", selectedPosition - 1);
		updateSelectedEntriesQuery.setParameterList("selectedEntries", selectedEntries);
		updateSelectedEntriesQuery.executeUpdate();
	}

	/**
	 * Reset the entry numbers (entryId) based on the order of current entryid.
	 *
	 * @param listId
	 */
	public void reOrderEntries(final Integer listId) {
		final String sql = "UPDATE listdata ld \n"
			+ "    JOIN (SELECT @position \\:= 0) r\n"
			+ "    INNER JOIN (\n"
			+ "        SELECT lrecid, entryid\n"
			+ "        FROM listdata innerListData\n"
			+ "        WHERE innerlistdata.listid = :listId \n"
			+ "        ORDER BY innerlistdata.entryid ASC) AS tmp\n"
			+ "    ON ld.lrecid = tmp.lrecid\n"
			+ "SET ld.entryid = @position \\:= @position + 1\n"
			+ "WHERE listid = :listId";
		final SQLQuery sqlQuery = this.getSession().createSQLQuery(sql);
		sqlQuery.setParameter("listId", listId);
		sqlQuery.executeUpdate();
	}

	public List<Integer> getListDataIdsByListId(final Integer listId) {
		final String sql = "SELECT lrecid FROM listdata ld WHERE ld.listid = :listId";
		final SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameter("listId", listId);
		return query.list();
	}

	public void copyEntries (final Integer sourceListId, final Integer destListId) {
		try {
			final SQLQuery sqlQuery = this.getSession().createSQLQuery(COPY_LISTDATA_TO_NEW_LIST);
			sqlQuery.setParameter("srcListid", sourceListId);
			sqlQuery.setParameter("destListid", destListId);
			sqlQuery.executeUpdate();
		} catch (final Exception e) {
			final String message = "Error with copyEntries(sourceListId=" + sourceListId + " ): " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message);
		}
	}
}
