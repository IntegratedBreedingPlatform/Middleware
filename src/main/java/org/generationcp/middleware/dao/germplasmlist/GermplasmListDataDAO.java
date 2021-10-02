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
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.api.germplasmlist.search.GermplasmListDataSearchRequest;
import org.generationcp.middleware.api.germplasmlist.search.GermplasmListDataSearchResponse;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.GermplasmListDataView;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.germplasm.GermplasmParent;
import org.generationcp.middleware.util.SQLQueryBuilder;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.data.domain.Pageable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * DAO class for {@link GermplasmListData}.
 */
public class GermplasmListDataDAO extends GenericDAO<GermplasmListData, Integer> {

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

	@SuppressWarnings("unchecked")
	public List<GermplasmListData> getByIds(final List<Integer> entryIds) {

		// Make sure parameters are not null.
		Preconditions.checkNotNull(entryIds, "List entry id's passed in cannot be null.");

		final Criteria criteria = this.getSession().createCriteria(GermplasmListData.class);
		criteria.createAlias(GermplasmListDataDAO.GERMPLASM_TABLE, GermplasmListDataDAO.GERMPLASM_TABLE_ALIAS);
		criteria.add(Restrictions.eq(GermplasmListDataDAO.GERMPLASM_DELETED_COLUMN, Boolean.FALSE));
		criteria.add(Restrictions.in(GermplasmListDataDAO.GERMPLASM_LIST_DATA_ID_COLUMN, entryIds));
		criteria.add(Restrictions.ne(GermplasmListDataDAO.GERMPLASM_LIST_DATA_TABLE_STATUS_COLUMN,
			GermplasmListDataDAO.STATUS_DELETED));
		criteria.addOrder(Order.asc(GermplasmListDataDAO.GERMPLASM_LIST_DATA_ENTRY_ID_COLUMN));
		return criteria.list();
	}

	public GermplasmListData getByListIdAndEntryId(final Integer listId, final Integer entryId) {

		// Make sure parameters are not null.
		Preconditions.checkNotNull(listId, "List id passed in cannot be null.");
		Preconditions.checkNotNull(entryId, "List entry id's passed in cannot be null.");

		final Criteria criteria = this.getSession().createCriteria(GermplasmListData.class);
		criteria.createAlias(GermplasmListDataDAO.GERMPLASM_LIST_NAME_TABLE,
			GermplasmListDataDAO.GERMPLASM_LIST_NAME_TABLE_ALIAS);
		criteria.createAlias(GermplasmListDataDAO.GERMPLASM_TABLE, GermplasmListDataDAO.GERMPLASM_TABLE_ALIAS);
		criteria.add(Restrictions.eq(GermplasmListDataDAO.GERMPLASM_DELETED_COLUMN, Boolean.FALSE));
		criteria.add(Restrictions.eq(GermplasmListDataDAO.GERMPLASM_LIST_NAME_ID_COLUMN, listId));
		criteria.add(Restrictions.eq(GermplasmListDataDAO.GERMPLASM_LIST_DATA_ENTRY_ID_COLUMN, entryId));
		criteria.add(Restrictions.ne(GermplasmListDataDAO.GERMPLASM_LIST_DATA_TABLE_STATUS_COLUMN,
			GermplasmListDataDAO.STATUS_DELETED));
		criteria.addOrder(Order.asc(GermplasmListDataDAO.GERMPLASM_LIST_DATA_ENTRY_ID_COLUMN));
		return (GermplasmListData) criteria.uniqueResult();
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

	/**
	 * This will return all items of a cross list along with data of parents.
	 * Note that we're getting the name of the parents from its preferred name which is indicated by name record with nstat = 1
	 */
	public List<GermplasmListData> retrieveGermplasmListDataWithImmediateParents(final Integer listID) {
		Preconditions.checkNotNull(listID, "List id passed cannot be null.");
		final List<GermplasmListData> germplasmListData = new ArrayList<>();

		try {

			final String queryStr = "select  lp.lrecid as lrecid,  lp.entryid as entryid,  lp.desig as desig,  lp.grpname as grpname, "
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
			query.addScalar("desig");
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
			final String designation = (String) row[2];
			final String femaleParent = (String) row[4];
			final Integer fgid = (Integer) row[5];
			final String maleParent = (String) row[6];
			final Integer mgid = (Integer) row[7];
			final Integer gid = (Integer) row[8];
			final String seedSource = (String) row[9];
			final String methodName = (String) row[10];
			final String malePedigree = (String) row[11];
			final String femalePedigree = (String) row[12];

			final GermplasmListData data = new GermplasmListData();
			data.setId(id);
			data.setEntryId(entryId);
			data.setGid(gid);
			data.setDesignation(designation);
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
					"UPDATE GermplasmListData listData SET listData.gid = :replaceWithGid, listData.designation = :designation, listData.groupName = :groupName WHERE listData.gid in (:gidsToReplace)");
			query.setParameter("replaceWithGid", germplasm.getGid());
			query.setParameter("designation",
				germplasm.getPreferredName() != null ? germplasm.getPreferredName().getNval() : StringUtils.EMPTY);
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
		String sql = "SELECT gid FROM listdata ld WHERE ld.listid = :listId";
		final SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameter("listId", listId);
		return query.list();
	}

}
