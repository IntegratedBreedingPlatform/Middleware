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

import java.util.List;

@Ignore("Historic failing test. Disabled temporarily. Developers working in this area please spend some time to fix and remove @Ignore.")
public class PedigreeDataManagerTest extends IntegrationTestBase {

	@Autowired
	private PedigreeDataManager pedigreeManager;

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
	public void testGetParentByGIDAndProgenitorNumber() throws Exception {
		Integer gid = Integer.valueOf(104);
		Integer progenitorNumber = Integer.valueOf(2);
		Germplasm result = this.pedigreeManager.getParentByGIDAndProgenitorNumber(gid, progenitorNumber);
		Assert.assertNotNull(result);
		Debug.println(IntegrationTestBase.INDENT, "testGetParentByGIDAndProgenitorNumber(): " + result);
	}

}
