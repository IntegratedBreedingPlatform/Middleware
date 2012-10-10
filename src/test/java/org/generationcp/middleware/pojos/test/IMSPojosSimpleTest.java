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

import org.generationcp.middleware.pojos.Lot;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.Transaction;
import org.generationcp.middleware.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class IMSPojosSimpleTest{

    private static final String CONFIG = "test-hibernate.cfg.xml";
    private HibernateUtil hibernateUtil;

    @BeforeClass
    public void setUp() throws Exception {
        hibernateUtil = new HibernateUtil(CONFIG);
    }

    @Test
    public void testLot() throws Exception {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM Lot");
        query.setMaxResults(5);
        List results = query.list();

        System.out.println("testLot() RESULTS: ");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof Lot);
            Assert.assertTrue(obj != null);
            Lot holder = (Lot) obj;
            System.out.println("  " + holder);
        }
    }

    @Test
    public void testTransaction() throws Exception {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM Transaction");
        query.setMaxResults(5);
        List results = query.list();

        System.out.println("testTransaction() RESULTS: ");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof Transaction);
            Assert.assertTrue(obj != null);
            Transaction holder = (Transaction) obj;
            System.out.println("  " + holder);
        }
    }

    @Test
    public void testPerson() throws Exception {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM Person");
        query.setMaxResults(5);
        List results = query.list();

        System.out.println("testPerson() RESULTS: ");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof Person);
            Assert.assertTrue(obj != null);
            Person holder = (Person) obj;
            System.out.println("  " + holder);
        }
    }

    @AfterClass
    public void tearDown() throws Exception {
        hibernateUtil.shutdown();
    }

}
