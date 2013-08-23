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
 * Contains the details of a character trait - name, id, description, 
 * number of locations, germplasms and observations as inherited from TraitInfo
 * plus the list of distinct observed values for the trait.
 * 
 */
public class CharacterTraitInfo extends TraitInfo{
    
    private List<String> values;

    public CharacterTraitInfo() {
    }
    
    public CharacterTraitInfo(TraitInfo traitInfo) {
        super(traitInfo.getId(), traitInfo.getName(), traitInfo
                .getDescription(), traitInfo.getLocationCount(),
                traitInfo.getGermplasmCount(), traitInfo.getObservationCount());
    }

    public CharacterTraitInfo(String traitName, int traitId, String description,
            long locationCount, long germplasmCount, long observationCount, List<String> values) {
        super(traitId, traitName, description, locationCount, germplasmCount, observationCount);
        this.values = values;
    }

    public List<String> getValues() {
        return values;
    }
    
    public void setValues(List<String> values) {
        this.values = values;
    }
    
    public void addValue(String newValue) {
        if (values == null){
            values = new ArrayList<String>();
        }
        values.add(newValue);
    }
    
    public void addValues(List<String> newValues) {
        if (values == null){
            values = new ArrayList<String>();
        }
        values.addAll(newValues);
    }
    
    public boolean isValueExists(String value){
        return (values != null ? values.contains(value) : false);
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CharacterTraitInfo [");
        builder.append(super.toString());
        builder.append(", valuesCount=");
        builder.append(values.size());
        builder.append(", values=");
        builder.append(values);
        builder.append("]");
        return builder.toString();
    }

    public void print(int indent){
        super.print(indent);
        Debug.println(indent + 3, "Values: " + getValues());
    }


}
