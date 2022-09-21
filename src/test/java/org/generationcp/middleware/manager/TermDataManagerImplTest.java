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

import org.generationcp.middleware.IntegrationTestBase;
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
import org.generationcp.middleware.api.role.RoleService;
import org.generationcp.middleware.manager.ontology.api.OntologyMethodDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyPropertyDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyScaleDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.manager.ontology.api.TermDataManager;
import org.generationcp.middleware.manager.ontology.daoElements.OntologyVariableInfo;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.utils.test.OntologyDataCreationUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TermDataManagerImplTest extends IntegrationTestBase {

	@Autowired
	private TermDataManager manager;
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
		this.testVariableInfo.addVariableType(VariableType.ENVIRONMENT_DETAIL);
		this.testVariableInfo.setIsFavorite(true);
		this.variableManager.addVariable(this.testVariableInfo);
	}

	@Test
	public void testGetAllTraitClass() throws Exception {

		List<Term> classes = this.manager.getTermByCvId(CvId.TRAIT_CLASS.getId());
		Assert.assertTrue(classes.size() > 0);
		for (Term c : classes) {
			c.print(IntegrationTestBase.INDENT);
		}
	}

	@Test
	public void testGetTermByNameAndCvId() throws Exception {
		Term term = this.manager.getTermByNameAndCvId("Project", CvId.PROPERTIES.getId());
		Assert.assertNotNull(term);
	}

	@Test
	public void testGetRelationshipsWithObjectAndType() throws Exception {
		List<TermRelationship> termRelationships =
				this.manager.getRelationshipsWithObjectAndType(this.testMethod.getId(), TermRelationshipId.HAS_METHOD);
		Assert.assertEquals(termRelationships.size(), 1);
		Assert.assertEquals(termRelationships.get(0).getSubjectTerm().getId(), this.testVariableInfo.getId());
	}
}
