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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CharacterLevelElement implements Serializable{

    private static final long serialVersionUID = -4284129132975100671L;

    private Integer ounitId;
    private Integer factorId;
    private String factorName;
    private String value;

    public CharacterLevelElement(Integer ounitId, Integer factorId, String factorName, String value) {
        super();
        this.ounitId = ounitId;
        this.factorId = factorId;
        this.factorName = factorName;
        this.value = value;
    }

    public Integer getOunitId() {
        return ounitId;
    }

    public void setOunitId(Integer ounitId) {
        this.ounitId = ounitId;
    }

    public Integer getFactorId() {
        return factorId;
    }

    public void setFactorId(Integer factorId) {
        this.factorId = factorId;
    }

    public String getFactorName() {
        return factorName;
    }

    public void setFactorName(String factorName) {
        this.factorName = factorName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "CharacterLevelElement [ounitId=" + ounitId + ", factorId=" + factorId + ", factorName=" + factorName + ", value=" + value
                + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CharacterLevelElement)) {
            return false;
        }

        CharacterLevelElement rhs = (CharacterLevelElement) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(ounitId, rhs.ounitId).append(factorId, rhs.factorId).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(41, 29).append(ounitId).append(factorId).toHashCode();
    }
}
