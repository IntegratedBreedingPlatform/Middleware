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

import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.inventory.manager.ExtendedLotDto;
import org.generationcp.middleware.domain.inventory.manager.LotAttributeColumnDto;
import org.generationcp.middleware.domain.inventory.manager.LotDto;
import org.generationcp.middleware.domain.inventory.manager.LotsSearchDto;
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
import org.hibernate.transform.AliasToBeanResultTransformer;
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
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DAO class for {@link Lot}.
 */
public class LotDAO extends GenericDAO<Lot, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(LotDAO.class);

	private static final String AT_LOT_DAO = " at LotDAO: ";

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

	public List<ExtendedLotDto> searchLots(final LotsSearchDto lotsSearchDto, final Pageable pageable, final Integer maxTotalResults) {
		try {
			final StringBuilder searchLotQuerySql = new StringBuilder(SearchLotDaoQuery.getSelectBaseQuery(lotsSearchDto));
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
			query.addScalar("locationAbbr");
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
			if(lotsSearchDto != null && !MapUtils.isEmpty(lotsSearchDto.getAttributes())) {
				final Map<Integer, Object> attributeFilters = lotsSearchDto.getAttributes();
				for (final Integer attributeVariableId : attributeFilters.keySet()) {
					query.addScalar(SearchLotDaoQuery.formatDynamicAttributeAlias(attributeVariableId));
				}
			}
			if (maxTotalResults != null) {
				query.setMaxResults(maxTotalResults);
			}

			GenericDAO.addPaginationToSQLQuery(query, pageable);

			final List<ExtendedLotDto> extendedLotDtos = new ArrayList<>();

			final List<Object[]> results = query.list();
			for (final Object[] result: results) {
				final ExtendedLotDto lotDto = new ExtendedLotDto();
				lotDto.setLotId((Integer) result[0]);
				lotDto.setLotUUID((String) result[1]);
				lotDto.setStockId((String) result[2]);
				lotDto.setGid((Integer) result[3]);
				lotDto.setMgid((Integer) result[4]);
				lotDto.setGermplasmMethodName((String) result[5]);
				lotDto.setGermplasmLocation((String) result[6]);
				lotDto.setDesignation((String) result[7]);
				lotDto.setStatus((String) result[8]);
				lotDto.setLocationId((Integer) result[9]);
				lotDto.setLocationName((String) result[10]);
				lotDto.setLocationAbbr((String) result[11]);
				lotDto.setUnitId((Integer) result[12]);
				lotDto.setUnitName((String) result[13]);
				lotDto.setActualBalance((Double) result[14]);
				lotDto.setAvailableBalance((Double) result[15]);
				lotDto.setReservedTotal((Double) result[16]);
				lotDto.setWithdrawalTotal((Double) result[17]);
				lotDto.setPendingDepositsTotal((Double) result[18]);
				lotDto.setNotes((String) result[19]);
				lotDto.setCreatedByUsername((String) result[20]);
				lotDto.setCreatedDate((Date) result[21]);
				lotDto.setLastDepositDate((Date) result[22]);
				lotDto.setLastWithdrawalDate((Date) result[23]);
				lotDto.setGermplasmUUID((String) result[24]);

				if(lotsSearchDto != null && !MapUtils.isEmpty(lotsSearchDto.getAttributes())) {
					final Map<Integer, Object> attributeFilters = lotsSearchDto.getAttributes();
					lotDto.setAttributeTypesValueMap(new HashMap<>());
					int i = 25;
					for (final Integer attributeVariableId : attributeFilters.keySet()) {
						lotDto.getAttributeTypesValueMap().put(attributeVariableId, result[i++]);
					}
				}

				extendedLotDtos.add(lotDto);
			}

			return extendedLotDtos;
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at searchLots() query on LotDAO: " + e.getMessage(), e);
		}

	}

	public long countSearchLots(final LotsSearchDto lotsSearchDto) {
		try {
			final StringBuilder filteredLotsQuery = new StringBuilder(SearchLotDaoQuery.getCountBaseQuery(lotsSearchDto));
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
			final StringBuilder lotsQuery = new StringBuilder(SearchLotDaoQuery.getSelectBaseQuery(searchDto));
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

	public List<LotAttributeColumnDto> getLotAttributeColumnDtos(final String programUUID) {
		try {
			final String sql = "SELECT DISTINCT (a.atype) AS id, c.name AS name, vpo.alias AS alias"
				+ "	FROM ims_lot_attribute a "
				+ "	INNER JOIN ims_lot lots ON lots.lotid = a.lotid "
				+ " INNER JOIN cvterm c ON c.cvterm_id = a.atype "
				+ "	LEFT JOIN variable_overrides vpo ON vpo.cvterm_id = c.cvterm_id AND vpo.program_uuid = :programUUID ";
			final SQLQuery query = this.getSession().createSQLQuery(sql);
			query.setParameter("programUUID", programUUID);
			query.setResultTransformer(new AliasToBeanResultTransformer(LotAttributeColumnDto.class));
			return  query.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at getGermplasmAttributeValues() in LotDAO: " + e.getMessage(), e);
		}
	}

	public Map<String, BigInteger> getLotsCountPerScaleName(final LotsSearchDto lotsSearchDto) {
		try {
			final Map<String, BigInteger> lotsCountPerScaleName = new HashMap<>();

			final StringBuilder filterLotsQuery = new StringBuilder(SearchLotDaoQuery.getSelectBaseQuery(lotsSearchDto));
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
