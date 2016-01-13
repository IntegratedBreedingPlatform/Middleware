
package org.generationcp.middleware.service.pedigree;

import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;

import com.google.common.base.Preconditions;

public class GermplasmHierarchyTraverser {

	final GermplasmDataManager germplasmDataManager;

	GermplasmHierarchyTraverser(GermplasmDataManager germplasmDataManager) {
		this.germplasmDataManager = germplasmDataManager;
	}

	public GermplasmNode getAllParents(final Integer gid) {
		final GermplasmNode germplasmNode = new GermplasmNode();
		germplasmNode.setRoot(getGermplasm(gid));
		getAllParents(germplasmNode.getRoot().getGermplasm(), germplasmNode);
		return germplasmNode;

	}

	private void getAllParents(final Germplasm germplasm, GermplasmNode germplasmNode) {

		Integer gpid1 = germplasm.getGpid1();
		// Get all parents on the female side
		if (gpid1 != null && gpid1 != 0) {
			final GermplasmDetails gpid1Germplasm = getGermplasm(gpid1);
			final GermplasmNode germplasmFemaleNode = new GermplasmNode();
			germplasmFemaleNode.setRoot(gpid1Germplasm);
			germplasmNode.setFemaleParent(germplasmFemaleNode);
			getAllParents(gpid1Germplasm.getGermplasm(),germplasmFemaleNode);
		}

		// Get all parents on the male side
		Integer gpid2 = germplasm.getGpid2();
		if (gpid2 != null && gpid2 != 0) {
			final GermplasmDetails gpid2Parents = getGermplasm(gpid2);
			final GermplasmNode germplasmMaleNode = new GermplasmNode();
			germplasmMaleNode.setRoot(gpid2Parents);
			germplasmNode.setMaleParent(germplasmMaleNode);
			getAllParents(gpid2Parents.getGermplasm(), germplasmMaleNode);
		}
	}

	private GermplasmDetails getGermplasm(final Integer germplasmId) {

		Preconditions.checkNotNull(germplasmId);

		if (germplasmId != 0) {
			Germplasm germplasm = germplasmDataManager.getGermplasmWithPrefName(germplasmId);
			Method methodByID = germplasmDataManager.getMethodByID(germplasm.getMethodId());
			return new GermplasmDetails(germplasm, methodByID, germplasm.getGnpgs());
		}
		throw new IllegalStateException(
				"We should have never called the method with a 0 as the germplasm id. "
				+ "Please contact administrator for further assistance.");

	}
}
