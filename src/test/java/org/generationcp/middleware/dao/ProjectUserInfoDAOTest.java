/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.dao;

import java.util.List;

import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.util.Debug;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ProjectUserInfoDAOTest extends MiddlewareIntegrationTest {

	private ProjectUserInfoDAO dao;
	private UserDAO userDao;
	private ProjectUserRoleDAO projectUserRoleDao;

	@Before
	public void setUp() throws Exception {
		Session currentSession = MiddlewareIntegrationTest.workbenchSessionUtil.getCurrentSession();

		this.dao = new ProjectUserInfoDAO();
		this.dao.setSession(currentSession);

		this.userDao = new UserDAO();
		this.projectUserRoleDao = new ProjectUserRoleDAO();

		this.userDao.setSession(currentSession);
		this.projectUserRoleDao.setSession(currentSession);

	}

	@Test
	public void testGetByProjectIdAndUserId() throws Exception {
		List<User> usersList = this.userDao.getAll();

		int userId = usersList.get(0).getUserid();
		int projectId = this.projectUserRoleDao.getProjectsByUser(usersList.get(0)).get(0).getProjectId().intValue();

		ProjectUserInfo result = this.dao.getByProjectIdAndUserId(projectId, userId);

		Assert.assertNotNull(result);

		if (result == null) {
			Debug.println(0, "testGetByProjectIdAndUserId RESULTS: no result");
		} else {
			Debug.println(0, "testGetByProjectIdAndUserId RESULTS:" + result.getProjectId());
			Debug.println(0, result.toString());
		}
	}
}
