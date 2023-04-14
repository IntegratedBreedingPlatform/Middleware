package org.generationcp.middleware.domain.genotype;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class SampleGenotypeImportRequestDto {

	private String sampleUID;

	private String variableId;

	private String value;

	public String getSampleUID() {
		return this.sampleUID;
	}

	public void setSampleUID(final String sampleUID) {
		this.sampleUID = sampleUID;
	}

	public String getVariableId() {
		return this.variableId;
	}

	public void setVariableId(final String variableId) {
		this.variableId = variableId;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(final String value) {
		this.value = value;
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
