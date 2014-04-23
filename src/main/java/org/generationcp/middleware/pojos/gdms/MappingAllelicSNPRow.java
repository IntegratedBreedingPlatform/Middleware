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
public class MappingAllelicSNPRow{
    
    private Marker marker;
    
    private AccMetadataSet accMetadataSet;
    
    private MarkerMetadataSet markerMetadataSet;
    
    private MappingPopValues mappingPopValues;
    
    private CharValues charValues;

    
    public MappingAllelicSNPRow() {
    }


    public MappingAllelicSNPRow(AccMetadataSet accMetadataSet, 
            MappingPopValues mappingPopValues, CharValues charValues) {
        this.accMetadataSet = accMetadataSet;
        this.mappingPopValues = mappingPopValues;
        this.charValues = charValues;
    }


    public MappingAllelicSNPRow(Marker marker, AccMetadataSet accMetadataSet, MarkerMetadataSet markerMetadataSet,
            MappingPopValues mappingPopValues, CharValues charValues) {
        super();
        this.marker = marker;
        this.accMetadataSet = accMetadataSet;
        this.markerMetadataSet = markerMetadataSet;
        this.mappingPopValues = mappingPopValues;
        this.charValues = charValues;
    }

    
    public Marker getMarker() {
        return marker;
    }


    
    public void setMarker(Marker marker) {
        this.marker = marker;
    }


    
    public AccMetadataSet getAccMetadataSet() {
        return accMetadataSet;
    }


    
    public void setAccMetadataSet(AccMetadataSet accMetadataSet) {
        this.accMetadataSet = accMetadataSet;
    }


    
    public MarkerMetadataSet getMarkerMetadataSet() {
        return markerMetadataSet;
    }


    
    public void setMarkerMetadataSet(MarkerMetadataSet markerMetadataSet) {
        this.markerMetadataSet = markerMetadataSet;
    }


    
    public MappingPopValues getMappingPopValues() {
        return mappingPopValues;
    }


    
    public void setMappingPopValues(MappingPopValues mappingPopValues) {
        this.mappingPopValues = mappingPopValues;
    }

    
    public CharValues getCharValues() {
        return charValues;
    }


    
    public void setCharValues(CharValues charValues) {
        this.charValues = charValues;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accMetadataSet == null) ? 0 : accMetadataSet.hashCode());
        result = prime * result + ((mappingPopValues == null) ? 0 : mappingPopValues.hashCode());
        result = prime * result + ((charValues == null) ? 0 : charValues.hashCode());
        result = prime * result + ((marker == null) ? 0 : marker.hashCode());
        result = prime * result + ((markerMetadataSet == null) ? 0 : markerMetadataSet.hashCode());
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
        MappingAllelicSNPRow other = (MappingAllelicSNPRow) obj;
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
        if (charValues == null) {
            if (other.charValues != null)
                return false;
        } else if (!charValues.equals(other.charValues))
            return false;
        if (marker == null) {
            if (other.marker != null)
                return false;
        } else if (!marker.equals(other.marker))
            return false;
        if (markerMetadataSet == null) {
            if (other.markerMetadataSet != null)
                return false;
        } else if (!markerMetadataSet.equals(other.markerMetadataSet))
            return false;
        return true;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MappingAllelicSNPRow [marker=");
        builder.append(marker);
        builder.append(", accMetadataSet=");
        builder.append(accMetadataSet);
        builder.append(", markerMetadataSet=");
        builder.append(markerMetadataSet);
        builder.append(", mappingPopValues=");
        builder.append(mappingPopValues);
        builder.append(", charValues=");
        builder.append(charValues);
        builder.append("]");
        return builder.toString();
    }

    
    

}
