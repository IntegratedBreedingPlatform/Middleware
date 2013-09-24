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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.hibernate.HibernateUtil;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestProjectPropertyDao{

    private static HibernateUtil hibernateUtil;
    private static ProjectPropertyDao dao;

    @BeforeClass
    public static void setUp() throws Exception {
        hibernateUtil = new HibernateUtil(new DatabaseConnectionParameters("testDatabaseConfig.properties", "central"));
        dao = new ProjectPropertyDao();
        dao.setSession(hibernateUtil.getCurrentSession());
    }

    @Test
    public void testGetStandardVariableIdsByPropertyNames() throws Exception {
    	List<String> propertyNames = Arrays.asList("ENTRY","ENTRYNO","PLOT", "TRIAL_NO", "TRIAL", "STUDY", "DATASET");
    			
    	Map<String, Set<Integer>> results = dao.getStandardVariableIdsByPropertyNames(propertyNames);

        System.out.println("testGetStandardVariableIdsByPropertyNames(propertyNames=" + propertyNames + ") RESULTS:");
        for (String name : propertyNames) {
        	System.out.println ("    Header = " + name + ", Terms = " + results.get(name));
        }
    }
    
    @AfterClass
    public static void tearDown() throws Exception {
        dao.setSession(null);
        dao = null;
        hibernateUtil.shutdown();
    }

}
