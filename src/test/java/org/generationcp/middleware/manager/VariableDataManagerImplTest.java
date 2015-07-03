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
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.manager.ontology.daoElements.OntologyVariableInfo;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.ontology.Method;
import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.manager.ontology.api.OntologyMethodDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyPropertyDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyScaleDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
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
public class VariableDataManagerImplTest extends DataManagerIntegrationTest {

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

		List<Variable> variables = VariableDataManagerImplTest.variableManager.getWithFilter(variableFilter);
		Assert.assertTrue(!variables.isEmpty());
		Debug.println(MiddlewareIntegrationTest.INDENT, "From Total Variables:  " + variables.size());
	}

	@Test
	public void testGetVariablesByProperty() throws Exception {
		VariableFilter variableFilter = new VariableFilter();
		variableFilter.addPropertyId(VariableDataManagerImplTest.testProperty.getId());

		List<Variable> variables = VariableDataManagerImplTest.variableManager.getWithFilter(variableFilter);
		Assert.assertTrue(variables.size() == 1);
	}

	@Test
	public void testGetVariable() throws Exception {
		Variable variable =
				VariableDataManagerImplTest.variableManager.getVariable(VariableDataManagerImplTest.testProject.getUniqueID(),
						VariableDataManagerImplTest.testVariableInfo.getId());
		Assert.assertNotNull(variable);
	}

	@Test
	public void testUpdateVariable() throws Exception {
		VariableDataManagerImplTest.variableManager.updateVariable(VariableDataManagerImplTest.testVariableInfo);
		Variable updatedVariable =
				VariableDataManagerImplTest.variableManager.getVariable(VariableDataManagerImplTest.testProject.getUniqueID(),
						VariableDataManagerImplTest.testVariableInfo.getId());
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
		VariableDataManagerImplTest.testProject = instance.createTestProjectData();
		VariableDataManagerImplTest.variableManager = DataManagerIntegrationTest.managerFactory.getOntologyVariableDataManager();
		VariableDataManagerImplTest.methodManager = DataManagerIntegrationTest.managerFactory.getOntologyMethodDataManager();
		VariableDataManagerImplTest.propertyManager = DataManagerIntegrationTest.managerFactory.getOntologyPropertyDataManager();
		VariableDataManagerImplTest.scaleManager = DataManagerIntegrationTest.managerFactory.getOntologyScaleDataManager();

		VariableDataManagerImplTest.testMethod = new org.generationcp.middleware.domain.ontology.Method();
		VariableDataManagerImplTest.testMethod.setName(MiddlewareIntegrationTest.getNewRandomName());
		VariableDataManagerImplTest.testMethod.setDefinition("Test Method");
		VariableDataManagerImplTest.methodManager.addMethod(VariableDataManagerImplTest.testMethod);

		VariableDataManagerImplTest.testProperty = new Property();
		VariableDataManagerImplTest.testProperty.setName(MiddlewareIntegrationTest.getNewRandomName());
		VariableDataManagerImplTest.testProperty.setDefinition("Test Property");
		VariableDataManagerImplTest.testProperty.setCropOntologyId("CO:0000001");
		VariableDataManagerImplTest.testProperty.addClass("My New Class");
		VariableDataManagerImplTest.propertyManager.addProperty(VariableDataManagerImplTest.testProperty);

		VariableDataManagerImplTest.testScale = new Scale();
		VariableDataManagerImplTest.testScale.setName(MiddlewareIntegrationTest.getNewRandomName());
		VariableDataManagerImplTest.testScale.setDefinition("Test Scale");
		VariableDataManagerImplTest.testScale.setDataType(DataType.NUMERIC_VARIABLE);
		VariableDataManagerImplTest.testScale.setMinValue("0");
		VariableDataManagerImplTest.testScale.setMaxValue("100");
		VariableDataManagerImplTest.scaleManager.addScale(VariableDataManagerImplTest.testScale);

		VariableDataManagerImplTest.testVariableInfo = new OntologyVariableInfo();
		VariableDataManagerImplTest.testVariableInfo.setProgramUuid(VariableDataManagerImplTest.testProject.getUniqueID());
		VariableDataManagerImplTest.testVariableInfo.setName(MiddlewareIntegrationTest.getNewRandomName());
		VariableDataManagerImplTest.testVariableInfo.setDescription("Test Variable");
		VariableDataManagerImplTest.testVariableInfo.setMethodId(VariableDataManagerImplTest.testMethod.getId());
		VariableDataManagerImplTest.testVariableInfo.setPropertyId(VariableDataManagerImplTest.testProperty.getId());
		VariableDataManagerImplTest.testVariableInfo.setScaleId(VariableDataManagerImplTest.testScale.getId());
		VariableDataManagerImplTest.testVariableInfo.setAlias("My alias");
		VariableDataManagerImplTest.testVariableInfo.setExpectedMin("0");
		VariableDataManagerImplTest.testVariableInfo.setExpectedMax("100");
		VariableDataManagerImplTest.testVariableInfo.addVariableType(VariableType.GERMPLASM_DESCRIPTOR);
		VariableDataManagerImplTest.testVariableInfo.addVariableType(VariableType.ANALYSIS);
		VariableDataManagerImplTest.testVariableInfo.setIsFavorite(true);
		VariableDataManagerImplTest.variableManager.addVariable(VariableDataManagerImplTest.testVariableInfo);
	}

	@AfterClass
	public static void tearDown() throws Exception {
		VariableDataManagerImplTest.variableManager.deleteVariable(VariableDataManagerImplTest.testVariableInfo.getId());
		VariableDataManagerImplTest.methodManager.deleteMethod(VariableDataManagerImplTest.testMethod.getId());
		VariableDataManagerImplTest.propertyManager.deleteProperty(VariableDataManagerImplTest.testProperty.getId());
		VariableDataManagerImplTest.scaleManager.deleteScale(VariableDataManagerImplTest.testScale.getId());
	}
}
