package org.generationcp.middleware.api.germplasm.pedigree.cop;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.apache.commons.lang3.tuple.Pair;
import org.generationcp.middleware.api.germplasm.pedigree.GermplasmTreeNode;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static java.util.Optional.empty;
import static java.util.Optional.of;

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

	private final BTypeEnum bTypeEnum;
	/**
	 * Named as in the paper just for consistency
	 */
	private final Table<Integer, Integer, Double> sparseMatrix;

	public CopCalculation() {
		this.bTypeEnum = BTypeEnum.SELF_POLINATING;
		this.sparseMatrix = HashBasedTable.create();
	}

	public CopCalculation(final BTypeEnum bTypeEnum, final Table<Integer, Integer, Double> sparseMatrix) {
		this.bTypeEnum = bTypeEnum != null ? bTypeEnum : BTypeEnum.SELF_POLINATING;
		this.sparseMatrix = sparseMatrix != null ? sparseMatrix : HashBasedTable.create();
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
		if (g1 == null || g2 == null || g1.getGid() == null || g2.getGid() == null) {
			return COP_DEFAULT;
		}
		if (this.sparseMatrix.contains(g1.getGid(), g2.getGid())) {
			return this.sparseMatrix.get(g1.getGid(), g2.getGid());
		}

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
			 * TODO
			 *  "..Although it is symmetrical in P and Q so that the parents of either could be used to obtain the right hand expansion.."
			 *  however if we invert the parents here, it won't work. Why?
			 */
			final Pair<GermplasmTreeNode, GermplasmTreeNode> byOrder = this.sortByOrder(g1, g2);
			final GermplasmTreeNode highOrder = byOrder.getLeft();
			final GermplasmTreeNode lowOrder = byOrder.getRight();

			final Optional<Pair<GermplasmTreeNode, GermplasmTreeNode>> parents = this.getParents(highOrder);
			if (parents.isPresent()) {
				final GermplasmTreeNode fp = parents.get().getLeft();
				final GermplasmTreeNode mp = parents.get().getRight();
				// Equation 1
				cop = (this.coefficientOfParentage(fp, lowOrder) + this.coefficientOfParentage(mp, lowOrder)) / 2.0;
			}

		}

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
			// TODO get btype from method?
			return this.bTypeEnum.getValue();
		}

		final double fg = g.getFemaleParentNode() != null && g.getMaleParentNode() != null
			? this.coefficientOfParentage(g.getFemaleParentNode(), g.getMaleParentNode())
			: COP_DEFAULT;

		// Equation 2
		return 1 - ((1 - fg) / Math.pow(2.0, this.countInbreedingGenerations(g)));
	}

	private boolean isBTypeScenario(final GermplasmTreeNode g) {
		// TODO complete
		//  "if Z traces back to a single progenitor, such as a mutant strain"

		// "If the progenitors of a strain are unknown"
		return g.getNumberOfProgenitors() > 0 && (g.getFemaleParentNode() == null || g.getMaleParentNode() == null);
	}

	private int countInbreedingGenerations(final GermplasmTreeNode g) {
		GermplasmTreeNode source = g.getMaleParentNode();
		if (source == null) {
			return UNKNOWN_INBREEDING_GENERATIONS;
		}

		int count = 1;
		while (source != null && source.getNumberOfProgenitors() < 0) {
			count++;
			source = source.getMaleParentNode();
		}
		return count;
	}

	/*
	 * TODO
	 *  - unit test separately
	 */
	private Optional<GermplasmTreeNode> getCommonDerivativeAncestor(final GermplasmTreeNode g1, final GermplasmTreeNode g2) {
		if (g1.getGid().equals(g2.getGid()) || g1.getNumberOfProgenitors() > 0 || g2.getNumberOfProgenitors() > 0) {
			return empty();
		}
		// source, aka immediate parent
		GermplasmTreeNode source1 = g1;
		final Set<GermplasmTreeNode> pedigree1 = new HashSet<>();
		pedigree1.add(g1);
		while (source1.getMaleParentNode() != null && source1.getNumberOfProgenitors() < 0) {
			source1 = source1.getMaleParentNode();
			if (source1 == g2) {
				return of(g2);
			}
			pedigree1.add(source1);
		}
		GermplasmTreeNode source2 = g2;
		final Set<GermplasmTreeNode> pedigree2 = new HashSet<>();
		pedigree2.add(g2);
		while (source2.getMaleParentNode() != null && source2.getNumberOfProgenitors() < 0) {
			if (pedigree1.contains(source2)) {
				return of(source2);
			}
			source2 = source2.getMaleParentNode();
			pedigree2.add(source2);
		}
		for (final GermplasmTreeNode p1 : pedigree1) {
			if (pedigree2.contains(p1)) {
				return of(p1);
			}
		}

		return empty();
	}

	private Optional<Pair<GermplasmTreeNode, GermplasmTreeNode>> getParents(final GermplasmTreeNode g) {
		if (this.hasUnknownParents(g)) {
			return empty();
		}
		if (g.getNumberOfProgenitors() > 0) {
			return of(Pair.of(g.getFemaleParentNode(), g.getMaleParentNode()));
		}
		final GermplasmTreeNode groupSource = g.getFemaleParentNode();
		// source, aka immediate parent
		GermplasmTreeNode source = g.getMaleParentNode();
		if (groupSource != null) {
			return of(Pair.of(groupSource.getFemaleParentNode(), groupSource.getMaleParentNode()));
		} else if (source != null) {
			while (source != null) {
				if (source.getNumberOfProgenitors() > 0 && source.getMaleParentNode() != null && source.getFemaleParentNode() != null) {
					return of(Pair.of(source.getFemaleParentNode(), source.getMaleParentNode()));
				} else {
					source = source.getMaleParentNode();
				}
			}
		}
		return empty();
	}

	private boolean hasUnknownParents(final GermplasmTreeNode g) {
		return g.getNumberOfProgenitors() > 0 && (g.getMaleParentNode() == null || g.getFemaleParentNode() == null);
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
