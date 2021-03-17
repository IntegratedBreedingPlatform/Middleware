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

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.generationcp.middleware.GermplasmTestDataGenerator;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.KeySequenceRegisterDAO;
import org.generationcp.middleware.dao.MethodDAO;
import org.generationcp.middleware.dao.NameDAO;
import org.generationcp.middleware.dao.ProgenitorDAO;
import org.generationcp.middleware.dao.UserDefinedFieldDAO;
import org.generationcp.middleware.dao.ims.LotDAO;
import org.generationcp.middleware.dao.ims.TransactionDAO;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.data.initializer.InventoryDetailsTestDataInitializer;
import org.generationcp.middleware.data.initializer.NameTestDataInitializer;
import org.generationcp.middleware.data.initializer.ProgramFavoriteTestDataInitializer;
import org.generationcp.middleware.data.initializer.UserDefinedFieldTestDataInitializer;
import org.generationcp.middleware.domain.gms.search.GermplasmSearchParameter;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Bibref;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmNameDetails;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Progenitor;
import org.generationcp.middleware.pojos.UDTableType;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.pojos.workbench.CropType;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertNotNull;

public class GermplasmDataManagerIntegrationTest extends IntegrationTestBase {

	private static final Integer CREATED_BY = new Random().nextInt();

	public static final String separator = "-";
	private static final String parent1Name = "CML502";
	private static final String parent2Name = "CLQRCWQ109";
	private static final String parent3Name = "CLQRCWQ55";

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Autowired
	private LocationDataManager locationManager;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;

	private NameDAO nameDAO;

	private GermplasmDAO germplasmDAO;

	private MethodDAO methodDAO;

	private LotDAO lotDAO;

	private TransactionDAO transactionDAO;

	private ProgenitorDAO progenitorDAO;

	private Project commonTestProject;

	private GermplasmTestDataGenerator germplasmTestDataGenerator;

	private ProgramFavoriteTestDataInitializer programFavoriteTestDataInitializer;

	private UserDefinedFieldDAO userDefinedFieldDAO;

	private KeySequenceRegisterDAO keySequenceRegisterDAO;

	private CropType cropType;

	@Before
	public void setUp() {
		this.programFavoriteTestDataInitializer = new ProgramFavoriteTestDataInitializer();

		if (this.nameDAO == null) {
			this.nameDAO = new NameDAO();
			this.nameDAO.setSession(this.sessionProvder.getSession());
		}

		if (this.germplasmDAO == null) {
			this.germplasmDAO = new GermplasmDAO();
			this.germplasmDAO.setSession(this.sessionProvder.getSession());
		}

		if (this.methodDAO == null) {
			this.methodDAO = new MethodDAO();
			this.methodDAO.setSession(this.sessionProvder.getSession());
		}


		if (this.germplasmTestDataGenerator == null) {
			this.germplasmTestDataGenerator = new GermplasmTestDataGenerator(this.germplasmDataManager);
		}

		if (this.commonTestProject == null) {
			this.commonTestProject = this.workbenchTestDataUtil.getCommonTestProject();
		}

		if (this.lotDAO == null) {
			this.lotDAO = new LotDAO();
			this.lotDAO.setSession(this.sessionProvder.getSession());
		}

		if (this.transactionDAO == null) {
			this.transactionDAO = new TransactionDAO();
			this.transactionDAO.setSession(this.sessionProvder.getSession());
		}

		if (this.userDefinedFieldDAO == null) {
			this.userDefinedFieldDAO = new UserDefinedFieldDAO();
			this.userDefinedFieldDAO.setSession(this.sessionProvder.getSession());
		}

		if (this.progenitorDAO == null) {
			this.progenitorDAO = new ProgenitorDAO();
			this.progenitorDAO.setSession(this.sessionProvder.getSession());
		}

		if (this.keySequenceRegisterDAO == null) {
			this.keySequenceRegisterDAO = new KeySequenceRegisterDAO();
			this.keySequenceRegisterDAO.setSession(this.sessionProvder.getSession());
		}

		this.cropType = new CropType();
		this.cropType.setUseUUID(false);
	}

