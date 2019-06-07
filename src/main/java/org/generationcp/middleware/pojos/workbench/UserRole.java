
package org.generationcp.middleware.pojos.workbench;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "users_roles")
public class UserRole {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "userid", nullable = false)
	private WorkbenchUser user;

	@ManyToOne
	@JoinColumn(name = "role_id", nullable = false)
	private Role role;

	@ManyToOne
	@JoinColumn(name = "crop_name", nullable = true)
	private CropType cropType;

	@ManyToOne
	@JoinColumn(name = "workbench_project_id", nullable = true)
	private Project workbenchProject;

	@ManyToOne
	@JoinColumn(name = "created_by", nullable = true)
	private WorkbenchUser createdBy;

	@Column(name = "created_date")
	private Timestamp createdDate;

	public UserRole() {
	}

	public UserRole(final WorkbenchUser user, final Integer roleId) {
		this.user = user;
		this.role = new Role(roleId);
	}

	public UserRole(final WorkbenchUser user, final Role role) {
		this.user = user;
		this.role = role;
	}

	public UserRole(final WorkbenchUser user, final Role role, final CropType cropType, final Project workbenchProject) {
		this.user = user;
		this.role = role;
		this.cropType = cropType;
		this.workbenchProject = workbenchProject;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public WorkbenchUser getUser() {
		return this.user;
	}

	public void setUser(final WorkbenchUser user) {
		this.user = user;
	}

	public Role getRole() {
		return this.role;
	}

	public void setRole(final Role role) {
		this.role = role;
	}

	public CropType getCropType() {
		return this.cropType;
	}

	public void setCropType(final CropType cropName) {
		this.cropType = cropName;
	}

	public Project getWorkbenchProject() {
		return this.workbenchProject;
	}

	public void setWorkbenchProject(final Project workbenchProject) {
		this.workbenchProject = workbenchProject;
	}

	public WorkbenchUser getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(final WorkbenchUser createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(final Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	@Override
	public String toString() {
		return "UserRole [User=" + this.user.getUserid() + ", Role=" + this.role + "]";
	}

	public String getCapitalizedRole() {
		return this.getRole().getCapitalizedRole();
	}

}
