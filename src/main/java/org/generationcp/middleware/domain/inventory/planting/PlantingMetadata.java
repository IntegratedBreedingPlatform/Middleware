package org.generationcp.middleware.domain.inventory.planting;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class PlantingMetadata {

	private Long pendingTransactionsCount;

	private Long confirmedTransactionsCount;

	public Long getPendingTransactionsCount() {
		return pendingTransactionsCount;
	}

	public void setPendingTransactionsCount(final Long pendingTransactionsCount) {
		this.pendingTransactionsCount = pendingTransactionsCount;
	}

	public Long getConfirmedTransactionsCount() {
		return confirmedTransactionsCount;
	}

	public void setConfirmedTransactionsCount(final Long confirmedTransactionsCount) {
		this.confirmedTransactionsCount = confirmedTransactionsCount;
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
