package org.generationcp.middleware.manager;

import org.generationcp.middleware.IntegrationTestBase;
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
	
	private Germplasm germplasmWithPolyCrosses;

	private Germplasm maternalGrandParent1;
	private Germplasm maternalGrandParent2;
	
	private Germplasm femaleParent;

	private Germplasm maleParent;
	
	@Before
	public void setup() {
		if (this.maternalGrandParent1 == null){
			this.maternalGrandParent1 = GermplasmTestDataInitializer.createGermplasm(1);
			this.germplasmManager.save(this.maternalGrandParent1);
			this.maternalGrandParent1.getPreferredName().setGermplasm(this.maternalGrandParent1);
			this.germplasmManager.addGermplasmName(this.maternalGrandParent1.getPreferredName());

		}

		if (this.maternalGrandParent2 == null){
			this.maternalGrandParent2 = GermplasmTestDataInitializer.createGermplasm(1);
			this.germplasmManager.save(this.maternalGrandParent2);
			this.maternalGrandParent2.getPreferredName().setGermplasm(this.maternalGrandParent2);
			this.germplasmManager.addGermplasmName(this.maternalGrandParent2.getPreferredName());
		}

		if (this.femaleParent == null){
			this.femaleParent = GermplasmTestDataInitializer.createGermplasm(1);
			this.femaleParent.setGpid1(this.maternalGrandParent1.getGid());
			this.femaleParent.setGpid2(this.maternalGrandParent2.getGid());
			this.germplasmManager.save(this.femaleParent);
			this.femaleParent.getPreferredName().setGermplasm(this.femaleParent);
			this.germplasmManager.addGermplasmName(this.femaleParent.getPreferredName());

		}

		if (this.maleParent == null){
			this.maleParent = GermplasmTestDataInitializer.createGermplasm(1);
			this.germplasmManager.save(this.maleParent);
			this.maleParent.getPreferredName().setGermplasm(this.maleParent);
			this.germplasmManager.addGermplasmName(this.maleParent.getPreferredName());

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
		final Germplasm femaleGermplasm = nodes.get(0).getGermplasm();
 		Assert.assertEquals(this.crossWithUnknownParent.getGpid1(), femaleGermplasm.getGid());
		Assert.assertEquals(this.femaleParent.getGid(), femaleGermplasm.getGid());
		Assert.assertEquals(this.femaleParent.getPreferredName().getNval(), femaleGermplasm.getPreferredName().getNval());

		final List<GermplasmPedigreeTreeNode> grandparentNodes = nodes.get(0).getLinkedNodes();
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
	
	@Test
	public void tesUpdateProgenitor() {
		this.germplasmWithPolyCrosses = GermplasmTestDataInitializer.createGermplasm(1);
		this.germplasmManager.save(this.germplasmWithPolyCrosses);
		final Integer gid = this.germplasmWithPolyCrosses.getGid();

		// Change gpid2
		Germplasm newGermplasm = GermplasmTestDataInitializer.createGermplasm(1);
		this.germplasmManager.save(newGermplasm);
		Integer newProgenitorId = newGermplasm.getGid();
		Assert.assertNotEquals(0, this.germplasmWithPolyCrosses.getGpid2().intValue());
		this.pedigreeManager.updateProgenitor(gid, newProgenitorId, 2);
		Assert.assertEquals(newProgenitorId, this.germplasmManager.getGermplasmByGID(gid).getGpid2());
		
		// New progenitor record should be created
		final Integer progenitorGid = this.crossWithUnknownParent.getGid();
		this.pedigreeManager.updateProgenitor(gid, progenitorGid, 3);
		List<Germplasm> progenitors = this.germplasmManager.getProgenitorsByGIDWithPrefName(gid);
		Assert.assertEquals(1, progenitors.size());
		Assert.assertEquals(progenitorGid, progenitors.get(0).getGid());
		
		
		// Update existing progenitor record
		newGermplasm = GermplasmTestDataInitializer.createGermplasm(1);
		this.germplasmManager.save(newGermplasm);
		newProgenitorId = newGermplasm.getGid();
		this.pedigreeManager.updateProgenitor(gid, newProgenitorId, 3);
		progenitors = this.germplasmManager.getProgenitorsByGIDWithPrefName(gid);
		Assert.assertEquals(1, progenitors.size());
		Assert.assertEquals(newProgenitorId, progenitors.get(0).getGid());
	}
	

}
