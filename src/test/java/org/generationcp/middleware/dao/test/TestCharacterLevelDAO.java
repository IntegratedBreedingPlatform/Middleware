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
import org.generationcp.middleware.pojos.StudyInfo;
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
        Integer factorId = Integer.valueOf(1031);
        Integer levelNo = Integer.valueOf(146919);
        List<DatasetCondition> results = dao.getConditionAndValueByFactorIdAndLevelNo(factorId, levelNo);
        System.out.println("testGetConditionAndValueByFactorIdAndLevelNo RESULTS: ");
        System.out.println("  factorId = " + factorId);
        System.out.println("  levelNo = " + levelNo);        
        for(DatasetCondition c : results) {
            System.out.println("  " + c);
        }
    }
    
    @Test
    public void testCountStudyInformationByGID() throws Exception {
        Long gid = Long.valueOf(50533);
        System.out.println("testCountStudyInformationByGID(GID=" + gid + ") : " + dao.countStudyInformationByGID(gid));
    }
    
    @Test
    public void testGetStudyInformationByGID() throws Exception {
        Long gid = Long.valueOf(50533);
        List<StudyInfo> results = dao.getStudyInformationByGID(gid);
        System.out.println("testGetStudyInformationByGID(GID=" + gid + ") RESULTS: ");
        for(StudyInfo info : results) {
            System.out.println("  " + info);
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
