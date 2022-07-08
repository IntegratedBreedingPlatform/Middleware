package org.generationcp.middleware.dao.ims;

import com.google.common.base.Joiner;
import org.generationcp.middleware.domain.inventory.manager.LotsSearchDto;
import org.generationcp.middleware.pojos.ims.ExperimentTransactionType;
import org.generationcp.middleware.pojos.ims.LotStatus;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.util.SqlQueryParamBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

final class SearchLotDaoQuery {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static final String ATTRIBUTE = "Attribute";

	private static final String BASE_QUERY = "SELECT %s "
		+ "FROM ims_lot lot " //
		+ "       LEFT JOIN ims_transaction transaction ON transaction.lotid = lot.lotid AND transaction.trnstat <> " + TransactionStatus.CANCELLED.getIntValue()  //
		+ "       INNER JOIN germplsm g on g.gid = lot.eid " //
		+ "       INNER JOIN names n FORCE INDEX (names_idx01) ON n.gid = lot.eid AND n.nstat = 1 " //
		+ "       LEFT JOIN methods m ON m.mid = g.methn " //
		+ "       LEFT JOIN location l on l.locid = lot.locid " //
		+ "       LEFT JOIN location gloc on gloc.locid = g.glocn " //
		+ "       LEFT join cvterm scale on scale.cvterm_id = lot.scaleid " //
		+ "       INNER JOIN workbench.users users on users.userid = lot.userid "; //
	private static final String DELETED_CONDITION = " WHERE g.deleted=0 ";

	private static final String SELECT_EXPRESION = " lot.lotid as lotId, " //
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
		+ "  l.labbr as locationAbbr, " //
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
		+ "  MAX(CASE WHEN transaction.trnstat = " + TransactionStatus.CONFIRMED.getIntValue() + " AND transaction.trnqty < 0 THEN transaction.trndate ELSE null END) AS lastWithdrawalDate, " //
		+ "  g.germplsm_uuid as germplasmUUID ";

	private static final String LIMIT_CLAUSE = " LIMIT 5000 ";

	public static String getSelectBaseQuery(final LotsSearchDto lotsSearchDto) {
		String selectExpression = SELECT_EXPRESION;
		String baseQuery = BASE_QUERY;
		if (!CollectionUtils.isEmpty(lotsSearchDto.getAttributeFilters())) {
			final Map<Integer, Object> attributeFilters = lotsSearchDto.getAttributeFilters();
			for (final Integer attributeVariableId : attributeFilters.keySet()) {
				final String alias = formatDynamicAttributeAlias(attributeVariableId);
				selectExpression += String.format(", %s.aval AS %s", alias, alias);
				baseQuery += formatLotAttributeJoin(alias, attributeVariableId);
			}
		}
		String selectBaseQuery = String.format(baseQuery, selectExpression);
		selectBaseQuery += DELETED_CONDITION;
		return selectBaseQuery;
	}



	public static String getCountBaseQuery(final LotsSearchDto lotsSearchDto) {
		String baseQuery = BASE_QUERY;
		if (!CollectionUtils.isEmpty(lotsSearchDto.getAttributeFilters())) {
			final Map<Integer, Object> attributeFilters = lotsSearchDto.getAttributeFilters();
			for (final Integer attributeVariableId : attributeFilters.keySet()) {
				final String alias = formatDynamicAttributeAlias(attributeVariableId);
				baseQuery += formatLotAttributeJoin(alias, attributeVariableId);
			}
		}
		baseQuery += DELETED_CONDITION;
		return String.format(baseQuery, " count(1) ");
	}

