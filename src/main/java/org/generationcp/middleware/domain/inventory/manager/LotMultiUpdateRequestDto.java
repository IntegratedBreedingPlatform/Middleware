package org.generationcp.middleware.domain.inventory.manager;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
public class LotMultiUpdateRequestDto {

	private List<LotUpdateDto> lotList;

	public List<LotUpdateDto> getLotList() {
		return this.lotList;
	}

	public void setLotList(final List<LotUpdateDto> lotList) {
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
