/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.manager;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.ProgenitorDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.PedigreeDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Progenitor;
import org.generationcp.middleware.util.MaxPedigreeLevelReachedException;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the PedigreeDataManager interface. To instantiate this
 * class, a Hibernate Session must be passed to its constructor.
 */
@Transactional
public class PedigreeDataManagerImpl extends DataManager implements PedigreeDataManager {

	public static final int MAX_PEDIGREE_LEVEL = 5;
	public static final int NONE = 0;
	public static final int MALE_RECURRENT = 1;
	public static final int FEMALE_RECURRENT = 2;

	private GermplasmDataManager germplasmDataManager;
	private static final ThreadLocal<Integer> PEDIGREE_COUNTER = new ThreadLocal<>();
	private static final ThreadLocal<Boolean> CALCULATE_FULL = new ThreadLocal<>();

	public PedigreeDataManagerImpl() {
	}

	public PedigreeDataManagerImpl(final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
		this.germplasmDataManager = new GermplasmDataManagerImpl(sessionProvider);
	}

	@Override
	public GermplasmPedigreeTree generatePedigreeTree(final Integer gid, final int level) {
		return this.generatePedigreeTree(gid, level, false);
	}

	@Override
	public Integer countPedigreeLevel(final Integer gid, final Boolean includeDerivativeLine) throws MaxPedigreeLevelReachedException {
		return this.countPedigreeLevel(gid, includeDerivativeLine, false);
	}

	@Override
	public Integer countPedigreeLevel(final Integer gid, final Boolean includeDerivativeLine, final boolean calculateFullPedigree)
		throws MaxPedigreeLevelReachedException {
		try {
			PEDIGREE_COUNTER.set(1);
			CALCULATE_FULL.set(calculateFullPedigree);
			return this.getPedigreeLevelCount(gid, includeDerivativeLine);
		} finally {
			PEDIGREE_COUNTER.remove();
			CALCULATE_FULL.remove();
		}
	}

	public Integer getPedigreeLevelCount(final Integer gid, final Boolean includeDerivativeLine) {
		Integer maxPedigreeLevel = 0;

		if (gid == null || gid == 0) {
			return maxPedigreeLevel;
		}

		final Germplasm germplasm = this.getGermplasmDao().getById(gid);

		if (germplasm.getGnpgs() == -1) {
			if (!includeDerivativeLine) {
				maxPedigreeLevel = this.getMaxPedigreeLevelFromParent(gid, 1, includeDerivativeLine);
			} else {
				maxPedigreeLevel = this.getMaxPedigreeLevelFromParent(gid, 2, includeDerivativeLine);
			}
		} else if (germplasm.getGnpgs() >= 2) {
			maxPedigreeLevel = this.getMaxPedigreeLevelFromBothParents(gid, includeDerivativeLine);
			if (germplasm.getGnpgs() > 2) {
				maxPedigreeLevel =
					this.getMaxPedigreeLevelFromProgenitor(gid, germplasm.getGnpgs(), includeDerivativeLine, maxPedigreeLevel);
			}
		}

		return maxPedigreeLevel + 1;
	}

	private Integer getMaxPedigreeLevelFromProgenitor(
		final Integer gid, final Integer gnpgs, final boolean includeDerivativeLine, final Integer maxPedigreeLevel) {
		final Germplasm parentGermplasm = this.getParentByGIDAndProgenitorNumber(gid, gnpgs);

		if (parentGermplasm != null) {
			this.incrementPedigreeLevelCounter();
			final Integer numOfPedigree = this.getPedigreeLevelCount(parentGermplasm.getGid(), includeDerivativeLine);
			if (numOfPedigree > maxPedigreeLevel) {
				return numOfPedigree;
			}
		}

		return maxPedigreeLevel;
	}

