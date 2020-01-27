
package org.generationcp.middleware.pojos.ims;

public enum TransactionStatus {

	PENDING(0, "Pending"), CONFIRMED(1, "Confirmed"), CANCELLED(9, "Cancelled");

	private int id;
	private String status;

	private TransactionStatus(int id, String status) {
		this.status = status;
		this.id = id;
	}

	public int getIntValue() {
		return this.id;
	}

	public String getValue() {
		return this.status;
	}
}
