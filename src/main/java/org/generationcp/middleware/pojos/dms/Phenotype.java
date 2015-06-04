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
import javax.persistence.UniqueConstraint;

/**
 * http://gmod.org/wiki/Chado_Tables#Table:_phenotype
 *
 * A phenotypic statement, or a single atomic phenotypic observation, is a controlled sentence describing observable effects of non-wild
 * type function. E.g. Obs=eye, attribute=color, cvalue=red.
 *
 * @author Joyce Avestro
 *
 */
@Entity
@Table(name = "phenotype", uniqueConstraints = {@UniqueConstraint(columnNames = {"uniquename"})})
public class Phenotype implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "phenotype_id")
	private Integer phenotypeId;

	@Basic(optional = false)
	@Column(name = "uniquename")
	private String uniqueName;

	@Column(name = "name")
	private String name;

	// References cvterm
	@Column(name = "observable_id")
	private Integer observableId;

	// References cvterm
	@Column(name = "attr_id")
	private Integer attributeId;

	@Column(name = "value")
	private String value;

	// References cvterm
	@Column(name = "cvalue_id")
	private Integer cValueId;

	// References cvterm
	@Column(name = "assay_id")
	private Integer assayId;

	public Phenotype() {
	}

	public Phenotype(Integer phenotypeId, String uniqueName, String name, Integer observableId, Integer attributeId, String value,
			Integer cValueId, Integer assayId) {
		this.phenotypeId = phenotypeId;
		this.uniqueName = uniqueName;
		this.name = name;
		this.observableId = observableId;
		this.attributeId = attributeId;
		this.value = value;
		this.cValueId = cValueId;
		this.assayId = assayId;
	}

	public Integer getPhenotypeId() {
		return this.phenotypeId;
	}

	public void setPhenotypeId(Integer phenotypeId) {
		this.phenotypeId = phenotypeId;
	}

	public String getUniqueName() {
		return this.uniqueName;
	}

	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getObservableId() {
		return this.observableId;
	}

	public void setObservableId(Integer observableId) {
		this.observableId = observableId;
	}

	public Integer getAttributeId() {
		return this.attributeId;
	}

	public void setAttributeId(Integer attributeId) {
		this.attributeId = attributeId;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getcValueId() {
		return this.cValueId;
	}

	public void setcValue(Integer cValueId) {
		this.cValueId = cValueId;
	}

	public Integer getAssayId() {
		return this.assayId;
	}

	public void setAssayId(Integer assayId) {
		this.assayId = assayId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.assayId == null ? 0 : this.assayId.hashCode());
		result = prime * result + (this.attributeId == null ? 0 : this.attributeId.hashCode());
		result = prime * result + (this.cValueId == null ? 0 : this.cValueId.hashCode());
		result = prime * result + (this.name == null ? 0 : this.name.hashCode());
		result = prime * result + (this.observableId == null ? 0 : this.observableId.hashCode());
		result = prime * result + (this.phenotypeId == null ? 0 : this.phenotypeId.hashCode());
		result = prime * result + (this.uniqueName == null ? 0 : this.uniqueName.hashCode());
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
		Phenotype other = (Phenotype) obj;
		if (this.assayId == null) {
			if (other.assayId != null) {
				return false;
			}
		} else if (!this.assayId.equals(other.assayId)) {
			return false;
		}
		if (this.attributeId == null) {
			if (other.attributeId != null) {
				return false;
			}
		} else if (!this.attributeId.equals(other.attributeId)) {
			return false;
		}
		if (this.cValueId == null) {
			if (other.cValueId != null) {
				return false;
			}
		} else if (!this.cValueId.equals(other.cValueId)) {
			return false;
		}
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		if (this.observableId == null) {
			if (other.observableId != null) {
				return false;
			}
		} else if (!this.observableId.equals(other.observableId)) {
			return false;
		}
		if (this.phenotypeId == null) {
			if (other.phenotypeId != null) {
				return false;
			}
		} else if (!this.phenotypeId.equals(other.phenotypeId)) {
			return false;
		}
		if (this.uniqueName == null) {
			if (other.uniqueName != null) {
				return false;
			}
		} else if (!this.uniqueName.equals(other.uniqueName)) {
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
		builder.append("Phenotype [phenotypeId=");
		builder.append(this.phenotypeId);
		builder.append(", uniqueName=");
		builder.append(this.uniqueName);
		builder.append(", name=");
		builder.append(this.name);
		builder.append(", observableId=");
		builder.append(this.observableId);
		builder.append(", attributeId=");
		builder.append(this.attributeId);
		builder.append(", value=");
		builder.append(this.value);
		builder.append(", cValue=");
		builder.append(this.cValueId);
		builder.append(", assayId=");
		builder.append(this.assayId);
		builder.append("]");
		return builder.toString();
	}

}
