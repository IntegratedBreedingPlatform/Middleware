
package org.generationcp.middleware.service.pedigree;


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

	final PedigreeStringBuilder pedigreeStringBuilder = new PedigreeStringBuilder();

	final InbredProcessor inbredProcessor = new InbredProcessor();

	@Override
	public PedigreeString processGermplasmNode(final GermplasmNode germplasmNode, final Integer level,
			FixedLineNameResolver fixedLineNameResolver) {

		final GermplasmNode femaleParentNode = germplasmNode.getFemaleParent();
		final GermplasmNode maleParentNode = germplasmNode.getMaleParent();

		// Female is the single cross hybrid and the male is an inbread
		if (femaleParentNode != null && femaleParentNode.getGermplasm().getGnpgs() > 0) {
			return constructPedigreeString(femaleParentNode, maleParentNode, level, fixedLineNameResolver);
			// Male is the single cross hybrid and the female is an inbread
		} else if (maleParentNode != null) {
			return constructPedigreeString(maleParentNode, femaleParentNode, level, fixedLineNameResolver);
		}

		final PedigreeString femalePedigreeString =
				inbredProcessor.processGermplasmNode(femaleParentNode, level - 1, fixedLineNameResolver);
		final PedigreeString malePedigreeString = inbredProcessor.processGermplasmNode(maleParentNode, level - 1, fixedLineNameResolver);
		final PedigreeString pedigreeString = new PedigreeString();
		pedigreeString.setNumberOfCrosses(1);
		pedigreeString.setPedigree(PedigreeStringGeneratorUtil.gerneratePedigreeString(femalePedigreeString, malePedigreeString));
		return pedigreeString;

	}

	private PedigreeString constructPedigreeString(final GermplasmNode singleCrossHybrids, final GermplasmNode inbread,
			final Integer level, final FixedLineNameResolver fixedLineNameResolver) {

		final GermplasmNode singleCrossHybridFemaleParent = singleCrossHybrids.getFemaleParent();
		final GermplasmNode singleCrossHybridMaleParent = singleCrossHybrids.getMaleParent();

		final PedigreeString singleCrossHybridFemaleParentPedigreeString =
				pedigreeStringBuilder.buildPedigreeString(singleCrossHybridFemaleParent, level - 1, fixedLineNameResolver);
		final PedigreeString singleCrossHybridMaleParentPedigreeString =
				pedigreeStringBuilder.buildPedigreeString(singleCrossHybridMaleParent, level - 1, fixedLineNameResolver);

		final PedigreeString singleCrossHybridPedigreeString = new PedigreeString();
		singleCrossHybridPedigreeString.setNumberOfCrosses(singleCrossHybridFemaleParentPedigreeString.getNumberOfCrosses() + 1);
		singleCrossHybridPedigreeString.setPedigree(PedigreeStringGeneratorUtil.gerneratePedigreeString(
				singleCrossHybridFemaleParentPedigreeString, singleCrossHybridMaleParentPedigreeString));

		final PedigreeString inbreadPedigreeString = inbredProcessor.processGermplasmNode(inbread, level - 1, fixedLineNameResolver);

		final PedigreeString pedigreeString = new PedigreeString();
		pedigreeString.setNumberOfCrosses(singleCrossHybridPedigreeString.getNumberOfCrosses() + 1);
		pedigreeString.setPedigree(PedigreeStringGeneratorUtil.gerneratePedigreeString(singleCrossHybridPedigreeString,
				inbreadPedigreeString));
		return pedigreeString;

	}

}
