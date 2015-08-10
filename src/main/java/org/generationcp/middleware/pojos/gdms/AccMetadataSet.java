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
	@Column(name = "gid")
	private Integer germplasmId;

	@Basic(optional = false)
	@Column(name = "nid")
	private Integer nameId;

	@Column(name = "acc_sample_id")
	private Integer accSampleId;

	public AccMetadataSet() {
	}

	public AccMetadataSet(Integer accMetadataSetId, Integer datasetId, Integer germplasmId, Integer nameId, Integer sampleId) {
		super();
		this.accMetadataSetId = accMetadataSetId;
		this.datasetId = datasetId;
		this.germplasmId = germplasmId;
		this.nameId = nameId;
		this.accSampleId = sampleId;
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

	public Integer getGermplasmId() {
		return this.germplasmId;
	}

	public void setGermplasmId(Integer germplasmId) {
		this.germplasmId = germplasmId;
	}

	public Integer getNameId() {
		return this.nameId;
	}

	public void setNameId(Integer nameId) {
		this.nameId = nameId;
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
		result = prime * result + (this.germplasmId == null ? 0 : this.germplasmId.hashCode());
		result = prime * result + (this.nameId == null ? 0 : this.nameId.hashCode());
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
		if (this.germplasmId == null) {
			if (other.germplasmId != null) {
				return false;
			}
		} else if (!this.germplasmId.equals(other.germplasmId)) {
			return false;
		}
		if (this.nameId == null) {
			if (other.nameId != null) {
				return false;
			}
		} else if (!this.nameId.equals(other.nameId)) {
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
		builder.append(", germplasmId=");
		builder.append(this.germplasmId);
		builder.append(", nameId=");
		builder.append(this.nameId);
		builder.append(", sampleId=");
		builder.append(this.accSampleId);
		builder.append("]");
		return builder.toString();
	}

}
