
package org.generationcp.middleware.pojos.ims;

public enum TransactionType {

	DEPOSIT("Deposit"), RESERVATION("Reservation"),  WITHDRAWAL("Withdrawal");

	private String type;

	private TransactionType(String status) {
		this.type = status;
	}

	public String getValue() {
		return this.type;
	}

}
