
package org.generationcp.middleware.pojos.workbench.settings;

import java.io.Serializable;
import java.util.List;

import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.util.Debug;

public class Variate implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private String description;
	private String property;
	private String scale;
	private String method;
	private String role;
	private String variableType;
	private String datatype;
	private Integer dataTypeId;
	private List<ValueReference> possibleValues;
	private Double minRange;
	private Double maxRange;
	private Operation operation;
	private int id;
	private int storedIn;

	public Variate() {
		super();
	}

	public Variate(String name, String description, String property, String scale, String method, String role, String datatype,
			Integer dataTypeId, List<ValueReference> possibleValues, Double minRange, Double maxRange) {
		super();
		this.name = name;
		this.description = description;
		this.property = property;
		this.scale = scale;
		this.method = method;
		this.role = role;
		this.datatype = datatype;
		this.dataTypeId = dataTypeId;
		this.possibleValues = possibleValues;
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

	public String getVariableType() {
		return variableType;
	}

	public void setVariableType(String variableType) {
		this.variableType = variableType;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	/**
	 * @return the dataTypeId
	 */
	public Integer getDataTypeId() {
		return this.dataTypeId;
	}

	/**
	 * @param dataTypeId the dataTypeId to set
	 */
	public void setDataTypeId(Integer dataTypeId) {
		this.dataTypeId = dataTypeId;
	}

	/**
	 * @return the possibleValues
	 */
	public List<ValueReference> getPossibleValues() {
		return this.possibleValues;
	}

	/**
	 * @param possibleValues the possibleValues to set
	 */
	public void setPossibleValues(List<ValueReference> possibleValues) {
		this.possibleValues = possibleValues;
	}

	/**
	 * @return the minRange
	 */
	public Double getMinRange() {
		return this.minRange;
	}

	/**
	 * @param minRange the minRange to set
	 */
	public void setMinRange(Double minRange) {
		this.minRange = minRange;
	}

	/**
	 * @return the maxRange
	 */
	public Double getMaxRange() {
		return this.maxRange;
	}

	/**
	 * @param maxRange the maxRange to set
	 */
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

	@Override
	public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Variate [name=");
        builder.append(this.name);
        builder.append(", description=");
        builder.append(this.description);
        builder.append(", property=");
        builder.append(this.property);
        builder.append(", scale=");
        builder.append(this.scale);
        builder.append(", method=");
        builder.append(this.method);
        builder.append(", role=");
        builder.append(this.role);
        builder.append(", variableType=");
        builder.append(this.variableType);
        builder.append(", datatype=");
        builder.append(this.datatype);
        builder.append(", dataTypeId=");
        builder.append(this.dataTypeId);
        builder.append(", possibleValues=");
        builder.append(this.possibleValues);
        builder.append(", minRange=");
        builder.append(this.minRange);
        builder.append(", maxRange=");
        builder.append(this.maxRange);
        builder.append(", operation=");
        builder.append(this.operation);
        builder.append(", id=");
        builder.append(this.id);
        builder.append(", storedIn=");
        builder.append(this.storedIn);
        builder.append("]");
        return builder.toString();
	}

	public void print(int indent) {
		Debug.println(indent, "Variate: ");
		Debug.println(indent + 3, "name " + this.name);
		Debug.println(indent + 3, "description " + this.description);
		Debug.println(indent + 3, "property: " + this.property);
		Debug.println(indent + 3, "scale: " + this.scale);
		Debug.println(indent + 3, "method: " + this.method);
		Debug.println(indent + 3, "role: " + this.role);
		Debug.println(indent + 3, "variableType: " + this.variableType);
		Debug.println(indent + 3, "datatype: " + this.datatype);
		Debug.println(indent + 3, "dataTypeId: " + this.dataTypeId);
		Debug.println(indent + 3, "possibleValues: " + this.possibleValues);
		Debug.println(indent + 3, "minRange: " + this.minRange);
		Debug.println(indent + 3, "maxRange: " + this.maxRange);
		Debug.println(indent + 3, "operation: " + this.operation);
		Debug.println(indent + 3, "id: " + this.id);
		Debug.println(indent + 3, "storedIn: " + this.storedIn);
	}
}
