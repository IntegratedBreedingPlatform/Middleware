
package org.generationcp.middleware.service.pedigree;

/**
 * A hybrid plant results from a cross of two genetically different plants. The two parents of a single-cross hybrid, which is also known as
 * a F1 hybrid, are inbreds. Each seed produced from crossing two inbreds has an array (collection) of alleles from each parent. Those two
 * arrays will be different if the inbreds are genetically different, but each seed contains the same female array and the same male array.
 * Thus, all plants of the same single-cross hybrid are genetically identical. At every locus where the two inbred parents possess different
 * alleles, the single-cross hybrid is heterozygous. A B \ / A/B
 */
public class SingleCrossHybridProcessor implements BreedingMethodProcessor {

	final PedigreeStringBuilder pedigreeStringBuilder = new PedigreeStringBuilder();

	final InbredProcessor inbredProcessor = new InbredProcessor();

	private int levelSubtractor;

	/**
	 * This processor is used by a complex cross. Please note a complex cross does not increment the level ( need to check this) and thus
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

	int getLevelSubtractor() {
		return levelSubtractor;
	}

	@Override
	public PedigreeString processGermplasmNode(final GermplasmNode germplasmNode, final Integer level,
			final FixedLineNameResolver fixedLineNameResolver) {

		final PedigreeString femalePedigreeString = getPedigreeString(level, fixedLineNameResolver, germplasmNode.getFemaleParent());

		final PedigreeString malePedigreeString = getPedigreeString(level, fixedLineNameResolver, germplasmNode.getMaleParent());

		final PedigreeString pedigreeString = new PedigreeString();
		pedigreeString.setNumberOfCrosses(femalePedigreeString.getNumberOfCrosses() + 1);
		pedigreeString.setPedigree(PedigreeStringGeneratorUtil.gerneratePedigreeString(femalePedigreeString, malePedigreeString));
		return pedigreeString;
	}

	private PedigreeString getPedigreeString(final Integer level, final FixedLineNameResolver fixedLineNameResolver,
			final GermplasmNode germplasmNode) {
		if (germplasmNode != null) {
			return pedigreeStringBuilder.buildPedigreeString(germplasmNode, level - levelSubtractor, fixedLineNameResolver);
		}
		return inbredProcessor.processGermplasmNode(germplasmNode, level - levelSubtractor, fixedLineNameResolver);
	}

}
