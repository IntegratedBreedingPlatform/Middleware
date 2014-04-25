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
 * POJO corresponding to Mapping Allelic SNP Genotyping data row used in setMappingAllelicSNP. 
 *
 */
public class MappingAllelicSSRDArTRow{
    
    private AccMetadataSet accMetadataSet;

    private MappingPopValues mappingPopValues;
    
    private AlleleValues alleleValues;
    
    private DartValues dartValues;
    
    public MappingAllelicSSRDArTRow() {
    }

    public MappingAllelicSSRDArTRow(AccMetadataSet accMetadataSet, 
            MappingPopValues mappingPopValues, AlleleValues alleleValues, DartValues dartValues) {
        this.accMetadataSet = accMetadataSet;
        this.mappingPopValues = mappingPopValues;
        this.alleleValues = alleleValues;
        this.dartValues = dartValues;
    }
    
    public AccMetadataSet getAccMetadataSet() {
        return accMetadataSet;
    }
    
    public void setAccMetadataSet(AccMetadataSet accMetadataSet) {
        this.accMetadataSet = accMetadataSet;
    }
    
    public MappingPopValues getMappingPopValues() {
        return mappingPopValues;
    }

    public void setMappingPopValues(MappingPopValues mappingPopValues) {
        this.mappingPopValues = mappingPopValues;
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
        result = prime * result + ((mappingPopValues == null) ? 0 : mappingPopValues.hashCode());
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
        MappingAllelicSSRDArTRow other = (MappingAllelicSSRDArTRow) obj;
        if (accMetadataSet == null) {
            if (other.accMetadataSet != null)
                return false;
        } else if (!accMetadataSet.equals(other.accMetadataSet))
            return false;
        if (mappingPopValues == null) {
            if (other.mappingPopValues != null)
                return false;
        } else if (!mappingPopValues.equals(other.mappingPopValues))
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
        builder.append("MappingAllelicSSRDArTRow [accMetadataSet=");
        builder.append(accMetadataSet);
        builder.append(", mappingPopValues=");
        builder.append(mappingPopValues);
        builder.append(", alleleValues=");
        builder.append(alleleValues);
        builder.append(", dartValues=");
        builder.append(dartValues);
        builder.append("]");
        return builder.toString();
    }

}
