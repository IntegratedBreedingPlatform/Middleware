
package org.generationcp.middleware.service.pedigree.string.processors;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.service.pedigree.GermplasmNode;
import org.generationcp.middleware.service.pedigree.PedigreeString;
import org.generationcp.middleware.service.pedigree.string.util.FixedLineNameResolver;
import org.generationcp.middleware.service.pedigree.string.util.PedigreeStringGeneratorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

/**
 * Enables us to construct a Pedigree string from a germplasm and its ancestor history.
 *
 */
public class PedigreeStringBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(PedigreeStringBuilder.class);

	private final InbredProcessor inbredProcessor = new InbredProcessor();;

	public PedigreeString buildPedigreeString(final GermplasmNode germplasmNode, final int level,
			final FixedLineNameResolver fixedLineNameResolver) {

		Preconditions.checkNotNull(germplasmNode);

		final Germplasm germplasm = germplasmNode.getGermplasm();

		if(germplasmNode != null && germplasmNode.getGermplasm() != null && germplasmNode.getGermplasm().getGid() != null) {
			LOG.debug("Building pedigree tree for germlasm with gid - '{}'", germplasmNode.getGermplasm().getGid());
		}

		final Optional<PedigreeString> fixedLineName = PedigreeStringGeneratorUtil.getFixedLineName(germplasmNode, fixedLineNameResolver);
		if (fixedLineName.isPresent()) {
			return fixedLineName.get();
		}

		if (level == 0) {
			return this.inbredProcessor.processGermplasmNode(germplasmNode, level, fixedLineNameResolver);
		}

		// Is this germplasm a result of a derivative or maintenance breeding method. If so skip node.
		if (germplasm != null && germplasm.getGnpgs() < 0) {
			return this.processDerivativeOrMaintenceGermplasm(germplasmNode, level, fixedLineNameResolver);
		}

		// Get breeding method used to create this germplasm.
		final BreedingMethodProcessor methodProcessor = BreedingMethodFactory.getMethodProcessor(germplasmNode);
		return methodProcessor.processGermplasmNode(germplasmNode, level, fixedLineNameResolver);
	}

	private PedigreeString processDerivativeOrMaintenceGermplasm(final GermplasmNode germplasmNode, final int level,
			final FixedLineNameResolver fixedLineNameResolver) {

		final GermplasmNode femaleParent = germplasmNode.getFemaleParent();

		if (femaleParent != null) {
			// Note derivative or maintenance methods are not consider a level increment
			return this.buildPedigreeString(germplasmNode.getFemaleParent(), level, fixedLineNameResolver);
		}

		return this.inbredProcessor.processGermplasmNode(germplasmNode, level - 1, fixedLineNameResolver);

	}

}
