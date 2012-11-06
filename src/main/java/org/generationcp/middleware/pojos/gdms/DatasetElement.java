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
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * 
 * @author Joyce Avestro
 * 
 */
public class DatasetElement implements Serializable{

    private static final long serialVersionUID = 1L;
    
    private Integer datasetId;
    private String datasetType;

    public DatasetElement() {
    }

    public DatasetElement(Integer datasetId, String datasetType) {        
        this.datasetId = datasetId;
        this.datasetType = datasetType;
    }

    public Integer getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(Integer datasetId) {
        this.datasetId = datasetId;
    }
    
    public String getDatasetType() {
        return datasetType;
    }
    
    public void setDatasetType(String datasetType) {
        this.datasetType = datasetType;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DatasetElement)) {
            return false;
        }

        DatasetElement rhs = (DatasetElement) obj;
        return new EqualsBuilder().append(datasetId, rhs.datasetId).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(53, 71).append(datasetId).toHashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DatasetElement [datasetId=");
        builder.append(datasetId);
        builder.append(", datasetType=");
        builder.append(datasetType);
        builder.append("]");
        return builder.toString();
    }

}
