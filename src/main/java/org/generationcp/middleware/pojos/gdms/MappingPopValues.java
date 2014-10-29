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

import javax.persistence.*;
import java.io.Serializable;

/**
 * POJO for gdms_mapping_pop_values table.
 * 
 * @author Mark Agarrado
 */
@Entity
@Table(name = "gdms_mapping_pop_values")
public class MappingPopValues implements Serializable{

    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @Column(name = "mp_id")
    private Integer mpId;
    
    @Column(name = "map_char_value")
    private String mapCharValue;
    
    @Column(name = "dataset_id")
    private Integer datasetId;
    
    @Column(name = "gid")
    private Integer gid;
    
    @Column(name = "marker_id")
    private Integer markerId;
   
    @Column(name = "acc_sample_id")
    private Integer accSampleId;

    @Column(name = "marker_sample_id")
    private Integer markerSampleId;

    
    public MappingPopValues() {
    }

    public MappingPopValues(Integer mpId, String mapCharValue,
			Integer datasetId, Integer gid, Integer markerId,
			Integer accSampleId, Integer markerSampleId) {
		this.mpId = mpId;
		this.mapCharValue = mapCharValue;
		this.datasetId = datasetId;
		this.gid = gid;
		this.markerId = markerId;
		this.accSampleId = accSampleId;
		this.markerSampleId = markerSampleId;
	}

	public Integer getMpId() {
        return mpId;
    }

    public void setMpId(Integer mpId) {
        this.mpId = mpId;
    }

    public String getMapCharValue() {
        return mapCharValue;
    }

    public void setMapCharValue(String mapCharValue) {
        this.mapCharValue = mapCharValue;
    }

    public Integer getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(Integer datasetId) {
        this.datasetId = datasetId;
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

	public Integer getAccSampleId() {
		return accSampleId;
	}

	public void setAccSampleId(Integer accSampleId) {
		this.accSampleId = accSampleId;
	}

	public Integer getMarkerSampleId() {
		return markerSampleId;
	}

	public void setMarkerSampleId(Integer markerSampleId) {
		this.markerSampleId = markerSampleId;
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
				+ ((mapCharValue == null) ? 0 : mapCharValue.hashCode());
		result = prime * result
				+ ((markerId == null) ? 0 : markerId.hashCode());
		result = prime * result
				+ ((markerSampleId == null) ? 0 : markerSampleId.hashCode());
		result = prime * result + ((mpId == null) ? 0 : mpId.hashCode());
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
		MappingPopValues other = (MappingPopValues) obj;
		if (accSampleId == null) {
			if (other.accSampleId != null) {
                return false;
            }
		} else if (!accSampleId.equals(other.accSampleId)) {
            return false;
        }
		if (datasetId == null) {
			if (other.datasetId != null) {
                return false;
            }
		} else if (!datasetId.equals(other.datasetId)) {
            return false;
        }
		if (gid == null) {
			if (other.gid != null) {
                return false;
            }
		} else if (!gid.equals(other.gid)) {
            return false;
        }
		if (mapCharValue == null) {
			if (other.mapCharValue != null) {
                return false;
            }
		} else if (!mapCharValue.equals(other.mapCharValue)) {
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
		if (mpId == null) {
			if (other.mpId != null) {
                return false;
            }
		} else if (!mpId.equals(other.mpId)) {
            return false;
        }
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MappingPopValues [mpId=");
		builder.append(mpId);
		builder.append(", mapCharValue=");
		builder.append(mapCharValue);
		builder.append(", datasetId=");
		builder.append(datasetId);
		builder.append(", gid=");
		builder.append(gid);
		builder.append(", markerId=");
		builder.append(markerId);
		builder.append(", accSampleId=");
		builder.append(accSampleId);
		builder.append(", markerSampleId=");
		builder.append(markerSampleId);
		builder.append("]");
		return builder.toString();
	}
    
    
}
