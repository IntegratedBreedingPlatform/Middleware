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
import java.util.Comparator;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * <b>Description</b>: Marker Alias POJO
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Dennis Billano <br>
 * <b>File Created</b>: March 7, 2013
 */
@Entity
@Table(name = "gdms_dataset_users")

public class DatasetUsers implements Serializable{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The dataset id. */
    @Id
    @Basic(optional = false)
    @Column(name = "dataset_id")
    private Integer datasetId;

    /** The marker type. */
    @Basic(optional = false)
    @Column(name = "user_id")
    private Integer userId;

    
    /**
     * Instantiates a new marker alias.
     */
    public DatasetUsers() {
    }

    /**
     * Instantiates a new marker alias.
     *
     * @param markerId the marker id
     * @param userId the user id
     */
    public DatasetUsers(Integer datasetId,
                    Integer userId) {
        
        this.datasetId = datasetId;
        this.userId = userId;
    }
    
    /**
     * Gets the marker id.
     *
     * @return the marker id
     */
    public Integer getDatasetId() {
        return datasetId;
    }
    
    /**
     * Sets the marker id.
     *
     * @param markerId the new marker id
     */
    public void setDatasetId(Integer datasetId) {
        this.datasetId = datasetId;
    }

    /**
     * Gets the user ID.
     *
     * @return the user ID
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * Sets the user ID.
     *
     * @param userId the user ID
     */
    public void setAlias(Integer userId) {
        this.userId = userId;
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
        if (!(obj instanceof DatasetUsers)) {
            return false;
        }

        DatasetUsers rhs = (DatasetUsers) obj;
        return new EqualsBuilder().append(datasetId, rhs.datasetId).isEquals();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(61, 131).append(datasetId).toHashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DatasetUser [datasetId=");
        builder.append(datasetId);
        builder.append(", userId=");
        builder.append(userId);
        builder.append("]");
        return builder.toString();
    }

}
