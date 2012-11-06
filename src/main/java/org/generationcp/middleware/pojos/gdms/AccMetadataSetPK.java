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
 * The Class AccMetadataSetPK.
 * 
 * @author Joyce Avestro
 * 
 */
@Embeddable
public class AccMetadataSetPK implements Serializable{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The dataset id. */
    @Basic(optional = false)
    @Column(name = "dataset_id")
    private Integer datasetId;

    /** The germplasm id. */
    @Basic(optional = false)
    @Column(name = "gid")
    private Integer germplasmId;

    /** The name id. */
    @Basic(optional = false)
    @Column(name = "nid")
    private Integer nameId;

    /**
     * Instantiates a new AccMetadataSetPK.
     */
    public AccMetadataSetPK() {
    }

    /**
     * Instantiates a new AccMetadataSetPK.
     *
     * @param datasetId the dataset id
     * @param germplasmId the germplasm id
     * @param nameId the name id
     */
    public AccMetadataSetPK(Integer datasetId, Integer germplasmId, Integer nameId) {
        this.datasetId = datasetId;
        this.germplasmId = germplasmId;
        this.nameId = nameId;
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
     * Gets the germplasm id.
     *
     * @return the germplasm id
     */
    public Integer getGermplasmId() {
        return germplasmId;
    }

    /**
     * Sets the germplasm id.
     *
     * @param germplasmId the new germplasm id
     */
    public void setGermplasmId(Integer germplasmId) {
        this.germplasmId = germplasmId;
    }

    /**
     * Gets the name id.
     *
     * @return the name id
     */
    public Integer getNameId() {
        return nameId;
    }

    /**
     * Sets the name id.
     *
     * @param nameId the new name id
     */
    public void setNameId(Integer nameId) {
        this.nameId = nameId;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(11, 71).append(datasetId).append(germplasmId).append(nameId).toHashCode();
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
        if (!(obj instanceof AccMetadataSetPK)) {
            return false;
        }

        AccMetadataSetPK rhs = (AccMetadataSetPK) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(datasetId, rhs.datasetId).append(germplasmId, rhs.germplasmId)
                .append(nameId, rhs.nameId).isEquals();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AccMetadataSetPK [datasetId=");
        builder.append(datasetId);
        builder.append(", germplasmId=");
        builder.append(germplasmId);
        builder.append(", nameId=");
        builder.append(nameId);
        builder.append("]");
        return builder.toString();
    }

}
