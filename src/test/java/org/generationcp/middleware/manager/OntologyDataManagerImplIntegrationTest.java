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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.StandardVariableSummary;
import org.generationcp.middleware.domain.dms.VariableConstraints;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Property;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.domain.oms.TraitClassReference;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.utils.test.Debug;
import org.generationcp.middleware.utils.test.OntologyDataManagerImplTestConstants;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Ignore("Historic failing test. Disabled temporarily. Developers working in this area please spend some time to fix and remove @Ignore.")
public class OntologyDataManagerImplIntegrationTest extends IntegrationTestBase {

	private static final String PROGRAM_UUID = UUID.randomUUID().toString();

	@BeforeClass
	public static void setUpOnce() {
		// Variable caching relies on the context holder to determine current crop database in use
		ContextHolder.setCurrentCrop("maize");
		ContextHolder.setCurrentProgram(PROGRAM_UUID);
	}

	@Autowired
	private OntologyDataManager ontologyDataManager;

	@Test
	public void testGetCvTermById() throws Exception {
		Term term = this.ontologyDataManager.getTermById(6040);
		Assert.assertNotNull(term);
		Debug.println(IntegrationTestBase.INDENT, "testGetCvTermById(): " + term);
	}

	@Test
	public void testGetStandardVariable() throws Exception {
		StandardVariable stdVar =
				this.ontologyDataManager.getStandardVariable(OntologyDataManagerImplTestConstants.STD_VARIABLE_ID, PROGRAM_UUID);
		Assert.assertNotNull(stdVar);
		Debug.println(IntegrationTestBase.INDENT, "testGetStandardVariable(): " + stdVar);
	}

	@Test
	public void getStandVariableList() throws MiddlewareQueryException {
		List<Integer> ids = Arrays.asList(new Integer[] {1, 2, 3, 4, 5});
		List<StandardVariable> standardVariables = this.ontologyDataManager.getStandardVariables(ids, null);
		Assert.assertNotNull(standardVariables);
		Assert.assertTrue(standardVariables.size() > 0);
		for (StandardVariable standardVariable : standardVariables) {
			Assert.assertTrue(ids.contains(standardVariable.getId()));
		}
	}

	@Test
	public void testGetStandardVariableSummaries() throws MiddlewareQueryException {
		final int PLANT_HEIGHT_ID = 18020, GRAIN_YIELD_ID = 18000;
		List<Integer> idList = Arrays.asList(PLANT_HEIGHT_ID, GRAIN_YIELD_ID);

		List<StandardVariableSummary> summaries = this.ontologyDataManager.getStandardVariableSummaries(idList);

		Assert.assertNotNull(summaries);
		Assert.assertEquals(idList.size(), summaries.size());
		for (StandardVariableSummary summary : summaries) {
			Assert.assertTrue(idList.contains(summary.getId()));
		}
	}

	@Test
	public void testGetStandardVariableSummary() throws MiddlewareQueryException {
		// Load summary from the view based method
		StandardVariableSummary summary =
				this.ontologyDataManager.getStandardVariableSummary(OntologyDataManagerImplTestConstants.PLANT_HEIGHT_ID);
		Assert.assertNotNull(summary);

		// Load details using the ususal method
		StandardVariable details =
				this.ontologyDataManager.getStandardVariable(OntologyDataManagerImplTestConstants.PLANT_HEIGHT_ID, PROGRAM_UUID);
		Assert.assertNotNull(details);

		// Make sure that the summary data loaded from view based method matches
		// with detailed data loaded using the usual method.
		this.assertVariableDataMatches(details, summary);
	}

	@Test
	public void testGetStandardVariableSummaryNew() throws MiddlewareQueryException {
		// First create a new Standardvariable
		StandardVariable myOwnPlantHeight = new StandardVariable();
		myOwnPlantHeight.setName("MyOwnPlantHeight " + new Random().nextInt(1000));
		myOwnPlantHeight.setDescription(myOwnPlantHeight.getName() + " - Description.");
		myOwnPlantHeight.setProperty(new Term(15020, "Plant height", "Plant height"));
		myOwnPlantHeight.setMethod(new Term(16010, "Soil to tip at maturity", "Soil to tip at maturity"));

		Term myOwnScale = new Term();
		myOwnScale.setName("MyOwnScale " + new Random().nextInt(1000));
		myOwnScale.setDefinition(myOwnScale.getName() + " - Description.");
		myOwnPlantHeight.setScale(myOwnScale);

		myOwnPlantHeight.setIsA(new Term(OntologyDataManagerImplTestConstants.OBJECT_ID, "Agronomic", "Agronomic"));
		myOwnPlantHeight.setDataType(new Term(1110, "Numeric variable", "Variable with numeric values either continuous or integer"));

		this.ontologyDataManager.addStandardVariable(myOwnPlantHeight, PROGRAM_UUID);

		// Load details using existing method
		StandardVariable details = this.ontologyDataManager.getStandardVariable(myOwnPlantHeight.getId(), PROGRAM_UUID);
		Assert.assertNotNull(details);

		// Load summary from the view based method
		StandardVariableSummary summary = this.ontologyDataManager.getStandardVariableSummary(myOwnPlantHeight.getId());
		Assert.assertNotNull(summary);

		// Make sure that the summary data loaded from view matches with details
		// data loaded using the usual method.
		this.assertVariableDataMatches(details, summary);

		// Test done. Cleanup the test data created.
		this.ontologyDataManager.deleteStandardVariable(myOwnPlantHeight.getId());
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

		Assert.assertEquals(details.getPhenotypicType(), summary.getPhenotypicType());
	}

	private void assertTermDataMatches(Term termDetails, TermSummary termSummary) {
		Assert.assertEquals(new Integer(termDetails.getId()), termSummary.getId());
		Assert.assertEquals(termDetails.getName(), termSummary.getName());
		Assert.assertEquals(termDetails.getDefinition(), termSummary.getDefinition());
	}

	@Test
	public void testCopyStandardVariable() throws Exception {
		StandardVariable stdVar =
				this.ontologyDataManager.getStandardVariable(OntologyDataManagerImplTestConstants.STD_VARIABLE_ID, PROGRAM_UUID);
		StandardVariable stdVar2 = stdVar.copy();

		Assert.assertNotSame(stdVar.getId(), stdVar2.getId());
		Assert.assertSame(stdVar.getProperty(), stdVar2.getProperty());
		Assert.assertSame(stdVar.getScale(), stdVar2.getScale());
		Assert.assertSame(stdVar.getMethod(), stdVar2.getMethod());
		Assert.assertSame(stdVar.getDataType(), stdVar2.getDataType());
		Assert.assertSame(stdVar.getPhenotypicType(), stdVar2.getPhenotypicType());
		Assert.assertSame(stdVar.getConstraints(), stdVar2.getConstraints());
		if (stdVar.getName() != null) {
			Assert.assertTrue(stdVar.getName().equals(stdVar2.getName()));
		}
		if (stdVar.getDescription() != null) {
			Assert.assertTrue(stdVar.getDescription().equals(stdVar2.getDescription()));
		}
		Assert.assertSame(stdVar.getEnumerations(), stdVar2.getEnumerations());

		Debug.println(IntegrationTestBase.INDENT, "testCopyStandardVariable(): \n    " + stdVar + "\n    " + stdVar2);
	}

