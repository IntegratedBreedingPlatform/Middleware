
package org.generationcp.middleware.service.pedigree.string.processors;

import org.generationcp.middleware.service.pedigree.GermplasmNode;
import org.generationcp.middleware.service.pedigree.PedigreeString;
import org.generationcp.middleware.service.pedigree.string.util.FixedLineNameResolver;

/**
 * Implemented by different breeding methods to perform custom pedigree string construction actions.
 */
public interface BreedingMethodProcessor {

	/**
	 * Implementations will do appropriate work according to the breeding method implemented
	 *
	 * @param germplasmNode the node to process
	 * @param level the number of levels to iterated
	 * @param fixedLineNameResolver resolver that enables us to resolve fixed lines in case of encountring a fixed line
	 * @return the resulting pedigree string
	 */
	PedigreeString processGermplasmNode(GermplasmNode germplasmNode, Integer level, FixedLineNameResolver fixedLineNameResolver);

}
