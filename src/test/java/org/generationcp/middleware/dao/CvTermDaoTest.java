/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.middleware.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.oms.CVDao;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.dao.oms.CVTermRelationshipDao;
import org.generationcp.middleware.dao.oms.CvTermSynonymDao;
import org.generationcp.middleware.data.initializer.CVTermTestDataInitializer;
import org.generationcp.middleware.data.initializer.StandardVariableTestDataInitializer;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.pojos.oms.CV;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermSynonym;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CvTermDaoTest extends IntegrationTestBase {

	private static CVDao cvDao;
	private static CVTermDao cvTermDao;
	private static CVTermRelationshipDao cvTermRelationshipDao;
	private static CvTermSynonymDao cvTermSynonymDao;

	private DaoFactory daoFactory;

	@Autowired
	private OntologyDataManager ontologyDataManager;

	@Before
	public void setUp() throws Exception {
		daoFactory = new DaoFactory(this.sessionProvder);
		CvTermDaoTest.cvTermDao = this.daoFactory.getCvTermDao();
		CvTermDaoTest.cvTermRelationshipDao = this.daoFactory.getCvTermRelationshipDao();

		CvTermDaoTest.cvDao = new CVDao(this.sessionProvder.getSession());
		CvTermDaoTest.cvTermSynonymDao = new CvTermSynonymDao(this.sessionProvder.getSession());
	}

	@Test
	public void testGetByName() throws Exception {

		final CV cv = new CV();
		cv.setName("TestCV");
		cv.setDefinition("Test CV description");

		CvTermDaoTest.cvDao.save(cv);

		final CVTerm term = new CVTerm();
		term.setName("Test term");
		term.setDefinition("Test description");
		term.setCv(cv.getCvId());
		term.setIsObsolete(false);
		term.setIsRelationshipType(false);
		term.setIsSystem(false);
		CvTermDaoTest.cvTermDao.save(term);

		final CVTerm cvTerm = CvTermDaoTest.cvTermDao.getByName(term.getName());

		Assert.assertNotNull(cvTerm);
		Assert.assertEquals(term.getCv(), cvTerm.getCv());
		Assert.assertEquals(term.getName(), cvTerm.getName());
		Assert.assertEquals(term.getDefinition(), cvTerm.getDefinition());
	}

	@Test
	public void testGetVariablesByTypeForNumericVariable() throws Exception {
		final int previousVariablesSize = CvTermDaoTest.cvTermDao
				.getVariablesByType(Arrays.asList(TermId.NUMERIC_VARIABLE.getId())).size();

		// Add Numeric Variable
		final Term propertyTerm = this.ontologyDataManager.findTermByName("Grain yield", CvId.PROPERTIES);
		final Term scaleTerm = this.ontologyDataManager.findTermByName("g", CvId.SCALES);
		final Term methodTerm = this.ontologyDataManager.findTermByName("Counting", CvId.METHODS);
		final Term dataType = this.ontologyDataManager.getTermById(TermId.NUMERIC_VARIABLE.getId());
		final StandardVariable standardVariable = StandardVariableTestDataInitializer
				.createStandardVariable(propertyTerm, scaleTerm, methodTerm, dataType);
		this.ontologyDataManager.addStandardVariable(standardVariable, UUID.randomUUID().toString());

		final int variablesSize = CvTermDaoTest.cvTermDao.getVariablesByType(Arrays.asList(TermId.NUMERIC_VARIABLE.getId()))
				.size();
		Assert.assertEquals(previousVariablesSize + 1, variablesSize);
	}

	@Test
	public void testGetVariablesByTypeForCharacterVariable() throws Exception {
		final int previousVariablesSize = CvTermDaoTest.cvTermDao
				.getVariablesByType(Arrays.asList(TermId.CHARACTER_VARIABLE.getId())).size();

		// Add Character Variable
		final Term propertyTerm = this.ontologyDataManager.findTermByName("Grain yield", CvId.PROPERTIES);
		final Term scaleTerm = this.ontologyDataManager.findTermByName("Text", CvId.SCALES);
		final Term methodTerm = this.ontologyDataManager.findTermByName("Counting", CvId.METHODS);
		final Term dataType = this.ontologyDataManager.getTermById(TermId.CHARACTER_VARIABLE.getId());
		final StandardVariable standardVariable = StandardVariableTestDataInitializer
				.createStandardVariable(propertyTerm, scaleTerm, methodTerm, dataType);
		this.ontologyDataManager.addStandardVariable(standardVariable, UUID.randomUUID().toString());

		final int variablesSize = CvTermDaoTest.cvTermDao.getVariablesByType(Arrays.asList(TermId.CHARACTER_VARIABLE.getId()))
				.size();
		Assert.assertEquals(previousVariablesSize + 1, variablesSize);
	}

	@Test
	public void testGetByNameAndCvId() throws Exception {

		final CV cv = new CV();
		cv.setName("TestCV");
		cv.setDefinition("Test CV description");

		CvTermDaoTest.cvDao.save(cv);

		final CVTerm term = new CVTerm();
		term.setName("Test term");
		term.setDefinition("Test description");
		term.setCv(cv.getCvId());
		term.setIsObsolete(false);
		term.setIsRelationshipType(false);
		term.setIsSystem(false);
		CvTermDaoTest.cvTermDao.save(term);

		final CVTerm cvTerm = CvTermDaoTest.cvTermDao.getByNameAndCvId(term.getName(), cv.getCvId());

		Assert.assertNotNull(cvTerm);
		Assert.assertEquals(term.getCv(), cvTerm.getCv());
		Assert.assertEquals(term.getName(), cvTerm.getName());
		Assert.assertEquals(term.getDefinition(), cvTerm.getDefinition());
	}

	@Test
	public void testGetStandardVariableIdsWithTypeByProperties() {

		final CVTerm standardVariableTerm = CVTermTestDataInitializer.createTerm("testTerm1", CvId.VARIABLES.getId());
		final CVTerm propertyTerm = CVTermTestDataInitializer.createTerm("testTermProperty", CvId.PROPERTIES.getId());
		CvTermDaoTest.cvTermDao.save(standardVariableTerm);
		CvTermDaoTest.cvTermDao.save(propertyTerm);
		CvTermDaoTest.cvTermRelationshipDao.save(standardVariableTerm.getCvTermId(), TermId.HAS_PROPERTY.getId(),
				propertyTerm.getCvTermId());

		final CVTermSynonym cvTermSynonym = new CVTermSynonym();
		cvTermSynonym.setSynonym(propertyTerm.getName());
		cvTermSynonym.setCvTermId(propertyTerm.getCvTermId());
		CvTermDaoTest.cvTermSynonymDao.save(cvTermSynonym);

		final List<String> propertyNameOrSynonyms = new ArrayList<>();
		propertyNameOrSynonyms.add(propertyTerm.getName());

		final Map<String, Map<Integer, VariableType>> result = CvTermDaoTest.cvTermDao
				.getStandardVariableIdsWithTypeByProperties(propertyNameOrSynonyms);

		final String propertyNameKey = propertyTerm.getName().toUpperCase();

		Assert.assertTrue(String.format("Expecting property name '{0}' in the result map", propertyNameKey),
				result.containsKey(propertyNameKey));
		Assert.assertTrue(
				String.format("The property name '{0}' should return the ID of standard variable associated to it.",
						propertyNameKey),
				result.get(propertyNameKey).containsKey(standardVariableTerm.getCvTermId()));

	}

	@Test
	public void testGetStandardVariableIdsWithTypeByPropertiesVariableIsObsolete() {

		final CVTerm standardVariableTerm = CVTermTestDataInitializer.createTerm("testTerm1", CvId.VARIABLES.getId());
		standardVariableTerm.setIsObsolete(true);
		final CVTerm propertyTerm = CVTermTestDataInitializer.createTerm("testTermProperty", CvId.PROPERTIES.getId());
		CvTermDaoTest.cvTermDao.save(standardVariableTerm);
		CvTermDaoTest.cvTermDao.save(propertyTerm);
		CvTermDaoTest.cvTermRelationshipDao.save(standardVariableTerm.getCvTermId(), TermId.HAS_PROPERTY.getId(),
				propertyTerm.getCvTermId());

		final CVTermSynonym cvTermSynonym = new CVTermSynonym();
		cvTermSynonym.setSynonym(propertyTerm.getName());
		cvTermSynonym.setCvTermId(propertyTerm.getCvTermId());
		CvTermDaoTest.cvTermSynonymDao.save(cvTermSynonym);

		final List<String> propertyNameOrSynonyms = new ArrayList<>();
		propertyNameOrSynonyms.add(propertyTerm.getName());

		final Map<String, Map<Integer, VariableType>> result = CvTermDaoTest.cvTermDao
				.getStandardVariableIdsWithTypeByProperties(propertyNameOrSynonyms);

		// The standard variable term is obsolete so the result should be empty
		Assert.assertTrue(result.isEmpty());

	}

	@Test
	public void testGetTermIdsWithTypeByNameOrSynonyms() {

		final String standardVariableName = "testTerm1";
		final String variableSynonym = "testTermSynonym";

		final CVTerm standardVariableTerm = CVTermTestDataInitializer.createTerm(standardVariableName,
				CvId.VARIABLES.getId());
		standardVariableTerm.setIsObsolete(false);
		CvTermDaoTest.cvTermDao.save(standardVariableTerm);

		final CVTermSynonym cvTermSynonym = new CVTermSynonym();
		cvTermSynonym.setSynonym(variableSynonym);
		cvTermSynonym.setCvTermId(standardVariableTerm.getCvTermId());
		CvTermDaoTest.cvTermSynonymDao.save(cvTermSynonym);

		final List<String> namesOrSynonyms = new ArrayList<>();
		namesOrSynonyms.add(standardVariableName);
		namesOrSynonyms.add(variableSynonym);

		final Map<String, Map<Integer, VariableType>> result = CvTermDaoTest.cvTermDao
				.getTermIdsWithTypeByNameOrSynonyms(namesOrSynonyms, CvId.VARIABLES.getId());

		final String variableNameKey = standardVariableTerm.getName().toUpperCase();
		final String synonymNameKey = variableSynonym.toUpperCase();

		Assert.assertTrue(String.format("Expecting variable name '{0}' in the result map", variableNameKey),
				result.containsKey(variableNameKey));
		Assert.assertTrue(
				String.format("The variable '{0}' should return a variable id {1}", standardVariableTerm.getName(),
						standardVariableTerm.getCvTermId()),
				result.get(variableNameKey).containsKey(standardVariableTerm.getCvTermId()));
		Assert.assertTrue(String.format("Expecting synonym name '{0}' in the result map", synonymNameKey),
				result.containsKey(synonymNameKey));
		Assert.assertTrue(
				String.format("The synonym name '{0}' should return the ID of standard variable associated to it.",
						synonymNameKey),
				result.get(synonymNameKey).containsKey(standardVariableTerm.getCvTermId()));

	}

	@Test
	public void testGetTermIdsWithTypeByNameOrSynonymsVariableIsObsolete() {

		final String standardVariableName = "testTerm1";
		final String variableSynonym = "testTermSynonym";

		final CVTerm variableTerm = CVTermTestDataInitializer.createTerm(standardVariableName, CvId.VARIABLES.getId());
		variableTerm.setIsObsolete(true);
		CvTermDaoTest.cvTermDao.save(variableTerm);

		final CVTermSynonym cvTermSynonym = new CVTermSynonym();
		cvTermSynonym.setSynonym(variableSynonym);
		cvTermSynonym.setCvTermId(variableTerm.getCvTermId());
		CvTermDaoTest.cvTermSynonymDao.save(cvTermSynonym);

		final List<String> namesOrSynonyms = new ArrayList<>();
		namesOrSynonyms.add(standardVariableName);
		namesOrSynonyms.add(variableSynonym);

		final Map<String, Map<Integer, VariableType>> result = CvTermDaoTest.cvTermDao
				.getTermIdsWithTypeByNameOrSynonyms(namesOrSynonyms, CvId.VARIABLES.getId());

		// The standard variable term is obsolete so the result should be empty
		Assert.assertTrue(result.isEmpty());

	}
}
