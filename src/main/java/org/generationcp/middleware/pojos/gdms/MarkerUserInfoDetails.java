/*******************************************************************************
 * Copyright (c) 2014, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.pojos.gdms;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "gdms_marker_user_info_details")
public class MarkerUserInfoDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "contact_id")
	private Integer contactId;

	@Column(name = "principal_investigator", columnDefinition = "char(50)")
	private String principalInvestigator;

	@Column(name = "contact")
	private String contact;

	@Column(name = "institute")
	private String institute;

	public MarkerUserInfoDetails() {
	}

	public MarkerUserInfoDetails(Integer contactId, String principalInvestigator, String contact, String institute) {
		this.contactId = contactId;
		this.principalInvestigator = principalInvestigator;
		this.contact = contact;
		this.institute = institute;
	}

	public Integer getContactId() {
		return this.contactId;
	}

	public void setContactId(Integer contactId) {
		this.contactId = contactId;
	}

	public String getPrincipalInvestigator() {
		return this.principalInvestigator;
	}

	public void setPrincipalInvestigator(String principalInvestigator) {
		this.principalInvestigator = principalInvestigator;
	}

	public String getContact() {
		return this.contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getInstitute() {
		return this.institute;
	}

	public void setInstitute(String institute) {
		this.institute = institute;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.contact == null ? 0 : this.contact.hashCode());
		result = prime * result + (this.contactId == null ? 0 : this.contactId.hashCode());
		result = prime * result + (this.institute == null ? 0 : this.institute.hashCode());
		result = prime * result + (this.principalInvestigator == null ? 0 : this.principalInvestigator.hashCode());
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
		MarkerUserInfoDetails other = (MarkerUserInfoDetails) obj;
		if (this.contact == null) {
			if (other.contact != null) {
				return false;
			}
		} else if (!this.contact.equals(other.contact)) {
			return false;
		}
		if (this.contactId == null) {
			if (other.contactId != null) {
				return false;
			}
		} else if (!this.contactId.equals(other.contactId)) {
			return false;
		}
		if (this.institute == null) {
			if (other.institute != null) {
				return false;
			}
		} else if (!this.institute.equals(other.institute)) {
			return false;
		}
		if (this.principalInvestigator == null) {
			if (other.principalInvestigator != null) {
				return false;
			}
		} else if (!this.principalInvestigator.equals(other.principalInvestigator)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Contact [contactId=");
		builder.append(this.contactId);
		builder.append(", principalInvestigator=");
		builder.append(this.principalInvestigator);
		builder.append(", contact=");
		builder.append(this.contact);
		builder.append(", institute=");
		builder.append(this.institute);
		builder.append("]");
		return builder.toString();
	}

}
