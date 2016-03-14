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

package org.generationcp.middleware.pojos;

import java.util.LinkedList;
import java.util.List;

/**
 * This class represents a node in a GermplasmPedigreeTree. Contains a Germplasm POJO and a List of GermplasmPedigreeTreeNodes containing
 * the linked germplasms.
 * 
 * @author Kevin Manansala
 * 
 */
public class GermplasmPedigreeTreeNode {

	private Germplasm germplasm;
    private final static int FEMALE_PARENT_INDEX = 0;
    private final static int MALE_PARENT_INDEX = 1;

	/**
	 * NOTE: The order of the linked nodes must be preserved so we used LinkedList here
     * TODO move away from ordered link nodes implementation, as it would require null values to act as
     *  placeholder if one of the earlier values is missing
	 */
	private List<GermplasmPedigreeTreeNode> linkedNodes = new LinkedList<GermplasmPedigreeTreeNode>();

	public Germplasm getGermplasm() {
		return this.germplasm;
	}

	public void setGermplasm(final Germplasm germplasm) {
		this.germplasm = germplasm;
	}

	/**
	 * Returns a List of GermplasmPedigreeTreeNode objects representing the child nodes.
	 * 
	 * @return linked nodes
	 */
	public List<GermplasmPedigreeTreeNode> getLinkedNodes() {
		return this.linkedNodes;
	}

	public void setLinkedNodes(final List<GermplasmPedigreeTreeNode> parents) {
		this.linkedNodes = parents;
	}

    public GermplasmPedigreeTreeNode getFemaleParent() {
        return this.linkedNodes.get(FEMALE_PARENT_INDEX);
    }

    public void setMaleParent(GermplasmPedigreeTreeNode maleParent) {
        this.linkedNodes.set(MALE_PARENT_INDEX, maleParent);
    }

    public void setFemaleParent(GermplasmPedigreeTreeNode femaleParent) {
        this.linkedNodes.set(FEMALE_PARENT_INDEX, femaleParent);
    }

    public GermplasmPedigreeTreeNode getMaleParent() {
        return this.linkedNodes.get(MALE_PARENT_INDEX);
    }

}
