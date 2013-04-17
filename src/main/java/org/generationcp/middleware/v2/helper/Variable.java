package org.generationcp.middleware.v2.helper;

import java.util.Arrays;
import java.util.List;

import org.generationcp.middleware.v2.pojos.CVTermId;

public class Variable {

	protected static final List<Integer> OBSERVATION_TYPES = Arrays.asList(
			CVTermId.OBSERVATION_VARIATE.getId(), CVTermId.CATEGORICAL_VARIATE.getId()
	);

	private Integer standardVariableId;
	private String name;
	private String description;
	private String property;
	private String method;
	private String scale;
	private String dataType;
	private Integer determinantId;
	
	public boolean isFactor() {
		return !isObservation();
	}
	
	public boolean isObservation() {
		return OBSERVATION_TYPES.contains(determinantId);
	}
	
	public Integer getStandardVariableId() {
		return standardVariableId;
	}
	
	public void setStandardVariableId(Integer standardVariableId) {
		this.standardVariableId = standardVariableId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getScale() {
		return scale;
	}

	public void setScale(String scale) {
		this.scale = scale;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public Integer getDeterminantId() {
		return determinantId;
	}

	public void setDeterminantId(Integer determinantId) {
		this.determinantId = determinantId;
	}

}
