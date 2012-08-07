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
package org.generationcp.middleware.pojos.gdms;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * <b>Description</b>: Placeholder POJO for Allelic Value that stores the marker id
 * Used by GenotypicDataManager.getAllelicValuesFromCharValuesByDatasetId().
 * Used by GenotypicDataManager.getAllelicValuesFromAlleleValuesByDatasetId().
 * Used by GenotypicDataManager.getAllelicValuesFromMappingPopValuesByDatasetId().
 * 
 * <br>
 * 
 * <b>Author</b>: Joyce Avestro <br>
 * <b>File Created</b>: Aug 07, 2012
 */
public class AllelicValueWithMarkerIdElement implements Serializable{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The Germplasm Id. */
    private Integer gid;
    
    /** The Data value (char_value for table char_values, 
     * allele_bin_value for allele_values table and map_char_value for mapping_pop_values) */
    private String data;
    
    /** The Marker Id. */
    private Integer markerId;

    /**
     * Instantiates a AllelicValueWithMarkerIdElement object.
     * 
     * @param gid
     * @param data
     * @param markerId
     */
    public AllelicValueWithMarkerIdElement(Integer gid, String data, Integer markerId) {
        this.gid = gid;
        this.data = data;
        this.markerId = markerId;
    }

    
    /**
     * Gets the Germplasm Id.
     * 
     * @return the gid
     */
    public Integer getGid() {
        return gid;
    }

    
    /**
     * Sets the Germplasm Id.
     * 
     * @param gid the gid to set
     */
    public void setGid(Integer gid) {
        this.gid = gid;
    }

    
    /**
     * Gets the Data value.
     * 
     * @return the data
     */
    public String getData() {
        return data;
    }

    
    /**
     * Sets the Data value.
     * 
     * @param data the data to set
     */
    public void setData(String data) {
        this.data = data;
    }

    
    /**
     * Gets the Marker Name.
     * 
     * @return the markerId
     */
    public Integer getMarkerId() {
        return markerId;
    }

    
    /**
     * Sets the Marker Name.
     * 
     * @param markerId the markerId to set
     */
    public void setmarkerId(Integer markerId) {
        this.markerId = markerId;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "AllelicValueWithMarkerIdElement [gid=" + gid + 
                ", data=" + data +
                ", markerId=" + markerId + "]";
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AllelicValueWithMarkerIdElement)) {
            return false;
        }

        AllelicValueWithMarkerIdElement rhs = (AllelicValueWithMarkerIdElement) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(gid, rhs.gid)
                .append(data, rhs.data)
                .append(markerId, rhs.markerId).isEquals();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 97).append(gid)
                .append(data)
                .append(markerId).toHashCode();
    }
}
