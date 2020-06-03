package org.generationcp.middleware.domain.dms;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class InstanceData {

	private Integer instanceId;
	// This can be nd_experimentprop or phenotype ID depending if variable is environmental condition
	private Integer instanceDataId;
	private Integer variableId;
	private String value;
	private Integer categoricalValueId;

	public InstanceData() {
		// Empty constructor is needed to be able to map JSON from request
	}

	public InstanceData(final Integer instanceId, final Integer instanceDataId, final Integer variableId, final String value,
		final Integer categoricalValueId) {
		this.instanceId = instanceId;
		this.instanceDataId = instanceDataId;
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

	public Integer getInstanceDataId() {
		return instanceDataId;
	}

	public void setInstanceDataId(final Integer instanceDataId) {
		this.instanceDataId = instanceDataId;
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
		if (!(other instanceof InstanceData)) {
			return false;
		}
		final InstanceData castOther = (InstanceData) other;
		return new EqualsBuilder().append(this.instanceDataId, castOther.instanceDataId).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.instanceDataId).toHashCode();
	}

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}


}
