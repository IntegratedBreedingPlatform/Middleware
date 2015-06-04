/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.pojos.gdms;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * POJO for gdms_marker_user_info table.
 *
 * @author Joyce Avestro
 */
@Entity
@Table(name = "gdms_marker_user_info")
public class MarkerUserInfo implements Serializable {

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
		return this.userInfoId;
	}

	public void setUserInfoId(Integer userInfoId) {
		this.userInfoId = userInfoId;
	}

	public MarkerUserInfoDetails getMarkerUserInfoDetails() {
		return this.markerUserInfoDetails;
	}

	public void setMarkerUserInfoDetails(MarkerUserInfoDetails markerUserInfoDetails) {
		this.markerUserInfoDetails = markerUserInfoDetails;
	}

	public Integer getMarkerId() {
		return this.markerId;
	}

	public void setMarkerId(Integer markerId) {
		this.markerId = markerId;
	}

	public String getPrincipalInvestigator() {
		if (this.markerUserInfoDetails != null) {
			return this.markerUserInfoDetails.getPrincipalInvestigator();
		}
		return null;
	}

	public void setPrincipalInvestigator(String principalInvestigator) {
		if (this.markerUserInfoDetails == null) {
			this.markerUserInfoDetails = new MarkerUserInfoDetails();
		}
		this.markerUserInfoDetails.setPrincipalInvestigator(principalInvestigator);
	}

	public String getContactValue() {
		if (this.markerUserInfoDetails != null) {
			return this.markerUserInfoDetails.getContact();
		}
		return null;
	}

	public void setContactValue(String contactString) {
		if (this.markerUserInfoDetails == null) {
			this.markerUserInfoDetails = new MarkerUserInfoDetails();
		}
		this.markerUserInfoDetails.setContact(contactString);
	}

	public String getInstitute() {
		if (this.markerUserInfoDetails != null) {
			return this.markerUserInfoDetails.getInstitute();
		}
		return null;
	}

	public void setInstitute(String institute) {
		if (this.markerUserInfoDetails == null) {
			this.markerUserInfoDetails = new MarkerUserInfoDetails();
		}
		this.markerUserInfoDetails.setInstitute(institute);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.markerUserInfoDetails == null ? 0 : this.markerUserInfoDetails.hashCode());
		result = prime * result + (this.markerId == null ? 0 : this.markerId.hashCode());
		result = prime * result + (this.userInfoId == null ? 0 : this.userInfoId.hashCode());
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
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		MarkerUserInfo other = (MarkerUserInfo) obj;
		if (this.markerUserInfoDetails == null) {
			if (other.markerUserInfoDetails != null) {
				return false;
			}
		} else if (!this.markerUserInfoDetails.equals(other.markerUserInfoDetails)) {
			return false;
		}
		if (this.markerId == null) {
			if (other.markerId != null) {
				return false;
			}
		} else if (!this.markerId.equals(other.markerId)) {
			return false;
		}
		if (this.userInfoId == null) {
			if (other.userInfoId != null) {
				return false;
			}
		} else if (!this.userInfoId.equals(other.userInfoId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MarkerUserInfo [userInfoId=");
		builder.append(this.userInfoId);
		builder.append(", markerId=");
		builder.append(this.markerId);
		builder.append(", contact=");
		builder.append(this.markerUserInfoDetails);
		builder.append("]");
		return builder.toString();
	}

}
