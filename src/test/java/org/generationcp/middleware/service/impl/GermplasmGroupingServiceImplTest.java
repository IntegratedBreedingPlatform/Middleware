
package org.generationcp.middleware.service.impl;

import java.util.Set;

import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.MethodDAO;
import org.generationcp.middleware.dao.UserDefinedFieldDAO;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class GermplasmGroupingServiceImplTest {

	@Mock
	private GermplasmDAO germplasmDAO;

	@Mock
	private MethodDAO methodDAO;

	@Mock
	private UserDefinedFieldDAO userDefinedFieldDAO;

	@Mock
	private GermplasmDataManager germplasmDataManager;

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

	@Before
	public void beforeEachTest() {
		MockitoAnnotations.initMocks(this);

		this.selectionHistoryNameCode = new UserDefinedField(111);
		this.selectionHistoryNameCode.setFtable("NAMES");
		this.selectionHistoryNameCode.setFtype("NAME");
		this.selectionHistoryNameCode.setFcode(GermplasmGroupingServiceImpl.SELECTION_HISTORY_NAME_CODE);
		Mockito.when(
				this.userDefinedFieldDAO.getByTableTypeAndCode("NAMES", "NAME", GermplasmGroupingServiceImpl.SELECTION_HISTORY_NAME_CODE))
				.thenReturn(this.selectionHistoryNameCode);
		
		this.selectionHistoryNameCode.setFcode(GermplasmGroupingServiceImpl.SELECTION_HISTORY_NAME_CODE_FOR_CROSS);
		Mockito.when(
				this.userDefinedFieldDAO.getByTableTypeAndCode("NAMES", "NAME", GermplasmGroupingServiceImpl.SELECTION_HISTORY_NAME_CODE_FOR_CROSS))
				.thenReturn(this.selectionHistoryNameCode);

		this.selHisFixNameCode = new UserDefinedField(222);
		this.selHisFixNameCode.setFtable("NAMES");
		this.selHisFixNameCode.setFtype("NAME");
		this.selHisFixNameCode.setFcode(GermplasmGroupingServiceImpl.SELECTION_HISTORY_AT_FIXATION_NAME_CODE);
		Mockito.when(this.userDefinedFieldDAO.getByTableTypeAndCode("NAMES", "NAME",
				GermplasmGroupingServiceImpl.SELECTION_HISTORY_AT_FIXATION_NAME_CODE)).thenReturn(this.selHisFixNameCode);
		
		this.codedName1 = new UserDefinedField(600000007);
		this.codedName1.setFtable("NAMES");
		this.codedName1.setFtype("NAME");
		this.codedName1.setFcode(GermplasmGroupingServiceImpl.CODED_NAME_1);
		Mockito.when(this.userDefinedFieldDAO.getByTableTypeAndCode("NAMES", "NAME",
				GermplasmGroupingServiceImpl.CODED_NAME_1)).thenReturn(this.codedName1);
		
		this.codedName2 = new UserDefinedField(600000008);
		this.codedName2.setFtable("NAMES");
		this.codedName2.setFtype("NAME");
		this.codedName2.setFcode(GermplasmGroupingServiceImpl.CODED_NAME_2);
		Mockito.when(this.userDefinedFieldDAO.getByTableTypeAndCode("NAMES", "NAME",
				GermplasmGroupingServiceImpl.CODED_NAME_2)).thenReturn(this.codedName2);
		
		this.codedName3 = new UserDefinedField(600000009);
		this.codedName3.setFtable("NAMES");
		this.codedName3.setFtype("NAME");
		this.codedName3.setFcode(GermplasmGroupingServiceImpl.CODED_NAME_3);
		Mockito.when(this.userDefinedFieldDAO.getByTableTypeAndCode("NAMES", "NAME",
				GermplasmGroupingServiceImpl.CODED_NAME_3)).thenReturn(this.codedName3);

		this.selectionHistoryNameParent = new Name();
		this.selectionHistoryNameParent.setNstat(1);
		this.selectionHistoryNameParent.setNval("SelectionHistory");
		this.selectionHistoryNameParent.setTypeId(this.selectionHistoryNameCode.getFldno());

		this.selectionHistoryNameChild1 = new Name();
		this.selectionHistoryNameChild1.setNstat(1);
		this.selectionHistoryNameChild1.setNval("SelectionHistoryChild1");
		this.selectionHistoryNameChild1.setTypeId(this.selectionHistoryNameCode.getFldno());

		this.selectionHistoryNameChild2 = new Name();
		this.selectionHistoryNameChild2.setNstat(1);
		this.selectionHistoryNameChild2.setNval("SelectionHistoryChild2");
		this.selectionHistoryNameChild2.setTypeId(this.selectionHistoryNameCode.getFldno());

		this.germplasmGroupingService = new GermplasmGroupingServiceImpl(this.germplasmDAO, this.methodDAO, this.userDefinedFieldDAO,
				germplasmDataManager, "maize");
	}

	/**
	 * MGID not present. No descendants.
	 */
	@Test
	public void testMarkFixedCase1() {
		Germplasm germplasmToFix = new Germplasm(1);
		germplasmToFix.getNames().add(this.selectionHistoryNameParent);

		this.germplasmGroupingService.markFixed(germplasmToFix, false, false);

		Assert.assertEquals("Expecting founder/parent mgid to be set the same as gid.", germplasmToFix.getGid(), germplasmToFix.getMgid());
		Assert.assertEquals("Existing selection history should become non-preferred name.", new Integer(0),
				this.selectionHistoryNameParent.getNstat());
		Assert.assertEquals("Selection history at fixation should be added as a new name.", 2, germplasmToFix.getNames().size());
		Assert.assertEquals("Selection history at fixation should be added as a preferred name.", new Integer(1),
				germplasmToFix.getNames().get(1).getNstat());
		Assert.assertEquals("Selection history at fixation should be added as with natype = SELHISFIX code.",
				this.selHisFixNameCode.getFldno(), germplasmToFix.getNames().get(1).getTypeId());

		Mockito.verify(this.germplasmDAO, Mockito.times(1)).save(Mockito.any(Germplasm.class));
	}

	/**
	 * MGID not present. Has descendants. Include descendants in group. Dont preserve existing group.
	 */
	@Test
	public void testMarkFixedCase2() {
		Germplasm germplasmToFix = new Germplasm(1);
		germplasmToFix.getNames().add(this.selectionHistoryNameParent);

		Germplasm child1 = new Germplasm(2);
		child1.setMgid(222);
		child1.getNames().add(this.selectionHistoryNameChild1);

		Germplasm child2 = new Germplasm(3);
		child2.setMgid(333);
		child2.getNames().add(this.selectionHistoryNameChild2);

		Mockito.when(this.germplasmDAO.getAllChildren(germplasmToFix.getGid())).thenReturn(Lists.newArrayList(child1, child2));

		this.germplasmGroupingService.markFixed(germplasmToFix, true, false);

		Assert.assertEquals("Expecting founder/parent mgid to be set the same as gid.", germplasmToFix.getGid(), germplasmToFix.getMgid());
		Assert.assertEquals("Expecting child1 mgid to be set the same as founder/parent gid.", germplasmToFix.getGid(), child1.getMgid());
		Assert.assertEquals("Expecting child2 mgid to be set the same as founder/parent gid.", germplasmToFix.getGid(), child2.getMgid());

		// Parent selection history name must be copied as selhisfix and as preferred name.
		Assert.assertEquals("Existing selection history should become non-preferred name for parent.", new Integer(0),
				this.selectionHistoryNameParent.getNstat());
		Assert.assertEquals("Selection history at fixation should be added as a new name for parent.", 2, germplasmToFix.getNames().size());
		Assert.assertEquals("Selection history at fixation should be added as a preferred name for parent.", new Integer(1),
				germplasmToFix.getNames().get(1).getNstat());
		Assert.assertEquals("Selection history at fixation should be added as with natype = SELHISFIX code for parent.",
				this.selHisFixNameCode.getFldno(), germplasmToFix.getNames().get(1).getTypeId());

		// Child1 selection history name must be copied as selhisfix and as preferred name.
		Assert.assertEquals("Existing selection history should become non-preferred name for child1.", new Integer(0),
				this.selectionHistoryNameChild1.getNstat());
		Assert.assertEquals("Selection history at fixation should be added as a new name for child1.", 2, child1.getNames().size());
		Assert.assertEquals("Selection history at fixation should be added as a preferred name for child1.", new Integer(1),
				child1.getNames().get(1).getNstat());
		Assert.assertEquals("Selection history at fixation should be added as with natype = SELHISFIX code for parent.",
				this.selHisFixNameCode.getFldno(), child1.getNames().get(1).getTypeId());

		// Child2 selection history name must be copied as selhisfix and as preferred name.
		Assert.assertEquals("Existing selection history should become non-preferred name for child2.", new Integer(0),
				this.selectionHistoryNameChild2.getNstat());
		Assert.assertEquals("Selection history at fixation should be added as a new name for child2.", 2, child2.getNames().size());
		Assert.assertEquals("Selection history at fixation should be added as a preferred name for child2.", new Integer(1),
				child2.getNames().get(1).getNstat());
		Assert.assertEquals("Selection history at fixation should be added as with natype = SELHISFIX code for child2.",
				this.selHisFixNameCode.getFldno(), child2.getNames().get(1).getTypeId());

		Mockito.verify(this.germplasmDAO, Mockito.times(3)).save(Mockito.any(Germplasm.class));
	}

	/**
	 * MGID not present. Has descendants. Dont include descendants in group.
	 */
	@Test
	public void testMarkFixedCase3() {
		Germplasm germplasmToFix = new Germplasm(1);

		Germplasm child1 = new Germplasm(2);
		child1.setMgid(222);

		Germplasm child2 = new Germplasm(3);
		child2.setMgid(333);

		Mockito.when(this.germplasmDAO.getAllChildren(germplasmToFix.getGid())).thenReturn(Lists.newArrayList(child1, child2));

		this.germplasmGroupingService.markFixed(germplasmToFix, false, false);

		Assert.assertEquals("Expecting founder/parent mgid to be set the same as gid.", germplasmToFix.getGid(), germplasmToFix.getMgid());
		Assert.assertEquals("Expecting child1 mgid to remain the same as before.", new Integer(222), child1.getMgid());
		Assert.assertEquals("Expecting child2 mgid to remain the same as before.", new Integer(333), child2.getMgid());

		Mockito.verify(this.germplasmDAO, Mockito.times(1)).save(Mockito.any(Germplasm.class));
	}

	/**
	 * MGID is present. No descendants. Preserve existing mgid. Existing mgid is non-zero.
	 */
	@Test
	public void testMarkFixedCase4_1() {
		Integer expectedMGID = new Integer(111);

		Germplasm germplasmToFix = new Germplasm(1);
		germplasmToFix.setMgid(expectedMGID);

		this.germplasmGroupingService.markFixed(germplasmToFix, false, true);

		Assert.assertEquals("Expecting founder/parent mgid to be preserved.", expectedMGID, germplasmToFix.getMgid());

		Mockito.verify(this.germplasmDAO, Mockito.never()).save(Mockito.any(Germplasm.class));
	}

	/**
	 * MGID is present. No descendants. Preserve existing mgid. Existing mgid is zero.
	 */
	@Test
	public void testMarkFixedCase4_2() {
		Germplasm germplasmToFix = new Germplasm(123);
		germplasmToFix.setMgid(0);

		this.germplasmGroupingService.markFixed(germplasmToFix, false, true);

		Assert.assertEquals("Expecting founder/parent mgid to be set to be the same as gid.", germplasmToFix.getGid(),
				germplasmToFix.getMgid());

		Mockito.verify(this.germplasmDAO, Mockito.times(1)).save(Mockito.any(Germplasm.class));
	}

	/**
	 * MGID is present. Has descendants. Dont preserve existing mgid. Include descendants in group.
	 */
	@Test
	public void testMarkFixedCase5() {
		Germplasm germplasmToFix = new Germplasm(1);
		germplasmToFix.setMgid(111);

		Germplasm child1 = new Germplasm(2);
		child1.setMgid(222);

		Germplasm child2 = new Germplasm(3);
		child2.setMgid(333);

		Mockito.when(this.germplasmDAO.getAllChildren(germplasmToFix.getGid())).thenReturn(Lists.newArrayList(child1, child2));

		this.germplasmGroupingService.markFixed(germplasmToFix, true, false);

		Assert.assertEquals("Expecting founder/parent mgid to be set the same as gid.", germplasmToFix.getGid(), germplasmToFix.getMgid());
		Assert.assertEquals("Expecting child1 mgid to be set the same as founder/parent gid.", germplasmToFix.getGid(), child1.getMgid());
		Assert.assertEquals("Expecting child2 mgid to be set the same as founder/parent gid.", germplasmToFix.getGid(), child2.getMgid());

		Mockito.verify(this.germplasmDAO, Mockito.times(3)).save(Mockito.any(Germplasm.class));
	}

	/**
	 * Method is generative for parent.
	 */
	@Test
	public void testMarkFixedCase6() {
		Germplasm germplasmToFix = new Germplasm(1);
		Integer generativeMethodId = 123;
		germplasmToFix.setMethodId(generativeMethodId);
		Integer expectedParentMGID = 111;
		germplasmToFix.setMgid(expectedParentMGID);

		Method method = new Method(generativeMethodId);
		method.setMtype("GEN");
		Mockito.when(this.methodDAO.getById(generativeMethodId)).thenReturn(method);

		Germplasm child1 = new Germplasm(2);
		child1.setMgid(222);

		Germplasm child2 = new Germplasm(3);
		child2.setMgid(333);

		Mockito.when(this.germplasmDAO.getAllChildren(germplasmToFix.getGid())).thenReturn(Lists.newArrayList(child1, child2));

		this.germplasmGroupingService.markFixed(germplasmToFix, true, false);

		Assert.assertEquals("Expecting no mgid changes when method is generative.", expectedParentMGID, germplasmToFix.getMgid());

		Mockito.verify(this.germplasmDAO, Mockito.never()).save(Mockito.any(Germplasm.class));
	}

	/**
	 * Method is generative for one of the descendant.
	 */
	@Test
	public void testMarkFixedCase7() {
		Germplasm germplasmToFix = new Germplasm(1);

		Integer generativeMethodId = 123;
		Method method = new Method(generativeMethodId);
		method.setMtype("GEN");
		Mockito.when(this.methodDAO.getById(generativeMethodId)).thenReturn(method);

		Germplasm child1 = new Germplasm(2);
		child1.setMgid(222);

		Germplasm child2 = new Germplasm(3);
		Integer expectedChild2MGID = 333;
		child2.setMgid(expectedChild2MGID);
		child2.setMethodId(generativeMethodId);

		Mockito.when(this.germplasmDAO.getAllChildren(germplasmToFix.getGid())).thenReturn(Lists.newArrayList(child1, child2));

		this.germplasmGroupingService.markFixed(germplasmToFix, true, false);

		Assert.assertEquals("Expecting founder/parent mgid to be set the same as gid.", germplasmToFix.getGid(), germplasmToFix.getMgid());
		Assert.assertEquals("Expecting child1 mgid to be set the same as founder/parent gid.", germplasmToFix.getGid(), child1.getMgid());
		Assert.assertEquals("Expecting child2 mgid to remain unchanged as method is generative.", expectedChild2MGID, child2.getMgid());

		Mockito.verify(this.germplasmDAO, Mockito.times(2)).save(Mockito.any(Germplasm.class));
	}

	/**
	 * First basic scenario with data available to go through all main logic steps. Cross with hybrid method, both parents containing mgid,
	 * previous crosses existing with mgid.
	 */
	@Test
	public void testProcessGroupInheritanceForCrosses1() {

		Integer crossGid1 = 1;
		Integer crossGid1Parent1 = 11, crossGid1Parent1MGID = 111;
		Integer crossGid1Parent2 = 12, crossGid1Parent2MGID = 112;
		Integer hybridMethodId = 416;

		Germplasm crossGermplasm1 = new Germplasm(crossGid1);
		crossGermplasm1.setMethodId(hybridMethodId);
		crossGermplasm1.setGpid1(crossGid1Parent1);
		crossGermplasm1.setGpid2(crossGid1Parent2);
		crossGermplasm1.setMgid(0);

		Germplasm crossGermplasm1Parent1 = new Germplasm(crossGid1Parent1);
		crossGermplasm1Parent1.setMgid(crossGid1Parent1MGID);

		Germplasm crossGermplasm1Parent2 = new Germplasm(crossGid1Parent2);
		crossGermplasm1Parent2.setMgid(crossGid1Parent2MGID);

		// Setup previous cross with MGID.
		Germplasm previousCross = new Germplasm(123);
		Integer previousCrossMGID = 456;
		previousCross.setMgid(previousCrossMGID);
		Mockito.when(this.germplasmDAO.getPreviousCrossesBetweenParentGroups(crossGermplasm1))
				.thenReturn(Lists.newArrayList(previousCross));

		// Just to test, create another cross with non-hybrid method. Expect this to not be processed.
		Integer crossGid2 = 2;
		Integer nonHybridMethodId = 416;
		Germplasm crossGermplasm2 = new Germplasm(crossGid2);
		crossGermplasm1.setMethodId(nonHybridMethodId);
		Mockito.when(this.germplasmDataManager.getGermplasmWithAllNamesAndAncestry(ImmutableSet.of(crossGid1, crossGid2), 2))
				.thenReturn(ImmutableList.of(crossGermplasm1, crossGermplasm1Parent1, crossGermplasm1Parent2, crossGermplasm2));
		this.germplasmGroupingService.processGroupInheritanceForCrosses(Lists.newArrayList(crossGid1, crossGid2), false, HYBRID_METHODS);

		Assert.assertEquals("Expected new cross to be assigned MGID from previous cross.", previousCrossMGID, crossGermplasm1.getMgid());

		// Previous crosses should be queried once.
		Mockito.verify(this.germplasmDAO, Mockito.times(1)).getPreviousCrossesBetweenParentGroups(crossGermplasm1);
		// One Germplasm record should be saved out of the two that are passed.
		Mockito.verify(this.germplasmDAO, Mockito.times(1)).save(Mockito.any(Germplasm.class));
	}

	/**
	 * Basic scenario with data available to go through all main logic steps. Cross with hybrid method, both parents containing mgid, no
	 * previous crosses existing.
	 */
	@Test
	public void testProcessGroupInheritanceForCrosses2() {

		Integer crossGid1 = 1;
		Integer crossGid1Parent1 = 11, crossGid1Parent1MGID = 111;
		Integer crossGid1Parent2 = 12, crossGid1Parent2MGID = 112;
		Integer hybridMethodId = 416;

		Germplasm crossGermplasm1 = new Germplasm(crossGid1);
		crossGermplasm1.setMethodId(hybridMethodId);
		crossGermplasm1.setGpid1(crossGid1Parent1);
		crossGermplasm1.setGpid2(crossGid1Parent2);
		crossGermplasm1.setMgid(0);

		Germplasm crossGermplasm1Parent1 = new Germplasm(crossGid1Parent1);
		crossGermplasm1Parent1.setMgid(crossGid1Parent1MGID);

		Germplasm crossGermplasm1Parent2 = new Germplasm(crossGid1Parent2);
		crossGermplasm1Parent2.setMgid(crossGid1Parent2MGID);

		// Just to test, create another cross with non-hybrid method. Expect this to not be processed.
		Integer crossGid2 = 2;
		Integer nonHybridMethodId = 416;
		Germplasm crossGermplasm2 = new Germplasm(crossGid2);
		crossGermplasm1.setMethodId(nonHybridMethodId);
		Mockito.when(this.germplasmDAO.getById(crossGid2)).thenReturn(crossGermplasm2);
		Mockito.when(this.germplasmDataManager.getGermplasmWithAllNamesAndAncestry(ImmutableSet.of(crossGid1, crossGid2), 2))
		.thenReturn(ImmutableList.of(crossGermplasm1, crossGermplasm1Parent1, crossGermplasm1Parent2, crossGermplasm2));

		this.germplasmGroupingService.processGroupInheritanceForCrosses(Lists.newArrayList(crossGid1, crossGid2), false, HYBRID_METHODS);

		Assert.assertEquals("Expected new cross to be assigned MGID = GID of the cross when no previous crosses exist.", crossGid1,
				crossGermplasm1.getMgid());
		// Previous crosses should be queried once.
		Mockito.verify(this.germplasmDAO, Mockito.times(1)).getPreviousCrossesBetweenParentGroups(crossGermplasm1);
		// No selection history should be copied.
		// One Germplasm record should be saved out of the two that are passed.
		Mockito.verify(this.germplasmDAO, Mockito.times(1)).save(Mockito.any(Germplasm.class));
	}

	/**
	 * Edge scenario method is hybrid but both parents don't have MGID.
	 */
	@Test
	public void testProcessGroupInheritanceForCrosses3() {

		Integer crossGid1 = 1;
		Integer crossGid1Parent1 = 11;
		Integer crossGid1Parent2 = 12;
		Integer hybridMethodId = 416;

		Germplasm crossGermplasm1 = new Germplasm(crossGid1);
		crossGermplasm1.setMethodId(hybridMethodId);
		crossGermplasm1.setGpid1(crossGid1Parent1);
		crossGermplasm1.setGpid2(crossGid1Parent2);
		crossGermplasm1.setMgid(0);

		Germplasm crossGermplasm1Parent1 = new Germplasm(crossGid1Parent1);
		crossGermplasm1Parent1.setMgid(null);

		Germplasm crossGermplasm1Parent2 = new Germplasm(crossGid1Parent2);
		crossGermplasm1Parent2.setMgid(null);
		Mockito.when(this.germplasmDataManager.getGermplasmWithAllNamesAndAncestry(ImmutableSet.of(crossGid1), 2))
		.thenReturn(ImmutableList.of(crossGermplasm1, crossGermplasm1Parent1, crossGermplasm1Parent2));

		this.germplasmGroupingService.processGroupInheritanceForCrosses(Lists.newArrayList(crossGid1), false, HYBRID_METHODS);

		Assert.assertEquals("Expected no MGID change.", new Integer(0), crossGermplasm1.getMgid());
		// Previous crosses should never be queried.
		Mockito.verify(this.germplasmDAO, Mockito.never()).getPreviousCrossesBetweenParentGroups(crossGermplasm1);
		// No selection history should be copied.
		// No Germplasm record should be saved.
		Mockito.verify(this.germplasmDAO, Mockito.never()).save(Mockito.any(Germplasm.class));
	}

	/**
	 * Edge scenario method is not hybrid.
	 */
	@Test
	public void testProcessGroupInheritanceForCrosses4() {

		Integer crossGid1 = 1;
		Integer crossGid1Parent1 = 11;
		Integer crossGid1Parent2 = 12;
		Integer nonHybridMethodId = -999;

		Germplasm crossGermplasm1 = new Germplasm(crossGid1);
		crossGermplasm1.setMethodId(nonHybridMethodId);
		crossGermplasm1.setGpid1(crossGid1Parent1);
		crossGermplasm1.setGpid2(crossGid1Parent2);
		crossGermplasm1.setMgid(0);
		Mockito.when(this.germplasmDataManager.getGermplasmWithAllNamesAndAncestry(ImmutableSet.of(crossGid1), 2))
		.thenReturn(ImmutableList.of(crossGermplasm1));

		this.germplasmGroupingService.processGroupInheritanceForCrosses(Lists.newArrayList(crossGid1), false, HYBRID_METHODS);

		Assert.assertEquals("Expected no MGID change.", new Integer(0), crossGermplasm1.getMgid());
		// Previous crosses should never be queried.
		Mockito.verify(this.germplasmDAO, Mockito.never()).getPreviousCrossesBetweenParentGroups(crossGermplasm1);
		// No selection history should be copied.
		// No Germplasm record should be saved.
		Mockito.verify(this.germplasmDAO, Mockito.never()).save(Mockito.any(Germplasm.class));
	}

	/**
	 * Cross with hybrid method, both parents containing mgid, previous crosses existing but none with mgid. Current cross should start a
	 * new group.
	 */
	@Test
	public void testProcessGroupInheritanceForCrosses5() {

		Integer crossGid1 = 1;
		Integer crossGid1Parent1 = 11, crossGid1Parent1MGID = 111;
		Integer crossGid1Parent2 = 12, crossGid1Parent2MGID = 112;
		Integer hybridMethodId = 416;

		Germplasm crossGermplasm1 = new Germplasm(crossGid1);
		crossGermplasm1.setMethodId(hybridMethodId);
		crossGermplasm1.setGpid1(crossGid1Parent1);
		crossGermplasm1.setGpid2(crossGid1Parent2);
		crossGermplasm1.setMgid(0);
		Mockito.when(this.germplasmDAO.getById(crossGid1)).thenReturn(crossGermplasm1);

		Germplasm crossGermplasm1Parent1 = new Germplasm(crossGid1Parent1);
		crossGermplasm1Parent1.setMgid(crossGid1Parent1MGID);
		Mockito.when(this.germplasmDAO.getById(crossGid1Parent1)).thenReturn(crossGermplasm1Parent1);

		Germplasm crossGermplasm1Parent2 = new Germplasm(crossGid1Parent2);
		crossGermplasm1Parent2.setMgid(crossGid1Parent2MGID);
		Mockito.when(this.germplasmDAO.getById(crossGid1Parent2)).thenReturn(crossGermplasm1Parent2);

		// Setup previous cross with MGID.
		Germplasm previousCross = new Germplasm(123);
		// No mgid on previous cross
		Mockito.when(this.germplasmDAO.getPreviousCrossesBetweenParentGroups(crossGermplasm1))
				.thenReturn(Lists.newArrayList(previousCross));
		Mockito.when(this.germplasmDataManager.getGermplasmWithAllNamesAndAncestry(ImmutableSet.of(crossGid1), 2))
		.thenReturn(ImmutableList.of(crossGermplasm1, crossGermplasm1Parent1, crossGermplasm1Parent2));

		this.germplasmGroupingService.processGroupInheritanceForCrosses(Lists.newArrayList(crossGid1), true, HYBRID_METHODS);

		Assert.assertEquals("Expected new cross to be assigned MGID = GID as none of the previous crosses have MGID.", crossGid1,
				crossGermplasm1.getMgid());
		Assert.assertEquals("Expected previous cross to be assigned same MGID as the current cross.", crossGermplasm1.getMgid(),
				previousCross.getMgid());

		// Previous crosses should be queried once.
		Mockito.verify(this.germplasmDAO, Mockito.times(1)).getPreviousCrossesBetweenParentGroups(crossGermplasm1);
		// One Germplasm record should be saved out of the two that are passed.
		// One other save should occur for saving mgid on previous cross.
		Mockito.verify(this.germplasmDAO, Mockito.times(2)).save(Mockito.any(Germplasm.class));
	}

	@Test
	public void testGetSelectionHistory() {
		Germplasm germplasm = new Germplasm();
		// Germplasm has no name yet, expect to return null
		Assert.assertNull(this.germplasmGroupingService.getSelectionHistory(germplasm,GermplasmGroupingServiceImpl.SELECTION_HISTORY_NAME_CODE));

		// Add a matching name
		Name selHisNameExpected = new Name(1);
		selHisNameExpected.setTypeId(this.selectionHistoryNameCode.getFldno());
		germplasm.getNames().add(selHisNameExpected);

		Name selectionHistoryName = this.germplasmGroupingService.getSelectionHistory(germplasm, GermplasmGroupingServiceImpl.SELECTION_HISTORY_NAME_CODE);
		Assert.assertEquals(selHisNameExpected, selectionHistoryName);
	}

	@Test
	public void testGetSelectionHistoryAtFixation() {
		Germplasm germplasm = new Germplasm();
		// Germplasm has no name yet, expect to return null
		Assert.assertNull(this.germplasmGroupingService.getSelectionHistory(germplasm, GermplasmGroupingServiceImpl.SELECTION_HISTORY_AT_FIXATION_NAME_CODE));

		// Add a matching name
		Name selHisFixNameExpected = new Name(1);
		selHisFixNameExpected.setTypeId(this.selHisFixNameCode.getFldno());
		germplasm.getNames().add(selHisFixNameExpected);

		Name selHisFixNameActual = this.germplasmGroupingService.getSelectionHistory(germplasm, GermplasmGroupingServiceImpl.SELECTION_HISTORY_AT_FIXATION_NAME_CODE);
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

		Mockito.when(this.userDefinedFieldDAO.getByTableTypeAndCode("NAMES", "NAME",
				GermplasmGroupingServiceImpl.SELECTION_HISTORY_AT_FIXATION_NAME_CODE)).thenReturn(null);

		this.germplasmGroupingService.getSelectionHistoryNameType(GermplasmGroupingServiceImpl.SELECTION_HISTORY_AT_FIXATION_NAME_CODE);
		Assert.fail("When " + GermplasmGroupingServiceImpl.SELECTION_HISTORY_AT_FIXATION_NAME_CODE
				+ " name type is not setup in UDFLD table, IllegalStateException is expected which did not happen.");
	}

	@Test
	public void testCopyParentalSelectionHistoryAtFixation() {

		// Setup source germplasm which has SELHISFIX name
		Germplasm advancedGermplasmSource = new Germplasm(2);

		Name selHisFixNameOfParent = new Name(11);
		selHisFixNameOfParent.setNstat(1);
		selHisFixNameOfParent.setNval("CML");
		selHisFixNameOfParent.setTypeId(this.selHisFixNameCode.getFldno());
		advancedGermplasmSource.getNames().add(selHisFixNameOfParent);

		// Setup advanced germplasm with a name given on advancing
		Germplasm advancedGermplasm = new Germplasm(3);
		advancedGermplasm.setGpid1(1);
		advancedGermplasm.setGpid2(advancedGermplasm.getGpid2());

		Name normalAdvancingNameOfChild = new Name(22);
		normalAdvancingNameOfChild.setNstat(1); // Set as preferred.
		normalAdvancingNameOfChild.setNval("CML-1-1-1");
		normalAdvancingNameOfChild.setTypeId(5);
		advancedGermplasm.getNames().add(normalAdvancingNameOfChild);

		// Setup parent child relationship mock between the two via gpid2
		Mockito.when(this.germplasmDAO.getById(advancedGermplasm.getGpid2())).thenReturn(advancedGermplasmSource);

		// Invoke the service
		this.germplasmGroupingService.copyParentalSelectionHistoryAtFixation(advancedGermplasm);

		// Expect that the advanced germplasm now has two names (SELHISFIX name for parent gets added)
		Assert.assertEquals("Advanced germplasm should have one additional name inherited from source (parent).", 2,
				advancedGermplasm.getNames().size());
		Assert.assertEquals("Normal advancing name should become non-preferred", new Integer(0), normalAdvancingNameOfChild.getNstat());
	}
	
	@Test
	public void testCopyCodedNames() {

		// Setup source germplasm which has coded name
		Germplasm advancedGermplasmSource = new Germplasm(2);

		Name code1 = new Name(12);
		code1.setNstat(0);
		code1.setNval("CODE1");
		code1.setTypeId(this.codedName1.getFldno());
		advancedGermplasmSource.getNames().add(code1);

		// Setup advanced germplasm with a name given on advancing
		Germplasm advancedGermplasm = new Germplasm(3);
		advancedGermplasm.setGpid1(1);
		advancedGermplasm.setGpid2(advancedGermplasm.getGpid2());

		Name normalAdvancingNameOfChild = new Name(22);
		normalAdvancingNameOfChild.setNstat(1); // Set as preferred.
		normalAdvancingNameOfChild.setNval("CML-1-1-1");
		normalAdvancingNameOfChild.setTypeId(5);
		advancedGermplasm.getNames().add(normalAdvancingNameOfChild);

		// Setup parent child relationship mock between the two via gpid2
		Mockito.when(this.germplasmDAO.getById(advancedGermplasm.getGpid2())).thenReturn(advancedGermplasmSource);

		// Invoke the service
		this.germplasmGroupingService.copyCodedNames(advancedGermplasm);

		// Expect that the advanced germplasm now has two names (SELHISFIX name for parent gets added)
		Assert.assertEquals("Advanced germplasm should have one additional name inherited from source (parent).", 2,
				advancedGermplasm.getNames().size());
		Assert.assertEquals("Normal advancing name should become non-preferred", new Integer(0), normalAdvancingNameOfChild.getNstat());
	}
	
	@Test
	public void testCopyCodedNamesPriorityForSettingPreferredName() {

		// Setup source germplasm which has coded name
		Germplasm advancedGermplasmSource = new Germplasm(2);

		Name code1 = new Name(12);
		code1.setNstat(0);
		code1.setNval("CODE1");
		code1.setTypeId(this.codedName1.getFldno());
		advancedGermplasmSource.getNames().add(code1);
		
		Name code2 = new Name(12);
		code2.setNstat(0);
		code2.setNval("CODE2");
		code2.setTypeId(this.codedName2.getFldno());
		advancedGermplasmSource.getNames().add(code2);
		
		Name code3 = new Name(12);
		code3.setNstat(0);
		code3.setNval("CODE3");
		code3.setTypeId(this.codedName3.getFldno());
		advancedGermplasmSource.getNames().add(code3);

		// Setup advanced germplasm with a name given on advancing
		Germplasm advancedGermplasm = new Germplasm(3);
		advancedGermplasm.setGpid1(1);
		advancedGermplasm.setGpid2(advancedGermplasm.getGpid2());

		Name normalAdvancingNameOfChild = new Name(22);
		normalAdvancingNameOfChild.setNstat(1); // Set as preferred.
		normalAdvancingNameOfChild.setNval("CML-1-1-1");
		normalAdvancingNameOfChild.setTypeId(5);
		advancedGermplasm.getNames().add(normalAdvancingNameOfChild);

		// Setup parent child relationship mock between the two via gpid2
		Mockito.when(this.germplasmDAO.getById(advancedGermplasm.getGpid2())).thenReturn(advancedGermplasmSource);

		// Invoke the service
		this.germplasmGroupingService.copyCodedNames(advancedGermplasm);

		// Expect that the advanced germplasm now has two names (SELHISFIX name for parent gets added)
		Assert.assertEquals("Advanced germplasm should have one additional name inherited from source (parent).", 4,
				advancedGermplasm.getNames().size());
		for(Name name: advancedGermplasm.getNames()){
			if(name.getTypeId().equals(this.codedName3.getFldno())){
				Assert.assertEquals("Name with fldno " + this.codedName3.getFldno() + " should be set as preferred name", new Integer(1), name.getNstat());
			} else{
				Assert.assertEquals("Other inherited names should be non-preferred", new Integer(0), name.getNstat());
			}
		}
	}
}
