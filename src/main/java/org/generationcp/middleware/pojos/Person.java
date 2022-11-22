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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.user.UserDto;
import org.generationcp.middleware.util.StringUtil;
import org.generationcp.middleware.util.Util;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * POJO for persons table.
 *
 */
@Entity
@Table(name = "persons")
public class Person implements Comparable<Person>, Serializable {

	private static final long serialVersionUID = -3159738927364282485L;
	
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

	@Fetch(FetchMode.SUBSELECT)
	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "crop_persons",
			joinColumns = @JoinColumn(name = "personid"),
			inverseJoinColumns = @JoinColumn(name = "crop_name"))
	private Set<CropType> crops = new HashSet<>();
	
	public Person() {
	}

	public Person(final UserDto userDto) {
		this.firstName = userDto.getFirstName();
		this.middleName = "";
		this.lastName = userDto.getLastName();
		this.email = userDto.getEmail();
		this.title = "-";
		this.contact = "-";
		this.extension = "-";
		this.fax  = "-";
		this.instituteId = 0;
		this.language = 0;
		this.notes = "-";
		this.positionName = "-";
		this.phone = "-";
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

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return this.middleName;
	}

	public void setMiddleName(final String middleName) {
		this.middleName = middleName;
	}

	public String getInitials() {
		final StringBuilder initials = new StringBuilder();
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

	public void setInstituteId(final Integer instituteId) {
		this.instituteId = instituteId;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public String getPositionName() {
		return this.positionName;
	}

	public void setPositionName(final String positionName) {
		this.positionName = positionName;
	}

	public Integer getLanguage() {
		return this.language;
	}

	public void setLanguage(final Integer language) {
		this.language = language;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(final String phone) {
		this.phone = phone;
	}

	public String getExtension() {
		return this.extension;
	}

	public void setExtension(final String extension) {
		this.extension = extension;
	}

	public String getFax() {
		return this.fax;
	}

	public void setFax(final String fax) {
		this.fax = fax;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public String getNotes() {
		return this.notes;
	}

	public void setNotes(final String notes) {
		this.notes = notes;
	}

	public String getContact() {
		return this.contact;
	}

	public void setContact(final String contact) {
		this.contact = contact;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
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
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!Person.class.isInstance(obj)) {
			return false;
		}

		final Person otherObj = (Person) obj;

		return new EqualsBuilder().append(this.id, otherObj.id).isEquals();
	}

	@Override
	public int compareTo(final Person o) {
		if (this.getDisplayName() != null && o != null) {
			return this.getDisplayName().compareTo(o.getDisplayName());
		}
		return 0;
	}

	public Set<CropType> getCrops() {
		return crops;
	}

	public void setCrops(final Set<CropType> crops) {
		this.crops = crops;
	}
}
