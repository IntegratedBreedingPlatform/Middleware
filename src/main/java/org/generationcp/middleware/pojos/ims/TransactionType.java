
package org.generationcp.middleware.pojos.ims;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Arrays;
import java.util.List;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TransactionType {

	WITHDRAWAL("Withdrawal", 1),
	DISCARD("Discard", 2),
	ADJUSTMENT("Adjustment", 3),
	DEPOSIT("Deposit", 4);

	private final Integer id;
	private String value;

	private static final List<TransactionType> LIST;

	static {
		LIST = Arrays.asList(TransactionType.values());
	}

	TransactionType(String type, Integer id) {
		this.value = type;
		this.id = id;
	}

	public String getValue() {
		return this.value;
	}

	public Integer getId() {
		return this.id;
	}

	public static List<TransactionType> getAll() {
		return LIST;
	}
}
