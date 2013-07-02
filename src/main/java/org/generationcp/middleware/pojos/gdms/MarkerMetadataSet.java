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

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * POJO for gdms_marker_metadataset table.
 *
 * @author Joyce Avestro
 */
@Entity
@Table(name = "gdms_marker_metadataset")
public class MarkerMetadataSet implements Serializable{

    private static final long serialVersionUID = 1L;

    public static final String GET_MARKER_ID_BY_DATASET_ID = 
            "SELECT marker_id " +
            "FROM gdms_marker_metadataset " +
            "WHERE dataset_id = :datasetId " +
            "ORDER BY marker_id;";

    public static final String GET_MARKERS_BY_GID_AND_DATASETS = 
            "SELECT DISTINCT marker_id " + 
            "FROM gdms_marker_metadataset JOIN gdms_acc_metadataset " +
            "        ON gdms_marker_metadataset.dataset_id = gdms_acc_metadataset.dataset_id " + 
            "WHERE gdms_marker_metadataset.dataset_id in (:datasetids)  " +
            "    AND gdms_acc_metadataset.gid = :gid " + 
            "ORDER BY gdms_marker_metadataset.marker_id ";
    
    public static final String COUNT_MARKERS_BY_GID_AND_DATASETS = 
            "SELECT COUNT(DISTINCT marker_id) " + 
            "FROM gdms_marker_metadataset JOIN gdms_acc_metadataset " +
            "        ON gdms_marker_metadataset.dataset_id = gdms_acc_metadataset.dataset_id " + 
            "WHERE gdms_marker_metadataset.dataset_id in (:datasetids)  " +
            "    AND gdms_acc_metadataset.gid = :gid " + 
            "ORDER BY gdms_marker_metadataset.marker_id ";

    @EmbeddedId
    protected MarkerMetadataSetPK id;

    public MarkerMetadataSet() {
    }

    public MarkerMetadataSet(MarkerMetadataSetPK id) {
        this.id = id;
    }
        
    public MarkerMetadataSet(Integer datasetId, Integer markerId) {
        this.id = new MarkerMetadataSetPK(datasetId, markerId);
    }
    
    public MarkerMetadataSetPK getId() {
        return id;
    }

    public void setId(MarkerMetadataSetPK id) {
        this.id = id;
    }

    public Integer getDatasetId() {
        return id.getDatasetId();
    }
    
    public void setDatasetId(Integer datasetId) {
        id.setDatasetId(datasetId);
    }
    
    public Integer getMarkerId() {
        return id.getMarkerId();
    }
    
    public void setMarkerId(Integer markerId) {
        id.setMarkerId(markerId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MarkerMetadataSet)) {
            return false;
        }

        MarkerMetadataSet rhs = (MarkerMetadataSet) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(id, rhs.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(37, 101).append(id).toHashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MarkerMetadataSet [id=");
        builder.append(id);
        builder.append("]");
        return builder.toString();
    }

}
