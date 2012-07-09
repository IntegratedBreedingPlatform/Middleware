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
 */
@Entity
@Table(name = "acc_metadataset")
public class AccMetadataSet implements Serializable{

    private static final long serialVersionUID = 1L;
    
    public static final String GET_NAME_IDS_BY_GERMPLASM_IDS = "select nid from acc_metadataset where gid in (:gIdList)";

    @EmbeddedId
    protected AccMetadataSetPK id;

    public AccMetadataSet() {
    }

    public AccMetadataSet(AccMetadataSetPK id) {
        this.id = id;
    }
        
    public AccMetadataSetPK getId() {
        return id;
    }
    
    public void setId(AccMetadataSetPK id) {
        this.id = id;
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
        return "AccMetaDataSet [id=" + id + "]";
    }

}
