package org.generationcp.middleware.domain.inventory;

import java.io.Serializable;

import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.pojos.Location;

public class LotDetails implements Serializable {

	private static final long serialVersionUID = 2572260467983831666L;

	// ims_lot fields
	private Integer lotId;
	private Integer entityIdOfLot;
	private Integer scaleId;
	private Integer locId;
	private String lotScaleNameAbbr;
	private String lotScaleMethodName;
	private String commentOfLot;

	// computed or looked up values
	private Double actualLotBalance;
	private Double availableLotBalance;
	private Double withdrawalBalance;
	private String withdrawalStatus;
	private Location locationOfLot;
	private Double reservedTotal;
	private Term scaleOfLot;
	private String stockIds;
	private Integer transactionId;
	private Boolean transactionStatus;

	public Integer getLotId() {
		return this.lotId;
	}

	public void setLotId(Integer lotId) {
		this.lotId = lotId;
	}

	public Integer getEntityIdOfLot() {
		return this.entityIdOfLot;
	}

	public void setEntityIdOfLot(Integer entityIdOfLot) {
		this.entityIdOfLot = entityIdOfLot;
	}

	public Double getActualLotBalance() {
		return this.actualLotBalance;
	}

	public void setActualLotBalance(Double actualLotBalance) {
		this.actualLotBalance = actualLotBalance;
	}

	public Location getLocationOfLot() {
		return this.locationOfLot;
	}

	public void setLocationOfLot(Location locationOfLot) {
		this.locationOfLot = locationOfLot;
	}

	public Term getScaleOfLot() {
		return this.scaleOfLot;
	}

	public void setScaleOfLot(Term scaleOfLot) {
		this.scaleOfLot = scaleOfLot;
	}

	public String getCommentOfLot() {
		return this.commentOfLot;
	}

	public void setCommentOfLot(String commentOfLot) {
		this.commentOfLot = commentOfLot;
	}

	public Double getAvailableLotBalance() {
		return this.availableLotBalance;
	}

	public void setAvailableLotBalance(Double availableLotBalance) {
		this.availableLotBalance = availableLotBalance;
	}

	public Double getReservedTotal() {
		return this.reservedTotal;
	}

	public void setReservedTotal(Double reservedTotal) {
		this.reservedTotal = reservedTotal;
	}

	public Integer getScaleId() {
		return this.scaleId;
	}

	public void setScaleId(Integer scaleId) {
		this.scaleId = scaleId;
	}

	public Integer getLocId() {
		return this.locId;
	}

	public void setLocId(Integer locId) {
		this.locId = locId;
	}

	public String getStockIds() {
		return this.stockIds;
	}

	public void setStockIds(String stockIds) {
		this.stockIds = stockIds;
	}

	public Double getWithdrawalBalance() {
		return withdrawalBalance;
	}

	public void setWithdrawalBalance(Double withdrawalBalance) {
		this.withdrawalBalance = withdrawalBalance;
	}

	public String getWithdrawalStatus() {
		return withdrawalStatus;
	}

	public void setWithdrawalStatus(String withdrawalStatus) {
		this.withdrawalStatus = withdrawalStatus;
	}

	public String getLotScaleNameAbbr() {
		return lotScaleNameAbbr;
	}

	public void setLotScaleNameAbbr(String lotScaleNameAbbr) {
		this.lotScaleNameAbbr = lotScaleNameAbbr;
	}

	public Integer getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Integer transactionId) {
		this.transactionId = transactionId;
	}

	public String getLotScaleMethodName() {
		return lotScaleMethodName;
	}

	public void setLotScaleMethodName(String lotScaleMethodName) {
		this.lotScaleMethodName = lotScaleMethodName;
	}

	public Boolean getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(Boolean transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LotReportRow [lotId=");
		builder.append(this.lotId);
		builder.append(", entityIdOfLot=");
		builder.append(this.entityIdOfLot);
		builder.append(", actualLotBalance=");
		builder.append(this.actualLotBalance);
		builder.append(", availableLotBalance=");
		builder.append(this.availableLotBalance);
		builder.append(", reservedTotal=");
		builder.append(this.reservedTotal);
		builder.append(", locationId=");
		builder.append(this.locId);
		builder.append(", locationOfLot=");
		builder.append(this.locationOfLot);
		builder.append(", scaleId=");
		builder.append(this.scaleId);
		builder.append(", scaleOfLot=");
		builder.append(this.scaleOfLot);
		builder.append(", commentOfLot=");
		builder.append(this.commentOfLot);
		builder.append(", stockIds=");
		builder.append(this.stockIds);
		builder.append("]");
		return builder.toString();
	}

}
