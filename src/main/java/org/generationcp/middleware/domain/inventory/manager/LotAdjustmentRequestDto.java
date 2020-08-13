package org.generationcp.middleware.domain.inventory.manager;

import org.generationcp.middleware.domain.inventory.common.SearchCompositeDto;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class LotAdjustmentRequestDto {

	private SearchCompositeDto<Integer, String> selectedLots;

	private Double balance;

	private String notes;

	public SearchCompositeDto<Integer, String> getSelectedLots() {
		return selectedLots;
	}

	public void setSelectedLots(
		final SearchCompositeDto<Integer, String> selectedLots) {
		this.selectedLots = selectedLots;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(final Double balance) {
		this.balance = balance;
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
