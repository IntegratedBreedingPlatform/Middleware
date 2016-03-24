
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

/**
 * In back crossing you have a donor parent (has a gene of interest) and a recurrent parent (an elite line that could be made better by
 * adding the gene of interest). The donor parent is crossed to the recurrent parent. The progeny of this cross is then crossed to the
 * recurrent parent (it is 'crossed back' to the recurrent parent, hence the term back cross). The progeny of this cross is selected for the
 * trait of interest and then crossed back to the recurrent parent. This process is repeated for as many back crosses as are needed to
 * create a line that is the recurrent parent with the gene of interest from the donor parent. The goal of backcrossing is to obtain a line
 * as identical as possible to the recurrent parent with the addition of the gene of interest that has been added through breeding.
 *														[238170 Backcross 2]
 *														/					\
 *												[238169 BC1 (BackCross1)]	[238167 Recurring Parent]
 *													/					\
 *											[238168 BC2 (Progeny)]		[238167 Recurring Parent]
 *											/		\
 *							[236052 Donor Parent]	[238167 Recurring Parent]
 */
public class BackcrossProcessor implements BreedingMethodProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(BackcrossProcessor.class);

	final private PedigreeStringBuilder pedigreeStringBuilder = new PedigreeStringBuilder();

	final private InbredProcessor inbredProcessor = new InbredProcessor();


	@Override
	public PedigreeString processGermplasmNode(final GermplasmNode germplasmNode, final Integer level,
			final FixedLineNameResolver fixedLineNameResolver) {

		if(germplasmNode != null && germplasmNode.getGermplasm() != null && germplasmNode.getGermplasm().getGid() != null) {
			LOG.debug("Germplasm with GID '{}' has a backcross breeding method. "
					+ "Processing using backcross processor.", germplasmNode.getGermplasm().getGid());
		}

		if (level == 0) {
			return this.inbredProcessor.processGermplasmNode(germplasmNode, level, fixedLineNameResolver);
		}

		final GermplasmNode femaleParent = germplasmNode.getFemaleParent();
		final GermplasmNode maleParent = germplasmNode.getMaleParent();

		// In order to have a backcross we must have two parents
		if (femaleParent != null && maleParent != null) {
			// Establish recurring parent
			final Optional<GermplasmNode> recurringParent = this.findRecurringParent(femaleParent, maleParent);
			if (recurringParent.isPresent()) {
				return this.computeBackcross(femaleParent, maleParent, recurringParent, level, fixedLineNameResolver);
			}
		}

		LOG.warn("Germplasm with GID '{}' has a backcross breeding method but no recurring parents were found. "
				+ "Just combining immediate parents. "
				+ "Please note the rest of the tree is not traversed.", germplasmNode.getGermplasm().getGid());

		// Not a backcross. Compute pedigree string just using immediate parents.
		final PedigreeString femaleLeafPedigreeString =
				this.inbredProcessor.processGermplasmNode(femaleParent, level - 1, fixedLineNameResolver);
		final PedigreeString maleLeafPedigreeString =
				this.inbredProcessor.processGermplasmNode(maleParent, level - 1, fixedLineNameResolver);

		final PedigreeString pedigreeString = new PedigreeString();
		pedigreeString.setPedigree(PedigreeStringGeneratorUtil.gerneratePedigreeString(femaleLeafPedigreeString, maleLeafPedigreeString));
		pedigreeString.setNumberOfCrosses(1);
		return pedigreeString;

	}

	private PedigreeString computeBackcross(final GermplasmNode femaleParent, final GermplasmNode maleParent,
			final Optional<GermplasmNode> recurringParent, final Integer level, final FixedLineNameResolver fixedLineNameResolver) {
		// Find donor parent
		final GermplasmNode donorParent = this.findDonorParentParent(femaleParent, maleParent, recurringParent.get());

		// Build donor parent string
		final PedigreeString donorParentString =
				this.pedigreeStringBuilder.buildPedigreeString(donorParent, level - 1, fixedLineNameResolver);

		// Build recurring parent string
		final PedigreeString recurringParentString =
				this.pedigreeStringBuilder.buildPedigreeString(recurringParent.get(), level - 1, fixedLineNameResolver);

		// Count number of recurring parent
		final int recurringParentCount;
		if (recurringParent.get() == maleParent) {
			recurringParentCount = this.recurringParentCount(femaleParent, recurringParent.get().getGermplasm().getGid());
		} else {
			recurringParentCount = this.recurringParentCount(maleParent, recurringParent.get().getGermplasm().getGid());
		}

		return
				this.buildBackcrossPedigreeString(femaleParent, recurringParent, recurringParentCount, recurringParentString,
						donorParentString, fixedLineNameResolver);
	}

	private PedigreeString buildBackcrossPedigreeString(final GermplasmNode femaleParent, final Optional<GermplasmNode> recurringParent,
			final int recurringParentCount, final PedigreeString recurringParentString, final PedigreeString nonRecurringParentString,
			final FixedLineNameResolver fixedLineNameResolver) {
		final PedigreeString backcrossPedigreeString = new PedigreeString();
		if (recurringParent.get() == femaleParent) {
			backcrossPedigreeString.setPedigree(PedigreeStringGeneratorUtil.gernerateBackcrossPedigreeString(nonRecurringParentString,
					recurringParentString, fixedLineNameResolver, recurringParentCount, true));
		} else {
			backcrossPedigreeString.setPedigree(PedigreeStringGeneratorUtil.gernerateBackcrossPedigreeString(recurringParentString,
					nonRecurringParentString, fixedLineNameResolver, recurringParentCount, false));
		}
		return backcrossPedigreeString;
	}

	/**
	 * @param femaleParent the female parent in the backcross
	 * @param maleParent the male parent in the backcross
	 * @param recurringParent the recurring parent
	 * @return the donor parent
	 */
	private GermplasmNode findDonorParentParent(final GermplasmNode femaleParent, final GermplasmNode maleParent,
			final GermplasmNode recurringParent) {
		Preconditions.checkNotNull(femaleParent);
		Preconditions.checkNotNull(maleParent);
		Preconditions.checkNotNull(recurringParent.getGermplasm());
		Preconditions.checkNotNull(recurringParent.getGermplasm().getGid());

		if (recurringParent == femaleParent) {
			return this.traverseTreeForDonorParent(maleParent, recurringParent.getGermplasm().getGid());
		} else if (recurringParent == maleParent) {
			return this.traverseTreeForDonorParent(femaleParent, recurringParent.getGermplasm().getGid());

		}
		throw new IllegalStateException("Recurring parent has been calcualted incorrectly. Please contact administrator");
	}

	/**
	 * Recursive method for finding donor parent
	 *
	 * @param nodeContainingDonorParent Node that will house the donor parent
	 * @param recurringParentGid recurring parent gid
	 * @return {@link GermplasmNode} which is our recurring parent
	 */
	private GermplasmNode traverseTreeForDonorParent(final GermplasmNode nodeContainingDonorParent, final Integer recurringParentGid) {
		if (nodeContainingDonorParent == null) {
			return null;
		}
		final Germplasm otherParentGermplasm = nodeContainingDonorParent.getGermplasm();
		final Integer otherParentFemaleParentGid = otherParentGermplasm.getGpid1();
		final Integer otherParentMaleParentGid = otherParentGermplasm.getGpid2();
		if (otherParentFemaleParentGid != null && !otherParentFemaleParentGid.equals(recurringParentGid)
				&& otherParentMaleParentGid != null && !otherParentMaleParentGid.equals(recurringParentGid)) {
			return nodeContainingDonorParent;
		}

		if (otherParentFemaleParentGid != null && otherParentFemaleParentGid.equals(recurringParentGid)) {
			return this.traverseTreeForDonorParent(nodeContainingDonorParent.getMaleParent(), recurringParentGid);
		}

		if (otherParentMaleParentGid != null && otherParentMaleParentGid.equals(recurringParentGid)) {
			return this.traverseTreeForDonorParent(nodeContainingDonorParent.getFemaleParent(), recurringParentGid);
		}

		return nodeContainingDonorParent;
	}

	/**
	 * @param femaleParent female parent
	 * @param maleParent male parent
	 * @return {@link Optional<GermplasmNode>} which is or recurring parent. If the optional call is empty then no recurring parent was
	 *         found.
	 */
	private Optional<GermplasmNode> findRecurringParent(final GermplasmNode femaleParent, final GermplasmNode maleParent) {
		final Germplasm femaleParentGermplasm = femaleParent.getGermplasm();
		final Germplasm maleParentGermplasm = maleParent.getGermplasm();
		GermplasmNode recurringParent = null;
		if (maleParentGermplasm.getGnpgs() >= 2
				&& (femaleParentGermplasm.getGid().equals(maleParentGermplasm.getGpid1()) || femaleParentGermplasm.getGid().equals(
						maleParentGermplasm.getGpid2()))) {

			recurringParent = femaleParent;
		} else if (femaleParentGermplasm.getGnpgs() >= 2
				&& (maleParentGermplasm.getGid().equals(femaleParentGermplasm.getGpid1()) || maleParentGermplasm.getGid().equals(
						femaleParentGermplasm.getGpid2()))) {
			recurringParent = maleParent;
		}
		return Optional.fromNullable(recurringParent);
	}

	/**
	 * The number of time the recurring parent has been back crossed
	 *
	 * @param donorParent the donor parent node
	 * @param recurringParentGid the recurring parent gid
	 * @return the number or times the recurring parent has been back crossed
	 */
	private int recurringParentCount(final GermplasmNode donorParent, final Integer recurringParentGid) {
		if (donorParent == null) {
			return 0;
		}

		final Germplasm donorParentGermplasm = donorParent.getGermplasm();
		final Integer donorParentsFemaleParent = donorParentGermplasm.getGpid1();
		final Integer donorParentsMaleParent = donorParentGermplasm.getGpid2();

		if (donorParentsFemaleParent != null && !donorParentsFemaleParent.equals(recurringParentGid) && donorParentsMaleParent != null
				&& !donorParentsMaleParent.equals(recurringParentGid)) {
			return 1;
		}

		if (donorParentsFemaleParent != null && donorParentsFemaleParent.equals(recurringParentGid)) {
			return this.recurringParentCount(donorParent.getMaleParent(), recurringParentGid) + 1;
		}

		if (donorParentsMaleParent != null && donorParentsMaleParent.equals(recurringParentGid)) {
			return this.recurringParentCount(donorParent.getFemaleParent(), recurringParentGid) + 1;
		}

		return 0;
	}

}
