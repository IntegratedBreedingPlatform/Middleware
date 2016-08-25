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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.generationcp.middleware.GermplasmTestDataGenerator;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.NameDAO;
import org.generationcp.middleware.data.initializer.NameTestDataInitializer;
import org.generationcp.middleware.domain.gms.search.GermplasmSearchParameter;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Bibref;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmNameDetails;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

public class GermplasmDataManagerIntegrationTest extends IntegrationTestBase {

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Autowired
	private LocationDataManager locationManager;

	@Autowired
	private UserDataManager userDataManager;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	private NameDAO nameDAO;

	private GermplasmDAO germplasmDAO;

	private Project commonTestProject;
	private WorkbenchTestDataUtil workbenchTestDataUtil;

	private GermplasmTestDataGenerator germplasmTestDataGenerator;
	
	private NameTestDataInitializer nameTDI;
	@Before
	public void setUp() throws Exception {
		this.nameTDI =  new NameTestDataInitializer();
		if (this.nameDAO == null) {
			this.nameDAO = new NameDAO();
			this.nameDAO.setSession(this.sessionProvder.getSession());
		}

		if (this.germplasmDAO == null) {
			this.germplasmDAO = new GermplasmDAO();
			this.germplasmDAO.setSession(this.sessionProvder.getSession());
		}

		if (this.germplasmTestDataGenerator == null) {
			this.germplasmTestDataGenerator = new GermplasmTestDataGenerator(this.germplasmDataManager);
		}

		if (this.workbenchTestDataUtil == null) {
			this.workbenchTestDataUtil = new WorkbenchTestDataUtil(this.workbenchDataManager);
			this.workbenchTestDataUtil.setUpWorkbench();
		}

		if (this.commonTestProject == null) {
			this.commonTestProject = this.workbenchTestDataUtil.getCommonTestProject();
		}

		// Make sure a seed User(1) is present in the crop db otherwise add one
		User user = this.userDataManager.getUserById(1);
		if (user == null) {
			user = new User();
			user.setAccess(1);
			user.setAssignDate(1);
			user.setCloseDate(1);
			user.setInstalid(1);
			user.setName("uname");
			user.setPassword("upwd");
			user.setPersonid(1);
			user.setStatus(1);
			user.setType(1);
			this.userDataManager.addUser(user);
		}
	}

