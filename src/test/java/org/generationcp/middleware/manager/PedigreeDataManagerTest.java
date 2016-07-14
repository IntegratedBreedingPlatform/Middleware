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

import java.util.List;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.PedigreeDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.generationcp.middleware.utils.test.Debug;
import org.generationcp.middleware.utils.test.MockDataUtil;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Ignore("Historic failing test. Disabled temporarily. Developers working in this area please spend some time to fix and remove @Ignore.")
public class PedigreeDataManagerTest extends IntegrationTestBase {

	@Autowired
	private PedigreeDataManager pedigreeManager;

	@Test
	public void testGetGermplasmDescendants() throws Exception {
		Integer gid = Integer.valueOf(1);
		List<Object[]> germplsmList = this.pedigreeManager.getDescendants(gid, 0, 20);

		Debug.println(IntegrationTestBase.INDENT, "testGetGermplasmDescendants(" + gid + "): ");
		for (Object[] object : germplsmList) {
			Debug.println(IntegrationTestBase.INDENT, "  progenitor number: " + object[0]);
			Debug.println(IntegrationTestBase.INDENT, "   " + object[1]);
		}
	}

	@Test
	public void testCountGermplasmDescendants() throws Exception {
		Integer gid = Integer.valueOf(1);
		long count = this.pedigreeManager.countDescendants(gid);
		Debug.println(IntegrationTestBase.INDENT, "testCountGermplasmDescendants(" + gid + "):" + count);
	}

	@Test
	public void testGetProgenitorByGID() throws Exception {
		Integer gid = Integer.valueOf(779745);
		Integer pNo = Integer.valueOf(10);
		Germplasm germplasm = this.pedigreeManager.getParentByGIDAndProgenitorNumber(gid, pNo);
		Debug.println(IntegrationTestBase.INDENT, "testGetProgenitorByGID(" + gid + ", " + pNo + "):" + germplasm);
	}

	@Test
	public void testGeneratePedigreeTree() throws Exception {
		Integer gid = Integer.valueOf(306436);
		int level = 4;
		Debug.println(IntegrationTestBase.INDENT, "GID = " + gid + ", level = " + level + ":");
		GermplasmPedigreeTree tree = this.pedigreeManager.generatePedigreeTree(gid, level);
		if (tree != null) {
			this.printNode(tree.getRoot(), 1);
		}
	}

	@Test
	public void testGetPedigreeLevelCount() throws Exception {
		Integer gid = 1;
		boolean includeDerivativeLine = false;
		Integer pedigreeLevelCount = this.pedigreeManager.countPedigreeLevel(gid, includeDerivativeLine);
		Debug.println(Integer.toString(pedigreeLevelCount));
		Assert.assertNotNull("It should not be null", pedigreeLevelCount);
		Assert.assertEquals("It should be equal to 1", new Integer(1), pedigreeLevelCount);

	}

	@Test
	public void testGetPedigreeLevelCount_IncludeDerivative() throws Exception {
		Integer gid = 1;
		boolean includeDerivativeLine = true;
		Integer pedigreeLevelCount = this.pedigreeManager.countPedigreeLevel(gid, includeDerivativeLine);
		Debug.println(Integer.toString(pedigreeLevelCount));
		Assert.assertNotNull("It should not be null", pedigreeLevelCount);
		Assert.assertEquals("It should be equal to 1", new Integer(1), pedigreeLevelCount);

	}

	@Test
	public void testGeneratePedigreeTree2() throws MiddlewareQueryException {
		int gid = 1;
		int levels = 3;
		Boolean includeDerivativeLines = true;

		GermplasmPedigreeTree germplasmPedigreeTree = this.pedigreeManager.generatePedigreeTree(gid, levels, includeDerivativeLines);
		Debug.println(IntegrationTestBase.INDENT, "generatePedigreeTree(" + gid + ", " + levels + ", " + includeDerivativeLines + ")");
		Debug.println(this.renderNode(germplasmPedigreeTree.getRoot(), ""));
	}

	private String renderNode(GermplasmPedigreeTreeNode node, String prefix) {
		StringBuffer outputString = new StringBuffer();
		if (node != null) {
			outputString.append("   -").append(prefix).append(" ").append(node.getGermplasm().getGid());
			for (GermplasmPedigreeTreeNode parent : node.getLinkedNodes()) {
				outputString.append("\n").append(this.renderNode(parent, prefix + "-"));
			}
		}
		return outputString.toString();
	}

