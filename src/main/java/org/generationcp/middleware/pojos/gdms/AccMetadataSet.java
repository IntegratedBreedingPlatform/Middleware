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
 * POJO for acc_metadataset table.
 * 
 * @author Joyce Avestro
 *  
 */
@Entity
@Table(name = "gdms_acc_metadataset")
public class AccMetadataSet implements Serializable{

    private static final long serialVersionUID = 1L;
    
    public static final String GET_NAME_IDS_BY_GERMPLASM_IDS = 
            "SELECT nid " +
            "FROM gdms_acc_metadataset " +
            "WHERE gid IN (:gIdList)";
    
    public static final String GET_NIDS_BY_DATASET_IDS = 
            "SELECT DISTINCT(nid) " +
            "FROM gdms_acc_metadataset " +
            "WHERE dataset_id IN (:datasetId) ";
    
    public static final String GET_NIDS_BY_DATASET_IDS_FILTER_BY_GIDS = 
            "AND gid NOT IN (:gids)";

    public static final String GET_ACC_METADATASETS_BY_GIDS = 
            "SELECT gid, nid, dataset_id " +
            "FROM gdms_acc_metadataset " +
            "WHERE gid IN (:gids) ";
    
    public static final String GET_ACC_METADATASETS_BY_DATASET_ID_AND_IN_GIDS = 
        "SELECT gid, nid " +
        "FROM gdms_acc_metadataset " +
        "WHERE gid IN (:gids) " +
        "AND dataset_id = :datasetId";
    
    public static final String GET_ACC_METADATASETS_BY_DATASET_ID_AND_NOT_IN_GIDS = 
        "SELECT gid, nid " +
        "FROM gdms_acc_metadataset " +
        "WHERE gid NOT IN (:gids) " +
        "AND dataset_id = :datasetId";
    
    public static final String COUNT_ACC_METADATASETS_BY_GIDS = 
            "SELECT COUNT(*) " +
            "FROM gdms_acc_metadataset " +
            "WHERE gid IN (:gids) ";
    
    public static final String GET_NIDS_BY_DATASET_IDS_AND_MARKER_IDS_AND_NOT_GIDS_SELECT = 
            "SELECT DISTINCT nid " ;
    
    public static final String COUNT_NIDS_BY_DATASET_IDS_AND_MARKER_IDS_AND_NOT_GIDS_SELECT = 
            "SELECT COUNT(DISTINCT nid) " ;
    
    public static final String GET_NIDS_BY_DATASET_IDS_AND_MARKER_IDS_AND_NOT_GIDS_FROM = 
        "FROM gdms_acc_metadataset gam "+
        "INNER JOIN gdms_marker_metadataset gmm on gmm.dataset_id = gam.dataset_id " + 
        "WHERE gam.dataset_id IN (:represnos) " ;
    
    public static final String GET_NIDS_BY_DATASET_IDS_FILTER_BY_MARKER_IDS = 
            "AND gmm.marker_id IN (:markerids) " ;
    
    public static final String GET_NIDS_BY_DATASET_IDS_FILTER_NOT_BY_GIDS = 
            "AND gam.gid NOT IN (:gids) " ; 
  
    public static final String GET_NIDS_BY_DATASET_IDS_ORDER = 
    		"ORDER BY nid DESC";

    public static final String GET_NIDS_BY_DATASET_IDS_AND_MARKER_IDS = 
            "SELECT DISTINCT nid from gdms_acc_metadataset gam "+
            "INNER JOIN gdms_marker_metadataset gmm on gmm.dataset_id = gam.dataset_id " + 
            "WHERE gam.dataset_id IN (:represnos) " ;
    
    public static final String COUNT_NIDS_BY_DATASET_IDS = 
    		"SELECT COUNT(DISTINCT nid) FROM gdms_acc_metadataset WHERE dataset_id IN (:datasetIds)";

    @EmbeddedId
    protected AccMetadataSetPK id;

    public AccMetadataSet() {
        this.id = new AccMetadataSetPK();
    }

    public AccMetadataSet(AccMetadataSetPK id) {
        this.id = id;
    }
        
    public AccMetadataSet(Integer datasetId, Integer germplasmId, Integer nameId) {
        this.id = new AccMetadataSetPK(datasetId, germplasmId, nameId);
    }

    public AccMetadataSetPK getId() {
        return id;
    }
    
    public void setId(AccMetadataSetPK id) {
        this.id = id;
    }
    
    public Integer getDatasetId(){
        return id.getDatasetId();
    }

    public void setDatasetId(Integer datasetId){
        id.setDatasetId(datasetId);
    }

    public Integer getGermplasmId(){
        return id.getGermplasmId();
    }

    public void setGermplasmId(Integer germplasmId){
        id.setGermplasmId(germplasmId);
    }
    
    public Integer getNameId(){
        return id.getNameId();
    }
    
    public void setNameId(Integer nameId){
        id.setNameId(nameId);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AccMetadataSet)) {
            return false;
        }

        AccMetadataSet rhs = (AccMetadataSet) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(id, rhs.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AccMetadataSet [id=");
        builder.append(id);
        builder.append("]");
        return builder.toString();
    }

}
