package org.generationcp.middleware.manager;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.NameDAO;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.PedigreeDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.generationcp.middleware.pojos.Name;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


public class PedigreeDataManagerImplTest extends IntegrationTestBase {
	
	@Autowired
	private GermplasmDataManager germplasmManager;
	
	@Autowired
	private PedigreeDataManager pedigreeManager;
	
	private Germplasm crossWithUnknownParent;

	private Germplasm crossWithKnownParents;

	private Germplasm maternalGrandParent1;
	private Germplasm maternalGrandParent2;
	
	private Germplasm femaleParent;

	private Germplasm maleParent;

	private NameDAO nameDAO;
	
	@Before
	public void setup() {
		if (this.nameDAO == null) {
			this.nameDAO = new NameDAO(this.sessionProvder.getSession());
		}

		if (this.maternalGrandParent1 == null){
			this.maternalGrandParent1 = GermplasmTestDataInitializer.createGermplasm(1);
			this.germplasmManager.save(this.maternalGrandParent1);
			this.maternalGrandParent1.getPreferredName().setGermplasm(this.maternalGrandParent1);
			this.nameDAO.save(this.maternalGrandParent1.getPreferredName());

		}

		if (this.maternalGrandParent2 == null){
			this.maternalGrandParent2 = GermplasmTestDataInitializer.createGermplasm(1);
			this.germplasmManager.save(this.maternalGrandParent2);
			this.maternalGrandParent2.getPreferredName().setGermplasm(this.maternalGrandParent2);
			this.nameDAO.save(this.maternalGrandParent2.getPreferredName());
		}

		if (this.femaleParent == null){
			this.femaleParent = GermplasmTestDataInitializer.createGermplasm(1);
			this.femaleParent.setGpid1(this.maternalGrandParent1.getGid());
			this.femaleParent.setGpid2(this.maternalGrandParent2.getGid());
			this.germplasmManager.save(this.femaleParent);
			this.femaleParent.getPreferredName().setGermplasm(this.femaleParent);
			this.nameDAO.save(this.femaleParent.getPreferredName());

		}

		if (this.maleParent == null){
			this.maleParent = GermplasmTestDataInitializer.createGermplasm(1);
			this.germplasmManager.save(this.maleParent);
			this.maleParent.getPreferredName().setGermplasm(this.maleParent);
			this.nameDAO.save(this.maleParent.getPreferredName());

		}
		if (this.crossWithUnknownParent == null) {
			this.crossWithUnknownParent = GermplasmTestDataInitializer.createGermplasm(1);
			this.crossWithUnknownParent.setGpid1(this.femaleParent.getGid());
			// Set male parent as Unknown
			this.crossWithUnknownParent.setGpid2(0);
			this.germplasmManager.save(this.crossWithUnknownParent);
		}

		if (this.crossWithKnownParents == null) {
			this.crossWithKnownParents = GermplasmTestDataInitializer.createGermplasm(1);
			this.crossWithKnownParents.setGpid1(this.femaleParent.getGid());
			this.crossWithKnownParents.setGpid2(this.maleParent.getGid());
			this.germplasmManager.save(this.crossWithKnownParents);
		}

		
	}
	
	@Test
	public void testGeneratePedigreeTreeWithUnknownMaleParent() {
		final GermplasmPedigreeTree tree = this.pedigreeManager.generatePedigreeTree(this.crossWithUnknownParent.getGid(), 2);
		Assert.assertEquals(this.crossWithUnknownParent, tree.getRoot().getGermplasm());
		final List<GermplasmPedigreeTreeNode> nodes = tree.getRoot().getLinkedNodes();
		Assert.assertEquals(2, nodes.size());
		final Germplasm femaleGermplasm = nodes.get(0).getGermplasm();
		Assert.assertEquals(this.crossWithUnknownParent.getGpid1(), femaleGermplasm.getGid());
		Assert.assertEquals(this.femaleParent.getGid(), femaleGermplasm.getGid());
		Assert.assertEquals(this.femaleParent.getPreferredName().getNval(), femaleGermplasm.getPreferredName().getNval());
		final Germplasm unknownGermplasm = nodes.get(1).getGermplasm();
		Assert.assertEquals(0, unknownGermplasm.getGid().intValue());
		Assert.assertEquals(Name.UNKNOWN, unknownGermplasm.getPreferredName().getNval());
	}

