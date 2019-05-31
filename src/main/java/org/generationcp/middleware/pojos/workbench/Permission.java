
package org.generationcp.middleware.pojos.workbench;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "permission")
public class Permission {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "permission_id", nullable = false)
	private Integer permissionId;

	@Column(name = "userid", nullable = false)
	private String name;

	@Column(name = "description", nullable = false)
	private String description;

	@ManyToOne
	@JoinColumn(name = "parent_id", nullable = true)
	private Permission parent;

	@OneToMany
	@JoinColumn(name = "workbench_sidebar_category_id", nullable = true)
	private List<WorkbenchSidebarCategory> sidebarCategory;

	public Integer getPermissionId() {
		return this.permissionId;
	}

	public void setPermissionId(final Integer permissionId) {
		this.permissionId = permissionId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public Permission getParent() {
		return this.parent;
	}

	public void setParent(final Permission parent) {
		this.parent = parent;
	}

	public List<WorkbenchSidebarCategory> getSidebarCategory() {
		return this.sidebarCategory;
	}

	public void setSidebarCategory(final List<WorkbenchSidebarCategory> sidebarCategory) {
		this.sidebarCategory = sidebarCategory;
	}
}
