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

package org.generationcp.middleware.dao.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.generationcp.middleware.dao.dms.ExperimentPropertyDao;
import org.generationcp.middleware.hibernate.HibernateUtil;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.util.Debug;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestExperimentPropertyDao{

    private static HibernateUtil hibernateUtil;
    private static ExperimentPropertyDao dao;

    @BeforeClass
    public static void setUp() throws Exception {
        hibernateUtil = new HibernateUtil(new DatabaseConnectionParameters("testDatabaseConfig.properties", "local"));
        dao = new ExperimentPropertyDao();
        dao.setSession(hibernateUtil.getCurrentSession());
    }


    @Test
    public void testGetReps() throws Exception {
        int projectId = -147;
        List<Integer> reps = dao.getRepsOfProject(projectId);
        Debug.println(0, "testGetStocks(projectId=" + projectId + ") RESULTS:");
        for (Integer rep: reps) {
        	Debug.println(3, rep.toString());
        }
        assertFalse(reps.isEmpty());
    }
    
    
    @Test
    public void testGetPlotCount() throws Exception {
        int projectId = -138;
        long plotCount = dao.getPlotCount(projectId);
        Debug.println(0, "testGetPlotCount(projectId=" + projectId + ") = " + plotCount);
        assertTrue(plotCount != 0);
    }
    
    
    @AfterClass
    public static void tearDown() throws Exception {
        dao.setSession(null);
        dao = null;
        hibernateUtil.shutdown();
    }

}
