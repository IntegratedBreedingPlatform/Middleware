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

import org.generationcp.middleware.pojos.Sample;

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
 * POJO for gdms_mapping_pop_values table.
 *
 * @author Mark Agarrado
 */
@Entity
@Table(name = "gdms_mapping_pop_values")
public class MappingPopValues implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "mp_id")
	private Integer mpId;

	@Column(name = "map_char_value")
	private String mapCharValue;

	@Column(name = "dataset_id")
	private Integer datasetId;

	@ManyToOne
	@JoinColumn(name = "sample_id")
	private Sample sample;

	@Column(name = "marker_id")
	private Integer markerId;

	@Column(name = "acc_sample_id")
	private Integer accSampleId;

	@Column(name = "marker_sample_id")
	private Integer markerSampleId;

	public MappingPopValues() {
	}

	public MappingPopValues(Integer mpId, String mapCharValue, Integer datasetId, Sample sample, Integer markerId, Integer accSampleId,
			Integer markerSampleId) {
		this.mpId = mpId;
		this.mapCharValue = mapCharValue;
		this.datasetId = datasetId;
		this.sample = sample;
		this.markerId = markerId;
		this.accSampleId = accSampleId;
		this.markerSampleId = markerSampleId;
	}

	public Integer getMpId() {
		return this.mpId;
	}

	public void setMpId(Integer mpId) {
		this.mpId = mpId;
	}

	public String getMapCharValue() {
		return this.mapCharValue;
	}

	public void setMapCharValue(String mapCharValue) {
		this.mapCharValue = mapCharValue;
	}

	public Integer getDatasetId() {
		return this.datasetId;
	}

	public void setDatasetId(Integer datasetId) {
		this.datasetId = datasetId;
	}

	public Sample getSample() {
		return this.sample;
	}

	public void setSample(Sample sample) {
		this.sample = sample;
	}

	public Integer getMarkerId() {
		return this.markerId;
	}

	public void setMarkerId(Integer markerId) {
		this.markerId = markerId;
	}

	public Integer getAccSampleId() {
		return this.accSampleId;
	}

	public void setAccSampleId(Integer accSampleId) {
		this.accSampleId = accSampleId;
	}

	public Integer getMarkerSampleId() {
		return this.markerSampleId;
	}

	public void setMarkerSampleId(Integer markerSampleId) {
		this.markerSampleId = markerSampleId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.accSampleId == null ? 0 : this.accSampleId.hashCode());
		result = prime * result + (this.datasetId == null ? 0 : this.datasetId.hashCode());
		result = prime * result + (this.sample == null ? 0 : this.sample.hashCode());
		result = prime * result + (this.mapCharValue == null ? 0 : this.mapCharValue.hashCode());
		result = prime * result + (this.markerId == null ? 0 : this.markerId.hashCode());
		result = prime * result + (this.markerSampleId == null ? 0 : this.markerSampleId.hashCode());
		result = prime * result + (this.mpId == null ? 0 : this.mpId.hashCode());
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
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		MappingPopValues other = (MappingPopValues) obj;
		if (this.accSampleId == null) {
			if (other.accSampleId != null) {
				return false;
			}
		} else if (!this.accSampleId.equals(other.accSampleId)) {
			return false;
		}
		if (this.datasetId == null) {
			if (other.datasetId != null) {
				return false;
			}
		} else if (!this.datasetId.equals(other.datasetId)) {
			return false;
		}
		if (this.sample == null) {
			if (other.sample != null) {
				return false;
			}
		} else if (!this.sample.equals(other.sample)) {
			return false;
		}
		if (this.mapCharValue == null) {
			if (other.mapCharValue != null) {
				return false;
			}
		} else if (!this.mapCharValue.equals(other.mapCharValue)) {
			return false;
		}
		if (this.markerId == null) {
			if (other.markerId != null) {
				return false;
			}
		} else if (!this.markerId.equals(other.markerId)) {
			return false;
		}
		if (this.markerSampleId == null) {
			if (other.markerSampleId != null) {
				return false;
			}
		} else if (!this.markerSampleId.equals(other.markerSampleId)) {
			return false;
		}
		if (this.mpId == null) {
			if (other.mpId != null) {
				return false;
			}
		} else if (!this.mpId.equals(other.mpId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MappingPopValues [mpId=");
		builder.append(this.mpId);
		builder.append(", mapCharValue=");
		builder.append(this.mapCharValue);
		builder.append(", datasetId=");
		builder.append(this.datasetId);
		builder.append(", sample=");
		builder.append(this.sample);
		builder.append(", markerId=");
		builder.append(this.markerId);
		builder.append(", accSampleId=");
		builder.append(this.accSampleId);
		builder.append(", markerSampleId=");
		builder.append(this.markerSampleId);
		builder.append("]");
		return builder.toString();
	}

}
