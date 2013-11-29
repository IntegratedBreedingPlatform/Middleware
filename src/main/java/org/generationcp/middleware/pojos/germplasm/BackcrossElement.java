package org.generationcp.middleware.pojos.germplasm;

public class BackcrossElement implements GermplasmCrossElement {
	private static final long serialVersionUID = 6253095292794735301L;

	GermplasmCrossElement parent;
	GermplasmCrossElement recurringParent;
	private int numberOfDosesOfRecurringParent;
	private boolean recurringParentOnTheRight = false;
	
	public GermplasmCrossElement getParent() {
		return parent;
	}
	
	public void setParent(GermplasmCrossElement parent) {
		this.parent = parent;
	}
	
	public GermplasmCrossElement getRecurringParent() {
		return recurringParent;
	}
	
	public void setRecurringParent(GermplasmCrossElement recurringParent) {
		this.recurringParent = recurringParent;
	}
	
	public int getNumberOfDosesOfRecurringParent() {
		return numberOfDosesOfRecurringParent;
	}
	
	public void setNumberOfDosesOfRecurringParent(int numberOfDosesOfRecurringParent) {
		this.numberOfDosesOfRecurringParent = numberOfDosesOfRecurringParent;
	}
	
	public void setRecurringParentOnTheRight(boolean temp){
		this.recurringParentOnTheRight = temp;
	}
	
	@Override
	public String toString() {
		StringBuilder toreturn = new StringBuilder();
		
		String parentString = "Unknown"; 
		if(this.parent != null){
			parentString = parent.toString();
		}
		
		String recurrentParentString = "Unknown";
		if(this.recurringParent != null){
			recurrentParentString = this.recurringParent.toString();
		}
			
		if(this.recurringParentOnTheRight){
			toreturn.append(parentString);
			toreturn.append("/");
			toreturn.append(numberOfDosesOfRecurringParent);
			toreturn.append("*");
			toreturn.append(recurrentParentString);
		} else{
			toreturn.append(recurrentParentString);
			toreturn.append("*");
			toreturn.append(numberOfDosesOfRecurringParent);
			toreturn.append("/");
			toreturn.append(parentString);
		}
		
		return toreturn.toString();
	}
}
