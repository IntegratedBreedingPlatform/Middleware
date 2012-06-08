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
public class GermplasmPedigreeTreeNode {
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
