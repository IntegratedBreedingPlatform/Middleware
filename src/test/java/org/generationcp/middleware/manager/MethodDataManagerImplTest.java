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
public class MethodDataManagerImplTest extends DataManagerIntegrationTest {

	private static OntologyMethodDataManager manager;

	private static Method testMethod;

	@Before
	public void setUp() throws Exception {
		MethodDataManagerImplTest.manager = DataManagerIntegrationTest.managerFactory.getOntologyMethodDataManager();
		String name = MiddlewareIntegrationTest.getNewRandomName();
		String definition = "Test Definition";
		MethodDataManagerImplTest.testMethod = new Method();
		MethodDataManagerImplTest.testMethod.setName(name);
		MethodDataManagerImplTest.testMethod.setDefinition(definition);
		MethodDataManagerImplTest.manager.addMethod(MethodDataManagerImplTest.testMethod);
	}

	@Test
	public void testGetAllMethods() throws Exception {
		List<Method> methods = MethodDataManagerImplTest.manager.getAllMethods();
		Assert.assertTrue(methods.size() > 0);
		Debug.println(MiddlewareIntegrationTest.INDENT, "From Total Methods:  " + methods.size());
	}

	@Test
	public void testGetMethodById() throws Exception {
		Method method = MethodDataManagerImplTest.manager.getMethod(4040);
		Assert.assertNotNull(method);
		Assert.assertEquals("Enumerated", method.getName());
	}

	@Test
	public void testAddMethod() throws Exception {
		Assert.assertNotNull(MethodDataManagerImplTest.testMethod.getId());
		Assert.assertTrue(MethodDataManagerImplTest.testMethod.getId() > 0);
		Debug.println(MiddlewareIntegrationTest.INDENT, "From db:  " + MethodDataManagerImplTest.testMethod);
	}

	@Test
	public void testUpdateMethod() throws Exception {
		MethodDataManagerImplTest.testMethod.setDefinition("new definition");
		MethodDataManagerImplTest.manager.updateMethod(MethodDataManagerImplTest.testMethod);
		Method updatedMethod = MethodDataManagerImplTest.manager.getMethod(MethodDataManagerImplTest.testMethod.getId());
		Assert.assertEquals(updatedMethod.getDefinition(), MethodDataManagerImplTest.testMethod.getDefinition());
		Debug.println(MiddlewareIntegrationTest.INDENT, "From db:  " + MethodDataManagerImplTest.testMethod);
	}

	@After
	public void tearDown() throws Exception {
		MethodDataManagerImplTest.manager.deleteMethod(MethodDataManagerImplTest.testMethod.getId());
	}
}
