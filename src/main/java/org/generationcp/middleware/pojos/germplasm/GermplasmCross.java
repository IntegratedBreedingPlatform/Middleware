package org.generationcp.middleware.pojos.germplasm;


public class GermplasmCross implements GermplasmCrossElement{

    private static final long serialVersionUID = 7519980544099378460L;
    
    private GermplasmCrossElement firstParent;
    private GermplasmCrossElement secondParent;
    private int numberOfCrossesBefore;                //the number of crosses before this cross
    
    public GermplasmCrossElement getFirstParent() {
        return firstParent;
    }
    
    public void setFirstParent(GermplasmCrossElement firstParent) {
        this.firstParent = firstParent;
    }
    
    public GermplasmCrossElement getSecondParent() {
        return secondParent;
    }
    
    public void setSecondParent(GermplasmCrossElement secondParent) {
        this.secondParent = secondParent;
    }
    
    public int getNumberOfCrossesBefore() {
        return numberOfCrossesBefore;
    }
    
    public void setNumberOfCrossesBefore(int numberOfCrossesBefore) {
        this.numberOfCrossesBefore = numberOfCrossesBefore;
    }

    @Override
    public String toString() {
        StringBuilder toreturn = new StringBuilder();
        
        toreturn.append(this.firstParent.toString());
        
        //number of slashes between first and second parent depends on the number
        //of crosses made
        if(this.numberOfCrossesBefore == 0){
            toreturn.append("/");
        } else if(this.numberOfCrossesBefore == 1){
            toreturn.append("//");
        } else if(this.numberOfCrossesBefore == 2){
            toreturn.append("///");
        } else {
            toreturn.append("/");
            toreturn.append(this.numberOfCrossesBefore + 1);
            toreturn.append("/");
        }
        
        toreturn.append(this.secondParent.toString());
        
        return toreturn.toString();
    }
}
