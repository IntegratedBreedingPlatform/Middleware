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
import java.util.Date;

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
@Table(name = "gdms_dataset")
public class Dataset implements Serializable{

    private static final long serialVersionUID = 1L;

    public static final String COUNT_BY_NAME = 
            "SELECT COUNT(dataset_name) " +
            "FROM gdms_dataset " +
            "WHERE dataset_type != 'QTL' ";

    public static final String GET_DATASET_NAMES_NOT_QTL = 
            "SELECT CONCAT(dataset_name, '') " +
            "FROM gdms_dataset " +
            "WHERE dataset_type != 'QTL' ";

    public static final String GET_DATASET_ID_NOT_MAPPING_AND_NOT_QTL = 
            "SELECT dataset_id " +
            "FROM gdms_dataset " +
            "WHERE dataset_type != 'mapping' " +
            "AND dataset_type != 'QTL' ";

    public static final String COUNT_DATASET_ID_NOT_MAPPING_AND_NOT_QTL = 
            "SELECT COUNT(dataset_id) " +
            "FROM gdms_dataset " +
            "WHERE dataset_type != 'mapping' " +
            "AND dataset_type != 'QTL' ";

    public static final String GET_DATASET_ID_BY_MAPPING_AND_NOT_QTL = 
            "SELECT dataset_id " +
            "FROM gdms_dataset " +
            "WHERE dataset_type = 'mapping' " +
            "AND dataset_type != 'QTL' ";
    

    public static final String COUNT_DATASET_ID_BY_MAPPING_AND_NOT_QTL = 
            "SELECT COUNT(dataset_id) " +
            "FROM gdms_dataset " +
            "WHERE dataset_type = 'mapping' " +
            "AND dataset_type != 'QTL' ";

    public static final String GET_DETAILS_BY_NAME = 
            "SELECT dataset_id, CONCAT(dataset_type, '') " +
            "FROM gdms_dataset " +
            "WHERE dataset_name = :datasetName";
    
    /** The dataset id. */
    @Id
    @Basic(optional = false)
    @Column(name = "dataset_id")
    private Integer datasetId;

    @Basic(optional = false)
    @Column(name = "dataset_name", columnDefinition = "char(30)")
    private String datasetName;

    @Column(name = "dataset_desc")
    private String datasetDesc;

    @Basic(optional = false)
    @Column(name = "dataset_type", columnDefinition = "char(10)")
    private String datasetType;
    
    @Basic(optional = false)
    @Column(name = "genus", columnDefinition = "char(25)")
    private String genus;
    
    @Column(name = "species", columnDefinition = "char(25)")
    private String species;
    
    @Column(name = "upload_template_date")
    private Date uploadTemplateDate;
    
    @Column(name = "remarks")
    private String remarks;
    
    @Basic(optional = false)
    @Column(name = "datatype")
    private String dataType;
    
    @Column(name = "missing_data")
    private String missingData;
    
    @Column(name = "method")
    private String method;
    
    @Column(name = "score")
    private String score;
    
    public Dataset() {
    }

    public Dataset(Integer datasetId,
            String datasetName,
            String datasetDesc,
            String datasetType,
            String genus, 
            String species, 
            Date uploadTemplateDate, 
            String remarks, 
            String dataType, 
            String missingData,
            String method,
            String score) {
        
        this.datasetId = datasetId;
        this.datasetName = datasetName;
        this.datasetDesc = datasetDesc;
        this.datasetType = datasetType;
        this.genus = genus;
        this.species = species;
        this.uploadTemplateDate = uploadTemplateDate;
        this.remarks = remarks;
        this.dataType = dataType;
        this.missingData = missingData;
        this.method = method;
        this.score = score;
    }

    public Integer getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(Integer datasetId) {
        this.datasetId = datasetId;
    }
    
    public String getDatasetName() {
        return datasetName;
    }

    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    public String getDatasetDesc() {
        return datasetDesc;
    }
    
    public void setDatasetDesc(String datasetDesc) {
        this.datasetDesc = datasetDesc;
    }
    
    public String getDatasetType() {
        return datasetType;
    }
    
    public void setDatasetType(String datasetType) {
        this.datasetType = datasetType;
    }
    
    public String getGenus() {
        return genus;
    }
    
    public void setGenus(String genus) {
        this.genus = genus;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public Date getUploadTemplateDate() {
        return uploadTemplateDate;
    }
    
    public void setUploadTemplateDate(Date uploadTemplateDate) {
        this.uploadTemplateDate = uploadTemplateDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getDataType() {
        return dataType;
    }
    
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    
    public String getMissingData() {
        return missingData;
    }

    public void setMissingData(String missingData) {
        this.missingData = missingData;
    }
    
    public String getMethod() {
        return method;
    }
    
    public void setMethod(String method) {
        this.method = method;
    }
    
    public String getScore() {
        return score;
    }
    
    public void setScore(String score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Dataset)) {
            return false;
        }

        Dataset rhs = (Dataset) obj;
        return new EqualsBuilder().append(datasetId, rhs.datasetId).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(37, 53).append(datasetId).toHashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Dataset [datasetId=");
        builder.append(datasetId);
        builder.append(", datasetName=");
        builder.append(datasetName);
        builder.append(", datasetDesc=");
        builder.append(datasetDesc);
        builder.append(", datasetType=");
        builder.append(datasetType);
        builder.append(", genus=");
        builder.append(genus);
        builder.append(", species=");
        builder.append(species);
        builder.append(", uploadTemplateDate=");
        builder.append(uploadTemplateDate);
        builder.append(", remarks=");
        builder.append(remarks);
        builder.append(", dataType=");
        builder.append(dataType);
        builder.append(", missingData=");
        builder.append(missingData);
        builder.append(", method=");
        builder.append(method);
        builder.append(", score=");
        builder.append(score);
        builder.append("]");
        return builder.toString();
    }

}
