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

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.generationcp.middleware.constant.ColumnLabels;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.PedigreeDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.generationcp.middleware.pojos.Progenitor;
import org.generationcp.middleware.util.MaxPedigreeLevelReachedException;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

	private DaoFactory daoFactory;

	public PedigreeDataManagerImpl() {
	}

	public PedigreeDataManagerImpl(final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
		this.germplasmDataManager = new GermplasmDataManagerImpl(sessionProvider);
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public GermplasmPedigreeTree generatePedigreeTree(final Integer gid, final int level) {
		return this.generatePedigreeTree(gid, level, false);
	}

	public Integer getPedigreeLevelCount(final Integer gid, final Boolean includeDerivativeLine) {
		Integer maxPedigreeLevel = 0;

		if (gid == null || gid == 0) {
			return maxPedigreeLevel;
		}

		final Germplasm germplasm = this.daoFactory.getGermplasmDao().getById(gid);

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
		if (level != 1) {
			// get parents of node
			final Germplasm germplasmOfNode = node.getGermplasm();
			final Integer maleGid = germplasmOfNode.getGpid2();
			final Integer femaleGid = germplasmOfNode.getGpid1();
			final boolean excludeDerivativeLines = false;
			if (germplasmOfNode.getGnpgs() == -1) {
				// Get and add the source germplasm, if it is unknown
				if (maleGid != 0) {
					this.addNodeForKnownParent(node, level, maleGid, excludeDerivativeLines);

					// Use female parent to continue traversal if source is unknown
				} else if (femaleGid != 0) {
					this.addNodeForDerivativeUnKnownMaleParentKnownFemaleParent(node, level, femaleGid, excludeDerivativeLines);
				}

			} else if (germplasmOfNode.getGnpgs() >= 2) {
				// Get and add female and male parents
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
		}
		return node;
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

	private void addNodeForDerivativeUnKnownMaleParentKnownFemaleParent(final GermplasmPedigreeTreeNode node, final int level, final int femaleGid,
																		boolean excludeDerivativeLines) {
		final GermplasmPedigreeTreeNode nodeForParent = new GermplasmPedigreeTreeNode();
		nodeForParent.setGermplasm(this.germplasmDataManager.getUnknownGermplasmWithPreferredName());
		node.getLinkedNodes().add(nodeForParent);
		this.addNodeForKnownParent(nodeForParent, level, femaleGid, excludeDerivativeLines);
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
		if (level != 1) {
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
		}
		return node;
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
	public Germplasm getParentByGIDAndProgenitorNumber(final Integer gid, final Integer progenitorNumber) {
		return this.daoFactory.getGermplasmDao().getProgenitorByGID(gid, progenitorNumber);
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
					this.daoFactory.getProgenitorDao().getByGIDAndPID(g.getGid(), gid).getProgenitorNumber();
			}
			germplasmList[1] = g;

			result.add(germplasmList);
		}

		return result;
	}

	private List<Germplasm> getGermplasmDescendantByGID(final Integer gid, final int start,
		final int numOfRows) {
		return this.daoFactory.getGermplasmDao().getGermplasmDescendantByGID(gid, start, numOfRows);
	}

	@Override
	public long countDescendants(final Integer gid) {
		return this.daoFactory.getGermplasmDao().countGermplasmDescendantByGID(gid);
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
		return this.daoFactory.getProgenitorDao().getByGID(gid);
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
			// check if there is an existing Progenitor record
			final Progenitor p = this.daoFactory.getProgenitorDao().getByGIDAndProgenitorNumber(gid, progenitorNumber);

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

	@Override
	public Table<Integer, String, Optional<Germplasm>> generatePedigreeTable(final Set<Integer> gids, final Integer level,
		final Boolean includeDerivativeLines) {
		final Table<Integer, String, Optional<Germplasm>> table = HashBasedTable.create();
		final Integer numberOfLevelsToTraverse = level + 1;//Not zero index
		for(final Integer gid : gids) {
			final GermplasmPedigreeTree root = this.generatePedigreeTree(gid, numberOfLevelsToTraverse, includeDerivativeLines);
			for (final GermplasmPedigreeTreeNode linkedNode : root.getRoot().getLinkedNodes()) {
				if(table.row(gid).isEmpty()) {
					table.put(gid, ColumnLabels.FGID.getName() , Optional.of(linkedNode.getGermplasm()));
					table.put(gid, ColumnLabels.MGID.getName(), Optional.empty());
				} else{
					table.put(gid, ColumnLabels.MGID.getName() , Optional.of(linkedNode.getGermplasm()));
				}
			}
			if(table.row(gid).isEmpty()) {
				table.put(gid, ColumnLabels.FGID.getName(), Optional.empty());
				table.put(gid, ColumnLabels.MGID.getName(), Optional.empty());
			}
		}
		return table;
	}

	private int addOrUpdateProgenitors(final List<Progenitor> progenitors) {

		int progenitorsSaved = 0;
		try {

			for (final Progenitor progenitor : progenitors) {
				this.daoFactory.getProgenitorDao().saveOrUpdate(progenitor);
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
