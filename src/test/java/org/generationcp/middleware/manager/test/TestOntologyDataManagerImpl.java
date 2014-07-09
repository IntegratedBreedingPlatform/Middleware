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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.generationcp.middleware.dao.oms.StandardVariableDao;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.NameSynonym;
import org.generationcp.middleware.domain.dms.NameType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.StandardVariableSummary;
import org.generationcp.middleware.domain.dms.VariableConstraints;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Property;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TermProperty;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.domain.oms.TraitClassReference;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.utils.test.Debug;
import org.generationcp.middleware.utils.test.TestOutputFormatter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

@SuppressWarnings("unused")
public class TestOntologyDataManagerImpl extends TestOutputFormatter {

    private static final Integer CV_TERM_ID = 1010;
    private static final String CV_TERM_NAME = "Study Information";
    private static final Integer STD_VARIABLE_ID = 8350; // 8310; 
    
    private static final int PLANT_HEIGHT_ID = 18020, GRAIN_YIELD_ID = 18000;
	
	private static ManagerFactory factory;
	private static OntologyDataManager manager;

	@BeforeClass
	public static void setUp() throws Exception {
		DatabaseConnectionParameters local = new DatabaseConnectionParameters("testDatabaseConfig.properties", "local");
		DatabaseConnectionParameters central = new DatabaseConnectionParameters("testDatabaseConfig.properties", "central");
		factory = new ManagerFactory(local, central);
		manager = factory.getNewOntologyDataManager();
	}

	@Test
	public void testGetCvTermById() throws Exception {
		Term term = manager.getTermById(6040);
		assertNotNull(term);
		Debug.println(INDENT, "testGetCvTermById(): " + term);
	}
	
	@Test
	public void testGetStandardVariable() throws Exception {
		StandardVariable stdVar = manager.getStandardVariable(STD_VARIABLE_ID);
		assertNotNull(stdVar);		
		Debug.println(INDENT, "testGetStandardVariable(): " + stdVar);
	}
	
	@Test
	public void getStandVariableList() throws MiddlewareQueryException {
		List<Integer> ids = Arrays.asList(new Integer[] {1,2,3,4,5});
		List<StandardVariable> standardVariables = manager.getStandardVariables(ids);
		assertNotNull(standardVariables);
		assertTrue(standardVariables.size() > 0);
		for (StandardVariable standardVariable : standardVariables) {
			assertTrue(ids.contains(standardVariable.getId()));
		}
	}
	
	@Test
	public void testGetStandardVariableSummariesCentral() throws MiddlewareQueryException {
		final int PLANT_HEIGHT_ID = 18020, GRAIN_YIELD_ID = 18000;		
		List<Integer> idList = Arrays.asList(PLANT_HEIGHT_ID, GRAIN_YIELD_ID);
		
		List<StandardVariableSummary> summaries = manager.getStandardVariableSummaries(idList);
		
		assertNotNull(summaries);
		assertEquals(idList.size(), summaries.size());
		for(StandardVariableSummary summary : summaries) {
			assertTrue(idList.contains(summary.getId()));
		}		
	}
	
	@Test
	public void testGetStandardVariableSummaryCentral() throws MiddlewareQueryException {		
		//Load summary from the view based method
		StandardVariableSummary summary = manager.getStandardVariableSummary(PLANT_HEIGHT_ID);	
		Assert.assertNotNull(summary);

		//Load details using the ususal method
		StandardVariable details = manager.getStandardVariable(PLANT_HEIGHT_ID);
		Assert.assertNotNull(details);
		
		//Make sure that the summary data loaded from view based method matches with detailed data loaded using the usual method.
		assertVariableDataMatches(details, summary);	
	}
	
	@Test
	public void testGetStandardVariableSummaryLocal() throws MiddlewareQueryException {			
		//First create a local Standardvariable
		StandardVariable myOwnPlantHeight = new StandardVariable();
		myOwnPlantHeight.setName("MyOwnPlantHeight " + new Random().nextInt(1000));
		myOwnPlantHeight.setDescription(myOwnPlantHeight.getName() + " - Description.");
		myOwnPlantHeight.setProperty(new Term(15020, "Plant height", "Plant height"));
		myOwnPlantHeight.setMethod(new Term(16010, "Soil to tip at maturity", "Soil to tip at maturity"));
		
		Term myOwnScale = new Term();
		myOwnScale.setName("MyOwnScale " + new Random().nextInt(1000));
		myOwnScale.setDefinition(myOwnScale.getName() + " - Description.");
		myOwnPlantHeight.setScale(myOwnScale);
		
		myOwnPlantHeight.setIsA(new Term(1340, "Agronomic", "Agronomic"));
		myOwnPlantHeight.setDataType(new Term(1110, "Numeric variable", "Variable with numeric values either continuous or integer"));
		myOwnPlantHeight.setStoredIn(new Term(1043, "Observation variate", "Phenotypic data stored in phenotype.value"));
		
		manager.addStandardVariable(myOwnPlantHeight);
		
		//Load details using existing method
		StandardVariable details = manager.getStandardVariable(myOwnPlantHeight.getId());
		Assert.assertNotNull(details);
		
		//Load summary from the view based method
		StandardVariableSummary summary = manager.getStandardVariableSummary(myOwnPlantHeight.getId());	
		Assert.assertNotNull(summary);
		
		//Make sure that the summary data loaded from view matches with details data loaded using the usual method.
		assertVariableDataMatches(details, summary);

		//Test done. Cleanup the test data created.
		manager.deleteStandardVariable(myOwnPlantHeight.getId());
	}
	
