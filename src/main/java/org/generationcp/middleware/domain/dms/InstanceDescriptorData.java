package org.generationcp.middleware.domain.dms;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class InstanceDescriptorData extends InstanceVariableData {

	private Integer instanceDescriptorDataId;

	public InstanceDescriptorData() {
		// Empty constructor is needed to be able to map JSON from request
	}

	public InstanceDescriptorData(final Integer instanceId, final Integer instanceDescriptorDataId, final Integer variableId,
		final String value,
		final Integer categoricalValueId) {
		this.instanceId = instanceId;
		this.instanceDescriptorDataId = instanceDescriptorDataId;
		this.variableId = variableId;
		this.value = value;
		this.categoricalValueId = categoricalValueId;
	}

	public Integer getInstanceDescriptorDataId() {
		return instanceDescriptorDataId;
	}

	public void setInstanceDescriptorDataId(final Integer instanceDescriptorDataId) {
		this.instanceDescriptorDataId = instanceDescriptorDataId;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof InstanceObservationData)) {
			return false;
		}
		final InstanceDescriptorData castOther = (InstanceDescriptorData) other;
		return new EqualsBuilder().append(this.instanceDescriptorDataId, castOther.instanceDescriptorDataId).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.instanceDescriptorDataId).toHashCode();
	}

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}

}
