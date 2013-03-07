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
import java.util.Comparator;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * <b>Description</b>: Marker Alias POJO
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Dennis Billano <br>
 * <b>File Created</b>: March 7, 2013
 */
@Entity
@Table(name = "gdms_marker_alias")

public class MarkerAlias implements Serializable{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The marker id. */
    @Id
    @Basic(optional = false)
    @Column(name = "marker_id")
    private Integer markerId;

    /** The marker type. */
    @Basic(optional = false)
    @Column(name = "alias")
    private String alias;

    
    
    /**
     * Instantiates a new marker alias.
     */
    public MarkerAlias() {
    }

    /**
     * Instantiates a new marker alias.
     *
     * @param markerId the marker id
     * @param alias the markeralias
     */
    public MarkerAlias(Integer markerId,
                    String alias) {
        
        this.markerId = markerId;
        this.alias = alias;
    }
    
    /**
     * Gets the marker id.
     *
     * @return the marker id
     */
    public Integer getMarkerId() {
        return markerId;
    }
    
    /**
     * Sets the marker id.
     *
     * @param markerId the new marker id
     */
    public void setMarkerId(Integer markerId) {
        this.markerId = markerId;
    }

    /**
     * Gets the alias.
     *
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Sets the alias.
     *
     * @param alias the new alias
     */
    public void setAlias(String alias) {
        this.alias = alias;
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
        if (!(obj instanceof MarkerAlias)) {
            return false;
        }

        MarkerAlias rhs = (MarkerAlias) obj;
        return new EqualsBuilder().append(markerId, rhs.markerId).isEquals();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(61, 131).append(markerId).toHashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Marker [markerId=");
        builder.append(markerId);
        builder.append(", alias=");
        builder.append(alias);
        builder.append("]");
        return builder.toString();
    }

}
