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

package org.generationcp.middleware.pojos.gdms.test;

import java.util.List;

import org.generationcp.middleware.pojos.gdms.AccMetadataSet;
import org.generationcp.middleware.pojos.gdms.AlleleValues;
import org.generationcp.middleware.pojos.gdms.CharValues;
import org.generationcp.middleware.pojos.gdms.Dataset;
import org.generationcp.middleware.pojos.gdms.Map;
import org.generationcp.middleware.pojos.gdms.MappingData;
import org.generationcp.middleware.pojos.gdms.MappingPop;
import org.generationcp.middleware.pojos.gdms.MappingPopValues;
import org.generationcp.middleware.pojos.gdms.Marker;
import org.generationcp.middleware.pojos.gdms.MarkerMetadataSet;
import org.generationcp.middleware.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("rawtypes")
public class PojosSimpleTest{

    private static final String CONFIG = "test-hibernate.cfg.xml";
    private HibernateUtil hibernateUtil;

    @Before
    public void setUp() throws Exception {
        hibernateUtil = new HibernateUtil(CONFIG);
    }

    @Test
    public void testAccMetadataSet() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM AccMetadataSet");
        query.setMaxResults(5);
        List results = query.list();

        System.out.println("testAccMetadataSet() RESULTS: ");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof AccMetadataSet);
            Assert.assertTrue(obj != null);
            AccMetadataSet element = (AccMetadataSet) obj;
            System.out.println("  " + element);
        }
    }

    @Test
    public void testAlleleValues() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM AlleleValues");
        query.setMaxResults(5);
        List results = query.list();

        System.out.println("testAlleleValues() RESULTS: ");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof AlleleValues);
            Assert.assertTrue(obj != null);
            AlleleValues element = (AlleleValues) obj;
            System.out.println("  " + element);
        }
    }

    @Test
    public void testCharValues() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM CharValues");
        query.setMaxResults(5);
        List results = query.list();

        System.out.println("testCharValues() RESULTS: ");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof CharValues);
            Assert.assertTrue(obj != null);
            CharValues element = (CharValues) obj;
            System.out.println("  " + element);
        }
    }

    @Test
    public void testDataset() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM Dataset");
        query.setMaxResults(5);
        List results = query.list();

        System.out.println("testDataset() RESULTS: ");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof Dataset);
            Assert.assertTrue(obj != null);
            Dataset element = (Dataset) obj;
            System.out.println("  " + element);
        }
    }

    @Test
    public void testMap() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM Map");
        query.setMaxResults(5);
        List results = query.list();

        System.out.println("testMap() RESULTS: ");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof Map);
            Assert.assertTrue(obj != null);
            Map element = (Map) obj;
            System.out.println("  " + element);
        }
    }

    @Test
    public void testMappingData() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM MappingData");
        query.setMaxResults(5);
        List results = query.list();

        System.out.println("testMappingData() RESULTS: ");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof MappingData);
            Assert.assertTrue(obj != null);
            MappingData element = (MappingData) obj;
            System.out.println("  " + element);
        }
    }

    @Test
    public void testMappingPop() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM MappingPop");
        query.setMaxResults(5);
        List results = query.list();

        System.out.println("testMappingPop() RESULTS: ");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof MappingPop);
            Assert.assertTrue(obj != null);
            MappingPop element = (MappingPop) obj;
            System.out.println("  " + element);
        }
    }

    @Test
    public void testMappingPopValues() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM MappingPopValues");
        query.setMaxResults(5);
        List results = query.list();

        System.out.println("testMappingPopValues() RESULTS: ");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof MappingPopValues);
            Assert.assertTrue(obj != null);
            MappingPopValues element = (MappingPopValues) obj;
            System.out.println("  " + element);
        }
    }

    @Test
    public void testMarker() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM Marker");
        query.setMaxResults(5);
        List results = query.list();

        System.out.println("testMarker() RESULTS: ");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof Marker);
            Assert.assertTrue(obj != null);
            Marker element = (Marker) obj;
            System.out.println("  " + element);
        }
    }

    @Test
    public void testMarkerMetadataSet() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM MarkerMetadataSet");
        query.setMaxResults(5);
        List results = query.list();

        System.out.println("testMarkerMetadataSet() RESULTS: ");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof MarkerMetadataSet);
            Assert.assertTrue(obj != null);
            MarkerMetadataSet element = (MarkerMetadataSet) obj;
            System.out.println("  " + element);
        }
    }

    @After
    public void tearDown() throws Exception {
        hibernateUtil.shutdown();
    }

}
