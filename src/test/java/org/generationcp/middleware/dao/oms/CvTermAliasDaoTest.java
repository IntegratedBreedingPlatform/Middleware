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
import org.generationcp.middleware.pojos.oms.CVTermAlias;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CvTermAliasDaoTest extends MiddlewareIntegrationTest {

    private static CvTermAliasDao dao;

    @BeforeClass
    public static void setUp() throws Exception {
        dao = new CvTermAliasDao();
        dao.setSession(sessionUtil.getCurrentSession());
    }

    @Test
    public void testSaveVariableAlias() throws Exception {
        CVTermAlias alias = dao.save(18000, 1, "Grain yield per unit area");
        dao.flush();
        CVTermAlias savedAlias = dao.getByCvTermAndProgram(18000, 1);
        assertNotNull(alias.getCvTermAliasId());
        assertEquals(alias.getCvTermAliasId(), savedAlias.getCvTermAliasId());
    }

    @AfterClass
    public static void tearDown() throws Exception {
        dao.setSession(null);
        dao = null;
    }

}
