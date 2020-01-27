
package org.generationcp.middleware.pojos.ims;

public enum TransactionType {

	DEPOSIT("Deposit", 0), WITHDRAWAL("Withdrawal", 1),
	DISCARD("Discard", 2), ADJUSTMENT("Adjustment", 3);

	private final Integer id;
	private String type;

	private TransactionType(String status, Integer id) {
		this.type = status;
		this.id = id;
	}

	public String getValue() {
		return this.type;
	}

}
