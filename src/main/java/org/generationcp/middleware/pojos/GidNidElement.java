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


/**
 * <b>Description</b>: Placeholder POJO for Gid-Nid Pairs Element
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Joyce Avestro <br>
 * <b>File Created</b>: August 8, 2012
 */
public class GidNidElement implements Serializable{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The Germplasm Id. */
    private Integer germplasmId;
    
    /** The Name Id. */
    private Integer nameId;


    
    public GidNidElement(Integer germplasmId, Integer nameId) {
        super();
        this.germplasmId = germplasmId;
        this.nameId = nameId;
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


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("GidNidElement [germplasmId=");
        builder.append(germplasmId);
        builder.append(", nameId=");
        builder.append(nameId);
        builder.append("]");
        return builder.toString();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof GidNidElement)) {
            return false;
        }

        GidNidElement rhs = (GidNidElement) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(germplasmId, rhs.germplasmId)
                .append(nameId, rhs.nameId).isEquals();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(77, 7).append(germplasmId)
                .append(nameId).toHashCode();
    }

}
