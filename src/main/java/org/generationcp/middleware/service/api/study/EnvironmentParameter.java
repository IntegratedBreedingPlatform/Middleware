package org.generationcp.middleware.service.api.study;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
@JsonInclude(JsonInclude.Include.NON_NULL) @JsonPropertyOrder({"parameterName", "description", "unit", "unitPUI", "value", "valuePUI"})
public class EnvironmentParameter {

	private String description;

	private String parameterName;

	private String parameterPUI;

	private String unit;

	private Integer unitPUI;

	private String value;

	private String valuePUI;

	public EnvironmentParameter() {
	}

	public EnvironmentParameter(final String description, final String parameterName, final String parameterPUI, final String unit,
		final Integer unitPUI, final String value, final String valuePUI) {
		this.description = description;
		this.parameterName = parameterName;
		this.parameterPUI = parameterPUI;
		this.unit = unit;
		this.unitPUI = unitPUI;
		this.value = value;
		this.valuePUI = valuePUI;
	}

	public EnvironmentParameter(final MeasurementVariable measurementVariable) {
		this.description = measurementVariable.getDescription();
		this.parameterName = measurementVariable.getName();
		this.parameterPUI = String.valueOf(measurementVariable.getTermId());
		this.unit = measurementVariable.getScale();
		this.unitPUI = measurementVariable.getScaleId();
		this.value = measurementVariable.getValue();
	}

	public String getDescription() {
		return this.description;
	}


	public EnvironmentParameter setDescription(final String description) {
		this.description = description;
		return this;
	}

	public String getParameterName() {
		return this.parameterName;
	}

	public EnvironmentParameter setParameterName(final String parameterName) {
		this.parameterName = parameterName;
		return this;
	}

	public String getParameterPUI() {
		return this.parameterPUI;
	}

	public EnvironmentParameter setParameterPUI(final String parameterPUI) {
		this.parameterPUI = parameterPUI;
		return this;
	}

	public String getUnit() {
		return this.unit;
	}

	public EnvironmentParameter setUnit(final String unit) {
		this.unit = unit;
		return this;
	}

	public Integer getUnitPUI() {
		return this.unitPUI;
	}

	public EnvironmentParameter setUnitPUI(final Integer unitPUI) {
		this.unitPUI = unitPUI;
		return this;
	}

	public String getValue() {
		return this.value;
	}

	public EnvironmentParameter setValue(final String value) {
		this.value = value;
		return this;
	}

	public String getValuePUI() {
		return this.valuePUI;
	}

	public EnvironmentParameter setValuePUI(final String valuePUI) {
		this.valuePUI = valuePUI;
		return this;
	}

}
