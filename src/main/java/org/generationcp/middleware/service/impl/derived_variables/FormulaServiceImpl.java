package org.generationcp.middleware.service.impl.derived_variables;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.domain.ontology.FormulaDto;
import org.generationcp.middleware.domain.ontology.FormulaVariable;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.derived_variables.FormulaDaoFactory;
import org.generationcp.middleware.pojos.derived_variables.Formula;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.service.api.derived_variables.FormulaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Optional;

@Transactional
@Service
public class FormulaServiceImpl implements FormulaService {


	private HibernateSessionProvider sessionProvider;
	private FormulaDaoFactory formulaDaoFactory;

	public FormulaServiceImpl() {
	}

	public FormulaServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
		this.formulaDaoFactory = new FormulaDaoFactory(this.sessionProvider);
	}

	@Override
	public Optional<FormulaDto> getByTargetId(final Integer targetId) {
		final Formula formula = this.formulaDaoFactory.getFormulaDAO().getByTargetVariableId(targetId);
		if (formula != null) {
			return Optional.of(convertToFormulaDto(formula));
		}
		return Optional.absent();
	}

	@Override
	public List<FormulaDto> getByTargetIds(final Set<Integer> variableIds) {

		final List<FormulaDto> formulaDtos = new ArrayList<>();
		final List<Formula> formulas = this.formulaDaoFactory.getFormulaDAO().getByTargetVariableIds(variableIds);
		for (final Formula formula : formulas) {
			formulaDtos.add(convertToFormulaDto(formula));
		}
		return formulaDtos;
	}

	@Override
	public Set<FormulaVariable> getAllFormulaVariables(final Set<Integer> variableIds) {
		final Set<FormulaVariable> formulaVariables = new HashSet<>();
		for (final FormulaDto formulaDto : getByTargetIds(variableIds)) {
			formulaVariables.addAll(formulaDto.getInputs());
			fillFormulaVariables(formulaDto, formulaVariables);
		}
		return formulaVariables;
	}

	protected void fillFormulaVariables(final FormulaDto formulaDto, final Set<FormulaVariable> formulaVariables) {
		for (final FormulaVariable formulaVariable : formulaDto.getInputs()) {
			final Optional<FormulaDto> formulaOptional = getByTargetId(formulaVariable.getId());
			if (formulaOptional.isPresent()) {
				formulaVariables.addAll(formulaOptional.get().getInputs());
				if (!formulaVariables.contains(formulaOptional.get())) {
					// If the argument variable is itself a derived trait, include its argument variables.
					fillFormulaVariables(formulaOptional.get(), formulaVariables);
				}
			}
		}
	}

	protected FormulaDto convertToFormulaDto(final Formula formula) {
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

	protected FormulaVariable convertToFormulaVariable(final CVTerm cvTerm) {
		final FormulaVariable formulaVariable = new FormulaVariable();
		formulaVariable.setId(cvTerm.getCvTermId());
		formulaVariable.setName(cvTerm.getName());
		return formulaVariable;
	}

	public HibernateSessionProvider getSessionProvider() {
		return this.sessionProvider;
	}

	public void setSessionProvider(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

	
	protected void setFormulaDaoFactory(FormulaDaoFactory formulaDaoFactory) {
		this.formulaDaoFactory = formulaDaoFactory;
	}

}
