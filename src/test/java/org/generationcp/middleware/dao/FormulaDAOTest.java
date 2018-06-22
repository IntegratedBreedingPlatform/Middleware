package org.generationcp.middleware.dao;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.data.initializer.CVTermTestDataInitializer;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.pojos.derived_variables.Formula;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

public class FormulaDAOTest extends IntegrationTestBase {

	private FormulaDAO formulaDAO;
	private CVTermDao cvtermDAO;

	@Before
	public void setup() {
		this.formulaDAO = new FormulaDAO();
		this.formulaDAO.setSession(this.sessionProvder.getSession());

		this.cvtermDAO = new CVTermDao();
		this.cvtermDAO.setSession(this.sessionProvder.getSession());
	}

	@Test
	public void testCreateFormula() {
		final Formula formula = new Formula();
		formula.setActive(true);
		formula.setDefinition(RandomStringUtils.randomAlphanumeric(50));
		formula.setDescription(RandomStringUtils.randomAlphanumeric(50));
		formula.setName(RandomStringUtils.randomAlphanumeric(50));

		CVTerm targetCVTerm = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
		CVTerm input1 = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
		CVTerm input2 = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());

		formula.setTargetCVTerm(targetCVTerm);

		formula.getInputs().add(input1);
		formula.getInputs().add(input2);

		this.cvtermDAO.save(targetCVTerm);
		this.cvtermDAO.save(input1);
		this.cvtermDAO.save(input2);

		this.formulaDAO.save(formula);

		final Formula savedFormula = this.formulaDAO.getById(formula.getFormulaId());

		Assert.assertThat("Should have inputs", savedFormula.getInputs().size(), Matchers.is(2));
		Assert.assertThat("Should have target variable", savedFormula.getTargetCVTerm().getCvTermId(),
			Matchers.is(targetCVTerm.getCvTermId()));

		savedFormula.getInputs().remove(0);
		this.formulaDAO.save(savedFormula);

		final Formula savedFormula2 = this.formulaDAO.getById(formula.getFormulaId());

		Assert.assertThat("Should have removed input", savedFormula2.getInputs().size(), Matchers.is(1));
	}
}
