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

import org.generationcp.middleware.dao.ProjectDAO;

import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.util.HibernateUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestProjectDAO{

    private static final String CONFIG = "test-hibernate.cfg.xml";
    private HibernateUtil hibernateUtil;
    private ProjectDAO dao;

    @Before
    public void setUp() throws Exception {
        hibernateUtil = new HibernateUtil(CONFIG);
        dao = new ProjectDAO();
        dao.setSession(hibernateUtil.getCurrentSession());
    }

    @Test
    public void testgetLastOpenedProject() throws Exception {
       

        Project result = dao.getLastOpenedProject(3);
        if (result == null){
        	System.out.println("testgetLastOpenedProject RESULTS: no result");
        }else{
        	System.out.println("testgetLastOpenedProject RESULTS:" + result.getProjectName());
        	System.out.println(result.toString());
        }
        
        
    }

    @After
    public void tearDown() throws Exception {
        dao.setSession(null);
        dao = null;
        hibernateUtil.shutdown();
    }

}

