package org.generationcp.middleware.domain.dms;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class ObservationData {

	private Integer instanceId;
	// This can be geolocationPropertyId or phenotype ID depending if variable is environmental detail or condition.
	private Integer observationId;
	private Integer variableId;
	private String value;
	private Integer categoricalValueId;

	public ObservationData() {
		// Empty constructor is needed to be able to map JSON from request
	}

	public ObservationData(final Integer instanceId, final Integer observationId, final Integer variableId, final String value,
		final Integer categoricalValueId) {
		this.instanceId = instanceId;
		this.observationId = observationId;
		this.variableId = variableId;
		this.value = value;
		this.categoricalValueId = categoricalValueId;
	}

	public Integer getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(final Integer instanceId) {
		this.instanceId = instanceId;
	}

	public Integer getObservationId() {
		return observationId;
	}

	public void setObservationId(final Integer observationId) {
		this.observationId = observationId;
	}

	public Integer getVariableId() {
		return variableId;
	}

	public void setVariableId(final Integer variableId) {
		this.variableId = variableId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public Integer getCategoricalValueId() {
		return categoricalValueId;
	}

	public void setCategoricalValueId(final Integer categoricalValueId) {
		this.categoricalValueId = categoricalValueId;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof ObservationData)) {
			return false;
		}
		final ObservationData castOther = (ObservationData) other;
		return new EqualsBuilder().append(this.observationId, castOther.observationId).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.observationId).toHashCode();
	}

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}

}
