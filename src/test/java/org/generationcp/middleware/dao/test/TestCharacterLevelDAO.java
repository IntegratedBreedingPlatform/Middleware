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

import org.generationcp.middleware.dao.CharacterLevelDAO;
import org.generationcp.middleware.pojos.DatasetCondition;
import org.generationcp.middleware.util.HibernateUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class TestCharacterLevelDAO{

    private static final String CONFIG = "test-hibernate.cfg.xml";
    private static HibernateUtil hibernateUtil;
    private static CharacterLevelDAO dao;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        hibernateUtil = new HibernateUtil(CONFIG);
        dao = new CharacterLevelDAO();
        dao.setSession(hibernateUtil.getCurrentSession());
    }

    @Test
    public void testGetConditionAndValueByFactorIdAndLevelNo() throws Exception {
        List<DatasetCondition> results = dao.getConditionAndValueByFactorIdAndLevelNo(Integer.valueOf(1031), Integer.valueOf(146919));
        System.out.println("RESULTS:");
        for(DatasetCondition c : results) {
            System.out.println(c);
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
