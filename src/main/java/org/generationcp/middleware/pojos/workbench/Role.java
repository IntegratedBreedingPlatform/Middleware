
package org.generationcp.middleware.pojos.workbench;

import org.apache.commons.lang.WordUtils;
import org.hibernate.annotations.Type;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "role")
@AutoProperty
public class Role implements Serializable {

	public static final String ADMIN = "ADMIN";
	public static final String SUPERADMIN = "SUPERADMIN";

	private static final long serialVersionUID = 7981410876951478010L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	private Integer id;

	@Column(name = "description", nullable = false)
	private String description;

	@Column(name = "name", nullable = false)
	private String name;

	@ManyToOne
	@JoinColumn (name = "role_type_id")
	private RoleType roleType;

	@Type(type = "org.hibernate.type.NumericBooleanType")
	@Basic(optional = false)
	@Column(name = "active", columnDefinition = "TINYINT")
	private Boolean active;

	@Type(type = "org.hibernate.type.NumericBooleanType")
	@Basic(optional = false)
	@Column(name = "editable", columnDefinition = "TINYINT")
	private Boolean editable;

	@Type(type = "org.hibernate.type.NumericBooleanType")
	@Basic(optional = false)
	@Column(name = "assignable", columnDefinition = "TINYINT")
	private Boolean assignable;

	@ManyToOne
	@JoinColumn(name = "created_by", nullable = true)
	private WorkbenchUser createdBy;

	@Column(name = "updated_date")
	private Timestamp updatedDate;

	@ManyToOne
	@JoinColumn(name = "updated_by", nullable = true)
	private WorkbenchUser updatedBy;

	@Column(name = "created_date")
	private Timestamp createdDate;

	public Role() {
	}

	public Role(final String description, final String name) {
		this.description = description;
		this.name = name;
	}

	public Role(final Integer id) {
		this.id = id;
	}

	public Role(final String name) {
		this.name = name;
	}

	public Role(final Integer id, final String name) {
		this.id = id;
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public RoleType getRoleType() {
		return this.roleType;
	}

	public void setRoleType(final RoleType roleType) {
		this.roleType = roleType;
	}

	public Boolean getActive() {
		return this.active;
	}

	public void setActive(final Boolean active) {
		this.active = active;
	}

	public Boolean getEditable() {
		return this.editable;
	}

	public void setEditable(final Boolean editable) {
		this.editable = editable;
	}

	public Boolean getAssignable() {
		return this.assignable;
	}

	public void setAssignable(final Boolean assignable) {
		this.assignable = assignable;
	}

	public WorkbenchUser getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(final WorkbenchUser createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getUpdatedDate() {
		return this.updatedDate;
	}

	public void setUpdatedDate(final Timestamp updatedDate) {
		this.updatedDate = updatedDate;
	}

	public WorkbenchUser getUpdatedBy() {
		return this.updatedBy;
	}

	public void setUpdatedBy(final WorkbenchUser updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Timestamp getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(final Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public static String getADMIN() {
		return ADMIN;
	}

	public static String getSUPERADMIN() {
		return SUPERADMIN;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getCapitalizedRole() {
		return WordUtils.capitalize(this.getName().toUpperCase());
	}

	public boolean isSuperAdminUser() {
		return SUPERADMIN.equalsIgnoreCase(this.name);
	}

}
