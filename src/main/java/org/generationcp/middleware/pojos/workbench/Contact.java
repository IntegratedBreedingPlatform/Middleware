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

package org.generationcp.middleware.pojos.workbench;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * POJO for persons table.
 * 
 */
@Entity
@Table(name = "persons")
public class Contact implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@Column(name = "personid")
	private Long contactId;

	@Basic(optional = false)
	@Column(name = "ptitle")
	private String title;

	@Basic(optional = false)
	@Column(name = "fname")
	private String firstName;

	@Basic(optional = false)
	@Column(name = "lname")
	private String lastName;

	@Basic(optional = false)
	@Column(name = "pemail")
	private String email;

	@Basic(optional = false)
	@Column(name = "pphone")
	private String phoneNumber;

	// TODO: this should be an object
	private String institution;

	// TODO: these fields are not found in schema
	private String address1;
	private String address2;
	private String skypeId;
	private String description;

	public Long getContactId() {
		return this.contactId;
	}

	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getInstitution() {
		return this.institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getAddress1() {
		return this.address1;
	}

	public void setAddress1(String address) {
		this.address1 = address;
	}

	public String getAddress2() {
		return this.address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getSkypeId() {
		return this.skypeId;
	}

	public void setSkypeId(String skypeId) {
		this.skypeId = skypeId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.contactId).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!Contact.class.isInstance(obj)) {
			return false;
		}

		Contact otherObj = (Contact) obj;

		return new EqualsBuilder().append(this.contactId, otherObj.contactId).isEquals();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Contact [contactId=");
		builder.append(this.contactId);
		builder.append(", title=");
		builder.append(this.title);
		builder.append(", firstName=");
		builder.append(this.firstName);
		builder.append(", lastName=");
		builder.append(this.lastName);
		builder.append(", email=");
		builder.append(this.email);
		builder.append(", phoneNumber=");
		builder.append(this.phoneNumber);
		builder.append(", institution=");
		builder.append(this.institution);
		builder.append(", address1=");
		builder.append(this.address1);
		builder.append(", address2=");
		builder.append(this.address2);
		builder.append(", skypeId=");
		builder.append(this.skypeId);
		builder.append(", description=");
		builder.append(this.description);
		builder.append("]");
		return builder.toString();
	}

}