	@Test
	public void testStandardVariableCache() throws Exception {
		Debug.println(IntegrationTestBase.INDENT, "testStandardVariableCache(): ");
		// First call to getStandardVariable() will put the value to the cache
		this.ontologyDataManager.getStandardVariable(OntologyDataManagerImplTestConstants.STD_VARIABLE_ID, PROGRAM_UUID);
		// Second (and subsequent) calls will retrieve the value from the cache
		this.ontologyDataManager.getStandardVariable(OntologyDataManagerImplTestConstants.STD_VARIABLE_ID, PROGRAM_UUID);
	}

	@Test
	public void testNameSynonyms() throws Exception {
		StandardVariable sv = this.ontologyDataManager.getStandardVariable(8383, PROGRAM_UUID);
		sv.print(IntegrationTestBase.INDENT);
	}

	@Test
	public void testAddStandardVariable() throws Exception {
		// create new trait
		String propertyName = "property name " + new Random().nextInt(10000);
		this.ontologyDataManager.addProperty(propertyName, "test property", 1087);

		StandardVariable stdVariable = new StandardVariable();
		stdVariable.setName("variable name " + new Random().nextInt(10000));
		stdVariable.setDescription("variable description");
		// stdVariable.setProperty(new Term(2002, "User", "Database user"));
		stdVariable.setProperty(this.ontologyDataManager.findTermByName(propertyName, CvId.PROPERTIES));
		stdVariable.setMethod(new Term(4030, "Assigned", "Term, name or id assigned"));
		stdVariable.setScale(new Term(6000, "DBCV", "Controlled vocabulary from a database"));
		stdVariable.setDataType(new Term(1120, "Character variable", "variable with char values"));
		stdVariable.setIsA(new Term(1050, "Study condition", "Study condition class"));
		stdVariable.setEnumerations(new ArrayList<Enumeration>());
		stdVariable.getEnumerations().add(new Enumeration(StudyType.N.getId(), StudyType.N.getName(),  StudyType.N.getLabel(), 1));
		stdVariable.getEnumerations().add(new Enumeration(StudyType.HB.getId(), StudyType.HB.getName(),  StudyType.HB.getLabel(), 2));
		stdVariable.getEnumerations().add(new Enumeration(StudyType.PN.getId(), StudyType.PN.getName(),  StudyType.PN.getLabel(), 3));
		stdVariable.setConstraints(new VariableConstraints(100.0, 999.0));
		stdVariable.setCropOntologyId("CROP-TEST");

		try {
			this.ontologyDataManager.addStandardVariable(stdVariable, PROGRAM_UUID);
		} catch (MiddlewareQueryException e) {
			if (e.getMessage().contains("already exists")) {
				// Ignore. The test run successfully before.
			}
		}

		Debug.println(IntegrationTestBase.INDENT, "Standard variable saved: " + stdVariable.getId());
	}

	@Test
	public void testAddStandardVariableWithMissingScalePropertyMethod() throws Exception {
		StandardVariable stdVariable = new StandardVariable();
		stdVariable.setName("Test SPM" + new Random().nextInt(10000));
		stdVariable.setDescription("Std variable with new scale, property, method");

		Term newProperty = new Term(2451, "Environment", "Environment");
		Term property = this.ontologyDataManager.findTermByName(newProperty.getName(), CvId.PROPERTIES);
		if (property == null) {
			Debug.println(IntegrationTestBase.INDENT, "new property = " + newProperty.getName());
			property = newProperty;
		} else {
			Debug.println(IntegrationTestBase.INDENT, "property id = " + property.getId());
		}
		Term newScale = new Term(6020, "Text", "Text");
		Term scale = this.ontologyDataManager.findTermByName(newScale.getName(), CvId.SCALES);
		if (scale == null) {
			Debug.println(IntegrationTestBase.INDENT, "new scale = " + newScale.getName());
			scale = newScale;
		} else {
			Debug.println(IntegrationTestBase.INDENT, "scale id = " + scale.getId());
		}
		Term newMethod = new Term(0, "Test Method", "Test Method");
		Term method = this.ontologyDataManager.findTermByName(newMethod.getName(), CvId.METHODS);
		if (method == null) {
			Debug.println(IntegrationTestBase.INDENT, "new method = " + newMethod.getName());
			method = newMethod;
		} else {
			Debug.println(IntegrationTestBase.INDENT, "method id = " + method.getId());
		}
		stdVariable.setProperty(property);
		stdVariable.setScale(scale);
		stdVariable.setMethod(method);

		// added as this is required
		stdVariable.setIsA(new Term(1050, "Study condition", "Study condition class"));
		stdVariable.setDataType(new Term(1120, "Character variable", "variable with char values"));
		stdVariable.setEnumerations(new ArrayList<Enumeration>());
		stdVariable.getEnumerations().add(new Enumeration(StudyType.N.getId(), StudyType.N.getName(),  StudyType.N.getLabel(), 1));
		stdVariable.getEnumerations().add(new Enumeration(StudyType.HB.getId(), StudyType.HB.getName(),  StudyType.HB.getLabel(), 2));
		stdVariable.getEnumerations().add(new Enumeration(StudyType.PN.getId(), StudyType.PN.getName(),  StudyType.PN.getLabel(), 3));
		stdVariable.setConstraints(new VariableConstraints(100.0, 999.0));

		this.ontologyDataManager.addStandardVariable(stdVariable, PROGRAM_UUID);

		Debug.println(IntegrationTestBase.INDENT, "Standard variable saved: " + stdVariable.getId());
	}

