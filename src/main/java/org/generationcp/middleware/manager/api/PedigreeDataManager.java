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

package org.generationcp.middleware.manager.api;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;


public interface PedigreeDataManager{

    /**
     * Creates a pedigree tree for the Germplasm identified by the given gid.
     * The tree contains all generative progenitors down to the specified level.
     * The Germplasm POJOs included in the tree have their preferred names
     * pre-loaded. The root of the tree is the Germplasm identified by the given
     * gid parameter. The nodes down the tree are the ancestors of the nodes
     * above them.
     * 
     * Example tree:
     * 
     * Result of calling: generatePedigreeTree(new Integer(306436), 4);
     * 
     * 306436 : TOX 494 (root node) 33208 : 63-83 (child node of root,
     * representing parent of Germplasm 306436) 2269311 : 63-83 310357 : IRAT 2
     * 96783 : IGUAPE CATETO (child node of root, representing parent of
     * Germplasm 306436) 312744 : RPCB-2B-849 (child node of root, representing
     * parent of Germplasm 306436) 2268822 : RPCB-2B-849 3160 : IR 1416-131
     * (child node of root, representing parent of Germplasm 306436) 2231 : IR
     * 1416 1163 : IR 400-28-4-5 (child node containing Germplasm 2231,
     * representing parent of Germplasm 2231) 2229 : TE TEP (child node
     * containing Germplasm 2231, representing parent of Germplasm 2231) 312646
     * : LITA 506 (child node of root, representing parent of Germplasm 306436)
     * 
     * 
     * @param gid
     *            - GID of a Germplasm
     * @param level
     *            - level of the tree to be created
     * @return GermplasmPedigreeTree representing the pedigree tree
     * @throws MiddlewareQueryException
     */
    public GermplasmPedigreeTree generatePedigreeTree(Integer gid, int level) throws MiddlewareQueryException;
    
    /**
     * Creates a pedigree tree for the Germplasm identified by the given gid.
     * The tree contains all generative progenitors down to the specified level.
     * The Germplasm POJOs included in the tree have their preferred names
     * pre-loaded. The root of the tree is the Germplasm identified by the given
     * gid parameter. The nodes down the tree are the ancestors of the nodes
     * above them.
     * 
     * Example tree:
     * 
     * Result of calling: generatePedigreeTree(new Integer(306436), 4, true);
     * 
     * 306436 : TOX 494 (root node) 33208 : 63-83 (child node of root,
     * representing parent of Germplasm 306436) 2269311 : 63-83 310357 : IRAT 2
     * 96783 : IGUAPE CATETO (child node of root, representing parent of
     * Germplasm 306436) 312744 : RPCB-2B-849 (child node of root, representing
     * parent of Germplasm 306436) 2268822 : RPCB-2B-849 3160 : IR 1416-131
     * (child node of root, representing parent of Germplasm 306436) 2231 : IR
     * 1416 1163 : IR 400-28-4-5 (child node containing Germplasm 2231,
     * representing parent of Germplasm 2231) 2229 : TE TEP (child node
     * containing Germplasm 2231, representing parent of Germplasm 2231) 312646
     * : LITA 506 (child node of root, representing parent of Germplasm 306436)
     * 
     * 
     * @param gid
     *            - GID of a Germplasm
     * @param level
     *            - level of the tree to be created
     * @param includeDerivativeLines
     *            - option to include derivative lines on result
     * @return GermplasmPedigreeTree representing the pedigree tree
     * @throws MiddlewareQueryException
     */
    public GermplasmPedigreeTree generatePedigreeTree(Integer gid, int level, Boolean includeDerivativeLines) throws MiddlewareQueryException;

    /**
     * Returns the GermplasmPedigreeTree object which represents the derivative
     * neighborhood for the germplasm identified by the given gid. The
     * derivative neighborhood is created by tracing back the source parents
     * from the given germplasm until the given number of steps backward is
     * reached or the first source germplasm created by a generative method is
     * reached, whichever comes first. The last source parent reached by tracing
     * back becomes the root of the GermplasmPedigreeTree object. From the root,
     * all immediate derived lines are retrieved and added to the tree. And then
     * from each of those derived germplasms, all immediate derived lines are
     * retrieved and added to the tree, and so on and so forth. The number of
     * levels of the tree is the sum of the actual number of steps backward made
     * to reach the root and the given number of steps forward plus 1 (for the
     * level which the given germplasm belongs).
     * 
     * The Germplasm POJOs included in the tree have their preferred names
     * pre-loaded.
     * 
     * @param gid
     * @param numberOfStepsBackward
     *            - number of steps backward from the germplasm identified by
     *            the given gid
     * @param numberOfStepsForward
     *            - number of steps forward from the germplasm identified by the
     *            given gid
     * @return GermplasmPedigreeTree representing the neighborhood
     */
    public GermplasmPedigreeTree getDerivativeNeighborhood(Integer gid, int numberOfStepsBackward, int numberOfStepsForward)
            throws MiddlewareQueryException;

    /**
     * Returns the GermplasmPedigreeTree object which represents the maintenance
     * neighborhood for the germplasm identified by the given gid. The
     * maintenance neighborhood is created by tracing back the source parents
     * from the given germplasm until the given number of steps backward is
     * reached or the first source germplasm created by a generative method is
     * reached, whichever comes first. The last source parent reached by tracing
     * back becomes the root of the GermplasmPedigreeTree object. From the root,
     * all immediate derived lines (created by the maintenance method) are retrieved and added to the tree. And then
     * from each of those derived germplasms, all immediate derived lines are
     * retrieved and added to the tree, and so on and so forth. The number of
     * levels of the tree is the sum of the actual number of steps backward made
     * to reach the root and the given number of steps forward plus 1 (for the
     * level which the given germplasm belongs).
     * 
     * The Germplasm POJOs included in the tree have their preferred names
     * pre-loaded.
     * 
     * @param gid
     * @param numberOfStepsBackward
     *            - number of steps backward from the germplasm identified by
     *            the given gid
     * @param numberOfStepsForward
     *            - number of steps forward from the germplasm identified by the
     *            given gid
     * @return GermplasmPedigreeTree representing the neighborhood
     */
    public GermplasmPedigreeTree getMaintenanceNeighborhood(Integer gid, int numberOfStepsBackward, int numberOfStepsForward)
            throws MiddlewareQueryException;

}
