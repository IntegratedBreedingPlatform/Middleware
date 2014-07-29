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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.hibernate.HibernateUtil;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.util.Debug;
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
    	List<String> propertyNames = Arrays.asList("ENTRY","ENTRYNO", "PLOT", "TRIAL_NO", "TRIAL", "STUDY", "DATASET", "LOC", "LOCN", "NURSER", "Plot Number");
    			
    	Map<String, Set<Integer>> results = dao.getStandardVariableIdsByPropertyNames(propertyNames);

        Debug.println(0, "testGetStandardVariableIdsByPropertyNames(propertyNames=" + propertyNames + ") RESULTS:");
        for (String name : propertyNames) {
        	Debug.println(0, "    Header = " + name + ", Terms = " + results.get(name));
        	
        	/* TO VERIFY:
        	 	SELECT DISTINCT ppValue.value, ppStdVar.id 
				FROM projectprop ppValue 
					INNER JOIN (SELECT project_id, value id FROM projectprop WHERE type_id = 1070) AS ppStdVar  
					 		ON ppValue.project_id = ppStdVar.project_id AND ppValue.type_id = 1060 
					 				AND ppValue.value IN (:propertyNames)  
        	 */
        }
    }
    
    @AfterClass
    public static void tearDown() throws Exception {
        dao.setSession(null);
        dao = null;
        hibernateUtil.shutdown();
    }

}
