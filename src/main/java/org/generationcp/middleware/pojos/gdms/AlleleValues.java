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
public class AlleleValues implements Serializable{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    // For getMarkerNamesByGIds()
    /** The Constant GET_ALLELE_COUNT_BY_GID. */
    public static final String GET_ALLELE_COUNT_BY_GID = 
            "SELECT COUNT(*) " +
            "FROM gdms_allele_values " +
            "WHERE gid IN (:gIdList)";
    
    // For getGermplasmNamesByMarkerNames()
    /** The Constant GET_ALLELE_COUNT_BY_MARKER_ID. */
    public static final String GET_ALLELE_COUNT_BY_MARKER_ID = 
            "SELECT COUNT(*) " +
            "FROM gdms_allele_values " +
            "WHERE marker_id IN (:markerIdList)";

    // For getGermplasmNamesByMarkerNames()
    public static final String GET_ALLELE_GERMPLASM_NAME_AND_MARKER_NAME_BY_MARKER_NAMES = 
            "SELECT n.nval, CONCAT(m.marker_name, '') " +  
            "FROM names n JOIN gdms_allele_values a ON n.gid = a.gid " +  
            "           JOIN gdms_marker m ON a.marker_id = m.marker_id " +
            "WHERE marker_name IN (:markerNameList) AND n.nstat = 1 " +
            "ORDER BY n.nval, m.marker_name";
    
    // For getAllelicValues by gid and marker names
    public static final String GET_ALLELIC_VALUES_BY_GIDS_AND_MARKER_NAMES = 
            "SELECT DISTINCT " +
                "gdms_allele_values.gid, " +
                "CONCAT(gdms_allele_values.allele_bin_value, ''), " +
                "CONCAT(gdms_marker.marker_name, '') " +
            "FROM gdms_allele_values, " +
                "gdms_marker " +
            "WHERE gdms_allele_values.marker_id = gdms_marker.marker_id " +
                "AND gdms_allele_values.gid IN (:gidList) " +
                "AND gdms_allele_values.marker_id IN (:markerIdList) " +
            "ORDER BY gdms_allele_values.gid DESC, gdms_marker.marker_name";

    // For getAllelicValues by datasetId
    public static final String GET_ALLELIC_VALUES_BY_DATASET_ID = 
            "SELECT gid, marker_id, CONCAT(allele_bin_value, '') " +
            "FROM gdms_allele_values " +
            "WHERE dataset_id = :datasetId " +
            "ORDER BY gid DESC, marker_id ASC";

    public static final String COUNT_BY_DATASET_ID = 
            "SELECT COUNT(*) " +
            "FROM gdms_allele_values " +
            "WHERE dataset_id = :datasetId";

    public static final String GET_GIDS_BY_MARKER_ID = 
            "SELECT DISTINCT gid " +
            "FROM gdms_allele_values " +
            "WHERE marker_id = :markerId";
    
    public static final String COUNT_GIDS_BY_MARKER_ID = 
            "SELECT COUNT(DISTINCT gid) " +
            "FROM gdms_allele_values " +
            "WHERE marker_id = :markerId";
    
    public static final String COUNT_ALLELE_VALUES_BY_GIDS = 
            "SELECT COUNT(*) " +
            "FROM gdms_allele_values " +
            "WHERE gid in (:gids)";

    public static final String GET_CHAR_ALLELE_VALUES_FOR_POLYMORPHIC_MARKERS_RETRIEVAL_BY_GIDS =     
            "SELECT DISTINCT gdms_char_values.dataset_id, gdms_char_values.gid, " 
                    + "CONCAT(gdms_marker.marker_name,''), CONCAT(gdms_char_values.char_value,'') " 
            + "FROM gdms_char_values INNER JOIN gdms_marker ON gdms_marker.marker_id = gdms_char_values.marker_id " 
            + "WHERE gdms_char_values.gid IN (:gids) "
            + "ORDER BY gid, marker_name ";

    public static final String COUNT_CHAR_ALLELE_VALUES_FOR_POLYMORPHIC_MARKERS_RETRIEVAL_BY_GIDS = 
            "SELECT COUNT(*) " 
            + "FROM gdms_char_values INNER JOIN gdms_marker ON gdms_marker.marker_id = gdms_char_values.marker_id " 
            + "WHERE gdms_char_values.gid IN (:gids) ";

    public static final String GET_INT_ALLELE_VALUES_FOR_POLYMORPHIC_MARKERS_RETRIEVAL_BY_GIDS = 
            "SELECT gdms_allele_values.dataset_id, gdms_allele_values.gid, " 
                    + "CONCAT(gdms_marker.marker_name, ''), CONCAT(gdms_allele_values.allele_bin_value, '') "
            + "FROM gdms_allele_values INNER JOIN gdms_marker ON gdms_marker.marker_id = gdms_allele_values.marker_id "
            + "WHERE gdms_allele_values.gid IN (:gids) "
            + "ORDER BY gid, marker_name ";
    
