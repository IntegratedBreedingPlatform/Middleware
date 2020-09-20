
package org.generationcp.middleware.pojos.workbench;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.generationcp.middleware.domain.workbench.PermissionDto;
import org.hibernate.annotations.BatchSize;

@Entity
@Table(name = "workbench_sidebar_category_link")
public class WorkbenchSidebarCategoryLink implements Serializable, Comparable<WorkbenchSidebarCategoryLink> {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@GeneratedValue
	@Column(name = "sidebar_category_link_id")
	private Integer sidebarCategoryLinkId;

	@ManyToOne(targetEntity = Tool.class)
	@JoinColumn(name = "tool_name", referencedColumnName = "name", nullable = false)
	private Tool tool;

	@ManyToOne(targetEntity = WorkbenchSidebarCategory.class)
	@JoinColumn(name = "sidebar_category_id", nullable = false)
	private WorkbenchSidebarCategory workbenchSidebarCategory;

	@Column(name = "sidebar_link_name")
	private String sidebarLinkName;

	@Column(name = "sidebar_link_title")
	private String sidebarLinkTitle;

	public WorkbenchSidebarCategoryLink() {
	}

	public WorkbenchSidebarCategoryLink(Tool tool, WorkbenchSidebarCategory workbenchSidebarCategory, String sidebarLinkName,
			String sidebarLinkTitle) {
		this.tool = tool;
		this.workbenchSidebarCategory = workbenchSidebarCategory;
		this.sidebarLinkName = sidebarLinkName;
		this.sidebarLinkTitle = sidebarLinkTitle;
	}

	public String getSidebarLinkName() {
		return this.sidebarLinkName;
	}

	public void setSidebarLinkName(String sidebarLinkName) {
		this.sidebarLinkName = sidebarLinkName;
	}

	public String getSidebarLinkTitle() {
		return this.sidebarLinkTitle;
	}

	public void setSidebarLinkTitle(String sidebarLinkTitle) {
		this.sidebarLinkTitle = sidebarLinkTitle;
	}

	public Integer getSidebarCategoryLinkId() {
		return this.sidebarCategoryLinkId;
	}

	public void setSidebarCategoryLinkId(Integer sidebarCategoryLinkId) {
		this.sidebarCategoryLinkId = sidebarCategoryLinkId;
	}

	public Tool getTool() {
		return this.tool;
	}

	public void setTool(Tool tool) {
		this.tool = tool;
	}

	public WorkbenchSidebarCategory getWorkbenchSidebarCategory() {
		return this.workbenchSidebarCategory;
	}

	public void setWorkbenchSidebarCategory(WorkbenchSidebarCategory workbenchSidebarCategory) {
		this.workbenchSidebarCategory = workbenchSidebarCategory;
	}


	@Override
	public String toString() {
		return "WorkbenchSidebarCategoryLink [sidebarCategoryLinkId=" + sidebarCategoryLinkId + ", tool=" + tool
				+ ", workbenchSidebarCategory=" + workbenchSidebarCategory + ", sidebarLinkName=" + sidebarLinkName + ", sidebarLinkTitle="
				+ sidebarLinkTitle + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sidebarCategoryLinkId == null) ? 0 : sidebarCategoryLinkId.hashCode());
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
		WorkbenchSidebarCategoryLink other = (WorkbenchSidebarCategoryLink) obj;
		if (sidebarCategoryLinkId == null) {
			if (other.sidebarCategoryLinkId != null)
				return false;
		} else if (!sidebarCategoryLinkId.equals(other.sidebarCategoryLinkId))
			return false;
		return true;
	}

	@Override
	public int compareTo(WorkbenchSidebarCategoryLink d) {
		return this.sidebarCategoryLinkId - d.getSidebarCategoryLinkId();
	}
}
