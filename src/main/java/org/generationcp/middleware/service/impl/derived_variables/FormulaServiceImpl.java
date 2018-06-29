package org.generationcp.middleware.service.impl.derived_variables;

import com.google.common.base.Optional;
import org.generationcp.middleware.dao.FormulaDAO;
import org.generationcp.middleware.domain.ontology.FormulaDto;
import org.generationcp.middleware.domain.ontology.FormulaVariable;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.derived_variables.Formula;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.service.api.derived_variables.FormulaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class FormulaServiceImpl implements FormulaService {

	private final HibernateSessionProvider sessionProvider;

	public FormulaServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

	private FormulaDAO getFormulaDAO() {
		final FormulaDAO dao = new FormulaDAO();
		dao.setSession(this.sessionProvider.getSession());
		return dao;
	}

	@Override
	public Optional<FormulaDto> getByTargetId(final Integer targetId) {
		final Formula formula = this.getFormulaDAO().getByTargetVariableId(targetId);
		if (formula != null) {
			return Optional.of(convertToFormulaDto(formula));
		}
		return Optional.absent();
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
			inputs.add(convertToFormulaVariable(cvTerm));
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

}
