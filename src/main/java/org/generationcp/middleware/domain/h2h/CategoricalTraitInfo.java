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

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.util.Debug;


/**
 * Contains the details of a categorical trait - name, id, description, 
 * number of locations, germplasms and observations as inherited from TraitInfo
 * plus the map of valid values for categorical variate to frequency of observing the values in datasets.
 * 
 */
public class CategoricalTraitInfo extends TraitInfo{
    
    private List<CategoricalValue> valuesCount;
        
    public CategoricalTraitInfo() {
    }
    
    public CategoricalTraitInfo(TraitInfo traitInfo) {
        super(traitInfo.getId(), traitInfo.getName(), traitInfo
                .getDescription(), traitInfo.getLocationCount(),
                traitInfo.getGermplasmCount(), traitInfo.getObservationCount());
    }

    public CategoricalTraitInfo(int traitId, String traitName, String description){
        super(traitId, traitName, description);
    }

    public CategoricalTraitInfo(int traitId, String traitName, String description,
            long locationCount, long germplasmCount, long observationCount) {
        super(traitId, traitName, description, locationCount, germplasmCount, observationCount);
    }
  
    public List<CategoricalValue> getValues() {
        return valuesCount;
    }
    
    public void setValues(List<CategoricalValue> valuesCount) {
        this.valuesCount = valuesCount;
    }
    
    public void addValue(CategoricalValue newValue){
        if (valuesCount == null){
            valuesCount = new ArrayList<CategoricalValue>();
        }
        if (valuesCount.contains(newValue)){
            int index = valuesCount.indexOf(newValue);
            valuesCount.set(index, newValue);
        } else {
            valuesCount.add(newValue);
        }
    }

    public void addValueCount(CategoricalValue value, long count){
        if (valuesCount == null){
            valuesCount = new ArrayList<CategoricalValue>();
        }
        if (valuesCount.contains(value)){
            int index = valuesCount.indexOf(value);
            CategoricalValue existingValue = valuesCount.get(index);
            existingValue.setCount(existingValue.getCount() + count);
            valuesCount.set(index, existingValue);
        } else {
            value.setCount(count);
            valuesCount.add(value);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CategoricalTraitInfo [");
        builder.append(super.toString());
        builder.append(", numberOfValues=");
        builder.append(valuesCount != null ? valuesCount.size() : 0);
        builder.append(", valuesCount=");
        builder.append(valuesCount);
        builder.append("]");
        return builder.toString();
    }

    public void print(int indent){
        super.print(indent);
        Debug.println(indent + 3, "ValuesCount: " + (valuesCount != null ? valuesCount.size() : 0));
        if (valuesCount != null){
            for (CategoricalValue value : valuesCount){
                value.print(indent + 6);
            }
        }
    }
    
}
