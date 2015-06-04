
package org.generationcp.middleware.pojos.workbench.settings;

import java.io.Serializable;
import java.util.List;

import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.manager.Operation;

public class Factor implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private String description;
	private String property;
	private String scale;
	private String method;
	private String role;
	private String datatype;
	private Integer termId;
	private String treatmentLabel;
	private Operation operation;
	private int id;
	private int storedIn;
	private Integer dataTypeId;
	private List<ValueReference> possibleValues;
	private Double minRange;
	private Double maxRange;

	public Factor() {
		super();
	}

	public Factor(String name, String description, String property, String scale, String method, String role, String datatype,
			Integer termId) {
		super();
		this.name = name;
		this.description = description;
		this.property = property;
		this.scale = scale;
		this.method = method;
		this.role = role;
		this.datatype = datatype;
		this.termId = termId;
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

	public Integer getTermId() {
		return this.termId;
	}

	public void setTermId(Integer termId) {
		this.termId = termId;
	}

	public String getTreatmentLabel() {
		return this.treatmentLabel;
	}

	public void setTreatmentLabel(String treatmentLabel) {
		this.treatmentLabel = treatmentLabel;
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

	public Integer getDataTypeId() {
		return this.dataTypeId;
	}

	public void setDataTypeId(Integer dataTypeId) {
		this.dataTypeId = dataTypeId;
	}

	public List<ValueReference> getPossibleValues() {
		return this.possibleValues;
	}

	public void setPossibleValues(List<ValueReference> possibleValues) {
		this.possibleValues = possibleValues;
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

}
