package org.generationcp.middleware.api.germplasm.pedigree.cop;

import com.google.common.collect.Table;
import org.generationcp.middleware.api.germplasm.pedigree.GermplasmTreeNode;
import org.generationcp.middleware.pojos.CopMatrix;

import java.util.Set;

public interface CopServiceAsync {

	GermplasmTreeNode getGermplasmPedigreeTree(Integer gid, Integer level, boolean includeDerivativeLines);

	void saveOrUpdate(CopMatrix copMatrix);
}
