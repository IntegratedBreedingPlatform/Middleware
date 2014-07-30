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
 * POJO corresponding to SSR Genotyping data row used in setSSR. 
 *
 */
public class SSRDataRow{
    
    private AccMetadataSet accMetadataSet;

    private AlleleValues alleleValues;

    
    public SSRDataRow() {
    }

    public SSRDataRow(AccMetadataSet accMetadataSet, AlleleValues alleleValues) {
        this.accMetadataSet = accMetadataSet;
        this.alleleValues = alleleValues;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accMetadataSet == null) ? 0 : accMetadataSet.hashCode());
        result = prime * result + ((alleleValues == null) ? 0 : alleleValues.hashCode());
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
        SSRDataRow other = (SSRDataRow) obj;
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
        return true;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SSRDataRow [accMetadataSet=");
        builder.append(accMetadataSet);
        builder.append(", alleleValues=");
        builder.append(alleleValues);
        builder.append("]");
        return builder.toString();
    }

    
    

}