	protected void incrementPedigreeLevelCounter() {
		final int currentCount = this.getCurrentCounterCount();
		final boolean calculateFull = this.getCalculateFullFlagValue();
		if ((currentCount + 1) > MAX_PEDIGREE_LEVEL && !calculateFull) {
			throw MaxPedigreeLevelReachedException.getInstance();
		} else {
			PEDIGREE_COUNTER.set(currentCount + 1);
		}
	}

	protected int getCurrentCounterCount() {
		return PEDIGREE_COUNTER.get();
	}

	protected boolean getCalculateFullFlagValue() {
		return CALCULATE_FULL.get();
	}

	private Integer getMaxPedigreeLevelFromParent(
		final Integer gid, final Integer parentNo, final boolean includeDerivativeLine) {
		final Integer parentId = this.getGermplasmProgenitorID(gid, parentNo);
		if (!includeDerivativeLine && parentId != null) {
			return this.getMaxPedigreeLevelFromBothParents(parentId, includeDerivativeLine);
		} else if (parentId != null) {
			this.incrementPedigreeLevelCounter();
			return this.getPedigreeLevelCount(parentId, includeDerivativeLine);
		}
		return 0;
	}

	private Integer getMaxPedigreeLevelFromBothParents(
		final Integer gid, final boolean includeDerivativeLine) {
		final int currentPedigreeCount = PEDIGREE_COUNTER.get();

		final Integer numOfPedigreeFromParent1 = this.getPedigreeLevel(gid, 1, includeDerivativeLine);
		PEDIGREE_COUNTER.set(currentPedigreeCount);
		final Integer numOfPedigreeFromParent2 = this.getPedigreeLevel(gid, 2, includeDerivativeLine);

		if (numOfPedigreeFromParent2 > numOfPedigreeFromParent1) {
			return numOfPedigreeFromParent2;
		}

		return numOfPedigreeFromParent1;
	}

	private Integer getPedigreeLevel(final Integer gid, final Integer parentNo, final boolean includeDerivativeLine) {
		final Integer parentId = this.getGermplasmProgenitorID(gid, parentNo);
		if (parentId != null) {
			this.incrementPedigreeLevelCounter();
			return this.getPedigreeLevelCount(parentId, includeDerivativeLine);
		}
		return 0;
	}

	private Integer getGermplasmProgenitorID(final Integer gid, final Integer proNo) {
		if (gid == null) {
			return null;
		}

		final Germplasm germplasm = this.getParentByGIDAndProgenitorNumber(gid, proNo);

		if (germplasm != null) {
			return germplasm.getGid();
		}
		return null;
	}

	@Override
	public GermplasmPedigreeTree generatePedigreeTree(final Integer gid, final int level, final Boolean includeDerivativeLines) {
		final GermplasmPedigreeTree tree = new GermplasmPedigreeTree();
		// set root node
		final Germplasm root = this.germplasmDataManager.getGermplasmWithPrefName(gid);

		if (root != null) {
			GermplasmPedigreeTreeNode rootNode = new GermplasmPedigreeTreeNode();
			rootNode.setGermplasm(root);
			if (level > 1) {
				if (includeDerivativeLines) {
					rootNode = this.addParents(rootNode, level);
				} else {
					rootNode = this.addParentsExcludeDerivativeLines(rootNode, level);
				}
			}
			tree.setRoot(rootNode);
			return tree;
		}
		return null;
	}

