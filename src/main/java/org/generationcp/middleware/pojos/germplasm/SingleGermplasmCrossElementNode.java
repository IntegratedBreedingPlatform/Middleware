package org.generationcp.middleware.pojos.germplasm;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.service.pedigree.PedigreeDataManagerFactory;
import org.generationcp.middleware.util.CrossExpansionProperties;

/**
 * Represents a node in a pedigree tree that is a result of a single cross.
 */
public class SingleGermplasmCrossElementNode implements GermplasmCrossElementNode {

	private static final long serialVersionUID = 1575136137040870760L;

	/**
	 * Germplasm this node represents
	 */
	private Germplasm germplasm;

	/**
	 * Is this a root node
	 */
	private boolean rootNode;

	public Germplasm getGermplasm() {
		return this.germplasm;
	}

	@Override
	public void setGermplasm(Germplasm germplasm) {
		this.germplasm = germplasm;
	}

	@Override
	public String getCrossExpansionString(String cropName, CrossExpansionProperties crossExpansionProperties, final PedigreeDataManagerFactory pedigreeDataManagerFactory) {


		final StringBuilder pedigreeString = new StringBuilder();
		if(!rootNode && germplasm != null) {
			if(CrossBuilderUtil.nameTypeBasedResolution(pedigreeString, pedigreeDataManagerFactory, this.germplasm.getGid(), crossExpansionProperties.getNameTypeOrder(cropName))){
				return pedigreeString.toString();
			}
		}

		if (this.germplasm != null) {
			Name nameObject = this.germplasm.getPreferredName();
			if (nameObject == null) {
				return this.germplasm.getGid().toString();
			} else {
				return nameObject.getNval();
			}
		} else {
			return "Unknown";
		}
	}

	@Override
	public boolean isRootNode() {
		return rootNode;
	}

	@Override
	public void setRootNode(boolean rootNode) {
		this.rootNode = rootNode;
	}

}
