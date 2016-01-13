package org.generationcp.middleware.service.pedigree;


public class GermplasmPedigreeCreationFactory {

	public static NodeProcessor getNodeProcessor(GermplasmNode currentNode) {
		Integer gnpgs = currentNode.getRoot().getGermplasm().getGnpgs();
		if(gnpgs < 0) {
			return new DerativeMaintenanceProcessor();
		}
		return new GenerativeProcessor();

	}

}
