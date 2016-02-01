
package org.generationcp.middleware.pojos.germplasm;

import java.util.List;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.service.pedigree.PedigreeDataManagerFactory;
import org.generationcp.middleware.util.CrossExpansionProperties;

public class NewBackcrossElement implements BackCrossOrNormalCross {

	private static final long serialVersionUID = 6253095292794735301L;

	NewGermplasmCrossElement parent;
	NewGermplasmCrossElement recurringParent;
	private int numberOfDosesOfRecurringParent;

	private boolean recurringParentOnTheRight = false;

	private Germplasm germplasm;

	private boolean rootNode;


	public NewGermplasmCrossElement getParent() {
		return this.parent;
	}

	public void setParent(NewGermplasmCrossElement parent) {
		this.parent = parent;
	}

	public NewGermplasmCrossElement getRecurringParent() {
		return this.recurringParent;
	}

	public void setRecurringParent(NewGermplasmCrossElement recurringParent) {
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

	@Override
	public void setGermplasm(Germplasm germplasm) {
		this.germplasm = germplasm;

	}

	@Override
	public Germplasm getGermplasm() {
		return germplasm;
	}

	public String getCrossExpansionString(final String cropName, final CrossExpansionProperties crossExpansionProperties, final PedigreeDataManagerFactory pedigreeDataManagerFactory) {

		final StringBuilder toreturn = new StringBuilder();

		final List<Integer> nameTypeOrder = crossExpansionProperties.getNameTypeOrder(cropName);
		final List<Name> namesByGID = pedigreeDataManagerFactory.getGermplasmDataManager().getByGIDWithListTypeFilters(germplasm.getGid(), null, nameTypeOrder);
		if(!rootNode) {
			if(CrossBuilderUtil.nameTypeBasedResolution(toreturn, nameTypeOrder, namesByGID)){
				return toreturn.toString();
			}
		}


		String parentString = "Unknown";
		if (this.parent != null) {
			parentString = this.parent.getCrossExpansionString(cropName, crossExpansionProperties, pedigreeDataManagerFactory);
		}

		String recurrentParentString = "Unknown";
		if (this.recurringParent != null) {
			recurrentParentString = this.recurringParent.getCrossExpansionString(cropName, crossExpansionProperties, pedigreeDataManagerFactory);
		}

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



	@Override
	public boolean isRootNode() {
		return rootNode;
	}

	@Override
	public void setRootNode(boolean roodNode) {
		this.rootNode = roodNode;
	}

}
