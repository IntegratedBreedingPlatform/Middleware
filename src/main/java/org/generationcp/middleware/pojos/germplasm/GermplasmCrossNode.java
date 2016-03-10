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

package org.generationcp.middleware.pojos.germplasm;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.service.pedigree.PedigreeDataManagerFactory;
import org.generationcp.middleware.util.CrossExpansionProperties;
/**
 * Represents a node in a pedigree tree that is a result of a cross.
 */
public class GermplasmCrossNode implements GermplasmCrossElementNode {

	private static final long serialVersionUID = 7519980544099378460L;


	private GermplasmCrossElementNode firstParent;

	private GermplasmCrossElementNode secondParent;

	/**
	 * the number of crosses before this cross
	 */
	private int numberOfCrossesBefore;

	/**
	 * Germplasm this node represents
	 */
	private Germplasm germplasm;

	/**
	 * Is this a root node
	 */
	private boolean rootNode;

	public GermplasmCrossElementNode getFirstParent() {
		return this.firstParent;
	}

	public void setFirstParent(GermplasmCrossElementNode firstParent) {
		this.firstParent = firstParent;
	}

	public GermplasmCrossElementNode getSecondParent() {
		return this.secondParent;
	}

	public void setSecondParent(GermplasmCrossElementNode secondParent) {
		this.secondParent = secondParent;
	}

	public int getNumberOfCrossesBefore() {
		return this.numberOfCrossesBefore;
	}

	public void setNumberOfCrossesBefore(int numberOfCrossesBefore) {
		this.numberOfCrossesBefore = numberOfCrossesBefore;
	}

	@Override
	public boolean isRootNode() {
		return rootNode;
	}

	@Override
	public void setRootNode(boolean roodNode) {
		this.rootNode = roodNode;
	}

	@Override
	public void setGermplasm(Germplasm germplasm) {
		this.germplasm = germplasm;
	}

	@Override
	public Germplasm getGermplasm() {
		return germplasm;
	}

	@Override
	public String getCrossExpansionString(final String cropName, final CrossExpansionProperties crossExpansionProperties, final PedigreeDataManagerFactory pedigreeDataManagerFactory) {

		final StringBuilder pedigreeString = new StringBuilder();

//		if(!rootNode && germplasm != null) {
//			if(CrossBuilderUtil.nameTypeBasedResolution(pedigreeString, pedigreeDataManagerFactory, this.germplasm, crossExpansionProperties.getNameTypeOrder(cropName))){
//				return pedigreeString.toString();
//			}
//		}


		if (this.firstParent != null) {
			if(!CrossBuilderUtil.nameTypeBasedResolution(pedigreeString, pedigreeDataManagerFactory, this.germplasm.getGpid1(), crossExpansionProperties.getNameTypeOrder(cropName))){
				pedigreeString.append(this.firstParent.getCrossExpansionString(cropName, crossExpansionProperties, pedigreeDataManagerFactory));
			}
		} else {
			pedigreeString.append("Unknown");
		}

		// number of slashes between first and second parent depends on the number
		// of crosses made
		if (this.numberOfCrossesBefore == 0) {
			pedigreeString.append("/");
		} else if (this.numberOfCrossesBefore == 1) {
			pedigreeString.append("//");
		} else if (this.numberOfCrossesBefore == 2) {
			pedigreeString.append("///");
		} else {
			pedigreeString.append("/");
			pedigreeString.append(this.numberOfCrossesBefore + 1);
			pedigreeString.append("/");
		}

		if (this.secondParent != null) {
			if(!CrossBuilderUtil.nameTypeBasedResolution(pedigreeString, pedigreeDataManagerFactory, this.getGermplasm().getGpid2(), crossExpansionProperties.getNameTypeOrder(cropName))){
				pedigreeString.append(this.secondParent.getCrossExpansionString(cropName, crossExpansionProperties, pedigreeDataManagerFactory));
			}
		} else {
			pedigreeString.append("Unknown");
		}

		return pedigreeString.toString();
	}

}
