
package org.generationcp.middleware.service.pedigree.string.processors;

import org.generationcp.middleware.service.pedigree.GermplasmNode;
import org.generationcp.middleware.service.pedigree.PedigreeString;
import org.generationcp.middleware.service.pedigree.string.util.FixedLineNameResolver;
import org.generationcp.middleware.service.pedigree.string.util.PedigreeStringGeneratorUtil;


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

	private InbredProcessor inbredProcessor = new InbredProcessor();;

	private PedigreeStringBuilder pedigreeStringBuilder = new PedigreeStringBuilder();

	@Override
	public PedigreeString processGermplasmNode(final GermplasmNode germplasmNode, final Integer level, final FixedLineNameResolver fixedLineNameResolver) {

		final PedigreeString femaleSingleCrossHybridPedigreeString =
				constructPedigreeStringForSingleCrossHybrids(germplasmNode.getFemaleParent(), level, fixedLineNameResolver);
		final PedigreeString maleSingleCrossHybridPedigreeString =
				constructPedigreeStringForSingleCrossHybrids(germplasmNode.getMaleParent(), level, fixedLineNameResolver);

		final PedigreeString doubleCrossPedigreeString = new PedigreeString();
		doubleCrossPedigreeString.setNumberOfCrosses(femaleSingleCrossHybridPedigreeString.getNumberOfCrosses()
				+ maleSingleCrossHybridPedigreeString.getNumberOfCrosses());
		doubleCrossPedigreeString.setPedigree(PedigreeStringGeneratorUtil.gerneratePedigreeString(femaleSingleCrossHybridPedigreeString,
				maleSingleCrossHybridPedigreeString));
		return doubleCrossPedigreeString;
	}

	private PedigreeString constructPedigreeStringForSingleCrossHybrids(final GermplasmNode singleCrossHybrids, Integer level, final FixedLineNameResolver fixedLineNameResolver) {
		if (singleCrossHybrids != null) {
			return constructPedigreeString(singleCrossHybrids, level, fixedLineNameResolver);
		}
		return inbredProcessor.processGermplasmNode(singleCrossHybrids, level-1, fixedLineNameResolver);
	}

	private PedigreeString constructPedigreeString(final GermplasmNode node, final Integer level, final FixedLineNameResolver fixedLineNameResolver) {
		final PedigreeString femalePedigreeString = pedigreeStringBuilder.buildPedigreeString(node.getFemaleParent(), level-1, fixedLineNameResolver);
		final PedigreeString malePedigreeString = pedigreeStringBuilder.buildPedigreeString(node.getMaleParent(), level-1, fixedLineNameResolver);
		final PedigreeString femaleCrossPedigreeString = new PedigreeString();
		femaleCrossPedigreeString.setNumberOfCrosses(femalePedigreeString.getNumberOfCrosses() + malePedigreeString.getNumberOfCrosses()
				+ 1);
		femaleCrossPedigreeString
				.setPedigree(PedigreeStringGeneratorUtil.gerneratePedigreeString(femalePedigreeString, malePedigreeString));
		return femaleCrossPedigreeString;
	}


}