	public static void addSearchLotsQueryFiltersAndGroupBy(
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

			if (!CollectionUtils.isEmpty(lotsSearchDto.getGermplasmUUIDs())) {
				paramBuilder.append(" and g.germplsm_uuid IN (:germplasmGuids)");
				paramBuilder.setParameterList("germplasmGuids", lotsSearchDto.getGermplasmUUIDs());
			}

			if (!CollectionUtils.isEmpty(lotsSearchDto.getAttributeFilters())) {
				final Map<Integer, Object> attributeFilters = lotsSearchDto.getAttributeFilters();
				attributeFilters.forEach((variableId, value) -> {
					if (value != null || !StringUtils.isEmpty((String) value)) {
						final String alias = formatDynamicAttributeAlias(variableId);
						final String parameterName = String.format("%s_ATTRIBUTE_FILTER", alias);
						paramBuilder.append(String.format(" and %s.aval LIKE :%s", alias, parameterName));
						paramBuilder.setParameter(parameterName, "%" + value + "%");
					}

				});
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
					+ "                and study_filter_iet.type = " + ExperimentTransactionType.PLANTING.getId()
					+ "     inner join ims_transaction study_filter_transaction on study_filter_iet.trnid = study_filter_transaction.trnid \n"
					+ "     inner join ims_lot study_filter_lot on study_filter_transaction.lotid = study_filter_lot.lotid \n" //
					+ " where study_filter_p.project_id in (:plantingStudyIds) and study_filter_lot.lotid = lot.lotid)"); //
				paramBuilder.setParameterList("plantingStudyIds", plantingStudyIds);
			}

			final List<Integer> harvestingStudyIds = lotsSearchDto.getHarvestingStudyIds();
			if (harvestingStudyIds != null && !harvestingStudyIds.isEmpty()) {
				paramBuilder.append(" and exists(select 1 \n" //
					+ " from project study_filter_p \n" //
					+ "	    inner join project study_filter_plotdata on study_filter_p.project_id = study_filter_plotdata.study_id \n" //
					+ "     inner join nd_experiment study_filter_nde on study_filter_plotdata.project_id = study_filter_nde.project_id \n"
					+ "     inner join ims_experiment_transaction study_filter_iet on study_filter_nde.nd_experiment_id = study_filter_iet.nd_experiment_id \n"
					+ "                and study_filter_iet.type = " + ExperimentTransactionType.HARVESTING.getId()
					+ "     inner join ims_transaction study_filter_transaction on study_filter_iet.trnid = study_filter_transaction.trnid \n"
					+ "     inner join ims_lot study_filter_lot on study_filter_transaction.lotid = study_filter_lot.lotid \n" //
					+ " where study_filter_p.project_id in (:harvestingStudyIds) and study_filter_lot.lotid = lot.lotid)"); //
				paramBuilder.setParameterList("harvestingStudyIds", harvestingStudyIds);
			}
		}
	}

	public static void addSortToSearchLotsQuery(final StringBuilder query, final Pageable pageable) {
		if (pageable != null) {
			if (pageable.getSort() != null) {
				final List<String> sorts = new ArrayList<>();
				for (final Sort.Order order : pageable.getSort()) {
					final String orderProperty = getOrderProperty(order.getProperty());
					sorts.add(orderProperty + " " + order.getDirection().toString());
				}
				if (!sorts.isEmpty()) {
					query.append(" ORDER BY ").append(Joiner.on(",").join(sorts));
				}
			}
		}
	}

	private static String getOrderProperty(final String orderProperty) {
		if (org.apache.commons.lang3.StringUtils.isNumeric(orderProperty)) {
			return formatDynamicAttributeAlias(Integer.parseInt(orderProperty));
		} else {
			return orderProperty;
		}
	}

	public static void addLimit(final StringBuilder query) {
		query.append(LIMIT_CLAUSE);
	}

	public static String formatDynamicAttributeAlias(final Integer variableId) {
		return String.format("%s_%s", ATTRIBUTE, variableId);
	}

	private static String formatLotAttributeJoin(final String alias, final Integer variableId) {
		return String.format(" LEFT JOIN ims_lot_attribute %1$s ON lot.lotid = %1$s.lotid AND %1$s.atype = %2$s", alias, variableId);
	}

}
