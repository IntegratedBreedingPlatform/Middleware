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

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

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
	@GeneratedValue
	@Column(name = "phenotype_id")
	private Long phenotypeId;

	@Basic(optional = false)
	@Column(name = "uniquename")
	private String uniqueName;
	
	@Column(name = "name")
	private String name;

    @OneToOne
    @JoinColumn(name="observable_id", referencedColumnName="cvterm_id")
	private CVTerm observable;

    @OneToOne
    @JoinColumn(name="attr_id", referencedColumnName="cvterm_id")
	private CVTerm attribute;
	
	@Column(name = "value")
	private String value;
	
    @OneToOne
    @JoinColumn(name="cvalue_id", referencedColumnName="cvterm_id")
	private CVTerm cValue;
	
    @OneToOne
    @JoinColumn(name="assay_id", referencedColumnName="cvterm_id")
	private CVTerm assay;

	public Phenotype() {
	}


	public Phenotype(Long phenotypeId, String uniqueName, String name,
			CVTerm observable, CVTerm attribute, String value, CVTerm cValue,
			CVTerm assay) {
		super();
		this.phenotypeId = phenotypeId;
		this.uniqueName = uniqueName;
		this.name = name;
		this.observable = observable;
		this.attribute = attribute;
		this.value = value;
		this.cValue = cValue;
		this.assay = assay;
	}


	public Long getPhenotypeId() {
		return phenotypeId;
	}

	public void setPhenotypeId(Long phenotypeId) {
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

	public CVTerm getObservable() {
		return observable;
	}

	public void setObservable(CVTerm observable) {
		this.observable = observable;
	}

	public CVTerm getAttribute() {
		return attribute;
	}

	public void setAttribute(CVTerm attribute) {
		this.attribute = attribute;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public CVTerm getcValue() {
		return cValue;
	}

	public void setcValue(CVTerm cValue) {
		this.cValue = cValue;
	}

	public CVTerm getAssay() {
		return assay;
	}

	public void setAssay(CVTerm assay) {
		this.assay = assay;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((assay == null) ? 0 : assay.hashCode());
		result = prime * result
				+ ((attribute == null) ? 0 : attribute.hashCode());
		result = prime * result + ((cValue == null) ? 0 : cValue.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((observable == null) ? 0 : observable.hashCode());
		result = prime * result
				+ ((phenotypeId == null) ? 0 : phenotypeId.hashCode());
		result = prime * result
				+ ((uniqueName == null) ? 0 : uniqueName.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Phenotype other = (Phenotype) obj;
		if (assay == null) {
			if (other.assay != null)
				return false;
		} else if (!assay.equals(other.assay))
			return false;
		if (attribute == null) {
			if (other.attribute != null)
				return false;
		} else if (!attribute.equals(other.attribute))
			return false;
		if (cValue == null) {
			if (other.cValue != null)
				return false;
		} else if (!cValue.equals(other.cValue))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (observable == null) {
			if (other.observable != null)
				return false;
		} else if (!observable.equals(other.observable))
			return false;
		if (phenotypeId == null) {
			if (other.phenotypeId != null)
				return false;
		} else if (!phenotypeId.equals(other.phenotypeId))
			return false;
		if (uniqueName == null) {
			if (other.uniqueName != null)
				return false;
		} else if (!uniqueName.equals(other.uniqueName))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
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
		builder.append(", observable=");
		builder.append(observable);
		builder.append(", attribute=");
		builder.append(attribute);
		builder.append(", value=");
		builder.append(value);
		builder.append(", cValue=");
		builder.append(cValue);
		builder.append(", assay=");
		builder.append(assay);
		builder.append("]");
		return builder.toString();
	}
	
	
	
}