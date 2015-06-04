
package org.generationcp.middleware.pojos.ims;

public enum LotStatus {

	ACTIVE(0), CLOSED(1);

	private int status;

	private LotStatus(int status) {
		this.status = status;
	}

	public int getIntValue() {
		return this.status;
	}
}
