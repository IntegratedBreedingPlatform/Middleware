
package org.generationcp.middleware.service.pedigree.string.processors;

import org.generationcp.middleware.service.pedigree.GermplasmNode;
import org.generationcp.middleware.service.pedigree.PedigreeString;
import org.generationcp.middleware.service.pedigree.string.util.FixedLineNameResolver;
import org.generationcp.middleware.service.pedigree.string.util.PedigreeStringGeneratorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The most prevalent type of hybrid that was grown in the United States in the 1930’s and 1940’s is known as a double-cross hybrid. As the
 * name implies, producing a double-cross hybrid requires two stages of crossing involving two pairs of inbreds (inbred line or simply a
 * line is a pure breeding strain). Two pairs of inbreds, A and B and Y and Z, are crossed to produce single-cross hybrids, AB and YZ. In
 * Then the two single-cross hybrids produced further are crossed to produce the double-cross. Typically, A and B are closely related and Y
 * and Z are also closely related, but neither A nor B is closely related to Y or Z.
 *
 *						 				  A x B	   Y x Z
 *						 					 \		/
 * (femaleSingleCrossHybridPedigreeString) 	A/B   Y/Z (maleSingleCrossHybridPedigreeString)
 *						 		  			  \	   /
 *					 						A/B//Y/Z (doubleCrossPedigreeString)
 */

public class DoubleCrossProcessor implements BreedingMethodProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(DoubleCrossProcessor.class);

	private final InbredProcessor inbredProcessor = new InbredProcessor();;

	private final PedigreeStringBuilder pedigreeStringBuilder = new PedigreeStringBuilder();



	@Override
	public PedigreeString processGermplasmNode(final GermplasmNode germplasmNode, final Integer level,
			final FixedLineNameResolver fixedLineNameResolver, final boolean originatesFromComplexCross) {

		if(germplasmNode != null && germplasmNode.getGermplasm() != null && germplasmNode.getGermplasm().getGid() != null) {
			LOG.debug("Germplasm with GID '{}' has a double cross breeding method. "
					+ "Processing using double cross processor.", germplasmNode.getGermplasm().getGid());
		}

		final PedigreeString femaleSingleCrossHybridPedigreeString =
				this.constructPedigreeStringForSingleCrossHybrids(germplasmNode.getFemaleParent(), level, fixedLineNameResolver, originatesFromComplexCross);
		final PedigreeString maleSingleCrossHybridPedigreeString =
				this.constructPedigreeStringForSingleCrossHybrids(germplasmNode.getMaleParent(), level, fixedLineNameResolver, originatesFromComplexCross);

		final PedigreeString doubleCrossPedigreeString = new PedigreeString();
		doubleCrossPedigreeString.setNumberOfCrosses(femaleSingleCrossHybridPedigreeString.getNumberOfCrosses() + 1);
		doubleCrossPedigreeString.setPedigree(PedigreeStringGeneratorUtil.gerneratePedigreeString(femaleSingleCrossHybridPedigreeString,
				maleSingleCrossHybridPedigreeString));
		return doubleCrossPedigreeString;
	}

	private PedigreeString constructPedigreeStringForSingleCrossHybrids(final GermplasmNode singleCrossHybrids, final Integer level,
			final FixedLineNameResolver fixedLineNameResolver, final boolean originatesFromComplexCross) {
		if (singleCrossHybrids != null) {
			return this.constructPedigreeString(singleCrossHybrids, level, fixedLineNameResolver, originatesFromComplexCross);
		}
		return this.inbredProcessor.processGermplasmNode(singleCrossHybrids, level - 1, fixedLineNameResolver, originatesFromComplexCross);
	}

	private PedigreeString constructPedigreeString(final GermplasmNode node, final Integer level,
			final FixedLineNameResolver fixedLineNameResolver, final boolean originatesFromComplexCross) {
		final PedigreeString femalePedigreeString = getPedigreeString(node.getFemaleParent(), level, fixedLineNameResolver, originatesFromComplexCross);
		final PedigreeString malePedigreeString = getPedigreeString(node.getMaleParent(), level, fixedLineNameResolver, originatesFromComplexCross);
		final PedigreeString femaleCrossPedigreeString = new PedigreeString();
		femaleCrossPedigreeString.setNumberOfCrosses(femalePedigreeString.getNumberOfCrosses() + malePedigreeString.getNumberOfCrosses()
					+ 1);
		femaleCrossPedigreeString
		.setPedigree(PedigreeStringGeneratorUtil.gerneratePedigreeString(femalePedigreeString, malePedigreeString));
		return femaleCrossPedigreeString;
	}

	private PedigreeString getPedigreeString(final GermplasmNode node, final Integer level, final FixedLineNameResolver fixedLineNameResolver,
			final boolean originatesFromComplexCross) {
		if(node != null) {
			return this.pedigreeStringBuilder.buildPedigreeString(node, level - 1, fixedLineNameResolver, originatesFromComplexCross);
		} 
		return this.inbredProcessor.processGermplasmNode(node, level - 1, fixedLineNameResolver, originatesFromComplexCross);

	}
}
