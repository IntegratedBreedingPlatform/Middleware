
package org.generationcp.middleware.service.pedigree.string.processors;

import org.generationcp.middleware.service.pedigree.GermplasmNode;
import org.generationcp.middleware.service.pedigree.PedigreeString;
import org.generationcp.middleware.service.pedigree.string.util.FixedLineNameResolver;
import org.generationcp.middleware.service.pedigree.string.util.PedigreeStringGeneratorUtil;

/**
 * This method only does A X B and does not traverse the tree.
 */
public class SimpleCrossProcessor implements BreedingMethodProcessor {

	final InbredProcessor inbredProcessor = new InbredProcessor();

	@Override
	public PedigreeString processGermplasmNode(final GermplasmNode germplasmNode, final Integer level,
			final FixedLineNameResolver fixedLineNameResolver) {
		final PedigreeString femaleLeafPedigreeString =
				this.inbredProcessor.processGermplasmNode(germplasmNode.getFemaleParent(), level - 1, fixedLineNameResolver);
		final PedigreeString maleLeafPedigreeString =
				this.inbredProcessor.processGermplasmNode(germplasmNode.getMaleParent(), level - 1, fixedLineNameResolver);

		final PedigreeString pedigreeString = new PedigreeString();
		pedigreeString.setPedigree(PedigreeStringGeneratorUtil.gerneratePedigreeString(femaleLeafPedigreeString, maleLeafPedigreeString));
		return pedigreeString;
	}

}
