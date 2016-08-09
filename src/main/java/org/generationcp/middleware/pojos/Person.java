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

package org.generationcp.middleware.pojos;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.generationcp.middleware.util.StringUtil;
import org.generationcp.middleware.util.Util;

/**
 * POJO for persons table.
 *
 */
@NamedQueries({@NamedQuery(name = "getByFullNameWithMiddleName", query = "SELECT p FROM Person p WHERE CONCAT(p.firstName, ' ', p.middleName, ' ', p.lastName) = :fullname"),
	           @NamedQuery(name = "getByFullName", query = "SELECT p FROM Person p WHERE CONCAT(p.firstName, ' ', p.lastName) = :fullname")})
@Entity
@Table(name = "persons")
public class Person implements Comparable<Person>, Serializable {

	private static final long serialVersionUID = -3159738927364282485L;

	public static final String GET_BY_FULLNAME = "getByFullName";
	public static final String GET_BY_FULLNAME_WITH_MIDDLENAME = "getByFullNameWithMiddleName";
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "personid")
	private Integer id;

	@Column(name = "fname")
	private String firstName;

	@Column(name = "lname")
	private String lastName;

	@Column(name = "ioname")
	private String middleName;

	@Column(name = "institid")
	private Integer instituteId;

	@Column(name = "ptitle")
	private String title;

	@Column(name = "poname")
	private String positionName;

	@Column(name = "plangu")
	private Integer language;

	@Column(name = "pphone")
	private String phone;

	@Column(name = "pextent")
	private String extension;

	@Column(name = "pfax")
	private String fax;

	@Column(name = "pemail")
	private String email;

	@Column(name = "pnotes")
	private String notes;

	@Column(name = "contact")
	private String contact;
	
	public Person() {
	}

	public Person(final String firstName, final String middleName, final String lastName) {
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
	}

	public Person(final Integer id, final String firstName, final String middleName, final String lastName) {
		this.id = id;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
	}

	/**
	 * Create a copy of this Person object. Note that this method does not copy the {@link Person#id} field.
	 * 
	 * @return the copy of this Person object.
	 */
	public Person copy() {
		final Person person = new Person();
		person.setFirstName(this.firstName);
		person.setLastName(this.lastName);
		person.setMiddleName(this.middleName);
		person.setInstituteId(this.instituteId);
		person.setTitle(this.title);
		person.setPositionName(this.positionName);
		person.setLanguage(this.language);
		person.setPhone(this.phone);
		person.setExtension(this.extension);
		person.setFax(this.fax);
		person.setEmail(this.email);
		person.setNotes(this.notes);
		person.setContact(this.contact);

		return person;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getMiddleName() {
		return this.middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getInitials() {
		StringBuilder initials = new StringBuilder();
		if (!StringUtil.isEmptyOrWhitespaceOnly(this.firstName)) {
			initials.append(this.firstName.trim().charAt(0));
		}
		if (!StringUtil.isEmptyOrWhitespaceOnly(this.middleName)) {
			initials.append(this.middleName.trim().charAt(0));
		}
		if (!StringUtil.isEmptyOrWhitespaceOnly(this.lastName)) {
			initials.append(this.lastName.trim().charAt(0));
		}

		return initials.toString().toLowerCase();
	}

	public String getInitialsWithTimestamp() {
		String timestamp = Util.getCurrentDateAsStringValue("yyMMddHHmmssSS");
		timestamp = timestamp.substring(0, 13);
		return this.getInitials() + timestamp;
	}

	public String getDisplayName() {
		final String displayFirstName = this.firstName == null || Util.isOneOf(this.firstName, "-", "'-'") ? "" : this.firstName;
		final String displayMiddleName = this.middleName == null || Util.isOneOf(this.middleName, "-", "'-'") ? "" : this.middleName;
		final String displayLastName = this.lastName == null || Util.isOneOf(this.lastName, "-", "'-'") ? "" : this.lastName;
		return StringUtil.joinIgnoreEmpty(" ", displayFirstName, displayMiddleName, displayLastName);
	}

	public Integer getInstituteId() {
		return this.instituteId;
	}

	public void setInstituteId(Integer instituteId) {
		this.instituteId = instituteId;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPositionName() {
		return this.positionName;
	}

	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}

	public Integer getLanguage() {
		return this.language;
	}

	public void setLanguage(Integer language) {
		this.language = language;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getExtension() {
		return this.extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getFax() {
		return this.fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNotes() {
		return this.notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getContact() {
		return this.contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Person [id=");
		builder.append(this.id);
		builder.append(", firstName=");
		builder.append(this.firstName);
		builder.append(", lastName=");
		builder.append(this.lastName);
		builder.append(", middleName=");
		builder.append(this.middleName);
		builder.append(", instituteId=");
		builder.append(this.instituteId);
		builder.append(", title=");
		builder.append(this.title);
		builder.append(", positionName=");
		builder.append(this.positionName);
		builder.append(", language=");
		builder.append(this.language);
		builder.append(", phone=");
		builder.append(this.phone);
		builder.append(", extension=");
		builder.append(this.extension);
		builder.append(", fax=");
		builder.append(this.fax);
		builder.append(", email=");
		builder.append(this.email);
		builder.append(", notes=");
		builder.append(this.notes);
		builder.append(", contact=");
		builder.append(this.contact);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!Person.class.isInstance(obj)) {
			return false;
		}

		Person otherObj = (Person) obj;

		return new EqualsBuilder().append(this.id, otherObj.id).isEquals();
	}

	@Override
	public int compareTo(Person o) {
		if (this.getDisplayName() != null && o != null) {
			return this.getDisplayName().compareTo(o.getDisplayName());
		}
		return 0;
	}

}
