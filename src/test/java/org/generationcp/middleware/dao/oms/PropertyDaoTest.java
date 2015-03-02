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
import org.generationcp.middleware.domain.oms.Property;
import org.generationcp.middleware.MiddlewareIntegrationTest;

import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import static org.junit.Assert.assertNotNull;

public class PropertyDaoTest extends MiddlewareIntegrationTest {

    private static PropertyDao dao;

    @BeforeClass
    public static void setUp() throws Exception {
        dao = new PropertyDao();
        dao.setSession(sessionUtil.getCurrentSession());
    }

    @Test
    public void testGetProperties() throws Exception {
        Property property = dao.getPropertyById(1050);
        assertNotNull(property);
        property.print(2);
    }
    
    @AfterClass
    public static void tearDown() throws Exception {
        dao.setSession(null);
        dao = null;
    }

}
