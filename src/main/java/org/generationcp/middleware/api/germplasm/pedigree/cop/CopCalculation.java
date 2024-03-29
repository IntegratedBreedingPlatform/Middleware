package org.generationcp.middleware.api.germplasm.pedigree.cop;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.apache.commons.lang3.tuple.Pair;
import org.generationcp.middleware.api.germplasm.pedigree.GermplasmTreeNode;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.pojos.Methods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static java.time.Duration.between;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.apache.commons.lang3.time.DurationFormatUtils.formatDurationHMS;
import static org.generationcp.middleware.util.Debug.debug;
import static org.generationcp.middleware.util.Debug.info;
import static org.springframework.util.CollectionUtils.isEmpty;

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

	private static final Logger LOG = LoggerFactory.getLogger(CopCalculation.class);

	private static final double COP_DEFAULT = 0.0;
	private static final int UNKNOWN_INBREEDING_GENERATIONS = 4;
	private static final int UNKNOWN_GID = 0;

	private static final double COPZ_CROSS_FERTILIZING_UNKNOWN_PARENTS = 0;
	private static final double COPZ_UNKNOWN_DERIVATIVE = 1;
	private static final double COPZ_CROSS_FERTILIZING_WIDE_VARIABILITY = -1;

	/**
	 * Named as in the paper just for consistency
	 */
	private final Table<Integer, Integer, Double> sparseMatrix;
	private final BTypeEnum btype;

	public CopCalculation() {
		this.sparseMatrix = HashBasedTable.create();
		this.btype = BTypeEnum.CROSS_FERTILIZING;
	}

	public CopCalculation(final BTypeEnum btype) {
		this.sparseMatrix = HashBasedTable.create();
		this.btype = btype;
	}

	public CopCalculation(final Table<Integer, Integer, Double> sparseMatrix, final BTypeEnum btype) {
		this.sparseMatrix = sparseMatrix != null ? sparseMatrix : HashBasedTable.create();
		this.btype = btype;
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
		if (this.sparseMatrix.contains(g2.getGid(), g1.getGid())) {
			final Double cop = this.sparseMatrix.get(g2.getGid(), g1.getGid());
			debug("cop found: (gid2=%s-gid1=%s) = %s", g2.getGid(), g1.getGid(), cop);
			return cop;
		}
		debug("calculating cop (gid1=%s-gid2=%s)", g1.getGid(), g2.getGid());
		final Instant start = Instant.now();

		double cop = COP_DEFAULT;

		/*
		 * Case: coefficient of inbreeding
		 */

		if (g1.getGid().equals(g2.getGid())) {
			final double coi = this.coefficientOfInbreeding(g1);
			// Equation 3
			cop = (1 + coi) / 2.0;
			debug("cop(gid=%s) = (1 + coi(%s)) / 2.0", g1.getGid(), g1.getGid());
			debug("cop(gid=%s) = (1 + %s) / 2.0", g1.getGid(), coi);
			return this.finish(g1, g2, start, cop);
		}

		/*
		 * Case: For strains which are sister lines derived from the same group,
		 * the effect of inbreeding depends on the inbreeding coefficient of the most recent common ancestor
		 */

		final Optional<GermplasmTreeNode> commonDerivativeAncestor = this.getCommonDerivativeAncestor(g1, g2);

		if (commonDerivativeAncestor.isPresent()) {
			final GermplasmTreeNode g = commonDerivativeAncestor.get();
			// ends up in coefficientOfInbreeding(), plus common validations of coefficientOfParentage()
			cop = this.coefficientOfParentage(g, g);
			return this.finish(g1, g2, start, cop);
		}

		/*
		 * Below here, only generative steps are considered
		 */

		final GermplasmTreeNode g01 = getGroupSource(g1);
		final GermplasmTreeNode g02 = getGroupSource(g2);

		if (this.hasUnknownCrossParents(g01) && this.hasUnknownCrossParents(g02)) {
			cop = COP_DEFAULT;
			return this.finish(g1, g2, start, cop);
		}

		/*
		 * Case: the basic relationship between COP values for strains in one generation and those in a previous one
		 *
		 *  "..Although it is symmetrical in P and Q so that the parents of either could be used to obtain the right hand expansion.."
		 *  ..expanding the strain with the highest order .. ensures that when the terminal ancestors are reached,
		 *  the computations involve COP values between the same strain, or unrelated strains or crosses between unrelated strains,
		 *  all of which are easily calculated"
		 */
		final Pair<GermplasmTreeNode, GermplasmTreeNode> byOrder = this.sortByOrder(g1, g2, g01, g02);
		final GermplasmTreeNode highOrder = byOrder.getLeft();
		final GermplasmTreeNode lowOrder = byOrder.getRight();

		final List<GermplasmTreeNode> parents = this.getCrossParents(highOrder);
		if (!isEmpty(parents)) {
			// Debug
			final List<String> cops = new ArrayList<>();

			// Equation 1 for multiple parents: (1/m) ∑ fPQi
			for (final GermplasmTreeNode parent : parents) {
				final double cop1 = this.coefficientOfParentage(parent, lowOrder);
				cops.add(String.valueOf(cop1));
				cop += cop1;
			}
			if (LOG.isDebugEnabled()) {
				if (cop > 0d) {
					debug("cop(gid1=%s-gid2=%s) = (%s) / %s", g1.getGid(), g2.getGid(), String.join("+", cops), parents.size());
					debug("cop(gid1=%s-gid2=%s) = (%s) / %s", g1.getGid(), g2.getGid(), cop, parents.size());
				}
			}
			cop /= parents.size();
		}

		return this.finish(g1, g2, start, cop);
	}

	private double finish(final GermplasmTreeNode g1, final GermplasmTreeNode g2, final Instant start, final double cop) {
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
		final GermplasmTreeNode g0 = getGroupSource(g);

		/*
		 * "BTYPE to implement some assumptions in the case of incomplete pedigree records.
		 * The user should set BTYPE = 1 for self-pollinating species and 0 otherwise.
		 * If the progenitors of a strain are unknown, then FZ is set to BTYPE.
		 * This occurs most frequently when Z is derived from a landrace or traditional cultivar
		 * and corresponds to an assumption of full inbreeding for self-pollinating crops and no inbreeding for others.
		 * Similarly, if Z traces back to a single progenitor, such as a mutant strain, then FZ = BTYPE."
		 */

		/*
		 * handle case with break in the records. E.g:
		 *  A
		 *  |
		 *  UNKNOWN
		 *  |
		 *  B
		 *  |
		 *  C
		 *  |
		 *  D
		 */
		GermplasmTreeNode source = g.getMaleParentNode();
		while (!isUnknown(source) && isDerivative(source)) {
			source = source.getMaleParentNode();
		}
		/*
		 * if break in the records but btype=2 => handle later by UNKNOWN_INBREEDING_GENERATIONS.
		 */
		if (isUnknown(source) && !BTypeEnum.SELF_FERTILIZING_F4.equals(this.btype)) {
			return this.btype.getValue();
		}

		final double copz = getCopZ(g, g0);

		/*
		 * Equation 2
		 *
		 * If generative, then generations = 0
		 * => F(g) = 1 - (1 - copz) / 2^0 = copz = f(g)
		 *
		 * Unknown inbreeding generations (btype=2):
		 * 1 - (1 - 0) / 2^4 = 15/16 = 0.9375
		 */
		final int inbreedingGenerationsCount = this.countInbreedingGenerations(g);
		final double coi = 1 - ((1 - copz) / Math.pow(2.0, inbreedingGenerationsCount));
		debug("coi(gid=%s) = 1 - ((1 - copz) / (2.0 ^ %s))", g.getGid(), inbreedingGenerationsCount);
		debug("coi(gid=%s) = 1 - ((1 - %s) / (2.0 ^ %s))", g.getGid(), copz, inbreedingGenerationsCount);
		debug("coi(gid=%s) = %s", g.getGid(), coi);
		return coi;
	}

	private double getCopZ(final GermplasmTreeNode g, final GermplasmTreeNode g0) {
		/*
		 * "if Z traces back to a single progenitor, such as a mutant strain"
		 * According to Graham: "refers to gpid1 >0 gpid2 = 0 and gnpgs = 1. with method for mutation"
		 */
		if (g0.getNumberOfProgenitors() == 1
			&& !isUnknown(g0.getFemaleParentNode()) && isUnknown(g0.getMaleParentNode())) {
			return this.getCopZMutant(g);
		}

		/*
		 * "If the progenitors of a strain are unknown"
		 */
		if (isUnknown(g0.getFemaleParentNode()) && isUnknown(g0.getMaleParentNode())) {
			return this.getCopZUnknownParents();
		}

		/*
		 * Unknown group
		 */
		if (isUnknown(g0.getFemaleParentNode())) {
			return this.getCopZUnknownGroup(g);
		}

		return !isUnknown(g0.getFemaleParentNode()) && !isUnknown(g0.getMaleParentNode())
			? this.coefficientOfParentage(g0.getFemaleParentNode(), g0.getMaleParentNode())
			: COP_DEFAULT;
	}

	private double getCopZMutant(final GermplasmTreeNode g) {
		if (this.isSelfFertilizing()) {
			return this.btype.getValue();
		} else {
			return this.getCopZFromMethod(g);
		}
	}

	private double getCopZUnknownParents() {
		if (this.isSelfFertilizing()) {
			return this.btype.getValue();
		} else {
			return COPZ_CROSS_FERTILIZING_UNKNOWN_PARENTS;
		}
	}

	private double getCopZUnknownGroup(final GermplasmTreeNode g) {
		if (this.isSelfFertilizing()) {
			return this.btype.getValue();
		} else {
			return this.getCopZFromMethod(g);
		}
	}

	private double getCopZFromMethod(final GermplasmTreeNode g) {
		if (Methods.UNKNOWN_DERIVATIVE_METHOD.getMethodCode().equals(g.getMethodCode())) {
			return COPZ_UNKNOWN_DERIVATIVE;
		}
		if (Arrays.stream(Methods.COP_CROSS_FERTILIZING_WIDE_VARIABILITY).anyMatch(m -> m.getMethodCode().equals(g.getMethodCode()))) {
			return COPZ_CROSS_FERTILIZING_WIDE_VARIABILITY;
		}

		return this.btype.getValue();
	}

	private boolean isSelfFertilizing() {
		return BTypeEnum.SELF_FERTILIZING.equals(this.btype) || BTypeEnum.SELF_FERTILIZING_F4.equals(this.btype);
	}

	/**
	 * The largest number of generative steps from the current ancestor to a terminal ancestor via any of its progenitors
	 * <pre>
	 * TODO
	 *  "Ancestors produced by derivative or maintenance methods have the same order as their group strains."
	 *  only node and group source set for now (they're the only ones used in {@link #sortByOrder}
	 * </pre>
	 * @param node
	 * @return max order of parent subtree + 1
	 */
	public static int populateOrder(final GermplasmTreeNode node, final int orderParam) {
		if (isUnknown(node)) {
			return orderParam;
		}
		final GermplasmTreeNode groupSource = getGroupSource(node);
		int order = orderParam;
		order = Math.max(
			populateOrder(groupSource.getFemaleParentNode(), order),
			populateOrder(groupSource.getMaleParentNode(), order)
		);
		final List<GermplasmTreeNode> otherProgenitors = groupSource.getOtherProgenitors();
		if (!isEmpty(otherProgenitors)) {
			for (final GermplasmTreeNode otherProgenitor : otherProgenitors) {
				order = Math.max(order, populateOrder(otherProgenitor, order));
			}
		}
		order = Math.max(order, groupSource.getOrder());
		node.setOrder(order);
		groupSource.setOrder(order);
		return order + 1;
	}

	private int countInbreedingGenerations(final GermplasmTreeNode g) {
		GermplasmTreeNode source = g.getMaleParentNode();
		if (isUnknown(source) && BTypeEnum.SELF_FERTILIZING_F4.equals(this.btype)) {
			return UNKNOWN_INBREEDING_GENERATIONS;
		}
		Preconditions.checkArgument(!isUnknown(source), "programming error in coefficient of inbreeding: Unknown source found");
		if (this.isGenerative(g)) {
			return 0;
		}

		int count = 1;
		while (!isUnknown(source) && isDerivative(source) && !isUnknown(source.getMaleParentNode())) {
			count++;
			source = source.getMaleParentNode();
		}
		/*
		 * Graham: It is not so important what happens here - I guess I just go with fully inbred if BTYPE=1 (handle above)
		 *  and F4 with BTYPE=2
		 *
		 *  A
		 *  |
		 *  UNKNOWN
		 *  |
		 *  B
		 *  |
		 *  C
		 *  |
		 *  D
		 */
		if (isDerivative(source) && BTypeEnum.SELF_FERTILIZING_F4.equals(this.btype)) {
			return UNKNOWN_INBREEDING_GENERATIONS;
		}
		return count;
	}

	/**
	 * Search in both pedigrees until it find a common ancestor or if one is ancestor of the other.
	 * <pre>
	 * TODO
	 *  - unit test separately
	 *  - improve perf, memoize
	 * </pre>
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

	/**
	 *
	 * @param g a group source
	 * @return
	 */
	private List<GermplasmTreeNode> getCrossParents(final GermplasmTreeNode g) {
		if (this.hasUnknownCrossParents(g)) {
			return emptyList();
		}
		if (this.isGenerative(g)) {
			final List<GermplasmTreeNode> nodes = newArrayList(g.getFemaleParentNode(), g.getMaleParentNode());
			if (!isEmpty(g.getOtherProgenitors())) {
				nodes.addAll(g.getOtherProgenitors());
			}
			return nodes;
		}
		return emptyList();
	}

	/**
	 * CopCalculation processes full pedigrees (with derivative lines)
	 * This method gets the group source (child of a cross) if the the line is derivative,
	 * or the latest known ancestor or itself if there are no ancestors.
	 */
	private static GermplasmTreeNode getGroupSource(final GermplasmTreeNode g) {
		final GermplasmTreeNode g0;
		if (isDerivative(g)) {
			final GermplasmTreeNode groupSource = g.getFemaleParentNode();
			GermplasmTreeNode source = g.getMaleParentNode();
			if (!isUnknown(groupSource)) {
				g0 = groupSource;
			} else if (!isUnknown(source)) {
				while (isDerivative(source) && !isUnknown(source.getMaleParentNode())) {
					source = source.getMaleParentNode();
				}
				if (isDerivative(source) && !isUnknown(source.getFemaleParentNode())) {
					/*
					 * Case: UNKNOWN source (break in the records) but group source is known.
					 * E.g:
					 *   A   B
					 *     C
					 *     |
					 *  UNKNOWN
					 *     |
					 *     D
					 */
					g0 = source.getFemaleParentNode();
				} else {
					/*
					 * either we found the child of a cross,
					 * or there are no more ancestors to traverse.
					 */
					g0 = source;
				}
			} else {
				/*
				 * No known parents
				 */
				g0 = g;
			}
		} else {
			g0 = g;
		}
		return g0;
	}

	private boolean isGenerative(final GermplasmTreeNode g) {
		return g != null && g.getNumberOfProgenitors() != null && g.getNumberOfProgenitors() > 0;
	}

	private static boolean isDerivative(final GermplasmTreeNode g) {
		return g != null && g.getNumberOfProgenitors() != null && g.getNumberOfProgenitors() < 0;
	}

	private boolean hasUnknownParents(final GermplasmTreeNode g) {
		return isUnknown(g.getMaleParentNode()) || isUnknown(g.getFemaleParentNode());
	}

	private boolean hasUnknownCrossParents(final GermplasmTreeNode g) {
		return this.isGenerative(g) && this.hasUnknownParents(g);
	}

	private static boolean isUnknown(final GermplasmTreeNode node) {
		return node == null || node.getGid() == UNKNOWN_GID;
	}

	/**
	 *
	 * @param g1
	 * @param g2
	 * @param g01 the group source of g1
	 * @param g02 the group source of g2
	 * @return Pair of (highest order, lowest order). The highest order is always the group source,
	 * 		   which simplifies the next step (get the parents)
	 */
	private Pair<GermplasmTreeNode, GermplasmTreeNode> sortByOrder(
		final GermplasmTreeNode g1,
		final GermplasmTreeNode g2,
		final GermplasmTreeNode g01,
		final GermplasmTreeNode g02
	) {

		if (this.hasUnknownParents(g02)) {
			return Pair.of(g01, g2);
		} else if (this.hasUnknownParents(g01)) {
			return Pair.of(g02, g1);
		}
		final Optional<Integer> g1Order = Optional.ofNullable(g01.getOrder());
		final Optional<Integer> g2Order = Optional.ofNullable(g02.getOrder());
		if (!g2Order.isPresent()) {
			return Pair.of(g01, g2);
		} else if (!g1Order.isPresent()) {
			return Pair.of(g02, g1);
		}

		return g1Order.get() > g2Order.get() ? Pair.of(g01, g2) : Pair.of(g02, g1);
	}
}
