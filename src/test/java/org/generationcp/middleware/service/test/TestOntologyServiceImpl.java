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


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Random;

import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Method;
import org.generationcp.middleware.domain.oms.Property;
import org.generationcp.middleware.domain.oms.Scale;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TraitClass;
import org.generationcp.middleware.domain.oms.TraitClassReference;
import org.generationcp.middleware.exceptions.MiddlewareException;
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
    
    private static final String NUMBER_OF_RECORDS = " # Records = ";
    private static final int AGRONOMIC_TRAIT_CLASS = 1340;

    
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
        Debug.println(0, "#####" + name.getMethodName() + " Start: ");
        startTime = System.nanoTime();
    }

    @Test
    public void testGetStandardVariableById() throws MiddlewareQueryException {
        StandardVariable var = ontologyService.getStandardVariable(8005);
        assertNotNull(var);
        var.print(3);
    }

    @Test
    public void testGetStandardVariables() throws MiddlewareQueryException {
        List<StandardVariable> vars = ontologyService.getStandardVariables("USER_NAME");
        assertFalse(vars.isEmpty());
        for (StandardVariable var : vars){
            var.print(3);
        }
    }
    
    @Test
    public void testAddStandardVariable() throws MiddlewareQueryException {
    }
    
    @Test
    public void testGetAllTermsByCvId() throws MiddlewareQueryException {
        List<Term> terms = ontologyService.getAllTermsByCvId(CvId.VARIABLES);
        for (Term term: terms) {
            term.print(3);
        }
    }
    
    /*======================= PROPERTY ================================== */

    @Test
    public void testGetPropertyById() throws MiddlewareQueryException {
        Property property = ontologyService.getProperty(2000);       
        assertNotNull(property);
        property.print(3);
    }

    @Test
    public void testGetPropertyByName() throws MiddlewareQueryException {
        Property property = ontologyService.getProperty("Dataset");       
        assertNotNull(property);
        property.print(3);
    }

    @Test
    public void testGetAllProperties() throws MiddlewareQueryException {
        List<Property> properties = ontologyService.getAllProperties();       
        assertFalse(properties.isEmpty());
        for (Property property : properties){
            property.print(3);
        }
        Debug.println(3, NUMBER_OF_RECORDS + properties.size());
    }

    @Test
    public void testAddProperty() throws MiddlewareQueryException {
        Property property = ontologyService.addProperty("NEW property", "New property description", AGRONOMIC_TRAIT_CLASS);
        property.print(3);
    }

    @Test
    public void testAddOrUpdateProperty() throws Exception {
        String name = "NEW property";
        String definition = "New property description " + (int) (Math.random() * 100);
        Property origProperty = ontologyService.getProperty(name);
        Property newProperty = ontologyService.addOrUpdateProperty(name, definition, AGRONOMIC_TRAIT_CLASS);
        
        Debug.println(3, "Original:  " + origProperty);
        Debug.println(3, "Updated :  " + newProperty);

        if (origProperty != null){ // if the operation is update, the ids must be same
                assertSame(origProperty.getId(), newProperty.getId());
        }
    }
    

    @Test
    public void testUpdateProperty() throws Exception {
        String name = "UPDATE property";
        String definition = "Update property description " + (int) (Math.random() * 100);
        Property origProperty = ontologyService.getProperty(name);
        if (origProperty == null || origProperty.getTerm() == null){ // first run, add before update
            origProperty = ontologyService.addProperty(name, definition, AGRONOMIC_TRAIT_CLASS);
        }
        ontologyService.updateProperty(new Property(new Term(origProperty.getId(), name, definition), origProperty.getIsA()));
        Property newProperty = ontologyService.getProperty(name);
        
        Debug.println(3, "Original:  " + origProperty);
        Debug.println(3, "Updated :  " + newProperty);
        assertTrue(newProperty.getDefinition().equals(definition));
    }
    
    
    /*======================= SCALE ================================== */

    @Test
    public void testGetScaleById() throws MiddlewareQueryException {
        Scale scale = ontologyService.getScale(6030);       
        assertNotNull(scale);
        scale.print(3);
    }

    @Test
    public void testGetAllScales() throws MiddlewareQueryException {
        List<Scale> scales = ontologyService.getAllScales();       
        assertFalse(scales.isEmpty());
        for (Scale scale : scales){
            scale.print(3);
        }
        Debug.println(3, NUMBER_OF_RECORDS + scales.size());
    }
    
    @Test
    public void testAddScale() throws MiddlewareQueryException {
        Scale scale = ontologyService.addScale("NEW scale", "New scale description");
        scale.print(3);
    }

    @Test
    public void testAddOrUpdateScale() throws Exception {
        String name = "NEW scale";
        String definition = "New scale description " + (int) (Math.random() * 100);
        Scale origScale = ontologyService.getScale(name);
        Scale newScale = ontologyService.addOrUpdateScale(name, definition);
        
        Debug.println(3, "Original:  " + origScale);
        Debug.println(3, "Updated :  " + newScale);

        if (origScale != null){ // if the operation is update, the ids must be same
                assertSame(origScale.getId(), newScale.getId());
        }
    }
    
    @Test
    public void testUpdateScale() throws Exception {
        String name = "UPDATE scale";
        String definition = "Update scale description " + (int) (Math.random() * 100);
        Scale origScale = ontologyService.getScale(name);
        if (origScale == null || origScale.getTerm() == null){ // first run, add before update
            origScale = ontologyService.addScale(name, definition);
        }
        ontologyService.updateScale(new Scale(new Term(origScale.getId(), name, definition)));
        Scale newScale = ontologyService.getScale(name);
        
        Debug.println(3, "Original:  " + origScale);
        Debug.println(3, "Updated :  " + newScale);
        assertTrue(newScale.getDefinition().equals(definition));
    }


    /*======================= METHOD ================================== */
    
    @Test
    public void testGetMethodById() throws MiddlewareQueryException {
        Method method = ontologyService.getMethod(4030);       
        assertNotNull(method);
        method.print(3);        
    }

    @Test
    public void testGetMethodByName() throws MiddlewareQueryException {
        Method method = ontologyService.getMethod("Enumerated");       
        assertNotNull(method);
        method.print(3);        
    }
    
    @Test
    public void testGetAllMethods() throws MiddlewareQueryException {
        List<Method> methods = ontologyService.getAllMethods();       
        assertFalse(methods.isEmpty());
        for (Method method : methods){
            method.print(3);
        }
        Debug.println(3, NUMBER_OF_RECORDS + methods.size());
    }
    
    @Test
    public void testAddMethod() throws MiddlewareQueryException {
        Method method = ontologyService.addMethod("NEW method", "New method description");
        method.print(3);
    }

    @Test
    public void testAddOrUpdateMethod() throws Exception {
        String name = "NEW method";
        String definition = "New method description " + (int) (Math.random() * 100);
        Method origMethod = ontologyService.getMethod(name);
        Method newMethod = ontologyService.addOrUpdateMethod(name, definition);
        
        Debug.println(3, "Original:  " + origMethod);
        Debug.println(3, "Updated :  " + newMethod);

        if (origMethod != null){ // if the operation is update, the ids must be same
                assertSame(origMethod.getId(), newMethod.getId());
        }
    }
    

    @Test
    public void testUpdateMethod() throws Exception {
        String name = "UPDATE method";
        String definition = "Update method description " + (int) (Math.random() * 100);
        Method origMethod = ontologyService.getMethod(name);
        if (origMethod == null || origMethod.getTerm() == null){ // first run, add before update
            origMethod = ontologyService.addMethod(name, definition);
        }
        ontologyService.updateMethod(new Method(new Term(origMethod.getId(), name, definition)));
        Method newMethod = ontologyService.getMethod(name);
        
        Debug.println(3, "Original:  " + origMethod);
        Debug.println(3, "Updated :  " + newMethod);
        assertTrue(newMethod.getDefinition().equals(definition));
    }

    /*======================= OTHERS ================================== */

    @Test
    public void testGetDataTypes() throws MiddlewareQueryException {
        List<Term> dataTypes = ontologyService.getAllDataTypes();       
        assertFalse(dataTypes.isEmpty());
        for (Term dataType : dataTypes){
            dataType.print(3);
        }

    }

    
    @Test
    public void testGetAllTraitGroupsHierarchy() throws MiddlewareQueryException {
        List<TraitClassReference> traitGroups = ontologyService.getAllTraitGroupsHierarchy();           
        assertFalse(traitGroups.isEmpty());
        for (TraitClassReference traitGroup : traitGroups){
            traitGroup.print(3);
        }
        Debug.println(3, NUMBER_OF_RECORDS + traitGroups.size());
    }

    @Test
    public void testGetAllTraitClasses() throws MiddlewareQueryException {
        List<TraitClassReference> traitClasses = ontologyService.getAllTraitClasses();           
        assertFalse(traitClasses.isEmpty());
        for (TraitClassReference traitClass : traitClasses){
            traitClass.print(3);
        }
        Debug.println(3, NUMBER_OF_RECORDS + traitClasses.size());
    }
    

    @Test
    public void testGetAllRoles() throws MiddlewareQueryException {
        List<Term> roles = ontologyService.getAllRoles();           
        assertFalse(roles.isEmpty());
        for (Term role : roles){
            Debug.println(3, "---");
            role.print(3);
        }
        Debug.println(3, NUMBER_OF_RECORDS + roles.size());
    }
    
    @Test
    public void testAddTraitClass() throws MiddlewareQueryException {
        String name = "Test Trait Class " + new Random().nextInt(10000);
        String definition = "Test Definition";
        
        TraitClass traitClass = ontologyService.addTraitClass(name, definition, TermId.ONTOLOGY_TRAIT_CLASS.getId());
        assertNotNull(traitClass);
        assertTrue(traitClass.getId() < 0);
        traitClass.print(3);
    }

    @Test
    public void testAddOrUpdateTraitClass() throws Exception {
        String name = "NEW trait class";
        String definition = "New trait class description " + (int) (Math.random() * 100);
        TraitClass newTraitClass = ontologyService.addOrUpdateTraitClass(name, definition, AGRONOMIC_TRAIT_CLASS);
        Debug.println(3, "Updated :  " + newTraitClass);
        assertTrue(newTraitClass.getDefinition().equals(definition));
    }

    @Test
    public void testUpdateTraitClassInCentral() throws Exception {
        String newValue = "Agronomic New";
        try {
            TraitClass origTraitClass = new TraitClass(ontologyService.getTermById(AGRONOMIC_TRAIT_CLASS), 
                                        ontologyService.getTermById(TermId.ONTOLOGY_TRAIT_CLASS.getId()));
            ontologyService.updateTraitClass(new TraitClass(new Term(origTraitClass.getId(), newValue, newValue), 
                                        origTraitClass.getIsA()));
        } catch (MiddlewareException e){
            Debug.println(3, "MiddlewareException expected: \"" + e.getMessage() + "\"");
            assertTrue(e.getMessage().contains("Cannot update terms in central"));
        }
    }

    @Test
    public void testUpdateTraitClassNotInCentral() throws Exception {
        String name = "UPDATE trait class";
        String definition = "UPDATE trait class description " + (int) (Math.random() * 100);
        TraitClass origTraitClass = ontologyService.addTraitClass(name, definition, AGRONOMIC_TRAIT_CLASS);            

        if (origTraitClass != null){
            TraitClass newTraitClass = ontologyService.updateTraitClass(new TraitClass(new Term(origTraitClass.getId(), name, definition), origTraitClass.getIsA()));
            Debug.println(3, "Original:  " + origTraitClass);
            Debug.println(3, "Updated :  " + newTraitClass);
            assertTrue(newTraitClass.getDefinition().equals(definition));
        }
    }


    @After
    public void afterEachTest() {
        long elapsedTime = System.nanoTime() - startTime;
        Debug.println(0, "#####" + name.getMethodName() + " End: Elapsed Time = " + elapsedTime + " ns = " + ((double) elapsedTime / 1000000000) + " s");
    }


    @AfterClass
    public static void tearDown() throws Exception {
        if (serviceFactory != null) {
            serviceFactory.close();
        }
    }
}
