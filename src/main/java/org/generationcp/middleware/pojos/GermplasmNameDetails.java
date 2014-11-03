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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * Placeholder POJO for Gid-Nid Pairs Element
 * 
 * @author Joyce Avestro
 * 
 */
public class GermplasmNameDetails implements Serializable{

    private static final long serialVersionUID = 1L;
    
    private Integer germplasmId;
    
    private Integer nameId;
    
    private String nVal;
    
    public GermplasmNameDetails(Integer germplasmId, Integer nameId) {
        this.germplasmId = germplasmId;
        this.nameId = nameId;
    }

    public GermplasmNameDetails(Integer germplasmId, Integer nameId, String nVal) {
        this.germplasmId = germplasmId;
        this.nameId = nameId;
        this.nVal = nVal;
    }

    public Integer getGermplasmId() {
        return germplasmId;
    }
    
    public void setGermplasmId(Integer germplasmId) {
        this.germplasmId = germplasmId;
    }
    
    public Integer getNameId() {
        return nameId;
    }
    
    public void setNameId(Integer nameId) {
        this.nameId = nameId;
    }

    
    public String getNVal() {
        return nVal;
    }
    
    public void setNVal(String nVal) {
        this.nVal = nVal;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("GermplasmNameDetails [germplasmId=");
        builder.append(germplasmId);
        builder.append(", nameId=");
        builder.append(nameId);
        builder.append(", nVal=");
        builder.append(nVal);
        builder.append("]");
        return builder.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof GermplasmNameDetails)) {
            return false;
        }

        GermplasmNameDetails rhs = (GermplasmNameDetails) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(germplasmId, rhs.germplasmId)
                .append(nameId, rhs.nameId).append(nVal, rhs.nVal).isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(77, 7).append(germplasmId)
                .append(nameId).append(nVal).toHashCode();
    }

}
