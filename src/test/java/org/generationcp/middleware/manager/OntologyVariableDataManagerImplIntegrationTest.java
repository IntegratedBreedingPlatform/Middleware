/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.manager;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.dao.FormulaDAO;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.FormulaDto;
import org.generationcp.middleware.domain.ontology.Method;
import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.api.role.RoleService;
import org.generationcp.middleware.manager.ontology.VariableCache;
import org.generationcp.middleware.manager.ontology.api.OntologyMethodDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyPropertyDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyScaleDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.manager.ontology.daoElements.OntologyVariableInfo;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.pojos.derived_variables.Formula;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.VariableOverrides;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.utils.test.Debug;
import org.generationcp.middleware.utils.test.OntologyDataCreationUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OntologyVariableDataManagerImplIntegrationTest extends IntegrationTestBase {

	private static final String CROP_ONTOLOGY_ID = "CO:0000001";

	@Autowired
	private OntologyVariableDataManager variableManager;

	@Autowired
	private OntologyMethodDataManager methodManager;

	@Autowired
	private OntologyPropertyDataManager propertyManager;

	@Autowired
	private OntologyScaleDataManager scaleManager;

	@Autowired
	private RoleService roleService;

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;
	
	private FormulaDAO formulaDAO;
	private CVTermDao cvTermDAO;
	
	private Project testProject;
	private Method testMethod;
	private Property testProperty;
	private Scale testScale;
	private OntologyVariableInfo testVariableInfo;

	private DaoFactory daoFactory;

	@BeforeClass
	public static void setUpOnce() {
		// Variable caching relies on the context holder to determine current crop database in use
		ContextHolder.setCurrentCrop("wheat");
	}
	
	/**
	 * All test depend on add variable, scale, property, method
	 *
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.daoFactory = new DaoFactory(this.sessionProvder);
		this.formulaDAO = daoFactory.getFormulaDAO();
		this.cvTermDAO = daoFactory.getCvTermDao();
		this.testProject = workbenchTestDataUtil.createTestProjectData();
		ContextHolder.setCurrentProgram(this.testProject.getUniqueID());

		this.testMethod = new org.generationcp.middleware.domain.ontology.Method();
		this.testMethod.setName(OntologyDataCreationUtil.getNewRandomName());
		this.testMethod.setDefinition("Test Method");
		this.methodManager.addMethod(this.testMethod);

		this.testProperty = new Property();
		this.testProperty.setName(OntologyDataCreationUtil.getNewRandomName());
		this.testProperty.setDefinition("Test Property");
		this.testProperty.setCropOntologyId(OntologyVariableDataManagerImplIntegrationTest.CROP_ONTOLOGY_ID);
		this.testProperty.addClass("My New Class");
		this.propertyManager.addProperty(this.testProperty);

		this.testScale = new Scale();
		this.testScale.setName(OntologyDataCreationUtil.getNewRandomName());
		this.testScale.setDefinition("Test Scale");
		this.testScale.setDataType(DataType.NUMERIC_VARIABLE);
		this.testScale.setMinValue("0");
		this.testScale.setMaxValue("100");
		this.scaleManager.addScale(this.testScale);

		this.testVariableInfo = this.buildVariable(this.testProperty);
	}

	@Test
	public void testGetAllVariablesUsingFilter() {
		final VariableFilter variableFilter = new VariableFilter();
		variableFilter.setFetchAll(true);

		final List<Variable> variables = this.variableManager.getWithFilter(variableFilter);
		Assert.assertTrue(!variables.isEmpty());
		Debug.println(IntegrationTestBase.INDENT, "From Total Variables:  " + variables.size());
	}

	@Test
	public void testGetAllVariablesById() {
		final List<Variable> variables = this.variableManager.getVariablesByIds(
			Arrays.asList(this.testVariableInfo.getId()), null
		);
		Assert.assertTrue(!variables.isEmpty());
		Debug.println(IntegrationTestBase.INDENT, "From Total Variables:  " + variables.size());
	}

	@Test
	public void testGetVariablesForCurrentProgramUsingFilter() {
		final VariableFilter variableFilter = new VariableFilter();
		variableFilter.setFetchAll(false);
		variableFilter.setProgramUuid(this.testProject.getUniqueID());
		
		final List<Variable> variables = this.variableManager.getWithFilter(variableFilter);
		final ImmutableMap<Integer, Variable> map = Maps.uniqueIndex(variables, new Function<Variable, Integer>() {
			@Override
			public Integer apply(Variable input) {
				return input.getId();
			}
		});
		Assert.assertNotNull(map.get(this.testVariableInfo.getId()));
	}
	
	@Test
	public void testGetVariablesWithFormulaUsingFilter() {
		final Formula formula = this.saveFormulaForTestVariable();
		final VariableFilter variableFilter = new VariableFilter();
		variableFilter.setFetchAll(false);
		variableFilter.setProgramUuid(this.testProject.getUniqueID());
		
		final List<Variable> variables = this.variableManager.getWithFilter(variableFilter);
		final ImmutableMap<Integer, Variable> map = Maps.uniqueIndex(variables, new Function<Variable, Integer>() {
			@Override
			public Integer apply(Variable input) {
				return input.getId();
			}
		});
		final int id = this.testVariableInfo.getId();
		Assert.assertNotNull(map.get(id));
		// Verify formula details
		final FormulaDto retrievedFormula = map.get(id).getFormula();
		Assert.assertNotNull(retrievedFormula);
		Assert.assertEquals(formula.getFormulaId(), retrievedFormula.getFormulaId());
		Assert.assertEquals(formula.getInputs().get(0).getCvTermId().intValue(), retrievedFormula.getInputs().get(0).getId());
	}

	@Test
	public void testGetVariablesByProperty() throws Exception {
		final VariableFilter variableFilter = new VariableFilter();
		variableFilter.addPropertyId(this.testProperty.getId());

		final List<Variable> variables = this.variableManager.getWithFilter(variableFilter);
		Assert.assertTrue(variables.size() == 1);
	}

	@Test
	public void testGetVariable() throws Exception {
		final Variable variable =
				this.variableManager.getVariable(this.testProject.getUniqueID(), this.testVariableInfo.getId(), true);
		variableManager.fillVariableUsage(variable);
		Assert.assertNotNull(variable);
		Assert.assertEquals("Variable should has the id " + this.testVariableInfo.getId(), this.testVariableInfo.getId(), variable.getId());
		Assert.assertFalse("Variable should not be obsolete.", variable.isObsolete());

		Assert.assertEquals("Study usage should be 0", new Integer(0), variable.getStudies());
		Assert.assertEquals("Observation usage should be 0", new Integer(0), variable.getObservations());
		Assert.assertFalse("Variable usage should be false", variable.getHasUsage());
		Assert.assertEquals("Crop ontology id should be " + OntologyVariableDataManagerImplIntegrationTest.CROP_ONTOLOGY_ID,
				OntologyVariableDataManagerImplIntegrationTest.CROP_ONTOLOGY_ID, variable.getProperty().getCropOntologyId());
	}
	
	@Test
	public void testGetVariableWithFormula() throws Exception {
		final Formula formula = this.saveFormulaForTestVariable();
		final Variable variable =
				this.variableManager.getVariable(this.testProject.getUniqueID(), this.testVariableInfo.getId(), true);

		Assert.assertNotNull(variable);
		// Verify formula details
		final FormulaDto retrievedFormula = variable.getFormula();
		Assert.assertNotNull(retrievedFormula);
		Assert.assertEquals(formula.getFormulaId(), retrievedFormula.getFormulaId());
		Assert.assertEquals(formula.getInputs().get(0).getCvTermId().intValue(), retrievedFormula.getInputs().get(0).getId());
		
	}

	@Test
	public void testNotRetrievingVariableUsageStatistics() throws Exception {
		final Variable variable =
				this.variableManager.getVariable(this.testProject.getUniqueID(), this.testVariableInfo.getId(), true);
		Assert.assertNotNull(variable);
		Assert.assertEquals("Study usage should be -1 i.e. unknown.", new Integer(-1), variable.getStudies());
		Assert.assertEquals("Observation usage should be -1 i.e. unknown.", new Integer(-1), variable.getObservations());
	}

	@Test
	public void testGetVariable_DontFilterObsolete() throws Exception {
		// set property, scale, method and variable to obsolete
		final CVTerm testPropertyCvTerm = this.cvTermDAO.getById(this.testProperty.getId());
		testPropertyCvTerm.setIsObsolete(true);
		this.cvTermDAO.update(testPropertyCvTerm);

		final CVTerm testScaleCvTerm = this.cvTermDAO.getById(this.testScale.getId());
		testScaleCvTerm.setIsObsolete(true);
		this.cvTermDAO.update(testScaleCvTerm);

		final CVTerm testMethodCvTerm = this.cvTermDAO.getById(this.testMethod.getId());
		testMethodCvTerm.setIsObsolete(true);
		this.cvTermDAO.update(testMethodCvTerm);

		final CVTerm testVariableCvTerm = this.cvTermDAO.getById(this.testVariableInfo.getId());
		testVariableCvTerm.setIsObsolete(true);
		this.cvTermDAO.update(testVariableCvTerm);

		final Variable variable =
				this.variableManager.getVariable(this.testProject.getUniqueID(), this.testVariableInfo.getId(), false);
		Assert.assertNotNull(variable);
		Assert.assertEquals("Variable should has the id " + this.testVariableInfo.getId(), this.testVariableInfo.getId(), variable.getId());
		Assert.assertTrue("Variable should be obsolete.", variable.isObsolete());

		// revert changes
		testPropertyCvTerm.setIsObsolete(false);
		this.cvTermDAO.update(testPropertyCvTerm);

		testScaleCvTerm.setIsObsolete(false);
		this.cvTermDAO.update(testScaleCvTerm);

		testMethodCvTerm.setIsObsolete(false);
		this.cvTermDAO.update(testMethodCvTerm);

		testVariableCvTerm.setIsObsolete(false);
		this.cvTermDAO.update(testVariableCvTerm);

	}

	@Test(expected = MiddlewareException.class)
	public void testAddAnalysisVariableShouldNotBeAssignedWithOtherVariableType() throws Exception {
		final OntologyVariableInfo variableInfo = new OntologyVariableInfo();
		variableInfo.setName(OntologyDataCreationUtil.getNewRandomName());
		variableInfo.addVariableType(VariableType.ANALYSIS);
		variableInfo.addVariableType(VariableType.ENVIRONMENT_DETAIL);
		this.variableManager.addVariable(variableInfo);
		Assert.fail("'Analysis' variable type should not be assigned together with any other variable type");
	}

	@Test(expected = MiddlewareException.class)
	public void testUpdateAnalysisVariableShouldNotBeAssignedWithOtherVariableType() throws Exception {
		this.testVariableInfo.addVariableType(VariableType.ENVIRONMENT_DETAIL);
		this.testVariableInfo.addVariableType(VariableType.ANALYSIS);
		this.variableManager.updateVariable(this.testVariableInfo);
		Assert.fail("'Analysis' variable type should not be assigned together with any other variable type");
	}

	@Test(expected = MiddlewareException.class)
	public void testAddAnalysisSummaryVariableShouldNotBeAssignedWithOtherVariableType() throws Exception {
		final OntologyVariableInfo variableInfo = new OntologyVariableInfo();
		variableInfo.setName(OntologyDataCreationUtil.getNewRandomName());
		variableInfo.addVariableType(VariableType.ANALYSIS_SUMMARY);
		variableInfo.addVariableType(VariableType.ENVIRONMENT_DETAIL);
		this.variableManager.addVariable(variableInfo);
		Assert.fail("'Analysis Summary' variable type should not be assigned together with any other variable type");
	}

	@Test(expected = MiddlewareException.class)
	public void testUpdateAnalysisSummaryVariableShouldNotBeAssignedWithOtherVariableType() throws Exception {
		this.testVariableInfo.addVariableType(VariableType.ENVIRONMENT_DETAIL);
		this.testVariableInfo.addVariableType(VariableType.ANALYSIS_SUMMARY);
		this.variableManager.updateVariable(this.testVariableInfo);
		Assert.fail("'Analysis Summary' variable type should not be assigned together with any other variable type");
	}
	
	@Test(expected = MiddlewareException.class)
	public void testAddAnalysisVariableShouldNotBeAssignedWithAnalysisSummaryVariableType() throws Exception {
		final OntologyVariableInfo variableInfo = new OntologyVariableInfo();
		variableInfo.setName(OntologyDataCreationUtil.getNewRandomName());
		variableInfo.addVariableType(VariableType.ANALYSIS);
		variableInfo.addVariableType(VariableType.ANALYSIS_SUMMARY);
		this.variableManager.addVariable(variableInfo);
		Assert.fail("'Analysis' variable type should not be assigned together with 'Analysis Summary' variable type");
	}
	
	@Test(expected = MiddlewareException.class)
	public void testAddObservationUnitVariableTypeShouldNotBeTrait() throws Exception {
		final OntologyVariableInfo variableInfo = new OntologyVariableInfo();
		variableInfo.setName(OntologyDataCreationUtil.getNewRandomName());
		variableInfo.addVariableType(VariableType.OBSERVATION_UNIT);
		variableInfo.addVariableType(VariableType.TRAIT);
		this.variableManager.addVariable(variableInfo);
		Assert.fail("'Observation Unit' variable type should not be assigned together 'Trait' variable type");
	}

	@Test(expected = MiddlewareException.class)
	public void testUpdateObservationUnitVariableTypeShouldNotBeTrait() throws Exception {
		this.testVariableInfo.addVariableType(VariableType.OBSERVATION_UNIT);
		this.testVariableInfo.addVariableType(VariableType.TRAIT);
		this.variableManager.updateVariable(this.testVariableInfo);
		Assert.fail("'Observation Unit' variable type should not be assigned together 'Trait' variable type");
	}

	@Test
	public void testUpdateVariable() throws Exception {
		this.variableManager.updateVariable(this.testVariableInfo);
		final Variable updatedVariable =
				this.variableManager.getVariable(this.testProject.getUniqueID(), this.testVariableInfo.getId(), true);
		Assert.assertNotNull(updatedVariable);
	}

	@Test
	public void testUpdateVariableWithSavingInSynonym() throws Exception {
		this.testVariableInfo.setName("UpdatedVariableName");
		this.variableManager.updateVariable(this.testVariableInfo);
		final Variable updatedVariable =
				this.variableManager.getVariable(this.testProject.getUniqueID(), this.testVariableInfo.getId(), true);
		Assert.assertEquals("UpdatedVariableName", updatedVariable.getName());
		Assert.assertNotNull(updatedVariable);
	}

	@Test(expected = MiddlewareException.class)
	public void testDeleteVariableWithSavingInSynonym() throws Exception {
		this.testVariableInfo.setName("UpdatedVariableName");
		this.variableManager.updateVariable(this.testVariableInfo);
		final Variable updatedVariable =
				this.variableManager.getVariable(this.testProject.getUniqueID(), this.testVariableInfo.getId(), true);
		Assert.assertEquals(this.testVariableInfo.getName(), updatedVariable.getName());
		this.variableManager.deleteVariable(this.testVariableInfo.getId());
		this.variableManager.getVariable(this.testProject.getUniqueID(), this.testVariableInfo.getId(), true);
		Assert.fail("Variable does not exist");
	}

	@Test
	public void testIsVariableUsedReturnsFalse() throws Exception {
		final boolean hasUsage = this.variableManager.isVariableUsedInStudy(this.testVariableInfo.getId());
		Assert.assertFalse("Variable should have no usage", hasUsage);
	}

	

	private OntologyVariableInfo buildVariable(final Property property) {
		OntologyVariableInfo variableInfo = new OntologyVariableInfo();
		variableInfo.setProgramUuid(this.testProject.getUniqueID());
		variableInfo.setName(OntologyDataCreationUtil.getNewRandomName());
		variableInfo.setDescription("Test Variable");
		variableInfo.setMethodId(this.testMethod.getId());
		variableInfo.setPropertyId(property.getId());
		variableInfo.setScaleId(this.testScale.getId());
		variableInfo.setAlias("My alias");
		variableInfo.setExpectedMin("0");
		variableInfo.setExpectedMax("100");
		variableInfo.addVariableType(VariableType.GERMPLASM_DESCRIPTOR);
		variableInfo.setIsFavorite(true);
		this.variableManager.addVariable(variableInfo);

		return variableInfo;
	}

	protected void createTestVariableWithCategoricalValue() {
		this.testScale = new Scale();
		this.testScale.setName(OntologyDataCreationUtil.getNewRandomName());
		this.testScale.setDefinition("Categorical Scale");
		this.testScale.setDataType(DataType.CATEGORICAL_VARIABLE);

		this.testScale.addCategory(new TermSummary(null, "1", "TestDefinition1"));
		this.testScale.addCategory(new TermSummary(null, "2", "TestDefinition2"));
		this.testScale.addCategory(new TermSummary(null, "3", "TestDefinition3"));

		this.scaleManager.addScale(this.testScale);

		this.testVariableInfo = this.buildVariable(this.testProperty);
	}
	
	private Formula saveFormulaForTestVariable() {
		final Formula formula = new Formula();
		formula.setActive(true);
		formula.setDefinition(RandomStringUtils.randomAlphanumeric(50));
		formula.setDescription(RandomStringUtils.randomAlphanumeric(50));
		formula.setName(RandomStringUtils.randomAlphanumeric(50));

		final CVTerm targetCVTerm = this.cvTermDAO.getById(this.testVariableInfo.getId());
		formula.setTargetCVTerm(targetCVTerm);
		
		final Property property = new Property();
		property.setName(OntologyDataCreationUtil.getNewRandomName());
		property.setDefinition("Test Property");
		property.setCropOntologyId(OntologyVariableDataManagerImplIntegrationTest.CROP_ONTOLOGY_ID);
		property.addClass("My New Class");
		this.propertyManager.addProperty(property);
		final OntologyVariableInfo inputVariable = this.buildVariable(property);
		final CVTerm inputCVTerm = this.cvTermDAO.getById(inputVariable.getId());
		formula.getInputs().add(inputCVTerm);
		
		return this.formulaDAO.save(formula);
	}

	@Test
	public void testAreVariablesUsedReturnsFalse() throws Exception {
		final List<Integer> list = new ArrayList<>();
		list.add(this.testVariableInfo.getId());
		final boolean hasUsage = this.variableManager.areVariablesUsedInStudy(list);
		Assert.assertFalse("Variables should have no usage", hasUsage);
	}

	@Test
	public void testGetVariableOverridesByVariableIds() throws Exception {
		final List<Integer> list = new ArrayList<>();
		list.add(this.testVariableInfo.getId());
		final List<VariableOverrides> override = this.variableManager.getVariableOverridesByVariableIds(list);
		final VariableOverrides variableOverrides = override.get(0);
		Assert.assertEquals(variableOverrides.getExpectedMin(), "0");
		Assert.assertEquals(variableOverrides.getExpectedMax(), "100");
	}

	@Test
	public void testDeleteVariablesFromCache() {

		final List<Integer> variablesIds = new ArrayList<Integer>();
		final int size = VariableCache.getCacheSize();

		final Integer variable1Id = 1;
		final Variable variable1 = new Variable();
		variable1.setId(variable1Id);

		VariableCache.addToCache(variable1Id, variable1);
		Assert.assertEquals(size + 1, VariableCache.getCacheSize());
		Assert.assertEquals(variable1, VariableCache.getFromCache(variable1Id));

		variablesIds.add(variable1Id);
		this.variableManager.deleteVariablesFromCache(variablesIds);
		Assert.assertEquals(size, VariableCache.getCacheSize());
		Assert.assertNull(VariableCache.getFromCache(variable1Id));
	}

	@Test
	public void testGetDataType() {
		Assert.assertEquals(DataType.NUMERIC_VARIABLE, this.variableManager.getDataType(this.testVariableInfo.getId()).get());
	}
}
