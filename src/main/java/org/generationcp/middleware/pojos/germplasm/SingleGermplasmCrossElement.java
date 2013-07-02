/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
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
