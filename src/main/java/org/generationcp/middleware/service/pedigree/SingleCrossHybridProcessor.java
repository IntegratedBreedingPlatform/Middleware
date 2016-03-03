
package org.generationcp.middleware.service.pedigree;

import com.google.common.base.Optional;

/**
 * A hybrid plant results from a cross of two genetically different plants. The two parents of a single-cross hybrid, which is also known as
 * a F1 hybrid, are inbreds. Each seed produced from crossing two inbreds has an array (collection) of alleles from each parent. Those two
 * arrays will be different if the inbreds are genetically different, but each seed contains the same female array and the same male array.
 * Thus, all plants of the same single-cross hybrid are genetically identical. At every locus where the two inbred parents possess different
 * alleles, the single-cross hybrid is heterozygous.
 * 	A 	B
 *	 \ /
 *	 A/B
 */
public class SingleCrossHybridProcessor implements BreedingMethodProcessor {

	final PedigreeStringBuilder pedigreeStringBuilder = new PedigreeStringBuilder();

	final InbredProcessor inbredProcessor = new InbredProcessor();

	private int levelSubtractor;

	/**
	 * This processor is used by a complex cross. Please note a complex cross does not increment the level ( need to checc this) and thus
	 * will pass in a value of 0.
	 *
	 * @param levelSubtractor a numerical value that enables us to control the perceived level while generating a pedigree string.
	 */
	public SingleCrossHybridProcessor(final int levelSubtractor) {
		this.levelSubtractor = levelSubtractor;
	}

	public SingleCrossHybridProcessor() {
		this.levelSubtractor = 1;
	}

	@Override
	public PedigreeString processGermplasmNode(final GermplasmNode germplasmNode, final Integer level,
			final FixedLineNameResolver fixedLineNameResolver) {

		if (level == 0) {
			return inbredProcessor.processGermplasmNode(germplasmNode, level, fixedLineNameResolver);
		}

		final Optional<PedigreeString> fixedLineName = PedigreeStringGeneratorUtil.getFixedLineName(germplasmNode, fixedLineNameResolver);
		if (fixedLineName.isPresent()) {
			return fixedLineName.get();
		}

		final GermplasmNode femaleParentNode = germplasmNode.getFemaleParent();
		final PedigreeString femalePedigreeString;
		if (femaleParentNode != null) {
			femalePedigreeString =
					pedigreeStringBuilder.buildPedigreeString(femaleParentNode, level - levelSubtractor, fixedLineNameResolver);
		} else {
			femalePedigreeString = inbredProcessor.processGermplasmNode(femaleParentNode, level - levelSubtractor, fixedLineNameResolver);
		}

		final GermplasmNode maleParentNode = germplasmNode.getMaleParent();
		final PedigreeString malePedigreeString;

		if (maleParentNode != null) {
			malePedigreeString = pedigreeStringBuilder.buildPedigreeString(maleParentNode, level - levelSubtractor, fixedLineNameResolver);
		} else {
			malePedigreeString = inbredProcessor.processGermplasmNode(maleParentNode, level - levelSubtractor, fixedLineNameResolver);
		}

		final PedigreeString pedigreeString = new PedigreeString();
		pedigreeString.setNumberOfCrosses(femalePedigreeString.getNumberOfCrosses() + 1);
		pedigreeString.setPedigree(PedigreeStringGeneratorUtil.gerneratePedigreeString(femalePedigreeString, malePedigreeString));
		return pedigreeString;
	}

}
