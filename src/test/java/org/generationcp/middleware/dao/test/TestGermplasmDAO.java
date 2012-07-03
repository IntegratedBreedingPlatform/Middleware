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
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.util.HibernateUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestGermplasmDAO{

    private static final String CONFIG = "test-hibernate.cfg.xml";
    private HibernateUtil hibernateUtil;
    private GermplasmDAO dao;

    @Before
    public void setUp() throws Exception {
        hibernateUtil = new HibernateUtil(CONFIG);
        dao = new GermplasmDAO();
        dao.setSession(hibernateUtil.getCurrentSession());
    }

    /**
     * @Test public void testFindAll() throws Exception { List<Germplasm>
     *       germplsmList = dao.findAll(0, 5); Assert.assertTrue(germplsmList !=
     *       null); Assert.assertTrue(germplsmList.size() >= 5);
     * 
     *       System.out.println("SEARCH RESULTS:"); for(Germplasm g :
     *       germplsmList) { System.out.println(g); } }
     * @Test public void testCountAll() throws Exception { Long count =
     *       dao.countAll(); Assert.assertTrue(count != null);
     *       System.out.println("COUNT = " + count); }
     * @Test public void testFindByPrefName() throws Exception { List<Germplasm>
     *       germplsmList = dao.findByPrefName("IR 64", 0, 5);
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
        List<Germplasm> results = dao.getDerivativeChildren(Integer.valueOf(1));
        Assert.assertTrue(results.size() > 0);
        System.out.println("RESULTS:");
        for (Germplasm g : results) {
            System.out.println(g.getGid() + " : " + g.getPreferredName().getNval());
        }
    }

    @After
    public void tearDown() throws Exception {
        dao.setSession(null);
        dao = null;
        hibernateUtil.shutdown();
    }

}