    public static final String COUNT_INT_ALLELE_VALUES_FOR_POLYMORPHIC_MARKERS_RETRIEVAL_BY_GIDS = 
            "SELECT COUNT(*) "
                    + "FROM gdms_allele_values INNER JOIN gdms_marker ON gdms_marker.marker_id = gdms_allele_values.marker_id " 
                    + "WHERE gdms_allele_values.gid IN (:gids) ";
    
    public static final String GET_MAPPING_ALLELE_VALUES_FOR_POLYMORPHIC_MARKERS_RETRIEVAL_BY_GIDS =     
            "SELECT gdms_mapping_pop_values.dataset_id, gdms_mapping_pop_values.gid, " 
                    + "CONCAT(gdms_marker.marker_name,''), CONCAT(gdms_mapping_pop_values.map_char_value,'') " 
            + "FROM gdms_mapping_pop_values INNER JOIN gdms_marker ON gdms_marker.marker_id = gdms_mapping_pop_values.marker_id " 
            + "WHERE gdms_mapping_pop_values.gid IN (:gids) "
            + "ORDER BY gid, marker_name ";
    
    public static final String COUNT_MAPPING_ALLELE_VALUES_FOR_POLYMORPHIC_MARKERS_RETRIEVAL_BY_GIDS = 
            "SELECT COUNT(*) " 
            + "FROM gdms_mapping_pop_values INNER JOIN gdms_marker ON gdms_marker.marker_id = gdms_mapping_pop_values.marker_id " 
            + "WHERE gdms_mapping_pop_values.gid IN (:gids) ";

    @Id
    @Basic(optional = false)
    @Column(name = "an_id")
    private Integer anId;

    /** The dataset id. */
    @Basic(optional = false)
    @Column(name = "dataset_id")
    private Integer datasetId;

    /** The germplasm id. */
    @Basic(optional = false)
    @Column(name = "gid")
    private Integer gId;
    
    /** The allele bin value. */
    @Basic(optional = false)
    @Column(name = "marker_id")
    private Integer markerId;
    
    @Basic(optional = false)
    @Column(name = "allele_bin_value")
    private String alleleBinValue;

    /** The allele raw value. */
    @Basic(optional = false)
    @Column(name = "allele_raw_value")
    private String alleleRawValue;
    
    /**
     * Instantiates a new allele values.
     */
    public AlleleValues() {
    }

    /**
     * Instantiates a new allele values.
     *
     * @param anId the anId
     * @param datasetId the dataset id
     * @param gId the germplasm id
     * @param alleleBinValue the allele bin value
     * @param alleleRawValue the allele raw value
     */
    public AlleleValues(Integer anId, Integer datasetId, Integer gId, Integer markerId, String alleleBinValue, String alleleRawValue) {
        super();
        this.anId = anId;
        this.datasetId = datasetId;
        this.gId = gId;
        this.markerId = markerId;
        this.alleleBinValue = alleleBinValue;
        this.alleleRawValue = alleleRawValue;
    }
    
    /**
     * Gets the anId.
     *
     * @return the anId
     */
    public Integer getAnId() {
        return anId;
    }
    
    /**
     * Sets the anId.
     *
     * @param anId the new anId
     */
    public void setAnId(Integer anId) {
        this.anId = anId;
    }
    
    /**
     * Gets the dataset id.
     *
     * @return the dataset id
     */
    public Integer getDatasetId() {
        return datasetId;
    }
    
    /**
     * Sets the dataset id.
     *
     * @param datasetId the new dataset id
     */
    public void setDatasetId(Integer datasetId) {
        this.datasetId = datasetId;
    }
    
    /**
     * Gets the germplasm id.
     *
     * @return the germplasm id
     */
    public Integer getgId() {
        return gId;
    }
    
    /**
     * Sets the germplasm id.
     *
     * @param gId the new germplasm id
     */
    public void setgId(Integer gId) {
        this.gId = gId;
    }

    public Integer getMarkerId() {
        return markerId;
    }
    
    public void setMarkerId(Integer markerId) {
        this.markerId = markerId;
    }

    /**
     * Gets the allele bin value.
     *
     * @return the allele bin value
     */
    public String getAlleleBinValue() {
        return alleleBinValue;
    }
    
    /**
     * Sets the allele bin value.
     *
     * @param alleleBinValue the new allele bin value
     */
    public void setAlleleBinValue(String alleleBinValue) {
        this.alleleBinValue = alleleBinValue;
    }
    
    /**
     * Gets the allele raw value.
     *
     * @return the allele raw value
     */
    public String getAlleleRawValue() {
        return alleleRawValue;
    }
    
    /**
     * Sets the allele raw value.
     *
     * @param alleleRawValue the new allele raw value
     */
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

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
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
        builder.append("]");
        return builder.toString();
    }
    
}
