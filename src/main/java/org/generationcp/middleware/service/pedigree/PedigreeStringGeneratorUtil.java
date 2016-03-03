
package org.generationcp.middleware.service.pedigree;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.generationcp.middleware.util.CrossExpansionProperties;

import com.google.common.base.Optional;

public class PedigreeStringGeneratorUtil {

	static String gerneratePedigreeString(final PedigreeString femalePedigreeString, final PedigreeString malePedigreeString) {
		return femalePedigreeString.getPedigree() + PedigreeStringGeneratorUtil.getSeperator(femalePedigreeString.getNumberOfCrosses())
				+ malePedigreeString.getPedigree();
	}

	static String gernerateBackcrossPedigreeString(final PedigreeString recurringParentString, final PedigreeString donorParentString,
			final int numberOfRecurringParents, final boolean isFemaleRecurringParent, final FixedLineNameResolver fixedLineNameResolver) {
		return recurringParentString.getPedigree()
				+ PedigreeStringGeneratorUtil.getSeperator(isFemaleRecurringParent, numberOfRecurringParents, fixedLineNameResolver)
				+ donorParentString.getPedigree();
	}

	static Optional<PedigreeString> getFixedLineName(final GermplasmNode germplasmNode, final FixedLineNameResolver fixedLineNameResolver) {
		final Optional<String> nameTypeBasedResolution = fixedLineNameResolver.nameTypeBasedResolution(germplasmNode);
		if (nameTypeBasedResolution.isPresent()) {
			final PedigreeString pedigreeString = new PedigreeString();
			pedigreeString.setPedigree(nameTypeBasedResolution.get());
			return Optional.fromNullable(pedigreeString);
		}
		return Optional.fromNullable(null);
	}

	static String getSeperator(final boolean isFemaleRecurringParent,
			final int numberOfCrosses,
			final FixedLineNameResolver fixedLineNameResolver) {

		final CrossExpansionProperties crossExpansionProperties = fixedLineNameResolver.getCrossExpansionProperties();
		final ImmutablePair<String, String> backcrossNotation = crossExpansionProperties.getBackcrossNotation(fixedLineNameResolver.getCropName());

		if (!isFemaleRecurringParent) {
			return "/" + numberOfCrosses + backcrossNotation.right;
		} else {
			return backcrossNotation.left + numberOfCrosses + "/";
		}
	}

	static String getSeperator(final int numberOfCrosses) {
		// of crosses made
		if (numberOfCrosses == 0) {
			return "/";
		} else if (numberOfCrosses == 1) {
			return "//";
		} else if (numberOfCrosses == 2) {
			return "///";
		} else {
			return "/" + (numberOfCrosses + 1) + "/";
		}
	}
}
