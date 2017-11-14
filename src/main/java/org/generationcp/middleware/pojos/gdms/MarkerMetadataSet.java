/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.pojos.gdms;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * POJO for gdms_marker_metadataset table.
 *
 * @author Joyce Avestro
 */
@Entity
@Table(name = "gdms_marker_metadataset")
public class MarkerMetadataSet implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "marker_metadataset_id")
	private Integer markerMetadataSetId;

	@ManyToOne
	@JoinColumn(name = "dataset_id")
	private Dataset dataset;

	@Basic(optional = false)
	@Column(name = "marker_id")
	private Integer markerId;

	@Column(name = "marker_sample_id")
	private Integer markerSampleId;

	public MarkerMetadataSet() {
	}

	public MarkerMetadataSet(final Integer markerMetadataSetId, final Dataset dataset, final Integer markerId, final Integer markerSampleId) {
		this.markerMetadataSetId = markerMetadataSetId;
		this.dataset = dataset;
		this.markerId = markerId;
		this.markerSampleId = markerSampleId;
	}

	public Integer getMarkerMetadataSetId() {
		return this.markerMetadataSetId;
	}

	public void setMarkerMetadataSetId(final Integer markerMetadataSetId) {
		this.markerMetadataSetId = markerMetadataSetId;
	}

	public Dataset getDataset() {
		return this.dataset;
	}

	public void setDataset(final Dataset dataset) {
		this.dataset = dataset;
	}

	public Integer getMarkerId() {
		return this.markerId;
	}

	public void setMarkerId(final Integer markerId) {
		this.markerId = markerId;
	}

	public Integer getMarkerSampleId() {
		return this.markerSampleId;
	}

	public void setMarkerSampleId(final Integer markerSampleId) {
		this.markerSampleId = markerSampleId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.dataset == null ? 0 : this.dataset.hashCode());
		result = prime * result + (this.markerId == null ? 0 : this.markerId.hashCode());
		result = prime * result + (this.markerMetadataSetId == null ? 0 : this.markerMetadataSetId.hashCode());
		result = prime * result + (this.markerSampleId == null ? 0 : this.markerSampleId.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		MarkerMetadataSet other = (MarkerMetadataSet) obj;
		if (this.dataset == null) {
			if (other.dataset != null) {
				return false;
			}
		} else if (!this.dataset.equals(other.dataset)) {
			return false;
		}
		if (this.markerId == null) {
			if (other.markerId != null) {
				return false;
			}
		} else if (!this.markerId.equals(other.markerId)) {
			return false;
		}
		if (this.markerMetadataSetId == null) {
			if (other.markerMetadataSetId != null) {
				return false;
			}
		} else if (!this.markerMetadataSetId.equals(other.markerMetadataSetId)) {
			return false;
		}
		if (this.markerSampleId == null) {
			if (other.markerSampleId != null) {
				return false;
			}
		} else if (!this.markerSampleId.equals(other.markerSampleId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MarkerMetadataSet [markerMetadataSetId=");
		builder.append(this.markerMetadataSetId);
		builder.append(", markerSampleId=");
		builder.append(this.markerSampleId);
		builder.append(", dataset=");
		builder.append(this.dataset);
		builder.append(", markerId=");
		builder.append(this.markerId);
		builder.append("]");
		return builder.toString();
	}

}
