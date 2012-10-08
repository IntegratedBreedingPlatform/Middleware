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

import org.generationcp.middleware.pojos.Scale;
import org.generationcp.middleware.pojos.ScaleContinuous;
import org.generationcp.middleware.pojos.ScaleDiscrete;
import org.generationcp.middleware.pojos.Trait;
import org.generationcp.middleware.pojos.TraitMethod;
import org.generationcp.middleware.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TMSPojosSimpleTest{

    private static final String CONFIG = "test-hibernate.cfg.xml";
    private HibernateUtil hibernateUtil;

    @Before
    public void setUp() throws Exception {
        hibernateUtil = new HibernateUtil(CONFIG);
    }

    @Test
    public void testScale() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM Scale");
        query.setMaxResults(5);
        List results = query.list();

        System.out.println("testScale() RESULTS:");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof Scale);
            Assert.assertTrue(obj != null);
            Scale holder = (Scale) obj;
            System.out.println("  " + holder);
        }
    }

    @Test
    public void testScaleContinuous() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM ScaleContinuous");
        query.setMaxResults(5);
        List results = query.list();

        System.out.println("testScaleContinuous() RESULTS:");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof ScaleContinuous);
            Assert.assertTrue(obj != null);
            ScaleContinuous holder = (ScaleContinuous) obj;
            System.out.println("  " + holder);
        }
    }

    @Test
    public void testScaleDiscrete() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM ScaleDiscrete");
        query.setMaxResults(5);
        List results = query.list();

        System.out.println("testScaleDiscrete() RESULTS:");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof ScaleDiscrete);
            Assert.assertTrue(obj != null);
            ScaleDiscrete holder = (ScaleDiscrete) obj;
            System.out.println("  " + holder);
        }
    }

    @Test
    public void testTraitMethod() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM TraitMethod");
        query.setMaxResults(5);
        List results = query.list();

        System.out.println("testTraitMethod() RESULTS:");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof TraitMethod);
            Assert.assertTrue(obj != null);
            TraitMethod holder = (TraitMethod) obj;
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

    @After
    public void tearDown() throws Exception {
        hibernateUtil.shutdown();
    }
}
