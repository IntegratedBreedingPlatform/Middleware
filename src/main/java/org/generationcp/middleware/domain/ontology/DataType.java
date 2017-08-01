
package org.generationcp.middleware.domain.ontology;

import java.util.HashMap;
import java.util.Map;

public enum DataType {

	CATEGORICAL_VARIABLE(1130, "Categorical", false, "C"),
	NUMERIC_VARIABLE(1110, "Numeric", false, "N"),
	DATE_TIME_VARIABLE(1117, "Date", false, "D"),
	CHARACTER_VARIABLE(1120, "Character", false, "T"),

	//Special Data types
	PERSON(1131, "Person", true, "T"),
	LOCATION(1132, "Location", true, "T"),
	STUDY(1133, "Study", true, "T"),
	DATASET(1134, "Dataset", true, "T"),
	GERMPLASM_LIST(1135, "Germplasm List", true, "T"),
	BREEDING_METHOD(1136, "Breeding Method", true, "T");

	private static final Map<Integer, DataType> byId = new HashMap<>();
	private static final Map<String, DataType> byName = new HashMap<>();
	private static final Map<String, DataType> byCode = new HashMap<>();

	private Integer id;
	private String name;
	private boolean systemDataType;
	private String dataTypeCode;

	DataType(Integer id, String name, boolean systemDataType, String dataTypeCode) {
		this.id = id;
		this.name = name;
		this.systemDataType = systemDataType;
		this.dataTypeCode = dataTypeCode;
	}

	static {
		for (DataType e : DataType.values()) {
			if (DataType.byId.put(e.getId(), e) != null) {
				throw new IllegalArgumentException("duplicate id: " + e.getId());
			}

			if (DataType.byName.put(e.getName(), e) != null) {
				throw new IllegalArgumentException("duplicate name: " + e.getName());
			}

			if (DataType.byCode.put(e.getDataTypeCode(), e) != null && !e.isSystemDataType()) {
				throw new IllegalArgumentException("duplicate code: " + e.getName());
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

	/**
	 * Gets the single character code that represents a data type.
	 * "C" for Categorical
	 * "N" for Numeric
	 * "D" for Date
	 * "T" for Character/Text
	 * For Special Data Types, the default data type code is "T" (Character/Text).
	 * @return
	 */
	public String getDataTypeCode() { return  this.dataTypeCode; }

	public static DataType getById(Integer id) {
		return DataType.byId.get(id);
	}

	public static DataType getByName(String name) {
		return DataType.byName.get(name);
	}

	public static DataType getByCode(String code) {
		return DataType.byCode.get(code);
	}

	@Override public String toString() {
		return "DataType{" +
				"id=" + id +
				", name='" + name + '\'' +
				", systemDataType=" + systemDataType +
				", dataTypeCode=" + dataTypeCode +
				"} " + super.toString();
	}
}
