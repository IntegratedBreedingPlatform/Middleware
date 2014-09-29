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
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.util.Debug;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ProjectUserInfoDAOTest extends MiddlewareIntegrationTest {

    private ProjectUserInfoDAO dao;
    private UserDAO userDao;
    private ProjectUserRoleDAO projectUserRoleDao;

    @Before
    public void setUp() throws Exception {
        Session currentSession = workbenchSessionUtil.getCurrentSession();


        dao = new ProjectUserInfoDAO();
        dao.setSession(currentSession);

        userDao = new UserDAO();
        projectUserRoleDao = new ProjectUserRoleDAO();

        userDao.setSession(currentSession);
        projectUserRoleDao.setSession(currentSession);

    }

    @Test
    public void testGetByProjectIdAndUserId() throws Exception {
        List<User> usersList = userDao.getAll();

        int userId = usersList.get(0).getUserid();
        int projectId = projectUserRoleDao.getProjectsByUser(usersList.get(0)).get(0).getProjectId().intValue();

        ProjectUserInfo result = dao.getByProjectIdAndUserId(projectId,userId);

        Assert.assertNotNull(result);

        if (result == null){
            Debug.println(0, "testGetByProjectIdAndUserId RESULTS: no result");
        }else{
            Debug.println(0, "testGetByProjectIdAndUserId RESULTS:" + result.getProjectId());
            Debug.println(0, result.toString());
        }
    }
}

