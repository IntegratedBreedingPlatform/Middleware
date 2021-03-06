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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.generationcp.middleware.pojos.Sample;

/**
 * POJO for allele_values table.
 *
 * @author Joyce Avestro
 */
@Entity
@Table(name = "gdms_allele_values")
public class AlleleValues implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "an_id")
	private Integer anId;

	@Basic(optional = false)
	@Column(name = "dataset_id")
	private Integer datasetId;

	@ManyToOne
	@JoinColumn(name = "sample_id")
	private Sample sample;

	@Basic(optional = false)
	@Column(name = "marker_id")
	private Integer markerId;

	@Basic(optional = false)
	@Column(name = "allele_bin_value")
	private String alleleBinValue;

	@Basic(optional = false)
	@Column(name = "allele_raw_value")
	private String alleleRawValue;

	@Column(name = "peak_height")
	private Integer peakHeight;

	@Column(name = "marker_sample_id")
	private Integer markerSampleId;

	@Column(name = "acc_sample_id")
	private Integer accSampleId;

	public AlleleValues() {
	}

	public AlleleValues(final Integer anId, final Integer datasetId, final Sample sample, final Integer markerId, final String alleleBinValue, final String alleleRawValue,
			final Integer peakHeight) {
		this.anId = anId;
		this.datasetId = datasetId;
		this.sample = sample;
		this.markerId = markerId;
		this.alleleBinValue = alleleBinValue;
		this.alleleRawValue = alleleRawValue;
		this.peakHeight = peakHeight;
	}

	public Integer getAnId() {
		return this.anId;
	}

	public void setAnId(final Integer anId) {
		this.anId = anId;
	}

	public Integer getDatasetId() {
		return this.datasetId;
	}

	public void setDatasetId(final Integer datasetId) {
		this.datasetId = datasetId;
	}

	public Sample getSample() {
		return this.sample;
	}

	public void setSample(final Sample sample) {
		this.sample = sample;
	}

	public Integer getMarkerId() {
		return this.markerId;
	}

	public void setMarkerId(final Integer markerId) {
		this.markerId = markerId;
	}

	public String getAlleleBinValue() {
		return this.alleleBinValue;
	}

	public void setAlleleBinValue(final String alleleBinValue) {
		this.alleleBinValue = alleleBinValue;
	}

	public String getAlleleRawValue() {
		return this.alleleRawValue;
	}

	public void setAlleleRawValue(final String alleleRawValue) {
		this.alleleRawValue = alleleRawValue;
	}

	public Integer getPeakHeight() {
		return this.peakHeight;
	}

	public void setPeakHeight(final Integer peakHeight) {
		this.peakHeight = peakHeight;
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
		return new HashCodeBuilder(37, 127).append(this.anId).toHashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof AlleleValues)) {
			return false;
		}

		final AlleleValues rhs = (AlleleValues) obj;
		return new EqualsBuilder().append(this.anId, rhs.anId).isEquals();
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("AlleleValues [anId=");
		builder.append(this.anId);
		builder.append(", datasetId=");
		builder.append(this.datasetId);
		builder.append(", sampleId=");
		builder.append(this.sample);
		builder.append(", markerId=");
		builder.append(this.markerId);
		builder.append(", alleleBinValue=");
		builder.append(this.alleleBinValue);
		builder.append(", alleleRawValue=");
		builder.append(this.alleleRawValue);
		builder.append(", peakHeight=");
		builder.append(this.peakHeight);
		builder.append(", markerSampleId=");
		builder.append(this.markerSampleId);
		builder.append(", accSampleId=");
		builder.append(this.accSampleId);
		builder.append("]");
		return builder.toString();
	}

}
