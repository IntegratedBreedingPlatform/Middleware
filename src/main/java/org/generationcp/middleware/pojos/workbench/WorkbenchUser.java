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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.generationcp.middleware.domain.workbench.PermissionDto;
import org.generationcp.middleware.domain.workbench.RoleType;
import org.generationcp.middleware.pojos.BeanFormState;
import org.generationcp.middleware.pojos.Person;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * POJO for users table in Workbench Database.
 * It differs from users in crop dbs as there are no
 * users_roles and role table and therefore relation in crop DBs
 */
@NamedQueries({
	@NamedQuery(name = "getUserByNameUsingEqual", query = "SELECT s FROM WorkbenchUser s WHERE s.name = :name"),
	@NamedQuery(name = "getUserByNameUsingLike", query = "SELECT s FROM WorkbenchUser s WHERE s.name LIKE :name"),
	@NamedQuery(name = "countUserByNameUsingEqual", query = "SELECT COUNT(s) FROM WorkbenchUser s WHERE s.name = :name"),
	@NamedQuery(name = "countUserByNameUsingLike", query = "SELECT COUNT(s) FROM WorkbenchUser s WHERE s.name LIKE :name"),
	@NamedQuery(name = "getByFullName", query = "SELECT u FROM WorkbenchUser u, Person p WHERE u.status = 0 AND u.person.id = p.id AND "
		+ "(CONCAT(p.firstName, ' ', p.middleName, ' ', p.lastName) = :fullname OR CONCAT(p.firstName, ' ', p.lastName) = :fullname)"),
	@NamedQuery(name = "countByFullName", query =
		"SELECT COUNT(u) FROM WorkbenchUser u, Person p WHERE u.status = 0 AND u.person.id = p.id AND "
			+ "(CONCAT(p.firstName, ' ', p.middleName, ' ', p.lastName) = :fullname OR CONCAT(p.firstName, ' ', p.lastName) = :fullname)")

})
@Entity
@Table(name = "users")
public class WorkbenchUser implements Serializable, BeanFormState {

	private static final long serialVersionUID = 1L;

	public static final String GET_USERS_BY_PROJECT_ID = "SELECT  "
		+ "  users.userid, "
		+ "  users.instalid, "
		+ "  users.ustatus, "
		+ "  users.uaccess, "
		+ "  users.utype, "
		+ "  users.uname, "
		+ "  users.upswd, "
		+ "  users.personid, "
		+ "  users.adate, "
		+ "  users.cdate, "
		+ "  pr.fname, "
		+ "  pr.lname "
		+ "    FROM "
		+ "       workbench_project p "
		+ "           INNER JOIN "
		+ "       crop_persons cp ON cp.crop_name = p.crop_type "
		+ "           INNER JOIN "
		+ "       users ON cp.personid = users.personid "
		+ "           INNER JOIN "
		+ "       persons pr ON pr.personid = users.personid "
		+ "           INNER JOIN "
		+ "       users_roles ur ON ur.userid = users.userid "
		+ "           INNER JOIN role r ON ur.role_id = r.id  "
		+ "   where  (r.role_type_id =  " + RoleType.INSTANCE.getId()
		+ "     or (r.role_type_id = " + RoleType.CROP.getId() + " and ur.crop_name = p.crop_type)  "
		+ "     or (r.role_type_id =  " + RoleType.PROGRAM.getId()
		+ " and ur.crop_name = p.crop_type AND ur.workbench_project_id = p.project_id))  "
		+ "    AND "
		+ "       p.project_id = :projectId "
		+ "    GROUP BY users.userid";

