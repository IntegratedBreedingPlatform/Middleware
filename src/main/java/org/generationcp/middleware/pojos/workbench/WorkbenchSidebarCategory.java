
package org.generationcp.middleware.pojos.workbench;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: cyrus Date: 11/20/13 Time: 5:42 PM To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "workbench_sidebar_category")
public class WorkbenchSidebarCategory implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@GeneratedValue
	@Column(name = "sidebar_category_id")
	private Integer sidebarCategoryId;

	@Column(name = "sidebar_category_name")
	private String sidebarCategoryName;

	@Column(name = "sidebar_category_label")
	private String sidebarCategorylabel;

	@OneToMany(mappedBy = "workbenchSidebarCategory", cascade = CascadeType.ALL)
	private List<WorkbenchSidebarCategoryLink> workbenchSidebarCategoryLinks;

	public WorkbenchSidebarCategory() {
	}

	public WorkbenchSidebarCategory(final String sidebarCategoryName, final String sidebarCategorylabel) {
		this.sidebarCategoryName = sidebarCategoryName;
		this.sidebarCategorylabel = sidebarCategorylabel;
	}

	public Integer getSidebarCategoryId() {
		return this.sidebarCategoryId;
	}

	public void setSidebarCategoryId(final Integer sidebarCategoryId) {
		this.sidebarCategoryId = sidebarCategoryId;
	}

	public String getSidebarCategoryName() {
		return this.sidebarCategoryName;
	}

	public void setSidebarCategoryName(final String sidebarCategoryName) {
		this.sidebarCategoryName = sidebarCategoryName;
	}

	public String getSidebarCategorylabel() {
		return this.sidebarCategorylabel;
	}

	public void setSidebarCategorylabel(final String sidebarCategorylabel) {
		this.sidebarCategorylabel = sidebarCategorylabel;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public List<WorkbenchSidebarCategoryLink> getWorkbenchSidebarCategoryLinks() {
		return this.workbenchSidebarCategoryLinks;
	}

	public void setWorkbenchSidebarCategoryLinks(
		final List<WorkbenchSidebarCategoryLink> workbenchSidebarCategoryLinks) {
		this.workbenchSidebarCategoryLinks = workbenchSidebarCategoryLinks;
	}
}
