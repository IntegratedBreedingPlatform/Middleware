
package org.generationcp.middleware.service.pedigree.string.util;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.generationcp.middleware.service.pedigree.GermplasmNode;
import org.generationcp.middleware.service.pedigree.PedigreeString;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

/**
 * Utility to help us generate pedigree strings.
 *
 */
public class PedigreeStringGeneratorUtil {

	private static final Logger LOG = LoggerFactory.getLogger(PedigreeStringGeneratorUtil.class);

	public static String gerneratePedigreeString(final PedigreeString femalePedigreeString, final PedigreeString malePedigreeString) {

		LOG.debug("Combining pedigree string. FemalePedigreeString '{}', MalePedigreeString '%s', Number of crosses - '{}'",
				femalePedigreeString.toString(), malePedigreeString.toString(), femalePedigreeString.getNumberOfCrosses());

		return femalePedigreeString.getPedigree() + PedigreeStringGeneratorUtil.getSeperator(femalePedigreeString.getNumberOfCrosses())
				+ malePedigreeString.getPedigree();
	}

	public static String gernerateBackcrossPedigreeString(final PedigreeString donorParentString,
			final PedigreeString recurringParentString, final FixedLineNameResolver fixedLineNameResolver,
			final int numberOfRecurringParents, final boolean isFemaleRecurringParent) {

		LOG.debug("Combining pedigree string. Donor Parent String - '%s', Recurring Parent String - '{}', Number of Recurrsions - '{}'",
				donorParentString.toString(), recurringParentString.toString(), numberOfRecurringParents);

		return recurringParentString.getPedigree()
				+ PedigreeStringGeneratorUtil.getSeperator(isFemaleRecurringParent, numberOfRecurringParents, fixedLineNameResolver)
				+ donorParentString.getPedigree();
	}

	public static Optional<PedigreeString> getFixedLineName(final GermplasmNode germplasmNode,
			final FixedLineNameResolver fixedLineNameResolver) {
		final Optional<String> nameTypeBasedResolution = fixedLineNameResolver.nameTypeBasedResolution(germplasmNode);
		if (nameTypeBasedResolution.isPresent()) {

			final String resolvedNameType = nameTypeBasedResolution.get();

			LOG.debug("Name type of '{}' resolved for gid - '{}'", resolvedNameType, germplasmNode.getGermplasm().getGid());

			final PedigreeString pedigreeString = new PedigreeString();
			pedigreeString.setPedigree(resolvedNameType);
			return Optional.fromNullable(pedigreeString);
		}
		return Optional.fromNullable(null);
	}

	private static String getSeperator(final boolean isFemaleRecurringParent, final int numberOfCrosses,
			final FixedLineNameResolver fixedLineNameResolver) {

		final CrossExpansionProperties crossExpansionProperties = fixedLineNameResolver.getCrossExpansionProperties();
		final ImmutablePair<String, String> backcrossNotation =
				crossExpansionProperties.getBackcrossNotation(fixedLineNameResolver.getCropName());

		if (!isFemaleRecurringParent) {
			return "/" + numberOfCrosses + backcrossNotation.right;
		} else {
			return backcrossNotation.left + numberOfCrosses + "/";
		}
	}

	private static String getSeperator(final int numberOfPreviousCrosses) {
		// of crosses made
		if (numberOfPreviousCrosses == 0) {
			return "/";
		} else if (numberOfPreviousCrosses == 1) {
			return "//";
		} else if (numberOfPreviousCrosses == 2) {
			return "///";
		} else {
			return "/" + (numberOfPreviousCrosses + 1) + "/";
		}
	}
}
