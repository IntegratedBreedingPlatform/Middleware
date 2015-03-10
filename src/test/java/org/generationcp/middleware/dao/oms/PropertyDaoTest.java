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

package org.generationcp.middleware.dao.oms;
import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.domain.oms.Property;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.pojos.ErrorCode;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;


public class PropertyDaoTest extends MiddlewareIntegrationTest {
    
    private static PropertyDao dao;

    @BeforeClass
    public static void setUp() throws Exception {
        dao = new PropertyDao();
        dao.setSession(sessionUtil.getCurrentSession());
    }
    
    @Test
    public void testGetPropertyById() throws Exception {
        Property property = dao.getPropertyById(2020);
        assertNotNull(property);
        property.print(2);
    }

    @Test
    public void testGetAllPropertiesByClassName() throws Exception {
        List<Property> properties = dao.getAllPropertiesWithClass("agronomic");
        for(Property p : properties){
            p.print(2);
        }
        Debug.println("Properties: " + properties.size());
        assertTrue(properties.size() >= 14);
    }

    @Test
    public void testGetAllProperties() throws Exception {
        List<Property> properties = dao.getAllProperties();
        for(Property p : properties){
            p.print(2);
        }
        Debug.println("Properties: " + properties.size());
        assertTrue(properties.size() > 0);
    }

    @Test
    public void testSaveProperty() throws Exception {
        Property property = dao.addProperty(getNewRandomName(), "test", "CO_322:0000046", new ArrayList<>(Arrays.asList("Agronomic")));
        property.print(INDENT);
        assertNotNull(property.getId());
        assertEquals(property.getDefinition(), "test");
        assertEquals(property.getClasses().size(), 1);
        assertEquals(property.getCropOntologyId(), "CO_322:0000046");
    }

    @Test
    public void testUpdateProperty() throws Exception {
        Property property = dao.addProperty(getNewRandomName(), "test", "CO_322:0000046", new ArrayList<>(Arrays.asList("Agronomic")));
        dao.updateProperty(property.getId(), property.getName(), "update", "CO_322:0000047", new ArrayList<>(Arrays.asList("Biotic stress")));
        Property updatedProperty = dao.getPropertyById(property.getId());
        updatedProperty.print(INDENT);
        assertEquals(updatedProperty.getDefinition(), "update");
        assertEquals(updatedProperty.getCropOntologyId(), "CO_322:0000047");
        assertEquals(updatedProperty.getClasses().size(), 1);
        assertEquals(updatedProperty.getClasses().get(0).getName(), "Biotic stress");
    }

    @Test
    public void testDeleteProperty() throws Exception {
        Property property = dao.addProperty(getNewRandomName(), "test", "CO_322:0000046", new ArrayList<>(Arrays.asList("Agronomic")));
        property.print(INDENT);
        dao.delete(property.getId());
        assertNull(dao.getPropertyById(property.getId()));
    }

    @Test
    public void testDeletePropertyForI18nMessage() throws Exception {
        try{
            dao.delete(0);    
        } catch (MiddlewareException e){
            assertEquals(e.getMessageKey(), ErrorCode.ENTITY_NOT_FOUND);
            assertEquals(e.getMessage(), String.format("Term for Property does not exist with id:%d", 0));
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        dao.setSession(null);
        dao = null;
    }
}
