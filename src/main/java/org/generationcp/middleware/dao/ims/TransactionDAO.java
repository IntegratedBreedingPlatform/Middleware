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
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.domain.inventory.manager.TransactionDto;
import org.generationcp.middleware.domain.inventory.manager.TransactionsSearchDto;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.ims.LotStatus;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.pojos.report.TransactionReportRow;
import org.generationcp.middleware.util.Util;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanConstructorResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO class for {@link Transaction}.
 *
 */
public class TransactionDAO extends GenericDAO<Transaction, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(TransactionDAO.class);
	private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

	@SuppressWarnings("unchecked")
	public List<Transaction> getAllReserve(final int start, final int numOfRows) {
		try {
			final Criteria criteria = this.getSession().createCriteria(Transaction.class);
			criteria.add(Restrictions.eq("status", 0));
			criteria.add(Restrictions.lt("quantity", 0d));
			criteria.setFirstResult(start);
			criteria.setMaxResults(numOfRows);
			return criteria.list();
		} catch (final HibernateException e) {
			final String message = "Error with getAllReserve() query from Transaction: " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public long countAllReserve() {
		try {
			final Criteria criteria = this.getSession().createCriteria(Transaction.class);
			criteria.setProjection(Projections.rowCount());
			criteria.add(Restrictions.eq("status", 0));
			criteria.add(Restrictions.lt("quantity", 0d));
			return ((Long) criteria.uniqueResult());
		} catch (final HibernateException e) {
			final String message = "Error with countAllReserve query from Transaction: " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Transaction> getAllDeposit(final int start, final int numOfRows) {
		try {
			final Criteria criteria = this.getSession().createCriteria(Transaction.class);
			criteria.add(Restrictions.eq("status", 0));
			criteria.add(Restrictions.gt("quantity", 0d));
			criteria.setFirstResult(start);
			criteria.setMaxResults(numOfRows);
			return criteria.list();
		} catch (final HibernateException e) {
			final String message = "Error with getAllDeposit query from Transaction: " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public long countAllDeposit() {
		try {
			final Criteria criteria = this.getSession().createCriteria(Transaction.class);
			criteria.setProjection(Projections.rowCount());
			criteria.add(Restrictions.eq("status", 0));
			criteria.add(Restrictions.gt("quantity", 0d));
			return ((Long) criteria.uniqueResult());
		} catch (final HibernateException e) {
			final String message = "Error with countAllDeposit() query from Transaction: " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Transaction> getAllUncommitted(final int start, final int numOfRows) {
		try {
			final Criteria criteria = this.getSession().createCriteria(Transaction.class);
			criteria.add(Restrictions.eq("status", 0));
			criteria.setFirstResult(start);
			criteria.setMaxResults(numOfRows);
			return criteria.list();
		} catch (final HibernateException e) {
			final String message = "Error with getAllUncomitted() query from Transaction: " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public long countAllUncommitted() {
		try {
			final Criteria criteria = this.getSession().createCriteria(Transaction.class);
			criteria.setProjection(Projections.rowCount());
			criteria.add(Restrictions.eq("status", 0));
			return ((Long) criteria.uniqueResult());
		} catch (final HibernateException e) {
			final String message = "Error with countAllUncommitted() query from Transaction: " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Transaction> getAllWithdrawals(final int start, final int numOfRows) {
		try {
			final Criteria criteria = this.getSession().createCriteria(Transaction.class);
			criteria.add(Restrictions.lt("quantity", 0d));
			criteria.setFirstResult(start);
			criteria.setMaxResults(numOfRows);
			return criteria.list();
		} catch (final HibernateException e) {
			final String message = "Error with getAllWithdrawals() query from Transaction: " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public long countAllWithdrawals() {
		try {
			final Criteria criteria = this.getSession().createCriteria(Transaction.class);
			criteria.setProjection(Projections.rowCount());
			criteria.add(Restrictions.lt("quantity", 0d));
			return ((Long) criteria.uniqueResult());
		} catch (final HibernateException e) {
			final String message = "Error with countAllWithdrawals() query from Transaction: " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Transaction> getEmptyLot(final int start, final int numOfRows) {
		try {
			final Query query = this.getSession().getNamedQuery(Transaction.GET_EMPTY_LOT);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);
			return query.list();
		} catch (final HibernateException e) {
			final String message = "Error with getEmptyLot() query from Transaction: " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Transaction> getLotWithMinimumAmount(final double minAmount, final int start, final int numOfRows) {
		try {
			final Query query = this.getSession().getNamedQuery(Transaction.GET_LOT_WITH_MINIMUM_AMOUNT);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);
			query.setParameter("minAmount", minAmount);
			return query.list();
		} catch (final HibernateException e) {
			final String message = "Error with getLotWithMinimumAmount(minAmount=\" + minAmount + \") query from Transaction: " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public List<InventoryDetails> getInventoryDetailsByTransactionRecordId(final List<Integer> recordIds) {
		final List<InventoryDetails> detailsList = new ArrayList<>();

		if (recordIds == null || recordIds.isEmpty()) {
			return detailsList;
		}

		try {
			final Session session = this.getSession();

			final StringBuilder sql =
					new StringBuilder().append("SELECT lot.lotid, lot.userid, lot.eid, lot.locid, lot.scaleid, ")
							.append("tran.sourceid, tran.trnqty, lot.stock_id, lot.comments, tran.recordid ")
							.append("FROM ims_transaction tran ").append("LEFT JOIN ims_lot lot ON lot.lotid = tran.lotid ")
							.append("WHERE lot.status = ").append(LotStatus.ACTIVE.getIntValue())
							.append("		 AND tran.recordid IN (:recordIds) ");
			final SQLQuery query = session.createSQLQuery(sql.toString());
			query.setParameterList("recordIds", recordIds);

			final List<Object[]> results = query.list();

			if (!results.isEmpty()) {
				for (final Object[] row : results) {
					final Integer lotId = (Integer) row[0];
					final Integer userId = (Integer) row[1];
					final Integer gid = (Integer) row[2];
					final Integer locationId = (Integer) row[3];
					final Integer scaleId = (Integer) row[4];
					final Integer sourceId = (Integer) row[5];
					final Double amount = (Double) row[6];
					final String inventoryID = (String) row[7];
					final String comment = (String) row[8];
					final Integer sourceRecordId = (Integer) row[9];

					final InventoryDetails details =
							new InventoryDetails(gid, null, lotId, locationId, null, userId, amount, sourceId, null, scaleId, null, comment);
					details.setInventoryID(inventoryID);
					details.setSourceRecordId(sourceRecordId);
					detailsList.add(details);
				}
			}

		} catch (final HibernateException e) {
			final String message = "Error with getInventoryDetailsByTransactionRecordId() query from Transaction: " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}

		return detailsList;
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, BigInteger> countLotsWithReservationForListEntries(final List<Integer> listEntryIds) {
		//FIXME delete because the value is never used. This query is wrong, should use gids instead of listEntryIds
		final Map<Integer, BigInteger> lotCounts = new HashMap<>();

		try {
			final String sql =
					"SELECT recordid, count(DISTINCT t.lotid) " + "FROM ims_transaction t " + "INNER JOIN ims_lot l ON l.lotid = t.lotid "
							+ "WHERE trnstat = 0 AND trnqty < 0 AND recordid IN (:entryIds) "
							+ "  AND l.status = 0 AND l.etype = 'GERMPLSM' " + "GROUP BY recordid " + "ORDER BY recordid ";
			final Query query = this.getSession().createSQLQuery(sql).setParameterList("entryIds", listEntryIds);
			final List<Object[]> result = query.list();
			for (final Object[] row : result) {
				final Integer entryId = (Integer) row[0];
				final BigInteger count = (BigInteger) row[1];

				lotCounts.put(entryId, count);
			}

		} catch (final Exception e) {
			final String message = "Error with countLotsWithReservationForListEntries=" + listEntryIds + " query from Transaction: " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}

		return lotCounts;
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, Object[]> retrieveWithdrawalBalanceWithDistinctScale(final List<Integer> listEntryIds) {
		final Map<Integer, Object[]> mapWithdrawalStatusEntryWise = new HashMap<>();

		try {
			final String sql =
					"SELECT recordid, sum(trnqty)*-1 as withdrawal, count(distinct l.scaleid),l.scaleid "
							+ "FROM ims_transaction t " + "INNER JOIN ims_lot l ON l.lotid = t.lotid "
							+ "WHERE trnqty < 0 AND trnstat <> 9 AND recordid IN (:entryIds) "
							+ "  AND l.status = 0 AND l.etype = 'GERMPLSM' " + "GROUP BY recordid " + "ORDER BY recordid ";
			final Query query = this.getSession().createSQLQuery(sql).setParameterList("entryIds", listEntryIds);
			final List<Object[]> result = query.list();
			for (final Object[] row : result) {
				final Integer entryId = (Integer) row[0];
				final Double withdrawalBalance = (Double) row[1];

				final BigInteger distinctWithdrawalScale = (BigInteger) row[2];
				final Integer withdrawalScale = (Integer) row[3];

				mapWithdrawalStatusEntryWise.put(entryId, new Object[] {withdrawalBalance, distinctWithdrawalScale, withdrawalScale});
			}

		} catch (final Exception e) {
			final String message = "Error with retrieveWithdrawalBalanceWithDistinctScale=" + listEntryIds + " query from Transaction: " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}

		return mapWithdrawalStatusEntryWise;
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> retrieveWithdrawalStatus(final Integer sourceId, final List<Integer> listGids) {
		final List<Object[]> listOfTransactionStatusForGermplsm = new ArrayList<>();

		try {
			final String sql =
					"select lot.*,recordid,trnstat  from  (SELECT i.lotid, i.eid FROM ims_lot i "
							+ " LEFT JOIN ims_transaction act ON act.lotid = i.lotid AND act.trnstat <> 9 "
							+ " WHERE i.status = 0 AND i.etype = 'GERMPLSM' AND i.eid  IN (:gIds) GROUP BY i.lotid ) lot "
							+ " LEFT JOIN ims_transaction res ON res.lotid = lot.lotid   AND trnstat in (0,1) AND trnqty < 0 "
							+ " AND sourceid = :sourceid AND sourcetype = 'LIST'  ORDER by lot.eid; ";
			final Query query = this.getSession().createSQLQuery(sql);
			query.setParameterList("gIds", listGids);
			query.setParameter("sourceid", sourceId);

			final List<Object[]> result = query.list();
			for (final Object[] row : result) {

				Integer lotId = null;
				Integer germplsmId = null;
				Integer recordId = null;
				Integer tranStatus = null;

				if(row[0] != null){
					lotId = (Integer) row[0];
				}

				if(row[1] != null){
					germplsmId = (Integer) row[1];
				}
				if(row[2] != null){
					recordId = (Integer) row[2];
				}
				if(row[3] != null){
					tranStatus = (Integer) row[3];
				}

				listOfTransactionStatusForGermplsm.add(new Object[]{ lotId, germplsmId, recordId, tranStatus });
			}

		} catch (final Exception e) {
			final String message = "Error withretrieveWithdrawalStatus=" + listGids + " query from Transaction: " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}

		return listOfTransactionStatusForGermplsm;
	}

	@SuppressWarnings("unchecked")
	public List<Transaction> getByLotIds(final List<Integer> lotIds) {
		final List<Transaction> transactions = new ArrayList<>();

		if (lotIds == null || lotIds.isEmpty()) {
			return transactions;
		}

		try {
			final Criteria criteria = this.getSession().createCriteria(Transaction.class);
			criteria.add(Restrictions.in("lot.id", lotIds));
			return criteria.list();
		} catch (final HibernateException e) {
			final String message = "Error getByLotIds() query from Transaction: " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}

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

	public void cancelReservationsForLotEntryAndLrecId(final Integer lotId, final Integer lrecId) {
		try {
			
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();
			
			final String sql =
					"UPDATE ims_transaction " + "SET trnstat = 9, " + "trndate = :currentDate " + "WHERE trnstat = 0 AND lotId = :lotId "
							+ "AND recordId = :lrecId " + "AND trnqty < 0 " + "AND sourceType = 'LIST'";
			final Query query =
					this.getSession().createSQLQuery(sql).setParameter("currentDate", Util.getCurrentDate())
							.setParameter("lotId", lotId).setParameter("lrecId", lrecId);
			query.executeUpdate();
		} catch (final Exception e) {
			final String message = "Error cancelReservationsForLotEntryAndLrecId(lotId:" + lotId + ", lrecId:" + lrecId
				+ ") query from Transaction: " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public void cancelUnconfirmedTransactionsForGermplasms(final List<Integer> gids) {
		try {
			final String sql =
					"UPDATE ims_transaction " + "SET trnstat = 9, " + "trndate = :currentDate "
							+ "WHERE trnstat = 0 AND sourceType = 'LIST' " + "AND lotid in ( select lotid from ims_lot "
							+ "WHERE status = 0 AND etype = 'GERMPLSM' " + "AND eid in (:gids))";
			final Query query =
					this.getSession().createSQLQuery(sql).setParameter("currentDate", Util.getCurrentDate())
							.setParameterList("gids", gids);
			query.executeUpdate();
		} catch (final Exception e) {
			final String message = "Error cancelUnconfirmedTransactionsForGermplasms=" + gids + ") query from Transaction: " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public Map<Integer, String> retrieveStockIds(final List<Integer> gIds) {

		final Map<Integer, String> gIdStockIdMap = new HashMap<>();

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

	public Boolean isStockIdExists(final List<String> stockIds) {
		final List<String> result = this.getSimilarStockIds(stockIds);
		return null != result && !result.isEmpty();

	}

	public List<String> getSimilarStockIds(final List<String> stockIds) {
		if (null == stockIds || stockIds.isEmpty()) {
			return new ArrayList<>();
		}

		final String sql = "SELECT stock_id" + " FROM ims_lot" + " WHERE stock_id IN (:STOCK_ID_LIST)";
		final Query query = this.getSession().createSQLQuery(sql).setParameterList("STOCK_ID_LIST", stockIds);

		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<String> getStockIdsByListDataProjectListId(final Integer listId) {
		try {
			final String sql =
				"SELECT lot.stock_id" + " FROM ims_transaction tran, listnms l, ims_lot lot " + " WHERE l.listId = :listId "
					+ " AND tran.lotid = lot.lotid AND tran.sourceId = l.listref AND sourceType = 'LIST'" + " AND lot.stock_id IS NOT NULL";
			final Query query = this.getSession().createSQLQuery(sql).setParameter("listId", listId);
			return query.list();
		} catch (final Exception e) {
			final String message = "Error with getStockIdsByListDataProjectListId(" + listId + ") query from Transaction: " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public List<TransactionReportRow> getTransactionDetailsForLot(final Integer lotId) {

		final List<TransactionReportRow> transactions = new ArrayList<>();
		try {
			final String sql = "SELECT i.userid,i.lotid,i.trndate,"
					+ "(CASE WHEN trnstat = 0 THEN '" + TransactionStatus.PENDING.getValue()
					+ "' WHEN trnstat = 1 THEN '" + TransactionStatus.CONFIRMED.getValue()
					+ "' WHEN trnstat = 2 THEN '" + TransactionStatus.CANCELLED.getValue()
					+ "' END) as trnStatus, "
					+ " i.trnqty,i.sourceid,l.listname, i.comments,"
					+ "(CASE WHEN trntype = 0 THEN '" + TransactionType.DEPOSIT.getValue()
					+ "' WHEN trntype = 1 THEN '" + TransactionType.WITHDRAWAL.getValue()
					+ "' WHEN trntype = 2 THEN '" + TransactionType.DISCARD.getValue()
					+ "' WHEN trntype = 3 THEN '" + TransactionType.ADJUSTMENT.getValue()
					+ "' END) as trntype "
					+ "FROM ims_transaction i LEFT JOIN listnms l ON l.listid = i.sourceid "
					+ " INNER JOIN ims_lot lot ON lot.lotid = i.lotid "
					+ "WHERE i.lotid = :lotId AND i.trnstat <> 9 ORDER BY i.trnid";

			final Query query = this.getSession().createSQLQuery(sql);

			query.setParameter("lotId", lotId);

			this.createTransactionRow(transactions, query);

		} catch (final Exception e) {
			final String message = "Error with ggetTransactionDetailsForLot(" + lotId + ") query from Transaction: " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}

		return transactions;
	}

	private void createTransactionRow(final List<TransactionReportRow> transactionReportRows, final Query query) {

		final List<Object[]> result = query.list();
		TransactionReportRow transaction = null;
		for (final Object[] row : result) {

			final Integer userId = (Integer) row[0];
			final Integer lotId = (Integer) row[1];
			final Date trnDate = (Date) row[2];
			final String trnStatus = (String) row[3];
			final Double trnQty = (Double) row[4];
			final Integer listId = (Integer) row[5];
			final String listName = (String) row[6];
			final String comments = (String) row[7];
			final String lotStatus = (String) row[8];

			transaction = new TransactionReportRow();
			transaction.setUserId(userId);
			transaction.setLotId(lotId);
			transaction.setDate(trnDate);
			transaction.setTrnStatus(trnStatus);
			transaction.setQuantity(trnQty);
			transaction.setListId(listId);
			transaction.setListName(listName);
			transaction.setCommentOfLot(comments);
			transaction.setLotStatus(lotStatus);

			transactionReportRows.add(transaction);
		}

	}

	//New inventory functions, please locate them below this line to help cleaning in the near future.
	private final String SEARCH_TRANSACTIONS_QUERY = "SELECT " //
		+ "    act.trnid AS transactionId,"//
		+ "    users.uname AS createdByUsername,"//
		+ "(CASE WHEN trntype = 0 THEN '" + TransactionType.DEPOSIT.getValue()
		+ "' WHEN trntype = 1 THEN '" + TransactionType.WITHDRAWAL.getValue()
		+ "' WHEN trntype = 2 THEN '" + TransactionType.DISCARD.getValue()
		+ "' WHEN trntype = 3 THEN '" + TransactionType.ADJUSTMENT.getValue()
		+ "' END) AS transactionType,"//
		+ "    act.trnqty AS amount,"//
		+ "    act.comments AS notes,"//
		+ "    act.trndate as transactionDate, "//
		+ "    i.lotid AS lotLotId," //
		+ "    i.eid AS lotGid,"//
		+ "    n.nval AS lotDesignation,"//
		+ "    i.stock_id AS lotStockId,"//
		+ "    scaleid AS lotScaleId,"//
		+ "    scale.name AS lotScaleName," //
		+ "    (CASE WHEN i.status = 0 THEN 'Active' WHEN i.status = 1 THEN 'Closed' END) AS lotStatus, "
		+ "   (CASE WHEN trnstat = 0 THEN '" + TransactionStatus.PENDING.getValue()
		+ "' WHEN trnstat = 1 THEN '" + TransactionStatus.CONFIRMED.getValue()
		+ "' WHEN trnstat = 2 THEN '" + TransactionStatus.CANCELLED.getValue()
		+ "' END) as transactionStatus"
		+ " FROM"//
		+ "   ims_transaction act "//
		+ "        INNER JOIN"//
		+ "    ims_lot i ON act.lotid = i.lotid "//
		+ "        LEFT JOIN"//
		+ "    cvterm scale ON scale.cvterm_id = i.scaleid"//
		+ "        LEFT JOIN"//
		+ "    germplsm g ON g.gid = i.eid"//
		+ "        LEFT JOIN"//
		+ "    names n ON n.gid = i.eid AND n.nstat = 1"//
		+ "        LEFT JOIN"//
		+ "    workbench.users users ON users.userid = i.userid"//
		+ " WHERE"//
		+ "    i.etype = 'GERMPLSM' "; //

	private String buildSearchTransactionsQuery(final TransactionsSearchDto transactionsSearchDto) {
		final StringBuilder query = new StringBuilder(this.SEARCH_TRANSACTIONS_QUERY);
		if (transactionsSearchDto != null) {
			if (transactionsSearchDto.getLotIds() != null && !transactionsSearchDto.getLotIds().isEmpty()) {
				query.append(" and i.lotid IN (").append(Joiner.on(",").join(transactionsSearchDto.getLotIds())).append(") ");
			}

			if (transactionsSearchDto.getTransactionIds() != null && !transactionsSearchDto.getTransactionIds().isEmpty()) {
				query.append(" and trnid IN (").append(Joiner.on(",").join(transactionsSearchDto.getTransactionIds())).append(") ");
			}

			if (transactionsSearchDto.getGids() != null && !transactionsSearchDto.getGids().isEmpty()) {
				query.append(" and i.eid IN (").append(Joiner.on(",").join(transactionsSearchDto.getGids())).append(") ");
			}

			if (transactionsSearchDto.getScaleIds() != null && !transactionsSearchDto.getScaleIds().isEmpty()) {
				query.append(" and i.scaleid IN (").append(Joiner.on(",").join(transactionsSearchDto.getScaleIds())).append(") ");
			}

			if (transactionsSearchDto.getDesignation() != null) {
				query.append(" and n.nval like '%").append(transactionsSearchDto.getDesignation()).append("%' ");
			}

			if (transactionsSearchDto.getStockId() != null) {
				query.append(" and i.stock_id like '").append(transactionsSearchDto.getStockId()).append("%' ");
			}

			if (transactionsSearchDto.getNotes() != null) {
				query.append(" and act.comments like '%").append(transactionsSearchDto.getNotes()).append("%' ");
			}

			if (transactionsSearchDto.getTransactionDateFrom() != null) {
				query.append(" and DATE(act.trndate) >= '").append(format.format(transactionsSearchDto.getTransactionDateFrom())).append("' ");
			}

			if (transactionsSearchDto.getTransactionDateTo() != null) {
				query.append(" and DATE(act.trndate) <= '").append(format.format(transactionsSearchDto.getTransactionDateTo())).append("' ");
			}

			if (transactionsSearchDto.getCreatedByUsername() != null) {
				query.append(" and users.uname like '%").append(transactionsSearchDto.getCreatedByUsername()).append("%'");
			}

			if (transactionsSearchDto.getMinAmount() != null) {
				query.append("and act.trnqty >= ")
					.append(transactionsSearchDto.getMinAmount()).append(" ");
			}

			if (transactionsSearchDto.getMaxAmount() != null) {
				query.append("and act.trnqty <= ")
					.append(transactionsSearchDto.getMaxAmount()).append(" ");
			}

			if (transactionsSearchDto.getTransactionType() != null) {
				query.append("and (CASE"
					+ "        WHEN trntype = " + TransactionType.DEPOSIT.getId() + " THEN '"
					+ TransactionType.DEPOSIT.getValue() + "'"
					+ "        WHEN trntype = " + TransactionType.WITHDRAWAL.getId() + " THEN '"
					+ TransactionType.WITHDRAWAL.getValue() + "'"
					+ "        WHEN trntype = " + TransactionType.DISCARD.getId() + " THEN '"
					+ TransactionType.DISCARD.getValue() + "'"
					+ "        WHEN trntype = " + TransactionType.ADJUSTMENT.getId() + " THEN '"
					+ TransactionType.ADJUSTMENT.getValue() + "'"
					+ "    END) = '")
					.append(transactionsSearchDto.getTransactionType()).append("' ");
			}

			if (transactionsSearchDto.getTransactionStatus() != null) {
				query.append("and (CASE"
					+ "        WHEN trnstat = " + TransactionStatus.PENDING.getIntValue() + " THEN '"
					+ TransactionStatus.PENDING.getValue() + "'"
					+ "        WHEN trnstat = " + TransactionStatus.CONFIRMED.getIntValue() + " THEN '"
					+ TransactionStatus.CONFIRMED.getValue() + "'"
					+ "        WHEN trnstat = " + TransactionStatus.CANCELLED.getIntValue() + " THEN '"
					+ TransactionStatus.CANCELLED.getValue() + "'"
					+ "    END) = '")
					.append(transactionsSearchDto.getTransactionStatus()).append("' ");
			}

			if (transactionsSearchDto.getStatusIds() != null && !transactionsSearchDto.getStatusIds().isEmpty()) {
				query.append(" and act.trnstat IN (").append(Joiner.on(",").join(transactionsSearchDto.getStatusIds())).append(") ");
			}

			if (transactionsSearchDto.getLotStatus() != null) {
				query.append(" and i.status = ").append(transactionsSearchDto.getLotStatus()).append(" ");
			}

		}

		return query.toString();
	}

	private String addSortToSearchTransactionsQuery(final String transactionsSearchQuery, final Pageable pageable) {
		final StringBuilder sortedTransactionsSearchQuery = new StringBuilder(transactionsSearchQuery);
		if (pageable != null) {
			if (pageable.getSort() != null) {
				final List<String> sorts = new ArrayList<>();
				for (final Sort.Order order : pageable.getSort()) {
					sorts.add(order.getProperty().replace(".", "") + " " + order.getDirection().toString());
				}
				if (!sorts.isEmpty()) {
					sortedTransactionsSearchQuery.append(" ORDER BY ").append(Joiner.on(",").join(sorts));
				}
			} else {
				sortedTransactionsSearchQuery.append(" ORDER BY lotLotId");
			}
		}
		return sortedTransactionsSearchQuery.toString();
	}

	public List<TransactionDto> searchTransactions(final TransactionsSearchDto transactionsSearchDto, final Pageable pageable) {
		try {
			final String filterTransactionsQuery =
				this.addSortToSearchTransactionsQuery(this.buildSearchTransactionsQuery(transactionsSearchDto), pageable);

			final SQLQuery query = this.getSession().createSQLQuery(filterTransactionsQuery);
			query.addScalar("transactionId");
			query.addScalar("createdByUsername");
			query.addScalar("transactionType");
			query.addScalar("amount");
			query.addScalar("notes");
			query.addScalar("transactionDate", Hibernate.DATE);
			query.addScalar("lotLotId");
			query.addScalar("lotGid");
			query.addScalar("lotDesignation");
			query.addScalar("lotStockId");
			query.addScalar("lotScaleId");
			query.addScalar("lotScaleName");
			query.addScalar("lotStatus");
			query.addScalar("transactionStatus");

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
			final StringBuilder countTransactionsQuery =
				new StringBuilder("Select count(1) from (").append(this.buildSearchTransactionsQuery(transactionsSearchDto))
					.append(") as filteredTransactions");
			final SQLQuery query = this.getSession().createSQLQuery(countTransactionsQuery.toString());
			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at countTransactionsQuery() query on TransactionDAO: " + e.getMessage(), e);
		}
	}

	private Constructor<TransactionDto> getTransactionDtoConstructor() {
		try {
			return TransactionDto.class.getConstructor(Integer.class, String.class, String.class, Double.class,
				String.class, Date.class, Integer.class, Integer.class, String.class, String.class, Integer.class, String.class,
				String.class, String.class);
		} catch (final NoSuchMethodException ex) {
			throw new RuntimeException(ex);
		}
	}

	public Transaction saveTransaction(final Transaction transaction) {
		return this.saveOrUpdate(transaction);
	}
}
