package org.generationcp.middleware.domain.inventory.manager;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class TransactionUpdateRequestDto {

	private Integer transactionId;
	private Double amount;
	private Double availableBalance;
	private String notes;

	public TransactionUpdateRequestDto() {
	}

	public TransactionUpdateRequestDto(final Integer transactionId, final Double amount, final Double availableBalance,
		final String notes) {
		this.transactionId = transactionId;
		this.amount = amount;
		this.availableBalance = availableBalance;
		this.notes = notes;
	}

	public Integer getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(final Integer transactionId) {
		this.transactionId = transactionId;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(final Double amount) {
		this.amount = amount;
	}

	public Double getAvailableBalance() {
		return availableBalance;
	}

	public void setAvailableBalance(final Double availableBalance) {
		this.availableBalance = availableBalance;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(final String notes) {
		this.notes = notes;
	}

	public boolean isValid() {
		return transactionId != null && ((amount != null ^ availableBalance != null) || (amount == null && availableBalance == null
			&& notes != null));
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
