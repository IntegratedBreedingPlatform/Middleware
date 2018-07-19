package org.generationcp.middleware.service.impl.derived_variables;

import com.google.common.base.Optional;
import org.generationcp.middleware.domain.ontology.FormulaDto;
import org.generationcp.middleware.domain.ontology.FormulaVariable;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.derived_variables.FormulaDaoFactory;
import org.generationcp.middleware.pojos.derived_variables.Formula;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.service.api.derived_variables.FormulaService;
import org.generationcp.middleware.util.FormulaUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
			return Optional.of(FormulaUtils.convertToFormulaDto(formula));
		}
		return Optional.absent();
	}

	@Override
	public List<FormulaDto> getByTargetIds(final Set<Integer> variableIds) {

		final List<FormulaDto> formulaDtos = new ArrayList<>();
		final List<Formula> formulas = this.formulaDaoFactory.getFormulaDAO().getByTargetVariableIds(variableIds);
		for (final Formula formula : formulas) {
			formulaDtos.add(FormulaUtils.convertToFormulaDto(formula));
		}
		return formulaDtos;
	}

	@Override
	public Set<FormulaVariable> getAllFormulaVariables(final Set<Integer> variableIds) {
		final Set<FormulaVariable> formulaVariables = new HashSet<>();
		for (final FormulaDto formulaDto : this.getByTargetIds(variableIds)) {
			formulaVariables.addAll(formulaDto.getInputs());
			this.fillFormulaVariables(formulaDto, formulaVariables);
		}
		return formulaVariables;
	}

	@Override
	public FormulaDto save(final FormulaDto formulaDto) {
		Formula formula = this.convertToFormula(formulaDto);
		formula = this.formulaDaoFactory.getFormulaDAO().save(formula);
		final FormulaDto result = FormulaUtils.convertToFormulaDto(formula);

		return result;
	}

	protected void fillFormulaVariables(final FormulaDto formulaDto, final Set<FormulaVariable> formulaVariables) {
		for (final FormulaVariable formulaVariable : formulaDto.getInputs()) {
			final Optional<FormulaDto> formulaOptional = this.getByTargetId(formulaVariable.getId());
			if (formulaOptional.isPresent()) {
				formulaVariables.addAll(formulaOptional.get().getInputs());
				if (!formulaVariables.contains(formulaVariable)) {
					// If the argument variable is itself a derived trait, include its argument variables.
					this.fillFormulaVariables(formulaOptional.get(), formulaVariables);
				}
			}
		}
	}

	Formula convertToFormula(FormulaDto formulaDto) {
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

	public HibernateSessionProvider getSessionProvider() {
		return this.sessionProvider;
	}

	public void setSessionProvider(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

	
	protected void setFormulaDaoFactory(final FormulaDaoFactory formulaDaoFactory) {
		this.formulaDaoFactory = formulaDaoFactory;
	}

}
