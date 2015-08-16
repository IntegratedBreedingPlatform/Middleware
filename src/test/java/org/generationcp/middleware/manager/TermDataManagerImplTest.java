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
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermRelationship;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Method;
import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.domain.ontology.TermRelationshipId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.manager.ontology.api.OntologyMethodDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyPropertyDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyScaleDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.manager.ontology.api.TermDataManager;
import org.generationcp.middleware.manager.ontology.daoElements.OntologyVariableInfo;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

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
		TermDataManagerImplTest.manager = DataManagerIntegrationTest.managerFactory.getTermDataManager();
		WorkbenchTestDataUtil instance = WorkbenchTestDataUtil.getInstance();
		TermDataManagerImplTest.testProject = instance.createTestProjectData();
		TermDataManagerImplTest.variableManager = DataManagerIntegrationTest.managerFactory.getOntologyVariableDataManager();
		TermDataManagerImplTest.methodManager = DataManagerIntegrationTest.managerFactory.getOntologyMethodDataManager();
		TermDataManagerImplTest.propertyManager = DataManagerIntegrationTest.managerFactory.getOntologyPropertyDataManager();
		TermDataManagerImplTest.scaleManager = DataManagerIntegrationTest.managerFactory.getOntologyScaleDataManager();

		TermDataManagerImplTest.testMethod = new org.generationcp.middleware.domain.ontology.Method();
		TermDataManagerImplTest.testMethod.setName(MiddlewareIntegrationTest.getNewRandomName());
		TermDataManagerImplTest.testMethod.setDefinition("Test Method");
		TermDataManagerImplTest.methodManager.addMethod(TermDataManagerImplTest.testMethod);

		TermDataManagerImplTest.testProperty = new Property();
		TermDataManagerImplTest.testProperty.setName(MiddlewareIntegrationTest.getNewRandomName());
		TermDataManagerImplTest.testProperty.setDefinition("Test Property");
		TermDataManagerImplTest.testProperty.setCropOntologyId("CO:0000001");
		TermDataManagerImplTest.testProperty.addClass("My New Class");
		TermDataManagerImplTest.propertyManager.addProperty(TermDataManagerImplTest.testProperty);

		TermDataManagerImplTest.testScale = new Scale();
		TermDataManagerImplTest.testScale.setName(MiddlewareIntegrationTest.getNewRandomName());
		TermDataManagerImplTest.testScale.setDefinition("Test Scale");
		TermDataManagerImplTest.testScale.setDataType(DataType.NUMERIC_VARIABLE);
		TermDataManagerImplTest.testScale.setMinValue("0");
		TermDataManagerImplTest.testScale.setMaxValue("100");
		TermDataManagerImplTest.scaleManager.addScale(TermDataManagerImplTest.testScale);

		TermDataManagerImplTest.testVariableInfo = new OntologyVariableInfo();
		TermDataManagerImplTest.testVariableInfo.setProgramUuid(TermDataManagerImplTest.testProject.getUniqueID());
		TermDataManagerImplTest.testVariableInfo.setName(MiddlewareIntegrationTest.getNewRandomName());
		TermDataManagerImplTest.testVariableInfo.setDescription("Test Variable");
		TermDataManagerImplTest.testVariableInfo.setMethodId(TermDataManagerImplTest.testMethod.getId());
		TermDataManagerImplTest.testVariableInfo.setPropertyId(TermDataManagerImplTest.testProperty.getId());
		TermDataManagerImplTest.testVariableInfo.setScaleId(TermDataManagerImplTest.testScale.getId());
		TermDataManagerImplTest.testVariableInfo.setAlias("My alias");
		TermDataManagerImplTest.testVariableInfo.setExpectedMin("0");
		TermDataManagerImplTest.testVariableInfo.setExpectedMax("100");
		TermDataManagerImplTest.testVariableInfo.addVariableType(VariableType.GERMPLASM_DESCRIPTOR);
		TermDataManagerImplTest.testVariableInfo.addVariableType(VariableType.ANALYSIS);
		TermDataManagerImplTest.testVariableInfo.setIsFavorite(true);
		TermDataManagerImplTest.variableManager.addVariable(TermDataManagerImplTest.testVariableInfo);
	}

	@Test
	public void testGetAllTraitClass() throws Exception {

		List<Term> classes = TermDataManagerImplTest.manager.getTermByCvId(CvId.TRAIT_CLASS.getId());
		Assert.assertTrue(classes.size() > 0);
		for (Term c : classes) {
			c.print(MiddlewareIntegrationTest.INDENT);
		}
	}

	@Test
	public void testGetTermByNameAndCvId() throws Exception {
		Term term = TermDataManagerImplTest.manager.getTermByNameAndCvId("Project", CvId.PROPERTIES.getId());
		Assert.assertNotNull(term);
	}

	@Test
	public void testGetRelationshipsWithObjectAndType() throws Exception {
		List<TermRelationship> termRelationships =
				TermDataManagerImplTest.manager.getRelationshipsWithObjectAndType(TermDataManagerImplTest.testMethod.getId(),
						TermRelationshipId.HAS_METHOD);
		Assert.assertEquals(termRelationships.size(), 1);
		Assert.assertEquals(termRelationships.get(0).getSubjectTerm().getId(), TermDataManagerImplTest.testVariableInfo.getId());
	}
}
