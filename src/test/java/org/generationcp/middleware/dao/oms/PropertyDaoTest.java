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
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.utils.test.Debug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class PropertyDaoTest extends MiddlewareIntegrationTest {

    private static PropertyDao dao;

    @BeforeClass
    public static void setUp() throws Exception {
        dao = new PropertyDao();
        dao.setSession(sessionUtil.getCurrentSession());
    }

    @Test
    public void testGenericTest() throws Exception {
        List<CVTerm> terms = dao.filterByColumnValue(CVTerm.class, "name", "Project");
        System.out.println(terms.size());
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
        assertTrue(properties.size() == 14);
    }

    @Test
    public void testGetAllPropertiesByClassNames() throws Exception {
        List<Property> properties = dao.getAllPropertiesWithClasses(new ArrayList<>(Arrays.asList("Agronomic", "Biotic stress")));
        for(Property p : properties){
            p.print(2);
        }
        assertTrue(properties.size() > 0);
    }

    @Test
    public void testGetAllPropertiesByFilter() throws Exception {
        List<Property> properties = dao.searchProperties("study");
        for(Property p : properties){
            p.print(2);
        }
        assertTrue(properties.size() > 0);
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
    
    @AfterClass
    public static void tearDown() throws Exception {
        dao.setSession(null);
        dao = null;
    }

}
