
package org.generationcp.middleware.service.pedigree.string.processors;

import org.generationcp.middleware.service.pedigree.GermplasmNode;
import org.generationcp.middleware.service.pedigree.PedigreeString;
import org.generationcp.middleware.service.pedigree.string.util.FixedLineNameResolver;
import org.generationcp.middleware.service.pedigree.string.util.PedigreeStringGeneratorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Result from the cross between one parent that is an F1 hybrid and the other is from an inbred (inbred line or simply a line is a pure
 * breeding strain) line.
 * 						 A x B
 *							\
 *		 (femaleParentNode) A/B   C (maleParentNode)
 *							  \	  /
 *							A/B//C
 *
 */
public class ThreeWayHybridProcessor implements BreedingMethodProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(ThreeWayHybridProcessor.class);

	final PedigreeStringBuilder pedigreeStringBuilder = new PedigreeStringBuilder();

	final InbredProcessor inbredProcessor = new InbredProcessor();


	@Override
	public PedigreeString processGermplasmNode(final GermplasmNode germplasmNode, final Integer level,
			final FixedLineNameResolver fixedLineNameResolver, final boolean originatesFromComplexCross) {

		if(germplasmNode != null && germplasmNode.getGermplasm() != null && germplasmNode.getGermplasm().getGid() != null) {
			LOG.debug("Germplasm with GID '{}' is being processed by a three way hybrid processor. "
					, germplasmNode.getGermplasm().getGid());
		}

		final GermplasmNode femaleParentNode = germplasmNode.getFemaleParent();
		final GermplasmNode maleParentNode = germplasmNode.getMaleParent();

		// Female is the single cross hybrid and the male is an inbred
		if (femaleParentNode != null && femaleParentNode.getGermplasm().getGnpgs() > 0) {
			return this.constructPedigreeString(true, femaleParentNode, maleParentNode, level, fixedLineNameResolver, originatesFromComplexCross);
			// Male is the single cross hybrid and the female is an inbred
		} else if (maleParentNode != null) {
			return this.constructPedigreeString(false, maleParentNode, femaleParentNode, level, fixedLineNameResolver, originatesFromComplexCross);
		}

		final PedigreeString femalePedigreeString =
				this.inbredProcessor.processGermplasmNode(femaleParentNode, level - 1, fixedLineNameResolver, originatesFromComplexCross);
		final PedigreeString malePedigreeString =
				this.inbredProcessor.processGermplasmNode(maleParentNode, level - 1, fixedLineNameResolver, originatesFromComplexCross);
		final PedigreeString pedigreeString = new PedigreeString();
		pedigreeString.setNumberOfCrosses(1);
		pedigreeString.setPedigree(PedigreeStringGeneratorUtil.gerneratePedigreeString(femalePedigreeString, malePedigreeString));
		return pedigreeString;

	}

	private PedigreeString constructPedigreeString(final Boolean femaleIsCross, final GermplasmNode singleCrossHybrids, final GermplasmNode inbread,
			final Integer level, final FixedLineNameResolver fixedLineNameResolver, final boolean originatesFromComplexCross) {

		final GermplasmNode singleCrossHybridFemaleParent = singleCrossHybrids.getFemaleParent();
		final GermplasmNode singleCrossHybridMaleParent = singleCrossHybrids.getMaleParent();

		final PedigreeString singleCrossHybridFemaleParentPedigreeString = getPedigreeString(level, fixedLineNameResolver, singleCrossHybridFemaleParent, originatesFromComplexCross);
		final PedigreeString singleCrossHybridMaleParentPedigreeString = getPedigreeString(level, fixedLineNameResolver, singleCrossHybridMaleParent, originatesFromComplexCross);

		final PedigreeString singleCrossHybridPedigreeString = new PedigreeString();
		singleCrossHybridPedigreeString.setNumberOfCrosses(singleCrossHybridFemaleParentPedigreeString.getNumberOfCrosses() + 1);
		singleCrossHybridPedigreeString.setPedigree(PedigreeStringGeneratorUtil.gerneratePedigreeString(
				singleCrossHybridFemaleParentPedigreeString, singleCrossHybridMaleParentPedigreeString));

		final PedigreeString inbreadPedigreeString = this.inbredProcessor.processGermplasmNode(inbread, level - 1, fixedLineNameResolver, originatesFromComplexCross);

		final PedigreeString pedigreeString = new PedigreeString();
		pedigreeString.setNumberOfCrosses(singleCrossHybridPedigreeString.getNumberOfCrosses() + 1);
		// Female pedigree string always comes first
		pedigreeString.setPedigree(femaleIsCross? PedigreeStringGeneratorUtil.gerneratePedigreeString(singleCrossHybridPedigreeString,
				inbreadPedigreeString) : PedigreeStringGeneratorUtil.gerneratePedigreeString(inbreadPedigreeString,
			singleCrossHybridMaleParentPedigreeString));
		return pedigreeString;

	}

	private PedigreeString getPedigreeString(final Integer level, final FixedLineNameResolver fixedLineNameResolver,
			final GermplasmNode singleCrossHybridFemaleParent, final boolean originatesFromComplexCross) {
		if(singleCrossHybridFemaleParent != null) {
			return this.pedigreeStringBuilder.buildPedigreeString(singleCrossHybridFemaleParent, level - 1, fixedLineNameResolver, originatesFromComplexCross);
		}
		return this.inbredProcessor.processGermplasmNode(singleCrossHybridFemaleParent, level - 1, fixedLineNameResolver, originatesFromComplexCross);
	}

}
