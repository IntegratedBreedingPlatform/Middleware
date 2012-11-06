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
 * <b>Description</b>: Placeholder POJO for Mapping Value Element
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Mark Agarrado <br>
 * <b>File Created</b>: Jul 11, 2012
 */
public class MappingValueElement implements Serializable{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The Dataset Id. */
    private Integer datasetId;
    
    /** The Mapping Type. */
    private String mappingPopType;
    
    /** The Parent A GID. */
    private Integer parentAGid;
    
    /** The Parent B GID. */
    private Integer parentBGid;
    
    /** The Marker Type. */
    private String markerType;

    /**
     * Instantiates a Mapping Value Element object.
     * 
     * @param datasetId
     * @param mappingType
     * @param parentAGid
     * @param parentBGid
     * @param markerType
     */
    public MappingValueElement(Integer datasetId,
                                String mappingType, 
                                Integer parentAGid, 
                                Integer parentBGid, 
                                String markerType) {
        
        this.datasetId = datasetId;
        this.mappingPopType = mappingType;
        this.parentAGid = parentAGid;
        this.parentBGid = parentBGid;
        this.markerType = markerType;
    }

    
    /**
     * Gets the Dataset Id.
     * 
     * @return the datasetId
     */
    public Integer getDatasetId() {
        return datasetId;
    }

    
    /**
     * Sets the Dataset Id.
     * 
     * @param datasetId the datasetId to set
     */
    public void setDatasetId(Integer datasetId) {
        this.datasetId = datasetId;
    }

    
    /**
     * Gets the Mapping Type.
     * 
     * @return the mappingType
     */
    public String getMappingType() {
        return mappingPopType;
    }

    
    /**
     * Sets the Mapping Type.
     * 
     * @param mappingType the mappingType to set
     */
    public void setMappingType(String mappingType) {
        this.mappingPopType = mappingType;
    }

    
    /**
     * Gets the Parent A GID.
     * 
     * @return the parentAGid
     */
    public Integer getParentAGid() {
        return parentAGid;
    }

    
    /**
     * Sets the Parent A GID.
     * 
     * @param parentAGid the parentAGid to set
     */
    public void setParentAGid(Integer parentAGid) {
        this.parentAGid = parentAGid;
    }

    
    /**
     * Gets the Parent B GID.
     * 
     * @return the parentBGid
     */
    public Integer getParentBGid() {
        return parentBGid;
    }

    
    /**
     * Sets the Parent B GID.
     * 
     * @param parentBGid the parentBGid to set
     */
    public void setParentBGid(Integer parentBGid) {
        this.parentBGid = parentBGid;
    }

    
    /**
     * Gets the Marker Type.
     * 
     * @return the markerType
     */
    public String getMarkerType() {
        return markerType;
    }

    
    /**
     * Sets the Marker Type.
     * 
     * @param markerType the markerType to set
     */
    public void setMarkerType(String markerType) {
        this.markerType = markerType;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MappingValueElement [datasetId=");
        builder.append(datasetId);
        builder.append(", mappingPopType=");
        builder.append(mappingPopType);
        builder.append(", parentAGid=");
        builder.append(parentAGid);
        builder.append(", parentBGid=");
        builder.append(parentBGid);
        builder.append(", markerType=");
        builder.append(markerType);
        builder.append("]");
        return builder.toString();
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
        if (!(obj instanceof MappingValueElement)) {
            return false;
        }

        MappingValueElement rhs = (MappingValueElement) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(datasetId, rhs.datasetId)
                .append(mappingPopType, rhs.mappingPopType)
                .append(parentAGid, rhs.parentAGid)
                .append(parentBGid, rhs.parentBGid)
                .append(markerType, rhs.markerType).isEquals();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(77, 177).append(datasetId)
                .append(mappingPopType)
                .append(parentAGid)
                .append(parentBGid)
                .append(markerType).toHashCode();
    }

}
