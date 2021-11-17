package org.generationcp.middleware.api.germplasm.pedigree;

import org.generationcp.middleware.domain.germplasm.GermplasmDto;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Progenitor;
import org.generationcp.middleware.util.MaxPedigreeLevelReachedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Note: see also {@link GermplasmPedigreeServiceAsyncImpl} (not a managed bean).
 * When autowiring things here, make sure to add setters there
 */
public class GermplasmPedigreeServiceImpl implements GermplasmPedigreeService {

	private final DaoFactory daoFactory;

	public static final int MAX_GENERATIONS_COUNT = 5;
	private static final ThreadLocal<Integer> GENERATIONS_COUNTER = new ThreadLocal<>();
	private static final ThreadLocal<Boolean> CALCULATE_FULL = new ThreadLocal<>();

	public GermplasmPedigreeServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public GermplasmTreeNode getGermplasmPedigreeTree(final Integer gid, final Integer level, final boolean includeDerivativeLines) {
		final Germplasm root = this.daoFactory.getGermplasmDao().getById(gid);

		if (root == null) {
			throw new MiddlewareRequestException("", "error.record.not.found", "gid=" + gid);
		}

		final GermplasmTreeNode rootNode = new GermplasmTreeNode(root);
		rootNode.setNumberOfGenerations(this.countGenerations(gid, includeDerivativeLines, level == null));
		final int finalLevel = level != null ? level : rootNode.getNumberOfGenerations();
		if (finalLevel > 1) {
			this.addParents(rootNode, finalLevel, root, !includeDerivativeLines);
		}

		return rootNode;
	}

	@Override
	public List<GermplasmDto> getGenerationHistory(final Integer gid) {
		final List<GermplasmDto> germplasmGenerationHistory = new ArrayList<>();

		GermplasmDto currentGermplasm = this.daoFactory.getGermplasmDao().getGermplasmDtoByGid(gid);
		if (currentGermplasm != null) {
			germplasmGenerationHistory.add(currentGermplasm);

			while (currentGermplasm.getNumberOfProgenitors() == -1) {
				// trace back the sources
				final Integer sourceId = currentGermplasm.getGpid2();
				currentGermplasm = this.daoFactory.getGermplasmDao().getGermplasmDtoByGid(sourceId);

				if (currentGermplasm != null) {
					germplasmGenerationHistory.add(currentGermplasm);
				} else {
					break;
				}
			}
		}
		return germplasmGenerationHistory;
	}

	@Override
	public List<GermplasmDto> getGroupRelatives(final Integer gid) {
		return this.daoFactory.getGermplasmDao().getGroupRelatives(gid);
	}

	@Override
	public List<GermplasmDto> getManagementNeighbors(final Integer gid) {
		return this.daoFactory.getGermplasmDao().getManagementNeighbors(gid);
	}

	@Override
	public GermplasmNeighborhoodNode getGermplasmMaintenanceNeighborhood(final Integer gid, final int numberOfStepsBackward,
		final int numberOfStepsForward) {
		return this.getNeighborhood(gid, numberOfStepsBackward, numberOfStepsForward, 'M');
	}

	@Override
	public GermplasmNeighborhoodNode getGermplasmDerivativeNeighborhood(final Integer gid, final int numberOfStepsBackward,
		final int numberOfStepsForward) {
		return this.getNeighborhood(gid, numberOfStepsBackward, numberOfStepsForward, 'D');
	}

	@Override
	public Integer countGenerations(final Integer gid, final boolean includeDerivativeLine, final boolean calculateFull) {
		try {
			CALCULATE_FULL.set(calculateFull);
			GENERATIONS_COUNTER.set(1);
			final Germplasm root = this.daoFactory.getGermplasmDao().getById(gid);
			return this.getNumberOfGenerations(root, includeDerivativeLine);
		} catch (final MaxPedigreeLevelReachedException e) {
			return GENERATIONS_COUNTER.get();
		} finally {
			GENERATIONS_COUNTER.remove();
			CALCULATE_FULL.remove();
		}
	}

	private GermplasmNeighborhoodNode getNeighborhood(final Integer gid, final int numberOfStepsBackward, final int numberOfStepsForward,
		final char methodType) {
		final Object[] traceResult = this.traceRoot(gid, numberOfStepsBackward, methodType);

		if (traceResult != null && traceResult.length >= 2) {
			final Germplasm rootGermplasm = (Germplasm) traceResult[0];

			final GermplasmNeighborhoodNode rootNode = new GermplasmNeighborhoodNode(rootGermplasm);
			final Integer stepsLeft = (Integer) traceResult[1];

			// get the derived lines from the root until the whole neighborhood is created
			final int treeLevel = numberOfStepsBackward - stepsLeft + numberOfStepsForward;
			return this.getDerivedLines(rootNode, treeLevel, methodType);
		} else {
			return null;
		}
	}

