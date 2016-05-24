
package org.generationcp.middleware.service.pedigree.string.processors;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.service.pedigree.GermplasmNode;
import org.generationcp.middleware.service.pedigree.PedigreeString;
import org.generationcp.middleware.service.pedigree.string.util.FixedLineNameResolver;
import org.generationcp.middleware.service.pedigree.string.util.PedigreeStringGeneratorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

/**
 * In plant breeding, inbred lines are used as stocks for the creation of hybrid lines to make use of the effects of heterosis. Inbreeding
 * in plants also occurs naturally in the form of self-pollination. As far as the pedigree string generation inbreds are the the top of the
 * tree and thus no further recursion is needed.
 */
public class InbredProcessor implements BreedingMethodProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(InbredProcessor.class);

	/**
	 * Used in case we cannot determine pedigree string using the preferred name or gid
	 */
	private static final String UNKNOWN_PEDIGREE_STRING = "Unknown";


	@Override
	public PedigreeString processGermplasmNode(final GermplasmNode germplasmNode, final Integer level,
			final FixedLineNameResolver fixedLineNameResolver, final boolean originatesFromComplexCross) {

		if(germplasmNode != null && germplasmNode.getGermplasm() != null && germplasmNode.getGermplasm().getGid() != null) {
			LOG.debug("Germplasm with GID '{}' is being processed by an inbread processor. "
					, germplasmNode.getGermplasm().getGid());
		}

		final Optional<PedigreeString> fixedLineName = PedigreeStringGeneratorUtil.getFixedLineName(germplasmNode, fixedLineNameResolver);
		if (fixedLineName.isPresent()) {
			return fixedLineName.get();
		}

		final PedigreeString pedigreeStringBuilders = new PedigreeString();

		if (germplasmNode == null || germplasmNode.getGermplasm() == null) {
			pedigreeStringBuilders.setPedigree(InbredProcessor.UNKNOWN_PEDIGREE_STRING);
			return pedigreeStringBuilders;
		}

		final Germplasm currentGermplasm = germplasmNode.getGermplasm();
		final Name nameObject = currentGermplasm.getPreferredName();
		if (nameObject == null || StringUtils.isBlank(nameObject.getNval())) {
			pedigreeStringBuilders.setPedigree(currentGermplasm.getGid().toString());
		} else {
			pedigreeStringBuilders.setPedigree(nameObject.getNval());
		}
		System.out.println(pedigreeStringBuilders.getPedigree());
		return pedigreeStringBuilders;

	}

}
