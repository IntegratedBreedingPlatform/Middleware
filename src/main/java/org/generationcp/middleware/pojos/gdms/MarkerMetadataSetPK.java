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
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * The primary identifier of {@link MarkerMetadataSet}.
 * 
 * @author Joyce Avestro
 */
@Embeddable
public class MarkerMetadataSetPK implements Serializable{

    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @Column(name = "dataset_id")
    private Integer datasetId;

    @Basic(optional = false)
    @Column(name = "marker_id")
    private Integer markerId;

    public MarkerMetadataSetPK() {
    }

    public MarkerMetadataSetPK(Integer datasetId, Integer markerId) {
        this.datasetId = datasetId;
        this.markerId = markerId;
    }
        
    public Integer getDatasetId() {
        return datasetId;
    }
    
    public void setDatasetId(Integer datasetId) {
        this.datasetId = datasetId;
    }
    
    public Integer getMarkerId() {
        return markerId;
    }
    
    public void setMarkerId(Integer markerId) {
        this.markerId = markerId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MarkerMetadataSetPK)) {
            return false;
        }

        MarkerMetadataSetPK rhs = (MarkerMetadataSetPK) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(datasetId, rhs.datasetId).append(markerId, rhs.markerId).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(37, 83).append(datasetId).append(markerId).toHashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MarkerMetadataSetPK [datasetId=");
        builder.append(datasetId);
        builder.append(", markerId=");
        builder.append(markerId);
        builder.append("]");
        return builder.toString();
    }

}
