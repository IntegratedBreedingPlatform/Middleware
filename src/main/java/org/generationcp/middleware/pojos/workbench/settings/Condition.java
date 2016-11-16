
package org.generationcp.middleware.pojos.workbench.settings;

import java.io.Serializable;
import java.util.List;

import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.manager.Operation;

public class Condition implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private String description;
	private String property;
	private String scale;
	private String method;
	private String role;
	private String datatype;
	private String value;
	private Integer dataTypeId;
	private Double minRange;
	private Double maxRange;
	private Operation operation;
	private int id;
	private int storedIn;
	private List<ValueReference> possibleValues;

	public Condition() {
		super();
	}

	public Condition(String name, String description, String property, String scale, String method, String role, String datatype,
			String value, Integer dataTypeId, Double minRange, Double maxRange) {
		super();
		this.name = name;
		this.description = description;
		this.property = property;
		this.scale = scale;
		this.method = method;
		this.role = role;
		this.datatype = datatype;
		this.value = value;
		this.dataTypeId = dataTypeId;
		this.minRange = minRange;
		this.maxRange = maxRange;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProperty() {
		return this.property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getScale() {
		return this.scale;
	}

	public void setScale(String scale) {
		this.scale = scale;
	}

	public String getMethod() {
		return this.method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getRole() {
		return this.role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getDatatype() {
		return this.datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getDataTypeId() {
		return this.dataTypeId;
	}

	public void setDataTypeId(Integer dataTypeId) {
		this.dataTypeId = dataTypeId;
	}

	public Double getMinRange() {
		return this.minRange;
	}

	public void setMinRange(Double minRange) {
		this.minRange = minRange;
	}

	public Double getMaxRange() {
		return this.maxRange;
	}

	public void setMaxRange(Double maxRange) {
		this.maxRange = maxRange;
	}

	/**
	 * @return the operation
	 */
	public Operation getOperation() {
		return this.operation;
	}

	/**
	 * @param operation the operation to set
	 */
	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the storedIn
	 */
	public int getStoredIn() {
		return this.storedIn;
	}

	/**
	 * @param storedIn the storedIn to set
	 */
	public void setStoredIn(int storedIn) {
		this.storedIn = storedIn;
	}

	
	public List<ValueReference> getPossibleValues() {
		return possibleValues;
	}

	
	public void setPossibleValues(List<ValueReference> possibleValues) {
		this.possibleValues = possibleValues;
	}

	
}
