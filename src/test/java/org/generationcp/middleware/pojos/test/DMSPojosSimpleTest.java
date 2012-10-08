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

import org.generationcp.middleware.pojos.CharacterData;
import org.generationcp.middleware.pojos.Factor;
import org.generationcp.middleware.pojos.NumericData;
import org.generationcp.middleware.pojos.Oindex;
import org.generationcp.middleware.pojos.Representation;
import org.generationcp.middleware.pojos.Study;
import org.generationcp.middleware.pojos.StudyEffect;
import org.generationcp.middleware.pojos.Variate;
import org.generationcp.middleware.util.HibernateUtil;
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

    @AfterClass
    public static void tearDown() throws Exception {
        hibernateUtil.shutdown();
    }

}
