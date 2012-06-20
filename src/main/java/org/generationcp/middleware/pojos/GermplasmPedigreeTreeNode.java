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

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a node in a GermplasmPedigreeTree. Contains a Germplasm
 * POJO and a List of GermplasmPedigreeTreeNodes containing the linked
 * germplasms.
 * 
 * @author Kevin Manansala
 * 
 */
public class GermplasmPedigreeTreeNode{

    private Germplasm germplasm;
    private List<GermplasmPedigreeTreeNode> linkedNodes = new ArrayList<GermplasmPedigreeTreeNode>();

    public Germplasm getGermplasm() {
        return germplasm;
    }

    public void setGermplasm(Germplasm germplasm) {
        this.germplasm = germplasm;
    }

    /**
     * Returns a List of GermplasmPedigreeTreeNode objects representing the
     * child nodes.
     * 
     * @return
     */
    public List<GermplasmPedigreeTreeNode> getLinkedNodes() {
        return linkedNodes;
    }

    public void setLinkedNodes(List<GermplasmPedigreeTreeNode> parents) {
        this.linkedNodes = parents;
    }
}
