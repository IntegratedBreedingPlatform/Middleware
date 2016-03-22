
package org.generationcp.middleware.service.pedigree.string.processors;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.service.pedigree.GermplasmNode;
import org.generationcp.middleware.service.pedigree.PedigreeString;
import org.generationcp.middleware.service.pedigree.string.util.FixedLineNameResolver;
import org.generationcp.middleware.service.pedigree.string.util.PedigreeStringGeneratorUtil;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

public class PedigreeStringBuilder {

	private final InbredProcessor inbredProcessor = new InbredProcessor();;

	public PedigreeString buildPedigreeString(final GermplasmNode germplasmNode, final int level,
			final FixedLineNameResolver fixedLineNameResolver) {

		Preconditions.checkNotNull(germplasmNode);

		final Optional<PedigreeString> fixedLineName = PedigreeStringGeneratorUtil.getFixedLineName(germplasmNode, fixedLineNameResolver);
		if (fixedLineName.isPresent()) {
			return fixedLineName.get();
		}

		if (level == 0) {
			return this.inbredProcessor.processGermplasmNode(germplasmNode, level, fixedLineNameResolver);
		}

		final Germplasm germplasm = germplasmNode.getGermplasm();

		// FIXME: Add some comments here
		if (germplasm != null && germplasm.getGnpgs() < 0) {
			return this.processDeravaitveOrMaintenceGermplasm(germplasmNode, level, fixedLineNameResolver);
		}

		final BreedingMethodProcessor methodProcessor = BreedingMethodFactory.getMethodProcessor(germplasmNode);
		return methodProcessor.processGermplasmNode(germplasmNode, level, fixedLineNameResolver);
	}

	private PedigreeString processDeravaitveOrMaintenceGermplasm(final GermplasmNode germplasmNode, final int level,
			final FixedLineNameResolver fixedLineNameResolver) {

		final GermplasmNode femaleParent = germplasmNode.getFemaleParent();

		if (femaleParent != null) {
			return this.buildPedigreeString(germplasmNode.getFemaleParent(), level, fixedLineNameResolver);
		}

		return this.inbredProcessor.processGermplasmNode(germplasmNode, level - 1, fixedLineNameResolver);

	}

}
