
package org.generationcp.middleware.service.api.study;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.generationcp.middleware.pojos.dms.Phenotype;

import java.util.Objects;

/**
 * A measurementVariable and its associated measurement.
 *
 */
public class MeasurementDto {

	private MeasurementVariableDto measurementVariable;

	private Integer phenotypeId;

	private String variableValue;

	private Phenotype.ValueStatus valueStatus;

	public MeasurementDto(final MeasurementVariableDto measurementVariable, final Integer phenotypeId, final String variableValue, final
		Phenotype.ValueStatus valueStatus) {
		this.phenotypeId = phenotypeId;
		this.variableValue = variableValue;
		this.measurementVariable = measurementVariable;
		this.valueStatus = valueStatus;
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
	public void setMeasurementVariable(final MeasurementVariableDto measurementVariable) {
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
	public void setPhenotypeId(final Integer phenotypeId) {
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
	public void setVariableValue(final String variableValue) {
		this.variableValue = variableValue;
	}

	public Phenotype.ValueStatus getValueStatus() {
		return this.valueStatus;
	}

	public void setValueStatus(final Phenotype.ValueStatus valueStatus) {
		this.valueStatus = valueStatus;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || this.getClass() != o.getClass())
			return false;
		final MeasurementDto that = (MeasurementDto) o;
		return Objects.equals(this.measurementVariable, that.measurementVariable) &&
			Objects.equals(this.phenotypeId, that.phenotypeId) &&
			Objects.equals(this.variableValue, that.variableValue) &&
			this.valueStatus == that.valueStatus;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.measurementVariable, this.phenotypeId, this.variableValue, this.valueStatus);
	}

	@Override
	public String toString() {
		return "MeasurementDto{" +
			"measurementVariable=" + this.measurementVariable +
			", phenotypeId=" + this.phenotypeId +
			", variableValue='" + this.variableValue + '\'' +
			", valueStatus=" + this.valueStatus +
			'}';
	}
}
