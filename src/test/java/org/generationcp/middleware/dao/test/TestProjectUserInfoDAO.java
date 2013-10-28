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

import org.generationcp.middleware.dao.ProjectUserInfoDAO;
import org.generationcp.middleware.hibernate.HibernateUtil;

import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.util.Debug;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestProjectUserInfoDAO{

    private HibernateUtil hibernateUtil;
    private ProjectUserInfoDAO dao;

    @Before
    public void setUp() throws Exception {
        hibernateUtil = new HibernateUtil(new DatabaseConnectionParameters("testDatabaseConfig.properties", "workbench"));
        dao = new ProjectUserInfoDAO();
        dao.setSession(hibernateUtil.getCurrentSession());
    }

    @Test
    public void testGetByProjectIdAndUserId() throws Exception {
           	
        ProjectUserInfo result = dao.getByProjectIdAndUserId(2, 1);
        if (result == null){
        	Debug.println(0, "testGetByProjectIdAndUserId RESULTS: no result");
        }else{
            Debug.println(0, "testGetByProjectIdAndUserId RESULTS:" + result.getProjectId());
            Debug.println(0, result.toString());
        }
        
        
    }

    @After
    public void tearDown() throws Exception {
        dao.setSession(null);
        dao = null;
        hibernateUtil.shutdown();
    }

}

