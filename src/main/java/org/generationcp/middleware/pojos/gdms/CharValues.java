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
@Table(name = "gdms_char_values")
public class CharValues implements Serializable{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    // For getMarkerNamesByGIds()
    public static final String GET_CHAR_COUNT_BY_GID = 
            "SELECT COUNT(*) " +
            "FROM gdms_char_values " +
            "WHERE gid IN (:gIdList)";

    // For getGermplasmNamesByMarkerNames()
    public static final String GET_CHAR_COUNT_BY_MARKER_ID = 
            "SELECT COUNT(*) " +
            "FROM gdms_char_values " +
            "WHERE marker_id IN (:markerIdList)";
    
    // For getGermplasmNamesByMarkerNames()
    public static final String GET_CHAR_GERMPLASM_NAME_AND_MARKER_NAME_BY_MARKER_NAMES = 
            "SELECT n.nval, CONCAT(m.marker_name, '') " +  
            "FROM names n JOIN gdms_char_values c ON n.gid = c.gid " +  
            "           JOIN gdms_marker m ON c.marker_id = m.marker_id " +
            "WHERE marker_name IN (:markerNameList) AND n.nstat = 1 " +
            "ORDER BY n.nval, m.marker_name";
    
    // For getAllelicValues by gid and marker names
    public static final String GET_ALLELIC_VALUES_BY_GIDS_AND_MARKER_NAMES =
            "SELECT DISTINCT " +
                "gdms_char_values.gid, " +
                "CONCAT(gdms_char_values.char_value, ''), " +
                "CONCAT(gdms_marker.marker_name, '') " +
            "FROM gdms_char_values, " +
                "gdms_marker " +
            "WHERE gdms_char_values.marker_id = gdms_marker.marker_id " +
                "AND gdms_char_values.gid IN (:gidList) " +
                "AND gdms_char_values.marker_id IN (:markerIdList) " +
            "ORDER BY gdms_char_values.gid DESC, gdms_marker.marker_name";
    
    // For getAllelicValues by datasetId
    public static final String GET_ALLELIC_VALUES_BY_DATASET_ID = 
            "SELECT gid, marker_id,  CONCAT(char_value, '') " +
            "FROM gdms_char_values " +
            "WHERE dataset_id = :datasetId " +
            "ORDER BY gid DESC, marker_id ASC";

    public static final String COUNT_BY_DATASET_ID = 
            "SELECT COUNT(*) " +
            "FROM gdms_char_values " +
            "WHERE dataset_id = :datasetId";
    
    public static final String GET_GIDS_BY_MARKER_ID = 
            "SELECT DISTINCT gid " +
            "FROM gdms_char_values " +
            "WHERE marker_id = :markerId";
    
    public static final String COUNT_GIDS_BY_MARKER_ID = 
            "SELECT COUNT(distinct gid) " +
            "FROM gdms_char_values " +
            "WHERE marker_id = :markerId";

    /** The ac id. */
    @Id
    @Basic(optional = false)
    @Column(name = "ac_id")
    private Integer acId;
    
    /** The dataset id. */
    @Basic(optional = false)
    @Column(name = "dataset_id")
    private Integer datasetId;

    /** The marker id. */
    @Basic(optional = false)
    @Column(name = "marker_id")
    private Integer markerId;

    /** The g id. */
    @Basic(optional = false)
    @Column(name = "gid")
    private Integer gId;
    
    /** The char value. */
    @Column(name = "char_value")
    String charValue;
    
    /**
     * Instantiates a new char values.
     */
    public CharValues() {
    }

    /**
     * Instantiates a new char values.
     *
     * @param acId the ac id
     * @param datasetId the dataset id
     * @param markerId the marker id
     * @param gId the g id
     * @param charValue the char value
     */
    public CharValues(Integer acId, Integer datasetId, Integer markerId, Integer gId, String charValue) {
        super();
        this.acId = acId;
        this.datasetId = datasetId;
        this.markerId = markerId;
        this.gId = gId;
        this.charValue = charValue;
    }
    
    /**
     * Gets the ac id.
     *
     * @return the ac id
     */
    public Integer getAcId() {
        return acId;
    }
    
    /**
     * Sets the ac id.
     *
     * @param acId the new ac id
     */
    public void setAcId(Integer acId) {
        this.acId = acId;
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
     * Gets the marker id.
     *
     * @return the marker id
     */
    public Integer getMarkerId() {
        return markerId;
    }
    
    /**
     * Sets the marker id.
     *
     * @param markerId the new marker id
     */
    public void setMarkerId(Integer markerId) {
        this.markerId = markerId;
    }
    
    /**
     * Gets the g id.
     *
     * @return the g id
     */
    public Integer getgId() {
        return gId;
    }
    
    /**
     * Sets the g id.
     *
     * @param gId the new g id
     */
    public void setgId(Integer gId) {
        this.gId = gId;
    }
    
    /**
     * Gets the char value.
     *
     * @return the char value
     */
    public String getCharValue() {
        return charValue;
    }
    
    /**
     * Sets the char value.
     *
     * @param charValue the new char value
     */
    public void setCharValue(String charValue) {
        this.charValue = charValue;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(3, 139).append(acId).toHashCode();
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
        if (!(obj instanceof CharValues)) {
            return false;
        }

        CharValues rhs = (CharValues) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(acId, rhs.acId).isEquals();
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
        builder.append("]");
        return builder.toString();
    }

}
