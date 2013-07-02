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

/**
 * POJO for dataset table.
 * 
 * @author Joyce Avestro
 * 
 */
@Entity
@Table(name = "gdms_dataset")
public class Dataset implements Serializable{

    private static final long serialVersionUID = 1L;

    public static final String COUNT_BY_NAME = 
            "SELECT COUNT(dataset_name) " 
    		+ "FROM gdms_dataset " 
    		+ "WHERE dataset_type != 'QTL' "
            ;

    public static final String GET_DATASET_NAMES_NOT_QTL = 
            "SELECT CONCAT(dataset_name, '') " 
    		+ "FROM gdms_dataset "
            + "WHERE dataset_type != 'QTL' "
    		;

    public static final String GET_DATASET_ID_NOT_MAPPING_AND_NOT_QTL = 
            "SELECT dataset_id " 
    		+ "FROM gdms_dataset " 
    		+ "WHERE dataset_type != 'mapping' " 
    		+ "AND dataset_type != 'QTL' "
            ;

    public static final String COUNT_DATASET_ID_NOT_MAPPING_AND_NOT_QTL = 
            "SELECT COUNT(dataset_id) " 
    		+ "FROM gdms_dataset " 
    		+ "WHERE dataset_type != 'mapping' " 
            + "AND dataset_type != 'QTL' "
            ;

    public static final String GET_DATASET_ID_BY_MAPPING_AND_NOT_QTL = 
            "SELECT dataset_id "
    		+ "FROM gdms_dataset "
            + "WHERE dataset_type = 'mapping' "
            + "AND dataset_type != 'QTL' "
            ;    

    public static final String COUNT_DATASET_ID_BY_MAPPING_AND_NOT_QTL = 
            "SELECT COUNT(dataset_id) " 
            + "FROM gdms_dataset " 
            + "WHERE dataset_type = 'mapping' " 
            + "AND dataset_type != 'QTL' "
            ;

    public static final String GET_DETAILS_BY_NAME = 
            "SELECT dataset_id, CONCAT(dataset_type, '') " 
            + "FROM gdms_dataset "
            + "WHERE dataset_name = :datasetName"
            ;
    
    public static final String GET_DATASET_NAMES_BY_QTL_ID = 
    		"SELECT DISTINCT CONCAT(dataset_name,'') "
			+ "FROM gdms_dataset gd "
    		+ "INNER JOIN "
    		+ "gdms_qtl gq ON gd.dataset_id = gq.dataset_id " 
    		+ "WHERE gq.qtl_id = :qtlId "
    		;
    
    public static final String COUNT_DATASET_NAMES_BY_QTL_ID = 
    		"SELECT COUNT(DISTINCT CONCAT(dataset_name,'')) "
			+ "FROM gdms_dataset gd "
    		+ "INNER JOIN "
    		+ "gdms_qtl gq ON gd.dataset_id = gq.dataset_id " 
    		+ "WHERE gq.qtl_id = :qtlId "
    		;
    
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
    
    @Column(name = "institute")
    private String institute;
    
