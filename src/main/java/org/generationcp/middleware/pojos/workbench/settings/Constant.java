
package org.generationcp.middleware.pojos.workbench.settings;

import java.io.Serializable;
import java.util.List;

import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.manager.Operation;

public class Constant implements Serializable {

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
	//private boolean isTrial;
	private int id;
	private int storedIn;
	private String label;
	private List<ValueReference> possibleValues;

	public Constant() {
		super();
	}

	public Constant(
		final String name, final String description, final String property, final String scale, final String method, final String role, final String datatype,
			final String value, final Integer dataTypeId, final Double minRange, final Double maxRange) {
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
		//this.isTrial = isTrial;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getProperty() {
		return this.property;
	}

	public void setProperty(final String property) {
		this.property = property;
	}

	public String getScale() {
		return this.scale;
	}

	public void setScale(final String scale) {
		this.scale = scale;
	}

	public String getMethod() {
		return this.method;
	}

	public void setMethod(final String method) {
		this.method = method;
	}

	public String getRole() {
		return this.role;
	}

	public void setRole(final String role) {
		this.role = role;
	}

	public String getDatatype() {
		return this.datatype;
	}

	public void setDatatype(final String datatype) {
		this.datatype = datatype;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public Integer getDataTypeId() {
		return this.dataTypeId;
	}

	public void setDataTypeId(final Integer dataTypeId) {
		this.dataTypeId = dataTypeId;
	}

	public Double getMinRange() {
		return this.minRange;
	}

	public void setMinRange(final Double minRange) {
		this.minRange = minRange;
	}

	public Double getMaxRange() {
		return this.maxRange;
	}

	public void setMaxRange(final Double maxRange) {
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
	public void setOperation(final Operation operation) {
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
	public void setId(final int id) {
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
	public void setStoredIn(final int storedIn) {
		this.storedIn = storedIn;
	}

/*	*//**
	 * @return the isTrial
	 *//*
	public boolean isTrial() {
		return this.isTrial;
	}*/

/*	*//**
	 * @param isTrial the isTrial to set
	 *//*
	public void setTrial(boolean isTrial) {
		this.isTrial = isTrial;
	}*/

	/**
	 * @return the label
	 */
	public String getLabel() {
		return this.label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(final String label) {
		this.label = label;
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
	public void setPossibleValues(final List<ValueReference> possibleValues) {
		this.possibleValues = possibleValues;
	}

}
