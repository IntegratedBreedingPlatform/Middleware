package org.generationcp.middleware.domain.inventory.manager;


import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class LotItemDto {

	private Integer gid;

	private Integer storageLocationId;

	/** Ignored if storageLocationId is present */
	private String storageLocationAbbr;

	private Integer unitId;

	/** Ignored if scaleId is present */
	private String unitName;

	private Double initialBalance;

	private boolean pendingStatus;

	private String stockId;

	private String notes;

	public Integer getGid() {
		return gid;
	}

	public void setGid(final Integer gid) {
		this.gid = gid;
	}

	public Integer getStorageLocationId() {
		return this.storageLocationId;
	}

	public void setStorageLocationId(final Integer storageLocationId) {
		this.storageLocationId = storageLocationId;
	}

	public String getStorageLocationAbbr() {
		return storageLocationAbbr;
	}

	public void setStorageLocationAbbr(final String storageLocationAbbr) {
		this.storageLocationAbbr = storageLocationAbbr;
	}

	public String getUnitName() {
		return unitName;
	}

	public Integer getUnitId() {
		return this.unitId;
	}

	public void setUnitId(final Integer unitId) {
		this.unitId = unitId;
	}

	public void setUnitName(final String unitName) {
		this.unitName = unitName;
	}

	public Double getInitialBalance() {
		return initialBalance;
	}

	public boolean isPendingStatus() {
		return this.pendingStatus;
	}

	public void setPendingStatus(final boolean pendingStatus) {
		this.pendingStatus = pendingStatus;
	}

	public void setInitialBalance(final Double initialBalance) {
		this.initialBalance = initialBalance;
	}

	public String getStockId() {
		return stockId;
	}

	public void setStockId(final String stockId) {
		this.stockId = stockId;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(final String notes) {
		this.notes = notes;
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

}

