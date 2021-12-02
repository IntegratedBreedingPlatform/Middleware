package org.generationcp.middleware.pojos.ims;

import java.util.Arrays;
import java.util.List;

public enum ExperimentTransactionType {

	PLANTING("Planting", 1),
	HARVESTING("Harvesting", 2);

	private String value;
	private final Integer id;

	private static final List<ExperimentTransactionType> LIST;

	static {
		LIST = Arrays.asList(ExperimentTransactionType.values());
	}

	ExperimentTransactionType(String value, Integer id) {
		this.value = value;
		this.id = id;
	}

	public String getValue() {
		return this.value;
	}

	public Integer getId() {
		return this.id;
	}

	public static List<ExperimentTransactionType> getAll() {
		return LIST;
	}

	public static ExperimentTransactionType getById(final int id) {
		return Arrays.stream(ExperimentTransactionType.values())
				.filter(type -> type.id == id)
				.findFirst()
				.orElseThrow(() -> new IllegalStateException(String.format("There is no a experiment transaction type with id %s.", id)));
	}

}
