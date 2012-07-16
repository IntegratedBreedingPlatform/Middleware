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
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


/**
 * The Class GermplasmMarkerName. Contains the germplasm name and marker names associated with it. 
 * Used in getting marker names by germplasmName.
 * 
 * @author Joyce Avestro
 *
 */
//public class GermplasmMarkerElement implements Serializable{
public class GermplasmMarkerElement {
        
//    /** The Constant serialVersionUID. */
//    private static final long serialVersionUID = 1L;

    /** The germplasm name. */
//    @Basic(optional = false)
//    @Column(name = "nval")
    private String germplasmName;
    
    /** The marker names. */
//    @Basic(optional = false)
//    @Column(name = "marker_name")
//    private String markerName;
    private List<String> markerNames;
    
    /**
     * Instantiates a new marker name element.
     */
    GermplasmMarkerElement(){
    }
   
    /**
     * Instantiates a new marker name element.
     *
     * @param gId the germplasm id
     * @param markerName the marker name
     */
//    public GermplasmMarkerElement(String markerName, String germplasmName){
//        this.markerName = markerName;              
//        this.germplasmName = germplasmName;
//    }

    public GermplasmMarkerElement(String germplasmName, List<String> markerNames){
        this.germplasmName = germplasmName;
        this.markerNames = markerNames;
    }
    
    /**
     * Gets the marker names.
     *
     * @return the marker names
     */
    public List<String> getMarkerNames() {
        return markerNames;
    }

    
    /**
     * Sets the marker names.
     *
     * @param markerNames the new marker name list
     */
    public void setMarkerName(List<String> markerNames) {
        this.markerNames = markerNames;
    }
   

    
    /**
     *
     * @return the germplasmName
     */
    public String getGermplasmName() {
        return germplasmName;
    }

    
    /**
     * Sets the germplasm name.
     *
     * @param germplasmName the new germplasmName
     */
    public void setGermplasmName(String germplasmName) {
        this.germplasmName = germplasmName;
    }

    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(139, 11).append(germplasmName).toHashCode();
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
        if (!(obj instanceof GermplasmMarkerElement)) {
            return false;
        }

        GermplasmMarkerElement rhs = (GermplasmMarkerElement) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(germplasmName, rhs.germplasmName)
                .append(markerNames, rhs.markerNames).isEquals();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer markerNameBuf = new StringBuffer("[");
        for (String mName : markerNames){
            if (markerNames.indexOf(mName) != markerNames.size() - 1) {         // if not the last element on the list
                markerNameBuf.append(mName).append(", ");
            } else {
                markerNameBuf.append(mName).append("]");
            }
        }
        if (markerNames.size() == 0) {  // if empty markerNames
            markerNameBuf.append("]");
        }
        
        
        return "GermplasmMarkerElement [germplasmName=" + germplasmName + ", markerNames=" + markerNameBuf.toString() + "]";
    }
    
}
