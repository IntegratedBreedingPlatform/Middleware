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

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.inventory.LotAggregateData;
import org.generationcp.middleware.domain.inventory.manager.ExtendedLotDto;
import org.generationcp.middleware.domain.inventory.manager.LotDto;
import org.generationcp.middleware.domain.inventory.manager.LotsSearchDto;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.LotStatus;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.util.SqlQueryParamBuilder;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * DAO class for {@link Lot}.
 */
public class LotDAO extends GenericDAO<Lot, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(LotDAO.class);

	private static final String QUERY_FROM_LOT = ") query from Lot: ";

	private static final String AT_LOT_DAO = " at LotDAO: ";

	private static final String ENTITY_TYPE = "entityType";

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	/*
	 * NOTE setting the trnstat=0 for actual_balance to include anticipated transaction to the total_amount. This is only temporary change
	 * as required by BMS-1052
	 */
	private static final String GET_LOTS_FOR_GERMPLASM_COLUMNS = "SELECT i.lotid, i.eid, " + "  locid, scaleid, i.comments, i.status,"
			+ "  SUM(CASE WHEN trnstat = 1 THEN trnqty ELSE 0 END) AS actual_balance, "
			+ "  SUM(CASE WHEN trnstat = " + TransactionStatus.CONFIRMED.getIntValue() + " OR (trnstat = " + TransactionStatus.PENDING.getIntValue() + " AND trntype = " + TransactionType.WITHDRAWAL.getId()
			+ ") THEN trnqty ELSE 0 END) AS available_balance, "
			+ "  SUM(CASE WHEN trnstat = 0 AND trnqty <=0 THEN trnqty * -1 ELSE 0 END) AS reserved_amt, "
			+ "  SUM(CASE WHEN trnstat = 1 AND trnqty <=0 THEN trnqty * -1 ELSE 0 END) AS committed_amt, ";

	private static final String GET_LOTS_FOR_GERMPLASM_COLUMNS_WITH_STOCKS =
			LotDAO.GET_LOTS_FOR_GERMPLASM_COLUMNS + "  GROUP_CONCAT(DISTINCT stock_id SEPARATOR ', ') AS stockids, created_date ";

	private static final String GET_LOTS_FOR_GERMPLASM_CONDITION =
			"FROM ims_lot i " + "LEFT JOIN ims_transaction act ON act.lotid = i.lotid AND act.trnstat <> 9 "
					+ "WHERE (i.status = 0 OR :includeCloseLots) AND i.etype = 'GERMPLSM' AND i.eid  IN (:gids) " + "GROUP BY i.lotid ";

	private static final String GET_LOTS_FOR_GERMPLASM =
			LotDAO.GET_LOTS_FOR_GERMPLASM_COLUMNS_WITH_STOCKS + LotDAO.GET_LOTS_FOR_GERMPLASM_CONDITION;

	private static final String GET_LOTS_FOR_GERMPLASM_WITH_FILTERED_STOCKS =
			LotDAO.GET_LOTS_FOR_GERMPLASM_COLUMNS_WITH_STOCKS + LotDAO.GET_LOTS_FOR_GERMPLASM_CONDITION;

	private static final String GET_LOTS_FOR_LIST_ENTRIES =
			"SELECT lot.*, recordid, trnqty * -1, trnstat, trnid " + "FROM " + "   (" + LotDAO.GET_LOTS_FOR_GERMPLASM + "   ) lot "
					+ " LEFT JOIN ims_transaction res ON res.lotid = lot.lotid " + "  AND trnstat in (:statusList) AND trnqty < 0 "
					+ "  AND sourceid = :listId AND sourcetype = 'LIST' ";

	private static final String GET_LOTS_STATUS_FOR_GERMPLASM = "SELECT i.lotid, COUNT(DISTINCT (act.trnstat)), act.trnstat"
			+ " FROM ims_lot i LEFT JOIN ims_transaction act ON act.lotid = i.lotid AND act.trnstat <> 9"
			+ " WHERE i.status = 0 AND i.etype = 'GERMPLSM' AND act.trnqty < 0 AND i.eid IN (:gids)" + "GROUP BY i.lotid ORDER BY lotid";

	private static final String GET_LOT_SCALE_FOR_GERMPLSMS = "select  lot.eid, lot.scaleid, cv.name from ims_lot lot "
			+ " LEFT JOIN cvterm_relationship cvr ON cvr.subject_id = lot.scaleid AND cvr.type_id ="+ TermId.HAS_SCALE.getId()
			+ " LEFT JOIN cvterm cv ON cv.cvterm_id = cvr.object_id "
			+ " where lot.eid in (:gids) AND lot.etype = 'GERMPLSM' AND lot.status <> 9 ORDER BY lot.eid";

	private static final String GET_GIDS_WITH_OPEN_LOTS = "select distinct (i.eid) FROM ims_lot i "
			+ "WHERE i.status = 0 AND i.etype = 'GERMPLSM' AND i.eid  IN (:gids) GROUP BY i.lotid ";

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
	public Map<Integer, Object[]> getAvailableBalanceCountAndTotalLotsCount(List<Integer> gids) throws MiddlewareQueryException {
		Map<Integer, Object[]> lotCounts = new HashMap<Integer, Object[]>();

		try {
			String sql = "SELECT entity_id, CAST(SUM(CASE WHEN avail_bal = 0 THEN 0 ELSE 1 END) AS UNSIGNED), Count(DISTINCT lotid) "
					+ ",sum(avail_bal), count(distinct scaleid), scaleid " + " FROM ( " + "SELECT i.lotid, i.eid AS entity_id, "
					+ "  SUM(CASE WHEN trnstat = " + TransactionStatus.CONFIRMED.getIntValue() + " OR (trnstat = " + TransactionStatus.PENDING.getIntValue() + " AND trntype = " + TransactionType.WITHDRAWAL.getId()
					+ ") THEN trnqty ELSE 0 END) AS avail_bal, "
					+ "i.scaleid as scaleid " + " FROM ims_lot i "
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

	public List<Lot> getLotAggregateDataForListEntry(Integer listId, Integer gid) throws MiddlewareQueryException {
		List<Lot> lots = new ArrayList<Lot>();

		try {
			String sql = LotDAO.GET_LOTS_FOR_LIST_ENTRIES + " ORDER by lot.lotid ";

			Query query = this.getSession().createSQLQuery(sql);
			query.setParameterList("gids", Collections.singletonList(gid));
			query.setParameter("listId", listId);
			query.setParameter("includeCloseLots", 1);

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

	public List<Lot> getLotAggregateDataForGermplasm(Integer gid) throws MiddlewareQueryException {
		List<Lot> lots = new ArrayList<Lot>();

		try {
			String sql = LotDAO.GET_LOTS_FOR_GERMPLASM + "ORDER by lotid ";

			Query query = this.getSession().createSQLQuery(sql);
			query.setParameterList("gids", Collections.singleton(gid));
			query.setParameter("includeCloseLots", 1);

			this.createLotRows(lots, query, false);

		} catch (Exception e) {
			this.logAndThrowException("Error at getLotAggregateDataForGermplasm for GID = " + gid + AT_LOT_DAO + e.getMessage(), e);
		}

		return lots;
	}

	public Map<Integer, Object[]> getLotStatusDataForGermplasm(Integer gid) throws MiddlewareQueryException {
		Map<Integer, Object[]> lotStatusCounts = new HashMap<Integer, Object[]>();

		try {
			String sql = LotDAO.GET_LOTS_STATUS_FOR_GERMPLASM;

			Query query = this.getSession().createSQLQuery(sql).setParameterList("gids", Collections.singletonList(gid));
			List<Object[]> result = query.list();
			for (Object[] row : result) {
				Integer lotId = (Integer) row[0];
				BigInteger lotDistinctStatusCount = (BigInteger) row[1];
				Integer distinctStatus = (Integer) row[2];

				lotStatusCounts.put(lotId, new Object[] {lotDistinctStatusCount, distinctStatus});
			}

		} catch (Exception e) {
			this.logAndThrowException("Error at getLotStatusDataForGermplasm for GID = " + gid + AT_LOT_DAO + e.getMessage(), e);
		}

		return lotStatusCounts;
	}

	public List<Object[]> retrieveLotScalesForGermplasms(final List<Integer> gids) throws MiddlewareQueryException {
		List<Object[]> lotScalesForGermplasm = new ArrayList<>();

		try {
			String sql = LotDAO.GET_LOT_SCALE_FOR_GERMPLSMS;

			Query query = this.getSession().createSQLQuery(sql).setParameterList("gids", gids);
			List<Object[]> result = query.list();
			for (Object[] row : result) {
				Integer gid = (Integer) row[0];
				Integer scaleId = (Integer) row[1];
				String scaleName = (String) row[2];
				lotScalesForGermplasm.add(new Object[] {gid, scaleId, scaleName});
			}

		} catch (Exception e) {
			this.logAndThrowException("Error at retrieveLotScalesForGermplasms for GIDss = " + gids + AT_LOT_DAO + e.getMessage(), e);
		}

		return lotScalesForGermplasm;
	}

	public Set<Integer> getGermplasmsWithOpenLots(final List<Integer> gids) {
		Set<Integer> gidsWithOpenLots = new HashSet<>();
		try {
			Query query = this.getSession().createSQLQuery(GET_GIDS_WITH_OPEN_LOTS).setParameterList("gids", gids);
			gidsWithOpenLots = Sets.newHashSet(query.list());
		} catch (Exception e) {
			LotDAO.LOG.error("Error at checkGermplasmsWithOpenLots for GIDss = " + gids + AT_LOT_DAO + e.getMessage(), e);
			throw new MiddlewareQueryException("Error at checkGermplasmsWithOpenLots for GIDss = " + gids + AT_LOT_DAO + e.getMessage(), e);
		}
		return gidsWithOpenLots;
	}

	@SuppressWarnings("unchecked")
	private void createLotRows(List<Lot> lots, Query query, boolean withReservationMap) {
		List<Object[]> result = query.list();

		Map<Integer, Double> reservationMap = null;
		Map<Integer, Double> committedMap = null;
		Map<Integer, Set<String>> reservationStatusMap = null;
		Lot lot = null;

		for (Object[] row : result) {
			Integer lotId = (Integer) row[0];
			if (lot == null || !lot.getId().equals(lotId)) {
				if (lot != null && reservationMap != null && committedMap != null) {
					lot.getAggregateData().setReservationMap(reservationMap);
					lot.getAggregateData().setReservationStatusMap(reservationStatusMap);
					lot.getAggregateData().setCommittedMap(committedMap);
				}
				Integer entityId = (Integer) row[1];
				Integer locationId = (Integer) row[2];
				Integer scaleId = (Integer) row[3];
				String comments = (String) row[4];
				Integer lotStatus = (Integer) row[5];
				Double actualBalance = (Double) row[6];
				Double availableBalance = (Double) row[7];
				Double reservedTotal = (Double) row[8];
				Double committedTotal = (Double) row[9];
				String stockIds = (String) row[10];
				Date createdDate = (Date) row[11];

				lot = new Lot(lotId);
				lot.setEntityId(entityId);
				lot.setLocationId(locationId);
				lot.setScaleId(scaleId);
				lot.setComments(comments);
				lot.setStatus(lotStatus);
				lot.setCreatedDate(createdDate);

				LotAggregateData aggregateData = new LotAggregateData(lotId);
				aggregateData.setActualBalance(actualBalance);
				aggregateData.setAvailableBalance(availableBalance);
				aggregateData.setReservedTotal(reservedTotal);
				aggregateData.setCommittedTotal(committedTotal);
				aggregateData.setStockIds(stockIds);

				reservationMap = new HashMap<Integer, Double>();
				aggregateData.setReservationMap(reservationMap);

				committedMap = new HashMap<>();
				aggregateData.setCommittedMap(committedMap);

				reservationStatusMap = new HashMap<>();
				aggregateData.setReservationStatusMap(reservationStatusMap);

				lot.setAggregateData(aggregateData);

				lots.add(lot);
			}

			if (withReservationMap) {
				Integer recordId = (Integer) row[12];
				Double qty = (Double) row[13];
				Integer transactionState = (Integer) row[14];

				// compute total reserved and committed for entry
				if (recordId != null && qty != null && transactionState != null) {
					Double prevValue = null;
					Double prevTotal = null;
					if(TransactionStatus.PENDING.getIntValue() == transactionState && (qty * -1) < 0.0) {
						prevValue = reservationMap.get(recordId);
						prevTotal = prevValue == null ? 0d : prevValue;

						reservationMap.put(recordId, prevTotal + qty);
					}

					if(TransactionStatus.CONFIRMED.getIntValue() == transactionState) {
						prevValue = committedMap.get(recordId);
						prevTotal = prevValue == null ? 0d : prevValue;

						committedMap.put(recordId, prevTotal + qty);
					}

				}

				if (transactionState != null) {
					if (!reservationStatusMap.containsKey(recordId)) {
						reservationStatusMap.put(recordId, new HashSet<String>());
					}
					reservationStatusMap.get(recordId).add(String.valueOf(transactionState));
				}

				if (row[15] != null) {
					Integer transactionId = (Integer) row[15];
					lot.getAggregateData().setTransactionId(transactionId);
				}

			}

		}

		// set last lot's reservation map
		if (lot != null && reservationMap != null) {
			lot.getAggregateData().setReservationMap(reservationMap);
		}

		// set last lot's comiitted map
		if (lot != null && committedMap != null) {
			lot.getAggregateData().setCommittedMap(committedMap);
		}
	}

	//New inventory functions, please locate them below this line to help cleaning in the near future.
	private final String SEARCH_LOT_QUERY = "SELECT lot.lotid as lotId, " //
		+ "  lot.lot_uuid AS lotUUID, " //
		+ "  lot.stock_id AS stockId, " //
		+ "  lot.eid as gid, " //
		+ "  g.mgid as mgid, " //
		+ "  m.mname as germplasmMethodName, " //
		+ "  gloc.lname as germplasmLocation, " //
		+ "  n.nval as designation, "
		+ "  CASE WHEN lot.status = 0 then '" + LotStatus.ACTIVE.name()  +"' else '"+ LotStatus.CLOSED.name()+ "' end as status, " //
		+ "  lot.locid as locationId, " //
 		+ "  l.lname as locationName, " //
		+ "  lot.scaleid as unitId, " //
		+ "  scale.name as unitName, " //
		+ "  SUM(CASE WHEN transaction.trnstat = " + TransactionStatus.CONFIRMED.getIntValue() +" THEN transaction.trnqty ELSE 0 END) AS actualBalance, " //
		+ "  SUM(CASE WHEN transaction.trnstat = " + TransactionStatus.CONFIRMED.getIntValue() + " OR (transaction.trnstat = " + TransactionStatus.PENDING.getIntValue() + " AND transaction.trntype = " + TransactionType.WITHDRAWAL.getId()
		+ ") THEN transaction.trnqty ELSE 0 END) AS availableBalance, " //
		+ "  SUM(CASE WHEN transaction.trnstat = " + TransactionStatus.PENDING.getIntValue() + " AND transaction.trntype = "
		+ TransactionType.WITHDRAWAL.getId() + " THEN transaction.trnqty * -1 ELSE 0 END) AS reservedTotal, " //
		+ "  SUM(CASE WHEN transaction.trnstat = " + TransactionStatus.CONFIRMED.getIntValue() + " AND transaction.trntype = "
		+ TransactionType.WITHDRAWAL.getId() + " THEN transaction.trnqty * -1 ELSE 0 END) AS withdrawalTotal, " //
		+ "  SUM(CASE WHEN transaction.trnstat = " + TransactionStatus.PENDING.getIntValue() + " and transaction.trntype = "
		+ TransactionType.DEPOSIT.getId() + " THEN transaction.trnqty ELSE 0 END) AS pendingDepositsTotal, " //
		+ "  lot.comments as notes, " //
		+ "  users.uname as createdByUsername, " //
		+ "  lot.created_date as createdDate, " //
		+ "  MAX(CASE WHEN transaction.trnstat = " + TransactionStatus.CONFIRMED.getIntValue() + " AND transaction.trnqty >= 0 THEN transaction.trndate ELSE null END) AS lastDepositDate, " //
		+ "  MAX(CASE WHEN transaction.trnstat = " + TransactionStatus.CONFIRMED.getIntValue() + " AND transaction.trnqty < 0 THEN transaction.trndate ELSE null END) AS lastWithdrawalDate " //
		+ "FROM ims_lot lot " //
		+ "       LEFT JOIN ims_transaction transaction ON transaction.lotid = lot.lotid AND transaction.trnstat <> " + TransactionStatus.CANCELLED.getIntValue()  //
		+ "       INNER JOIN germplsm g on g.gid = lot.eid " //
		+ "       INNER JOIN names n ON n.gid = lot.eid AND n.nstat = 1 " //
		+ "       LEFT JOIN methods m ON m.mid = g.methn " //
		+ "       LEFT JOIN location l on l.locid = lot.locid " //
		+ "       LEFT JOIN location gloc on gloc.locid = g.glocn " //
		+ "       LEFT join cvterm scale on scale.cvterm_id = lot.scaleid " //
		+ "       INNER JOIN workbench.users users on users.userid = lot.userid " //
		+ "WHERE g.deleted=0 "; //

	private static void addSearchLotsQueryFiltersAndGroupBy(
		final SqlQueryParamBuilder paramBuilder,
		final LotsSearchDto lotsSearchDto) {

		if (lotsSearchDto != null) {
			final List<Integer> lotIds = lotsSearchDto.getLotIds();
			if (lotIds != null && !lotIds.isEmpty()) {
				paramBuilder.append(" and lot.lotid IN (:lotIds)");
				paramBuilder.setParameterList("lotIds", lotIds);
			}

			final List<String> lotUUIDs = lotsSearchDto.getLotUUIDs();
			if (lotUUIDs != null && !lotUUIDs.isEmpty()) {
				paramBuilder.append(" and lot.lot_uuid IN (:lotUUIDs)");
				paramBuilder.setParameterList("lotUUIDs", lotUUIDs);
			}

			final List<Integer> gids = lotsSearchDto.getGids();
			if (gids != null && !gids.isEmpty()) {
				paramBuilder.append(" and lot.eid IN (:gids)");
				paramBuilder.setParameterList("gids", gids);
			}

			final List<Integer> mgids = lotsSearchDto.getMgids();
			if (mgids != null && !mgids.isEmpty()) {
				paramBuilder.append(" and g.mgid IN (:mgids)");
				paramBuilder.setParameterList("mgids", mgids);
			}

			final List<Integer> locationIds = lotsSearchDto.getLocationIds();
			if (locationIds != null && !locationIds.isEmpty()) {
				paramBuilder.append(" and lot.locid IN (:locationIds)");
				paramBuilder.setParameterList("locationIds", locationIds);
			}

			final List<Integer> unitIds = lotsSearchDto.getUnitIds();
			if (unitIds != null && !unitIds.isEmpty()) {
				paramBuilder.append(" and lot.scaleid IN (:unitIds)");
				paramBuilder.setParameterList("unitIds", unitIds);
			}

			final String designation = lotsSearchDto.getDesignation();
			if (designation != null) {
				paramBuilder.append(" and n.nval like :designation");
				paramBuilder.setParameter("designation", '%' + designation + '%');
			}

			final Integer lotStatus = lotsSearchDto.getStatus();
			if (lotStatus != null) {
				paramBuilder.append(" and lot.status = :lotStatus");
				paramBuilder.setParameter("lotStatus", lotStatus);
			}

			final String notes = lotsSearchDto.getNotesContainsString();
			if (notes != null) {
				paramBuilder.append(" and lot.comments like :notes");
				paramBuilder.setParameter("notes", '%' + notes + '%');
			}

			final String locationName = lotsSearchDto.getLocationNameContainsString();
			if (locationName != null) {
				paramBuilder.append(" and l.lname like :locationName");
				paramBuilder.setParameter("locationName", '%' + locationName + '%');
			}

			final Date createdDateFrom = lotsSearchDto.getCreatedDateFrom();
			if (createdDateFrom != null) {
				paramBuilder.append(" and DATE(lot.created_date) >= :createdDateFrom");
				paramBuilder.setParameter("createdDateFrom", DATE_FORMAT.format(createdDateFrom));
			}

			final Date createdDateTo = lotsSearchDto.getCreatedDateTo();
			if (createdDateTo != null) {
				paramBuilder.append(" and DATE(lot.created_date) <= :createdDateTo");
				paramBuilder.setParameter("createdDateTo", DATE_FORMAT.format(createdDateTo));
			}

			final String createdByUsername =lotsSearchDto.getCreatedByUsername();
			if (createdByUsername != null) {
				paramBuilder.append(" and users.uname like :createdByUsername");
				paramBuilder.setParameter("createdByUsername", '%' + createdByUsername + '%');
			}

			final List<Integer> germplasmListIds = lotsSearchDto.getGermplasmListIds();
			if (germplasmListIds != null && !germplasmListIds.isEmpty()) {
				paramBuilder.append(" and lot.eid in (select distinct (gid) from listdata where listid in (:germplasmListIds))"
					+ " and lot.etype = 'GERMPLSM' ");
				paramBuilder.setParameterList("germplasmListIds", germplasmListIds);
			}

			final String stockId = lotsSearchDto.getStockId();
			if (stockId != null) {
				paramBuilder.append(" and lot.stock_id like :stockId");
				paramBuilder.setParameter("stockId", stockId + '%');
			}

		}
		paramBuilder.append(" GROUP BY lot.lotid ");

		if (lotsSearchDto != null) {

			paramBuilder.append(" having 1=1 ");

			final Double minActualBalance = lotsSearchDto.getMinActualBalance();
			if (minActualBalance != null) {
				paramBuilder.append(" and SUM(CASE WHEN transaction.trnstat = " + TransactionStatus.CONFIRMED.getIntValue() 
					+ " THEN transaction.trnqty ELSE 0 END) >= :minActualBalance");
				paramBuilder.setParameter("minActualBalance", minActualBalance);
			}

			final Double maxActualBalance = lotsSearchDto.getMaxActualBalance();
			if (maxActualBalance != null) {
				paramBuilder.append(" and SUM(CASE WHEN transaction.trnstat = " + TransactionStatus.CONFIRMED.getIntValue() 
					+ " THEN transaction.trnqty ELSE 0 END) <= :maxActualBalance");
				paramBuilder.setParameter("maxActualBalance", maxActualBalance);
			}

			final Double minAvailableBalance = lotsSearchDto.getMinAvailableBalance();
			if (minAvailableBalance != null) {
				paramBuilder.append(" and SUM(CASE WHEN transaction.trnstat = " + TransactionStatus.CONFIRMED.getIntValue() 
					+ " OR (transaction.trnstat = " + TransactionStatus.PENDING.getIntValue() 
					+ "     AND transaction.trntype = " + TransactionType.WITHDRAWAL.getId() 
					+ ") THEN transaction.trnqty ELSE 0 END) >= :minAvailableBalance");
				paramBuilder.setParameter("minAvailableBalance", minAvailableBalance);
			}

			final Double maxAvailableBalance = lotsSearchDto.getMaxAvailableBalance();
			if (maxAvailableBalance != null) {
				paramBuilder.append(" and SUM(CASE WHEN transaction.trnstat = " + TransactionStatus.CONFIRMED.getIntValue()
					+ " OR (transaction.trnstat = " + TransactionStatus.PENDING.getIntValue()
					+ "     AND transaction.trntype = " + TransactionType.WITHDRAWAL.getId()
					+ " ) THEN transaction.trnqty ELSE 0 END) <= :maxAvailableBalance");
				paramBuilder.setParameter("maxAvailableBalance", maxAvailableBalance);
			}

			final Double minReservedTotal = lotsSearchDto.getMinReservedTotal();
			if (minReservedTotal != null) {
				paramBuilder.append(" and SUM(CASE WHEN transaction.trnstat = " + TransactionStatus.PENDING.getIntValue()
					+ " AND transaction.trntype = " + TransactionType.WITHDRAWAL.getId()
					+ " THEN transaction.trnqty * -1 ELSE 0 END) >= :minReservedTotal");
				paramBuilder.setParameter("minReservedTotal", minReservedTotal);
			}

			final Double maxReservedTotal = lotsSearchDto.getMaxReservedTotal();
			if (maxReservedTotal != null) {
				paramBuilder.append(" and SUM(CASE WHEN transaction.trnstat = " + TransactionStatus.PENDING.getIntValue()
					+ " AND transaction.trntype = " + TransactionType.WITHDRAWAL.getId()
					+ " THEN transaction.trnqty * -1 ELSE 0 END) <= :maxReservedTotal");
				paramBuilder.setParameter("maxReservedTotal", maxReservedTotal);
			}

			final Double minWithdrawalTotal = lotsSearchDto.getMinWithdrawalTotal();
			if (minWithdrawalTotal != null) {
				paramBuilder.append(" and SUM(CASE WHEN transaction.trnstat = " + TransactionStatus.CONFIRMED.getIntValue()
					+ " AND transaction.trntype = " + TransactionType.WITHDRAWAL.getId()
					+ " THEN transaction.trnqty * -1 ELSE 0 END) >= :minWithdrawalTotal");
				paramBuilder.setParameter("minWithdrawalTotal", minWithdrawalTotal);
			}

			final Double maxWithdrawalTotal = lotsSearchDto.getMaxWithdrawalTotal();
			if (maxWithdrawalTotal != null) {
				paramBuilder.append(" and SUM(CASE WHEN transaction.trnstat = " + TransactionStatus.CONFIRMED.getIntValue()
					+ " AND transaction.trntype = " + TransactionType.WITHDRAWAL.getId()
					+ " THEN transaction.trnqty * -1 ELSE 0 END) <= :maxWithdrawalTotal");
				paramBuilder.setParameter("maxWithdrawalTotal", maxWithdrawalTotal);
			}

			final Double minPendingDepositsTotal = lotsSearchDto.getMinPendingDepositsTotal();
			if (minPendingDepositsTotal != null) {
				paramBuilder.append(" and SUM(CASE WHEN transaction.trnstat = " + TransactionStatus.PENDING.getIntValue()
					+ "  and transaction.trntype = " + TransactionType.DEPOSIT.getId()
					+ "  THEN transaction.trnqty ELSE 0 END) >= :minPendingDepositsTotal");
				paramBuilder.setParameter("minPendingDepositsTotal", minPendingDepositsTotal);
			}

			final Double maxPendingDepositsTotal = lotsSearchDto.getMaxPendingDepositsTotal();
			if (maxPendingDepositsTotal != null) {
				paramBuilder.append(" and SUM(CASE WHEN transaction.trnstat = " + TransactionStatus.PENDING.getIntValue()
					+ "  and transaction.trntype = " + TransactionType.DEPOSIT.getId()
					+ "  THEN transaction.trnqty ELSE 0 END) <= :maxPendingDepositsTotal");
				paramBuilder.setParameter("maxPendingDepositsTotal", maxPendingDepositsTotal);
			}

			final Date lastDepositDateFrom = lotsSearchDto.getLastDepositDateFrom();
			if (lastDepositDateFrom != null) {
				paramBuilder.append(" and DATE(MAX(CASE WHEN transaction.trnstat = " + TransactionStatus.CONFIRMED.getIntValue()
					+ " AND transaction.trnqty >= 0 THEN transaction.trndate ELSE null END)) >= :lastDepositDateFrom");
				paramBuilder.setParameter("lastDepositDateFrom", DATE_FORMAT.format(lastDepositDateFrom));
			}

			final Date lastDepositDateTo = lotsSearchDto.getLastDepositDateTo();
			if (lastDepositDateTo != null) {
				paramBuilder.append(" and DATE(MAX(CASE WHEN transaction.trnstat = " + TransactionStatus.CONFIRMED.getIntValue()
					+ " AND transaction.trnqty >= 0 THEN transaction.trndate ELSE null END)) <= :lastDepositDateTo");
				paramBuilder.setParameter("lastDepositDateTo", DATE_FORMAT.format(lastDepositDateTo));
			}

			final Date lastWithdrawalDateFrom = lotsSearchDto.getLastWithdrawalDateFrom();
			if (lastWithdrawalDateFrom != null) {
				paramBuilder.append(" and DATE(MAX(CASE WHEN transaction.trnstat = " + TransactionStatus.CONFIRMED.getIntValue()
					+ " AND transaction.trnqty < 0 THEN transaction.trndate ELSE null END)) >= :lastWithdrawalDateFrom");
				paramBuilder.setParameter("lastWithdrawalDateFrom", DATE_FORMAT.format(lastWithdrawalDateFrom));
			}

			final Date lastWithdrawalDateTo = lotsSearchDto.getLastWithdrawalDateTo();
			if (lastWithdrawalDateTo != null) {
				paramBuilder.append(" and DATE(MAX(CASE WHEN transaction.trnstat = " + TransactionStatus.CONFIRMED.getIntValue()
					+ " AND transaction.trnqty < 0 THEN transaction.trndate ELSE null END)) <= :lastWithdrawalDateTo");
				paramBuilder.setParameter("lastWithdrawalDateTo", DATE_FORMAT.format(lastWithdrawalDateTo));
			}

			final List<Integer> plantingStudyIds = lotsSearchDto.getPlantingStudyIds();
			if (plantingStudyIds != null && !plantingStudyIds.isEmpty()) {
				paramBuilder.append(" and exists(select 1 \n" //
					+ " from project study_filter_p \n" //
					+ "	    inner join project study_filter_plotdata on study_filter_p.project_id = study_filter_plotdata.study_id \n" //
					+ "     inner join nd_experiment study_filter_nde on study_filter_plotdata.project_id = study_filter_nde.project_id \n"
					+ "     inner join ims_experiment_transaction study_filter_iet on study_filter_nde.nd_experiment_id = study_filter_iet.nd_experiment_id \n"
					+ "     inner join ims_transaction study_filter_transaction on study_filter_iet.trnid = study_filter_transaction.trnid \n"
					+ "     inner join ims_lot study_filter_lot on study_filter_transaction.lotid = study_filter_lot.lotid \n" //
					+ " where study_filter_p.project_id in (:plantingStudyIds) and study_filter_lot.lotid = lot.lotid)"); //
				paramBuilder.setParameterList("plantingStudyIds", plantingStudyIds);
			}
		}
	}

	private static void addSortToSearchLotsQuery(final StringBuilder lotsSearchQuery, final Pageable pageable) {
		if (pageable != null) {
			if (pageable.getSort() != null) {
				final List<String> sorts = new ArrayList<>();
				for (final Sort.Order order : pageable.getSort()) {
					sorts.add(order.getProperty() + " " + order.getDirection().toString());
				}
				if (!sorts.isEmpty()) {
					lotsSearchQuery.append(" ORDER BY ").append(Joiner.on(",").join(sorts));
				}
			}
		}
	}

	public List<ExtendedLotDto> searchLots(final LotsSearchDto lotsSearchDto, final Pageable pageable) {
		try {
			final StringBuilder searchLotQuerySql = new StringBuilder(SEARCH_LOT_QUERY);
			addSearchLotsQueryFiltersAndGroupBy(new SqlQueryParamBuilder(searchLotQuerySql), lotsSearchDto);
			addSortToSearchLotsQuery(searchLotQuerySql, pageable);

			final SQLQuery query = this.getSession().createSQLQuery(searchLotQuerySql.toString());
			addSearchLotsQueryFiltersAndGroupBy(new SqlQueryParamBuilder(query), lotsSearchDto);

			query.addScalar("lotId");
			query.addScalar("lotUUID");
			query.addScalar("stockId");
			query.addScalar("gid");
			query.addScalar("mgid");
			query.addScalar("germplasmMethodName");
			query.addScalar("germplasmLocation");
			query.addScalar("designation");
			query.addScalar("status");
			query.addScalar("locationId");
			query.addScalar("locationName");
			query.addScalar("unitId");
			query.addScalar("unitName");
			query.addScalar("actualBalance");
			query.addScalar("availableBalance");
			query.addScalar("reservedTotal");
			query.addScalar("withdrawalTotal");
			query.addScalar("pendingDepositsTotal");
			query.addScalar("notes");
			query.addScalar("createdByUsername");
			query.addScalar("createdDate", Hibernate.DATE);
			query.addScalar("lastDepositDate", Hibernate.DATE);
			query.addScalar("lastWithdrawalDate",Hibernate.DATE);

			query.setResultTransformer(Transformers.aliasToBean(ExtendedLotDto.class));

			GenericDAO.addPaginationToSQLQuery(query, pageable);

			final List<ExtendedLotDto> extendedLotDtos = query.list();

			return extendedLotDtos;
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at searchLots() query on LotDAO: " + e.getMessage(), e);
		}

	}

	public long countSearchLots(final LotsSearchDto lotsSearchDto) {
		try {
			final StringBuilder filteredLotsQuery = new StringBuilder(SEARCH_LOT_QUERY);
			addSearchLotsQueryFiltersAndGroupBy(new SqlQueryParamBuilder(filteredLotsQuery), lotsSearchDto);
			final String countLotsQuery = "Select count(1) from (" + filteredLotsQuery + ") as filteredLots";
			final SQLQuery query = this.getSession().createSQLQuery(countLotsQuery.toString());
			addSearchLotsQueryFiltersAndGroupBy(new SqlQueryParamBuilder(query), lotsSearchDto);
			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at countSearchLots() query on LotDAO: " + e.getMessage(), e);
		}
	}

	public List<UserDefinedField> getGermplasmAttributeTypes(final LotsSearchDto searchDto) {
		try {
			final StringBuilder lotsQuery = new StringBuilder(SEARCH_LOT_QUERY);
			addSearchLotsQueryFiltersAndGroupBy(new SqlQueryParamBuilder(lotsQuery), searchDto);

			final String sql = "select distinct {u.*} from atributs a inner join udflds u "
				+ " 	inner join (" + lotsQuery + ") lots on lots.gid = a.gid"
				+ " where a.atype = u.fldno"
				+ " order by u.fname";

			final SQLQuery query = this.getSession().createSQLQuery(sql);
			addSearchLotsQueryFiltersAndGroupBy(new SqlQueryParamBuilder(query), searchDto);
			query.addEntity("u", UserDefinedField.class);
			return query.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at getGermplasmAttributeTypes() in LotDAO: " + e.getMessage(), e);
		}
	}

	public Map<Integer, Map<Integer, String>> getGermplasmAttributeValues(final LotsSearchDto searchDto) {
		try {
			final StringBuilder lotsQuery = new StringBuilder(SEARCH_LOT_QUERY);
			addSearchLotsQueryFiltersAndGroupBy(new SqlQueryParamBuilder(lotsQuery), searchDto);

			final String sql = "select distinct {a.*} from atributs a inner join (" + lotsQuery + ") lots on lots.gid = a.gid";

			final SQLQuery query = this.getSession().createSQLQuery(sql);
			addSearchLotsQueryFiltersAndGroupBy(new SqlQueryParamBuilder(query), searchDto);
			query.addEntity("a", Attribute.class);
			final List<Attribute> attributes = query.list();

			if (attributes.isEmpty()) {
				return null;
			}

			final HashMap<Integer, Map<Integer, String>> attributeMapByGid = new HashMap<>();
			for (final Attribute attribute : attributes) {
				Map<Integer, String> attrByType = attributeMapByGid.get(attribute.getGermplasmId());
				if (attrByType == null) {
					attrByType = new HashMap<>();
				}
				attrByType.put(attribute.getTypeId(), attribute.getAval());
				attributeMapByGid.put(attribute.getGermplasmId(), attrByType);
			}

			return attributeMapByGid;
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at getGermplasmAttributeValues() in LotDAO: " + e.getMessage(), e);
		}
	}

	public Map<String, BigInteger> getLotsCountPerScaleName(final LotsSearchDto lotsSearchDto) {
		try {
			final Map<String, BigInteger> lotsCountPerScaleName = new HashMap<>();

			final StringBuilder filterLotsQuery = new StringBuilder(SEARCH_LOT_QUERY);
			addSearchLotsQueryFiltersAndGroupBy(new SqlQueryParamBuilder(filterLotsQuery), lotsSearchDto);

			final String countQuery = "SELECT scale.name, count(*) from ("  //
			+ filterLotsQuery + ") as lot left join cvterm scale on (scale.cvterm_id = lot.unitId) " //
				+ "group by  scale.name "; //

			final SQLQuery query = this.getSession().createSQLQuery(countQuery);
			addSearchLotsQueryFiltersAndGroupBy(new SqlQueryParamBuilder(query), lotsSearchDto);

			List<Object[]> result = query.list();
			for (Object[] row : result) {
				final String scaleName = (String) row[0];

				final BigInteger count = (BigInteger) row[1];

				lotsCountPerScaleName.put(scaleName, count);
			}

			return lotsCountPerScaleName;

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at getLotsCountPerScaleName() query on LotDAO: " + e.getMessage(), e);
		}
	}

	public List<String> getInventoryIDsWithBreederIdentifier(final String identifier) {
		try {
			final String queryString = "select stock_id FROM ims_lot WHERE stock_id "
				+ "RLIKE '^:identifier[0-9][0-9]*.*'".replace(":identifier", identifier);
			final Query query = this.getSession().createSQLQuery(queryString);
			return query.list();
		} catch (final HibernateException e) {
			final String message = "Error with getInventoryIDsWithBreederIdentifier query from Transaction: " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public List<LotDto> getLotsByStockIds(final List<String> stockIds) {
		if (stockIds == null || stockIds.isEmpty()) {
			return new ArrayList<>();
		}
		try {
			final String sql = "select l.lotid as lotId, " //
				+ "  l.stock_id as stockId, " //
				+ "  l.eid as gid, " //
				+ "  l.locid as locationId, " //
				+ "  l.scaleid as unitId, " //
				+ "  l.comments as notes, " //
				+ "  CASE WHEN l.status = 0 then 'Active' else 'Closed' end as status " //
				+ "from ims_lot l " //
				+ "       inner join workbench.users u on (u.userid = l.userid) " //
				+ "where l.stock_id in (:stockIds)";

			final SQLQuery query = this.getSession().createSQLQuery(sql);
			query.addScalar("lotId", Hibernate.INTEGER);
			query.addScalar("stockId", Hibernate.STRING);
			query.addScalar("gid", Hibernate.INTEGER);
			query.addScalar("locationId", Hibernate.INTEGER);
			query.addScalar("unitId", Hibernate.INTEGER);
			query.addScalar("notes", Hibernate.STRING);
			query.addScalar("status", Hibernate.STRING);

			query.setParameterList("stockIds", stockIds);

			query.setResultTransformer(Transformers.aliasToBean(LotDto.class));

			return query.list();

		} catch (final HibernateException e) {

			final String message = "Error with getLotsByStockIds query on LotDAO: " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public void closeLots(final List<Integer> lotIds) {
		try {
			String hqlUpdate = "update Lot l set l.status= :status where l.id in (:idList)";
			this.getSession().createQuery(hqlUpdate)
				.setParameter("status", LotStatus.CLOSED.getIntValue())
				.setParameterList("idList", lotIds)
				.executeUpdate();
		} catch (final HibernateException e) {
			final String message = "Error with closeLots query from Transaction: " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}
}
