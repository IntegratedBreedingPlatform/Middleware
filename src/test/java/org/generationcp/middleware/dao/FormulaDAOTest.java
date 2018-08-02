package org.generationcp.middleware.dao;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import static org.hamcrest.core.Is.is;

public class FormulaDAOTest extends IntegrationTestBase {

	private FormulaDAO formulaDAO;
	private CVTermDao cvtermDAO;
	private CVTerm targetCVTerm;
	private CVTerm input1;
	private CVTerm input2;

	@Before
	public void setup() {
		this.formulaDAO = new FormulaDAO();
		this.formulaDAO.setSession(this.sessionProvder.getSession());

		this.cvtermDAO = new CVTermDao();
		this.cvtermDAO.setSession(this.sessionProvder.getSession());
	}

	@Test
	public void testCreateFormula() {
		final Formula formula = saveTestFormula();

		final Formula savedFormula = this.formulaDAO.getById(formula.getFormulaId());

		Assert.assertThat("Should have inputs", savedFormula.getInputs().size(), Matchers.is(2));
		Assert.assertThat("Should have target variable", savedFormula.getTargetCVTerm().getCvTermId(),
			Matchers.is(targetCVTerm.getCvTermId()));

		savedFormula.getInputs().remove(0);
		this.formulaDAO.save(savedFormula);

		final Formula savedFormula2 = this.formulaDAO.getById(formula.getFormulaId());

		Assert.assertThat("Should have removed input", savedFormula2.getInputs().size(), Matchers.is(1));
	}
	
	@Test
	public void testGetByTargetVariableId() {
		final Formula formula = saveTestFormula();
		final Formula retrievedFormula = this.formulaDAO.getByTargetVariableId(this.targetCVTerm.getCvTermId());
		Assert.assertThat("Should retrieve created formula by target variable ID", retrievedFormula, Matchers.equalTo(formula));
	}
	
	@Test
	public void testGetByTargetVariableIds() {
		final Formula formula1 = saveTestFormula();
		final Formula formula2 = saveTestFormula();
		final Set<Integer> idsSet =
				new HashSet<>(Arrays.asList(formula1.getTargetCVTerm().getCvTermId(), formula2.getTargetCVTerm().getCvTermId()));
		final List<Formula> formulaList = this.formulaDAO.getByTargetVariableIds(idsSet);
		Assert.assertThat("Should retrieve correct list of formula by target variable IDs", formulaList.size(), Matchers.equalTo(2));
		Assert.assertTrue(formulaList.contains(formula1));
		Assert.assertTrue(formulaList.contains(formula2));
	}

	@Test
	public void testGetByInputId() {
		final Formula formula1 = saveTestFormula();
		final Formula formula2 = saveTestFormula();

		this.sessionProvder.getSession().flush();

		final List<Formula> fromFormula1Input1 = this.formulaDAO.getByInputId(formula1.getInputs().get(0).getCvTermId());

		Assert.assertThat(fromFormula1Input1.size(), is(1));
		Assert.assertThat(fromFormula1Input1.get(0).getFormulaId(), is(formula1.getFormulaId()));

		final List<Formula> fromFormula1Input2 = this.formulaDAO.getByInputId(formula1.getInputs().get(1).getCvTermId());

		Assert.assertThat(fromFormula1Input2.size(), is(1));
		Assert.assertThat(fromFormula1Input2.get(0).getFormulaId(), is(formula1.getFormulaId()));

		final List<Formula> fromFormula2 = this.formulaDAO.getByInputId(formula2.getInputs().get(0).getCvTermId());

		Assert.assertThat(fromFormula2.size(), is(1));
		Assert.assertThat(fromFormula2.get(0).getFormulaId(), is(formula2.getFormulaId()));
	}

	protected Formula saveTestFormula() {
		final Formula formula = new Formula();
		formula.setActive(true);
		formula.setDefinition(RandomStringUtils.randomAlphanumeric(50));
		formula.setDescription(RandomStringUtils.randomAlphanumeric(50));
		formula.setName(RandomStringUtils.randomAlphanumeric(50));

		targetCVTerm = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
		input1 = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
		input2 = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());

		formula.setTargetCVTerm(targetCVTerm);
		formula.getInputs().add(input1);
		formula.getInputs().add(input2);

		this.cvtermDAO.save(targetCVTerm);
		this.cvtermDAO.save(input1);
		this.cvtermDAO.save(input2);

		return this.formulaDAO.save(formula);
	}
}