	@Test
	public void testGetMethodsByIDs() throws Exception {

		// Attempt to get all locations so we can proceed
		final List<Method> locationList = this.germplasmDataManager.getAllMethods();
		Assert.assertTrue(locationList != null);
		Assert.assertTrue("we cannot proceed test if size < 0", locationList.size() > 0);

		final List<Integer> ids = new ArrayList<>();

		for (final Method ls : locationList) {
			ids.add(ls.getMid());

			// only get subset of locations
			if (ids.size() < 5) {
				break;
			}
		}

		final List<Method> results = this.germplasmDataManager.getMethodsByIDs(ids);
		Assert.assertTrue(results != null);
		Assert.assertTrue(results.size() < 5);

		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testGetGermplasmByName() throws Exception {
		final String name = "CML502RLT";

		final List<Germplasm> germplasmList = this.germplasmDataManager.getGermplasmByName(name, 0, 5, Operation.EQUAL);
		Assert.assertTrue(germplasmList != null);

		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmByName(" + name + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, germplasmList);
	}

	@Test
	public void testCountGermplasmByName() throws Exception {
		final String name = "(CML454 X CML451)-B-3-1-1";
		final long count = this.germplasmDataManager.countGermplasmByName(name, Operation.EQUAL);
		Debug.println(IntegrationTestBase.INDENT, "testCountGermplasmByName(" + name + "): " + count);
	}

	@Test
	public void testGetGermplasmByNameUsingLike() throws Exception {
		final String name = "IR%";

		final List<Germplasm> germplasmList = this.germplasmDataManager.getGermplasmByName(name, 0, 5, Operation.LIKE);
		Assert.assertTrue(germplasmList != null);

		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmByNameUsingLike(" + name + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, germplasmList);
	}

	@Test
	public void testCountGermplasmByNameUsingLike() throws Exception {
		final String name = "IR%";

		final long count = this.germplasmDataManager.countGermplasmByName(name, Operation.LIKE);
		Debug.println(IntegrationTestBase.INDENT, "testCountGermplasmByNameUsingLike(" + name + ") RESULTS:" + count);
	}

	@Test
	public void testGetGermplasmByLocationNameUsingEqual() throws Exception {
		final String name = "Philippines";
		final List<Germplasm> germplasmList = this.germplasmDataManager.getGermplasmByLocationName(name, 0, 5, Operation.EQUAL);
		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmByLocationNameUsingEqual(" + name + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, germplasmList);
	}

	@Test
	public void testCountGermplasmByLocationNameUsingEqual() throws Exception {
		final String name = "Philippines";
		final long count = this.germplasmDataManager.countGermplasmByLocationName(name, Operation.EQUAL);
		Debug.println(IntegrationTestBase.INDENT, "testCountGermplasmByLocationNameUsingEqual(" + name + "): " + count);
	}

	@Test
	public void testGetGermplasmByLocationNameUsingLike() throws Exception {
		final String name = "International%";
		final List<Germplasm> germplasmList = this.germplasmDataManager.getGermplasmByLocationName(name, 0, 5, Operation.LIKE);
		Assert.assertTrue(germplasmList != null);
		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmByLocationNameUsingLike(" + name + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, germplasmList);
	}

	@Test
	public void testCountGermplasmByLocationNameUsingLike() throws Exception {
		final String name = "International%";
		final long count = this.germplasmDataManager.countGermplasmByLocationName(name, Operation.LIKE);
		Debug.println(IntegrationTestBase.INDENT, "testCountGermplasmByLocationNameUsingLike(" + name + "): " + count);
	}

	@Test
	public void testGetGermplasmByMethodNameUsingEqual() throws Exception {
		final String name = "SINGLE CROSS";

		final List<Germplasm> germplasmList = this.germplasmDataManager.getGermplasmByMethodName(name, 0, 5, Operation.EQUAL);
		Assert.assertTrue(germplasmList != null);

		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmByMethodNameUsingEqual(" + name + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, germplasmList);
	}

	@Test
	public void testCountGermplasmByMethodNameUsingEqual() throws Exception {
		final String name = "SINGLE CROSS";
		final long count = this.germplasmDataManager.countGermplasmByMethodName(name, Operation.EQUAL);
		Debug.println(IntegrationTestBase.INDENT, "testCountGermplasmByMethodNameUsingEqual(" + name + "): " + count);
	}

	@Test
	public void testGetGermplasmByMethodNameUsingLike() throws Exception {
		final String name = "%CROSS%";

		final List<Germplasm> germplasmList = this.germplasmDataManager.getGermplasmByMethodName(name, 0, 5, Operation.LIKE);
		Assert.assertTrue(germplasmList != null);

		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmByMethodNameUsingLike(" + name + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, germplasmList);
	}

	@Test
	public void testCountGermplasmByMethodNameUsingLike() throws Exception {
		final String name = "%CROSS%";
		final long count = this.germplasmDataManager.countGermplasmByMethodName(name, Operation.LIKE);
		Debug.println(IntegrationTestBase.INDENT, "testCountGermplasmByMethodNameUsingLike(" + name + "): " + count);
	}

	@Test
	public void testGetGermplasmByGID() throws Exception {
		final Integer gid = 50533;
		final Germplasm germplasm = this.germplasmDataManager.getGermplasmByGID(gid);
		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmByGID(" + gid + "): " + germplasm);
	}

	@Test
	public void testGetGermplasmWithPrefName() throws Exception {
		final Integer gid = 50533;
		final Germplasm germplasm = this.germplasmDataManager.getGermplasmWithPrefName(gid);

		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmWithPrefName(" + gid + "): " + germplasm);
		if (germplasm != null) {
			Debug.println("  preferredName = " + germplasm.getPreferredName());
		}
	}

	@Test
	public void testGetGermplasmWithPrefAbbrev() throws Exception {
		final Integer gid = 151;
		final Germplasm germplasm = this.germplasmDataManager.getGermplasmWithPrefAbbrev(gid);
		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmWithPrefAbbrev(" + gid + "): " + germplasm);
	}

	@Test
	public void testGetGermplasmNameByID() throws Exception {
		final Integer gid = 42268;
		final Name name = this.germplasmDataManager.getGermplasmNameByID(gid);
		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmNameByID(" + gid + "): " + name);
	}

	@Test
	public void testGetNamesByGID() throws Exception {
		final Integer gid = 2434138;
		final List<Name> names = this.germplasmDataManager.getNamesByGID(gid, null, null);
		Debug.println(IntegrationTestBase.INDENT, "testGetNamesByGID(" + gid + "): " + names.size());
		Debug.printObjects(names);
	}

	@Test
	public void testGetPreferredNameByGID() throws Exception {
		final Integer gid = 1;
		Debug.println(IntegrationTestBase.INDENT,
				"testGetPreferredNameByGID(" + gid + "): " + this.germplasmDataManager.getPreferredNameByGID(gid));
	}

	@Test
	public void testGetPreferredNameValueByGID() throws Exception {
		final Integer gid = 1;
		Debug.println(IntegrationTestBase.INDENT,
				"testGetPreferredNameValueByGID(" + gid + "): " + this.germplasmDataManager.getPreferredNameValueByGID(gid));
	}

	@Test
	public void testGetPreferredAbbrevByGID() throws Exception {
		final Integer gid = 1;
		Debug.println(IntegrationTestBase.INDENT,
				"testGetPreferredAbbrevByGID(" + gid + "): " + this.germplasmDataManager.getPreferredAbbrevByGID(gid));
	}

	@Test
	public void testGetPreferredIdByGID() throws Exception {
		final Integer gid = 986634;
		Debug.println(IntegrationTestBase.INDENT,
				"testGetPreferredIdByGID(" + gid + "): " + this.germplasmDataManager.getPreferredIdByGID(gid));
	}

	@Test
	public void testGetPreferredIdsByListId() throws Exception {
		final Integer listId = 2591;
		Debug.println(IntegrationTestBase.INDENT,
				"testGetPreferredIdsByListId(" + listId + "): " + this.germplasmDataManager.getPreferredIdsByListId(listId));
	}

	@Test
	public void testGetNameByGIDAndNval() throws Exception {
		final Integer gid = 225266;
		final String nVal = "C 65-44";
		Debug.println(IntegrationTestBase.INDENT, "testGetNameByGIDAndNval(" + gid + ", " + nVal + ", GetGermplasmByNameModes.NORMAL) : "
				+ this.germplasmDataManager.getNameByGIDAndNval(gid, nVal, GetGermplasmByNameModes.NORMAL));
		Debug.println(
				IntegrationTestBase.INDENT,
				"testGetNameByGIDAndNval(" + gid + ", " + nVal + ", GetGermplasmByNameModes.SPACES_REMOVED) : "
						+ this.germplasmDataManager.getNameByGIDAndNval(gid, nVal, GetGermplasmByNameModes.SPACES_REMOVED));
		Debug.println(
				IntegrationTestBase.INDENT,
				"testGetNameByGIDAndNval(" + gid + ", " + nVal + ", GetGermplasmByNameModes.STANDARDIZED) : "
						+ this.germplasmDataManager.getNameByGIDAndNval(gid, nVal, GetGermplasmByNameModes.STANDARDIZED));
	}

	@Test
	public void testGetNamesByGIDWithStatus() throws Exception {
		final Integer gid = 50533;
		final Integer status = 1;
		final GermplasmNameType type = null;
		final List<Name> names = this.germplasmDataManager.getNamesByGID(gid, status, type);
		Debug.println(IntegrationTestBase.INDENT, "testGetNamesByGIDWithStatus(gid=" + gid + ", status" + status + ", type=" + type + "): "
				+ names);
	}

	@Test
	public void testGetNamesByGIDWithStatusAndType() throws Exception {
		final Integer gid = 50533;
		final Integer status = 8;
		final GermplasmNameType type = GermplasmNameType.INTERNATIONAL_TESTING_NUMBER;
		final List<Name> names = this.germplasmDataManager.getNamesByGID(gid, status, type);
		Debug.println(IntegrationTestBase.INDENT, "testGetNamesByGIDWithStatusAndType(gid=" + gid + ", status" + status + ", type=" + type
				+ "): " + names);
	}

	@Test
	public void testGetAttributesByGID() throws Exception {
		final Integer gid = 50533;
		final List<Attribute> attributes = this.germplasmDataManager.getAttributesByGID(gid);
		Debug.println(IntegrationTestBase.INDENT, "testGetAttributesByGID(" + gid + "): " + attributes);
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

		this.germplasmDataManager.addMethod(method);

		method = this.germplasmDataManager.getMethodByID(method.getMid());
		Debug.println(IntegrationTestBase.INDENT, "testAddMethod(" + method + "): " + method);

		this.germplasmDataManager.deleteMethod(method);
	}

	@Test
	public void testAddMethods() throws MiddlewareQueryException {
		final List<Method> methods = new ArrayList<>();
		final String programUUID = UUID.randomUUID().toString();
		methods.add(new Method(null, "GEN", "S", "UGM", "yesno", "description 1", Integer.valueOf(0), Integer.valueOf(0), Integer
				.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(2), Integer.valueOf(19980610),
				programUUID));
		methods.add(new Method(null, "GEN", "S", "UGM", "yesno", "description 2", Integer.valueOf(0), Integer.valueOf(0), Integer
				.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(2), Integer.valueOf(19980610),
				programUUID));
		methods.add(new Method(null, "GEN", "S", "UGM", "yesno", "description 3", Integer.valueOf(0), Integer.valueOf(0), Integer
				.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(2), Integer.valueOf(19980610),
				programUUID));

		final List<Integer> methodsAdded = this.germplasmDataManager.addMethod(methods);
		Debug.println(IntegrationTestBase.INDENT, "testAddMethods() Methods added: " + methodsAdded.size());

		for (final Integer id : methodsAdded) {
			final Method method = this.germplasmDataManager.getMethodByID(id);
			Debug.println(IntegrationTestBase.INDENT, method);
			this.germplasmDataManager.deleteMethod(method);
		}
	}

	@Test
	public void testGetMethodsByUniqueID() throws MiddlewareQueryException {
		final String programUUID = "030850c4-41f8-4baf-81a3-03b99669e996";
		final List<Method> methodsFilteredByProgramUUID = this.germplasmDataManager.getMethodsByUniqueID(programUUID);
		Assert.assertNotNull("Expecting to have returned results.", methodsFilteredByProgramUUID);
		Debug.println(IntegrationTestBase.INDENT, "testGetMethodsByUniqueID(programUUID=" + programUUID + "): "
				+ methodsFilteredByProgramUUID.size());
		Debug.printObjects(IntegrationTestBase.INDENT * 2, methodsFilteredByProgramUUID);
	}

	@Test
	public void testGetMethodsByType() throws MiddlewareQueryException {
		final String type = "GEN"; // Tested with rice and cowpea
		final int start = 0;
		final int numOfRows = 5;

		final List<Method> methods = this.germplasmDataManager.getMethodsByType(type);
		Assert.assertNotNull("Expecting to have returned results.", methods);
		Debug.println(IntegrationTestBase.INDENT, "testGetMethodsByType(type=" + type + "): " + methods.size());
		Debug.printObjects(IntegrationTestBase.INDENT * 2, methods);

		final String programUUID = "030850c4-41f8-4baf-81a3-03b99669e996";
		final List<Method> methodsFilteredByProgramUUID = this.germplasmDataManager.getMethodsByType(type, programUUID);
		Assert.assertNotNull("Expecting to have returned results.", methodsFilteredByProgramUUID);
		Debug.println(IntegrationTestBase.INDENT, "testGetMethodsByType(type=" + type + ", programUUID=" + programUUID + "): "
				+ methodsFilteredByProgramUUID.size());
		Debug.printObjects(IntegrationTestBase.INDENT * 2, methodsFilteredByProgramUUID);

		final List<Method> methodList = this.germplasmDataManager.getMethodsByType(type, start, numOfRows);
		Assert.assertNotNull("Expecting to have returned results.", methodList);
		Debug.println(IntegrationTestBase.INDENT, "testGetMethodsByType(type=" + type + ", start=" + start + ", numOfRows=" + numOfRows
				+ "): " + methodList.size());
		Debug.printObjects(IntegrationTestBase.INDENT * 2, methodList);
	}

	@Test
	public void testCountMethodsByUniqueID() throws Exception {
		final String programUUID = "030850c4-41f8-4baf-81a3-03b99669e996";
		final long count = this.germplasmDataManager.countMethodsByUniqueID(programUUID);
		Assert.assertTrue("Expecting to have returned results.", count > 0);
		Debug.println(IntegrationTestBase.INDENT, "testCountMethodsByUniqueID(programUUID=" + programUUID + "): " + count);
	}

	@Test
	public void testCountMethodsByType() throws Exception {
		String type = "GEN";
		final long count = this.germplasmDataManager.countMethodsByType(type);
		Assert.assertTrue("Expecting to have returned results.", count > 0);
		Debug.println(IntegrationTestBase.INDENT, "testCountMethodsByType(type=" + type + "): " + count);

		type = "GEN";
		final String programUUID = "030850c4-41f8-4baf-81a3-03b99669e996";
		final long countWithProgramUUID = this.germplasmDataManager.countMethodsByType(type, programUUID);
		Assert.assertTrue("Expecting to have returned results.", count > 0);
		Debug.println(IntegrationTestBase.INDENT, "testCountMethodsByType(type=" + type + "): " + countWithProgramUUID);

		Assert.assertTrue("The results that is filtered by programUUID must be less than or equal to the results without programUUID.",
				count >= countWithProgramUUID);
	}

	@Test
	public void testGetMethodsByGroup() throws MiddlewareQueryException {
		final String group = "S"; // Tested with rice and cowpea
		final int start = 0;
		final int numOfRows = 5;

		final List<Method> methods = this.germplasmDataManager.getMethodsByGroup(group);
		Debug.println(IntegrationTestBase.INDENT, "testGetMethodsByGroup(group=" + group + "): " + methods.size());
		Debug.printObjects(IntegrationTestBase.INDENT * 2, methods);

		final List<Method> methodList = this.germplasmDataManager.getMethodsByGroup(group, start, numOfRows);
		Debug.println(IntegrationTestBase.INDENT, "testGetMethodsByGroup(group=" + group + ", start=" + start + ", numOfRows=" + numOfRows
				+ "): " + methodList.size());
		Debug.printObjects(IntegrationTestBase.INDENT, methodList);
	}

	@Test
	public void testGetMethodsByGroupIncludesGgroup() throws MiddlewareQueryException {
		final String group = "O"; // Tested with rice and cowpea
		final List<Method> methods = this.germplasmDataManager.getMethodsByGroupIncludesGgroup(group);
		Debug.println(IntegrationTestBase.INDENT, "testGetMethodsByGroup(group=" + group + "): " + methods.size());
		Debug.printObjects(IntegrationTestBase.INDENT, methods);
	}

	@Test
	public void testGetMethodsByGroupAndType() throws MiddlewareQueryException {
		final String group = "O"; // Tested with rice and cowpea
		final String type = "GEN"; // Tested with rice and cowpea

		final List<Method> methods = this.germplasmDataManager.getMethodsByGroupAndType(group, type);
		Debug.println(IntegrationTestBase.INDENT, "testGetMethodsByGroupAndType(group=" + group + "and " + type + "): " + methods.size());
		Debug.printObjects(IntegrationTestBase.INDENT, methods);
	}

	@Test
	public void testGetMethodsByGroupAndTypeAndName() throws MiddlewareQueryException {
		final String group = "O"; // Tested with rice and cowpea
		final String type = "GEN"; // Tested with rice and cowpea
		final String name = "ALLO-POLYPLOID CF"; // Tested with rice and cowpea

		final List<Method> methods = this.germplasmDataManager.getMethodsByGroupAndTypeAndName(group, type, name);
		Debug.println(IntegrationTestBase.INDENT, "testGetMethodsByGroupAndTypeAndName(group=" + group + " and type=" + type + " and name="
				+ name + "): " + methods.size());
		Debug.printObjects(IntegrationTestBase.INDENT, methods);
	}

	@Test
	public void testCountMethodsByGroup() throws Exception {
		final String group = "S"; // Tested with rice and cowpea
		final long count = this.germplasmDataManager.countMethodsByGroup(group);
		Debug.println(IntegrationTestBase.INDENT, "testCountMethodsByGroup(group=" + group + "): " + count);
	}

	@Test
	public void testGetGermplasmDetailsByGermplasmNames() throws Exception {
		final List<String> germplasmNames =
				Arrays.asList("C 65 CU   79", "C 65 CU 80", "C 65 CU 81", "Kevin 64", "Kevin 65", " BASMATI   370");
		// SQL TO VERIFY (CENTRAL AND LOCAL): select gid, nid, nval from names where nval in (:germplasmNames);

		List<GermplasmNameDetails> results =
				this.germplasmDataManager.getGermplasmNameDetailsByGermplasmNames(germplasmNames, GetGermplasmByNameModes.NORMAL);
		Debug.println(IntegrationTestBase.INDENT, "GetGermplasmByNameModes.NORMAL:");
		Debug.printObjects(IntegrationTestBase.INDENT, results);

		results = this.germplasmDataManager.getGermplasmNameDetailsByGermplasmNames(germplasmNames, GetGermplasmByNameModes.SPACES_REMOVED);
		Debug.println(IntegrationTestBase.INDENT, "GetGermplasmByNameModes.SPACES_REMOVED:");
		Debug.printObjects(IntegrationTestBase.INDENT, results);

		results = this.germplasmDataManager.getGermplasmNameDetailsByGermplasmNames(germplasmNames, GetGermplasmByNameModes.STANDARDIZED);
		Debug.println(IntegrationTestBase.INDENT, "GetGermplasmByNameModes.STANDARDIZED:");
		Debug.printObjects(IntegrationTestBase.INDENT, results);

		results =
				this.germplasmDataManager.getGermplasmNameDetailsByGermplasmNames(germplasmNames,
						GetGermplasmByNameModes.SPACES_REMOVED_BOTH_SIDES);
		Debug.println(IntegrationTestBase.INDENT, "GetGermplasmByNameModes.SPACES_REMOVED_BOTH_SIDES:");
		Debug.printObjects(IntegrationTestBase.INDENT, results);

	}

	@Test
	public void testUpdateGermplasmName() throws Exception {
		final Integer nameId = 1; // Assumption: id=1 exists
		final Name name = this.germplasmDataManager.getGermplasmNameByID(nameId);
		if (name != null) {
			final String nameBefore = name.toString();
			name.setLocationId(this.locationManager.getLocationByID(1).getLocid()); // Assumption: location with
			// id=1 exists
			this.germplasmDataManager.updateGermplasmName(name);
			Debug.println(IntegrationTestBase.INDENT, "testUpdateGermplasmName(" + nameId + "): " + "\n\tBEFORE: " + nameBefore
					+ "\n\tAFTER: " + name.toString());
		}
	}

	@Test
	public void testAddGermplasmAttribute() throws Exception {
		final Integer gid = 50533;
		final Attribute attribute = new Attribute();
		attribute.setAdate(0);
		attribute.setAval("aval");
		attribute.setGermplasmId(gid);
		attribute.setLocationId(0);
		attribute.setUserId(0);
		attribute.setReferenceId(0);
		attribute.setTypeId(0);
		final Integer id = this.germplasmDataManager.addGermplasmAttribute(attribute);
		Debug.println(IntegrationTestBase.INDENT, "testAddGermplasmAttribute(" + gid + "): " + id + " = " + attribute);
	}

	@Test
	public void testUpdateGermplasmAttribute() throws Exception {
		final Integer attributeId = 1; // Assumption: attribute with id = 1 exists

		final Attribute attribute = this.germplasmDataManager.getAttributeById(attributeId);

		if (attribute != null) {
			final String attributeString = attribute.toString();
			attribute.setAdate(0);
			attribute.setLocationId(0);
			attribute.setUserId(0);
			attribute.setReferenceId(0);
			attribute.setTypeId(0);
			this.germplasmDataManager.updateGermplasmAttribute(attribute);

			Debug.println(IntegrationTestBase.INDENT, "testUpdateGermplasmAttribute(" + attributeId + "): " + "\ntBEFORE: "
					+ attributeString + "\ntAFTER: " + attribute);
		}
	}

	@Test
	public void testGetUserDefinedFieldByFieldTable() throws MiddlewareQueryException {
		final String tableName = "ATRIBUTS";
		final String fieldType = "ATTRIBUTE";
		final List<UserDefinedField> userDefineField =
				this.germplasmDataManager.getUserDefinedFieldByFieldTableNameAndType(tableName, fieldType);
		Debug.println(IntegrationTestBase.INDENT,
				"testGetUserDefineFieldByTableNameAndType(type=" + tableName + "): " + userDefineField.size());
		for (final UserDefinedField u : userDefineField) {
			Debug.println(IntegrationTestBase.INDENT, u);
		}
	}

	@Test
	public void testGetPlotCodeField() {
		final UserDefinedField plotCodeField = this.germplasmDataManager.getPlotCodeField();
		// Should never return null no matter whether the plot code UDFLD is present in the target database or not.
		Assert.assertNotNull("GermplasmDataManager.getPlotCodeField() should never return null.", plotCodeField);

		if (plotCodeField.getFldno() != 0) {
			// Non-zero fldno is a case where the UDFLD table has a record matching ftable=ATRIBUTS, ftype=PASSPORT, fcode=PLOTCODE
			// Usually the id of this record is 1552. Not asserting as we dont want tests to depend on primary key values to be exact.
			Assert.assertEquals("ATRIBUTS", plotCodeField.getFtable());
			Assert.assertEquals("PASSPORT", plotCodeField.getFtype());
			Assert.assertEquals("PLOTCODE", plotCodeField.getFcode());
		}
	}

	@Test
	public void testGetPlotCodeValue() {
		final GermplasmDataManagerImpl unitToTest = new GermplasmDataManagerImpl();

		// We want to mock away calls to other methods in same unit.
		final GermplasmDataManagerImpl partiallyMockedUnit = Mockito.spy(unitToTest);
		final Integer testGid = 1;

		// First set up data such that no plot code attribute is associated.
		Mockito.doReturn(null).when(partiallyMockedUnit).getPlotCodeField();
		final List<Attribute> attributes = new ArrayList<Attribute>();
		Mockito.doReturn(attributes).when(partiallyMockedUnit).getAttributesByGID(Matchers.anyInt());

		final String plotCode1 = partiallyMockedUnit.getPlotCodeValue(testGid);
		Assert.assertNotNull("getPlotCodeValue() should never return null.", plotCode1);
		Assert.assertEquals("Expected `Unknown` returned when there is no plot code attribute present.", "Unknown", plotCode1);

		// Now setup data so that gid has plot code attribute associated with it.
		final UserDefinedField udfld = new UserDefinedField();
		udfld.setFldno(1152);
		udfld.setFtable("ATRIBUTS");
		udfld.setFtype("PASSPORT");
		udfld.setFcode("PLOTCODE");

		Mockito.when(partiallyMockedUnit.getPlotCodeField()).thenReturn(udfld);
		final Attribute plotCodeAttr = new Attribute();
		plotCodeAttr.setTypeId(udfld.getFldno());
		plotCodeAttr.setAval("The PlotCode Value");
		attributes.add(plotCodeAttr);
		Mockito.when(partiallyMockedUnit.getAttributesByGID(testGid)).thenReturn(attributes);

		final String plotCode2 = partiallyMockedUnit.getPlotCodeValue(testGid);
		Assert.assertNotNull("getPlotCodeValue() should never return null.", plotCode2);
		Assert.assertEquals("Expected value of plot code attribute returned when plot code attribute is present.", plotCodeAttr.getAval(),
				plotCode2);
	}

	@Test
	public void testGetNextSequenceNumberForCrossName() throws MiddlewareQueryException {
		final String prefix = "IR";
		Debug.println("Next number in sequence for prefix (" + prefix + ") is : "
				+ this.germplasmDataManager.getNextSequenceNumberForCrossName(prefix));
	}

	@Test
	public void testGetPreferredIdsByGIDs() throws MiddlewareQueryException {
		final List<Integer> gids = Arrays.asList(1, 2, 3, 4, 5);
		final Map<Integer, String> results = this.germplasmDataManager.getPrefferedIdsByGIDs(gids);
		for (final Integer gid : results.keySet()) {
			Debug.println(IntegrationTestBase.INDENT, gid + " : " + results.get(gid));
		}
	}

	@Test
	public void testGetAllMethods() throws Exception {
		final List<Method> results = this.germplasmDataManager.getAllMethods();
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testGetAllMethodsOrderByMname() throws Exception {
		final List<Method> results = this.germplasmDataManager.getAllMethodsOrderByMname();
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testGetAllMethodsNotGenerative() throws Exception {
		final List<Method> results = this.germplasmDataManager.getAllMethodsNotGenerative();
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testGetAllGermplasm() throws Exception {
		final List<Germplasm> germplasms = this.germplasmDataManager.getAllGermplasm(0, 100);
		Debug.printObjects(IntegrationTestBase.INDENT, germplasms);
	}

	@Test
	public void testGetBibliographicalReferenceById() throws Exception {
		final Integer id = 1;
		final Bibref bibref = this.germplasmDataManager.getBibliographicReferenceByID(id);
		Debug.println(IntegrationTestBase.INDENT, "testGetBibliographicalReferenceById(" + id + "): " + bibref);
	}

	@Test
	public void testGetMethodByID() throws Exception {
		final Integer id = 4;
		final Method methodid = this.germplasmDataManager.getMethodByID(id);
		Assert.assertNotNull(methodid);
		Debug.println(IntegrationTestBase.INDENT, "testGetMethodByID(" + id + "): ");
		Debug.println(IntegrationTestBase.INDENT, methodid);
	}

	@Test
	public void testGetUserDefinedFieldByID() throws Exception {
		final Integer id = 1;
		final UserDefinedField result = this.germplasmDataManager.getUserDefinedFieldByID(id);
		Assert.assertNotNull(result);
		Debug.println(IntegrationTestBase.INDENT, result);
	}

	@Test
	public void testGetBibliographicReferenceByID() throws Exception {
		final Bibref bibref = new Bibref();
		final PodamFactory factory = new PodamFactoryImpl();
		factory.populatePojo(bibref, Bibref.class);
		// Let hibernate generate id.
		bibref.setRefid(null);
		this.germplasmDataManager.addBibliographicReference(bibref);

		final Bibref result = this.germplasmDataManager.getBibliographicReferenceByID(bibref.getRefid());
		Debug.println(IntegrationTestBase.INDENT, result);
	}

	@Test
	public void testGetGermplasmByLocationId() throws Exception {
		final String name = "RCH";
		final int locationID = 0;

		final List<Germplasm> germplasmList = this.germplasmDataManager.getGermplasmByLocationId(name, locationID);
		Assert.assertTrue(germplasmList != null);

		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmByLocationId(" + name + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, germplasmList);
	}

	@Test
	public void testGetGermplasmByGidRange() throws Exception {
		final int startGID = 1;
		final int endGID = 5;

		final List<Germplasm> germplasmList = this.germplasmDataManager.getGermplasmByGidRange(startGID, endGID);
		Assert.assertTrue(germplasmList != null);

		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmByGidRange(" + startGID + "," + endGID + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, germplasmList);
	}

	@Test
	public void testGetGermplasmByGIDList() throws Exception {
		final List<Integer> gids = Arrays.asList(1, 2, 3, 4, 5);

		final List<Germplasm> germplasmList = this.germplasmDataManager.getGermplasms(gids);
		Assert.assertTrue(germplasmList != null);

		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmByGidList(" + gids + "): ");
		for (final Germplasm g : germplasmList) {
			Debug.println(IntegrationTestBase.INDENT, g);
		}
	}

	@Test
	public void testGetPreferredNamesByGIDs() throws MiddlewareQueryException {
		final List<Integer> gids = Arrays.asList(1, 2, 3, 4, 5);
		final Map<Integer, String> results = this.germplasmDataManager.getPreferredNamesByGids(gids);
		for (final Integer gid : results.keySet()) {
			Debug.println(IntegrationTestBase.INDENT, gid + " : " + results.get(gid));
		}
	}

	@Test
	public void testGetLocationNamesByGIDs() throws MiddlewareQueryException {
		final List<Integer> gids = Arrays.asList(1, 2, 3, 4, 5);
		final Map<Integer, String> results = this.germplasmDataManager.getLocationNamesByGids(gids);
		for (final Integer gid : results.keySet()) {
			Debug.println(IntegrationTestBase.INDENT, gid + " : " + results.get(gid));
		}
	}

	@Test
	public void testSearchGermplasm() throws MiddlewareQueryException {
		final String q = "CML";
		final GermplasmSearchParameter searchParameter = new GermplasmSearchParameter(q, Operation.LIKE);
		searchParameter.setIncludeParents(true);
		searchParameter.setWithInventoryOnly(false);
		searchParameter.setIncludeMGMembers(false);
		searchParameter.setStartingRow(0);
		searchParameter.setNumberOfEntries(25);

		final List<Germplasm> results = this.germplasmDataManager.searchForGermplasm(searchParameter);
		Debug.println(IntegrationTestBase.INDENT, "searchForGermplasm(" + q + "): " + results.size() + " matches found.");
	}

	@Test
	public void getGermplasmDatesByGids() throws MiddlewareQueryException {
		final List<Integer> gids = Arrays.asList(1, 2, 3, 4, 5);
		final Map<Integer, Integer> results = this.germplasmDataManager.getGermplasmDatesByGids(gids);
		Debug.println(IntegrationTestBase.INDENT, "getGermplasmDatesByGids(" + gids + "): ");
		Debug.println(IntegrationTestBase.INDENT, results.toString());
	}

	@Test
	public void getMethodsByGids() throws MiddlewareQueryException {
		final List<Integer> gids = Arrays.asList(1, 2, 3, 4, 5);
		final Map<Integer, Object> results = this.germplasmDataManager.getMethodsByGids(gids);
		Debug.println(IntegrationTestBase.INDENT, "getGermplasmDatesByGids(" + gids + "): ");
		Debug.println(IntegrationTestBase.INDENT, results.toString());
	}

	@Test
	public void getAttributeTypesByGIDList() throws MiddlewareQueryException {
		final List<Integer> gids = Arrays.asList(1, 2, 3, 4, 5);
		final List<UserDefinedField> results = this.germplasmDataManager.getAttributeTypesByGIDList(gids);
		Debug.println(IntegrationTestBase.INDENT, "getAttributeTypesByGIDList(" + gids + "): ");
		for (final UserDefinedField field : results) {
			Debug.println(IntegrationTestBase.INDENT, field.getFname());
		}
	}

	@Test
	public void getAttributeValuesByTypeAndGIDList() throws MiddlewareQueryException {
		final List<Integer> gids = Arrays.asList(1, 2, 3, 4, 5);
		final Integer attributeType = 1115;
		final Map<Integer, String> results = this.germplasmDataManager.getAttributeValuesByTypeAndGIDList(attributeType, gids);
		Debug.println(IntegrationTestBase.INDENT, "getAttributeValuesByTypeAndGIDList(" + attributeType + ", " + gids + "): ");
		Debug.println(IntegrationTestBase.INDENT, results.toString());
	}

	@Test
	public void getMethodClasses() throws MiddlewareQueryException {
		final List<Term> terms = this.germplasmDataManager.getMethodClasses();
		System.out.println(terms);
	}

	@Test
	public void testGetMethodByName() throws Exception {
		final String name = "breeders seed";
		final Method method = this.germplasmDataManager.getMethodByName(name);
		Assert.assertNotNull(method);
		Debug.println(IntegrationTestBase.INDENT, "testGetMethodByName(" + name + "): ");
		Debug.println(IntegrationTestBase.INDENT, method);
	}

	@Test
	public void testGetMethodByNameWithProgramUUID() throws Exception {
		final String name = "breeders seed";
		final String programUUID = this.commonTestProject.getUniqueID();
		final Method method = this.germplasmDataManager.getMethodByName(name, programUUID);
		Assert.assertNotNull("Expecting the return method is not null.", method);
	}

	@Test
	public void testGetMethodByCode() throws Exception {
		final String code = "VBS";
		final Method method = this.germplasmDataManager.getMethodByCode(code);
		Assert.assertNotNull(method);
		Debug.println(IntegrationTestBase.INDENT, "testGetMethodByCode(" + code + "): ");
		Debug.println(IntegrationTestBase.INDENT, method);
	}

	@Test
	public void testGetMethodByCodeWithProgramUUID() throws Exception {
		final String code = "VBS";
		final String programUUID = this.commonTestProject.getUniqueID();
		final Method method = this.germplasmDataManager.getMethodByCode(code, programUUID);
		Assert.assertNotNull("Expecting the return method is not null.", method);
	}

	@Test
	public void testAddUserDefinedField() throws Exception {
		final UserDefinedField field = new UserDefinedField();
		field.setFtable("ATRIBUTS");
		field.setFtype("ATTRIBUTE");
		field.setFcode("MATURITY");
		field.setFname("Maturity class");
		field.setFfmt("MCLASS,ASSIGNED,C");
		field.setFdesc("-");
		field.setLfldno(0);

		field.setFuid(1);
		field.setFdate(20041116);
		field.setScaleid(0);

		final Integer success = this.germplasmDataManager.addUserDefinedField(field);
		Assert.assertTrue(success > 0);

		Debug.println(IntegrationTestBase.INDENT, "testAddUserDefinedField(" + field + "): ");
	}

	@Test
	public void testAddAttribute() throws Exception {
		final Attribute attr = new Attribute();
		attr.setGermplasmId(237431);
		attr.setTypeId(1);
		attr.setUserId(1);
		attr.setAval("EARLY");
		attr.setLocationId(31);
		attr.setReferenceId(0);
		attr.setAdate(20041116);

		this.germplasmDataManager.addAttribute(attr);
		Assert.assertTrue(attr.getAid() != null);

		Debug.println(IntegrationTestBase.INDENT, "testAddAttribute(" + attr + "): ");

		final Attribute readAttribute = this.germplasmDataManager.getAttributeById(attr.getAid());
		Assert.assertNotNull(readAttribute);
		Debug.println(IntegrationTestBase.INDENT, "testGetAttributeById(" + attr.getAid() + ") Results:");
		Debug.println(IntegrationTestBase.INDENT, readAttribute);
	}

	@Test
	public void getProgramMethodsAndDeleteByUniqueId() {
		// create program locations
		final String programUUID = this.commonTestProject.getUniqueID();
		final Method testMethod1 = this.createMethodTestData(programUUID);
		final Method testMethod2 = this.createMethodTestData(programUUID);
		try {
			this.germplasmDataManager.addMethod(testMethod1);
			this.germplasmDataManager.addMethod(testMethod2);
			// verify
			List<Method> methodList = this.germplasmDataManager.getProgramMethods(programUUID);
			Assert.assertEquals("There should be 2 program methods with programUUID[" + programUUID + "]", 2, methodList.size());
			// delete locations
			this.germplasmDataManager.deleteProgramMethodsByUniqueId(programUUID);
			methodList = this.germplasmDataManager.getProgramMethods(programUUID);
			Assert.assertTrue("There should be no program methods with programUUID[" + programUUID + "]", methodList.isEmpty());
		} catch (final MiddlewareQueryException e) {
			Assert.fail("Getting and deleting of program methods failed ");
		}
	}

	private Method createMethodTestData(final String programUUID) {
		final Method method = new Method();
		method.setUniqueID(programUUID);
		method.setMname("TEST-LOCATION" + System.currentTimeMillis());
		method.setMdesc("TEST-LOCATION-DESC" + System.currentTimeMillis());
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
		final Integer gid = 1;
		this.germplasmDataManager.getGermplasmWithMethodType(gid);
	}

	@Test
	public void shouldGetMapForGermplasmCount() throws Exception {

		final Germplasm germplasm1 = new Germplasm();
		germplasm1.setGid(-1);
		germplasm1.setMethodId(31);
		germplasm1.setGnpgs(-1);
		germplasm1.setGrplce(-1);
		germplasm1.setGpid1(0);
		germplasm1.setGpid2(0);
		germplasm1.setUserId(1);
		germplasm1.setLgid(0);
		germplasm1.setLocationId(0);
		germplasm1.setGdate(20151102);
		germplasm1.setReferenceId(0);

		this.germplasmDAO.save(germplasm1);

		final Germplasm germplasm2 = new Germplasm();
		germplasm2.setGid(-2);
		germplasm2.setMethodId(31);
		germplasm2.setGnpgs(-1);
		germplasm2.setGrplce(0);
		germplasm2.setGpid1(0);
		germplasm2.setGpid2(0);
		germplasm2.setUserId(1);
		germplasm2.setLgid(0);
		germplasm2.setLocationId(0);
		germplasm2.setGdate(20151103);
		germplasm2.setReferenceId(0);

		this.germplasmDAO.save(germplasm2);

		final Integer gid1 = germplasm1.getGid();
		final Integer gid2 = germplasm2.getGid();

		final Name name1 = this.nameTDI.createName(GermplasmNameType.LINE_NAME.getUserDefinedFieldID(), gid1, "I-1RT  /  P 001 A-23 / ");
		this.nameDAO.save(name1);

		final Name name2 = this.nameTDI.createName(GermplasmNameType.LINE_NAME.getUserDefinedFieldID(), gid2, "I-1RT/P 1 A-23/");
		this.nameDAO.save(name2);

		final Name name3 = this.nameTDI.createName(GermplasmNameType.LINE_NAME.getUserDefinedFieldID(), gid2, "I-1RT/P001A-23/");
		this.nameDAO.save(name3);

		final Name name4 = this.nameTDI.createName(GermplasmNameType.LINE_NAME.getUserDefinedFieldID(), gid2, "(CML454 X CML451)-B-3-1-112");
		this.nameDAO.save(name4);

		final Name name5 = this.nameTDI.createName(GermplasmNameType.LINE_NAME.getUserDefinedFieldID(), gid1, "(CML454 X CML451)-B-3-1-112");
		this.nameDAO.save(name5);

		final Name name6 = this.nameTDI.createName(GermplasmNameType.LINE_NAME.getUserDefinedFieldID(), gid1, "(CML454XCML451)-B-3-1-112");
		this.nameDAO.save(name6);

		final List<String> names = new ArrayList<>(Arrays.asList("I-1RT  /  P 001 A-23 / ", "(CML454 X CML451)-B-3-1-112"));

		final Map<String, Integer> mapCountByNamePermutations = this.germplasmDataManager.getCountByNamePermutations(names);

		Assert.assertEquals(2, mapCountByNamePermutations.size());
		Assert.assertEquals((Integer) 2, mapCountByNamePermutations.get("I-1RT  /  P 001 A-23 / "));
		Assert.assertEquals((Integer) 1, mapCountByNamePermutations.get("(CML454 X CML451)-B-3-1-112"));
	}

	/**
	 * test to verify germplasm permutations should be processed for large amount of data.
	 *
	 * @throws Exception
	 */
	@Test
	public void testShouldLoadAndProcessLargeGermplasmNamePermutations() throws Exception{
		final String fileLocation = GermplasmDataManagerIntegrationTest.class.getClassLoader().getResource("germplasm_designation_name_list.txt").getFile();
		final List<String> nameList = FileUtils.readLines(new File(fileLocation));

		final Map<String, Integer> mapCountByNamePermutations = this.germplasmDataManager.getCountByNamePermutations(nameList);

		Assert.assertTrue(mapCountByNamePermutations.size() > 0);
	}
	
	@Test
	public void testGetNamesByGidsAndNTypeIdsInMap(){
		int GID = 1;
		
		Map<Integer, List<Name>> namesMap = this.germplasmDataManager.getNamesByGidsAndNTypeIdsInMap(Arrays.asList(GID), Arrays.asList(GermplasmNameType.DERIVATIVE_NAME.getUserDefinedFieldID()));
		int sizeBeforeAddingNewName = namesMap.get(GID) != null? namesMap.get(GID).size():0;
		
		Name name = this.nameTDI.createName(GermplasmNameType.DERIVATIVE_NAME.getUserDefinedFieldID(), GID, "DERIVATIVE NAME 00001");
		name = this.nameDAO.save(name);
		Map<Integer, List<Name>> newNamesMap = this.germplasmDataManager.getNamesByGidsAndNTypeIdsInMap(Arrays.asList(GID), Arrays.asList(GermplasmNameType.DERIVATIVE_NAME.getUserDefinedFieldID()));
		int sizeAfterAddingNewName = newNamesMap.get(GID).size();
		
		//Assert that the new size has 1 more name, which is the newly added name
		Assert.assertEquals("Both sizes should be equal.", sizeBeforeAddingNewName + 1, sizeAfterAddingNewName);
	}
}
