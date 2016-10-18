/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p>
 * Generation Challenge Programme (GCP)
 * <p>
 * <p>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.middleware.dao.ims;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.inventory.LotAggregateData;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link Lot}.
 */
public class LotDAO extends GenericDAO<Lot, Integer> {

	private static final String LOCATION_ID2 = ", locationId=";

	private static final String ENTITY_ID2 = ", entityId=";

	private static final String QUERY_FROM_LOT = ") query from Lot: ";

	private static final String AT_LOT_DAO = " at LotDAO: ";

	private static final String LOCATION_ID = "locationId";

	private static final String ENTITY_TYPE = "entityType";

	private static final String ENTITY_ID = "entityId";

	/*
	 * NOTE setting the trnstat=0 for actual_balance to include anticipated transaction to the total_amount. This is only temporary change
	 * as required by BMS-1052
	 */
	private static final String GET_LOTS_FOR_GERMPLASM_COLUMNS = "SELECT i.lotid, i.eid, " + "  locid, scaleid, i.comments, "
			+ "  SUM(CASE WHEN trnstat = 0 AND trnqty > 0 THEN trnqty ELSE 0 END) AS actual_balance, "
			+ "  CASE WHEN SUM(trnqty) is null THEN 0 ELSE SUM(trnqty) END AS available_balance, "
			+ "  SUM(CASE WHEN trnstat = 0 AND trnqty <=0 THEN trnqty * -1 ELSE 0 END) AS reserved_amt, "
			+ "  SUM(CASE WHEN trnstat = 1 AND trnqty <=0 THEN trnqty * -1 ELSE 0 END) AS committed_amt, ";

	private static final String GET_LOTS_FOR_GERMPLASM_COLUMNS_WITH_STOCKS =
			LotDAO.GET_LOTS_FOR_GERMPLASM_COLUMNS + "  GROUP_CONCAT(inventory_id SEPARATOR ', ') AS stockids ";

	private static final String GET_LOTS_FOR_GERMPLASM_CONDITION =
			"FROM ims_lot i " + "LEFT JOIN ims_transaction act ON act.lotid = i.lotid AND act.trnstat <> 9 "
					+ "WHERE i.status = 0 AND i.etype = 'GERMPLSM' AND i.eid  IN (:gids) " + "GROUP BY i.lotid ";

	private static final String GET_LOTS_FOR_GERMPLASM =
			LotDAO.GET_LOTS_FOR_GERMPLASM_COLUMNS_WITH_STOCKS + LotDAO.GET_LOTS_FOR_GERMPLASM_CONDITION;

	private static final String GET_LOTS_FOR_GERMPLASM_WITH_FILTERED_STOCKS =
			LotDAO.GET_LOTS_FOR_GERMPLASM_COLUMNS_WITH_STOCKS + LotDAO.GET_LOTS_FOR_GERMPLASM_CONDITION;

	private static final String GET_LOTS_FOR_LIST_ENTRIES =
			"SELECT lot.*, recordid, trnqty * -1, trnstat, trnid " + "FROM " + "   (" + LotDAO.GET_LOTS_FOR_GERMPLASM + "   ) lot "
					+ " LEFT JOIN ims_transaction res ON res.lotid = lot.lotid " + "  AND trnstat in (:statusList) AND trnqty < 0 "
					+ "  AND sourceid = :listId AND sourcetype = 'LIST' ";

	private static final String GET_LOTS_FOR_LIST =
			"SELECT lot.*, recordid, trnqty * -1 , trnstat, trnid " + "FROM " + "   (" + LotDAO.GET_LOTS_FOR_GERMPLASM_WITH_FILTERED_STOCKS
					+ "   ) lot " + " LEFT JOIN ims_transaction res ON res.lotid = lot.lotid "
					+ "  AND trnstat in (:statusList) AND trnqty < 0 " + "  AND sourceid = :listId AND sourcetype = 'LIST' ";

