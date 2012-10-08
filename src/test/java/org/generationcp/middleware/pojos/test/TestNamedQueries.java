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

import java.math.BigInteger;
import java.util.List;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestNamedQueries{

    private static final String CONFIG = "test-hibernate.cfg.xml";
    private HibernateUtil hibernateUtil;

    @Before
    public void setUp() throws Exception {
        hibernateUtil = new HibernateUtil(CONFIG);
    }

    @Test
    public void testGetGermplasmByPrefName() {
        String name = "IR 64";
        Session session = hibernateUtil.getCurrentSession();
        long start = System.currentTimeMillis();
        Query query = session.getNamedQuery(Germplasm.GET_BY_PREF_NAME);
        query.setParameter("name", name);
        query.setMaxResults(5);
        List results = query.list();
        long end = System.currentTimeMillis();
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
        System.out.println("testGetGermplasmByPrefName(name=" + name + ") SEARCH RESULTS:");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof Germplasm);
            Assert.assertTrue(obj != null);
            Germplasm holder = (Germplasm) obj;
            System.out.println("  " + holder);
        }
    }

    @Test
    public void testGetAllGermplasm() {
        Session session = hibernateUtil.getCurrentSession();
        long start = System.currentTimeMillis();
        Query query = session.getNamedQuery(Germplasm.GET_ALL);
        query.setMaxResults(5);
        List results = query.list();
        long end = System.currentTimeMillis();
        System.out.println("testGetAllGermplasm() SEARCH RESULTS:");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof Germplasm);
            Assert.assertTrue(obj != null);
            Germplasm holder = (Germplasm) obj;
            System.out.println("  " + holder);
        }
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testCountAllGermplasm() {
        Session session = hibernateUtil.getCurrentSession();
        long start = System.currentTimeMillis();
        Query query = session.getNamedQuery(Germplasm.COUNT_ALL);
        Long result = (Long) query.uniqueResult();
        long end = System.currentTimeMillis();
        Assert.assertTrue(result != null);
        System.out.println("testCountAllGermplasm(): " + result);
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @Test
    public void testCountGermplasmByPrefName() {
        String name = "IR 64";
        Session session = hibernateUtil.getCurrentSession();
        long start = System.currentTimeMillis();
        Query query = session.createSQLQuery(Germplasm.COUNT_BY_PREF_NAME);
        query.setString("name", name);
        BigInteger result = (BigInteger) query.uniqueResult();
        long end = System.currentTimeMillis();
        Assert.assertTrue(result != null);
        System.out.println("testCountGermplasmByPrefName(name=" + name + "): " + result);
        System.out.println("  QUERY TIME: " + (end - start) + " ms");
    }

    @After
    public void tearDown() throws Exception {
        hibernateUtil.shutdown();
    }

}
