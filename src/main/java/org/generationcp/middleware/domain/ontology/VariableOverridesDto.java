package org.generationcp.middleware.domain.ontology;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class VariableOverridesDto {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private Integer variableId;
	private String programUuid;
	private String alias;
	private String expectedMin;
	private String expectedMax;

	public VariableOverridesDto() {

	}

	public VariableOverridesDto(final Integer id, final Integer variableId, final String programUuid, final String alias,
		final String expectedMin, final String expectedMax) {
		this.id = id;
		this.variableId = variableId;
		this.programUuid = programUuid;
		this.alias = alias;
		this.expectedMin = expectedMin;
		this.expectedMax = expectedMax;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public Integer getVariableId() {
		return this.variableId;
	}

	public void setVariableId(final Integer variableId) {
		this.variableId = variableId;
	}

	public String getProgramUuid() {
		return this.programUuid;
	}

	public void setProgramUuid(final String programUuid) {
		this.programUuid = programUuid;
	}

	public String getAlias() {
		return this.alias;
	}

	public void setAlias(final String alias) {
		this.alias = alias;
	}

	public String getExpectedMin() {
		return this.expectedMin;
	}

	public void setExpectedMin(final String expectedMin) {
		this.expectedMin = expectedMin;
	}

	public String getExpectedMax() {
		return this.expectedMax;
	}

	public void setExpectedMax(final String expectedMax) {
		this.expectedMax = expectedMax;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || this.getClass() != o.getClass()) {
			return false;
		}

		final VariableOverridesDto variableOverrides = (VariableOverridesDto) o;

		return new EqualsBuilder() //
			.append(this.id, variableOverrides.id) //
			.append(this.variableId, variableOverrides.getVariableId()) //
			.append(this.programUuid, variableOverrides.getProgramUuid()) //
			.append(this.alias, variableOverrides.getAlias()) //
			.append(this.expectedMax, variableOverrides.getExpectedMax()) //
			.append(this.expectedMin, variableOverrides.getExpectedMin()) //
			.isEquals(); //
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(this.id).append(this.variableId).append(this.programUuid).append(this.alias) //
			.append(this.expectedMax).append(this.expectedMin).toHashCode();
	}

}