	@Test
	public void testGeneratePedigreeTreeWithUnknownMaleParentIncludingDerivativeLines() {
		this.crossWithUnknownParent.setGnpgs(-1);
		this.germplasmManager.save(this.crossWithUnknownParent);

		final GermplasmPedigreeTree tree = this.pedigreeManager.generatePedigreeTree(this.crossWithUnknownParent.getGid(), 3, true);
		Assert.assertEquals(this.crossWithUnknownParent, tree.getRoot().getGermplasm());
		final List<GermplasmPedigreeTreeNode> nodes = tree.getRoot().getLinkedNodes();
		Assert.assertEquals(1, nodes.size());

		final GermplasmPedigreeTreeNode uknownNode = nodes.get(0);
		final Germplasm unknownGermplasm = uknownNode.getGermplasm();

		final Germplasm femaleGermplasm = uknownNode.getLinkedNodes().get(0).getGermplasm();
		Assert.assertEquals(true, unknownGermplasm.getGid().equals(0));
		Assert.assertEquals(this.femaleParent.getGid(), femaleGermplasm.getGid());
		Assert.assertEquals(this.femaleParent.getPreferredName().getNval(), femaleGermplasm.getPreferredName().getNval());

		final GermplasmPedigreeTreeNode grandParentsTreeNode = uknownNode.getLinkedNodes().get(0); //Female Node
		final List<GermplasmPedigreeTreeNode> grandparentNodes = grandParentsTreeNode.getLinkedNodes();
		Assert.assertEquals(2, grandparentNodes.size());
		final Germplasm maternalGP1 = grandparentNodes.get(0).getGermplasm();
		Assert.assertEquals(this.maternalGrandParent1.getGid(), maternalGP1.getGid());
		Assert.assertEquals(this.maternalGrandParent1.getPreferredName().getNval(), maternalGP1.getPreferredName().getNval());
		final Germplasm maternalGP2 = grandparentNodes.get(1).getGermplasm();
		Assert.assertEquals(this.maternalGrandParent2.getGid(), maternalGP2.getGid());
		Assert.assertEquals(this.maternalGrandParent2.getPreferredName().getNval(), maternalGP2.getPreferredName().getNval());

	}

	@Test
	public void testGeneratePedigreeTreeWithUnknownFemaleParent() {
		this.crossWithUnknownParent.setGpid1(0);
		this.crossWithUnknownParent.setGpid2(this.maleParent.getGid());
		this.germplasmManager.save(this.crossWithUnknownParent);

		final GermplasmPedigreeTree tree = this.pedigreeManager.generatePedigreeTree(this.crossWithUnknownParent.getGid(), 2);
		Assert.assertEquals(this.crossWithUnknownParent, tree.getRoot().getGermplasm());
		final List<GermplasmPedigreeTreeNode> nodes = tree.getRoot().getLinkedNodes();
		Assert.assertEquals(2, nodes.size());
		final Germplasm femaleGermplasm = nodes.get(0).getGermplasm();
		Assert.assertEquals(0, femaleGermplasm.getGid().intValue());
		Assert.assertEquals(Name.UNKNOWN, femaleGermplasm.getPreferredName().getNval());
		final Germplasm maleGermplasm = nodes.get(1).getGermplasm();
		Assert.assertEquals(this.crossWithUnknownParent.getGpid2(), maleGermplasm.getGid());
		Assert.assertEquals(this.maleParent.getGid(), maleGermplasm.getGid());
		Assert.assertEquals(this.maleParent.getPreferredName().getNval(), maleGermplasm.getPreferredName().getNval());
	}

	@Test
	public void testGeneratePedigreeTreeWithTwoUnknownParents() {
		//  Make both parents unknown
		this.crossWithUnknownParent.setGpid1(0);
		this.germplasmManager.save(this.crossWithUnknownParent);

		final GermplasmPedigreeTree tree = this.pedigreeManager.generatePedigreeTree(this.crossWithUnknownParent.getGid(), 2);
		Assert.assertEquals(this.crossWithUnknownParent, tree.getRoot().getGermplasm());
		// Not expecting any node for parents if both of them are unknown
		Assert.assertTrue(tree.getRoot().getLinkedNodes().isEmpty());
	}

	@Test
	public void testGeneratePedigreeTreeWithTwoKnownParents() {
		final GermplasmPedigreeTree tree = this.pedigreeManager.generatePedigreeTree(this.crossWithKnownParents.getGid(), 2);
		Assert.assertEquals(this.crossWithKnownParents, tree.getRoot().getGermplasm());
		final List<GermplasmPedigreeTreeNode> nodes = tree.getRoot().getLinkedNodes();
		Assert.assertEquals(2, nodes.size());
		final Germplasm femaleGermplasm = nodes.get(0).getGermplasm();
		Assert.assertEquals(this.crossWithKnownParents.getGpid1(), femaleGermplasm.getGid());
		Assert.assertEquals(this.femaleParent.getGid(), femaleGermplasm.getGid());
		Assert.assertEquals(this.femaleParent.getPreferredName().getNval(), femaleGermplasm.getPreferredName().getNval());
		final Germplasm maleGermplasm = nodes.get(1).getGermplasm();
		Assert.assertEquals(this.crossWithKnownParents.getGpid2(), maleGermplasm.getGid());
		Assert.assertEquals(this.maleParent.getGid(), maleGermplasm.getGid());
		Assert.assertEquals(this.maleParent.getPreferredName().getNval(), maleGermplasm.getPreferredName().getNval());
	}

}
