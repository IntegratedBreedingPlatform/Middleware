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
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Basic;

/**
 * POJO for gdms_marker_user_info table.
 *
 * @author Joyce Avestro
 */
@Entity
@Table(name = "gdms_marker_user_info")
public class MarkerUserInfo implements Serializable{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "marker_id")
    private Integer markerId;

    @Column(name = "principal_investigator")
    @Basic(optional = false)
    private String principalInvestigator;

    @Column(name = "contact")
    private String contact; 
    
    @Column(name = "institute")
    private String institute;
    
    public MarkerUserInfo() {
        super();
    }

    public MarkerUserInfo(Integer markerId, String principalInvestigator, String contact, String institute) {
        super();
        this.markerId = markerId;
        this.principalInvestigator = principalInvestigator;
        this.contact = contact;
        this.institute = institute;
    }

    
    public Integer getMarkerId() {
        return markerId;
    }

    
    public void setMarkerId(Integer markerId) {
        this.markerId = markerId;
    }

    
    public String getPrincipalInvestigator() {
        return principalInvestigator;
    }

    
    public void setPrincipalInvestigator(String principalInvestigator) {
        this.principalInvestigator = principalInvestigator;
    }

    
    public String getContact() {
        return contact;
    }

    
    public void setContact(String contact) {
        this.contact = contact;
    }

    
    public String getInstitute() {
        return institute;
    }

    
    public void setInstitute(String institute) {
        this.institute = institute;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((contact == null) ? 0 : contact.hashCode());
        result = prime * result + ((institute == null) ? 0 : institute.hashCode());
        result = prime * result + ((markerId == null) ? 0 : markerId.hashCode());
        result = prime * result + ((principalInvestigator == null) ? 0 : principalInvestigator.hashCode());
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
        MarkerUserInfo other = (MarkerUserInfo) obj;
        if (contact == null) {
            if (other.contact != null)
                return false;
        } else if (!contact.equals(other.contact))
            return false;
        if (institute == null) {
            if (other.institute != null)
                return false;
        } else if (!institute.equals(other.institute))
            return false;
        if (markerId == null) {
            if (other.markerId != null)
                return false;
        } else if (!markerId.equals(other.markerId))
            return false;
        if (principalInvestigator == null) {
            if (other.principalInvestigator != null)
                return false;
        } else if (!principalInvestigator.equals(other.principalInvestigator))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MarkerUserInfo [markerId=");
        builder.append(markerId);
        builder.append(", principalInvestigator=");
        builder.append(principalInvestigator);
        builder.append(", contact=");
        builder.append(contact);
        builder.append(", institute=");
        builder.append(institute);
        builder.append("]");
        return builder.toString();
    }
    
}
