package org.generationcp.middleware.domain.inventory;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * POJO for storing aggregate inventory information and list of lots associated with germplasm
 *
 * @author Darla Ani
 */
public class GermplasmInventory implements Serializable {

	private static final long serialVersionUID = -5519155457123841685L;

	public static final String RESERVED = "Reserved";

	public static final String COMMITTED = "Committed";

	public static final String MIXED = "Mixed";

	public static final String WITHDRAWN = "Withdrawn";

	private Integer gid;

	// number of lots with actual inventory available for given germplasm
	private Integer actualInventoryLotCount;

	// number of lots with reserved amount for given germplasm
	private Integer reservedLotCount;

	// number of lots for given germplasm
	private Integer lotCount;

	// String of StockIDs separated by comma per list entry.
	private String stockIDs;

	// total seed balance across all lots for germplsm
	private Double totalAvailableBalance;

	// scaleId of lots if all lots have same scaleId
	private Integer scaleIdForGermplsm;

	// scale of lots if all lots have same scale
	private String scaleForGermplsm;

	// count of different scale across all lots for germplsm
	private Integer distinctScaleCountForGermplsm;

	// total withdrawal balance of germplsm per list entry
	private Double withdrawalBalance;

	// distinct scales of withdrawal per list entry
	private Integer distinctCountWithdrawalScale;

	// scale withdrawal if ony one scaleId across all withdrawal
	private Integer withdrawalScaleId;

	// scale withdrawal if ony one scale across all withdrawal
	private String withdrawalScale;

	// count of different withdrawal status per list
	private Integer distinctCountWithdrawalStatus;

	// withdrawal status(0=reserved, 1=committed) if only one across all transactions
	private Integer withdrawalStatus;

	// overall status for germplsm for list entry
	private String transactionStatus;

	// list of lots for germplasm
	private List<? extends LotDetails> lotRows;

	public GermplasmInventory(Integer gid) {
		super();
		this.gid = gid;
	}

	public Integer getGid() {
		return this.gid;
	}

	public void setGid(Integer gid) {
		this.gid = gid;
	}

	public Integer getActualInventoryLotCount() {
		return this.actualInventoryLotCount;
	}

	public void setActualInventoryLotCount(Integer actualInventoryLotCount) {
		this.actualInventoryLotCount = actualInventoryLotCount;
	}

	public Integer getReservedLotCount() {
		return this.reservedLotCount;
	}

	public void setReservedLotCount(Integer reservedLotCount) {
		this.reservedLotCount = reservedLotCount;
	}

	public List<? extends LotDetails> getLotRows() {
		return this.lotRows;
	}

	public void setLotRows(List<? extends LotDetails> lotRows) {
		this.lotRows = lotRows;
		this.lotCount = lotRows.size();
	}

	public String getStockIDs() {
		return this.stockIDs;
	}

	public void setStockIDs(String stockIDs) {
		this.stockIDs = stockIDs;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GermplasmInventory [");
		builder.append(this.getFieldsToString());
		builder.append("]");
		return builder.toString();
	}

	protected String getFieldsToString() {
		StringBuilder builder = new StringBuilder();
		builder.append("gid=");
		builder.append(this.gid);
		builder.append(", actualInventoryLotCount=");
		builder.append(this.actualInventoryLotCount);
		builder.append(", reservedLotCount=");
		builder.append(this.reservedLotCount);
		if (this.lotRows != null) {
			builder.append(", lotCount = ");
			builder.append(this.lotRows.size());
			builder.append(", lots={");
			for (LotDetails lot : this.lotRows) {
				builder.append(lot);
			}
			builder.append("}");
		} else {
			builder.append(", lotCount = ");
			builder.append(this.lotCount);
		}
		builder.append(", stockIDs=");
		builder.append(this.stockIDs);

		return builder.toString();
	}

	public Integer getLotCount() {
		return this.lotCount;
	}

	public void setLotCount(Integer lotCount) {
		this.lotCount = lotCount;
	}

	public Double getTotalAvailableBalance() {
		return totalAvailableBalance;
	}

	public void setTotalAvailableBalance(Double totalAvailableBalance) {
		this.totalAvailableBalance = totalAvailableBalance;
	}

	public String getAvailable() {
		if (this.getDistinctScaleCountForGermplsm() == 0) {
			return "-";
		} else if (this.getDistinctScaleCountForGermplsm() == 1) {
			StringBuilder available = new StringBuilder();
			available.append(this.getTotalAvailableBalance());

			if (!StringUtils.isEmpty(this.getScaleForGermplsm())) {
				available.append(" " + this.getScaleForGermplsm());
			}
			return available.toString();
		} else {
			return MIXED;
		}
	}

	public Integer getScaleIdForGermplsm() {
		return scaleIdForGermplsm;
	}

	public void setScaleIdForGermplsm(Integer scaleIdForGermplsm) {
		this.scaleIdForGermplsm = scaleIdForGermplsm;
	}

	public String getScaleForGermplsm() {
		return scaleForGermplsm;
	}

	public void setScaleForGermplsm(String scaleForGermplsm) {
		this.scaleForGermplsm = scaleForGermplsm;
	}

	public Integer getDistinctScaleCountForGermplsm() {
		return distinctScaleCountForGermplsm;
	}

	public void setDistinctScaleCountForGermplsm(Integer distinctScaleCountForGermplsm) {
		this.distinctScaleCountForGermplsm = distinctScaleCountForGermplsm;
	}

	public Double getWithdrawalBalance() {
		return withdrawalBalance;
	}

	public void setWithdrawalBalance(Double withdrawalBalance) {
		this.withdrawalBalance = withdrawalBalance;
	}

	public String getWithdrawalScale() {
		return withdrawalScale;
	}

	public void setWithdrawalScale(String withdrawalScale) {
		this.withdrawalScale = withdrawalScale;
	}

	public Integer getDistinctCountWithdrawalScale() {
		return distinctCountWithdrawalScale;
	}

	public void setDistinctCountWithdrawalScale(Integer distinctCountWithdrawalScale) {
		this.distinctCountWithdrawalScale = distinctCountWithdrawalScale;
	}

	public Integer getDistinctCountWithdrawalStatus() {
		return distinctCountWithdrawalStatus;
	}

	public void setDistinctCountWithdrawalStatus(Integer distinctCountWithdrawalStatus) {
		this.distinctCountWithdrawalStatus = distinctCountWithdrawalStatus;
	}

	public Integer getWithdrawalStatus() {
		return withdrawalStatus;
	}

	public void setWithdrawalStatus(Integer withdrawalStatus) {
		this.withdrawalStatus = withdrawalStatus;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public Integer getWithdrawalScaleId() {
		return withdrawalScaleId;
	}

	public void setWithdrawalScaleId(Integer withdrawalScaleId) {
		this.withdrawalScaleId = withdrawalScaleId;
	}
}
