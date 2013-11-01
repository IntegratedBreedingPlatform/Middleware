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
import org.generationcp.middleware.domain.oms.StandardVariableReference;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TraitClassReference;
import org.generationcp.middleware.hibernate.HibernateUtil;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.util.Debug;
import org.generationcp.middleware.domain.oms.PropertyReference;
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
    	List<String> nameOrSynonyms = Arrays.asList("ENTRY","ENTRYNO", "PLOT", "TRIAL_NO", "TRIAL", "STUDY", "DATASET", "LOC", "LOCN", "NURSER", "Plot Number");

    	Map<String, Set<Integer>> results = dao.getTermsByNameOrSynonyms(nameOrSynonyms, CvId.VARIABLES.getId());   
    	
        Debug.println(0, "testGetTermsByNameOrSynonyms(nameOrSynonyms=" + nameOrSynonyms + ") RESULTS:");
        for (String name : nameOrSynonyms) {
        	Debug.println(0, "    Name/Synonym = " + name + ", Terms = " + results.get(name));
        }
        
        /* SQL TO VERIFY:
			SELECT DISTINCT cvterm.name, syn.synonym, cvterm.cvterm_id  
			FROM cvterm, cvtermsynonym syn 
			WHERE cvterm.cv_id = 1040
			 AND (cvterm.name IN (:nameOrSynonyms) 
			      OR (syn.synonym IN (:nameOrSynonyms) AND syn.cvterm_id = cvterm.cvterm_id)) 
         */
        
    }
    
    @Test
    public void testGetByNameAndCvId() throws Exception {
    	CVTerm cvterm = dao.getByNameAndCvId("User", CvId.PROPERTIES.getId());
    	assertTrue(cvterm.getCvTermId() == 2002);
    	Debug.println(0, "testGetByNameAndCvId(\"User\", "+CvId.PROPERTIES.getId()+"): " + cvterm);

    	cvterm = dao.getByNameAndCvId("DBCV", CvId.SCALES.getId());
    	Debug.println(0, "testGetByNameAndCvId(\"DBCV\", "+CvId.SCALES.getId()+"): " + cvterm);
    	assertTrue(cvterm.getCvTermId() == 6000);

    	cvterm = dao.getByNameAndCvId("Assigned", CvId.METHODS.getId());
    	Debug.println(0, "testGetByNameAndCvId(\"Assigned\", "+CvId.METHODS.getId()+"): " + cvterm);
    	assertTrue(cvterm.getCvTermId() == 4030);
    	
    }
    
    @Test
    public void testGetTermsByNameOrSynonym() throws Exception {
    	List<Integer> termIds = dao.getTermsByNameOrSynonym("Cooperator", 1010);
    	Debug.println(0, "testGetTermsByNameOrSynonym(): " + termIds);
    }
    
    @Test
    public void testGetStandardVariableIdsByProperties() throws Exception {
    	List<String> nameOrSynonyms = Arrays.asList("ENTRY","ENTRYNO", "PLOT", "TRIAL_NO", "TRIAL", "STUDY", "DATASET", "LOC", "LOCN", "NURSER", "Plot Number");
    	Map<String, Set<Integer>> results = dao.getStandardVariableIdsByProperties(nameOrSynonyms);   
    	
        Debug.println(0, "testGetStandardVariableIdsByProperties(nameOrSynonyms=" + nameOrSynonyms + ") RESULTS:");
        for (String name : nameOrSynonyms) {
            Debug.println(0, "    Name/Synonym = " + name + ", Terms = " + results.get(name));
        }
        
        /* SQL TO VERIFY:
			SELECT DISTINCT cvtr.name, syn.synonym, cvt.cvterm_id
			FROM cvterm_relationship cvr
					INNER JOIN cvterm cvtr ON cvr.object_id = cvtr.cvterm_id AND cvr.type_id = 1200
					INNER JOIN cvterm cvt ON cvr.subject_id = cvt.cvterm_id AND cvt.cv_id = 1040
					, cvtermsynonym syn
			WHERE (cvtr.cvterm_id = syn.cvterm_id AND syn.synonym IN (:propertyNames) 
			    	OR cvtr.name IN (:propertyNames));
         */
    }

    @Test
    public void testGetOntologyTraitClasses() throws Exception {
        List<TraitClassReference> traitClasses = dao.getTraitClasses(TermId.ONTOLOGY_TRAIT_CLASS);
        assertTrue(traitClasses.size() > 0);
        Debug.println(4, "testGetOntologyTraitClasses(): " );
        for (TraitClassReference trait : traitClasses){
            Debug.println(8, trait.toString());
        }
    }

    @Test
    public void testGetOntologyResearchClasses() throws Exception {
        List<TraitClassReference> traitClasses = dao.getTraitClasses(TermId.ONTOLOGY_RESEARCH_CLASS);
        assertTrue(traitClasses.size() > 0);
        Debug.println(4, "testGetTraitClasses(): " );
        for (TraitClassReference trait : traitClasses){
            Debug.println(8, trait.toString());
        }
    }
    
    @Test
    public void testGetAllTraitClasses() throws Exception {
        List<TraitClassReference> traitClasses = dao.getAllTraitClasses();
        assertTrue(traitClasses.size() > 0);
        Debug.println(4, "testGetAllTraitClasses(): " );
        for (TraitClassReference trait : traitClasses){
            Debug.println(8, trait.toString());
        }
    }

    @Test
    public void testGetPropertiesOfTraitClasses() throws Exception {
        
        List<Integer> traitClassIds = Arrays.asList(1340, 1345, 1350, 1360, 1370, 1380, 1410, 22568);
        
        Map<Integer, List<PropertyReference>> traitClassProperties = dao.getPropertiesOfTraitClasses(traitClassIds);
        assertTrue(traitClassProperties.size() > 0);
        Debug.println(4, "testGetPropertiesOfTraitClasses(): " );
        for (Integer traitClassId : traitClassIds){
            List<PropertyReference> properties = traitClassProperties.get(traitClassId);
            if (properties != null){
                Debug.println(4, traitClassId + " (size = " + properties.size() + ") : " + properties);
                for (PropertyReference property : properties){
                    property.print(4);
                }
            } else {
                Debug.println(4, traitClassId + " (size = 0) : " + properties);
            }
        }
    }

    @Test
    public void testGetStandardVariablesOfProperties() throws Exception {
        
        List<Integer> propertyIds = Arrays.asList(1340, 2000, 2002, 2010, 2012, 2015, 2270);
        
        Map<Integer, List<StandardVariableReference>> propertyVariables = dao.getStandardVariablesOfProperties(propertyIds);
        assertTrue(propertyVariables.size() > 0);
        Debug.println(4, "testGetStandardVariablesOfProperties(): " );
        for (Integer id : propertyIds){
            List<StandardVariableReference> properties = propertyVariables.get(id);
            if (properties != null){
                Debug.println(4, id + " (size = " + properties.size() + ") : " + properties);
            } else {
                Debug.println(4, id + " (size = 0) : " + properties);
            }
        }
    }
    
    @AfterClass
    public static void tearDown() throws Exception {
        dao.setSession(null);
        dao = null;
        hibernateUtil.shutdown();
    }

}
