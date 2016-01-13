
package org.generationcp.middleware.pojos.germplasm;

import java.util.List;

import org.generationcp.middleware.pojos.Name;

public class BackcrossElement implements GermplasmCrossElement {

	private static final long serialVersionUID = 6253095292794735301L;

	GermplasmCrossElement parent;
	GermplasmCrossElement recurringParent;
	private int numberOfDosesOfRecurringParent;
	private boolean recurringParentOnTheRight = false;

	private Integer level;

	private List<Name> name;

	private Integer rootLevel;

	public GermplasmCrossElement getParent() {
		return this.parent;
	}

	public void setParent(GermplasmCrossElement parent) {
		this.parent = parent;
	}

	public GermplasmCrossElement getRecurringParent() {
		return this.recurringParent;
	}

	public void setRecurringParent(GermplasmCrossElement recurringParent) {
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
	public Integer getRootLevel() {
		return this.rootLevel;
	}

	@Override
	public void setRootLevel(final Integer rootLevel) {
		this.rootLevel = rootLevel;
	}

	@Override
	public void setLevel(Integer level) {
		this.level = level;
	}

	@Override
	public void setNames(List<Name> name) {
		this.name = name;
	}

	@Override
	public String toString() {
		StringBuilder toreturn = new StringBuilder();

		String parentString = "Unknown";
		if (this.parent != null) {
			parentString = this.parent.toString();
		}

		String recurrentParentString = "Unknown";
		if (this.recurringParent != null) {
			recurrentParentString = this.recurringParent.toString();
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

}
