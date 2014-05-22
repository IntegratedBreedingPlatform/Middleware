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

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
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
public class AccMetadataSet implements Serializable{

    private static final long serialVersionUID = 1L;
    
    @Id
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
    private Integer sampleId;

    public AccMetadataSet() {
    }

    public AccMetadataSet(Integer accMetadataSetId, Integer datasetId,
			Integer germplasmId, Integer nameId, Integer sampleId) {
		super();
		this.accMetadataSetId = accMetadataSetId;
		this.datasetId = datasetId;
		this.germplasmId = germplasmId;
		this.nameId = nameId;
		this.sampleId = sampleId;
	}

	public Integer getAccMetadataSetId() {
		return accMetadataSetId;
	}

	public void setAccMetadataSetId(Integer accMetadataSetId) {
		this.accMetadataSetId = accMetadataSetId;
	}

	public Integer getDatasetId() {
		return datasetId;
	}

	public void setDatasetId(Integer datasetId) {
		this.datasetId = datasetId;
	}

	public Integer getGermplasmId() {
		return germplasmId;
	}

	public void setGermplasmId(Integer germplasmId) {
		this.germplasmId = germplasmId;
	}

	public Integer getNameId() {
		return nameId;
	}

	public void setNameId(Integer nameId) {
		this.nameId = nameId;
	}

	public Integer getSampleId() {
		return sampleId;
	}

	public void setSampleId(Integer sampleId) {
		this.sampleId = sampleId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((accMetadataSetId == null) ? 0 : accMetadataSetId.hashCode());
		result = prime * result
				+ ((datasetId == null) ? 0 : datasetId.hashCode());
		result = prime * result
				+ ((germplasmId == null) ? 0 : germplasmId.hashCode());
		result = prime * result + ((nameId == null) ? 0 : nameId.hashCode());
		result = prime * result
				+ ((sampleId == null) ? 0 : sampleId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AccMetadataSet other = (AccMetadataSet) obj;
		if (accMetadataSetId == null) {
			if (other.accMetadataSetId != null)
				return false;
		} else if (!accMetadataSetId.equals(other.accMetadataSetId))
			return false;
		if (datasetId == null) {
			if (other.datasetId != null)
				return false;
		} else if (!datasetId.equals(other.datasetId))
			return false;
		if (germplasmId == null) {
			if (other.germplasmId != null)
				return false;
		} else if (!germplasmId.equals(other.germplasmId))
			return false;
		if (nameId == null) {
			if (other.nameId != null)
				return false;
		} else if (!nameId.equals(other.nameId))
			return false;
		if (sampleId == null) {
			if (other.sampleId != null)
				return false;
		} else if (!sampleId.equals(other.sampleId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AccMetadataSet [accMetadataSetId=");
		builder.append(accMetadataSetId);
		builder.append(", datasetId=");
		builder.append(datasetId);
		builder.append(", germplasmId=");
		builder.append(germplasmId);
		builder.append(", nameId=");
		builder.append(nameId);
		builder.append(", sampleId=");
		builder.append(sampleId);
		builder.append("]");
		return builder.toString();
	}

}
