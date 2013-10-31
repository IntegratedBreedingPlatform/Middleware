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

package org.generationcp.middleware.manager.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.NameSynonym;
import org.generationcp.middleware.domain.dms.NameType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.VariableConstraints;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Property;
import org.generationcp.middleware.domain.oms.PropertyReference;
import org.generationcp.middleware.domain.oms.StandardVariableReference;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TraitReference;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.util.Debug;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class TestOntologyDataManagerImpl {

	private static final Integer CV_TERM_ID = 1010;
	private static final String CV_TERM_NAME = "Study Information";
	private static final Integer STD_VARIABLE_ID = 8350; // 8310; 
	
	private static ManagerFactory factory;
	private static OntologyDataManager manager;

	private long startTime;

	@Rule
	public TestName name = new TestName();

	@BeforeClass
	public static void setUp() throws Exception {
		DatabaseConnectionParameters local = new DatabaseConnectionParameters("testDatabaseConfig.properties", "local");
		DatabaseConnectionParameters central = new DatabaseConnectionParameters("testDatabaseConfig.properties", "central");
		factory = new ManagerFactory(local, central);
		manager = factory.getNewOntologyDataManager();
	}

	@Before
	public void beforeEachTest() {
        Debug.println(0, "#####" + name.getMethodName() + " Start: ");
		startTime = System.nanoTime();
	}
	
	@After
	public void afterEachTest() {
		long elapsedTime = System.nanoTime() - startTime;
		Debug.println(0, "#####" + name.getMethodName() + ": Elapsed Time = " + elapsedTime + " ns = " + ((double) elapsedTime/1000000000) + " s");
	}

	@Test
	public void testGetCvTermById() throws Exception {
		Term term = manager.getTermById(CV_TERM_ID);
		assertNotNull(term);
		assertTrue(term.getId() == CV_TERM_ID);
		assertTrue(term.getName().equals(CV_TERM_NAME));
		
		Debug.println(0, "testGetCvTermById(): " + term);
	}
	
	@Test
	public void testGetStandardVariable() throws Exception {
		StandardVariable stdVar = manager.getStandardVariable(STD_VARIABLE_ID);
		assertNotNull(stdVar);		
		Debug.println(0, "testGetStandardVariable(): " + stdVar);
	}
	
	@Test
	public void testCopyStandardVariable() throws Exception {
		StandardVariable stdVar = manager.getStandardVariable(STD_VARIABLE_ID);
		StandardVariable stdVar2 = stdVar.copy();
		
		assertNotSame(stdVar.getId(), stdVar2.getId());
		assertSame(stdVar.getProperty(), stdVar2.getProperty());
		assertSame(stdVar.getScale(), stdVar2.getScale());
		assertSame(stdVar.getMethod(), stdVar2.getMethod());
		assertSame(stdVar.getDataType(), stdVar2.getDataType());
		assertSame(stdVar.getStoredIn(), stdVar2.getStoredIn());
		assertSame(stdVar.getPhenotypicType(), stdVar2.getPhenotypicType());
		assertSame(stdVar.getConstraints(), stdVar2.getConstraints());
		if (stdVar.getName() != null) assertTrue(stdVar.getName().equals(stdVar2.getName()));
		if (stdVar.getDescription() != null) assertTrue(stdVar.getDescription().equals(stdVar2.getDescription()));
		assertSame(stdVar.getEnumerations(), stdVar2.getEnumerations());
		
	    Debug.println(0, "testCopyStandardVariable(): \n    " + stdVar + "\n    " + stdVar2);
	}
	
	
	@Test
	public void testStandardVariableCache() throws Exception {
		Debug.println(0, "testStandardVariableCache(): ");
		manager.getStandardVariable(STD_VARIABLE_ID); 		// First call to getStandardVariable() will put the value to the cache
		manager.getStandardVariable(STD_VARIABLE_ID);		// Second (and subsequent) calls will retrieve the value from the cache
	}
	
	@Test
	public void testNameSynonyms() throws Exception {
		StandardVariable sv = manager.getStandardVariable(8383);
		sv.print(0);
	}
	
	@Test
	public void testAddStandardVariable() throws Exception {
		StandardVariable stdVariable = new StandardVariable();
		stdVariable.setName("variable name " + new Random().nextInt(10000));
		stdVariable.setDescription("variable description");
		stdVariable.setProperty(new Term(2002, "User", "Database user"));
		stdVariable.setMethod(new Term(4030, "Assigned", "Term, name or id assigned"));
		stdVariable.setScale(new Term(6000, "DBCV", "Controlled vocabulary from a database"));
		stdVariable.setStoredIn(new Term(1010, "Study information", "Study element"));
		stdVariable.setDataType(new Term(1120, "Character variable", "variable with char values"));
		stdVariable.setIsA(new Term(1050,"Study condition","Study condition class"));
		stdVariable.setNameSynonyms(new ArrayList<NameSynonym>());
		stdVariable.getNameSynonyms().add(new NameSynonym("Person", NameType.ALTERNATIVE_ENGLISH));
		stdVariable.getNameSynonyms().add(new NameSynonym("Tiga-gamit", NameType.ALTERNATIVE_FRENCH));
		stdVariable.setEnumerations(new ArrayList<Enumeration>());
		stdVariable.getEnumerations().add(new Enumeration(10000, "N", "Nursery", 1));
		stdVariable.getEnumerations().add(new Enumeration(10001, "HB", "Hybridization nursery", 2));
		stdVariable.getEnumerations().add(new Enumeration(10002, "PN", "Pedigree nursery", 3));
		stdVariable.setConstraints(new VariableConstraints(100, 999));
		
		manager.addStandardVariable(stdVariable);
		
		Debug.println(0, "Standard variable saved: " + stdVariable.getId());
	}
	
	@Test
	public void testAddStandardVariableWithMissingScalePropertyMethod() throws Exception {
		StandardVariable stdVariable = new StandardVariable();
		stdVariable.setName("Test SPM" + new Random().nextInt(10000));
		stdVariable.setDescription("Std variable with new scale, property, method");
		
		Term newProperty = new Term(2451, "Environment", "Environment");
		Term property = manager.findTermByName(newProperty.getName(),CvId.PROPERTIES);
		if(property==null) {
			Debug.println(0, "new property = " + newProperty.getName());
			property = newProperty;
		} else {
			Debug.println(0, "property id = " + property.getId());
		}
		Term newScale = new Term(6020, "Text", "Text");
		Term scale = manager.findTermByName(newScale.getName(),CvId.SCALES);
		if(scale==null) {
			Debug.println(0, "new scale = " + newScale.getName());
			scale = newScale;
		} else {
			Debug.println(0, "scale id = " + scale.getId());
		}
		Term newMethod = new Term(0, "Test Method", "Test Method");
		Term method = manager.findTermByName(newMethod.getName(),CvId.METHODS);
		if(method==null) {
			Debug.println(0, "new method = " + newMethod.getName());
			method = newMethod;
		} else {
			Debug.println(0, "method id = " + method.getId());
		}
		stdVariable.setProperty(property);
		stdVariable.setScale(scale);
		stdVariable.setMethod(method);
		
		stdVariable.setStoredIn(new Term(1010, "Study information", "Study element"));
		//added as this is required
		stdVariable.setIsA(new Term(1050,"Study condition","Study condition class"));		
		stdVariable.setDataType(new Term(1120, "Character variable", "variable with char values"));
		stdVariable.setNameSynonyms(new ArrayList<NameSynonym>());
		stdVariable.getNameSynonyms().add(new NameSynonym("test", NameType.ALTERNATIVE_ENGLISH));
		stdVariable.getNameSynonyms().add(new NameSynonym("essai", NameType.ALTERNATIVE_FRENCH));
		stdVariable.setEnumerations(new ArrayList<Enumeration>());
		stdVariable.getEnumerations().add(new Enumeration(10000, "N", "Nursery", 1));
		stdVariable.getEnumerations().add(new Enumeration(10001, "HB", "Hybridization nursery", 2));
		stdVariable.getEnumerations().add(new Enumeration(10002, "PN", "Pedigree nursery", 3));
		stdVariable.setConstraints(new VariableConstraints(100, 999));
		
		manager.addStandardVariable(stdVariable);
		
		Debug.println(0, "Standard variable saved: " + stdVariable.getId());
	}
	
	@SuppressWarnings("deprecation")
    @Test
	public void testAddMethod() throws Exception {
		String name = "Test Method " + new Random().nextInt(10000);
		String definition = "Test Definition";
		Term term = manager.addMethod(name, definition);
		assertTrue(term.getId() < 0);
	    Debug.println(0, "testAddMethod():  " + term);
	    term = manager.getTermById(term.getId());
	    Debug.println(0, "From db:  " + term);
	}
	
	@Test
	public void testGetStandardVariableIdByPropertyScaleMethod() throws Exception {
		Integer propertyId = Integer.valueOf(2010);
		Integer scaleId = Integer.valueOf(6000);
		Integer methodId = Integer.valueOf(4030);
		
		Integer varid = manager.getStandardVariableIdByPropertyScaleMethod(propertyId, scaleId, methodId);
		assertNotNull(varid);
		Debug.println(0, "testGetStandadardVariableIdByPropertyScaleMethod() Results: " + varid);
	}
	
    @Test
    public void testFindStandardVariablesByNameOrSynonym() throws Exception {
        Debug.println(0, "Test FindStandardVariablesByNameOrSynonym");
        Set<StandardVariable> standardVariables = manager.findStandardVariablesByNameOrSynonym("foo bar");
        assertSame(standardVariables.size(), 0);
        
        standardVariables = manager.findStandardVariablesByNameOrSynonym("Accession name");
        assertSame(standardVariables.size(), 1);
        for (StandardVariable stdVar : standardVariables) {
            stdVar.print(0);
        }
        
        standardVariables = manager.findStandardVariablesByNameOrSynonym("THR");
        assertSame(standardVariables.size(), 1);
        for (StandardVariable stdVar : standardVariables) {
            stdVar.print(0);
        }
    } 

    @Test
    public void testFindStandardVariablesByNameOrSynonymWithProperties() throws Exception {
        Debug.println(0, "Test getTraitDetailsByTAbbr");
        Set<StandardVariable> standardVariables = manager.findStandardVariablesByNameOrSynonym("Accession name");
        assertSame(standardVariables.size(), 1);
        for (StandardVariable stdVar : standardVariables) {
            stdVar.print(0);
            Term term = manager.getTermById(stdVar.getId());
            term.print(4);   
        }
    }

    @SuppressWarnings("deprecation")
	@Test
	public void testFindMethodById() throws Exception {
		
		// term doesn't exist
		Term term = manager.findMethodById(999999);
		assertNull(term);
		
		// term exist but isn't a method
		term = manager.findMethodById(22066);
		assertNull(term);
		
		// term does exist in central
		term = manager.findMethodById(20732);
		assertNotNull(term);
		term.print(0);
		Debug.println(0, "");
		
		// add a method to local
		String name = "Test Method " + new Random().nextInt(10000);
		String definition = "Test Definition";
		term = manager.addMethod(name, definition);
		// term does exist in local
		
		term = manager.findMethodById(term.getId());
		assertNotNull(term);
		term.print(0);
	}
	
	@Test
	public void testFindMethodByName() throws Exception {
		Debug.println(0, "Test findMethodByName");
		
		// term doesn't exist
		Term term = manager.findMethodByName("foo bar");
		assertNull(term);
		
		// term exist but isn't a method
		term = manager.findMethodByName("PANH");
		assertNull(term);
		
		// term does exist in central
		term = manager.findMethodByName("Vegetative Stage");
		assertNotNull(term);
		term.print(0);
		Debug.println(0, "");
	}
	
	
	@Test
	public void testFindStandardVariableByTraitScaleMethodNames() throws Exception{
		StandardVariable stdVar = manager.findStandardVariableByTraitScaleMethodNames("Cooperator", "DBCV", "Assigned");		
		Debug.println(0, "testFindStandardVariableByTraitScaleMethodNames(): " + stdVar);
	}
	

	@Test
	public void testGetAllTermsByCvId() throws Exception{
		List<Term> terms = manager.getAllTermsByCvId(CvId.METHODS);		
		Debug.println(0, "testGetAllTermsByCvId - Get Methods: " + terms.size());
		printTerms(terms);
		terms = manager.getAllTermsByCvId(CvId.PROPERTIES);		
		Debug.println(0, "testGetAllTermsByCvId - Get Properties: " + terms.size());
		printTerms(terms);
		terms = manager.getAllTermsByCvId(CvId.SCALES);		
		Debug.println(0, "testGetAllTermsByCvId - Get Scales: " + terms.size());
		printTerms(terms);
	}
	
	@Test
	public void testGetAllTermsByCvIdWithStartAndNumOfRows() throws Exception{
		List<Term> terms1 = manager.getAllTermsByCvId(CvId.METHODS, 0, 2);		
		Debug.println(0, "Get First 2 Methods: " + terms1.size());
		printTerms(terms1);
		
		List<Term> terms2 = manager.getAllTermsByCvId(CvId.METHODS, 2, 2);		
		Debug.println(0, "Get Next 2 Methods: " + terms2.size());
		printTerms(terms2);
		
		terms1.addAll(terms2);
		
		List<Term> terms = manager.getAllTermsByCvId(CvId.METHODS, 0, 4);		
		Debug.println(0, "Get First 4 Methods: " + terms.size());
		printTerms(terms);
		
		assertEquals(terms1, terms);
		
		List<Term> allTerms = manager.getAllTermsByCvId(CvId.METHODS);		
		Debug.println(0, "Get All Methods: " + allTerms.size());
		
		List<Term> allTerms2 = manager.getAllTermsByCvId(CvId.METHODS, 0, allTerms.size());		
		Debug.println(0, "Get All Methods with start and numOfRows: " + allTerms2.size());
		printTerms(allTerms2);
		
		assertEquals(allTerms, allTerms2);
		
	}
	
	private void printTerms(List<Term> terms){
		for (Term term : terms){
			term.print(4);
			Debug.println(0, "    ----------");
		}
	}

	@Test
	public void testCountTermsByCvId() throws Exception{
		long count = manager.countTermsByCvId(CvId.METHODS);		
		Debug.println(0, "testCountTermsByCvId() - Count All Methods: " + count);
		count = manager.countTermsByCvId(CvId.PROPERTIES);		
		Debug.println(0, "testCountTermsByCvId() - Count All Properties: " + count);
		count = manager.countTermsByCvId(CvId.SCALES);		
		Debug.println(0, "testCountTermsByCvId() - Count All Scales: " + count);
	}

	@AfterClass
	public static void tearDown() throws Exception {
		if (factory != null) {
			factory.close();
		}
	}
	
	@Test
	public void testGetMethodsForTrait() throws Exception{
		StandardVariable stdVar = manager.findStandardVariableByTraitScaleMethodNames("User", "DBCV", "Assigned");
		List<Term> terms = manager.getMethodsForTrait(stdVar.getProperty().getId());
		Debug.println(0, "Size: " + terms.size());
		assertNotNull(terms);
		boolean hasAssigned = false;
		for (Term term : terms) {
			if(term.getName().equals("Assigned")) {
				hasAssigned = true;
			}
			Debug.println(0, "method: " + term.getName());
		}
		assertTrue(hasAssigned);//should return Assigned
		
		//2nd test
		stdVar = manager.findStandardVariableByTraitScaleMethodNames("Germplasm entry", "Number", "Enumerated");
		terms = manager.getMethodsForTrait(stdVar.getProperty().getId());
		Debug.println(0, "Size: " + terms.size());
		assertNotNull(terms);
		boolean hasEnumerated = false;
		for (Term term : terms) {
			if(term.getName().equals("Enumerated")) {
				hasEnumerated = true;
			}
			Debug.println(0, "method: " + term.getName());
		}
		assertTrue(hasEnumerated);//should return Enumerated
	}
	
	@Test
	public void testGetScalesForTrait() throws Exception{
		StandardVariable stdVar = manager.findStandardVariableByTraitScaleMethodNames("User", "DBCV", "Assigned");
		List<Term> terms = manager.getScalesForTrait(stdVar.getProperty().getId());
		Debug.println(0, "Size: " + terms.size());
		assertNotNull(terms);
		boolean hasDBCV = false;
		for (Term term : terms) {
			if(term.getName().equals("DBCV")) {
				hasDBCV = true;
			}
			Debug.println(0, "scale: " + term.getName());
		}
		assertTrue(hasDBCV);//should return DBCV
		
		//2nd test
		stdVar = manager.findStandardVariableByTraitScaleMethodNames("Germplasm entry", "Number", "Enumerated");
		terms = manager.getScalesForTrait(stdVar.getProperty().getId());
		Debug.println(0, "Size: " + terms.size());
		assertNotNull(terms);
		boolean hasNumber = false;
		for (Term term : terms) {
			if(term.getName().equals("Number")) {
				hasNumber = true;
			}
			Debug.println(0, "scale: " + term.getName());
		}
		assertTrue(hasNumber);//should return Number
	}
	
	@Test
	public void testAddTerm() throws Exception {
		String name = "Test Method " + new Random().nextInt(10000);
		String definition = "Test Definition";
		
		//add a method, should allow insert
		
		CvId cvId = CvId.METHODS;
		Term term = manager.addTerm(name, definition, cvId);
		assertNotNull(term);
		assertTrue(term.getId() < 0);
	    Debug.println(0, "testAddTerm():  " + term);
	    term = manager.getTermById(term.getId());
	    System.out.println("From db:  " + term);
	    
	    // add a variable, should not allow insert and should throw an exception
	    // uncomment the ff. to test adding variables
	    /* 
	    name = "Test Variable " + new Random().nextInt(10000);
		definition = "Test Variable";
	    cvId = CvId.VARIABLES;
		term = manager.addTerm(name, definition, cvId);
		assertTrue(term == null);
	    */
	}
	
	@Test
	public void testFindTermByName() throws Exception {
		// term doesn't exist
		Term term = manager.findTermByName("foo bar", CvId.METHODS);
		assertNull(term);
		
		// term exist but isn't a method
		term = manager.findTermByName("PANH", CvId.METHODS);
		assertNull(term);
		
		// term does exist in central
		term = manager.findTermByName("Vegetative Stage", CvId.METHODS);
		assertNotNull(term);
		term.print(0);
		Debug.println(0, "");
		
	}
	
	@Test
	public void testGetDataTypes() throws Exception{
		List<Term> terms = manager.getDataTypes();		
		Debug.println(0, "testGetDataTypes: " + terms.size());
		printTerms(terms);
	}
	
	@Test 
	public void testGetStandardVariablesForPhenotypicType() throws Exception{
		PhenotypicType phenotypicType =  PhenotypicType.TRIAL_ENVIRONMENT;
		Integer start = 0;
		Integer numOfRows = 100;
		
		Map<String, StandardVariable> standardVariables = manager.getStandardVariablesForPhenotypicType(phenotypicType, start, numOfRows);
		
		for(Object key : standardVariables.keySet()) {
	        Debug.println(0, key + " : " + standardVariables.get(key).getId() + " : " + standardVariables.get(key).toString());
	    }
		
		Debug.println(0, "count: " + standardVariables.size());
	}

	
    @Test
    public void testGetStandardVariablesInProjects() throws Exception {
    	List<String> headers = Arrays.asList("ENTRY","ENTRYNO", "PLOT", "TRIAL_NO", "TRIAL", "STUDY", "DATASET", "LOC", "LOCN", "NURSER", "Plot Number");
    	
    	Map<String, List<StandardVariable>> results = manager.getStandardVariablesInProjects(headers);

        Debug.println(0, "testGetStandardVariablesInProjects(headers=" + headers + ") RESULTS:");
        for (String name : headers) {
        	System.out.print ("Header = " + name + ", StandardVariables: ");
        	if (results.get(name).size() > 0){
	        	for (StandardVariable var : results.get(name)){
	        		System.out.print(var.getId() + ", ");
	        	}
	        	Debug.println(0, "");
        	} else {
            	System.out.println ("    No standard variables found.");        		
        	}
        }
    }


	@Test
	public void testFindTermsByNameOrSynonym() throws Exception {
		// term doesn't exist
		List<Term> terms = manager.findTermsByNameOrSynonym("foo bar", CvId.METHODS);
		assertSame(terms.size(), 0);
		
		// term exist but isn't a method
		terms = manager.findTermsByNameOrSynonym("PANH", CvId.METHODS);
		assertSame(terms.size(), 0);
		
		// term does exist in central
		terms = manager.findTermsByNameOrSynonym("Vegetative Stage", CvId.METHODS);
		assertNotNull(terms);
		terms.get(0).print(0);
		Debug.println(0, "");
		
		// name is in synonyms
		terms = manager.findTermsByNameOrSynonym("Accession Name", CvId.VARIABLES);
		assertNotNull(terms);
		terms.get(0).print(0);
		Debug.println(0, "");

		// name is both in term and in synonyms 
		// need to modify the entry in cvterm where name = "Cooperator" to have cv_id = 1010
		terms = manager.findTermsByNameOrSynonym("Cooperator", CvId.PROPERTIES);
		assertNotNull(terms);
		for (Term term: terms){
			term.print(0);
			Debug.println(0, "");
		}

	}

	@Test
	public void testGetIsAOfProperties() throws Exception{
		List<Term> terms1 = manager.getIsAOfProperties(0, 2);		
		Debug.println(0, "Get First 2 isA: " + terms1.size());
		printTerms(terms1);
		
		List<Term> terms2 = manager.getIsAOfProperties(2, 2);		
		Debug.println(0, "Get Next 2 isA: " + terms2.size());
		printTerms(terms2);
		
		terms1.addAll(terms2);
		
		List<Term> terms = manager.getIsAOfProperties(0, 4);		
		Debug.println(0, "Get First 4 isA: " + terms.size());
		printTerms(terms);
		
		assertEquals(terms1, terms);
		
		List<Term> allTerms = manager.getIsAOfProperties(0,0);		
		Debug.println(0, "Get All isA: " + allTerms.size());
		printTerms(allTerms);
		
	}
	
	@Test
	public void testCountIsAOfProperties() throws Exception{
		long asOf = manager.countIsAOfProperties();		
		Debug.println(0, "count is a properties " + asOf);
	}
	
	@Test
	public void testAddProperty() throws Exception {
		String name = "Germplasm type 3";
		String definition = "Germplasm type description 3";
		int isA = 1087;
		
        Debug.println(0, "testAddProperty(name=" + name + ", definition=" + definition + ", isA=" + isA + "): ");
		Term term = manager.addProperty(name, definition, isA);
        	term.print(4);

	}
	
    @Test
    public void testGetProperty() throws Exception {
        int termId = 2452;
        
        Property property = manager.getProperty(termId);
        
        Debug.println(0, property.toString());
    }
    
    @Test
    public void testGetTraitGroups() throws Exception {
        List<TraitReference> traitGroups = manager.getTraitGroups();
        for (TraitReference traitGroup : traitGroups){
            traitGroup.print(3);
        }
    }
    
    @Test
    public void testGetAllTraitClasses() throws Exception {
        List<TraitReference> traitClasses = manager.getAllTraitClasses();
        for (TraitReference traitClass : traitClasses){
            traitClass.print(3);
        }
    }
    
    @Test
    public void testPrintTraitGroupsWithNegativeIdsOnly() throws Exception {

        List<TraitReference> traitGroups = manager.getTraitGroups();
        for (TraitReference traitGroup : traitGroups){
            traitGroup.print(3);
            if (traitGroup.getId() < 0){
                traitGroup.print(3);
            }
            List<PropertyReference> properties = traitGroup.getProperties();
            if (!properties.isEmpty()){
                for(PropertyReference property : properties){
                    if (property.getId() < 0){
                        property.print(6);
                    }
                    List<StandardVariableReference> variables = property.getStandardVariables();
                    if (!variables.isEmpty()){
                        for(StandardVariableReference variable : variables){
                            if (variable.getId() < 0){
                                variable.print(9);
                            }
                        }
                    }
                }
            }
            
        }
        
        
    }
    
    
	@Test
	public void testGetPropertyByName() throws Exception {
		String name = "Season";
		Property property = manager.getProperty(name);
		Debug.println(0, property.toString());
	}
	
	@Test 
        public void testGetAllStandardVariable() throws Exception{
            Set<StandardVariable> standardVariables = manager.getAllStandardVariables();
            for (StandardVariable stdVar : standardVariables) {
                stdVar.print(0);
            }
            
            Debug.println(0, "count: " + standardVariables.size());
        }
	
    @Test
    public void testAddPropertyIsARelationship() throws Exception {
        Term term = manager.addPropertyIsARelationship(1050, 1340);
        Debug.println(0, "From db:  " + term);
    }
	

    @Test
    public void testAddOrUpdateTermAndRelationshipFoundInCentral() throws Exception {
        String name = "Season";
        String definition = "Growing Season " +(int) (Math.random() * 100); // add random number to see the update
        try{
            manager.addOrUpdateTermAndRelationship(name, definition, CvId.PROPERTIES, TermId.IS_A.getId(), 1340);
        } catch (MiddlewareException e){
            Debug.println(3, "MiddlewareQueryException expected: \"" + e.getMessage() + "\"");
            assertTrue(e.getMessage().contains(" is retrieved from the central database and cannot be updated"));
        }

    }


    @Test
    public void testAddOrUpdateTermAndRelationshipNotInCentral() throws Exception {
        String name = "Study condition NEW";
        String definition = "Study condition NEW class " +(int) (Math.random() * 100); // add random number to see the update
        Term origTerm = manager.findTermByName(name, CvId.PROPERTIES);
        Term newTerm = manager.addOrUpdateTermAndRelationship(name, definition, CvId.PROPERTIES, TermId.IS_A.getId(), 1340);
        Debug.println(3, "Original:  " + origTerm);
        Debug.println(3, "Updated :  " + newTerm);
        
        if (origTerm != null) { // if the operation is update, the ids must be same
            assertSame(origTerm.getId(), newTerm.getId());
        }

    }

    @Test
    public void testAddOrUpdateTermFoundInCentral() throws Exception {
        String name = "Score";
        String definition = "Score NEW " + (int) (Math.random() * 100); // add random number to see the update
        try{
            manager.addOrUpdateTerm(name, definition, CvId.SCALES);
        } catch (MiddlewareException e){
            Debug.println(3, "MiddlewareQueryException expected: \"" + e.getMessage() + "\"");
            assertTrue(e.getMessage().contains(" is retrieved from the central database and cannot be updated"));
        }
    }

    @Test
    public void testAddOrUpdateTermNotInCentral() throws Exception {
        String name = "Real";
        String definition = "Real Description NEW " + (int) (Math.random() * 100); // add random number to see the update
        Term origTerm = manager.findTermByName(name, CvId.SCALES);
        Term newTerm = manager.addOrUpdateTerm(name, definition, CvId.SCALES);
        Debug.println(3, "Original:  " + origTerm);
        Debug.println(3, "Updated :  " + newTerm);
        
        if (origTerm != null) { // if the operation is update, the ids must be same
            assertSame(origTerm.getId(), newTerm.getId());
        }
    }
    
    @Test
    public void testGetStandardVariableIdByTermId() throws Exception {
        StandardVariable stdVariable = new StandardVariable();
        stdVariable.setName("variable name " + new Random().nextInt(10000));
        stdVariable.setDescription("variable description");
        stdVariable.setProperty(new Term(2002, "User", "Database user"));
        stdVariable.setMethod(new Term(4030, "Assigned", "Term, name or id assigned"));
        stdVariable.setScale(new Term(6000, "DBCV", "Controlled vocabulary from a database"));
        stdVariable.setStoredIn(new Term(1010, "Study information", "Study element"));
        stdVariable.setDataType(new Term(1120, "Character variable", "variable with char values"));
        stdVariable.setIsA(new Term(1050,"Study condition","Study condition class"));
        stdVariable.setNameSynonyms(new ArrayList<NameSynonym>());
        stdVariable.getNameSynonyms().add(new NameSynonym("Person", NameType.ALTERNATIVE_ENGLISH));
        stdVariable.getNameSynonyms().add(new NameSynonym("Tiga-gamit", NameType.ALTERNATIVE_FRENCH));
        stdVariable.setEnumerations(new ArrayList<Enumeration>());
        stdVariable.getEnumerations().add(new Enumeration(10000, "N", "Nursery", 1));
        stdVariable.getEnumerations().add(new Enumeration(10001, "HB", "Hybridization nursery", 2));
        stdVariable.getEnumerations().add(new Enumeration(10002, "PN", "Pedigree nursery", 3));
        stdVariable.setConstraints(new VariableConstraints(100, 999));
        
        manager.addStandardVariable(stdVariable);
        
        Integer stdVariableId = manager.getStandardVariableIdByTermId(stdVariable.getProperty().getId(), TermId.HAS_PROPERTY);
        Debug.println(0, "From db:  " + stdVariableId);
    }
    
    @Test
    public void testDeleteTerm() throws Exception {
        //terms to be deleted should be from local db
        
        String name = "Test Method " + new Random().nextInt(10000);
        String definition = "Test Definition";
        
        //add a method, should allow insert
        
        CvId cvId = CvId.METHODS;
        Term term = manager.addTerm(name, definition, cvId);
        
        manager.deleteTerm(term.getId(), cvId);
        
        //check if value does not exist anymore
        term = manager.getTermById(term.getId());
        assertNull(term);
        
        name = "Test Scale " + new Random().nextInt(10000);
        definition = "Test Definition";
        
        cvId = CvId.SCALES;
        term = manager.addTerm(name, definition, cvId);
        
        manager.deleteTerm(term.getId(), cvId);
        
        //check if value does not exist anymore
        term = manager.getTermById(term.getId());
        assertNull(term);
    }
    
    @Test
    public void testDeleteTermAndRelationship() throws Exception {
        String name = "Test Property" + new Random().nextInt(10000);
        String definition = "Property Definition";
        int isA = 1087;
        
        Term term = manager.addProperty(name, definition, isA);
        manager.deleteTermAndRelationship(term.getId(), CvId.PROPERTIES, TermId.IS_A.getId(), isA);
        
        term= manager.getTermById(term.getId());
        assertNull(term);
        
        name = "Test Trait Class " + new Random().nextInt(10000);
        definition = "Test Definition";
        
        term = manager.addTraitClass(name, definition);
        manager.deleteTermAndRelationship(term.getId(), CvId.IBDB_TERMS, TermId.IS_A.getId(), TermId.ONTOLOGY_TRAIT_CLASS.getId());
        
        term = manager.getTermById(term.getId());
        assertNull(term);
    }

}
