
package org.generationcp.middleware.service.impl;

import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.pojos.Germplasm;
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

	private GermplasmGroupingServiceImpl germplasmGroupingService;

	@Before
	public void beforeEachTest() {
		MockitoAnnotations.initMocks(this);
		this.germplasmGroupingService = new GermplasmGroupingServiceImpl(this.germplasmDAO);
	}

	/**
	 * MGID not present. No descendants.
	 */
	@Test
	public void testMarkFixedCase1() {
		Germplasm germplasmToFix = new Germplasm(1);

		this.germplasmGroupingService.markFixed(germplasmToFix, false, false);

		Assert.assertEquals("Expecting founder/parent mgid to be set the same as gid.", germplasmToFix.getGid(), germplasmToFix.getMgid());

		// TODO assert that the selection history is copied with new name type.
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

		// TODO assert that the selection history is copied with new name type.
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

		// TODO assert that the selection history is copied with new name type.
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

		// TODO assert that the selection history is copied with new name type.
	}
}
