package org.generationcp.middleware.util;

import org.generationcp.middleware.domain.ontology.FormulaDto;
import org.generationcp.middleware.domain.ontology.FormulaVariable;
import org.generationcp.middleware.pojos.derived_variables.Formula;
import org.generationcp.middleware.pojos.oms.CVTerm;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class FormulaUtils {

	private FormulaUtils() { }

	public static FormulaDto convertToFormulaDto(final Formula formula) {
		final FormulaDto formulaDto = new FormulaDto();

		formulaDto.setName(formula.getName());
		formulaDto.setTarget(convertToFormulaVariable(formula.getTargetCVTerm()));
		formulaDto.setFormulaId(formula.getFormulaId());
		formulaDto.setDefinition(formula.getDefinition());
		formulaDto.setDescription(formula.getDescription());
		formulaDto.setActive(formula.getActive());

		final List<FormulaVariable> inputs = new ArrayList<>();
		for (final CVTerm cvTerm : formula.getInputs()) {
			final FormulaVariable formulaVariable = convertToFormulaVariable(cvTerm);
			formulaVariable.setTargetTermId(formulaDto.getTarget().getId());
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

	public static Formula convertToFormula(final FormulaDto formulaDto) {
		final Formula formula = new Formula();

		formula.setName(formulaDto.getName());
		final CVTerm cvterm = new CVTerm();
		cvterm.setCvTermId(formulaDto.getTarget().getId());
		formula.setTargetCVTerm(cvterm);
		formula.setFormulaId(formulaDto.getFormulaId());
		formula.setDefinition(formulaDto.getDefinition());
		formula.setDescription(formulaDto.getDescription());
		formula.setActive(formulaDto.getActive());
		formula.setInputs(convertFormulaVariableListToCVTermList(formulaDto.getInputs()));

		return formula;
	}

	private static ArrayList convertFormulaVariableListToCVTermList(final List<FormulaVariable> formulaVariables) {
		final Set<CVTerm> inputs = new LinkedHashSet<>();
		for (final FormulaVariable formulaVariable : formulaVariables) {
			final CVTerm input = new CVTerm();
			input.setCvTermId(formulaVariable.getId());
			input.setName(formulaVariable.getName());
			inputs.add(input);
		}
		return new ArrayList<>(inputs);
	}
}
