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
 * POJO for gdms_marker_metadataset table.
 *
 * @author Joyce Avestro
 */
@Entity
@Table(name = "gdms_marker_metadataset")
public class MarkerMetadataSet implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "marker_metadataset_id")
    private Integer markerMetadataSetId;
    
    @Basic(optional = false)
    @Column(name = "dataset_id")
    private Integer datasetId;

    @Basic(optional = false)
    @Column(name = "marker_id")
    private Integer markerId;

    @Column(name = "marker_sample_id")
    private Integer markerSampleId;
    
    public MarkerMetadataSet() {
    }

	public MarkerMetadataSet(Integer markerMetadataSetId,
			Integer datasetId, Integer markerId, Integer markerSampleId) {
		this.markerMetadataSetId = markerMetadataSetId;
		this.datasetId = datasetId;
		this.markerId = markerId;
		this.markerSampleId = markerSampleId;
	}

	public Integer getMarkerMetadataSetId() {
		return markerMetadataSetId;
	}

	public void setMarkerMetadataSetId(Integer markerMetadataSetId) {
		this.markerMetadataSetId = markerMetadataSetId;
	}

	public Integer getDatasetId() {
		return datasetId;
	}

	public void setDatasetId(Integer datasetId) {
		this.datasetId = datasetId;
	}

	public Integer getMarkerId() {
		return markerId;
	}

	public void setMarkerId(Integer markerId) {
		this.markerId = markerId;
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
				+ ((datasetId == null) ? 0 : datasetId.hashCode());
		result = prime * result
				+ ((markerId == null) ? 0 : markerId.hashCode());
		result = prime
				* result
				+ ((markerMetadataSetId == null) ? 0 : markerMetadataSetId
						.hashCode());
		result = prime * result
				+ ((markerSampleId == null) ? 0 : markerSampleId.hashCode());
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
		MarkerMetadataSet other = (MarkerMetadataSet) obj;
		if (datasetId == null) {
			if (other.datasetId != null) {
                return false;
            }
		} else if (!datasetId.equals(other.datasetId)) {
            return false;
        }
		if (markerId == null) {
			if (other.markerId != null) {
                return false;
            }
		} else if (!markerId.equals(other.markerId)) {
            return false;
        }
		if (markerMetadataSetId == null) {
			if (other.markerMetadataSetId != null) {
                return false;
            }
		} else if (!markerMetadataSetId.equals(other.markerMetadataSetId)) {
            return false;
        }
		if (markerSampleId == null) {
			if (other.markerSampleId != null) {
                return false;
            }
		} else if (!markerSampleId.equals(other.markerSampleId)) {
            return false;
        }
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MarkerMetadataSet [markerMetadataSetId=");
		builder.append(markerMetadataSetId);
		builder.append(", markerSampleId=");
		builder.append(markerSampleId);
		builder.append(", datasetId=");
		builder.append(datasetId);
		builder.append(", markerId=");
		builder.append(markerId);
		builder.append("]");
		return builder.toString();
	}

}
