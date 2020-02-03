
package org.generationcp.middleware.pojos.ims;

public enum TransactionType {

	WITHDRAWAL("Withdrawal", 1),
	DISCARD("Discard", 2),
	ADJUSTMENT("Adjustment", 3),
	DEPOSIT("Deposit", 4);

	private final Integer id;
	private String value;

	private TransactionType(String type, Integer id) {
		this.value = type;
		this.id = id;
	}

	public String getValue() {
		return this.value;
	}

	public Integer getId() {
		return this.id;
	}
}
