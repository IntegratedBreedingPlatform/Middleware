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

package org.generationcp.middleware.dao;

import java.util.List;

import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.util.Debug;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class GermplasmDAOTest extends MiddlewareIntegrationTest {

    private static GermplasmDAO dao;

    @BeforeClass
    public static void setUp() throws Exception {
        dao = new GermplasmDAO();
        dao.setSession(sessionUtil.getCurrentSession());
    }

    /**
     * @Test public void testGetAll() throws Exception { List<Germplasm>
     *       germplsmList = dao.getAll(0, 5); Assert.assertTrue(germplsmList !=
     *       null); Assert.assertTrue(germplsmList.size() >= 5);
     * 
     *       Debug.println(0, "SEARCH RESULTS:"); for(Germplasm g :
     *       germplsmList) { Debug.println(0, g); } }
     * @Test public void testCountAll() throws Exception { long count =
     *       dao.countAll(); Assert.assertTrue(count != null);
     *       Debug.println(0, "COUNT = " + count); }
     * @Test public void testGetByPrefName() throws Exception { List<Germplasm>
     *       germplsmList = dao.getByPrefName("IR 64", 0, 5);
     *       Assert.assertTrue(germplsmList != null);
     *       Assert.assertTrue(germplsmList.size() >= 5);
     * 
     *       Debug.println(0, "SEARCH RESULTS:"); for(Germplasm g :
     *       germplsmList) { Debug.println(0, g); } }
     * @Test public void testCountByPrefName() throws Exception { BigInteger
     *       count = dao.countByPrefName("IR 64"); Assert.assertTrue(count !=
     *       null); Debug.println(0, "COUNT = " + count); }
     * @Test public void testGetProgenitorsByGIDWithPrefName() throws Exception
     *       { List<Germplasm> results = dao.getProgenitorsByGIDWithPrefName(new
     *       Integer(306436)); Assert.assertTrue(results.size() > 0);
     *       Debug.println(0, "RESULTS:"); for(Germplasm g : results) {
     *       Debug.println(0, g); Debug.println(0, g.getPreferredName()); }
     *       }
     **/
    @Test
    public void testGetDerivativeChildren() throws Exception {
        Integer gid = Integer.valueOf(1);
        //List<Germplasm> results = dao.getDerivativeChildren(gid);
        List<Germplasm> results = dao.getChildren(gid, 'D');
        Assert.assertTrue(results.size() > 0);
        Debug.println(0, "testGetDerivativeChildren(GId=" + gid + ") RESULTS:");
        for (Germplasm g : results) {
            Debug.println(0, "  " + g.getGid() + " : " + g.getPreferredName().getNval());
        }
    }
    
    @Test
    public void testGetMaintenanceChildren() throws Exception {
    	Integer gid = Integer.valueOf(2590);
        List<Germplasm> results = dao.getChildren(gid, 'M');
        Assert.assertTrue(results.size() > 0);
        Debug.println(0, "testGetMaintenanceChildren(GId=" + gid + ") RESULTS:");
        for (Germplasm g : results) {
            Debug.println(0, "  " + g.getGid() + " : " + g.getPreferredName().getNval());
        }
    }
    
    @Test
    public void testSearchForGermplasmsExactMatchGID() throws Exception {

        List<Germplasm> results = dao.searchForGermplasms("1", Operation.EQUAL, false, false, sessionUtil.getCurrentSession());
        Assert.assertTrue(results.size() == 1);
        
    }
    
    @Test
    public void testSearchForGermplasmsExactMatchGermplasmName() throws Exception {

        List<Germplasm> results = dao.searchForGermplasms("CML0", Operation.EQUAL, false, false, sessionUtil.getCurrentSession());
        Assert.assertTrue(results.size() == 1);
        
    }
    
    @Test
    public void testSearchForGermplasmsExactMatchGIDWithWildCard() throws Exception {

        List<Germplasm> results = dao.searchForGermplasms("1%", Operation.EQUAL, false, false, sessionUtil.getCurrentSession());
        Assert.assertTrue(results.size() == 111);
        
    }
    
    @Test
    public void testSearchForGermplasmsExactMatchGermplasmNameWithWildCard() throws Exception {

        List<Germplasm> results = dao.searchForGermplasms("CML%", Operation.EQUAL, false, false, sessionUtil.getCurrentSession());
        Assert.assertTrue(results.size() == 200);
        
    }
    
    @Test
    public void testSearchForGermplasmsPartialMatchGID() throws Exception {

        List<Germplasm> results = dao.searchForGermplasms("1", Operation.LIKE, false, false, sessionUtil.getCurrentSession());
        Assert.assertTrue(results.size() == 111);
        
    }
    
    @Test
    public void testSearchForGermplasmsPartialMatchGermplasmName() throws Exception {

        List<Germplasm> results = dao.searchForGermplasms("CML", Operation.LIKE, false, false, sessionUtil.getCurrentSession());
        Assert.assertTrue(results.size() == 200);
        
    }

    @AfterClass
    public static void tearDown() throws Exception {
        dao.setSession(null);
        dao = null;
    }

}