	private void assertVariableDataMatches(StandardVariable details, StandardVariableSummary summary) {
		Assert.assertEquals(new Integer(details.getId()), summary.getId());
		Assert.assertEquals(details.getName(), details.getName());
		Assert.assertEquals(details.getDescription(), details.getDescription());
		
		assertTermDataMatches(details.getProperty(), summary.getProperty());
		assertTermDataMatches(details.getMethod(), summary.getMethod());
		assertTermDataMatches(details.getScale(), summary.getScale());
		assertTermDataMatches(details.getIsA(), summary.getIsA());
		assertTermDataMatches(details.getDataType(), summary.getDataType());
		assertTermDataMatches(details.getStoredIn(), summary.getStoredIn());
		
		Assert.assertEquals(details.getPhenotypicType(), summary.getPhenotypicType());
	}
	
	private void assertTermDataMatches(Term termDetails, TermSummary termSummary) {
		Assert.assertEquals(new Integer(termDetails.getId()), termSummary.getId());
		Assert.assertEquals(termDetails.getName(), termSummary.getName());
		Assert.assertEquals(termDetails.getDefinition(), termSummary.getDefinition());
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
		
	    Debug.println(INDENT, "testCopyStandardVariable(): \n    " + stdVar + "\n    " + stdVar2);
	}
	
	@Test
	public void testStandardVariableCache() throws Exception {
		Debug.println(INDENT, "testStandardVariableCache(): ");
		manager.getStandardVariable(STD_VARIABLE_ID); 		// First call to getStandardVariable() will put the value to the cache
		manager.getStandardVariable(STD_VARIABLE_ID);		// Second (and subsequent) calls will retrieve the value from the cache
	}
	
	@Test
	public void testNameSynonyms() throws Exception {
		StandardVariable sv = manager.getStandardVariable(8383);
		sv.print(INDENT);
	}
	
	@Test
	public void testAddStandardVariable() throws Exception {
	    //create new trait
	    String propertyName = "property name " + new Random().nextInt(10000);
	    manager.addProperty(propertyName, "test property", 1087);
		
	    StandardVariable stdVariable = new StandardVariable();
		stdVariable.setName("variable name " + new Random().nextInt(10000));
		stdVariable.setDescription("variable description");
		//stdVariable.setProperty(new Term(2002, "User", "Database user"));
		stdVariable.setProperty(manager.findTermByName(propertyName, CvId.PROPERTIES));
		stdVariable.setMethod(new Term(4030, "Assigned", "Term, name or id assigned"));
		stdVariable.setScale(new Term(6000, "DBCV", "Controlled vocabulary from a database"));
		stdVariable.setStoredIn(new Term(1010, "Study information", "Study element"));
		stdVariable.setDataType(new Term(1120, "Character variable", "variable with char values"));
		stdVariable.setIsA(new Term(1050,"Study condition","Study condition class"));
		stdVariable.setEnumerations(new ArrayList<Enumeration>());
		stdVariable.getEnumerations().add(new Enumeration(10000, "N", "Nursery", 1));
		stdVariable.getEnumerations().add(new Enumeration(10001, "HB", "Hybridization nursery", 2));
		stdVariable.getEnumerations().add(new Enumeration(10002, "PN", "Pedigree nursery", 3));
		stdVariable.setConstraints(new VariableConstraints(100.0, 999.0));
		stdVariable.setCropOntologyId("CROP-TEST");
		
		try{
			manager.addStandardVariable(stdVariable);
		} catch (MiddlewareQueryException e) {
			if (e.getMessage().contains("already exists")){
				// Ignore. The test run successfully before.
			}
		}
		
		Debug.println(INDENT, "Standard variable saved: " + stdVariable.getId());
	}
	