	//User with access to a program is the union of:
	//1. Users with Instance role
	//2. Users with crop role and no program role assigned
	//3. Users with explicit access to the program via program role
	public static final String GET_ACTIVE_USER_IDS_WITH_ACCESS_TO_A_PROGRAM =
		"select distinct u.userid " //
			+ "from users u " //
			+ "       inner join crop_persons cp on cp.personid = u.personid " //
			+ "       inner join users_roles ur on ur.userid = u.userid " //
			+ "       inner join role r on ur.role_id = r.id " //
			+ "       left join workbench_project wp on ur.workbench_project_id = wp.project_id " //
			+ "where ((role_type_id = " + RoleType.INSTANCE.getId() + ") " //
			+ "  || (role_type_id = " + RoleType.CROP.getId()
			+ " and ur.crop_name = cp.crop_name and not exists(SELECT distinct p1.project_id " //
			+ "                                      FROM workbench_project p1 " //
			+ "                                             INNER JOIN " //
			+ "                                      users_roles ur1 ON ur1.workbench_project_id = p1.project_id " //
			+ "                                             INNER JOIN role r1 ON ur1.role_id = r1.id " //
			+ "                                      where r1.role_type_id = " + RoleType.PROGRAM.getId() //
			+ "                                        AND ur1.crop_name = cp.crop_name AND ur1.userid = u.userid) "
			//
			+ "    || (role_type_id= " + RoleType.PROGRAM.getId() + " and wp.project_id = :projectId))) and " //
			+ "  u.ustatus = 0 and cp.crop_name = (select wpi.crop_type from workbench_project wpi where wpi.project_id = :projectId) ";

	public static final String GET_BY_NAME_USING_EQUAL = "getUserByNameUsingEqual";
	public static final String GET_BY_NAME_USING_LIKE = "getUserByNameUsingLike";
	public static final String GET_BY_FULLNAME = "getByFullName";
	public static final String COUNT_BY_FULLNAME = "countByFullName";

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

	@Column(name = "adate")
	private Integer adate;

	@Column(name = "cdate")
	private Integer cdate;

	@Fetch(FetchMode.SUBSELECT)
	@OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@NotFound(action = NotFoundAction.IGNORE)
	private List<UserRole> roles;

