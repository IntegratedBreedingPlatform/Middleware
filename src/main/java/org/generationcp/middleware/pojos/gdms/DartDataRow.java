/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.pojos.gdms;


/**
 * POJO corresponding to DArT Genotyping data row used in setDart. 
 *
 */
public class DartDataRow{
    
    private AccMetadataSet accMetadataSet;
    
    private AlleleValues alleleValues;
    
    private DartValues dartValues;
    
    public DartDataRow() {
    }

    public DartDataRow(AccMetadataSet accMetadataSet, AlleleValues alleleValues, DartValues dartValues) {
        this.accMetadataSet = accMetadataSet;
        this.alleleValues = alleleValues;
        this.dartValues = dartValues;
    }

    public AccMetadataSet getAccMetadataSet() {
        return accMetadataSet;
    }

    public void setAccMetadataSet(AccMetadataSet accMetadataSet) {
        this.accMetadataSet = accMetadataSet;
    }

    public AlleleValues getAlleleValues() {
        return alleleValues;
    }

    public void setAlleleValues(AlleleValues alleleValues) {
        this.alleleValues = alleleValues;
    }

    public DartValues getDartValues() {
        return dartValues;
    }

    public void setDartValues(DartValues dartValues) {
        this.dartValues = dartValues;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accMetadataSet == null) ? 0 : accMetadataSet.hashCode());
        result = prime * result + ((alleleValues == null) ? 0 : alleleValues.hashCode());
        result = prime * result + ((dartValues == null) ? 0 : dartValues.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DartDataRow other = (DartDataRow) obj;
        if (accMetadataSet == null) {
            if (other.accMetadataSet != null)
                return false;
        } else if (!accMetadataSet.equals(other.accMetadataSet))
            return false;
        if (alleleValues == null) {
            if (other.alleleValues != null)
                return false;
        } else if (!alleleValues.equals(other.alleleValues))
            return false;
        if (dartValues == null) {
            if (other.dartValues != null)
                return false;
        } else if (!dartValues.equals(other.dartValues))
            return false;
        return true;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DartDataRow [accMetadataSet=");
        builder.append(accMetadataSet);
        builder.append(", alleleValues=");
        builder.append(alleleValues);
        builder.append(", dartValues=");
        builder.append(dartValues);
        builder.append("]");
        return builder.toString();
    }

    
    

}
