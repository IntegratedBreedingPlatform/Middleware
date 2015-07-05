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

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Method;
import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.manager.ontology.api.OntologyMethodDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyPropertyDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyScaleDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.manager.ontology.daoElements.OntologyVariableInfo;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Extends {@link DataManagerIntegrationTest}
 */
public class OntologyVariableDataManagerImplIntegrationTest extends DataManagerIntegrationTest {

	private static OntologyVariableDataManager variableManager;
	private static OntologyMethodDataManager methodManager;
	private static OntologyPropertyDataManager propertyManager;
	private static OntologyScaleDataManager scaleManager;
	private static Project testProject;
	private static Method testMethod;
	private static Property testProperty;
	private static Scale testScale;
	private static OntologyVariableInfo testVariableInfo;

	@Test
	public void testGetAllVariablesUsingFilter() throws MiddlewareException {
		VariableFilter variableFilter = new VariableFilter();
		variableFilter.setFetchAll(true);

		List<Variable> variables = OntologyVariableDataManagerImplIntegrationTest.variableManager.getWithFilter(variableFilter);
		Assert.assertTrue(!variables.isEmpty());
		Debug.println(MiddlewareIntegrationTest.INDENT, "From Total Variables:  " + variables.size());
	}

	@Test
	public void testGetVariablesByProperty() throws Exception {
		VariableFilter variableFilter = new VariableFilter();
		variableFilter.addPropertyId(OntologyVariableDataManagerImplIntegrationTest.testProperty.getId());

		List<Variable> variables = OntologyVariableDataManagerImplIntegrationTest.variableManager.getWithFilter(variableFilter);
		Assert.assertTrue(variables.size() == 1);
	}

	@Test
	public void testGetVariable() throws Exception {
		Variable variable =
				OntologyVariableDataManagerImplIntegrationTest.variableManager.getVariable(
						OntologyVariableDataManagerImplIntegrationTest.testProject.getUniqueID(),
						OntologyVariableDataManagerImplIntegrationTest.testVariableInfo.getId());
		Assert.assertNotNull(variable);
	}

	@Test
	public void testUpdateVariable() throws Exception {
		OntologyVariableDataManagerImplIntegrationTest.variableManager
				.updateVariable(OntologyVariableDataManagerImplIntegrationTest.testVariableInfo);
		Variable updatedVariable =
				OntologyVariableDataManagerImplIntegrationTest.variableManager.getVariable(
						OntologyVariableDataManagerImplIntegrationTest.testProject.getUniqueID(),
						OntologyVariableDataManagerImplIntegrationTest.testVariableInfo.getId());
		Assert.assertNotNull(updatedVariable);
	}

