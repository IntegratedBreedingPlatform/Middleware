package org.generationcp.middleware.domain.inventory.manager;


import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class LotItemDto {

	private Integer gid;

	private String storageLocationAbbr;

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

	public String getStorageLocationAbbr() {
		return storageLocationAbbr;
	}

	public void setStorageLocationAbbr(final String storageLocationAbbr) {
		this.storageLocationAbbr = storageLocationAbbr;
	}

	public String getScaleName() {
		return scaleName;
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

