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

package org.generationcp.middleware.pojos;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * POJO for acc_metadataset table
 * 
 */
@Entity
@Table(name = "acc_metadataset")
public class AccMetadataSet implements Serializable{

    private static final long serialVersionUID = 1L;
    
    public static final String GET_NAME_IDS_BY_GERMPLASM_IDS = "select nid from acc_metadataset where gid in (:gIdList)";


    @Id
    @Basic(optional = false)
    @Column(name = "dataset_id")
    private Integer datasetId;

    @Basic(optional = false)
    @Column(name = "gid")
    private Integer germplasmId;

    @Basic(optional = false)
    @Column(name = "nid")
    private Integer nameId;

    public AccMetadataSet() {
    }
    public AccMetadataSet(Integer datasetId, Integer nameId, Integer germplasmId) {
        super();
        this.datasetId = datasetId;
        this.nameId = nameId;
        this.germplasmId = germplasmId;
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
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof AccMetadataSet) {
            AccMetadataSet param = (AccMetadataSet) obj;
            if (this.getDatasetId() == param.getDatasetId() && this.germplasmId == param.getGermplasmId() && this.getNameId() == param.getNameId()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.getNameId();
    }

    @Override
    public String toString() {
        return "AccMetaDataSet [datasetId=" + datasetId + ", nameId=" + nameId + ", germplasmId=" + germplasmId + "]";
    }

}
