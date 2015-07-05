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
public class OntologyPropertyDataManagerImplIntegrationTest extends DataManagerIntegrationTest {

	private static OntologyPropertyDataManager propertyDataManager;
	private static TermDataManager termDataManager;
	private static Property testProperty;

	private static String className = "newClass";

	@Before
	public void setUp() throws Exception {
		OntologyPropertyDataManagerImplIntegrationTest.propertyDataManager = DataManagerIntegrationTest.managerFactory.getOntologyPropertyDataManager();
		OntologyPropertyDataManagerImplIntegrationTest.termDataManager = DataManagerIntegrationTest.managerFactory.getTermDataManager();
		OntologyPropertyDataManagerImplIntegrationTest.testProperty = new Property();
		OntologyPropertyDataManagerImplIntegrationTest.testProperty.setName(MiddlewareIntegrationTest.getNewRandomName());
		OntologyPropertyDataManagerImplIntegrationTest.testProperty.setDefinition("definition");
		OntologyPropertyDataManagerImplIntegrationTest.testProperty.setCropOntologyId("CO_322:0000046");
		OntologyPropertyDataManagerImplIntegrationTest.testProperty.addClass(OntologyPropertyDataManagerImplIntegrationTest.className);
		OntologyPropertyDataManagerImplIntegrationTest.testProperty.addClass(MiddlewareIntegrationTest.getNewRandomName());
		Debug.println("adding test property " + OntologyPropertyDataManagerImplIntegrationTest.testProperty);
		OntologyPropertyDataManagerImplIntegrationTest.propertyDataManager.addProperty(OntologyPropertyDataManagerImplIntegrationTest.testProperty);
	}

	@Test
	public void testGetPropertyById() throws Exception {
		Property property = OntologyPropertyDataManagerImplIntegrationTest.propertyDataManager.getProperty(OntologyPropertyDataManagerImplIntegrationTest.testProperty.getId());
		Assert.assertNotNull(property);
		property.print(2);
	}

	@Test
	public void testGetAllPropertiesByClassName() throws Exception {
		List<Property> properties = OntologyPropertyDataManagerImplIntegrationTest.propertyDataManager.getAllPropertiesWithClass("agronomic");
		for (Property p : properties) {
			p.print(2);
		}
		Debug.println("Properties: " + properties.size());
		Assert.assertTrue(properties.size() >= 10);
	}

	@Test
	public void testGetAllProperties() throws Exception {
		List<Property> properties = OntologyPropertyDataManagerImplIntegrationTest.propertyDataManager.getAllProperties();
		for (Property p : properties) {
			p.print(2);
		}
		Debug.println("Properties: " + properties.size());
		Assert.assertTrue(properties.size() > 0);
	}

	@Test
	public void testUpdateProperty() throws Exception {
		OntologyPropertyDataManagerImplIntegrationTest.testProperty.setDefinition("new definition");
		OntologyPropertyDataManagerImplIntegrationTest.testProperty.setCropOntologyId("CO_322:0000047");
		OntologyPropertyDataManagerImplIntegrationTest.testProperty.getClasses().clear();
		OntologyPropertyDataManagerImplIntegrationTest.testProperty.addClass(MiddlewareIntegrationTest.getNewRandomName());
		OntologyPropertyDataManagerImplIntegrationTest.propertyDataManager.updateProperty(OntologyPropertyDataManagerImplIntegrationTest.testProperty);
		Property updatedProperty =
				OntologyPropertyDataManagerImplIntegrationTest.propertyDataManager.getProperty(OntologyPropertyDataManagerImplIntegrationTest.testProperty.getId());
		Assert.assertEquals(updatedProperty.getDefinition(), "new definition");
		Assert.assertEquals(updatedProperty.getCropOntologyId(), "CO_322:0000047");
		Assert.assertEquals(updatedProperty.getClasses().size(), 1);
		Assert.assertTrue(updatedProperty.getClasses().containsAll(OntologyPropertyDataManagerImplIntegrationTest.testProperty.getClasses()));

		// className = newClass should delete on-fly when class is no longer used by any property.
		Term classTerm =
				OntologyPropertyDataManagerImplIntegrationTest.termDataManager.getTermByNameAndCvId(OntologyPropertyDataManagerImplIntegrationTest.className,
						CvId.TRAIT_CLASS.getId());
		Assert.assertNull(classTerm);
	}

	@After
	public void tearDown() throws Exception {
		OntologyPropertyDataManagerImplIntegrationTest.propertyDataManager.deleteProperty(OntologyPropertyDataManagerImplIntegrationTest.testProperty.getId());
	}
}