    @Column(name = "principal_investigator")
    private String principalInvestigator;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "purpose_of_study")
    private String purposeOfStudy;


    public Dataset() {
    }

    public Dataset(Integer datasetId, String datasetName, String datasetDesc,
			String datasetType, String genus, String species,
			Date uploadTemplateDate, String remarks, String dataType,
			String missingData, String method, String score, String institute,
			String principalInvestigator, String email, String purposeOfStudy) {
		super();
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
		this.institute = institute;
		this.principalInvestigator = principalInvestigator;
		this.email = email;
		this.purposeOfStudy = purposeOfStudy;
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

    public String getInstitute() {
		return institute;
	}

	public void setInstitute(String institute) {
		this.institute = institute;
	}

	public String getPrincipalInvestigator() {
		return principalInvestigator;
	}

	public void setPrincipalInvestigator(String principalInvestigator) {
		this.principalInvestigator = principalInvestigator;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPurposeOfStudy() {
		return purposeOfStudy;
	}

	public void setPurposeOfStudy(String purposeOfStudy) {
		this.purposeOfStudy = purposeOfStudy;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Dataset other = (Dataset) obj;
		if (dataType == null) {
			if (other.dataType != null)
				return false;
		} else if (!dataType.equals(other.dataType))
			return false;
		if (datasetDesc == null) {
			if (other.datasetDesc != null)
				return false;
		} else if (!datasetDesc.equals(other.datasetDesc))
			return false;
		if (datasetId == null) {
			if (other.datasetId != null)
				return false;
		} else if (!datasetId.equals(other.datasetId))
			return false;
		if (datasetName == null) {
			if (other.datasetName != null)
				return false;
		} else if (!datasetName.equals(other.datasetName))
			return false;
		if (datasetType == null) {
			if (other.datasetType != null)
				return false;
		} else if (!datasetType.equals(other.datasetType))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (genus == null) {
			if (other.genus != null)
				return false;
		} else if (!genus.equals(other.genus))
			return false;
		if (institute == null) {
			if (other.institute != null)
				return false;
		} else if (!institute.equals(other.institute))
			return false;
		if (method == null) {
			if (other.method != null)
				return false;
		} else if (!method.equals(other.method))
			return false;
		if (missingData == null) {
			if (other.missingData != null)
				return false;
		} else if (!missingData.equals(other.missingData))
			return false;
		if (principalInvestigator == null) {
			if (other.principalInvestigator != null)
				return false;
		} else if (!principalInvestigator.equals(other.principalInvestigator))
			return false;
		if (purposeOfStudy == null) {
			if (other.purposeOfStudy != null)
				return false;
		} else if (!purposeOfStudy.equals(other.purposeOfStudy))
			return false;
		if (remarks == null) {
			if (other.remarks != null)
				return false;
		} else if (!remarks.equals(other.remarks))
			return false;
		if (score == null) {
			if (other.score != null)
				return false;
		} else if (!score.equals(other.score))
			return false;
		if (species == null) {
			if (other.species != null)
				return false;
		} else if (!species.equals(other.species))
			return false;
		if (uploadTemplateDate == null) {
			if (other.uploadTemplateDate != null)
				return false;
		} else if (!uploadTemplateDate.equals(other.uploadTemplateDate))
			return false;
		return true;
	}

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dataType == null) ? 0 : dataType.hashCode());
		result = prime * result
				+ ((datasetDesc == null) ? 0 : datasetDesc.hashCode());
		result = prime * result
				+ ((datasetId == null) ? 0 : datasetId.hashCode());
		result = prime * result
				+ ((datasetName == null) ? 0 : datasetName.hashCode());
		result = prime * result
				+ ((datasetType == null) ? 0 : datasetType.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((genus == null) ? 0 : genus.hashCode());
		result = prime * result
				+ ((institute == null) ? 0 : institute.hashCode());
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result
				+ ((missingData == null) ? 0 : missingData.hashCode());
		result = prime
				* result
				+ ((principalInvestigator == null) ? 0 : principalInvestigator
						.hashCode());
		result = prime * result
				+ ((purposeOfStudy == null) ? 0 : purposeOfStudy.hashCode());
		result = prime * result + ((remarks == null) ? 0 : remarks.hashCode());
		result = prime * result + ((score == null) ? 0 : score.hashCode());
		result = prime * result + ((species == null) ? 0 : species.hashCode());
		result = prime
				* result
				+ ((uploadTemplateDate == null) ? 0 : uploadTemplateDate
						.hashCode());
		return result;
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
		builder.append(", institute=");
		builder.append(institute);
		builder.append(", principalInvestigator=");
		builder.append(principalInvestigator);
		builder.append(", email=");
		builder.append(email);
		builder.append(", purposeOfStudy=");
		builder.append(purposeOfStudy);
		builder.append("]");
		return builder.toString();
	}

}
