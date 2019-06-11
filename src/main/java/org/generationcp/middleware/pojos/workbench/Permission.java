
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
@Table(name = "permission")
public class Permission {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "permission_id", nullable = false)
	private Integer permissionId;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "description", nullable = false)
	private String description;

	@ManyToOne
	@JoinColumn(name = "parent_id", nullable = true)
	private Permission parent;

	@Column(name = "workbench_sidebar_category_link_id", nullable = true)
	private WorkbenchSidebarCategoryLink sidebarCategoryLink;

	public Integer getPermissionId() {
		return permissionId;
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

	public WorkbenchSidebarCategoryLink getSidebarCategoryLink() {
		return this.sidebarCategoryLink;
	}

	public void setSidebarCategoryLink(final WorkbenchSidebarCategoryLink sidebarCategoryLink) {
		this.sidebarCategoryLink = sidebarCategoryLink;
	}
}