	@Test
	public void testGetManagementNeighbors() throws Exception {
		Integer gid = Integer.valueOf(1);
		int count = (int) this.pedigreeManager.countManagementNeighbors(gid);
		List<Germplasm> neighbors = this.pedigreeManager.getManagementNeighbors(gid, 0, count);
		Assert.assertNotNull(neighbors);
		Assert.assertFalse(neighbors.isEmpty());
		Debug.println(IntegrationTestBase.INDENT, "testGetManagementNeighbors(" + gid + "):" + count);
		for (Germplasm g : neighbors) {
			String name = g.getPreferredName() != null ? g.getPreferredName().getNval() : null;
			Debug.println(IntegrationTestBase.INDENT, g.getGid() + " : " + name);
		}
	}

	@Test
	public void testGetGroupRelatives() throws Exception {
		Integer gid = Integer.valueOf(1);

		long count = this.pedigreeManager.countGroupRelatives(gid);
		List<Germplasm> neighbors = this.pedigreeManager.getGroupRelatives(gid, 0, (int) count);
		Assert.assertNotNull(neighbors);

		Debug.println(IntegrationTestBase.INDENT, "testGetGroupRelatives(" + gid + "):" + neighbors.size());
		for (Germplasm g : neighbors) {
			String name = g.getPreferredName() != null ? g.getPreferredName().getNval() : null;
			Debug.println(IntegrationTestBase.INDENT, g.getGid() + " : " + name);
		}
	}

	@Test
	public void testGetGenerationHistory() throws Exception {
		Integer gid = Integer.valueOf(50533);
		List<Germplasm> results = this.pedigreeManager.getGenerationHistory(gid);
		Assert.assertNotNull(results);
		Assert.assertFalse(results.isEmpty());

		Debug.println(IntegrationTestBase.INDENT, "testGetGenerationHistory(" + gid + "):" + results.size());
		for (Germplasm g : results) {
			String name = g.getPreferredName() != null ? g.getPreferredName().getNval() : null;
			Debug.println(IntegrationTestBase.INDENT, g.getGid() + " : " + name);
		}
	}

	@Test
	public void testGetDescendants() throws Exception {
		Integer id = Integer.valueOf(2);
		List<Object[]> results = this.pedigreeManager.getDescendants(id, 0, 100);
		Assert.assertNotNull(results);
		Assert.assertFalse(results.isEmpty());
		for (Object[] result : results) {
			Debug.println(IntegrationTestBase.INDENT, result[0]);
			Debug.println(IntegrationTestBase.INDENT, result[1]);
		}
	}

	@Test
	public void testGetDerivativeNeighborhood() throws Exception {
		Integer gid = Integer.valueOf(1);
		int stepsBack = 3;
		int stepsForward = 3;
		GermplasmPedigreeTree tree = this.pedigreeManager.getDerivativeNeighborhood(gid, stepsBack, stepsForward);
		Debug.println(IntegrationTestBase.INDENT, "testGetDerivativeNeighborhood(" + gid + ", " + stepsBack + ", " + stepsForward + "): ");
		if (tree != null) {
			this.printNode(tree.getRoot(), 1);
		}
	}

