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

import org.generationcp.middleware.hibernate.HibernateUtil;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.util.Debug;
import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

@SuppressWarnings("rawtypes")
public class TestNamedQueries{

    private static HibernateUtil hibernateUtil;

    private long startTime;

    @Rule
    public TestName name = new TestName();

    @BeforeClass
    public static void setUp() throws Exception {
        hibernateUtil = new HibernateUtil(new DatabaseConnectionParameters("testDatabaseConfig.properties", "central"));
    }
    
    @Before
    public void beforeEachTest() {
        Debug.println(0, "#####" + name.getMethodName() + " Start: ");
        startTime = System.nanoTime();
    }

    @Test
    public void testGetGermplasmByPrefName() {
        String name = "IR 64";
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.getNamedQuery(Germplasm.GET_BY_PREF_NAME);
        query.setParameter("name", name);
        query.setMaxResults(5);
        List results = query.list();
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof Germplasm);
            Assert.assertTrue(obj != null);
            Germplasm holder = (Germplasm) obj;
            Debug.println(0, "  " + holder);
        }
    }

    @Test
    public void testGetAllGermplasm() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.getNamedQuery(Germplasm.GET_ALL);
        query.setMaxResults(5);
        List results = query.list();
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof Germplasm);
            Assert.assertTrue(obj != null);
            Germplasm holder = (Germplasm) obj;
            Debug.println(0, "  " + holder);
        }
    }

    @Test
    public void testCountAllGermplasm() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.getNamedQuery(Germplasm.COUNT_ALL);
        Long result = (Long) query.uniqueResult();
        Assert.assertTrue(result != null);
    }

    @Test
    public void testCountGermplasmByPrefName() {
        String name = "IR 64";
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createSQLQuery(Germplasm.COUNT_BY_PREF_NAME);
        query.setString("name", name);
        BigInteger result = (BigInteger) query.uniqueResult();
        Assert.assertTrue(result != null);
    }

    @After
    public void afterEachTest() {
        long elapsedTime = System.nanoTime() - startTime;
        Debug.println(0, "#####" + name.getMethodName() + " End: Elapsed Time = " + elapsedTime + " ns = " + ((double) elapsedTime / 1000000000) + " s");
    }


    @AfterClass
    public static void tearDown() throws Exception {
        hibernateUtil.shutdown();
    }

}
