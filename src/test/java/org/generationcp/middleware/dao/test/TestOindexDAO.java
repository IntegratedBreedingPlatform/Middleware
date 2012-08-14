/***************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * @author Kevin L. Manansala
 * 
 * This software is licensed for use under the terms of the 
 * GNU General Public License (http://bit.ly/8Ztv8M) and the 
 * provisions of Part F of the Generation Challenge Programme 
 * Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 **************************************************************/
package org.generationcp.middleware.dao.test;


import java.util.List;

import org.generationcp.middleware.dao.OindexDAO;
import org.generationcp.middleware.util.HibernateUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class TestOindexDAO{
    
    private static final String CONFIG = "test-hibernate.cfg.xml";
    private static HibernateUtil hibernateUtil;
    private static OindexDAO dao;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        hibernateUtil = new HibernateUtil(CONFIG);
        dao = new OindexDAO();
        dao.setSession(hibernateUtil.getCurrentSession());
    }
    
    @Test
    public void testGetFactorIdAndLevelNoOfConditionsByRepresentationId() throws Exception {
        List<Object[]> results = dao.getFactorIdAndLevelNoOfConditionsByRepresentationId(Integer.valueOf(2));
        System.out.println("Results:");
        for(Object[] result : results) {
            Integer factorid = (Integer) result[0];
            Integer levelno = (Integer) result[1];
            System.out.println("factorid: " + factorid + " AND " + "levelno: " + levelno);
        }
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        dao.setSession(null);
        dao = null;
        hibernateUtil.closeCurrentSession();
        hibernateUtil.shutdown();
    }

}
