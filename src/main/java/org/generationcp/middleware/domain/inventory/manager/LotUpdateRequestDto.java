package org.generationcp.middleware.domain.inventory.manager;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class LotUpdateRequestDto {

	private LotSingleUpdateRequestDto singleInput;

	private LotMultiUpdateRequestDto multiInput;

	public LotSingleUpdateRequestDto getSingleInput() {
		return this.singleInput;
	}

	public void setSingleInput(final LotSingleUpdateRequestDto singleInput) {
		this.singleInput = singleInput;
	}

	public LotMultiUpdateRequestDto getMultiInput() {
		return this.multiInput;
	}

	public void setMultiInput(final LotMultiUpdateRequestDto multiInput) {
		this.multiInput = multiInput;
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
	public boolean equals(Object o) {
		return Pojomatic.equals(this, o);
	}

}
