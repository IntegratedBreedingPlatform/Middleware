/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.middleware.manager;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.domain.oms.Property;
import org.generationcp.middleware.manager.ontology.api.OntologyPropertyDataManager;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class OntologyPropertyDataManagerImplTest extends DataManagerIntegrationTest {

	private static OntologyPropertyDataManager propertyDataManager;
    private static Property testProperty;

	@Before
	public void setUp() throws Exception {
		propertyDataManager = DataManagerIntegrationTest.managerFactory.getOntologyPropertyDataManager();
        testProperty = new Property();
        testProperty.setName(getNewRandomName());
        testProperty.setDefinition("definition");
        testProperty.setCropOntologyId("CO_322:0000046");
        testProperty.addClass("newClass");
        testProperty.addClass(getNewRandomName());
        Debug.println("adding test property " + testProperty);
        propertyDataManager.addProperty(testProperty);
    }

    @Test
    public void testGetPropertyById() throws Exception {
        Property property = propertyDataManager.getProperty(testProperty.getId());
        assertNotNull(property);
        property.print(2);
    }

    @Test
    public void testGetAllPropertiesByClassName() throws Exception {
        List<Property> properties = propertyDataManager.getAllPropertiesWithClass("agronomic");
        for(Property p : properties){
            p.print(2);
        }
        Debug.println("Properties: " + properties.size());
        assertTrue(properties.size() >= 10);
    }

    @Test
    public void testGetAllProperties() throws Exception {
        List<Property> properties = propertyDataManager.getAllProperties();
        for(Property p : properties){
            p.print(2);
        }
        Debug.println("Properties: " + properties.size());
        assertTrue(properties.size() > 0);
    }

    @Test
    public void testUpdateProperty() throws Exception {
        testProperty.setDefinition("new definition");
        testProperty.setCropOntologyId("CO_322:0000047");
        testProperty.getClasses().clear();
        testProperty.addClass(getNewRandomName());
        propertyDataManager.updateProperty(testProperty);
        Property updatedProperty = propertyDataManager.getProperty(testProperty.getId());
        assertEquals(updatedProperty.getDefinition(), "new definition");
        assertEquals(updatedProperty.getCropOntologyId(), "CO_322:0000047");
        assertEquals(updatedProperty.getClasses().size(), 1);
        assertTrue(updatedProperty.getClasses().containsAll(testProperty.getClasses()));
    }

    @After
    public void tearDown() throws Exception {
        propertyDataManager.deleteProperty(testProperty.getId());
    }
}
