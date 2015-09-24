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
import java.util.Objects;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.dao.oms.CvTermPropertyDao;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.Method;
import org.generationcp.middleware.manager.ontology.api.OntologyMethodDataManager;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.util.ISO8601DateParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Integration test class to test Ontology Method Data Manger
 */
public class OntologyMethodDataManagerImplIntegrationTest extends IntegrationTestBase {

	@Autowired
	private OntologyMethodDataManager manager;

	@Autowired
	private OntologyDaoFactory daoFactory;

	private CVTermDao termDao;
	private CvTermPropertyDao propertyDao;

	@Before
	public void setUp() throws Exception {
		this.termDao = this.daoFactory.getCvTermDao();
		this.propertyDao = this.daoFactory.getCvTermPropertyDao();
	}

	/*
	* This test inserts 3 terms with cv as Method along with additional properties like Created Date and Last Modified Date
	* and ensures that GetAllMethods function will return full method object.
	* */
	@Test
	public void testGetAllMethodsShouldGetFullMethods() {
		List<CVTerm> methodTerms = new ArrayList<>();
		// Fill 3 methods using TestDataHelper
		TestDataHelper.fillTestMethodsCvTerms(methodTerms, 3);

		Map<Integer, CVTerm> termMap = new HashMap<>();
		// save 3 methods using termDao
		for (CVTerm term : methodTerms) {
			this.termDao.save(term);
			termMap.put(term.getCvTermId(), term);
		}

		Date testCreatedDate = this.constructDate(2015, Calendar.JANUARY, 1);
		List<CVTermProperty> methodCreatedDateProperties = new ArrayList<>();
		TestDataHelper.fillTestCreatedDateProperties(methodTerms, methodCreatedDateProperties, testCreatedDate);

		Map<Integer, String> createdDateMap = new HashMap<>();

		// Fetch Created Date Properties and save it using propertyDao
		for (CVTermProperty property : methodCreatedDateProperties) {
            this.propertyDao.save(property);
			createdDateMap.put(property.getCvTermId(), property.getValue());
		}

		Date testUpdatedDate = this.constructDate(2015, Calendar.MAY, 20);
		List<CVTermProperty> methodUpdatedDateProperties = new ArrayList<>();
		TestDataHelper.fillTestUpdatedDateProperties(methodTerms, methodUpdatedDateProperties, testUpdatedDate);

		Map<Integer, String> updateDateMap = new HashMap<>();

		// Fetch Updated Date Property and save it using propertyDao
		for (CVTermProperty property : methodUpdatedDateProperties) {
            this.propertyDao.save(property);
			updateDateMap.put(property.getCvTermId(), property.getValue());
		}

		// Fetch all methods and check our last inserted method exists or not
		List<Method> methods = this.manager.getAllMethods();

		// Iterate all methods and find our inserted method and assert it
		for (Method m : methods) {
			// Make sure our method exists and is inserted properly and display proper message if it is not inserted properly
			String message = "The %s for method '" + m.getId() + "' was not added correctly.";
			if (termMap.containsKey(m.getId())) {
				CVTerm methodTerm = termMap.get(m.getId());
				String createdDateProperty = createdDateMap.get(m.getId());
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
	* This test inserts one term with cv as Method along with additional properties like Created Date and Last Modified Date
	* and ensures that GetMethodById function will return full method object.
	* */
	@Test
	public void testGetMethodByIdShouldGetFullMethodWithIdSupplied() throws Exception {
		// Save Method Term using termDao
		CVTerm methodTerm = TestDataHelper.getTestCvTerm(CvId.METHODS);
        this.termDao.save(methodTerm);

		// Fill Test Created Date Property using TestDataHelper
		Date testCreatedDate = this.constructDate(2015, Calendar.JANUARY, 1);
		List<CVTermProperty> methodCreatedDateProperties = new ArrayList<>();
		TestDataHelper.fillTestCreatedDateProperties(Collections.singletonList(methodTerm), methodCreatedDateProperties, testCreatedDate);

		CVTermProperty methodCreatedDateProperty = methodCreatedDateProperties.get(0);
        this.propertyDao.save(methodCreatedDateProperty);

		// Fill Test Updated Date Property using TestDataHelper
		Date testUpdatedDate = this.constructDate(2015, Calendar.MAY, 20);
		List<CVTermProperty> methodUpdatedDateProperties = new ArrayList<>();
		TestDataHelper.fillTestUpdatedDateProperties(Collections.singletonList(methodTerm), methodUpdatedDateProperties, testUpdatedDate);

		CVTermProperty methodUpdatedDateProperty = methodUpdatedDateProperties.get(0);
        this.propertyDao.save(methodUpdatedDateProperty);

		// Fetch all methods and check our method exists or not.
		Method method = this.manager.getMethod(methodTerm.getCvTermId());

		// Make sure each method data inserted properly, assert them and display proper message if not inserted properly
		String message = "The %s for method '" + method.getId() + "' was not added correctly.";
		Assert.assertEquals(String.format(message, "Name"), methodTerm.getName(), method.getName());
		Assert.assertEquals(String.format(message, "Definition"), methodTerm.getDefinition(), method.getDefinition());
		Assert.assertEquals(String.format(message, "IsObsolete"), methodTerm.isObsolete(), method.isObsolete());
		Assert.assertEquals(String.format(message, "CreatedDate"), method.getDateCreated(), testCreatedDate);
		Assert.assertEquals(String.format(message, "UpdatedDate"), method.getDateLastModified(), testUpdatedDate);
	}

	/**
	 * This test inserts method using manager and assert term and created date property
	 */
	@Test
	public void testAddMethodShouldAddNewMethod() throws Exception {
		// Create Method and add it using manager
		Method method = new Method();
		method.setName(TestDataHelper.getNewRandomName("Name"));
		method.setDefinition("Test Definition");

		Date date = this.constructDate(2015, Calendar.JANUARY, 1);
		this.stubCurrentDate(date);

		this.manager.addMethod(method);

		CVTerm cvterm = this.termDao.getById(method.getId());

		// Make sure each method data inserted properly, assert them and display proper message if not inserted properly
		String message = "The %s for method '" + method.getId() + "' was not added correctly.";
		Assert.assertEquals(String.format(message, "Name"), method.getName(), cvterm.getName());
		Assert.assertEquals(String.format(message, "Definition"), method.getDefinition(), cvterm.getDefinition());
		Assert.assertEquals(String.format(message, "IsObsolete"), false, cvterm.isObsolete());
		Assert.assertEquals(String.format(message, "CreatedDate"), date, method.getDateCreated());

		// Fetch Created date property and assert it
		List<CVTermProperty> addedProperties = this.propertyDao.getByCvTermId(method.getId());
		Assert.assertTrue(String.format(message, "CreatedDate"), addedProperties.size() == 1);
		Assert.assertEquals(String.format(message, "CreatedDate"), addedProperties.get(0).getValue(), ISO8601DateParser.toString(date));
	}

	/**
	 * This test will check UpdateMethod should update CvTerm and last modified date property
	 */
	@Test
	public void testUpdateMethodShouldUpdateExistingMethod() throws Exception {
		// Save Method Term using termDao
		CVTerm methodTerm = TestDataHelper.getTestCvTerm(CvId.METHODS);
        this.termDao.save(methodTerm);

		// Fill Test Created Date Property using TestDataHelper
		Date testCreatedDate = this.constructDate(2015, Calendar.JANUARY, 1);
		List<CVTermProperty> methodCreatedDateProperties = new ArrayList<>();
		TestDataHelper.fillTestCreatedDateProperties(Collections.singletonList(methodTerm), methodCreatedDateProperties, testCreatedDate);

		CVTermProperty methodCreatedDateProperty = methodCreatedDateProperties.get(0);
        this.propertyDao.save(methodCreatedDateProperty);

		//Updating method via manager
		Method method = new Method();
		method.setId(methodTerm.getCvTermId());
		method.setName("New Method Name");
		method.setDefinition("New Method Definition");

		Date date = constructDate(2015, Calendar.JANUARY, 1);
		this.stubCurrentDate(date);

		this.manager.updateMethod(method);

		CVTerm cvterm = this.termDao.getById(method.getId());

		// Make sure the inserted data should come as they are inserted and Display proper message if the data doesn't come as expected
		String message = "The %s for method '" + method.getId() + "' was not updated correctly.";
		Assert.assertEquals(String.format(message, "Name"), method.getName(), cvterm.getName());
		Assert.assertEquals(String.format(message, "Definition"), method.getDefinition(), cvterm.getDefinition());
		Assert.assertEquals(String.format(message, "IsObsolete"), false, cvterm.isObsolete());
		Assert.assertEquals(String.format(message, "UpdatedDate"), date, method.getDateLastModified());

		// Make sure there are two properties. One for Created date and one for Updated date
		List<CVTermProperty> updatedProperties = this.propertyDao.getByCvTermId(cvterm.getCvTermId());
		Assert.assertTrue(String.format(message, "Properties"), updatedProperties.size() == 2);

		CVTermProperty lastUpdateDateProperty = null;
		CVTermProperty createdDateProperty = null;
		for (CVTermProperty property : updatedProperties) {
			if (Objects.equals(property.getTypeId(), TermId.LAST_UPDATE_DATE.getId())) {
				lastUpdateDateProperty = property;
			} else if (Objects.equals(property.getTypeId(), TermId.CREATION_DATE.getId())) {
				createdDateProperty = property;
			}
		}

		// Assert for Created Date & Last Updated date Property
		Assert.assertNotNull(createdDateProperty);
		Assert.assertNotNull(lastUpdateDateProperty);

		Assert.assertEquals(String.format(message, "CreatedDate"), createdDateProperty.getValue(),
				ISO8601DateParser.toString(testCreatedDate));
		Assert.assertEquals(String.format(message, "UpdatedDate"), lastUpdateDateProperty.getValue(),
				ISO8601DateParser.toString(method.getDateLastModified()));
	}

	/**
	 * This test will check DeleteMethod should delete term and related properties.
	 */
	@Test
	public void testDeleteMethodShouldDeleteExistingMethod() throws Exception {
		// Save Method Term using termDao
		CVTerm methodTerm = TestDataHelper.getTestCvTerm(CvId.METHODS);
        this.termDao.save(methodTerm);

		// Fill Test Created Date Property using TestDataHelper
		Date testCreatedDate = this.constructDate(2015, Calendar.JANUARY, 1);
		List<CVTermProperty> methodCreatedDateProperties = new ArrayList<>();
		TestDataHelper.fillTestCreatedDateProperties(Collections.singletonList(methodTerm), methodCreatedDateProperties, testCreatedDate);

		CVTermProperty methodCreatedDateProperty = methodCreatedDateProperties.get(0);
		// Save Method Created Date Property using propertydao
        this.propertyDao.save(methodCreatedDateProperty);

		// Fill Test Updated Date Property using TestDataHelper
		Date testUpdatedDate = this.constructDate(2015, Calendar.MAY, 20);
		List<CVTermProperty> methodUpdatedDateProperties = new ArrayList<>();
		TestDataHelper.fillTestUpdatedDateProperties(Collections.singletonList(methodTerm), methodUpdatedDateProperties, testUpdatedDate);

		CVTermProperty methodUpdatedDateProperty = methodUpdatedDateProperties.get(0);
		// Save Method Updated Date Property using propertydao
        this.propertyDao.save(methodUpdatedDateProperty);

		// Delete the method
		this.manager.deleteMethod(methodTerm.getCvTermId());

		CVTerm cvterm = this.termDao.getById(methodTerm.getCvTermId());

		// Make sure the method must be deleted and it asserts null
		String message = "The %s for method '" + methodTerm.getCvTermId() + "' was not deleted correctly.";
		Assert.assertNull(String.format(message, "Term"), cvterm);

		// Make sure the properties must be deleted and it asserts null
		List<CVTermProperty> updatedProperties = this.propertyDao.getByCvTermId(methodTerm.getCvTermId());
		Assert.assertTrue(String.format(message, "Properties"), updatedProperties.size() == 0);
	}
}
