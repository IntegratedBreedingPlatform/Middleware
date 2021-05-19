package org.generationcp.middleware.api.germplasm.pedigree;

import org.generationcp.middleware.api.germplasm.GermplasmService;
import org.generationcp.middleware.domain.germplasm.GermplasmDto;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.util.MaxPedigreeLevelReachedException;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

public class GermplasmPedigreeServiceImpl implements GermplasmPedigreeService {

	private final DaoFactory daoFactory;

	@Resource
	private GermplasmService germplasmService;

	public static final int MAX_GENERATIONS_COUNT = 5;
	private static final ThreadLocal<Integer> GENERATIONS_COUNTER = new ThreadLocal<>();
	private static final ThreadLocal<Boolean> CALCULATE_FULL = new ThreadLocal<>();

	public GermplasmPedigreeServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public GermplasmTreeNode getGermplasmPedigreeTree(final Integer gid, Integer level, final boolean includeDerivativeLines) {
		final Germplasm root = this.germplasmService.getGermplasmWithPreferredName(gid);

		final GermplasmTreeNode rootNode = new GermplasmTreeNode(root);
		rootNode.setNumberOfGenerations(this.countGenerations(gid, includeDerivativeLines, level == null));
		level = level == null? rootNode.getNumberOfGenerations() : level;
		if (level > 1) {
			this.addParents(rootNode, level, root, !includeDerivativeLines);
		}

		return rootNode;
	}

