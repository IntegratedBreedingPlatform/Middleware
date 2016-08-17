/*******************************************************************************
 *
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

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ProjectDAOTest extends IntegrationTestBase {

	private static ProjectDAO projectDAO;
	private static CropTypeDAO cropTypeDAO;
	private static UserDAO userDAO;
	private static ProjectUserInfoDAO projectUserInfoDAO;

	@Autowired
	@Qualifier(value = "workbenchSessionProvider")
	protected HibernateSessionProvider workbenchSessionProvider;

	@Before
	public void setUp() throws Exception {
		final Session session = this.workbenchSessionProvider.getSession();
		ProjectDAOTest.projectDAO = new ProjectDAO();
		ProjectDAOTest.projectDAO.setSession(session);
		ProjectDAOTest.cropTypeDAO = new CropTypeDAO();
		ProjectDAOTest.cropTypeDAO.setSession(session);
		ProjectDAOTest.userDAO = new UserDAO();
		ProjectDAOTest.userDAO.setSession(session);
		ProjectDAOTest.projectUserInfoDAO = new ProjectUserInfoDAO();
		ProjectDAOTest.projectUserInfoDAO.setSession(session);
	}

	@Test
	public void testGetProjectsByCropType() {
		final CropType cropType = ProjectDAOTest.cropTypeDAO.getByName(ContextHolder.getCurrentCrop());
		final List<Project> projects = ProjectDAOTest.projectDAO.getProjectsByCropType(cropType);
		Assert.assertNotNull("The list should not be null", projects);
		for (final Project project : projects) {
			Assert.assertEquals("The crop type should be the same", cropType, project.getCropType());
		}
	}

	@Test
	public void testGetAdminUserIdsOfCrop() {
		final String crop = ContextHolder.getCurrentCrop();

		final List<Integer> adminUserIds = ProjectDAOTest.projectDAO.getAdminUserIdsOfCrop(crop);
		Assert.assertNotNull("The list should not be null", adminUserIds);

		// check if the users are admin
		final List<User> users = ProjectDAOTest.userDAO.getByIds(adminUserIds);
		for (final User user : users) {
			final List<UserRole> roles = user.getRoles();
			boolean isAdmin = false;
			for (final UserRole userRole : roles) {
				final String role = userRole.getRole();
				if ("ADMIN".equalsIgnoreCase(role)) {
					isAdmin = true;
				}
			}
			Assert.assertTrue("The user should be an admin", isAdmin);
		}

		// check if the users are users of the crop
		final List<Integer> cropUserIds = new ArrayList<>();
		final CropType cropType = ProjectDAOTest.cropTypeDAO.getByName(crop);
		final List<Project> projects = ProjectDAOTest.projectDAO.getProjectsByCropType(cropType);
		for (final Project project : projects) {
			// get users
			final List<ProjectUserInfo> projectUserInfoList =
					ProjectDAOTest.projectUserInfoDAO.getByProjectId(project.getProjectId().intValue());
			for (final ProjectUserInfo projectUserInfo : projectUserInfoList) {
				cropUserIds.add(projectUserInfo.getUserId());
			}
		}
		Assert.assertTrue("The users should be crop users", cropUserIds.containsAll(adminUserIds));
	}

}
