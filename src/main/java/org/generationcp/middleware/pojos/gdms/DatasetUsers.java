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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.io.Serializable;

/**
 * POJO for gdms_dataset_users table.
 * 
 * @author Dennis Billano
 */
@Entity
@Table(name = "gdms_dataset_users")
public class DatasetUsers implements Serializable{

    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @Column(name = "dataset_id")
    private Integer datasetId;

    @Basic(optional = false)
    @Column(name = "user_id")
    private Integer userId;

    public DatasetUsers() {
    }

    public DatasetUsers(Integer datasetId,
                    Integer userId) {
        this.datasetId = datasetId;
        this.userId = userId;
    }
    
    public Integer getDatasetId() {
        return datasetId;
    }
    
    public void setDatasetId(Integer datasetId) {
        this.datasetId = datasetId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setAlias(Integer userId) {
        this.userId = userId;
    }

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
