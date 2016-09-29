
package org.generationcp.middleware.domain.inventory;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * This POJO stores aggregate information about lot such as actual balance, available balance, total reserved etc
 * 
 * @author Darla Ani
 *
 */
public class LotAggregateData implements Serializable {

	private static final long serialVersionUID = 438412738380543621L;

	private Integer lotId;

	private Double actualBalance;

	private Double availableBalance;

	private Double reservedTotal;

	private Double committedTotal;

	private String stockIds;

	private Integer transactionId;

	// key = record id and value = reserved quantity
	private Map<Integer, Double> reservationMap;

	// key = record id and value = 0 or 1 status
	private Map<Integer, Set<String>> reservationStatusMap;


	public LotAggregateData(Integer lotId) {
		super();
		this.lotId = lotId;
	}

	public Integer getLotId() {
		return this.lotId;
	}

	public void setLotId(Integer lotId) {
		this.lotId = lotId;
	}

	public Double getActualBalance() {
		return this.actualBalance;
	}

	public void setActualBalance(Double actualBalance) {
		this.actualBalance = actualBalance;
	}

	public Double getAvailableBalance() {
		return this.availableBalance;
	}

	public void setAvailableBalance(Double availableBalance) {
		this.availableBalance = availableBalance;
	}

	public Double getReservedTotal() {
		return this.reservedTotal;
	}

	public void setReservedTotal(Double reservedTotal) {
		this.reservedTotal = reservedTotal;
	}

	public Map<Integer, Double> getReservationMap() {
		return this.reservationMap;
	}

	public void setReservationMap(Map<Integer, Double> reservationMap) {
		this.reservationMap = reservationMap;
	}

	public String getStockIds() {
		return this.stockIds;
	}

	public void setStockIds(String stockIds) {
		this.stockIds = stockIds;
	}

	public Double getCommittedTotal() {
		return committedTotal;
	}

	public void setCommittedTotal(Double committedTotal) {
		this.committedTotal = committedTotal;
	}

	public Map<Integer, Set<String>> getReservationStatusMap() {
		return reservationStatusMap;
	}

	public void setReservationStatusMap(Map<Integer, Set<String>> reservationStatusMap) {
		this.reservationStatusMap = reservationStatusMap;
	}

	public Integer getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Integer transactionId) {
		this.transactionId = transactionId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LotAggregateData [lotId=");
		builder.append(this.lotId);
		builder.append(", actualBalance=");
		builder.append(this.actualBalance);
		builder.append(", availableBalance=");
		builder.append(this.availableBalance);
		builder.append(", reservedTotal=");
		builder.append(this.reservedTotal);
		builder.append(", committedTotal=");
		builder.append(this.committedTotal);
		if (this.reservationMap != null && !this.reservationMap.isEmpty()) {
			builder.append(", reservationMap={");
			for (Integer id : this.reservationMap.keySet()) {
				builder.append("Id:");
				builder.append(id);
				builder.append("->");
				builder.append("Qty:");
				builder.append(this.reservationMap.get(id));
			}
		}
		builder.append(", stockIds=");
		builder.append(this.stockIds);
		builder.append("]");
		return builder.toString();
	}

}
