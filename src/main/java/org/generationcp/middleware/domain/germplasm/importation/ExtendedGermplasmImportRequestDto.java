package org.generationcp.middleware.domain.germplasm.importation;

import org.pojomatic.Pojomatic;

public class ExtendedGermplasmImportRequestDto extends GermplasmImportRequestDto {

	private Double amount;

	private String stockId;

	private String storageLocationAbbr;

	private String unit;

	public Double getAmount() {
		return amount;
	}

	public void setAmount(final Double amount) {
		this.amount = amount;
	}

	public String getStockId() {
		return stockId;
	}

	public void setStockId(final String stockId) {
		this.stockId = stockId;
	}

	public String getStorageLocationAbbr() {
		return storageLocationAbbr;
	}

	public void setStorageLocationAbbr(final String storageLocationAbbr) {
		this.storageLocationAbbr = storageLocationAbbr;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(final String unit) {
		this.unit = unit;
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
