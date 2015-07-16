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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * POJO for gdms_dart_values table.
 *
 * @author Dennis Billano
 */
@Entity
@Table(name = "gdms_dart_values")
public class DartValues implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ad_id")
	private Integer adId;

	@Column(name = "dataset_id")
	private Integer datasetId;

	@Column(name = "marker_id")
	private Integer markerId;

	@Column(name = "clone_id")
	private Integer cloneId;

	@Column(name = "qvalue")
	private Float qValue;

	@Column(name = "reproducibility")
	private Float reproducibility;

	@Column(name = "call_rate")
	private Float callRate;

	@Column(name = "pic_value")
	private Float picValue;

	@Column(name = "discordance")
	private Float discordance;

	public DartValues() {
		super();
	}

	public DartValues(Integer adId, Integer datasetId, Integer markerId, Integer cloneId, Float qValue, Float reproducibility,
			Float callRate, Float picValue, Float discordance) {
		super();
		this.adId = adId;
		this.datasetId = datasetId;
		this.markerId = markerId;
		this.cloneId = cloneId;
		this.qValue = qValue;
		this.reproducibility = reproducibility;
		this.callRate = callRate;
		this.picValue = picValue;
		this.discordance = discordance;
	}

	public Integer getAdId() {
		return this.adId;
	}

	public void setAdId(Integer adId) {
		this.adId = adId;
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

	public Integer getCloneId() {
		return this.cloneId;
	}

	public void setCloneId(Integer cloneId) {
		this.cloneId = cloneId;
	}

	public Float getqValue() {
		return this.qValue;
	}

	public void setqValue(Float qValue) {
		this.qValue = qValue;
	}

	public Float getReproducibility() {
		return this.reproducibility;
	}

	public void setReproducibility(Float reproducibility) {
		this.reproducibility = reproducibility;
	}

	public Float getCallRate() {
		return this.callRate;
	}

	public void setCallRate(Float callRate) {
		this.callRate = callRate;
	}

	public Float getPicValue() {
		return this.picValue;
	}

	public void setPicValue(Float picValue) {
		this.picValue = picValue;
	}

	public Float getDiscordance() {
		return this.discordance;
	}

	public void setDiscordance(Float discordance) {
		this.discordance = discordance;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(13, 127).append(this.markerId).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof DartValues)) {
			return false;
		}

		DartValues rhs = (DartValues) obj;
		return new EqualsBuilder().appendSuper(super.equals(obj)).append(this.adId, rhs.adId).isEquals();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DartValues [adId=");
		builder.append(this.adId);
		builder.append(", datasetId=");
		builder.append(this.datasetId);
		builder.append(", markerId=");
		builder.append(this.markerId);
		builder.append(", cloneId=");
		builder.append(this.cloneId);
		builder.append(", qValue=");
		builder.append(this.qValue);
		builder.append(", reproducibility=");
		builder.append(this.reproducibility);
		builder.append(", callRate=");
		builder.append(this.callRate);
		builder.append(", picValue=");
		builder.append(this.picValue);
		builder.append(", discordance=");
		builder.append(this.discordance);
		builder.append("]");
		return builder.toString();
	}

}