	@SuppressWarnings("unchecked")
	public List<Lot> getByEntityType(String type, int start, int numOfRows) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Lot.class);
			criteria.add(Restrictions.eq(ENTITY_TYPE, type));
			criteria.setFirstResult(start);
			criteria.setMaxResults(numOfRows);
			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByEntityType(type=" + type + QUERY_FROM_LOT + e.getMessage(), e);
		}
		return new ArrayList<Lot>();
	}

	public long countByEntityType(String type) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Lot.class);
			criteria.setProjection(Projections.rowCount());
			criteria.add(Restrictions.eq(ENTITY_TYPE, type));
			return ((Long) criteria.uniqueResult()).longValue();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with countByEntityType(type=" + type + QUERY_FROM_LOT + e.getMessage(), e);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<Lot> getByEntityTypeAndEntityId(String type, Integer entityId, int start, int numOfRows) throws MiddlewareQueryException {
		try {
			if (entityId != null) {
				Criteria criteria = this.getSession().createCriteria(Lot.class);
				criteria.add(Restrictions.eq(ENTITY_TYPE, type));
				criteria.add(Restrictions.eq(ENTITY_ID, entityId));
				criteria.setFirstResult(start);
				criteria.setMaxResults(numOfRows);
				return criteria.list();
			}
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with getByEntityTypeAndEntityId(type=" + type + ENTITY_ID2 + entityId + QUERY_FROM_LOT + e.getMessage(), e);
		}
		return new ArrayList<Lot>();
	}

	public long countByEntityTypeAndEntityId(String type, Integer entityId) throws MiddlewareQueryException {
		try {
			if (entityId != null) {
				Criteria criteria = this.getSession().createCriteria(Lot.class);
				criteria.setProjection(Projections.rowCount());
				criteria.add(Restrictions.eq(ENTITY_TYPE, type));
				criteria.add(Restrictions.eq(ENTITY_ID, entityId));
				return ((Long) criteria.uniqueResult()).longValue();
			}
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with countByEntityTypeAndEntityId(type=" + type + ENTITY_ID2 + entityId + QUERY_FROM_LOT + e.getMessage(), e);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<Lot> getByEntityTypeAndLocationId(String type, Integer locationId, int start, int numOfRows)
			throws MiddlewareQueryException {
		try {
			if (locationId != null) {
				Criteria criteria = this.getSession().createCriteria(Lot.class);
				criteria.add(Restrictions.eq(ENTITY_TYPE, type));
				criteria.add(Restrictions.eq(LOCATION_ID, locationId));
				criteria.setFirstResult(start);
				criteria.setMaxResults(numOfRows);
				return criteria.list();
			}
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with getByEntityTypeAndLocationId(type=" + type + LOCATION_ID2 + locationId + QUERY_FROM_LOT + e.getMessage(),
					e);
		}
		return new ArrayList<Lot>();
	}

	public long countByEntityTypeAndLocationId(String type, Integer locationId) throws MiddlewareQueryException {
		try {
			if (locationId != null) {
				Criteria criteria = this.getSession().createCriteria(Lot.class);
				criteria.setProjection(Projections.rowCount());
				criteria.add(Restrictions.eq(ENTITY_TYPE, type));
				criteria.add(Restrictions.eq(LOCATION_ID, locationId));
				return ((Long) criteria.uniqueResult()).longValue();
			}
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with countByEntityTypeAndLocationId(type=" + type + LOCATION_ID2 + locationId + QUERY_FROM_LOT + e.getMessage(),
					e);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<Lot> getByEntityTypeAndEntityIdAndLocationId(String type, Integer entityId, Integer locationId, int start, int numOfRows)
			throws MiddlewareQueryException {
		try {
			if (entityId != null && locationId != null) {
				Criteria criteria = this.getSession().createCriteria(Lot.class);
				criteria.add(Restrictions.eq(ENTITY_TYPE, type));
				criteria.add(Restrictions.eq(ENTITY_ID, entityId));
				criteria.add(Restrictions.eq(LOCATION_ID, locationId));
				criteria.setFirstResult(start);
				criteria.setMaxResults(numOfRows);
				return criteria.list();
			}
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with getByEntityTypeAndEntityIdAndLocationId(type=" + type + ENTITY_ID2 + entityId + LOCATION_ID2 + locationId
							+ QUERY_FROM_LOT + e.getMessage(), e);
		}
		return new ArrayList<Lot>();
	}

	public long countByEntityTypeAndEntityIdAndLocationId(String type, Integer entityId, Integer locationId)
			throws MiddlewareQueryException {
		try {
			if (entityId != null && locationId != null) {
				Criteria criteria = this.getSession().createCriteria(Lot.class);
				criteria.setProjection(Projections.rowCount());
				criteria.add(Restrictions.eq(ENTITY_TYPE, type));
				criteria.add(Restrictions.eq(ENTITY_ID, entityId));
				criteria.add(Restrictions.eq(LOCATION_ID, locationId));
				return ((Long) criteria.uniqueResult()).longValue();
			}
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with countByEntityTypeAndEntityIdAndLocationId(type=" + type + ENTITY_ID2 + entityId + LOCATION_ID2 + locationId
							+ QUERY_FROM_LOT + e.getMessage(), e);
		}
		return 0;
	}

	public Double getActualLotBalance(Integer lotId) throws MiddlewareQueryException {
		try {
			if (lotId != null) {
				Lot lot = this.getById(lotId, false);
				Criteria criteria = this.getSession().createCriteria(Transaction.class);
				criteria.setProjection(Projections.sum("quantity"));
				criteria.add(Restrictions.eq("lot", lot));
				// get only committed transactions
				criteria.add(Restrictions.eq("status", 1));
				return (Double) criteria.uniqueResult();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getActualLotBalance(lotId=" + lotId + QUERY_FROM_LOT + e.getMessage(), e);
		}
		return 0d;
	}

	public Double getAvailableLotBalance(Integer lotId) throws MiddlewareQueryException {
		try {
			if (lotId != null) {
				Lot lot = this.getById(lotId, false);
				Criteria criteria = this.getSession().createCriteria(Transaction.class);
				criteria.setProjection(Projections.sum("quantity"));
				criteria.add(Restrictions.eq("lot", lot));
				// get all non-cancelled transactions
				criteria.add(Restrictions.ne("status", 9));
				return (Double) criteria.uniqueResult();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getAvailableLotBalance(lotId=" + lotId + QUERY_FROM_LOT + e.getMessage(), e);
		}
		return 0d;
	}

	@SuppressWarnings("unchecked")
	public List<Lot> getByEntityTypeEntityIdsLocationIdAndScaleId(String type, List<Integer> entityIds, Integer locationId, Integer scaleId)
			throws MiddlewareQueryException {
		try {
			if (entityIds != null && !entityIds.isEmpty() && locationId != null) {
				Criteria criteria = this.getSession().createCriteria(Lot.class);
				criteria.add(Restrictions.eq(ENTITY_TYPE, type));
				criteria.add(Restrictions.in(ENTITY_ID, entityIds));
				criteria.add(Restrictions.eq(LOCATION_ID, locationId));
				criteria.add(Restrictions.eq("scaleId", scaleId));
				return criteria.list();
			}
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with getByEntityTypeEntityIdLocationIdAndScaleId(type=" + type + ", entityIds=" + entityIds + LOCATION_ID2
							+ locationId + ", scaleId=" + scaleId + QUERY_FROM_LOT + e.getMessage(), e);
		}
		return new ArrayList<Lot>();
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, BigInteger> countLotsWithAvailableBalance(List<Integer> gids) throws MiddlewareQueryException {
		Map<Integer, BigInteger> lotCounts = new HashMap<Integer, BigInteger>();

		try {
			String sql = "SELECT entity_id, CAST(SUM(CASE WHEN avail_bal = 0 THEN 0 ELSE 1 END) AS UNSIGNED) FROM ( "
					+ "SELECT i.lotid, i.eid AS entity_id, " + "   SUM(trnqty) AS avail_bal " + "  FROM ims_lot i "
					+ "  LEFT JOIN ims_transaction act ON act.lotid = i.lotid AND act.trnstat <> 9 "
					+ " WHERE i.status = 0 AND i.etype = 'GERMPLSM' AND i.eid  in (:gids) " + " GROUP BY i.lotid ) inv "
					+ "WHERE avail_bal > -1 " + "GROUP BY entity_id;";

			Query query = this.getSession().createSQLQuery(sql).setParameterList("gids", gids);
			List<Object[]> result = query.list();
			for (Object[] row : result) {
				Integer gid = (Integer) row[0];
				BigInteger count = (BigInteger) row[1];

				lotCounts.put(gid, count);
			}

		} catch (Exception e) {
			this.logAndThrowException("Error at countLotsWithAvailableBalance=" + gids + AT_LOT_DAO + e.getMessage(), e);
		}

		return lotCounts;
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, Object[]> getLotsWithAvailableBalanceCountAndTotalLotsCount(List<Integer> gids) throws MiddlewareQueryException {
		Map<Integer, Object[]> lotCounts = new HashMap<Integer, Object[]>();

		try {
			String sql = "SELECT entity_id, CAST(SUM(CASE WHEN avail_bal = 0 THEN 0 ELSE 1 END) AS UNSIGNED), Count(DISTINCT lotid) "
					+ ",sum(avail_bal), count(distinct scaleid), scaleid " + " FROM ( " + "SELECT i.lotid, i.eid AS entity_id, "
					+ "   SUM(trnqty) AS avail_bal, i.scaleid as scaleid " + " FROM ims_lot i "
					+ "  LEFT JOIN ims_transaction act ON act.lotid = i.lotid AND act.trnstat <> 9 "
					+ " WHERE i.status = 0 AND i.etype = 'GERMPLSM' AND i.eid  in (:gids) " + " GROUP BY i.lotid ) inv "
					+ "WHERE avail_bal > -1 " + "GROUP BY entity_id;";

			Query query = this.getSession().createSQLQuery(sql).setParameterList("gids", gids);
			List<Object[]> result = query.list();
			for (Object[] row : result) {
				Integer gid = (Integer) row[0];
				BigInteger lotsWithAvailableBalance = (BigInteger) row[1];
				BigInteger lotCount = (BigInteger) row[2];
				Double availableBalance = (Double) row[3];
				BigInteger distinctScaleIdCount = (BigInteger) row[4];
				Integer allLotsScaleId = null;
				if (row[5] != null) {
					allLotsScaleId = (Integer) row[5];
				}

				lotCounts.put(gid,
						new Object[] {lotsWithAvailableBalance, lotCount, availableBalance, distinctScaleIdCount, allLotsScaleId});
			}

		} catch (Exception e) {
			this.logAndThrowException("Error at countLotsWithAvailableBalanceAndTotalLots=" + gids + AT_LOT_DAO + e.getMessage(), e);
		}

		return lotCounts;
	}

	@SuppressWarnings("unchecked")
	public List<Lot> getByEntityTypeAndEntityIds(String type, List<Integer> entityIds) throws MiddlewareQueryException {
		try {
			if (entityIds != null && !entityIds.isEmpty()) {
				Criteria criteria = this.getSession().createCriteria(Lot.class);
				criteria.add(Restrictions.eq(ENTITY_TYPE, type));
				criteria.add(Restrictions.in(ENTITY_ID, entityIds));
				return criteria.list();
			}
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with getByEntityTypeAndEntityIds(type=" + type + ", entityIds=" + entityIds + QUERY_FROM_LOT + e.getMessage(),
					e);
		}
		return new ArrayList<Lot>();
	}

	public List<Lot> getLotAggregateDataForListEntry(Integer listId, Integer gid) throws MiddlewareQueryException {
		List<Lot> lots = new ArrayList<Lot>();

		try {
			String sql = LotDAO.GET_LOTS_FOR_LIST_ENTRIES + " ORDER by lot.lotid ";

			Query query = this.getSession().createSQLQuery(sql);
			query.setParameterList("gids", Collections.singletonList(gid));
			query.setParameter("listId", listId);

			List<Integer> statusList = Lists.newArrayList();
			statusList.add(0);
			statusList.add(1);
			query.setParameterList("statusList", statusList);

			this.createLotRows(lots, query, true);

		} catch (Exception e) {
			this.logAndThrowException(
					"Error at getLotAggregateDataForListEntry for list ID = " + listId + " and GID = " + gid + AT_LOT_DAO + e.getMessage(),
					e);
		}

		return lots;
	}

	public List<Lot> getLotAggregateDataForList(Integer listId, List<Integer> gids) throws MiddlewareQueryException {
		List<Lot> lots = new ArrayList<Lot>();

		try {
			String sql = LotDAO.GET_LOTS_FOR_LIST + " ORDER by lot.eid ";

			Query query = this.getSession().createSQLQuery(sql);
			query.setParameterList("gids", gids);
			query.setParameter("listId", listId);
			List<Integer> statusList = Lists.newArrayList();
			statusList.add(0);
			statusList.add(1);
			query.setParameterList("statusList", statusList);

			this.createLotRows(lots, query, true);

		} catch (Exception e) {
			this.logAndThrowException(
					"Error at getLotAggregateDataForList for list ID = " + listId + " and GIDs = " + gids + AT_LOT_DAO + e.getMessage(), e);
		}

		return lots;
	}

	public List<Lot> getReservedLotAggregateDataForList(Integer listId, List<Integer> gids) throws MiddlewareQueryException {
		List<Lot> lots = new ArrayList<Lot>();

		try {
			String sql = LotDAO.GET_LOTS_FOR_LIST + " ORDER by lot.eid ";

			Query query = this.getSession().createSQLQuery(sql);
			query.setParameterList("gids", gids);
			query.setParameter("listId", listId);
			List<Integer> statusList = Lists.newArrayList();
			statusList.add(0);
			query.setParameterList("statusList", statusList);

			this.createLotRows(lots, query, true);

		} catch (Exception e) {
			this.logAndThrowException(
					"Error at getReservedLotAggregateDataForList for list ID = " + listId + " and GIDs = " + gids + AT_LOT_DAO + e
							.getMessage(), e);
		}
		return lots;
	}

	public List<Lot> getLotAggregateDataForGermplasm(Integer gid) throws MiddlewareQueryException {
		List<Lot> lots = new ArrayList<Lot>();

		try {
			String sql = LotDAO.GET_LOTS_FOR_GERMPLASM + "ORDER by lotid ";

			Query query = this.getSession().createSQLQuery(sql);
			query.setParameterList("gids", Collections.singleton(gid));

			this.createLotRows(lots, query, false);

		} catch (Exception e) {
			this.logAndThrowException("Error at getLotAggregateDataForGermplasm for GID = " + gid + AT_LOT_DAO + e.getMessage(), e);
		}

		return lots;
	}

	@SuppressWarnings("unchecked")
	private void createLotRows(List<Lot> lots, Query query, boolean withReservationMap) {
		List<Object[]> result = query.list();

		Map<Integer, Double> reservationMap = null;
		Map<Integer, Set<String>> reservationStatusMap = null;
		Lot lot = null;

		for (Object[] row : result) {
			Integer lotId = (Integer) row[0];
			if (lot == null || !lot.getId().equals(lotId)) {
				if (lot != null && reservationMap != null) {
					lot.getAggregateData().setReservationMap(reservationMap);
					lot.getAggregateData().setReservationStatusMap(reservationStatusMap);
				}
				Integer entityId = (Integer) row[1];
				Integer locationId = (Integer) row[2];
				Integer scaleId = (Integer) row[3];
				String comments = (String) row[4];
				Double actualBalance = (Double) row[5];
				Double availableBalance = (Double) row[6];
				Double reservedTotal = (Double) row[7];
				Double committedTotal = (Double) row[8];
				String stockIds = (String) row[9];

				lot = new Lot(lotId);
				lot.setEntityId(entityId);
				lot.setLocationId(locationId);
				lot.setScaleId(scaleId);
				lot.setComments(comments);

				LotAggregateData aggregateData = new LotAggregateData(lotId);
				aggregateData.setActualBalance(actualBalance);
				aggregateData.setAvailableBalance(availableBalance);
				aggregateData.setReservedTotal(reservedTotal);
				aggregateData.setCommittedTotal(committedTotal);
				aggregateData.setStockIds(stockIds);

				reservationMap = new HashMap<Integer, Double>();
				aggregateData.setReservationMap(reservationMap);

				reservationStatusMap = new HashMap<>();
				aggregateData.setReservationStatusMap(reservationStatusMap);

				lot.setAggregateData(aggregateData);

				lots.add(lot);
			}

			if (withReservationMap) {
				Integer recordId = (Integer) row[10];
				Double qty = (Double) row[11];
				Integer transactionState = (Integer) row[12];

				// compute total reserved for entry
				if (recordId != null && qty != null) {
					Double prevValue = reservationMap.get(recordId);
					Double prevTotal = prevValue == null ? 0d : prevValue;
					reservationMap.put(recordId, prevTotal + qty);
				}

				if (transactionState != null) {
					if (!reservationStatusMap.containsKey(recordId)) {
						reservationStatusMap.put(recordId, new HashSet<String>());
					}
					reservationStatusMap.get(recordId).add(String.valueOf(transactionState));
				}

				if (row[13] != null) {
					Integer transactionId = (Integer) row[13];
					lot.getAggregateData().setTransactionId(transactionId);
				}

			}

		}

		// set last lot's reservation map
		if (lot != null && reservationMap != null) {
			lot.getAggregateData().setReservationMap(reservationMap);
		}
	}

}
