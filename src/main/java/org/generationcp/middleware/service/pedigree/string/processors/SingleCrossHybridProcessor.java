
package org.generationcp.middleware.service.pedigree.string.processors;

import org.generationcp.middleware.service.pedigree.GermplasmNode;
import org.generationcp.middleware.service.pedigree.PedigreeString;
import org.generationcp.middleware.service.pedigree.string.util.FixedLineNameResolver;
import org.generationcp.middleware.service.pedigree.string.util.PedigreeStringGeneratorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A hybrid plant results from a cross of two genetically different plants. The two parents of a single-cross hybrid, which is also known as
 * a F1 hybrid, are inbreds. Each seed produced from crossing two inbreds has an array (collection) of alleles from each parent. Those two
 * arrays will be different if the inbreds are genetically different, but each seed contains the same female array and the same male array.
 * Thus, all plants of the same single-cross hybrid are genetically identical. At every locus where the two inbred parents possess different
 * alleles, the single-cross hybrid is heterozygous.
 * 	A	 B
 * 	 \ /
 * 	 A/B
 */
public class SingleCrossHybridProcessor implements BreedingMethodProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(SingleCrossHybridProcessor.class);

	final PedigreeStringBuilder pedigreeStringBuilder = new PedigreeStringBuilder();

	final InbredProcessor inbredProcessor = new InbredProcessor();

	private final int levelSubtractor;


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

	public int getLevelSubtractor() {
		return this.levelSubtractor;
	}

	@Override
	public PedigreeString processGermplasmNode(final GermplasmNode germplasmNode, final Integer level,
			final FixedLineNameResolver fixedLineNameResolver, final boolean originatesFromComplexCross) {

		if(germplasmNode != null && germplasmNode.getGermplasm() != null && germplasmNode.getGermplasm().getGid() != null) {
			LOG.debug("Germplasm with GID '{}' is being processed by an single cross processor. "
					, germplasmNode.getGermplasm().getGid());
		}
		
		final PedigreeString femalePedigreeString = this.getPedigreeString(level, fixedLineNameResolver, germplasmNode.getFemaleParent(), originatesFromComplexCross);

		final PedigreeString malePedigreeString = this.getPedigreeString(level, fixedLineNameResolver, germplasmNode.getMaleParent(), originatesFromComplexCross);

		final PedigreeString pedigreeString = new PedigreeString();
		pedigreeString.setNumberOfCrosses(femalePedigreeString.getNumberOfCrosses() + 1);
		pedigreeString.setPedigree(PedigreeStringGeneratorUtil.gerneratePedigreeString(femalePedigreeString, malePedigreeString));
		return pedigreeString;
	}

	private PedigreeString getPedigreeString(final Integer level, final FixedLineNameResolver fixedLineNameResolver,
			final GermplasmNode germplasmNode, final boolean originatesFromComplexCross) {
		boolean complexCross = levelSubtractor == 0 ? true : originatesFromComplexCross;
		if (germplasmNode != null) {
			return this.pedigreeStringBuilder.buildPedigreeString(germplasmNode, level - this.levelSubtractor, fixedLineNameResolver, complexCross);
		}
		return this.inbredProcessor.processGermplasmNode(germplasmNode, level - this.levelSubtractor, fixedLineNameResolver, complexCross);
	}

}
