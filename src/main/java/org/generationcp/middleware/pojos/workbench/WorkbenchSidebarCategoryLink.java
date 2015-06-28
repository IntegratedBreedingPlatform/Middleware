
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

/**
 * Created with IntelliJ IDEA. User: cyrus Date: 11/20/13 Time: 5:49 PM To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "workbench_sidebar_category_link")
public class WorkbenchSidebarCategoryLink implements Serializable {

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
}
