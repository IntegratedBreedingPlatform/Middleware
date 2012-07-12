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
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * POJO for allele_values table
 * 
 * @author Joyce Avestro
 *  
 */
@Entity
@Table(name = "allele_values")
public class AlleleValues implements Serializable{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    // For getMarkerNamesByGIds()
    public static final String GET_ALLELE_COUNT_BY_GID = "select count(*) from allele_values where gid in (:gIdList)";
    
    // For getGermplasmNamesByMarkerNames()
    public static final String GET_ALLELE_COUNT_BY_MARKER_ID = "select count(*) from allele_values where marker_id in (:markerIdList)";
    public static final String GET_ALLELE_GIDS_BY_MARKER_ID = 
            "SELECT distinct gid FROM allele_values WHERE marker_id in (:markerIdList) ORDER BY gid";

        
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
    @Column(name = "allele_bin_value")
    private String alleleBinValue;

    @Basic(optional = false)
    @Column(name = "allele_raw_value")
    private String alleleRawValue;
    
    public AlleleValues() {
    }

    public AlleleValues(Integer anId, Integer datasetId, Integer gId, String alleleBinValue, String alleleRawValue) {
        super();
        this.anId = anId;
        this.datasetId = datasetId;
        this.gId = gId;
        this.alleleBinValue = alleleBinValue;
        this.alleleRawValue = alleleRawValue;
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
    
    public Integer getgId() {
        return gId;
    }
    
    public void setgId(Integer gId) {
        this.gId = gId;
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

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "AlleleValues [anId=" + anId 
                        + ", datasetId=" + datasetId 
                        + ", gId=" + gId 
                        + ", alleleBinValue=" + alleleBinValue 
                        + ", alleleRawValue=" + alleleRawValue + "]";
    }

}
