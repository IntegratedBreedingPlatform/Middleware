package org.generationcp.middleware.domain.inventory.manager;


import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class LotItemDto {

	private Integer gid;

	private Integer storageLocationId;

	/** Ignored if storageLocationId is present */
	private String storageLocationAbbr;

	private Integer scaleId;

	/** Ignored if scaleId is present */
	private String scaleName;

	private Double initialBalance;

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

	public String getScaleName() {
		return scaleName;
	}

	public Integer getScaleId() {
		return this.scaleId;
	}

	public void setScaleId(final Integer scaleId) {
		this.scaleId = scaleId;
	}

	public void setScaleName(final String scaleName) {
		this.scaleName = scaleName;
	}

	public Double getInitialBalance() {
		return initialBalance;
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

