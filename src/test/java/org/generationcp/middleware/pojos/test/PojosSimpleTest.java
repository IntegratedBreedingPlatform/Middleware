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
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Bibref;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Georef;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Installation;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Locdes;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Progenitor;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.util.Debug;
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
    public void testAtributs() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM Attribute");
        query.setMaxResults(5);
        List results = query.list();

        for (Object obj : results) {
            Assert.assertTrue(obj instanceof Attribute);
            Assert.assertTrue(obj != null);
            Attribute atributs = (Attribute) obj;
            Debug.println(INDENT, atributs);
        }
    }

    @Test
    public void testBibrefs() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM Bibref");
        query.setMaxResults(5);
        List results = query.list();

        for (Object obj : results) {
            Assert.assertTrue(obj instanceof Bibref);
            Assert.assertTrue(obj != null);
            Bibref holder = (Bibref) obj;
            Debug.println(INDENT, holder);
        }
    }

    @Test
    public void testCntry() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM Country");
        query.setMaxResults(5);
        List results = query.list();

        for (Object obj : results) {
            Assert.assertTrue(obj instanceof Country);
            Assert.assertTrue(obj != null);
            Country holder = (Country) obj;
            Debug.println(INDENT, holder);
        }
    }

    @Test
    public void testGeoref() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM Georef");
        query.setMaxResults(5);
        List results = query.list();

        for (Object obj : results) {
            Assert.assertTrue(obj instanceof Georef);
            Assert.assertTrue(obj != null);
            Georef holder = (Georef) obj;
            Debug.println(INDENT, holder);
        }
    }

    @Test
    public void testGermplsm() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM Germplasm");
        query.setMaxResults(5);
        List results = query.list();

        for (Object obj : results) {
            Assert.assertTrue(obj instanceof Germplasm);
            Assert.assertTrue(obj != null);
            Germplasm holder = (Germplasm) obj;
            Debug.println(INDENT, holder);
        }
    }

    @Test
    public void testLocation() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM Location");
        query.setMaxResults(5);
        List results = query.list();

        for (Object obj : results) {
            Assert.assertTrue(obj instanceof Location);
            Assert.assertTrue(obj != null);
            Location holder = (Location) obj;
            Debug.println(INDENT, holder);
        }
    }

    @Test
    public void testLocdes() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM Locdes");
        query.setMaxResults(5);
        List results = query.list();

        for (Object obj : results) {
            Assert.assertTrue(obj instanceof Locdes);
            Assert.assertTrue(obj != null);
            Locdes holder = (Locdes) obj;
            Debug.println(INDENT, holder);
        }
    }

    @Test
    public void testMethods() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM Method");
        query.setMaxResults(5);
        List results = query.list();

        for (Object obj : results) {
            Assert.assertTrue(obj instanceof Method);
            Assert.assertTrue(obj != null);
            Method holder = (Method) obj;
            Debug.println(INDENT, holder);
        }
    }

    @Test
    public void testNames() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM Name");
        query.setMaxResults(5);
        List results = query.list();

        for (Object obj : results) {
            Assert.assertTrue(obj instanceof Name);
            Assert.assertTrue(obj != null);
            Name holder = (Name) obj;
            Debug.println(INDENT, holder);
        }
    }

    @Test
    public void testProgntrs() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM Progenitor");
        query.setMaxResults(5);
        List results = query.list();

        for (Object obj : results) {
            Assert.assertTrue(obj instanceof Progenitor);
            Assert.assertTrue(obj != null);
            Progenitor holder = (Progenitor) obj;
            Debug.println(INDENT, holder);
        }
    }

    @Test
    public void testUdflds() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM UserDefinedField");
        query.setMaxResults(5);
        List results = query.list();

        for (Object obj : results) {
            Assert.assertTrue(obj instanceof UserDefinedField);
            Assert.assertTrue(obj != null);
            UserDefinedField holder = (UserDefinedField) obj;
            Debug.println(INDENT, holder);
        }
    }

    @Test
    public void testUser() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM User");
        query.setMaxResults(5);
        List results = query.list();

        for (Object obj : results) {
            Assert.assertTrue(obj instanceof User);
            Assert.assertTrue(obj != null);
            User holder = (User) obj;
            Debug.println(INDENT, holder);
        }
    }

    @Test
    public void testGettingGermplasmDetails() {
        Integer gid = Integer.valueOf(50533);
        Session session = hibernateUtil.getCurrentSession();
        Germplasm g = (Germplasm) session.load(Germplasm.class, gid);
        Debug.println(INDENT, g);
    }

    @Test
    public void testGermplasmList() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM GermplasmList");
        query.setMaxResults(5);
        List results = query.list();

        for (Object obj : results) {
            Assert.assertTrue(obj instanceof GermplasmList);
            Assert.assertTrue(obj != null);
            GermplasmList holder = (GermplasmList) obj;
            Debug.println(INDENT, holder);
        }
    }

    @Test
    public void testGermplasmListData() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM GermplasmListData");
        query.setMaxResults(5);
        List results = query.list();

        for (Object obj : results) {
            Assert.assertTrue(obj instanceof GermplasmListData);
            Assert.assertTrue(obj != null);
            GermplasmListData holder = (GermplasmListData) obj;
            Debug.println(INDENT, holder);
        }
    }

    @Test
    public void testInstallation() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM Installation");
        query.setMaxResults(5);
        List results = query.list();

        for (Object obj : results) {
            Assert.assertTrue(obj instanceof Installation);
            Assert.assertTrue(obj != null);
            Installation holder = (Installation) obj;
            Debug.println(INDENT, holder);
        }
    }
    
    @Test
    public void testListDataProperty() {
        Session session = hibernateUtil.getCurrentSession();
        
        // UNCOMMENT TO TEST: (IMPORTANT -- RUN IN RICE DB ONLY)
        Query query = session.createQuery("FROM GermplasmListData where id IN (32334, 32335, 32336)");
        List results = query.list();

        for (Object obj : results) {
        	Assert.assertTrue(obj instanceof GermplasmListData);
        	Assert.assertTrue(obj != null);
        	GermplasmListData data = (GermplasmListData) obj;
        	data.print(INDENT);
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        hibernateUtil.shutdown();
    }

}
