
package org.generationcp.middleware.service.pedigree.string.processors;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.service.pedigree.GermplasmNode;
import org.generationcp.middleware.service.pedigree.PedigreeString;
import org.generationcp.middleware.service.pedigree.string.util.FixedLineNameResolver;
import org.generationcp.middleware.service.pedigree.string.util.PedigreeStringGeneratorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import static org.generationcp.middleware.util.Debug.debug;

/**
 * Enables us to construct a Pedigree string from a germplasm and its ancestor history.
 *
 */
public class PedigreeStringBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(PedigreeStringBuilder.class);

	private final InbredProcessor inbredProcessor = new InbredProcessor();;

	public PedigreeString buildPedigreeString(final GermplasmNode germplasmNode, final int level,
			final FixedLineNameResolver fixedLineNameResolver, final boolean originatesFromComplexCross) {

		final Monitor monitor = MonitorFactory.start("org.generationcp.middleware.service.pedigree.string.processors.PedigreeStringBuilder.buildPedigreeString(GermplasmNode, int, FixedLineNameResolver, boolean)");

		try {
			Preconditions.checkNotNull(germplasmNode);
	
			final Germplasm germplasm = germplasmNode.getGermplasm();
	
			if(germplasmNode != null && germplasmNode.getGermplasm() != null && germplasmNode.getGermplasm().getGid() != null) {
				debug("Building pedigree tree for germlasm with gid - '{}'", germplasmNode.getGermplasm().getGid());
			}
	
			final Optional<PedigreeString> fixedLineName = PedigreeStringGeneratorUtil.getFixedLineName(germplasmNode, fixedLineNameResolver);
			if (fixedLineName.isPresent()) {
				return fixedLineName.get();
			}
	
			if (level == 0) {
				return this.inbredProcessor.processGermplasmNode(germplasmNode, level, fixedLineNameResolver, originatesFromComplexCross);
			}
	
			// Is this germplasm that is a result of a derivative or maintenance breeding method. If so skip node.
			if (germplasm != null && germplasm.getGnpgs() < 0) {
				return this.processDerivativeOrMaintenceGermplasm(germplasmNode, level, fixedLineNameResolver, originatesFromComplexCross);
			}
	
			// Get breeding method used to create this germplasm.
			final BreedingMethodProcessor methodProcessor = BreedingMethodFactory.getMethodProcessor(germplasmNode);
			return methodProcessor.processGermplasmNode(germplasmNode, level, fixedLineNameResolver, originatesFromComplexCross);
		} finally {
			debug("" + monitor.stop());
		}
	}

	private PedigreeString processDerivativeOrMaintenceGermplasm(final GermplasmNode germplasmNode, final int level,
			final FixedLineNameResolver fixedLineNameResolver, final boolean originatesFromComplexCross) {

		final GermplasmNode femaleParent = germplasmNode.getFemaleParent();

		if (femaleParent != null && !originatesFromComplexCross) {
			// Note derivative or maintenance methods are not consider a level increment
			return this.buildPedigreeString(germplasmNode.getFemaleParent(), level, fixedLineNameResolver, originatesFromComplexCross);
		}

		return this.inbredProcessor.processGermplasmNode(germplasmNode, level - 1, fixedLineNameResolver, originatesFromComplexCross);

	}

}
