package org.generationcp.middleware.manager;

import java.util.Arrays;
import java.util.List;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.PedigreeDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Progenitor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class PedigreeDataManagerImplTest extends IntegrationTestBase {
	
	@Autowired
	private GermplasmDataManager germplasmManager;
	
	@Autowired
	private PedigreeDataManager pedigreeManager;
	
	private Germplasm germplasmWithUnknownParent;
	
	private Germplasm germplasmWithPolyCrosses;
	
	@Before
	public void setup() {
		if (this.germplasmWithUnknownParent == null) {
			this.germplasmWithUnknownParent = GermplasmTestDataInitializer.createGermplasm(1);
			// Set male parent as Unknown
			this.germplasmWithUnknownParent.setGpid2(0);
			this.germplasmManager.save(this.germplasmWithUnknownParent);
		}
		
	}
	
	@Test
	public void testGeneratePedigreeTreeWithUnknownParent() {
		final GermplasmPedigreeTree tree = this.pedigreeManager.generatePedigreeTree(this.germplasmWithUnknownParent.getGid(), 2);
		Assert.assertEquals(this.germplasmWithUnknownParent, tree.getRoot().getGermplasm());
		final List<GermplasmPedigreeTreeNode> nodes = tree.getRoot().getLinkedNodes();
		Assert.assertEquals(2, nodes.size());
		Assert.assertEquals(this.germplasmWithUnknownParent.getGpid1(), nodes.get(0).getGermplasm().getGid());
		final Germplasm unknownGermplasm = nodes.get(1).getGermplasm();
		Assert.assertEquals(0, unknownGermplasm.getGid().intValue());
		Assert.assertEquals(Name.UNKNOWN, unknownGermplasm.getPreferredName().getNval());
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
		final Integer progenitorGid = this.germplasmWithUnknownParent.getGid();
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
