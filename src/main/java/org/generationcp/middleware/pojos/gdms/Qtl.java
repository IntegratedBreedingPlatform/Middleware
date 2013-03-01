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
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * POJO for dataset table
 * 
 * @author Joyce Avestro
 * 
 */
@Entity
@Table(name = "gdms_qtl")
public class Qtl implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "qtl_id")
    private Integer qtlId;

    @Basic(optional = false)
    @Column(name = "qtl_name")
    private String qtlName;

    @Column(name = "dataset_id")
    private Integer datasetId;

    public Qtl() {
    }

    public Qtl(Integer qtlId,
            String qtlName,
            Integer datasetId) {
        
        this.qtlId = qtlId;
        this.qtlName = qtlName;
        this.datasetId = datasetId;
    }


    public Integer getQtlId() {
        return qtlId;
    }

    public void setQtlId(Integer qtlId) {
        this.qtlId = qtlId;
    }

    public String getQtlName() {
        return qtlName;
    }

    public void setQtlName(String qtlName) {
        this.qtlName = qtlName;
    }

    public Integer getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(Integer datasetId) {
        this.datasetId = datasetId;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Qtl)) {
            return false;
        }

        Qtl rhs = (Qtl) obj;
        return new EqualsBuilder().append(qtlId, rhs.qtlId).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(37, 53).append(qtlId).toHashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Qtl [qtlId=");
        builder.append(qtlId);
        builder.append(", qtlName=");
        builder.append(qtlName);
        builder.append(", datasetId=");
        builder.append(datasetId);
        builder.append("]");
        return builder.toString();
    }
}
