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
 * <b>Description</b>: Placeholder POJO for Allelic Value Element
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Mark Agarrado <br>
 * <b>File Created</b>: Jul 13, 2012
 */
public class AllelicValueElement implements Serializable{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The Germplasm Id. */
    private Integer gid;
    
    /** The Data value. */
    private String data;
    
    /** The Marker Name. */
    private String markerName;

    /**
     * Instantiates a Mapping Value Element object.
     * 
     * @param gid
     * @param data
     * @param markerName
     */
    public AllelicValueElement(Integer gid, String data, String markerName) {
        this.gid = gid;
        this.data = data;
        this.markerName = markerName;
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
     * @return the markerName
     */
    public String getMarkerName() {
        return markerName;
    }

    
    /**
     * Sets the Marker Name.
     * 
     * @param markerName the markerName to set
     */
    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "AllelicValueElement [gid=" + gid + 
                ", data=" + data +
                ", markerName=" + markerName + "]";
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
        if (!(obj instanceof AllelicValueElement)) {
            return false;
        }

        AllelicValueElement rhs = (AllelicValueElement) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(gid, rhs.gid)
                .append(data, rhs.data)
                .append(markerName, rhs.markerName).isEquals();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 77).append(gid)
                .append(data)
                .append(markerName).toHashCode();
    }
}