	/**
	 * Recursive function to get the derived lines given a Germplasm. This
	 * constructs the derivative neighborhood.
	 *
	 * @param node
	 * @param steps
	 * @return
	 */
	private GermplasmNeighborhoodNode getDerivedLines(final GermplasmNeighborhoodNode node, final int steps, final char methodType) {
		if (steps <= 0) {
			return node;
		} else {
			final Integer gid = node.getGid();
			final List<Germplasm> derivedGermplasms = this.daoFactory.getGermplasmDao().getDescendants(gid, methodType);
			for (final Germplasm germplasm : derivedGermplasms) {
				final GermplasmNeighborhoodNode derivedNode = new GermplasmNeighborhoodNode(germplasm);
				node.getLinkedNodes().add(this.getDerivedLines(derivedNode, steps - 1, methodType));
			}
			return node;
		}
	}

	/**
	 * Recursive function which gets the root of a derivative neighborhood by
	 * tracing back through the source germplasms. The function stops when the
	 * steps are exhausted or a germplasm created by a generative method is
	 * encountered, whichever comes first.
	 *
	 * @param gid
	 * @param steps
	 * @return Object[] - first element is the Germplasm POJO, second is an
	 * Integer which is the number of steps left to take
	 */
	private Object[] traceRoot(final Integer gid, final int steps, final char methodType) {
		final Germplasm germplasm = this.daoFactory.getGermplasmDao().getById(gid);

		if (germplasm == null) {
			return new Object[0];
		} else if (steps == 0 || germplasm.getGnpgs() != -1) {
			return new Object[] {germplasm, Integer.valueOf(steps)};
		} else {
			int nextStep = steps;

			//for MAN neighborhood, move the step count only if the ancestor is a MAN.
			//otherwise, skip through the ancestor without changing the step count
			if (methodType == 'M') {
				final Method method = this.daoFactory.getMethodDAO().getById(germplasm.getMethodId());
				if (method != null && "MAN".equals(method.getMtype())) {
					nextStep--;
				}

				//for DER neighborhood, always move the step count
			} else {
				nextStep--;
			}

			final Object[] returned = this.traceRoot(germplasm.getGpid2(), nextStep, methodType);
			if (returned != null) {
				return returned;
			} else {
				return new Object[] {germplasm, Integer.valueOf(steps)};
			}
		}
	}

	/**
	 * Given a GermplasmPedigreeTreeNode and the level of the desired tree, add parents
	 * to the node recursively until the specified level of the tree is reached.
	 *
	 * @param node
	 * @param level
	 * @return the given GermplasmTreeNode with its parents added to it
	 */
	private GermplasmTreeNode addParents(final GermplasmTreeNode node, final int level, final Germplasm germplasm,
		final boolean excludeDerivativeLines) {
		if (level != 1) {
			final int maleGid = germplasm.getMaleParent() == null ? 0 : germplasm.getMaleParent().getGid();
			final int femaleGid = germplasm.getFemaleParent() == null ? 0 : germplasm.getFemaleParent().getGid();
			if (germplasm.getGnpgs() == -1) {
				if (excludeDerivativeLines) {
					// get and add the source germplasm
					if (germplasm.getFemaleParent() != null) {
						this.addNodeForKnownParents(node, level, germplasm.getFemaleParent(), true);
					}
				} else {
					// Get and add the source germplasm, if it is unknown
					if (maleGid != 0) {
						this.addMaleParentNode(node, level, germplasm.getMaleParent(), false);
						// Use female parent to continue traversal if source is unknown
					} else if (femaleGid != 0) {
						node.setMaleParentNode(this.createUnknownParent());
						this.addFemaleParentNode(node, level, germplasm.getFemaleParent(), false);
					}
				}
			} else if (germplasm.getGnpgs() >= 2) {
				// Get and add female and male parents for generative germplasm
				this.addNodeForParents(node, level, germplasm, excludeDerivativeLines);
			}
		}
		return node;
	}

	private void addNodeForParents(final GermplasmTreeNode node, final int level, final Germplasm germplasm,
		final boolean excludeDerivativeLines) {
		// For generative germplasm, do not add any node if both parents are UNKNOWN (GID=0)
		if (!(germplasm.getGpid1() == 0 && germplasm.getGpid2() == 0)) {
			this.addFemaleParentNode(node, level, germplasm.getFemaleParent(), excludeDerivativeLines);
			if (germplasm.getGpid2() == 0) {
				node.setMaleParentNode(this.createUnknownParent());
			} else {
				this.addMaleParentNode(node, level, germplasm.getMaleParent(), excludeDerivativeLines);
			}

			// If there are more parents, get and add each of them
			if (germplasm.getGnpgs() > 2) {
				for (final Progenitor progenitor : germplasm.getOtherProgenitors()) {
					final Germplasm otherParent = progenitor.getProgenitorGermplasm();
					final GermplasmTreeNode maleParentNode = new GermplasmTreeNode(otherParent);
					node.getOtherProgenitors().add(this.addParents(maleParentNode, level - 1, otherParent, excludeDerivativeLines));
				}
			}
		}
	}

