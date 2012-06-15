/***************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the 
 * GNU General Public License (http://bit.ly/8Ztv8M) and the 
 * provisions of Part F of the Generation Challenge Programme 
 * Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 **************************************************************/
package org.generationcp.middleware.pojos;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "data_n")
public class NumericData implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String GET_BY_OUNIT_ID_LIST = "select dn.ounitid, dn.variatid, v.vname, dn.dvalue "
	    + "from data_n dn join variate v on dn.variatid = v.variatid "
	    + "where dn.ounitid in (:ounitIdList)";

    @EmbeddedId
    protected NumericDataPK id;

    @Column(name = "dvalue")
    private Double value;

    @ManyToOne(targetEntity = Variate.class)
    @JoinColumn(name = "variatid", nullable = false, insertable = false, updatable = false)
    private Variate variate;

    public NumericData() {
    }

    public NumericData(NumericDataPK id, Double value) {
	super();
	this.id = id;
	this.value = value;
    }

    public NumericDataPK getId() {
	return id;
    }

    public void setId(NumericDataPK id) {
	this.id = id;
    }

    public Double getValue() {
	return value;
    }

    public void setValue(Double value) {
	this.value = value;
    }

    public Variate getVariate() {
	return variate;
    }

    public void setVariate(Variate variate) {
	this.variate = variate;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((id == null) ? 0 : id.hashCode());
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
	NumericData other = (NumericData) obj;
	if (id == null) {
	    if (other.id != null)
		return false;
	} else if (!id.equals(other.id))
	    return false;
	return true;
    }

    @Override
    public String toString() {
	return "NumericData [id=" + id + ", value=" + value + "]";
    }

}