	@Test
	public void testAddStandardVariableWithMissingScalePropertyMethod() throws Exception {
		StandardVariable stdVariable = new StandardVariable();
		stdVariable.setName("Test SPM" + new Random().nextInt(10000));
		stdVariable.setDescription("Std variable with new scale, property, method");
		
		Term newProperty = new Term(2451, "Environment", "Environment");
		Term property = manager.findTermByName(newProperty.getName(),CvId.PROPERTIES);
		if(property == null) {
			Debug.println(INDENT, "new property = " + newProperty.getName());
			property = newProperty;
		} else {
			Debug.println(INDENT, "property id = " + property.getId());
		}
		Term newScale = new Term(6020, "Text", "Text");
		Term scale = manager.findTermByName(newScale.getName(),CvId.SCALES);
		if(scale == null) {
			Debug.println(INDENT, "new scale = " + newScale.getName());
			scale = newScale;
		} else {
			Debug.println(INDENT, "scale id = " + scale.getId());
		}
		Term newMethod = new Term(0, "Test Method", "Test Method");
		Term method = manager.findTermByName(newMethod.getName(),CvId.METHODS);
		if(method == null) {
			Debug.println(INDENT, "new method = " + newMethod.getName());
			method = newMethod;
		} else {
			Debug.println(INDENT, "method id = " + method.getId());
		}
		stdVariable.setProperty(property);
		stdVariable.setScale(scale);
		stdVariable.setMethod(method);
		
		stdVariable.setStoredIn(new Term(1010, "Study information", "Study element"));
		//added as this is required
		stdVariable.setIsA(new Term(1050,"Study condition","Study condition class"));		
		stdVariable.setDataType(new Term(1120, "Character variable", "variable with char values"));
		stdVariable.setEnumerations(new ArrayList<Enumeration>());
		stdVariable.getEnumerations().add(new Enumeration(10000, "N", "Nursery", 1));
		stdVariable.getEnumerations().add(new Enumeration(10001, "HB", "Hybridization nursery", 2));
		stdVariable.getEnumerations().add(new Enumeration(10002, "PN", "Pedigree nursery", 3));
		stdVariable.setConstraints(new VariableConstraints(100.0, 999.0));
		
		manager.addStandardVariable(stdVariable);
		
		Debug.println(INDENT, "Standard variable saved: " + stdVariable.getId());
	}
	

    
    @Test
    public void testAddStandardVariableEnumeration() throws Exception {
        int standardVariableId = 22554;
        String name = "8";
        String description = "Fully exserted";
        StandardVariable standardVariable = manager.getStandardVariable(standardVariableId);
        Enumeration validValue = new Enumeration(null, name, description, 1);

        Debug.printObject(INDENT, standardVariable);
        manager.saveOrUpdateStandardVariableEnumeration(standardVariable, validValue);
        Debug.printObject(INDENT, validValue);
        standardVariable = manager.getStandardVariable(standardVariableId);
        Debug.printObject(INDENT, standardVariable);
        assertNotNull(standardVariable.getEnumeration(validValue.getId()));
        
        // TO VERIFY IN MYSQL, delete the lines marked (*) below, then check in local: 
        // select * from cvterm where name = "8" and definition = "Fully exserted";
        // select * from cvterm_relationship where subject_id = 22554;
        
        // (*) clean up
        manager.deleteStandardVariableEnumeration(standardVariableId, validValue.getId());
    }
    
    @Test
    public void testUpdateStandardVariableEnumeration() throws Exception {
        // Case 1: NEW VALID VALUE
        int standardVariableId = 22554;
        String name = "8";
        String description = "Fully exserted";
        StandardVariable standardVariable = manager.getStandardVariable(standardVariableId);
        Enumeration validValue = new Enumeration(null, name, description, 1);

        Debug.printObject(INDENT, standardVariable);
        manager.saveOrUpdateStandardVariableEnumeration(standardVariable, validValue);
        Integer validValueGeneratedId1 = validValue.getId();
        Debug.printObject(INDENT, validValue);
        standardVariable = manager.getStandardVariable(standardVariableId);
        Debug.printObject(INDENT, standardVariable);
        assertNotNull(standardVariable.getEnumeration(validValue.getId()));
        
        // TO VERIFY IN MYSQL, delete the lines marked (*) below, then check in local: 
        // select * from cvterm where name = "8" and definition = "Fully exserted";
        // select * from cvterm_relationship where subject_id = 22554;
        
        // Case 2: UPDATE CENTRAL VALID VALUE
        Integer validValueId = 22667;
        name = "3"; 
        description = "Moderately exserted"; // Original value in central:  "Moderately well exserted"
        validValue = new Enumeration(validValueId, name, description, 1);
        manager.saveOrUpdateStandardVariableEnumeration(standardVariable, validValue);
        
        Debug.printObject(INDENT, validValue);
        standardVariable = manager.getStandardVariable(standardVariableId);
        Debug.printObject(INDENT, standardVariable);
        assertNotNull(standardVariable.getEnumeration(validValue.getId()));
        
        // Case 3: UPDATE LOCAL VALID VALUE
        description = "Moderately well exserted";
        validValue.setDescription(description);
        manager.saveOrUpdateStandardVariableEnumeration(standardVariable, validValue);
        
        Debug.printObject(INDENT, validValue);
        standardVariable = manager.getStandardVariable(standardVariableId);
        Debug.printObject(INDENT, standardVariable);
        assertTrue(standardVariable.getEnumeration(validValue.getId()).getDescription().equals(description));
        
        // (*) clean up
        manager.deleteStandardVariableEnumeration(standardVariableId, validValueGeneratedId1);
        manager.deleteStandardVariableEnumeration(standardVariableId, validValue.getId());
    }
	
    @Test
	public void testAddMethod() throws Exception {
		String name = "Test Method " + new Random().nextInt(10000);
		String definition = "Test Definition";
		Term term = manager.addMethod(name, definition);
		assertTrue(term.getId() < 0);
	    Debug.println(INDENT, "testAddMethod():  " + term);
	    term = manager.getTermById(term.getId());
	    Debug.println(INDENT, "From db:  " + term);
	}
	
	/*@Test
	public void testGetStandardVariableIdByPropertyScaleMethod() throws Exception {
		Integer propertyId = Integer.valueOf(2010);
		Integer scaleId = Integer.valueOf(6000);
		Integer methodId = Integer.valueOf(4030);
		
		Integer varid = manager.getStandardVariableIdByPropertyScaleMethod(propertyId, scaleId, methodId);
		assertNotNull(varid);
		Debug.println(INDENT, "testGetStandadardVariableIdByPropertyScaleMethod() Results: " + varid);
	}*/
	
