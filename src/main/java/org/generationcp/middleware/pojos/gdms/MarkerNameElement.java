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
import javax.persistence.Basic;
import javax.persistence.Column;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


/**
 * The Class MarkerNameElement. Contains the pair germplasm id and marker name. 
 * Used in getting marker names by gid.
 * 
 * @author Joyce Avestro
 *
 */
public class MarkerNameElement implements Serializable{
        
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The germplasm id. */
    @Basic(optional = false)
    @Column(name = "gid")
    private Integer gId;
    
    /** The marker name. */
    @Basic(optional = false)
    @Column(name = "marker_name")
    private String markerName;
    
    /**
     * Instantiates a new marker name element.
     */
    MarkerNameElement(){
    }
   
    /**
     * Instantiates a new marker name element.
     *
     * @param gId the germplasm id
     * @param markerName the marker name
     */
    public MarkerNameElement(Integer gId, String markerName){
        this.gId = gId;
        this.markerName = markerName;              
    }

    
    /**
     * Gets the g id.
     *
     * @return the g id
     */
    public Integer getgId() {
        return gId;
    }

    
    /**
     * Sets the g id.
     *
     * @param gId the new g id
     */
    public void setgId(Integer gId) {
        this.gId = gId;
    }

    
    /**
     * Gets the marker name.
     *
     * @return the marker name
     */
    public String getMarkerName() {
        return markerName;
    }

    
    /**
     * Sets the marker name.
     *
     * @param markerName the new marker name
     */
    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }
   

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(139, 17).append(gId).append(markerName).toHashCode();
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
        if (!(obj instanceof MarkerNameElement)) {
            return false;
        }

        MarkerNameElement rhs = (MarkerNameElement) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(gId, rhs.gId)
                .append(markerName, rhs.markerName).isEquals();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "MarkerNameElement [gId=" + gId + ", markerName=" + markerName + "]";
    }
    
}
