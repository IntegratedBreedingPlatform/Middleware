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
import javax.persistence.Table;

/**
 * POJO for allele_values table.
 *
 * @author Joyce Avestro
 */
@Entity
@Table(name = "gdms_char_values")
public class CharValues implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ac_id")
	private Integer acId;

	@Basic(optional = false)
	@Column(name = "dataset_id")
	private Integer datasetId;

	@Basic(optional = false)
	@Column(name = "marker_id")
	private Integer markerId;

	@Basic(optional = false)
	@Column(name = "sample_id")
	private Integer sampleId;

	@Column(name = "char_value")
	String charValue;

	@Column(name = "marker_sample_id")
	Integer markerSampleId;

	@Column(name = "acc_sample_id")
	Integer accSampleId;

	public CharValues() {
	}

	public CharValues(Integer acId, Integer datasetId, Integer markerId, Integer sampleId, String charValue, Integer markerSampleId,
			Integer accSampleId) {
		this.acId = acId;
		this.datasetId = datasetId;
		this.markerId = markerId;
		this.sampleId = sampleId;
		this.charValue = charValue;
		this.markerSampleId = markerSampleId;
		this.accSampleId = accSampleId;
	}

	public Integer getAcId() {
		return this.acId;
	}

	public void setAcId(Integer acId) {
		this.acId = acId;
	}

	public Integer getDatasetId() {
		return this.datasetId;
	}

	public void setDatasetId(Integer datasetId) {
		this.datasetId = datasetId;
	}

	public Integer getMarkerId() {
		return this.markerId;
	}

	public void setMarkerId(Integer markerId) {
		this.markerId = markerId;
	}

	public Integer getSampleId() {
		return this.sampleId;
	}

	public void setSampleId(Integer sampleId) {
		this.sampleId = sampleId;
	}

	public String getCharValue() {
		return this.charValue;
	}

	public void setCharValue(String charValue) {
		this.charValue = charValue;
	}

	public Integer getMarkerSampleId() {
		return this.markerSampleId;
	}

	public void setMarkerSampleId(Integer markerSampleId) {
		this.markerSampleId = markerSampleId;
	}

	public Integer getAccSampleId() {
		return this.accSampleId;
	}

	public void setAccSampleId(Integer accSampleId) {
		this.accSampleId = accSampleId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.acId == null ? 0 : this.acId.hashCode());
		result = prime * result + (this.accSampleId == null ? 0 : this.accSampleId.hashCode());
		result = prime * result + (this.charValue == null ? 0 : this.charValue.hashCode());
		result = prime * result + (this.datasetId == null ? 0 : this.datasetId.hashCode());
		result = prime * result + (this.sampleId == null ? 0 : this.sampleId.hashCode());
		result = prime * result + (this.markerId == null ? 0 : this.markerId.hashCode());
		result = prime * result + (this.markerSampleId == null ? 0 : this.markerSampleId.hashCode());
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
		CharValues other = (CharValues) obj;
		if (this.acId == null) {
			if (other.acId != null) {
				return false;
			}
		} else if (!this.acId.equals(other.acId)) {
			return false;
		}
		if (this.accSampleId == null) {
			if (other.accSampleId != null) {
				return false;
			}
		} else if (!this.accSampleId.equals(other.accSampleId)) {
			return false;
		}
		if (this.charValue == null) {
			if (other.charValue != null) {
				return false;
			}
		} else if (!this.charValue.equals(other.charValue)) {
			return false;
		}
		if (this.datasetId == null) {
			if (other.datasetId != null) {
				return false;
			}
		} else if (!this.datasetId.equals(other.datasetId)) {
			return false;
		}
		if (this.sampleId == null) {
			if (other.sampleId != null) {
				return false;
			}
		} else if (!this.sampleId.equals(other.sampleId)) {
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
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CharValues [acId=");
		builder.append(this.acId);
		builder.append(", datasetId=");
		builder.append(this.datasetId);
		builder.append(", markerId=");
		builder.append(this.markerId);
		builder.append(", sampleId=");
		builder.append(this.sampleId);
		builder.append(", charValue=");
		builder.append(this.charValue);
		builder.append(", markerSampleId=");
		builder.append(this.markerSampleId);
		builder.append(", accSampleId=");
		builder.append(this.accSampleId);
		builder.append("]");
		return builder.toString();
	}

}
