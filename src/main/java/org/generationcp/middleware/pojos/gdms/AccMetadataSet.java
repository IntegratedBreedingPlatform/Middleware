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

	@Basic(optional = false)
	@Column(name = "dataset_id")
	private Integer datasetId;

	@Basic(optional = false)
	@Column(name = "sample_id")
	private Integer sampleId;

	@Column(name = "acc_sample_id")
	private Integer accSampleId;

	public AccMetadataSet() {
	}

	public AccMetadataSet(Integer accMetadataSetId, Integer datasetId, Integer sampleId, Integer accSampleId) {
		super();
		this.accMetadataSetId = accMetadataSetId;
		this.datasetId = datasetId;
		this.sampleId = sampleId;
		this.accSampleId = accSampleId;
	}

	public Integer getAccMetadataSetId() {
		return this.accMetadataSetId;
	}

	public void setAccMetadataSetId(Integer accMetadataSetId) {
		this.accMetadataSetId = accMetadataSetId;
	}

	public Integer getDatasetId() {
		return this.datasetId;
	}

	public void setDatasetId(Integer datasetId) {
		this.datasetId = datasetId;
	}

	public Integer getSampleId() {
		return sampleId;
	}

	public void setSampleId(final Integer sampleId) {
		this.sampleId = sampleId;
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
		result = prime * result + (this.datasetId == null ? 0 : this.datasetId.hashCode());
		result = prime * result + (this.sampleId == null ? 0 : this.sampleId.hashCode());
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
		builder.append(", datasetId=");
		builder.append(this.datasetId);
		builder.append(", sampleId=");
		builder.append(this.sampleId);
		builder.append(", accSampleId=");
		builder.append(this.accSampleId);
		builder.append("]");
		return builder.toString();
	}

}
