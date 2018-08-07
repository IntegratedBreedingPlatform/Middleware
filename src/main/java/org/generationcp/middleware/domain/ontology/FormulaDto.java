package org.generationcp.middleware.domain.ontology;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

public class FormulaDto {

	private static final long serialVersionUID = 1L;

	private Integer formulaId;
	private FormulaVariable target;
	//TODO Perhaps we can keep a an internal copy of inputs as a map (Improvement)
	private List<FormulaVariable> inputs = new ArrayList<>();
	private String definition;
	private Boolean active;
	private String name;
	private String description;

	public Integer getFormulaId() {
		return this.formulaId;
	}

	public void setFormulaId(final Integer formulaId) {
		this.formulaId = formulaId;
	}

	public FormulaVariable getTarget() {
		return this.target;
	}

	public void setTarget(final FormulaVariable target) {
		this.target = target;
	}

	public List<FormulaVariable> getInputs() {
		return this.inputs;
	}

	public void setInputs(final List<FormulaVariable> inputs) {
		this.inputs = inputs;
	}

	public String getDefinition() {
		return this.definition;
	}

	public void setDefinition(final String definition) {
		this.definition = definition;
	}

	public Boolean getActive() {
		return this.active;
	}

	public void setActive(final Boolean active) {
		this.active = active;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public boolean isInputVariablePresent(final Integer inputCvTermId) {
		for (final FormulaVariable formulaVariable: this.inputs) {
			if (Integer.valueOf(formulaVariable.getId()).equals(inputCvTermId))
				return true;
		}
		return false;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || this.getClass() != o.getClass()) {
			return false;
		}

		final FormulaDto formula = (FormulaDto) o;

		return new EqualsBuilder() //
			.append(this.formulaId, formula.formulaId) //
			.append(this.target.getId(), formula.getTarget().getId()) //
			.append(this.definition, formula.definition) //
			.append(this.active, formula.active) //
			.append(this.name, formula.name) //
			.append(this.description, formula.description) //
			.isEquals(); //
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(this.formulaId).append(this.target).append(this.definition).append(this.active).append(
			this.name)
				.append(this.description).toHashCode();
	}

}
