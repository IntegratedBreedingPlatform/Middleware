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

package org.generationcp.middleware.dao.test;

import java.util.List;

import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.hibernate.HibernateUtil;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.pojos.Germplasm;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestGermplasmDAO{

    private static HibernateUtil hibernateUtil;
    private static GermplasmDAO dao;

    @BeforeClass
    public static void setUp() throws Exception {
        hibernateUtil = new HibernateUtil(new DatabaseConnectionParameters("testDatabaseConfig.properties", "central"));
        dao = new GermplasmDAO();
        dao.setSession(hibernateUtil.getCurrentSession());
    }

    /**
     * @Test public void testGetAll() throws Exception { List<Germplasm>
     *       germplsmList = dao.getAll(0, 5); Assert.assertTrue(germplsmList !=
     *       null); Assert.assertTrue(germplsmList.size() >= 5);
     * 
     *       System.out.println("SEARCH RESULTS:"); for(Germplasm g :
     *       germplsmList) { System.out.println(g); } }
     * @Test public void testCountAll() throws Exception { long count =
     *       dao.countAll(); Assert.assertTrue(count != null);
     *       System.out.println("COUNT = " + count); }
     * @Test public void testGetByPrefName() throws Exception { List<Germplasm>
     *       germplsmList = dao.getByPrefName("IR 64", 0, 5);
     *       Assert.assertTrue(germplsmList != null);
     *       Assert.assertTrue(germplsmList.size() >= 5);
     * 
     *       System.out.println("SEARCH RESULTS:"); for(Germplasm g :
     *       germplsmList) { System.out.println(g); } }
     * @Test public void testCountByPrefName() throws Exception { BigInteger
     *       count = dao.countByPrefName("IR 64"); Assert.assertTrue(count !=
     *       null); System.out.println("COUNT = " + count); }
     * @Test public void testGetProgenitorsByGIDWithPrefName() throws Exception
     *       { List<Germplasm> results = dao.getProgenitorsByGIDWithPrefName(new
     *       Integer(306436)); Assert.assertTrue(results.size() > 0);
     *       System.out.println("RESULTS:"); for(Germplasm g : results) {
     *       System.out.println(g); System.out.println(g.getPreferredName()); }
     *       }
     **/

    @Test
    public void testGetDerivativeChildren() throws Exception {
        Integer gid = Integer.valueOf(1);
        //List<Germplasm> results = dao.getDerivativeChildren(gid);
        List<Germplasm> results = dao.getChildren(gid, 'D');
        Assert.assertTrue(results.size() > 0);
        System.out.println("testGetDerivativeChildren(GId=" + gid + ") RESULTS:");
        for (Germplasm g : results) {
            System.out.println("  " + g.getGid() + " : " + g.getPreferredName().getNval());
        }
    }
    
    @Test
    public void testGetMaintenanceChildren() throws Exception {
    	Integer gid = Integer.valueOf(2590);
        List<Germplasm> results = dao.getChildren(gid, 'M');
        Assert.assertTrue(results.size() > 0);
        System.out.println("testGetMaintenanceChildren(GId=" + gid + ") RESULTS:");
        for (Germplasm g : results) {
            System.out.println("  " + g.getGid() + " : " + g.getPreferredName().getNval());
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        dao.setSession(null);
        dao = null;
        hibernateUtil.shutdown();
    }

}
