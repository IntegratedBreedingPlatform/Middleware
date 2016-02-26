package org.generationcp.middleware.manager.ontology;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;
import org.generationcp.middleware.UnitTestBase;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.dao.oms.CVTermRelationshipDao;
import org.generationcp.middleware.dao.oms.CvTermPropertyDao;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.manager.ontology.api.OntologyCommonDAO;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.generationcp.middleware.util.Util;
import org.generationcp.middleware.utils.test.UnitTestDaoIDGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class OntologyPropertyDataManagerImplUnitTest extends UnitTestBase{

	@InjectMocks
	private OntologyPropertyDataManagerImpl propertyDataManager = new OntologyPropertyDataManagerImpl();

	@Mock
	private CVTermDao cvTermDao;

	@Mock
	private CvTermPropertyDao cvTermPropertyDao;

	@Mock
	private CVTermRelationshipDao cvTermRelationshipDao;

	@Mock
	private OntologyDaoFactory daoFactory;

	@Mock
	private OntologyCommonDAO ontologyCommonDAO;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Mockito.when(this.daoFactory.getCvTermDao()).thenReturn(cvTermDao);
		Mockito.when(this.daoFactory.getCvTermPropertyDao()).thenReturn(cvTermPropertyDao);
		Mockito.when(this.daoFactory.getCvTermRelationshipDao()).thenReturn(cvTermRelationshipDao);
	}

	/**
	 * Test to verify if get all properties works successfully or not.
	 * @throws Exception
	 */
	@Test
	public void testGetAllPropertiesShouldFetchAndVerify() throws Exception {

		Map<Integer, Property> propertyMap = new HashMap<>();

		TestDataHelper.fillTestProperties(propertyMap, 3);

		// Fill Test Created Date Property
		Date testCreatedDate = TestDataHelper.constructDate(2015, Calendar.JANUARY, 1);
		List<CVTermProperty> createdDateProperties = new ArrayList<>();
		TestDataHelper.fillTestCreatedDatePropertyProps(new ArrayList<>(propertyMap.values()), createdDateProperties, testCreatedDate);

		// Fill Test Updated Date Property
		Date testUpdatedDate = TestDataHelper.constructDate(2015, Calendar.JUNE, 30);
		List<CVTermProperty> updatedDateProperties = new ArrayList<>();
		TestDataHelper.fillTestUpdatedDatePropertyProps(new ArrayList<>(propertyMap.values()), updatedDateProperties, testUpdatedDate);

		List<CVTermProperty> combinedProperties = new ArrayList<>(createdDateProperties);
		combinedProperties.addAll(updatedDateProperties);

		Mockito.when(this.cvTermPropertyDao.getByCvTermIds(new ArrayList<>(propertyMap.keySet()))).thenReturn(combinedProperties);
		Mockito.when(this.ontologyCommonDAO.getPropertiesWithCropOntologyAndTraits(true, null, true)).thenReturn(propertyMap);

		List<Property> properties = this.propertyDataManager.getAllProperties();

		List<String> propertyNames = Util.convertAll(properties, new Function<Property, String>() {

			@Override
			public String apply(Property x) {
				return x.getName();
			}
		});

		final boolean propertyNameIsOrdered = Ordering.natural().isOrdered(propertyNames);

		Assert.assertEquals(propertyMap.size(), properties.size());
		Assert.assertTrue(propertyNameIsOrdered);

	}

	@Test
	public void testGetPropertyByIdShouldFetchAndVerify() throws Exception {
		Map<Integer, Property> propertyMap = new HashMap<>();
		TestDataHelper.fillTestProperties(propertyMap, 1);

		// Fill Test Created Date Property
		Date testCreatedDate = TestDataHelper.constructDate(2015, Calendar.JANUARY, 1);
		List<CVTermProperty> createdDateProperties = new ArrayList<>();
		TestDataHelper.fillTestCreatedDatePropertyProps(new ArrayList<>(propertyMap.values()), createdDateProperties, testCreatedDate);

		// Fill Test Updated Date Property
		Date testUpdatedDate = TestDataHelper.constructDate(2015, Calendar.JUNE, 30);
		List<CVTermProperty> updatedDateProperties = new ArrayList<>();
		TestDataHelper.fillTestUpdatedDatePropertyProps(new ArrayList<>(propertyMap.values()), updatedDateProperties, testUpdatedDate);

		List<CVTermProperty> combinedProperties = new ArrayList<>(createdDateProperties);
		combinedProperties.addAll(updatedDateProperties);

		Property existingProperty = propertyMap.get(propertyMap.keySet().iterator().next());

		Mockito.when(this.cvTermPropertyDao.getByCvTermIds(new ArrayList<>(Collections.singletonList(existingProperty.getId())))).thenReturn(combinedProperties);
		Mockito.when(this.ontologyCommonDAO.getPropertiesWithCropOntologyAndTraits(false, new ArrayList<>(Collections.singletonList(existingProperty.getId())), true)).thenReturn(propertyMap);

		final Property property = this.propertyDataManager.getProperty(existingProperty.getId(), true);

		// Make sure our property exists and is inserted properly and display proper message if it is not inserted properly
		String message = "The %s for property '" + existingProperty.getId() + "' was not added correctly.";
		Assert.assertEquals(String.format(message, "Name"), existingProperty.getName(), property.getName());
		Assert.assertEquals(String.format(message, "Definition"), existingProperty.getDefinition(), property.getDefinition());
		Assert.assertEquals(String.format(message, "CreatedDate"),existingProperty.getDateCreated(), property.getDateCreated());
		Assert.assertEquals(String.format(message, "UpdatedDate"), existingProperty.getDateLastModified(), property.getDateLastModified());
		Assert.assertEquals(String.format(message, "CropOntologyId"), existingProperty.getCropOntologyId(), property.getCropOntologyId());
		final Set<String> classNames = existingProperty.getClasses();

		for(String className : classNames) {
			Assert.assertTrue(String.format(message, "ClassName"), property.getClasses().contains(className));
		}
	}

	@Test
	public void testGetPropertyByIdShouldReturnEmptyList() throws Exception {
		Map<Integer, Property> propertyMap = new HashMap<>();
		TestDataHelper.fillTestProperties(propertyMap, 1);

		Integer propertyId = propertyMap.keySet().iterator().next();
		Mockito.when(this.ontologyCommonDAO.getPropertiesWithCropOntologyAndTraits(false, new ArrayList<>(Collections.singletonList(propertyMap.get(propertyId))), true)).thenReturn(
				null);

		final Property property = this.propertyDataManager.getProperty(propertyId, true);

		Assert.assertNull(property);
	}

	/**
	 * This test should check dao calls and created date
	 */
	@Test
	public void testAddPropertyShouldVerifyDaoCallsAndSetCreatedDate() throws Exception {

		Property property = new Property();
		property.setName(TestDataHelper.getNewRandomName("Property"));
		property.setDefinition("Test Property Name");
		property.setCropOntologyId("CO:101");
		property.addClass("Class1");
		property.addClass("Class2");

		CVTerm cvTerm = new CVTerm();
		cvTerm.setCvTermId(UnitTestDaoIDGenerator.generateId(CVTerm.class));
		cvTerm.setCv(CvId.PROPERTIES.getId());
		cvTerm.setName(property.getName());
		cvTerm.setDefinition(property.getDefinition());
		cvTerm.setIsObsolete(false);
		cvTerm.setIsRelationshipType(false);

		Date date = TestDataHelper.constructDate(2015, Calendar.JANUARY, 1);
		this.stubCurrentDate(date);

		Mockito.when(this.cvTermDao.getByNameAndCvId(property.getName(), CvId.PROPERTIES.getId())).thenReturn(null);
		Mockito.when(this.cvTermDao.save(property.getName(), property.getDefinition(), CvId.PROPERTIES)).thenReturn(cvTerm);
		Mockito.when(this.cvTermRelationshipDao.getBySubjectIdAndTypeId(property.getId(), TermId.IS_A.getId())).thenReturn(
				new ArrayList<CVTermRelationship>());

		List<String> classes = new ArrayList<>(property.getClasses());

		CVTerm class1 = new CVTerm(100, CvId.TRAIT_CLASS.getId(), classes.get(0), null, null, 0, 0);
		CVTerm class2 = new CVTerm(101, CvId.TRAIT_CLASS.getId(), classes.get(1), null, null, 0, 0);

		Mockito.when(this.cvTermDao.save(classes.get(0), null, CvId.TRAIT_CLASS)).thenReturn(class1);
		Mockito.when(this.cvTermDao.save(classes.get(1), null, CvId.TRAIT_CLASS)).thenReturn(class2);

		this.propertyDataManager.addProperty(property);

		// Make sure each property data inserted properly, assert them and display proper message if not inserted properly
		String message = "The %s for property '" + property.getId() + "' was not added correctly.";
		Assert.assertNotNull(String.format(message, "Id"), property.getId());

		//check cvid
		Assert.assertNotNull(String.format(message, "CvId"), property.getVocabularyId());

		Assert.assertEquals(String.format(message, "CreatedDate"), date, property.getDateCreated());

		//Verify save cvterm
		Mockito.verify(this.cvTermDao).save(property.getName(), property.getDefinition(), CvId.PROPERTIES);
	}

	/**
	 * This test should check what if property with same name exists
	 */
	@Test(expected = MiddlewareException.class)
	public void testAddPropertyShouldNotAllowNewPropertyWithSameName() {
		// Create Property and add it using manager
		Property property = TestDataHelper.generateProperty();

		CVTerm cvTerm = new CVTerm();
		cvTerm.setCvTermId(UnitTestDaoIDGenerator.generateId(CVTerm.class));
		cvTerm.setCv(CvId.PROPERTIES.getId());
		cvTerm.setName(property.getName());
		cvTerm.setDefinition(property.getDefinition());
		cvTerm.setIsObsolete(false);
		cvTerm.setIsRelationshipType(false);

		Mockito.when(this.cvTermDao.getByNameAndCvId(property.getName(), CvId.PROPERTIES.getId())).thenReturn(cvTerm);

		this.propertyDataManager.addProperty(property);
	}

	@Test
	public void testAddPropertyWithEmptyClassName() throws Exception {

		Property property = new Property();
		property.setName(TestDataHelper.getNewRandomName("Property"));
		property.setDefinition("Test Property Name");
		property.setCropOntologyId("CO:101");
		property.addClass("");
		property.addClass("Class2");

		CVTerm cvTerm = new CVTerm();
		cvTerm.setCvTermId(UnitTestDaoIDGenerator.generateId(CVTerm.class));
		cvTerm.setCv(CvId.PROPERTIES.getId());
		cvTerm.setName(property.getName());
		cvTerm.setDefinition(property.getDefinition());
		cvTerm.setIsObsolete(false);
		cvTerm.setIsRelationshipType(false);

		Date date = TestDataHelper.constructDate(2015, Calendar.JANUARY, 1);
		this.stubCurrentDate(date);

		Mockito.when(this.cvTermDao.getByNameAndCvId(property.getName(), CvId.PROPERTIES.getId())).thenReturn(null);
		Mockito.when(this.cvTermDao.save(property.getName(), property.getDefinition(), CvId.PROPERTIES)).thenReturn(cvTerm);
		Mockito.when(this.cvTermRelationshipDao.getBySubjectIdAndTypeId(property.getId(), TermId.IS_A.getId())).thenReturn(
				new ArrayList<CVTermRelationship>());

		List<String> classes = new ArrayList<>(property.getClasses());

		CVTerm class2 = new CVTerm(101, CvId.TRAIT_CLASS.getId(), classes.get(1), null, null, 0, 0);

		Mockito.when(this.cvTermDao.save(classes.get(1), null, CvId.TRAIT_CLASS)).thenReturn(class2);

		this.propertyDataManager.addProperty(property);

		// Make sure each property data inserted properly, assert them and display proper message if not inserted properly
		String message = "The %s for property '" + property.getId() + "' was not added correctly.";
		Assert.assertNotNull(String.format(message, "Id"), property.getId());

		//check cvid
		Assert.assertNotNull(String.format(message, "CvId"), property.getVocabularyId());

		Assert.assertEquals(String.format(message, "CreatedDate"), date, property.getDateCreated());

		//Verify save cvterm
		Mockito.verify(this.cvTermDao).save(property.getName(), property.getDefinition(), CvId.PROPERTIES);
	}

	@Test
	public void testUpdatePropertyShouldVerifyDaoCallsAndSetUpdatedDate() throws Exception {
		CVTerm propertyTerm = TestDataHelper.getTestCvTerm(CvId.PROPERTIES);

		//Updating property via manager
		Property property = new Property();
		property.setId(propertyTerm.getCvTermId());
		property.setName("New Property Name");
		property.setDefinition("New Property Definition");
		property.setCropOntologyId("CO:1234");

		Mockito.when(this.cvTermDao.getById(propertyTerm.getCvTermId())).thenReturn(propertyTerm);
		Mockito.when(this.cvTermDao.merge(propertyTerm)).thenReturn(propertyTerm);

		Date updatedDate = TestDataHelper.constructDate(2015, Calendar.MAY, 20);
		this.stubCurrentDate(updatedDate);

		this.propertyDataManager.updateProperty(property);

		// Make sure each property data inserted properly, assert them and display proper message if not inserted properly
		String message = "The %s for property '" + propertyTerm.getCvTermId() + "' was not updated correctly.";
		Assert.assertNotNull(String.format(message, "Id"), property.getId());

		Mockito.verify(this.cvTermDao).getById(propertyTerm.getCvTermId());
		Mockito.verify(this.cvTermDao).merge(propertyTerm);

		// TODO: Mockito.verify needs to be fixed
        // Mockito.verify(this.cvTermPropertyDao, Mockito.times(2)).save(property.getId(), Matchers.any(Integer.class), Matchers.any(String.class), 0);
	}

	/**
	 * This test should check if Term is not property
	 */
	@Test(expected = MiddlewareException.class)
	public void testUpdatePropertyShouldFailIfTermIsNotProperty() throws Exception {
		CVTerm methodTerm = TestDataHelper.getTestCvTerm(CvId.METHODS);

		//Updating property via manager
		Property property = new Property();
		property.setId(methodTerm.getCvTermId());
		property.setName("New Property Name");
		property.setDefinition("New Property Definition");

		Mockito.when(this.cvTermDao.getById(methodTerm.getCvTermId())).thenReturn(methodTerm);
		this.propertyDataManager.updateProperty(property);
	}

	/**
	 * This test should check delete property should delete
	 * 1. Term
	 * 2. Properties associated to term
	 */
	@Test
	public void testDeletePropertyShouldVerifyDaoCalls() throws Exception {
		// Save Property Term
		CVTerm propertyTerm = TestDataHelper.getTestCvTerm(CvId.PROPERTIES);

		final Integer isATermCount = 1;
		List<CVTerm> isATerms = TestDataHelper.generateNewIsATerms(isATermCount, this.cvTermDao);

		List<CVTermRelationship> cvTermRelationships = new ArrayList<>();
		CVTermRelationship cvTermRelationship = new CVTermRelationship();
		cvTermRelationship.setSubjectId(propertyTerm.getCvTermId());
		cvTermRelationship.setTypeId(TermId.IS_A.getId());
		cvTermRelationship.setObjectId(isATerms.get(0).getCvTermId());
		cvTermRelationships.add(cvTermRelationship);

		// Fill Test Created Date Property
		Date testCreatedDate = TestDataHelper.constructDate(2015, Calendar.JANUARY, 1);
		List<CVTermProperty> createdDateProperties = new ArrayList<>();
		TestDataHelper.fillTestCreatedDateProperties(Collections.singletonList(propertyTerm), createdDateProperties, testCreatedDate);

		// Fill Test Updated Date Property
		Date testUpdatedDate = TestDataHelper.constructDate(2015, Calendar.MAY, 20);
		List<CVTermProperty> updatedDateProperties = new ArrayList<>();
		TestDataHelper.fillTestUpdatedDateProperties(Collections.singletonList(propertyTerm), updatedDateProperties, testUpdatedDate);

		List<CVTermProperty> combinedProperties = new ArrayList<>(createdDateProperties);
		combinedProperties.addAll(updatedDateProperties);

		Mockito.when(this.cvTermDao.getById(isATerms.get(0).getCvTermId())).thenReturn(isATerms.get(0));
		Mockito.when(this.cvTermPropertyDao.getByCvTermId(propertyTerm.getCvTermId())).thenReturn(combinedProperties);
		Mockito.doNothing().when(this.cvTermDao).makeTransient(propertyTerm);
		Mockito.doNothing().when(this.cvTermPropertyDao).makeTransient(Mockito.any(CVTermProperty.class));
		Mockito.when(this.cvTermDao.getById(propertyTerm.getCvTermId())).thenReturn(propertyTerm);
		Mockito.when(this.cvTermRelationshipDao.getByObjectId(propertyTerm.getCvTermId())).thenReturn(new ArrayList<CVTermRelationship>());
		Mockito.when(this.cvTermRelationshipDao.getBySubject(propertyTerm.getCvTermId())).thenReturn(cvTermRelationships);

		this.propertyDataManager.deleteProperty(propertyTerm.getCvTermId());

		Mockito.verify(this.cvTermDao, Mockito.times(1)).makeTransient(propertyTerm);
		Mockito.verify(this.cvTermPropertyDao, Mockito.times(2)).makeTransient(Mockito.any(CVTermProperty.class));
		Mockito.verify(this.cvTermRelationshipDao, Mockito.times(1)).makeTransient(Mockito.any(CVTermRelationship.class));
	}

	/**
	 * This test should fail if term is referred
	 */
	@Test(expected = MiddlewareException.class)
	public void testDeletePropertyShouldNotAllowIfTermIsReferred() throws Exception {
		// Save Property Term using dao
		CVTerm propertyTerm = TestDataHelper.getTestCvTerm(CvId.PROPERTIES);
		Mockito.when(this.cvTermDao.getById(propertyTerm.getCvTermId())).thenReturn(propertyTerm);
		Mockito.doReturn(true).when(this.cvTermRelationshipDao).isTermReferred(propertyTerm.getCvTermId());
		this.propertyDataManager.deleteProperty(propertyTerm.getCvTermId());
	}

	/**
	 * This test should check term exists
	 */
	@Test(expected = MiddlewareException.class)
	public void testDeletePropertyShouldFailIfTermDoesNotExists() throws Exception {
		Mockito.when(this.cvTermDao.getById(0)).thenReturn(null);
		this.propertyDataManager.deleteProperty(0);
	}

	/**
	 * Test to validate Parent Class
	 * @throws Exception
	 */
	@Test(expected = MiddlewareException.class)
	public void testValidateParentClass() throws Exception {
		// Save Property Term
		CVTerm propertyTerm = TestDataHelper.getTestCvTerm(CvId.PROPERTIES);

		final Integer isATermCount = 1;
		List<CVTerm> isATerms = TestDataHelper.generateNewIsATerms(isATermCount, this.cvTermDao);

		List<CVTermRelationship> cvTermRelationships = new ArrayList<>();
		CVTermRelationship cvTermRelationship = new CVTermRelationship();
		cvTermRelationship.setSubjectId(propertyTerm.getCvTermId());
		cvTermRelationship.setTypeId(TermId.IS_A.getId());
		cvTermRelationship.setObjectId(isATerms.get(0).getCvTermId());
		cvTermRelationships.add(cvTermRelationship);

		// Fill Test Created Date Property
		Date testCreatedDate = TestDataHelper.constructDate(2015, Calendar.JANUARY, 1);
		List<CVTermProperty> createdDateProperties = new ArrayList<>();
		TestDataHelper.fillTestCreatedDateProperties(Collections.singletonList(propertyTerm), createdDateProperties, testCreatedDate);

		// Fill Test Updated Date Property
		Date testUpdatedDate = TestDataHelper.constructDate(2015, Calendar.MAY, 20);
		List<CVTermProperty> updatedDateProperties = new ArrayList<>();
		TestDataHelper.fillTestUpdatedDateProperties(Collections.singletonList(propertyTerm), updatedDateProperties, testUpdatedDate);

		List<CVTermProperty> combinedProperties = new ArrayList<>(createdDateProperties);
		combinedProperties.addAll(updatedDateProperties);

		Mockito.when(this.cvTermDao.getById(isATerms.get(0).getCvTermId())).thenReturn(propertyTerm);
		Mockito.when(this.cvTermPropertyDao.getByCvTermId(propertyTerm.getCvTermId())).thenReturn(combinedProperties);
		Mockito.doNothing().when(this.cvTermDao).makeTransient(propertyTerm);
		Mockito.doNothing().when(this.cvTermPropertyDao).makeTransient(Mockito.any(CVTermProperty.class));
		Mockito.when(this.cvTermDao.getById(propertyTerm.getCvTermId())).thenReturn(propertyTerm);
		Mockito.when(this.cvTermRelationshipDao.getByObjectId(propertyTerm.getCvTermId())).thenReturn(new ArrayList<CVTermRelationship>());
		Mockito.when(this.cvTermRelationshipDao.getBySubject(propertyTerm.getCvTermId())).thenReturn(cvTermRelationships);
		this.propertyDataManager.deleteProperty(propertyTerm.getCvTermId());
	}

	@Test(expected = MiddlewareException.class)
	public void testIfPropertyIsReferred() throws Exception {
		CVTerm propertyTerm = TestDataHelper.getTestCvTerm(CvId.PROPERTIES);

		final Integer isATermCount = 1;
		List<CVTerm> isATerms = TestDataHelper.generateNewIsATerms(isATermCount, this.cvTermDao);

		List<CVTermRelationship> cvTermRelationships = new ArrayList<>();
		CVTermRelationship cvTermRelationship = new CVTermRelationship();
		cvTermRelationship.setSubjectId(propertyTerm.getCvTermId());
		cvTermRelationship.setTypeId(TermId.IS_A.getId());
		cvTermRelationship.setObjectId(isATerms.get(0).getCvTermId());
		cvTermRelationships.add(cvTermRelationship);

		// Fill Test Created Date Property
		Date testCreatedDate = TestDataHelper.constructDate(2015, Calendar.JANUARY, 1);
		List<CVTermProperty> createdDateProperties = new ArrayList<>();
		TestDataHelper.fillTestCreatedDateProperties(Collections.singletonList(propertyTerm), createdDateProperties, testCreatedDate);

		// Fill Test Updated Date Property
		Date testUpdatedDate = TestDataHelper.constructDate(2015, Calendar.MAY, 20);
		List<CVTermProperty> updatedDateProperties = new ArrayList<>();
		TestDataHelper.fillTestUpdatedDateProperties(Collections.singletonList(propertyTerm), updatedDateProperties, testUpdatedDate);

		List<CVTermProperty> combinedProperties = new ArrayList<>(createdDateProperties);
		combinedProperties.addAll(updatedDateProperties);

		Mockito.when(this.cvTermDao.getById(propertyTerm.getCvTermId())).thenReturn(propertyTerm);
		Mockito.when(this.cvTermRelationshipDao.isTermReferred(propertyTerm.getCvTermId())).thenReturn(true);

		this.propertyDataManager.deleteProperty(propertyTerm.getCvTermId());
	}
}
