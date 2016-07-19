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

package org.generationcp.middleware.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.google.common.base.Preconditions;

/**
 * DAO class for {@link GermplasmListData}.
 *
 */
public class GermplasmListDataDAO extends GenericDAO<GermplasmListData, Integer> {

	static final String GERMPLASM_LIST_DATA_LIST_ID_COLUMN = "listId";

	static final String GERMPLASM_TABLE = "germplasm";

	static final String GERMPLASM_TABLE_ALIAS = "g";

	static final String GERMPLASM_LIST_NAME_TABLE = "list";

	static final String GERMPLASM_LIST_NAME_TABLE_ALIAS = "l";

	static final String GERMPLASM_LIST_DATA_ID_COLUMN = "id";

	static final String GERMPLASM_LIST_DATA_ENTRY_ID_COLUMN = "entryId";

	static final String GERMPLASM_GID_COLUMN = GermplasmListDataDAO.GERMPLASM_TABLE_ALIAS + ".gid";

	static final String GERMPLASM_GRPLCE_COLUMN = GermplasmListDataDAO.GERMPLASM_TABLE_ALIAS + ".grplce";

	static final String GERMPLASM_LIST_NAME_ID_COLUMN = GermplasmListDataDAO.GERMPLASM_LIST_NAME_TABLE_ALIAS + ".id";

	static final String GERMPLASM_LIST_DATA_TABLE_STATUS_COLUMN = "status";

	static final Integer STATUS_DELETED = 9;

