
package org.generationcp.middleware.pojos.workbench;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

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

	@ManyToOne
	@JoinColumn(name = "workbench_sidebar_category_link_id", nullable = true)
	private WorkbenchSidebarCategoryLink sidebarCategoryLink;

	@Fetch(FetchMode.SUBSELECT)
	@OneToMany(mappedBy = "permission", fetch = FetchType.EAGER)
	private final List<RoleTypePermission> roleTypePermissions = new ArrayList<>();

	@Column(name = "rank", nullable = false)
	private Integer rank;

	public List<RoleTypePermission> getRoleTypePermissions() {
		return this.roleTypePermissions;
	}

	public Integer getPermissionId() {
		return this.permissionId;
	}

	public void setPermissionId(final Integer permissionId) {
		this.permissionId = permissionId;
	}

	public Integer getRank() {
		return this.rank;
	}

	public void setRank(final Integer rank) {
		this.rank = rank;
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
