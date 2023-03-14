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

package org.generationcp.middleware.dao.ims;

import com.google.common.base.Joiner;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.api.inventory.study.StudyTransactionsDto;
import org.generationcp.middleware.api.inventory.study.StudyTransactionsRequest;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.inventory.manager.TransactionDto;
import org.generationcp.middleware.domain.inventory.manager.TransactionsSearchDto;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.ims.ExperimentTransaction;
import org.generationcp.middleware.pojos.ims.ExperimentTransactionType;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.util.SqlQueryParamBuilder;
import org.generationcp.middleware.util.Util;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanConstructorResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DAO class for {@link Transaction}.
 *
 */
public class TransactionDAO extends GenericDAO<Transaction, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(TransactionDAO.class);
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	public TransactionDAO(final Session session) {
		super(session);
	}

	public void cancelUnconfirmedTransactionsForListEntries(final List<Integer> listEntryIds) {
		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();

			final String sql =
					"UPDATE ims_transaction " + "SET trnstat = 9, " + "trndate = :currentDate "
							+ "WHERE trnstat = 0 AND recordid IN (:entryIds) " + "AND sourceType = 'LIST'";
			final Query query =
					this.getSession().createSQLQuery(sql).setParameter("currentDate", Util.getCurrentDate())
							.setParameterList("entryIds", listEntryIds);
			query.executeUpdate();
		} catch (final Exception e) {
			final String message = "Error cancelUnconfirmedTransactionsForListEntries=" + listEntryIds + " query from Transaction: " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public Map<Integer, String> retrieveStockIds(final List<Integer> gIds) {

		final Map<Integer, String> gIdStockIdMap = new HashMap<>();

		if (gIds.isEmpty()) {
			return gIdStockIdMap;
		}

		final String sql =
				"SELECT b.eid ,group_concat(distinct b.stock_id SEPARATOR ', ')  " + "FROM "
						+ "ims_lot b  "
						+ "left JOIN ims_transaction c ON b.lotid = c.lotid  "
						+ "WHERE cast(b.eid as UNSIGNED) in (:gIds) GROUP BY b.eid";

		final Query query = this.getSession().createSQLQuery(sql).setParameterList("gIds", gIds);

		final List<Object[]> result = query.list();
		for (final Object[] row : result) {
			final Integer gid = (Integer) row[0];
			final String stockIds = (String) row[1];

			gIdStockIdMap.put(gid, stockIds);
		}
		return gIdStockIdMap;

	}

	//New inventory functions, please locate them below this line to help cleaning in the near future.
	private static final String SEARCH_TRANSACTIONS_QUERY = "SELECT " //
		+ "    tr.trnid AS transactionId,"//
		+ "    users.uname AS createdByUsername,"//
		+ "(CASE WHEN trntype = " + TransactionType.DEPOSIT.getId() + " THEN '" + TransactionType.DEPOSIT.getValue()
		+ "' WHEN trntype = " + TransactionType.WITHDRAWAL.getId() + "  THEN '" + TransactionType.WITHDRAWAL.getValue()
		+ "' WHEN trntype = " + TransactionType.DISCARD.getId() + "  THEN '" + TransactionType.DISCARD.getValue()
		+ "' WHEN trntype = " + TransactionType.ADJUSTMENT.getId() + "  THEN '" + TransactionType.ADJUSTMENT.getValue()
		+ "' END) AS transactionType,"//
		+ "    tr.trnqty AS amount,"//
		+ "(SELECT SUM(CASE WHEN transaction.trnstat = " + TransactionStatus.CONFIRMED.getIntValue()
		+ " OR (transaction.trnstat = " + TransactionStatus.PENDING.getIntValue() + " AND transaction.trntype = " + TransactionType.WITHDRAWAL.getId() + ") "
		+ "  THEN transaction.trnqty ELSE 0 END) FROM ims_transaction transaction WHERE  transaction.lotid = lot.lotid ) AS availableBalance, " //
		+ "    tr.comments AS notes,"//
		+ "    tr.trndate as createdDate, "//
		+ "    lot.lotid AS lotLotId," //
		+ "    lot.lot_uuid AS lotUUID," //
		+ "    lot.eid AS lotGid,"//
		+ "    n.nval AS lotDesignation,"//
		+ "    lot.stock_id AS lotStockId,"//
		+ "    scaleid AS lotScaleId,"//
		+ "    scale.name AS lotUnitName," //
		+ "    (CASE WHEN lot.status = 0 THEN 'Active' WHEN lot.status = 1 THEN 'Closed' END) AS lotStatus, "
		+ "   (CASE WHEN trnstat = " + TransactionStatus.PENDING.getIntValue() + " THEN '" + TransactionStatus.PENDING.getValue()
		+ "' WHEN trnstat = " + TransactionStatus.CONFIRMED.getIntValue() + " THEN '" + TransactionStatus.CONFIRMED.getValue()
		+ "' WHEN trnstat = " + TransactionStatus.CANCELLED.getIntValue() + " THEN '" + TransactionStatus.CANCELLED.getValue()
		+ "' END) as transactionStatus, "
		+ " lot.locid as lotLocationId, "
		+ " loc.lname as lotLocationName, "//
		+ " loc.labbr as lotLocationAbbr, "//
		+ " lot.comments as lotComments, "//
		+ " g.germplsm_uuid as gerplsmUUID "
		+ " FROM"//
		+ "   ims_transaction tr "//
		+ "        INNER JOIN"//
		+ "    ims_lot lot ON tr.lotid = lot.lotid "//
		+ "		   LEFT JOIN" //
		+ "	   location loc on loc.locid = lot.locid "//
		+ "        LEFT JOIN"//
		+ "    cvterm scale ON scale.cvterm_id = lot.scaleid"//
		+ "        INNER JOIN"//
		+ "    germplsm g ON g.gid = lot.eid"//
		+ "        LEFT JOIN"//
		+ "    names n ON n.gid = lot.eid AND n.nstat = 1"//
		+ "        LEFT JOIN"//
		+ "    workbench.users users ON users.userid = tr.userid"//
		+ " WHERE"//
		+ "    lot.etype = 'GERMPLSM' and g.deleted=0 "; //

	private static void addSearchTransactionsFilters(
		final SqlQueryParamBuilder paramBuilder,
		final TransactionsSearchDto transactionsSearchDto) {

		if (transactionsSearchDto != null) {
			final List<Integer> lotIds = transactionsSearchDto.getLotIds();
			if (lotIds != null && !lotIds.isEmpty()) {
				paramBuilder.append(" and lot.lotid IN (:lotIds)");
				paramBuilder.setParameterList("lotIds", lotIds);
			}

			final String lotLocationAbbr = transactionsSearchDto.getLotLocationAbbr();
			if (lotLocationAbbr != null) {
				paramBuilder.append(" and loc.labbr like :lotLocationAbbr");
				paramBuilder.setParameter("lotLocationAbbr", '%' + lotLocationAbbr + '%');
			}

			final List<String> lotUUIDs = transactionsSearchDto.getLotUUIDs();
			if (lotUUIDs != null && !lotUUIDs.isEmpty()) {
				paramBuilder.append(" and lot.lot_uuid IN (:lotUUIDs)");
				paramBuilder.setParameterList("lotUUIDs", lotUUIDs);
			}

			final List<Integer> transactionIds = transactionsSearchDto.getTransactionIds();
			if (transactionIds != null && !transactionIds.isEmpty()) {
				paramBuilder.append(" and trnid IN (:transactionIds)");
				paramBuilder.setParameterList("transactionIds", transactionIds);
			}

			final List<Integer> gids = transactionsSearchDto.getGids();
			if (gids != null && !gids.isEmpty()) {
				paramBuilder.append(" and lot.eid IN (:gids)");
				paramBuilder.setParameterList("gids", gids);
			}

			final List<Integer> unitIds = transactionsSearchDto.getUnitIds();
			if (unitIds != null && !unitIds.isEmpty()) {
				paramBuilder.append(" and lot.scaleid IN (:unitIds)");
				paramBuilder.setParameterList("unitIds", unitIds);
			}

			final String designation = transactionsSearchDto.getDesignation();
			if (designation != null) {
				paramBuilder.append(" and n.nval like :designation");
				paramBuilder.setParameter("designation", '%' + designation + '%');
			}

			final String stockId = transactionsSearchDto.getStockId();
			if (stockId != null) {
				paramBuilder.append(" and lot.stock_id like :stockId");
				paramBuilder.setParameter("stockId", stockId + '%');
			}

			final String notes = transactionsSearchDto.getNotes();
			if (notes != null) {
				paramBuilder.append(" and tr.comments like :notes");
				paramBuilder.setParameter("notes", '%' + notes + '%');
			}

			final Date createdDateFrom = transactionsSearchDto.getCreatedDateFrom();
			if (createdDateFrom != null) {
				paramBuilder.append(" and DATE(tr.trndate) >= :createdDateFrom");
				paramBuilder.setParameter("createdDateFrom", DATE_FORMAT.format(createdDateFrom));
			}

			final Date createdDateTo = transactionsSearchDto.getCreatedDateTo();
			if (createdDateTo != null) {
				paramBuilder.append(" and DATE(tr.trndate) <= :createdDateTo");
				paramBuilder.setParameter("createdDateTo", DATE_FORMAT.format(createdDateTo));
			}

			final String createdByUsername = transactionsSearchDto.getCreatedByUsername();
			if (createdByUsername != null) {
				paramBuilder.append(" and users.uname like :createdByUsername");
				paramBuilder.setParameter("createdByUsername", '%' + createdByUsername + '%');
			}

			final Double minAmount = transactionsSearchDto.getMinAmount();
			if (minAmount != null) {
				paramBuilder.append(" and tr.trnqty >= :minAmount");
				paramBuilder.setParameter("minAmount", minAmount);
			}

			final Double maxAmount = transactionsSearchDto.getMaxAmount();
			if (maxAmount != null) {
				paramBuilder.append(" and tr.trnqty <= :maxAmount");
				paramBuilder.setParameter("maxAmount", maxAmount);
			}

			final List<Integer> transactionTypes = transactionsSearchDto.getTransactionTypes();
			if (transactionTypes != null && !transactionTypes.isEmpty()) {
				paramBuilder.append(" and trntype IN (:transactionTypes)");
				paramBuilder.setParameterList("transactionTypes", transactionTypes);
			}

			final List<Integer> transactionStatus = transactionsSearchDto.getTransactionStatus();
			if (transactionStatus != null && !transactionStatus.isEmpty()) {
				paramBuilder.append(" and trnstat IN (:transactionStatus)");
				paramBuilder.setParameterList("transactionStatus", transactionStatus);
			}

			final List<Integer> statusIds = transactionsSearchDto.getStatusIds();
			if (statusIds != null && !statusIds.isEmpty()) {
				paramBuilder.append(" and tr.trnstat IN (:statusIds)");
				paramBuilder.setParameterList("statusIds", statusIds);
			}

			final Integer lotStatus = transactionsSearchDto.getLotStatus();
			if (lotStatus != null) {
				paramBuilder.append(" and lot.status = :lotStatus");
				paramBuilder.setParameter("lotStatus", lotStatus);
			}

			final List<Integer> germplasmListIds = transactionsSearchDto.getGermplasmListIds();
			if (germplasmListIds != null && !germplasmListIds.isEmpty()) {
				paramBuilder.append(" and lot.eid in (select distinct (gid) from listdata where listid in (:germplasmListIds))"
					+ " and lot.etype = 'GERMPLSM' ");
				paramBuilder.setParameterList("germplasmListIds", germplasmListIds);
			}

			final List<Integer> plantingStudyIds = transactionsSearchDto.getPlantingStudyIds();
			if (plantingStudyIds != null && !plantingStudyIds.isEmpty()) {
				paramBuilder.append(" and exists(select 1 \n" //
					+ " from project study_filter_p \n" //
					+ "	    inner join project study_filter_plotdata on study_filter_p.project_id = study_filter_plotdata.study_id \n" //
					+ "     inner join nd_experiment study_filter_nde on study_filter_plotdata.project_id = study_filter_nde.project_id \n"
					+ "     inner join ims_experiment_transaction study_filter_iet on study_filter_nde.nd_experiment_id = study_filter_iet.nd_experiment_id \n"
					+ "                and study_filter_iet.type = " + ExperimentTransactionType.PLANTING.getId()
					+ " where study_filter_p.project_id in (:plantingStudyIds) and study_filter_iet.trnid = tr.trnid)"); //
				paramBuilder.setParameterList("plantingStudyIds", plantingStudyIds);
			}

			final List<Integer> harvestingStudyIds = transactionsSearchDto.getHarvestingStudyIds();
			if (harvestingStudyIds != null && !harvestingStudyIds.isEmpty()) {
				paramBuilder.append(" and exists(select 1 \n" //
					+ " from project study_filter_p \n" //
					+ "	    inner join project study_filter_plotdata on study_filter_p.project_id = study_filter_plotdata.study_id \n" //
					+ "     inner join nd_experiment study_filter_nde on study_filter_plotdata.project_id = study_filter_nde.project_id \n"
					+ "     inner join ims_experiment_transaction study_filter_iet on study_filter_nde.nd_experiment_id = study_filter_iet.nd_experiment_id \n"
					+ "                and study_filter_iet.type = " + ExperimentTransactionType.HARVESTING.getId()
					+ " where study_filter_p.project_id in (:harvestingStudyIds) and study_filter_iet.trnid = tr.trnid)"); //
				paramBuilder.setParameterList("harvestingStudyIds", harvestingStudyIds);
			}

			if (!CollectionUtils.isEmpty(transactionsSearchDto.getGermplasmUUIDs())) {
				paramBuilder.append(" and g.germplsm_uuid IN (:guids)");
				paramBuilder.setParameterList("guids", transactionsSearchDto.getGermplasmUUIDs());
			}

			if (!CollectionUtils.isEmpty(transactionsSearchDto.getObservationUnitIds())) {
				paramBuilder.append(" and exists (select 1 "
					+ " from nd_experiment nde "
					+ "		inner join ims_experiment_transaction ims_et on nde.nd_experiment_id = ims_et.nd_experiment_id "
					+ " where nde.obs_unit_id in (:observationUnitIds) and ims_et.trnid = tr.trnid)");
				paramBuilder.setParameterList("observationUnitIds", transactionsSearchDto.getObservationUnitIds());
			}
		}
	}

	private static void addSortToSearchTransactionsQuery(final StringBuilder transactionsSearchQuery, final Pageable pageable) {
		if (pageable != null) {
			if (pageable.getSort() != null) {
				final List<String> sorts = new ArrayList<>();
				for (final Sort.Order order : pageable.getSort()) {
					sorts.add(order.getProperty().replace(".", "") + " " + order.getDirection().toString());
				}
				if (!sorts.isEmpty()) {
					transactionsSearchQuery.append(" ORDER BY ").append(Joiner.on(",").join(sorts));
				}
			} else {
				transactionsSearchQuery.append(" ORDER BY lotLotId");
			}
		}
	}

	public List<TransactionDto> searchTransactions(final TransactionsSearchDto transactionsSearchDto, final Pageable pageable) {
		try {
			final StringBuilder filterTransactionsQuery = new StringBuilder(SEARCH_TRANSACTIONS_QUERY);
			addSearchTransactionsFilters(new SqlQueryParamBuilder(filterTransactionsQuery), transactionsSearchDto);
			addSortToSearchTransactionsQuery(filterTransactionsQuery, pageable);

			final SQLQuery query = this.getSession().createSQLQuery(filterTransactionsQuery.toString());
			addSearchTransactionsFilters(new SqlQueryParamBuilder(query), transactionsSearchDto);
			this.addSearchTransactionsQueryScalars(query);

			query.setResultTransformer(new AliasToBeanConstructorResultTransformer(this.getTransactionDtoConstructor()));

			GenericDAO.addPaginationToSQLQuery(query, pageable);

			final List<TransactionDto> transactionDtos = query.list();

			return transactionDtos;
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at searchTransactions() query on TransactionDAO: " + e.getMessage(), e);
		}

	}

	public long countSearchTransactions(final TransactionsSearchDto transactionsSearchDto) {
		try {
			final StringBuilder filterTransactionsQuery = new StringBuilder(SEARCH_TRANSACTIONS_QUERY);
			addSearchTransactionsFilters(new SqlQueryParamBuilder(filterTransactionsQuery), transactionsSearchDto);
			final String countTransactionsQuery =
				"Select count(1) from (" + filterTransactionsQuery.toString() + ") as filteredTransactions";
			final SQLQuery query = this.getSession().createSQLQuery(countTransactionsQuery.toString());
			addSearchTransactionsFilters(new SqlQueryParamBuilder(query), transactionsSearchDto);
			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at countTransactionsQuery() query on TransactionDAO: " + e.getMessage(), e);
		}
	}

	public List<TransactionDto> getAvailableBalanceTransactions(final Integer lotId) {
		try {

			if (lotId != null) {
				final StringBuilder sql = new StringBuilder(SEARCH_TRANSACTIONS_QUERY);
				sql.append(" and (tr.trnstat =").append(TransactionStatus.CONFIRMED.getIntValue()).append(" or (tr.trnstat = ")
					.append(TransactionStatus.PENDING.getIntValue()).
					append(" and tr.trntype = ").append(TransactionType.WITHDRAWAL.getId()).append(")) ");
				// FIXME
				sql.append(" and tr.lotid = ").append(lotId).append(" ");
				final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
				this.addSearchTransactionsQueryScalars(query);

				query.setResultTransformer(new AliasToBeanConstructorResultTransformer(this.getTransactionDtoConstructor()));

				final List<TransactionDto> transactionDtos = query.list();

				return transactionDtos;
			} else {
				return new ArrayList<>();
			}

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at getAvailableBalanceTransactions() query on TransactionDAO: " + e.getMessage(), e);
		}
	}

	public List<Transaction> getByIds(final Set<Integer> transactionIds) {
		final List<Transaction> transactions = new ArrayList<>();

		if (transactionIds == null || transactionIds.isEmpty()) {
			return transactions;
		}

		try {
			final Criteria criteria = this.getSession().createCriteria(Transaction.class);
			criteria.add(Restrictions.in("id", transactionIds));
			return criteria.list();
		} catch (final HibernateException e) {
			final String message = "Error getByIds() query from Transaction: " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}

	}

	private Constructor<TransactionDto> getTransactionDtoConstructor() {
		try {
			return TransactionDto.class.getConstructor(
				Integer.class, // transactionId
				String.class,    // createdByUsername
				String.class,    // transactionType
				Double.class,    // amount
				Double.class,	 // available Balance
				String.class,    // notes
				Date.class,      // createdDate
				Integer.class,   // lotId
				String.class,    // lotUUID
				Integer.class,   // gid
				String.class,    // designation
				String.class,    // stockId
				Integer.class,   // scaleId
				String.class,    // scaleName
				String.class,    // lotStatus
				String.class,    // transactionStatus
				Integer.class,   // locationId
				String.class,    // locationName
				String.class,    // locationAbbr
				String.class,    // comments
				String.class     // germplsmUUID
			);
		} catch (final NoSuchMethodException ex) {
			throw new RuntimeException(ex);
		}
	}

	private Constructor<StudyTransactionsDto> getStudyTransactionsDtoConstructor() {
		try {
			return StudyTransactionsDto.class.getConstructor(
				Integer.class, // transactionId
				String.class,    // createdByUsername
				String.class,    // transactionType
				Double.class,    // amount
				Double.class,	 // available Balance
				String.class,    // notes
				Date.class,      // createdDate
				Integer.class,   // lotId
				String.class,    // lotUUID
				Integer.class,   // gid
				String.class,    // designation
				String.class,    // stockId
				Integer.class,   // scaleId
				String.class,    // scaleName
				String.class,    // lotStatus
				String.class,    // transactionStatus
				Integer.class,   // locationId
				String.class,    // locationName
				String.class,    // locationAbbr
				String.class,    // comments
				String.class	 // germplsmUUID
			);
		} catch (final NoSuchMethodException ex) {
			throw new RuntimeException(ex);
		}
	}

	private void addSearchTransactionsQueryScalars(final SQLQuery query) {
		query.addScalar("transactionId");
		query.addScalar("createdByUsername");
		query.addScalar("transactionType");
		query.addScalar("amount");
		query.addScalar("availableBalance");
		query.addScalar("notes");
		query.addScalar("createdDate", DateType.INSTANCE);
		query.addScalar("lotLotId");
		query.addScalar("lotUUID");
		query.addScalar("lotGid");
		query.addScalar("lotDesignation");
		query.addScalar("lotStockId");
		query.addScalar("lotScaleId");
		query.addScalar("lotUnitName");
		query.addScalar("lotStatus");
		query.addScalar("transactionStatus");
		query.addScalar("lotLocationId");
		query.addScalar("lotLocationName");
		query.addScalar("lotLocationAbbr");
		query.addScalar("lotComments");
		query.addScalar("gerplsmUUID");
	}


	public Transaction update(final Transaction transaction) {
		super.update(transaction);
		this.getSession().flush();
		return transaction;
	}

	public long countStudyTransactions(final Integer studyId, final StudyTransactionsRequest studyTransactionsRequest) {
		TransactionsSearchDto transactionsSearch = null;
		if (studyTransactionsRequest != null) {
			transactionsSearch = studyTransactionsRequest.getTransactionsSearch();
		}

		final StringBuilder obsUnitsQueryFilterSql = this.buildObsUnitsQuery();
		addObsUnitFilters(new SqlQueryParamBuilder(obsUnitsQueryFilterSql), studyTransactionsRequest);
		final StringBuilder transactionsQuerySql = this.buildStudyTransactionsQuery(transactionsSearch, obsUnitsQueryFilterSql);

		final SQLQuery transactionsQuery =
			this.getSession().createSQLQuery("select count(1) from ( " + transactionsQuerySql.toString() + ") T");
		transactionsQuery.setParameter("studyId", studyId);
		final SqlQueryParamBuilder paramBuilder = new SqlQueryParamBuilder(transactionsQuery);
		addSearchTransactionsFilters(paramBuilder, transactionsSearch);
		addObsUnitFilters(paramBuilder, studyTransactionsRequest);
		this.excludeCancelledTransactions(paramBuilder);
		return ((BigInteger) transactionsQuery.uniqueResult()).longValue();
	}

	private void excludeCancelledTransactions(final SqlQueryParamBuilder paramBuilder) {
		// Exclude "Cancelled" study transactions from result
		paramBuilder.append(" and trnstat != :cancelledStatus ");
		paramBuilder.setParameter("cancelledStatus", TransactionStatus.CANCELLED.getIntValue());
	}

	public List<StudyTransactionsDto> searchStudyTransactions(
		final Integer studyId,
		final StudyTransactionsRequest studyTransactionsRequest, final Pageable pageable) {

		final TransactionsSearchDto transactionsSearch = studyTransactionsRequest.getTransactionsSearch();

		final StringBuilder obsUnitsQueryFilterSql = this.buildObsUnitsQuery();
		addObsUnitFilters(new SqlQueryParamBuilder(obsUnitsQueryFilterSql), studyTransactionsRequest);

		final StringBuilder transactionsQuerySql = this.buildStudyTransactionsQuery(transactionsSearch, obsUnitsQueryFilterSql);
		addPageRequestOrderBy(transactionsQuerySql, pageable);

		// transactions data
		final SQLQuery transactionsQuery = this.getSession().createSQLQuery(transactionsQuerySql.toString());
		transactionsQuery.setParameter("studyId", studyId);
		final SqlQueryParamBuilder paramBuilder = new SqlQueryParamBuilder(transactionsQuery);
		addSearchTransactionsFilters(paramBuilder, transactionsSearch);
		addObsUnitFilters(paramBuilder, studyTransactionsRequest);
		this.excludeCancelledTransactions(paramBuilder);
		addPaginationToSQLQuery(transactionsQuery, pageable);
		this.addSearchTransactionsQueryScalars(transactionsQuery);
		transactionsQuery.setResultTransformer(new AliasToBeanConstructorResultTransformer(this.getStudyTransactionsDtoConstructor()));
		final List<StudyTransactionsDto> transactions = transactionsQuery.list();

		// obs units data
		final StringBuilder obsUnitsQuerySql = this.buildObsUnitsQuery();
		final SQLQuery obsUnitsQuery = this.getSession().createSQLQuery(obsUnitsQuerySql.toString());
		obsUnitsQuery.setParameter("studyId", studyId);
		obsUnitsQuery.addScalar("ndExperimentId").addScalar("transactionId").addScalar("instanceNo", new IntegerType())
			.addScalar("entryType").addScalar("entryNo", new IntegerType()).addScalar("repNo", new IntegerType())
			.addScalar("blockNo", new IntegerType()).addScalar("plotNo", new IntegerType()).addScalar("obsUnitId");
		obsUnitsQuery.setResultTransformer(Transformers.aliasToBean(StudyTransactionsDto.ObservationUnitDto.class));
		final List<StudyTransactionsDto.ObservationUnitDto> obsUnits = obsUnitsQuery.list();

		// mapping

		final Map<Integer, List<StudyTransactionsDto.ObservationUnitDto>> obsUnitsByTransactionId =
			obsUnits.stream().collect(Collectors.groupingBy(StudyTransactionsDto.ObservationUnitDto::getTransactionId,
				LinkedHashMap::new, Collectors.toList()));

		for (final StudyTransactionsDto transaction : transactions) {
			if (obsUnitsByTransactionId.get(transaction.getTransactionId()) != null) {
				transaction.setObservationUnits(obsUnitsByTransactionId.get(transaction.getTransactionId()));
			}
		}

		return transactions;
	}

	public StudyTransactionsDto getStudyTransactionByTransactionId(final Integer transactionId) {

		final StringBuilder sql = new StringBuilder(SEARCH_TRANSACTIONS_QUERY);
		sql.append(" and tr.trnid = ").append(transactionId);
		final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
		this.addSearchTransactionsQueryScalars(query);
		query.setResultTransformer(new AliasToBeanConstructorResultTransformer(this.getStudyTransactionsDtoConstructor()));

		final StudyTransactionsDto studyTransactionsDto = (StudyTransactionsDto) query.uniqueResult();

		final Criteria criteria = this.getSession().createCriteria(ExperimentTransaction.class);
		criteria.add(Restrictions.eq("transaction.id", transactionId));
		final List<ExperimentTransaction> result = criteria.list();
		if (CollectionUtils.isEmpty(result)) {
			return studyTransactionsDto;
		}

		// We take the first value to get the experiment type because all of the experiment transaction are grouped by type
		final ExperimentTransactionType experimentTransactionType = ExperimentTransactionType.getById(result.get(0).getType());
		studyTransactionsDto.setExperimentTransactionType(experimentTransactionType);

		final List<StudyTransactionsDto.ObservationUnitDto> observationUnitDtos = result.stream().map(experimentTransaction -> {
			final Transaction transaction = experimentTransaction.getTransaction();
			final ExperimentModel experiment = experimentTransaction.getExperiment();
			final StudyTransactionsDto.ObservationUnitDto observationUnitDto = new StudyTransactionsDto.ObservationUnitDto();
			observationUnitDto.setNdExperimentId(experiment.getNdExperimentId());
			observationUnitDto.setTransactionId(transaction.getId());
			observationUnitDto.setObsUnitId(experiment.getObsUnitId());

			final DmsProject study = experiment.getProject().getStudy();
			observationUnitDto.setStudyId(study.getProjectId());
			observationUnitDto.setStudyName(study.getName());
			return observationUnitDto;
		}).collect(Collectors.toList());

		studyTransactionsDto.setObservationUnits(observationUnitDtos);
		return studyTransactionsDto;
	}

	private StringBuilder buildStudyTransactionsQuery(
		final TransactionsSearchDto transactionsSearchDto,
		final StringBuilder obsUnitsQuerySql) {

		final StringBuilder searchTransactionsQuery = new StringBuilder(SEARCH_TRANSACTIONS_QUERY);
		final SqlQueryParamBuilder paramBuilder = new SqlQueryParamBuilder(searchTransactionsQuery);
		addSearchTransactionsFilters(paramBuilder, transactionsSearchDto);
		this.excludeCancelledTransactions(paramBuilder);
		return new StringBuilder(""  //
				+ " select SEARCH_TRANSACTIONS_QUERY.* " //
				+ " from ( " //
				+ 		searchTransactionsQuery.toString() //
				+ " ) SEARCH_TRANSACTIONS_QUERY inner join ( " //
				+ 		obsUnitsQuerySql //
				+ " ) OBS_UNITS_QUERY on SEARCH_TRANSACTIONS_QUERY.transactionId = OBS_UNITS_QUERY.transactionId " //
				+ " group by SEARCH_TRANSACTIONS_QUERY.transactionId ");
	}

	private StringBuilder buildObsUnitsQuery() {
		return new StringBuilder("" //
			+ "     select iet.nd_experiment_id as ndExperimentId, " //
			+ "         iet.trnid as transactionId, " //
			+ "         cast(ndgeo.description as unsigned) as instanceNo, " //
			+ "         (SELECT iispcvt.definition FROM stockprop isp INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = isp.type_id INNER JOIN cvterm iispcvt ON iispcvt.cvterm_id = isp.cvalue_id WHERE isp.stock_id = s.stock_id AND ispcvt.name = 'ENTRY_TYPE') AS entryType, "  //
			+ "         (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = plot.nd_experiment_id AND ispcvt.name = 'REP_NO') AS repNo, "  //
			+ "         (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = plot.nd_experiment_id AND ispcvt.name = 'BLOCK_NO') AS blockNo, "  //
			+ "         cast(s.uniquename as unsigned) as entryNo, " //
			+ "         cast((SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = plot.nd_experiment_id AND ispcvt.name = 'PLOT_NO') as unsigned) AS plotNo, "  //
			+ "         plot.obs_unit_id as obsUnitId " //
			+ "     from ims_experiment_transaction iet " //
			+ "              inner join nd_experiment plot on iet.nd_experiment_id = plot.nd_experiment_id " //
			+ "              inner join nd_geolocation ndgeo on plot.nd_geolocation_id = ndgeo.nd_geolocation_id " //
			+ "              inner join project plotdata on plot.project_id = plotdata.project_id "
			+ "              inner join project study on plotdata.study_id = study.project_id "
			+ "	             inner join stock s on s.stock_id = plot.stock_id " //
			+ "     where study.project_id = :studyId"
			+ "     having 1 = 1 ");
	}

	/**
	 * Filter obs units following the inventory filters convention, using list of ids for numeric values
	 */
	private static void addObsUnitFilters(final SqlQueryParamBuilder paramBuilder,
		final StudyTransactionsRequest studyTransactionsRequest) {

		if (studyTransactionsRequest == null) {
			return;
		}

		final List<Integer> instanceNoList = studyTransactionsRequest.getInstanceNoList();
		if (instanceNoList != null && !instanceNoList.isEmpty()) {
			paramBuilder.append(" and instanceNo in (:instanceNoList) ");
			paramBuilder.setParameterList("instanceNoList", instanceNoList);
		}

		final List<Integer> plotNoList = studyTransactionsRequest.getPlotNoList();
		if (plotNoList != null && !plotNoList.isEmpty()) {
			paramBuilder.append(" and plotNo in (:plotNoList) ");
			paramBuilder.setParameterList("plotNoList", plotNoList);
		}

		final List<Integer> entryNoList = studyTransactionsRequest.getEntryNoList();
		if (entryNoList != null && !entryNoList.isEmpty()) {
			paramBuilder.append(" and entryNo in (:entryNoList) ");
			paramBuilder.setParameterList("entryNoList", entryNoList);
		}

		final String entryType = studyTransactionsRequest.getEntryType();
		if (!StringUtils.isBlank(entryType)) {
			paramBuilder.append(" and entryType like :entryType ");
			paramBuilder.setParameter("entryType", "%" + entryType + "%");
		}

		final List<Integer> observationUnitIds = studyTransactionsRequest.getObservationUnitIds();
		if (observationUnitIds != null && !observationUnitIds.isEmpty()) {
			paramBuilder.append(" and ndExperimentId in (:observationUnitList) ");
			paramBuilder.setParameterList("observationUnitList", observationUnitIds);
		}
	}
}
