package org.generationcp.middleware.pojos.ims;

import java.util.Arrays;
import java.util.List;

public enum ExperimentTransactionType {

	PLANTING("Planting", 1),
	HARVESTING("Harvesting", 2);

	private final Integer id;
	private String value;

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
}
