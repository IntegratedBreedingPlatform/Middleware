package org.generationcp.middleware.domain.inventory.manager;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class LotDepositDto {

	private String lotUID;
	private Double amount;
	private String notes;

	public LotDepositDto(){

	}

	public LotDepositDto(final String lotUID, final Double amount, final String notes) {
		this.lotUID = lotUID;
		this.amount = amount;
		this.notes = notes;
	}

	public String getLotUID() {
		return lotUID;
	}

	public void setLotUID(final String lotUID) {
		this.lotUID = lotUID;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(final Double amount) {
		this.amount = amount;
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
