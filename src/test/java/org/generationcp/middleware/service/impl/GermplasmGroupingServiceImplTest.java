
package org.generationcp.middleware.service.impl;

import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.MethodDAO;
import org.generationcp.middleware.dao.NameDAO;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;

public class GermplasmGroupingServiceImplTest {

	@Mock
	private GermplasmDAO germplasmDAO;

	@Mock
	private NameDAO nameDAO;

	@Mock
	private MethodDAO methodDAO;

	private GermplasmGroupingServiceImpl germplasmGroupingService;

	@Before
	public void beforeEachTest() {
		MockitoAnnotations.initMocks(this);

		final Name selectionHistoryName = new Name();
		selectionHistoryName.setNval("SelectionHistory");

		Mockito.when(this.nameDAO.getByGIDWithFilters(Mockito.anyInt(), Mockito.anyInt(), Mockito.eq(GermplasmNameType.SELECTION_HISTORY)))
				.thenReturn(
				Lists.newArrayList(selectionHistoryName));

		this.germplasmGroupingService = new GermplasmGroupingServiceImpl(this.germplasmDAO, this.nameDAO, this.methodDAO);
	}

	/**
	 * MGID not present. No descendants.
	 */
	@Test
	public void testMarkFixedCase1() {
		Germplasm germplasmToFix = new Germplasm(1);

		this.germplasmGroupingService.markFixed(germplasmToFix, false, false);

		Assert.assertEquals("Expecting founder/parent mgid to be set the same as gid.", germplasmToFix.getGid(), germplasmToFix.getMgid());

		Mockito.verify(this.nameDAO, Mockito.times(1)).save(Mockito.any(Name.class));
		Mockito.verify(this.germplasmDAO, Mockito.times(1)).save(Mockito.any(Germplasm.class));
	}

	/**
	 * MGID not present. Has descendants. Include descendants in group. Dont preserve existing group.
	 */
	@Test
	public void testMarkFixedCase2() {
		Germplasm germplasmToFix = new Germplasm(1);

		Germplasm child1 = new Germplasm(2);
		child1.setMgid(222);

		Germplasm child2 = new Germplasm(3);
		child2.setMgid(333);

		Mockito.when(this.germplasmDAO.getAllChildren(germplasmToFix.getGid())).thenReturn(Lists.newArrayList(child1, child2));

		this.germplasmGroupingService.markFixed(germplasmToFix, true, false);

		Assert.assertEquals("Expecting founder/parent mgid to be set the same as gid.", germplasmToFix.getGid(), germplasmToFix.getMgid());
		Assert.assertEquals("Expecting child1 mgid to be set the same as founder/parent gid.", germplasmToFix.getGid(), child1.getMgid());
		Assert.assertEquals("Expecting child2 mgid to be set the same as founder/parent gid.", germplasmToFix.getGid(), child2.getMgid());

		Mockito.verify(this.nameDAO, Mockito.times(3)).save(Mockito.any(Name.class));
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

		Mockito.verify(this.nameDAO, Mockito.times(1)).save(Mockito.any(Name.class));
		Mockito.verify(this.germplasmDAO, Mockito.times(1)).save(Mockito.any(Germplasm.class));
	}

