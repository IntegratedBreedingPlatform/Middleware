
package org.generationcp.middleware.pojos.workbench;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "workbench_sidebar_category_link_role")
public class WorkbenchSidebarCategoryLinkRole implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@GeneratedValue
	@Column(name = "sidebar_category_link_role_id")
	private Integer sidebarCategoryLinkRoleId;

	@ManyToOne(targetEntity = WorkbenchSidebarCategoryLink.class)
	@JoinColumn(name = "sidebar_category_link_id", nullable = false)
	private WorkbenchSidebarCategoryLink sidebarLink;

	@ManyToOne(targetEntity = Role.class)
	@JoinColumn(name = "role_id", nullable = false)
	private Role role;

	public WorkbenchSidebarCategoryLinkRole() {
	}

	public WorkbenchSidebarCategoryLinkRole(WorkbenchSidebarCategoryLink link, Role role) {
		super();
		this.sidebarLink = link;
		this.role = role;
	}

	
	public WorkbenchSidebarCategoryLink getLink() {
		return sidebarLink;
	}

	
	public void setLink(WorkbenchSidebarCategoryLink link) {
		this.sidebarLink = link;
	}

	
	public Role getRole() {
		return role;
	}

	
	public void setRole(Role role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "WorkbenchSidebarCategoryLinkRole [sidebarCategoryLinkRoleId=" + sidebarCategoryLinkRoleId + ", sidebarLink=" + sidebarLink.getSidebarLinkTitle()
				+ ", role=" + role + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sidebarCategoryLinkRoleId == null) ? 0 : sidebarCategoryLinkRoleId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WorkbenchSidebarCategoryLinkRole other = (WorkbenchSidebarCategoryLinkRole) obj;
		if (sidebarCategoryLinkRoleId == null) {
			if (other.sidebarCategoryLinkRoleId != null)
				return false;
		} else if (!sidebarCategoryLinkRoleId.equals(other.sidebarCategoryLinkRoleId))
			return false;
		return true;
	}
	
}
