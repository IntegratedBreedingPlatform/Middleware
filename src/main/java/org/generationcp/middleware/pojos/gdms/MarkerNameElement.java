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
        
    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @Column(name = "gid")
    private Integer gId;
    
    @Basic(optional = false)
    @Column(name = "marker_name")
    private String markerName;
    
    MarkerNameElement(){
    }
   
    public MarkerNameElement(Integer gId, String markerName){
        this.gId = gId;
        this.markerName = markerName;              
    }

    public Integer getgId() {
        return gId;
    }

    public void setgId(Integer gId) {
        this.gId = gId;
    }

    public String getMarkerName() {
        return markerName;
    }

    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }
   
    @Override
    public int hashCode() {
        return new HashCodeBuilder(139, 17).append(gId).append(markerName).toHashCode();
    }

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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MarkerNameElement [gId=");
        builder.append(gId);
        builder.append(", markerName=");
        builder.append(markerName);
        builder.append("]");
        return builder.toString();
    }
    
}
