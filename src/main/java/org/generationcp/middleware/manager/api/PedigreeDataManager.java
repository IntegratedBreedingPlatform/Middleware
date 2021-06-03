/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.manager.api;

import com.google.common.collect.Table;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;
import org.generationcp.middleware.pojos.Progenitor;
import org.generationcp.middleware.util.MaxPedigreeLevelReachedException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PedigreeDataManager {

	/**
	 * Creates a pedigree tree for the Germplasm identified by the given gid. The tree contains all generative progenitors down to the
	 * specified level. The Germplasm POJOs included in the tree have their preferred names pre-loaded. The root of the tree is the
	 * Germplasm identified by the given gid parameter. The nodes down the tree are the ancestors of the nodes above them.
	 *
	 * Example tree:
	 *
	 * Result of calling: generatePedigreeTree(new Integer(306436), 4);
	 *
	 * 306436 : TOX 494 (root node) 33208 : 63-83 (child node of root, representing parent of Germplasm 306436) 2269311 : 63-83 310357 :
	 * IRAT 2 96783 : IGUAPE CATETO (child node of root, representing parent of Germplasm 306436) 312744 : RPCB-2B-849 (child node of root,
	 * representing parent of Germplasm 306436) 2268822 : RPCB-2B-849 3160 : IR 1416-131 (child node of root, representing parent of
	 * Germplasm 306436) 2231 : IR 1416 1163 : IR 400-28-4-5 (child node containing Germplasm 2231, representing parent of Germplasm 2231)
	 * 2229 : TE TEP (child node containing Germplasm 2231, representing parent of Germplasm 2231) 312646 : LITA 506 (child node of root,
	 * representing parent of Germplasm 306436)
	 *
	 *
	 * @param gid - GID of a Germplasm
	 * @param level - level of the tree to be created
	 * @return GermplasmPedigreeTree representing the pedigree tree
	 */
	GermplasmPedigreeTree generatePedigreeTree(Integer gid, int level);

	/**
	 * Creates a pedigree tree for the Germplasm identified by the given gid. The tree contains all generative progenitors down to the
	 * specified level. The Germplasm POJOs included in the tree have their preferred names pre-loaded. The root of the tree is the
	 * Germplasm identified by the given gid parameter. The nodes down the tree are the ancestors of the nodes above them.
	 *
	 * Example tree:
	 *
	 * Result of calling: generatePedigreeTree(new Integer(306436), 4, true);
	 *
	 * 306436 : TOX 494 (root node) 33208 : 63-83 (child node of root, representing parent of Germplasm 306436) 2269311 : 63-83 310357 :
	 * IRAT 2 96783 : IGUAPE CATETO (child node of root, representing parent of Germplasm 306436) 312744 : RPCB-2B-849 (child node of root,
	 * representing parent of Germplasm 306436) 2268822 : RPCB-2B-849 3160 : IR 1416-131 (child node of root, representing parent of
	 * Germplasm 306436) 2231 : IR 1416 1163 : IR 400-28-4-5 (child node containing Germplasm 2231, representing parent of Germplasm 2231)
	 * 2229 : TE TEP (child node containing Germplasm 2231, representing parent of Germplasm 2231) 312646 : LITA 506 (child node of root,
	 * representing parent of Germplasm 306436)
	 *
	 *
	 * @param gid - GID of a Germplasm
	 * @param level - level of the tree to be created
	 * @param includeDerivativeLines - option to include derivative lines on result
	 * @return GermplasmPedigreeTree representing the pedigree tree
	 */
	GermplasmPedigreeTree generatePedigreeTree(Integer gid, int level, Boolean includeDerivativeLines);

	/**
	 *
	 * @param gid
	 * @return
	 */
	Integer countPedigreeLevel(Integer gid, Boolean includeDerivativeLine) throws MaxPedigreeLevelReachedException;

	Integer countPedigreeLevel(Integer gid, Boolean includeDerivativeLine, boolean calculateFullPedigree) throws MaxPedigreeLevelReachedException;

	/**
	 * Returns the GermplasmPedigreeTree object which represents the derivative neighborhood for the germplasm identified by the given gid.
	 * The derivative neighborhood is created by tracing back the source parents from the given germplasm until the given number of steps
	 * backward is reached or the first source germplasm created by a generative method is reached, whichever comes first. The last source
	 * parent reached by tracing back becomes the root of the GermplasmPedigreeTree object. From the root, all immediate derived lines are
	 * retrieved and added to the tree. And then from each of those derived germplasms, all immediate derived lines are retrieved and added
	 * to the tree, and so on and so forth. The number of levels of the tree is the sum of the actual number of steps backward made to reach
	 * the root and the given number of steps forward plus 1 (for the level which the given germplasm belongs).
	 *
	 * The Germplasm POJOs included in the tree have their preferred names pre-loaded.
	 *
	 * @param gid
	 * @param numberOfStepsBackward - number of steps backward from the germplasm identified by the given gid
	 * @param numberOfStepsForward - number of steps forward from the germplasm identified by the given gid
	 * @return GermplasmPedigreeTree representing the neighborhood
	 */
	GermplasmPedigreeTree getDerivativeNeighborhood(Integer gid, int numberOfStepsBackward, int numberOfStepsForward);

	/**
	 * Returns the GermplasmPedigreeTree object which represents the maintenance neighborhood for the germplasm identified by the given gid.
	 * The maintenance neighborhood is created by tracing back the source parents from the given germplasm until the given number of steps
	 * backward is reached or the first source germplasm created by a generative method is reached, whichever comes first. The last source
	 * parent reached by tracing back becomes the root of the GermplasmPedigreeTree object. From the root, all immediate derived lines
	 * (created by the maintenance method) are retrieved and added to the tree. And then from each of those derived germplasms, all
	 * immediate derived lines are retrieved and added to the tree, and so on and so forth. The number of levels of the tree is the sum of
	 * the actual number of steps backward made to reach the root and the given number of steps forward plus 1 (for the level which the
	 * given germplasm belongs).
	 *
	 * The Germplasm POJOs included in the tree have their preferred names pre-loaded.
	 *
	 * @param gid
	 * @param numberOfStepsBackward - number of steps backward from the germplasm identified by the given gid
	 * @param numberOfStepsForward - number of steps forward from the germplasm identified by the given gid
	 * @return GermplasmPedigreeTree representing the neighborhood
	 */
	GermplasmPedigreeTree getMaintenanceNeighborhood(Integer gid, int numberOfStepsBackward, int numberOfStepsForward);

	/**
	 * Returns the Germplasm representing the children of the Germplasm identified by the given gid. The function returns a List of Object
	 * arrays. Each Object array contains 2 elements, the first is an int to specify the progenitor number and the second is the Germplasm
	 * POJO representing the child germplasm.
	 *
	 * @param gid - gid of the parent Germplasm
	 * @param start - the starting index of the sublist of results to be returned
	 * @param numOfRows - the number of rows to be included in the sublist of results to be returned
	 * @return List of Object arrays, the arrays have 2 elements in them
	 */
	List<Object[]> getDescendants(Integer gid, int start, int numOfRows);

	/**
	 * Returns the number of children of the Germplasm identified by the given gid.
	 *
	 * @param gid
	 * @return count of children
	 */
	long countDescendants(Integer gid);

	/**
	 * Returns the Germplasm representing the parent of the child Germplasm identified by the given gid and having the given progenitor
	 * number.
	 *
	 * @param gid - gid of child Germplasm
	 * @param progenitorNumber - progenitor number of the parent with respect to the child
	 * @return Germplasm POJO
	 */
	Germplasm getParentByGIDAndProgenitorNumber(Integer gid, Integer progenitorNumber);

	int calculateRecurrentParent(Integer maleParentGID, Integer femaleParentGID);
	
	List<Progenitor> getProgenitorsByGID(final Integer gid);

	/**
	 * Given the gid of the child germplasm, the gid of the parent germplasm and the progenitor number, this method makes the necessary
	 * changes to save the relationship on the database.
	 *
	 * This method will either update the Germplasm record, to change the gpid1 or gpid2 fields (if the progenitor number given is 1 or 2),
	 * or will either add or update the Progenitor record which represents this relationship. A new Progenitor record will be stored when
	 * necessary.
	 *
	 * @param gid the gid
	 * @param progenitorId the progenitor id
	 * @param progenitorNumber the progenitor number
	 * @return Returns the id of the updated Progenitor
	 */
	Integer updateProgenitor(Integer gid, Integer progenitorId, Integer progenitorNumber);

	/***
	 * Returns female parent and male parent of given gid
	 * @param gids
	 * @param level
	 * @param includeDerivativeLines
	 * @return
	 */
	Table<Integer, String, Optional<Germplasm>> generatePedigreeTable(Set<Integer> gids, Integer level, Boolean includeDerivativeLines);
}