	@SuppressWarnings("unchecked")
	public List<GermplasmListData> getByListId(final Integer id) {
		// Make sure parameters are not null.
		Preconditions.checkNotNull(id, "List id passed in cannot be null.");

		final Criteria criteria = this.getSession().createCriteria(GermplasmListData.class);
		criteria.createAlias(GermplasmListDataDAO.GERMPLASM_LIST_NAME_TABLE, GermplasmListDataDAO.GERMPLASM_LIST_NAME_TABLE_ALIAS);
		criteria.createAlias(GermplasmListDataDAO.GERMPLASM_TABLE, GermplasmListDataDAO.GERMPLASM_TABLE_ALIAS);

		criteria.add(Restrictions.eq(GermplasmListDataDAO.GERMPLASM_LIST_NAME_ID_COLUMN, id));
		criteria.add(Restrictions.ne(GermplasmListDataDAO.GERMPLASM_LIST_DATA_TABLE_STATUS_COLUMN, GermplasmListDataDAO.STATUS_DELETED));
		criteria.add(Restrictions.neProperty(GermplasmListDataDAO.GERMPLASM_GRPLCE_COLUMN, GermplasmListDataDAO.GERMPLASM_GID_COLUMN));
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
		sql.append(" and g.grplce != g.gid");
		sql.append(" and l.listid = :listId ");
		final Session session = this.getSession();
		final SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter(GermplasmListDataDAO.GERMPLASM_LIST_DATA_LIST_ID_COLUMN, id);
		return ((BigInteger) query.uniqueResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	public List<GermplasmListData> getByIds(final List<Integer> entryIds) {

		// Make sure parameters are not null.
		Preconditions.checkNotNull(entryIds, "List entry id's passed in cannot be null.");

		final Criteria criteria = this.getSession().createCriteria(GermplasmListData.class);
		criteria.createAlias(GermplasmListDataDAO.GERMPLASM_TABLE, GermplasmListDataDAO.GERMPLASM_TABLE_ALIAS);
		criteria.add(Restrictions.neProperty(GermplasmListDataDAO.GERMPLASM_GRPLCE_COLUMN, GermplasmListDataDAO.GERMPLASM_GID_COLUMN));
		criteria.add(Restrictions.in(GermplasmListDataDAO.GERMPLASM_LIST_DATA_ID_COLUMN, entryIds));
		criteria.add(Restrictions.ne(GermplasmListDataDAO.GERMPLASM_LIST_DATA_TABLE_STATUS_COLUMN, GermplasmListDataDAO.STATUS_DELETED));
		criteria.addOrder(Order.asc(GermplasmListDataDAO.GERMPLASM_LIST_DATA_ENTRY_ID_COLUMN));
		return criteria.list();
	}

	public GermplasmListData getByListIdAndEntryId(final Integer listId, final Integer entryId) {

		// Make sure parameters are not null.
		Preconditions.checkNotNull(listId, "List id passed in cannot be null.");
		Preconditions.checkNotNull(entryId, "List entry id's passed in cannot be null.");

		final Criteria criteria = this.getSession().createCriteria(GermplasmListData.class);
		criteria.createAlias(GermplasmListDataDAO.GERMPLASM_LIST_NAME_TABLE, GermplasmListDataDAO.GERMPLASM_LIST_NAME_TABLE_ALIAS);
		criteria.createAlias(GermplasmListDataDAO.GERMPLASM_TABLE, GermplasmListDataDAO.GERMPLASM_TABLE_ALIAS);
		criteria.add(Restrictions.neProperty(GermplasmListDataDAO.GERMPLASM_GRPLCE_COLUMN, GermplasmListDataDAO.GERMPLASM_GID_COLUMN));
		criteria.add(Restrictions.eq(GermplasmListDataDAO.GERMPLASM_LIST_NAME_ID_COLUMN, listId));
		criteria.add(Restrictions.eq(GermplasmListDataDAO.GERMPLASM_LIST_DATA_ENTRY_ID_COLUMN, entryId));
		criteria.add(Restrictions.ne(GermplasmListDataDAO.GERMPLASM_LIST_DATA_TABLE_STATUS_COLUMN, GermplasmListDataDAO.STATUS_DELETED));
		criteria.addOrder(Order.asc(GermplasmListDataDAO.GERMPLASM_LIST_DATA_ENTRY_ID_COLUMN));
		return (GermplasmListData) criteria.uniqueResult();
	}

	public GermplasmListData getByListIdAndLrecId(final Integer listId, final Integer lrecId) {

		// Make sure parameters are not null.
		Preconditions.checkNotNull(listId, "List id passed cannot be null.");
		Preconditions.checkNotNull(lrecId, "List record id's passed in cannot be null.");

		final Criteria criteria = this.getSession().createCriteria(GermplasmListData.class);
		criteria.createAlias(GermplasmListDataDAO.GERMPLASM_LIST_NAME_TABLE, GermplasmListDataDAO.GERMPLASM_LIST_NAME_TABLE_ALIAS);
		criteria.createAlias(GermplasmListDataDAO.GERMPLASM_TABLE, GermplasmListDataDAO.GERMPLASM_TABLE_ALIAS);
		criteria.add(Restrictions.neProperty(GermplasmListDataDAO.GERMPLASM_GRPLCE_COLUMN, GermplasmListDataDAO.GERMPLASM_GID_COLUMN));
		criteria.add(Restrictions.eq(GermplasmListDataDAO.GERMPLASM_LIST_NAME_ID_COLUMN, listId));
		criteria.add(Restrictions.eq(GermplasmListDataDAO.GERMPLASM_LIST_DATA_ID_COLUMN, lrecId));
		criteria.add(Restrictions.ne(GermplasmListDataDAO.GERMPLASM_LIST_DATA_TABLE_STATUS_COLUMN, GermplasmListDataDAO.STATUS_DELETED));
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

	public List<GermplasmListData> getListDataWithParents(final Integer listID) {
		final List<GermplasmListData> germplasmListData = new ArrayList<GermplasmListData>();

		if (listID == null) {
			return germplasmListData;
		}

		try {

			final String queryStr =
					"select  lp.lrecid as lrecid,  lp.entryid as entryid,  lp.desig as desig,  lp.grpname as grpname, "
							+ " fn.nval as fnval,  fp.gid as fpgid,  mn.nval as mnval,  mp.gid as mpgid,  g.gid as gid,  lp.source as source  "
							+ "from listdata lp  inner join germplsm g on lp.gid = g.gid  "
							+ "left outer join germplsm mp on g.gpid2 = mp.gid  "
							+ "left outer join names mn on mp.gid = mn.gid and mn.nstat = 1  "
							+ "left outer join germplsm fp on g.gpid1 = fp.gid  "
							+ "left outer join names fn on fp.gid = fn.gid and fn.nstat = 1  "
							+ "where lp.listid = :listId group by entryid";

			final SQLQuery query = this.getSession().createSQLQuery(queryStr);
			query.setParameter("listId", listID);
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

			this.createGermplasmListDataRows(germplasmListData, query);

		} catch (final HibernateException e) {
			this.logAndThrowException("Error in getListDataWithParents=" + listID + " in GermplasmListDataDAO: " + e.getMessage(), e);
		}

		return germplasmListData;
	}

	private void createGermplasmListDataRows(final List<GermplasmListData> germplasmListDataList, final SQLQuery query) {
		final List<Object[]> result = query.list();

		for (final Object[] row : result) {
			final Integer id = (Integer) row[0];
			final Integer entryId = (Integer) row[1];
			final String designation = (String) row[2];
			final String parentage = (String) row[3];
			final String femaleParent = (String) row[4];
			final Integer fgid = (Integer) row[5];
			final String maleParent = (String) row[6];
			final Integer mgid = (Integer) row[7];
			final Integer gid = (Integer) row[8];
			final String seedSource = (String) row[9];

			final GermplasmListData germplasmListData = new GermplasmListData();
			germplasmListData.setId(id);
			germplasmListData.setEntryId(entryId);
			germplasmListData.setDesignation(designation);
			germplasmListData.setGroupName(parentage);
			germplasmListData.setFemaleParent(femaleParent);
			germplasmListData.setFgid(fgid);
			germplasmListData.setMaleParent(maleParent);
			germplasmListData.setMgid(mgid);
			germplasmListData.setGid(gid);
			germplasmListData.setSeedSource(seedSource);

			germplasmListDataList.add(germplasmListData);
		}
	}
}
