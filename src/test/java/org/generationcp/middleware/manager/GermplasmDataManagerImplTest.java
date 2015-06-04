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

package org.generationcp.middleware.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.GermplasmTestDataGenerator;
import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Bibref;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmNameDetails;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.service.api.PedigreeService;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class GermplasmDataManagerImplTest extends DataManagerIntegrationTest {

	private static PedigreeService pedigreeService;
	private static GermplasmDataManager manager;
	private static LocationDataManager locationManager;
	private static UserDataManager userDataManager;
	private static Project commonTestProject;
	private static WorkbenchTestDataUtil workbenchTestDataUtil;

	private static GermplasmTestDataGenerator germplasmTestDataGenerator;

	@BeforeClass
	public static void setUp() throws Exception {
		GermplasmDataManagerImplTest.manager = DataManagerIntegrationTest.managerFactory.getGermplasmDataManager();
		GermplasmDataManagerImplTest.locationManager = DataManagerIntegrationTest.managerFactory.getLocationDataManager();
		GermplasmDataManagerImplTest.userDataManager = DataManagerIntegrationTest.managerFactory.getUserDataManager();
		GermplasmDataManagerImplTest.germplasmTestDataGenerator = new GermplasmTestDataGenerator(GermplasmDataManagerImplTest.manager);
		GermplasmDataManagerImplTest.pedigreeService = DataManagerIntegrationTest.managerFactory.getPedigreeService();
		// make sure a seed User(1) is present in the db otherwise add one
		User user = GermplasmDataManagerImplTest.userDataManager.getUserById(1);
		if (user == null) {
			user = new User();
			user.setAccess(1);
			user.setAdate(1);
			user.setCdate(1);
			user.setInstalid(1);
			user.setName("uname");
			user.setPassword("upwd");
			user.setPersonid(1);
			user.setStatus(1);
			user.setType(1);
			GermplasmDataManagerImplTest.userDataManager.addUser(user);
		}

		GermplasmDataManagerImplTest.workbenchTestDataUtil = WorkbenchTestDataUtil.getInstance();
		GermplasmDataManagerImplTest.workbenchTestDataUtil.setUpWorkbench();
		GermplasmDataManagerImplTest.commonTestProject = GermplasmDataManagerImplTest.workbenchTestDataUtil.getCommonTestProject();

		GermplasmDataManagerImplTest.germplasmTestDataGenerator.createGermplasmRecords(200, "CML");
	}

	@Test
	public void testGetMethodsByIDs() throws Exception {

		// Attempt to get all locations so we can proceed
		List<Method> locationList = GermplasmDataManagerImplTest.manager.getAllMethods();
		Assert.assertTrue(locationList != null);
		Assert.assertTrue("we cannot proceed test if size < 0", locationList.size() > 0);

		List<Integer> ids = new ArrayList<Integer>();

		for (Method ls : locationList) {
			ids.add(ls.getMid());

			// only get subset of locations
			if (ids.size() < 5) {
				break;
			}
		}

		List<Method> results = GermplasmDataManagerImplTest.manager.getMethodsByIDs(ids);
		Assert.assertTrue(results != null);
		Assert.assertTrue(results.size() < 5);

		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);
	}

	@Test
	public void testGetGermplasmByName() throws Exception {
		String name = "CML502RLT";

		List<Germplasm> germplasmList =
				GermplasmDataManagerImplTest.manager.getGermplasmByName(name, 0, 5, GetGermplasmByNameModes.NORMAL, Operation.EQUAL, null,
						null, Database.CENTRAL);
		Assert.assertTrue(germplasmList != null);

		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetGermplasmByName(" + name + "): ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, germplasmList);
	}

	@Test
	public void testGetGermplasmByNameOriginalStandardizedAndNoSpace() throws Exception {
		String name = "IR  65";
		List<Germplasm> germplasmList =
				GermplasmDataManagerImplTest.manager.getGermplasmByName(name, 0,
						Long.valueOf(GermplasmDataManagerImplTest.manager.countGermplasmByName(name, Operation.EQUAL)).intValue(),
						Operation.EQUAL);

		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetGermplasmByNameOriginalStandardizedAndNoSpace(" + name + "): "
				+ germplasmList.size());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, germplasmList);

		name = "IR 65%";
		germplasmList =
				GermplasmDataManagerImplTest.manager.getGermplasmByName(name, 0,
						Long.valueOf(GermplasmDataManagerImplTest.manager.countGermplasmByName(name, Operation.LIKE)).intValue(),
						Operation.LIKE);
	}

	@Test
	public void testCountGermplasmByName() throws Exception {
		String name = "IR 10";
		long count =
				GermplasmDataManagerImplTest.manager.countGermplasmByName(name, GetGermplasmByNameModes.NORMAL, Operation.EQUAL, null,
						null, Database.CENTRAL);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testCountGermplasmByName(" + name + "): " + count);
	}

	@Test
	public void testCountGermplasmByNameOriginalStandardizedAndNoSpace() throws Exception {
		String name = "IR  65";
		long count = GermplasmDataManagerImplTest.manager.countGermplasmByName(name, Operation.EQUAL);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testCountGermplasmByNameOriginalStandardizedAndNoSpace(" + name + "): " + count);
	}

	@Test
	public void testGetGermplasmByNameUsingLike() throws Exception {
		String name = "IR%";

		List<Germplasm> germplasmList =
				GermplasmDataManagerImplTest.manager.getGermplasmByName(name, 0, 5, GetGermplasmByNameModes.NORMAL, Operation.LIKE, null,
						null, Database.CENTRAL);
		Assert.assertTrue(germplasmList != null);

		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetGermplasmByNameUsingLike(" + name + "): ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, germplasmList);
	}

	@Test
	public void testCountGermplasmByNameUsingLike() throws Exception {
		String name = "IR%";

		long count =
				GermplasmDataManagerImplTest.manager.countGermplasmByName(name, GetGermplasmByNameModes.NORMAL, Operation.LIKE, null, null,
						Database.CENTRAL);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testCountGermplasmByNameUsingLike(" + name + ") RESULTS:" + count);
	}

	@Test
	public void testGetGermplasmByNameWithStatus() throws Exception {
		String name = "IR 64";
		List<Germplasm> germplasmList =
				GermplasmDataManagerImplTest.manager.getGermplasmByName(name, 0, 5, GetGermplasmByNameModes.NORMAL, Operation.EQUAL,
						Integer.valueOf(1), null, Database.CENTRAL);
		Assert.assertTrue(germplasmList != null);

		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetGermplasmByNameWithStatus(" + name + "): ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, germplasmList);
	}

	@Test
	public void testCountGermplasmByNameWithStatus() throws Exception {
		String name = "IR 64";
		long count =
				GermplasmDataManagerImplTest.manager.countGermplasmByName(name, GetGermplasmByNameModes.NORMAL, Operation.EQUAL,
						Integer.valueOf(1), null, Database.CENTRAL);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testCountGermplasmByNameWithStatus(" + name + "): " + count);
	}

	@Test
	public void testGetGermplasmByNameWithStatusAndType() throws Exception {
		String name = "IR 64";
		List<Germplasm> germplasmList =
				GermplasmDataManagerImplTest.manager.getGermplasmByName(name, 0, 5, GetGermplasmByNameModes.NORMAL, Operation.EQUAL,
						Integer.valueOf(1), GermplasmNameType.RELEASE_NAME, Database.CENTRAL);
		Assert.assertTrue(germplasmList != null);

		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetGermplasmByNameWithStatusAndType(" + name + "): ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, germplasmList);
	}

	@Test
	public void testCountGermplasmByNameWithStatusAndType() throws Exception {
		String name = "IR 64";
		long count =
				GermplasmDataManagerImplTest.manager.countGermplasmByName(name, GetGermplasmByNameModes.NORMAL, Operation.EQUAL,
						Integer.valueOf(1), GermplasmNameType.RELEASE_NAME, Database.CENTRAL);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testCountGermplasmByNameWithStatusAndType(" + name + "): " + count);
	}

	@Test
	public void testGetGermplasmByNameWithStatusUsingLike() throws Exception {
		String name = "IR%";
		List<Germplasm> germplasmList =
				GermplasmDataManagerImplTest.manager.getGermplasmByName(name, 0, 5, GetGermplasmByNameModes.NORMAL, Operation.LIKE,
						Integer.valueOf(1), null, Database.CENTRAL);
		Assert.assertTrue(germplasmList != null);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetGermplasmByNameWithStatusUsingLike(" + name + "): ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, germplasmList);
	}

	@Test
	public void testGetGermplasmByNameWithStatusAndTypeUsingLike() throws Exception {
		String name = "IR%";
		List<Germplasm> germplasmList =
				GermplasmDataManagerImplTest.manager.getGermplasmByName(name, 0, 5, GetGermplasmByNameModes.NORMAL, Operation.LIKE,
						Integer.valueOf(1), GermplasmNameType.RELEASE_NAME, Database.CENTRAL);
		Assert.assertTrue(germplasmList != null);

		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetGermplasmByNameWithStatusAndTypeUsingLike(" + name + "): ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, germplasmList);
	}

	@Test
	public void testGetGermplasmByLocationNameUsingEqual() throws Exception {
		String name = "Philippines";
		List<Germplasm> germplasmList =
				GermplasmDataManagerImplTest.manager.getGermplasmByLocationName(name, 0, 5, Operation.EQUAL, Database.CENTRAL);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetGermplasmByLocationNameUsingEqual(" + name + "): ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, germplasmList);
	}

	@Test
	public void testCountGermplasmByLocationNameUsingEqual() throws Exception {
		String name = "Philippines";
		long count = GermplasmDataManagerImplTest.manager.countGermplasmByLocationName(name, Operation.EQUAL, Database.CENTRAL);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testCountGermplasmByLocationNameUsingEqual(" + name + "): " + count);
	}

	@Test
	public void testGetGermplasmByLocationNameUsingLike() throws Exception {
		String name = "International%";
		List<Germplasm> germplasmList =
				GermplasmDataManagerImplTest.manager.getGermplasmByLocationName(name, 0, 5, Operation.LIKE, Database.CENTRAL);
		Assert.assertTrue(germplasmList != null);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetGermplasmByLocationNameUsingLike(" + name + "): ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, germplasmList);
	}

	@Test
	public void testCountGermplasmByLocationNameUsingLike() throws Exception {
		String name = "International%";
		long count = GermplasmDataManagerImplTest.manager.countGermplasmByLocationName(name, Operation.LIKE, Database.CENTRAL);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testCountGermplasmByLocationNameUsingLike(" + name + "): " + count);
	}

	@Test
	public void testGetGermplasmByMethodNameUsingEqual() throws Exception {
		String name = "SINGLE CROSS";

		List<Germplasm> germplasmList =
				GermplasmDataManagerImplTest.manager.getGermplasmByMethodName(name, 0, 5, Operation.EQUAL, Database.CENTRAL);
		Assert.assertTrue(germplasmList != null);

		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetGermplasmByMethodNameUsingEqual(" + name + "): ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, germplasmList);
	}

	@Test
	public void testCountGermplasmByMethodNameUsingEqual() throws Exception {
		String name = "SINGLE CROSS";
		long count = GermplasmDataManagerImplTest.manager.countGermplasmByMethodName(name, Operation.EQUAL, Database.CENTRAL);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testCountGermplasmByMethodNameUsingEqual(" + name + "): " + count);
	}

	@Test
	public void testGetGermplasmByMethodNameUsingLike() throws Exception {
		String name = "%CROSS%";

		List<Germplasm> germplasmList =
				GermplasmDataManagerImplTest.manager.getGermplasmByMethodName(name, 0, 5, Operation.LIKE, Database.CENTRAL);
		Assert.assertTrue(germplasmList != null);

		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetGermplasmByMethodNameUsingLike(" + name + "): ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, germplasmList);
	}

	@Test
	public void testCountGermplasmByMethodNameUsingLike() throws Exception {
		String name = "%CROSS%";
		long count = GermplasmDataManagerImplTest.manager.countGermplasmByMethodName(name, Operation.LIKE, Database.CENTRAL);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testCountGermplasmByMethodNameUsingLike(" + name + "): " + count);
	}

	@Test
	public void testGetGermplasmByGID() throws Exception {
		Integer gid = Integer.valueOf(50533);
		Germplasm germplasm = GermplasmDataManagerImplTest.manager.getGermplasmByGID(gid);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetGermplasmByGID(" + gid + "): " + germplasm);
	}

	@Test
	public void testGetGermplasmWithPrefName() throws Exception {
		Integer gid = Integer.valueOf(50533);
		Germplasm germplasm = GermplasmDataManagerImplTest.manager.getGermplasmWithPrefName(gid);

		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetGermplasmWithPrefName(" + gid + "): " + germplasm);
		if (germplasm != null) {
			Debug.println("  preferredName = " + germplasm.getPreferredName());
		}
	}

	@Test
	public void testGetGermplasmWithPrefAbbrev() throws Exception {
		Integer gid = Integer.valueOf(151);
		Germplasm germplasm = GermplasmDataManagerImplTest.manager.getGermplasmWithPrefAbbrev(gid);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetGermplasmWithPrefAbbrev(" + gid + "): " + germplasm);
		Debug.println("  preferredName = " + germplasm.getPreferredName());
		Debug.println("  preferredAbbreviation = " + germplasm.getPreferredAbbreviation());
	}

	@Test
	public void testGetGermplasmNameByID() throws Exception {
		Integer gid = Integer.valueOf(42268);
		Name name = GermplasmDataManagerImplTest.manager.getGermplasmNameByID(gid);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetGermplasmNameByID(" + gid + "): " + name);
	}

	@Test
	public void testGetNamesByGID() throws Exception {
		Integer gid = Integer.valueOf(2434138);
		List<Name> names = GermplasmDataManagerImplTest.manager.getNamesByGID(gid, null, null);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetNamesByGID(" + gid + "): " + names.size());
		Debug.printObjects(names);
	}

	@Test
	public void testGetPreferredNameByGID() throws Exception {
		Integer gid = Integer.valueOf(1);
		Debug.println(MiddlewareIntegrationTest.INDENT,
				"testGetPreferredNameByGID(" + gid + "): " + GermplasmDataManagerImplTest.manager.getPreferredNameByGID(gid));
	}

	@Test
	public void testGetPreferredNameValueByGID() throws Exception {
		Integer gid = Integer.valueOf(1);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetPreferredNameValueByGID(" + gid + "): "
				+ GermplasmDataManagerImplTest.manager.getPreferredNameValueByGID(gid));
	}

	@Test
	public void testGetPreferredAbbrevByGID() throws Exception {
		Integer gid = Integer.valueOf(1);
		Debug.println(MiddlewareIntegrationTest.INDENT,
				"testGetPreferredAbbrevByGID(" + gid + "): " + GermplasmDataManagerImplTest.manager.getPreferredAbbrevByGID(gid));
	}

	@Test
	public void testGetPreferredIdByGID() throws Exception {
		Integer gid = Integer.valueOf(986634);
		Debug.println(MiddlewareIntegrationTest.INDENT,
				"testGetPreferredIdByGID(" + gid + "): " + GermplasmDataManagerImplTest.manager.getPreferredIdByGID(gid));
	}

	@Test
	public void testGetPreferredIdsByListId() throws Exception {
		Integer listId = Integer.valueOf(2591);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetPreferredIdsByListId(" + listId + "): "
				+ GermplasmDataManagerImplTest.manager.getPreferredIdsByListId(listId));
	}

	@Test
	public void testGetNameByGIDAndNval() throws Exception {
		Integer gid = Integer.valueOf(225266);
		String nVal = "C 65-44";
		Debug.println(MiddlewareIntegrationTest.INDENT,
				"testGetNameByGIDAndNval(" + gid + ", " + nVal + ", GetGermplasmByNameModes.NORMAL) : "
						+ GermplasmDataManagerImplTest.manager.getNameByGIDAndNval(gid, nVal, GetGermplasmByNameModes.NORMAL));
		Debug.println(MiddlewareIntegrationTest.INDENT,
				"testGetNameByGIDAndNval(" + gid + ", " + nVal + ", GetGermplasmByNameModes.SPACES_REMOVED) : "
						+ GermplasmDataManagerImplTest.manager.getNameByGIDAndNval(gid, nVal, GetGermplasmByNameModes.SPACES_REMOVED));
		Debug.println(MiddlewareIntegrationTest.INDENT,
				"testGetNameByGIDAndNval(" + gid + ", " + nVal + ", GetGermplasmByNameModes.STANDARDIZED) : "
						+ GermplasmDataManagerImplTest.manager.getNameByGIDAndNval(gid, nVal, GetGermplasmByNameModes.STANDARDIZED));
	}

	@Test
	public void testGetNamesByGIDWithStatus() throws Exception {
		Integer gid = Integer.valueOf(50533);
		Integer status = Integer.valueOf(1);
		GermplasmNameType type = null;
		List<Name> names = GermplasmDataManagerImplTest.manager.getNamesByGID(gid, status, type);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetNamesByGIDWithStatus(gid=" + gid + ", status" + status + ", type=" + type
				+ "): " + names);
	}

	@Test
	public void testGetNamesByGIDWithStatusAndType() throws Exception {
		Integer gid = Integer.valueOf(50533);
		Integer status = Integer.valueOf(8);
		GermplasmNameType type = GermplasmNameType.INTERNATIONAL_TESTING_NUMBER;
		List<Name> names = GermplasmDataManagerImplTest.manager.getNamesByGID(gid, status, type);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetNamesByGIDWithStatusAndType(gid=" + gid + ", status" + status + ", type="
				+ type + "): " + names);
	}

	@Test
	public void testGetAttributesByGID() throws Exception {
		Integer gid = Integer.valueOf(50533);
		List<Attribute> attributes = GermplasmDataManagerImplTest.manager.getAttributesByGID(gid);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetAttributesByGID(" + gid + "): " + attributes);
	}

	@Test
	public void testAddMethod() throws MiddlewareQueryException {
		Method method = new Method();
		method.setMname("yesno");
		method.setGeneq(0);
		method.setLmid(2);
		method.setMattr(0);
		method.setMcode("UGM");
		method.setMdate(19980610);
		method.setMdesc("yay");
		method.setMfprg(0);
		method.setMgrp("S");
		method.setMprgn(0);
		method.setReference(0);
		method.setUser(0);

		method.setMtype("GEN");

		GermplasmDataManagerImplTest.manager.addMethod(method);

		method = GermplasmDataManagerImplTest.manager.getMethodByID(method.getMid());
		Debug.println(MiddlewareIntegrationTest.INDENT, "testAddMethod(" + method + "): " + method);

		GermplasmDataManagerImplTest.manager.deleteMethod(method);
	}

	@Test
	public void testAddMethods() throws MiddlewareQueryException {
		List<Method> methods = new ArrayList<Method>();
		String programUUID = UUID.randomUUID().toString();
		methods.add(new Method(1, "GEN", "S", "UGM", "yesno", "description 1", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0),
				Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(2), Integer.valueOf(19980610), programUUID));
		methods.add(new Method(2, "GEN", "S", "UGM", "yesno", "description 2", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0),
				Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(2), Integer.valueOf(19980610), programUUID));
		methods.add(new Method(3, "GEN", "S", "UGM", "yesno", "description 3", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0),
				Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(2), Integer.valueOf(19980610), programUUID));

		List<Integer> methodsAdded = GermplasmDataManagerImplTest.manager.addMethod(methods);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testAddMethods() Methods added: " + methodsAdded.size());

		for (Integer id : methodsAdded) {
			Method method = GermplasmDataManagerImplTest.manager.getMethodByID(id);
			Debug.println(MiddlewareIntegrationTest.INDENT, method);
			GermplasmDataManagerImplTest.manager.deleteMethod(method);
		}
	}

	@Test
	public void testGetMethodsByUniqueID() throws MiddlewareQueryException {
		String programUUID = "030850c4-41f8-4baf-81a3-03b99669e996";
		List<Method> methodsFilteredByProgramUUID = GermplasmDataManagerImplTest.manager.getMethodsByUniqueID(programUUID);
		Assert.assertNotNull("Expecting to have returned results.", methodsFilteredByProgramUUID);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetMethodsByUniqueID(programUUID=" + programUUID + "): "
				+ methodsFilteredByProgramUUID.size());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT * 2, methodsFilteredByProgramUUID);
	}

	@Test
	public void testGetMethodsByType() throws MiddlewareQueryException {
		String type = "GEN"; // Tested with rice and cowpea
		int start = 0;
		int numOfRows = 5;

		List<Method> methods = GermplasmDataManagerImplTest.manager.getMethodsByType(type);
		Assert.assertNotNull("Expecting to have returned results.", methods);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetMethodsByType(type=" + type + "): " + methods.size());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT * 2, methods);

		String programUUID = "030850c4-41f8-4baf-81a3-03b99669e996";
		List<Method> methodsFilteredByProgramUUID = GermplasmDataManagerImplTest.manager.getMethodsByType(type, programUUID);
		Assert.assertNotNull("Expecting to have returned results.", methodsFilteredByProgramUUID);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetMethodsByType(type=" + type + ", programUUID=" + programUUID + "): "
				+ methodsFilteredByProgramUUID.size());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT * 2, methodsFilteredByProgramUUID);

		List<Method> methodList = GermplasmDataManagerImplTest.manager.getMethodsByType(type, start, numOfRows);
		Assert.assertNotNull("Expecting to have returned results.", methodList);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetMethodsByType(type=" + type + ", start=" + start + ", numOfRows="
				+ numOfRows + "): " + methodList.size());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT * 2, methodList);
	}

	@Test
	public void testCountMethodsByUniqueID() throws Exception {
		String programUUID = "030850c4-41f8-4baf-81a3-03b99669e996";
		long count = GermplasmDataManagerImplTest.manager.countMethodsByUniqueID(programUUID);
		Assert.assertTrue("Expecting to have returned results.", count > 0);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testCountMethodsByUniqueID(programUUID=" + programUUID + "): " + count);
	}

	@Test
	public void testCountMethodsByType() throws Exception {
		String type = "GEN";
		long count = GermplasmDataManagerImplTest.manager.countMethodsByType(type);
		Assert.assertTrue("Expecting to have returned results.", count > 0);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testCountMethodsByType(type=" + type + "): " + count);

		type = "GEN";
		String programUUID = "030850c4-41f8-4baf-81a3-03b99669e996";
		long countWithProgramUUID = GermplasmDataManagerImplTest.manager.countMethodsByType(type, programUUID);
		Assert.assertTrue("Expecting to have returned results.", count > 0);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testCountMethodsByType(type=" + type + "): " + countWithProgramUUID);

		Assert.assertTrue("The results that is filtered by programUUID must be less than or equal to the results without programUUID.",
				count >= countWithProgramUUID);
	}

	@Test
	public void testGetMethodsByGroup() throws MiddlewareQueryException {
		String group = "S"; // Tested with rice and cowpea
		int start = 0;
		int numOfRows = 5;

		List<Method> methods = GermplasmDataManagerImplTest.manager.getMethodsByGroup(group);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetMethodsByGroup(group=" + group + "): " + methods.size());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT * 2, methods);

		List<Method> methodList = GermplasmDataManagerImplTest.manager.getMethodsByGroup(group, start, numOfRows);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetMethodsByGroup(group=" + group + ", start=" + start + ", numOfRows="
				+ numOfRows + "): " + methodList.size());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, methodList);
	}

	@Test
	public void testGetMethodsByGroupIncludesGgroup() throws MiddlewareQueryException {
		String group = "O"; // Tested with rice and cowpea
		List<Method> methods = GermplasmDataManagerImplTest.manager.getMethodsByGroupIncludesGgroup(group);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetMethodsByGroup(group=" + group + "): " + methods.size());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, methods);
	}

	@Test
	public void testGetMethodsByGroupAndType() throws MiddlewareQueryException {
		String group = "O"; // Tested with rice and cowpea
		String type = "GEN"; // Tested with rice and cowpea

		List<Method> methods = GermplasmDataManagerImplTest.manager.getMethodsByGroupAndType(group, type);
		Debug.println(MiddlewareIntegrationTest.INDENT,
				"testGetMethodsByGroupAndType(group=" + group + "and " + type + "): " + methods.size());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, methods);
	}

	@Test
	public void testGetMethodsByGroupAndTypeAndName() throws MiddlewareQueryException {
		String group = "O"; // Tested with rice and cowpea
		String type = "GEN"; // Tested with rice and cowpea
		String name = "ALLO-POLYPLOID CF"; // Tested with rice and cowpea

		List<Method> methods = GermplasmDataManagerImplTest.manager.getMethodsByGroupAndTypeAndName(group, type, name);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetMethodsByGroupAndTypeAndName(group=" + group + " and type=" + type
				+ " and name=" + name + "): " + methods.size());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, methods);
	}

	@Test
	public void testCountMethodsByGroup() throws Exception {
		String group = "S"; // Tested with rice and cowpea
		long count = GermplasmDataManagerImplTest.manager.countMethodsByGroup(group);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testCountMethodsByGroup(group=" + group + "): " + count);
	}

	@Test
	public void testGetGermplasmDetailsByGermplasmNames() throws Exception {
		List<String> germplasmNames = Arrays.asList("C 65 CU   79", "C 65 CU 80", "C 65 CU 81", "Kevin 64", "Kevin 65", " BASMATI   370");
		// SQL TO VERIFY (CENTRAL AND LOCAL): select gid, nid, nval from names where nval in (:germplasmNames);

		List<GermplasmNameDetails> results =
				GermplasmDataManagerImplTest.manager
						.getGermplasmNameDetailsByGermplasmNames(germplasmNames, GetGermplasmByNameModes.NORMAL);
		Debug.println(MiddlewareIntegrationTest.INDENT, "GetGermplasmByNameModes.NORMAL:");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);

		results =
				GermplasmDataManagerImplTest.manager.getGermplasmNameDetailsByGermplasmNames(germplasmNames,
						GetGermplasmByNameModes.SPACES_REMOVED);
		Debug.println(MiddlewareIntegrationTest.INDENT, "GetGermplasmByNameModes.SPACES_REMOVED:");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);

		results =
				GermplasmDataManagerImplTest.manager.getGermplasmNameDetailsByGermplasmNames(germplasmNames,
						GetGermplasmByNameModes.STANDARDIZED);
		Debug.println(MiddlewareIntegrationTest.INDENT, "GetGermplasmByNameModes.STANDARDIZED:");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);

		results =
				GermplasmDataManagerImplTest.manager.getGermplasmNameDetailsByGermplasmNames(germplasmNames,
						GetGermplasmByNameModes.SPACES_REMOVED_BOTH_SIDES);
		Debug.println(MiddlewareIntegrationTest.INDENT, "GetGermplasmByNameModes.SPACES_REMOVED_BOTH_SIDES:");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);

	}

	@Test
	public void testUpdateGermplasmName() throws Exception {
		Integer nameId = 1; // Assumption: id=1 exists
		Name name = GermplasmDataManagerImplTest.manager.getGermplasmNameByID(nameId);
		if (name != null) {
			String nameBefore = name.toString();
			name.setLocationId(GermplasmDataManagerImplTest.locationManager.getLocationByID(1).getLocid()); // Assumption: location with
																											// id=1 exists
			GermplasmDataManagerImplTest.manager.updateGermplasmName(name);
			Debug.println(MiddlewareIntegrationTest.INDENT, "testUpdateGermplasmName(" + nameId + "): " + "\n\tBEFORE: " + nameBefore
					+ "\n\tAFTER: " + name.toString());
		}
	}

	@Test
	public void testAddGermplasmAttribute() throws Exception {
		Integer gid = Integer.valueOf(50533);
		Attribute attribute = new Attribute();
		attribute.setAdate(0);
		attribute.setAval("aval");
		attribute.setGermplasmId(gid);
		attribute.setLocationId(0);
		attribute.setUserId(0);
		attribute.setReferenceId(0);
		attribute.setTypeId(0);
		Integer id = GermplasmDataManagerImplTest.manager.addGermplasmAttribute(attribute);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testAddGermplasmAttribute(" + gid + "): " + id + " = " + attribute);
	}

	@Test
	public void testUpdateGermplasmAttribute() throws Exception {
		Integer attributeId = 1; // Assumption: attribute with id = 1 exists

		Attribute attribute = GermplasmDataManagerImplTest.manager.getAttributeById(attributeId);

		if (attribute != null) {
			String attributeString = "";
			attributeString = attribute.toString();
			attribute.setAdate(0);
			attribute.setLocationId(0);
			attribute.setUserId(0);
			attribute.setReferenceId(0);
			attribute.setTypeId(0);
			GermplasmDataManagerImplTest.manager.updateGermplasmAttribute(attribute);

			Debug.println(MiddlewareIntegrationTest.INDENT, "testUpdateGermplasmAttribute(" + attributeId + "): " + "\ntBEFORE: "
					+ attributeString + "\ntAFTER: " + attribute);
		}
	}

	@Test
	public void testGetUserDefinedFieldByFieldTable() throws MiddlewareQueryException {
		// String tableName="LOCATION";
		// String fieldType="LTYPE";

		String tableName = "ATRIBUTS";
		String fieldType = "ATTRIBUTE";
		List<UserDefinedField> userDefineField =
				GermplasmDataManagerImplTest.manager.getUserDefinedFieldByFieldTableNameAndType(tableName, fieldType);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetUserDefineFieldByTableNameAndType(type=" + tableName + "): "
				+ userDefineField.size());
		for (UserDefinedField u : userDefineField) {
			Debug.println(MiddlewareIntegrationTest.INDENT, u);
		}
	}

	@Test
	public void testGetCrossExpansion() throws Exception {
		CrossExpansionProperties crossExpansionProperties = new CrossExpansionProperties();
		crossExpansionProperties.setDefaultLevel(1);
		Debug.println(GermplasmDataManagerImplTest.pedigreeService.getCrossExpansion(Integer.valueOf(1), crossExpansionProperties));
	}

	@Test
	public void testGetNextSequenceNumberForCrossName() throws MiddlewareQueryException {
		String prefix = "IR";
		Debug.println("Next number in sequence for prefix (" + prefix + ") is : "
				+ GermplasmDataManagerImplTest.manager.getNextSequenceNumberForCrossName(prefix));
	}

	@Test
	public void testGetPreferredIdsByGIDs() throws MiddlewareQueryException {
		List<Integer> gids = Arrays.asList(1, 2, 3, 4, 5);
		Map<Integer, String> results = GermplasmDataManagerImplTest.manager.getPrefferedIdsByGIDs(gids);
		for (Integer gid : results.keySet()) {
			Debug.println(MiddlewareIntegrationTest.INDENT, gid + " : " + results.get(gid));
		}
	}

	@Test
	public void testGetAllMethods() throws Exception {
		List<Method> results = GermplasmDataManagerImplTest.manager.getAllMethods();
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);
	}

	@Test
	public void testGetAllMethodsNotGenerative() throws Exception {
		List<Method> results = GermplasmDataManagerImplTest.manager.getAllMethodsNotGenerative();
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);
	}

	@Test
	public void testGetAllGermplasm() throws Exception {
		List<Germplasm> germplasms = GermplasmDataManagerImplTest.manager.getAllGermplasm(0, 100, Database.CENTRAL);
		Assert.assertNotNull(germplasms);
		Assert.assertTrue(!germplasms.isEmpty());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, germplasms);
	}

	@Test
	@Ignore
	// Need to setup data first.
	public void testGetAttributeById() throws Exception {
		Integer id = Integer.valueOf(1);
		Attribute attributes = GermplasmDataManagerImplTest.manager.getAttributeById(id);
		Assert.assertNotNull(attributes);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetAttributeById(" + id + ") Results:");
		Debug.println(MiddlewareIntegrationTest.INDENT, attributes);
	}

	@Test
	public void testGetBibliographicalReferenceById() throws Exception {
		Integer id = Integer.valueOf(1);
		Bibref bibref = GermplasmDataManagerImplTest.manager.getBibliographicReferenceByID(id);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetBibliographicalReferenceById(" + id + "): " + bibref);
	}

	@Test
	public void testGetMethodByID() throws Exception {
		Integer id = Integer.valueOf(4);
		Method methodid = GermplasmDataManagerImplTest.manager.getMethodByID(id);
		Assert.assertNotNull(methodid);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetMethodByID(" + id + "): ");
		Debug.println(MiddlewareIntegrationTest.INDENT, methodid);
	}

	@Test
	public void testGetUserDefinedFieldByID() throws Exception {
		Integer id = Integer.valueOf(1);
		UserDefinedField result = GermplasmDataManagerImplTest.manager.getUserDefinedFieldByID(id);
		Assert.assertNotNull(result);
		Debug.println(MiddlewareIntegrationTest.INDENT, result);
	}

	@Test
	@Ignore
	// need to setup some data first.
	public void testGetBibliographicReferenceByID() throws Exception {
		Integer id = Integer.valueOf(1);
		Bibref result = GermplasmDataManagerImplTest.manager.getBibliographicReferenceByID(id);
		Debug.println(MiddlewareIntegrationTest.INDENT, result);
	}

	@Test
	public void testGetGermplasmByLocationId() throws Exception {
		String name = "RCH";
		int locationID = 0;

		List<Germplasm> germplasmList = GermplasmDataManagerImplTest.manager.getGermplasmByLocationId(name, locationID);
		Assert.assertTrue(germplasmList != null);

		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetGermplasmByLocationId(" + name + "): ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, germplasmList);
	}

	@Test
	public void testGetGermplasmByGidRange() throws Exception {
		int startGID = 1;
		int endGID = 5;

		List<Germplasm> germplasmList = GermplasmDataManagerImplTest.manager.getGermplasmByGidRange(startGID, endGID);
		Assert.assertTrue(germplasmList != null);

		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetGermplasmByGidRange(" + startGID + "," + endGID + "): ");
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, germplasmList);
	}

	@Test
	public void testGetGermplasmByGIDList() throws Exception {
		List<Integer> gids = Arrays.asList(1, 2, 3, 4, 5);

		List<Germplasm> germplasmList = GermplasmDataManagerImplTest.manager.getGermplasms(gids);
		Assert.assertTrue(germplasmList != null);

		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetGermplasmByGidList(" + gids + "): ");
		for (Germplasm g : germplasmList) {
			Debug.println(MiddlewareIntegrationTest.INDENT, g);
		}
	}

	@Test
	public void testGetPreferredNamesByGIDs() throws MiddlewareQueryException {
		List<Integer> gids = Arrays.asList(1, 2, 3, 4, 5);
		Map<Integer, String> results = GermplasmDataManagerImplTest.manager.getPreferredNamesByGids(gids);
		for (Integer gid : results.keySet()) {
			Debug.println(MiddlewareIntegrationTest.INDENT, gid + " : " + results.get(gid));
		}
	}

	@Test
	public void testGetLocationNamesByGIDs() throws MiddlewareQueryException {
		List<Integer> gids = Arrays.asList(1, 2, 3, 4, 5);
		Map<Integer, String> results = GermplasmDataManagerImplTest.manager.getLocationNamesByGids(gids);
		for (Integer gid : results.keySet()) {
			Debug.println(MiddlewareIntegrationTest.INDENT, gid + " : " + results.get(gid));
		}
	}

	@Test
	public void testSearchGermplasm() throws MiddlewareQueryException {
		String q = "CML";
		List<Germplasm> results = GermplasmDataManagerImplTest.manager.searchForGermplasm(q, Operation.LIKE, true, false);
		Debug.println(MiddlewareIntegrationTest.INDENT, "searchForGermplasm(" + q + "): " + results.size() + " matches found.");
	}

	@Test
	public void getGermplasmDatesByGids() throws MiddlewareQueryException {
		List<Integer> gids = Arrays.asList(1, 2, 3, 4, 5);
		Map<Integer, Integer> results = GermplasmDataManagerImplTest.manager.getGermplasmDatesByGids(gids);
		Debug.println(MiddlewareIntegrationTest.INDENT, "getGermplasmDatesByGids(" + gids + "): ");
		Debug.println(MiddlewareIntegrationTest.INDENT, results.toString());
	}

	@Test
	public void getMethodsByGids() throws MiddlewareQueryException {
		List<Integer> gids = Arrays.asList(1, 2, 3, 4, 5);
		Map<Integer, Object> results = GermplasmDataManagerImplTest.manager.getMethodsByGids(gids);
		Debug.println(MiddlewareIntegrationTest.INDENT, "getGermplasmDatesByGids(" + gids + "): ");
		Debug.println(MiddlewareIntegrationTest.INDENT, results.toString());
	}

	@Test
	public void getAttributeTypesByGIDList() throws MiddlewareQueryException {
		List<Integer> gids = Arrays.asList(1, 2, 3, 4, 5);
		List<UserDefinedField> results = GermplasmDataManagerImplTest.manager.getAttributeTypesByGIDList(gids);
		Debug.println(MiddlewareIntegrationTest.INDENT, "getAttributeTypesByGIDList(" + gids + "): ");
		for (UserDefinedField field : results) {
			Debug.println(MiddlewareIntegrationTest.INDENT, field.getFname());
		}
	}

	@Test
	public void getAttributeValuesByTypeAndGIDList() throws MiddlewareQueryException {
		List<Integer> gids = Arrays.asList(1, 2, 3, 4, 5);
		Integer attributeType = 1115;
		Map<Integer, String> results = GermplasmDataManagerImplTest.manager.getAttributeValuesByTypeAndGIDList(attributeType, gids);
		Debug.println(MiddlewareIntegrationTest.INDENT, "getAttributeValuesByTypeAndGIDList(" + attributeType + ", " + gids + "): ");
		Debug.println(MiddlewareIntegrationTest.INDENT, results.toString());
	}

	@Test
	public void getMethodClasses() throws MiddlewareQueryException {
		List<Term> terms = GermplasmDataManagerImplTest.manager.getMethodClasses();
		System.out.println(terms);
	}

	@Test
	public void testGetMethodByName() throws Exception {
		String name = "breeders seed";
		Method method = GermplasmDataManagerImplTest.manager.getMethodByName(name);
		Assert.assertNotNull(method);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetMethodByName(" + name + "): ");
		Debug.println(MiddlewareIntegrationTest.INDENT, method);
	}

	@Test
	public void testGetMethodByNameWithProgramUUID() throws Exception {
		String name = "breeders seed";
		String programUUID = GermplasmDataManagerImplTest.commonTestProject.getUniqueID();
		Method method = GermplasmDataManagerImplTest.manager.getMethodByName(name, programUUID);
		Assert.assertNotNull("Expecting the return method is not null.", method);
	}

	@Test
	public void testGetMethodByCode() throws Exception {
		String code = "VBS";
		Method method = GermplasmDataManagerImplTest.manager.getMethodByCode(code);
		Assert.assertNotNull(method);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetMethodByCode(" + code + "): ");
		Debug.println(MiddlewareIntegrationTest.INDENT, method);
	}

	@Test
	public void testGetMethodByCodeWithProgramUUID() throws Exception {
		String code = "VBS";
		String programUUID = GermplasmDataManagerImplTest.commonTestProject.getUniqueID();
		Method method = GermplasmDataManagerImplTest.manager.getMethodByCode(code, programUUID);
		Assert.assertNotNull("Expecting the return method is not null.", method);
	}

	@Test
	public void testAddUserDefinedField() throws Exception {
		UserDefinedField field = new UserDefinedField();
		field.setFtable("ATRIBUTS");
		field.setFtype("ATTRIBUTE");
		field.setFcode("MATURITY");
		field.setFname("Maturity class");
		field.setFfmt("MCLASS,ASSIGNED,C");
		field.setFdesc("-");
		field.setLfldno(0);

		// requires a seed User in the datase
		field.setUser(GermplasmDataManagerImplTest.userDataManager.getAllUsers().get(0));
		field.setFdate(20041116);
		field.setScaleid(0);

		Integer success = GermplasmDataManagerImplTest.manager.addUserDefinedField(field);
		Assert.assertTrue(success > 0);

		Debug.println(MiddlewareIntegrationTest.INDENT, "testAddUserDefinedField(" + field + "): ");
	}

	@Test
	public void testAddAttribute() throws Exception {
		Attribute attr = new Attribute();
		attr.setGermplasmId(237431);
		attr.setTypeId(1);
		attr.setUserId(1);
		attr.setAval("EARLY");
		attr.setLocationId(31);
		attr.setReferenceId(0);
		attr.setAdate(20041116);

		Integer success = GermplasmDataManagerImplTest.manager.addAttribute(attr);
		Assert.assertTrue(success > 0);

		Debug.println(MiddlewareIntegrationTest.INDENT, "testAddAttribute(" + attr + "): ");
		Debug.println(MiddlewareIntegrationTest.INDENT, success);
	}

	@Test
	public void getProgramMethodsAndDeleteByUniqueId() {
		// create program locations
		String programUUID = GermplasmDataManagerImplTest.commonTestProject.getUniqueID();
		int testMethodID1 = 100000;
		int testMethodID2 = 100001;
		Method testMethod1 = this.createMethodTestData(testMethodID1, programUUID);
		Method testMethod2 = this.createMethodTestData(testMethodID2, programUUID);
		try {
			GermplasmDataManagerImplTest.manager.addMethod(testMethod1);
			GermplasmDataManagerImplTest.manager.addMethod(testMethod2);
			// verify
			List<Method> methodList = GermplasmDataManagerImplTest.manager.getProgramMethods(programUUID);
			Assert.assertEquals("There should be 2 program methods with programUUID[" + programUUID + "]", 2, methodList.size());
			// delete locations
			GermplasmDataManagerImplTest.manager.deleteProgramMethodsByUniqueId(programUUID);
			methodList = GermplasmDataManagerImplTest.manager.getProgramMethods(programUUID);
			Assert.assertTrue("There should be no program methods with programUUID[" + programUUID + "]", methodList.isEmpty());
		} catch (MiddlewareQueryException e) {
			Assert.fail("Getting and deleting of program methods failed ");
		}
	}

	private Method createMethodTestData(int id, String programUUID) {
		Method method = new Method();
		method.setUniqueID(programUUID);
		method.setMid(id);
		method.setMname("TEST-LOCATION" + id);
		method.setMdesc("TEST-LOCATION-DESC" + id);
		method.setMcode("0");
		method.setMgrp("0");
		method.setMtype("0");
		method.setReference(0);
		method.setGeneq(0);
		method.setMprgn(0);
		method.setMfprg(0);
		method.setMattr(0);
		method.setUser(0);
		method.setLmid(0);
		method.setMdate(0);
		return method;
	}

	@Test
	public void testGetGermplasmWithMethodType() throws Exception {
		Integer gid = 1;
		Germplasm germplasm = GermplasmDataManagerImplTest.manager.getGermplasmWithMethodType(gid);
		Assert.assertNotNull("It should not be null", germplasm);
		Assert.assertEquals("It should be equals", gid, germplasm.getGid());
	}

	@Test
	@Ignore
	// TODO: This test is failed in Ontology branch. There is no lgid with -1 value in DBScripts.
	public void testGetGermplasmByLocalGID() throws Exception {
		Integer lgid = 0;

		Germplasm germplasm = GermplasmDataManagerImplTest.manager.getGermplasmByLocalGid(lgid);

		Assert.assertNotNull("It should not be null", germplasm);

		if (germplasm != null) {
			Assert.assertEquals("It should be equals", lgid, germplasm.getLgid());
		}
	}
}
