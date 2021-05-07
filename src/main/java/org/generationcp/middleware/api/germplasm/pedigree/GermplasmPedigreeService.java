package org.generationcp.middleware.api.germplasm.pedigree;

import org.generationcp.middleware.pojos.GermplasmPedigreeTree;

public interface GermplasmPedigreeService {

	GermplasmTreeNode getGermplasmPedigreeTree(Integer gid, Integer level, boolean includeDerivativeLine);

}
