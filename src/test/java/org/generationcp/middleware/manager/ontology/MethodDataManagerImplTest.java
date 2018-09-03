/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p>
 * Generation Challenge Programme (GCP)
 * <p>
 * <p>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.middleware.manager.ontology;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.dao.oms.CVTermRelationshipDao;
import org.generationcp.middleware.dao.oms.CvTermPropertyDao;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.Method;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.util.ISO8601DateParser;
import org.generationcp.middleware.utils.test.UnitTestDaoIDGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Unit test class to test Ontology Method Data Manger
 */
public class MethodDataManagerImplTest {

	@Mock
	private CVTermDao cvTermDao;

	@Mock
	private CvTermPropertyDao cvTermPropertyDao;

	@Mock
	private CVTermRelationshipDao cvTermRelationshipDao;

	@Mock
	private DaoFactory daoFactory;

	private OntologyMethodDataManagerImpl methodDataManager;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.methodDataManager = new OntologyMethodDataManagerImpl(this.daoFactory);
		Mockito.when(this.daoFactory.getCvTermDao()).thenReturn(this.cvTermDao);
		Mockito.when(this.daoFactory.getCvTermPropertyDao()).thenReturn(this.cvTermPropertyDao);
		Mockito.when(this.daoFactory.getCvTermRelationshipDao()).thenReturn(this.cvTermRelationshipDao);
	}

	/*
	 * This test mock 3 terms with cv as Method along with additional properties like Created Date and Last Modified Date
	 * and ensures that GetAllMethods function will return full method object.
	 * */
	@Test
	public void testGetAllMethodsShouldFetchAndVerify() throws Exception {

		List<CVTerm> methodTerms = new ArrayList<>();
		// Fill 3 methods using TestDataHelper
		TestDataHelper.fillTestMethodsCvTerms(methodTerms, 3);

		Map<Integer, CVTerm> termMap = new HashMap<>();
		// save 3 methods using dao
		for (CVTerm term : methodTerms) {
			termMap.put(term.getCvTermId(), term);
		}

		// Fill Test Created Date Property using Calendar
		Calendar cal = Calendar.getInstance();
		cal.set(2015, Calendar.JANUARY, 1);
		Date testCreatedDate = cal.getTime();
		List<CVTermProperty> methodCreatedDateProperties = new ArrayList<>();
		TestDataHelper.fillTestCreatedDateProperties(methodTerms, methodCreatedDateProperties, testCreatedDate);

		Map<Integer, String> createDateMap = new HashMap<>();
		for (CVTermProperty property : methodCreatedDateProperties) {
			createDateMap.put(property.getCvTermId(), property.getValue());
		}

		// Fill Test Updated Date Property using Calendar
		cal.set(2015, Calendar.MAY, 20);
		Date testUpdatedDate = cal.getTime();
		List<CVTermProperty> methodUpdatedDateProperties = new ArrayList<>();
		TestDataHelper.fillTestUpdatedDateProperties(methodTerms, methodUpdatedDateProperties, testUpdatedDate);

		Map<Integer, String> updateDateMap = new HashMap<>();
		for (CVTermProperty property : methodUpdatedDateProperties) {
			updateDateMap.put(property.getCvTermId(), property.getValue());
		}

		Mockito.when(this.cvTermDao.getAllByCvId(CvId.METHODS, true)).thenReturn(methodTerms);

		List<CVTermProperty> combinedProperties = new ArrayList<>(methodCreatedDateProperties);
		combinedProperties.addAll(methodUpdatedDateProperties);

		Mockito.when(this.cvTermPropertyDao.getByCvTermIds(new ArrayList<>(termMap.keySet()))).thenReturn(combinedProperties);

		// Fetch all methods and check our last inserted method exists or not
		List<Method> methods = this.methodDataManager.getAllMethods();

		// Iterate all methods and find our inserted method and assert it
		for (Method m : methods) {
			// Make sure our method exists and is inserted properly and display proper message if it is not inserted properly
			String message = "The %s for method '" + m.getId() + "' was not added correctly.";
			if (termMap.containsKey(m.getId())) {
				CVTerm methodTerm = termMap.get(m.getId());
				String createdDateProperty = createDateMap.get(m.getId());
				String updatedDateProperty = updateDateMap.get(m.getId());

				Assert.assertEquals(String.format(message, "Name"), methodTerm.getName(), m.getName());
				Assert.assertEquals(String.format(message, "Definition"), methodTerm.getDefinition(), m.getDefinition());
				Assert.assertEquals(String.format(message, "IsObsolete"), methodTerm.isObsolete(), m.isObsolete());
				Assert.assertEquals(String.format(message, "CreatedDate"), createdDateProperty,
						ISO8601DateParser.toString(m.getDateCreated()));
				Assert.assertEquals(String.format(message, "UpdatedDate"), updatedDateProperty,
						ISO8601DateParser.toString(m.getDateLastModified()));

			}
		}
	}

	/*
	 * This test mock one term with cv as Method along with additional properties like Created Date and Last Modified Date
	 * and ensures that GetMethodById function will return full method object.
	 * */
	@Test
	public void testGetMethodByIdShouldFetchAndVerify() throws Exception {
		CVTerm methodTerm = TestDataHelper.getTestCvTerm(CvId.METHODS);

		// Fill Test Created Date Property using Calendar
		Calendar cal = Calendar.getInstance();
		cal.set(2015, Calendar.JANUARY, 1);
		Date testCreatedDate = cal.getTime();
		List<CVTermProperty> methodCreatedDateProperties = new ArrayList<>();
		TestDataHelper.fillTestCreatedDateProperties(Collections.singletonList(methodTerm), methodCreatedDateProperties, testCreatedDate);

		// Fill Test Updated Date Property using Calendar
		cal.set(2015, Calendar.MAY, 20);
		Date testUpdatedDate = cal.getTime();
		List<CVTermProperty> methodUpdatedDateProperties = new ArrayList<>();
		TestDataHelper.fillTestUpdatedDateProperties(Collections.singletonList(methodTerm), methodUpdatedDateProperties, testUpdatedDate);

		Mockito.when(this.cvTermDao.getAllByCvId(Collections.singletonList(methodTerm.getCvTermId()), CvId.METHODS, true))
		.thenReturn(Collections.singletonList(methodTerm));
		List<CVTermProperty> combinedProperties = new ArrayList<>(methodCreatedDateProperties);
		combinedProperties.addAll(methodUpdatedDateProperties);
		Mockito.when(this.cvTermPropertyDao.getByCvTermIds(Collections.singletonList(methodTerm.getCvTermId())))
		.thenReturn(combinedProperties);

		Method method = this.methodDataManager.getMethod(methodTerm.getCvTermId(), true);

		// Make sure each method data inserted properly, assert them and display proper message
		String message = "The %s for method '" + method.getId() + "' was not added correctly.";
		Assert.assertEquals(String.format(message, "Name"), methodTerm.getName(), method.getName());
		Assert.assertEquals(String.format(message, "Definition"), methodTerm.getDefinition(), method.getDefinition());
		Assert.assertEquals(String.format(message, "IsObsolete"), methodTerm.isObsolete(), method.isObsolete());
		Assert.assertEquals(String.format(message, "CreatedDate"), method.getDateCreated(), testCreatedDate);
		Assert.assertEquals(String.format(message, "UpdatedDate"), method.getDateLastModified(), testUpdatedDate);
		Assert.assertFalse("Method " + method.getId() + " should not be obsolete", method.isObsolete());
	}

	/*
	 * This test will check GetMethodById return null if term with CV as Method does not exists
	 * */
	@Test
	public void testGetMethodByIdShouldReturnNullIfTermDoesNotExists() throws Exception {
		Method method = this.methodDataManager.getMethod(0,true);
		Assert.assertNull(method);
		Mockito.verify(this.cvTermDao, Mockito.times(1)).getAllByCvId(Collections.singletonList(0), CvId.METHODS, true);
	}

	/**
	 * This test check add method using manager and checks
	 * 1. CreatedDate is set
	 * 2. Verify method calls
	 */
	@Test
	public void testAddMethodShouldVerifyDaoCallsAndSetCreatedDate() throws Exception {
		// Create Method and add it using manager
		Method method = new Method();
		method.setName(TestDataHelper.getNewRandomName("Method"));
		method.setDefinition("Test Definition");

		CVTerm cvTerm = new CVTerm();
		cvTerm.setCvTermId(UnitTestDaoIDGenerator.generateId(CVTerm.class));
		cvTerm.setCv(CvId.METHODS.getId());
		cvTerm.setName(method.getName());
		cvTerm.setDefinition(method.getDefinition());
		cvTerm.setIsObsolete(false);
		cvTerm.setIsRelationshipType(false);

		Mockito.when(this.cvTermDao.getByNameAndCvId(method.getName(), CvId.METHODS.getId())).thenReturn(null);

		Mockito.when(this.cvTermDao.save(method.getName(), method.getDefinition(), CvId.METHODS)).thenReturn(cvTerm);

		//save property dao mocking

		this.methodDataManager.addMethod(method);

		// Make sure each method data inserted properly, assert them and display proper message if not inserted properly
		String message = "The %s for method '" + method.getId() + "' was not added correctly.";
		Assert.assertNotNull(String.format(message, "Id"), method.getId());

		//check cvid
		Assert.assertNotNull(String.format(message, "CvId"), method.getVocabularyId());

		Assert.assertNotNull(String.format(message, "CreatedDate"), method.getDateCreated());

		//Verify save cvterm
		Mockito.verify(this.cvTermDao).save(method.getName(), method.getDefinition(), CvId.METHODS);

		//Verify created date saved
		String stringDateValue = ISO8601DateParser.toString(method.getDateCreated());
		Mockito.verify(this.cvTermPropertyDao).save(method.getId(), TermId.CREATION_DATE.getId(), stringDateValue, 0);
	}

	/**
	 * This test should check what if method with same name exists
	 */
	@Test(expected = MiddlewareException.class)
	public void testAddMethodShouldNotAllowNewMethodWithSameName() {
		// Create Method and add it using manager
		Method method = new Method();
		method.setName(TestDataHelper.getNewRandomName("Name"));
		method.setDefinition("Test Definition");

		CVTerm cvTerm = new CVTerm();
		cvTerm.setCvTermId(UnitTestDaoIDGenerator.generateId(CVTerm.class));
		cvTerm.setCv(CvId.METHODS.getId());
		cvTerm.setName(method.getName());
		cvTerm.setDefinition(method.getDefinition());
		cvTerm.setIsObsolete(false);
		cvTerm.setIsRelationshipType(false);

		Mockito.when(this.cvTermDao.getByNameAndCvId(method.getName(), CvId.METHODS.getId())).thenReturn(cvTerm);

		this.methodDataManager.addMethod(method);
	}

	/**
	 * This test check update method using manager and checks
	 * 1. DateLastUpdated is set
	 * 2. Verify method calls
	 */
	@Test
	public void testUpdateMethodShouldVerifyDaoCallsAndSetUpdatedDate() throws Exception {
		CVTerm methodTerm = TestDataHelper.getTestCvTerm(CvId.METHODS);

		CVTerm cvTerm = new CVTerm();
		cvTerm.setCvTermId(UnitTestDaoIDGenerator.generateId(CVTerm.class));
		cvTerm.setCv(CvId.METHODS.getId());
		cvTerm.setName(TestDataHelper.getNewRandomName("Name"));
		cvTerm.setDefinition("TestDefinition");
		cvTerm.setIsObsolete(false);
		cvTerm.setIsRelationshipType(false);

		// Fill Test Updated Date Property using Calendar
		Calendar cal = Calendar.getInstance();
		cal.set(2015, Calendar.MAY, 20);
		Date testUpdatedDate = cal.getTime();
		List<CVTermProperty> methodUpdatedDateProperties = new ArrayList<>();
		TestDataHelper.fillTestUpdatedDateProperties(Collections.singletonList(methodTerm), methodUpdatedDateProperties, testUpdatedDate);

		//Updating method via manager
		Method method = new Method();
		method.setId(methodTerm.getCvTermId());
		method.setName("New Method Name");
		method.setDefinition("New Method Definition");

		Mockito.when(this.cvTermDao.getById(methodTerm.getCvTermId())).thenReturn(cvTerm);
		Mockito.when(this.cvTermDao.merge(cvTerm)).thenReturn(cvTerm);

		this.methodDataManager.updateMethod(method);

		// Make sure each method data inserted properly, assert them and display proper message if not inserted properly
		String message = "The %s for method '" + cvTerm.getCvTermId() + "' was not updated correctly.";
		Assert.assertNotNull(String.format(message, "Id"), method.getId());
		Assert.assertNotNull(String.format(message, "VocabularyId"), method.getVocabularyId());

		Mockito.verify(this.cvTermDao).getById(methodTerm.getCvTermId());
		Mockito.verify(this.cvTermDao).merge(cvTerm);

		String stringDateValue = ISO8601DateParser.toString(method.getDateLastModified());
		Mockito.verify(this.cvTermPropertyDao).save(method.getId(), TermId.LAST_UPDATE_DATE.getId(), stringDateValue, 0);
	}

	/**
	 * This test should check what if Term is not method
	 */
	@Test(expected = MiddlewareException.class)
	public void testUpdateMethodShouldFailIfTermIsNotMethod() throws Exception {
		CVTerm scaleTerm = TestDataHelper.getTestCvTerm(CvId.SCALES);

		//Updating method via manager
		Method method = new Method();
		method.setId(scaleTerm.getCvTermId());
		method.setName("New Method Name");
		method.setDefinition("New Method Definition");

		Mockito.when(this.cvTermDao.getById(scaleTerm.getCvTermId())).thenReturn(scaleTerm);
		this.methodDataManager.updateMethod(method);
	}

	/**
	 * This test should check delete method should delete
	 * 1. Term
	 * 2. Properties associated to term
	 */
	@Test
	public void testDeleteMethodShouldVerifyDaoCalls() throws Exception {
		// Save Method Term using dao
		CVTerm methodTerm = TestDataHelper.getTestCvTerm(CvId.METHODS);

		// Fill Test Created Date Property using Calendar
		Calendar cal = Calendar.getInstance();
		cal.set(2015, Calendar.JANUARY, 1);
		Date testCreatedDate = cal.getTime();
		List<CVTermProperty> methodCreatedDateProperties = new ArrayList<>();
		TestDataHelper.fillTestCreatedDateProperties(Collections.singletonList(methodTerm), methodCreatedDateProperties, testCreatedDate);

		// Fill Test Updated Date Property using Calendar
		cal.set(2015, Calendar.MAY, 20);
		Date testUpdatedDate = cal.getTime();
		List<CVTermProperty> methodUpdatedDateProperties = new ArrayList<>();
		TestDataHelper.fillTestUpdatedDateProperties(Collections.singletonList(methodTerm), methodUpdatedDateProperties, testUpdatedDate);

		List<CVTermProperty> combinedProperties = new ArrayList<>(methodCreatedDateProperties);
		combinedProperties.addAll(methodUpdatedDateProperties);

		Mockito.when(this.cvTermDao.getById(methodTerm.getCvTermId())).thenReturn(methodTerm);

		Mockito.when(this.cvTermPropertyDao.getByCvTermId(methodTerm.getCvTermId())).thenReturn(combinedProperties);

		Mockito.doNothing().when(this.cvTermDao).makeTransient(methodTerm);

		Mockito.doNothing().when(this.cvTermPropertyDao).makeTransient(Mockito.any(CVTermProperty.class));

		this.methodDataManager.deleteMethod(methodTerm.getCvTermId());

		Mockito.verify(this.cvTermDao, Mockito.times(1)).makeTransient(methodTerm);

		Mockito.verify(this.cvTermPropertyDao, Mockito.times(2)).makeTransient(Mockito.any(CVTermProperty.class));

	}

	/**
	 * This test should check term is method
	 */
	@Test(expected = MiddlewareException.class)
	public void testDeleteMethodShouldFailIfTermIsNotMethod() throws Exception {
		// Save Method Term using dao
		CVTerm scaleTerm = TestDataHelper.getTestCvTerm(CvId.SCALES);

		Mockito.when(this.cvTermDao.getById(scaleTerm.getCvTermId())).thenReturn(scaleTerm);
		this.methodDataManager.deleteMethod(scaleTerm.getCvTermId());
	}

	/**
	 * This test should check term exists
	 */
	@Test(expected = MiddlewareException.class)
	public void testDeleteMethodShouldFailIfTermDoesNotExists() throws Exception {
		Mockito.when(this.cvTermDao.getById(0)).thenReturn(null);
		this.methodDataManager.deleteMethod(0);
	}

	/**
	 * This test should fail if term is referred
	 */
	@Test(expected = MiddlewareException.class)
	public void testDeleteMethodShouldAllowIfTermIsReferred() throws Exception {
		// Save Method Term using dao
		CVTerm methodTerm = TestDataHelper.getTestCvTerm(CvId.METHODS);
		Mockito.when(this.cvTermDao.getById(methodTerm.getCvTermId())).thenReturn(methodTerm);
		Mockito.doReturn(true).when(this.cvTermRelationshipDao).isTermReferred(methodTerm.getCvTermId());
		this.methodDataManager.deleteMethod(methodTerm.getCvTermId());
	}

	@Test
	public void testGetMethod_DontFilterObsolete() {
		CVTerm methodTerm = TestDataHelper.getTestCvTerm(CvId.METHODS);
		methodTerm.setIsObsolete(true);

		boolean filterObsolete = false;
		Mockito.when(this.cvTermDao.getAllByCvId(Collections.singletonList(methodTerm.getCvTermId()), CvId.METHODS, filterObsolete))
		.thenReturn(Collections.singletonList(methodTerm));

		Method method = this.methodDataManager.getMethod(methodTerm.getCvTermId(), filterObsolete);
		Assert.assertNotNull(method);
		Assert.assertTrue("Method should have id " + methodTerm.getCvTermId(), methodTerm.getCvTermId().intValue() == method.getId());
		Assert.assertTrue("Method" + method.getId() + " should be obsolete", method.isObsolete());

	}
}
