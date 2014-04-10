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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.NameSynonym;
import org.generationcp.middleware.domain.dms.NameType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.VariableConstraints;
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

    private static final int NUMERIC_VARIABLE = 1;
    private static final int CHARACTER_VARIABLE = 2;
    private static final int CATEGORICAL_VARIABLE = 3;
    
    private static ServiceFactory serviceFactory;
    private static OntologyService ontologyService;

    private long startTime;

    @Rule
    public TestName name = new TestName();
    
    static DatabaseConnectionParameters local;
    static DatabaseConnectionParameters central;


    @BeforeClass
    public static void setUp() throws Exception {

        local = new DatabaseConnectionParameters(
                "testDatabaseConfig.properties", "local");
        central = new DatabaseConnectionParameters(
                "testDatabaseConfig.properties", "central");

        serviceFactory = new ServiceFactory(local, central);

        ontologyService = serviceFactory.getOntologyService();
        
    }

    @Before
    public void beforeEachTest(){
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
        List<StandardVariable> vars = ontologyService.getStandardVariables("CUAN_75DAG");
        assertFalse(vars.isEmpty());
        for (StandardVariable var : vars){
            var.print(3);
        }
    }
    
    @Test
    public void testGetNumericStandardVariableWithMinMax() throws MiddlewareQueryException {
        StandardVariable var = ontologyService.getStandardVariable(8270);
        assertNotNull(var);
        assertNotNull(var.getConstraints());
        assertNotNull(var.getConstraints().getMinValueId());
        var.print(3);
    }

    
    @Test
    public void testGetStandardVariablesByTraitClass() throws MiddlewareQueryException {
        List<StandardVariable> vars = ontologyService.getStandardVariablesByTraitClass(Integer.valueOf(1410)); 
        assertFalse(vars.isEmpty());
        for (StandardVariable var : vars){
            Debug.println(3, var.toString());
        }
    }

    @Test
    public void testGetStandardVariablesByProperty() throws MiddlewareQueryException {
        List<StandardVariable> vars = ontologyService.getStandardVariablesByProperty(Integer.valueOf(20109));
        assertFalse(vars.isEmpty()); // stdvarid = 20961
        for (StandardVariable var : vars){
            Debug.println(3, var.toString());
        }
    }

    @Test
    public void testGetStandardVariablesByMethod() throws MiddlewareQueryException {
        List<StandardVariable> vars = ontologyService.getStandardVariablesByMethod(Integer.valueOf(20643));
        assertFalse(vars.isEmpty());
        for (StandardVariable var : vars){
            Debug.println(3, var.toString());
        }
    }

    @Test
    public void testGetStandardVariablesByScale() throws MiddlewareQueryException {
        List<StandardVariable> vars = ontologyService.getStandardVariablesByScale(Integer.valueOf(20392));
        assertFalse(vars.isEmpty());
        for (StandardVariable var : vars){
            Debug.println(3, var.toString());
        }
    }

    @Test
    public void testAddStandardVariable() throws MiddlewareQueryException {
    }
    
    @Test
    public void testDeleteStandardVariable() throws MiddlewareQueryException {
        StandardVariable stdVar = createNewStandardVariable(CHARACTER_VARIABLE);    
        
        ontologyService.deleteStandardVariable(stdVar.getId());
        Term term = ontologyService.getTermById(stdVar.getId());
        
        assertNull(term);
    }
    
    @Test
    public void testAddOrUpdateStandardVariableMinMaxConstraintsInCentral() throws Exception {
        int standardVariableId = 8270; // numeric variable
        VariableConstraints constraints = new VariableConstraints(-100.0, 100.0);
        try{
            ontologyService.addOrUpdateStandardVariableMinMaxConstraints(
                standardVariableId, constraints);
        } catch (MiddlewareException e){
            Debug.println(3, "MiddlewareException expected: \n\t" + e.getMessage());
            assertTrue(e.getMessage().contains("Cannot update the constraints of standard variables from Central database."));
        }
    }
    
    @Test
    public void testAddOrUpdateStandardVariableMinMaxConstraintsInLocal() throws Exception {
        StandardVariable variable = createNewStandardVariable(NUMERIC_VARIABLE);
        int standardVariableId = variable.getId(); 
        VariableConstraints constraints = new VariableConstraints(-100.0, 100.0);
        ontologyService.addOrUpdateStandardVariableMinMaxConstraints(
                standardVariableId, constraints);
        constraints.print(3);
        assertNotNull(constraints);
        assertNotNull(constraints.getMinValueId());
        assertNotNull(constraints.getMaxValueId());
        
        // cleanup
        ontologyService.deleteStandardVariable(standardVariableId);
    }
    
    @Test
    public void testDeleteStandardVariableMinMaxConstraints() throws MiddlewareQueryException {
        StandardVariable variable = createNewStandardVariable(NUMERIC_VARIABLE);
        int standardVariableId = variable.getId(); 
        assertNotNull(variable.getConstraints().getMinValueId());
        ontologyService.deleteStandardVariableMinMaxConstraints(standardVariableId);
        assertNull(ontologyService.getStandardVariable(standardVariableId).getConstraints());

        // cleanup
        ontologyService.deleteStandardVariable(standardVariableId);
    }

    @Test
    public void testAddGetDeleteStandardVariableValidValue() throws MiddlewareQueryException, MiddlewareException {
        int standardVariableId = 22550; // categorical variable
        String validValueLabel = "8";
        String validValueDescription = "Majority of plants severely stunted";
        
        StandardVariable standardVariable = ontologyService.getStandardVariable(standardVariableId);
        Enumeration validValue = new Enumeration(null, validValueLabel, validValueDescription, 8);
        
        Debug.println(3, "BEFORE ADD: ");
        standardVariable.print(6);

        ontologyService.addStandardVariableValidValue(standardVariable, validValue);

        Debug.println(3, "AFTER ADD: ");
        standardVariable = ontologyService.getStandardVariable(standardVariableId);
        standardVariable.print(6);
        
        // Assertion for successful add
        assertNotNull(standardVariable.getEnumeration(validValueLabel,validValueDescription));
        
        // Test delete
        validValue = standardVariable.getEnumeration(validValueLabel, validValueDescription);
        assertTrue(validValue != null && validValue.getId() != null);
        
        ontologyService.deleteStandardVariableValidValue(standardVariableId, validValue.getId());
        
        Debug.println(3, "AFTER DELETE: ");
        standardVariable = ontologyService.getStandardVariable(standardVariableId);
        standardVariable.print(6);
        assertNull(standardVariable.getEnumeration(validValueLabel, validValueDescription));
        
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
        Property newProperty = ontologyService.addOrUpdateProperty(name, definition, AGRONOMIC_TRAIT_CLASS, null);
        
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
    public void testGetScaleByName() throws MiddlewareQueryException {
        Scale scale = ontologyService.getScale("Calculated");       
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
        List<TraitClassReference> traitGroups = ontologyService.getAllTraitGroupsHierarchy(true);           
        assertFalse(traitGroups.isEmpty());
        for (TraitClassReference traitGroup : traitGroups){
            traitGroup.print(3);
        }
        Debug.println(3, NUMBER_OF_RECORDS + traitGroups.size());
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
        
        ontologyService.deleteTraitClass(traitClass.getId());
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

    @Test
    public void testDeleteTraitClass() throws Exception {
        String name = "Test Trait Class " + new Random().nextInt(10000);
        String definition = "Test Definition";
        
        Term term = ontologyService.addTraitClass(name, definition, TermId.ONTOLOGY_TRAIT_CLASS.getId()).getTerm();
        ontologyService.deleteTraitClass(term.getId());
        
        term = ontologyService.getTermById(term.getId());
        assertNull(term);
    }
    
    @Test
    public void testDeleteProperty() throws Exception {
        String name = "Test Property" + new Random().nextInt(10000);
        String definition = "Property Definition";
        int isA = 1087;
        
        Property property = ontologyService.addProperty(name, definition, isA);
        ontologyService.deleteProperty(property.getId(), isA);
        
        Term term= ontologyService.getTermById(property.getId());
        assertNull(term);
    }
    
    @Test
    public void testDeleteMethod() throws Exception {
        String name = "Test Method " + new Random().nextInt(10000);
        String definition = "Test Definition";
        
        //add a method, should allow insert
        Method method= ontologyService.addMethod(name, definition);
        
        ontologyService.deleteMethod(method.getId());
        
        //check if value does not exist anymore
        Term term = ontologyService.getTermById(method.getId());
        assertNull(term);
    }
    
    @Test
    public void testDeleteScale() throws Exception {
        String name = "Test Scale " + new Random().nextInt(10000);
        String definition = "Test Definition";
        
        Scale scale = ontologyService.addScale(name, definition);
        
        ontologyService.deleteScale(scale.getId());
        
        //check if value does not exist anymore
        Term term = ontologyService.getTermById(scale.getId());
        assertNull(term);
    }
    
    @Test
    public void testValidateDeleteStandardVariableEnumeration() throws Exception {
    	
    	int standardVariableId = TermId.CHECK.getId();
    	int enumerationId = -2;
    	
    	boolean found = ontologyService.validateDeleteStandardVariableEnumeration(standardVariableId, enumerationId);
    	Debug.println(0, "testValidateDeleteStandardVariableEnumeration " + found);
    }
    

    private StandardVariable createNewStandardVariable(int dataType) throws MiddlewareQueryException{
        String propertyName = "property name " + new Random().nextInt(10000);
        ontologyService.addProperty(propertyName, "test property", 1087);
            
        StandardVariable stdVariable = new StandardVariable();
            stdVariable.setName("variable name " + new Random().nextInt(10000));
            stdVariable.setDescription("variable description");
            stdVariable.setProperty(ontologyService.findTermByName(propertyName, CvId.PROPERTIES));
            stdVariable.setMethod(new Term(4030, "Assigned", "Term, name or id assigned"));
            stdVariable.setScale(new Term(6000, "DBCV", "Controlled vocabulary from a database"));
            stdVariable.setStoredIn(new Term(1010, "Study information", "Study element"));


            switch (dataType){
                case NUMERIC_VARIABLE:  
                    stdVariable.setDataType(new Term(1110, "Numeric variable", "Variable with numeric values either continuous or integer"));
                    stdVariable.setConstraints(new VariableConstraints(100.0, 999.0));
                    break;
                case CHARACTER_VARIABLE: 
                        stdVariable.setDataType(new Term(1120, "Character variable", "variable with char values"));
                        break;
                case CATEGORICAL_VARIABLE : 
                        stdVariable.setDataType(new Term(1130, "Categorical variable", "Variable with discrete class values (numeric or character all treated as character)"));
                        stdVariable.setEnumerations(new ArrayList<Enumeration>());
                        stdVariable.getEnumerations().add(new Enumeration(10000, "N", "Nursery", 1));
                        stdVariable.getEnumerations().add(new Enumeration(10001, "HB", "Hybridization nursery", 2));
                        stdVariable.getEnumerations().add(new Enumeration(10002, "PN", "Pedigree nursery", 3));
                        break;
            }
            
            
            stdVariable.setIsA(new Term(1050,"Study condition","Study condition class"));
            stdVariable.setNameSynonyms(new ArrayList<NameSynonym>());
            stdVariable.getNameSynonyms().add(new NameSynonym("Person", NameType.ALTERNATIVE_ENGLISH));
            stdVariable.getNameSynonyms().add(new NameSynonym("Tiga-gamit", NameType.ALTERNATIVE_FRENCH));
            stdVariable.setCropOntologyId("CROP-TEST");
            
            ontologyService.addStandardVariable(stdVariable);
            
            Debug.println(0, "Standard variable saved: " + stdVariable.getId());
            
            return stdVariable;

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
