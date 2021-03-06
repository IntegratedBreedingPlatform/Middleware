
package org.generationcp.middleware.service.pedigree.string.processors;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.service.pedigree.GermplasmNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory method that enables us to select the correct breeding method processor according to the germplasm node provided. The breeding
 * method processor can be used to create the appropriate string.
 */
public class BreedingMethodFactory {

	private static final Logger LOG = LoggerFactory.getLogger(BreedingMethodFactory.class);


	public static BreedingMethodProcessor getMethodProcessor(final GermplasmNode germplasmNode) {
		// FIXME: This is not sustainable. We need to do the logic on unique method codes.
		final String methodName = BreedingMethodFactory.getMethodName(germplasmNode);

		if(germplasmNode != null && germplasmNode.getGermplasm() != null && germplasmNode.getGermplasm().getGid() != null) {
			LOG.debug("Germplasm with GID '{}' has a method name '{}'", germplasmNode.getGermplasm().getGid(), methodName);

		// If germplasm node is null or has no GID, return inbred processor by default
		} else {
			return new InbredProcessor();
		}

		if (methodName.contains("single cross")) {
			return new SingleCrossHybridProcessor();
		} else if (methodName.contains("double cross")) {
			return new DoubleCrossProcessor();
		} else if (methodName.contains("three-way cross")) {
			return new ThreeWayHybridProcessor();
		} else if (methodName.contains("backcross")) {
			return new BackcrossProcessor();
		} else if (methodName.contains("cross") && methodName.contains("complex")) {
			return new SingleCrossHybridProcessor(0);
		} else if (methodName.contains("cross")) {
			return new SimpleCrossProcessor();
		}

		//  Any method for which there is not cross expansion algorithm should be treated like a single cross
		return new SingleCrossHybridProcessor();
	}

	private static String getMethodName(final GermplasmNode germplasmNode) {

		if (germplasmNode != null && germplasmNode.getMethod() != null && StringUtils.isNotBlank(germplasmNode.getMethod().getMname())) {
			final String methodName = germplasmNode.getMethod().getMname();
			return methodName.toLowerCase();
		}
		return "";
	}

}
