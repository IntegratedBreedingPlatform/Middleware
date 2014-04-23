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
import org.generationcp.middleware.utils.test.TestOutputFormatter;
import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("rawtypes")
public class IMSPojosSimpleTest extends TestOutputFormatter{

    private static HibernateUtil hibernateUtil;

    @BeforeClass
    public static void setUp() throws Exception {
        hibernateUtil = new HibernateUtil(new DatabaseConnectionParameters("testDatabaseConfig.properties", "local"));
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
            Debug.println(INDENT, holder);
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
            Debug.println(INDENT, holder);
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
            Debug.println(INDENT, holder);
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        hibernateUtil.shutdown();
    }

}