	@Override
	public List<GermplasmDto> getGenerationHistory(final Integer gid) {
		final List<GermplasmDto> germplasmGenerationHistory = new ArrayList<>();

		GermplasmDto currentGermplasm = this.daoFactory.getGermplasmDao().getGermplasmDtoByGid(gid);
		if (currentGermplasm != null) {
			germplasmGenerationHistory.add(currentGermplasm);

			while (currentGermplasm.getGnpgs() == -1) {
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
			return this.getNumberOfGenerations(gid, includeDerivativeLine);
		} catch (final MaxPedigreeLevelReachedException e) {
			return GENERATIONS_COUNTER.get();
		}finally {
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
		final Germplasm germplasm = this.germplasmService.getGermplasmWithPreferredName(gid);

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
	private GermplasmTreeNode addParents(final GermplasmTreeNode node, final int level, final Germplasm germplasmOfNode,
		final boolean excludeDerivativeLines) {
		if (level != 1) {
			final Integer maleGid = germplasmOfNode.getGpid2();
			final Integer femaleGid = germplasmOfNode.getGpid1();
			if (germplasmOfNode.getGnpgs() == -1) {
				if (excludeDerivativeLines) {
					// get and add the source germplasm
					final Germplasm parent = this.germplasmService.getGermplasmWithPreferredName(femaleGid);
					if (parent != null) {
						this.addNodeForKnownParents(node, level, parent, true);
					}
				} else {
					// Get and add the source germplasm, if it is unknown
					if (maleGid != 0) {
						this.addMaleParentNode(node, level, maleGid, false);
						// Use female parent to continue traversal if source is unknown
					} else if (femaleGid != 0) {
						node.setMaleParentNode(this.createUnknownParent());
						this.addFemaleParentNode(node, level, femaleGid, false);
					}
				}
			} else if (germplasmOfNode.getGnpgs() >= 2) {
				// Get and add female and male parents for generative germplasm
				this.addNodeForParents(node, level, germplasmOfNode, excludeDerivativeLines);
			}
		}
		return node;
	}

	private void addNodeForParents(final GermplasmTreeNode node, final int level, final Germplasm germplasm,
		final boolean excludeDerivativeLines) {
		// For generative germplasm, do not add any node if both parents are UNKNOWN (GID=0)
		if (!(germplasm.getGpid1() == 0 && germplasm.getGpid2() == 0)) {
			this.addFemaleParentNode(node, level, germplasm.getGpid1(), excludeDerivativeLines);
			if (germplasm.getGpid2() == 0) {
				node.setMaleParentNode(this.createUnknownParent());
			} else {
				this.addMaleParentNode(node, level, germplasm.getGpid2(), excludeDerivativeLines);
			}

			// If there are more parents, get and add each of them
			if (germplasm.getGnpgs() > 2) {
				final List<Germplasm> otherParents =
					this.germplasmService.getProgenitorsWithPreferredName(germplasm.getGid());
				for (final Germplasm otherParent : otherParents) {
					final GermplasmTreeNode maleParentNode = new GermplasmTreeNode(otherParent);
					node.getOtherProgenitors().add(this.addParents(maleParentNode, level - 1, otherParent, excludeDerivativeLines));
				}
			}
		}
	}

	private void addNodeForKnownParents(final GermplasmTreeNode node, final int level, final Germplasm germplasm,
		final boolean excludeDerivativeLines) {
		this.addFemaleParentNode(node, level, germplasm.getGpid1(), excludeDerivativeLines);
		this.addMaleParentNode(node, level, germplasm.getGpid2(), excludeDerivativeLines);
	}

	private void addMaleParentNode(final GermplasmTreeNode node, final int level, final Integer gid, final boolean excludeDerivativeLines) {
		final Germplasm maleParent = this.germplasmService.getGermplasmWithPreferredName(gid);
		if (maleParent != null) {
			final GermplasmTreeNode maleParentNode = new GermplasmTreeNode(maleParent);
			node.setMaleParentNode(maleParentNode);
			this.addParents(maleParentNode, level - 1, maleParent, excludeDerivativeLines);
		}
	}

	private void addFemaleParentNode(final GermplasmTreeNode node, final int level, final Integer gid,
		final boolean excludeDerivativeLines) {
		final Germplasm femaleParent = this.germplasmService.getGermplasmWithPreferredName(gid);
		if (femaleParent != null) {
			final GermplasmTreeNode femaleParentNode = new GermplasmTreeNode(femaleParent);
			node.setFemaleParentNode(femaleParentNode);
			this.addParents(femaleParentNode, level - 1, femaleParent, excludeDerivativeLines);
		}
	}

	private GermplasmTreeNode createUnknownParent() {
		return new GermplasmTreeNode(0, Name.UNKNOWN, null, null, null);
	}

	public Integer getNumberOfGenerations(final Integer gid, final Boolean includeDerivativeLine) {
		Integer maxPedigreeLevel = 0;

		if (gid == null || gid == 0) {
			return maxPedigreeLevel;
		}

		final Germplasm germplasm = this.daoFactory.getGermplasmDao().getById(gid);

		if (germplasm.getGnpgs() == -1) {
			if (!includeDerivativeLine) {
				maxPedigreeLevel = this.getMaxGenerationCountFromParent(germplasm.getGpid1(), false);
			} else {
				maxPedigreeLevel = this.getMaxGenerationCountFromParent(germplasm.getGpid2(), true);
			}
		} else if (germplasm.getGnpgs() >= 2) {
			maxPedigreeLevel = this.getMaxGenerationCountFromBothParents(gid, includeDerivativeLine);
			if (germplasm.getGnpgs() > 2) {
				final List<Germplasm> otherParents =
					this.germplasmService.getProgenitorsWithPreferredName(gid);
				for (final Germplasm otherParent : otherParents) {
					maxPedigreeLevel = Math.max(maxPedigreeLevel, this.getGenerationsCount(otherParent.getGid(), includeDerivativeLine));
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
		final Integer gid, final boolean includeDerivativeLine) {
		final int currentPedigreeCount = GENERATIONS_COUNTER.get();

		final Germplasm germplasm = this.daoFactory.getGermplasmDao().getById(gid);

		final Integer numOfPedigreeFromParent1 = this.getGenerationsCount(germplasm.getGpid1(), includeDerivativeLine);
		GENERATIONS_COUNTER.set(currentPedigreeCount);
		final Integer numOfPedigreeFromParent2 = this.getGenerationsCount(germplasm.getGpid2(), includeDerivativeLine);

		return Math.max(numOfPedigreeFromParent1, numOfPedigreeFromParent2);
	}

	private Integer getGenerationsCount(final Integer parentId, final boolean includeDerivativeLine) {
		if (parentId != null) {
			this.incrementGenerationsCounter();
			return this.getNumberOfGenerations(parentId, includeDerivativeLine);
		}
		return 0;
	}

	private Integer getMaxGenerationCountFromParent(final Integer parentId, final boolean includeDerivativeLines) {
		if (parentId == 0) {
			return 0;
		}
		if (!includeDerivativeLines) {
			return this.getMaxGenerationCountFromBothParents(parentId, false);
		} else {
			this.incrementGenerationsCounter();
			return this.getNumberOfGenerations(parentId, true);
		}
	}

}
