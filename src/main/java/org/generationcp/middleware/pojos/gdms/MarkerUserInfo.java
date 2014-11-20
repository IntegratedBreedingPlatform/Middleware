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

import javax.persistence.*;
import java.io.Serializable;

/**
 * POJO for gdms_marker_user_info table.
 *
 * @author Joyce Avestro
 */
@Entity
@Table(name = "gdms_marker_user_info")
public class MarkerUserInfo implements Serializable{

    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "userinfo_id")
    private Integer userInfoId;

    @Column(name = "marker_id")
    private Integer markerId;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "contact_id")
    private MarkerUserInfoDetails markerUserInfoDetails;

    public MarkerUserInfo() {
    }

    public MarkerUserInfo(Integer markerId, String principalInvestigator, String contact, String institute) {
        this.markerId = markerId;
        this.markerUserInfoDetails = new MarkerUserInfoDetails(null, principalInvestigator, contact, institute);
    }

    public MarkerUserInfo(Integer markerId, Integer contactId, String principalInvestigator, String contact, String institute) {
        this.markerId = markerId;
        this.markerUserInfoDetails = new MarkerUserInfoDetails(contactId, principalInvestigator, contact, institute);
    }
    
    public MarkerUserInfo(Integer markerId, MarkerUserInfoDetails contact) {
        this.markerId = markerId;
        this.markerUserInfoDetails = contact;
    }

    public Integer getUserInfoId() {
		return userInfoId;
	}

	public void setUserInfoId(Integer userInfoId) {
		this.userInfoId = userInfoId;
	}

	public MarkerUserInfoDetails getMarkerUserInfoDetails() {
		return markerUserInfoDetails;
	}

	public void setMarkerUserInfoDetails(MarkerUserInfoDetails markerUserInfoDetails) {
		this.markerUserInfoDetails = markerUserInfoDetails;
	}

	public Integer getMarkerId() {
        return markerId;
    }
    
    public void setMarkerId(Integer markerId) {
        this.markerId = markerId;
    }
    
    public String getPrincipalInvestigator() {
    	if (markerUserInfoDetails != null){
    		return markerUserInfoDetails.getPrincipalInvestigator();
    	}
        return null;
    }
    
    public void setPrincipalInvestigator(String principalInvestigator) {
    	if (markerUserInfoDetails == null){
    		markerUserInfoDetails = new MarkerUserInfoDetails();
    	}
		markerUserInfoDetails.setPrincipalInvestigator(principalInvestigator);
    }
    
    public String getContactValue() {
    	if (markerUserInfoDetails != null){
    		return markerUserInfoDetails.getContact();
    	}
        return null;
    }
    
    public void setContactValue(String contactString) {
    	if (markerUserInfoDetails == null){
    		markerUserInfoDetails = new MarkerUserInfoDetails();
    	}
		markerUserInfoDetails.setContact(contactString);
    }

    public String getInstitute() {
    	if (markerUserInfoDetails != null){
    		return markerUserInfoDetails.getInstitute();
    	}
        return null;
    }
    
    public void setInstitute(String institute) {
    	if (markerUserInfoDetails == null){
    		markerUserInfoDetails = new MarkerUserInfoDetails();
    	}
		markerUserInfoDetails.setInstitute(institute);
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((markerUserInfoDetails == null) ? 0 : markerUserInfoDetails.hashCode());
		result = prime * result
				+ ((markerId == null) ? 0 : markerId.hashCode());
		result = prime * result
				+ ((userInfoId == null) ? 0 : userInfoId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
            return true;
        }
		if (obj == null) {
            return false;
        }
		if (getClass() != obj.getClass()) {
            return false;
        }
		MarkerUserInfo other = (MarkerUserInfo) obj;
		if (markerUserInfoDetails == null) {
			if (other.markerUserInfoDetails != null) {
                return false;
            }
		} else if (!markerUserInfoDetails.equals(other.markerUserInfoDetails)) {
            return false;
        }
		if (markerId == null) {
			if (other.markerId != null) {
                return false;
            }
		} else if (!markerId.equals(other.markerId)) {
            return false;
        }
		if (userInfoId == null) {
			if (other.userInfoId != null) {
                return false;
            }
		} else if (!userInfoId.equals(other.userInfoId)) {
            return false;
        }
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MarkerUserInfo [userInfoId=");
		builder.append(userInfoId);
		builder.append(", markerId=");
		builder.append(markerId);
		builder.append(", contact=");
		builder.append(markerUserInfoDetails);
		builder.append("]");
		return builder.toString();
	}
    
}
