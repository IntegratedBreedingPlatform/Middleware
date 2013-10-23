/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/
package org.generationcp.middleware.service.test;


import static org.junit.Assert.assertTrue;

import java.util.List;

import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.oms.Method;
import org.generationcp.middleware.domain.oms.Property;
import org.generationcp.middleware.domain.oms.Scale;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TraitReference;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.service.ServiceFactory;
import org.generationcp.middleware.service.api.OntologyService;
import org.generationcp.middleware.util.Debug;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TestOntologyServiceImpl {
    
    private static ServiceFactory serviceFactory;
    private static OntologyService ontologyService;

    private long startTime;

    @Rule
    public TestName name = new TestName();

    @BeforeClass
    public static void setUp() throws Exception {
        DatabaseConnectionParameters local = new DatabaseConnectionParameters(
                "testDatabaseConfig.properties", "local");
        DatabaseConnectionParameters central = new DatabaseConnectionParameters(
                "testDatabaseConfig.properties", "central");

        serviceFactory = new ServiceFactory(local, central);

        ontologyService = serviceFactory.getOntologyService();

    }

    @Before
    public void beforeEachTest() {
        System.out.println("#####" + name.getMethodName() + " Start: ");
        startTime = System.nanoTime();
    }

    @Test
    public void testGetStandardVariableById() throws MiddlewareQueryException {
        StandardVariable var = ontologyService.getStandardVariable(8005);
        assertTrue(var != null);
        var.print(3);
    }

    @Test
    public void testGetStandardVariables() throws MiddlewareQueryException {
        List<StandardVariable> vars = ontologyService.getStandardVariables("USER_NAME");
        assertTrue(!vars.isEmpty());
        for (StandardVariable var : vars){
            var.print(3);
        }
    }
    
    @Test
    public void testAddStandardVariable() throws MiddlewareQueryException {
    }
    
    /*======================= PROPERTY ================================== */

    @Test
    public void testGetPropertyById() throws MiddlewareQueryException {
        Property property = ontologyService.getProperty(2000);       
        assertTrue(property != null);
        property.print(3);
    }

    @Test
    public void testGetPropertyByName() throws MiddlewareQueryException {
        Property property = ontologyService.getProperty("Dataset");       
        assertTrue(property != null);
        property.print(3);
    }

    @Test
    public void testGetAllProperties() throws MiddlewareQueryException {
        List<Property> properties = ontologyService.getAllProperties();       
        assertTrue(!properties.isEmpty());
        for (Property property : properties){
            property.print(3);
        }
        Debug.println(3, " # Records = " + properties.size());
    }

    @Test
    public void testGetAllScales() throws MiddlewareQueryException {
        List<Scale> scales = ontologyService.getAllScales();       
        assertTrue(!scales.isEmpty());
        for (Scale scale : scales){
            scale.print(3);
        }
        Debug.println(3, " # Records = " + scales.size());
    }


    @Test
    public void testGetAllMethods() throws MiddlewareQueryException {
        List<Method> methods = ontologyService.getAllMethods();       
        assertTrue(!methods.isEmpty());
        for (Method method : methods){
            method.print(3);
        }
        Debug.println(3, " # Records = " + methods.size());
    }
    
    @Test
    public void testAddProperty() throws MiddlewareQueryException {
    }
    
    /*======================= SCALE ================================== */

    @Test
    public void testGetScaleById() throws MiddlewareQueryException {
        Scale scale = ontologyService.getScale(6030);       
        assertTrue(scale != null);
        scale.print(3);
    }

    /*======================= METHOD ================================== */
    
    @Test
    public void testGetMethodById() throws MiddlewareQueryException {
        Term method = ontologyService.getMethod(4030);       
        assertTrue(method != null);
        method.print(3);        
    }

    @Test
    public void testGetMethodByName() throws MiddlewareQueryException {
        Term method = ontologyService.getMethod("Enumerated");       
        assertTrue(method != null);
        method.print(3);        
    }
    
    @Test
    public void testAddMethod() throws MiddlewareQueryException {
    }
    
    
    /*======================= OTHERS ================================== */

    @Test
    public void testGetDataTypes() throws MiddlewareQueryException {
        List<Term> dataTypes = ontologyService.getDataTypes();       
        assertTrue(!dataTypes.isEmpty());
        for (Term dataType : dataTypes){
            dataType.print(3);
        }

    }

    
    @Test
    public void testGetTraitGroups() throws MiddlewareQueryException {
        List<TraitReference> traitGroups = ontologyService.getTraitGroups();           
        assertTrue(!traitGroups.isEmpty());
        for (TraitReference traitGroup : traitGroups){
            traitGroup.print(3);
        }

    }



    @After
    public void afterEachTest() {
        long elapsedTime = System.nanoTime() - startTime;
        System.out.println("#####" + name.getMethodName() + " End: Elapsed Time = " + elapsedTime + " ns = " + ((double) elapsedTime / 1000000000) + " s");
    }


    @AfterClass
    public static void tearDown() throws Exception {
        if (serviceFactory != null) {
            serviceFactory.close();
        }
    }
}
