package org.generationcp.middleware.api.germplasm.pedigree.cop;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.apache.commons.lang3.tuple.Pair;
import org.generationcp.middleware.api.germplasm.pedigree.GermplasmTreeNode;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.time.Duration.between;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.apache.commons.lang3.time.DurationFormatUtils.formatDurationHMS;
import static org.generationcp.middleware.util.Debug.debug;
import static org.generationcp.middleware.util.Debug.info;

/**
 * Coefficient of parentage (f): calculation utilities.
 * <br>
 * Example of COP from paper:
 * <pre>
 *                                         ┌────────────────────────────────────┐
 *              P3 Cultivar                │    fPQ=(fBQ+fRQ)/2 =87/128         │
 *                 order 3                 │                                    │
 *                  ┼                      │                                    │
 *                  o                      │ fBQ=(fBA+fBB)/2    =31/64          │
 *       Selections o                      │ fRQ=fZZ =(1+FZ)/2  =7/8            │
 *                  o                      │ FZ=1-(1-fAB)/2^n   =1-1/2^2=3/4    │
 *                  o           Q2         │ fBB=(1+FB)/2       =31/32          │
 *           Cross  ┼            ┼         │ FB=1-(1-fEF)/2^n   =1-1/2^4=15/16  │
 *            ┌─────^────┐       o         │ fBA=(fEA+fFA)/2    =0              │
 *            │          │       ┼         └────────────────────────────────────┘
 *           B1          R2      o
 *            ┌┐         │       │
 * Unknown    ││         │       │
 * number of  ││         o─── Z2─┘Common
 * generations├┘              ┼   ancestor
 *            │               o
 *       ┌────^───┐           ┼
 *       │        │       ┌───^────┐
 *       E0       F0      │        │
 *                       A0        B1
 *    Landrace
 *    order 0           Landrace   Breeding line
 *                      order 0    Also parent of P
 *                                 order 1
 *
 * Equations from paper:
 *  1) fBA =(fEA+fFA)/2
 *  2) FB  =1-(1-fEF)/2^n (if n unknown then n=4)
 *  3) fBB =(1+FB)/2
 * </pre>
 *
 * @see <a target="_top" href="https://cropforge.github.io/iciswiki/articles/t/d/m/TDM_COP2.htm">Routine Computation and Visualization of Coefficients of Parentage Using the International Crop Information System</a>
 */
public class CopCalculation {

	private static final double COP_DEFAULT = 0.0;
	private static final int UNKNOWN_INBREEDING_GENERATIONS = 4;
	private static final int UNKNOWN_GID = 0;

	/**
	 * Named as in the paper just for consistency
	 */
	private final Table<Integer, Integer, Double> sparseMatrix;
	private final BTypeEnum btype;

	public CopCalculation() {
		this.sparseMatrix = HashBasedTable.create();
		this.btype = BTypeEnum.OTHER;
	}

	public CopCalculation(final Table<Integer, Integer, Double> sparseMatrix, final int bType) {
		this.sparseMatrix = sparseMatrix != null ? sparseMatrix : HashBasedTable.create();
		this.btype = BTypeEnum.fromValue(bType);
	}

