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
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.pojos.Lot;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.Transaction;
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
public class IMSPojosSimpleTest{

    private static HibernateUtil hibernateUtil;


    private long startTime;

    @Rule
    public TestName name = new TestName();

    @BeforeClass
    public static void setUp() throws Exception {
        hibernateUtil = new HibernateUtil(new DatabaseConnectionParameters("testDatabaseConfig.properties", "local"));
    }

    @Before
    public void beforeEachTest() {
        Debug.println(0, "#####" + name.getMethodName() + " Start: ");
        startTime = System.nanoTime();
    }

    @Test
    public void testLot() throws Exception {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM Lot");
        query.setMaxResults(5);
        List results = query.list();

        for (Object obj : results) {
            Assert.assertTrue(obj instanceof Lot);
            Assert.assertTrue(obj != null);
            Lot holder = (Lot) obj;
            Debug.println(0, "  " + holder);
        }
    }

	@Test
    public void testTransaction() throws Exception {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM Transaction");
        query.setMaxResults(5);
        List results = query.list();

        for (Object obj : results) {
            Assert.assertTrue(obj instanceof Transaction);
            Assert.assertTrue(obj != null);
            Transaction holder = (Transaction) obj;
            Debug.println(0, "  " + holder);
        }
    }

    @Test
    public void testPerson() throws Exception {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM Person");
        query.setMaxResults(5);
        List results = query.list();

        for (Object obj : results) {
            Assert.assertTrue(obj instanceof Person);
            Assert.assertTrue(obj != null);
            Person holder = (Person) obj;
            Debug.println(0, "  " + holder);
        }
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
