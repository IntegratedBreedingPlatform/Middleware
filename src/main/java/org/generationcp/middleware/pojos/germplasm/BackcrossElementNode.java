
package org.generationcp.middleware.pojos.germplasm;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.service.pedigree.PedigreeDataManagerFactory;
import org.generationcp.middleware.util.CrossExpansionProperties;

/**
 * Represents a node in a pedigree tree that is a result of a backcorss.
 */
public class BackcrossElementNode implements GermplasmCrossElementNode {

	private static final long serialVersionUID = 6253095292794735301L;

	/**
	 * The parent for the backcross
	 */
	GermplasmCrossElementNode parent;

	/**
	 * The recurring parent for the backcross.
	 */
	GermplasmCrossElementNode recurringParent;

	/**
	 * The number of recurring parents
	 */
	private int numberOfDosesOfRecurringParent;

	/**
	 * Is the recurring parent on the right
	 */
	private boolean recurringParentOnTheRight = false;

	/**
	 * Germplasm this node represents
	 */
	private Germplasm germplasm;

	/**
	 * Is this a root node
	 */
	private boolean rootNode;


	public GermplasmCrossElementNode getParent() {
		return this.parent;
	}

	public void setParent(GermplasmCrossElementNode parent) {
		this.parent = parent;
	}

	public GermplasmCrossElementNode getRecurringParent() {
		return this.recurringParent;
	}

	public void setRecurringParent(GermplasmCrossElementNode recurringParent) {
		this.recurringParent = recurringParent;
	}

	public int getNumberOfDosesOfRecurringParent() {
		return this.numberOfDosesOfRecurringParent;
	}

	public boolean isRecurringParentOnTheRight() {
		return this.recurringParentOnTheRight;
	}

	public void setNumberOfDosesOfRecurringParent(int numberOfDosesOfRecurringParent) {
		this.numberOfDosesOfRecurringParent = numberOfDosesOfRecurringParent;
	}

	public void setRecurringParentOnTheRight(boolean temp) {
		this.recurringParentOnTheRight = temp;
	}

	/**
	 * (non-Javadoc)
	 * @see org.generationcp.middleware.pojos.germplasm.GermplasmCrossElementNode#setGermplasm(org.generationcp.middleware.pojos.Germplasm)
	 */
	@Override
	public void setGermplasm(Germplasm germplasm) {
		this.germplasm = germplasm;

	}

	/**
	 * (non-Javadoc)
	 * @see org.generationcp.middleware.pojos.germplasm.GermplasmCrossElementNode#getGermplasm()
	 */
	@Override
	public Germplasm getGermplasm() {
		return germplasm;
	}

	/**
	 * (non-Javadoc)
	 * @see org.generationcp.middleware.pojos.germplasm.GermplasmCrossElementNode#isRootNode()
	 */
	@Override
	public boolean isRootNode() {
		return rootNode;
	}

	/**
	 *  (non-Javadoc)
	 * @see org.generationcp.middleware.pojos.germplasm.GermplasmCrossElementNode#setRootNode(boolean)
	 */
	@Override
	public void setRootNode(boolean roodNode) {
		this.rootNode = roodNode;
	}

	public String getCrossExpansionString(final String cropName, final CrossExpansionProperties crossExpansionProperties, final PedigreeDataManagerFactory pedigreeDataManagerFactory) {


//		if(!rootNode && germplasm != null) {
//			if(CrossBuilderUtil.nameTypeBasedResolution(toreturn, pedigreeDataManagerFactory, this.germplasm, crossExpansionProperties.getNameTypeOrder(cropName))){
//				return toreturn.toString();
//			}
//		}

		final StringBuilder parentString = new StringBuilder();
		if (this.parent != null) {
			if(!CrossBuilderUtil.nameTypeBasedResolution(parentString, pedigreeDataManagerFactory, this.getGermplasm().getGpid1(), crossExpansionProperties.getNameTypeOrder(cropName))){
				parentString.append(this.parent.getCrossExpansionString(cropName, crossExpansionProperties, pedigreeDataManagerFactory));
			}
		} else {
			parentString.append("Unknown");
		}


		final StringBuilder recurrentParentString = new StringBuilder();
		if (this.recurringParent != null) {
			if(!CrossBuilderUtil.nameTypeBasedResolution(recurrentParentString, pedigreeDataManagerFactory, this.getGermplasm().getGpid1(), crossExpansionProperties.getNameTypeOrder(cropName))){
				recurrentParentString.append(this.recurringParent.getCrossExpansionString(cropName, crossExpansionProperties, pedigreeDataManagerFactory));
			}
		} else {
			parentString.append("Unknown");
		}

		final StringBuilder toreturn = new StringBuilder();
		if (this.recurringParentOnTheRight) {
			toreturn.append(parentString);
			toreturn.append("/");
			toreturn.append(this.numberOfDosesOfRecurringParent);
			toreturn.append("*");
			toreturn.append(recurrentParentString);
		} else {
			toreturn.append(recurrentParentString);
			toreturn.append("*");
			toreturn.append(this.numberOfDosesOfRecurringParent);
			toreturn.append("/");
			toreturn.append(parentString);
		}

		return toreturn.toString();
	}





}
