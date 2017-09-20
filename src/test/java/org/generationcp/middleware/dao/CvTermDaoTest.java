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

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.oms.CVDao;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.dao.oms.CVTermRelationshipDao;
import org.generationcp.middleware.dao.oms.CvTermSynonymDao;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.pojos.oms.CV;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.generationcp.middleware.pojos.oms.CVTermSynonym;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CvTermDaoTest extends IntegrationTestBase {

	private static CVDao cvDao;
	private static CVTermDao dao;
	private static CVTermRelationshipDao cvTermRelationshipDao;
	private static CvTermSynonymDao cvTermSynonymDao;

	@Before
	public void setUp() throws Exception {
		cvDao = new CVDao();
		cvDao.setSession(this.sessionProvder.getSession());
		dao = new CVTermDao();
		dao.setSession(this.sessionProvder.getSession());
		cvTermRelationshipDao = new CVTermRelationshipDao();
		cvTermRelationshipDao.setSession(this.sessionProvder.getSession());
		cvTermSynonymDao = new CvTermSynonymDao();
		cvTermSynonymDao.setSession(this.sessionProvder.getSession());

	}

	@Test
	public void testGetByName() throws Exception {

		CV cv = new CV();
		cv.setName("TestCV");
		cv.setDefinition("Test CV description");

		cvDao.save(cv);

		CVTerm term = new CVTerm();
		term.setName("Test term");
		term.setDefinition("Test description");
		term.setCv(cv.getCvId());
		term.setIsObsolete(false);
		term.setIsRelationshipType(false);

		dao.save(term);

		CVTerm cvTerm = dao.getByName(term.getName());

		Assert.assertNotNull(cvTerm);
		Assert.assertEquals(term.getCv(), cvTerm.getCv());
		Assert.assertEquals(term.getName(), cvTerm.getName());
		Assert.assertEquals(term.getDefinition(), cvTerm.getDefinition());
	}

	@Test
	public void testGetByNameAndCvId() throws Exception {

		CV cv = new CV();
		cv.setName("TestCV");
		cv.setDefinition("Test CV description");

		cvDao.save(cv);

		CVTerm term = new CVTerm();
		term.setName("Test term");
		term.setDefinition("Test description");
		term.setCv(cv.getCvId());
		term.setIsObsolete(false);
		term.setIsRelationshipType(false);

		dao.save(term);

		CVTerm cvTerm = dao.getByNameAndCvId(term.getName(), cv.getCvId());

		Assert.assertNotNull(cvTerm);
		Assert.assertEquals(term.getCv(), cvTerm.getCv());
		Assert.assertEquals(term.getName(), cvTerm.getName());
		Assert.assertEquals(term.getDefinition(), cvTerm.getDefinition());
	}

	@Test
	public void testGetStandardVariableIdsWithTypeByProperties() {

		final CVTerm standardVariableTerm = this.createTerm("testTerm1", CvId.VARIABLES.getId());
		final CVTerm propertyTerm = this.createTerm("testTermProperty", CvId.PROPERTIES.getId());
		dao.save(standardVariableTerm);
		dao.save(propertyTerm);
		cvTermRelationshipDao.save(standardVariableTerm.getCvTermId(), TermId.HAS_PROPERTY.getId(), propertyTerm.getCvTermId());

		CVTermSynonym cvTermSynonym = new CVTermSynonym();
		cvTermSynonym.setSynonym(propertyTerm.getName());
		cvTermSynonym.setCvTermId(propertyTerm.getCvTermId());
		cvTermSynonymDao.save(cvTermSynonym);

		List<String> propertyNameOrSynonyms = new ArrayList<>();
		propertyNameOrSynonyms.add(propertyTerm.getName());

		Map<String, Map<Integer, VariableType>> result = dao.getStandardVariableIdsWithTypeByProperties(propertyNameOrSynonyms);

		final String propertyNameKey = propertyTerm.getName().toUpperCase();

		Assert.assertTrue(String.format("Expecting property name '{0}' in the result map", propertyNameKey),
				result.containsKey(propertyNameKey));
		Assert.assertTrue(
				String.format("The property name '{0}' should return the ID of standard variable associated to it.", propertyNameKey),
				result.get(propertyNameKey).containsKey(standardVariableTerm.getCvTermId()));

	}

	@Test
	public void testGetStandardVariableIdsWithTypeByPropertiesVariableIsObsolete() {

		final CVTerm standardVariableTerm = this.createTerm("testTerm1", CvId.VARIABLES.getId());
		standardVariableTerm.setIsObsolete(true);
		final CVTerm propertyTerm = this.createTerm("testTermProperty", CvId.PROPERTIES.getId());
		dao.save(standardVariableTerm);
		dao.save(propertyTerm);
		cvTermRelationshipDao.save(standardVariableTerm.getCvTermId(), TermId.HAS_PROPERTY.getId(), propertyTerm.getCvTermId());

		CVTermSynonym cvTermSynonym = new CVTermSynonym();
		cvTermSynonym.setSynonym(propertyTerm.getName());
		cvTermSynonym.setCvTermId(propertyTerm.getCvTermId());
		cvTermSynonymDao.save(cvTermSynonym);

		List<String> propertyNameOrSynonyms = new ArrayList<>();
		propertyNameOrSynonyms.add(propertyTerm.getName());

		Map<String, Map<Integer, VariableType>> result = dao.getStandardVariableIdsWithTypeByProperties(propertyNameOrSynonyms);

		// The standard variable term is obsolete so the result should be empty
		Assert.assertTrue(result.isEmpty());

	}

	@Test
	public void testGetTermIdsWithTypeByNameOrSynonyms() {

		final String standardVariableName = "testTerm1";
		final String variableSynonym = "testTermSynonym";

		final CVTerm standardVariableTerm = this.createTerm(standardVariableName, CvId.VARIABLES.getId());
		standardVariableTerm.setIsObsolete(false);
		dao.save(standardVariableTerm);

		CVTermSynonym cvTermSynonym = new CVTermSynonym();
		cvTermSynonym.setSynonym(variableSynonym);
		cvTermSynonym.setCvTermId(standardVariableTerm.getCvTermId());
		cvTermSynonymDao.save(cvTermSynonym);

		List<String> namesOrSynonyms = new ArrayList<>();
		namesOrSynonyms.add(standardVariableName);
		namesOrSynonyms.add(variableSynonym);

		Map<String, Map<Integer, VariableType>> result = dao.getTermIdsWithTypeByNameOrSynonyms(namesOrSynonyms, CvId.VARIABLES.getId());

		final String variableNameKey = standardVariableTerm.getName().toUpperCase();
		final String synonymNameKey = variableSynonym.toUpperCase();

		Assert.assertTrue(String.format("Expecting variable name '{0}' in the result map", variableNameKey),
				result.containsKey(variableNameKey));
		Assert.assertTrue(String.format("The variable '{0}' should return a variable id {1}", standardVariableTerm.getName(),
				standardVariableTerm.getCvTermId()), result.get(variableNameKey).containsKey(standardVariableTerm.getCvTermId()));
		Assert.assertTrue(String.format("Expecting synonym name '{0}' in the result map", synonymNameKey),
				result.containsKey(synonymNameKey));
		Assert.assertTrue(
				String.format("The synonym name '{0}' should return the ID of standard variable associated to it.", synonymNameKey),
				result.get(synonymNameKey).containsKey(standardVariableTerm.getCvTermId()));

	}

	@Test
	public void testGetTermIdsWithTypeByNameOrSynonymsVariableIsObsolete() {

		final String standardVariableName = "testTerm1";
		final String variableSynonym = "testTermSynonym";

		final CVTerm variableTerm = this.createTerm(standardVariableName, CvId.VARIABLES.getId());
		variableTerm.setIsObsolete(true);
		dao.save(variableTerm);

		CVTermSynonym cvTermSynonym = new CVTermSynonym();
		cvTermSynonym.setSynonym(variableSynonym);
		cvTermSynonym.setCvTermId(variableTerm.getCvTermId());
		cvTermSynonymDao.save(cvTermSynonym);

		List<String> namesOrSynonyms = new ArrayList<>();
		namesOrSynonyms.add(standardVariableName);
		namesOrSynonyms.add(variableSynonym);

		Map<String, Map<Integer, VariableType>> result = dao.getTermIdsWithTypeByNameOrSynonyms(namesOrSynonyms, CvId.VARIABLES.getId());

		// The standard variable term is obsolete so the result should be empty
		Assert.assertTrue(result.isEmpty());

	}

	private CVTerm createTerm(final String name, final Integer cvId) {

		CVTerm term = new CVTerm();
		term.setName(name);
		term.setDefinition("Test description");
		term.setCv(cvId);
		term.setIsObsolete(false);
		term.setIsRelationshipType(false);

		return term;
	}

}
