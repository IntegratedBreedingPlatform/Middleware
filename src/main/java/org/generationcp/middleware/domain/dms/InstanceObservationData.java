package org.generationcp.middleware.domain.dms;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class InstanceObservationData extends InstanceVariableData {

	private Integer instanceObservationId;

	public InstanceObservationData() {
		// Empty constructor is needed to be able to map JSON from request
	}

	public InstanceObservationData(final Integer instanceId, final Integer instanceObservationId, final Integer variableId, final String value,
		final Integer categoricalValueId) {
		this.instanceId = instanceId;
		this.instanceObservationId = instanceObservationId;
		this.variableId = variableId;
		this.value = value;
		this.categoricalValueId = categoricalValueId;
	}

	public Integer getInstanceObservationId() {
		return instanceObservationId;
	}

	public void setInstanceObservationId(final Integer instanceObservationId) {
		this.instanceObservationId = instanceObservationId;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof InstanceObservationData)) {
			return false;
		}
		final InstanceObservationData castOther = (InstanceObservationData) other;
		return new EqualsBuilder().append(this.instanceObservationId, castOther.instanceObservationId).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.instanceObservationId).toHashCode();
	}

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}

}
