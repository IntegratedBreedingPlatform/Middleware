package org.generationcp.middleware.domain.ontology;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

public class FormulaDto {

	private static final long serialVersionUID = 1L;

	private Integer formulaId;

	private Integer targetTermId;

	private List<FormulaVariable> inputs = new ArrayList<>();

	private String definition;

	private Boolean active;

	private String name;

	private String description;

	public Integer getFormulaId() {
		return formulaId;
	}

	public void setFormulaId(final Integer formulaId) {
		this.formulaId = formulaId;
	}

	public Integer getTargetTermId() {
		return targetTermId;
	}

	public void setTargetTermId(final Integer targetTermId) {
		this.targetTermId = targetTermId;
	}

	public List<FormulaVariable> getInputs() {
		return this.inputs;
	}

	public void setInputs(final List<FormulaVariable> inputs) {
		this.inputs = inputs;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(final String definition) {
		this.definition = definition;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(final Boolean active) {
		this.active = active;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public boolean isInputVariablePresent(final Integer inputCvTermId) {
		for (final FormulaVariable formulaVariable: this.inputs) {
			if (formulaVariable.getTargetTermId().equals(inputCvTermId))
				return true;
		}
		return false;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final FormulaDto formula = (FormulaDto) o;

		return new EqualsBuilder().append(formulaId, formula.formulaId).append(targetTermId, formula.targetTermId)
				.append(definition, formula.definition).append(active, formula.active).append(name, formula.name)
				.append(description, formula.description).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(formulaId).append(targetTermId).append(definition).append(active).append(name)
				.append(description).toHashCode();
	}

}
