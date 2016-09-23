
package org.generationcp.middleware.domain.inventory;

import org.generationcp.middleware.domain.oms.Term;

import java.io.Serializable;
import java.util.List;

/**
 * POJO for storing aggregate inventory information and list of lots associated with germplasm
 *
 * @author Darla Ani
 *
 */
public class GermplasmInventory implements Serializable {

	private static final long serialVersionUID = -5519155457123841685L;

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
	private Term scaleForGermplsm;

	// count of different scale across all lots for germplsm
	private Integer distinctScaleCountForGermplsm;

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

	public Integer getScaleIdForGermplsm() {
		return scaleIdForGermplsm;
	}

	public void setScaleIdForGermplsm(Integer scaleIdForGermplsm) {
		this.scaleIdForGermplsm = scaleIdForGermplsm;
	}

	public Term getScaleForGermplsm() {
		return scaleForGermplsm;
	}

	public void setScaleForGermplsm(Term scaleForGermplsm) {
		this.scaleForGermplsm = scaleForGermplsm;
	}

	public Integer getDistinctScaleCountForGermplsm() {
		return distinctScaleCountForGermplsm;
	}

	public void setDistinctScaleCountForGermplsm(Integer distinctScaleCountForGermplsm) {
		this.distinctScaleCountForGermplsm = distinctScaleCountForGermplsm;
	}
}
