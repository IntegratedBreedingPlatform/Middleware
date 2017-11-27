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

	@ManyToOne
	@JoinColumn(name = "dataset_id")
	private Dataset dataset;

	@ManyToOne
	@JoinColumn(name = "marker_id")
	private Marker marker;

	@ManyToOne
	@JoinColumn(name = "sample_id")
	private Sample sample;

	@Column(name = "char_value")
	private String charValue;

	@Column(name = "marker_sample_id")
	private Integer markerSampleId;

	@Column(name = "acc_sample_id")
	private Integer accSampleId;

	public CharValues() {
	}

	public CharValues(
			final Integer acId, final Dataset dataset, final Marker marker, final Sample sample, final String charValue, final Integer markerSampleId,
			final Integer accSampleId) {
		this.acId = acId;
		this.dataset = dataset;
		this.marker = marker;
		this.sample = sample;
		this.charValue = charValue;
		this.markerSampleId = markerSampleId;
		this.accSampleId = accSampleId;
	}

	public Integer getAcId() {
		return this.acId;
	}

	public void setAcId(final Integer acId) {
		this.acId = acId;
	}

	public Dataset getDataset() {
		return this.dataset;
	}

	public void setDataset(final Dataset dataset) {
		this.dataset = dataset;
	}

	public Marker getMarker() {
		return this.marker;
	}

	public void setMarker(final Marker marker) {
		this.marker = marker;
	}

	public Sample getSample() {
		return this.sample;
	}

	public void setSample(final Sample sample) {
		this.sample = sample;
	}

	public String getCharValue() {
		return this.charValue;
	}

	public void setCharValue(final String charValue) {
		this.charValue = charValue;
	}

	public Integer getMarkerSampleId() {
		return this.markerSampleId;
	}

	public void setMarkerSampleId(final Integer markerSampleId) {
		this.markerSampleId = markerSampleId;
	}

	public Integer getAccSampleId() {
		return this.accSampleId;
	}

	public void setAccSampleId(final Integer accSampleId) {
		this.accSampleId = accSampleId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.acId == null ? 0 : this.acId.hashCode());
		result = prime * result + (this.accSampleId == null ? 0 : this.accSampleId.hashCode());
		result = prime * result + (this.charValue == null ? 0 : this.charValue.hashCode());
		result = prime * result + (this.dataset == null ? 0 : this.dataset.hashCode());
		result = prime * result + (this.sample == null ? 0 : this.sample.hashCode());
		result = prime * result + (this.marker == null ? 0 : this.marker.hashCode());
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
		if (this.dataset == null) {
			if (other.dataset != null) {
				return false;
			}
		} else if (!this.dataset.equals(other.dataset)) {
			return false;
		}
		if (this.sample == null) {
			if (other.sample != null) {
				return false;
			}
		} else if (!this.sample.equals(other.sample)) {
			return false;
		}
		if (this.marker == null) {
			if (other.marker != null) {
				return false;
			}
		} else if (!this.marker.equals(other.marker)) {
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
		builder.append(", dataset=");
		builder.append(this.dataset);
		builder.append(", markerId=");
		builder.append(this.marker);
		builder.append(", sample=");
		builder.append(this.sample);
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