	@Test
	public void testGetDerivativeNeighborhood2() throws Exception {

		MockDataUtil.mockNeighborhoodTestData(this.pedigreeManager, 'D');
		GermplasmPedigreeTree tree;

		Debug.println(IntegrationTestBase.INDENT, "TestCase #1: GID = TOP node (GID, Backward, Forward = -1, 0, 6)");
		tree = this.pedigreeManager.getDerivativeNeighborhood(-1, 0, 6);
		MockDataUtil.printTree(tree);
		Assert.assertEquals("-1*-2**-4***-9***-10**-5*-3**-6**-7***-11****-12****-13*****-14**-8***-15****-17****-18*****-20***-16****-19",
				MockDataUtil.printTree(tree, "*", ""));

		Debug.println(IntegrationTestBase.INDENT, "TestCase #2: GID = LEAF node (GID, Backward, Forward = -5, 2, 10)");
		tree = this.pedigreeManager.getDerivativeNeighborhood(-5, 2, 10);
		MockDataUtil.printTree(tree);
		Assert.assertEquals("-1*-2**-4***-9***-10**-5*-3**-6**-7***-11****-12****-13*****-14**-8***-15****-17****-18*****-20***-16****-19",
				MockDataUtil.printTree(tree, "*", ""));

		Debug.println(IntegrationTestBase.INDENT,
				"TestCase #3: GID = MID node AND Backward < Depth of LEAF (GID, Backward, Forward = -11, 1, 10)");
		tree = this.pedigreeManager.getDerivativeNeighborhood(-11, 1, 10);
		MockDataUtil.printTree(tree);
		Assert.assertEquals("-7*-11**-12**-13***-14", MockDataUtil.printTree(tree, "*", ""));

		Debug.println(IntegrationTestBase.INDENT,
				"TestCase #4: GID = MID node AND Backward = Dept of LEAF (GID, Backward, Forward = -11, 3, 10)");
		tree = this.pedigreeManager.getDerivativeNeighborhood(-11, 3, 10);
		MockDataUtil.printTree(tree);
		Assert.assertEquals("-1*-2**-4***-9***-10**-5*-3**-6**-7***-11****-12****-13*****-14**-8***-15****-17****-18*****-20***-16****-19",
				MockDataUtil.printTree(tree, "*", ""));

		Debug.println(IntegrationTestBase.INDENT,
				"TestCase #5: GID = MID node AND Backward > Dept of LEAF (GID, Backward, Forward = -11, 5, 10)");
		tree = this.pedigreeManager.getDerivativeNeighborhood(-11, 5, 10);
		MockDataUtil.printTree(tree);
		Assert.assertEquals("-1*-2**-4***-9***-10**-5*-3**-6**-7***-11****-12****-13*****-14**-8***-15****-17****-18*****-20***-16****-19",
				MockDataUtil.printTree(tree, "*", ""));

		Debug.println(IntegrationTestBase.INDENT,
				"TestCase #6: GID = MID node AND Forward < Tree Depth - MID depth (GID, Backward, Forward = -3, 1, 2)");
		tree = this.pedigreeManager.getDerivativeNeighborhood(-3, 1, 2);
		MockDataUtil.printTree(tree);
		Assert.assertEquals("-1*-2**-4***-9***-10**-5*-3**-6**-7***-11**-8***-15***-16", MockDataUtil.printTree(tree, "*", ""));

		Debug.println(IntegrationTestBase.INDENT,
				"TestCase #7: GID is MAN, but Ancestors and Descendants have non-MAN members (GID, Backward, Forward = -15, 2, 1)");
		tree = this.pedigreeManager.getDerivativeNeighborhood(-15, 2, 1);
		MockDataUtil.printTree(tree);
		Assert.assertEquals("-3*-6*-7**-11***-12***-13*-8**-15***-17***-18**-16***-19", MockDataUtil.printTree(tree, "*", ""));

		Debug.println(IntegrationTestBase.INDENT,
				"TestCase #8: Should stop at GEN even if Backward count is not exhausted (GID, Backward, Forward = -9, 4, 1)");
		tree = this.pedigreeManager.getDerivativeNeighborhood(-9, 4, 1);
		MockDataUtil.printTree(tree);
		Assert.assertEquals("-4*-9*-10", MockDataUtil.printTree(tree, "*", ""));

		// cleanup
		MockDataUtil.cleanupMockMaintenanceTestData(this.pedigreeManager);
	}

	private void printNode(GermplasmPedigreeTreeNode node, int level) {
		StringBuffer tabs = new StringBuffer();

		for (int ctr = 1; ctr < level; ctr++) {
			tabs.append("\t");
		}

		String name = node.getGermplasm().getPreferredName() != null ? node.getGermplasm().getPreferredName().getNval() : null;
		Debug.println(IntegrationTestBase.INDENT, tabs.toString() + node.getGermplasm().getGid() + " : " + name);

		for (GermplasmPedigreeTreeNode parent : node.getLinkedNodes()) {
			this.printNode(parent, level + 1);
		}
	}