	private void addNodeForKnownParents(final GermplasmTreeNode node, final int level, final Germplasm germplasm,
		final boolean excludeDerivativeLines) {
		this.addFemaleParentNode(node, level, germplasm.getFemaleParent(), excludeDerivativeLines);
		this.addMaleParentNode(node, level, germplasm.getMaleParent(), excludeDerivativeLines);
	}

	private void addMaleParentNode(final GermplasmTreeNode node, final int level, final Germplasm germplasm,
		final boolean excludeDerivativeLines) {
		if (germplasm != null) {
			final GermplasmTreeNode maleParentNode = new GermplasmTreeNode(germplasm);
			node.setMaleParentNode(maleParentNode);
			this.addParents(maleParentNode, level - 1, germplasm, excludeDerivativeLines);
		}
	}

	private void addFemaleParentNode(final GermplasmTreeNode node, final int level, final Germplasm germplasm,
		final boolean excludeDerivativeLines) {
		if (germplasm != null) {
			final GermplasmTreeNode femaleParentNode = new GermplasmTreeNode(germplasm);
			node.setFemaleParentNode(femaleParentNode);
			this.addParents(femaleParentNode, level - 1, germplasm, excludeDerivativeLines);
		}
	}

	private GermplasmTreeNode createUnknownParent() {
		return new GermplasmTreeNode(0, Name.UNKNOWN, null, null, null);
	}

	public Integer getNumberOfGenerations(final Germplasm germplasm, final boolean includeDerivativeLine) {
		Integer maxPedigreeLevel = 0;

		if (germplasm == null || germplasm.getGid() == 0) {
			return maxPedigreeLevel;
		}

		if (germplasm.getGnpgs() == -1) {
			if (!includeDerivativeLine) {
				maxPedigreeLevel = this.getMaxGenerationCountFromParent(germplasm.getFemaleParent(), false);
			} else {
				maxPedigreeLevel = this.getMaxGenerationCountFromParent(germplasm.getMaleParent(), true);
			}
		} else if (germplasm.getGnpgs() >= 2) {
			maxPedigreeLevel = this.getMaxGenerationCountFromBothParents(germplasm, includeDerivativeLine);
			if (germplasm.getGnpgs() > 2) {
				for (final Progenitor progenitor : germplasm.getOtherProgenitors()) {
					maxPedigreeLevel =
						Math.max(maxPedigreeLevel, this.getGenerationsCount(progenitor.getProgenitorGermplasm(), includeDerivativeLine));
				}

			}
		}

		return maxPedigreeLevel + 1;
	}

	protected void incrementGenerationsCounter() {
		final int currentCount = GENERATIONS_COUNTER.get();
		if ((currentCount + 1) > MAX_GENERATIONS_COUNT && !CALCULATE_FULL.get()) {
			// Increment to return 6 if we don't need to calculate the full generations count
			GENERATIONS_COUNTER.set(currentCount + 1);
			throw MaxPedigreeLevelReachedException.getInstance();
		} else {
			GENERATIONS_COUNTER.set(currentCount + 1);
		}
	}

	private Integer getMaxGenerationCountFromBothParents(
		final Germplasm germplasm, final boolean includeDerivativeLine) {
		final int currentPedigreeCount = GENERATIONS_COUNTER.get();

		final Integer numOfPedigreeFromParent1 = this.getGenerationsCount(germplasm.getFemaleParent(), includeDerivativeLine);
		GENERATIONS_COUNTER.set(currentPedigreeCount);
		final Integer numOfPedigreeFromParent2 = this.getGenerationsCount(germplasm.getMaleParent(), includeDerivativeLine);

		return Math.max(numOfPedigreeFromParent1, numOfPedigreeFromParent2);
	}

	private Integer getGenerationsCount(final Germplasm germplasm, final boolean includeDerivativeLine) {
		if (germplasm != null) {
			this.incrementGenerationsCounter();
			return this.getNumberOfGenerations(germplasm, includeDerivativeLine);
		}
		return 0;
	}

	private Integer getMaxGenerationCountFromParent(final Germplasm germplasm, final boolean includeDerivativeLines) {
		if (germplasm == null || germplasm.getGid() == 0) {
			return 0;
		}
		if (!includeDerivativeLines) {
			return this.getMaxGenerationCountFromBothParents(germplasm, false);
		} else {
			this.incrementGenerationsCounter();
			return this.getNumberOfGenerations(germplasm, true);
		}
	}

}
