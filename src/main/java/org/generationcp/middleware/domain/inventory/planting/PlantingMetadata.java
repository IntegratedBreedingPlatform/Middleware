package org.generationcp.middleware.domain.inventory.planting;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.math.BigInteger;

@AutoProperty
public class PlantingMetadata {

	private BigInteger pendingTransactionsCount;

	private BigInteger confirmedTransactionsCount;

	public BigInteger getPendingTransactionsCount() {
		return pendingTransactionsCount;
	}

	public void setPendingTransactionsCount(final BigInteger pendingTransactionsCount) {
		this.pendingTransactionsCount = pendingTransactionsCount;
	}

	public BigInteger getConfirmedTransactionsCount() {
		return confirmedTransactionsCount;
	}

	public void setConfirmedTransactionsCount(final BigInteger confirmedTransactionsCount) {
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
