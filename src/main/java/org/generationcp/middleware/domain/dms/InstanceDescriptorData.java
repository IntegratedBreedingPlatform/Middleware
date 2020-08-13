package org.generationcp.middleware.domain.dms;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class InstanceDescriptorData extends InstanceVariableData {

	private Integer descriptorDataId;

	public InstanceDescriptorData() {
		// Empty constructor is needed to be able to map JSON from request
	}

	public InstanceDescriptorData(final Integer instanceId, final Integer descriptorDataId, final Integer variableId, final String value,
		final Integer categoricalValueId) {
		this.instanceId = instanceId;
		this.descriptorDataId = descriptorDataId;
		this.variableId = variableId;
		this.value = value;
		this.categoricalValueId = categoricalValueId;
	}

	public Integer getDescriptorDataId() {
		return descriptorDataId;
	}

	public void setDescriptorDataId(final Integer descriptorDataId) {
		this.descriptorDataId = descriptorDataId;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof InstanceObservationData)) {
			return false;
		}
		final InstanceDescriptorData castOther = (InstanceDescriptorData) other;
		return new EqualsBuilder().append(this.descriptorDataId, castOther.descriptorDataId).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.descriptorDataId).toHashCode();
	}

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}

}
