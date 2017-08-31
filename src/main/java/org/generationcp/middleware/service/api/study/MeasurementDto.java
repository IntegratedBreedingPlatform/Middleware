
package org.generationcp.middleware.service.api.study;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * A measurementVariable and its associated measurement.
 *
 */
public class MeasurementDto {

	private MeasurementVariableDto measurementVariable;

	private Integer phenotypeId;

	private String variableValue;

	public MeasurementDto(final MeasurementVariableDto measurementVariable, final Integer phenotypeId, final String variableValue) {
		this.phenotypeId = phenotypeId;
		this.variableValue = variableValue;
		this.measurementVariable = measurementVariable;
	}

	public MeasurementDto(final String variableValue) {
		this.variableValue = variableValue;
	}

	/**
	 * @return the measurementVariable
	 */
	public MeasurementVariableDto getMeasurementVariable() {
		return this.measurementVariable;
	}

	/**
	 * @param measurementVariable the measurementVariable to set
	 */
	public void setMeasurementVariable(MeasurementVariableDto measurementVariable) {
		this.measurementVariable = measurementVariable;
	}

	/**
	 * @return the phenotypeId
	 */
	public Integer getPhenotypeId() {
		return this.phenotypeId;
	}

	/**
	 * @param phenotypeId the phenotypeId to set
	 */
	public void setPhenotypeId(Integer phenotypeId) {
		this.phenotypeId = phenotypeId;
	}

	/**
	 * @return the triatValue
	 */
	public String getVariableValue() {
		return this.variableValue;
	}

	/**
	 * @param variableValue the variableValue to set
	 */
	public void setVariableValue(String variableValue) {
		this.variableValue = variableValue;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof MeasurementDto))
			return false;
		MeasurementDto castOther = (MeasurementDto) other;
		return new EqualsBuilder().append(measurementVariable, castOther.measurementVariable).append(phenotypeId, castOther.phenotypeId)
				.append(variableValue, castOther.variableValue).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(measurementVariable).append(phenotypeId).append(variableValue).toHashCode();
	}

	@Override
	public String toString() {
		return "MeasurementDto [measurementVariable=" + measurementVariable + ", phenotypeId=" + phenotypeId + ", variableValue=" + variableValue + "]";
	}
}