	@Test
	public void testGetMaintenanceNeighborhood() throws Exception {

		MockDataUtil.mockNeighborhoodTestData(this.pedigreeManager, 'M');
		GermplasmPedigreeTree tree;

		Debug.println(IntegrationTestBase.INDENT, "TestCase #1: GID = TOP node (GID, Backward, Forward = -1, 0, 6)");
		tree = this.pedigreeManager.getMaintenanceNeighborhood(-1, 0, 6);
		MockDataUtil.printTree(tree);
		Assert.assertEquals("-1*-2*-3**-6**-7***-11****-12****-13*****-14", MockDataUtil.printTree(tree, "*", ""));

		Debug.println(IntegrationTestBase.INDENT, "TestCase #2: GID = LEAF node (GID, Backward, Forward = -5, 2, 10)");
		tree = this.pedigreeManager.getMaintenanceNeighborhood(-5, 2, 10);
		MockDataUtil.printTree(tree);
		Assert.assertEquals("-1*-2*-3**-6**-7***-11****-12****-13*****-14", MockDataUtil.printTree(tree, "*", ""));

		Debug.println(IntegrationTestBase.INDENT,
				"TestCase #3: GID = MID node AND Backward < Depth of LEAF (GID, Backward, Forward = -11, 1, 10)");
		tree = this.pedigreeManager.getMaintenanceNeighborhood(-11, 1, 10);
		MockDataUtil.printTree(tree);
		Assert.assertEquals("-7*-11**-12**-13***-14", MockDataUtil.printTree(tree, "*", ""));

		Debug.println(IntegrationTestBase.INDENT,
				"TestCase #4: GID = MID node AND Backward = Dept of LEAF (GID, Backward, Forward = -11, 3, 10)");
		tree = this.pedigreeManager.getMaintenanceNeighborhood(-11, 3, 10);
		MockDataUtil.printTree(tree);
		Assert.assertEquals("-1*-2*-3**-6**-7***-11****-12****-13*****-14", MockDataUtil.printTree(tree, "*", ""));

		Debug.println(IntegrationTestBase.INDENT,
				"TestCase #5: GID = MID node AND Backward > Dept of LEAF (GID, Backward, Forward = -11, 5, 10)");
		tree = this.pedigreeManager.getMaintenanceNeighborhood(-11, 5, 10);
		MockDataUtil.printTree(tree);
		Assert.assertEquals("-1*-2*-3**-6**-7***-11****-12****-13*****-14", MockDataUtil.printTree(tree, "*", ""));

		Debug.println(IntegrationTestBase.INDENT,
				"TestCase #6: GID = MID node AND Forward < Tree Depth - MID depth (GID, Backward, Forward = -3, 1, 2)");
		tree = this.pedigreeManager.getMaintenanceNeighborhood(-3, 1, 2);
		MockDataUtil.printTree(tree);
		Assert.assertEquals("-1*-2*-3**-6**-7***-11", MockDataUtil.printTree(tree, "*", ""));

		Debug.println(IntegrationTestBase.INDENT,
				"TestCase #7: GID is MAN, but Ancestors and Descendants have non-MAN members (GID, Backward, Forward = -15, 2, 1)");
		tree = this.pedigreeManager.getMaintenanceNeighborhood(-15, 2, 1);
		MockDataUtil.printTree(tree);
		Assert.assertEquals("-1*-2*-3**-6**-7***-11", MockDataUtil.printTree(tree, "*", ""));

		Debug.println(IntegrationTestBase.INDENT,
				"TestCase #8: Should stop at GEN even if Backward count is not exhausted (GID, Backward, Forward = -9, 4, 1)");
		tree = this.pedigreeManager.getMaintenanceNeighborhood(-9, 4, 1);
		MockDataUtil.printTree(tree);
		Assert.assertEquals("-4*-9*-10", MockDataUtil.printTree(tree, "*", ""));

		// cleanup
		MockDataUtil.cleanupMockMaintenanceTestData(this.pedigreeManager);
	}

	@Test
	public void testGetParentByGIDAndProgenitorNumber() throws Exception {
		Integer gid = Integer.valueOf(104);
		Integer progenitorNumber = Integer.valueOf(2);
		Germplasm result = this.pedigreeManager.getParentByGIDAndProgenitorNumber(gid, progenitorNumber);
		Assert.assertNotNull(result);
		Debug.println(IntegrationTestBase.INDENT, "testGetParentByGIDAndProgenitorNumber(): " + result);
	}

	@Test
	public void testCountDescendants() throws Exception {
		Integer gid = 10; // change gid value
		long count = this.pedigreeManager.countDescendants(gid);
		Assert.assertNotNull(count);
		Debug.println(IntegrationTestBase.INDENT, "testCountDescendants(" + gid + "):" + count);
	}

	@Test
	public void testCountGroupRelatives() throws Exception {
		Integer gid = 1; // change gid value
		long count = this.pedigreeManager.countGroupRelatives(gid);
		Assert.assertNotNull(count);
		Debug.println(IntegrationTestBase.INDENT, "testCountGroupRelatives(" + gid + "):" + count);
	}

	@Test
	public void testCountManagementNeighbors() throws Exception {
		Integer gid = 1; // change gid value
		long count = this.pedigreeManager.countManagementNeighbors(gid);
		Assert.assertNotNull(count);
		Debug.println(IntegrationTestBase.INDENT, "testCountManagementNeighbors(" + gid + "):" + count);
	}
}
