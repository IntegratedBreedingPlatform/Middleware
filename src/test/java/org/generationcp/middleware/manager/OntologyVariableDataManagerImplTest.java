/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.middleware.manager;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.domain.oms.*;
import org.generationcp.middleware.manager.ontology.api.OntologyMethodDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyPropertyDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyScaleDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class OntologyVariableDataManagerImplTest extends DataManagerIntegrationTest {

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
    public void testGetAllVariables() throws Exception {
        List<OntologyVariableSummary> variables = variableManager.getWithFilter(testProject.getUniqueID(), null, null, null, null);
        Assert.assertTrue(variables.size() > 0);
        Debug.println(MiddlewareIntegrationTest.INDENT, "From Total Variables:  " + variables.size());
    }

    @Test
    public void testGetVariablesByProperty() throws Exception {
        List<OntologyVariableSummary> variables = variableManager.getWithFilter(testProject.getUniqueID(), null, testMethod.getId(), testProperty.getId(), testScale.getId());
        Assert.assertTrue(variables.size() == 1);
    }

    @Test
    public void testGetVariable() throws Exception {
        OntologyVariable variable = variableManager.getVariable(testProject.getUniqueID(), testVariableInfo.getId());
        Assert.assertNotNull(variable);
    }

    /*@Test
    public void testUpdateVariable() throws Exception {
        OntologyVariable variable = variableManager.getVariable(testProject.getUniqueID(), testVariableInfo.getId());

        OntologyVariableInfo info = new OntologyVariableInfo();
        info.setId(18000);
        info.setName(variable.getName());
        info.setDescription("Variable Description");
        info.setAlias("my_var");
        info.setMinValue("0");
        info.setMaxValue("10");
        info.setMethodId(variable.getMethod().getId());
        info.setPropertyId(variable.getProperty().getId());
        info.setScaleId(variable.getScale().getId());
        info.setProgramUuid(testProject.getUniqueID());
        info.addVariableType(VariableType.GERMPLASM_DESCRIPTOR);
        variableManager.updateVariable(info);

        OntologyVariable updatedVariable = variableManager.getVariable(testProject.getUniqueID(), 18000);
        Assert.assertNotNull(updatedVariable);
    }*/

    /**
     * All test depend on add variable, scale, property, method
     * @throws Exception
     */
    @BeforeClass
    public static void setUp() throws Exception {
        WorkbenchTestDataUtil instance = WorkbenchTestDataUtil.getInstance();
        testProject = instance.createTestProjectData();
        variableManager = DataManagerIntegrationTest.managerFactory.getOntologyVariableDataManager();
        methodManager = DataManagerIntegrationTest.managerFactory.getOntologyMethodDataManager();
        propertyManager = DataManagerIntegrationTest.managerFactory.getOntologyPropertyDataManager();
        scaleManager = DataManagerIntegrationTest.managerFactory.getOntologyScaleDataManager();

        testMethod = new Method();
        testMethod.setName(getNewRandomName());
        testMethod.setDefinition("Test Method");
        methodManager.addMethod(testMethod);

        testProperty = new Property();
        testProperty.setName(getNewRandomName());
        testProperty.setDefinition("Test Property");
        testProperty.setCropOntologyId("CO:0000001");
        testProperty.addClass("My New Class");
        propertyManager.addProperty(testProperty);

        testScale = new Scale();
        testScale.setName(getNewRandomName());
        testScale.setDefinition("Test Scale");
        testScale.setDataType(DataType.NUMERIC_VARIABLE);
        testScale.setMinValue("0");
        testScale.setMaxValue("100");
        scaleManager.addScale(testScale);

        testVariableInfo = new OntologyVariableInfo();
        testVariableInfo.setProgramUuid(testProject.getUniqueID());
        testVariableInfo.setName(getNewRandomName());
        testVariableInfo.setDescription("Test Variable");
        testVariableInfo.setMethodId(testMethod.getId());
        testVariableInfo.setPropertyId(testProperty.getId());
        testVariableInfo.setScaleId(testScale.getId());
        testVariableInfo.setIsFavorite(true);
        variableManager.addVariable(testVariableInfo);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        variableManager.deleteVariable(testVariableInfo.getId());
        methodManager.deleteMethod(testMethod.getId());
        propertyManager.deleteProperty(testProperty.getId());
        scaleManager.deleteScale(testScale.getId());
    }
}
