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
import org.generationcp.middleware.domain.ontology.Method;
import org.generationcp.middleware.manager.ontology.api.OntologyMethodDataManager;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Implements {@link DataManagerIntegrationTest}
 */
public class OntologyMethodDataManagerImplIntegrationTest extends DataManagerIntegrationTest {

	private static OntologyMethodDataManager manager;

	private static Method testMethod;

	@Before
	public void setUp() throws Exception {
		OntologyMethodDataManagerImplIntegrationTest.manager = DataManagerIntegrationTest.managerFactory.getOntologyMethodDataManager();
		String name = MiddlewareIntegrationTest.getNewRandomName();
		String definition = "Test Definition";
		OntologyMethodDataManagerImplIntegrationTest.testMethod = new Method();
		OntologyMethodDataManagerImplIntegrationTest.testMethod.setName(name);
		OntologyMethodDataManagerImplIntegrationTest.testMethod.setDefinition(definition);
		OntologyMethodDataManagerImplIntegrationTest.manager.addMethod(OntologyMethodDataManagerImplIntegrationTest.testMethod);
	}

	@Test
	public void testGetAllMethods() throws Exception {
		List<Method> methods = OntologyMethodDataManagerImplIntegrationTest.manager.getAllMethods();
		Assert.assertTrue(methods.size() > 0);
		Debug.println(MiddlewareIntegrationTest.INDENT, "From Total Methods:  " + methods.size());
	}

	@Test
	public void testGetMethodById() throws Exception {
		Method method = OntologyMethodDataManagerImplIntegrationTest.manager.getMethod(4040);
		Assert.assertNotNull(method);
		Assert.assertEquals("Enumerated", method.getName());
	}

	@Test
	public void testAddMethod() throws Exception {
		Assert.assertNotNull(OntologyMethodDataManagerImplIntegrationTest.testMethod.getId());
		Assert.assertTrue(OntologyMethodDataManagerImplIntegrationTest.testMethod.getId() > 0);
		Debug.println(MiddlewareIntegrationTest.INDENT, "From db:  " + OntologyMethodDataManagerImplIntegrationTest.testMethod);
	}

	@Test
	public void testUpdateMethod() throws Exception {
		OntologyMethodDataManagerImplIntegrationTest.testMethod.setDefinition("new definition");
		OntologyMethodDataManagerImplIntegrationTest.manager.updateMethod(OntologyMethodDataManagerImplIntegrationTest.testMethod);
		Method updatedMethod = OntologyMethodDataManagerImplIntegrationTest.manager.getMethod(OntologyMethodDataManagerImplIntegrationTest.testMethod.getId());
		Assert.assertEquals(updatedMethod.getDefinition(), OntologyMethodDataManagerImplIntegrationTest.testMethod.getDefinition());
		Debug.println(MiddlewareIntegrationTest.INDENT, "From db:  " + OntologyMethodDataManagerImplIntegrationTest.testMethod);
	}

	@After
	public void tearDown() throws Exception {
		OntologyMethodDataManagerImplIntegrationTest.manager.deleteMethod(OntologyMethodDataManagerImplIntegrationTest.testMethod.getId());
	}
}
