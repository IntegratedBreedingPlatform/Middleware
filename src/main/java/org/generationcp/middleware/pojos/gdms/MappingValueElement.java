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
    
    private Integer markerSampleId; 
    
    private Integer accSampleId; 

    public MappingValueElement(Integer datasetId, String mappingType, Integer parentAGid, Integer parentBGid,
            String markerType, Integer markerSampleId, Integer accSampleId) {

        this.datasetId = datasetId;
        this.mappingPopType = mappingType;
        this.parentAGid = parentAGid;
        this.parentBGid = parentBGid;
        this.markerType = markerType;
        this.markerSampleId = markerSampleId;
        this.accSampleId = accSampleId;
    }

    public MappingValueElement(Integer datasetId, String mappingType, Integer parentAGid, Integer parentBGid,
            Integer gid, Integer markerId, String markerType, Integer markerSampleId, Integer accSampleId) {

    	this(datasetId, mappingType, parentAGid, parentBGid, markerType, markerSampleId, accSampleId);
        this.gid = gid;
        this.markerId = markerId;
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
   
    
    public String getMappingPopType() {
		return mappingPopType;
	}

	public void setMappingPopType(String mappingPopType) {
		this.mappingPopType = mappingPopType;
	}

	public Integer getMarkerSampleId() {
		return markerSampleId;
	}

	public void setMarkerSampleId(Integer markerSampleId) {
		this.markerSampleId = markerSampleId;
	}

	public Integer getAccSampleId() {
		return accSampleId;
	}

	public void setAccSampleId(Integer accSampleId) {
		this.accSampleId = accSampleId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((accSampleId == null) ? 0 : accSampleId.hashCode());
		result = prime * result
				+ ((datasetId == null) ? 0 : datasetId.hashCode());
		result = prime * result + ((gid == null) ? 0 : gid.hashCode());
		result = prime * result
				+ ((mappingPopType == null) ? 0 : mappingPopType.hashCode());
		result = prime * result
				+ ((markerId == null) ? 0 : markerId.hashCode());
		result = prime * result
				+ ((markerSampleId == null) ? 0 : markerSampleId.hashCode());
		result = prime * result
				+ ((markerType == null) ? 0 : markerType.hashCode());
		result = prime * result
				+ ((parentAGid == null) ? 0 : parentAGid.hashCode());
		result = prime * result
				+ ((parentBGid == null) ? 0 : parentBGid.hashCode());
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
		MappingValueElement other = (MappingValueElement) obj;
		if (accSampleId == null) {
			if (other.accSampleId != null)
				return false;
		} else if (!accSampleId.equals(other.accSampleId))
			return false;
		if (datasetId == null) {
			if (other.datasetId != null)
				return false;
		} else if (!datasetId.equals(other.datasetId))
			return false;
		if (gid == null) {
			if (other.gid != null)
				return false;
		} else if (!gid.equals(other.gid))
			return false;
		if (mappingPopType == null) {
			if (other.mappingPopType != null)
				return false;
		} else if (!mappingPopType.equals(other.mappingPopType))
			return false;
		if (markerId == null) {
			if (other.markerId != null)
				return false;
		} else if (!markerId.equals(other.markerId))
			return false;
		if (markerSampleId == null) {
			if (other.markerSampleId != null)
				return false;
		} else if (!markerSampleId.equals(other.markerSampleId))
			return false;
		if (markerType == null) {
			if (other.markerType != null)
				return false;
		} else if (!markerType.equals(other.markerType))
			return false;
		if (parentAGid == null) {
			if (other.parentAGid != null)
				return false;
		} else if (!parentAGid.equals(other.parentAGid))
			return false;
		if (parentBGid == null) {
			if (other.parentBGid != null)
				return false;
		} else if (!parentBGid.equals(other.parentBGid))
			return false;
		return true;
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
		builder.append(", markerSampleId=");
		builder.append(markerSampleId);
		builder.append(", accSampleId=");
		builder.append(accSampleId);
		builder.append("]");
		return builder.toString();
	}

}
