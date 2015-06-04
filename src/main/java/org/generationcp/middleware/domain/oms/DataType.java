
package org.generationcp.middleware.domain.oms;

import java.util.HashMap;
import java.util.Map;

public enum DataType {

	CATEGORICAL_VARIABLE(1130, "Categorical"), NUMERIC_VARIABLE(1110, "Numeric"), DATE_TIME_VARIABLE(1117, "Date"), CHARACTER_VARIABLE(
			1120, "Character");

	private Integer id;
	private String name;

	DataType(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	private static final Map<Integer, DataType> byId = new HashMap<>();
	private static final Map<String, DataType> byName = new HashMap<>();

	static {
		for (DataType e : DataType.values()) {
			if (DataType.byId.put(e.getId(), e) != null) {
				throw new IllegalArgumentException("duplicate id: " + e.getId());
			}

			if (DataType.byName.put(e.getName(), e) != null) {
				throw new IllegalArgumentException("duplicate name: " + e.getName());
			}
		}
	}

	public Integer getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public static DataType getById(Integer id) {
		return DataType.byId.get(id);
	}

	public static DataType getByName(String name) {
		return DataType.byName.get(name);
	}

	@Override
	public String toString() {
		return "DataType{" + "id=" + this.id + ", name='" + this.name + '\'' + '}';
	}
}
