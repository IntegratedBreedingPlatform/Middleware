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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.domain.dms.Enumeration;
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
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.utils.test.Debug;
import org.generationcp.middleware.utils.test.OntologyDataManagerImplTestConstants;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class OntologyDataManagerImplTest extends DataManagerIntegrationTest implements
		OntologyDataManagerImplTestConstants {

	private static OntologyDataManager manager;

	@BeforeClass
	public static void setUp() throws Exception {
		manager = DataManagerIntegrationTest.managerFactory
				.getNewOntologyDataManager();
	}

	@Test
	public void testGetCvTermById() throws Exception {
		Term term = manager.getTermById(6040);
		Assert.assertNotNull(term);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetCvTermById(): " + term);
	}

	@Test
	public void testGetStandardVariable() throws Exception {
		StandardVariable stdVar = manager
				.getStandardVariable(OntologyDataManagerImplTestConstants.STD_VARIABLE_ID);
		Assert.assertNotNull(stdVar);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetStandardVariable(): " + stdVar);
	}

	@Test
	public void getStandVariableList() throws MiddlewareQueryException {
		List<Integer> ids = Arrays.asList(new Integer[] { 1, 2, 3, 4, 5 });
		List<StandardVariable> standardVariables = manager
				.getStandardVariables(ids);
		Assert.assertNotNull(standardVariables);
		Assert.assertTrue(standardVariables.size() > 0);
		for (StandardVariable standardVariable : standardVariables) {
			Assert.assertTrue(ids.contains(standardVariable.getId()));
		}
	}

	@Test
	public void testGetStandardVariableSummariesCentral() throws MiddlewareQueryException {
		final int PLANT_HEIGHT_ID = 18020, GRAIN_YIELD_ID = 18000;
		List<Integer> idList = Arrays.asList(PLANT_HEIGHT_ID, GRAIN_YIELD_ID);

		List<StandardVariableSummary> summaries = manager
				.getStandardVariableSummaries(idList);

		Assert.assertNotNull(summaries);
		Assert.assertEquals(idList.size(), summaries.size());
		for (StandardVariableSummary summary : summaries) {
			Assert.assertTrue(idList.contains(summary.getId()));
		}
	}

	@Test
	public void testGetStandardVariableSummaryCentral() throws MiddlewareQueryException {
		// Load summary from the view based method
		StandardVariableSummary summary = manager
				.getStandardVariableSummary(OntologyDataManagerImplTestConstants.PLANT_HEIGHT_ID);
		Assert.assertNotNull(summary);

		// Load details using the ususal method
		StandardVariable details = manager
				.getStandardVariable(OntologyDataManagerImplTestConstants.PLANT_HEIGHT_ID);
		Assert.assertNotNull(details);

		// Make sure that the summary data loaded from view based method matches
		// with detailed data loaded using the usual method.
		this.assertVariableDataMatches(details, summary);
	}

	@Test
	public void testGetStandardVariableSummaryLocal() throws MiddlewareQueryException {
		// First create a local Standardvariable
		StandardVariable myOwnPlantHeight = new StandardVariable();
		myOwnPlantHeight.setName("MyOwnPlantHeight " + new Random().nextInt(1000));
		myOwnPlantHeight.setDescription(myOwnPlantHeight.getName() + " - Description.");
		myOwnPlantHeight.setProperty(new Term(15020, "Plant height", "Plant height"));
		myOwnPlantHeight.setMethod(new Term(16010, "Soil to tip at maturity",
				"Soil to tip at maturity"));

		Term myOwnScale = new Term();
		myOwnScale.setName("MyOwnScale " + new Random().nextInt(1000));
		myOwnScale.setDefinition(myOwnScale.getName() + " - Description.");
		myOwnPlantHeight.setScale(myOwnScale);

		myOwnPlantHeight.setIsA(new Term(OntologyDataManagerImplTestConstants.OBJECT_ID,
				"Agronomic", "Agronomic"));
		myOwnPlantHeight.setDataType(new Term(1110, "Numeric variable",
				"Variable with numeric values either continuous or integer"));
		myOwnPlantHeight.setStoredIn(new Term(1043, "Observation variate",
				"Phenotypic data stored in phenotype.value"));

		manager.addStandardVariable(myOwnPlantHeight);

		// Load details using existing method
		StandardVariable details = manager
				.getStandardVariable(myOwnPlantHeight.getId());
		Assert.assertNotNull(details);

		// Load summary from the view based method
		StandardVariableSummary summary = manager
				.getStandardVariableSummary(myOwnPlantHeight.getId());
		Assert.assertNotNull(summary);

		// Make sure that the summary data loaded from view matches with details
		// data loaded using the usual method.
		this.assertVariableDataMatches(details, summary);

		// Test done. Cleanup the test data created.
		manager.deleteStandardVariable(myOwnPlantHeight.getId());
	}

	private void assertVariableDataMatches(StandardVariable details, StandardVariableSummary summary) {
		Assert.assertEquals(new Integer(details.getId()), summary.getId());
		Assert.assertEquals(details.getName(), summary.getName());
		Assert.assertEquals(details.getDescription(), summary.getDescription());

		this.assertTermDataMatches(details.getProperty(), summary.getProperty());
		this.assertTermDataMatches(details.getMethod(), summary.getMethod());
		this.assertTermDataMatches(details.getScale(), summary.getScale());
		this.assertTermDataMatches(details.getIsA(), summary.getIsA());
		this.assertTermDataMatches(details.getDataType(), summary.getDataType());
		this.assertTermDataMatches(details.getStoredIn(), summary.getStoredIn());

		Assert.assertEquals(details.getPhenotypicType(), summary.getPhenotypicType());
	}

	private void assertTermDataMatches(Term termDetails, TermSummary termSummary) {
		Assert.assertEquals(new Integer(termDetails.getId()), termSummary.getId());
		Assert.assertEquals(termDetails.getName(), termSummary.getName());
		Assert.assertEquals(termDetails.getDefinition(), termSummary.getDefinition());
	}

	@Test
	public void testCopyStandardVariable() throws Exception {
		StandardVariable stdVar = manager
				.getStandardVariable(OntologyDataManagerImplTestConstants.STD_VARIABLE_ID);
		StandardVariable stdVar2 = stdVar.copy();

		Assert.assertNotSame(stdVar.getId(), stdVar2.getId());
		Assert.assertSame(stdVar.getProperty(), stdVar2.getProperty());
		Assert.assertSame(stdVar.getScale(), stdVar2.getScale());
		Assert.assertSame(stdVar.getMethod(), stdVar2.getMethod());
		Assert.assertSame(stdVar.getDataType(), stdVar2.getDataType());
		Assert.assertSame(stdVar.getStoredIn(), stdVar2.getStoredIn());
		Assert.assertSame(stdVar.getPhenotypicType(), stdVar2.getPhenotypicType());
		Assert.assertSame(stdVar.getConstraints(), stdVar2.getConstraints());
		if (stdVar.getName() != null) {
			Assert.assertTrue(stdVar.getName().equals(stdVar2.getName()));
		}
		if (stdVar.getDescription() != null) {
			Assert.assertTrue(stdVar.getDescription().equals(stdVar2.getDescription()));
		}
		Assert.assertSame(stdVar.getEnumerations(), stdVar2.getEnumerations());

		Debug.println(MiddlewareIntegrationTest.INDENT, "testCopyStandardVariable(): \n    "
				+ stdVar + "\n    " + stdVar2);
	}

	@Test
	public void testStandardVariableCache() throws Exception {
		Debug.println(MiddlewareIntegrationTest.INDENT, "testStandardVariableCache(): ");
		// First call to getStandardVariable() will put the value to the cache
		manager
				.getStandardVariable(OntologyDataManagerImplTestConstants.STD_VARIABLE_ID); 
		// Second (and subsequent) calls will retrieve the value from the cache
		manager
				.getStandardVariable(OntologyDataManagerImplTestConstants.STD_VARIABLE_ID); 
	}

	@Test
	public void testNameSynonyms() throws Exception {
		StandardVariable sv = manager.getStandardVariable(8383);
		sv.print(MiddlewareIntegrationTest.INDENT);
	}

	@Test
	public void testAddStandardVariable() throws Exception {
		// create new trait
		String propertyName = "property name " + new Random().nextInt(10000);
		manager.addProperty(propertyName, "test property", 1087);

		StandardVariable stdVariable = new StandardVariable();
		stdVariable.setName("variable name " + new Random().nextInt(10000));
		stdVariable.setDescription("variable description");
		// stdVariable.setProperty(new Term(2002, "User", "Database user"));
		stdVariable.setProperty(manager.findTermByName(propertyName,
				CvId.PROPERTIES));
		stdVariable.setMethod(new Term(4030, "Assigned", "Term, name or id assigned"));
		stdVariable.setScale(new Term(6000, "DBCV", "Controlled vocabulary from a database"));
		stdVariable.setStoredIn(new Term(1010, "Study information", "Study element"));
		stdVariable.setDataType(new Term(1120, "Character variable", "variable with char values"));
		stdVariable.setIsA(new Term(1050, "Study condition", "Study condition class"));
		stdVariable.setEnumerations(new ArrayList<Enumeration>());
		stdVariable.getEnumerations().add(new Enumeration(10000, "N", "Nursery", 1));
		stdVariable.getEnumerations().add(new Enumeration(10001, "HB", "Hybridization nursery", 2));
		stdVariable.getEnumerations().add(new Enumeration(10002, "PN", "Pedigree nursery", 3));
		stdVariable.setConstraints(new VariableConstraints(100.0, 999.0));
		stdVariable.setCropOntologyId("CROP-TEST");

		try {
			manager.addStandardVariable(stdVariable);
		} catch (MiddlewareQueryException e) {
			if (e.getMessage().contains("already exists")) {
				// Ignore. The test run successfully before.
			}
		}

		Debug.println(MiddlewareIntegrationTest.INDENT,
				"Standard variable saved: " + stdVariable.getId());
	}

	@Test
	public void testAddStandardVariableWithMissingScalePropertyMethod() throws Exception {
		StandardVariable stdVariable = new StandardVariable();
		stdVariable.setName("Test SPM" + new Random().nextInt(10000));
		stdVariable.setDescription("Std variable with new scale, property, method");

		Term newProperty = new Term(2451, "Environment", "Environment");
		Term property = manager.findTermByName(newProperty.getName(),
				CvId.PROPERTIES);
		if (property == null) {
			Debug.println(MiddlewareIntegrationTest.INDENT,
					"new property = " + newProperty.getName());
			property = newProperty;
		} else {
			Debug.println(MiddlewareIntegrationTest.INDENT, "property id = " + property.getId());
		}
		Term newScale = new Term(6020, "Text", "Text");
		Term scale = manager.findTermByName(newScale.getName(),
				CvId.SCALES);
		if (scale == null) {
			Debug.println(MiddlewareIntegrationTest.INDENT, "new scale = " + newScale.getName());
			scale = newScale;
		} else {
			Debug.println(MiddlewareIntegrationTest.INDENT, "scale id = " + scale.getId());
		}
		Term newMethod = new Term(0, "Test Method", "Test Method");
		Term method = manager.findTermByName(newMethod.getName(),
				CvId.METHODS);
		if (method == null) {
			Debug.println(MiddlewareIntegrationTest.INDENT, "new method = " + newMethod.getName());
			method = newMethod;
		} else {
			Debug.println(MiddlewareIntegrationTest.INDENT, "method id = " + method.getId());
		}
		stdVariable.setProperty(property);
		stdVariable.setScale(scale);
		stdVariable.setMethod(method);

		stdVariable.setStoredIn(new Term(1010, "Study information", "Study element"));
		// added as this is required
		stdVariable.setIsA(new Term(1050, "Study condition", "Study condition class"));
		stdVariable.setDataType(new Term(1120, "Character variable", "variable with char values"));
		stdVariable.setEnumerations(new ArrayList<Enumeration>());
		stdVariable.getEnumerations().add(new Enumeration(10000, "N", "Nursery", 1));
		stdVariable.getEnumerations().add(new Enumeration(10001, "HB", "Hybridization nursery", 2));
		stdVariable.getEnumerations().add(new Enumeration(10002, "PN", "Pedigree nursery", 3));
		stdVariable.setConstraints(new VariableConstraints(100.0, 999.0));

		manager.addStandardVariable(stdVariable);

		Debug.println(MiddlewareIntegrationTest.INDENT,
				"Standard variable saved: " + stdVariable.getId());
	}

	@Test
	public void testAddStandardVariableEnumeration() throws Exception {
		int standardVariableId = OntologyDataManagerImplTestConstants.CATEGORICAL_VARIABLE_TERM_ID;
		String name = "Name_" + new Random().nextInt(10000);
		String description = "Test Valid Value" + new Random().nextInt(10000);
		StandardVariable standardVariable = manager
				.getStandardVariable(standardVariableId);
		Enumeration validValue = new Enumeration(null, name, description, 1);

		Debug.printObject(MiddlewareIntegrationTest.INDENT, standardVariable);
		manager.saveOrUpdateStandardVariableEnumeration(
				standardVariable, validValue);
		Debug.printObject(MiddlewareIntegrationTest.INDENT, validValue);
		standardVariable = manager
				.getStandardVariable(standardVariableId);
		Debug.printObject(MiddlewareIntegrationTest.INDENT, standardVariable);
		Assert.assertNotNull(standardVariable.getEnumeration(validValue.getId()));

		// TO VERIFY IN MYSQL, delete the lines marked (*) below, then check in
		// local:
		// select * from cvterm where name = "8" and definition =
		// "Fully exserted";
		// select * from cvterm_relationship where subject_id = 22554;

		// (*) clean up
		manager.deleteStandardVariableEnumeration(standardVariableId,
				validValue.getId());
	}

	@Test
	public void testUpdateStandardVariableEnumeration() throws Exception {
		// Case 1: NEW VALID VALUE
		int standardVariableId = OntologyDataManagerImplTestConstants.CATEGORICAL_VARIABLE_TERM_ID;
		String name = "Name_" + new Random().nextInt(10000);
		String description = "Test Valid Value" + new Random().nextInt(10000);
		StandardVariable standardVariable = manager
				.getStandardVariable(standardVariableId);
		Enumeration validValue = new Enumeration(null, name, description, standardVariable
				.getEnumerations().size() + 1);

		Debug.printObject(MiddlewareIntegrationTest.INDENT, standardVariable);
		manager.saveOrUpdateStandardVariableEnumeration(
				standardVariable, validValue);
		standardVariable = manager
				.getStandardVariable(standardVariableId);
		Integer validValueGeneratedId1 = standardVariable.getEnumerationByName(name).getId();
		Debug.printObject(MiddlewareIntegrationTest.INDENT, validValue);
		standardVariable = manager
				.getStandardVariable(standardVariableId);
		Debug.printObject(MiddlewareIntegrationTest.INDENT, standardVariable);
		Assert.assertNotNull(standardVariable.getEnumeration(validValue.getId()));

		// TO VERIFY IN MYSQL, delete the lines marked (*) below, then check in
		// local:
		// select * from cvterm where name = "8" and definition =
		// "Fully exserted";
		// select * from cvterm_relationship where subject_id = 22554;

		// Case 2: UPDATE CENTRAL VALID VALUE

		Integer validValueId = OntologyDataManagerImplTestConstants.CROP_SESCND_VALID_VALUE_FROM_CENTRAL;
		name = "Name_" + new Random().nextInt(10000);
		description = "Test Valid Value " + new Random().nextInt(10000); // Original
																			// value
																			// in
																			// central:
																			// "Moderately well exserted"
		validValue = new Enumeration(validValueId, name, description, 1);
		manager.saveOrUpdateStandardVariableEnumeration(
				standardVariable, validValue);

		Debug.printObject(MiddlewareIntegrationTest.INDENT, validValue);
		standardVariable = manager
				.getStandardVariable(standardVariableId);
		Debug.printObject(MiddlewareIntegrationTest.INDENT, standardVariable);
		Assert.assertNotNull(standardVariable.getEnumeration(validValue.getId()));

		// Case 3: UPDATE LOCAL VALID VALUE
		description = "Test Valid Value " + new Random().nextInt(10000);
		validValue.setDescription(description);
		manager.saveOrUpdateStandardVariableEnumeration(
				standardVariable, validValue);

		Debug.printObject(MiddlewareIntegrationTest.INDENT, validValue);
		standardVariable = manager
				.getStandardVariable(standardVariableId);
		Debug.printObject(MiddlewareIntegrationTest.INDENT, standardVariable);
		Assert.assertTrue(standardVariable.getEnumeration(validValue.getId()).getDescription()
				.equals(description));

		// (*) clean up
		manager.deleteStandardVariableEnumeration(standardVariableId,
				validValueGeneratedId1);
		manager.deleteStandardVariableEnumeration(standardVariableId,
				validValue.getId());

	}

	@Test
	public void testAddMethod() throws Exception {
		String name = "Test Method " + new Random().nextInt(10000);
		String definition = "Test Definition";
		Term term = manager.addMethod(name, definition);
		Assert.assertTrue(term.getId() < 0);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testAddMethod():  " + term);
		term = manager.getTermById(term.getId());
		Debug.println(MiddlewareIntegrationTest.INDENT, "From db:  " + term);
	}

	/*
	 * @Test public void testGetStandardVariableIdByPropertyScaleMethod() throws
	 * Exception { Integer propertyId = Integer.valueOf(2010); Integer scaleId =
	 * Integer.valueOf(6000); Integer methodId = Integer.valueOf(4030);
	 * 
	 * Integer varid =
	 * manager.getStandardVariableIdByPropertyScaleMethod(propertyId, scaleId,
	 * methodId); assertNotNull (varid); Debug.println(INDENT,
	 * "testGetStandadardVariableIdByPropertyScaleMethod() Results: " + varid);
	 * }
	 */

	/**
	 * This tests an expected property of the ontology data manager to return a
	 * non empty map, even for entries that cannot be matched to a standard
	 * variable in the database
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetStandardVariablesInProjectsNonNullEvenIfNotPresent() throws Exception {
		String baseVariableName = "test";
		Random random = new Random();
		int testItemCount = random.nextInt(10);

		List<String> headers = new ArrayList<String>(testItemCount);

		for (int i = 0; i < testItemCount; i++) {
			headers.add(baseVariableName + i);
		}

		Map<String, List<StandardVariable>> results = manager
				.getStandardVariablesInProjects(headers);

		Assert.assertNotNull(
				"Application is unable to return non empty output even on non present variables",
				results);
		Assert.assertTrue(
				"Application is unable to return non empty output even on non present variables",
				results.size() == testItemCount);

		for (String header : headers) {
			Assert.assertTrue(
					"Application is unable to return non empty output for each of the passed header parameters",
					results.containsKey(header));
		}

		for (Map.Entry<String, List<StandardVariable>> entry : results.entrySet()) {
			List<StandardVariable> vars = entry.getValue();

			Assert.assertNotNull(
					"Application should give a non null list of standard variables for a given header name, even if not present",
					vars);
			Assert.assertTrue("Application shouldn't be able to give values for dummy input",
					vars.size() == 0);
		}
	}

	/**
	 * This tests the ability of the application to retrieve the standard
	 * variables associated with known central headers
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetStandardVariablesInProjectsKnownCentralHeaders() throws Exception {
		List<String> headers = Arrays
				.asList(OntologyDataManagerImplTestConstants.CENTRAL_COMMON_HEADERS);

		Map<String, List<StandardVariable>> results = manager
				.getStandardVariablesInProjects(headers);

		for (Map.Entry<String, List<StandardVariable>> entry : results.entrySet()) {
			List<StandardVariable> variableList = entry.getValue();

			Assert.assertTrue(
					"Application is unable to return a list of standard variables for known existing central headers ",
					variableList.size() > 0);
		}

	}

	/**
	 * This tests the ability of the application to retrieve standard variables
	 * for newly created items
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetStandardVariablesForNewlyCreatedEntries() throws Exception {
		// set up and create a new standard variable for this test
		StandardVariable dummyVariable = this.constructDummyStandardVariable();
		manager.addStandardVariable(dummyVariable);

		List<String> headers = Arrays.asList(dummyVariable.getName());

		Map<String, List<StandardVariable>> results = manager
				.getStandardVariablesInProjects(headers);

		try {

			for (Map.Entry<String, List<StandardVariable>> entry : results.entrySet()) {
				List<StandardVariable> variableList = entry.getValue();

				Assert.assertTrue(
						"Application is unable to return a proper sized list of standard variables for newly added standard variables",
						variableList.size() == 1);

				StandardVariable retrieved = variableList.get(0);
				Assert.assertEquals(
						"Application is unable to retrieve the proper standard variable for newly created entries",
						dummyVariable.getName(), retrieved.getName());
			}
		} finally {
			// make test clean by deleting the dummy variable created during
			// start of test
			manager.deleteStandardVariable(dummyVariable.getId());
		}

	}

	protected StandardVariable constructDummyStandardVariable() throws Exception {

		StandardVariable standardVariable = new StandardVariable();

		standardVariable.setName("TestVariable" + new Random().nextLong());
		standardVariable.setDescription("For unit testing purposes");

		Term propertyTerm = manager.findTermByName("Yield",
				CvId.PROPERTIES);
		standardVariable.setProperty(propertyTerm);

		Term scaleTerm = manager.findTermByName("g", CvId.SCALES);
		standardVariable.setScale(scaleTerm);

		Term methodTerm = manager.findTermByName("Counting",
				CvId.METHODS);
		standardVariable.setMethod(methodTerm);

		Term storedInTerm = manager
				.getTermById(TermId.OBSERVATION_VARIATE.getId());
		standardVariable.setStoredIn(storedInTerm);

		Term dataType = manager.getTermById(TermId.NUMERIC_VARIABLE
				.getId());
		standardVariable.setDataType(dataType);

		standardVariable.setPhenotypicType(PhenotypicType.VARIATE);

		return standardVariable;
	}

	@Test
	public void testFindStandardVariablesByNameOrSynonym() throws Exception {
		Debug.println(MiddlewareIntegrationTest.INDENT, "Test FindStandardVariablesByNameOrSynonym");
		Set<StandardVariable> standardVariables = manager
				.findStandardVariablesByNameOrSynonym(OntologyDataManagerImplTestConstants.TERM_NAME_NOT_EXISTING);
		Assert.assertSame(standardVariables.size(), 0);

		standardVariables = manager
				.findStandardVariablesByNameOrSynonym(OntologyDataManagerImplTestConstants.TERM_NAME_IS_IN_SYNONYMS);
		Assert.assertSame(standardVariables.size(), 1);
		for (StandardVariable stdVar : standardVariables) {
			stdVar.print(MiddlewareIntegrationTest.INDENT);
		}

		standardVariables = manager
				.findStandardVariablesByNameOrSynonym(OntologyDataManagerImplTestConstants.TERM_SYNONYM);
		Assert.assertSame(standardVariables.size(), 1);
		for (StandardVariable stdVar : standardVariables) {
			stdVar.print(MiddlewareIntegrationTest.INDENT);
		}
	}

	@Test
	public void testFindStandardVariablesByNameOrSynonymWithProperties() throws Exception {
		Debug.println(MiddlewareIntegrationTest.INDENT, "Test getTraitDetailsByTAbbr");
		Set<StandardVariable> standardVariables = manager
				.findStandardVariablesByNameOrSynonym(OntologyDataManagerImplTestConstants.TERM_NAME_IS_IN_SYNONYMS);
		Assert.assertSame(standardVariables.size(), 1);
		for (StandardVariable stdVar : standardVariables) {
			stdVar.print(MiddlewareIntegrationTest.INDENT);
			Term term = manager.getTermById(stdVar.getId());
			term.print(MiddlewareIntegrationTest.INDENT);
		}
	}

	@Test
	public void testFindMethodById() throws Exception {

		// term doesn't exist
		Term term = manager
				.findMethodById(OntologyDataManagerImplTestConstants.TERM_ID_NOT_EXISTING);
		Assert.assertNull(term);

		// term exist but isn't a method
		term = manager
				.findMethodById(OntologyDataManagerImplTestConstants.TERM_ID_NOT_METHOD);
		Assert.assertNull(term);

		// term does exist in central
		term = manager
				.findMethodById(OntologyDataManagerImplTestConstants.TERM_ID_IN_CENTRAL);
		Assert.assertNotNull(term);
		term.print(MiddlewareIntegrationTest.INDENT);
		Debug.println(MiddlewareIntegrationTest.INDENT, "");

		// add a method to local
		String name = "Test Method " + new Random().nextInt(10000);
		String definition = "Test Definition";
		term = manager.addMethod(name, definition);
		// term does exist in local

		term = manager.findMethodById(term.getId());
		Assert.assertNotNull(term);
		term.print(MiddlewareIntegrationTest.INDENT);
	}

	@Test
	public void testFindMethodByName() throws Exception {
		Debug.println(MiddlewareIntegrationTest.INDENT, "Test findMethodByName");

		// term doesn't exist
		Term term = manager
				.findMethodByName(OntologyDataManagerImplTestConstants.TERM_NAME_NOT_EXISTING);
		Assert.assertNull(term);

		// term exist but isn't a method
		term = manager
				.findMethodByName(OntologyDataManagerImplTestConstants.TERM_NAME_NOT_METHOD);
		Assert.assertNull(term);

		// term does exist in central
		term = manager
				.findMethodByName(OntologyDataManagerImplTestConstants.TERM_NAME_IN_CENTRAL);
		Assert.assertNotNull(term);
		term.print(MiddlewareIntegrationTest.INDENT);
		Debug.println(MiddlewareIntegrationTest.INDENT, "");
	}

	@Test
	public void testFindStandardVariableByTraitScaleMethodNames() throws Exception {
		StandardVariable stdVar = manager
				.findStandardVariableByTraitScaleMethodNames("Cooperator", "DBCV", "Assigned");
		Debug.println(MiddlewareIntegrationTest.INDENT,
				"testFindStandardVariableByTraitScaleMethodNames(): " + stdVar);
	}

	@Test
	public void testGetAllTermsByCvId() throws Exception {
		List<Term> terms = manager.getAllTermsByCvId(CvId.METHODS);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetAllTermsByCvId - Get Methods: "
				+ terms.size());
		this.printTerms(terms);
		terms = manager.getAllTermsByCvId(CvId.PROPERTIES);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetAllTermsByCvId - Get Properties: "
				+ terms.size());
		this.printTerms(terms);
		terms = manager.getAllTermsByCvId(CvId.SCALES);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetAllTermsByCvId - Get Scales: "
				+ terms.size());
		this.printTerms(terms);
	}

	@Test
	public void testGetAllTermsByCvIdWithStartAndNumOfRows() throws Exception {
		List<Term> terms1 = manager.getAllTermsByCvId(CvId.METHODS, 0,
				2);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Get First 2 Methods: " + terms1.size());
		this.printTerms(terms1);

		List<Term> terms2 = manager.getAllTermsByCvId(CvId.METHODS, 2,
				2);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Get Next 2 Methods: " + terms2.size());
		this.printTerms(terms2);

		terms1.addAll(terms2);

		List<Term> terms = manager
				.getAllTermsByCvId(CvId.METHODS, 0, 4);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Get First 4 Methods: " + terms.size());
		this.printTerms(terms);

		Assert.assertEquals(terms1, terms);

		List<Term> allTerms = manager.getAllTermsByCvId(CvId.METHODS);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Get All Methods: " + allTerms.size());

		List<Term> allTerms2 = manager.getAllTermsByCvId(CvId.METHODS,
				0, allTerms.size());
		Debug.println(MiddlewareIntegrationTest.INDENT,
				"Get All Methods with start and numOfRows: " + allTerms2.size());
		this.printTerms(allTerms2);

		Assert.assertEquals(allTerms, allTerms2);

	}

	private void printTerms(List<Term> terms) {
		for (Term term : terms) {
			term.print(MiddlewareIntegrationTest.INDENT);
			Debug.println(MiddlewareIntegrationTest.INDENT, "    ----------");
		}
	}

	@Test
	public void testCountTermsByCvId() throws Exception {
		long count = manager.countTermsByCvId(CvId.METHODS);
		Debug.println(MiddlewareIntegrationTest.INDENT,
				"testCountTermsByCvId() - Count All Methods: " + count);
		count = manager.countTermsByCvId(CvId.PROPERTIES);
		Debug.println(MiddlewareIntegrationTest.INDENT,
				"testCountTermsByCvId() - Count All Properties: " + count);
		count = manager.countTermsByCvId(CvId.SCALES);
		Debug.println(MiddlewareIntegrationTest.INDENT,
				"testCountTermsByCvId() - Count All Scales: " + count);
	}

	@Test
	public void testGetMethodsForTrait() throws Exception {
		StandardVariable stdVar = manager
				.findStandardVariableByTraitScaleMethodNames("User", "DBCV", "Assigned");
		List<Term> terms = manager.getMethodsForTrait(stdVar
				.getProperty().getId());
		Debug.println(MiddlewareIntegrationTest.INDENT, "Size: " + terms.size());
		Assert.assertNotNull(terms);
		boolean hasAssigned = false;
		for (Term term : terms) {
			if (term.getName().equals("Assigned")) {
				hasAssigned = true;
			}
			Debug.println(MiddlewareIntegrationTest.INDENT, "method: " + term.getName());
		}
		Assert.assertTrue(hasAssigned);// should return Assigned

		// 2nd test
		stdVar = manager.findStandardVariableByTraitScaleMethodNames(
				"Germplasm entry", "Number", "Enumerated");
		terms = manager
				.getMethodsForTrait(stdVar.getProperty().getId());
		Debug.println(MiddlewareIntegrationTest.INDENT, "Size: " + terms.size());
		Assert.assertNotNull(terms);
		boolean hasEnumerated = false;
		for (Term term : terms) {
			if (term.getName().equals("Enumerated")) {
				hasEnumerated = true;
			}
			Debug.println(MiddlewareIntegrationTest.INDENT, "method: " + term.getName());
		}
		Assert.assertTrue(hasEnumerated);// should return Enumerated
	}

	@Test
	public void testGetScalesForTrait() throws Exception {
		StandardVariable stdVar = manager
				.findStandardVariableByTraitScaleMethodNames("User", "DBCV", "Assigned");
		List<Term> terms = manager.getScalesForTrait(stdVar
				.getProperty().getId());
		Debug.println(MiddlewareIntegrationTest.INDENT, "Size: " + terms.size());
		Assert.assertNotNull(terms);
		boolean hasDBCV = false;
		for (Term term : terms) {
			if (term.getName().equals("DBCV")) {
				hasDBCV = true;
			}
			Debug.println(MiddlewareIntegrationTest.INDENT, "scale: " + term.getName());
		}
		Assert.assertTrue(hasDBCV);// should return DBCV

		// 2nd test
		stdVar = manager.findStandardVariableByTraitScaleMethodNames(
				"Germplasm entry", "Number", "Enumerated");
		terms = manager.getScalesForTrait(stdVar.getProperty().getId());
		Debug.println(MiddlewareIntegrationTest.INDENT, "Size: " + terms.size());
		Assert.assertNotNull(terms);
		boolean hasNumber = false;
		for (Term term : terms) {
			if (term.getName().equals("Number")) {
				hasNumber = true;
			}
			Debug.println(MiddlewareIntegrationTest.INDENT, "scale: " + term.getName());
		}
		Assert.assertTrue(hasNumber);// should return Number
	}

	@Test
	public void testAddTerm() throws Exception {
		String name = "Test Method " + new Random().nextInt(10000);
		String definition = "Test Definition";

		// add a method, should allow insert

		CvId cvId = CvId.METHODS;
		Term term = manager.addTerm(name, definition, cvId);
		Assert.assertNotNull(term);
		Assert.assertTrue(term.getId() < 0);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testAddTerm():  " + term);
		term = manager.getTermById(term.getId());
		Debug.println(MiddlewareIntegrationTest.INDENT, "From db:  " + term);

		// add a variable, should not allow insert and should throw an exception
		// uncomment the ff. to test adding variables
		/*
		 * name = "Test Variable " + new Random().nextInt(10000); definition =
		 * "Test Variable"; cvId = CvId.VARIABLES; term = manager.addTerm(name,
		 * definition, cvId); assertTrue(term == null);
		 */
	}

	@Test
	public void testFindTermByName() throws Exception {
		// term doesn't exist
		Term term = manager.findTermByName(
				OntologyDataManagerImplTestConstants.TERM_NAME_NOT_EXISTING, CvId.METHODS);
		Assert.assertNull(term);

		// term exist but isn't a method
		term = manager.findTermByName(
				OntologyDataManagerImplTestConstants.TERM_NAME_NOT_METHOD, CvId.METHODS);
		Assert.assertNull(term);

		// term does exist in central
		term = manager.findTermByName(
				OntologyDataManagerImplTestConstants.TERM_NAME_IN_CENTRAL, CvId.METHODS);
		Assert.assertNotNull(term);
		term.print(MiddlewareIntegrationTest.INDENT);
		Debug.println(MiddlewareIntegrationTest.INDENT, "");

	}

	@Test
	public void testGetDataTypes() throws Exception {
		List<Term> terms = manager.getDataTypes();
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetDataTypes: " + terms.size());
		this.printTerms(terms);
	}

	@Test
	public void testGetStandardVariablesForPhenotypicType() throws Exception {
		PhenotypicType phenotypicType = PhenotypicType.TRIAL_ENVIRONMENT;
		Integer start = 0;
		Integer numOfRows = 100;

		Map<String, StandardVariable> standardVariables = manager
				.getStandardVariablesForPhenotypicType(phenotypicType, start, numOfRows);

		for (Object key : standardVariables.keySet()) {
			Debug.println(key + " : " + standardVariables.get(key).getId() + " : "
					+ standardVariables.get(key).toString());
		}

		Debug.println(MiddlewareIntegrationTest.INDENT, "count: " + standardVariables.size());
	}

	@Test
	public void testGetStandardVariablesInProjects() throws Exception {
		List<String> headers = Arrays.asList("ENTRY", "ENTRYNO", "PLOT", "TRIAL_NO", "TRIAL",
				"STUDY", "DATASET", "LOC", "LOCN", "NURSER", "Plot Number");

		Map<String, List<StandardVariable>> results = manager
				.getStandardVariablesInProjects(headers);

		Debug.println(MiddlewareIntegrationTest.INDENT,
				"testGetStandardVariablesInProjects(headers=" + headers + ") RESULTS:");
		for (String name : headers) {
			Debug.println(MiddlewareIntegrationTest.INDENT, "Header = " + name
					+ ", StandardVariables: ");
			if (results.get(name).size() > 0) {
				for (StandardVariable var : results.get(name)) {
					Debug.println(MiddlewareIntegrationTest.INDENT, var.getId() + ", ");
				}
				Debug.println(MiddlewareIntegrationTest.INDENT, "");
			} else {
				Debug.println(MiddlewareIntegrationTest.INDENT, "    No standard variables found.");
			}
		}
	}

	@Test
	public void testFindTermsByNameOrSynonym() throws Exception {
		// term doesn't exist
		List<Term> terms = manager.findTermsByNameOrSynonym(
				OntologyDataManagerImplTestConstants.TERM_NAME_NOT_EXISTING, CvId.METHODS);
		Assert.assertSame(terms.size(), 0);

		// term exist but isn't a method
		terms = manager.findTermsByNameOrSynonym(
				OntologyDataManagerImplTestConstants.TERM_NAME_NOT_METHOD, CvId.METHODS);
		Assert.assertSame(terms.size(), 0);

		// term does exist in central
		terms = manager.findTermsByNameOrSynonym(
				OntologyDataManagerImplTestConstants.TERM_NAME_IN_CENTRAL, CvId.METHODS);
		Assert.assertNotNull(terms);
		Assert.assertTrue(!terms.isEmpty());

		terms.get(0).print(MiddlewareIntegrationTest.INDENT);
		Debug.println(MiddlewareIntegrationTest.INDENT, "");

		// name is in synonyms
		terms = manager.findTermsByNameOrSynonym(
				OntologyDataManagerImplTestConstants.TERM_NAME_IS_IN_SYNONYMS, CvId.VARIABLES);
		Assert.assertNotNull(terms);
		terms.get(0).print(MiddlewareIntegrationTest.INDENT);
		Debug.println(MiddlewareIntegrationTest.INDENT, "");

		// name is both in term and in synonyms
		// need to modify the entry in cvterm where name = "Cooperator" to have
		// cv_id = 1010
		terms = manager.findTermsByNameOrSynonym("Cooperator",
				CvId.PROPERTIES);
		Assert.assertNotNull(terms);
		for (Term term : terms) {
			term.print(MiddlewareIntegrationTest.INDENT);
			Debug.println(MiddlewareIntegrationTest.INDENT, "");
		}

	}

	@Test
	public void testGetIsAOfProperties() throws Exception {
		List<Term> terms1 = manager.getIsAOfProperties(0, 2);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Get First 2 isA: " + terms1.size());
		this.printTerms(terms1);

		List<Term> terms2 = manager.getIsAOfProperties(2, 2);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Get Next 2 isA: " + terms2.size());
		this.printTerms(terms2);

		terms1.addAll(terms2);

		List<Term> terms = manager.getIsAOfProperties(0, 4);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Get First 4 isA: " + terms.size());
		this.printTerms(terms);

		Assert.assertEquals(terms1, terms);

		List<Term> allTerms = manager.getIsAOfProperties(0, 0);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Get All isA: " + allTerms.size());
		this.printTerms(allTerms);

	}

	@Test
	public void testCountIsAOfProperties() throws Exception {
		long asOf = manager.countIsAOfProperties();
		Debug.println(MiddlewareIntegrationTest.INDENT, "count is a properties " + asOf);
	}

	@Test
	public void testAddProperty() throws Exception {
		String name = "Germplasm type 3";
		String definition = "Germplasm type description 3";
		int isA = 1087;

		Debug.println(MiddlewareIntegrationTest.INDENT, "testAddProperty(name=" + name
				+ ", definition=" + definition + ", isA=" + isA + "): ");
		Term term = manager.addProperty(name, definition, isA);
		term.print(MiddlewareIntegrationTest.INDENT);

	}

	@Test
	public void testGetProperty() throws Exception {
		int termId = 2452;

		Property property = manager.getProperty(termId);

		Debug.println(MiddlewareIntegrationTest.INDENT, property.toString());
	}

	@Test
	public void testGetAllTraitGroupsHierarchy() throws Exception {
		List<TraitClassReference> traitGroups = manager
				.getAllTraitGroupsHierarchy(true);
		for (TraitClassReference traitGroup : traitGroups) {
			traitGroup.print(MiddlewareIntegrationTest.INDENT);
		}
	}

	@Test
	public void testGetPropertyByName() throws Exception {
		String name = "Season";
		Property property = manager.getProperty(name);
		Debug.println(MiddlewareIntegrationTest.INDENT, property.toString());
	}

	@Test
	public void testGetAllStandardVariable() throws Exception {
		Set<StandardVariable> standardVariables = manager
				.getAllStandardVariables();
		for (StandardVariable stdVar : standardVariables) {
			stdVar.print(MiddlewareIntegrationTest.INDENT);
		}

		Debug.println(MiddlewareIntegrationTest.INDENT, "count: " + standardVariables.size());
	}

	@Test
	public void testGetStandardVariablesByTraitClass() throws MiddlewareQueryException {
		List<StandardVariable> vars = manager.getStandardVariables(
				OntologyDataManagerImplTestConstants.NONEXISTING_TERM_TRAIT_CLASS_ID, null, null,
				null);
		Assert.assertTrue(vars.isEmpty());

		vars = manager
				.getStandardVariables(
						OntologyDataManagerImplTestConstants.EXPECTED_TERM_TRAIT_CLASS_ID, null,
						null, null);
		Assert.assertFalse(vars.isEmpty());

		for (StandardVariable var : vars) {
			Debug.println(MiddlewareIntegrationTest.INDENT, var.toString());
		}
	}

	@Test
	public void testGetStandardVariablesByProperty() throws MiddlewareQueryException {
		List<StandardVariable> vars = manager
				.getStandardVariables(null,
						OntologyDataManagerImplTestConstants.NONEXISTING_TERM_PROPERTY_ID, null,
						null);

		Assert.assertTrue(vars.isEmpty());

		vars = manager.getStandardVariables(null,
				OntologyDataManagerImplTestConstants.EXPECTED_TERM_PROPERTY_ID, null, null);
		Assert.assertFalse(vars.isEmpty());

		for (StandardVariable var : vars) {
			Debug.println(MiddlewareIntegrationTest.INDENT, var.toString());
		}
	}

	@Test
	public void testGetStandardVariablesByMethod() throws MiddlewareQueryException {
		List<StandardVariable> vars = manager.getStandardVariables(
				null, null, OntologyDataManagerImplTestConstants.NONEXISTING_TERM_METHOD_ID, null);
		Assert.assertTrue(vars.isEmpty());

		vars = manager.getStandardVariables(null, null,
				OntologyDataManagerImplTestConstants.EXPECTED_TERM_METHOD_ID, null);
		Assert.assertFalse(vars.isEmpty());

		for (StandardVariable var : vars) {
			Debug.println(MiddlewareIntegrationTest.INDENT, var.toString());
		}
	}

	@Test
	public void testGetStandardVariablesByScale() throws MiddlewareQueryException {
		List<StandardVariable> vars = manager.getStandardVariables(
				null, null, null, OntologyDataManagerImplTestConstants.NONEXISTING_TERM_SCALE_ID);
		Assert.assertTrue(vars.isEmpty());

		vars = manager.getStandardVariables(null, null, null,
				OntologyDataManagerImplTestConstants.EXPECTED_TERM_SCALE_ID);
		Assert.assertFalse(vars.isEmpty());

		for (StandardVariable var : vars) {
			Debug.println(MiddlewareIntegrationTest.INDENT, var.toString());
		}
	}

	@Test
	public void testAddOrUpdateTermAndRelationshipFoundInCentral() throws Exception {
		String name = "Season";
		String definition = "Growing Season " + (int) (Math.random() * 100); // add
																				// random
																				// number
																				// to
																				// see
																				// the
																				// update
		try {
			manager.addOrUpdateTermAndRelationship(name, definition,
					CvId.PROPERTIES, TermId.IS_A.getId(),
					OntologyDataManagerImplTestConstants.OBJECT_ID, null);
		} catch (MiddlewareException e) {
			Debug.println(MiddlewareIntegrationTest.INDENT,
					"MiddlewareException expected: \"" + e.getMessage() + "\"");
			Assert.assertTrue(e.getMessage().contains(
					" is retrieved from the central database and cannot be updated"));
		}
	}

	@Test
	public void testAddOrUpdateTermAndRelationshipNotInCentral() throws Exception {
		String name = "Study condition NEW";
		String definition = "Study condition NEW class " + (int) (Math.random() * 100); // add
																						// random
																						// number
																						// to
																						// see
																						// the
																						// update
		Term origTerm = manager.findTermByName(name, CvId.PROPERTIES);
		Term newTerm = manager.addOrUpdateTermAndRelationship(name,
				definition, CvId.PROPERTIES, TermId.IS_A.getId(),
				OntologyDataManagerImplTestConstants.OBJECT_ID, null);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Original:  " + origTerm);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Updated :  " + newTerm);

		if (origTerm != null) { // if the operation is update, the ids must be
								// same
			Assert.assertSame(origTerm.getId(), newTerm.getId());
		}
	}

	@Test
	public void testUpdateTermAndRelationshipFoundInCentral() throws Exception {
		String name = "Slope";
		String definition = "Land slope " + (int) (Math.random() * 100); // add
																			// random
																			// number
																			// to
																			// see
																			// the
																			// update
		Term origTerm = manager.findTermByName(name, CvId.PROPERTIES);
		try {
			manager.updateTermAndRelationship(new Term(
					origTerm.getId(), name, definition), TermId.IS_A.getId(),
					OntologyDataManagerImplTestConstants.OBJECT_ID);
		} catch (MiddlewareException e) {
			Debug.println(MiddlewareIntegrationTest.INDENT,
					"MiddlewareException expected: \"" + e.getMessage() + "\"");
			Assert.assertTrue(e.getMessage().contains("Cannot update terms in central"));
		}
	}

	@Test
	public void testUpdateTermAndRelationshipNotInCentral() throws Exception {
		String name = "Slope NEW";
		String definition = "Slope NEW class " + (int) (Math.random() * 100); // add
																				// random
																				// number
																				// to
																				// see
																				// the
																				// update

		Term origTerm = manager.findTermByName(name, CvId.PROPERTIES);
		if (origTerm == null) { // first run, add before update
			origTerm = manager.addOrUpdateTermAndRelationship(name,
					definition, CvId.PROPERTIES, TermId.IS_A.getId(),
					OntologyDataManagerImplTestConstants.OBJECT_ID, null);
		}

		manager.updateTermAndRelationship(new Term(origTerm.getId(),
				name, definition), TermId.IS_A.getId(),
				OntologyDataManagerImplTestConstants.OBJECT_ID);
		Term newTerm = manager.findTermByName(name, CvId.PROPERTIES);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Original:  " + origTerm);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Updated :  " + newTerm);

		if (origTerm != null && newTerm != null) {
			Assert.assertTrue(newTerm.getDefinition().equals(definition));
		}
	}

	@Test
	public void testAddOrUpdateTermFoundInCentral() throws Exception {
		String name = "Score";
		// add random number to see the update
		String definition = "Score NEW " + (int) (Math.random() * 100);
		try {
			manager.addOrUpdateTerm(name, definition, CvId.SCALES);
		} catch (MiddlewareQueryException e) {
			Debug.println(MiddlewareIntegrationTest.INDENT,
					"MiddlewareException expected: \"" + e.getMessage() + "\"");
			Assert.assertTrue(e.getMessage().contains("The term you entered is invalid"));
		}
	}

	@Test
	public void testAddOrUpdateTermNotInCentral() throws Exception {
		String name = "Real";
		// add random number to see the update
		String definition = "Real Description NEW " + (int) (Math.random() * 100); 
		Term origTerm = manager.findTermByName(name, CvId.SCALES);
		Term newTerm = manager.addOrUpdateTerm(name, definition,
				CvId.SCALES);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Original:  " + origTerm);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Updated :  " + newTerm);

		if (origTerm != null) { // if the operation is update, the ids must be
								// same
			Assert.assertSame(origTerm.getId(), newTerm.getId());
		}
	}

	@Test
	public void testUpdateTermFoundInCentral() throws Exception {
		String name = "Score";
		// add random number to see the update
		String definition = "Score NEW " + (int) (Math.random() * 100); 
		Term origTerm = manager.findTermByName(name, CvId.SCALES);
		try {
			manager.updateTerm(new Term(origTerm.getId(), name,
					definition));
		} catch (MiddlewareException e) {
			Debug.println(MiddlewareIntegrationTest.INDENT,
					"MiddlewareException expected: \"" + e.getMessage() + "\"");
			Assert.assertTrue(e.getMessage().contains("Cannot update terms in central"));
		}
	}

	@Test
	public void testUpdateTermNotInCentral() throws Exception {
		String name = "Integer2";
		// add random number to see the update
		String definition = "Integer NEW " + (int) (Math.random() * 100);

		Term origTerm = manager.findTermByName(name, CvId.SCALES);
		if (origTerm == null) { // first run, add before update
			origTerm = manager.addTerm(name, definition, CvId.SCALES);
		}

		manager
				.updateTerm(new Term(origTerm.getId(), name, definition));
		Term newTerm = manager.findTermByName(name, CvId.SCALES);

		Debug.println(MiddlewareIntegrationTest.INDENT, "Original:  " + origTerm);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Updated :  " + newTerm);

		if (origTerm != null && newTerm != null) {
			Assert.assertTrue(newTerm.getDefinition().equals(definition));
		}
	}

	@Test
	public void testGetStandardVariableIdByTermId() throws Exception {
		String propertyName = "property name " + new Random().nextInt(10000);
		manager.addProperty(propertyName, "test property", 1087);

		StandardVariable stdVariable = new StandardVariable();
		stdVariable.setName("variable name " + new Random().nextInt(10000));
		stdVariable.setDescription("variable description");
		stdVariable.setProperty(manager.findTermByName(propertyName,
				CvId.PROPERTIES));
		stdVariable.setMethod(new Term(4030, "Assigned", "Term, name or id assigned"));
		stdVariable.setScale(new Term(6000, "DBCV", "Controlled vocabulary from a database"));
		stdVariable.setStoredIn(new Term(1010, "Study information", "Study element"));
		stdVariable.setDataType(new Term(1120, "Character variable", "variable with char values"));
		stdVariable.setIsA(new Term(1050, "Study condition", "Study condition class"));
		stdVariable.setEnumerations(new ArrayList<Enumeration>());
		stdVariable.getEnumerations().add(new Enumeration(10000, "N", "Nursery", 1));
		stdVariable.getEnumerations().add(new Enumeration(10001, "HB", "Hybridization nursery", 2));
		stdVariable.getEnumerations().add(new Enumeration(10002, "PN", "Pedigree nursery", 3));
		stdVariable.setConstraints(new VariableConstraints(100.0, 999.0));
		stdVariable.setCropOntologyId("CROP-TEST");

		manager.addStandardVariable(stdVariable);

		Integer stdVariableId = manager.getStandardVariableIdByTermId(
				stdVariable.getProperty().getId(), TermId.HAS_PROPERTY);
		Debug.println(MiddlewareIntegrationTest.INDENT, "From db:  " + stdVariableId);
	}

	@Test
	public void testDeleteTerm() throws Exception {
		// terms to be deleted should be from local db

		String name = "Test Method " + new Random().nextInt(10000);
		String definition = "Test Definition";

		// add a method, should allow insert

		CvId cvId = CvId.METHODS;
		Term term = manager.addTerm(name, definition, cvId);

		manager.deleteTerm(term.getId(), cvId);

		// check if value does not exist anymore
		term = manager.getTermById(term.getId());
		Assert.assertNull(term);

		name = "Test Scale " + new Random().nextInt(10000);
		definition = "Test Definition";

		cvId = CvId.SCALES;
		term = manager.addTerm(name, definition, cvId);

		manager.deleteTerm(term.getId(), cvId);

		// check if value does not exist anymore
		term = manager.getTermById(term.getId());
		Assert.assertNull(term);
	}

	@Test
	public void testDeleteTermAndRelationship() throws Exception {
		String name = "Test Property" + new Random().nextInt(10000);
		String definition = "Property Definition";
		int isA = 1087;

		Term term = manager.addProperty(name, definition, isA);
		manager.deleteTermAndRelationship(term.getId(),
				CvId.PROPERTIES, TermId.IS_A.getId(), isA);

		term = manager.getTermById(term.getId());
		Assert.assertNull(term);

		name = "Test Trait Class " + new Random().nextInt(10000);
		definition = "Test Definition";

		term = manager.addTraitClass(name, definition,
				TermId.ONTOLOGY_TRAIT_CLASS.getId()).getTerm();
		manager.deleteTermAndRelationship(term.getId(),
				CvId.IBDB_TERMS, TermId.IS_A.getId(), TermId.ONTOLOGY_TRAIT_CLASS.getId());

		term = manager.getTermById(term.getId());
		Assert.assertNull(term);
	}

	@Test
	public void testDeleteOntology() throws Exception {
		String name = "Test Property" + new Random().nextInt(10000);
		String definition = "Property Definition";
		/*
		 * int isA = 1087; Term term = manager.addProperty(name, definition,
		 * isA); manager.deleteTermAndRelationship(term.getId(),
		 * CvId.PROPERTIES, TermId.IS_A.getId(), isA);
		 * 
		 * term= manager.getTermById(term.getId()); assertNull(term);
		 */
		name = "Parent Test Trait Class " + new Random().nextInt(10000);
		definition = "Parent Test Definition";

		Term termParent = manager.addTraitClass(name, definition,
				TermId.ONTOLOGY_TRAIT_CLASS.getId()).getTerm();

		name = "Child Test Trait Class " + new Random().nextInt(10000);
		definition = "Child Test Definition";
		Term termChild = manager.addTraitClass(name, definition,
				termParent.getId()).getTerm();
		boolean hasMiddlewareException = false;
		try {
			manager.deleteTermAndRelationship(termParent.getId(),
					CvId.IBDB_TERMS, TermId.IS_A.getId(), TermId.ONTOLOGY_TRAIT_CLASS.getId());
		} catch (MiddlewareQueryException e) {
			hasMiddlewareException = true;
		}
		Assert.assertEquals(true, hasMiddlewareException);

		// we do the cleanup here

		manager.deleteTermAndRelationship(termChild.getId(),
				CvId.IBDB_TERMS, TermId.IS_A.getId(), TermId.ONTOLOGY_TRAIT_CLASS.getId());
		manager.deleteTermAndRelationship(termParent.getId(),
				CvId.IBDB_TERMS, TermId.IS_A.getId(), TermId.ONTOLOGY_TRAIT_CLASS.getId());

		Term term = manager.getTermById(termChild.getId());
		Assert.assertNull(term);
		term = manager.getTermById(termParent.getId());
		Assert.assertNull(term);
	}

	@Test
	public void testGetAllPropertiesWithTraitClass() throws Exception {
		List<Property> properties = manager
				.getAllPropertiesWithTraitClass();
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, properties);
	}

	@Test
	public void testDeleteStandardVariable() throws Exception {
		List<TermProperty> termProperties = new ArrayList<TermProperty>();
		termProperties.add(new TermProperty(1, TermId.CROP_ONTOLOGY_ID.getId(), "CO:12345", 0));

		String propertyName = "property name " + new Random().nextInt(10000);
		manager.addProperty(propertyName, "test property", 1087);
		Property property = manager.getProperty(propertyName);

		String scaleName = "scale name " + new Random().nextInt(10000);
		Term scale = manager.addTerm(scaleName, "test scale",
				CvId.SCALES);

		String methodName = "method name " + new Random().nextInt(10000);
		Term method = manager.addTerm(methodName, methodName,
				CvId.METHODS);

		Term dataType = new Term(1120, "Character variable", "variable with char values");
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
		Debug.println(MiddlewareIntegrationTest.INDENT, String.valueOf(standardVariable.getId()));
		manager.deleteStandardVariable(standardVariable.getId());
		Term term = manager.getTermById(standardVariable.getId());

		Assert.assertNull(term);
	}
	
}
