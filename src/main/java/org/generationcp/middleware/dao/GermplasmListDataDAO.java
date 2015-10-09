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
import java.util.List;

import org.generationcp.middleware.pojos.GermplasmListData;
import org.hibernate.Criteria;
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

	static final String GERMPLASM_GID_COLUMN = GERMPLASM_TABLE_ALIAS + ".gid";

	static final String GERMPLASM_GRPLCE_COLUMN = GERMPLASM_TABLE_ALIAS + ".grplce";

	static final String GERMPLASM_LIST_NAME_ID_COLUMN = GERMPLASM_LIST_NAME_TABLE_ALIAS + ".id";

	static final String GERMPLASM_LIST_DATA_TABLE_STATUS_COLUMN = "status";

	static final Integer STATUS_DELETED = 9;

	@SuppressWarnings("unchecked")
	public List<GermplasmListData> getByListId(final Integer id) {
		// Make sure parameters are not null.
		Preconditions.checkNotNull(id, "List id passed in cannot be null.");

		final Criteria criteria = this.getSession().createCriteria(GermplasmListData.class);
		criteria.createAlias(GERMPLASM_LIST_NAME_TABLE, GERMPLASM_LIST_NAME_TABLE_ALIAS);
		criteria.createAlias(GERMPLASM_TABLE, GERMPLASM_TABLE_ALIAS);

		criteria.add(Restrictions.eq(GERMPLASM_LIST_NAME_ID_COLUMN, id));
		criteria.add(Restrictions.ne(GERMPLASM_LIST_DATA_TABLE_STATUS_COLUMN, GermplasmListDataDAO.STATUS_DELETED));
		criteria.add(Restrictions.neProperty(GERMPLASM_GRPLCE_COLUMN, GERMPLASM_GID_COLUMN));
		criteria.addOrder(Order.asc(GERMPLASM_LIST_DATA_ENTRY_ID_COLUMN));
		return criteria.list();

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
		query.setParameter(GERMPLASM_LIST_DATA_LIST_ID_COLUMN, id);
		return ((BigInteger) query.uniqueResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	public List<GermplasmListData> getByIds(final List<Integer> entryIds) {

		// Make sure parameters are not null.
		Preconditions.checkNotNull(entryIds, "List entry id's passed in cannot be null.");

		final Criteria criteria = this.getSession().createCriteria(GermplasmListData.class);
		criteria.createAlias(GERMPLASM_TABLE, GERMPLASM_TABLE_ALIAS);
		criteria.add(Restrictions.neProperty(GERMPLASM_GRPLCE_COLUMN, GERMPLASM_GID_COLUMN));
		criteria.add(Restrictions.in(GERMPLASM_LIST_DATA_ID_COLUMN, entryIds));
		criteria.add(Restrictions.ne(GERMPLASM_LIST_DATA_TABLE_STATUS_COLUMN, GermplasmListDataDAO.STATUS_DELETED));
		criteria.addOrder(Order.asc(GERMPLASM_LIST_DATA_ENTRY_ID_COLUMN));
		return criteria.list();
	}

	public GermplasmListData getByListIdAndEntryId(final Integer listId, final Integer entryId) {

		// Make sure parameters are not null.
		Preconditions.checkNotNull(listId, "List id passed in cannot be null.");
		Preconditions.checkNotNull(entryId, "List entry id's passed in cannot be null.");

		final Criteria criteria = this.getSession().createCriteria(GermplasmListData.class);
		criteria.createAlias(GERMPLASM_LIST_NAME_TABLE, GERMPLASM_LIST_NAME_TABLE_ALIAS);
		criteria.createAlias(GERMPLASM_TABLE, GERMPLASM_TABLE_ALIAS);
		criteria.add(Restrictions.neProperty(GERMPLASM_GRPLCE_COLUMN, GERMPLASM_GID_COLUMN));
		criteria.add(Restrictions.eq(GERMPLASM_LIST_NAME_ID_COLUMN, listId));
		criteria.add(Restrictions.eq(GERMPLASM_LIST_DATA_ENTRY_ID_COLUMN, entryId));
		criteria.add(Restrictions.ne(GERMPLASM_LIST_DATA_TABLE_STATUS_COLUMN, GermplasmListDataDAO.STATUS_DELETED));
		criteria.addOrder(Order.asc(GERMPLASM_LIST_DATA_ENTRY_ID_COLUMN));
		return (GermplasmListData) criteria.uniqueResult();
	}

	public GermplasmListData getByListIdAndLrecId(final Integer listId, final Integer lrecId) {

		// Make sure parameters are not null.
		Preconditions.checkNotNull(listId, "List id passed cannot be null.");
		Preconditions.checkNotNull(lrecId, "List record id's passed in cannot be null.");

		final Criteria criteria = this.getSession().createCriteria(GermplasmListData.class);
		criteria.createAlias(GERMPLASM_LIST_NAME_TABLE, GERMPLASM_LIST_NAME_TABLE_ALIAS);
		criteria.createAlias(GERMPLASM_TABLE, GERMPLASM_TABLE_ALIAS);
		criteria.add(Restrictions.neProperty(GERMPLASM_GRPLCE_COLUMN, GERMPLASM_GID_COLUMN));
		criteria.add(Restrictions.eq(GERMPLASM_LIST_NAME_ID_COLUMN, listId));
		criteria.add(Restrictions.eq(GERMPLASM_LIST_DATA_ID_COLUMN, lrecId));
		criteria.add(Restrictions.ne(GERMPLASM_LIST_DATA_TABLE_STATUS_COLUMN, GermplasmListDataDAO.STATUS_DELETED));
		criteria.addOrder(Order.asc(GERMPLASM_LIST_DATA_ID_COLUMN));
		return (GermplasmListData) criteria.uniqueResult();

	}

	public int deleteByListId(final Integer listId) {
		// Make sure parameters are not null.
		Preconditions.checkNotNull(listId, "List id passed cannot be null.");
		final Query query = this.getSession().getNamedQuery(GermplasmListData.DELETE_BY_LIST_ID);
		query.setInteger(GERMPLASM_LIST_DATA_LIST_ID_COLUMN, listId);
		return query.executeUpdate();
	}

}
