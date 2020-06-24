
package org.generationcp.middleware.pojos.ims;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Arrays;
import java.util.List;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TransactionStatus {

	PENDING(0, "Pending"), CONFIRMED(1, "Confirmed"), CANCELLED(9, "Cancelled");

	private static final List<TransactionStatus> LIST;

	static {
		LIST = Arrays.asList(TransactionStatus.values());
	}

	private int id;
	private String status;

	TransactionStatus(int id, String status) {
		this.status = status;
		this.id = id;
	}

	public int getIntValue() {
		return this.id;
	}

	public String getValue() {
		return this.status;
	}

	public static List<TransactionStatus> getAll() {
		return LIST;
	}
}
