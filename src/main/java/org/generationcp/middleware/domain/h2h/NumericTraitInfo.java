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

import org.generationcp.middleware.util.Debug;


/**
 * Contains the details of a numeric trait - name, id, description, 
 * number of locations, germplasms and observations as inherited from TraitInfo
 * plus the additional fields minValue, maxValue, medianValue.
 * 
 */
public class NumericTraitInfo extends TraitInfo{
    
    private double minValue;
    private double maxValue;
    private double medianValue;
    
    public NumericTraitInfo() {
    }
    
    public NumericTraitInfo(TraitInfo traitInfo) {
        super(traitInfo.getId(), traitInfo.getName(), traitInfo
                .getDescription(), traitInfo.getLocationCount(),
                traitInfo.getGermplasmCount(), traitInfo.getObservationCount());
    }

    public NumericTraitInfo(String traitName, int traitId, String description,
            long locationCount, long germplasmCount, long observationCount, double minValue, double maxValue, double medianValue) {
        super(traitId, traitName, description, locationCount, germplasmCount, observationCount);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.medianValue = medianValue;
    }

    public double getMinValue() {
        return minValue;
    }
    
    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }
    
    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public double getMedianValue() {
        return medianValue;
    }

    public void setMedianValue(double medianValue) {
        this.medianValue = medianValue;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NumericTraitInfo [");
        builder.append(super.toString());
        builder.append(", minValue=");
        builder.append(minValue);
        builder.append(", maxValue=");
        builder.append(maxValue);
        builder.append(", medianValue=");
        builder.append(medianValue);
        builder.append("]");
        return builder.toString();
    }

    public void print(int indent){
        super.print(indent);
        Debug.println(indent + 3, "Minimum Value: " + getMinValue());
        Debug.println(indent + 3, "Maximum Value: " + getMaxValue());
        Debug.println(indent + 3, "Median Value: " + getMedianValue());
    }

}
