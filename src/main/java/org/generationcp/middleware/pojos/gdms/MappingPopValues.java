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
 * <b>Description</b>: MappingPopValues POJO
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Mark Agarrado <br>
 * <b>File Created</b>: Jul 11, 2012
 */
@Entity
@Table(name = "mapping_pop_values")
public class MappingPopValues implements Serializable{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    // For getMarkerNamesByGIds()
    public static final String GET_MAPPING_COUNT_BY_GID = "select count(*) from mapping_pop_values where gid in (:gIdList)";

    // For getGermplasmNamesByMarkerNames()
    public static final String GET_MAPPING_COUNT_BY_MARKER_ID = "select count(*) from mapping_pop_values where marker_id in (:markerIdList)";

    // For getGermplasmNamesByMarkerNames()
    public static final String GET_MAPPING_GERMPLASM_NAME_AND_MARKER_NAME_BY_MARKER_NAMES = 
            "select n.nval, concat(m.marker_name, '') " +  
            "from names n join mapping_pop_values mp on n.gid = mp.gid " +  
            "           join marker m on mp.marker_id = m.marker_id " +
            "where marker_name in (:markerNameList) and n.nstat = 1 " +
            "order by n.nval, m.marker_name";
    
    // For getAllelicValues by gid and marker names
    public static final String GET_ALLELIC_VALUES_BY_GIDS_AND_MARKER_NAMES =
        "SELECT DISTINCT " +
            "mapping_pop_values.gid, " +
            "concat(mapping_pop_values.map_char_value, ''), " +
            "concat(marker.marker_name, '') " +
        "FROM mapping_pop_values, " +
            "marker " +
        "WHERE mapping_pop_values.marker_id=marker.marker_id " +
            "AND mapping_pop_values.gid IN (:gidList) " +
            "AND mapping_pop_values.marker_id IN (:markerIdList) " +
        "ORDER BY mapping_pop_values.gid DESC, marker.marker_name";

    /**
     * The Mp Id.
     */
    @Id
    @Basic(optional = false)
    @Column(name = "mp_id")
    private Integer mpId;
    
    /**
     * The Map Char Value.
     */
    @Column(name = "map_char_value")
    private String mapCharValue;
    
    /**
     * The Dataset Id.
     */
    @Column(name = "dataset_id")
    private Integer datasetId;
    
    /**
     * The GID.
     */
    @Column(name = "gid")
    private Integer gid;
    
    /**
     * The Marker Id.
     */
    @Column(name = "marker_id")
    private Integer markerId;
    
    /**
     * Instantiates a new MappingPopValues object.
     */
    public MappingPopValues() {
        
    }

    /**
     * Instantiates a new MappingPopValues object.
     * 
     * @param mpId
     * @param mapCharValue
     * @param datasetId
     * @param gid
     * @param markerId
     */
    public MappingPopValues(Integer mpId,
                            String mapCharValue,
                            Integer datasetId,
                            Integer gid,
                            Integer markerId) {
        
        this.mpId = mpId;
        this.mapCharValue = mapCharValue;
        this.datasetId = datasetId;
        this.gid = gid;
        this.markerId = markerId;
    }

    
    /**
     * Gets the Mp Id.
     * 
     * @return the mpId
     */
    public Integer getMpId() {
        return mpId;
    }

    
    /**
     * Sets the Mp Id.
     * 
     * @param mpId the mpId to set
     */
    public void setMpId(Integer mpId) {
        this.mpId = mpId;
    }

    
    /**
     * Gets the Map Char Value.
     * 
     * @return the mapCharValue
     */
    public String getMapCharValue() {
        return mapCharValue;
    }

    
    /**
     * Sets the Map Char Value.
     * 
     * @param mapCharValue the mapCharValue to set
     */
    public void setMapCharValue(String mapCharValue) {
        this.mapCharValue = mapCharValue;
    }

    
    /**
     * Gets the Dataset Id.
     * 
     * @return the datasetId
     */
    public Integer getDatasetId() {
        return datasetId;
    }

    
    /**
     * Sets the Dataset Id.
     * 
     * @param datasetId the datasetId to set
     */
    public void setDatasetId(Integer datasetId) {
        this.datasetId = datasetId;
    }

    
    /**
     * Gets the GID.
     * 
     * @return the gid
     */
    public Integer getGid() {
        return gid;
    }

    
    /**
     * Sets the GID.
     * 
     * @param gid the gid to set
     */
    public void setGid(Integer gid) {
        this.gid = gid;
    }

    
    /**
     * Gets the Marker Id.
     * 
     * @return the markerId
     */
    public Integer getMarkerId() {
        return markerId;
    }

    
    /**
     * Sets the Marker Id.
     * 
     * @param markerId the markerId to set
     */
    public void setMarkerId(Integer markerId) {
        this.markerId = markerId;
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
        if (!(obj instanceof MappingPopValues)) {
            return false;
        }

        MappingPopValues rhs = (MappingPopValues) obj;
        return new EqualsBuilder().append(mpId, rhs.mpId).isEquals();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(markerId).toHashCode();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "MappingPopValues [mpId=" + mpId +
                        ", mapCharValue=" + mapCharValue +
                        ", datasetId=" + datasetId +
                        ", gid=" + gid +
                        ", markerId=" + markerId +"]";
    }
    
}