	@Test
	public void testGetMethodsByIDs() {

		// Attempt to get all locations so we can proceed
		final List<Method> locationList = this.germplasmDataManager.getAllMethods();
		assertThat(locationList, is(notNullValue()));
		assertThat("we cannot proceed test if size < 0",locationList.size(), is(greaterThan(0)));

		final List<Integer> ids = new ArrayList<>();

		for (final Method ls : locationList) {
			ids.add(ls.getMid());

			// only get subset of locations
			if (ids.size() < 5) {
				break;
			}
		}

		final List<Method> results = this.germplasmDataManager.getMethodsByIDs(ids);
		assertThat(results, is(notNullValue()));
		assertThat(results.size(), is(lessThan(5)));

		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testGetGermplasmByName() {
		final String name = "CML502RLT";

		final List<Germplasm> germplasmList = this.germplasmDataManager.getGermplasmByName(name, 0, 5, Operation.EQUAL);
		assertThat(germplasmList, is(notNullValue()));

		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmByName(" + name + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, germplasmList);
	}

	@Test
	public void testCountGermplasmByName() {
		final String name = "(CML454 X CML451)-B-3-1-1";
		final long count = this.germplasmDataManager.countGermplasmByName(name, Operation.EQUAL);
		Debug.println(IntegrationTestBase.INDENT, "testCountGermplasmByName(" + name + "): " + count);
	}

	@Test
	public void testGetGermplasmByNameUsingLike() {
		final String name = "IR%";

		final List<Germplasm> germplasmList = this.germplasmDataManager.getGermplasmByName(name, 0, 5, Operation.LIKE);
		assertThat(germplasmList, is(notNullValue()));

		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmByNameUsingLike(" + name + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, germplasmList);
	}

	@Test
	public void testCountGermplasmByNameUsingLike() {
		final String name = "IR%";

		final long count = this.germplasmDataManager.countGermplasmByName(name, Operation.LIKE);
		Debug.println(IntegrationTestBase.INDENT, "testCountGermplasmByNameUsingLike(" + name + ") RESULTS:" + count);
	}

	@Test
	public void testGetGermplasmByLocationNameUsingEqual() {
		final String name = "Philippines";
		final List<Germplasm> germplasmList = this.germplasmDataManager.getGermplasmByLocationName(name, 0, 5, Operation.EQUAL);
		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmByLocationNameUsingEqual(" + name + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, germplasmList);
	}

	@Test
	public void testCountGermplasmByLocationNameUsingEqual() {
		final String name = "Philippines";
		final long count = this.germplasmDataManager.countGermplasmByLocationName(name, Operation.EQUAL);
		Debug.println(IntegrationTestBase.INDENT, "testCountGermplasmByLocationNameUsingEqual(" + name + "): " + count);
	}

	@Test
	public void testGetGermplasmByLocationNameUsingLike() {
		final String name = "International%";
		final List<Germplasm> germplasmList = this.germplasmDataManager.getGermplasmByLocationName(name, 0, 5, Operation.LIKE);
		assertThat(germplasmList, is(notNullValue()));

		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmByLocationNameUsingLike(" + name + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, germplasmList);
	}

	@Test
	public void testCountGermplasmByLocationNameUsingLike() {
		final String name = "International%";
		final long count = this.germplasmDataManager.countGermplasmByLocationName(name, Operation.LIKE);
		Debug.println(IntegrationTestBase.INDENT, "testCountGermplasmByLocationNameUsingLike(" + name + "): " + count);
	}

	@Test
	public void testGetGermplasmByMethodNameUsingEqual() {
		final String name = "SINGLE CROSS";

		final List<Germplasm> germplasmList = this.germplasmDataManager.getGermplasmByMethodName(name, 0, 5, Operation.EQUAL);
		assertThat(germplasmList, is(notNullValue()));

		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmByMethodNameUsingEqual(" + name + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, germplasmList);
	}

	@Test
	public void testCountGermplasmByMethodNameUsingEqual() {
		final String name = "SINGLE CROSS";
		final long count = this.germplasmDataManager.countGermplasmByMethodName(name, Operation.EQUAL);
		Debug.println(IntegrationTestBase.INDENT, "testCountGermplasmByMethodNameUsingEqual(" + name + "): " + count);
	}

	@Test
	public void testGetGermplasmByMethodNameUsingLike() {
		final String name = "%CROSS%";

		final List<Germplasm> germplasmList = this.germplasmDataManager.getGermplasmByMethodName(name, 0, 5, Operation.LIKE);
		assertThat(germplasmList, is(notNullValue()));

		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmByMethodNameUsingLike(" + name + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, germplasmList);
	}

	@Test
	public void testCountGermplasmByMethodNameUsingLike() {
		final String name = "%CROSS%";
		final long count = this.germplasmDataManager.countGermplasmByMethodName(name, Operation.LIKE);
		Debug.println(IntegrationTestBase.INDENT, "testCountGermplasmByMethodNameUsingLike(" + name + "): " + count);
	}

	@Test
	public void testGetGermplasmByGID() {
		final int gid = 50533;
		final Germplasm germplasm = this.germplasmDataManager.getGermplasmByGID(gid);
		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmByGID(" + gid + "): " + germplasm);
	}

	@Test
	public void testGetGermplasmWithPrefName() {
		final int gid = 50533;
		final Germplasm germplasm = this.germplasmDataManager.getGermplasmWithPrefName(gid);

		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmWithPrefName(" + gid + "): " + germplasm);
		if (germplasm != null) {
			Debug.println("  preferredName = " + germplasm.getPreferredName());
		}
	}

	@Test
	public void testGetGermplasmWithPrefAbbrev() {
		final int gid = 151;
		final Germplasm germplasm = this.germplasmDataManager.getGermplasmWithPrefAbbrev(gid);
		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmWithPrefAbbrev(" + gid + "): " + germplasm);
	}

	@Test
	public void testGetGermplasmNameByID() {
		final int gid = 42268;
		final Name name = this.germplasmDataManager.getGermplasmNameByID(gid);
		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmNameByID(" + gid + "): " + name);
	}

	@Test
	public void testGetNamesByGID() {
		final int gid = 2434138;
		final List<Name> names = this.germplasmDataManager.getNamesByGID(gid, null, null);
		Debug.println(IntegrationTestBase.INDENT, "testGetNamesByGID(" + gid + "): " + names.size());
		Debug.printObjects(names);
	}

	@Test
	public void testGetMethodCodeByMethodIds() {
		final Set<Integer> methodIds = new HashSet<Integer>(Arrays.asList(1));
		final List<String> methodCodes = this.germplasmDataManager.getMethodCodeByMethodIds(methodIds);
		assertThat("The method code should be UGM", "UGM", is(equalTo(methodCodes.get(0))));
	}

	@Test
	public void testGetPreferredNameByGID() {
		final Integer gid = 1;
		Debug.println(IntegrationTestBase.INDENT,
			"testGetPreferredNameByGID(" + gid + "): " + this.germplasmDataManager.getPreferredNameByGID(gid));
	}

	@Test
	public void testGetPreferredNameValueByGID() {
		final Integer gid = 1;
		Debug.println(IntegrationTestBase.INDENT,
			"testGetPreferredNameValueByGID(" + gid + "): " + this.germplasmDataManager.getPreferredNameValueByGID(gid));
	}

	@Test
	public void testGetPreferredAbbrevByGID() {
		final Integer gid = 1;
		Debug.println(IntegrationTestBase.INDENT,
			"testGetPreferredAbbrevByGID(" + gid + "): " + this.germplasmDataManager.getPreferredAbbrevByGID(gid));
	}

	@Test
	public void testGetPreferredIdByGID() {
		final Integer gid = 986634;
		Debug.println(IntegrationTestBase.INDENT,
			"testGetPreferredIdByGID(" + gid + "): " + this.germplasmDataManager.getPreferredIdByGID(gid));
	}

	@Test
	public void testGetPreferredIdsByListId() {
		final Integer listId = 2591;
		Debug.println(IntegrationTestBase.INDENT,
			"testGetPreferredIdsByListId(" + listId + "): " + this.germplasmDataManager.getPreferredIdsByListId(listId));
	}

	@Test
	public void testGetNameByGIDAndNval() {
		final Integer gid = 225266;
		final String nVal = "C 65-44";
		Debug.println(IntegrationTestBase.INDENT,
			"testGetNameByGIDAndNval(" + gid + ", " + nVal + ", GetGermplasmByNameModes.NORMAL) : " + this.germplasmDataManager
				.getNameByGIDAndNval(gid, nVal, GetGermplasmByNameModes.NORMAL));
		Debug.println(IntegrationTestBase.INDENT,
			"testGetNameByGIDAndNval(" + gid + ", " + nVal + ", GetGermplasmByNameModes.SPACES_REMOVED) : " + this.germplasmDataManager
				.getNameByGIDAndNval(gid, nVal, GetGermplasmByNameModes.SPACES_REMOVED));
		Debug.println(IntegrationTestBase.INDENT,
			"testGetNameByGIDAndNval(" + gid + ", " + nVal + ", GetGermplasmByNameModes.STANDARDIZED) : " + this.germplasmDataManager
				.getNameByGIDAndNval(gid, nVal, GetGermplasmByNameModes.STANDARDIZED));
	}

	@Test
	public void testGetNamesByGIDWithStatus() {
		final int gid = 50533;
		final int status = 1;
		final GermplasmNameType type = null;
		final List<Name> names = this.germplasmDataManager.getNamesByGID(gid, status, type);
		Debug.println(IntegrationTestBase.INDENT,
			"testGetNamesByGIDWithStatus(gid=" + gid + ", status" + status + ", type=" + type + "): " + names);
	}

	@Test
	public void testGetNamesByGIDWithStatusAndType() {
		final int gid = 50533;
		final int status = 8;
		final GermplasmNameType type = GermplasmNameType.INTERNATIONAL_TESTING_NUMBER;
		final List<Name> names = this.germplasmDataManager.getNamesByGID(gid, status, type);
		Debug.println(IntegrationTestBase.INDENT,
			"testGetNamesByGIDWithStatusAndType(gid=" + gid + ", status" + status + ", type=" + type + "): " + names);
	}

	@Test
	public void testGetAttributesByGID() {
		final int gid = 50533;
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
		methods.add(
			new Method(null, "GEN", "S", "UGM", "yesno", "description 1", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0),
				Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(2), Integer.valueOf(19980610), programUUID));
		methods.add(
			new Method(null, "GEN", "S", "UGM", "yesno", "description 2", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0),
				Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(2), Integer.valueOf(19980610), programUUID));
		methods.add(
			new Method(null, "GEN", "S", "UGM", "yesno", "description 3", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0),
				Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(2), Integer.valueOf(19980610), programUUID));

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
		assertThat("Expecting to have returned results.", methodsFilteredByProgramUUID, is(notNullValue()));
		Debug.println(IntegrationTestBase.INDENT,
			"testGetMethodsByUniqueID(programUUID=" + programUUID + "): " + methodsFilteredByProgramUUID.size());
		Debug.printObjects(IntegrationTestBase.INDENT * 2, methodsFilteredByProgramUUID);
	}

	@Test
	public void testGetMethodsByType() throws MiddlewareQueryException {
		final String type = "GEN"; // Tested with rice and cowpea
		final int start = 0;
		final int numOfRows = 5;

		final List<Method> methods = this.germplasmDataManager.getMethodsByType(type);
		assertThat("Expecting to have returned results.", methods, is(notNullValue()));
		Debug.println(IntegrationTestBase.INDENT, "testGetMethodsByType(type=" + type + "): " + methods.size());
		Debug.printObjects(IntegrationTestBase.INDENT * 2, methods);

		final String programUUID = "030850c4-41f8-4baf-81a3-03b99669e996";
		final List<Method> methodsFilteredByProgramUUID = this.germplasmDataManager.getMethodsByType(type, programUUID);
		assertThat("Expecting to have returned results.", methodsFilteredByProgramUUID, is(notNullValue()));

		Debug.println(IntegrationTestBase.INDENT,
			"testGetMethodsByType(type=" + type + ", programUUID=" + programUUID + "): " + methodsFilteredByProgramUUID.size());
		Debug.printObjects(IntegrationTestBase.INDENT * 2, methodsFilteredByProgramUUID);

		final List<Method> methodList = this.germplasmDataManager.getMethodsByType(type, start, numOfRows);
		assertThat("Expecting to have returned results.", methodList, is(notNullValue()));

		Debug.println(IntegrationTestBase.INDENT,
			"testGetMethodsByType(type=" + type + ", start=" + start + ", numOfRows=" + numOfRows + "): " + methodList.size());
		Debug.printObjects(IntegrationTestBase.INDENT * 2, methodList);
	}

	@Test
	public void testCountMethodsByUniqueID() {
		final String programUUID = "030850c4-41f8-4baf-81a3-03b99669e996";
		final long count = this.germplasmDataManager.countMethodsByUniqueID(programUUID);
		assertThat("Expecting to have returned results.", count, is(greaterThan(0l)));
		Debug.println(IntegrationTestBase.INDENT, "testCountMethodsByUniqueID(programUUID=" + programUUID + "): " + count);
	}

	@Test
	public void testCountMethodsByType() {
		String type = "GEN";
		final long count = this.germplasmDataManager.countMethodsByType(type);
		assertThat("Expecting to have returned results.", count, is(greaterThan(0l)));

		Debug.println(IntegrationTestBase.INDENT, "testCountMethodsByType(type=" + type + "): " + count);

		type = "GEN";
		final String programUUID = "030850c4-41f8-4baf-81a3-03b99669e996";
		final long countWithProgramUUID = this.germplasmDataManager.countMethodsByType(type, programUUID);
		assertThat("Expecting to have returned results.", count, is(greaterThan(0l)));

		Debug.println(IntegrationTestBase.INDENT, "testCountMethodsByType(type=" + type + "): " + countWithProgramUUID);

		assertThat("The results that is filtered by programUUID must be less than or equal to the results without programUUID.", count,
			is(greaterThanOrEqualTo(countWithProgramUUID)));

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
		Debug.println(IntegrationTestBase.INDENT,
			"testGetMethodsByGroup(group=" + group + ", start=" + start + ", numOfRows=" + numOfRows + "): " + methodList.size());
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
		Debug.println(IntegrationTestBase.INDENT,
			"testGetMethodsByGroupAndTypeAndName(group=" + group + " and type=" + type + " and name=" + name + "): " + methods.size());
		Debug.printObjects(IntegrationTestBase.INDENT, methods);
	}

	@Test
	public void testCountMethodsByGroup() {
		final String group = "S"; // Tested with rice and cowpea
		final long count = this.germplasmDataManager.countMethodsByGroup(group);
		Debug.println(IntegrationTestBase.INDENT, "testCountMethodsByGroup(group=" + group + "): " + count);
	}

	@Test
	public void testGetGermplasmDetailsByGermplasmNames() {
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

		results = this.germplasmDataManager
			.getGermplasmNameDetailsByGermplasmNames(germplasmNames, GetGermplasmByNameModes.SPACES_REMOVED_BOTH_SIDES);
		Debug.println(IntegrationTestBase.INDENT, "GetGermplasmByNameModes.SPACES_REMOVED_BOTH_SIDES:");
		Debug.printObjects(IntegrationTestBase.INDENT, results);

	}

	@Test
	public void testUpdateGermplasmName() {
		final int nameId = 1; // Assumption: id=1 exists
		final Name name = this.germplasmDataManager.getGermplasmNameByID(nameId);
		if (name != null) {
			final String nameBefore = name.toString();
			name.setLocationId(this.locationManager.getLocationByID(1).getLocid()); // Assumption: location with
			// id=1 exists
			this.germplasmDataManager.updateGermplasmName(name);
			Debug.println(IntegrationTestBase.INDENT,
				"testUpdateGermplasmName(" + nameId + "): " + "\n\tBEFORE: " + nameBefore + "\n\tAFTER: " + name.toString());
		}
	}

	@Test
	public void testAddGermplasmAttribute() {
		final Integer gid = 50533;
		final Attribute attribute = new Attribute();
		attribute.setAdate(0);
		attribute.setAval("aval");
		attribute.setGermplasmId(gid);
		attribute.setLocationId(0);
		attribute.setCreatedBy(0);
		attribute.setReferenceId(0);
		attribute.setTypeId(0);
		final Integer id = this.germplasmDataManager.addGermplasmAttribute(attribute);
		Debug.println(IntegrationTestBase.INDENT, "testAddGermplasmAttribute(" + gid + "): " + id + " = " + attribute);
	}

	@Test
	public void testUpdateGermplasmAttribute() {
		final int attributeId = 1; // Assumption: attribute with id = 1 exists

		final Attribute attribute = this.germplasmDataManager.getAttributeById(attributeId);

		if (attribute != null) {
			final String attributeString = attribute.toString();
			attribute.setAdate(0);
			attribute.setLocationId(0);
			attribute.setCreatedBy(0);
			attribute.setReferenceId(0);
			attribute.setTypeId(0);
			this.germplasmDataManager.updateGermplasmAttribute(attribute);

			Debug.println(IntegrationTestBase.INDENT,
				"testUpdateGermplasmAttribute(" + attributeId + "): " + "\ntBEFORE: " + attributeString + "\ntAFTER: " + attribute);
		}
	}

	@Test
	public void testAddGermplasmWithNameAndProgenitors() {

		final UserDefinedField nameType = this.createUserdefinedField("NAMES", "NAME", RandomStringUtils.randomAlphabetic(5).toUpperCase());
		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		germplasm.getPreferredName().setTypeId(nameType.getFldno());

		final Germplasm maleParent1 = GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		final Germplasm maleParent2 = GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.germplasmDAO.save(maleParent1);
		this.germplasmDAO.save(maleParent2);

		final Progenitor progenitor1 = new Progenitor(null, 3, maleParent1.getGid(), CREATED_BY);
		final Progenitor progenitor2 = new Progenitor(null, 4, maleParent2.getGid(), CREATED_BY);

		final Triple<Germplasm, Name, List<Progenitor>>
			germplasmTriple = ImmutableTriple.of(germplasm, germplasm.getPreferredName(), Arrays.asList(progenitor1, progenitor2));
		final List<Integer> gids = this.germplasmDataManager.addGermplasm(Arrays.asList(germplasmTriple), this.cropType);

		final int savedGermplasmGid = gids.get(0);
		final Germplasm savedGermplasm = this.germplasmDAO.getById(savedGermplasmGid);
		final Name savedName = this.nameDAO.getNamesByGids(Arrays.asList(savedGermplasmGid)).get(0);
		assertNotNull(savedGermplasm);
		Assert.assertEquals(4, savedGermplasm.getGnpgs().intValue());
		assertNotNull(savedName);
		Assert.assertEquals(1, savedName.getNstat().intValue());
		assertNotNull(this.progenitorDAO.getByGIDAndPID(savedGermplasmGid, progenitor1.getProgenitorGid()));
		assertNotNull(this.progenitorDAO.getByGIDAndPID(savedGermplasmGid, progenitor2.getProgenitorGid()));

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
		assertThat("GermplasmDataManager.getPlotCodeField() should never return null.", plotCodeField, is(notNullValue()));
		if (plotCodeField.getFldno() != 0) {
			// Non-zero fldno is a case where the UDFLD table has a record matching ftable=ATRIBUTS, ftype=PASSPORT, fcode=PLOTCODE
			// Usually the id of this record is 1552. Not asserting as we dont want tests to depend on primary key values to be exact.
			assertThat("ATRIBUTS", is(equalTo(plotCodeField.getFtable())));
			assertThat("PASSPORT", is(equalTo(plotCodeField.getFtype())));
			assertThat("PLOTCODE", is(equalTo(plotCodeField.getFcode())));

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
		assertThat("getPlotCodeValue() should never return null.", plotCode1, is(notNullValue()));
		assertThat("Expected `Unknown` returned when there is no plot code attribute present.", "Unknown", is(equalTo(plotCode1)));
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
		assertThat("getPlotCodeValue() should never return null.", plotCode2, is(notNullValue()));
		assertThat("Expected value of plot code attribute returned when plot code attribute is present.", plotCodeAttr.getAval(),
			is(equalTo(plotCode2)));
	}

	@Test
	public void testGetPreferredIdsByGIDs() throws MiddlewareQueryException {
		final List<Integer> gids = Arrays.asList(1, 2, 3, 4, 5);
		final Map<Integer, String> results = this.germplasmDataManager.getPreferredIdsByGIDs(gids);
		for (final Integer gid : results.keySet()) {
			Debug.println(IntegrationTestBase.INDENT, gid + " : " + results.get(gid));
		}
	}

	@Test
	public void testGetAllMethods() {
		final List<Method> results = this.germplasmDataManager.getAllMethods();
		assertThat(results, is(notNullValue()));
		assertThat(results, not(org.hamcrest.Matchers.empty()));
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testGetAllMethodsOrderByMname() {
		final List<Method> results = this.germplasmDataManager.getAllMethodsOrderByMname();
		assertThat(results, is(notNullValue()));
		assertThat(results, not(org.hamcrest.Matchers.empty()));
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testGetAllMethodsNotGenerative() {
		final List<Method> results = this.germplasmDataManager.getAllMethodsNotGenerative();
		assertThat(results, is(notNullValue()));
		assertThat(results, not(org.hamcrest.Matchers.empty()));
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testGetAllGermplasm() {
		final List<Germplasm> germplasms = this.germplasmDataManager.getAllGermplasm(0, 100);
		Debug.printObjects(IntegrationTestBase.INDENT, germplasms);
	}

	@Test
	public void testGetBibliographicalReferenceById() {
		final int id = 1;
		final Bibref bibref = this.germplasmDataManager.getBibliographicReferenceByID(id);
		Debug.println(IntegrationTestBase.INDENT, "testGetBibliographicalReferenceById(" + id + "): " + bibref);
	}

	@Test
	public void testGetMethodByID() {
		final int id = 4;
		final Method methodid = this.germplasmDataManager.getMethodByID(id);
		assertThat(methodid, is(notNullValue()));
		Debug.println(IntegrationTestBase.INDENT, "testGetMethodByID(" + id + "): ");
		Debug.println(IntegrationTestBase.INDENT, methodid);
	}

	@Test
	public void testGetUserDefinedFieldByID() {
		final Integer id = 1;
		final UserDefinedField result = this.germplasmDataManager.getUserDefinedFieldByID(id);
		assertThat(result, is(notNullValue()));
		Debug.println(IntegrationTestBase.INDENT, result);
	}

	@Test
	public void testGetBibliographicReferenceByID() {
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
	public void testGetGermplasmByLocationId() {
		final String name = "RCH";
		final int locationID = 0;

		final List<Germplasm> germplasmList = this.germplasmDataManager.getGermplasmByLocationId(name, locationID);
		assertThat(germplasmList, is(notNullValue()));

		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmByLocationId(" + name + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, germplasmList);
	}

	@Test
	public void testGetGermplasmByGidRange() {
		final int startGID = 1;
		final int endGID = 5;

		final List<Germplasm> germplasmList = this.germplasmDataManager.getGermplasmByGidRange(startGID, endGID);
		assertThat(germplasmList, is(notNullValue()));

		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmByGidRange(" + startGID + "," + endGID + "): ");
		Debug.printObjects(IntegrationTestBase.INDENT, germplasmList);
	}

	@Test
	public void testGetGermplasmByGIDList() {
		final List<Integer> gids = Arrays.asList(1, 2, 3, 4, 5);

		final List<Germplasm> germplasmList = this.germplasmDataManager.getGermplasms(gids);
		assertThat(germplasmList, is(notNullValue()));

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
	public void testSearchGermplasmWithInventory() throws MiddlewareQueryException {
		final Germplasm germplasm =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		final Integer germplasmId = this.germplasmDataManager.addGermplasm(germplasm, germplasm.getPreferredName(), this.cropType);

		final Lot lot = InventoryDetailsTestDataInitializer.createLot(1, "GERMPLSM", germplasmId, 1, 8264, 0, 1, "Comments", "InventoryId");
		this.lotDAO.save(lot);

		final Transaction transaction =
			InventoryDetailsTestDataInitializer
				.createTransaction(2.0, 0, "2 reserved", lot, 1, 1, 1, "LIST", TransactionType.WITHDRAWAL.getId());
		this.transactionDAO.save(transaction);

		final GermplasmSearchParameter searchParameter =
			new GermplasmSearchParameter(germplasm.getPreferredName().getNval(), Operation.LIKE);
		searchParameter.setIncludeParents(false);
		searchParameter.setWithInventoryOnly(true);
		searchParameter.setIncludeMGMembers(false);
		searchParameter.setStartingRow(0);
		searchParameter.setNumberOfEntries(25);

		final List<Germplasm> resultsWithInventoryOnly = this.germplasmDataManager.searchForGermplasm(searchParameter);

		assertThat(1, is(equalTo(resultsWithInventoryOnly.size())));
		assertThat(1, is(equalTo(resultsWithInventoryOnly.get(0).getInventoryInfo().getActualInventoryLotCount().intValue())));
		assertThat("2.0", is(equalTo(resultsWithInventoryOnly.get(0).getInventoryInfo().getTotalAvailableBalance().toString())));
		assertThat("g", is(equalTo(resultsWithInventoryOnly.get(0).getInventoryInfo().getScaleForGermplsm())));

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
		final int attributeType = 1115;
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
	public void testGetMethodByName() {
		final String name = "breeders seed";
		final Method method = this.germplasmDataManager.getMethodByName(name);
		assertThat(method, is(notNullValue()));
		Debug.println(IntegrationTestBase.INDENT, "testGetMethodByName(" + name + "): ");
		Debug.println(IntegrationTestBase.INDENT, method);
	}

	@Test
	public void testGetMethodByNameWithProgramUUID() {
		final String name = "breeders seed";
		final String programUUID = this.commonTestProject.getUniqueID();
		final Method method = this.germplasmDataManager.getMethodByName(name, programUUID);
		assertThat("Expecting the return method is not null.", method, is(notNullValue()));
	}

	@Test
	public void testGetMethodByCode() {
		final String code = "VBS";
		final Method method = this.germplasmDataManager.getMethodByCode(code);
		assertThat(method, is(notNullValue()));
		Debug.println(IntegrationTestBase.INDENT, "testGetMethodByCode(" + code + "): ");
		Debug.println(IntegrationTestBase.INDENT, method);
	}

	@Test
	public void testGetMethodByCodeWithProgramUUID() {
		final String code = "VBS";
		final String programUUID = this.commonTestProject.getUniqueID();
		final Method method = this.germplasmDataManager.getMethodByCode(code, programUUID);
		assertThat("Expecting the return method is not null.", method, is(notNullValue()));
	}

	@Test
	public void testAddUserDefinedField() {
		final UserDefinedField field = new UserDefinedField();
		field.setFtable("ATRIBUTS");
		field.setFtype("ATTRIBUTE");
		field.setFcode("MATURITY");
		field.setFname("Maturity class");
		field.setFfmt("MCLASS,ASSIGNED,C");
		field.setFdesc(separator);
		field.setLfldno(0);

		field.setFuid(1);
		field.setFdate(20041116);
		field.setScaleid(0);

		final Integer success = this.germplasmDataManager.addUserDefinedField(field);
		assertThat(success, is(greaterThan(0)));

		Debug.println(IntegrationTestBase.INDENT, "testAddUserDefinedField(" + field + "): ");
	}

	@Test
	public void testAddAttribute() {
		final Attribute attr = new Attribute();
		attr.setGermplasmId(237431);
		attr.setTypeId(1);
		attr.setCreatedBy(1);
		attr.setAval("EARLY");
		attr.setLocationId(31);
		attr.setReferenceId(0);
		attr.setAdate(20041116);

		this.germplasmDataManager.addAttribute(attr);
		assertThat(attr.getAid(), is(notNullValue()));

		Debug.println(IntegrationTestBase.INDENT, "testAddAttribute(" + attr + "): ");

		final Attribute readAttribute = this.germplasmDataManager.getAttributeById(attr.getAid());
		assertThat(readAttribute, is(notNullValue()));
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
			assertThat("There should be 2 program methods with programUUID[" + programUUID + "]", 2, is(equalTo(methodList.size())));
			// delete locations
			this.germplasmDataManager.deleteProgramMethodsByUniqueId(programUUID);
			methodList = this.germplasmDataManager.getProgramMethods(programUUID);
			assertThat("There should be no program methods with programUUID[" + programUUID + "]", methodList,
				org.hamcrest.Matchers.empty());
		} catch (final MiddlewareQueryException e) {
			assertThat("Getting and deleting of program methods failed", Boolean.FALSE);
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
	public void testGetFavoriteMethodsByMethodType() {
		final Method method = this.germplasmDataManager.getMethodByID(154);

		final String programUUID = UUID.randomUUID().toString();
		final ProgramFavorite programFavorite = this.programFavoriteTestDataInitializer.createProgramFavorite(method.getMid(), programUUID);
		this.germplasmDataManager.saveProgramFavorite(programFavorite);

		final List<Method> methods = this.germplasmDataManager.getFavoriteMethodsByMethodType(method.getMtype(), programUUID);
		final Method resultMethod = methods.get(0);
		assertThat("The method code should be " + method.getMcode(), method.getMcode(), is(equalTo(resultMethod.getMcode())));
		assertThat("The method id should be " + method.getMid(), method.getMid(), is(equalTo(resultMethod.getMid())));
		assertThat("The method type should be " + method.getMtype(), method.getMtype(), is(equalTo(resultMethod.getMtype())));
		assertThat("The method group should be " + method.getMgrp(), method.getMgrp(), is(equalTo(resultMethod.getMgrp())));
		assertThat("The method name should be " + method.getMname(), method.getMname(), is(equalTo(resultMethod.getMname())));
		assertThat("The method description should be " + method.getMdesc(), method.getMdesc(), is(equalTo(resultMethod.getMdesc())));
	}

	@Test
	public void testGetNamesByGidsAndNTypeIdsInMap() {
		final int GID1 = 1;
		final int GID2 = 2;
		final int GID3 = 3;

		//Get the names map before adding new names
		final Map<Integer, List<Name>> namesMap = this.germplasmDataManager.getNamesByGidsAndNTypeIdsInMap(Arrays.asList(GID1, GID2, GID3),
			Arrays.asList(GermplasmNameType.DERIVATIVE_NAME.getUserDefinedFieldID(), GermplasmNameType.LINE_NAME.getUserDefinedFieldID()));

		//Add new name for germplasm with gid = GID1
		Name name = NameTestDataInitializer.createName(GermplasmNameType.LINE_NAME.getUserDefinedFieldID(), GID1, "LINE NAME 00001");
		this.nameDAO.save(name);

		//Add new names for germplasm with gid = GID2
		name = NameTestDataInitializer.createName(GermplasmNameType.DERIVATIVE_NAME.getUserDefinedFieldID(), GID2, "DERIVATIVE NAME 00001");
		this.nameDAO.save(name);
		name = NameTestDataInitializer.createName(GermplasmNameType.LINE_NAME.getUserDefinedFieldID(), GID2, "LINE NAME 00001");
		this.nameDAO.save(name);

		//Get the names map after adding new names
		final Map<Integer, List<Name>> newNamesMap = this.germplasmDataManager
			.getNamesByGidsAndNTypeIdsInMap(Arrays.asList(GID1, GID2, GID3), Arrays
				.asList(GermplasmNameType.DERIVATIVE_NAME.getUserDefinedFieldID(), GermplasmNameType.LINE_NAME.getUserDefinedFieldID()));

		int sizeBeforeAddingNewName = namesMap.get(GID1) != null ? namesMap.get(GID1).size() : 0;
		int sizeAfterAddingNewName = newNamesMap.get(GID1).size();
		// Assert that the new size has 1 more name, which is the newly added name for germplasm with gid = GID1
		assertThat("Expecting list of names for GID 1 to be incremented by 1 new name.", sizeBeforeAddingNewName + 1,
			is(equalTo(sizeAfterAddingNewName)));
		sizeBeforeAddingNewName = namesMap.get(GID2) != null ? namesMap.get(GID2).size() : 0;
		sizeAfterAddingNewName = newNamesMap.get(GID2).size();
		assertThat("Expecting list of names for GID 2 to be incremented by 2 new names.", sizeBeforeAddingNewName + 2,
			is(equalTo(sizeAfterAddingNewName)));
		sizeBeforeAddingNewName = namesMap.get(GID3) != null ? namesMap.get(GID3).size() : 0;
		sizeAfterAddingNewName = newNamesMap.get(GID3) != null ? namesMap.get(GID3).size() : 0;
		assertThat("Expecting list of names for GID 3 to be constant since there are no new names added for it.", sizeBeforeAddingNewName,
			is(equalTo(sizeAfterAddingNewName)));
	}

	@Test
	public void testGetParentsInfoByGIDList() {
		final int GID1 = 1;
		final int GID2 = 2;
		final int GID3 = 3;
		final GermplasmDataManager germplasmDataManager = Mockito.mock(GermplasmDataManager.class);

		final Map<Integer, String[]> parentsInfo = new HashMap<>();

		final String[] parent1 = new String[] {separator, parent1Name};
		final String[] parent2 = new String[] {separator, parent2Name};
		final String[] parent3 = new String[] {separator, parent3Name};
		parentsInfo.put(1, parent1);
		parentsInfo.put(2, parent2);
		parentsInfo.put(3, parent3);

		Mockito.when(germplasmDataManager.getParentsInfoByGIDList(Arrays.asList(GID1, GID2, GID3))).thenReturn(parentsInfo);

		final Map<Integer, String[]> result = germplasmDataManager.getParentsInfoByGIDList((Arrays.asList(GID1, GID2, GID3)));
		assertThat(result.get(1)[0], is(equalTo(separator)));
		assertThat(result.get(1)[1], is(equalTo(parent1Name)));
		assertThat(result.get(2)[0], is(equalTo(separator)));
		assertThat(result.get(2)[1], is(equalTo(parent2Name)));
		assertThat(result.get(3)[0], is(equalTo(separator)));
		assertThat(result.get(3)[1], is(equalTo(parent3Name)));
	}

	@Test
	public void testGetAllAttributeTypes() {
		final List<UserDefinedField> attributeTypes = this.germplasmDataManager.getAllAttributesTypes();
		assertThat(attributeTypes, is(notNullValue()));
		for (final UserDefinedField field : attributeTypes) {
			assertThat("ATRIBUTS", is(equalTo(field.getFtable())));
		}
	}

	@Test
	public void testGetByFieldTableNameAndFTypeAndFName() {
		final UserDefinedField udfld = UserDefinedFieldTestDataInitializer.createUserDefinedField("NAMES", "NAME", "FNAME12345");
		this.germplasmDataManager.addUserDefinedField(udfld);
		final List<UserDefinedField> userDefinedFields = this.germplasmDataManager.getUserDefinedFieldByFieldTableNameAndFTypeAndFName(udfld.getFtable(), udfld.getFtype(), udfld.getFname());
		assertNotNull(userDefinedFields);
		Assert.assertFalse(userDefinedFields.isEmpty());
		for(final UserDefinedField userDefinedField: userDefinedFields) {
			Assert.assertEquals(udfld.getFtable(), userDefinedField.getFtable());
			Assert.assertEquals(udfld.getFtype(), userDefinedField.getFtype());
			Assert.assertEquals(udfld.getFname(), userDefinedField.getFname());
		}
	}

	@Test
	public void testGetAttributeValue() {
		final String attributeVal = "TEST_ATTRIBUTE";
		final Germplasm germplasm = this.createGermplasm();
		assertThat(germplasm.getGid(), is(notNullValue()));

		final Germplasm germplasmDB = this.germplasmDAO.getById(germplasm.getGid());
		assertThat(germplasm, is(equalTo(germplasmDB)));
		assertThat(germplasmDB, is(notNullValue()));

		final UserDefinedField userdefinedField = this.createUserdefinedField("ATRIBUTS", "PASSPORT", "TEST_ATT");
		assertThat(userdefinedField.getFldno(), is(notNullValue()));

		final UserDefinedField userdefinedFieldDB = this.userDefinedFieldDAO.getById(userdefinedField.getFldno());
		assertThat(userdefinedFieldDB, is(notNullValue()));
		assertThat(userdefinedField, is(equalTo(userdefinedFieldDB)));

		final Attribute attr = this.createAttribute(germplasmDB, userdefinedFieldDB, attributeVal);
		assertThat(attr.getAid(), is(notNullValue()));

		final Attribute attrDB = this.germplasmDataManager.getAttributeById(attr.getAid());
		assertThat(attrDB, is(notNullValue()));
		assertThat(attr, is(equalTo(attrDB)));

		final String attributeValue = this.germplasmDataManager.getAttributeValue(germplasmDB.getGid(), userdefinedField.getFcode());
		assertThat(attributeValue, is(notNullValue()));
		assertThat(attributeVal, is(attributeValue));
	}

	@Test
	public void testSave() {

		final Germplasm germplasm = this.createGermplasm();
		try {
			this.germplasmDataManager.save(germplasm);
		} catch (final MiddlewareQueryException e) {
			Assert.fail("Cannot save germplasm.");
		}

	}

	@Test
	public void testGetNamesByTypeAndGIDList() {
		final UserDefinedField nameType = this.createUserdefinedField("NAMES", "NAME", RandomStringUtils.randomAlphabetic(5).toUpperCase());
		final Germplasm germplasm1 = GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		germplasm1.getPreferredName().setTypeId(nameType.getFldno());
		final Integer gid1 = this.germplasmDataManager.addGermplasm(germplasm1, germplasm1.getPreferredName(), this.cropType);

		final Germplasm germplasm2 = GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		germplasm2.getPreferredName().setTypeId(nameType.getFldno());
		final Integer gid2 = this.germplasmDataManager.addGermplasm(germplasm2, germplasm2.getPreferredName(), this.cropType);

		final Germplasm germplasm3 = GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		final Integer gid3 = this.germplasmDataManager.addGermplasm(germplasm3, germplasm3.getPreferredName(), this.cropType);

		final Map<Integer, String> namesMap = this.germplasmDataManager.getNamesByTypeAndGIDList(nameType.getFldno(), Arrays.asList(gid1, gid2, gid3));
		assertNotNull(namesMap);
		Assert.assertEquals(3, namesMap.size());
		Assert.assertEquals(germplasm1.getPreferredName().getNval(), namesMap.get(gid1));
		Assert.assertEquals(germplasm2.getPreferredName().getNval(), namesMap.get(gid2));
		Assert.assertEquals("-", namesMap.get(gid3));
	}

	@Test
	public void testGetNamesByGidsAndPrefixes() {
		final Germplasm germplasm = this.createGermplasm();
		final Name name1 = NameTestDataInitializer.createName(2016, germplasm.getGid(), "PREF 001");
		this.nameDAO.save(name1);
		final Name name2 = NameTestDataInitializer.createName(2016, germplasm.getGid(), "REF 001");
		this.nameDAO.save(name2);
		final List<String> names = this.germplasmDataManager.getNamesByGidsAndPrefixes(Collections.singletonList(germplasm.getGid()), Collections.singletonList("PREF"));
		Assert.assertEquals(1, names.size());
		Assert.assertEquals(name1.getNval(), names.get(0));
	}

	@Test
	public void shouldGetUserDefinedFieldByTableTypeAndCodes() {

		final String fname1 = UUID.randomUUID().toString();
		this.userDefinedFieldDAO.save(UserDefinedFieldTestDataInitializer.createUserDefinedField(UDTableType.NAMES_NAME.getTable(),
			UDTableType.NAMES_NAME.getType(), fname1));

		final HashSet codes = new HashSet() {{
			this.add(UserDefinedFieldTestDataInitializer.CODE);
		}};
		final List<UserDefinedField> fields = this.germplasmDataManager.getUserDefinedFieldByTableTypeAndCodes(UDTableType.NAMES_NAME.getTable(),
			Collections.singleton(UDTableType.NAMES_NAME.getType()),
			codes);
		assertNotNull(fields);
		assertThat(fields, hasSize(1));
		assertThat(fields.get(0).getFcode(), is(UserDefinedFieldTestDataInitializer.CODE));
		assertThat(fields.get(0).getFname(), is(fname1));
	}

	@Test
	public void shouldGetAllUserDefinedFieldByTableAndTypeWithEmptyCodes() {

		final List<UserDefinedField> namesUserDefined = this.germplasmDataManager
			.getUserDefinedFieldByFieldTableNameAndType(UDTableType.NAMES_NAME.getTable(), UDTableType.NAMES_NAME.getType());

		final List<UserDefinedField> fields = this.germplasmDataManager.getUserDefinedFieldByTableTypeAndCodes(UDTableType.NAMES_NAME.getTable(),
			Collections.singleton(UDTableType.NAMES_NAME.getType()),
			new HashSet<>());
		assertNotNull(fields);
		assertThat(fields.size(), is(namesUserDefined.size()));
	}

	@Test
	public void shouldGetAllUserDefinedFieldByTableAndTypeWithNullCodes() {

		final List<UserDefinedField> namesUserDefined = this.germplasmDataManager
			.getUserDefinedFieldByFieldTableNameAndType(UDTableType.NAMES_NAME.getTable(), UDTableType.NAMES_NAME.getType());

		final List<UserDefinedField> fields = this.germplasmDataManager.getUserDefinedFieldByTableTypeAndCodes(UDTableType.NAMES_NAME.getTable(),
			Collections.singleton(UDTableType.NAMES_NAME.getType()),
			null);
		assertNotNull(fields);
		assertThat(fields.size(), is(namesUserDefined.size()));
	}

	private Attribute createAttribute(final Germplasm germplasm, final UserDefinedField userDefinedField, final String aval) {
		final Attribute attr = new Attribute();
		attr.setAid(1);
		attr.setGermplasmId(germplasm.getGid());
		attr.setTypeId(userDefinedField.getFldno());
		attr.setCreatedBy(1);
		attr.setAval(aval);
		attr.setLocationId(0);
		attr.setReferenceId(null);
		attr.setAdate(20180206);

		this.germplasmDataManager.addAttribute(attr);
		return attr;
	}

	private UserDefinedField createUserdefinedField(final String ftable, final String ftype, final String fcode) {
		final UserDefinedField usdl = new UserDefinedField();
		usdl.setFtable(ftable);
		usdl.setFtype(ftype);
		usdl.setFcode(fcode);
		usdl.setFname("Test");
		usdl.setFfmt(separator);
		usdl.setFdesc(separator);
		usdl.setLfldno(0);
		usdl.setLfldno(0);
		usdl.setFuid(0);
		usdl.setFdate(20180206);
		usdl.setScaleid(0);

		this.userDefinedFieldDAO.save(usdl);
		return usdl;
	}

	private Germplasm createGermplasm() {
		final Germplasm germplasm = new Germplasm();
		germplasm.setGid(1166066);
		germplasm.setMethodId(31);
		germplasm.setGnpgs(-1);
		germplasm.setGrplce(0);
		germplasm.setGpid1(0);
		germplasm.setGpid2(0);
		germplasm.setLgid(0);
		germplasm.setLocationId(0);
		germplasm.setGdate(20180206);
		germplasm.setReferenceId(0);

		this.germplasmDAO.save(germplasm);
		return germplasm;
	}
}
