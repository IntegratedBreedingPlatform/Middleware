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

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Embeddable
public class ParentElement implements Serializable{
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The parent A gid. */
    @Column(name = "parent_a_nid")
    private Integer parentANId;

    /** The parent B gid. */
    @Column(name = "parent_b_nid")
    private Integer parentBNId;

    /** The mapping type. */
    @Column(name = "mapping_type")
    private String mappingPopType;


    public ParentElement() {
    }

    public ParentElement(Integer parentANId, Integer parentBNId, String mappingType) {
        this.parentANId = parentANId;
        this.parentBNId = parentBNId;
        this.mappingPopType = mappingType;
    }

    public Integer getParentANId() {
        return parentANId;
    }
    
    public void setParentANId(Integer parentAGId) {
        this.parentANId = parentAGId;
    }
    
    public Integer getParentBGId() {
        return parentBNId;
    }
    
    public void setParentBGId(Integer parentBGId) {
        this.parentBNId = parentBGId;
    }
    
    public String getMappingType() {
        return mappingPopType;
    }

    public void setMappingType(String mappingType) {
        this.mappingPopType = mappingType;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 139).append(parentANId).append(parentBNId).append(mappingPopType).toHashCode();
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
        if (!(obj instanceof ParentElement)) {
            return false;
        }

        ParentElement rhs = (ParentElement) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(parentANId, rhs.parentANId).append(parentBNId, rhs.parentBNId)
                .append(mappingPopType, rhs.mappingPopType).isEquals();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ParentElement [parentAGId=");
        builder.append(parentANId);
        builder.append(", parentBGId=");
        builder.append(parentBNId);
        builder.append(", mappingPopType=");
        builder.append(mappingPopType);
        builder.append("]");
        return builder.toString();
    }
    
}