    @Test
    public void testFindStandardVariablesByNameOrSynonym() throws Exception {
        Debug.println(INDENT, "Test FindStandardVariablesByNameOrSynonym");
        Set<StandardVariable> standardVariables = manager.findStandardVariablesByNameOrSynonym("foo bar");
        assertSame(standardVariables.size(), 0);
        
        standardVariables = manager.findStandardVariablesByNameOrSynonym("Accession name");
        assertSame(standardVariables.size(), 1);
        for (StandardVariable stdVar : standardVariables) {
            stdVar.print(INDENT);
        }
        
        standardVariables = manager.findStandardVariablesByNameOrSynonym("THR");
        assertSame(standardVariables.size(), 1);
        for (StandardVariable stdVar : standardVariables) {
            stdVar.print(INDENT);
        }
    } 
    
    @Test
    public void testFindStandardVariablesByNameOrSynonymWithProperties() throws Exception {
        Debug.println(INDENT, "Test getTraitDetailsByTAbbr");
        Set<StandardVariable> standardVariables = manager.findStandardVariablesByNameOrSynonym("Accession name");
        assertSame(standardVariables.size(), 1);
        for (StandardVariable stdVar : standardVariables) {
            stdVar.print(INDENT);
            Term term = manager.getTermById(stdVar.getId());
            term.print(INDENT);   
        }
    }

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
		term.print(INDENT);
		Debug.println(INDENT, "");
		
		// add a method to local
		String name = "Test Method " + new Random().nextInt(10000);
		String definition = "Test Definition";
		term = manager.addMethod(name, definition);
		// term does exist in local
		
