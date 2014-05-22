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
 * POJO for allele_values table.
 *
 * @author Joyce Avestro
 */
@Entity
@Table(name = "gdms_char_values")
public class CharValues implements Serializable{

    private static final long serialVersionUID = 1L;
    
    @Id
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
    @Column(name = "gid")
    private Integer gId;
    
    @Column(name = "char_value")
    String charValue;
    
    @Column(name = "marker_sample_id")
    Integer markerSampleId;

    @Column(name = "acc_sample_id")
    Integer accSampleId;
 
    public CharValues() {
    }

    public CharValues(Integer acId, Integer datasetId, Integer markerId, Integer gId, String charValue, Integer markerSampleId, Integer accSampleId) {
        this.acId = acId;
        this.datasetId = datasetId;
        this.markerId = markerId;
        this.gId = gId;
        this.charValue = charValue;
        this.markerSampleId = markerSampleId;
        this.accSampleId = accSampleId;
    }
    
    public Integer getAcId() {
        return acId;
    }

    public void setAcId(Integer acId) {
        this.acId = acId;
    }
    
    public Integer getDatasetId() {
        return datasetId;
    }
    
    public void setDatasetId(Integer datasetId) {
        this.datasetId = datasetId;
    }
    
    public Integer getMarkerId() {
        return markerId;
    }
    
    public void setMarkerId(Integer markerId) {
        this.markerId = markerId;
    }
    
    public Integer getGid() {
        return gId;
    }
    
    public void setGid(Integer gId) {
        this.gId = gId;
    }
    
    public String getCharValue() {
        return charValue;
    }
    
    public void setCharValue(String charValue) {
        this.charValue = charValue;
    }

    public Integer getMarkerSampleId() {
        return markerSampleId;
    }

    public void setMarkerSampleId(Integer markerSampleId) {
        this.markerSampleId = markerSampleId;
    }
    
    public Integer getAccSampleId() {
        return accSampleId;
    }

    public void setAccSampleId(Integer accSampleId) {
        this.accSampleId = accSampleId;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((acId == null) ? 0 : acId.hashCode());
		result = prime * result
				+ ((accSampleId == null) ? 0 : accSampleId.hashCode());
		result = prime * result
				+ ((charValue == null) ? 0 : charValue.hashCode());
		result = prime * result
				+ ((datasetId == null) ? 0 : datasetId.hashCode());
		result = prime * result + ((gId == null) ? 0 : gId.hashCode());
		result = prime * result
				+ ((markerId == null) ? 0 : markerId.hashCode());
		result = prime * result
				+ ((markerSampleId == null) ? 0 : markerSampleId.hashCode());
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
		CharValues other = (CharValues) obj;
		if (acId == null) {
			if (other.acId != null)
				return false;
		} else if (!acId.equals(other.acId))
			return false;
		if (accSampleId == null) {
			if (other.accSampleId != null)
				return false;
		} else if (!accSampleId.equals(other.accSampleId))
			return false;
		if (charValue == null) {
			if (other.charValue != null)
				return false;
		} else if (!charValue.equals(other.charValue))
			return false;
		if (datasetId == null) {
			if (other.datasetId != null)
				return false;
		} else if (!datasetId.equals(other.datasetId))
			return false;
		if (gId == null) {
			if (other.gId != null)
				return false;
		} else if (!gId.equals(other.gId))
			return false;
		if (markerId == null) {
			if (other.markerId != null)
				return false;
		} else if (!markerId.equals(other.markerId))
			return false;
		if (markerSampleId == null) {
			if (other.markerSampleId != null)
				return false;
		} else if (!markerSampleId.equals(other.markerSampleId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CharValues [acId=");
		builder.append(acId);
		builder.append(", datasetId=");
		builder.append(datasetId);
		builder.append(", markerId=");
		builder.append(markerId);
		builder.append(", gId=");
		builder.append(gId);
		builder.append(", charValue=");
		builder.append(charValue);
		builder.append(", markerSampleId=");
		builder.append(markerSampleId);
		builder.append(", accSampleId=");
		builder.append(accSampleId);
		builder.append("]");
		return builder.toString();
	}
    
}
