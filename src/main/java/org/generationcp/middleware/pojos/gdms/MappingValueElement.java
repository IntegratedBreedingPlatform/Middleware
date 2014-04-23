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
 * Placeholder POJO for Mapping Value Element
 * 
 * @author Mark Agarrado
 */
public class MappingValueElement implements Serializable{

    private static final long serialVersionUID = 1L;
    
    private Integer datasetId;
    
    private String mappingPopType;
    
    private Integer parentAGid;
    
    private Integer parentBGid;
    
    private Integer gid;

    private Integer markerId;
    
    private String markerType;

    public MappingValueElement(Integer datasetId, String mappingType, Integer parentAGid, Integer parentBGid,
            String markerType) {

        this.datasetId = datasetId;
        this.mappingPopType = mappingType;
        this.parentAGid = parentAGid;
        this.parentBGid = parentBGid;
        this.markerType = markerType;
    }

    public MappingValueElement(Integer datasetId, String mappingType, Integer parentAGid, Integer parentBGid,
            Integer gid, Integer markerId, String markerType) {

        this.datasetId = datasetId;
        this.mappingPopType = mappingType;
        this.parentAGid = parentAGid;
        this.parentBGid = parentBGid;
        this.gid = gid;
        this.markerId = markerId;
        this.markerType = markerType;
    }

    public Integer getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(Integer datasetId) {
        this.datasetId = datasetId;
    }

    public String getMappingType() {
        return mappingPopType;
    }

    public void setMappingType(String mappingType) {
        this.mappingPopType = mappingType;
    }

    public Integer getParentAGid() {
        return parentAGid;
    }

    public void setParentAGid(Integer parentAGid) {
        this.parentAGid = parentAGid;
    }

    public Integer getParentBGid() {
        return parentBGid;
    }

    public void setParentBGid(Integer parentBGid) {
        this.parentBGid = parentBGid;
    }

    public Integer getGid() {
        return gid;
    }

    public void setGid(Integer gid) {
        this.gid = gid;
    }

    public Integer getMarkerId() {
        return markerId;
    }

    public void setMarkerId(Integer markerId) {
        this.markerId = markerId;
    }

    public String getMarkerType() {
        return markerType;
    }

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
        builder.append(", gid=");
        builder.append(gid);
        builder.append(", markerId=");
        builder.append(markerId);
        builder.append(", markerType=");
        builder.append(markerType);
        builder.append("]");
        return builder.toString();
    }
    
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
                .append(gid, rhs.gid)
                .append(markerId, rhs.markerId)
                .append(markerType, rhs.markerType).isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(77, 177).append(datasetId)
                .append(mappingPopType)
                .append(parentAGid)
                .append(parentBGid)
                .append(gid)
                .append(markerId)
                .append(markerType).toHashCode();
    }

}
