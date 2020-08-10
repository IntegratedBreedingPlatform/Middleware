package org.generationcp.middleware.domain.dms;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class ObservationData extends InstanceVariableData {

	private Integer observationDataId;

	public ObservationData() {
		// Empty constructor is needed to be able to map JSON from request
	}

	public ObservationData(final Integer instanceId, final Integer observationDataId, final Integer variableId, final String value,
		final Integer categoricalValueId) {
		this.instanceId = instanceId;
		this.observationDataId = observationDataId;
		this.variableId = variableId;
		this.value = value;
		this.categoricalValueId = categoricalValueId;
	}

	public Integer getObservationDataId() {
		return observationDataId;
	}

	public void setObservationDataId(final Integer observationDataId) {
		this.observationDataId = observationDataId;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof ObservationData)) {
			return false;
		}
		final ObservationData castOther = (ObservationData) other;
		return new EqualsBuilder().append(this.observationDataId, castOther.observationDataId).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.observationDataId).toHashCode();
	}

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}

}
