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

import java.util.List;

import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Method;
import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyMethodDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyPropertyDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyScaleDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.manager.ontology.daoElements.OntologyVariableInfo;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.utils.test.Debug;
import org.generationcp.middleware.utils.test.OntologyDataCreationUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
	private WorkbenchDataManager workbenchDataManager;


	private Project testProject;
	private Method testMethod;
	private Property testProperty;
	private Scale testScale;
	private OntologyVariableInfo testVariableInfo;

	@BeforeClass
	public static void setUpOnce() {
		// Variable caching relies on the context holder to determine current crop database in use
		ContextHolder.setCurrentCrop("maize");
	}

	@Test
	public void testGetAllVariablesUsingFilter() throws MiddlewareException {
		VariableFilter variableFilter = new VariableFilter();
		variableFilter.setFetchAll(true);

		List<Variable> variables = this.variableManager.getWithFilter(variableFilter);
		Assert.assertTrue(!variables.isEmpty());
		Debug.println(IntegrationTestBase.INDENT, "From Total Variables:  " + variables.size());
	}

	@Test
	public void testGetVariablesByProperty() throws Exception {
		VariableFilter variableFilter = new VariableFilter();
		variableFilter.addPropertyId(this.testProperty.getId());

		List<Variable> variables = this.variableManager.getWithFilter(variableFilter);
		Assert.assertTrue(variables.size() == 1);
	}

	@Test
	public void testGetVariable() throws Exception {
		Variable variable = this.variableManager.getVariable(this.testProject.getUniqueID(), this.testVariableInfo.getId(), true, true);
		Assert.assertNotNull(variable);
		Assert.assertEquals("Variable should has the id " + this.testVariableInfo.getId(), this.testVariableInfo.getId(), variable.getId());
		Assert.assertFalse("Variable should not be obsolete.", variable.isObsolete());

		Assert.assertEquals("Study usage should be 0", new Integer(0), variable.getStudies() );
		Assert.assertEquals("Observation usage should be 0", new Integer(0), variable.getObservations());
		Assert.assertEquals("Crop ontology id should be " + CROP_ONTOLOGY_ID, CROP_ONTOLOGY_ID, variable.getProperty().getCropOntologyId());
	}

	@Test
	public void testNotRetrievingVariableUsageStatistics() throws Exception {
		Variable variable = this.variableManager.getVariable(this.testProject.getUniqueID(), this.testVariableInfo.getId(), true, false);
		Assert.assertNotNull(variable);
		Assert.assertEquals("Study usage should be -1 i.e. unknown.", new Integer(-1), variable.getStudies() );
		Assert.assertEquals("Observation usage should be -1 i.e. unknown.", new Integer(-1), variable.getObservations());
	}

	@Test
	public void testGetVariable_DontFilterObsolete() throws Exception {
		CVTermDao cvtermDao = new CVTermDao();
		cvtermDao.setSession(this.sessionProvder.getSession());

		// set property, scale, method and variable to obsolete
		CVTerm testPropertyCvTerm = cvtermDao.getById(this.testProperty.getId());
		testPropertyCvTerm.setIsObsolete(true);
		cvtermDao.update(testPropertyCvTerm);

		CVTerm testScaleCvTerm = cvtermDao.getById(this.testScale.getId());
		testScaleCvTerm.setIsObsolete(true);
		cvtermDao.update(testScaleCvTerm);

		CVTerm testMethodCvTerm = cvtermDao.getById(this.testMethod.getId());
		testMethodCvTerm.setIsObsolete(true);
		cvtermDao.update(testMethodCvTerm);

		CVTerm testVariableCvTerm = cvtermDao.getById(this.testVariableInfo.getId());
		testVariableCvTerm.setIsObsolete(true);
		cvtermDao.update(testVariableCvTerm);

		Variable variable = this.variableManager.getVariable(this.testProject.getUniqueID(), this.testVariableInfo.getId(), false, false);
		Assert.assertNotNull(variable);
		Assert.assertEquals("Variable should has the id " + this.testVariableInfo.getId(), this.testVariableInfo.getId(), variable.getId());
		Assert.assertTrue("Variable should be obsolete.", variable.isObsolete());

		// revert changes
		testPropertyCvTerm.setIsObsolete(false);
		cvtermDao.update(testPropertyCvTerm);

		testScaleCvTerm.setIsObsolete(false);
		cvtermDao.update(testScaleCvTerm);

		testMethodCvTerm.setIsObsolete(false);
		cvtermDao.update(testMethodCvTerm);

		testVariableCvTerm.setIsObsolete(false);
		cvtermDao.update(testVariableCvTerm);

	}

	@Test(expected = MiddlewareException.class)
	public void testAddAnalysisVariableShouldNotBeAssignedWithOtherVariableType() throws Exception {
		OntologyVariableInfo variableInfo = new OntologyVariableInfo();
		variableInfo.setName(OntologyDataCreationUtil.getNewRandomName());
		variableInfo.addVariableType(VariableType.ANALYSIS);
		variableInfo.addVariableType(VariableType.ENVIRONMENT_DETAIL);
		this.variableManager.addVariable(variableInfo);
		Assert.fail("Analysis variable type should not be assigned together with any other variable type");
	}

	@Test(expected = MiddlewareException.class)
	public void testUpdateAnalysisVariableShouldNotBeAssignedWithOtherVariableType() throws Exception {
		this.testVariableInfo.addVariableType(VariableType.ENVIRONMENT_DETAIL);
		this.testVariableInfo.addVariableType(VariableType.ANALYSIS);
		this.variableManager.updateVariable(this.testVariableInfo);
		Assert.fail("Analysis variable type should not be assigned together with any other variable type");
	}

	@Test
	public void testUpdateVariable() throws Exception {
		this.variableManager.updateVariable(this.testVariableInfo);
		Variable updatedVariable = this.variableManager.getVariable(this.testProject.getUniqueID(), this.testVariableInfo.getId(), true, false);
		Assert.assertNotNull(updatedVariable);
	}

	@Test
	public void testUpdateVariableWithSavingInSynonym() throws Exception {
		this.testVariableInfo.setName("UpdatedVariableName");
		this.variableManager.updateVariable(this.testVariableInfo);
		Variable updatedVariable = this.variableManager.getVariable(this.testProject.getUniqueID(), this.testVariableInfo.getId(), true, false);
		Assert.assertEquals("UpdatedVariableName",updatedVariable.getName());
		Assert.assertNotNull(updatedVariable);
	}

	@Test(expected = MiddlewareException.class)
	public void testDeleteVariableWithSavingInSynonym() throws Exception {
		this.testVariableInfo.setName("UpdatedVariableName");
		this.variableManager.updateVariable(this.testVariableInfo);
		Variable updatedVariable = this.variableManager.getVariable(this.testProject.getUniqueID(), this.testVariableInfo.getId(), true, false);
		Assert.assertEquals(testVariableInfo.getName(),updatedVariable.getName());
		this.variableManager.deleteVariable(this.testVariableInfo.getId());
		this.variableManager.getVariable(this.testProject.getUniqueID(), this.testVariableInfo.getId(), true, false);
		Assert.fail("Variable does not exist");
	}

	@Test
	public void testGetCategoricalValue() throws Exception {
		createTestVariableWithCategoricalValue();
		Scale scale = this.scaleManager.getScaleById(this.testScale.getId(), true);
		TermSummary categorical = scale.getCategories().get(0);
		Assert.assertEquals("Unable to retrieve the categorical value of a variable", categorical.getDefinition(),
				this.variableManager.retrieveVariableCategoricalValue(this.testProject.getUniqueID(), this.testVariableInfo.getId(), categorical.getId()));

	}

	/**
	 * All test depend on add variable, scale, property, method
	 *
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		WorkbenchTestDataUtil instance = new WorkbenchTestDataUtil(this.workbenchDataManager);
		this.testProject = instance.createTestProjectData();

		this.testMethod = new org.generationcp.middleware.domain.ontology.Method();
		this.testMethod.setName(OntologyDataCreationUtil.getNewRandomName());
		this.testMethod.setDefinition("Test Method");
		this.methodManager.addMethod(this.testMethod);

		this.testProperty = new Property();
		this.testProperty.setName(OntologyDataCreationUtil.getNewRandomName());
		this.testProperty.setDefinition("Test Property");
		this.testProperty.setCropOntologyId(CROP_ONTOLOGY_ID);
		this.testProperty.addClass("My New Class");
		this.propertyManager.addProperty(this.testProperty);

		this.testScale = new Scale();
		this.testScale.setName(OntologyDataCreationUtil.getNewRandomName());
		this.testScale.setDefinition("Test Scale");
		this.testScale.setDataType(DataType.NUMERIC_VARIABLE);
		this.testScale.setMinValue("0");
		this.testScale.setMaxValue("100");
		this.scaleManager.addScale(this.testScale);

		this.testVariableInfo = new OntologyVariableInfo();
		this.testVariableInfo.setProgramUuid(this.testProject.getUniqueID());
		this.testVariableInfo.setName(OntologyDataCreationUtil.getNewRandomName());
		this.testVariableInfo.setDescription("Test Variable");
		this.testVariableInfo.setMethodId(this.testMethod.getId());
		this.testVariableInfo.setPropertyId(this.testProperty.getId());
		this.testVariableInfo.setScaleId(this.testScale.getId());
		this.testVariableInfo.setAlias("My alias");
		this.testVariableInfo.setExpectedMin("0");
		this.testVariableInfo.setExpectedMax("100");
		this.testVariableInfo.addVariableType(VariableType.GERMPLASM_DESCRIPTOR);
		this.testVariableInfo.setIsFavorite(true);
		this.variableManager.addVariable(this.testVariableInfo);
	}

	protected void createTestVariableWithCategoricalValue() {
		WorkbenchTestDataUtil instance = new WorkbenchTestDataUtil(this.workbenchDataManager);
		this.testProject = instance.createTestProjectData();

		this.testMethod = new org.generationcp.middleware.domain.ontology.Method();
		this.testMethod.setName(OntologyDataCreationUtil.getNewRandomName());
		this.testMethod.setName(OntologyDataCreationUtil.getNewRandomName());
		this.testMethod.setDefinition("Test Method");
		this.methodManager.addMethod(this.testMethod);

		this.testProperty = new Property();
		this.testProperty.setName(OntologyDataCreationUtil.getNewRandomName());
		this.testProperty.setDefinition("Test Property");
		this.testProperty.setCropOntologyId(CROP_ONTOLOGY_ID);
		this.testProperty.addClass("My New Class");
		this.propertyManager.addProperty(this.testProperty);

		this.testScale = new Scale();
		this.testScale.setName(OntologyDataCreationUtil.getNewRandomName());
		this.testScale.setDefinition("Categorical Scale");
		this.testScale.setDataType(DataType.CATEGORICAL_VARIABLE);


		this.testScale.addCategory(new TermSummary(null, "1", "TestDefinition1"));
		this.testScale.addCategory(new TermSummary(null, "2", "TestDefinition2"));
		this.testScale.addCategory(new TermSummary(null, "3", "TestDefinition3"));

		this.scaleManager.addScale(this.testScale);

		this.testVariableInfo = new OntologyVariableInfo();
		this.testVariableInfo.setProgramUuid(this.testProject.getUniqueID());
		this.testVariableInfo.setName(OntologyDataCreationUtil.getNewRandomName());
		this.testVariableInfo.setDescription("Test Variable");
		this.testVariableInfo.setMethodId(this.testMethod.getId());
		this.testVariableInfo.setPropertyId(this.testProperty.getId());
		this.testVariableInfo.setScaleId(this.testScale.getId());
		this.testVariableInfo.setAlias("My alias");
		this.testVariableInfo.setExpectedMin("0");
		this.testVariableInfo.setExpectedMax("100");
		this.testVariableInfo.addVariableType(VariableType.GERMPLASM_DESCRIPTOR);
		this.testVariableInfo.setIsFavorite(true);
		this.variableManager.addVariable(this.testVariableInfo);
	}
}