	/**
	 * MGID is present. No descendants. Preserve existing mgid.
	 */
	@Test
	public void testMarkFixedCase4() {
		Germplasm germplasmToFix = new Germplasm(1);
		germplasmToFix.setMgid(111);

		this.germplasmGroupingService.markFixed(germplasmToFix, false, true);

		Assert.assertEquals("Expecting founder/parent mgid to be preserved.", new Integer(111), germplasmToFix.getMgid());

		Mockito.verify(this.nameDAO, Mockito.never()).save(Mockito.any(Name.class));
		Mockito.verify(this.germplasmDAO, Mockito.never()).save(Mockito.any(Germplasm.class));
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

		Mockito.verify(this.nameDAO, Mockito.times(3)).save(Mockito.any(Name.class));
		Mockito.verify(this.germplasmDAO, Mockito.times(3)).save(Mockito.any(Germplasm.class));
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
		Mockito.when(this.germplasmDAO.getById(crossGid1)).thenReturn(crossGermplasm1);

		Germplasm crossGermplasm1Parent1 = new Germplasm(crossGid1Parent1);
		crossGermplasm1Parent1.setMgid(crossGid1Parent1MGID);
		Mockito.when(this.germplasmDAO.getById(crossGid1Parent1)).thenReturn(crossGermplasm1Parent1);

		Germplasm crossGermplasm1Parent2 = new Germplasm(crossGid1Parent2);
		crossGermplasm1Parent2.setMgid(crossGid1Parent2MGID);
		Mockito.when(this.germplasmDAO.getById(crossGid1Parent2)).thenReturn(crossGermplasm1Parent2);

		// Setup previous cross with MGID.
		Germplasm previousCross = new Germplasm(123);
		Integer previousCrossMGID = 456;
		previousCross.setMgid(previousCrossMGID);
		Mockito.when(this.germplasmDAO.getPreviousCrosses(crossGid1Parent1, crossGid1Parent2))
				.thenReturn(Lists.newArrayList(previousCross));

		// Just to test, create another cross with non-hybrid method. Expect this to not be processed.
		Integer crossGid2 = 2;
		Integer nonHybridMethodId = 416;
		Germplasm crossGermplasm2 = new Germplasm(crossGid2);
		crossGermplasm1.setMethodId(nonHybridMethodId);
		Mockito.when(this.germplasmDAO.getById(crossGid2)).thenReturn(crossGermplasm2);

		this.germplasmGroupingService.processGroupInheritanceForCrosses(Lists.newArrayList(crossGid1, crossGid2));

		Assert.assertEquals("Expected new cross to be assigned MGID from previous cross.", previousCrossMGID, crossGermplasm1.getMgid());

		// Previous crosses should be queried once.
		Mockito.verify(this.germplasmDAO, Mockito.times(1)).getPreviousCrosses(crossGid1Parent1, crossGid1Parent2);
		// Selection history from previous cross should be copied to the cross once
		Mockito.verify(this.nameDAO, Mockito.times(1)).save(Mockito.any(Name.class));
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
		Mockito.when(this.germplasmDAO.getById(crossGid1)).thenReturn(crossGermplasm1);

		Germplasm crossGermplasm1Parent1 = new Germplasm(crossGid1Parent1);
		crossGermplasm1Parent1.setMgid(crossGid1Parent1MGID);
		Mockito.when(this.germplasmDAO.getById(crossGid1Parent1)).thenReturn(crossGermplasm1Parent1);

		Germplasm crossGermplasm1Parent2 = new Germplasm(crossGid1Parent2);
		crossGermplasm1Parent2.setMgid(crossGid1Parent2MGID);
		Mockito.when(this.germplasmDAO.getById(crossGid1Parent2)).thenReturn(crossGermplasm1Parent2);

		// Just to test, create another cross with non-hybrid method. Expect this to not be processed.
		Integer crossGid2 = 2;
		Integer nonHybridMethodId = 416;
		Germplasm crossGermplasm2 = new Germplasm(crossGid2);
		crossGermplasm1.setMethodId(nonHybridMethodId);
		Mockito.when(this.germplasmDAO.getById(crossGid2)).thenReturn(crossGermplasm2);

		this.germplasmGroupingService.processGroupInheritanceForCrosses(Lists.newArrayList(crossGid1, crossGid2));

		Assert.assertEquals("Expected new cross to be assigned MGID = GID of the cross when no previous crosses exist.", crossGid1,
				crossGermplasm1.getMgid());
		// Previous crosses should be queried once.
		Mockito.verify(this.germplasmDAO, Mockito.times(1)).getPreviousCrosses(crossGid1Parent1, crossGid1Parent2);
		// No selection history should be copied.
		Mockito.verify(this.nameDAO, Mockito.never()).save(Mockito.any(Name.class));
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
		Mockito.when(this.germplasmDAO.getById(crossGid1)).thenReturn(crossGermplasm1);

		Germplasm crossGermplasm1Parent1 = new Germplasm(crossGid1Parent1);
		crossGermplasm1Parent1.setMgid(null);
		Mockito.when(this.germplasmDAO.getById(crossGid1Parent1)).thenReturn(crossGermplasm1Parent1);

		Germplasm crossGermplasm1Parent2 = new Germplasm(crossGid1Parent2);
		crossGermplasm1Parent2.setMgid(null);
		Mockito.when(this.germplasmDAO.getById(crossGid1Parent2)).thenReturn(crossGermplasm1Parent2);


		this.germplasmGroupingService.processGroupInheritanceForCrosses(Lists.newArrayList(crossGid1));

		Assert.assertEquals("Expected no MGID change.", new Integer(0), crossGermplasm1.getMgid());
		// Previous crosses should never be queried.
		Mockito.verify(this.germplasmDAO, Mockito.never()).getPreviousCrosses(crossGid1Parent1, crossGid1Parent2);
		// No selection history should be copied.
		Mockito.verify(this.nameDAO, Mockito.never()).save(Mockito.any(Name.class));
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
		Mockito.when(this.germplasmDAO.getById(crossGid1)).thenReturn(crossGermplasm1);

		this.germplasmGroupingService.processGroupInheritanceForCrosses(Lists.newArrayList(crossGid1));

		Assert.assertEquals("Expected no MGID change.", new Integer(0), crossGermplasm1.getMgid());
		// Previous crosses should never be queried.
		Mockito.verify(this.germplasmDAO, Mockito.never()).getPreviousCrosses(crossGid1Parent1, crossGid1Parent2);
		// No selection history should be copied.
		Mockito.verify(this.nameDAO, Mockito.never()).save(Mockito.any(Name.class));
		// No Germplasm record should be saved.
		Mockito.verify(this.germplasmDAO, Mockito.never()).save(Mockito.any(Germplasm.class));
	}
}
