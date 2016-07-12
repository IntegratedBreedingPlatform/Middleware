
package org.generationcp.middleware.service.pedigree.string.processors;

import org.generationcp.middleware.service.pedigree.GermplasmNode;
import org.generationcp.middleware.service.pedigree.PedigreeString;
import org.generationcp.middleware.service.pedigree.string.util.FixedLineNameResolver;
import org.generationcp.middleware.service.pedigree.string.util.PedigreeStringGeneratorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This method only does A X B and does not traverse the tree.
 */
public class SimpleCrossProcessor implements BreedingMethodProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(SimpleCrossProcessor.class);

	final InbredProcessor inbredProcessor = new InbredProcessor();

	@Override
	public PedigreeString processGermplasmNode(final GermplasmNode germplasmNode, final Integer level,
			final FixedLineNameResolver fixedLineNameResolver, final boolean originatesFromComplexCross) {

		if(germplasmNode != null && germplasmNode.getGermplasm() != null && germplasmNode.getGermplasm().getGid() != null) {
			LOG.debug("Germplasm with GID '{}' is being processed by an simple cross processor. "
					, germplasmNode.getGermplasm().getGid());
		}

		final PedigreeString femaleLeafPedigreeString =
				this.inbredProcessor.processGermplasmNode(germplasmNode.getFemaleParent(), level - 1, fixedLineNameResolver, originatesFromComplexCross);
		final PedigreeString maleLeafPedigreeString =
				this.inbredProcessor.processGermplasmNode(germplasmNode.getMaleParent(), level - 1, fixedLineNameResolver, originatesFromComplexCross);

		final PedigreeString pedigreeString = new PedigreeString();
		pedigreeString.setPedigree(PedigreeStringGeneratorUtil.gerneratePedigreeString(femaleLeafPedigreeString, maleLeafPedigreeString));
		return pedigreeString;
	}

}
