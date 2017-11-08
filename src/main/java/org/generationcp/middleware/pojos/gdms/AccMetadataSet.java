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
 * POJO for acc_metadataset table.
 *
 * @author Joyce Avestro
 * 
 */
@Entity
@Table(name = "gdms_acc_metadataset")
public class AccMetadataSet implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "acc_metadataset_id")
	private Integer accMetadataSetId;

	@ManyToOne
	@JoinColumn(name = "dataset_id")
	private Dataset dataset;

	@ManyToOne
	@JoinColumn(name = "sample_id")
	private Sample sample;

	@Column(name = "acc_sample_id")
	private Integer accSampleId;

	public AccMetadataSet() {
	}

	public AccMetadataSet(Integer accMetadataSetId, Dataset dataset, Sample sample, Integer accSampleId) {
		super();
		this.accMetadataSetId = accMetadataSetId;
		this.dataset = dataset;
		this.sample = sample;
		this.accSampleId = accSampleId;
	}

	public Integer getAccMetadataSetId() {
		return this.accMetadataSetId;
	}

	public void setAccMetadataSetId(Integer accMetadataSetId) {
		this.accMetadataSetId = accMetadataSetId;
	}

	public Dataset getDataset() {
		return this.dataset;
	}

	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}

	public Sample getSample() {
		return sample;
	}

	public void setSample(final Sample sample) {
		this.sample = sample;
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
		result = prime * result + (this.accMetadataSetId == null ? 0 : this.accMetadataSetId.hashCode());
		result = prime * result + (this.dataset == null ? 0 : this.dataset.hashCode());
		result = prime * result + (this.sample == null ? 0 : this.sample.hashCode());
		result = prime * result + (this.accSampleId == null ? 0 : this.accSampleId.hashCode());
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
		AccMetadataSet other = (AccMetadataSet) obj;
		if (this.accMetadataSetId == null) {
			if (other.accMetadataSetId != null) {
				return false;
			}
		} else if (!this.accMetadataSetId.equals(other.accMetadataSetId)) {
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
		if (this.accSampleId == null) {
			if (other.accSampleId != null) {
				return false;
			}
		} else if (!this.accSampleId.equals(other.accSampleId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AccMetadataSet [accMetadataSetId=");
		builder.append(this.accMetadataSetId);
		builder.append(", dataset=");
		builder.append(this.dataset);
		builder.append(", sample=");
		builder.append(this.sample);
		builder.append(", accSampleId=");
		builder.append(this.accSampleId);
		builder.append("]");
		return builder.toString();
	}

}
