package org.generationcp.middleware.util;

import org.generationcp.middleware.domain.ontology.FormulaDto;
import org.generationcp.middleware.domain.ontology.FormulaVariable;
import org.generationcp.middleware.pojos.derived_variables.Formula;
import org.generationcp.middleware.pojos.oms.CVTerm;

import java.util.ArrayList;
import java.util.List;

public final class FormulaUtils {

	private FormulaUtils() { }

	public static FormulaDto convertToFormulaDto(final Formula formula) {
		final FormulaDto formulaDto = new FormulaDto();

		formulaDto.setName(formula.getName());
		formulaDto.setTargetTermId(formula.getTargetCVTerm().getCvTermId());
		formulaDto.setFormulaId(formula.getFormulaId());
		formulaDto.setDefinition(formula.getDefinition());
		formulaDto.setDescription(formula.getDescription());
		formulaDto.setActive(formula.getActive());

		final List<FormulaVariable> inputs = new ArrayList<>();
		for (final CVTerm cvTerm : formula.getInputs()) {
			final FormulaVariable formulaVariable = convertToFormulaVariable(cvTerm);
			formulaVariable.setTargetTermId(formulaDto.getTargetTermId());
			inputs.add(formulaVariable);
		}
		formulaDto.setInputs(inputs);

		return formulaDto;
	}

	public static FormulaVariable convertToFormulaVariable(final CVTerm cvTerm) {
		final FormulaVariable formulaVariable = new FormulaVariable();
		formulaVariable.setId(cvTerm.getCvTermId());
		formulaVariable.setName(cvTerm.getName());
		return formulaVariable;
	}

	public static Formula convertToFormula(FormulaDto formulaDto) {
		final Formula formula = new Formula();

		formula.setName(formulaDto.getName());
		final CVTerm cvterm = new CVTerm();
		cvterm.setCvTermId(formulaDto.getTargetTermId());
		formula.setTargetCVTerm(cvterm);
		formula.setFormulaId(formulaDto.getFormulaId());
		formula.setDefinition(formulaDto.getDefinition());
		formula.setDescription(formulaDto.getDescription());
		formula.setActive(formulaDto.getActive());

		final List<CVTerm> inputs = new ArrayList<>();
		for (FormulaVariable formulaVariable : formulaDto.getInputs()) {
			final CVTerm input = new CVTerm();
			input.setCvTermId(formulaVariable.getId());
			input.setName(formulaVariable.getName());
			inputs.add(input);
		}
		formula.setInputs(inputs);

		return formula;
	}
}