	@Test
	public void testAddStandardVariableEnumeration() throws Exception {
		int standardVariableId = OntologyDataManagerImplTestConstants.CATEGORICAL_VARIABLE_TERM_ID;
		String name = "Name_" + new Random().nextInt(10000);
		String description = "Test Valid Value" + new Random().nextInt(10000);
		StandardVariable standardVariable = this.ontologyDataManager.getStandardVariable(standardVariableId, PROGRAM_UUID);
		Enumeration validValue = new Enumeration(null, name, description, 1);

		Debug.printObject(IntegrationTestBase.INDENT, standardVariable);
		this.ontologyDataManager.saveOrUpdateStandardVariableEnumeration(standardVariable, validValue);
		Debug.printObject(IntegrationTestBase.INDENT, validValue);
		standardVariable = this.ontologyDataManager.getStandardVariable(standardVariableId, PROGRAM_UUID);
		Debug.printObject(IntegrationTestBase.INDENT, standardVariable);
		Assert.assertNotNull(standardVariable.getEnumeration(validValue.getId()));

		// TO VERIFY IN MYSQL, delete the lines marked (*) below, then check in
		// local:
		// select * from cvterm where name = "8" and definition =
		// "Fully exserted";
		// select * from cvterm_relationship where subject_id = 22554;

		// (*) clean up
		this.ontologyDataManager.deleteStandardVariableEnumeration(standardVariableId, validValue.getId());
	}

	@Test
	public void testUpdateStandardVariableEnumeration() throws Exception {
		// Case 1: NEW VALID VALUE
		int standardVariableId = OntologyDataManagerImplTestConstants.CATEGORICAL_VARIABLE_TERM_ID;
		String name = "Name_" + new Random().nextInt(10000);
		String description = "Test Valid Value" + new Random().nextInt(10000);
		StandardVariable standardVariable = this.ontologyDataManager.getStandardVariable(standardVariableId, PROGRAM_UUID);
		Enumeration validValue = new Enumeration(null, name, description, standardVariable.getEnumerations().size() + 1);

		Debug.printObject(IntegrationTestBase.INDENT, standardVariable);
		this.ontologyDataManager.saveOrUpdateStandardVariableEnumeration(standardVariable, validValue);
		standardVariable = this.ontologyDataManager.getStandardVariable(standardVariableId, PROGRAM_UUID);
		Integer validValueGeneratedId1 = standardVariable.getEnumerationByName(name).getId();
		Debug.printObject(IntegrationTestBase.INDENT, validValue);
		standardVariable = this.ontologyDataManager.getStandardVariable(standardVariableId, PROGRAM_UUID);
		Debug.printObject(IntegrationTestBase.INDENT, standardVariable);
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
		this.ontologyDataManager.saveOrUpdateStandardVariableEnumeration(standardVariable, validValue);

		Debug.printObject(IntegrationTestBase.INDENT, validValue);
		standardVariable = this.ontologyDataManager.getStandardVariable(standardVariableId, PROGRAM_UUID);
		Debug.printObject(IntegrationTestBase.INDENT, standardVariable);
		Assert.assertNotNull(standardVariable.getEnumeration(validValue.getId()));

		// Case 3: UPDATE LOCAL VALID VALUE
		description = "Test Valid Value " + new Random().nextInt(10000);
		validValue.setDescription(description);
		this.ontologyDataManager.saveOrUpdateStandardVariableEnumeration(standardVariable, validValue);

		Debug.printObject(IntegrationTestBase.INDENT, validValue);
		standardVariable = this.ontologyDataManager.getStandardVariable(standardVariableId, PROGRAM_UUID);
		Debug.printObject(IntegrationTestBase.INDENT, standardVariable);
		Assert.assertTrue(standardVariable.getEnumeration(validValue.getId()).getDescription().equals(description));

		// (*) clean up
		this.ontologyDataManager.deleteStandardVariableEnumeration(standardVariableId, validValueGeneratedId1);
		this.ontologyDataManager.deleteStandardVariableEnumeration(standardVariableId, validValue.getId());

	}

	@Test
	public void testAddMethod() throws Exception {
		String name = "Test Method " + new Random().nextInt(10000);
		String definition = "Test Definition";
		Term term = this.ontologyDataManager.addMethod(name, definition);
		Assert.assertTrue(term.getId() > 0);
		Debug.println(IntegrationTestBase.INDENT, "testAddMethod():  " + term);
		term = this.ontologyDataManager.getTermById(term.getId());
		Debug.println(IntegrationTestBase.INDENT, "From db:  " + term);
	}

	/*
	 * @Test public void testGetStandardVariableIdByPropertyScaleMethod() throws Exception { Integer propertyId = Integer.valueOf(2010);
	 * Integer scaleId = Integer.valueOf(6000); Integer methodId = Integer.valueOf(4030);
	 * 
	 * Integer varid = manager.getStandardVariableIdByPropertyScaleMethod(propertyId, scaleId, methodId); assertNotNull (varid);
	 * Debug.println(INDENT, "testGetStandadardVariableIdByPropertyScaleMethod() Results: " + varid); }
	 */

