package org.generationcp.middleware.api.germplasm.pedigree.cop;

import org.generationcp.middleware.api.germplasm.pedigree.GermplasmTreeNode;
import org.generationcp.middleware.pojos.CopMatrix;

public interface CopServiceAsync {

	GermplasmTreeNode getGermplasmPedigreeTree(Integer gid, Integer level, boolean includeDerivativeLines);

	void saveOrUpdate(CopMatrix copMatrix);
}
