package org.generationcp.middleware.pojos.workbench;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "role")
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
	private Date updatedDate;

	@ManyToOne
	@JoinColumn(name = "updated_by", nullable = true)
	private WorkbenchUser updatedBy;

	@Column(name = "created_date")
	private Date createdDate;

	@Fetch(FetchMode.SUBSELECT)
	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(
		name = "role_permission",
		joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
		inverseJoinColumns = @JoinColumn(name = "permission_id"))
	private List<Permission> permissions = new ArrayList<>();

	@Fetch(FetchMode.SUBSELECT)
	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "users_roles",
			joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "id"))
	private List<UserRole> userRoles = new ArrayList<>();

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

	public Date getUpdatedDate() {
		return this.updatedDate;
	}

	public void setUpdatedDate(final Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public WorkbenchUser getUpdatedBy() {
		return this.updatedBy;
	}

	public void setUpdatedBy(final WorkbenchUser updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(final Date createdDate) {
		this.createdDate = createdDate;
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

	public List<Permission> getPermissions() {
		return this.permissions;
	}

	public void setPermissions(final List<Permission> permissions) {
		this.permissions = permissions;
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
		if (!Role.class.isInstance(obj)) {
			return false;
		}

		final Role otherObj = (Role) obj;

		return new EqualsBuilder().append(this.id, otherObj.id).isEquals();
	}

	@Override
	public String toString() {
		return "Role{" + "roleId=" + this.id + ", description='" + this.description + '\'' + ", name='" + this.name
			+ '\'' + ", roleType=" + this.roleType + ", active=" + this.active + ", editable=" + this.editable + ", assignable="
			+ this.assignable + ", createdBy=" + this.createdBy + ", createdDate=" + this.createdBy + ", updatedBy=" + this.updatedBy +
			", updatedDate=" + this.updatedDate + '}';
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

	public List<UserRole> getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(final List<UserRole> userRoles) {
		this.userRoles = userRoles;
	}
}