	/**
	 * This tests an expected property of the ontology data manager to return a non empty map, even for entries that cannot be matched to a
	 * standard variable in the database
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

		Map<String, List<StandardVariable>> results = this.ontologyDataManager.getStandardVariablesInProjects(headers, PROGRAM_UUID);

		Assert.assertNotNull("Application is unable to return non empty output even on non present variables", results);
		Assert.assertTrue("Application is unable to return non empty output even on non present variables", results.size() == testItemCount);

		for (String header : headers) {
			Assert.assertTrue("Application is unable to return non empty output for each of the passed header parameters",
					results.containsKey(header));
		}

		for (Map.Entry<String, List<StandardVariable>> entry : results.entrySet()) {
			List<StandardVariable> vars = entry.getValue();

			Assert.assertNotNull(
					"Application should give a non null list of standard variables for a given header name, even if not present", vars);
			Assert.assertTrue("Application shouldn't be able to give values for dummy input", vars.isEmpty());
		}
	}

	/**
	 * This tests the ability of the application to retrieve the standard variables associated with known headers
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetStandardVariablesInProjectsKnownHeaders() throws Exception {
		List<String> headers = Arrays.asList(OntologyDataManagerImplTestConstants.COMMON_HEADERS);

		Map<String, List<StandardVariable>> results = this.ontologyDataManager.getStandardVariablesInProjects(headers, PROGRAM_UUID);

		for (Map.Entry<String, List<StandardVariable>> entry : results.entrySet()) {
			List<StandardVariable> variableList = entry.getValue();

			Assert.assertTrue("Application is unable to return a list of standard variables for known existing central headers ",
					variableList.size() > 0);
		}

	}

	/**
	 * This tests the ability of the application to retrieve standard variables for newly created items
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetStandardVariablesForNewlyCreatedEntries() throws Exception {
		// set up and create a new standard variable for this test
		StandardVariable dummyVariable = this.constructDummyStandardVariable();
		this.ontologyDataManager.addStandardVariable(dummyVariable, PROGRAM_UUID);

		List<String> headers = Arrays.asList(dummyVariable.getName());

		Map<String, List<StandardVariable>> results = this.ontologyDataManager.getStandardVariablesInProjects(headers, PROGRAM_UUID);

		try {

			for (Map.Entry<String, List<StandardVariable>> entry : results.entrySet()) {
				List<StandardVariable> variableList = entry.getValue();

				Assert.assertTrue(
						"Application is unable to return a proper sized list of standard variables for newly added standard variables",
						variableList.size() == 1);

				StandardVariable retrieved = variableList.get(0);
				Assert.assertEquals("Application is unable to retrieve the proper standard variable for newly created entries",
						dummyVariable.getName(), retrieved.getName());
			}
		} finally {
			// make test clean by deleting the dummy variable created during
			// start of test
			this.ontologyDataManager.deleteStandardVariable(dummyVariable.getId());
		}

	}

	protected StandardVariable constructDummyStandardVariable() throws Exception {

		StandardVariable standardVariable = new StandardVariable();

		standardVariable.setName("TestVariable" + new Random().nextLong());
		standardVariable.setDescription("For unit testing purposes");

		Term propertyTerm = this.ontologyDataManager.findTermByName("Yield", CvId.PROPERTIES);
		standardVariable.setProperty(propertyTerm);

		Term scaleTerm = this.ontologyDataManager.findTermByName("g", CvId.SCALES);
		standardVariable.setScale(scaleTerm);

		Term methodTerm = this.ontologyDataManager.findTermByName("Counting", CvId.METHODS);
		standardVariable.setMethod(methodTerm);

		Term dataType = this.ontologyDataManager.getTermById(TermId.NUMERIC_VARIABLE.getId());
		standardVariable.setDataType(dataType);

		standardVariable.setPhenotypicType(PhenotypicType.VARIATE);

		return standardVariable;
	}

	@Test
	public void testFindStandardVariablesByNameOrSynonym() throws Exception {
		Debug.println(IntegrationTestBase.INDENT, "Test FindStandardVariablesByNameOrSynonym");
		Set<StandardVariable> standardVariables =
				this.ontologyDataManager.findStandardVariablesByNameOrSynonym(OntologyDataManagerImplTestConstants.TERM_NAME_NOT_EXISTING,
						PROGRAM_UUID);
		Assert.assertSame(standardVariables.size(), 0);

		standardVariables =
				this.ontologyDataManager.findStandardVariablesByNameOrSynonym(
						OntologyDataManagerImplTestConstants.TERM_NAME_IS_IN_SYNONYMS, PROGRAM_UUID);
		Assert.assertSame(standardVariables.size(), 1);
		for (StandardVariable stdVar : standardVariables) {
			stdVar.print(IntegrationTestBase.INDENT);
		}

		standardVariables =
				this.ontologyDataManager.findStandardVariablesByNameOrSynonym(OntologyDataManagerImplTestConstants.TERM_SYNONYM,
						PROGRAM_UUID);
		Assert.assertSame(standardVariables.size(), 1);
		for (StandardVariable stdVar : standardVariables) {
			stdVar.print(IntegrationTestBase.INDENT);
		}
	}

	@Test
	public void testFindStandardVariablesByNameOrSynonymWithProperties() throws Exception {
		Debug.println(IntegrationTestBase.INDENT, "Test getTraitDetailsByTAbbr");
		Set<StandardVariable> standardVariables =
				this.ontologyDataManager.findStandardVariablesByNameOrSynonym(
						OntologyDataManagerImplTestConstants.TERM_NAME_IS_IN_SYNONYMS, PROGRAM_UUID);
		Assert.assertSame(standardVariables.size(), 1);
		for (StandardVariable stdVar : standardVariables) {
			stdVar.print(IntegrationTestBase.INDENT);
			Term term = this.ontologyDataManager.getTermById(stdVar.getId());
			term.print(IntegrationTestBase.INDENT);
		}
	}

	@Test
	public void testFindMethodById() throws Exception {

		// term doesn't exist
		Term term = this.ontologyDataManager.findMethodById(OntologyDataManagerImplTestConstants.TERM_ID_NOT_EXISTING);
		Assert.assertNull(term);

		// term exist but isn't a method
		term = this.ontologyDataManager.findMethodById(OntologyDataManagerImplTestConstants.TERM_ID_NOT_METHOD);
		Assert.assertNull(term);

		// term does exist in central
		term = this.ontologyDataManager.findMethodById(OntologyDataManagerImplTestConstants.TERM_ID_IN_CENTRAL);
		Assert.assertNotNull(term);
		term.print(IntegrationTestBase.INDENT);
		Debug.println(IntegrationTestBase.INDENT, "");

		// add a method to local
		String name = "Test Method " + new Random().nextInt(10000);
		String definition = "Test Definition";
		term = this.ontologyDataManager.addMethod(name, definition);
		// term does exist in local

		term = this.ontologyDataManager.findMethodById(term.getId());
		Assert.assertNotNull(term);
		term.print(IntegrationTestBase.INDENT);
	}

	@Test
	public void testFindMethodByName() throws Exception {
		Debug.println(IntegrationTestBase.INDENT, "Test findMethodByName");

		// term doesn't exist
		Term term = this.ontologyDataManager.findMethodByName(OntologyDataManagerImplTestConstants.TERM_NAME_NOT_EXISTING);
		Assert.assertNull(term);

		// term exist but isn't a method
		term = this.ontologyDataManager.findMethodByName(OntologyDataManagerImplTestConstants.TERM_NAME_NOT_METHOD);
		Assert.assertNull(term);

		// term does exist in central
		term = this.ontologyDataManager.findMethodByName(OntologyDataManagerImplTestConstants.TERM_NAME_IN_CENTRAL);
		Assert.assertNotNull(term);
		term.print(IntegrationTestBase.INDENT);
		Debug.println(IntegrationTestBase.INDENT, "");
	}

	@Test
	public void testFindStandardVariableByTraitScaleMethodNames() throws Exception {
		StandardVariable stdVar =
				this.ontologyDataManager.findStandardVariableByTraitScaleMethodNames("Cooperator", "DBCV", "Assigned", PROGRAM_UUID);
		Debug.println(IntegrationTestBase.INDENT, "testFindStandardVariableByTraitScaleMethodNames(): " + stdVar);
	}

	@Test
	public void testGetAllTermsByCvId() throws Exception {
		List<Term> terms = this.ontologyDataManager.getAllTermsByCvId(CvId.METHODS);
		Debug.println(IntegrationTestBase.INDENT, "testGetAllTermsByCvId - Get Methods: " + terms.size());
		this.printTerms(terms);
		terms = this.ontologyDataManager.getAllTermsByCvId(CvId.PROPERTIES);
		Debug.println(IntegrationTestBase.INDENT, "testGetAllTermsByCvId - Get Properties: " + terms.size());
		this.printTerms(terms);
		terms = this.ontologyDataManager.getAllTermsByCvId(CvId.SCALES);
		Debug.println(IntegrationTestBase.INDENT, "testGetAllTermsByCvId - Get Scales: " + terms.size());
		this.printTerms(terms);
	}

	private void printTerms(List<Term> terms) {
		for (Term term : terms) {
			term.print(IntegrationTestBase.INDENT);
			Debug.println(IntegrationTestBase.INDENT, "    ----------");
		}
	}

	@Test
	public void testCountTermsByCvId() throws Exception {
		long count = this.ontologyDataManager.countTermsByCvId(CvId.METHODS);
		Debug.println(IntegrationTestBase.INDENT, "testCountTermsByCvId() - Count All Methods: " + count);
		count = this.ontologyDataManager.countTermsByCvId(CvId.PROPERTIES);
		Debug.println(IntegrationTestBase.INDENT, "testCountTermsByCvId() - Count All Properties: " + count);
		count = this.ontologyDataManager.countTermsByCvId(CvId.SCALES);
		Debug.println(IntegrationTestBase.INDENT, "testCountTermsByCvId() - Count All Scales: " + count);
	}

	@Test
	public void testGetMethodsForTrait() throws Exception {
		StandardVariable stdVar =
				this.ontologyDataManager.findStandardVariableByTraitScaleMethodNames("User", "DBCV", "Assigned", PROGRAM_UUID);
		List<Term> terms = this.ontologyDataManager.getMethodsForTrait(stdVar.getProperty().getId());
		Debug.println(IntegrationTestBase.INDENT, "Size: " + terms.size());
		Assert.assertNotNull(terms);
		boolean hasAssigned = false;
		for (Term term : terms) {
			if (term.getName().equals("Assigned")) {
				hasAssigned = true;
			}
			Debug.println(IntegrationTestBase.INDENT, "method: " + term.getName());
		}
		Assert.assertTrue(hasAssigned);// should return Assigned

		// 2nd test
		stdVar =
				this.ontologyDataManager.findStandardVariableByTraitScaleMethodNames("Germplasm entry", "Number", "Enumerated",
						PROGRAM_UUID);
		terms = this.ontologyDataManager.getMethodsForTrait(stdVar.getProperty().getId());
		Debug.println(IntegrationTestBase.INDENT, "Size: " + terms.size());
		Assert.assertNotNull(terms);
		boolean hasEnumerated = false;
		for (Term term : terms) {
			if (term.getName().equals("Enumerated")) {
				hasEnumerated = true;
			}
			Debug.println(IntegrationTestBase.INDENT, "method: " + term.getName());
		}
		Assert.assertTrue(hasEnumerated);// should return Enumerated
	}

	@Test
	public void testGetScalesForTrait() throws Exception {
		StandardVariable stdVar =
				this.ontologyDataManager.findStandardVariableByTraitScaleMethodNames("User", "DBCV", "Assigned", PROGRAM_UUID);
		List<Term> terms = this.ontologyDataManager.getScalesForTrait(stdVar.getProperty().getId());
		Debug.println(IntegrationTestBase.INDENT, "Size: " + terms.size());
		Assert.assertNotNull(terms);
		boolean hasDBCV = false;
		for (Term term : terms) {
			if (term.getName().equals("DBCV")) {
				hasDBCV = true;
			}
			Debug.println(IntegrationTestBase.INDENT, "scale: " + term.getName());
		}
		Assert.assertTrue(hasDBCV);// should return DBCV

		// 2nd test
		stdVar =
				this.ontologyDataManager.findStandardVariableByTraitScaleMethodNames("Germplasm entry", "Number", "Enumerated",
						PROGRAM_UUID);
		terms = this.ontologyDataManager.getScalesForTrait(stdVar.getProperty().getId());
		Debug.println(IntegrationTestBase.INDENT, "Size: " + terms.size());
		Assert.assertNotNull(terms);
		boolean hasNumber = false;
		for (Term term : terms) {
			if (term.getName().equals("Number")) {
				hasNumber = true;
			}
			Debug.println(IntegrationTestBase.INDENT, "scale: " + term.getName());
		}
		Assert.assertTrue(hasNumber);// should return Number
	}

	@Test
	public void testAddTerm() throws Exception {
		String name = "Test Method " + new Random().nextInt(10000);
		String definition = "Test Definition";

		// add a method, should allow insert

		CvId cvId = CvId.METHODS;
		Term term = this.ontologyDataManager.addTerm(name, definition, cvId);
		Assert.assertNotNull(term);
		Assert.assertTrue(term.getId() > 0);
		Debug.println(IntegrationTestBase.INDENT, "testAddTerm():  " + term);
		term = this.ontologyDataManager.getTermById(term.getId());
		Debug.println(IntegrationTestBase.INDENT, "From db:  " + term);

		// add a variable, should not allow insert and should throw an exception
		// uncomment the ff. to test adding variables
		/*
		 * name = "Test Variable " + new Random().nextInt(10000); definition = "Test Variable"; cvId = CvId.VARIABLES; term =
		 * manager.addTerm(name, definition, cvId); assertTrue(term == null);
		 */
	}

	@Test
	public void testFindTermByName() throws Exception {
		// term doesn't exist
		Term term = this.ontologyDataManager.findTermByName(OntologyDataManagerImplTestConstants.TERM_NAME_NOT_EXISTING, CvId.METHODS);
		Assert.assertNull(term);

		// term exist but isn't a method
		term = this.ontologyDataManager.findTermByName(OntologyDataManagerImplTestConstants.TERM_NAME_NOT_METHOD, CvId.METHODS);
		Assert.assertNull(term);

		// term does exist in central
		term = this.ontologyDataManager.findTermByName(OntologyDataManagerImplTestConstants.TERM_NAME_IN_CENTRAL, CvId.METHODS);
		Assert.assertNotNull(term);
		term.print(IntegrationTestBase.INDENT);
		Debug.println(IntegrationTestBase.INDENT, "");

	}

	@Test
	public void testGetDataTypes() throws Exception {
		List<Term> terms = this.ontologyDataManager.getDataTypes();
		Debug.println(IntegrationTestBase.INDENT, "testGetDataTypes: " + terms.size());
		this.printTerms(terms);
	}

	@Test
	public void testGetStandardVariablesForPhenotypicType() throws Exception {
		PhenotypicType phenotypicType = PhenotypicType.TRIAL_ENVIRONMENT;
		Integer start = 0;
		Integer numOfRows = 100;

		Map<String, StandardVariable> standardVariables =
				this.ontologyDataManager.getStandardVariablesForPhenotypicType(phenotypicType, PROGRAM_UUID, start, numOfRows);

		for (Object key : standardVariables.keySet()) {
			Debug.println(key + " : " + standardVariables.get(key).getId() + " : " + standardVariables.get(key).toString());
		}

		Debug.println(IntegrationTestBase.INDENT, "count: " + standardVariables.size());
	}

	@Test
	public void testGetStandardVariablesInProjects() throws Exception {
		List<String> headers =
				Arrays.asList("ENTRY", "ENTRYNO", "PLOT", "TRIAL_NO", "TRIAL", "STUDY", "DATASET", "LOC", "LOCN", "NURSER", "Plot Number");

		Map<String, List<StandardVariable>> results = this.ontologyDataManager.getStandardVariablesInProjects(headers, PROGRAM_UUID);

		Debug.println(IntegrationTestBase.INDENT, "testGetStandardVariablesInProjects(headers=" + headers + ") RESULTS:");
		for (String name : headers) {
			Debug.println(IntegrationTestBase.INDENT, "Header = " + name + ", StandardVariables: ");
			if (results.get(name).size() > 0) {
				for (StandardVariable var : results.get(name)) {
					Debug.println(IntegrationTestBase.INDENT, var.getId() + ", ");
				}
				Debug.println(IntegrationTestBase.INDENT, "");
			} else {
				Debug.println(IntegrationTestBase.INDENT, "    No standard variables found.");
			}
		}
	}

	@Test
	public void testFindTermsByNameOrSynonym() throws Exception {
		// term doesn't exist
		List<Term> terms =
				this.ontologyDataManager
				.findTermsByNameOrSynonym(OntologyDataManagerImplTestConstants.TERM_NAME_NOT_EXISTING, CvId.METHODS);
		Assert.assertSame(terms.size(), 0);

		// term exist but isn't a method
		terms = this.ontologyDataManager.findTermsByNameOrSynonym(OntologyDataManagerImplTestConstants.TERM_NAME_NOT_METHOD, CvId.METHODS);
		Assert.assertSame(terms.size(), 0);

		// term does exist in central
		terms = this.ontologyDataManager.findTermsByNameOrSynonym(OntologyDataManagerImplTestConstants.TERM_NAME_IN_CENTRAL, CvId.METHODS);
		Assert.assertNotNull(terms);
		Assert.assertTrue(!terms.isEmpty());

		terms.get(0).print(IntegrationTestBase.INDENT);
		Debug.println(IntegrationTestBase.INDENT, "");

		// name is in synonyms
		terms =
				this.ontologyDataManager.findTermsByNameOrSynonym(OntologyDataManagerImplTestConstants.TERM_NAME_IS_IN_SYNONYMS,
						CvId.VARIABLES);
		Assert.assertNotNull(terms);
		terms.get(0).print(IntegrationTestBase.INDENT);
		Debug.println(IntegrationTestBase.INDENT, "");

		// name is both in term and in synonyms
		// need to modify the entry in cvterm where name = "Cooperator" to have
		// cv_id = 1010
		terms = this.ontologyDataManager.findTermsByNameOrSynonym("Cooperator", CvId.PROPERTIES);
		Assert.assertNotNull(terms);
		for (Term term : terms) {
			term.print(IntegrationTestBase.INDENT);
			Debug.println(IntegrationTestBase.INDENT, "");
		}

	}

	@Test
	public void testCountIsAOfProperties() throws Exception {
		long asOf = this.ontologyDataManager.countIsAOfProperties();
		Debug.println(IntegrationTestBase.INDENT, "count is a properties " + asOf);
	}

	@Test
	public void testAddProperty() throws Exception {
		String name = "Germplasm type 3";
		String definition = "Germplasm type description 3";
		int isA = 1087;

		Debug.println(IntegrationTestBase.INDENT, "testAddProperty(name=" + name + ", definition=" + definition + ", isA=" + isA + "): ");
		Term term = this.ontologyDataManager.addProperty(name, definition, isA);
		term.print(IntegrationTestBase.INDENT);

	}

	@Test
	public void testGetProperty() throws Exception {
		int termId = 2452;

		Property property = this.ontologyDataManager.getProperty(termId);

		Debug.println(IntegrationTestBase.INDENT, property.toString());
	}

	@Test
	public void testGetAllTraitGroupsHierarchy() throws Exception {
		List<TraitClassReference> traitGroups = this.ontologyDataManager.getAllTraitGroupsHierarchy(true);
		for (TraitClassReference traitGroup : traitGroups) {
			traitGroup.print(IntegrationTestBase.INDENT);
		}
	}

	@Test
	public void testGetPropertyByName() throws Exception {
		String name = "Season";
		Property property = this.ontologyDataManager.getProperty(name);
		Debug.println(IntegrationTestBase.INDENT, property.toString());
	}

	@Test
	@Ignore(value = "Skipping this test. It takes minute+ to run and does not have any assertions anyway. Needs revising.")
	public void testGetAllStandardVariable() throws Exception {
		Set<StandardVariable> standardVariables = this.ontologyDataManager.getAllStandardVariables(PROGRAM_UUID);
		for (StandardVariable stdVar : standardVariables) {
			stdVar.print(IntegrationTestBase.INDENT);
		}

		Debug.println(IntegrationTestBase.INDENT, "count: " + standardVariables.size());
	}

	@Test
	public void testGetStandardVariablesByTraitClass() throws MiddlewareQueryException {
		List<StandardVariable> vars =
				this.ontologyDataManager.getStandardVariables(OntologyDataManagerImplTestConstants.NONEXISTING_TERM_TRAIT_CLASS_ID, null,
						null, null, PROGRAM_UUID);
		Assert.assertTrue(vars.isEmpty());

		vars =
				this.ontologyDataManager.getStandardVariables(OntologyDataManagerImplTestConstants.EXPECTED_TERM_TRAIT_CLASS_ID, null,
						null, null, PROGRAM_UUID);
		Assert.assertFalse(vars.isEmpty());

		for (StandardVariable var : vars) {
			Debug.println(IntegrationTestBase.INDENT, var.toString());
		}
	}

	@Test
	public void testGetStandardVariablesByProperty() throws MiddlewareQueryException {
		List<StandardVariable> vars =
				this.ontologyDataManager.getStandardVariables(null, OntologyDataManagerImplTestConstants.NONEXISTING_TERM_PROPERTY_ID,
						null, null, PROGRAM_UUID);

		Assert.assertTrue(vars.isEmpty());

		vars =
				this.ontologyDataManager.getStandardVariables(null, OntologyDataManagerImplTestConstants.EXPECTED_TERM_PROPERTY_ID, null,
						null, PROGRAM_UUID);
		Assert.assertFalse(vars.isEmpty());

		for (StandardVariable var : vars) {
			Debug.println(IntegrationTestBase.INDENT, var.toString());
		}
	}

	@Test
	public void testGetStandardVariablesByMethod() throws MiddlewareQueryException {
		List<StandardVariable> vars =
				this.ontologyDataManager.getStandardVariables(null, null, OntologyDataManagerImplTestConstants.NONEXISTING_TERM_METHOD_ID,
						null, PROGRAM_UUID);
		Assert.assertTrue(vars.isEmpty());

		vars =
				this.ontologyDataManager.getStandardVariables(null, null, OntologyDataManagerImplTestConstants.EXPECTED_TERM_METHOD_ID,
						null, PROGRAM_UUID);
		Assert.assertFalse(vars.isEmpty());

		for (StandardVariable var : vars) {
			Debug.println(IntegrationTestBase.INDENT, var.toString());
		}
	}

	@Test
	public void testGetStandardVariablesByScale() throws MiddlewareQueryException {
		List<StandardVariable> vars =
				this.ontologyDataManager.getStandardVariables(null, null, null,
						OntologyDataManagerImplTestConstants.NONEXISTING_TERM_SCALE_ID, PROGRAM_UUID);
		Assert.assertTrue(vars.isEmpty());

		vars =
				this.ontologyDataManager.getStandardVariables(null, null, null,
						OntologyDataManagerImplTestConstants.EXPECTED_TERM_SCALE_ID, PROGRAM_UUID);
		Assert.assertFalse(vars.isEmpty());

		for (StandardVariable var : vars) {
			Debug.println(IntegrationTestBase.INDENT, var.toString());
		}
	}

	@Test
	public void testAddOrUpdateTermAndRelationship() throws Exception {
		String name = "Study condition NEW";
		String definition = "Study condition NEW class " + (int) (Math.random() * 100);
		Term origTerm = this.ontologyDataManager.findTermByName(name, CvId.PROPERTIES);
		Term newTerm =
				this.ontologyDataManager.addOrUpdateTermAndRelationship(name, definition, CvId.PROPERTIES, TermId.IS_A.getId(),
						OntologyDataManagerImplTestConstants.OBJECT_ID);
		Debug.println(IntegrationTestBase.INDENT, "Original:  " + origTerm);
		Debug.println(IntegrationTestBase.INDENT, "Updated :  " + newTerm);

		if (origTerm != null) { // if the operation is update, the ids must be
			// same
			Assert.assertEquals(origTerm.getId(), newTerm.getId());
		}
	}

	@Test
	public void testUpdateTermAndRelationship() throws Exception {
		String name = "Slope NEW";
		String definition = "Slope NEW class " + (int) (Math.random() * 100);
		Term origTerm = this.ontologyDataManager.findTermByName(name, CvId.PROPERTIES);
		if (origTerm == null) { // first run, add before update
			origTerm =
					this.ontologyDataManager.addOrUpdateTermAndRelationship(name, definition, CvId.PROPERTIES, TermId.IS_A.getId(),
							OntologyDataManagerImplTestConstants.OBJECT_ID);
		}

		this.ontologyDataManager.updateTermAndRelationship(new Term(origTerm.getId(), name, definition), TermId.IS_A.getId(),
				OntologyDataManagerImplTestConstants.OBJECT_ID);
		Term newTerm = this.ontologyDataManager.findTermByName(name, CvId.PROPERTIES);
		Debug.println(IntegrationTestBase.INDENT, "Original:  " + origTerm);
		Debug.println(IntegrationTestBase.INDENT, "Updated :  " + newTerm);

		if (newTerm != null) {
			Assert.assertTrue(newTerm.getDefinition().equals(definition));
		}
	}

	@Test
	public void testAddOrUpdateTerm() throws Exception {
		String name = "Real";
		// add random number to see the update
		String definition = "Real Description NEW " + (int) (Math.random() * 100);
		Term origTerm = this.ontologyDataManager.findTermByName(name, CvId.SCALES);
		Term newTerm = this.ontologyDataManager.addOrUpdateTerm(name, definition, CvId.SCALES);

		if (origTerm != null) { // if the operation is update, the ids must be same
			Assert.assertEquals(origTerm.getId(), newTerm.getId());
		}
	}

	@Test
	public void testUpdateTerm() throws Exception {
		String name = "Integer2";
		// add random number to see the update
		String definition = "Integer NEW " + (int) (Math.random() * 100);

		Term origTerm = this.ontologyDataManager.findTermByName(name, CvId.SCALES);
		if (origTerm == null) { // first run, add before update
			origTerm = this.ontologyDataManager.addTerm(name, definition, CvId.SCALES);
		}

		this.ontologyDataManager.updateTerm(new Term(origTerm.getId(), name, definition));
		Term newTerm = this.ontologyDataManager.findTermByName(name, CvId.SCALES);

		if (origTerm != null && newTerm != null) {
			Assert.assertTrue(newTerm.getDefinition().equals(definition));
		}
	}

	@Test
	public void testUpdateTerms() throws Exception {

		String termName1 = "Sample Term 1";
		String termName2 = "Sample Term 2";
		String termDescription1 = "Sample Term Description 1";
		String termDescription2 = "Sample Term Description 2";

		Term term1 = this.ontologyDataManager.addTerm(termName1, termDescription1, CvId.SCALES);
		Term term2 = this.ontologyDataManager.addTerm(termName2, termDescription2, CvId.SCALES);

		// Update the added terms name with new names.
		List<Term> terms = new ArrayList<>();
		term1.setName("New Term Name 1");
		term2.setName("New Term Name 2");
		terms.add(term1);
		terms.add(term2);

		this.ontologyDataManager.updateTerms(terms);

		Term newTerm1 = this.ontologyDataManager.getTermById(term1.getId());
		Term newTerm2 = this.ontologyDataManager.getTermById(term2.getId());

		Assert.assertEquals("The term's name should be updated", "New Term Name 1", newTerm1.getName());
		Assert.assertEquals("The term's name should be updated", "New Term Name 2", newTerm2.getName());

	}


	@Test
	public void testGetStandardVariableIdByTermId() throws Exception {
		String propertyName = "property name " + new Random().nextInt(10000);
		this.ontologyDataManager.addProperty(propertyName, "test property", 1087);

		StandardVariable stdVariable = new StandardVariable();
		stdVariable.setName("variable name " + new Random().nextInt(10000));
		stdVariable.setDescription("variable description");
		stdVariable.setProperty(this.ontologyDataManager.findTermByName(propertyName, CvId.PROPERTIES));
		stdVariable.setMethod(new Term(4030, "Assigned", "Term, name or id assigned"));
		stdVariable.setScale(new Term(6000, "DBCV", "Controlled vocabulary from a database"));
		stdVariable.setDataType(new Term(1120, "Character variable", "variable with char values"));
		stdVariable.setIsA(new Term(1050, "Study condition", "Study condition class"));
		stdVariable.setEnumerations(new ArrayList<Enumeration>());
		stdVariable.getEnumerations().add(new Enumeration(StudyType.N.getId(), StudyType.N.getName(),  StudyType.N.getLabel(), 1));
		stdVariable.getEnumerations().add(new Enumeration(StudyType.HB.getId(), StudyType.HB.getName(),  StudyType.HB.getLabel(), 2));
		stdVariable.getEnumerations().add(new Enumeration(StudyType.PN.getId(), StudyType.PN.getName(),  StudyType.PN.getLabel(), 3));
		stdVariable.setConstraints(new VariableConstraints(100.0, 999.0));
		stdVariable.setCropOntologyId("CROP-TEST");

		this.ontologyDataManager.addStandardVariable(stdVariable, PROGRAM_UUID);

		Integer stdVariableId =
				this.ontologyDataManager.getStandardVariableIdByTermId(stdVariable.getProperty().getId(), TermId.HAS_PROPERTY);
		Assert.assertNotNull(stdVariableId);
	}

	@Test
	public void testDeleteTerm() throws Exception {
		// terms to be deleted should be from local db

		String name = "Test Method " + new Random().nextInt(10000);
		String definition = "Test Definition";

		// add a method, should allow insert

		CvId cvId = CvId.METHODS;
		Term term = this.ontologyDataManager.addTerm(name, definition, cvId);

		this.ontologyDataManager.deleteTerm(term.getId(), cvId);

		// check if value does not exist anymore
		term = this.ontologyDataManager.getTermById(term.getId());
		Assert.assertNull(term);

		name = "Test Scale " + new Random().nextInt(10000);
		definition = "Test Definition";

		cvId = CvId.SCALES;
		term = this.ontologyDataManager.addTerm(name, definition, cvId);

		this.ontologyDataManager.deleteTerm(term.getId(), cvId);

		// check if value does not exist anymore
		term = this.ontologyDataManager.getTermById(term.getId());
		Assert.assertNull(term);
	}

	@Test
	public void testDeleteTermAndRelationship() throws Exception {
		String name = "Test Property" + new Random().nextInt(10000);
		String definition = "Property Definition";
		int isA = 1087;

		Term term = this.ontologyDataManager.addProperty(name, definition, isA);
		this.ontologyDataManager.deleteTermAndRelationship(term.getId(), CvId.PROPERTIES, TermId.IS_A.getId(), isA);

		term = this.ontologyDataManager.getTermById(term.getId());
		Assert.assertNull(term);

		name = "Test Trait Class " + new Random().nextInt(10000);
		definition = "Test Definition";

		term = this.ontologyDataManager.addTraitClass(name, definition, TermId.ONTOLOGY_TRAIT_CLASS.getId()).getTerm();
		this.ontologyDataManager.deleteTermAndRelationship(term.getId(), CvId.IBDB_TERMS, TermId.IS_A.getId(),
				TermId.ONTOLOGY_TRAIT_CLASS.getId());

		term = this.ontologyDataManager.getTermById(term.getId());
		Assert.assertNull(term);
	}

	@Test
	public void testDeleteParentChildTerms() throws Exception {
		String parentTermName = "Parent Test Trait Class" + new Random().nextInt(10000);
		String parentTermDef = parentTermName + " Definition";
		Term termParent =
				this.ontologyDataManager.addTraitClass(parentTermName, parentTermDef, TermId.ONTOLOGY_TRAIT_CLASS.getId()).getTerm();

		String childTermName = "Child Test Trait Class" + new Random().nextInt(10000);
		String childTermDef = childTermName + " Definition";
		this.ontologyDataManager.addTraitClass(childTermName, childTermDef, termParent.getId()).getTerm();

		boolean hasMiddlewareException = false;
		try {
			this.ontologyDataManager.deleteTermAndRelationship(termParent.getId(), CvId.IBDB_TERMS, TermId.IS_A.getId(),
					TermId.ONTOLOGY_TRAIT_CLASS.getId());
		} catch (MiddlewareQueryException e) {
			hasMiddlewareException = true;
		}
		Assert.assertEquals(true, hasMiddlewareException);
	}

	@Test
	public void testGetAllPropertiesWithTraitClass() throws Exception {
		List<Property> properties = this.ontologyDataManager.getAllPropertiesWithTraitClass();
		Debug.printObjects(IntegrationTestBase.INDENT, properties);
	}

	@Test
	public void testDeleteStandardVariable() throws Exception {
		StandardVariable myOwnPlantHeight = new StandardVariable();
		myOwnPlantHeight.setName("MyOwnPlantHeight " + new Random().nextInt(1000));
		myOwnPlantHeight.setDescription(myOwnPlantHeight.getName() + " - Description.");
		myOwnPlantHeight.setProperty(new Term(15020, "Plant height", "Plant height"));
		myOwnPlantHeight.setMethod(new Term(16010, "Soil to tip at maturity", "Soil to tip at maturity"));

		Term myOwnScale = new Term();
		myOwnScale.setName("MyOwnScale " + new Random().nextInt(1000));
		myOwnScale.setDefinition(myOwnScale.getName() + " - Description.");
		myOwnPlantHeight.setScale(myOwnScale);

		myOwnPlantHeight.setIsA(new Term(OntologyDataManagerImplTestConstants.OBJECT_ID, "Agronomic", "Agronomic"));
		myOwnPlantHeight.setDataType(new Term(1110, "Numeric variable", "Variable with numeric values either continuous or integer"));

		this.ontologyDataManager.addStandardVariable(myOwnPlantHeight, PROGRAM_UUID);
		// Check that variable got created
		Assert.assertNotNull(myOwnPlantHeight.getId());

		this.ontologyDataManager.deleteStandardVariable(myOwnPlantHeight.getId());

		// Check that the variable got deleted
		Term term = this.ontologyDataManager.getTermById(myOwnPlantHeight.getId());
		Assert.assertNull("Expected the standard variable deleted but it is still there!", term);
	}

	@Test
	public void testGetCVIdByName() throws MiddlewareQueryException {
		Integer cvId = this.ontologyDataManager.getCVIdByName("Variables");
		Assert.assertTrue(cvId == 1040);
	}
}
