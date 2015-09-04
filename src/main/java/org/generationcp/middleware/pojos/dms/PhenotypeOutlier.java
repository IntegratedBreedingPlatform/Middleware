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

package org.generationcp.middleware.pojos.dms;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author Aldrin Batac
 *
 */
@Entity
@Table(name = "phenotype_outlier")
public class PhenotypeOutlier implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "phenotype_outlier_id")
	private Integer phenotypeOutlierId;

	// References phenotype
	@Basic(optional = false)
	@Column(name = "phenotype_id")
	private Integer phenotypeId;

	@Column(name = "value")
	private String value;

	public PhenotypeOutlier() {
	}

	public PhenotypeOutlier(Integer phenotypeId, Integer observableId, Integer plotNo, Integer projectId,
			String value) {
		this.setPhenotypeId(phenotypeId);
		this.setValue(value);
	}

	public Integer getPhenotypeOutlierId() {
		return this.phenotypeOutlierId;
	}

	public void setPhenotypeOutlierId(Integer phenotypeOutlierId) {
		this.phenotypeOutlierId = phenotypeOutlierId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.phenotypeOutlierId == null ? 0 : this.phenotypeOutlierId.hashCode());
		result = prime * result + (this.phenotypeId == null ? 0 : this.phenotypeId.hashCode());
		result = prime * result + (this.value == null ? 0 : this.value.hashCode());
		return result;
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
		PhenotypeOutlier other = (PhenotypeOutlier) obj;

		if (this.phenotypeOutlierId == null) {
			if (other.phenotypeOutlierId != null) {
				return false;
			}
		} else if (!this.phenotypeOutlierId.equals(other.phenotypeOutlierId)) {
			return false;
		}

		if (this.phenotypeId == null) {
			if (other.phenotypeId != null) {
				return false;
			}
		} else if (!this.phenotypeId.equals(other.phenotypeId)) {
			return false;
		}

		if (this.value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!this.value.equals(other.value)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PhenotypeOutlier [phenotypeOutlierId=");
		builder.append(this.getPhenotypeOutlierId());
		builder.append(", phenotypeId=");
		builder.append(this.getPhenotypeId());
		builder.append(", value=");
		builder.append(this.getValue());
		builder.append("]");
		return builder.toString();
	}

	public Integer getPhenotypeId() {
		return this.phenotypeId;
	}

	public void setPhenotypeId(Integer phenotypeId) {
		this.phenotypeId = phenotypeId;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
