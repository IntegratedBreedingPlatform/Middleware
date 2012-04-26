package org.generationcp.middleware.pojos;

/**
 * This class represents a pedigree tree.  From the root node down to the
 * leaf nodes, one can traverse the entire tree.
 * 
 * @author Kevin Manansala
 *
 */
public class GermplasmPedigreeTree
{
	private GermplasmPedigreeTreeNode root;

	public GermplasmPedigreeTreeNode getRoot()
	{
		return root;
	}

	public void setRoot(GermplasmPedigreeTreeNode root)
	{
		this.root = root;
	}
}
