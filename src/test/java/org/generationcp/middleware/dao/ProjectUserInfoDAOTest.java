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

import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.util.Debug;
import org.junit.Before;
import org.junit.Test;

public class ProjectUserInfoDAOTest extends MiddlewareIntegrationTest {

    private ProjectUserInfoDAO dao;

    @Before
    public void setUp() throws Exception {
        dao = new ProjectUserInfoDAO();
        dao.setSession(workbenchSessionUtil.getCurrentSession());
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
}

