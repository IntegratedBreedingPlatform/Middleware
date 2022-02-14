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

	/***
	 * Returns female parent and male parent of given gid
	 * @param gids
	 * @param level
	 * @param includeDerivativeLines
	 * @return
	 */
	Table<Integer, String, Optional<Germplasm>> generatePedigreeTable(Set<Integer> gids, Integer level, Boolean includeDerivativeLines);
}
