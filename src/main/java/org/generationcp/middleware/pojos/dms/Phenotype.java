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
package org.generationcp.middleware.pojos.dms;

import javax.persistence.*;
import java.io.Serializable;

/**
 * http://gmod.org/wiki/Chado_Tables#Table:_phenotype
 * 
 * A phenotypic statement, or a single atomic phenotypic observation, 
 * is a controlled sentence describing observable effects of non-wild type function. 
 * E.g. Obs=eye, attribute=color, cvalue=red.
 * 
 * @author Joyce Avestro
 *
 */
@Entity
@Table(	name = "phenotype", 
		uniqueConstraints = {@UniqueConstraint(columnNames = { "uniquename" }) })
public class Phenotype implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@Column(name = "phenotype_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer phenotypeId;

	//kim
	//@Basic(optional = false)
	@Column(name = "uniquename")
	private String uniqueName;
	
	@Column(name = "name")
	private String name;

	// References cvterm
    @Column(name="observable_id")
	private Integer observableId;

    // References cvterm
    @Column(name="attr_id")
	private Integer attributeId;
	
	@Column(name = "value")
	private String value;
	
	// References cvterm
    @Column(name="cvalue_id")
	private Integer cValueId;
	
	// References cvterm
    @Column(name="assay_id")
	private Integer assayId;

	public Phenotype() {
	}


	public Phenotype(Integer phenotypeId, String uniqueName, String name,
			Integer observableId, Integer attributeId, String value, Integer cValueId,
			Integer assayId) {
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
		return phenotypeId;
	}

	public void setPhenotypeId(Integer phenotypeId) {
		this.phenotypeId = phenotypeId;
	}

	public String getUniqueName() {
		return uniqueName;
	}

	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getObservableId() {
		return observableId;
	}

	public void setObservableId(Integer observableId) {
		this.observableId = observableId;
	}

	public Integer getAttributeId() {
		return attributeId;
	}

	public void setAttributeId(Integer attributeId) {
		this.attributeId = attributeId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getcValueId() {
		return cValueId;
	}

	public void setcValue(Integer cValueId) {
		this.cValueId = cValueId;
	}

	public Integer getAssayId() {
		return assayId;
	}

	public void setAssayId(Integer assayId) {
		this.assayId = assayId;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((assayId == null) ? 0 : assayId.hashCode());
		result = prime * result
				+ ((attributeId == null) ? 0 : attributeId.hashCode());
		result = prime * result + ((cValueId == null) ? 0 : cValueId.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((observableId == null) ? 0 : observableId.hashCode());
		result = prime * result
				+ ((phenotypeId == null) ? 0 : phenotypeId.hashCode());
		result = prime * result
				+ ((uniqueName == null) ? 0 : uniqueName.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		if (getClass() != obj.getClass()) {
            return false;
        }
		Phenotype other = (Phenotype) obj;
		if (assayId == null) {
			if (other.assayId != null) {
                return false;
            }
		} else if (!assayId.equals(other.assayId)) {
            return false;
        }
		if (attributeId == null) {
			if (other.attributeId != null) {
                return false;
            }
		} else if (!attributeId.equals(other.attributeId)) {
            return false;
        }
		if (cValueId == null) {
			if (other.cValueId != null) {
                return false;
            }
		} else if (!cValueId.equals(other.cValueId)) {
            return false;
        }
		if (name == null) {
			if (other.name != null) {
                return false;
            }
		} else if (!name.equals(other.name)) {
            return false;
        }
		if (observableId == null) {
			if (other.observableId != null) {
                return false;
            }
		} else if (!observableId.equals(other.observableId)) {
            return false;
        }
		if (phenotypeId == null) {
			if (other.phenotypeId != null) {
                return false;
            }
		} else if (!phenotypeId.equals(other.phenotypeId)) {
            return false;
        }
		if (uniqueName == null) {
			if (other.uniqueName != null) {
                return false;
            }
		} else if (!uniqueName.equals(other.uniqueName)) {
            return false;
        }
		if (value == null) {
			if (other.value != null) {
                return false;
            }
		} else if (!value.equals(other.value)) {
            return false;
        }
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Phenotype [phenotypeId=");
		builder.append(phenotypeId);
		builder.append(", uniqueName=");
		builder.append(uniqueName);
		builder.append(", name=");
		builder.append(name);
		builder.append(", observableId=");
		builder.append(observableId);
		builder.append(", attributeId=");
		builder.append(attributeId);
		builder.append(", value=");
		builder.append(value);
		builder.append(", cValue=");
		builder.append(cValueId);
		builder.append(", assayId=");
		builder.append(assayId);
		builder.append("]");
		return builder.toString();
	}
	
	
	
}