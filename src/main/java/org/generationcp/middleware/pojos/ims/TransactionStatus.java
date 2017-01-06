
package org.generationcp.middleware.pojos.ims;

public enum TransactionStatus {

	ANTICIPATED(0), DEPOSITED(0), RESERVED(0)

	, CONFIRMED(1), STORED(1), RETRIEVED(1), COMMITTED(1)

	, CANCELLED(9);

	private int status;

	private TransactionStatus(int status) {
		this.status = status;
	}

	public int getIntValue() {
		return this.status;
	}

}
