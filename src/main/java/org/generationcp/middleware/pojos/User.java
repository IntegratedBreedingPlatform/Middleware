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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * POJO for users table.
 *
 */
@NamedQueries({
	@NamedQuery(name = "getByFullName",
	query = "SELECT u FROM User u, Person p WHERE u.personid = p.id AND (CONCAT(p.firstName, ' ', p.middleName, ' ', p.lastName) = :fullname OR CONCAT(p.firstName, ' ', p.lastName) = :fullname)")
})
@Entity
@Table(name = "users")
public class User implements Serializable, BeanFormState {

	private static final long serialVersionUID = 1L;
	
	public static final String GET_BY_FULLNAME = "getByFullName";

	public static final String GET_USERS_ASSOCIATED_TO_STUDY = "SELECT DISTINCT \n"
			+ "  person.personid AS personId, \n"
			+ "  person.fname    AS fName, \n"
			+ "  person.lname    AS lName, \n"
			+ "  person.pemail   AS email, \n"
			+ "  role.role_id    AS roleId \n"
			+ "  role.name       AS roleName \n"
			+ "FROM cvterm scale INNER JOIN cvterm_relationship r ON (r.object_id = scale.cvterm_id) \n"
			+ "  INNER JOIN cvterm variable ON (r.subject_id = variable.cvterm_id) \n"
			+ "  INNER JOIN projectprop pp ON (pp.variable_id = variable.cvterm_id) \n"
			+ "  INNER JOIN workbench.persons person ON (pp.value = person.personid) \n"
			+ "  INNER JOIN workbench.users user ON (user.personid = person.personid) \n"
			+ "  LEFT JOIN workbench.users_roles urole ON (urole.userid = user.userid) \n"
			+ "  LEFT JOIN workbench.role ON (role.role_id = urole.role_id) \n"
			+ "WHERE pp.project_id = :studyId AND r.object_id = 1901";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "userid")
	private Integer userid;

	@Column(name = "instalid")
	private Integer instalid;

	@Column(name = "ustatus")
	private Integer status;

	@Column(name = "uaccess")
	private Integer access;

	@Column(name = "utype")
	private Integer type;

	@Column(name = "uname")
	private String name;

	@Column(name = "upswd")
	private String password;

	@Column(name = "personid")
	private Integer personid;

	@Column(name = "adate")
	private Integer adate;

	@Column(name = "cdate")
	private Integer cdate;

	@Transient
	private Boolean isnew = false;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="personid", insertable=false, updatable=false)
	@NotFound(action = NotFoundAction.IGNORE)
	private Person person;

	@Transient
	private Boolean active = false;

	@Transient
	private Boolean enabled = true;

	public User() {
	}

	public User(Integer userid) {
		super();
		this.userid = userid;
	}

	public User(Integer userid, Integer instalid, Integer status, Integer access, Integer type, String name, String password,
			Integer personid, Integer adate, Integer cdate) {
		super();
		this.userid = userid;
		this.instalid = instalid;
		this.status = status;
		this.access = access;
		this.type = type;
		this.name = name;
		this.password = password;
		this.personid = personid;
		this.adate = adate;
		this.cdate = cdate;

	}

	/**
	 * Get a copy of this {@link User} object. Note that this method will not copy the {@link User#userid} field.
	 * 
	 * @return the copy of the User object
	 */
	public User copy() {
		User user = new User();
		user.setInstalid(this.instalid);
		user.setStatus(this.status);
		user.setAccess(this.access);
		user.setType(this.type);
		user.setName(this.name);
		user.setPassword(this.password);
		user.setPersonid(this.personid);
		user.setAssignDate(this.adate);
		user.setCloseDate(this.cdate);
		user.setIsNew(this.isnew);
		user.setActive(this.active);
		user.setEnabled(this.enabled);
		return user;
	}

	public Integer getUserid() {
		return this.userid;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
	}

	public Integer getInstalid() {
		return this.instalid;
	}

	public void setInstalid(Integer instalid) {
		this.instalid = instalid;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getAccess() {
		return this.access;
	}

	public void setAccess(Integer access) {
		this.access = access;
	}

	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getPersonid() {
		return this.personid;
	}

	public void setPersonid(Integer personid) {
		this.personid = personid;
	}

	public Integer getAssignDate() {
		return this.adate;
	}

	public void setAssignDate(Integer adate) {
		this.adate = adate;
	}

	public Integer getCloseDate() {
		return this.cdate;
	}

	public void setCloseDate(Integer cdate) {
		this.cdate = cdate;
	}

	public Person getPerson() {
		return this.person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Boolean isNew() {
		return this.isnew;
	}

	public void setIsNew(Boolean val) {
		this.isnew = val;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.userid).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!User.class.isInstance(obj)) {
			return false;
		}

		User otherObj = (User) obj;

		return new EqualsBuilder().append(this.userid, otherObj.userid).isEquals();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("User [userid=");
		builder.append(this.userid);
		builder.append(", instalid=");
		builder.append(this.instalid);
		builder.append(", status=");
		builder.append(this.status);
		builder.append(", access=");
		builder.append(this.access);
		builder.append(", type=");
		builder.append(this.type);
		builder.append(", name=");
		builder.append(this.name);
		builder.append(", password=");
		builder.append(this.password);
		builder.append(", personid=");
		builder.append(this.personid);
		builder.append(", adate=");
		builder.append(this.adate);
		builder.append(", cdate=");
		builder.append(this.cdate);
		builder.append(", person=");
		builder.append(this.person);
		builder.append(", isnew=");
		builder.append(this.isnew);
		builder.append(", isActive=");
		builder.append(this.active);
		builder.append(", isEnabled=");
		builder.append(this.enabled);

		builder.append("]");
		return builder.toString();
	}

	@Override
	public boolean isActive() {
		return this.active;
	}

	@Override
	public void setActive(Boolean val) {
		this.active = val;
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

	@Override
	public void setEnabled(Boolean val) {
		this.enabled = val;

	}

}
