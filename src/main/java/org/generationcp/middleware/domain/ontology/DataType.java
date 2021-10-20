
package org.generationcp.middleware.domain.ontology;

import java.util.HashMap;
import java.util.Map;

public enum DataType {

	CATEGORICAL_VARIABLE(1130, "Categorical", false, "C", "Nominal"),
	NUMERIC_VARIABLE(1110, "Numeric", false, "N", "Numerical"),
	NUMERIC_DBID_VARIABLE(1118, "Numeric DBID Variable", true, "N", null),
	DATE_TIME_VARIABLE(1117, "Date", false, "D", "Date"),
	CHARACTER_VARIABLE(1120, "Character", false, "T", "Text"),

	//Special Data types
	PERSON(1131, "Person", true, "C", null),
	LOCATION(1132, "Location", true, "C", null),
	STUDY(1133, "Study", true, "C", null),
	DATASET(1134, "Dataset", true, "C", null),
	GERMPLASM_LIST(1135, "Germplasm List", true, "C", null),
	BREEDING_METHOD(1136, "Breeding Method", true, "C", null);

	private static final Map<Integer, DataType> byId = new HashMap<>();
	private static final Map<String, DataType> byName = new HashMap<>();
	private static final Map<String, DataType> byCode = new HashMap<>();
	private static final Map<String, DataType> byBrapiName = new HashMap<>();

	private final Integer id;
	private final String name;
	private final String brapiName;
	private final boolean systemDataType;
	private final String dataTypeCode;

	DataType(final Integer id, final String name, final boolean systemDataType, final String dataTypeCode, final String brapiName) {
		this.id = id;
		this.name = name;
		this.systemDataType = systemDataType;
		this.dataTypeCode = dataTypeCode;
		this.brapiName = brapiName;
	}

	static {
		for (final DataType e : DataType.values()) {
			if (DataType.byId.put(e.getId(), e) != null) {
				throw new IllegalArgumentException("duplicate id: " + e.getId());
			}

			if (DataType.byName.put(e.getName(), e) != null) {
				throw new IllegalArgumentException("duplicate name: " + e.getName());
			}

			if (DataType.byCode.put(e.getDataTypeCode(), e) != null && !e.isSystemDataType()) {
				throw new IllegalArgumentException("duplicate code: " + e.getName());
			}

			if (DataType.byBrapiName.put(e.getBrapiName(), e) != null && !e.isSystemDataType()) {
				throw new IllegalArgumentException("duplicate brapi name: " + e.getBrapiName());
			}
		}
	}

	public Integer getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getBrapiName() {
		return this.brapiName;
	}

	public boolean isSystemDataType() {
		return this.systemDataType;
	}

	/**
	 * Gets the single character code that represents a data type.
	 * "C" for Categorical
	 * "N" for Numeric
	 * "D" for Date
	 * "T" for Character/Text
	 * For Special Data Types, the default data type code is letter 'C'.
	 *
	 * @return
	 */
	public String getDataTypeCode() {
		return this.dataTypeCode;
	}

	public static DataType getById(final Integer id) {
		return DataType.byId.get(id);
	}

	public static DataType getByName(final String name) {
		return DataType.byName.get(name);
	}

	public static DataType getByCode(final String code) {
		return DataType.byCode.get(code);
	}

	public static DataType getByBrapiName(final String brapiName) {
		return DataType.byBrapiName.get(brapiName);
	}

	@Override
	public String toString() {
		return "DataType{" +
			"id=" + this.id +
			", name='" + this.name + '\'' +
			", systemDataType=" + this.systemDataType +
			", dataTypeCode=" + this.dataTypeCode +
			"} " + super.toString();
	}
}
