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

import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.hibernate.HibernateUtil;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestCvTermDao{

    private static HibernateUtil hibernateUtil;
    private static CVTermDao dao;

    @BeforeClass
    public static void setUp() throws Exception {
        hibernateUtil = new HibernateUtil(new DatabaseConnectionParameters("testDatabaseConfig.properties", "central"));
        dao = new CVTermDao();
        dao.setSession(hibernateUtil.getCurrentSession());
    }

    @Test
    public void testGetTermsByNameOrSynonyms() throws Exception {
    	List<String> nameOrSynonyms = Arrays.asList("ENTRY","ENTRYNO","PLOT", "TRIAL_NO", "TRIAL", "STUDY", "DATASET");

    	Map<String, Set<Integer>> results = dao.getTermsByNameOrSynonyms(nameOrSynonyms, CvId.VARIABLES.getId());   
    	
        System.out.println("testGetTermsByNameOrSynonyms(nameOrSynonyms=" + nameOrSynonyms + ") RESULTS:");
        for (String name : nameOrSynonyms) {
        	System.out.println ("    Name/Synonym = " + name + ", Terms = " + results.get(name));
        }
    }
    
    @Test
    public void testGetByNameAndCvId() throws Exception {
    	CVTerm cvterm = dao.getByNameAndCvId("User", CvId.PROPERTIES.getId());
    	assertTrue(cvterm.getCvTermId() == 2002);
    	System.out.println("testGetByNameAndCvId(\"User\", "+CvId.PROPERTIES.getId()+"): " + cvterm);

    	cvterm = dao.getByNameAndCvId("DBCV", CvId.SCALES.getId());
    	System.out.println("testGetByNameAndCvId(\"DBCV\", "+CvId.SCALES.getId()+"): " + cvterm);
    	assertTrue(cvterm.getCvTermId() == 6000);

    	cvterm = dao.getByNameAndCvId("Assigned", CvId.METHODS.getId());
    	System.out.println("testGetByNameAndCvId(\"Assigned\", "+CvId.METHODS.getId()+"): " + cvterm);
    	assertTrue(cvterm.getCvTermId() == 4030);
    	
    }
    @Test
    public void testGetStandardVariableIdsByProperties() throws Exception {
    	List<String> nameOrSynonyms = Arrays.asList("ENTRY","ENTRYNO","PLOT", "TRIAL_NO", "TRIAL", "STUDY", "DATASET");
    	Map<String, Set<Integer>> results = dao.getStandardVariableIdsByProperties(nameOrSynonyms);   
    	
        System.out.println("testGetStandardVariableIdsByProperties(nameOrSynonyms=" + nameOrSynonyms + ") RESULTS:");
        for (String name : nameOrSynonyms) {
        	System.out.println ("    Name/Synonym = " + name + ", Terms = " + results.get(name));
        }
        
        /* SQL TO VERIFY:
			SELECT DISTINCT cvtr.name, syn.synonym, cvt.cvterm_id
			FROM cvterm_relationship cvr
			INNER JOIN cvterm cvtr ON cvr.object_id = cvtr.cvterm_id AND cvr.type_id = 1200
			INNER JOIN cvterm cvt ON cvr.subject_id = cvt.cvterm_id AND cvt.cv_id = 1040
			, cvtermsynonym syn
			WHERE (cvtr.cvterm_id = syn.cvterm_id AND syn.synonym IN (:propertyNames) 
			    OR cvtr.name IN (:propertyNames);
         */
    }

    
    
    @AfterClass
    public static void tearDown() throws Exception {
        dao.setSession(null);
        dao = null;
        hibernateUtil.shutdown();
    }

}
