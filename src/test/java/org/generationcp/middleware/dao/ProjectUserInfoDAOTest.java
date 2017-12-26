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

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.util.Debug;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("Historic failing test. Disabled temporarily. Developers working in this area please spend some time to fix and remove @Ignore.")
public class ProjectUserInfoDAOTest extends IntegrationTestBase {

	private ProjectUserInfoDAO dao;
	private UserDAO userDao;

	@Before
	public void setUp() throws Exception {
		Session currentSession = this.sessionProvder.getSession();

		this.dao = new ProjectUserInfoDAO();
		this.dao.setSession(currentSession);

		this.userDao = new UserDAO();

		this.userDao.setSession(currentSession);

	}

	@Test
	public void testGetByProjectIdAndUserId() throws Exception {
		List<User> usersList = this.userDao.getAll();

		int userId = usersList.get(0).getUserid();
		Long projectId = this.dao.getProjectsByUser(usersList.get(0)).get(0).getProjectId();

		ProjectUserInfo result = this.dao.getByProjectIdAndUserId(projectId, userId);

		Assert.assertNotNull(result);
		Debug.println(0, "testGetByProjectIdAndUserId RESULTS:" + result.getProject());
		Debug.println(0, result.toString());
	}
}
