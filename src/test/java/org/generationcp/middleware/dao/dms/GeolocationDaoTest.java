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

package org.generationcp.middleware.dao.dms;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.util.Debug;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class GeolocationDaoTest extends MiddlewareIntegrationTest {

    private static GeolocationDao dao;

    @BeforeClass
    public static void setUp() throws Exception {
        dao = new GeolocationDao();
        dao.setSession(sessionUtil.getCurrentSession());
    }


    @Test
    public void testGetTrialEnvironmentDetails() throws Exception {
        Set<Integer> environmentIds = new HashSet<Integer>();
        environmentIds.add(5822);
        List<TrialEnvironment> results = dao.getTrialEnvironmentDetails(environmentIds);
        Debug.println(0, "testGetTrialEnvironmentDetails(environmentIds=" + environmentIds + ") RESULTS:");
        for (TrialEnvironment env : results) {
        	env.print(4);
        }
    }
    
    @AfterClass
    public static void tearDown() throws Exception {
        dao.setSession(null);
        dao = null;
    }

}
