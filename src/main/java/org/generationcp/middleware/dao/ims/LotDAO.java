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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.inventory.LotAggregateData;
import org.generationcp.middleware.domain.inventory.manager.ExtendedLotDto;
import org.generationcp.middleware.domain.inventory.manager.LotDto;
import org.generationcp.middleware.domain.inventory.manager.LotsSearchDto;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.LotStatus;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.util.SqlQueryParamBuilder;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DAO class for {@link Lot}.
 */
public class LotDAO extends GenericDAO<Lot, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(LotDAO.class);

	private static final String QUERY_FROM_LOT = ") query from Lot: ";

	private static final String AT_LOT_DAO = " at LotDAO: ";

	private static final String ENTITY_TYPE = "entityType";

	/*
	 * NOTE setting the trnstat=0 for actual_balance to include anticipated transaction to the total_amount. This is only temporary change
	 * as required by BMS-1052
	 */
	private static final String GET_LOTS_FOR_GERMPLASM_COLUMNS = "SELECT i.lotid, i.eid, " + "  locid, scaleid, i.comments, i.status,"
		+ "  SUM(CASE WHEN trnstat = 1 THEN trnqty ELSE 0 END) AS actual_balance, "
		+ "  SUM(CASE WHEN trnstat = " + TransactionStatus.CONFIRMED.getIntValue() + " OR (trnstat = "
		+ TransactionStatus.PENDING.getIntValue() + " AND trntype = " + TransactionType.WITHDRAWAL.getId()
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

	private static final String GET_LOTS_FOR_LIST_ENTRIES =
		"SELECT lot.*, recordid, trnqty * -1, trnstat, trnid " + "FROM " + "   (" + LotDAO.GET_LOTS_FOR_GERMPLASM + "   ) lot "
			+ " LEFT JOIN ims_transaction res ON res.lotid = lot.lotid " + "  AND trnstat in (:statusList) AND trnqty < 0 "
			+ "  AND sourceid = :listId AND sourcetype = 'LIST' ";

	private static final String GET_LOTS_STATUS_FOR_GERMPLASM = "SELECT i.lotid, COUNT(DISTINCT (act.trnstat)), act.trnstat"
		+ " FROM ims_lot i LEFT JOIN ims_transaction act ON act.lotid = i.lotid AND act.trnstat <> 9"
		+ " WHERE i.status = 0 AND i.etype = 'GERMPLSM' AND act.trnqty < 0 AND i.eid IN (:gids)" + "GROUP BY i.lotid ORDER BY lotid";

	private static final String GET_LOT_SCALE_FOR_GERMPLSMS = "select  lot.eid, lot.scaleid, cv.name from ims_lot lot "
		+ " LEFT JOIN cvterm_relationship cvr ON cvr.subject_id = lot.scaleid AND cvr.type_id =" + TermId.HAS_SCALE.getId()
		+ " LEFT JOIN cvterm cv ON cv.cvterm_id = cvr.object_id "
		+ " where lot.eid in (:gids) AND lot.etype = 'GERMPLSM' AND lot.status <> 9 ORDER BY lot.eid";

	@SuppressWarnings("unchecked")
	public List<Lot> getByEntityType(final String type, final int start, final int numOfRows) throws MiddlewareQueryException {
		try {
			final Criteria criteria = this.getSession().createCriteria(Lot.class);
			criteria.add(Restrictions.eq(ENTITY_TYPE, type));
			criteria.setFirstResult(start);
			criteria.setMaxResults(numOfRows);
			return criteria.list();
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getByEntityType(type=" + type + QUERY_FROM_LOT + e.getMessage(), e);
		}
		return new ArrayList<Lot>();
	}

	public List<Lot> getByGids(final List<Integer> gids) {
		if (CollectionUtils.isNotEmpty(gids)) {
			try {
				final Criteria criteria = this.getSession().createCriteria(Lot.class);
				criteria.add(Restrictions.in("entityId", gids));
				return criteria.list();
			} catch (final HibernateException e) {
				throw new MiddlewareQueryException("Error with getByGids(gid=" + gids + ") query from LotDAO: " + e.getMessage(), e);
			}
		}
		return new ArrayList<>();
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, BigInteger> countLotsWithAvailableBalance(final List<Integer> gids) throws MiddlewareQueryException {
		final Map<Integer, BigInteger> lotCounts = new HashMap<Integer, BigInteger>();

		try {
			final String sql = "SELECT entity_id, CAST(SUM(CASE WHEN avail_bal = 0 THEN 0 ELSE 1 END) AS UNSIGNED) FROM ( "
				+ "SELECT i.lotid, i.eid AS entity_id, " + "   SUM(trnqty) AS avail_bal " + "  FROM ims_lot i "
				+ "  LEFT JOIN ims_transaction act ON act.lotid = i.lotid AND act.trnstat <> 9 "
				+ " WHERE i.status = 0 AND i.etype = 'GERMPLSM' AND i.eid  in (:gids) " + " GROUP BY i.lotid ) inv "
				+ "WHERE avail_bal > -1 " + "GROUP BY entity_id;";

			final Query query = this.getSession().createSQLQuery(sql).setParameterList("gids", gids);
			final List<Object[]> result = query.list();
			for (final Object[] row : result) {
				final Integer gid = (Integer) row[0];
				final BigInteger count = (BigInteger) row[1];

				lotCounts.put(gid, count);
			}

		} catch (final Exception e) {
			this.logAndThrowException("Error at countLotsWithAvailableBalance=" + gids + AT_LOT_DAO + e.getMessage(), e);
		}

		return lotCounts;
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, Object[]> getAvailableBalanceCountAndTotalLotsCount(final List<Integer> gids) throws MiddlewareQueryException {
		final Map<Integer, Object[]> lotCounts = new HashMap<Integer, Object[]>();

		try {
			final String sql = "SELECT entity_id, CAST(SUM(CASE WHEN avail_bal = 0 THEN 0 ELSE 1 END) AS UNSIGNED), Count(DISTINCT lotid) "
				+ ",sum(avail_bal), count(distinct scaleid), scaleid " + " FROM ( " + "SELECT i.lotid, i.eid AS entity_id, "
				+ "  SUM(CASE WHEN trnstat = " + TransactionStatus.CONFIRMED.getIntValue() + " OR (trnstat = "
				+ TransactionStatus.PENDING.getIntValue() + " AND trntype = " + TransactionType.WITHDRAWAL.getId()
				+ ") THEN trnqty ELSE 0 END) AS avail_bal, "
				+ "i.scaleid as scaleid " + " FROM ims_lot i "
				+ "  LEFT JOIN ims_transaction act ON act.lotid = i.lotid AND act.trnstat <> 9 "
				+ " WHERE i.status = 0 AND i.etype = 'GERMPLSM' AND i.eid  in (:gids) " + " GROUP BY i.lotid ) inv "
				+ "WHERE avail_bal > -1 " + "GROUP BY entity_id;";

			final Query query = this.getSession().createSQLQuery(sql).setParameterList("gids", gids);
			final List<Object[]> result = query.list();
			for (final Object[] row : result) {
				final Integer gid = (Integer) row[0];
				final BigInteger lotsWithAvailableBalance = (BigInteger) row[1];
				final BigInteger lotCount = (BigInteger) row[2];
				final Double availableBalance = (Double) row[3];
				final BigInteger distinctScaleIdCount = (BigInteger) row[4];
				Integer allLotsScaleId = null;
				if (row[5] != null) {
					allLotsScaleId = (Integer) row[5];
				}

				lotCounts.put(gid,
					new Object[] {lotsWithAvailableBalance, lotCount, availableBalance, distinctScaleIdCount, allLotsScaleId});
			}

		} catch (final Exception e) {
			this.logAndThrowException("Error at countLotsWithAvailableBalanceAndTotalLots=" + gids + AT_LOT_DAO + e.getMessage(), e);
		}

		return lotCounts;
	}

	public List<Lot> getLotAggregateDataForListEntry(final Integer listId, final Integer gid) throws MiddlewareQueryException {
		final List<Lot> lots = new ArrayList<Lot>();

		try {
			final String sql = LotDAO.GET_LOTS_FOR_LIST_ENTRIES + " ORDER by lot.lotid ";

			final Query query = this.getSession().createSQLQuery(sql);
			query.setParameterList("gids", Collections.singletonList(gid));
			query.setParameter("listId", listId);
			query.setParameter("includeCloseLots", 1);

			final List<Integer> statusList = Lists.newArrayList();
			statusList.add(0);
			statusList.add(1);
			query.setParameterList("statusList", statusList);

			this.createLotRows(lots, query, true);

		} catch (final Exception e) {
			this.logAndThrowException(
				"Error at getLotAggregateDataForListEntry for list ID = " + listId + " and GID = " + gid + AT_LOT_DAO + e.getMessage(),
				e);
		}
		return lots;
	}

	public List<Lot> getLotAggregateDataForGermplasm(final Integer gid) throws MiddlewareQueryException {
		final List<Lot> lots = new ArrayList<Lot>();

		try {
			final String sql = LotDAO.GET_LOTS_FOR_GERMPLASM + "ORDER by lotid ";

			final Query query = this.getSession().createSQLQuery(sql);
			query.setParameterList("gids", Collections.singleton(gid));
			query.setParameter("includeCloseLots", 1);

			this.createLotRows(lots, query, false);

		} catch (final Exception e) {
			this.logAndThrowException("Error at getLotAggregateDataForGermplasm for GID = " + gid + AT_LOT_DAO + e.getMessage(), e);
		}

		return lots;
	}

	public Map<Integer, Object[]> getLotStatusDataForGermplasm(final Integer gid) throws MiddlewareQueryException {
		final Map<Integer, Object[]> lotStatusCounts = new HashMap<Integer, Object[]>();

		try {
			final String sql = LotDAO.GET_LOTS_STATUS_FOR_GERMPLASM;

			final Query query = this.getSession().createSQLQuery(sql).setParameterList("gids", Collections.singletonList(gid));
			final List<Object[]> result = query.list();
			for (final Object[] row : result) {
				final Integer lotId = (Integer) row[0];
				final BigInteger lotDistinctStatusCount = (BigInteger) row[1];
				final Integer distinctStatus = (Integer) row[2];

				lotStatusCounts.put(lotId, new Object[] {lotDistinctStatusCount, distinctStatus});
			}

		} catch (final Exception e) {
			this.logAndThrowException("Error at getLotStatusDataForGermplasm for GID = " + gid + AT_LOT_DAO + e.getMessage(), e);
		}

		return lotStatusCounts;
	}

	public List<Object[]> retrieveLotScalesForGermplasms(final List<Integer> gids) throws MiddlewareQueryException {
		final List<Object[]> lotScalesForGermplasm = new ArrayList<>();

		try {
			final String sql = LotDAO.GET_LOT_SCALE_FOR_GERMPLSMS;

			final Query query = this.getSession().createSQLQuery(sql).setParameterList("gids", gids);
			final List<Object[]> result = query.list();
			for (final Object[] row : result) {
				final Integer gid = (Integer) row[0];
				final Integer scaleId = (Integer) row[1];
				final String scaleName = (String) row[2];
				lotScalesForGermplasm.add(new Object[] {gid, scaleId, scaleName});
			}

		} catch (final Exception e) {
			this.logAndThrowException("Error at retrieveLotScalesForGermplasms for GIDss = " + gids + AT_LOT_DAO + e.getMessage(), e);
		}

		return lotScalesForGermplasm;
	}

	public Set<Integer> getGermplasmsWithOpenLots(final List<Integer> gids) {
		if (CollectionUtils.isEmpty(gids)) {
			return Collections.emptySet();
		}
		try {
			final Query query = this.getSession().createSQLQuery("select distinct (i.eid) FROM ims_lot i "
					+ "WHERE i.status = 0 AND i.etype = 'GERMPLSM' AND i.eid  IN (:gids) GROUP BY i.lotid ")
				.setParameterList("gids", gids);
			return Sets.newHashSet(query.list());
		} catch (final Exception e) {
			LotDAO.LOG.error("Error at checkGermplasmsWithOpenLots for GIDs = " + gids + AT_LOT_DAO + e.getMessage(), e);
			throw new MiddlewareQueryException("Error at checkGermplasmsWithOpenLots for GIDss = " + gids + AT_LOT_DAO + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private void createLotRows(final List<Lot> lots, final Query query, final boolean withReservationMap) {
		final List<Object[]> result = query.list();

		Map<Integer, Double> reservationMap = null;
		Map<Integer, Double> committedMap = null;
		Map<Integer, Set<String>> reservationStatusMap = null;
		Lot lot = null;

		for (final Object[] row : result) {
			final Integer lotId = (Integer) row[0];
			if (lot == null || !lot.getId().equals(lotId)) {
				if (lot != null && reservationMap != null && committedMap != null) {
					lot.getAggregateData().setReservationMap(reservationMap);
					lot.getAggregateData().setReservationStatusMap(reservationStatusMap);
					lot.getAggregateData().setCommittedMap(committedMap);
				}
				final Integer entityId = (Integer) row[1];
				final Integer locationId = (Integer) row[2];
				final Integer scaleId = (Integer) row[3];
				final String comments = (String) row[4];
				final Integer lotStatus = (Integer) row[5];
				final Double actualBalance = (Double) row[6];
				final Double availableBalance = (Double) row[7];
				final Double reservedTotal = (Double) row[8];
				final Double committedTotal = (Double) row[9];
				final String stockIds = (String) row[10];
				final Date createdDate = (Date) row[11];

				lot = new Lot(lotId);
				lot.setEntityId(entityId);
				lot.setLocationId(locationId);
				lot.setScaleId(scaleId);
				lot.setComments(comments);
				lot.setStatus(lotStatus);
				lot.setCreatedDate(createdDate);

				final LotAggregateData aggregateData = new LotAggregateData(lotId);
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
				final Integer recordId = (Integer) row[12];
				final Double qty = (Double) row[13];
				final Integer transactionState = (Integer) row[14];

				// compute total reserved and committed for entry
				if (recordId != null && qty != null && transactionState != null) {
					Double prevValue = null;
					Double prevTotal = null;
					if (TransactionStatus.PENDING.getIntValue() == transactionState && (qty * -1) < 0.0) {
						prevValue = reservationMap.get(recordId);
						prevTotal = prevValue == null ? 0d : prevValue;

						reservationMap.put(recordId, prevTotal + qty);
					}

					if (TransactionStatus.CONFIRMED.getIntValue() == transactionState) {
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
					final Integer transactionId = (Integer) row[15];
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

	public List<ExtendedLotDto> searchLots(final LotsSearchDto lotsSearchDto, final Pageable pageable, final Integer maxTotalResults) {
		try {
			final StringBuilder searchLotQuerySql = new StringBuilder(SearchLotDaoQuery.getSelectBaseQuery());
			SearchLotDaoQuery.addSearchLotsQueryFiltersAndGroupBy(new SqlQueryParamBuilder(searchLotQuerySql), lotsSearchDto);
			SearchLotDaoQuery.addSortToSearchLotsQuery(searchLotQuerySql, pageable);

			final SQLQuery query = this.getSession().createSQLQuery(searchLotQuerySql.toString());
			SearchLotDaoQuery.addSearchLotsQueryFiltersAndGroupBy(new SqlQueryParamBuilder(query), lotsSearchDto);

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
			query.addScalar("createdDate", DateType.INSTANCE);
			query.addScalar("lastDepositDate", DateType.INSTANCE);
			query.addScalar("lastWithdrawalDate", DateType.INSTANCE);
			query.addScalar("germplasmUUID");

			query.setResultTransformer(Transformers.aliasToBean(ExtendedLotDto.class));

			if (maxTotalResults != null) {
				query.setMaxResults(maxTotalResults);
			}

			GenericDAO.addPaginationToSQLQuery(query, pageable);

			final List<ExtendedLotDto> extendedLotDtos = query.list();

			return extendedLotDtos;
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at searchLots() query on LotDAO: " + e.getMessage(), e);
		}

	}

	public long countSearchLots(final LotsSearchDto lotsSearchDto) {
		try {
			final StringBuilder filteredLotsQuery = new StringBuilder(SearchLotDaoQuery.getCountBaseQuery());
			SearchLotDaoQuery.addSearchLotsQueryFiltersAndGroupBy(new SqlQueryParamBuilder(filteredLotsQuery), lotsSearchDto);
			SearchLotDaoQuery.addLimit(filteredLotsQuery);
			final String countLotsQuery = "Select count(1) from (" + filteredLotsQuery + ") as filteredLots";
			final SQLQuery query = this.getSession().createSQLQuery(countLotsQuery);
			SearchLotDaoQuery.addSearchLotsQueryFiltersAndGroupBy(new SqlQueryParamBuilder(query), lotsSearchDto);
			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at countSearchLots() query on LotDAO: " + e.getMessage(), e);
		}
	}

	public Map<Integer, Map<Integer, String>> getGermplasmAttributeValues(final LotsSearchDto searchDto) {
		try {
			final StringBuilder lotsQuery = new StringBuilder(SearchLotDaoQuery.getSelectBaseQuery());
			SearchLotDaoQuery.addSearchLotsQueryFiltersAndGroupBy(new SqlQueryParamBuilder(lotsQuery), searchDto);

			final String sql = "select distinct {a.*} from atributs a inner join (" + lotsQuery + ") lots on lots.gid = a.gid";

			final SQLQuery query = this.getSession().createSQLQuery(sql);
			SearchLotDaoQuery.addSearchLotsQueryFiltersAndGroupBy(new SqlQueryParamBuilder(query), searchDto);
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

			final StringBuilder filterLotsQuery = new StringBuilder(SearchLotDaoQuery.getSelectBaseQuery());
			SearchLotDaoQuery.addSearchLotsQueryFiltersAndGroupBy(new SqlQueryParamBuilder(filterLotsQuery), lotsSearchDto);

			final String countQuery = "SELECT scale.name, count(*) from ("  //
				+ filterLotsQuery + ") as lot left join cvterm scale on (scale.cvterm_id = lot.unitId) " //
				+ "group by  scale.name "; //

			final SQLQuery query = this.getSession().createSQLQuery(countQuery);
			SearchLotDaoQuery.addSearchLotsQueryFiltersAndGroupBy(new SqlQueryParamBuilder(query), lotsSearchDto);

			final List<Object[]> result = query.list();
			for (final Object[] row : result) {
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
			final String queryString = "select stock_id FROM ims_lot WHERE UPPER(stock_id) "
				+ "RLIKE UPPER('^:identifier[0-9][0-9]*.*')".replace(":identifier", identifier);
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
			query.addScalar("lotId", IntegerType.INSTANCE);
			query.addScalar("stockId", StringType.INSTANCE);
			query.addScalar("gid", IntegerType.INSTANCE);
			query.addScalar("locationId", IntegerType.INSTANCE);
			query.addScalar("unitId", IntegerType.INSTANCE);
			query.addScalar("notes", StringType.INSTANCE);
			query.addScalar("status", StringType.INSTANCE);

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
			if (CollectionUtils.isNotEmpty(lotIds)) {
				final String hqlUpdate = "update Lot l set l.status= :status where l.id in (:idList)";
				this.getSession().createQuery(hqlUpdate)
					.setParameter("status", LotStatus.CLOSED.getIntValue())
					.setParameterList("idList", lotIds)
					.executeUpdate();
			}
		} catch (final HibernateException e) {
			final String message = "Error with closeLots query from Transaction: " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public void replaceGermplasm(final List<Integer> gidsToReplace, final Integer gidReplaceWith) {
		try {
			if (CollectionUtils.isNotEmpty(gidsToReplace)) {
				final String hqlUpdate = "update Lot l set l.entityId = :gidReplaceWith where l.entityId in (:gidsToReplace)";
				this.getSession().createQuery(hqlUpdate)
					.setParameter("gidReplaceWith", gidReplaceWith)
					.setParameterList("gidsToReplace", gidsToReplace)
					.executeUpdate();
			}
		} catch (final HibernateException e) {
			final String message = "Error with replaceGermplasm query from Lot: " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}

	}
}
