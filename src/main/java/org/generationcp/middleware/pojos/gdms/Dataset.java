/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.pojos.gdms;

import org.generationcp.middleware.pojos.Sample;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * POJO for dataset table.
 *
 * @author Joyce Avestro
 *
 */
@Entity
@Table(name = "gdms_dataset")
public class Dataset implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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

	@OneToMany(mappedBy = "dataset", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<AccMetadataSet> accMetadataSets;

	@OneToMany(mappedBy = "dataset", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<CharValues> charValues;

	@OneToMany(mappedBy = "dataset", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<MarkerMetadataSet> markerMetadataSets;

	public Dataset() {
	}

	public Dataset(Integer datasetId, String datasetName, String datasetDesc, String datasetType, String genus, String species,
			Date uploadTemplateDate, String remarks, String dataType, String missingData, String method, String score, String institute,
			String principalInvestigator, String email, String purposeOfStudy, List<AccMetadataSet> accMetadataSets, List<CharValues> charValues, List<MarkerMetadataSet> markerMetadataSets) {
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
		this.accMetadataSets = accMetadataSets;
		this.charValues = charValues;
		this.markerMetadataSets = markerMetadataSets;
	}

	public Integer getDatasetId() {
		return this.datasetId;
	}

	public void setDatasetId(Integer datasetId) {
		this.datasetId = datasetId;
	}

	public String getDatasetName() {
		return this.datasetName;
	}

	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}

	public String getDatasetDesc() {
		return this.datasetDesc;
	}

	public void setDatasetDesc(String datasetDesc) {
		this.datasetDesc = datasetDesc;
	}

	public String getDatasetType() {
		return this.datasetType;
	}

	public void setDatasetType(String datasetType) {
		this.datasetType = datasetType;
	}

	public String getGenus() {
		return this.genus;
	}

	public void setGenus(String genus) {
		this.genus = genus;
	}

	public String getSpecies() {
		return this.species;
	}

	public void setSpecies(String species) {
		this.species = species;
	}

	public Date getUploadTemplateDate() {
		return this.uploadTemplateDate;
	}

	public void setUploadTemplateDate(Date uploadTemplateDate) {
		this.uploadTemplateDate = uploadTemplateDate;
	}

	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getDataType() {
		return this.dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getMissingData() {
		return this.missingData;
	}

	public void setMissingData(String missingData) {
		this.missingData = missingData;
	}

	public String getMethod() {
		return this.method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getScore() {
		return this.score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getInstitute() {
		return this.institute;
	}

	public void setInstitute(String institute) {
		this.institute = institute;
	}

	public String getPrincipalInvestigator() {
		return this.principalInvestigator;
	}

	public void setPrincipalInvestigator(String principalInvestigator) {
		this.principalInvestigator = principalInvestigator;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPurposeOfStudy() {
		return this.purposeOfStudy;
	}

	public void setPurposeOfStudy(String purposeOfStudy) {
		this.purposeOfStudy = purposeOfStudy;
	}

	public List<AccMetadataSet> getAccMetadataSets() {
		return accMetadataSets;
	}

	public void setAccMetadataSets(final List<AccMetadataSet> accMetadataSets) {
		this.accMetadataSets = accMetadataSets;
	}

	public List<CharValues> getCharValues() {
		return charValues;
	}

	public void setCharValues(final List<CharValues> charValues) {
		this.charValues = charValues;
	}

	public List<MarkerMetadataSet> getMarkerMetadataSets() {
		return markerMetadataSets;
	}

	public void setMarkerMetadataSets(final List<MarkerMetadataSet> markerMetadataSets) {
		this.markerMetadataSets = markerMetadataSets;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		Dataset other = (Dataset) obj;
		if (this.dataType == null) {
			if (other.dataType != null) {
				return false;
			}
		} else if (!this.dataType.equals(other.dataType)) {
			return false;
		}
		if (this.datasetDesc == null) {
			if (other.datasetDesc != null) {
				return false;
			}
		} else if (!this.datasetDesc.equals(other.datasetDesc)) {
			return false;
		}
		if (this.datasetId == null) {
			if (other.datasetId != null) {
				return false;
			}
		} else if (!this.datasetId.equals(other.datasetId)) {
			return false;
		}
		if (this.datasetName == null) {
			if (other.datasetName != null) {
				return false;
			}
		} else if (!this.datasetName.equals(other.datasetName)) {
			return false;
		}
		if (this.datasetType == null) {
			if (other.datasetType != null) {
				return false;
			}
		} else if (!this.datasetType.equals(other.datasetType)) {
			return false;
		}
		if (this.email == null) {
			if (other.email != null) {
				return false;
			}
		} else if (!this.email.equals(other.email)) {
			return false;
		}
		if (this.genus == null) {
			if (other.genus != null) {
				return false;
			}
		} else if (!this.genus.equals(other.genus)) {
			return false;
		}
		if (this.institute == null) {
			if (other.institute != null) {
				return false;
			}
		} else if (!this.institute.equals(other.institute)) {
			return false;
		}
		if (this.method == null) {
			if (other.method != null) {
				return false;
			}
		} else if (!this.method.equals(other.method)) {
			return false;
		}
		if (this.missingData == null) {
			if (other.missingData != null) {
				return false;
			}
		} else if (!this.missingData.equals(other.missingData)) {
			return false;
		}
		if (this.principalInvestigator == null) {
			if (other.principalInvestigator != null) {
				return false;
			}
		} else if (!this.principalInvestigator.equals(other.principalInvestigator)) {
			return false;
		}
		if (this.purposeOfStudy == null) {
			if (other.purposeOfStudy != null) {
				return false;
			}
		} else if (!this.purposeOfStudy.equals(other.purposeOfStudy)) {
			return false;
		}
		if (this.remarks == null) {
			if (other.remarks != null) {
				return false;
			}
		} else if (!this.remarks.equals(other.remarks)) {
			return false;
		}
		if (this.score == null) {
			if (other.score != null) {
				return false;
			}
		} else if (!this.score.equals(other.score)) {
			return false;
		}
		if (this.species == null) {
			if (other.species != null) {
				return false;
			}
		} else if (!this.species.equals(other.species)) {
			return false;
		}
		if (this.uploadTemplateDate == null) {
			if (other.uploadTemplateDate != null) {
				return false;
			}
		} else if (!this.uploadTemplateDate.equals(other.uploadTemplateDate)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.dataType == null ? 0 : this.dataType.hashCode());
		result = prime * result + (this.datasetDesc == null ? 0 : this.datasetDesc.hashCode());
		result = prime * result + (this.datasetId == null ? 0 : this.datasetId.hashCode());
		result = prime * result + (this.datasetName == null ? 0 : this.datasetName.hashCode());
		result = prime * result + (this.datasetType == null ? 0 : this.datasetType.hashCode());
		result = prime * result + (this.email == null ? 0 : this.email.hashCode());
		result = prime * result + (this.genus == null ? 0 : this.genus.hashCode());
		result = prime * result + (this.institute == null ? 0 : this.institute.hashCode());
		result = prime * result + (this.method == null ? 0 : this.method.hashCode());
		result = prime * result + (this.missingData == null ? 0 : this.missingData.hashCode());
		result = prime * result + (this.principalInvestigator == null ? 0 : this.principalInvestigator.hashCode());
		result = prime * result + (this.purposeOfStudy == null ? 0 : this.purposeOfStudy.hashCode());
		result = prime * result + (this.remarks == null ? 0 : this.remarks.hashCode());
		result = prime * result + (this.score == null ? 0 : this.score.hashCode());
		result = prime * result + (this.species == null ? 0 : this.species.hashCode());
		result = prime * result + (this.uploadTemplateDate == null ? 0 : this.uploadTemplateDate.hashCode());
		return result;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Dataset [datasetId=");
		builder.append(this.datasetId);
		builder.append(", datasetName=");
		builder.append(this.datasetName);
		builder.append(", datasetDesc=");
		builder.append(this.datasetDesc);
		builder.append(", datasetType=");
		builder.append(this.datasetType);
		builder.append(", genus=");
		builder.append(this.genus);
		builder.append(", species=");
		builder.append(this.species);
		builder.append(", uploadTemplateDate=");
		builder.append(this.uploadTemplateDate);
		builder.append(", remarks=");
		builder.append(this.remarks);
		builder.append(", dataType=");
		builder.append(this.dataType);
		builder.append(", missingData=");
		builder.append(this.missingData);
		builder.append(", method=");
		builder.append(this.method);
		builder.append(", score=");
		builder.append(this.score);
		builder.append(", institute=");
		builder.append(this.institute);
		builder.append(", principalInvestigator=");
		builder.append(this.principalInvestigator);
		builder.append(", email=");
		builder.append(this.email);
		builder.append(", purposeOfStudy=");
		builder.append(this.purposeOfStudy);
		builder.append("]");
		return builder.toString();
	}

}
