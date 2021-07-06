package org.generationcp.middleware.domain.ontology;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
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
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}


}