	@Transient
	private Boolean isnew = false;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "personid")
	@NotFound(action = NotFoundAction.IGNORE)
	private Person person;

	@Transient
	private Boolean active = false;

	@Transient
	private Boolean enabled = true;

	@Transient
	private List<PermissionDto> permissions = new ArrayList<>();

	public WorkbenchUser() {
	}

	public WorkbenchUser(final Integer userid) {
		super();
		this.userid = userid;
	}

	public WorkbenchUser(
		final Integer userid, final Integer instalid, final Integer status, final Integer access, final Integer type,
		final String name, final String password,
		final Person person, final Integer adate, final Integer cdate) {
		super();
		this.userid = userid;
		this.instalid = instalid;
		this.status = status;
		this.access = access;
		this.type = type;
		this.name = name;
		this.password = password;
		this.person = person;
		this.adate = adate;
		this.cdate = cdate;

	}

	/**
	 * Get a copy of this {@link WorkbenchUser} object. Note that this method will not copy the {@link WorkbenchUser#userid} field.
	 *
	 * @return the copy of the User object
	 */
	public WorkbenchUser copy() {
		final WorkbenchUser user = new WorkbenchUser();
		user.setInstalid(this.instalid);
		user.setStatus(this.status);
		user.setAccess(this.access);
		user.setType(this.type);
		user.setName(this.name);
		user.setPassword(this.password);
		user.setPerson(this.person);
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

	public void setUserid(final Integer userid) {
		this.userid = userid;
	}

	public Integer getInstalid() {
		return this.instalid;
	}

	public void setInstalid(final Integer instalid) {
		this.instalid = instalid;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(final Integer status) {
		this.status = status;
	}

	public Integer getAccess() {
		return this.access;
	}

	public void setAccess(final Integer access) {
		this.access = access;
	}

	public Integer getType() {
		return this.type;
	}

	public void setType(final Integer type) {
		this.type = type;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public Integer getAssignDate() {
		return this.adate;
	}

	public void setAssignDate(final Integer adate) {
		this.adate = adate;
	}

	public Integer getCloseDate() {
		return this.cdate;
	}

	public void setCloseDate(final Integer cdate) {
		this.cdate = cdate;
	}

	public Person getPerson() {
		return this.person;
	}

	public void setPerson(final Person person) {
		this.person = person;
	}

	public Boolean isNew() {
		return this.isnew;
	}

	public void setIsNew(final Boolean val) {
		this.isnew = val;
	}

	public List<UserRole> getRoles() {
		return this.roles;
	}

	public void setRoles(final List<UserRole> roles) {
		this.roles = roles;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.userid).hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof WorkbenchUser)) {
			return false;
		}

		final WorkbenchUser otherObj = (WorkbenchUser) obj;

		return new EqualsBuilder().append(this.userid, otherObj.userid).isEquals();
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
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
	public void setActive(final Boolean val) {
		this.active = val;
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

	@Override
	public void setEnabled(final Boolean val) {
		this.enabled = val;
	}

	public List<PermissionDto> getPermissions() {
		return this.permissions;
	}

	public void setPermissions(final List<PermissionDto> permissions) {
		this.permissions = permissions;
	}

	public boolean isSuperAdmin() {
		if (this.roles == null) {
			return false;
		}
		for (final UserRole userRole : this.roles) {
			if (userRole.getRole().getName().toUpperCase().equals(Role.SUPERADMIN)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasInstanceRole() {
		if (this.roles == null) {
			return false;
		}
		for (final UserRole userRole : this.roles) {
			if (userRole.getRole().getRoleType().getId().equals(RoleType.INSTANCE.getId())) {
				return true;
			}
		}
		return false;
	}

	public UserRole getInstanceRole() {
		if (this.roles == null) {
			return null;
		}
		for (final UserRole userRole : this.roles) {
			if (userRole.getRole().getRoleType().getId().equals(RoleType.INSTANCE.getId())) {
				return userRole;
			}
		}
		return null;
	}

	public boolean hasCropRole(final String crop) {
		if (this.roles == null) {
			return false;
		}
		for (final UserRole userRole : this.roles) {
			if (userRole.getRole().getRoleType().getId().equals(RoleType.CROP.getId()) && userRole.getCropType().getCropName()
				.equalsIgnoreCase(crop)) {
				return true;
			}
		}
		return false;
	}

	public UserRole getCropRole(final String crop) {
		if (this.roles == null) {
			return null;
		}
		for (final UserRole userRole : this.roles) {
			if (userRole.getRole().getRoleType().getId().equals(RoleType.CROP.getId()) && userRole.getCropType().getCropName()
				.equalsIgnoreCase(crop)) {
				return userRole;
			}
		}
		return null;
	}

	public boolean hasProgramRoles(final String crop) {
		if (this.roles == null) {
			return false;
		}
		for (final UserRole userRole : this.roles) {
			if (userRole.getRole().getRoleType().getId().equals(RoleType.PROGRAM.getId()) && userRole.getCropType().getCropName()
				.equalsIgnoreCase(crop)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasAccessToAGivenProgram(final String cropName, final Long programId) {
		if (this.roles == null) {
			return false;
		}
		if (this.hasInstanceRole() || (this.hasCropRole(cropName) && !this.hasProgramRoles(cropName)) || this.getRoles().stream()
			.anyMatch(
				ur -> ur.getRole().getRoleType().getId().equals(RoleType.PROGRAM.getId()) && ur.getCropType().getCropName()
					.equalsIgnoreCase(cropName)
					&& ur.getWorkbenchProject().getProjectId().equals((programId)))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param cropName
	 * @return True if the user has only program role types for the specified cropName
	 */
	public boolean hasOnlyProgramRoles(final String cropName) {
		if (this.roles == null) {
			return false;
		}
		boolean hasProgramRole = false;
		for (final UserRole userRole : this.roles) {
			final Integer roleTypeId = userRole.getRole().getRoleType().getId();
			if (roleTypeId == null || roleTypeId.equals(RoleType.INSTANCE.getId())) {
				return false;
			} else if (userRole.getCropType().getCropName().equals(cropName)) {
				if (roleTypeId.equals(RoleType.CROP.getId())) {
					return false;
				} else if (roleTypeId.equals(RoleType.PROGRAM.getId())) {
					hasProgramRole = true;
				}
			}
		}
		return hasProgramRole;
	}
}
