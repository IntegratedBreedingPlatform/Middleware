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

import java.util.List;

import org.generationcp.middleware.dao.dms.ExperimentPropertyDao;
import org.generationcp.middleware.domain.fieldbook.FieldMapDatasetInfo;
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
        hibernateUtil = new HibernateUtil(new DatabaseConnectionParameters("testDatabaseConfig.properties", "central"));
        dao = new ExperimentPropertyDao();
        dao.setSession(hibernateUtil.getCurrentSession());
    }


    @Test
    public void testGetFieldMapLabels() throws Exception {
        int projectId = 5734; //5790;
        List<FieldMapDatasetInfo> datasets = dao.getFieldMapLabels(projectId);
        Debug.println(0, "testGetFieldMapLabels(projectId=" + projectId + ") RESULTS:");
        for (FieldMapDatasetInfo dataset: datasets) {
        	Debug.println(3, dataset.toString());
        }
        assertFalse(datasets.isEmpty());
    }
    
    @AfterClass
    public static void tearDown() throws Exception {
        dao.setSession(null);
        dao = null;
        hibernateUtil.shutdown();
    }

}
