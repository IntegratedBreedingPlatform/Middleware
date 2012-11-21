package org.generationcp.middleware.pojos.germplasm;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;


public class SingleGermplasmCrossElement implements GermplasmCrossElement{

    private static final long serialVersionUID = 1575136137040870760L;
    
    private Germplasm germplasm;
    
    public Germplasm getGermplasm() {
        return germplasm;
    }
    
    public void setGermplasm(Germplasm germplasm) {
        this.germplasm = germplasm;
    }
    
    @Override
    public String toString() {
        Name nameObject = this.germplasm.getPreferredName();
        if(nameObject == null){
            return this.germplasm.getGid().toString();
        } else {
            return nameObject.getNval();
        }
    }
}
