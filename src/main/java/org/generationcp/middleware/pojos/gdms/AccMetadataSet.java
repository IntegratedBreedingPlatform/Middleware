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
 * POJO for acc_metadataset table
 * 
 * @author Joyce Avestro
 *  
 */
@Entity
@Table(name = "gdms_acc_metadataset")
public class AccMetadataSet implements Serializable{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The Constant GET_NAME_IDS_BY_GERMPLASM_IDS. */
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

    /** The id. */
    @EmbeddedId
    protected AccMetadataSetPK id;

    /**
     * Instantiates a new acc metadata set.
     */
    public AccMetadataSet() {
    }

    /**
     * Instantiates a new acc metadata set.
     *
     * @param id the id
     */
    public AccMetadataSet(AccMetadataSetPK id) {
        this.id = id;
    }
        
    /**
     * Gets the id.
     *
     * @return the id
     */
    public AccMetadataSetPK getId() {
        return id;
    }
    
    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(AccMetadataSetPK id) {
        this.id = id;
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
        if (!(obj instanceof AccMetadataSet)) {
            return false;
        }

        AccMetadataSet rhs = (AccMetadataSet) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(id, rhs.id).isEquals();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
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
