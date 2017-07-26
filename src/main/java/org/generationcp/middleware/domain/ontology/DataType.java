
package org.generationcp.middleware.domain.ontology;

import java.util.HashMap;
import java.util.Map;

public enum DataType {

	CATEGORICAL_VARIABLE(1130, "Categorical", false),
	NUMERIC_VARIABLE(1110, "Numeric", false),
	DATE_TIME_VARIABLE(1117, "Date", false),
	CHARACTER_VARIABLE(1120, "Character", false),
	NUMERIC_DBID_VARIABLE(1118, "Numeric DBID", false),

	//Special Data types
	PERSON(1131, "Person", true),
	LOCATION(1132, "Location", true),
	STUDY(1133, "Study", true),
	DATASET(1134, "Dataset", true),
	GERMPLASM_LIST(1135, "Germplasm List", true),
	BREEDING_METHOD(1136, "Breeding Method", true);

	private Integer id;
	private String name;
	private boolean systemDataType;

	DataType(Integer id, String name, boolean systemDataType) {
		this.id = id;
		this.name = name;
		this.systemDataType = systemDataType;
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

	public boolean isSystemDataType(){
		return this.systemDataType;
	}

	public static DataType getById(Integer id) {
		return DataType.byId.get(id);
	}

	public static DataType getByName(String name) {
		return DataType.byName.get(name);
	}

	@Override public String toString() {
		return "DataType{" +
				"id=" + id +
				", name='" + name + '\'' +
				", systemDataType=" + systemDataType +
				"} " + super.toString();
	}
}