	/**
	 * Given a GermplasmPedigreeTreeNode and the level of the desired tree, add parents
	 * to the node recursively until the specified level of the tree is reached.
	 *
	 * @param node
	 * @param level
	 * @return the given GermplasmPedigreeTreeNode with its parents added to it
	 */
	private GermplasmPedigreeTreeNode addParents(final GermplasmPedigreeTreeNode node, final int level) {
		if (level == 1) {
			return node;
		} else {
			// get parents of node
			final Germplasm germplasmOfNode = node.getGermplasm();
			final Integer maleGid = germplasmOfNode.getGpid2();
			final boolean excludeDerivativeLines = false;
			if (germplasmOfNode.getGnpgs() == -1) {
				// Get and add the source germplasm
				this.addNodeForKnownParent(node, level, maleGid, excludeDerivativeLines);

			} else if (germplasmOfNode.getGnpgs() >= 2) {
				// Get and add female and male parents
				final Integer femaleGid = germplasmOfNode.getGpid1();
				this.addNodesForParents(node, level, femaleGid, maleGid, excludeDerivativeLines);

				// IF there are more parents, get and add each of them
				if (germplasmOfNode.getGnpgs() > 2) {
					final List<Germplasm> otherParents =
						this.germplasmDataManager.getProgenitorsByGIDWithPrefName(germplasmOfNode.getGid());
					for (final Germplasm otherParent : otherParents) {
						final GermplasmPedigreeTreeNode nodeForOtherParent = new GermplasmPedigreeTreeNode();
						nodeForOtherParent.setGermplasm(otherParent);
						node.getLinkedNodes().add(this.addParents(nodeForOtherParent, level - 1));
					}
				}
			}
			return node;
		}
	}

	void addNodeForParent(final GermplasmPedigreeTreeNode node, final int level, final Integer parentGid,
		final boolean excludeDerivativeLines) {
		if (parentGid == 0) {
			this.addUnknownParent(node);

		} else {
			this.addNodeForKnownParent(node, level, parentGid, excludeDerivativeLines);
		}
	}

	private void addNodeForKnownParent(final GermplasmPedigreeTreeNode node, final int level, final Integer parentGid,
		final boolean excludeDerivativeLines) {
		final Germplasm parent = this.germplasmDataManager.getGermplasmWithPrefName(parentGid);
		if (parent != null) {
			final GermplasmPedigreeTreeNode nodeForParent = new GermplasmPedigreeTreeNode();
			nodeForParent.setGermplasm(parent);
			if (excludeDerivativeLines) {
				node.getLinkedNodes().add(this.addParentsExcludeDerivativeLines(nodeForParent, level - 1));
			} else {
				node.getLinkedNodes().add(this.addParents(nodeForParent, level - 1));
			}
		}
	}

	private void addUnknownParent(final GermplasmPedigreeTreeNode node) {
		final GermplasmPedigreeTreeNode nodeForParent = new GermplasmPedigreeTreeNode();
		nodeForParent.setGermplasm(this.germplasmDataManager.getUnknownGermplasmWithPreferredName());
		node.getLinkedNodes().add(nodeForParent);
	}

	/**
	 * Given a GermplasmPedigreeTreeNode and the level of the desired tree, add parents
	 * to the node recursively excluding derivative lines until the specified level of
	 * the tree is reached.
	 *
	 * @param node
	 * @param level
	 * @return the given GermplasmPedigreeTreeNode with its parents added to it
	 */
	private GermplasmPedigreeTreeNode addParentsExcludeDerivativeLines(final GermplasmPedigreeTreeNode node, final int level) {
		if (level == 1) {
			return node;
		} else {
			// get parents of node
			final Germplasm germplasmOfNode = node.getGermplasm();

			final Integer femaleGid = germplasmOfNode.getGpid1();
			final boolean excludeDerivativeLines = true;
			if (germplasmOfNode.getGnpgs() == -1) {
				// get and add the source germplasm

				final Germplasm parent = this.germplasmDataManager.getGermplasmWithPrefName(femaleGid);
				if (parent != null) {
					this.addNodeForKnownParent(node, level, parent.getGpid1(), excludeDerivativeLines);
					this.addNodeForKnownParent(node, level, parent.getGpid2(), excludeDerivativeLines);
				}
			} else if (germplasmOfNode.getGnpgs() >= 2) {
				// Get and add female and male parents
				final Integer maleGid = germplasmOfNode.getGpid2();
				this.addNodesForParents(node, level, femaleGid, maleGid, excludeDerivativeLines);

				if (germplasmOfNode.getGnpgs() > 2) {
					// if there are more parents, get and add each of them
					final List<Germplasm> otherParents =
						this.germplasmDataManager.getProgenitorsByGIDWithPrefName(germplasmOfNode.getGid());
					for (final Germplasm otherParent : otherParents) {
						final GermplasmPedigreeTreeNode nodeForOtherParent = new GermplasmPedigreeTreeNode();
						nodeForOtherParent.setGermplasm(otherParent);
						node.getLinkedNodes().add(this.addParentsExcludeDerivativeLines(nodeForOtherParent, level - 1));
					}
				}
			}
			return node;
		}
	}

