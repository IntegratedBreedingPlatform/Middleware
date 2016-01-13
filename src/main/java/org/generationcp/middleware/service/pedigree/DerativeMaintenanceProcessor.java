package org.generationcp.middleware.service.pedigree;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.germplasm.SingleGermplasmCrossElement;


public class DerativeMaintenanceProcessor  implements NodeProcessor {

	@Override
	public void process(GermplasmNode rootNode) {
//		// for germplasms created via a derivative or maintenance
//		// method
//		// skip and then expand on the gpid1 parent
//		final Germplasm germplasmToExpand = rootNode.getRoot().getGermplasm();
//		if (germplasmToExpand.getGpid1() != null && germplasmToExpand.getGpid1() != 0 && !forComplexCross) {
//			SingleGermplasmCrossElement nextElement = new SingleGermplasmCrossElement();
//			Germplasm gpid1Germplasm = rootNode.getFemaleParent().getRoot().getGermplasm();
//			if (gpid1Germplasm != null) {
//				nextElement.setGermplasm(gpid1Germplasm);
//				return this.expandGermplasmCross(nextElement, level, forComplexCross);
//			} else {
//				return element;
//			}
//		} else {
//			return element;
//		}
	}

}
