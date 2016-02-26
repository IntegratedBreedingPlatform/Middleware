package org.generationcp.middleware.manager.ontology;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.common.base.Function;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.dao.oms.CVTermRelationshipDao;
import org.generationcp.middleware.dao.oms.CvTermPropertyDao;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.manager.ontology.api.OntologyPropertyDataManager;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.generationcp.middleware.util.ISO8601DateParser;
import org.generationcp.middleware.util.Util;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class OntologyPropertyDataManagerImplIntegrationTest extends IntegrationTestBase {

    @Autowired
    private OntologyPropertyDataManager manager;

    @Autowired
    private OntologyDaoFactory daoFactory;

    private CVTermDao termDao;
    private CvTermPropertyDao propertyDao;
    private CVTermRelationshipDao relationshipDao;

    @Before
    public void setUp() throws Exception {
        this.termDao = this.daoFactory.getCvTermDao();
        this.propertyDao = this.daoFactory.getCvTermPropertyDao();
        this.relationshipDao = this.daoFactory.getCvTermRelationshipDao();
    }

    @Test
    public void getPropertiesShouldGetFullProperties() throws Exception {
        List<CVTerm> propertyTerms = new ArrayList<>();
        TestDataHelper.fillTestPropertiesCvTerms(propertyTerms, 3);

        Map<Integer, CVTerm> termMap = new HashMap<>();
        // save 3 properties using termDao
        for (CVTerm term : propertyTerms) {
            termDao.save(term);
            termMap.put(term.getCvTermId(), term);
        }

		final Integer isATermCount = 5;

        List<CVTerm> isATerms = TestDataHelper.generateNewIsATerms(isATermCount, this.termDao);

        Map<Integer, Set<String>> propertyClassesMap = new HashMap<>();

		TestDataHelper.fillIsARelationshipsForProperty(propertyTerms, isATerms, propertyClassesMap, this.relationshipDao);

		Map<Integer, String> cropOntologyIdMap = new HashMap<>();

		TestDataHelper.fillCropOntologyForProperty(propertyTerms, cropOntologyIdMap, this.propertyDao);

        //Save created date
        Map<Integer, String> createdDateMap = new HashMap<>();
        Date testCreatedDate = TestDataHelper.constructDate(2015, Calendar.JANUARY, 1);
        List<CVTermProperty> createdDateProperties = new ArrayList<>();
        TestDataHelper.fillTestCreatedDateProperties(propertyTerms, createdDateProperties, testCreatedDate);

        for (CVTermProperty property : createdDateProperties) {
            propertyDao.save(property);
            createdDateMap.put(property.getCvTermId(), property.getValue());
        }

        //Save last modification date
        Map<Integer, String> updateDateMap = new HashMap<>();
        Date testUpdatedDate = TestDataHelper.constructDate(2015, Calendar.MAY, 20);
        List<CVTermProperty> updatedDateProperties = new ArrayList<>();
        TestDataHelper.fillTestUpdatedDateProperties(propertyTerms, updatedDateProperties, testUpdatedDate);


        // Fetch Updated Date Property and save it using propertyDao
        for (CVTermProperty property : updatedDateProperties) {
            propertyDao.save(property);
            updateDateMap.put(property.getCvTermId(), property.getValue());
        }

        // Fetch all properties and check our last inserted property exists or not
        List<Property> properties = this.manager.getAllProperties();

        // Iterate all properties and find our inserted properties and assert it
        for (Property property : properties) {
            // Make sure our property exists and is inserted properly and display proper message if it is not inserted properly
            String message = "The %s for property '" + property.getId() + "' was not added correctly.";
            if (termMap.containsKey(property.getId())) {
                CVTerm propertyTerm = termMap.get(property.getId());
                String createdDateProperty = createdDateMap.get(property.getId());
                String updatedDateProperty = updateDateMap.get(property.getId());

                Assert.assertEquals(String.format(message, "Name"), propertyTerm.getName(), property.getName());
                Assert.assertEquals(String.format(message, "Definition"), propertyTerm.getDefinition(), property.getDefinition());
                Assert.assertEquals(String.format(message, "IsObsolete"), propertyTerm.isObsolete(), property.isObsolete());
                Assert.assertEquals(String.format(message, "CreatedDate"), createdDateProperty,
                        ISO8601DateParser.toString(property.getDateCreated()));
                Assert.assertEquals(String.format(message, "UpdatedDate"), updatedDateProperty,
                        ISO8601DateParser.toString(property.getDateLastModified()));

                Assert.assertEquals(String.format(message, "CropOntologyId"), cropOntologyIdMap.get(property.getId()),
						property.getCropOntologyId());

				final Set<String> classNames = propertyClassesMap.get(property.getId());

				//assert total classes should match classNames
				Assert.assertEquals(String.format(message, "Class Size"), property.getClasses().size(), classNames.size());

				//iterate all class with exist
				for(String className : classNames) {
					Assert.assertTrue(String.format(message, "Class"), property.getClasses().contains(className));
				}
            }
        }
    }

    @Test
    public void testGetPropertyByIdShouldGetFullPropertyWithIdSupplied() throws Exception {
        CVTerm propertyTerm = TestDataHelper.getTestCvTerm(CvId.PROPERTIES);
        this.termDao.save(propertyTerm);

		final Integer isATermCount = 2;
		List<CVTerm> isATerms = TestDataHelper.generateNewIsATerms(isATermCount, this.termDao);
        this.relationshipDao.save(propertyTerm.getCvTermId(), TermId.IS_A.getId(), isATerms.get(0).getCvTermId());
        this.relationshipDao.save(propertyTerm.getCvTermId(), TermId.IS_A.getId(), isATerms.get(1).getCvTermId());

        String cropOntologyId = TestDataHelper.getNewRandomName("CO:");

        this.propertyDao.updateOrDeleteProperty(propertyTerm.getCvTermId(), TermId.CROP_ONTOLOGY_ID.getId(), cropOntologyId, 0);

        Date testCreatedDate = TestDataHelper.constructDate(2015, Calendar.JANUARY, 1);
        List<CVTermProperty> createdDateProperties = new ArrayList<>();
        TestDataHelper.fillTestCreatedDateProperties(Collections.singletonList(propertyTerm), createdDateProperties, testCreatedDate);

        CVTermProperty createProperty = createdDateProperties.get(0);
        this.propertyDao.save(createProperty);

        //Save last modification date
        Date testUpdatedDate = TestDataHelper.constructDate(2015, Calendar.MAY, 20);
        List<CVTermProperty> updatedDateProperties = new ArrayList<>();
        TestDataHelper.fillTestUpdatedDateProperties(Collections.singletonList(propertyTerm), updatedDateProperties, testUpdatedDate);

        CVTermProperty updateProperty = updatedDateProperties.get(0);
        this.propertyDao.save(updateProperty);


       Property property=this.manager.getProperty(propertyTerm.getCvTermId(), true);

        // Make sure our property exists and is inserted properly and display proper message if it is not inserted properly
        String message = "The %s for property '" + property.getId() + "' was not added correctly.";
        Assert.assertEquals(String.format(message, "Name"), propertyTerm.getName(), property.getName());
        Assert.assertEquals(String.format(message, "Definition"), propertyTerm.getDefinition(), property.getDefinition());
        Assert.assertEquals(String.format(message, "IsObsolete"), propertyTerm.isObsolete(), property.isObsolete());
        Assert.assertEquals(String.format(message, "CreatedDate"), testCreatedDate, property.getDateCreated());
        Assert.assertEquals(String.format(message, "UpdatedDate"), testUpdatedDate, property.getDateLastModified());

        Assert.assertEquals(String.format(message, "CropOntologyId"), cropOntologyId, property.getCropOntologyId());
		Assert.assertEquals(String.format(message, "Class Size"), isATermCount.intValue(), property.getClasses().size());
		for (CVTerm isATerm : isATerms) {
			Assert.assertTrue(property.getClasses().contains(isATerm.getName()));
		}
    }

	/**
	 * This test inserts property using manager and assert term and created date property
	 * @throws Exception
	 */
	@Test
	public void testAddPropertyShouldAddNewProperty() throws Exception {
		// Create Property and add it using manager
		Property property = new Property();
		property.setName(TestDataHelper.getNewRandomName("Name"));
		property.setDefinition("Test Definition");
		property.addClass("Test Class1");
		property.addClass("Test Class2");
		property.setCropOntologyId("CO:321");

		Date date = TestDataHelper.constructDate(2015, Calendar.JANUARY, 1);
		this.stubCurrentDate(date);

		this.manager.addProperty(property);

		CVTerm cvterm = this.termDao.getById(property.getId());

		// Make sure each property data inserted properly, assert them and display proper message if not inserted properly
		String message = "The %s for property '" + property.getId() + "' was not added correctly.";
		Assert.assertEquals(String.format(message, "Name"), property.getName(), cvterm.getName());
		Assert.assertEquals(String.format(message, "Definition"), property.getDefinition(), cvterm.getDefinition());
		Assert.assertEquals(String.format(message, "IsObsolete"), false, cvterm.isObsolete());
		Assert.assertEquals(String.format(message, "CreatedDate"), date, property.getDateCreated());

		// Fetch Created date property and assert it
		List<CVTermProperty> addedProperties = this.propertyDao.getByCvTermId(property.getId());
		Assert.assertTrue(String.format(message, "Property Size"), addedProperties.size() == 2);

		for (CVTermProperty cvTermProperty : addedProperties) {
			if (Objects.equals(cvTermProperty.getTypeId(), TermId.CREATION_DATE.getId())) {
				Assert.assertEquals(String.format(message, "CreatedDate"), cvTermProperty.getValue(), ISO8601DateParser.toString(date));
			}
			if (Objects.equals(cvTermProperty.getTypeId(), TermId.CROP_ONTOLOGY_ID.getId())) {
				Assert.assertEquals(String.format(message, "Crop Ontology Id"), cvTermProperty.getValue(), property.getCropOntologyId());
			}
		}

		List<Integer> addedClassIds = Util.convertAll(this.relationshipDao.getBySubject(property.getId()), new Function<CVTermRelationship, Integer>() {

			@Override
			public Integer apply(CVTermRelationship x) {
				return x.getObjectId();
			}
		});

		//Fetching class names from cvterm ids.
		List<String> classNames = Util.convertAll(this.termDao.getByIds(addedClassIds), new Function<CVTerm, String>() {

			@Override
			public String apply(CVTerm x){
					return x.getName();
			}
		});

		//assert total classes should match classNames
		Assert.assertEquals(String.format(message, "Class Size"), property.getClasses().size(), classNames.size());

		//iterate all class with exist
		for(String className : classNames) {
			Assert.assertTrue(String.format(message, "Class"), property.getClasses().contains(className));
		}
	}

	/**
	 * Test to verify if update property method works expected and assert updated data.
	 * @throws Exception
	 */
	@Test
	public void testUpdatePropertyShouldUpdateExistingProperty() throws Exception {
		CVTerm propertyTerm = TestDataHelper.getTestCvTerm(CvId.PROPERTIES);
		this.termDao.save(propertyTerm);

		final Integer isATermCount = 3;
		List<CVTerm> isATerms = TestDataHelper.generateNewIsATerms(isATermCount, this.termDao);
		this.relationshipDao.save(propertyTerm.getCvTermId(), TermId.IS_A.getId(), isATerms.get(0).getCvTermId());
		this.relationshipDao.save(propertyTerm.getCvTermId(), TermId.IS_A.getId(), isATerms.get(1).getCvTermId());

		String cropOntologyId = TestDataHelper.getNewRandomName("CO:");

		this.propertyDao.updateOrDeleteProperty(propertyTerm.getCvTermId(), TermId.CROP_ONTOLOGY_ID.getId(), cropOntologyId, 0);

		Date testCreatedDate = TestDataHelper.constructDate(2015, Calendar.JANUARY, 1);
		List<CVTermProperty> createdDateProperties = new ArrayList<>();
		TestDataHelper.fillTestCreatedDateProperties(Collections.singletonList(propertyTerm), createdDateProperties, testCreatedDate);

		CVTermProperty createProperty = createdDateProperties.get(0);
		this.propertyDao.save(createProperty);

		//Save last modification date
		Date testUpdatedDate = TestDataHelper.constructDate(2015, Calendar.MAY, 20);
		List<CVTermProperty> updatedDateProperties = new ArrayList<>();
		TestDataHelper.fillTestUpdatedDateProperties(Collections.singletonList(propertyTerm), updatedDateProperties, testUpdatedDate);

		CVTermProperty updateProperty = updatedDateProperties.get(0);
		this.propertyDao.save(updateProperty);

		//Updating updatedProperty via manager
		Property updatedProperty = new Property();
		updatedProperty.setId(propertyTerm.getCvTermId());
		updatedProperty.setName("New Property Name");
		updatedProperty.setDefinition("New Property Definition");
		updatedProperty.addClass(isATerms.get(0).getName());
		updatedProperty.addClass(isATerms.get(2).getName());
		updatedProperty.setCropOntologyId("CO:123");

		Date date = TestDataHelper.constructDate(2015, Calendar.JANUARY, 1);
		this.stubCurrentDate(date);

		this.manager.updateProperty(updatedProperty);

		CVTerm cvterm = this.termDao.getById(updatedProperty.getId());

		// Make sure the inserted data should come as they are inserted and Display proper message if the data doesn't come as expected
		String message = "The %s for property '" + updatedProperty.getId() + "' was not updated correctly.";
		Assert.assertEquals(String.format(message, "Name"), updatedProperty.getName(), cvterm.getName());
		Assert.assertEquals(String.format(message, "Definition"), updatedProperty.getDefinition(), cvterm.getDefinition());
		Assert.assertEquals(String.format(message, "IsObsolete"), false, cvterm.isObsolete());
		Assert.assertEquals(String.format(message, "UpdatedDate"), date, updatedProperty.getDateLastModified());

		// Fetch Created date property and assert it
		List<CVTermProperty> updatedProperties = this.propertyDao.getByCvTermId(updatedProperty.getId());
		Assert.assertTrue(String.format(message, "Property Size"), updatedProperties.size() == 3);

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
				ISO8601DateParser.toString(updatedProperty.getDateLastModified()));

		List<Integer> addedClassIds = Util.convertAll(this.relationshipDao.getBySubject(updatedProperty.getId()), new Function<CVTermRelationship, Integer>() {

			@Override
			public Integer apply(CVTermRelationship x) {
				return x.getObjectId();
			}
		});

		//Fetching class names from cvterm ids.
		List<String> classNames = Util.convertAll(this.termDao.getByIds(addedClassIds), new Function<CVTerm, String>() {

			@Override
			public String apply(CVTerm x){
				return x.getName();
			}
		});

		//assert total classes should match classNames
		Assert.assertEquals(String.format(message, "Class Size"), updatedProperty.getClasses().size(), classNames.size());

		//iterate all class with exist
		for(String className : classNames) {
			Assert.assertTrue(String.format(message, "Class"), updatedProperty.getClasses().contains(className));
		}

		//isA of index 1 should be deleted from cvterm on-fly by update property method as it is not referred to any other property.
		Assert.assertNull(String.format(message, "Deleted Class"), this.termDao.getById(isATerms.get(1).getCvTermId()));
	}

	/**
	 * Test to verify if delete property method works expected and assert null after deleting property.
	 * @throws Exception
	 */
	@Test
	public void testDeletePropertyShouldDeleteExistingProperty() throws Exception {
		// Save Property Term using termDao
		CVTerm propertyTerm = TestDataHelper.getTestCvTerm(CvId.PROPERTIES);
		this.termDao.save(propertyTerm);

		final Integer isATermCount = 2;
		List<CVTerm> isATerms = TestDataHelper.generateNewIsATerms(isATermCount, this.termDao);
		this.relationshipDao.save(propertyTerm.getCvTermId(), TermId.IS_A.getId(), isATerms.get(0).getCvTermId());
		this.relationshipDao.save(propertyTerm.getCvTermId(), TermId.IS_A.getId(), isATerms.get(1).getCvTermId());

		String cropOntologyId = TestDataHelper.getNewRandomName("CO:");

		this.propertyDao.updateOrDeleteProperty(propertyTerm.getCvTermId(), TermId.CROP_ONTOLOGY_ID.getId(), cropOntologyId, 0);

		// Fill Test Created Date Property using TestDataHelper
		Date testCreatedDate = TestDataHelper.constructDate(2015, Calendar.JANUARY, 1);
		List<CVTermProperty> createdDateProperties = new ArrayList<>();
		TestDataHelper.fillTestCreatedDateProperties(Collections.singletonList(propertyTerm), createdDateProperties, testCreatedDate);

		CVTermProperty createdDateProperty = createdDateProperties.get(0);
		// Save Property Created Date Property using propertydao
		this.propertyDao.save(createdDateProperty);

		// Fill Test Updated Date Property using TestDataHelper
		Date testUpdatedDate = TestDataHelper.constructDate(2015, Calendar.MAY, 20);
		List<CVTermProperty> updatedDateProperties = new ArrayList<>();
		TestDataHelper.fillTestUpdatedDateProperties(Collections.singletonList(propertyTerm), updatedDateProperties, testUpdatedDate);

		CVTermProperty updatedDateProperty = updatedDateProperties.get(0);
		// Save Property Updated Date Property using propertydao
		this.propertyDao.save(updatedDateProperty);

		// Delete the property
		this.manager.deleteProperty(propertyTerm.getCvTermId());

		CVTerm cvterm = this.termDao.getById(propertyTerm.getCvTermId());

		// Make sure the property must be deleted and it asserts null
		String message = "The %s for property '" + propertyTerm.getCvTermId() + "' was not deleted correctly.";
		Assert.assertNull(String.format(message, "Property"), cvterm);

		// Make sure the properties must be deleted and it asserts null
		List<CVTermProperty> deletedProperties = this.propertyDao.getByCvTermId(propertyTerm.getCvTermId());
		Assert.assertTrue(String.format(message, "Property Props"), Objects.equals(deletedProperties.size(), 0));

		List<CVTermRelationship> deletedRelationships = this.relationshipDao.getBySubject(propertyTerm.getCvTermId());
		Assert.assertTrue(String.format(message, "Property Relationships"), Objects.equals(deletedRelationships.size(), 0));
	}

	/**
	 * Test to verify to get property by trait classes and variable types.
	 * @throws Exception
	 */
	@Test
	public void testGetPropertyByTraitClassShouldGetFullProperty() throws Exception {

		final int testPropertyCount = 3;
		final int testVariableCount = 5;
		List<CVTerm> propertyTerms = new ArrayList<>();
		TestDataHelper.fillTestPropertiesCvTerms(propertyTerms, testPropertyCount);

		Map<Integer, CVTerm> mapPropertyTerm = new HashMap<>();
		// save 3 properties using termDao
		for (CVTerm term : propertyTerms) {
			termDao.save(term);
			mapPropertyTerm.put(term.getCvTermId(), term);
		}

		final Integer isATermCount = 3;
		List<CVTerm> isATerms = TestDataHelper.generateNewIsATerms(isATermCount, this.termDao);

		//Assigning is_a to property. Avoid using loop or randomize for getting more control over assertion
		this.relationshipDao.save(propertyTerms.get(0).getCvTermId(), TermId.IS_A.getId(), isATerms.get(0).getCvTermId());
		this.relationshipDao.save(propertyTerms.get(0).getCvTermId(), TermId.IS_A.getId(), isATerms.get(1).getCvTermId());
		this.relationshipDao.save(propertyTerms.get(1).getCvTermId(), TermId.IS_A.getId(), isATerms.get(0).getCvTermId());
		this.relationshipDao.save(propertyTerms.get(2).getCvTermId(), TermId.IS_A.getId(), isATerms.get(2).getCvTermId());

		List<CVTerm> variableTerms = new ArrayList<>();
		TestDataHelper.fillTestVariableCvTerms(variableTerms, testVariableCount);
		for (CVTerm term : variableTerms) {
			termDao.save(term);
		}

		List<String> variableTypeNames = new ArrayList<>();

		for (int typeIndex = 0; typeIndex < 6; typeIndex ++){
			variableTypeNames.add(TestDataHelper.getNewRandomName("VT:"));
		}

		//Assigning variable types to variable. Avoid using loop or randomize for getting more control over assertion
		this.propertyDao.updateOrDeleteProperty(variableTerms.get(0).getCvTermId(), TermId.VARIABLE_TYPE.getId(), variableTypeNames.get(0), 0);
		this.propertyDao.updateOrDeleteProperty(variableTerms.get(0).getCvTermId(), TermId.VARIABLE_TYPE.getId(), variableTypeNames.get(1), 0);
		this.propertyDao.updateOrDeleteProperty(variableTerms.get(0).getCvTermId(), TermId.VARIABLE_TYPE.getId(), variableTypeNames.get(2), 0);
		this.propertyDao.updateOrDeleteProperty(variableTerms.get(1).getCvTermId(), TermId.VARIABLE_TYPE.getId(), variableTypeNames.get(0), 0);
		this.propertyDao.updateOrDeleteProperty(variableTerms.get(1).getCvTermId(), TermId.VARIABLE_TYPE.getId(), variableTypeNames.get(1), 0);
		this.propertyDao.updateOrDeleteProperty(variableTerms.get(2).getCvTermId(), TermId.VARIABLE_TYPE.getId(), variableTypeNames.get(3), 0);
		this.propertyDao.updateOrDeleteProperty(variableTerms.get(3).getCvTermId(), TermId.VARIABLE_TYPE.getId(), variableTypeNames.get(4), 0);
		this.propertyDao.updateOrDeleteProperty(variableTerms.get(4).getCvTermId(), TermId.VARIABLE_TYPE.getId(), variableTypeNames.get(5),
				0);

		//Assigning property to variable. Avoid using loop or randomize for getting more control over assertion
		this.relationshipDao.save(variableTerms.get(0).getCvTermId(), TermId.HAS_PROPERTY.getId(), propertyTerms.get(0).getCvTermId());
		this.relationshipDao.save(variableTerms.get(1).getCvTermId(), TermId.HAS_PROPERTY.getId(), propertyTerms.get(0).getCvTermId());
		this.relationshipDao.save(variableTerms.get(2).getCvTermId(), TermId.HAS_PROPERTY.getId(), propertyTerms.get(1).getCvTermId());
		this.relationshipDao.save(variableTerms.get(3).getCvTermId(), TermId.HAS_PROPERTY.getId(), propertyTerms.get(1).getCvTermId());
		this.relationshipDao.save(variableTerms.get(4).getCvTermId(), TermId.HAS_PROPERTY.getId(), propertyTerms.get(2).getCvTermId());


		//As per data inserted above expect isATerms(2) should return propertyTerms(2)
		final List<Property> result1 = this.manager.getAllPropertiesWithClassAndVariableType(new String[] {isATerms.get(2).getName()}, null);

		Assert.assertEquals(result1.size(), 1);
		Assert.assertEquals(result1.get(0).getId(), propertyTerms.get(2).getCvTermId().intValue());

		//will return single property list
		final List<Property> result2 = this.manager.getAllPropertiesWithClassAndVariableType(null, new String[] {variableTypeNames.get(0), variableTypeNames.get(1)});

		Assert.assertEquals(result2.size(), 1);
		Assert.assertEquals(result2.get(0).getId(), propertyTerms.get(0).getCvTermId().intValue());

		//will return empty list
		final List<Property> result3 = this.manager.getAllPropertiesWithClassAndVariableType(new String[] {isATerms.get(1).getName()}, new String[] {variableTypeNames.get(4)});

		Assert.assertTrue(result3.isEmpty());

		//will return 2 properties
		final List<Property> result4 = this.manager.getAllPropertiesWithClassAndVariableType(new String[] {isATerms.get(0).getName(),
				isATerms.get(1).getName()}, new String[] {variableTypeNames.get(1), variableTypeNames.get(3)});

		Assert.assertTrue(result4.size() == 2);
		Assert.assertTrue(mapPropertyTerm.keySet().contains(result4.get(0).getId()));
		Assert.assertTrue(mapPropertyTerm.keySet().contains(result4.get(1).getId()));
	}
}
