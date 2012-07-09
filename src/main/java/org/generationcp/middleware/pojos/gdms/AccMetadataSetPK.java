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

@Embeddable
public class AccMetadataSetPK implements Serializable{

    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @Column(name = "dataset_id")
    private Integer datasetId;

    @Basic(optional = false)
    @Column(name = "gid")
    private Integer germplasmId;

    @Basic(optional = false)
    @Column(name = "nid")
    private Integer nameId;

    public AccMetadataSetPK() {
    }

    public AccMetadataSetPK(Integer datasetId, Integer germplasmId, Integer nameId) {
        this.datasetId = datasetId;
        this.germplasmId = germplasmId;
        this.nameId = nameId;
    }

    public Integer getDatasetId() {
        return datasetId;
    }
    
    public void setDatasetId(Integer datasetId) {
        this.datasetId = datasetId;
    }
    
    public Integer getGermplasmId() {
        return germplasmId;
    }

    public void setGermplasmId(Integer germplasmId) {
        this.germplasmId = germplasmId;
    }

    public Integer getNameId() {
        return nameId;
    }

    public void setNameId(Integer nameId) {
        this.nameId = nameId;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(11, 71).append(datasetId).append(germplasmId).append(nameId).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AccMetadataSetPK)) {
            return false;
        }

        AccMetadataSetPK rhs = (AccMetadataSetPK) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(datasetId, rhs.datasetId).append(germplasmId, rhs.germplasmId)
                .append(nameId, rhs.nameId).isEquals();
    }

    @Override
    public String toString() {
        return "AccMetadataSetPK [datasetId=" + datasetId + ", germplasmId=" + germplasmId +  ", nameId=" + nameId + "]";
    }

}
