/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.middleware.dao;

import com.google.common.collect.Lists;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

public class CropTypeDAOTest extends IntegrationTestBase {

	private CropTypeDAO cropTypeDAO;

	private WorkbenchUserDAO workbenchUserDAO;

	private ProjectDAO projectDAO;

	private ProjectUserInfoDAO projectUserInfoDAO;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Before
	public void setUp() throws Exception {
		this.cropTypeDAO = new CropTypeDAO();
		this.cropTypeDAO.setSession(this.workbenchSessionProvider.getSession());
	}

	@Test
	public void testGetAvailableCropsForUser() {

		// Create dummy crops
		final String crop1 = "Crop1";
		final String crop2 = "Crop2";
		final String crop3 = "Crop3";
		final CropType customCrop1 = this.createCropType(crop1);
		final CropType customCrop2 = this.createCropType(crop2);
		final CropType customCrop3 = this.createCropType(crop3);

		final int workbenchUserId1 = this.createWorkbenchUser("User999", Lists.newArrayList(customCrop1, customCrop2));
		final int workbenchUserId2 = this.createWorkbenchUser("User1000", Lists.newArrayList(customCrop3));

		// Create dummy projects
		this.createProject("Project1", customCrop1, workbenchUserId1);
		this.createProject("Project2", customCrop2, workbenchUserId1);
		this.createProject("Project3", customCrop3, workbenchUserId2);

		final List<CropType> cropsForWorkbenchUser1 = this.cropTypeDAO.getAvailableCropsForUser(workbenchUserId1);
		final List<CropType> cropsForWorkbenchUser2 = this.cropTypeDAO.getAvailableCropsForUser(workbenchUserId2);

		Assert.assertEquals(2, cropsForWorkbenchUser1.size());
		Assert.assertEquals(crop1, cropsForWorkbenchUser1.get(0).getCropName());
		Assert.assertEquals(crop2, cropsForWorkbenchUser1.get(1).getCropName());

		Assert.assertEquals(1, cropsForWorkbenchUser2.size());
		Assert.assertEquals(crop3, cropsForWorkbenchUser2.get(0).getCropName());

	}

	int createWorkbenchUser(final String userName, final List<CropType> crops) {
		final WorkbenchUser workbenchUser = new WorkbenchUser();
		workbenchUser.setName(userName);
		workbenchUser.setPersonid(1);
		workbenchUser.setAccess(0);
		workbenchUser.setActive(true);
		workbenchUser.setInstalid(0);
		workbenchUser.setStatus(0);
		workbenchUser.setType(0);
		workbenchUser.setAssignDate(20190101);
		workbenchUser.setCloseDate(20190101);
		workbenchUser.setPassword("password");
		workbenchUser.setCrops(crops);
		return this.workbenchDataManager.addUser(workbenchUser);
	}

	CropType createCropType(final String cropName) {
		final CropType cropType = new CropType();
		cropType.setCropName(cropName);
		this.workbenchDataManager.addCropType(cropType);
		return cropType;
	}

	Project createProject(final String projectName, final CropType cropType, final int workbenchUserId) {
		final Project project = new Project();
		project.setProjectName(projectName);
		project.setCropType(cropType);
		project.setUserId(workbenchUserId);
		project.setUniqueID(UUID.randomUUID().toString());
		this.workbenchDataManager.addProject(project);
		this.createProjectUserInfo(project, workbenchUserId);
		return project;
	}

	ProjectUserInfo createProjectUserInfo(final Project project, final int workbenchUserId) {
		final ProjectUserInfo projectUserInfo = new ProjectUserInfo();
		projectUserInfo.setProject(project);
		projectUserInfo.setUserId(workbenchUserId);
		this.workbenchDataManager.saveOrUpdateProjectUserInfo(projectUserInfo);
		return projectUserInfo;
	}

}