	/*
	 * TODO:
	 *  - verify termination
	 */
	/**
	 * aka f()
	 *
	 * @param g1
	 * @param g2
	 */
	public double coefficientOfParentage(final GermplasmTreeNode g1, final GermplasmTreeNode g2) {
		if (Thread.currentThread().isInterrupted()) {
			throw new MiddlewareRequestException("", "cop.async.interrupted");
		}

		if (g1 == null || g2 == null
			|| g1.getGid() == null || g2.getGid() == null
			|| g1.getGid() == 0 || g2.getGid() == 0) {
			return COP_DEFAULT;
		}
		if (this.sparseMatrix.contains(g1.getGid(), g2.getGid())) {
			final Double cop = this.sparseMatrix.get(g1.getGid(), g2.getGid());
			debug("cop found: (gid1=%s-gid2=%s) = %s", g1.getGid(), g2.getGid(), cop);
			return cop;
		}
		info("calculating cop (gid1=%s-gid2=%s)", g1.getGid(), g2.getGid());
		final Instant start = Instant.now();

		double cop = COP_DEFAULT;

		final Optional<GermplasmTreeNode> commonDerivativeAncestor = this.getCommonDerivativeAncestor(g1, g2);

		if (g1.getGid().equals(g2.getGid())) {
			// Equation 3
			cop = (1 + this.coefficientOfInbreeding(g1)) / 2.0;
		} else if (commonDerivativeAncestor.isPresent()) {
			final GermplasmTreeNode g = commonDerivativeAncestor.get();
			cop = this.coefficientOfParentage(g, g);
		} else if (this.hasUnknownParents(g1) || this.hasUnknownParents(g2)) {
			cop = COP_DEFAULT;
		} else {
			/*
			 * from paper:
			 *  "..Although it is symmetrical in P and Q so that the parents of either could be used to obtain the right hand expansion.."
			 *  ..expanding the strain with the highest order .. ensures that when the terminal ancestors are reached,
			 *  the computations involve COP values between the same strain, or unrelated strains or crosses between unrelated strains,
			 *  all of which are easily calculated"
			 */
			final Pair<GermplasmTreeNode, GermplasmTreeNode> byOrder = this.sortByOrder(g1, g2);
			final GermplasmTreeNode highOrder = byOrder.getLeft();
			final GermplasmTreeNode lowOrder = byOrder.getRight();

			// TODO other progenitors: (1/m) ∑ fPQi
			//  https://github.com/IntegratedBreedingPlatform/AWSApps/blob/80756ceae4ccf6d330d8c9a8e9f02dcedeaffa50/COP/ibp_smart-module-lambda-import-master/sm-lambda-import/cop_src/cop/algorithm_v2.py#L43
			final Optional<Pair<GermplasmTreeNode, GermplasmTreeNode>> parents = this.getCrossParents(highOrder);
			if (parents.isPresent()) {
				final GermplasmTreeNode fp = parents.get().getLeft();
				final GermplasmTreeNode mp = parents.get().getRight();
				// Equation 1
				cop = (this.coefficientOfParentage(fp, lowOrder) + this.coefficientOfParentage(mp, lowOrder)) / 2.0;
			}

		}

		final Instant end = Instant.now();
		info("calculated cop (gid1=%s-gid2=%s) = %s, Duration: %s", g1.getGid(), g2.getGid(), cop,
			formatDurationHMS(between(start, end).toMillis()));

		this.sparseMatrix.put(g1.getGid(), g2.getGid(), cop);
		return cop;
	}

	/**
	 * aka F()
	 *
	 * @param g
	 */
	public double coefficientOfInbreeding(final GermplasmTreeNode g) {
		if (this.isBTypeScenario(g)) {
			return this.getBType(g);
		}

		final double fg = !isUnknown(g.getFemaleParentNode()) && !isUnknown(g.getMaleParentNode())
			? this.coefficientOfParentage(g.getFemaleParentNode(), g.getMaleParentNode())
			: COP_DEFAULT;

		/*
		 * Equation 2
		 *
		 * If generative, then generations = 0
		 * => F(g) = 1 - (1 - fg) / 2^0 = fg
		 *
		 * Unknown inbreeding generations:
		 * 1 - (1 - 0) / 2^4 = 0.9375
		 */
		return 1 - ((1 - fg) / Math.pow(2.0, this.countInbreedingGenerations(g)));
	}

	private double getBType(final GermplasmTreeNode g) {
		// TODO get btype from method?
		return this.btype.getValue();
	}

	private boolean isBTypeScenario(final GermplasmTreeNode g) {
		// TODO complete
		//  "if Z traces back to a single progenitor, such as a mutant strain"

		/*
		 * "If the progenitors of a strain are unknown"
		 * if only male parent (immediate source) is unknown => handle later by UNKNOWN_INBREEDING_GENERATIONS
		 */
		return this.isDerivative(g) && isUnknown(g.getFemaleParentNode()) && (isUnknown(g.getMaleParentNode()));
	}

	private int countInbreedingGenerations(final GermplasmTreeNode g) {
		GermplasmTreeNode source = g.getMaleParentNode();
		if (isUnknown(source)) {
			return UNKNOWN_INBREEDING_GENERATIONS;
		}
		if (this.isGenerative(g)) {
			return 0;
		}

		int count = 1;
		while (!isUnknown(source) && this.isDerivative(source) && !isUnknown(source.getMaleParentNode())) {
			count++;
			source = source.getMaleParentNode();
		}
		return count;
	}

