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

package org.generationcp.middleware.domain.h2h;

import java.util.HashMap;
import java.util.Map;


/**
 * Contains the details of a categorical trait - name, id, description, 
 * number of locations, germplasms and observations as inherited from TraitInfo
 * plus the map of valid values for categorical variate to frequency of observing the values in datasets.
 * 
 */
public class CategoricalTraitInfo extends TraitInfo{
    
    private Map<String, Integer> valuesCount;
    
    public CategoricalTraitInfo() {
    }
    
    public CategoricalTraitInfo(TraitInfo traitInfo) {
        super(traitInfo.getTraitName(), traitInfo.getTraitId(), traitInfo
                .getDescription(), traitInfo.getLocationCount(),
                traitInfo.getGermplasmCount(), traitInfo.getObservationCount());
    }

    public CategoricalTraitInfo(String traitName, int traitId, String description,
            long locationCount, long germplasmCount, long observationCount, Map<String, Integer> valuesCount) {
        super(traitName, traitId, description, locationCount, germplasmCount, observationCount);
        this.valuesCount = valuesCount;
    }

    public Map<String, Integer> getValuesCount() {
        return valuesCount;
    }
    
    public void setValuesCount(Map<String, Integer>  valuesCount) {
        this.valuesCount = valuesCount;
    }
    
    public void addValuesCount(Map<String, Integer>  newValuesCount) {
        if (valuesCount == null){
            valuesCount = new HashMap<String, Integer>();
        }
        valuesCount.putAll(newValuesCount);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CategoricalTraitInfo [");
        builder.append(super.toString());
        builder.append(", valuesCount=");
        builder.append("]");
        return builder.toString();
    }


}
