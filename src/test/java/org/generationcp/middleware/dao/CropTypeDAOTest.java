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

import com.google.common.collect.Sets;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.api.role.RoleService;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

public class CropTypeDAOTest extends IntegrationTestBase {

	private WorkbenchDaoFactory workbenchDaoFactory;

	@Autowired
	private RoleService roleService;

	@Autowired
	private UserService userService;

	@Before
	public void setUp() throws Exception {
		if (workbenchDaoFactory == null) {
			this.workbenchDaoFactory = new WorkbenchDaoFactory(this.workbenchSessionProvider);
		}
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

		final WorkbenchUser workbenchUser1 = this.createWorkbenchUser(RandomStringUtils.randomAlphabetic(10),
				Sets.newHashSet(customCrop1, customCrop2));
		final WorkbenchUser workbenchUser2 = this.createWorkbenchUser(RandomStringUtils.randomAlphabetic(10), Sets.newHashSet(customCrop3));

		final List<CropType> cropsForWorkbenchUser1 = this.workbenchDaoFactory.getCropTypeDAO().getAvailableCropsForUser(workbenchUser1.getUserid());
		final List<CropType> cropsForWorkbenchUser2 = this.workbenchDaoFactory.getCropTypeDAO().getAvailableCropsForUser(workbenchUser2.getUserid());

		Assert.assertEquals(2, cropsForWorkbenchUser1.size());
		Assert.assertEquals(crop1, cropsForWorkbenchUser1.get(0).getCropName());
		Assert.assertEquals(crop2, cropsForWorkbenchUser1.get(1).getCropName());

		Assert.assertEquals(1, cropsForWorkbenchUser2.size());
		Assert.assertEquals(crop3, cropsForWorkbenchUser2.get(0).getCropName());

	}

	WorkbenchUser createWorkbenchUser(final String userName, final Set<CropType> crops) {
		final WorkbenchUser workbenchUser = new WorkbenchUser();
		workbenchUser.setName(userName);
		final Person person = new Person();
		workbenchUser.setPerson(person);
		workbenchUser.setAccess(0);
		workbenchUser.setActive(true);
		workbenchUser.setInstalid(0);
		workbenchUser.setStatus(0);
		workbenchUser.setType(0);
		workbenchUser.setAssignDate(20190101);
		workbenchUser.setCloseDate(20190101);
		workbenchUser.setPassword("password");
		person.setCrops(crops);
		person.setContact("contact");
		person.setEmail(RandomStringUtils.randomAlphabetic(6) + "@gmail.com");
		person.setExtension("ext");
		person.setFax("fax");
		person.setFirstName("fname");
		person.setLastName("lname");
		person.setInstituteId(1);
		person.setLanguage(1);
		person.setMiddleName("mName");
		person.setNotes("notes");
		person.setPhone("23");
		person.setPositionName("positionMName");
		person.setTitle("Mrs");
		this.workbenchDaoFactory.getPersonDAO().save(person);
		return this.workbenchDaoFactory.getWorkbenchUserDAO().save(workbenchUser);
	}

	CropType createCropType(final String cropName) {
		final CropType cropType = new CropType();
		cropType.setCropName(cropName);
		this.workbenchDaoFactory.getCropTypeDAO().saveOrUpdate(cropType);
		return cropType;
	}

}
