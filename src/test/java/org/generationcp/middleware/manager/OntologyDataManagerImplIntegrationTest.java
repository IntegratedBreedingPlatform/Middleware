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

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.StandardVariableSummary;
import org.generationcp.middleware.domain.dms.VariableConstraints;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Property;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.domain.oms.TraitClassReference;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.utils.test.OntologyDataManagerImplTestConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class OntologyDataManagerImplIntegrationTest extends IntegrationTestBase {

	private static final String PROGRAM_UUID = UUID.randomUUID().toString();

	@BeforeClass
	public static void setUpOnce() {
		// Variable caching relies on the context holder to determine current crop database in use
		ContextHolder.setCurrentCrop("maize");
		ContextHolder.setCurrentProgram(PROGRAM_UUID);
	}

	private OntologyDataManagerImpl ontologyDataManager;
	private CVTermDao cvTermDao;

	@Before
	public void init() {
		this.ontologyDataManager = new OntologyDataManagerImpl(this.sessionProvder);
		this.cvTermDao = new CVTermDao();
		this.cvTermDao.setSession(this.sessionProvder.getSession());
	}

	@Test
	public void testGetCvTermById() {
		final Term term = this.ontologyDataManager.getTermById(6040);
		Assert.assertNotNull(term);
	}

	@Test
	public void testGetStandardVariable() {
		final StandardVariable standardVariable =
			this.ontologyDataManager.getStandardVariable(OntologyDataManagerImplTestConstants.STD_VARIABLE_ID, PROGRAM_UUID);
		Assert.assertNotNull(standardVariable);
	}

	@Test
	public void getStandVariableList() {
		final StandardVariable standardVariable1 = this.createStandardVariable("StandardVariable1");
		final StandardVariable standardVariable2 = this.createStandardVariable("StandardVariable2");

		final List<Integer> ids = Arrays.asList(standardVariable1.getId(), standardVariable2.getId());
		final List<StandardVariable> standardVariables = this.ontologyDataManager.getStandardVariables(ids, PROGRAM_UUID);
		Assert.assertEquals(2, standardVariables.size());
		Assert.assertEquals(standardVariable1.getId(), standardVariables.get(0).getId());
		Assert.assertEquals(standardVariable2.getId(), standardVariables.get(1).getId());
	}

	@Test
	public void testGetStandardVariableSummaries() {

		final StandardVariable standardVariable1 = this.createStandardVariable("StandardVariable1");
		final StandardVariable standardVariable2 = this.createStandardVariable("StandardVariable2");

		final List<Integer> ids = Arrays.asList(standardVariable1.getId(), standardVariable2.getId());

		final List<StandardVariableSummary> summaries = this.ontologyDataManager.getStandardVariableSummaries(ids);

		Assert.assertNotNull(summaries);
		Assert.assertEquals(ids.size(), summaries.size());
		for (final StandardVariableSummary summary : summaries) {
			Assert.assertTrue(ids.contains(summary.getId()));
		}
	}

	@Test
	public void testGetStandardVariableSummary() {
		// First create a new Standardvariable
		final StandardVariable testStandardVariable = this.createStandardVariable("TestVariable");

		// Load summary from the view based method
		final StandardVariableSummary summary = this.ontologyDataManager.getStandardVariableSummary(testStandardVariable.getId());
		Assert.assertNotNull(summary);

		// Make sure that the summary data loaded from view matches with details
		// data loaded using the usual method.
		this.assertVariableDataMatches(testStandardVariable, summary);

	}

	private void assertVariableDataMatches(final StandardVariable standardVariable, final StandardVariableSummary summary) {
		Assert.assertEquals(new Integer(standardVariable.getId()), summary.getId());
		Assert.assertEquals(standardVariable.getName(), summary.getName());
		Assert.assertEquals(standardVariable.getDescription(), summary.getDescription());

		this.assertTermDataMatches(standardVariable.getProperty(), summary.getProperty());
		this.assertTermDataMatches(standardVariable.getMethod(), summary.getMethod());
		this.assertTermDataMatches(standardVariable.getScale(), summary.getScale());
		this.assertTermDataMatches(standardVariable.getDataType(), summary.getDataType());

		Assert.assertEquals(standardVariable.getPhenotypicType(), summary.getPhenotypicType());
	}

	private void assertTermDataMatches(final Term termDetails, final TermSummary termSummary) {
		Assert.assertEquals(new Integer(termDetails.getId()), termSummary.getId());
		Assert.assertEquals(termDetails.getName(), termSummary.getName());
		Assert.assertEquals(termDetails.getDefinition(), termSummary.getDefinition());
	}

	@Test
	public void testCopyStandardVariable() {
		final StandardVariable stdVar =
			this.ontologyDataManager.getStandardVariable(OntologyDataManagerImplTestConstants.STD_VARIABLE_ID, PROGRAM_UUID);
		final StandardVariable stdVar2 = stdVar.copy();

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

	}

	@Test
	public void testStandardVariableCache() {
		// First call to getStandardVariable() will put the value to the cache
		this.ontologyDataManager.getStandardVariable(OntologyDataManagerImplTestConstants.STD_VARIABLE_ID, PROGRAM_UUID);
		// Second (and subsequent) calls will retrieve the value from the cache
		this.ontologyDataManager.getStandardVariable(OntologyDataManagerImplTestConstants.STD_VARIABLE_ID, PROGRAM_UUID);
	}

	@Test
	public void testNameSynonyms() {
		final StandardVariable sv = this.ontologyDataManager.getStandardVariable(8383, PROGRAM_UUID);
		sv.print(IntegrationTestBase.INDENT);
	}

	@Test
	public void testAddStandardVariable() {
		// create new trait
		final String propertyName = "property name " + new Random().nextInt(10000);
		this.ontologyDataManager.addProperty(propertyName, "test property", 1087);

		final StandardVariable stdVariable = new StandardVariable();
		stdVariable.setName("variable name " + new Random().nextInt(10000));
		stdVariable.setDescription("variable description");
		// stdVariable.setProperty(new Term(2002, "User", "Database user"));
		stdVariable.setProperty(this.ontologyDataManager.findTermByName(propertyName, CvId.PROPERTIES));
		stdVariable.setMethod(new Term(4030, "Assigned", "Term, name or id assigned"));
		stdVariable.setScale(new Term(6000, "DBCV", "Controlled vocabulary from a database"));
		stdVariable.setDataType(new Term(1120, "Character variable", "variable with char values"));
		stdVariable.setIsA(new Term(1050, "Study condition", "Study condition class"));
		stdVariable.setEnumerations(new ArrayList<Enumeration>());
		stdVariable.getEnumerations().add(new Enumeration(10000, "N", "Nursery", 1));
		stdVariable.getEnumerations().add(new Enumeration(10001, "HB", "Hybridization Nursery", 2));
		stdVariable.getEnumerations().add(new Enumeration(10002, "PN", "Pedigree Nursery", 3));
		stdVariable.setConstraints(new VariableConstraints(100.0, 999.0));
		stdVariable.setCropOntologyId("CROP-TEST");

		try {
			this.ontologyDataManager.addStandardVariable(stdVariable, PROGRAM_UUID);
		} catch (final MiddlewareQueryException e) {
			if (e.getMessage().contains("already exists")) {
				// Ignore. The test run successfully before.
			}
		}

	}

	@Test(expected = MiddlewareQueryException.class)
	public void testAddStandardVariableWithMissingScalePropertyMethod() {
		final StandardVariable standardVariable = new StandardVariable();
		standardVariable.setName("Test SPM" + new Random().nextInt(10000));
		standardVariable.setDescription("Std variable with new scale, property, method");

		final Term newProperty = new Term(2451, "Environment", "Environment");
		Term property = this.ontologyDataManager.findTermByName(newProperty.getName(), CvId.PROPERTIES);
		if (property == null) {
			property = newProperty;
		}
		final Term newScale = new Term(6020, "Text", "Text");
		Term scale = this.ontologyDataManager.findTermByName(newScale.getName(), CvId.SCALES);
		if (scale == null) {
			scale = newScale;
		}
		final Term newMethod = new Term(0, "Test Method", "Test Method");
		Term method = this.ontologyDataManager.findTermByName(newMethod.getName(), CvId.METHODS);
		if (method == null) {
			method = newMethod;
		}
		standardVariable.setProperty(property);
		standardVariable.setScale(scale);
		standardVariable.setMethod(method);

		this.ontologyDataManager.addStandardVariable(standardVariable, PROGRAM_UUID);

	}

	@Test
	public void testAddStandardVariableEnumeration() {
		final int standardVariableId = OntologyDataManagerImplTestConstants.CATEGORICAL_VARIABLE_TERM_ID;
		final String name = "Name_" + new Random().nextInt(10000);
		final String description = "Test Valid Value" + new Random().nextInt(10000);
		StandardVariable standardVariable = this.ontologyDataManager.getStandardVariable(standardVariableId, PROGRAM_UUID);
		final Enumeration validValue = new Enumeration(null, name, description, 1);

		this.ontologyDataManager.saveOrUpdateStandardVariableEnumeration(standardVariable, validValue);
		standardVariable = this.ontologyDataManager.getStandardVariable(standardVariableId, PROGRAM_UUID);
		Assert.assertNotNull(standardVariable.getEnumeration(validValue.getId()));

	}

	@Test
	public void testUpdateStandardVariableEnumeration() {
		// Case 1: NEW VALID VALUE
		final int standardVariableId = OntologyDataManagerImplTestConstants.CATEGORICAL_VARIABLE_TERM_ID;
		String name = "Name_" + new Random().nextInt(10000);
		String description = "Test Valid Value" + new Random().nextInt(10000);
		StandardVariable standardVariable = this.ontologyDataManager.getStandardVariable(standardVariableId, PROGRAM_UUID);
		Enumeration validValue = new Enumeration(null, name, description, standardVariable.getEnumerations().size() + 1);

		this.ontologyDataManager.saveOrUpdateStandardVariableEnumeration(standardVariable, validValue);
		standardVariable = this.ontologyDataManager.getStandardVariable(standardVariableId, PROGRAM_UUID);
		final Integer validValueGeneratedId1 = standardVariable.getEnumerationByName(name).getId();
		standardVariable = this.ontologyDataManager.getStandardVariable(standardVariableId, PROGRAM_UUID);
		Assert.assertNotNull(standardVariable.getEnumeration(validValue.getId()));

		// Case 2: UPDATE CENTRAL VALID VALUE
		final Integer validValueId = OntologyDataManagerImplTestConstants.CROP_SESCND_VALID_VALUE_FROM_CENTRAL;
		name = "Name_" + new Random().nextInt(10000);
		description = "Test Valid Value " + new Random().nextInt(10000); // Original
		// value
		// in
		// central:
		// "Moderately well exserted"
		validValue = new Enumeration(validValueId, name, description, 1);
		this.ontologyDataManager.saveOrUpdateStandardVariableEnumeration(standardVariable, validValue);

		standardVariable = this.ontologyDataManager.getStandardVariable(standardVariableId, PROGRAM_UUID);
		Assert.assertNotNull(standardVariable.getEnumeration(validValue.getId()));

		// Case 3: UPDATE LOCAL VALID VALUE
		description = "Test Valid Value " + new Random().nextInt(10000);
		validValue.setDescription(description);
		this.ontologyDataManager.saveOrUpdateStandardVariableEnumeration(standardVariable, validValue);
		standardVariable = this.ontologyDataManager.getStandardVariable(standardVariableId, PROGRAM_UUID);
		Assert.assertTrue(standardVariable.getEnumeration(validValue.getId()).getDescription().equals(description));

		// (*) clean up
		this.ontologyDataManager.deleteStandardVariableEnumeration(standardVariableId, validValueGeneratedId1);
		this.ontologyDataManager.deleteStandardVariableEnumeration(standardVariableId, validValue.getId());

	}

	@Test
	public void testAddMethod() {
		final String name = "Test Method " + new Random().nextInt(10000);
		final String definition = "Test Definition";
		Term term = this.ontologyDataManager.addMethod(name, definition);
		Assert.assertTrue(term.getId() > 0);
		term = this.ontologyDataManager.getTermById(term.getId());
	}

	/**
	 * This tests an expected property of the ontology data manager to return a non empty map, even for entries that cannot be matched to a
	 * standard variable in the database
	 *
	 * @
	 */
	@Test
	public void testGetStandardVariablesInProjectsNonNullEvenIfNotPresent() {

		final String baseVariableName = "TEST_VARIABLE_NAME";
		final Random random = new Random();
		final int testItemCount = random.nextInt(10);

		final List<String> headers = new ArrayList<>(testItemCount);

		for (int i = 0; i < testItemCount; i++) {
			headers.add(baseVariableName + i);
		}

		final Map<String, List<StandardVariable>> results = this.ontologyDataManager.getStandardVariablesInProjects(headers, PROGRAM_UUID);

		Assert.assertNotNull("Application is unable to return non empty output even on non present variables", results);
		Assert
			.assertEquals("Application is unable to return non empty output even on non present variables", testItemCount, results.size());

		for (final String header : headers) {
			Assert.assertTrue(
				"Application is unable to return non empty output for each of the passed header parameters",
				results.containsKey(header));
		}

		for (final Map.Entry<String, List<StandardVariable>> entry : results.entrySet()) {
			final List<StandardVariable> vars = entry.getValue();

			Assert.assertNotNull(
				"Application should give a non null list of standard variables for a given header name, even if not present", vars);
			Assert.assertTrue("Application shouldn't be able to give values for dummy input", vars.isEmpty());
		}
	}

	/**
	 * This tests the ability of the application to retrieve the standard variables associated with known headers
	 *
	 * @
	 */
	@Test
	public void testGetStandardVariablesInProjectsKnownHeaders() {
		final List<String> headers = Arrays.asList(OntologyDataManagerImplTestConstants.COMMON_HEADERS);

		final Map<String, List<StandardVariable>> results = this.ontologyDataManager.getStandardVariablesInProjects(headers, PROGRAM_UUID);

		for (final Map.Entry<String, List<StandardVariable>> entry : results.entrySet()) {
			final List<StandardVariable> variableList = entry.getValue();

			Assert.assertTrue(
				"Application is unable to return a list of standard variables for known existing central headers ",
				variableList.size() > 0);
		}

	}

	/**
	 * This tests the ability of the application to retrieve standard variables for newly created items
	 *
	 * @
	 */
	@Test
	public void testGetStandardVariablesForNewlyCreatedEntries() {
		// set up and create a new standard variable for this test
		final StandardVariable standardVariable = this.createStandardVariable("testVariable");

		final List<String> headers = Arrays.asList(standardVariable.getName());

		final Map<String, List<StandardVariable>> results = this.ontologyDataManager.getStandardVariablesInProjects(headers, PROGRAM_UUID);

		for (final Map.Entry<String, List<StandardVariable>> entry : results.entrySet()) {
			final List<StandardVariable> variableList = entry.getValue();

			Assert.assertTrue(
				"Application is unable to return a proper sized list of standard variables for newly added standard variables",
				variableList.size() == 1);

			final StandardVariable retrieved = variableList.get(0);
			Assert.assertEquals("Application is unable to retrieve the proper standard variable for newly created entries",
				standardVariable.getName(), retrieved.getName());
		}

	}

	@Test
	public void testFindStandardVariablesByNameOrSynonym() {
		Set<StandardVariable> standardVariables =
			this.ontologyDataManager.findStandardVariablesByNameOrSynonym(
				OntologyDataManagerImplTestConstants.TERM_NAME_NOT_EXISTING,
				PROGRAM_UUID);
		Assert.assertSame(standardVariables.size(), 0);

		standardVariables =
			this.ontologyDataManager.findStandardVariablesByNameOrSynonym(
				OntologyDataManagerImplTestConstants.TERM_NAME_IS_IN_SYNONYMS, PROGRAM_UUID);
		Assert.assertSame(standardVariables.size(), 1);
		for (final StandardVariable stdVar : standardVariables) {
			stdVar.print(IntegrationTestBase.INDENT);
		}

		standardVariables =
			this.ontologyDataManager.findStandardVariablesByNameOrSynonym(
				OntologyDataManagerImplTestConstants.TERM_SYNONYM,
				PROGRAM_UUID);
		Assert.assertSame(standardVariables.size(), 1);
		for (final StandardVariable stdVar : standardVariables) {
			stdVar.print(IntegrationTestBase.INDENT);
		}
	}

	@Test
	public void testFindStandardVariablesByNameOrSynonymWithProperties() {
		final Set<StandardVariable> standardVariables =
			this.ontologyDataManager.findStandardVariablesByNameOrSynonym(
				OntologyDataManagerImplTestConstants.TERM_NAME_IS_IN_SYNONYMS, PROGRAM_UUID);
		Assert.assertSame(standardVariables.size(), 1);
		for (final StandardVariable stdVar : standardVariables) {
			stdVar.print(IntegrationTestBase.INDENT);
			final Term term = this.ontologyDataManager.getTermById(stdVar.getId());
			term.print(IntegrationTestBase.INDENT);
		}
	}

	@Test
	public void testFindMethodById() {

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

		// add a method to local
		final String name = "Test Method " + new Random().nextInt(10000);
		final String definition = "Test Definition";
		term = this.ontologyDataManager.addMethod(name, definition);
		// term does exist in local

		term = this.ontologyDataManager.findMethodById(term.getId());
		Assert.assertNotNull(term);
		term.print(IntegrationTestBase.INDENT);
	}

	@Test
	public void testFindMethodByName() {
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
	}

	@Test
	public void testFindStandardVariableByTraitScaleMethodNames() {

		final StandardVariable standardVariable = this.createStandardVariable("testVariable");

		final StandardVariable result =
			this.ontologyDataManager.findStandardVariableByTraitScaleMethodNames(standardVariable.getProperty().getName(),
				standardVariable.getScale().getName(), standardVariable.getMethod().getName(), PROGRAM_UUID);
		Assert.assertNotNull(result);
	}

	@Test
	public void testGetAllTermsByCvId() {
		List<Term> terms = this.ontologyDataManager.getAllTermsByCvId(CvId.METHODS);
		Assert.assertNotNull(terms);

		terms = this.ontologyDataManager.getAllTermsByCvId(CvId.PROPERTIES);
		Assert.assertNotNull(terms);

		terms = this.ontologyDataManager.getAllTermsByCvId(CvId.SCALES);
		Assert.assertNotNull(terms);
	}

	@Test
	public void testCountTermsByCvId() {
		long count = this.ontologyDataManager.countTermsByCvId(CvId.PROPERTIES);
		Assert.assertTrue(count != 0);

		count = this.ontologyDataManager.countTermsByCvId(CvId.SCALES);
		Assert.assertTrue(count != 0);

	}

	@Test
	public void testGetMethodsForTrait() {
		final StandardVariable standardVariable = this.createStandardVariable("testVariable");

		final List<Term> terms = this.ontologyDataManager.getMethodsForTrait(standardVariable.getProperty().getId());

		Assert.assertFalse(terms.isEmpty());

	}

	@Test
	public void testGetScalesForTrait() {
		final StandardVariable standardVariable = this.createStandardVariable("testVariable");

		final List<Term> terms = this.ontologyDataManager.getScalesForTrait(standardVariable.getProperty().getId());

		Assert.assertFalse(terms.isEmpty());
	}

	@Test
	public void testAddTerm() {
		final String name = "Test Method " + new Random().nextInt(10000);
		final String definition = "Test Definition";
		// add a method, should allow insert
		final CvId cvId = CvId.METHODS;
		Term term = this.ontologyDataManager.addTerm(name, definition, cvId);
		Assert.assertNotNull(term);
		Assert.assertTrue(term.getId() > 0);
		term = this.ontologyDataManager.getTermById(term.getId());
	}

	@Test
	public void testFindTermByName() {
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

	}

	@Test
	public void testGetDataTypes() {
		final List<Term> terms = this.ontologyDataManager.getDataTypes();
		Assert.assertNotNull(terms);
	}

	@Test
	public void testGetStandardVariablesInProjects() {
		final String entry = "ENTRY";
		final String entryno = "ENTRYNO";
		final String plot = "PLOT";

		final List<String> headers =
			Arrays.asList(entry, entryno, plot);

		final Map<String, List<StandardVariable>> results = this.ontologyDataManager.getStandardVariablesInProjects(headers, PROGRAM_UUID);

		Assert.assertTrue(results.get(entry).isEmpty());
		Assert.assertFalse(results.get(entryno).isEmpty());
		Assert.assertFalse(results.get(plot).isEmpty());
	}

	@Test
	public void testFindTermsByNameOrSynonym() {
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

		// name is in synonyms
		terms =
			this.ontologyDataManager.findTermsByNameOrSynonym(
				OntologyDataManagerImplTestConstants.TERM_NAME_IS_IN_SYNONYMS,
				CvId.VARIABLES);
		Assert.assertNotNull(terms);
		terms.get(0).print(IntegrationTestBase.INDENT);

		// name is both in term and in synonyms
		// need to modify the entry in cvterm where name = "Cooperator" to have
		// cv_id = 1010
		terms = this.ontologyDataManager.findTermsByNameOrSynonym("Cooperator", CvId.PROPERTIES);
		Assert.assertNotNull(terms);
		for (final Term term : terms) {
			term.print(IntegrationTestBase.INDENT);
		}

	}

	@Test
	public void testCountIsAOfProperties() {
		final long asOf = this.ontologyDataManager.countIsAOfProperties();
		Assert.assertTrue(asOf != 0);
	}

	@Test
	public void testAddProperty() {
		final int isA = 1087;
		final Term term =
			this.ontologyDataManager.addProperty(RandomStringUtils.randomAlphanumeric(10), RandomStringUtils.randomAlphanumeric(10), isA);
		Assert.assertNotNull(term);

	}

	@Test
	public void testGetProperty() {
		final int isA = 1087;
		final Term term =
			this.ontologyDataManager.addProperty(RandomStringUtils.randomAlphanumeric(10), RandomStringUtils.randomAlphanumeric(10), isA);
		final Property property = this.ontologyDataManager.getProperty(term.getId());
		Assert.assertNotNull(property);

	}

	@Test
	public void testGetAllTraitGroupsHierarchy() {
		final List<TraitClassReference> traitGroups = this.ontologyDataManager.getAllTraitGroupsHierarchy(true);
		for (final TraitClassReference traitGroup : traitGroups) {
			traitGroup.print(IntegrationTestBase.INDENT);
		}
	}

	@Test
	public void testGetPropertyByName() {
		final String name = "Season";
		final Property property = this.ontologyDataManager.getProperty(name);
	}

	@Test
	@Ignore(value = "Skipping this test. It takes minute+ to run and does not have any assertions anyway. Needs revising.")
	public void testGetAllStandardVariable() {
		final Set<StandardVariable> standardVariables = this.ontologyDataManager.getAllStandardVariables(PROGRAM_UUID);
		for (final StandardVariable stdVar : standardVariables) {
			stdVar.print(IntegrationTestBase.INDENT);
		}
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
	}

	@Test
	public void testAddOrUpdateTermAndRelationship() {
		final String name = "Study condition NEW";
		final String definition = "Study condition NEW class " + (int) (Math.random() * 100);
		final Term origTerm = this.ontologyDataManager.findTermByName(name, CvId.PROPERTIES);
		final Term newTerm =
			this.ontologyDataManager.addOrUpdateTermAndRelationship(name, definition, CvId.PROPERTIES, TermId.IS_A.getId(),
				OntologyDataManagerImplTestConstants.OBJECT_ID);

		if (origTerm != null) { // if the operation is update, the ids must be
			// same
			Assert.assertEquals(origTerm.getId(), newTerm.getId());
		}
	}

	@Test
	public void testUpdateTermAndRelationship() {
		final String name = "Slope NEW";
		final String definition = "Slope NEW class " + (int) (Math.random() * 100);
		Term origTerm = this.ontologyDataManager.findTermByName(name, CvId.PROPERTIES);
		if (origTerm == null) { // first run, add before update
			origTerm =
				this.ontologyDataManager.addOrUpdateTermAndRelationship(name, definition, CvId.PROPERTIES, TermId.IS_A.getId(),
					OntologyDataManagerImplTestConstants.OBJECT_ID);
		}

		this.ontologyDataManager.updateTermAndRelationship(new Term(origTerm.getId(), name, definition), TermId.IS_A.getId(),
			OntologyDataManagerImplTestConstants.OBJECT_ID);
		final Term newTerm = this.ontologyDataManager.findTermByName(name, CvId.PROPERTIES);

		if (newTerm != null) {
			Assert.assertTrue(newTerm.getDefinition().equals(definition));
		}
	}

	@Test
	public void testAddOrUpdateTerm() {
		final String name = "Real";
		// add random number to see the update
		final String definition = "Real Description NEW " + (int) (Math.random() * 100);
		final Term origTerm = this.ontologyDataManager.findTermByName(name, CvId.SCALES);
		final Term newTerm = this.ontologyDataManager.addOrUpdateTerm(name, definition, CvId.SCALES);

		if (origTerm != null) { // if the operation is update, the ids must be same
			Assert.assertEquals(origTerm.getId(), newTerm.getId());
		}
	}

	@Test
	public void testUpdateTerm() {
		final String name = "Integer2";
		// add random number to see the update
		final String definition = "Integer NEW " + (int) (Math.random() * 100);

		Term origTerm = this.ontologyDataManager.findTermByName(name, CvId.SCALES);
		if (origTerm == null) { // first run, add before update
			origTerm = this.ontologyDataManager.addTerm(name, definition, CvId.SCALES);
		}

		this.ontologyDataManager.updateTerm(new Term(origTerm.getId(), name, definition));
		final Term newTerm = this.ontologyDataManager.findTermByName(name, CvId.SCALES);

		if (origTerm != null && newTerm != null) {
			Assert.assertTrue(newTerm.getDefinition().equals(definition));
		}
	}

	@Test
	public void testUpdateTerms() {

		final String termName1 = "Sample Term 1";
		final String termName2 = "Sample Term 2";
		final String termDescription1 = "Sample Term Description 1";
		final String termDescription2 = "Sample Term Description 2";

		final Term term1 = this.ontologyDataManager.addTerm(termName1, termDescription1, CvId.SCALES);
		final Term term2 = this.ontologyDataManager.addTerm(termName2, termDescription2, CvId.SCALES);

		// Update the added terms name with new names.
		final List<Term> terms = new ArrayList<>();
		term1.setName("New Term Name 1");
		term2.setName("New Term Name 2");
		terms.add(term1);
		terms.add(term2);

		this.ontologyDataManager.updateTerms(terms);

		final Term newTerm1 = this.ontologyDataManager.getTermById(term1.getId());
		final Term newTerm2 = this.ontologyDataManager.getTermById(term2.getId());

		Assert.assertEquals("The term's name should be updated", "New Term Name 1", newTerm1.getName());
		Assert.assertEquals("The term's name should be updated", "New Term Name 2", newTerm2.getName());

	}

	@Test
	public void testGetStandardVariableIdByTermId() {

		final StandardVariable standardVariable = this.createStandardVariable("testVariable");

		final Integer stdVariableId =
			this.ontologyDataManager.getStandardVariableIdByTermId(standardVariable.getProperty().getId(), TermId.HAS_PROPERTY);

		Assert.assertNotNull(stdVariableId);
	}

	@Test
	public void testDeleteTerm() {
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
	public void testDeleteTermAndRelationship() {
		String name = "Test Property" + new Random().nextInt(10000);
		String definition = "Property Definition";
		final int isA = 1087;

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

	@Test(expected = MiddlewareQueryException.class)
	public void testDeleteParentChildTerms() {
		final String parentTermName = "Parent Test Trait Class" + new Random().nextInt(10000);
		final String parentTermDef = parentTermName + " Definition";
		final Term termParent =
			this.ontologyDataManager.addTraitClass(parentTermName, parentTermDef, TermId.ONTOLOGY_TRAIT_CLASS.getId()).getTerm();

		final String childTermName = "Child Test Trait Class" + new Random().nextInt(10000);
		final String childTermDef = childTermName + " Definition";
		this.ontologyDataManager.addTraitClass(childTermName, childTermDef, termParent.getId()).getTerm();

		this.ontologyDataManager.deleteTermAndRelationship(termParent.getId(), CvId.IBDB_TERMS, TermId.IS_A.getId(),
			TermId.ONTOLOGY_TRAIT_CLASS.getId());

	}

	@Test
	public void testGetAllPropertiesWithTraitClass() {
		final List<Property> properties = this.ontologyDataManager.getAllPropertiesWithTraitClass();
		Assert.assertFalse(properties.isEmpty());
	}

	@Test
	@Ignore(value = "This test is failing and it seems it's due to a bug in deleteStandardVariable. Skipping this test.")
	public void testDeleteStandardVariable() {
		final StandardVariable standardVariable = this.createStandardVariable("testVariable");

		this.ontologyDataManager.deleteStandardVariable(standardVariable.getId());

		// Check that the variable got deleted
		final Term term = this.ontologyDataManager.getTermById(standardVariable.getId());
		Assert.assertNull("Expected the standard variable deleted but it is still there!", term);
	}

	@Test
	public void testGetCVIdByName() throws MiddlewareQueryException {
		final Integer cvId = this.ontologyDataManager.getCVIdByName("Variables");
		Assert.assertEquals(1040, cvId.intValue());
	}

	private StandardVariable createStandardVariable(final String name) {

		final CVTerm property = this.cvTermDao.save(RandomStringUtils.randomAlphanumeric(10), "", CvId.PROPERTIES);
		final CVTerm scale = this.cvTermDao.save(RandomStringUtils.randomAlphanumeric(10), "", CvId.SCALES);
		final CVTerm method = this.cvTermDao.save(RandomStringUtils.randomAlphanumeric(10), "", CvId.METHODS);
		final CVTerm numericDataType = this.cvTermDao.getById(DataType.NUMERIC_VARIABLE.getId());

		final StandardVariable standardVariable = new StandardVariable();
		standardVariable.setName(name);
		standardVariable.setProperty(new Term(property.getCvTermId(), property.getName(), property.getDefinition()));
		standardVariable.setScale(new Term(scale.getCvTermId(), scale.getName(), scale.getDefinition()));
		standardVariable.setMethod(new Term(method.getCvTermId(), method.getName(), method.getDefinition()));
		standardVariable.setDataType(new Term(numericDataType.getCvTermId(), numericDataType.getName(), numericDataType.getDefinition()));
		this.ontologyDataManager.addStandardVariable(standardVariable, PROGRAM_UUID);

		return standardVariable;
	}
}
