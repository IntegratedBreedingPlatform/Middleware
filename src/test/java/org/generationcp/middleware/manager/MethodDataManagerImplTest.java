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
import org.generationcp.middleware.domain.ontology.Method;
import org.generationcp.middleware.manager.ontology.api.OntologyMethodDataManager;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Implements {@link DataManagerIntegrationTest}
 */
public class MethodDataManagerImplTest extends DataManagerIntegrationTest {

	private static OntologyMethodDataManager manager;

    private static Method testMethod;

	@Before
	public void setUp() throws Exception {
		manager = DataManagerIntegrationTest.managerFactory.getOntologyMethodDataManager();
        String name = getNewRandomName();
        String definition = "Test Definition";
        testMethod = new Method();
        testMethod.setName(name);
        testMethod.setDefinition(definition);
        manager.addMethod(testMethod);
    }

    @Test
    public void testGetAllMethods() throws Exception {
        List<Method> methods = manager.getAllMethods();
        Assert.assertTrue(methods.size() > 0);
        Debug.println(MiddlewareIntegrationTest.INDENT, "From Total Methods:  " + methods.size());
    }

    @Test
    public void testGetMethodById() throws Exception{
        Method method = manager.getMethod(4040);
        Assert.assertNotNull(method);
        Assert.assertEquals("Enumerated", method.getName());
    }

    @Test
    public void testAddMethod() throws Exception {
        Assert.assertNotNull(testMethod.getId());
        Assert.assertTrue(testMethod.getId() > 0);
        Debug.println(MiddlewareIntegrationTest.INDENT, "From db:  " + testMethod);
    }

    @Test
    public void testUpdateMethod() throws Exception {
        testMethod.setDefinition("new definition");
        manager.updateMethod(testMethod);
        Method updatedMethod = manager.getMethod(testMethod.getId());
        Assert.assertEquals(updatedMethod.getDefinition(), testMethod.getDefinition());
        Debug.println(MiddlewareIntegrationTest.INDENT, "From db:  " + testMethod);
    }

    @After
    public void tearDown() throws Exception {
        manager.deleteMethod(testMethod.getId());
    }
}
