
package org.generationcp.middleware.pojos.workbench;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
	private CropType cropName;

	@ManyToOne
	@JoinColumn(name = "workbench_project_id", nullable = true)
	private Project workbenchProject;

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

	public UserRole(final WorkbenchUser user, final Role role, final CropType cropName, final Project workbenchProject) {
		this.user = user;
		this.role = role;
		this.cropName = cropName;
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

	public CropType getCropName() {
		return this.cropName;
	}

	public void setCropName(final CropType cropName) {
		this.cropName = cropName;
	}

	public Project getWorkbenchProject() {
		return this.workbenchProject;
	}

	public void setWorkbenchProject(final Project workbenchProject) {
		this.workbenchProject = workbenchProject;
	}

	@Override
	public String toString() {
		return "UserRole [User=" + this.user.getUserid() + ", Role=" + this.role + "]";
	}

	public String getCapitalizedRole() {
		return this.getRole().getCapitalizedRole();
	}

}
