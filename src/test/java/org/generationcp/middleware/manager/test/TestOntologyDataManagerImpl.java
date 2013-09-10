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

import java.util.List;

import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestOntologyDataManagerImpl {

	private static final Integer CV_TERM_ID = 1010;
	private static final String CV_TERM_NAME = "Study Information";
	private static final Integer STD_VARIABLE_ID = 8350; // 8310; 
	
	private static ManagerFactory factory;
	private static OntologyDataManager manager;

	@BeforeClass
	public static void setUp() throws Exception {
		DatabaseConnectionParameters local = new DatabaseConnectionParameters("testDatabaseConfig.properties", "local");
		DatabaseConnectionParameters central = new DatabaseConnectionParameters("testDatabaseConfig.properties", "central");
		factory = new ManagerFactory(local, central);
		manager = factory.getNewOntologyDataManager();
	}
/*
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
		assertTrue(stdVar.getFactorType() == stdVar2.getFactorType());
		assertTrue(stdVar.getConstraints() == stdVar2.getConstraints());
		assertTrue(stdVar.getName().equals(stdVar2.getName()));
		assertTrue(stdVar.getDescription().equals(stdVar2.getDescription()));
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
		
		// add a method to local
		String name = "Test Method " + new Random().nextInt(10000);
		String definition = "Test Definition";
		term = manager.addMethod(name, definition);
		// term does exist in local
		
		term = manager.findMethodByName(term.getName());
		assertTrue(term != null);
		term.print(0);
	}
	*/
	
	@Test
	public void testFindStandardVariableByTraitScaleMethodNames() throws Exception{
		System.out.println("Test findStandardVariableByTraitScaleMethodNames");
		StandardVariable stdVar = manager.findStandardVariableByTraitScaleMethodNames("User", "DBCV", "Assigned");		
		System.out.println("testFindStandardVariableByTraitScaleMethodNames(): " + stdVar);
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
}
