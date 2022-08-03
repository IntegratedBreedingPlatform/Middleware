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

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Method;
import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.api.role.RoleService;
import org.generationcp.middleware.manager.ontology.api.OntologyMethodDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyPropertyDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyScaleDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.manager.ontology.daoElements.OntologyVariableInfo;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.utils.test.Debug;
import org.generationcp.middleware.utils.test.OntologyDataCreationUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class VariableDataManagerImplTest extends IntegrationTestBase {

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

	private Project testProject;
	private Method testMethod;
	private Property testProperty;
	private Scale testScale;
	private OntologyVariableInfo testVariableInfo;

	@Test
	public void testGetAllVariablesUsingFilter() throws MiddlewareException {
		final VariableFilter variableFilter = new VariableFilter();
		variableFilter.setFetchAll(true);

		final List<Variable> variables = this.variableManager.getWithFilter(variableFilter);
		Assert.assertTrue(!variables.isEmpty());
		Debug.println(IntegrationTestBase.INDENT, "From Total Variables:  " + variables.size());
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
		final Variable variable = this.variableManager.getVariable(this.testProject.getUniqueID(), this.testVariableInfo.getId(), true);

		Assert.assertNotNull(variable);
	}

	@Test
	public void testUpdateVariable() throws Exception {
		this.variableManager.updateVariable(this.testVariableInfo);
		final Variable updatedVariable =
			this.variableManager.getVariable(this.testProject.getUniqueID(), this.testVariableInfo.getId(), true);

		Assert.assertNotNull(updatedVariable);
	}

	/**
	 * All test depend on add variable, scale, property, method
	 *
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.testProject = this.workbenchTestDataUtil.createTestProjectData();

		this.testMethod = new org.generationcp.middleware.domain.ontology.Method();
		this.testMethod.setName(OntologyDataCreationUtil.getNewRandomName());
		this.testMethod.setDefinition("Test Method");
		this.methodManager.addMethod(this.testMethod);

		this.testProperty = new Property();
		this.testProperty.setName(OntologyDataCreationUtil.getNewRandomName());
		this.testProperty.setDefinition("Test Property");
		this.testProperty.setCropOntologyId("CO:0000001");
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
}
