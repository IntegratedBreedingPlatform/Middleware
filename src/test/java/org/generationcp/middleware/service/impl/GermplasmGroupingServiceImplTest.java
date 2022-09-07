package org.generationcp.middleware.service.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.MethodDAO;
import org.generationcp.middleware.dao.UserDefinedFieldDAO;
import org.generationcp.middleware.data.initializer.NameTestDataInitializer;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class GermplasmGroupingServiceImplTest {

	private static final Integer PREFERRED_CODE = 1;
	private static final Integer NON_PREFERRED_CODE = 0;
	private static final String CROP = "maize";

	@Mock
	private GermplasmDAO germplasmDAO;

	@Mock
	private MethodDAO methodDAO;

	@Mock
	private UserDefinedFieldDAO userDefinedFieldDAO;

	@Mock
	private GermplasmDataManager germplasmDataManager;

	@Mock
	private DaoFactory daoFactory;

	@InjectMocks
	private GermplasmGroupingServiceImpl germplasmGroupingService;

	private Name selectionHistoryNameParent;
	private Name selectionHistoryNameChild1;
	private Name selectionHistoryNameChild2;

	private UserDefinedField selectionHistoryNameCode;
	private UserDefinedField selHisFixNameCode;
	private UserDefinedField codedName1;
	private UserDefinedField codedName2;
	private UserDefinedField codedName3;

	static final Set<Integer> HYBRID_METHODS = Sets.newHashSet(416, 417, 418, 419, 426, 321);
	private final Integer gidToFix = new Random().nextInt();
	private Germplasm germplasmToFix;

	@Before
	public void beforeEachTest() {
		MockitoAnnotations.initMocks(this);
		ReflectionTestUtils.setField(this.germplasmGroupingService, "daoFactory", this.daoFactory);
		ReflectionTestUtils.setField(this.germplasmGroupingService, "germplasmDataManager", this.germplasmDataManager);
		ReflectionTestUtils.setField(this.germplasmGroupingService, "maxRecursiveQueries", 10);
		Mockito.doReturn(this.germplasmDAO).when(this.daoFactory).getGermplasmDao();
		Mockito.doReturn(this.methodDAO).when(this.daoFactory).getMethodDAO();
		Mockito.doReturn(this.userDefinedFieldDAO).when(this.daoFactory).getUserDefinedFieldDAO();

		this.selectionHistoryNameCode = new UserDefinedField(111);
		this.selectionHistoryNameCode.setFtable("NAMES");
		this.selectionHistoryNameCode.setFtype("NAME");
		this.selectionHistoryNameCode.setFcode(GermplasmGroupingServiceImpl.SELECTION_HISTORY_NAME_CODE);
		Mockito.when(
				this.userDefinedFieldDAO.getByTableTypeAndCode("NAMES", "NAME", GermplasmGroupingServiceImpl.SELECTION_HISTORY_NAME_CODE))
				.thenReturn(this.selectionHistoryNameCode);

		this.selectionHistoryNameCode.setFcode(GermplasmGroupingServiceImpl.SELECTION_HISTORY_NAME_CODE_FOR_CROSS);
		Mockito.when(this.userDefinedFieldDAO
				.getByTableTypeAndCode("NAMES", "NAME", GermplasmGroupingServiceImpl.SELECTION_HISTORY_NAME_CODE_FOR_CROSS))
				.thenReturn(this.selectionHistoryNameCode);

		this.selHisFixNameCode = new UserDefinedField(222);
		this.selHisFixNameCode.setFtable("NAMES");
		this.selHisFixNameCode.setFtype("NAME");
		this.selHisFixNameCode.setFcode(GermplasmGroupingServiceImpl.SELECTION_HISTORY_AT_FIXATION_NAME_CODE);
		Mockito.when(this.userDefinedFieldDAO
				.getByTableTypeAndCode("NAMES", "NAME", GermplasmGroupingServiceImpl.SELECTION_HISTORY_AT_FIXATION_NAME_CODE))
				.thenReturn(this.selHisFixNameCode);

		this.codedName1 = new UserDefinedField(600000007);
		this.codedName1.setFtable("NAMES");
		this.codedName1.setFtype("NAME");
		this.codedName1.setFcode(GermplasmGroupingServiceImpl.CODED_NAME_1);
		Mockito.when(this.userDefinedFieldDAO.getByTableTypeAndCode("NAMES", "NAME", GermplasmGroupingServiceImpl.CODED_NAME_1))
				.thenReturn(this.codedName1);

		this.codedName2 = new UserDefinedField(600000008);
		this.codedName2.setFtable("NAMES");
		this.codedName2.setFtype("NAME");
		this.codedName2.setFcode(GermplasmGroupingServiceImpl.CODED_NAME_2);
		Mockito.when(this.userDefinedFieldDAO.getByTableTypeAndCode("NAMES", "NAME", GermplasmGroupingServiceImpl.CODED_NAME_2))
				.thenReturn(this.codedName2);

		this.codedName3 = new UserDefinedField(600000009);
		this.codedName3.setFtable("NAMES");
		this.codedName3.setFtype("NAME");
		this.codedName3.setFcode(GermplasmGroupingServiceImpl.CODED_NAME_3);
		Mockito.when(this.userDefinedFieldDAO.getByTableTypeAndCode("NAMES", "NAME", GermplasmGroupingServiceImpl.CODED_NAME_3))
				.thenReturn(this.codedName3);

		this.selectionHistoryNameParent = new Name();
		this.selectionHistoryNameParent.setNstat(GermplasmGroupingServiceImplTest.PREFERRED_CODE);
		this.selectionHistoryNameParent.setNval("SelectionHistory");
		this.selectionHistoryNameParent.setTypeId(this.selectionHistoryNameCode.getFldno());

		this.selectionHistoryNameChild1 = new Name();
		this.selectionHistoryNameChild1.setNstat(GermplasmGroupingServiceImplTest.PREFERRED_CODE);
		this.selectionHistoryNameChild1.setNval("SelectionHistoryChild1");
		this.selectionHistoryNameChild1.setTypeId(this.selectionHistoryNameCode.getFldno());

		this.selectionHistoryNameChild2 = new Name();
		this.selectionHistoryNameChild2.setNstat(GermplasmGroupingServiceImplTest.PREFERRED_CODE);
		this.selectionHistoryNameChild2.setNval("SelectionHistoryChild2");
		this.selectionHistoryNameChild2.setTypeId(this.selectionHistoryNameCode.getFldno());

		this.germplasmToFix = new Germplasm();
		this.germplasmToFix.setGid(this.gidToFix);
		this.germplasmToFix.setNames(Lists.newArrayList(NameTestDataInitializer.createName(1, this.gidToFix, RandomStringUtils.randomAlphabetic(20))));
		final Method testMethod = new Method();
		testMethod.setMtype("DER");
		testMethod.setMid(new Random().nextInt(100));
		this.germplasmToFix.setMethod(testMethod);
		Mockito.doReturn(Collections.singletonList(this.germplasmToFix)).when(this.germplasmDAO).getByGIDList(ArgumentMatchers.anyList());
		Mockito.doReturn(testMethod).when(this.methodDAO).getById(testMethod.getMid());
	}

	/**
	 * MGID not present. No descendants.
	 */
	@Test
	public void testMarkFixedCase1() {
		this.germplasmToFix.getNames().clear();
		this.germplasmToFix.getNames().add(this.selectionHistoryNameParent);

		this.germplasmGroupingService.markFixed(Collections.singletonList(this.gidToFix), false, false);

		Assert.assertEquals("Expecting founder/parent mgid to be set the same as gid.", this.gidToFix, this.germplasmToFix.getMgid());
		Assert.assertEquals("Existing selection history should remain preferred name.", GermplasmGroupingServiceImplTest.PREFERRED_CODE,
				this.selectionHistoryNameParent.getNstat());
		Assert.assertEquals("Selection history at fixation should be added as a new name.", 2, this.germplasmToFix.getNames().size());
		Assert.assertEquals("Selection history at fixation should be added as a non-preferred name.",
				GermplasmGroupingServiceImplTest.NON_PREFERRED_CODE, this.germplasmToFix.getNames().get(1).getNstat());
		Assert.assertEquals("Selection history at fixation should be added as with name type = SELHISFIX code.",
				this.selHisFixNameCode.getFldno(), this.germplasmToFix.getNames().get(1).getTypeId());

		Mockito.verify(this.germplasmDAO, Mockito.times(1)).save(ArgumentMatchers.any(Germplasm.class));
	}

	/**
	 * MGID not present. Has descendants. Include descendants in group. Dont
	 * preserve existing group.
	 */
	@Test
	public void testMarkFixedCase2() {
		this.germplasmToFix.getNames().clear();
		this.germplasmToFix.getNames().add(this.selectionHistoryNameParent);

		final Germplasm child1 = new Germplasm();
		child1.setGid(2);
		child1.setMgid(222);
		child1.getNames().add(this.selectionHistoryNameChild1);

		final Germplasm child2 = new Germplasm();
		child2.setGid(3);
		child2.setMgid(333);
		child2.getNames().add(this.selectionHistoryNameChild2);

		Mockito.when(this.germplasmDAO.getAllChildren(this.gidToFix)).thenReturn(Lists.newArrayList(child1, child2));

		this.germplasmGroupingService.markFixed(Collections.singletonList(this.gidToFix), true, false);

		Assert.assertEquals("Expecting founder/parent mgid to be set the same as gid.", this.gidToFix, this.germplasmToFix.getMgid());
		Assert.assertEquals("Expecting child1 mgid to be set the same as founder/parent gid.", this.gidToFix, child1.getMgid());
		Assert.assertEquals("Expecting child2 mgid to be set the same as founder/parent gid.", this.gidToFix, child2.getMgid());

		// Parent selection history name must be copied as selhisfix and as
		// preferred name.
		Assert.assertEquals("Existing selection history should remain preferred name for parent.",
				GermplasmGroupingServiceImplTest.PREFERRED_CODE, this.selectionHistoryNameParent.getNstat());
		Assert.assertEquals("Selection history at fixation should be added as a new name for parent.", 2, this.germplasmToFix.getNames().size());
		Assert.assertEquals("Selection history at fixation should be added as a non-preferred name for parent.",
				GermplasmGroupingServiceImplTest.NON_PREFERRED_CODE, this.germplasmToFix.getNames().get(1).getNstat());
		Assert.assertEquals("Selection history at fixation should be added as with name type = SELHISFIX code for parent.",
				this.selHisFixNameCode.getFldno(), this.germplasmToFix.getNames().get(1).getTypeId());

		// Child1 selection history name must be copied as selhisfix and as
		// preferred name.
		Assert.assertEquals("Existing selection history should remain preferred name for child1.",
				GermplasmGroupingServiceImplTest.PREFERRED_CODE, this.selectionHistoryNameChild1.getNstat());
		Assert.assertEquals("Selection history at fixation should be added as a new name for child1.", 2, child1.getNames().size());
		Assert.assertEquals("Selection history at fixation should be added as non-preferred name for child1.",
				GermplasmGroupingServiceImplTest.NON_PREFERRED_CODE, child1.getNames().get(1).getNstat());
		Assert.assertEquals("Selection history at fixation should be added as with name type = SELHISFIX code for parent.",
				this.selHisFixNameCode.getFldno(), child1.getNames().get(1).getTypeId());

		// Child2 selection history name must be copied as selhisfix and as
		// preferred name.
		Assert.assertEquals("Existing selection history should remain preferred name for child2.",
				GermplasmGroupingServiceImplTest.PREFERRED_CODE, this.selectionHistoryNameChild2.getNstat());
		Assert.assertEquals("Selection history at fixation should be added as a new name for child2.", 2, child2.getNames().size());
		Assert.assertEquals("Selection history at fixation should be added as a non-preferred name for child2.",
				GermplasmGroupingServiceImplTest.NON_PREFERRED_CODE, child2.getNames().get(1).getNstat());
		Assert.assertEquals("Selection history at fixation should be added as with name type = SELHISFIX code for child2.",
				this.selHisFixNameCode.getFldno(), child2.getNames().get(1).getTypeId());

		Mockito.verify(this.germplasmDAO, Mockito.times(3)).save(ArgumentMatchers.any(Germplasm.class));
	}

	/**
	 * MGID not present. Has descendants. Dont include descendants in group.
	 */
	@Test
	public void testMarkFixedCase3() {
		final Germplasm child1 = new Germplasm();
		child1.setGid(2);
		child1.setMgid(222);

		final Germplasm child2 = new Germplasm();
		child2.setGid(3);
		child2.setMgid(333);

		Mockito.when(this.germplasmDAO.getAllChildren(this.gidToFix)).thenReturn(Lists.newArrayList(child1, child2));

		this.germplasmGroupingService.markFixed(Collections.singletonList(this.gidToFix), false, false);

		Assert.assertEquals("Expecting founder/parent mgid to be set the same as gid.", this.gidToFix, this.germplasmToFix.getMgid());
		Assert.assertEquals("Expecting child1 mgid to remain the same as before.", new Integer(222), child1.getMgid());
		Assert.assertEquals("Expecting child2 mgid to remain the same as before.", new Integer(333), child2.getMgid());

		Mockito.verify(this.germplasmDAO, Mockito.times(1)).save(ArgumentMatchers.any(Germplasm.class));
	}

	/**
	 * MGID is present. No descendants. Preserve existing mgid. Existing mgid is
	 * non-zero.
	 */
	@Test
	public void testMarkFixedCase4_1() {
		final Integer expectedMGID = 111;

		this.germplasmToFix.setMgid(expectedMGID);

		this.germplasmGroupingService.markFixed(Collections.singletonList(this.gidToFix), false, true);

		Assert.assertEquals("Expecting founder/parent mgid to be preserved.", expectedMGID, this.germplasmToFix.getMgid());

		Mockito.verify(this.germplasmDAO, Mockito.never()).save(ArgumentMatchers.any(Germplasm.class));
	}

	/**
	 * MGID is present. No descendants. Preserve existing mgid. Existing mgid is
	 * zero.
	 */
	@Test
	public void testMarkFixedCase4_2() {
		this.germplasmToFix.setMgid(0);

		this.germplasmGroupingService.markFixed(Collections.singletonList(this.gidToFix), false, true);

		Assert.assertEquals("Expecting founder/parent mgid to be set to be the same as gid.", this.gidToFix,
				germplasmToFix.getMgid());

		Mockito.verify(this.germplasmDAO, Mockito.times(1)).save(ArgumentMatchers.any(Germplasm.class));
	}

	/**
	 * MGID is present. Has descendants. Dont preserve existing mgid. Include
	 * descendants in group.
	 */
	@Test
	public void testMarkFixedCase5() {
		this.germplasmToFix.setMgid(111);

		final Germplasm child1 = new Germplasm();
		child1.setGid(2);
		child1.setMgid(222);

		final Germplasm child2 = new Germplasm();
		child2.setGid(3);
		child2.setMgid(333);

		Mockito.when(this.germplasmDAO.getAllChildren(this.gidToFix)).thenReturn(Lists.newArrayList(child1, child2));

		this.germplasmGroupingService.markFixed(Collections.singletonList(this.gidToFix), true, false);

		Assert.assertEquals("Expecting founder/parent mgid to be set the same as gid.", this.gidToFix, this.germplasmToFix.getMgid());
		Assert.assertEquals("Expecting child1 mgid to be set the same as founder/parent gid.", this.gidToFix, child1.getMgid());
		Assert.assertEquals("Expecting child2 mgid to be set the same as founder/parent gid.", this.gidToFix, child2.getMgid());

		Mockito.verify(this.germplasmDAO, Mockito.times(3)).save(ArgumentMatchers.any(Germplasm.class));
	}

	/**
	 * Method is generative for parent.
	 */
	@Test
	public void testMarkFixedCase6() {
		final Integer generativeMethodId = 123;
		final Integer expectedParentMGID = 111;
		this.germplasmToFix.setMgid(expectedParentMGID);

		final Method method = new Method(generativeMethodId);
		method.setMtype("GEN");
		this.germplasmToFix.setMethod(method);

		final Germplasm child1 = new Germplasm();
		child1.setGid(2);
		child1.setMgid(222);

		final Germplasm child2 = new Germplasm();
		child2.setGid(3);
		child2.setMgid(333);

		Mockito.when(this.germplasmDAO.getAllChildren(this.gidToFix)).thenReturn(Lists.newArrayList(child1, child2));

		this.germplasmGroupingService.markFixed(Collections.singletonList(this.gidToFix), true, false);

		Assert.assertEquals("Expecting no mgid changes when method is generative.", expectedParentMGID, this.germplasmToFix.getMgid());

		Mockito.verify(this.germplasmDAO, Mockito.never()).save(ArgumentMatchers.any(Germplasm.class));
	}

	/**
	 * Method is generative for one of the descendant.
	 */
	@Test
	public void testMarkFixedCase7() {

		final Germplasm child1 = new Germplasm();
		child1.setGid(2);
		child1.setMgid(222);

		final Germplasm child2 = new Germplasm();
		child2.setGid(3);
		final Integer expectedChild2MGID = 333;
		child2.setMgid(expectedChild2MGID);

		final Integer generativeMethodId = 123;
		final Method method = new Method(generativeMethodId);
		method.setMtype("GEN");
		child2.setMethod(method);

		Mockito.when(this.germplasmDAO.getAllChildren(this.gidToFix)).thenReturn(Lists.newArrayList(child1, child2));

		this.germplasmGroupingService.markFixed(Collections.singletonList(this.gidToFix), true, false);

		Assert.assertEquals("Expecting founder/parent mgid to be set the same as gid.", this.gidToFix, this.germplasmToFix.getMgid());
		Assert.assertEquals("Expecting child1 mgid to be set the same as founder/parent gid.", this.gidToFix, child1.getMgid());
		Assert.assertEquals("Expecting child2 mgid to remain unchanged as method is generative.", expectedChild2MGID, child2.getMgid());

		Mockito.verify(this.germplasmDAO, Mockito.times(2)).save(ArgumentMatchers.any(Germplasm.class));
	}
	
	@Test
	public void testMarkFixedCase_ExceedsMaxRecursiveQueries() {
		final Integer generativeMethodId = 123;
		final Method method = new Method(generativeMethodId);
		method.setMtype("GEN");
		Mockito.when(this.methodDAO.getById(generativeMethodId)).thenReturn(method);

		final Germplasm child1 = new Germplasm();
		child1.setGid(2);
		child1.setMgid(222);

		final Germplasm child2 = new Germplasm();
		child2.setGid(3);
		final Integer expectedChild2MGID = 333;
		child2.setMgid(expectedChild2MGID);
		child2.setMethod(method);

		final Germplasm child3 = new Germplasm();
		child2.setGid(4);

		Mockito.when(this.germplasmDAO.getAllChildren(this.gidToFix)).thenReturn(Lists.newArrayList(child1, child2));
		Mockito.when(this.germplasmDAO.getAllChildren(child2.getGid())).thenReturn(Lists.newArrayList(child3));

		// Set 1 as maximum level to traverse for descendants
		ReflectionTestUtils.setField(this.germplasmGroupingService, "maxRecursiveQueries", 1);
		try {
			this.germplasmGroupingService.markFixed(Collections.singletonList(this.gidToFix), true, false);
			Assert.fail();
		} catch (final MiddlewareRequestException exception) {
			Assert.assertTrue(exception.getErrorCodeParamsMultiMap().containsKey("germplasm.grouping.max.recursive.queries.reached"));
		}
	}

	/**
	 * First basic scenario with data available to go through all main logic
	 * steps. Cross with hybrid method, both parents containing mgid, previous
	 * crosses existing with mgid.
	 */
	@Test
	public void testProcessGroupInheritanceForCrosses1() {
		final Integer crossGid1 = 1;
		final Integer crossGid1Parent1 = 11;
		final Integer crossGid1Parent1MGID = 111;
		final Integer crossGid1Parent2 = 12;
		final Integer crossGid1Parent2MGID = 112;
		final Integer hybridMethodId = 416;

		final Germplasm crossGermplasm1 = new Germplasm();
		crossGermplasm1.setGid(crossGid1);
		crossGermplasm1.setMethod(new Method(hybridMethodId));
		crossGermplasm1.setGpid1(crossGid1Parent1);
		crossGermplasm1.setGpid2(crossGid1Parent2);
		crossGermplasm1.setMgid(0);

		final Germplasm crossGermplasm1Parent1 = new Germplasm();
		crossGermplasm1Parent1.setGid(crossGid1Parent1);
		crossGermplasm1Parent1.setMgid(crossGid1Parent1MGID);
		crossGermplasm1Parent1.setMethod(new Method(1));

		final Germplasm crossGermplasm1Parent2 = new Germplasm();
		crossGermplasm1Parent2.setGid(crossGid1Parent2);
		crossGermplasm1Parent2.setMgid(crossGid1Parent2MGID);
		crossGermplasm1Parent2.setMethod(new Method(1));

		// Setup previous cross with MGID.
		final Germplasm previousCross = new Germplasm();
		previousCross.setGid(123);
		final Integer previousCrossMGID = 456;
		previousCross.setMgid(previousCrossMGID);
		previousCross.setMethod(new Method(1));
		Mockito.when(this.germplasmDAO.getPreviousCrossesBetweenParentGroups(crossGermplasm1))
				.thenReturn(Lists.newArrayList(previousCross));

		// Just to test, create another cross with non-hybrid method. Expect
		// this to not be processed.
		final Integer crossGid2 = 2;
		final Integer nonHybridMethodId = 416;
		final Germplasm crossGermplasm2 = new Germplasm();
		crossGermplasm2.setGid(crossGid2);
		crossGermplasm2.setMethod(new Method(hybridMethodId));
		crossGermplasm1.setMethod(new Method(nonHybridMethodId));
		Mockito.when(this.germplasmDataManager.getGermplasmWithAllNamesAndAncestry(ImmutableSet.of(crossGid1, crossGid2), 2))
				.thenReturn(ImmutableList.of(crossGermplasm1, crossGermplasm1Parent1, crossGermplasm1Parent2, crossGermplasm2));
		this.germplasmGroupingService.processGroupInheritanceForCrosses(GermplasmGroupingServiceImplTest.CROP, this.getGermplasmIdMethodIdMap(Lists.newArrayList(crossGermplasm1, crossGermplasm2)), false,
				GermplasmGroupingServiceImplTest.HYBRID_METHODS);

		Assert.assertEquals("Expected new cross to be assigned MGID from previous cross.", previousCrossMGID, crossGermplasm1.getMgid());

		// Previous crosses should be queried once.
		Mockito.verify(this.germplasmDAO, Mockito.times(1)).getPreviousCrossesBetweenParentGroups(crossGermplasm1);
		// One Germplasm record should be saved out of the two that are passed.
		Mockito.verify(this.germplasmDAO, Mockito.times(1)).save(ArgumentMatchers.any(Germplasm.class));
	}

	/**
	 * Basic scenario with data available to go through all main logic steps.
	 * Cross with hybrid method, both parents containing mgid, no previous
	 * crosses existing.
	 */
	@Test
	public void testProcessGroupInheritanceForCrosses2() {

		final Integer crossGid1 = 1;
		final Integer crossGid1Parent1 = 11;
		final Integer crossGid1Parent1MGID = 111;
		final Integer crossGid1Parent2 = 12;
		final Integer crossGid1Parent2MGID = 112;
		final Integer hybridMethodId = 416;

		final Germplasm crossGermplasm1 = new Germplasm();
		crossGermplasm1.setGid(crossGid1);
		crossGermplasm1.setMethod(new Method(hybridMethodId));
		crossGermplasm1.setGpid1(crossGid1Parent1);
		crossGermplasm1.setGpid2(crossGid1Parent2);
		crossGermplasm1.setMgid(0);

		final Germplasm crossGermplasm1Parent1 = new Germplasm();
		crossGermplasm1Parent1.setGid(crossGid1Parent1);
		crossGermplasm1Parent1.setMgid(crossGid1Parent1MGID);
		crossGermplasm1Parent1.setMethod(new Method(1));

		final Germplasm crossGermplasm1Parent2 = new Germplasm();
		crossGermplasm1Parent2.setGid(crossGid1Parent2);
		crossGermplasm1Parent2.setMgid(crossGid1Parent2MGID);
		crossGermplasm1Parent2.setMethod(new Method(1));

		// Just to test, create another cross with non-hybrid method. Expect
		// this to not be processed.
		final Integer crossGid2 = 2;
		final Integer nonHybridMethodId = 416;
		final Germplasm crossGermplasm2 = new Germplasm();
		crossGermplasm2.setGid(crossGid2);
		crossGermplasm2.setMethod(new Method(hybridMethodId));
		crossGermplasm1.setMethod(new Method(nonHybridMethodId));
		Mockito.when(this.germplasmDAO.getById(crossGid2)).thenReturn(crossGermplasm2);
		Mockito.when(this.germplasmDataManager.getGermplasmWithAllNamesAndAncestry(ImmutableSet.of(crossGid1, crossGid2), 2))
				.thenReturn(ImmutableList.of(crossGermplasm1, crossGermplasm1Parent1, crossGermplasm1Parent2, crossGermplasm2));

		this.germplasmGroupingService.processGroupInheritanceForCrosses(GermplasmGroupingServiceImplTest.CROP, this.getGermplasmIdMethodIdMap(Lists.newArrayList(crossGermplasm1, crossGermplasm2)), false,
				GermplasmGroupingServiceImplTest.HYBRID_METHODS);

		Assert.assertEquals("Expected new cross to be assigned MGID = GID of the cross when no previous crosses exist.", crossGid1,
				crossGermplasm1.getMgid());
		// Previous crosses should be queried once.
		Mockito.verify(this.germplasmDAO, Mockito.times(1)).getPreviousCrossesBetweenParentGroups(crossGermplasm1);
		// No selection history should be copied.
		// One Germplasm record should be saved out of the two that are passed.
		Mockito.verify(this.germplasmDAO, Mockito.times(1)).save(ArgumentMatchers.any(Germplasm.class));
	}

	/**
	 * Edge scenario method is hybrid but both parents don't have MGID.
	 */
	@Test
	public void testProcessGroupInheritanceForCrosses3() {

		final Integer crossGid1 = 1;
		final Integer crossGid1Parent1 = 11;
		final Integer crossGid1Parent2 = 12;
		final Integer hybridMethodId = 416;

		final Germplasm crossGermplasm1 = new Germplasm();
		crossGermplasm1.setGid(crossGid1);
		crossGermplasm1.setMethod(new Method(hybridMethodId));
		crossGermplasm1.setGpid1(crossGid1Parent1);
		crossGermplasm1.setGpid2(crossGid1Parent2);
		crossGermplasm1.setMgid(0);

		final Germplasm crossGermplasm1Parent1 = new Germplasm();
		crossGermplasm1Parent1.setGid(crossGid1Parent1);
		crossGermplasm1Parent1.setMgid(null);
		crossGermplasm1Parent1.setMethod(new Method(1));

		final Germplasm crossGermplasm1Parent2 = new Germplasm();
		crossGermplasm1Parent2.setGid(crossGid1Parent2);
		crossGermplasm1Parent2.setMgid(null);
		crossGermplasm1Parent2.setMethod(new Method(1));
		Mockito.when(this.germplasmDataManager.getGermplasmWithAllNamesAndAncestry(ImmutableSet.of(crossGid1), 2))
				.thenReturn(ImmutableList.of(crossGermplasm1, crossGermplasm1Parent1, crossGermplasm1Parent2));

		this.germplasmGroupingService
				.processGroupInheritanceForCrosses(GermplasmGroupingServiceImplTest.CROP, this.getGermplasmIdMethodIdMap(Collections.singletonList(crossGermplasm1)), false, GermplasmGroupingServiceImplTest.HYBRID_METHODS);

		Assert.assertEquals("Expected no MGID change.", new Integer(0), crossGermplasm1.getMgid());
		// Previous crosses should never be queried.
		Mockito.verify(this.germplasmDAO, Mockito.never()).getPreviousCrossesBetweenParentGroups(crossGermplasm1);
		// No selection history should be copied.
		// No Germplasm record should be saved.
		Mockito.verify(this.germplasmDAO, Mockito.never()).save(ArgumentMatchers.any(Germplasm.class));
	}

	/**
	 * Edge scenario method is not hybrid.
	 */
	@Test
	public void testProcessGroupInheritanceForCrosses4() {

		final Integer crossGid1 = 1;
		final Integer crossGid1Parent1 = 11;
		final Integer crossGid1Parent2 = 12;
		final Integer nonHybridMethodId = -999;

		final Germplasm crossGermplasm1 = new Germplasm();
		crossGermplasm1.setGid(crossGid1);
		crossGermplasm1.setMethod(new Method(nonHybridMethodId));
		crossGermplasm1.setGpid1(crossGid1Parent1);
		crossGermplasm1.setGpid2(crossGid1Parent2);
		crossGermplasm1.setMgid(0);
		Mockito.when(this.germplasmDataManager.getGermplasmWithAllNamesAndAncestry(ImmutableSet.of(crossGid1), 2))
				.thenReturn(ImmutableList.of(crossGermplasm1));

		this.germplasmGroupingService
				.processGroupInheritanceForCrosses(GermplasmGroupingServiceImplTest.CROP, this.getGermplasmIdMethodIdMap(Collections.singletonList(crossGermplasm1)), false, GermplasmGroupingServiceImplTest.HYBRID_METHODS);

		Assert.assertEquals("Expected no MGID change.", new Integer(0), crossGermplasm1.getMgid());
		// Previous crosses should never be queried.
		Mockito.verify(this.germplasmDAO, Mockito.never()).getPreviousCrossesBetweenParentGroups(crossGermplasm1);
		// No selection history should be copied.
		// No Germplasm record should be saved.
		Mockito.verify(this.germplasmDAO, Mockito.never()).save(ArgumentMatchers.any(Germplasm.class));
	}

	/**
	 * Cross with hybrid method, both parents containing mgid, previous crosses
	 * existing but none with mgid. Current cross should start a new group.
	 */
	@Test
	public void testProcessGroupInheritanceForCrosses5() {

		final Integer crossGid1 = 1;
		final Integer crossGid1Parent1 = 11;
		final Integer crossGid1Parent1MGID = 111;
		final Integer crossGid1Parent2 = 12;
		final Integer crossGid1Parent2MGID = 112;
		final Integer hybridMethodId = 416;

		final Germplasm crossGermplasm1 = new Germplasm();
		crossGermplasm1.setGid(crossGid1);
		crossGermplasm1.setMethod(new Method(hybridMethodId));
		crossGermplasm1.setGpid1(crossGid1Parent1);
		crossGermplasm1.setGpid2(crossGid1Parent2);
		crossGermplasm1.setMgid(0);
		Mockito.when(this.germplasmDAO.getById(crossGid1)).thenReturn(crossGermplasm1);

		final Germplasm crossGermplasm1Parent1 = new Germplasm();
		crossGermplasm1Parent1.setGid(crossGid1Parent1);
		crossGermplasm1Parent1.setMgid(crossGid1Parent1MGID);
		crossGermplasm1Parent1.setMethod(new Method(1));
		Mockito.when(this.germplasmDAO.getById(crossGid1Parent1)).thenReturn(crossGermplasm1Parent1);

		final Germplasm crossGermplasm1Parent2 = new Germplasm();
		crossGermplasm1Parent2.setGid(crossGid1Parent2);
		crossGermplasm1Parent2.setMgid(crossGid1Parent2MGID);
		crossGermplasm1Parent2.setMethod(new Method(2));
		Mockito.when(this.germplasmDAO.getById(crossGid1Parent2)).thenReturn(crossGermplasm1Parent2);

		// Setup previous cross with MGID.
		final Germplasm previousCross = new Germplasm();
		previousCross.setGid(123);
		previousCross.setMethod(new Method(1));
		// No mgid on previous cross
		Mockito.when(this.germplasmDAO.getPreviousCrossesBetweenParentGroups(crossGermplasm1))
				.thenReturn(Lists.newArrayList(previousCross));
		Mockito.when(this.germplasmDataManager.getGermplasmWithAllNamesAndAncestry(ImmutableSet.of(crossGid1), 2))
				.thenReturn(ImmutableList.of(crossGermplasm1, crossGermplasm1Parent1, crossGermplasm1Parent2));

		this.germplasmGroupingService
				.processGroupInheritanceForCrosses(GermplasmGroupingServiceImplTest.CROP, this.getGermplasmIdMethodIdMap(Collections.singletonList(crossGermplasm1)), true, GermplasmGroupingServiceImplTest.HYBRID_METHODS);

		Assert.assertEquals("Expected new cross to be assigned MGID = GID as none of the previous crosses have MGID.", crossGid1,
				crossGermplasm1.getMgid());
		Assert.assertEquals("Expected previous cross to be assigned same MGID as the current cross.", crossGermplasm1.getMgid(),
				previousCross.getMgid());

		// Previous crosses should be queried once.
		Mockito.verify(this.germplasmDAO, Mockito.times(1)).getPreviousCrossesBetweenParentGroups(crossGermplasm1);
		// One Germplasm record should be saved out of the two that are passed.
		// One other save should occur for saving mgid on previous cross.
		Mockito.verify(this.germplasmDAO, Mockito.times(2)).save(ArgumentMatchers.any(Germplasm.class));
	}

	/**
	 * Basic scenario with data available to go through all main logic steps.
	 * Cross with hybrid method, both parents containing mgid, previous crosses
	 * existing with mgid. Apply group to previous crosses.
	 */
	@Test
	public void testProcessGroupInheritanceForCrosses6() {

		final Integer crossGid1 = 1;
		final Integer crossGid1Parent1 = 11;
		final Integer crossGid1Parent1MGID = 111;
		final Integer crossGid1Parent2 = 12;
		final Integer crossGid1Parent2MGID = 112;
		final Integer hybridMethodId = 416;

		final Germplasm crossGermplasm1 = new Germplasm();
		crossGermplasm1.setGid(crossGid1);
		crossGermplasm1.setMethod(new Method(hybridMethodId));
		crossGermplasm1.setGpid1(crossGid1Parent1);
		crossGermplasm1.setGpid2(crossGid1Parent2);
		crossGermplasm1.setMgid(0);

		final Germplasm crossGermplasm1Parent1 = new Germplasm();
		crossGermplasm1Parent1.setGid(crossGid1Parent1);
		crossGermplasm1Parent1.setMgid(crossGid1Parent1MGID);
		crossGermplasm1Parent1.setMethod(new Method(1));

		final Germplasm crossGermplasm1Parent2 = new Germplasm();
		crossGermplasm1Parent2.setGid(crossGid1Parent2);
		crossGermplasm1Parent2.setMgid(crossGid1Parent2MGID);
		crossGermplasm1Parent2.setMethod(new Method(1));

		// Setup previous cross with MGID.
		final Germplasm previousCross = new Germplasm();
		previousCross.setGid(123);
		previousCross.setMethod(new Method(1));
		final Integer previousCrossMGID = 456;
		previousCross.setMgid(previousCrossMGID);
		Mockito.when(this.germplasmDAO.getPreviousCrossesBetweenParentGroups(crossGermplasm1))
				.thenReturn(Lists.newArrayList(previousCross));

		// Just to test, create another cross with non-hybrid method. Expect
		// this to not be processed.
		final Integer crossGid2 = 2;
		final Integer nonHybridMethodId = 416;
		final Germplasm crossGermplasm2 = new Germplasm();
		crossGermplasm2.setGid(crossGid2);
		crossGermplasm2.setMethod(new Method(hybridMethodId));
		crossGermplasm1.setMethod(new Method(nonHybridMethodId));
		Mockito.when(this.germplasmDataManager.getGermplasmWithAllNamesAndAncestry(ImmutableSet.of(crossGid1, crossGid2), 2))
				.thenReturn(ImmutableList.of(crossGermplasm1, crossGermplasm1Parent1, crossGermplasm1Parent2, crossGermplasm2));
		this.germplasmGroupingService.processGroupInheritanceForCrosses(GermplasmGroupingServiceImplTest.CROP, this.getGermplasmIdMethodIdMap(Lists.newArrayList(crossGermplasm1, crossGermplasm2)), true,
				GermplasmGroupingServiceImplTest.HYBRID_METHODS);

		Assert.assertEquals("Expected new cross to be assigned MGID from previous cross.", previousCrossMGID, crossGermplasm1.getMgid());

		// Previous crosses should be queried once.
		Mockito.verify(this.germplasmDAO, Mockito.times(1)).getPreviousCrossesBetweenParentGroups(crossGermplasm1);
		// One Germplasm record should be saved out of the two that are passed.
		// One other save should occur for saving mgid on previous cross.
		Mockito.verify(this.germplasmDAO, Mockito.times(2)).save(ArgumentMatchers.any(Germplasm.class));
	}

	/**
	 * Edge scenario method is hybrid but male parent is unknown
	 */
	@Test
	public void testProcessGroupInheritanceForCrossesWhereOneParentIsUnknown() {

		final Integer crossGid1 = 1;
		final Integer crossGid1Parent1 = 11;
		final Integer hybridMethodId = 416;

		final Germplasm crossGermplasm1 = new Germplasm();
		crossGermplasm1.setGid(crossGid1);
		crossGermplasm1.setMethod(new Method(hybridMethodId));
		crossGermplasm1.setGpid1(crossGid1Parent1);
		crossGermplasm1.setGpid2(0);
		crossGermplasm1.setMgid(0);

		final Germplasm crossGermplasm1Parent1 = new Germplasm();
		crossGermplasm1Parent1.setGid(crossGid1Parent1);
		crossGermplasm1Parent1.setMgid(null);
		crossGermplasm1Parent1.setMethod(new Method(1));

		Mockito.when(this.germplasmDataManager.getGermplasmWithAllNamesAndAncestry(ImmutableSet.of(crossGid1), 2))
			.thenReturn(ImmutableList.of(crossGermplasm1, crossGermplasm1Parent1));

		this.germplasmGroupingService
			.processGroupInheritanceForCrosses(GermplasmGroupingServiceImplTest.CROP, this.getGermplasmIdMethodIdMap(Collections.singletonList(crossGermplasm1)), false, GermplasmGroupingServiceImplTest.HYBRID_METHODS);

		Assert.assertEquals("Expected no MGID change.", new Integer(0), crossGermplasm1.getMgid());
		// Previous crosses should never be queried.
		Mockito.verify(this.germplasmDAO, Mockito.never()).getPreviousCrossesBetweenParentGroups(crossGermplasm1);
		// No selection history should be copied.
		// No Germplasm record should be saved.
		Mockito.verify(this.germplasmDAO, Mockito.never()).save(ArgumentMatchers.any(Germplasm.class));
	}

	@Test
	public void testGetSelectionHistory() {
		final Germplasm germplasm = new Germplasm();
		// Germplasm has no name yet, expect to return null
		Assert.assertNull(
				this.germplasmGroupingService.getSelectionHistory(germplasm, GermplasmGroupingServiceImpl.SELECTION_HISTORY_NAME_CODE));

		// Add a matching name
		final Name selHisNameExpected = new Name();
		selHisNameExpected.setNid(1);
		selHisNameExpected.setTypeId(this.selectionHistoryNameCode.getFldno());
		germplasm.getNames().add(selHisNameExpected);

		final Name selectionHistoryName =
				this.germplasmGroupingService.getSelectionHistory(germplasm, GermplasmGroupingServiceImpl.SELECTION_HISTORY_NAME_CODE);
		Assert.assertEquals(selHisNameExpected, selectionHistoryName);
	}

	@Test
	public void testGetSelectionHistoryAtFixation() {
		final Germplasm germplasm = new Germplasm();
		// Germplasm has no name yet, expect to return null
		Assert.assertNull(this.germplasmGroupingService
				.getSelectionHistory(germplasm, GermplasmGroupingServiceImpl.SELECTION_HISTORY_AT_FIXATION_NAME_CODE));

		// Add a matching name
		final Name selHisFixNameExpected = new Name();
		selHisFixNameExpected.setNid(1);
		selHisFixNameExpected.setTypeId(this.selHisFixNameCode.getFldno());
		germplasm.getNames().add(selHisFixNameExpected);

		final Name selHisFixNameActual = this.germplasmGroupingService
				.getSelectionHistory(germplasm, GermplasmGroupingServiceImpl.SELECTION_HISTORY_AT_FIXATION_NAME_CODE);
		Assert.assertEquals(selHisFixNameExpected, selHisFixNameActual);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetSelectionHistoryNameType() {

		Mockito.when(
				this.userDefinedFieldDAO.getByTableTypeAndCode("NAMES", "NAME", GermplasmGroupingServiceImpl.SELECTION_HISTORY_NAME_CODE))
				.thenReturn(null);

		this.germplasmGroupingService.getSelectionHistoryNameType(GermplasmGroupingServiceImpl.SELECTION_HISTORY_NAME_CODE);
		Assert.fail("When " + GermplasmGroupingServiceImpl.SELECTION_HISTORY_NAME_CODE
				+ " name type is not setup in UDFLD table, IllegalStateException is expected which did not happen.");
	}

	@Test(expected = IllegalStateException.class)
	public void testGetSelectionHistoryAtFixationNameType() {

		Mockito.when(this.userDefinedFieldDAO
				.getByTableTypeAndCode("NAMES", "NAME", GermplasmGroupingServiceImpl.SELECTION_HISTORY_AT_FIXATION_NAME_CODE))
				.thenReturn(null);

		this.germplasmGroupingService.getSelectionHistoryNameType(GermplasmGroupingServiceImpl.SELECTION_HISTORY_AT_FIXATION_NAME_CODE);
		Assert.fail("When " + GermplasmGroupingServiceImpl.SELECTION_HISTORY_AT_FIXATION_NAME_CODE
				+ " name type is not setup in UDFLD table, IllegalStateException is expected which did not happen.");
	}

	@Test
	public void testCopyParentalSelectionHistoryAtFixation() {

		// Setup source germplasm which has SELHISFIX name
		final Germplasm advancedGermplasmSource = new Germplasm();
		advancedGermplasmSource.setGid(2);

		final Name selHisFixNameOfParent = NameTestDataInitializer
				.createName(11, GermplasmGroupingServiceImplTest.PREFERRED_CODE, "CML", this.selHisFixNameCode.getFldno());
		advancedGermplasmSource.getNames().add(selHisFixNameOfParent);

		// Setup advanced germplasm with a name given on advancing
		final Germplasm advancedGermplasm = new Germplasm();
		advancedGermplasm.setGid(3);
		advancedGermplasm.setGpid1(1);
		advancedGermplasm.setGpid2(advancedGermplasm.getGpid2());

		final Name normalAdvancingNameOfChild =
				NameTestDataInitializer.createName(22, GermplasmGroupingServiceImplTest.PREFERRED_CODE, "CML-1-1-1", 5);
		advancedGermplasm.getNames().add(normalAdvancingNameOfChild);

		// Setup parent child relationship mock between the two via gpid2
		Mockito.when(this.germplasmDAO.getById(advancedGermplasm.getGpid2())).thenReturn(advancedGermplasmSource);

		// Invoke the service
		this.germplasmGroupingService.copyParentalSelectionHistoryAtFixation(advancedGermplasm);

		// Expect that the advanced germplasm now has two names (SELHISFIX name
		// for parent gets added)
		Assert.assertEquals("Advanced germplasm should have one additional name inherited from source (parent).", 2,
				advancedGermplasm.getNames().size());
		Assert.assertEquals("Normal advancing name should remain preferred", GermplasmGroupingServiceImplTest.PREFERRED_CODE,
				normalAdvancingNameOfChild.getNstat());
	}

	@Test
	public void testCopyCodedNames() {

		// Setup source germplasm which has coded name
		final Germplasm advancedGermplasmSource = new Germplasm();
		advancedGermplasmSource.setGid(2);

		final Name code1 = NameTestDataInitializer
				.createName(12, GermplasmGroupingServiceImplTest.NON_PREFERRED_CODE, "CODE1", this.codedName1.getFldno());
		advancedGermplasmSource.getNames().add(code1);

		// Setup advanced germplasm with a name given on advancing
		final Germplasm advancedGermplasm = new Germplasm();
		advancedGermplasm.setGid(3);
		advancedGermplasm.setGpid1(1);
		advancedGermplasm.setGpid2(advancedGermplasm.getGpid2());

		final Name normalAdvancingNameOfChild =
				NameTestDataInitializer.createName(22, GermplasmGroupingServiceImplTest.PREFERRED_CODE, "CML-1-1-1", 5);
		advancedGermplasm.getNames().add(normalAdvancingNameOfChild);

		// Invoke the service
		this.germplasmGroupingService.copyCodedNames(advancedGermplasm, advancedGermplasmSource);

		// Expect that the advanced germplasm now has two names (SELHISFIX name
		// for parent gets added)
		Assert.assertEquals("Advanced germplasm should have one additional name inherited from source (parent).", 2,
				advancedGermplasm.getNames().size());
		Assert.assertEquals("Normal advancing name should become non-preferred", GermplasmGroupingServiceImplTest.NON_PREFERRED_CODE,
				normalAdvancingNameOfChild.getNstat());
	}

	@Test
	public void testCopyCodedNamesPriorityForSettingPreferredName() {

		// Setup source germplasm which has coded name
		final Germplasm advancedGermplasmSource = new Germplasm();
		advancedGermplasmSource.setGid(2);

		final Name code1 = NameTestDataInitializer
				.createName(12, GermplasmGroupingServiceImplTest.NON_PREFERRED_CODE, "CODE1", this.codedName1.getFldno());
		advancedGermplasmSource.getNames().add(code1);

		final Name code2 = NameTestDataInitializer
				.createName(12, GermplasmGroupingServiceImplTest.NON_PREFERRED_CODE, "CODE2", this.codedName2.getFldno());
		advancedGermplasmSource.getNames().add(code2);

		final Name code3 = NameTestDataInitializer
				.createName(12, GermplasmGroupingServiceImplTest.NON_PREFERRED_CODE, "CODE3", this.codedName3.getFldno());
		advancedGermplasmSource.getNames().add(code3);

		// Setup advanced germplasm with a name given on advancing
		final Germplasm advancedGermplasm = new Germplasm();
		advancedGermplasm.setGid(3);
		advancedGermplasm.setGpid1(1);
		advancedGermplasm.setGpid2(advancedGermplasm.getGpid2());

		final Name normalAdvancingNameOfChild =
				NameTestDataInitializer.createName(22, GermplasmGroupingServiceImplTest.PREFERRED_CODE, "CML-1-1-1", 5);
		advancedGermplasm.getNames().add(normalAdvancingNameOfChild);

		// Invoke the service
		this.germplasmGroupingService.copyCodedNames(advancedGermplasm, advancedGermplasmSource);

		// Expect that the advanced germplasm now has two names (SELHISFIX name
		// for parent gets added)
		Assert.assertEquals("Advanced germplasm should have one additional name inherited from source (parent).", 4,
				advancedGermplasm.getNames().size());
		for (final Name name : advancedGermplasm.getNames()) {
			if (name.getTypeId().equals(this.codedName3.getFldno())) {
				Assert.assertEquals("Name with fldno " + this.codedName3.getFldno() + " should be set as preferred name",
						GermplasmGroupingServiceImplTest.PREFERRED_CODE, name.getNstat());
			} else {
				Assert.assertEquals("Other inherited names should be non-preferred", GermplasmGroupingServiceImplTest.NON_PREFERRED_CODE,
						name.getNstat());
			}
		}
	}

	@Test
	public void testGetDescendantGroupMembersGids() {
		final Integer mgid = 11;
		final Integer rootGid = 13;
		final Germplasm derivative = new Germplasm();
		derivative.setGid(22);
		derivative.setMgid(mgid);
		Mockito.when(this.germplasmDAO.getDescendants(rootGid, 'D')).thenReturn(Collections.singletonList(derivative));

		final Germplasm maintenance = new Germplasm();
		maintenance.setGid(25);
		maintenance.setMgid(0);
		Mockito.when(this.germplasmDAO.getDescendants(rootGid, 'M')).thenReturn(Collections.singletonList(maintenance));

		final List<Integer> descendantGroupMembers =  this.germplasmGroupingService.getDescendantGroupMembersGids(rootGid, mgid);
		Assert.assertEquals(1, descendantGroupMembers.size());
		Assert.assertTrue(descendantGroupMembers.contains(derivative.getGid()));
	}

	private Map<Integer, Integer> getGermplasmIdMethodIdMap(final List<Germplasm> germplasm) {
		return germplasm.stream().collect(Collectors.toMap(Germplasm::getGid, g -> g.getMethod().getMid()));
	}

}
