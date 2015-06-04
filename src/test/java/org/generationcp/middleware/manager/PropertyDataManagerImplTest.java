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

import java.util.List;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.manager.ontology.api.OntologyPropertyDataManager;
import org.generationcp.middleware.manager.ontology.api.TermDataManager;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Implements {@link DataManagerIntegrationTest}
 */
public class PropertyDataManagerImplTest extends DataManagerIntegrationTest {

	private static OntologyPropertyDataManager propertyDataManager;
	private static TermDataManager termDataManager;
	private static Property testProperty;

	private static String className = "newClass";

	@Before
	public void setUp() throws Exception {
		PropertyDataManagerImplTest.propertyDataManager = DataManagerIntegrationTest.managerFactory.getOntologyPropertyDataManager();
		PropertyDataManagerImplTest.termDataManager = DataManagerIntegrationTest.managerFactory.getTermDataManager();
		PropertyDataManagerImplTest.testProperty = new Property();
		PropertyDataManagerImplTest.testProperty.setName(MiddlewareIntegrationTest.getNewRandomName());
		PropertyDataManagerImplTest.testProperty.setDefinition("definition");
		PropertyDataManagerImplTest.testProperty.setCropOntologyId("CO_322:0000046");
		PropertyDataManagerImplTest.testProperty.addClass(PropertyDataManagerImplTest.className);
		PropertyDataManagerImplTest.testProperty.addClass(MiddlewareIntegrationTest.getNewRandomName());
		Debug.println("adding test property " + PropertyDataManagerImplTest.testProperty);
		PropertyDataManagerImplTest.propertyDataManager.addProperty(PropertyDataManagerImplTest.testProperty);
	}

	@Test
	public void testGetPropertyById() throws Exception {
		Property property = PropertyDataManagerImplTest.propertyDataManager.getProperty(PropertyDataManagerImplTest.testProperty.getId());
		Assert.assertNotNull(property);
		property.print(2);
	}

	@Test
	public void testGetAllPropertiesByClassName() throws Exception {
		List<Property> properties = PropertyDataManagerImplTest.propertyDataManager.getAllPropertiesWithClass("agronomic");
		for (Property p : properties) {
			p.print(2);
		}
		Debug.println("Properties: " + properties.size());
		Assert.assertTrue(properties.size() >= 10);
	}

	@Test
	public void testGetAllProperties() throws Exception {
		List<Property> properties = PropertyDataManagerImplTest.propertyDataManager.getAllProperties();
		for (Property p : properties) {
			p.print(2);
		}
		Debug.println("Properties: " + properties.size());
		Assert.assertTrue(properties.size() > 0);
	}

	@Test
	public void testUpdateProperty() throws Exception {
		PropertyDataManagerImplTest.testProperty.setDefinition("new definition");
		PropertyDataManagerImplTest.testProperty.setCropOntologyId("CO_322:0000047");
		PropertyDataManagerImplTest.testProperty.getClasses().clear();
		PropertyDataManagerImplTest.testProperty.addClass(MiddlewareIntegrationTest.getNewRandomName());
		PropertyDataManagerImplTest.propertyDataManager.updateProperty(PropertyDataManagerImplTest.testProperty);
		Property updatedProperty =
				PropertyDataManagerImplTest.propertyDataManager.getProperty(PropertyDataManagerImplTest.testProperty.getId());
		Assert.assertEquals(updatedProperty.getDefinition(), "new definition");
		Assert.assertEquals(updatedProperty.getCropOntologyId(), "CO_322:0000047");
		Assert.assertEquals(updatedProperty.getClasses().size(), 1);
		Assert.assertTrue(updatedProperty.getClasses().containsAll(PropertyDataManagerImplTest.testProperty.getClasses()));

		// className = newClass should delete on-fly when class is no longer used by any property.
		Term classTerm =
				PropertyDataManagerImplTest.termDataManager.getTermByNameAndCvId(PropertyDataManagerImplTest.className,
						CvId.TRAIT_CLASS.getId());
		Assert.assertNull(classTerm);
	}

	@After
	public void tearDown() throws Exception {
		PropertyDataManagerImplTest.propertyDataManager.deleteProperty(PropertyDataManagerImplTest.testProperty.getId());
	}
}
