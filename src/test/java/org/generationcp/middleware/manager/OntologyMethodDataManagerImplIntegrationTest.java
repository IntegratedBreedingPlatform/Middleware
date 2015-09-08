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
import org.generationcp.middleware.domain.ontology.Method;
import org.generationcp.middleware.manager.ontology.api.OntologyMethodDataManager;
import org.generationcp.middleware.utils.test.Debug;
import org.generationcp.middleware.utils.test.OntologyDataCreationUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class OntologyMethodDataManagerImplIntegrationTest extends IntegrationTestBase {

	@Autowired
	private OntologyMethodDataManager manager;

	private Method testMethod;

	@Before
	public void setUp() throws Exception {
		String name = OntologyDataCreationUtil.getNewRandomName();
		String definition = "Test Definition";
		this.testMethod = new Method();
		this.testMethod.setName(name);
		this.testMethod.setDefinition(definition);
		this.manager.addMethod(this.testMethod);
	}

	@Test
	public void testGetAllMethods() throws Exception {
		List<Method> methods = this.manager.getAllMethods();
		Assert.assertTrue(methods.size() > 0);
		Debug.println(IntegrationTestBase.INDENT, "From Total Methods:  " + methods.size());
	}

	@Test
	public void testGetMethodById() throws Exception {
		Method method = this.manager.getMethod(4040);
		Assert.assertNotNull(method);
		Assert.assertEquals("Enumerated", method.getName());
	}

	@Test
	public void testAddMethod() throws Exception {
		Assert.assertNotNull(this.testMethod.getId());
		Assert.assertTrue(this.testMethod.getId() > 0);
		Debug.println(IntegrationTestBase.INDENT, "From db:  " + this.testMethod);
	}

	@Test
	public void testUpdateMethod() throws Exception {
		this.testMethod.setDefinition("new definition");
		this.manager.updateMethod(this.testMethod);
		Method updatedMethod = this.manager.getMethod(this.testMethod.getId());
		Assert.assertEquals(updatedMethod.getDefinition(), this.testMethod.getDefinition());
		Debug.println(IntegrationTestBase.INDENT, "From db:  " + this.testMethod);
	}

	@After
	public void tearDown() throws Exception {
		this.manager.deleteMethod(this.testMethod.getId());
	}
}
