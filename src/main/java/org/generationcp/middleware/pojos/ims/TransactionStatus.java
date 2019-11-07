
package org.generationcp.middleware.pojos.ims;

public enum TransactionStatus {

	//  Transaction status: 0=Anticipated (Deposit or Reserved), 1=Confirmed (Stored or Retrieved), 9=Cancelled Transaction

	ANTICIPATED(0), COMMITTED(1), CANCELLED(9);

	private int status;

	private TransactionStatus(int status) {
		this.status = status;
	}

	public int getIntValue() {
		return this.status;
	}

}
