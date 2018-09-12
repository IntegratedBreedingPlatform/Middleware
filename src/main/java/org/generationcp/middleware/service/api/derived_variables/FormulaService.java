package org.generationcp.middleware.service.api.derived_variables;

import com.google.common.base.Optional;
import org.generationcp.middleware.domain.ontology.FormulaDto;
import org.generationcp.middleware.domain.ontology.FormulaVariable;

import java.util.List;
import java.util.Set;

/**
 * Formula used by Derived Variables
 */
public interface FormulaService {

	public Optional<FormulaDto> getById(Integer formulaId);

	public Optional<FormulaDto> getByTargetId(Integer targetId);

	public List<FormulaDto> getByTargetIds(Set<Integer> variableIds);

	List<FormulaDto> getByInputId(Integer inputId);

	/**
	 * Gets all FormulaVariables from a given derived trait variableIds including the FormulaVariables of an argument variable if it is itself
	 * a derived trait.
	 *
	 * @param variableIds
	 * @return
	 */
	Set<FormulaVariable> getAllFormulaVariables(final Set<Integer> variableIds);

	FormulaDto save(final FormulaDto formulaDto);

	void delete(final Integer formulaId);

	FormulaDto update(final FormulaDto formulaDto);

}
