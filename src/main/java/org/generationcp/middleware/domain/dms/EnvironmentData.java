package org.generationcp.middleware.domain.dms;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class EnvironmentData {

	private Integer environmentId;
	// This can be nd_experimentprop or phenotype ID depending if variable is environmental condition
	private Integer environmentDataId;
	private Integer variableId;
	private String value;
	private Integer categoricalValueId;
	private Boolean variableIsEnvironmentalCondition;

	public EnvironmentData() {
		// Empty constructor is needed to be able to map JSON from request
	}

	public EnvironmentData(final Integer environmentId, final Integer environmentDataId, final Integer variableId, final String value,
		final Integer categoricalValueId, final Boolean variableIsEnvironmentalCondition) {
		this.environmentId = environmentId;
		this.environmentDataId = environmentDataId;
		this.variableId = variableId;
		this.value = value;
		this.categoricalValueId = categoricalValueId;
		this.variableIsEnvironmentalCondition = variableIsEnvironmentalCondition;
	}

	public Integer getEnvironmentId() {
		return environmentId;
	}

	public void setEnvironmentId(final Integer environmentId) {
		this.environmentId = environmentId;
	}

	public Integer getEnvironmentDataId() {
		return environmentDataId;
	}

	public void setEnvironmentDataId(final Integer environmentDataId) {
		this.environmentDataId = environmentDataId;
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

	public Boolean getVariableIsEnvironmentalCondition() {
		return variableIsEnvironmentalCondition;
	}

	public void setVariableIsEnvironmentalCondition(final Boolean variableIsEnvironmentalCondition) {
		this.variableIsEnvironmentalCondition = variableIsEnvironmentalCondition;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof EnvironmentData)) {
			return false;
		}
		final EnvironmentData castOther = (EnvironmentData) other;
		return new EqualsBuilder().append(this.environmentDataId, castOther.environmentDataId).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.environmentDataId).toHashCode();
	}

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}


}
