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
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.DataType;
import org.generationcp.middleware.domain.oms.OntologyVariableInfo;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermRelationship;
import org.generationcp.middleware.domain.oms.TermRelationshipId;
import org.generationcp.middleware.domain.oms.VariableType;
import org.generationcp.middleware.domain.ontology.Method;
import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.manager.ontology.api.OntologyMethodDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyPropertyDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyScaleDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.manager.ontology.api.TermDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class TermDataManagerImplTest extends DataManagerIntegrationTest {

	private static TermDataManager manager;
    private static OntologyVariableDataManager variableManager;
    private static OntologyMethodDataManager methodManager;
    private static OntologyPropertyDataManager propertyManager;
    private static OntologyScaleDataManager scaleManager;
    private static Project testProject;
    private static Method testMethod;
    private static Property testProperty;
    private static Scale testScale;
    private static OntologyVariableInfo testVariableInfo;

	@BeforeClass
	public static void setUp() throws Exception {
		manager = DataManagerIntegrationTest.managerFactory.getTermDataManager();
        WorkbenchTestDataUtil instance = WorkbenchTestDataUtil.getInstance();
        testProject = instance.createTestProjectData();
        variableManager = DataManagerIntegrationTest.managerFactory.getOntologyVariableDataManager();
        methodManager = DataManagerIntegrationTest.managerFactory.getOntologyMethodDataManager();
        propertyManager = DataManagerIntegrationTest.managerFactory.getOntologyPropertyDataManager();
        scaleManager = DataManagerIntegrationTest.managerFactory.getOntologyScaleDataManager();

        testMethod = new org.generationcp.middleware.domain.ontology.Method();
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
        testVariableInfo.setAlias("My alias");
        testVariableInfo.setMinValue("0");
        testVariableInfo.setMaxValue("100");
        testVariableInfo.addVariableType(VariableType.GERMPLASM_DESCRIPTOR);
        testVariableInfo.addVariableType(VariableType.ANALYSIS);
        testVariableInfo.setIsFavorite(true);
        variableManager.addVariable(testVariableInfo);
	}

    @Test
    public void testGetAllTraitClass() throws Exception {

        List<Term> classes = manager.getTermByCvId(CvId.TRAIT_CLASS.getId());
        Assert.assertTrue(classes.size() > 0);
        for(Term c : classes){
            c.print(MiddlewareIntegrationTest.INDENT);
        }
    }

    @Test
    public void testGetTermByNameAndCvId() throws Exception {
        Term term = manager.getTermByNameAndCvId("Project", CvId.PROPERTIES.getId());
        Assert.assertNotNull(term);
    }

    @Test
    public void testGetRelationshipsWithObjectAndType() throws Exception {
        List<TermRelationship> termRelationships = manager.getRelationshipsWithObjectAndType(testMethod.getId(), TermRelationshipId.HAS_METHOD);
        Assert.assertEquals(termRelationships.size(), 1);
        Assert.assertEquals(termRelationships.get(0).getSubjectTerm().getId(), testVariableInfo.getId());
    }
}
