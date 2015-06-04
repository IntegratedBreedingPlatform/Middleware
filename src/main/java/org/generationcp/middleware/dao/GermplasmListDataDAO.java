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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link GermplasmListData}.
 *
 */
public class GermplasmListDataDAO extends GenericDAO<GermplasmListData, Integer> {

	private static final Integer STATUS_DELETED = 9;

	@SuppressWarnings("unchecked")
	public List<GermplasmListData> getByListId(Integer id, int start, int numOfRows) throws MiddlewareQueryException {
		try {
			if (id != null) {
				Criteria criteria = this.getSession().createCriteria(GermplasmListData.class);
				criteria.createAlias("list", "l");
				criteria.add(Restrictions.eq("l.id", id));
				criteria.add(Restrictions.ne("status", GermplasmListDataDAO.STATUS_DELETED));
				criteria.setFirstResult(start);
				criteria.setMaxResults(numOfRows);
				criteria.addOrder(Order.asc("entryId"));
				return this.getUpdatedGermplasmListData(criteria.list());
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByListId(id=" + id + ") query from GermplasmListData " + e.getMessage(), e);
		}
		return new ArrayList<GermplasmListData>();
	}

	private List<GermplasmListData> getUpdatedGermplasmListData(List<GermplasmListData> list) throws MiddlewareQueryException {
		List<GermplasmListData> germplasmListDataList = new ArrayList<GermplasmListData>();
		if (list == null || list.isEmpty()) {
			return germplasmListDataList;
		}
		for (GermplasmListData germplasmListData : list) {
			GermplasmListData updatedGermplasmListData = this.getUpdatedGermplasmListData(germplasmListData);
			if (updatedGermplasmListData == null) {
				continue;
			}
			germplasmListDataList.add(updatedGermplasmListData);
		}
		return germplasmListDataList;
	}

	private GermplasmListData getUpdatedGermplasmListData(GermplasmListData germplasmListData) throws MiddlewareQueryException {
		if (germplasmListData == null) {
			return null;
		}
		Germplasm germplasm = this.getGermplasm(germplasmListData.getGid());
		if (germplasm == null) {
			return null;
		} else if (germplasm != null && !new Integer(0).equals(germplasm.getGrplce())) {
			germplasmListData.setGid(germplasm.getGrplce());
		}
		return germplasmListData;
	}

	private Germplasm getGermplasm(Integer gid) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Germplasm.class);
			criteria.add(Restrictions.neProperty("grplce", "gid"));
			criteria.add(Restrictions.eq("gid", gid));
			return (Germplasm) criteria.uniqueResult();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getGermplasm(gid=" + gid + ") " + e.getMessage(), e);
		}
		return null;
	}

	public long countByListId(Integer id) throws MiddlewareQueryException {
		try {
			StringBuilder sql = new StringBuilder("select count(1) from listdata l, germplsm g");
			sql.append(" where l.gid = g.gid and l.lrstatus != ");
			sql.append(GermplasmListDataDAO.STATUS_DELETED);
			sql.append(" and g.grplce != g.gid");
			sql.append(" and l.listid = :listId ");
			Session session = this.getSession();
			SQLQuery query = session.createSQLQuery(sql.toString());
			query.setParameter("listId", id);
			return ((BigInteger) query.uniqueResult()).longValue();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with countByListId(id=" + id + ") query from GermplasmListData " + e.getMessage(), e);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<GermplasmListData> getByIds(List<Integer> entryIds) throws MiddlewareQueryException {
		try {
			if (entryIds != null && !entryIds.isEmpty()) {
				Criteria criteria = this.getSession().createCriteria(GermplasmListData.class);
				criteria.add(Restrictions.in("id", entryIds));
				criteria.add(Restrictions.ne("status", GermplasmListDataDAO.STATUS_DELETED));
				criteria.addOrder(Order.asc("entryId"));
				return this.getUpdatedGermplasmListData(criteria.list());
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByIds(ids=" + entryIds + ") query from GermplasmListData " + e.getMessage(), e);
		}
		return new ArrayList<GermplasmListData>();
	}

	@SuppressWarnings("unchecked")
	public List<GermplasmListData> getByListIdAndGID(Integer listId, Integer gid) throws MiddlewareQueryException {
		try {
			if (listId != null && gid != null) {
				Criteria criteria = this.getSession().createCriteria(GermplasmListData.class);
				criteria.createAlias("list", "l");
				criteria.add(Restrictions.eq("l.id", listId));
				criteria.add(Restrictions.eq("gid", gid));
				criteria.add(Restrictions.ne("status", GermplasmListDataDAO.STATUS_DELETED));
				criteria.addOrder(Order.asc("entryId"));
				return this.getUpdatedGermplasmListData(criteria.list());
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByListIdAndGID(listId=" + listId + ", gid=" + gid + ") query from GermplasmListData "
					+ e.getMessage(), e);
		}
		return new ArrayList<GermplasmListData>();
	}

	public GermplasmListData getByListIdAndEntryId(Integer listId, Integer entryId) throws MiddlewareQueryException {
		try {
			if (listId != null && entryId != null) {
				Criteria criteria = this.getSession().createCriteria(GermplasmListData.class);
				criteria.createAlias("list", "l");
				criteria.add(Restrictions.eq("l.id", listId));
				criteria.add(Restrictions.eq("entryId", entryId));
				criteria.add(Restrictions.ne("status", GermplasmListDataDAO.STATUS_DELETED));
				criteria.addOrder(Order.asc("entryId"));
				return this.getUpdatedGermplasmListData((GermplasmListData) criteria.uniqueResult());
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByListIdAndEntryId(listId=" + listId + ", entryId=" + entryId
					+ ") query from GermplasmListData " + e.getMessage(), e);
		}
		return null;
	}

	public GermplasmListData getByListIdAndLrecId(Integer listId, Integer lrecId) throws MiddlewareQueryException {
		try {
			if (listId != null && lrecId != null) {
				Criteria criteria = this.getSession().createCriteria(GermplasmListData.class);
				criteria.createAlias("list", "l");
				criteria.add(Restrictions.eq("l.id", listId));
				criteria.add(Restrictions.eq("id", lrecId));
				criteria.add(Restrictions.ne("status", GermplasmListDataDAO.STATUS_DELETED));
				criteria.addOrder(Order.asc("id"));
				return this.getUpdatedGermplasmListData((GermplasmListData) criteria.uniqueResult());
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByListIdAndEntryId(listId=" + listId + ", lrecId=" + lrecId
					+ ") query from GermplasmListData " + e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<GermplasmListData> getByGID(Integer gid, int start, int numOfRows) throws MiddlewareQueryException {
		try {
			if (gid != null) {
				Criteria criteria = this.getSession().createCriteria(GermplasmListData.class);
				criteria.createAlias("list", "l");
				criteria.add(Restrictions.eq("gid", gid));
				criteria.add(Restrictions.ne("status", GermplasmListDataDAO.STATUS_DELETED));
				criteria.setFirstResult(start);
				criteria.setMaxResults(numOfRows);
				criteria.addOrder(Order.asc("l.id"));
				criteria.addOrder(Order.asc("entryId"));
				return this.getUpdatedGermplasmListData(criteria.list());
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByGID(gid=" + gid + ") query from GermplasmListData " + e.getMessage(), e);
		}
		return new ArrayList<GermplasmListData>();
	}

	public long countByGID(Integer gid) throws MiddlewareQueryException {
		try {
			if (gid != null) {
				StringBuilder sql = new StringBuilder("select count(1) from listdata l, germplsm g");
				sql.append(" where l.gid = g.gid and l.lrstatus != ");
				sql.append(GermplasmListDataDAO.STATUS_DELETED);
				sql.append(" and g.grplce != g.gid");
				sql.append(" and g.gid = :gid ");
				Session session = this.getSession();
				SQLQuery query = session.createSQLQuery(sql.toString());
				query.setParameter("gid", gid);
				return ((BigInteger) query.uniqueResult()).longValue();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with countByGID(gid=" + gid + ") query from GermplasmListData " + e.getMessage(), e);
		}
		return 0;
	}

	public int deleteByListId(Integer listId) throws MiddlewareQueryException {
		try {
			if (listId != null) {
				Query query = this.getSession().getNamedQuery(GermplasmListData.DELETE_BY_LIST_ID);
				query.setInteger("listId", listId);
				return query.executeUpdate();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with deleteByListId(listId=" + listId + ")  query from GermplasmListData " + e.getMessage(), e);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getGidsByListId(Integer listId) throws MiddlewareQueryException {
		List<Integer> gids = new ArrayList<Integer>();

		try {
			Session session = this.getSession();
			SQLQuery query = session.createSQLQuery("SELECT gid FROM listdata WHERE listid = :listId ");
			query.setParameter("listId", listId);
			return query.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getGidsByListId() query from GermplasmList: " + e.getMessage(), e);
		}
		return gids;
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, String> getGidAndDesigByListId(Integer listId) throws MiddlewareQueryException {
		Map<Integer, String> toReturn = new HashMap<Integer, String>();

		try {
			Session session = this.getSession();
			SQLQuery query = session.createSQLQuery("SELECT DISTINCT gid, desig FROM listdata WHERE listid = :listId ");
			query.setParameter("listId", listId);

			List<Object[]> results = query.list();

			for (Object[] row : results) {
				Integer gid = (Integer) row[0];
				String desig = (String) row[1];
				toReturn.put(gid, desig);
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getGidAndDesigByListId() query from GermplasmList: " + e.getMessage(), e);
		}
		return toReturn;
	}

}
