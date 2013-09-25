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
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

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
		startTime = System.nanoTime();
	}
	
	@After
	public void afterEachTest() {
		long elapsedTime = System.nanoTime() - startTime;
		System.out.println("#####" + name.getMethodName() + ": Elapsed Time = " + elapsedTime + " ns = " + ((double) elapsedTime/1000000000) + " s");
	}

	@Test
	public void testGetCvTermById() throws Exception {
		Term term = manager.getTermById(CV_TERM_ID);
		assertNotNull(term);
		assertTrue(term.getId() == CV_TERM_ID);
		assertTrue(term.getName().equals(CV_TERM_NAME));
		
		System.out.println("testGetCvTermById(): " + term);
	}
	
	@Test
	public void testGetStandardVariable() throws Exception {
		StandardVariable stdVar = manager.getStandardVariable(STD_VARIABLE_ID);
		assertNotNull(stdVar);		
		System.out.println("testGetStandardVariable(): " + stdVar);
	}
	
	@Test
	public void testCopyStandardVariable() throws Exception {
		StandardVariable stdVar = manager.getStandardVariable(STD_VARIABLE_ID);
		StandardVariable stdVar2 = stdVar.copy();
		
		assertTrue(stdVar.getId() != stdVar2.getId());
		assertTrue(stdVar.getProperty() == stdVar2.getProperty());
		assertTrue(stdVar.getScale() == stdVar2.getScale());
		assertTrue(stdVar.getMethod() == stdVar2.getMethod());
		assertTrue(stdVar.getDataType() == stdVar2.getDataType());
		assertTrue(stdVar.getStoredIn() == stdVar2.getStoredIn());
		assertTrue(stdVar.getPhenotypicType() == stdVar2.getPhenotypicType());
		assertTrue(stdVar.getConstraints() == stdVar2.getConstraints());
		if (stdVar.getName() != null) assertTrue(stdVar.getName().equals(stdVar2.getName()));
		if (stdVar.getDescription() != null) assertTrue(stdVar.getDescription().equals(stdVar2.getDescription()));
		assertTrue(stdVar.getEnumerations() == stdVar2.getEnumerations());
		
	    System.out.println("testCopyStandardVariable(): \n    " + stdVar + "\n    " + stdVar2);
	}
	
	
	@Test
	public void testStandardVariableCache() throws Exception {
		System.out.println("testStandardVariableCache(): ");
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
		
		System.out.println("Standard variable saved: " + stdVariable.getId());
	}
	
	@Test
	public void testAddStandardVariableWithMissingScalePropertyMethod() throws Exception {
		StandardVariable stdVariable = new StandardVariable();
		stdVariable.setName("Test SPM" + new Random().nextInt(10000));
		stdVariable.setDescription("Std variable with new scale, property, method");
		
		Term newProperty = new Term(2451, "Environment", "Environment");
		Term property = manager.findTermByName(newProperty.getName(),CvId.PROPERTIES);
		if(property==null) {
			System.out.println("new property = " + newProperty.getName());
			property = newProperty;
		} else {
			System.out.println("property id = " + property.getId());
		}
		Term newScale = new Term(6020, "Text", "Text");
		Term scale = manager.findTermByName(newScale.getName(),CvId.SCALES);
		if(scale==null) {
			System.out.println("new scale = " + newScale.getName());
			scale = newScale;
		} else {
			System.out.println("scale id = " + scale.getId());
		}
		Term newMethod = new Term(0, "Test Method", "Test Method");
		Term method = manager.findTermByName(newMethod.getName(),CvId.METHODS);
		if(method==null) {
			System.out.println("new method = " + newMethod.getName());
			method = newMethod;
		} else {
			System.out.println("method id = " + method.getId());
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
		
		System.out.println("Standard variable saved: " + stdVariable.getId());
	}
	
	@Test
	public void testAddMethod() throws Exception {
		String name = "Test Method " + new Random().nextInt(10000);
		String definition = "Test Definition";
		Term term = manager.addMethod(name, definition);
		assertTrue(term.getId() < 0);
	    System.out.println("testAddMethod():  " + term);
	    term = manager.getTermById(term.getId());
	    System.out.println("From db:  " + term);
	}
	
	@Test
	public void testGetStandadardVariableIdByPropertyScaleMethod() throws Exception {
		Integer propertyId = Integer.valueOf(2002);
		Integer scaleId = Integer.valueOf(6000);
		Integer methodId = Integer.valueOf(4030);
		
		Integer varid = manager.getStandadardVariableIdByPropertyScaleMethod(propertyId, scaleId, methodId);
		Assert.assertNotNull(varid);
		System.out.println("testGetStandadardVariableIdByPropertyScaleMethod() Results: " + varid);
	}
	
    @Test
    public void testFindStandardVariablesByNameOrSynonym() throws Exception {
        System.out.println("Test FindStandardVariablesByNameOrSynonym");
        Set<StandardVariable> standardVariables = manager.findStandardVariablesByNameOrSynonym("foo bar");
        assertTrue(standardVariables.size() == 0);
        
        standardVariables = manager.findStandardVariablesByNameOrSynonym("Accession name");
        assertTrue(standardVariables.size() == 1);
        for (StandardVariable stdVar : standardVariables) {
            stdVar.print(0);
        }
        
        standardVariables = manager.findStandardVariablesByNameOrSynonym("THR");
        assertTrue(standardVariables.size() == 1);
        for (StandardVariable stdVar : standardVariables) {
            stdVar.print(0);
        }
    } 

    @Test
    public void testFindStandardVariablesByNameOrSynonymWithProperties() throws Exception {
        System.out.println("Test getTraitDetailsByTAbbr");
        Set<StandardVariable> standardVariables = manager.findStandardVariablesByNameOrSynonym("Accession name");
        assertTrue(standardVariables.size() == 1);
        for (StandardVariable stdVar : standardVariables) {
            stdVar.print(0);
            Term term = manager.getTermById(stdVar.getId());
            term.print(4);   
        }
    }

	@Test
	public void testFindMethodById() throws Exception {
		System.out.println("Test findMethodById");
		
		// term doesn't exist
		Term term = manager.findMethodById(999999);
		assertTrue(term == null);
		
		// term exist but isn't a method
		term = manager.findMethodById(22066);
		assertTrue(term == null);
		
		// term does exist in central
		term = manager.findMethodById(20732);
		assertTrue(term != null);
		term.print(0);
		System.out.println();
		
		// add a method to local
		String name = "Test Method " + new Random().nextInt(10000);
		String definition = "Test Definition";
		term = manager.addMethod(name, definition);
		// term does exist in local
		
		term = manager.findMethodById(term.getId());
		assertTrue(term != null);
		term.print(0);
	}
	
	@Test
	public void testFindMethodByName() throws Exception {
		System.out.println("Test findMethodByName");
		
		// term doesn't exist
		Term term = manager.findMethodByName("foo bar");
		assertTrue(term == null);
		
		// term exist but isn't a method
		term = manager.findMethodByName("PANH");
		assertTrue(term == null);
		
		// term does exist in central
		term = manager.findMethodByName("Vegetative Stage");
		assertTrue(term != null);
		term.print(0);
		System.out.println();
	}
	
	
	@Test
	public void testFindStandardVariableByTraitScaleMethodNames() throws Exception{
		System.out.println("Test findStandardVariableByTraitScaleMethodNames");
		StandardVariable stdVar = manager.findStandardVariableByTraitScaleMethodNames("Cooperator", "DBCV", "Assigned");		
		System.out.println("testFindStandardVariableByTraitScaleMethodNames(): " + stdVar);
	}
	

	@Test
	public void testGetAllTermsByCvId() throws Exception{
		System.out.println("testGetAllTermsByCvId:");
		List<Term> terms = manager.getAllTermsByCvId(CvId.METHODS);		
		System.out.println("testGetAllTermsByCvId - Get Methods: " + terms.size());
		printTerms(terms);
		terms = manager.getAllTermsByCvId(CvId.PROPERTIES);		
		System.out.println("testGetAllTermsByCvId - Get Properties: " + terms.size());
		printTerms(terms);
		terms = manager.getAllTermsByCvId(CvId.SCALES);		
		System.out.println("testGetAllTermsByCvId - Get Scales: " + terms.size());
		printTerms(terms);
	}
	
	@Test
	public void testGetAllTermsByCvIdWithStartAndNumOfRows() throws Exception{
		System.out.println("testGetAllTermsByCvIdWithStartAndNumOfRows:");
		List<Term> terms1 = manager.getAllTermsByCvId(CvId.METHODS, 0, 2);		
		System.out.println("Get First 2 Methods: " + terms1.size());
		printTerms(terms1);
		
		List<Term> terms2 = manager.getAllTermsByCvId(CvId.METHODS, 2, 2);		
		System.out.println("Get Next 2 Methods: " + terms2.size());
		printTerms(terms2);
		
		terms1.addAll(terms2);
		
		List<Term> terms = manager.getAllTermsByCvId(CvId.METHODS, 0, 4);		
		System.out.println("Get First 4 Methods: " + terms.size());
		printTerms(terms);
		
		assertEquals(terms1, terms);
		
		List<Term> allTerms = manager.getAllTermsByCvId(CvId.METHODS);		
		System.out.println("Get All Methods: " + allTerms.size());
		
		List<Term> allTerms2 = manager.getAllTermsByCvId(CvId.METHODS, 0, allTerms.size());		
		System.out.println("Get All Methods with start and numOfRows: " + allTerms2.size());
		printTerms(allTerms2);
		
		assertEquals(allTerms, allTerms2);
		
	}
	
	private void printTerms(List<Term> terms){
		for (Term term : terms){
			term.print(4);
			System.out.println("    ----------");
		}
	}

	@Test
	public void testCountTermsByCvId() throws Exception{
		System.out.println("testCountTermsByCvId:");
		long count = manager.countTermsByCvId(CvId.METHODS);		
		System.out.println("testCountTermsByCvId() - Count All Methods: " + count);
		count = manager.countTermsByCvId(CvId.PROPERTIES);		
		System.out.println("testCountTermsByCvId() - Count All Properties: " + count);
		count = manager.countTermsByCvId(CvId.SCALES);		
		System.out.println("testCountTermsByCvId() - Count All Scales: " + count);
	}

	@AfterClass
	public static void tearDown() throws Exception {
		if (factory != null) {
			factory.close();
		}
	}
	
	@Test
	public void testGetMethodsForTrait() throws Exception{
		System.out.println("Test getMethodsForTrait");
		StandardVariable stdVar = manager.findStandardVariableByTraitScaleMethodNames("User", "DBCV", "Assigned");
		List<Term> terms = manager.getMethodsForTrait(stdVar.getProperty().getId());
		System.out.println("Size: " + terms.size());
		assertNotNull(terms);
		boolean hasAssigned = false;
		for (Term term : terms) {
			if(term.getName().equals("Assigned")) {
				hasAssigned = true;
			}
			System.out.println("method: " + term.getName());
		}
		assertTrue(hasAssigned);//should return Assigned
		
		//2nd test
		stdVar = manager.findStandardVariableByTraitScaleMethodNames("Germplasm entry", "Number", "Enumerated");
		terms = manager.getMethodsForTrait(stdVar.getProperty().getId());
		System.out.println("Size: " + terms.size());
		assertNotNull(terms);
		boolean hasEnumerated = false;
		for (Term term : terms) {
			if(term.getName().equals("Enumerated")) {
				hasEnumerated = true;
			}
			System.out.println("method: " + term.getName());
		}
		assertTrue(hasEnumerated);//should return Enumerated
	}
	
	@Test
	public void testGetScalesForTrait() throws Exception{
		System.out.println("Test getScalesForTrait");
		StandardVariable stdVar = manager.findStandardVariableByTraitScaleMethodNames("User", "DBCV", "Assigned");
		List<Term> terms = manager.getScalesForTrait(stdVar.getProperty().getId());
		System.out.println("Size: " + terms.size());
		assertNotNull(terms);
		boolean hasDBCV = false;
		for (Term term : terms) {
			if(term.getName().equals("DBCV")) {
				hasDBCV = true;
			}
			System.out.println("scale: " + term.getName());
		}
		assertTrue(hasDBCV);//should return DBCV
		
		//2nd test
		stdVar = manager.findStandardVariableByTraitScaleMethodNames("Germplasm entry", "Number", "Enumerated");
		terms = manager.getScalesForTrait(stdVar.getProperty().getId());
		System.out.println("Size: " + terms.size());
		assertNotNull(terms);
		boolean hasNumber = false;
		for (Term term : terms) {
			if(term.getName().equals("Number")) {
				hasNumber = true;
			}
			System.out.println("scale: " + term.getName());
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
	    System.out.println("testAddTerm():  " + term);
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
		System.out.println("Test findTermByName");
		
		// term doesn't exist
		Term term = manager.findTermByName("foo bar", CvId.METHODS);
		assertTrue(term == null);
		
		// term exist but isn't a method
		term = manager.findTermByName("PANH", CvId.METHODS);
		assertTrue(term == null);
		
		// term does exist in central
		term = manager.findTermByName("Vegetative Stage", CvId.METHODS);
		assertTrue(term != null);
		term.print(0);
		System.out.println();
		
	}
	
	@Test
	public void testGetDataTypes() throws Exception{
		System.out.println("testGetDataTypes:");
		List<Term> terms = manager.getDataTypes();		
		System.out.println("testGetDataTypes: " + terms.size());
		printTerms(terms);
	}
	
	@Test 
	public void testGetStandardVariablesForPhenotypicType() throws Exception{
		System.out.println("Test testGetStandardVariablesForPhenotypicType");
		
		PhenotypicType phenotypicType =  PhenotypicType.TRIAL_ENVIRONMENT;
		Integer start = 0;
		Integer numOfRows = 100;
		
		Map<String, StandardVariable> standardVariables = manager.getStandardVariablesForPhenotypicType(phenotypicType, start, numOfRows);
		
		for(Object key : standardVariables.keySet()) {
	        System.out.println(key + " : " + standardVariables.get(key).getId() + " : " + standardVariables.get(key).toString());
	    }
		
		System.out.println("count: " + standardVariables.size());
	}

	
    @Test
    public void testGetStandardVariablesInProjects() throws Exception {
    	List<String> headers = Arrays.asList("ENTRY","ENTRYNO", "PLOT", "TRIAL_NO", "TRIAL", "STUDY", "DATASET", "LOC", "LOCN", "NURSER", "Plot Number");
    	
    	Map<String, List<StandardVariable>> results = manager.getStandardVariablesInProjects(headers);

        System.out.println("testGetStandardVariablesInProjects(headers=" + headers + ") RESULTS:");
        for (String name : headers) {
        	System.out.print ("Header = " + name + ", StandardVariables: ");
        	if (results.get(name).size() > 0){
	        	for (StandardVariable var : results.get(name)){
	        		System.out.print(var.getId() + ", ");
	        	}
	        	System.out.println();
        	} else {
            	System.out.println ("    No standard variables found.");        		
        	}
        }
    }


	@Test
	public void testFindTermsByNameOrSynonym() throws Exception {
		// term doesn't exist
		List<Term> terms = manager.findTermsByNameOrSynonym("foo bar", CvId.METHODS);
		assertTrue(terms.size() == 0);
		
		// term exist but isn't a method
		terms = manager.findTermsByNameOrSynonym("PANH", CvId.METHODS);
		assertTrue(terms.size() == 0);
		
		// term does exist in central
		terms = manager.findTermsByNameOrSynonym("Vegetative Stage", CvId.METHODS);
		assertTrue(terms != null);
		terms.get(0).print(0);
		System.out.println();
		
		// name is in synonyms
		terms = manager.findTermsByNameOrSynonym("Accession Name", CvId.VARIABLES);
		assertTrue(terms != null);
		terms.get(0).print(0);
		System.out.println();

		// name is both in term and in synonyms 
		// need to modify the entry in cvterm where name = "Cooperator" to have cv_id = 1010
		terms = manager.findTermsByNameOrSynonym("Cooperator", CvId.PROPERTIES);
		assertTrue(terms != null);
		terms.get(0).print(0);
		System.out.println();

	}

	@Test
	public void testGetIsAOfProperties() throws Exception{
		System.out.println("testGetIsAOfProperties:");
		List<Term> terms1 = manager.getIsAOfProperties(0, 2);		
		System.out.println("Get First 2 isA: " + terms1.size());
		printTerms(terms1);
		
		List<Term> terms2 = manager.getIsAOfProperties(2, 2);		
		System.out.println("Get Next 2 isA: " + terms2.size());
		printTerms(terms2);
		
		terms1.addAll(terms2);
		
		List<Term> terms = manager.getIsAOfProperties(0, 4);		
		System.out.println("Get First 4 isA: " + terms.size());
		printTerms(terms);
		
		assertEquals(terms1, terms);
		
		List<Term> allTerms = manager.getIsAOfProperties(0,0);		
		System.out.println("Get All isA: " + allTerms.size());
		printTerms(allTerms);
		
	}
	
	@Test
	public void testCountIsAOfProperties() throws Exception{
		System.out.println("testCountIsAOfProperties:");
		long asOf = manager.countIsAOfProperties();		
		System.out.println("count is a properties " + asOf);
		
	}
	
	@Test
	public void testAddProperty() throws Exception {
		String name = "Germplasm type";
		String definition = "Germplasm type description";
		int isA = 1087;
		
        System.out.println("testAddProperty(name=" + name + ", definition=" + definition + ", isA=" + isA + "): ");
		Term term = manager.addProperty(name, definition, isA);
        System.out.println("testAddProperty(name=" + name + ", definition=" + definition + ", isA=" + isA + "): " );
        	term.print(4);

	}
	
	@Test
	public void testGetProperty() throws Exception {
		int termId = 2452;
		
		Property property = manager.getProperty(termId);
		
		System.out.println(property);
	}
	
}