	/**
	 * All test depend on add variable, scale, property, method
	 *
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		WorkbenchTestDataUtil instance = WorkbenchTestDataUtil.getInstance();
		OntologyVariableDataManagerImplIntegrationTest.testProject = instance.createTestProjectData();
		OntologyVariableDataManagerImplIntegrationTest.variableManager =
				DataManagerIntegrationTest.managerFactory.getOntologyVariableDataManager();
		OntologyVariableDataManagerImplIntegrationTest.methodManager =
				DataManagerIntegrationTest.managerFactory.getOntologyMethodDataManager();
		OntologyVariableDataManagerImplIntegrationTest.propertyManager =
				DataManagerIntegrationTest.managerFactory.getOntologyPropertyDataManager();
		OntologyVariableDataManagerImplIntegrationTest.scaleManager =
				DataManagerIntegrationTest.managerFactory.getOntologyScaleDataManager();

		OntologyVariableDataManagerImplIntegrationTest.testMethod = new org.generationcp.middleware.domain.ontology.Method();
		OntologyVariableDataManagerImplIntegrationTest.testMethod.setName(MiddlewareIntegrationTest.getNewRandomName());
		OntologyVariableDataManagerImplIntegrationTest.testMethod.setDefinition("Test Method");
		OntologyVariableDataManagerImplIntegrationTest.methodManager.addMethod(OntologyVariableDataManagerImplIntegrationTest.testMethod);

		OntologyVariableDataManagerImplIntegrationTest.testProperty = new Property();
		OntologyVariableDataManagerImplIntegrationTest.testProperty.setName(MiddlewareIntegrationTest.getNewRandomName());
		OntologyVariableDataManagerImplIntegrationTest.testProperty.setDefinition("Test Property");
		OntologyVariableDataManagerImplIntegrationTest.testProperty.setCropOntologyId("CO:0000001");
		OntologyVariableDataManagerImplIntegrationTest.testProperty.addClass("My New Class");
		OntologyVariableDataManagerImplIntegrationTest.propertyManager
				.addProperty(OntologyVariableDataManagerImplIntegrationTest.testProperty);

		OntologyVariableDataManagerImplIntegrationTest.testScale = new Scale();
		OntologyVariableDataManagerImplIntegrationTest.testScale.setName(MiddlewareIntegrationTest.getNewRandomName());
		OntologyVariableDataManagerImplIntegrationTest.testScale.setDefinition("Test Scale");
		OntologyVariableDataManagerImplIntegrationTest.testScale.setDataType(DataType.NUMERIC_VARIABLE);
		OntologyVariableDataManagerImplIntegrationTest.testScale.setMinValue("0");
		OntologyVariableDataManagerImplIntegrationTest.testScale.setMaxValue("100");
		OntologyVariableDataManagerImplIntegrationTest.scaleManager.addScale(OntologyVariableDataManagerImplIntegrationTest.testScale);

		OntologyVariableDataManagerImplIntegrationTest.testVariableInfo = new OntologyVariableInfo();
		OntologyVariableDataManagerImplIntegrationTest.testVariableInfo
				.setProgramUuid(OntologyVariableDataManagerImplIntegrationTest.testProject.getUniqueID());
		OntologyVariableDataManagerImplIntegrationTest.testVariableInfo.setName(MiddlewareIntegrationTest.getNewRandomName());
		OntologyVariableDataManagerImplIntegrationTest.testVariableInfo.setDescription("Test Variable");
		OntologyVariableDataManagerImplIntegrationTest.testVariableInfo
				.setMethodId(OntologyVariableDataManagerImplIntegrationTest.testMethod.getId());
		OntologyVariableDataManagerImplIntegrationTest.testVariableInfo
				.setPropertyId(OntologyVariableDataManagerImplIntegrationTest.testProperty.getId());
		OntologyVariableDataManagerImplIntegrationTest.testVariableInfo.setScaleId(OntologyVariableDataManagerImplIntegrationTest.testScale
				.getId());
		OntologyVariableDataManagerImplIntegrationTest.testVariableInfo.setAlias("My alias");
		OntologyVariableDataManagerImplIntegrationTest.testVariableInfo.setExpectedMin("0");
		OntologyVariableDataManagerImplIntegrationTest.testVariableInfo.setExpectedMax("100");
		OntologyVariableDataManagerImplIntegrationTest.testVariableInfo.addVariableType(VariableType.GERMPLASM_DESCRIPTOR);
		OntologyVariableDataManagerImplIntegrationTest.testVariableInfo.addVariableType(VariableType.ANALYSIS);
		OntologyVariableDataManagerImplIntegrationTest.testVariableInfo.setIsFavorite(true);
		OntologyVariableDataManagerImplIntegrationTest.variableManager
				.addVariable(OntologyVariableDataManagerImplIntegrationTest.testVariableInfo);
	}

	@AfterClass
	public static void tearDown() throws Exception {
		OntologyVariableDataManagerImplIntegrationTest.variableManager
				.deleteVariable(OntologyVariableDataManagerImplIntegrationTest.testVariableInfo.getId());
		OntologyVariableDataManagerImplIntegrationTest.methodManager.deleteMethod(OntologyVariableDataManagerImplIntegrationTest.testMethod
				.getId());
		OntologyVariableDataManagerImplIntegrationTest.propertyManager
				.deleteProperty(OntologyVariableDataManagerImplIntegrationTest.testProperty.getId());
		OntologyVariableDataManagerImplIntegrationTest.scaleManager.deleteScale(OntologyVariableDataManagerImplIntegrationTest.testScale
				.getId());
	}
}
