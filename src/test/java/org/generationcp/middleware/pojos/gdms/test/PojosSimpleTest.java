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

import org.generationcp.middleware.hibernate.HibernateUtil;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
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
import org.generationcp.middleware.utils.test.Debug;
import org.generationcp.middleware.utils.test.TestOutputFormatter;
import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("rawtypes")
public class PojosSimpleTest extends TestOutputFormatter{

    private static HibernateUtil hibernateUtil;

    @BeforeClass
    public static void setUp() throws Exception {
        hibernateUtil = new HibernateUtil(new DatabaseConnectionParameters("testDatabaseConfig.properties", "central"));
    }
    
    @Test
    public void testAccMetadataSet() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM AccMetadataSet");
        query.setMaxResults(5);
        List results = query.list();

        for (Object obj : results) {
            Assert.assertTrue(obj instanceof AccMetadataSet);
            Assert.assertTrue(obj != null);
            AccMetadataSet element = (AccMetadataSet) obj;
            Debug.println(INDENT, element);
        }
    }

    @Test
    public void testAlleleValues() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM AlleleValues");
        query.setMaxResults(5);
        List results = query.list();

        for (Object obj : results) {
            Assert.assertTrue(obj instanceof AlleleValues);
            Assert.assertTrue(obj != null);
            AlleleValues element = (AlleleValues) obj;
            Debug.println(INDENT, element);
        }
    }

    @Test
    public void testCharValues() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM CharValues");
        query.setMaxResults(5);
        List results = query.list();

        for (Object obj : results) {
            Assert.assertTrue(obj instanceof CharValues);
            Assert.assertTrue(obj != null);
            CharValues element = (CharValues) obj;
            Debug.println(INDENT, element);
        }
    }

    @Test
    public void testDataset() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM Dataset");
        query.setMaxResults(5);
        List results = query.list();

        for (Object obj : results) {
            Assert.assertTrue(obj instanceof Dataset);
            Assert.assertTrue(obj != null);
            Dataset element = (Dataset) obj;
            Debug.println(INDENT, element);
        }
    }

    @Test
    public void testMap() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM Map");
        query.setMaxResults(5);
        List results = query.list();

        for (Object obj : results) {
            Assert.assertTrue(obj instanceof Map);
            Assert.assertTrue(obj != null);
            Map element = (Map) obj;
            Debug.println(INDENT, element);
        }
    }

    @Test
    public void testMappingData() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM MappingData");
        query.setMaxResults(5);
        List results = query.list();

        for (Object obj : results) {
            Assert.assertTrue(obj instanceof MappingData);
            Assert.assertTrue(obj != null);
            MappingData element = (MappingData) obj;
            Debug.println(INDENT, element);
        }
    }

    @Test
    public void testMappingPop() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM MappingPop");
        query.setMaxResults(5);
        List results = query.list();

        for (Object obj : results) {
            Assert.assertTrue(obj instanceof MappingPop);
            Assert.assertTrue(obj != null);
            MappingPop element = (MappingPop) obj;
            Debug.println(INDENT, element);
        }
    }

    @Test
    public void testMappingPopValues() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM MappingPopValues");
        query.setMaxResults(5);
        List results = query.list();

        for (Object obj : results) {
            Assert.assertTrue(obj instanceof MappingPopValues);
            Assert.assertTrue(obj != null);
            MappingPopValues element = (MappingPopValues) obj;
            Debug.println(INDENT, element);
        }
    }

    @Test
    public void testMarker() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM Marker");
        query.setMaxResults(5);
        List results = query.list();

        for (Object obj : results) {
            Assert.assertTrue(obj instanceof Marker);
            Assert.assertTrue(obj != null);
            Marker element = (Marker) obj;
            Debug.println(INDENT, element);
        }
    }

    @Test
    public void testMarkerMetadataSet() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM MarkerMetadataSet");
        query.setMaxResults(5);
        List results = query.list();

        for (Object obj : results) {
            Assert.assertTrue(obj instanceof MarkerMetadataSet);
            Assert.assertTrue(obj != null);
            MarkerMetadataSet element = (MarkerMetadataSet) obj;
            Debug.println(INDENT, element);
        }
    } 

    @AfterClass
    public static void tearDown() throws Exception {
        hibernateUtil.shutdown();
    }

}