	private void addNodesForParents(final GermplasmPedigreeTreeNode node, final int level, final Integer femaleGid, final Integer maleGid,
		final boolean excludeDerivativeLines) {
		// Do not add any node if both parents are UNKNOWN (GID=0)
		if (!(maleGid == 0 && femaleGid == 0)) {
			this.addNodeForParent(node, level, femaleGid, excludeDerivativeLines);
			this.addNodeForParent(node, level, maleGid, excludeDerivativeLines);
		}
	}

	@Override
	public GermplasmPedigreeTree getMaintenanceNeighborhood(final Integer gid, final int numberOfStepsBackward,
		final int numberOfStepsForward) {

		return this.getNeighborhood(gid, numberOfStepsBackward, numberOfStepsForward, 'M');
	}

	@Override
	public GermplasmPedigreeTree getDerivativeNeighborhood(final Integer gid, final int numberOfStepsBackward,
		final int numberOfStepsForward) {

		return this.getNeighborhood(gid, numberOfStepsBackward, numberOfStepsForward, 'D');
	}

	private GermplasmPedigreeTree getNeighborhood(final Integer gid, final int numberOfStepsBackward, final int numberOfStepsForward,
		final char methodType) {
		final GermplasmPedigreeTree neighborhood = new GermplasmPedigreeTree();

		// get the root of the neighborhood
		final Object[] traceResult = this.traceRoot(gid, numberOfStepsBackward, methodType);

		if (traceResult != null && traceResult.length >= 2) {
			final Germplasm root = (Germplasm) traceResult[0];
			final Integer stepsLeft = (Integer) traceResult[1];

			GermplasmPedigreeTreeNode rootNode = new GermplasmPedigreeTreeNode();
			rootNode.setGermplasm(root);

			// get the derived lines from the root until the whole neighborhood is created
			final int treeLevel = numberOfStepsBackward - stepsLeft + numberOfStepsForward;
			rootNode = this.getDerivedLines(rootNode, treeLevel, methodType);

			neighborhood.setRoot(rootNode);

			return neighborhood;
		} else {
			return null;
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
		final Germplasm germplasm = this.germplasmDataManager.getGermplasmWithPrefName(gid);

		if (germplasm == null) {
			return new Object[0];
		} else if (steps == 0 || germplasm.getGnpgs() != -1) {
			return new Object[] {germplasm, Integer.valueOf(steps)};
		} else {
			int nextStep = steps;

			//for MAN neighborhood, move the step count only if the ancestor is a MAN.
			//otherwise, skip through the ancestor without changing the step count
			if (methodType == 'M') {
				final Method method = this.germplasmDataManager.getMethodByID(germplasm.getMethodId());
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
	 * Recursive function to get the derived lines given a Germplasm. This
	 * constructs the derivative neighborhood.
	 *
	 * @param node
	 * @param steps
	 * @return
	 */
	private GermplasmPedigreeTreeNode getDerivedLines(final GermplasmPedigreeTreeNode node, final int steps, final char methodType) {
		if (steps <= 0) {
			return node;
		} else {
			final Integer gid = node.getGermplasm().getGid();
			final List<Germplasm> derivedGermplasms = this.getChildren(gid, methodType);
			for (final Germplasm g : derivedGermplasms) {
				final GermplasmPedigreeTreeNode derivedNode = new GermplasmPedigreeTreeNode();
				derivedNode.setGermplasm(g);
				node.getLinkedNodes().add(this.getDerivedLines(derivedNode, steps - 1, methodType));
			}

			return node;
		}
	}

	private List<Germplasm> getChildren(final Integer gid, final char methodType) {
		return this.getGermplasmDao().getChildren(gid, methodType);
	}

	@Override
	public Germplasm getParentByGIDAndProgenitorNumber(final Integer gid, final Integer progenitorNumber) {
		return this.getGermplasmDao().getProgenitorByGID(gid, progenitorNumber);
	}

	@Override
	public List<Object[]> getDescendants(final Integer gid, final int start, final int numOfRows) {
		final List<Object[]> result = new ArrayList<>();
		Object[] germplasmList;

		final List<Germplasm> germplasmDescendant = this.getGermplasmDescendantByGID(gid, start, numOfRows);
		for (final Germplasm g : germplasmDescendant) {
			germplasmList = new Object[2];
			if (g.getGpid1().equals(gid)) {
				germplasmList[0] = 1;
			} else if (g.getGpid2().equals(gid)) {
				germplasmList[0] = 2;
			} else {
				germplasmList[0] =
					this.getProgenitorDao().getByGIDAndPID(g.getGid(), gid).getProgenitorNumber();
			}
			germplasmList[1] = g;

			result.add(germplasmList);
		}

		return result;
	}

	private List<Germplasm> getGermplasmDescendantByGID(final Integer gid, final int start,
		final int numOfRows) {
		return this.getGermplasmDao().getGermplasmDescendantByGID(gid, start, numOfRows);
	}

	@Override
	public long countDescendants(final Integer gid) {
		return this.getGermplasmDao().countGermplasmDescendantByGID(gid);
	}

	@Override
	public List<Germplasm> getManagementNeighbors(final Integer gid, final int start, final int numOfRows) {
		return this.getGermplasmDao().getManagementNeighbors(gid, start, numOfRows);
	}

	@Override
	public long countManagementNeighbors(final Integer gid) {
		return this.getGermplasmDao().countManagementNeighbors(gid);
	}

	@Override
	public long countGroupRelatives(final Integer gid) {
		return this.getGermplasmDao().countGroupRelatives(gid);
	}

	@Override
	public List<Germplasm> getGroupRelatives(final Integer gid, final int start, final int numRows) {
		return this.getGermplasmDao().getGroupRelatives(gid, start, numRows);
	}

	@Override
	public List<Germplasm> getGenerationHistory(final Integer gid) {
		final List<Germplasm> toreturn = new ArrayList<>();

		Germplasm currentGermplasm = this.germplasmDataManager.getGermplasmWithPrefName(gid);
		if (currentGermplasm != null) {
			toreturn.add(currentGermplasm);

			while (currentGermplasm.getGnpgs() == -1) {
				// trace back the sources
				final Integer sourceId = currentGermplasm.getGpid2();
				currentGermplasm = this.getGermplasmDataManager().getGermplasmWithPrefName(sourceId);

				if (currentGermplasm != null) {
					toreturn.add(currentGermplasm);
				} else {
					break;
				}
			}
		}
		return toreturn;
	}

	public GermplasmDataManager getGermplasmDataManager() {
		return this.germplasmDataManager;
	}

	public void setGermplasmDataManager(final GermplasmDataManager germplasmDataManager) {
		this.germplasmDataManager = germplasmDataManager;
	}

	public int calculateRecurrentParent(final Integer maleParentGID, final Integer femaleParentGID) {
		final Germplasm maleParent = this.getGermplasmDataManager().getGermplasmByGID(maleParentGID);
		final Germplasm femaleParent = this.getGermplasmDataManager().getGermplasmByGID(femaleParentGID);

		if (maleParent == null || femaleParent == null) {
			return NONE;
		}

		if (femaleParent.getGnpgs() >= 2 && (maleParentGID.equals(femaleParent.getGpid1())
			|| maleParentGID.equals(femaleParent.getGpid2()))) {
			return MALE_RECURRENT;
		} else if (maleParent.getGnpgs() >= 2 && (femaleParentGID.equals(maleParent.getGpid1())
			|| femaleParentGID.equals(maleParent.getGpid2()))) {
			return FEMALE_RECURRENT;
		}

		return NONE;
	}

	@Override
	public List<Progenitor> getProgenitorsByGID(final Integer gid) {
		return this.getProgenitorDao().getByGID(gid);
	}

	@Override
	public Integer updateProgenitor(final Integer gid, final Integer progenitorId, final Integer progenitorNumber) {

		// check if the germplasm record identified by gid exists
		final Germplasm child = this.germplasmDataManager.getGermplasmByGID(gid);
		if (child == null) {
			throw new MiddlewareQueryException("Error in PedigreeDataManager.updateProgenitor(gid=" + gid + ", progenitorId="
				+ progenitorId + ", progenitorNumber=" + progenitorNumber + "): There is no germplasm record with gid: " + gid,
				new Throwable());
		}

		// check if the germplasm record identified by progenitorId exists
		final Germplasm parent = this.germplasmDataManager.getGermplasmByGID(progenitorId);
		if (parent == null) {
			throw new MiddlewareQueryException(
				"Error in PedigreeDataManager.updateProgenitor(gid=" + gid + ", progenitorId=" + progenitorId + ", progenitorNumber="
					+ progenitorNumber + "): There is no germplasm record with progenitorId: " + progenitorId,
				new Throwable());
		}

		// check progenitor number
		if (progenitorNumber == 1 || progenitorNumber == 2) {
			if (progenitorNumber == 1) {
				child.setGpid1(progenitorId);
			} else {
				child.setGpid2(progenitorId);
			}

			final List<Germplasm> germplasm = new ArrayList<>();
			germplasm.add(child);
			this.germplasmDataManager.addOrUpdateGermplasm(germplasm, Operation.UPDATE);
		} else if (progenitorNumber > 2) {
			final ProgenitorDAO dao = this.getProgenitorDao();

			// check if there is an existing Progenitor record
			final Progenitor p = dao.getByGIDAndProgenitorNumber(gid, progenitorNumber);

			if (p != null) {
				// update the existing record
				p.setProgenitorGid(progenitorId);

				final List<Progenitor> progenitors = new ArrayList<>();
				progenitors.add(p);
				final int updated = this.addOrUpdateProgenitors(progenitors);
				if (updated == 1) {
					return progenitorId;
				}
			} else {
				// create new Progenitor record
				final Progenitor newRecord = new Progenitor(new Germplasm(gid), progenitorNumber, progenitorId);
				final List<Progenitor> progenitors = new ArrayList<>();
				progenitors.add(newRecord);
				final int added = this.addOrUpdateProgenitors(progenitors);
				if (added == 1) {
					return progenitorId;
				}
			}
		} else {
			throw new MiddlewareQueryException("Error in PedigreeDataManager.updateProgenitor(gid=" + gid + ", progenitorId="
				+ progenitorId + ", progenitorNumber=" + progenitorNumber + "): Invalid progenitor number: " + progenitorNumber,
				new Throwable());
		}

		return progenitorId;
	}

	private int addOrUpdateProgenitors(final List<Progenitor> progenitors) {

		int progenitorsSaved = 0;
		try {

			final ProgenitorDAO dao = this.getProgenitorDao();

			for (final Progenitor progenitor : progenitors) {
				dao.saveOrUpdate(progenitor);
				progenitorsSaved++;
			}

		} catch (final Exception e) {

			throw new MiddlewareQueryException(
				"Error encountered while saving Progenitor: PedigreeDataManager.addOrUpdateProgenitors(progenitors=" + progenitors
					+ "): " + e.getMessage(),
				e);
		}
		return progenitorsSaved;
	}

}