	/**
	 * Search in both pedigrees until it find a common ancestor or if one is ancestor of the other.
	 *
	 * TODO
	 *  - unit test separately
	 */
	private Optional<GermplasmTreeNode> getCommonDerivativeAncestor(final GermplasmTreeNode g1, final GermplasmTreeNode g2) {
		if (g1.getGid().equals(g2.getGid()) || this.isGenerative(g1) || this.isGenerative(g2)) {
			return empty();
		}
		// source, aka immediate parent
		GermplasmTreeNode source1 = g1;
		final Map<Integer, GermplasmTreeNode> pedigree1 = new HashMap<>();
		pedigree1.put(g1.getGid(), g1);
		while (!isUnknown(source1.getMaleParentNode()) && source1.getNumberOfProgenitors() < 0) {
			source1 = source1.getMaleParentNode();
			if (source1.getGid().equals(g2.getGid())) {
				return of(g2);
			}
			pedigree1.put(source1.getGid(), source1);
		}
		GermplasmTreeNode source2 = g2;
		final Map<Integer, GermplasmTreeNode> pedigree2 = new HashMap<>();
		pedigree2.put(g2.getGid(), g2);
		while (!isUnknown(source2.getMaleParentNode()) && source2.getNumberOfProgenitors() < 0) {
			if (pedigree1.containsKey(source2.getGid())) {
				return of(source2);
			}
			source2 = source2.getMaleParentNode();
			pedigree2.put(source2.getGid(), source2);
		}
		for (final Map.Entry<Integer, GermplasmTreeNode> e : pedigree1.entrySet()) {
			if (pedigree2.containsKey(e.getKey())) {
				return of(e.getValue());
			}
		}

		return empty();
	}

	private Optional<Pair<GermplasmTreeNode, GermplasmTreeNode>> getCrossParents(final GermplasmTreeNode g) {
		if (this.hasUnknownParents(g)) {
			return empty();
		}
		if (this.isGenerative(g)) {
			return of(Pair.of(g.getFemaleParentNode(), g.getMaleParentNode()));
		}
		final GermplasmTreeNode groupSource = g.getFemaleParentNode();
		// source, aka immediate parent
		GermplasmTreeNode source = g.getMaleParentNode();
		if (!isUnknown(groupSource)) {
			return of(Pair.of(groupSource.getFemaleParentNode(), groupSource.getMaleParentNode()));
		} else if (!isUnknown(source)) {
			while (!isUnknown(source)) {
				if (this.isGenerative(source) && !isUnknown(source.getMaleParentNode()) && !isUnknown(source.getFemaleParentNode())) {
					return of(Pair.of(source.getFemaleParentNode(), source.getMaleParentNode()));
				} else {
					source = source.getMaleParentNode();
				}
			}
		}
		return empty();
	}

	private boolean isGenerative(final GermplasmTreeNode g) {
		return g != null && g.getNumberOfProgenitors() != null && g.getNumberOfProgenitors() > 0;
	}

	private boolean isDerivative(final GermplasmTreeNode g) {
		return g != null && g.getNumberOfProgenitors() != null && g.getNumberOfProgenitors() < 0;
	}

	private boolean hasUnknownParents(final GermplasmTreeNode g) {
		return this.isGenerative(g) && (isUnknown(g.getMaleParentNode()) || isUnknown(g.getFemaleParentNode()));
	}

	private static boolean isUnknown(final GermplasmTreeNode node) {
		return node == null || node.getGid() == UNKNOWN_GID;
	}

	private Pair<GermplasmTreeNode, GermplasmTreeNode> sortByOrder(final GermplasmTreeNode g1, final GermplasmTreeNode g2) {
		final Optional<Integer> g1Order = Optional.ofNullable(g1.getNumberOfGenerations());
		final Optional<Integer> g2Order = Optional.ofNullable(g2.getNumberOfGenerations());
		if (!g2Order.isPresent()) {
			return Pair.of(g1, g2);
		} else if (!g1Order.isPresent()) {
			return Pair.of(g2, g1);
		}

		return g1Order.get() > g2Order.get() ? Pair.of(g1, g2) : Pair.of(g2, g1);
	}
}
