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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
    @Basic(optional = false)
    @Column(name = "an_id")
    private Integer anId;

    @Basic(optional = false)
    @Column(name = "dataset_id")
    private Integer datasetId;

    @Basic(optional = false)
    @Column(name = "gid")
    private Integer gId;

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

    public AlleleValues(Integer anId, Integer datasetId, Integer gId,
                        Integer markerId, String alleleBinValue, String alleleRawValue,
                        Integer peakHeight) {
        this.anId = anId;
        this.datasetId = datasetId;
        this.gId = gId;
        this.markerId = markerId;
        this.alleleBinValue = alleleBinValue;
        this.alleleRawValue = alleleRawValue;
        this.peakHeight = peakHeight;
    }

    public Integer getAnId() {
        return anId;
    }

    public void setAnId(Integer anId) {
        this.anId = anId;
    }

    public Integer getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(Integer datasetId) {
        this.datasetId = datasetId;
    }

    public Integer getGid() {
        return gId;
    }

    public void setGid(Integer gId) {
        this.gId = gId;
    }

    public Integer getMarkerId() {
        return markerId;
    }

    public void setMarkerId(Integer markerId) {
        this.markerId = markerId;
    }

    public String getAlleleBinValue() {
        return alleleBinValue;
    }

    public void setAlleleBinValue(String alleleBinValue) {
        this.alleleBinValue = alleleBinValue;
    }

    public String getAlleleRawValue() {
        return alleleRawValue;
    }

    public void setAlleleRawValue(String alleleRawValue) {
        this.alleleRawValue = alleleRawValue;
    }

    public Integer getPeakHeight() {
        return peakHeight;
    }

    public void setPeakHeight(Integer peakHeight) {
        this.peakHeight = peakHeight;
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
        return new HashCodeBuilder(37, 127).append(anId).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AlleleValues)) {
            return false;
        }

        AlleleValues rhs = (AlleleValues) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(anId, rhs.anId).isEquals();
    }

    @Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AlleleValues [anId=");
		builder.append(anId);
		builder.append(", datasetId=");
		builder.append(datasetId);
		builder.append(", gId=");
		builder.append(gId);
		builder.append(", markerId=");
		builder.append(markerId);
		builder.append(", alleleBinValue=");
		builder.append(alleleBinValue);
		builder.append(", alleleRawValue=");
		builder.append(alleleRawValue);
		builder.append(", peakHeight=");
		builder.append(peakHeight);
		builder.append(", markerSampleId=");
		builder.append(markerSampleId);
		builder.append(", accSampleId=");
		builder.append(accSampleId);
		builder.append("]");
		return builder.toString();
	}

}
