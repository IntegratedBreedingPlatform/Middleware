package org.generationcp.middleware.service.impl.derived_variables;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.dao.FormulaDAO;
import org.generationcp.middleware.data.initializer.CVTermTestDataInitializer;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.ontology.FormulaDto;
import org.generationcp.middleware.domain.ontology.FormulaVariable;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.derived_variables.FormulaDaoFactory;
import org.generationcp.middleware.pojos.derived_variables.Formula;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class FormulaServiceImplTest {
	
	@Mock
	private HibernateSessionProvider session;
	
	@Mock
	private FormulaDAO formulaDao;
	
	@Mock
	private FormulaDaoFactory factory;
	
	@InjectMocks
	private FormulaServiceImpl formulaServiceImpl;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.formulaServiceImpl.setFormulaDaoFactory(this.factory);
		Mockito.doReturn(this.formulaDao).when(this.factory).getFormulaDAO();
	}
	
	@Test
	public void testConvertToFormulaDto() {
		final Formula testFormula = this.createTestFormula();
		final FormulaDto formulaDto = this.formulaServiceImpl.convertToFormulaDto(testFormula);
		this.verifyFormulaDto(testFormula, formulaDto);
	}

	private void verifyFormulaDto(final Formula testFormula, final FormulaDto formulaDto) {
		Assert.assertEquals(testFormula.getFormulaId(), formulaDto.getFormulaId());
		Assert.assertEquals(testFormula.getName(), formulaDto.getName());
		Assert.assertEquals(testFormula.getTargetCVTerm().getCvTermId(), formulaDto.getTargetTermId());
		Assert.assertEquals(testFormula.getDefinition(), formulaDto.getDefinition());
		Assert.assertEquals(testFormula.getDescription(), formulaDto.getDescription());
		Assert.assertEquals(testFormula.getActive(), formulaDto.getActive());
		Assert.assertEquals(testFormula.getInputs().size(), formulaDto.getInputs().size());
		final Iterator<CVTerm> formulaInputsIterator = testFormula.getInputs().iterator();
		final Iterator<FormulaVariable> formulaDtoInputsIterator = formulaDto.getInputs().iterator();
		while (formulaInputsIterator.hasNext()) {
			final CVTerm expectedInput = formulaInputsIterator.next();
			final FormulaVariable actualInput = formulaDtoInputsIterator.next();
			verifyFormulaVariable(expectedInput, actualInput);
		}
	}

	private void verifyFormulaVariable(final CVTerm expectedInput, final FormulaVariable actualInput) {
		Assert.assertEquals(expectedInput.getCvTermId().intValue(), actualInput.getId());
		Assert.assertEquals(expectedInput.getName(), actualInput.getName());
	}
	
	@Test
	public void testGetByTargetId() {
		final Formula formula1 = this.createTestFormula();
		Mockito.doReturn(formula1).when(this.formulaDao).getByTargetVariableId(Matchers.anyInt());
		final Optional<FormulaDto> formulaDto = this.formulaServiceImpl.getByTargetId(formula1.getTargetCVTerm().getCvTermId());
		Assert.assertTrue(formulaDto.isPresent());
		this.verifyFormulaDto(formula1, formulaDto.get());
	}
	
	@Test
	public void testGetByTargetIdWithNoMatchInDB() {
		final Optional<FormulaDto> formulaDto = this.formulaServiceImpl.getByTargetId(new Random().nextInt(Integer.MAX_VALUE));
		Assert.assertFalse(formulaDto.isPresent());
	}
	
	@Test
	public void testGetByTargetIds() {
		final Formula formula1 = this.createTestFormula();
		final Formula formula2 = this.createTestFormula();
		final Formula formula3 = this.createTestFormula();
		final List<Formula> formulaList = Arrays.asList(formula1, formula2, formula3);
		Mockito.doReturn(formulaList).when(this.formulaDao)
				.getByTargetVariableIds(Matchers.anySetOf(Integer.class));
		
		final Set<Integer> idsSet = new HashSet<>(Arrays.asList(formula1.getTargetCVTerm().getCvTermId(),
				formula2.getTargetCVTerm().getCvTermId(), formula3.getTargetCVTerm().getCvTermId()));
		final List<FormulaDto> formulaDtoList = this.formulaServiceImpl.getByTargetIds(idsSet);
		Mockito.verify(this.formulaDao).getByTargetVariableIds(idsSet);
		Assert.assertEquals(formulaList.size(), formulaDtoList.size());
		final Iterator<Formula> formulaListIterator = formulaList.iterator();
		final Iterator<FormulaDto> formulaDtoListIterator = formulaDtoList.iterator();
		while (formulaListIterator.hasNext()) {
			this.verifyFormulaDto(formulaListIterator.next(), formulaDtoListIterator.next());
		}
		
	}
	
	@Test
	public void testGetAllFormulaVariables() {
		final Formula formula1 = this.createTestFormula();
		final Formula formula2 = this.createTestFormula();
		final Formula formula3 = this.createTestFormula();
		final List<Formula> formulaList = Arrays.asList(formula1, formula2, formula3);
		Mockito.doReturn(formulaList).when(this.formulaDao)
				.getByTargetVariableIds(Matchers.anySetOf(Integer.class));
		
		final Set<Integer> idsSet = new HashSet<>(Arrays.asList(formula1.getTargetCVTerm().getCvTermId(),
				formula2.getTargetCVTerm().getCvTermId(), formula3.getTargetCVTerm().getCvTermId()));
		final Set<FormulaVariable> formulaVariables = this.formulaServiceImpl.getAllFormulaVariables(idsSet);
		Mockito.verify(this.formulaDao).getByTargetVariableIds(idsSet);
		Assert.assertEquals(6, formulaVariables.size());
		final ImmutableMap<Integer, FormulaVariable> variablesMap =
				Maps.uniqueIndex(formulaVariables, new Function<FormulaVariable, Integer>() {

					@Override
					public Integer apply(FormulaVariable input) {
						return input.getId();
					}
				});
		final Iterator<Formula> formulaListIterator = formulaList.iterator();
		while (formulaListIterator.hasNext()) {
			final Formula expectedInput = formulaListIterator.next();
			for (final CVTerm input : expectedInput.getInputs()) {
				Assert.assertNotNull(variablesMap.get(input.getCvTermId()));
				verifyFormulaVariable(input, variablesMap.get(input.getCvTermId()));
			}
		}
		
	}
	
	@Test
	public void testGetAllFormulaVariablesWhenFormulaVariableIsDerivedTrait() {
		final Formula formula1 = this.createTestFormula();
		final Formula formula2 = this.createTestFormula();
		final List<Formula> formulaList = Arrays.asList(formula1, formula2);
		Mockito.doReturn(formulaList).when(this.formulaDao)
				.getByTargetVariableIds(Matchers.anySetOf(Integer.class));
		// Setup one input variable to be derived trait itself
		final CVTerm inputVarWithFormula = formula1.getInputs().get(0);
		final Formula formula3 = this.createTestFormula();
		formula3.setTargetCVTerm(inputVarWithFormula);
		Mockito.doReturn(formula3).when(this.formulaDao).getByTargetVariableId(inputVarWithFormula.getCvTermId()); 
		
		final Set<Integer> idsSet = new HashSet<>(Arrays.asList(formula1.getTargetCVTerm().getCvTermId(),
				formula2.getTargetCVTerm().getCvTermId()));
		final Set<FormulaVariable> formulaVariables = this.formulaServiceImpl.getAllFormulaVariables(idsSet);
		Mockito.verify(this.formulaDao).getByTargetVariableIds(idsSet);
		Assert.assertEquals(6, formulaVariables.size());
		final ImmutableMap<Integer, FormulaVariable> variablesMap =
				Maps.uniqueIndex(formulaVariables, new Function<FormulaVariable, Integer>() {

					@Override
					public Integer apply(FormulaVariable input) {
						return input.getId();
					}
				});
		final Iterator<Formula> formulaListIterator = formulaList.iterator();
		while (formulaListIterator.hasNext()) {
			final Formula expectedInput = formulaListIterator.next();
			for (final CVTerm input : expectedInput.getInputs()) {
				Assert.assertNotNull(variablesMap.get(input.getCvTermId()));
				verifyFormulaVariable(input, variablesMap.get(input.getCvTermId()));
			}
		}
		// Verify that input variables of derived trait that was used as input are included
		for (final CVTerm input : formula3.getInputs()) {
			Assert.assertNotNull(variablesMap.get(input.getCvTermId()));
			verifyFormulaVariable(input, variablesMap.get(input.getCvTermId()));
		}
		
	}

	private Formula createTestFormula() {
		final Formula formula = new Formula();
		formula.setFormulaId(new Random().nextInt(Integer.MAX_VALUE));
		formula.setActive(true);
		formula.setDefinition(RandomStringUtils.randomAlphanumeric(50));
		formula.setDescription(RandomStringUtils.randomAlphanumeric(50));
		formula.setName(RandomStringUtils.randomAlphanumeric(50));

		final CVTerm targetCVTerm = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
		targetCVTerm.setCvTermId(new Random().nextInt(Integer.MAX_VALUE));
		final CVTerm input1 = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
		input1.setCvTermId(new Random().nextInt(Integer.MAX_VALUE));
		final CVTerm input2 = CVTermTestDataInitializer.createTerm(RandomStringUtils.randomAlphanumeric(50), CvId.VARIABLES.getId());
		input2.setCvTermId(new Random().nextInt(Integer.MAX_VALUE));
		
		formula.setTargetCVTerm(targetCVTerm);
		formula.getInputs().add(input1);
		formula.getInputs().add(input2);

		return formula;	
	}
	
	

}
