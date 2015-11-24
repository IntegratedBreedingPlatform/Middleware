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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.ims.EntityType;
import org.generationcp.middleware.pojos.ims.LotStatus;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.util.Util;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link Transaction}.
 *
 */
public class TransactionDAO extends GenericDAO<Transaction, Integer> {

	@SuppressWarnings("unchecked")
	public List<Transaction> getAllReserve(int start, int numOfRows) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Transaction.class);
			criteria.add(Restrictions.eq("status", Integer.valueOf(0)));
			criteria.add(Restrictions.lt("quantity", Double.valueOf(0)));
			criteria.setFirstResult(start);
			criteria.setMaxResults(numOfRows);
			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getAllReserve() query from Transaction: " + e.getMessage(), e);
		}
		return new ArrayList<Transaction>();
	}

	public List<String> getInventoryIDsWithBreederIdentifier(String identifier) throws MiddlewareQueryException {
		try {
			String queryString = Transaction.GET_INVENTORY_ID_WITH_IDENTIFIER_QUERY.replace(":identifier", identifier);
			Query query = this.getSession().createSQLQuery(queryString);
			return query.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with get query from Transaction: " + e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	public long countAllReserve() throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Transaction.class);
			criteria.setProjection(Projections.rowCount());
			criteria.add(Restrictions.eq("status", Integer.valueOf(0)));
			criteria.add(Restrictions.lt("quantity", Double.valueOf(0)));
			return ((Long) criteria.uniqueResult()).longValue(); // count
		} catch (HibernateException e) {
			this.logAndThrowException("Error with countAllReserve() query from Transaction: " + e.getMessage(), e);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<Transaction> getAllDeposit(int start, int numOfRows) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Transaction.class);
			criteria.add(Restrictions.eq("status", Integer.valueOf(0)));
			criteria.add(Restrictions.gt("quantity", Double.valueOf(0)));
			criteria.setFirstResult(start);
			criteria.setMaxResults(numOfRows);
			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getAllDeposit() query from Transaction: " + e.getMessage(), e);
		}
		return new ArrayList<Transaction>();
	}

	public long countAllDeposit() throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Transaction.class);
			criteria.setProjection(Projections.rowCount());
			criteria.add(Restrictions.eq("status", Integer.valueOf(0)));
			criteria.add(Restrictions.gt("quantity", Double.valueOf(0)));
			return ((Long) criteria.uniqueResult()).longValue(); // count
		} catch (HibernateException e) {
			this.logAndThrowException("Error with countAllDeposit() query from Transaction: " + e.getMessage(), e);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<Transaction> getAllReserveByRequestor(Integer personId, int start, int numOfRows) throws MiddlewareQueryException {
		try {
			if (personId != null) {
				Criteria criteria = this.getSession().createCriteria(Transaction.class);
				criteria.add(Restrictions.eq("status", Integer.valueOf(0)));
				criteria.add(Restrictions.lt("quantity", Double.valueOf(0)));
				criteria.add(Restrictions.eq("personId", personId));
				criteria.setFirstResult(start);
				criteria.setMaxResults(numOfRows);
				return criteria.list();
			}
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with getAllReserveByRequestor(personId=" + personId + ") query from Transaction: " + e.getMessage(), e);
		}
		return new ArrayList<Transaction>();
	}

	public long countAllReserveByRequestor(Integer personId) throws MiddlewareQueryException {
		try {
			if (personId != null) {
				Criteria criteria = this.getSession().createCriteria(Transaction.class);
				criteria.setProjection(Projections.rowCount());
				criteria.add(Restrictions.eq("status", Integer.valueOf(0)));
				criteria.add(Restrictions.lt("quantity", Double.valueOf(0)));
				criteria.add(Restrictions.eq("personId", personId));
				return ((Long) criteria.uniqueResult()).longValue();
			}
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with countAllReserveByRequestor(personId=" + personId + ") query from Transaction: " + e.getMessage(), e);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<Transaction> getAllDepositByDonor(Integer personId, int start, int numOfRows) throws MiddlewareQueryException {
		try {
			if (personId != null) {
				Criteria criteria = this.getSession().createCriteria(Transaction.class);
				criteria.add(Restrictions.eq("status", Integer.valueOf(0)));
				criteria.add(Restrictions.gt("quantity", Double.valueOf(0)));
				criteria.add(Restrictions.eq("personId", personId));
				criteria.setFirstResult(start);
				criteria.setMaxResults(numOfRows);
				return criteria.list();
			}
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with getAllDepositByDonor(personId=" + personId + ") query from Transaction: " + e.getMessage(), e);
		}
		return new ArrayList<Transaction>();
	}

	public long countAllDepositByDonor(Integer personId) throws MiddlewareQueryException {
		try {
			if (personId != null) {
				Criteria criteria = this.getSession().createCriteria(Transaction.class);
				criteria.setProjection(Projections.rowCount());
				criteria.add(Restrictions.eq("status", Integer.valueOf(0)));
				criteria.add(Restrictions.gt("quantity", Double.valueOf(0)));
				criteria.add(Restrictions.eq("personId", personId));
				return ((Long) criteria.uniqueResult()).longValue();
			}
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with countAllDepositByDonor(personId=" + personId + ") query from Transaction: " + e.getMessage(), e);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<Transaction> getAllUncommitted(int start, int numOfRows) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Transaction.class);
			criteria.add(Restrictions.eq("status", Integer.valueOf(0)));
			criteria.setFirstResult(start);
			criteria.setMaxResults(numOfRows);
			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getAllUncommitted() query from Transaction: " + e.getMessage(), e);
		}
		return new ArrayList<Transaction>();
	}

	public long countAllUncommitted() throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Transaction.class);
			criteria.setProjection(Projections.rowCount());
			criteria.add(Restrictions.eq("status", Integer.valueOf(0)));
			return ((Long) criteria.uniqueResult()).longValue(); // count
		} catch (HibernateException e) {
			this.logAndThrowException("Error with countAllUncommitted() query from Transaction: " + e.getMessage(), e);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<Transaction> getAllWithdrawals(int start, int numOfRows) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Transaction.class);
			criteria.add(Restrictions.lt("quantity", Double.valueOf(0)));
			criteria.setFirstResult(start);
			criteria.setMaxResults(numOfRows);
			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getAllWithdrawals() query from Transaction: " + e.getMessage(), e);
		}
		return new ArrayList<Transaction>();
	}

	public long countAllWithdrawals() throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Transaction.class);
			criteria.setProjection(Projections.rowCount());
			criteria.add(Restrictions.lt("quantity", Double.valueOf(0)));
			return ((Long) criteria.uniqueResult()).longValue(); // count
		} catch (HibernateException e) {
			this.logAndThrowException("Error with countAllWithdrawals() query from Transaction: " + e.getMessage(), e);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<Transaction> getEmptyLot(int start, int numOfRows) throws MiddlewareQueryException {
		try {
			Query query = this.getSession().getNamedQuery(Transaction.GET_EMPTY_LOT);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);
			return query.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getEmptyLot() query from Transaction: " + e.getMessage(), e);
		}
		return new ArrayList<Transaction>();
	}

	@SuppressWarnings("unchecked")
	public List<Transaction> getLotWithMinimumAmount(double minAmount, int start, int numOfRows) throws MiddlewareQueryException {
		try {
			Query query = this.getSession().getNamedQuery(Transaction.GET_LOT_WITH_MINIMUM_AMOUNT);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);
			query.setParameter("minAmount", minAmount);
			return query.list();
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with getLotWithMinimumAmount(minAmount=" + minAmount + ") query from Transaction: " + e.getMessage(), e);
		}
		return new ArrayList<Transaction>();
	}

	public List<InventoryDetails> getInventoryDetailsByTransactionRecordId(List<Integer> recordIds) throws MiddlewareQueryException {
		List<InventoryDetails> detailsList = new ArrayList<InventoryDetails>();

		if (recordIds == null || recordIds.isEmpty()) {
			return detailsList;
		}

		try {
			Session session = this.getSession();

			StringBuilder sql =
					new StringBuilder().append("SELECT lot.lotid, lot.userid, lot.eid, lot.locid, lot.scaleid, ")
							.append("tran.sourceid, tran.trnqty, tran.inventory_id, lot.comments, tran.recordid ")
							.append("FROM ims_transaction tran ").append("LEFT JOIN ims_lot lot ON lot.lotid = tran.lotid ")
							.append("WHERE lot.status = ").append(LotStatus.ACTIVE.getIntValue())
							.append("		 AND tran.recordid IN (:recordIds) ");
			SQLQuery query = session.createSQLQuery(sql.toString());
			query.setParameterList("recordIds", recordIds);

			List<Object[]> results = query.list();

			if (!results.isEmpty()) {
				for (Object[] row : results) {
					Integer lotId = (Integer) row[0];
					Integer userId = (Integer) row[1];
					Integer gid = (Integer) row[2];
					Integer locationId = (Integer) row[3];
					Integer scaleId = (Integer) row[4];
					Integer sourceId = (Integer) row[5];
					Double amount = (Double) row[6];
					String inventoryID = (String) row[7];
					String comment = (String) row[8];
					Integer sourceRecordId = (Integer) row[9];

					InventoryDetails details =
							new InventoryDetails(gid, null, lotId, locationId, null, userId, amount, sourceId, null, scaleId, null, comment);
					details.setInventoryID(inventoryID);
					details.setSourceRecordId(sourceRecordId);
					detailsList.add(details);
				}
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getInventoryDetailsByTransactionRecordId() query from TransactionDAO: " + e.getMessage(),
					e);
		}

		return detailsList;
	}

	@SuppressWarnings("unchecked")
	public List<InventoryDetails> getInventoryDetailsByGids(List<Integer> gids) throws MiddlewareQueryException {
		List<InventoryDetails> inventoryDetails = new ArrayList<InventoryDetails>();

		if (gids == null || gids.isEmpty()) {
			return inventoryDetails;
		}

		try {
			Session session = this.getSession();

			StringBuilder sql =
					new StringBuilder().append("SELECT lot.lotid, lot.userid, lot.eid, lot.locid, lot.scaleid, ")
							.append("tran.sourceid, tran.trnqty, lot.comments ").append("FROM ims_lot lot ")
							.append("LEFT JOIN ims_transaction tran ON lot.lotid = tran.lotid ").append("WHERE lot.status = ")
							.append(LotStatus.ACTIVE.getIntValue()).append("		 AND lot.eid IN (:gids) ");
			SQLQuery query = session.createSQLQuery(sql.toString());
			query.setParameterList("gids", gids);

			List<Object[]> results = query.list();

			if (!results.isEmpty()) {
				for (Object[] row : results) {
					Integer lotId = (Integer) row[0];
					Integer userId = (Integer) row[1];
					Integer gid = (Integer) row[2];
					Integer locationId = (Integer) row[3];
					Integer scaleId = (Integer) row[4];
					Integer sourceId = (Integer) row[5];
					Double amount = (Double) row[6];
					String comment = (String) row[7];

					inventoryDetails.add(new InventoryDetails(gid, null, lotId, locationId, null, userId, amount, sourceId, null, scaleId,
							null, comment));
				}
			}

			for (Integer gid : gids) {
				if (!this.isGidInInventoryList(inventoryDetails, gid)) {
					inventoryDetails.add(new InventoryDetails(gid, null, null, null, null, null, null, null, null, null, null, null));
				}
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getGidsByListId() query from GermplasmList: " + e.getMessage(), e);
		}

		return inventoryDetails;
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, BigInteger> countLotsWithReservationForListEntries(List<Integer> listEntryIds) throws MiddlewareQueryException {
		Map<Integer, BigInteger> lotCounts = new HashMap<Integer, BigInteger>();

		try {
			String sql =
					"SELECT recordid, count(DISTINCT t.lotid) " + "FROM ims_transaction t " + "INNER JOIN ims_lot l ON l.lotid = t.lotid "
							+ "WHERE trnstat = 0 AND trnqty < 0 AND recordid IN (:entryIds) "
							+ "  AND l.status = 0 AND l.etype = 'GERMPLSM' " + "GROUP BY recordid " + "ORDER BY recordid ";
			Query query = this.getSession().createSQLQuery(sql).setParameterList("entryIds", listEntryIds);
			List<Object[]> result = query.list();
			for (Object[] row : result) {
				Integer entryId = (Integer) row[0];
				BigInteger count = (BigInteger) row[1];

				lotCounts.put(entryId, count);
			}

		} catch (Exception e) {
			this.logAndThrowException(
					"Error at countLotsWithReservationForListEntries=" + listEntryIds + " at TransactionDAO: " + e.getMessage(), e);
		}

		return lotCounts;
	}

	private boolean isGidInInventoryList(List<InventoryDetails> inventoryDetails, Integer gid) {
		for (InventoryDetails detail : inventoryDetails) {
			if (detail.getGid().equals(gid)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public List<Transaction> getByLotIds(List<Integer> lotIds) throws MiddlewareQueryException {
		List<Transaction> transactions = new ArrayList<Transaction>();

		if (lotIds == null || lotIds.isEmpty()) {
			return transactions;
		}

		try {
			Criteria criteria = this.getSession().createCriteria(Transaction.class);
			criteria.add(Restrictions.in("lot.id", lotIds));
			return criteria.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByLotIds() query from Transaction: " + e.getMessage(), e);
		}

		return transactions;
	}

	public void cancelUnconfirmedTransactionsForListEntries(List<Integer> listEntryIds) throws MiddlewareQueryException {
		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();
			
			String sql =
					"UPDATE ims_transaction " + "SET trnstat = 9, " + "trndate = :currentDate "
							+ "WHERE trnstat = 0 AND recordid IN (:entryIds) " + "AND sourceType = 'LIST'";
			Query query =
					this.getSession().createSQLQuery(sql).setParameter("currentDate", Util.getCurrentDateAsIntegerValue())
							.setParameterList("entryIds", listEntryIds);
			query.executeUpdate();
		} catch (Exception e) {
			this.logAndThrowException("Error at cancelReservationForListEntries=" + listEntryIds + " at TransactionDAO: " + e.getMessage(),
					e);
		}
	}

	public void cancelReservationsForLotEntryAndLrecId(Integer lotId, Integer lrecId) throws MiddlewareQueryException {
		try {
			
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();
			
			String sql =
					"UPDATE ims_transaction " + "SET trnstat = 9, " + "trndate = :currentDate " + "WHERE trnstat = 0 AND lotId = :lotId "
							+ "AND recordId = :lrecId " + "AND trnqty < 0 " + "AND sourceType = 'LIST'";
			Query query =
					this.getSession().createSQLQuery(sql).setParameter("currentDate", Util.getCurrentDateAsIntegerValue())
							.setParameter("lotId", lotId).setParameter("lrecId", lrecId);
			query.executeUpdate();
		} catch (Exception e) {
			this.logAndThrowException("Error at cancelReservationsForListEntries(lotId:" + lotId + ", lrecId:" + lrecId
					+ ") at TransactionDAO: " + e.getMessage(), e);
		}
	}

	public void cancelUnconfirmedTransactionsForGermplasms(List<Integer> gids) throws MiddlewareQueryException {
		try {
			String sql =
					"UPDATE ims_transaction " + "SET trnstat = 9, " + "trndate = :currentDate "
							+ "WHERE trnstat = 0 AND sourceType = 'LIST' " + "AND lotid in ( select lotid from ims_lot "
							+ "WHERE status = 0 AND etype = 'GERMPLSM' " + "AND eid in (:gids))";
			Query query =
					this.getSession().createSQLQuery(sql).setParameter("currentDate", Util.getCurrentDateAsIntegerValue())
							.setParameterList("gids", gids);
			query.executeUpdate();
		} catch (Exception e) {
			this.logAndThrowException(
					"Error at cancelUnconfirmedTransactionsForGermplasms=" + gids + " at TransactionDAO: " + e.getMessage(), e);
		}
	}

	public void cancelUnconfirmedTransactionsForLists(List<Integer> listIds) throws MiddlewareQueryException {
		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();
			
			String sql =
					"UPDATE ims_transaction " + "SET trnstat = 9, " + "trndate = :currentDate "
							+ "WHERE trnstat = 0 AND sourceId in (:listIds) " + "AND sourceType = 'LIST'";
			Query query =
					this.getSession().createSQLQuery(sql).setParameter("currentDate", Util.getCurrentDateAsIntegerValue())
							.setParameterList("listIds", listIds);
			query.executeUpdate();
		} catch (Exception e) {
			this.logAndThrowException(
					"Error at cancelUnconfirmedTransactionsForLists=" + listIds + " at TransactionDAO: " + e.getMessage(), e);
		}
	}

	public boolean transactionsExistForListData(Integer dataListId) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(Transaction.class);
			criteria.add(Restrictions.eq("sourceId", dataListId));
			criteria.add(Restrictions.eq("sourceType", EntityType.LIST.name()));
			criteria.setProjection(Projections.rowCount());

			Number number = (Number) criteria.uniqueResult();
			return number.intValue() > 0;
		} catch (HibernateException e) {
			this.logAndThrowException("Error at transactionsExistForListData=" + dataListId + " at TransactionDAO: " + e.getMessage(), e);
			return false;
		}
	}

	public Map<Integer, String> retrieveStockIds(List<Integer> lrecIds) {

		Map<Integer, String> lrecIdStockIdMap = new HashMap<>();

		String sql =
				"SELECT a.lrecid,group_concat(inventory_id SEPARATOR ', ')  " + "FROM listdata a  "
						+ "inner join ims_lot b ON a.gid = b.eid  "
						+ "INNER JOIN ims_transaction c ON b.lotid = c.lotid and a.lrecid = c.recordid "
						+ "WHERE a.lrecid in (:lrecids) GROUP BY a.lrecid";

		Query query = this.getSession().createSQLQuery(sql).setParameterList("lrecids", lrecIds);

		List<Object[]> result = query.list();
		for (Object[] row : result) {
			Integer lrecid = (Integer) row[0];
			String stockIds = (String) row[1];

			lrecIdStockIdMap.put(lrecid, stockIds);
		}
		return lrecIdStockIdMap;

	}

	public Boolean isStockIdExists(final List<String> stockIds) {
		final List<String> result = this.getSimilarStockIds(stockIds);
		return null != result && !result.isEmpty();

	}

	public List<String> getSimilarStockIds(final List<String> stockIds) {
		if (null == stockIds || stockIds.isEmpty()) {
			return new ArrayList<>();
		}

		final String sql = "SELECT inventory_id" + " FROM ims_transaction" + " WHERE inventory_id IN (:STOCK_ID_LIST)";
		final Query query = this.getSession().createSQLQuery(sql).setParameterList("STOCK_ID_LIST", stockIds);

		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<String> getStockIdsByListDataProjectListId(Integer listId) throws MiddlewareQueryException {
		try {
			String sql =
					"SELECT tran.inventory_id" + " FROM ims_transaction tran, listnms l" + " WHERE l.listId = :listId "
							+ " AND sourceId = l.listref AND sourceType = 'LIST'" + " AND inventory_id IS NOT NULL";
			Query query = this.getSession().createSQLQuery(sql).setParameter("listId", listId);
			return query.list();
		} catch (Exception e) {
			this.logAndThrowException("Error at getStockIdsByListId(" + listId + ") at TransactionDAO: " + e.getMessage(), e);
		}
		return new ArrayList<String>();
	}
}
