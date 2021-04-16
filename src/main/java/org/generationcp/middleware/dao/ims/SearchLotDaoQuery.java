package org.generationcp.middleware.dao.ims;

import org.generationcp.middleware.pojos.ims.LotStatus;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.pojos.ims.TransactionType;

final class SearchLotDaoQuery {

	private static final String BASE_QUERY = "SELECT %s "
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

	public static String getSelectBaseQuery() {
		return String.format(BASE_QUERY, SELECT_EXPRESION);
	}

	public static String getCountBaseQuery() {
		return String.format(BASE_QUERY, " count(1) ");
	}

}