		term = manager.findMethodById(term.getId());
		assertNotNull(term);
		term.print(INDENT);
	}
	
	@Test
	public void testFindMethodByName() throws Exception {
		Debug.println(INDENT, "Test findMethodByName");
		
		// term doesn't exist
		Term term = manager.findMethodByName("foo bar");
		assertNull(term);
		
		// term exist but isn't a method
		term = manager.findMethodByName("PANH");
		assertNull(term);
		
		// term does exist in central
		term = manager.findMethodByName("Vegetative Stage");
		assertNotNull(term);
		term.print(INDENT);
		Debug.println(INDENT, "");
	}
	
	
	@Test
	public void testFindStandardVariableByTraitScaleMethodNames() throws Exception{
		StandardVariable stdVar = manager.findStandardVariableByTraitScaleMethodNames("Cooperator", "DBCV", "Assigned");		
		Debug.println(INDENT, "testFindStandardVariableByTraitScaleMethodNames(): " + stdVar);
	}
	

	@Test
	public void testGetAllTermsByCvId() throws Exception{
		List<Term> terms = manager.getAllTermsByCvId(CvId.METHODS);		
		Debug.println(INDENT, "testGetAllTermsByCvId - Get Methods: " + terms.size());
		printTerms(terms);
		terms = manager.getAllTermsByCvId(CvId.PROPERTIES);		
		Debug.println(INDENT, "testGetAllTermsByCvId - Get Properties: " + terms.size());
		printTerms(terms);
		terms = manager.getAllTermsByCvId(CvId.SCALES);		
		Debug.println(INDENT, "testGetAllTermsByCvId - Get Scales: " + terms.size());
		printTerms(terms);
	}
	
	@Test
	public void testGetAllTermsByCvIdWithStartAndNumOfRows() throws Exception{
		List<Term> terms1 = manager.getAllTermsByCvId(CvId.METHODS, 0, 2);		
		Debug.println(INDENT, "Get First 2 Methods: " + terms1.size());
		printTerms(terms1);
		
		List<Term> terms2 = manager.getAllTermsByCvId(CvId.METHODS, 2, 2);		
		Debug.println(INDENT, "Get Next 2 Methods: " + terms2.size());
		printTerms(terms2);
		
		terms1.addAll(terms2);
		
		List<Term> terms = manager.getAllTermsByCvId(CvId.METHODS, 0, 4);		
		Debug.println(INDENT, "Get First 4 Methods: " + terms.size());
		printTerms(terms);
		
		assertEquals(terms1, terms);
		
		List<Term> allTerms = manager.getAllTermsByCvId(CvId.METHODS);		
		Debug.println(INDENT, "Get All Methods: " + allTerms.size());
		
		List<Term> allTerms2 = manager.getAllTermsByCvId(CvId.METHODS, 0, allTerms.size());		
		Debug.println(INDENT, "Get All Methods with start and numOfRows: " + allTerms2.size());
		printTerms(allTerms2);
		
		assertEquals(allTerms, allTerms2);
		
	}
	
	private void printTerms(List<Term> terms){
		for (Term term : terms){
			term.print(INDENT);
			Debug.println(INDENT, "    ----------");
		}
	}

	@Test
	public void testCountTermsByCvId() throws Exception{
		long count = manager.countTermsByCvId(CvId.METHODS);		
		Debug.println(INDENT, "testCountTermsByCvId() - Count All Methods: " + count);
		count = manager.countTermsByCvId(CvId.PROPERTIES);		
		Debug.println(INDENT, "testCountTermsByCvId() - Count All Properties: " + count);
		count = manager.countTermsByCvId(CvId.SCALES);		
		Debug.println(INDENT, "testCountTermsByCvId() - Count All Scales: " + count);
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
		Debug.println(INDENT, "Size: " + terms.size());
		assertNotNull(terms);
		boolean hasAssigned = false;
		for (Term term : terms) {
			if(term.getName().equals("Assigned")) {
				hasAssigned = true;
			}
			Debug.println(INDENT, "method: " + term.getName());
		}
		assertTrue(hasAssigned);//should return Assigned
		
		//2nd test
		stdVar = manager.findStandardVariableByTraitScaleMethodNames("Germplasm entry", "Number", "Enumerated");
		terms = manager.getMethodsForTrait(stdVar.getProperty().getId());
		Debug.println(INDENT, "Size: " + terms.size());
		assertNotNull(terms);
		boolean hasEnumerated = false;
		for (Term term : terms) {
			if(term.getName().equals("Enumerated")) {
				hasEnumerated = true;
			}
			Debug.println(INDENT, "method: " + term.getName());
		}
		assertTrue(hasEnumerated);//should return Enumerated
	}
	
	@Test
	public void testGetScalesForTrait() throws Exception{
		StandardVariable stdVar = manager.findStandardVariableByTraitScaleMethodNames("User", "DBCV", "Assigned");
		List<Term> terms = manager.getScalesForTrait(stdVar.getProperty().getId());
		Debug.println(INDENT, "Size: " + terms.size());
		assertNotNull(terms);
		boolean hasDBCV = false;
		for (Term term : terms) {
			if(term.getName().equals("DBCV")) {
				hasDBCV = true;
			}
			Debug.println(INDENT, "scale: " + term.getName());
		}
		assertTrue(hasDBCV);//should return DBCV
		
		//2nd test
		stdVar = manager.findStandardVariableByTraitScaleMethodNames("Germplasm entry", "Number", "Enumerated");
		terms = manager.getScalesForTrait(stdVar.getProperty().getId());
		Debug.println(INDENT, "Size: " + terms.size());
		assertNotNull(terms);
		boolean hasNumber = false;
		for (Term term : terms) {
			if(term.getName().equals("Number")) {
				hasNumber = true;
			}
			Debug.println(INDENT, "scale: " + term.getName());
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
	    Debug.println(INDENT, "testAddTerm():  " + term);
	    term = manager.getTermById(term.getId());
	    Debug.println(INDENT, "From db:  " + term);
	    
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
		term.print(INDENT);
		Debug.println(INDENT, "");
		
	}
	
	@Test
	public void testGetDataTypes() throws Exception{
		List<Term> terms = manager.getDataTypes();		
		Debug.println(INDENT, "testGetDataTypes: " + terms.size());
		printTerms(terms);
	}
	
	@Test 
	public void testGetStandardVariablesForPhenotypicType() throws Exception{
		PhenotypicType phenotypicType =  PhenotypicType.TRIAL_ENVIRONMENT;
		Integer start = 0;
		Integer numOfRows = 100;
		
		Map<String, StandardVariable> standardVariables = manager.getStandardVariablesForPhenotypicType(phenotypicType, start, numOfRows);
		
		for(Object key : standardVariables.keySet()) {
	        Debug.println(key + " : " + standardVariables.get(key).getId() + " : " + standardVariables.get(key).toString());
	    }
		
		Debug.println(INDENT, "count: " + standardVariables.size());
	}

	
    @Test
    public void testGetStandardVariablesInProjects() throws Exception {
    	List<String> headers = Arrays.asList("ENTRY","ENTRYNO", "PLOT", "TRIAL_NO", "TRIAL", "STUDY", "DATASET", "LOC", "LOCN", "NURSER", "Plot Number");
    	
    	Map<String, List<StandardVariable>> results = manager.getStandardVariablesInProjects(headers);

        Debug.println(INDENT, "testGetStandardVariablesInProjects(headers=" + headers + ") RESULTS:");
        for (String name : headers) {
            Debug.println(INDENT, "Header = " + name + ", StandardVariables: ");
        	if (results.get(name).size() > 0){
	        	for (StandardVariable var : results.get(name)){
	        		Debug.println(INDENT, var.getId() + ", ");
	        	}
	        	Debug.println(INDENT, "");
        	} else {
        	    Debug.println(INDENT, "    No standard variables found.");        		
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
		terms.get(0).print(INDENT);
		Debug.println(INDENT, "");
		
		// name is in synonyms
		terms = manager.findTermsByNameOrSynonym("Accession Name", CvId.VARIABLES);
		assertNotNull(terms);
		terms.get(0).print(INDENT);
		Debug.println(INDENT, "");

		// name is both in term and in synonyms 
		// need to modify the entry in cvterm where name = "Cooperator" to have cv_id = 1010
		terms = manager.findTermsByNameOrSynonym("Cooperator", CvId.PROPERTIES);
		assertNotNull(terms);
		for (Term term: terms){
			term.print(INDENT);
			Debug.println(INDENT, "");
		}

	}

	@Test
	public void testGetIsAOfProperties() throws Exception{
		List<Term> terms1 = manager.getIsAOfProperties(0, 2);		
		Debug.println(INDENT, "Get First 2 isA: " + terms1.size());
		printTerms(terms1);
		
		List<Term> terms2 = manager.getIsAOfProperties(2, 2);		
		Debug.println(INDENT, "Get Next 2 isA: " + terms2.size());
		printTerms(terms2);
		
		terms1.addAll(terms2);
		
		List<Term> terms = manager.getIsAOfProperties(0, 4);		
		Debug.println(INDENT, "Get First 4 isA: " + terms.size());
		printTerms(terms);
		
		assertEquals(terms1, terms);
		
		List<Term> allTerms = manager.getIsAOfProperties(0,0);		
		Debug.println(INDENT, "Get All isA: " + allTerms.size());
		printTerms(allTerms);
		
	}
	
	@Test
	public void testCountIsAOfProperties() throws Exception{
		long asOf = manager.countIsAOfProperties();		
		Debug.println(INDENT, "count is a properties " + asOf);
	}
	
	@Test
	public void testAddProperty() throws Exception {
		String name = "Germplasm type 3";
		String definition = "Germplasm type description 3";
		int isA = 1087;
		
        Debug.println(INDENT, "testAddProperty(name=" + name + ", definition=" + definition + ", isA=" + isA + "): ");
		Term term = manager.addProperty(name, definition, isA);
        	term.print(INDENT);

	}
	
    @Test
    public void testGetProperty() throws Exception {
        int termId = 2452;
        
        Property property = manager.getProperty(termId);
        
        Debug.println(INDENT, property.toString());
    }
    
    @Test
    public void testGetAllTraitGroupsHierarchy() throws Exception {
        List<TraitClassReference> traitGroups = manager.getAllTraitGroupsHierarchy(true);
        for (TraitClassReference traitGroup : traitGroups){
            traitGroup.print(INDENT);
        }
    }
    
    
	@Test
	public void testGetPropertyByName() throws Exception {
		String name = "Season";
		Property property = manager.getProperty(name);
		Debug.println(INDENT, property.toString());
	}
	
	@Test 
        public void testGetAllStandardVariable() throws Exception{
            Set<StandardVariable> standardVariables = manager.getAllStandardVariables();
            for (StandardVariable stdVar : standardVariables) {
                stdVar.print(INDENT);
            }
            
            Debug.println(INDENT, "count: " + standardVariables.size());
        }
	

    @Test
    public void testGetStandardVariablesByTraitClass() throws MiddlewareQueryException {
        List<StandardVariable> vars = manager.getStandardVariables(Integer.valueOf(1410), null, null, null);
        
        assertFalse(vars.isEmpty());
        
        StandardVariable expectedVar = new StandardVariable();
        expectedVar.setId(21744);
        assertTrue(vars.contains(expectedVar));
        
        for (StandardVariable var : vars){
            Debug.println(INDENT, var.toString());
        }
    }

    @Test
    public void testGetStandardVariablesByProperty() throws MiddlewareQueryException {
        List<StandardVariable> vars = manager.getStandardVariables(null, Integer.valueOf(20109), null, null);
       
        assertFalse(vars.isEmpty()); 

        StandardVariable expectedVar = new StandardVariable();
        expectedVar.setId(20961);
        assertTrue(vars.contains(expectedVar));// stdvarid = 20961
        
        for (StandardVariable var : vars){
            Debug.println(INDENT, var.toString());
        }
    }

    @Test
    public void testGetStandardVariablesByMethod() throws MiddlewareQueryException {
        List<StandardVariable> vars = manager.getStandardVariables(null, null, Integer.valueOf(20643), null);
        
        assertFalse(vars.isEmpty());
        
        StandardVariable expectedVar = new StandardVariable();
        expectedVar.setId(20954);
        assertTrue(vars.contains(expectedVar));
        
        for (StandardVariable var : vars){
            Debug.println(INDENT, var.toString());
        }
    }

    @Test
    public void testGetStandardVariablesByScale() throws MiddlewareQueryException {
        List<StandardVariable> vars = manager.getStandardVariables(null, null, null, Integer.valueOf(20392));
        
        assertFalse(vars.isEmpty());
        
        StandardVariable expectedVar = new StandardVariable();
        expectedVar.setId(20953);
        assertTrue(vars.contains(expectedVar));
       
        for (StandardVariable var : vars){
            Debug.println(INDENT, var.toString());
        }
    }

	
    @Test
    public void testAddOrUpdateTermAndRelationshipFoundInCentral() throws Exception {
        String name = "Season";
        String definition = "Growing Season " + (int) (Math.random() * 100); // add random number to see the update
        try{
            manager.addOrUpdateTermAndRelationship(name, definition, CvId.PROPERTIES, TermId.IS_A.getId(), 1340, null);
        } catch (MiddlewareException e){
            Debug.println(INDENT, "MiddlewareException expected: \"" + e.getMessage() + "\"");
            assertTrue(e.getMessage().contains(" is retrieved from the central database and cannot be updated"));
        }
    }


    @Test
    public void testAddOrUpdateTermAndRelationshipNotInCentral() throws Exception {
        String name = "Study condition NEW";
        String definition = "Study condition NEW class " + (int) (Math.random() * 100); // add random number to see the update
        Term origTerm = manager.findTermByName(name, CvId.PROPERTIES);
        Term newTerm = manager.addOrUpdateTermAndRelationship(name, definition, CvId.PROPERTIES, TermId.IS_A.getId(), 1340, null);
        Debug.println(INDENT, "Original:  " + origTerm);
        Debug.println(INDENT, "Updated :  " + newTerm);
        
        if (origTerm != null) { // if the operation is update, the ids must be same
            assertSame(origTerm.getId(), newTerm.getId());
        }
    }

    @Test
    public void testUpdateTermAndRelationshipFoundInCentral() throws Exception {
        String name = "Slope";
        String definition = "Land slope " + (int) (Math.random() * 100); // add random number to see the update
        Term origTerm = manager.findTermByName(name, CvId.PROPERTIES);
        try{
            manager.updateTermAndRelationship(new Term(origTerm.getId(), name, definition), TermId.IS_A.getId(), 1340);
        } catch (MiddlewareException e){
            Debug.println(INDENT, "MiddlewareException expected: \"" + e.getMessage() + "\"");
            assertTrue(e.getMessage().contains("Cannot update terms in central"));
        }
    }

    @Test
    public void testUpdateTermAndRelationshipNotInCentral() throws Exception {
        String name = "Slope NEW";
        String definition = "Slope NEW class " + (int) (Math.random() * 100); // add random number to see the update

        Term origTerm = manager.findTermByName(name, CvId.PROPERTIES);
        if (origTerm == null){ // first run, add before update
            origTerm = manager.addOrUpdateTermAndRelationship(name, definition, CvId.PROPERTIES, TermId.IS_A.getId(), 1340, null);
        }

        manager.updateTermAndRelationship(new Term(origTerm.getId(), name, definition), TermId.IS_A.getId(), 1340);
        Term newTerm = manager.findTermByName(name, CvId.PROPERTIES);
        Debug.println(INDENT, "Original:  " + origTerm);
        Debug.println(INDENT, "Updated :  " + newTerm);
        
        if (origTerm != null && newTerm != null) { 
            assertTrue(newTerm.getDefinition().equals(definition));
        }
    }

    @Test
    public void testAddOrUpdateTermFoundInCentral() throws Exception {
        String name = "Score";
        String definition = "Score NEW " + (int) (Math.random() * 100); // add random number to see the update
        try{
            manager.addOrUpdateTerm(name, definition, CvId.SCALES);
        } catch (MiddlewareQueryException e){
            Debug.println(INDENT, "MiddlewareException expected: \"" + e.getMessage() + "\"");
            assertTrue(e.getMessage().contains("The term you entered is invalid"));
        }
    }

    @Test
    public void testAddOrUpdateTermNotInCentral() throws Exception {
        String name = "Real";
        String definition = "Real Description NEW " + (int) (Math.random() * 100); // add random number to see the update
        Term origTerm = manager.findTermByName(name, CvId.SCALES);
        Term newTerm = manager.addOrUpdateTerm(name, definition, CvId.SCALES);
        Debug.println(INDENT, "Original:  " + origTerm);
        Debug.println(INDENT, "Updated :  " + newTerm);
        
        if (origTerm != null) { // if the operation is update, the ids must be same
            assertSame(origTerm.getId(), newTerm.getId());
        }
    }
    
    @Test
    public void testUpdateTermFoundInCentral() throws Exception {
        String name = "Score";
        String definition = "Score NEW " + (int) (Math.random() * 100); // add random number to see the update
        Term origTerm = manager.findTermByName(name, CvId.SCALES);
        try{
            manager.updateTerm(new Term(origTerm.getId(), name, definition));
        } catch (MiddlewareException e){
            Debug.println(INDENT, "MiddlewareException expected: \"" + e.getMessage() + "\"");
            assertTrue(e.getMessage().contains("Cannot update terms in central"));
        }
    }

    @Test
    public void testUpdateTermNotInCentral() throws Exception {
        String name = "Integer2";
        String definition = "Integer NEW " + (int) (Math.random() * 100); // add random number to see the update

        Term origTerm = manager.findTermByName(name, CvId.SCALES);
        if (origTerm == null){ // first run, add before update
            origTerm = manager.addTerm(name, definition, CvId.SCALES);
        }
        
        manager.updateTerm(new Term(origTerm.getId(), name, definition));
        Term newTerm = manager.findTermByName(name, CvId.SCALES);

        Debug.println(INDENT, "Original:  " + origTerm);
        Debug.println(INDENT, "Updated :  " + newTerm);
        
        if (origTerm != null && newTerm != null) { 
            assertTrue(newTerm.getDefinition().equals(definition));
        }
    }
    
    @Test
    public void testGetStandardVariableIdByTermId() throws Exception {
        String propertyName = "property name " + new Random().nextInt(10000);
        manager.addProperty(propertyName, "test property", 1087);
            
        StandardVariable stdVariable = new StandardVariable();
        stdVariable.setName("variable name " + new Random().nextInt(10000));
        stdVariable.setDescription("variable description");
        //stdVariable.setProperty(new Term(2002, "User", "Database user"));
        stdVariable.setProperty(manager.findTermByName(propertyName, CvId.PROPERTIES));
        stdVariable.setMethod(new Term(4030, "Assigned", "Term, name or id assigned"));
        stdVariable.setScale(new Term(6000, "DBCV", "Controlled vocabulary from a database"));
        stdVariable.setStoredIn(new Term(1010, "Study information", "Study element"));
        stdVariable.setDataType(new Term(1120, "Character variable", "variable with char values"));
        stdVariable.setIsA(new Term(1050,"Study condition","Study condition class"));
        stdVariable.setEnumerations(new ArrayList<Enumeration>());
        stdVariable.getEnumerations().add(new Enumeration(10000, "N", "Nursery", 1));
        stdVariable.getEnumerations().add(new Enumeration(10001, "HB", "Hybridization nursery", 2));
        stdVariable.getEnumerations().add(new Enumeration(10002, "PN", "Pedigree nursery", 3));
        stdVariable.setConstraints(new VariableConstraints(100.0, 999.0));
        stdVariable.setCropOntologyId("CROP-TEST");
        
        manager.addStandardVariable(stdVariable);
        
        Integer stdVariableId = manager.getStandardVariableIdByTermId(stdVariable.getProperty().getId(), TermId.HAS_PROPERTY);
        Debug.println(INDENT, "From db:  " + stdVariableId);
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
        
        term = manager.addTraitClass(name, definition, TermId.ONTOLOGY_TRAIT_CLASS.getId()).getTerm();
        manager.deleteTermAndRelationship(term.getId(), CvId.IBDB_TERMS, TermId.IS_A.getId(), TermId.ONTOLOGY_TRAIT_CLASS.getId());
        
        term = manager.getTermById(term.getId());
        assertNull(term);
    }
    
    @Test    
    public void testDeleteOntology() throws Exception {
        String name = "Test Property" + new Random().nextInt(10000);
        String definition = "Property Definition";
        /*
        int isA = 1087;
        Term term = manager.addProperty(name, definition, isA);
        manager.deleteTermAndRelationship(term.getId(), CvId.PROPERTIES, TermId.IS_A.getId(), isA);
        
        term= manager.getTermById(term.getId());
        assertNull(term);
        */
        name = "Parent Test Trait Class " + new Random().nextInt(10000);
        definition = "Parent Test Definition";
        
        Term termParent = manager.addTraitClass(name, definition, TermId.ONTOLOGY_TRAIT_CLASS.getId()).getTerm();
        
        name = "Child Test Trait Class " + new Random().nextInt(10000);
        definition = "Child Test Definition";        
        Term termChild = manager.addTraitClass(name, definition, termParent.getId()).getTerm();
        boolean hasMiddlewareException = false;
        try{
            manager.deleteTermAndRelationship(termParent.getId(), CvId.IBDB_TERMS, TermId.IS_A.getId(), TermId.ONTOLOGY_TRAIT_CLASS.getId());
        }catch (MiddlewareQueryException e) {
            hasMiddlewareException = true;
        }
        assertEquals(true, hasMiddlewareException);
        
        //we do the cleanup here
        
        manager.deleteTermAndRelationship(termChild.getId(), CvId.IBDB_TERMS, TermId.IS_A.getId(), TermId.ONTOLOGY_TRAIT_CLASS.getId());
        manager.deleteTermAndRelationship(termParent.getId(), CvId.IBDB_TERMS, TermId.IS_A.getId(), TermId.ONTOLOGY_TRAIT_CLASS.getId());
        
        Term term = manager.getTermById(termChild.getId());
        assertNull(term);
        term = manager.getTermById(termParent.getId());
        assertNull(term);
    }
    
    @Test
    public void testGetAllPropertiesWithTraitClass() throws Exception {
        List<Property> properties = manager.getAllPropertiesWithTraitClass();
        Debug.printObjects(INDENT, properties);
    }
    
    @Test
    public void testDeleteStandardVariable() throws Exception {
        List<TermProperty> termProperties = new ArrayList<TermProperty>();
        termProperties.add(new TermProperty(1, TermId.CROP_ONTOLOGY_ID.getId(), "CO:12345", 0));
        
        String propertyName = "property name " + new Random().nextInt(10000);
        manager.addProperty(propertyName, "test property", 1087);
        Property property = manager.getProperty(propertyName);
        
        String scaleName = "scale name " + new Random().nextInt(10000);
        Term scale = manager.addTerm(scaleName, "test scale", CvId.SCALES);
        
        String methodName = "method name " + new Random().nextInt(10000);
        Term method = manager.addTerm(methodName, methodName, CvId.METHODS);
        
        Term dataType =new Term(1120, "Character variable", "variable with char values");
        Term storedIn = new Term(1010, "Study information", "Study element");
        Term traitClass = new Term(600, "TRAIT CLASS", "TRAIT CLASS DEF");
        
        StandardVariable standardVariable = new StandardVariable();
        standardVariable.setName("TestVariable" + new Random().nextInt(10000));
        standardVariable.setDescription("Test Desc");
        standardVariable.setProperty(property.getTerm());
        standardVariable.setMethod(method);
        standardVariable.setScale(scale);
        standardVariable.setDataType(dataType);
        standardVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
        standardVariable.setIsA(traitClass);
        standardVariable.setStoredIn(storedIn);
        standardVariable.setCropOntologyId("CO:1200");
        manager.addStandardVariable(standardVariable);        
        Debug.println(INDENT, String.valueOf(standardVariable.getId()));
        manager.deleteStandardVariable(standardVariable.getId());
        Term term = manager.getTermById(standardVariable.getId());
        
        assertNull(term);
    }
}
