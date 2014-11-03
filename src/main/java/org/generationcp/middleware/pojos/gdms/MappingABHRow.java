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
 * POJO corresponding to Mapping ABH Genotyping data row used in setMappingABH. 
 *
 */
public class MappingABHRow{
    
    private AccMetadataSet accMetadataSet;
    
    private MappingPopValues mappingPopValues;

    public MappingABHRow() {
    }

    public MappingABHRow(AccMetadataSet accMetadataSet, MappingPopValues mappingPopValues) {
        this.accMetadataSet = accMetadataSet;
        this.mappingPopValues = mappingPopValues;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accMetadataSet == null) ? 0 : accMetadataSet.hashCode());
        result = prime * result + ((mappingPopValues == null) ? 0 : mappingPopValues.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MappingABHRow other = (MappingABHRow) obj;
        if (accMetadataSet == null) {
            if (other.accMetadataSet != null) {
                return false;
            }
        } else if (!accMetadataSet.equals(other.accMetadataSet)) {
            return false;
        }
        if (mappingPopValues == null) {
            if (other.mappingPopValues != null) {
                return false;
            }
        } else if (!mappingPopValues.equals(other.mappingPopValues)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MappingABHRow [accMetadataSet=");
        builder.append(accMetadataSet);
        builder.append(", mappingPopValues=");
        builder.append(mappingPopValues);
        builder.append("]");
        return builder.toString();
    }

}
