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
 * Placeholder POJO for Allelic Value that stores the marker id
 * Used by GenotypicDataManager.getAllelicValuesFromCharValuesByDatasetId().
 * Used by GenotypicDataManager.getAllelicValuesFromAlleleValuesByDatasetId().
 * Used by GenotypicDataManager.getAllelicValuesFromMappingPopValuesByDatasetId().
 * 
 * @author Joyce Avestro 
 */
public class AllelicValueWithMarkerIdElement implements Serializable{

    private static final long serialVersionUID = 1L;

    private Integer gid;
    
    /** The Data value (char_value for table char_values, 
     * allele_bin_value for allele_values table and map_char_value for mapping_pop_values) */
    private String data;
    
    private Integer markerId;
    
    private Integer peakHeight;
    
    private Integer markerSampleId;
    
    private Integer accSampleId;

    public AllelicValueWithMarkerIdElement(Integer gid, String data, Integer markerId
            , Integer markerSampleId, Integer accSampleId) {
        this(gid, data, markerId, null, markerSampleId, accSampleId);
    }

    public AllelicValueWithMarkerIdElement(Integer gid, String data, Integer markerId, Integer peakHeight
            , Integer markerSampleId, Integer accSampleId) {
        this.gid = gid;
        this.data = data;
        this.markerId = markerId;
        this.peakHeight = peakHeight;
        this.markerSampleId = markerSampleId;
        this.accSampleId = accSampleId;
    }

    public Integer getGid() {
        return gid;
    }

    public void setGid(Integer gid) {
        this.gid = gid;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getMarkerId() {
        return markerId;
    }

    public void setmarkerId(Integer markerId) {
        this.markerId = markerId;
    }

	public void setMarkerId(Integer markerId) {
		this.markerId = markerId;
	}
    
    public Integer getPeakHeight() {
		return peakHeight;
	}

	public void setPeakHeight(Integer peakHeight) {
		this.peakHeight = peakHeight;
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
        result = prime * result + ((accSampleId == null) ? 0 : accSampleId.hashCode());
        result = prime * result + ((data == null) ? 0 : data.hashCode());
        result = prime * result + ((gid == null) ? 0 : gid.hashCode());
        result = prime * result + ((markerId == null) ? 0 : markerId.hashCode());
        result = prime * result + ((markerSampleId == null) ? 0 : markerSampleId.hashCode());
        result = prime * result + ((peakHeight == null) ? 0 : peakHeight.hashCode());
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
        AllelicValueWithMarkerIdElement other = (AllelicValueWithMarkerIdElement) obj;
        if (accSampleId == null) {
            if (other.accSampleId != null) {
                return false;
            }
        } else if (!accSampleId.equals(other.accSampleId)) {
            return false;
        }
        if (data == null) {
            if (other.data != null) {
                return false;
            }
        } else if (!data.equals(other.data)) {
            return false;
        }
        if (gid == null) {
            if (other.gid != null) {
                return false;
            }
        } else if (!gid.equals(other.gid)) {
            return false;
        }
        if (markerId == null) {
            if (other.markerId != null) {
                return false;
            }
        } else if (!markerId.equals(other.markerId)) {
            return false;
        }
        if (markerSampleId == null) {
            if (other.markerSampleId != null) {
                return false;
            }
        } else if (!markerSampleId.equals(other.markerSampleId)) {
            return false;
        }
        if (peakHeight == null) {
            if (other.peakHeight != null) {
                return false;
            }
        } else if (!peakHeight.equals(other.peakHeight)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AllelicValueWithMarkerIdElement [gid=");
        builder.append(gid);
        builder.append(", data=");
        builder.append(data);
        builder.append(", markerId=");
        builder.append(markerId);
        builder.append(", peakHeight=");
        builder.append(peakHeight);
        builder.append(", markerSampleId=");
        builder.append(markerSampleId);
        builder.append(", accSampleId=");
        builder.append(accSampleId);
        builder.append("]");
        return builder.toString();
    }


}
