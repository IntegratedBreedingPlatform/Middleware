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
import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.manager.ontology.api.OntologyCommonDAO;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.util.Util;
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
		Date testCreatedDate = this.constructDate(2015, Calendar.JANUARY, 1);
		List<CVTermProperty> createdDateProperties = new ArrayList<>();
		TestDataHelper.fillTestCreatedDatePropertyProps(new ArrayList<>(propertyMap.values()), createdDateProperties, testCreatedDate);

		// Fill Test Updated Date Property
		Date testUpdatedDate = this.constructDate(2015, Calendar.JUNE, 30);
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
		Date testCreatedDate = this.constructDate(2015, Calendar.JANUARY, 1);
		List<CVTermProperty> createdDateProperties = new ArrayList<>();
		TestDataHelper.fillTestCreatedDatePropertyProps(new ArrayList<>(propertyMap.values()), createdDateProperties, testCreatedDate);

		// Fill Test Updated Date Property
		Date testUpdatedDate = this.constructDate(2015, Calendar.JUNE, 30);
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
}
