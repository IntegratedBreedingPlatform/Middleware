package org.generationcp.middleware.domain.inventory.manager;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
public class LotImportRequestDto {

	private String stockIdPrefix;

	private List<LotItemDto> lotList;

	public String getStockIdPrefix() {
		return stockIdPrefix;
	}

	public void setStockIdPrefix(final String stockIdPrefix) {
		this.stockIdPrefix = stockIdPrefix;
	}

	public List<LotItemDto> getLotList() {
		return lotList;
	}

	public void setLotList(final List<LotItemDto> lotList) {
		this.lotList = lotList;
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
