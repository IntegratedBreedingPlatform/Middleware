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

package org.generationcp.middleware.pojos.test;

import java.util.List;

import org.generationcp.middleware.hibernate.HibernateUtil;
import org.generationcp.middleware.pojos.CharacterData;
import org.generationcp.middleware.pojos.CharacterLevel;
import org.generationcp.middleware.pojos.Factor;
import org.generationcp.middleware.pojos.NumericData;
import org.generationcp.middleware.pojos.NumericLevel;
import org.generationcp.middleware.pojos.Oindex;
import org.generationcp.middleware.pojos.Representation;
import org.generationcp.middleware.pojos.ScaleContinuous;
import org.generationcp.middleware.pojos.ScaleDiscrete;
import org.generationcp.middleware.pojos.Study;
import org.generationcp.middleware.pojos.StudyEffect;
import org.generationcp.middleware.pojos.Trait;
import org.generationcp.middleware.pojos.TraitMethod;
import org.generationcp.middleware.pojos.Variate;
import org.generationcp.middleware.v2.pojos.CV;
import org.generationcp.middleware.v2.pojos.CVTerm;
import org.generationcp.middleware.v2.pojos.CVTermProperty;
import org.generationcp.middleware.v2.pojos.CVTermRelationship;
import org.generationcp.middleware.v2.pojos.CVTermSynonym;
import org.generationcp.middleware.v2.pojos.DmsProject;
import org.generationcp.middleware.v2.pojos.ExperimentModel;
import org.generationcp.middleware.v2.pojos.ExperimentPhenotype;
import org.generationcp.middleware.v2.pojos.ExperimentProject;
import org.generationcp.middleware.v2.pojos.ExperimentProperty;
import org.generationcp.middleware.v2.pojos.ExperimentStock;
import org.generationcp.middleware.v2.pojos.Geolocation;
import org.generationcp.middleware.v2.pojos.GeolocationProperty;
import org.generationcp.middleware.v2.pojos.Phenotype;
import org.generationcp.middleware.v2.pojos.ProjectProperty;
import org.generationcp.middleware.v2.pojos.ProjectRelationship;
import org.generationcp.middleware.v2.pojos.StockModel;
import org.generationcp.middleware.v2.pojos.StockProperty;
import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class DMSPojosSimpleTest{

    private static final String CONFIG = "test-hibernate.cfg.xml";
    private static HibernateUtil hibernateUtil;

    @BeforeClass
    public static void setUp() throws Exception {
        hibernateUtil = new HibernateUtil(CONFIG);
    }
    

    @Test
    public void testNumericData() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM NumericData");
        query.setMaxResults(5);
        List results = query.list();

        System.out.println("testNumericData() RESULTS: ");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof NumericData);
            Assert.assertTrue(obj != null);
            NumericData holder = (NumericData) obj;
            System.out.println("  " + holder);
        }
    }

    @Test
    public void testCharacterData() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM CharacterData");
        query.setMaxResults(5);
        List results = query.list();

        System.out.println("testCharacterData() RESULTS: ");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof CharacterData);
            Assert.assertTrue(obj != null);
            CharacterData holder = (CharacterData) obj;
            System.out.println("  " + holder);
        }
    }

    @Test
    public void testVariate() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM Variate");
        query.setMaxResults(5);
        List results = query.list();

        System.out.println("testVariate() RESULTS: ");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof Variate);
            Assert.assertTrue(obj != null);
            Variate holder = (Variate) obj;
            System.out.println("  " + holder);
        }
    }

    @Test
    public void testFactor() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM Factor");
        query.setMaxResults(5);
        List results = query.list();

        System.out.println("testFactor() RESULTS: ");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof Factor);
            Assert.assertTrue(obj != null);
            Factor holder = (Factor) obj;
            System.out.println("  " + holder);
        }
    }

    @Test
    public void testOindex() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM Oindex");
        query.setMaxResults(5);
        List results = query.list();

        System.out.println("testOindex() RESULTS: ");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof Oindex);
            Assert.assertTrue(obj != null);
            Oindex holder = (Oindex) obj;
            System.out.println("  " + holder);
        }
    }

    @Test
    public void testStudyEffect() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM StudyEffect");
        query.setMaxResults(5);
        List results = query.list();

        System.out.println("testStudyEffect() RESULTS: ");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof StudyEffect);
            Assert.assertTrue(obj != null);
            StudyEffect holder = (StudyEffect) obj;
            System.out.println("  " + holder);
        }
    }

    @Test
    public void testStudy() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM Study");
        query.setMaxResults(5);
        List results = query.list();

        System.out.println("testStudy() RESULTS: ");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof Study);
            Assert.assertTrue(obj != null);
            Study holder = (Study) obj;
            System.out.println("  " + holder);
        }
    }

    @Test
    public void testRepresentation() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM Representation");
        query.setMaxResults(5);
        List results = query.list();

        System.out.println("testRepresentation() RESULTS: ");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof Representation);
            Assert.assertTrue(obj != null);
            Representation holder = (Representation) obj;
            System.out.println("  " + holder);
        }
    }
    
    @Test
    public void testTrait() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM Trait");
        query.setMaxResults(5);
        List results = query.list();

        System.out.println("testTrait() RESULTS: ");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof Trait);
            Assert.assertTrue(obj != null);
            Trait holder = (Trait) obj;
            System.out.println("  " + holder);
        }
    }

    @Test
    public void testTraitMethod() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM TraitMethod");
        query.setMaxResults(5);
        List results = query.list();

        System.out.println("testTraitMethod() RESULTS: ");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof TraitMethod);
            Assert.assertTrue(obj != null);
            TraitMethod holder = (TraitMethod) obj;
            System.out.println("  " + holder);
        }
    }



    @Test
    public void testScaleDiscrete() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM ScaleDiscrete");
        query.setMaxResults(5);
        List results = query.list();

        System.out.println("testScaleDiscrete() RESULTS: ");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof ScaleDiscrete);
            Assert.assertTrue(obj != null);
            ScaleDiscrete holder = (ScaleDiscrete) obj;
            System.out.println("  " + holder);
        }
    }

    @Test
    public void testScaleContinuous() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM ScaleContinuous");
        query.setMaxResults(5);
        List results = query.list();

        System.out.println("testScaleContinuous() RESULTS: ");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof ScaleContinuous);
            Assert.assertTrue(obj != null);
            ScaleContinuous holder = (ScaleContinuous) obj;
            System.out.println("  " + holder);
        }
    }



    @Test
    public void testGeolocation() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM Geolocation");
        query.setMaxResults(5);
        List results = query.list();

        System.out.println("testGeolocation() RESULTS: ");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof Geolocation);
            Assert.assertTrue(obj != null);
            Geolocation holder = (Geolocation) obj;
            System.out.println("  " + holder);
        }
    }
    
    @Test
    public void testCV() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM CV");
        query.setMaxResults(10);
        List results = query.list();

        System.out.println("testCV() RESULTS: ");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof CV);
            Assert.assertTrue(obj != null);
            CV holder = (CV) obj;
            System.out.println("  " + holder);
        }
    }

    @Test
    public void testCVTerm() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM CVTerm");
        query.setMaxResults(10);
        List results = query.list();

        System.out.println("testCVTerm() RESULTS: ");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof CVTerm);
            Assert.assertTrue(obj != null);
            CVTerm holder = (CVTerm) obj;
            System.out.println("  " + holder);
        }
    }

    @Test
    public void testCVTermRelationship() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM CVTermRelationship");
        query.setMaxResults(10);
        List results = query.list();

        System.out.println("testCVTermRelationship() RESULTS: ");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof CVTermRelationship);
            Assert.assertTrue(obj != null);
            CVTermRelationship holder = (CVTermRelationship) obj;
            System.out.println("  " + holder);
        }
    }
    
    @Test
    public void testExperimentProperty() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM ExperimentProperty");
        query.setMaxResults(10);
        List results = query.list();

        System.out.println("testExperimentProperty() RESULTS: ");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof ExperimentProperty);
            Assert.assertTrue(obj != null);
            ExperimentProperty holder = (ExperimentProperty) obj;
            System.out.println("  " + holder);
        }
    }

    @Test
    public void testCVTermSynonym() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM CVTermSynonym");
        query.setMaxResults(10);
        List results = query.list();

        System.out.println("testCVTermSynonym() RESULTS: ");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof CVTermSynonym);
            Assert.assertTrue(obj != null);
            CVTermSynonym holder = (CVTermSynonym) obj;
            System.out.println("  " + holder);
        }
    }

    @Test
    public void testDmsProject() {
    	Session session = hibernateUtil.getCurrentSession();
    	Query query = session.createQuery("FROM DmsProject");
    	query.setMaxResults(5);
    	
    	System.out.println("testDmsProject() RESULTS: ");
    	for (Object obj : query.list()) {
    		Assert.assertTrue(obj instanceof DmsProject);
    		Assert.assertTrue(obj != null);
    		System.out.println(" " + obj);
    	}
    }

    @Test
    public void testProjectProperty() {
    	Session session = hibernateUtil.getCurrentSession();
    	Query query = session.createQuery("FROM ProjectProperty");
    	query.setMaxResults(5);
    	
    	System.out.println("testProjectProperty() RESULTS: ");
    	for (Object obj : query.list()) {
    		Assert.assertTrue(obj instanceof ProjectProperty);
    		Assert.assertTrue(obj != null);
    		System.out.println(" " + obj);
    	}
    }

    @Test
    public void testExperiment() {
    	Session session = hibernateUtil.getCurrentSession();
    	Query query = session.createQuery("FROM Experiment");
    	query.setMaxResults(5);
    	
    	System.out.println("testExperiment() RESULTS: ");
    	for (Object obj : query.list()) {
    		Assert.assertTrue(obj instanceof ExperimentModel);
    		Assert.assertTrue(obj != null);
    		System.out.println(" " + obj);
    	}
    }
    
    @Test
    public void testExperimentProject() {
    	Session session = hibernateUtil.getCurrentSession();
    	Query query = session.createQuery("FROM ExperimentProject");
    	query.setMaxResults(5);
    	
    	System.out.println("testExperimentProject() RESULTS: ");
    	for (Object obj : query.list()) {
    		Assert.assertTrue(obj instanceof ExperimentProject);
    		Assert.assertTrue(obj != null);
    		System.out.println(" " + obj);
    	}
    }
    
    @Test
    public void testExperimentPhenotype() {
    	Session session = hibernateUtil.getCurrentSession();
    	Query query = session.createQuery("FROM ExperimentPhenotype");
    	query.setMaxResults(5);
    	
    	System.out.println("testExperimentPhenotype() RESULTS: ");
    	for (Object obj : query.list()) {
    		Assert.assertTrue(obj instanceof ExperimentPhenotype);
    		Assert.assertTrue(obj != null);
    		System.out.println(" " + obj);
    	}
    }

    @Test
    public void testStock() {
    	Session session = hibernateUtil.getCurrentSession();
    	Query query = session.createQuery("FROM Stock");
    	query.setMaxResults(5);
    	
    	System.out.println("testStock() RESULTS: ");
    	for (Object obj : query.list()) {
    		Assert.assertTrue(obj instanceof StockModel);
    		Assert.assertTrue(obj != null);
    		System.out.println(" " + obj);
    	}
    }

    @Test
    public void testStockProperty() {
    	Session session = hibernateUtil.getCurrentSession();
    	Query query = session.createQuery("FROM StockProperty");
    	query.setMaxResults(5);
    	
    	System.out.println("testStockProperty() RESULTS: ");
    	for (Object obj : query.list()) {
    		Assert.assertTrue(obj instanceof StockProperty);
    		Assert.assertTrue(obj != null);
    		System.out.println(" " + obj);
    	}
    }


    @Test
    public void testPhenotype() {
    	Session session = hibernateUtil.getCurrentSession();
    	Query query = session.createQuery("FROM Phenotype");
    	query.setMaxResults(5);
    	
    	System.out.println("testPhenotype() RESULTS: ");
    	for (Object obj : query.list()) {
    		Assert.assertTrue(obj instanceof Phenotype);
    		Assert.assertTrue(obj != null);
    		System.out.println(" " + obj);
    	}
    }

    @Test
    public void testExperimentStock() {
    	Session session = hibernateUtil.getCurrentSession();
    	Query query = session.createQuery("FROM ExperimentStock");
    	query.setMaxResults(5);
    	
    	System.out.println("testExperimentStock() RESULTS: ");
    	for (Object obj : query.list()) {
    		Assert.assertTrue(obj instanceof ExperimentStock);
    		Assert.assertTrue(obj != null);
    		System.out.println(" " + obj);
    	}
    }
    
    @Test
    public void testGeolocationProperty() {
    	Session session = hibernateUtil.getCurrentSession();
    	Query query = session.createQuery("FROM GeolocationProperty");
    	query.setMaxResults(5);
    	
    	System.out.println("testGeolocationProperty() RESULTS: ");
    	for (Object obj : query.list()) {
    		Assert.assertTrue(obj instanceof GeolocationProperty);
    		Assert.assertTrue(obj != null);
    		System.out.println(" " + obj);
    	}
    }

    @Test
    public void testProjectRelationship() {
    	Session session = hibernateUtil.getCurrentSession();
    	Query query = session.createQuery("FROM ProjectRelationship");
    	query.setMaxResults(5);
    	
    	System.out.println("testProjectRelationship() RESULTS: ");
    	for (Object obj : query.list()) {
    		Assert.assertTrue(obj instanceof ProjectRelationship);
    		Assert.assertTrue(obj != null);
    		System.out.println(" " + obj);
    	}
    }

    @Test
    public void testCVTermProperty() {
    	Session session = hibernateUtil.getCurrentSession();
    	Query query = session.createQuery("FROM CVTermProperty");
    	query.setMaxResults(5);
    	
    	System.out.println("testCVTermProperty() RESULTS: ");
    	for (Object obj : query.list()) {
    		Assert.assertTrue(obj instanceof CVTermProperty);
    		Assert.assertTrue(obj != null);
    		System.out.println(" " + obj);
    	}
    }
    
    //====================   OLD SCHEMA (will be removed in the future ) ======================
    @Test
    public void testCharacterLevel() {
    	Session session = hibernateUtil.getCurrentSession();
    	Query query = session.createQuery("FROM CharacterLevel");
    	query.setMaxResults(5);
    	
    	System.out.println("testCharacterLevel() RESULTS: ");
    	for (Object obj : query.list()) {
    		Assert.assertTrue(obj instanceof CharacterLevel);
    		Assert.assertTrue(obj != null);
    		System.out.println(" " + obj);
    	}
    }

    @Test
    public void testNumericLevel() {
    	Session session = hibernateUtil.getCurrentSession();
    	Query query = session.createQuery("FROM NumericLevel");
    	query.setMaxResults(5);
    	
    	System.out.println("testNumericLevel() RESULTS: ");
    	for (Object obj : query.list()) {
    		Assert.assertTrue(obj instanceof NumericLevel);
    		Assert.assertTrue(obj != null);
    		System.out.println(" " + obj);
    	}
    }

    @AfterClass
    public static void tearDown() throws Exception {
        hibernateUtil.shutdown();
    }

}
