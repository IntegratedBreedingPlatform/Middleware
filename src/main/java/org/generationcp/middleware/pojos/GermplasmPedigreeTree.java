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

package org.generationcp.middleware.pojos;

/**
 * This class represents a pedigree tree. From the root node down to the leaf
 * nodes, one can traverse the entire tree.
 * 
 * The tree is traversed by getting the root node first. This is represented by
 * a GermplasmPedigreeTreeNode object. The children nodes can be retrieved by
 * calling its getLinkedNodes() method. This method returns a List of child
 * nodes. For each child node retrieve the children nodes. Recursively do this
 * to expand all the nodes included in the tree.
 * 
 * @author Kevin Manansala
 * 
 */
public class GermplasmPedigreeTree{

    private GermplasmPedigreeTreeNode root;

    public GermplasmPedigreeTreeNode getRoot() {
        return root;
    }

    public void setRoot(GermplasmPedigreeTreeNode root) {
        this.root = root;
    }
}
