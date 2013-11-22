package org.generationcp.middleware.pojos.workbench;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: cyrus
 * Date: 11/20/13
 * Time: 5:42 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name="workbench_sidebar_category")
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

    public WorkbenchSidebarCategory() {
    }

    public WorkbenchSidebarCategory(String sidebarCategoryName, String sidebarCategorylabel) {
        this.sidebarCategoryName = sidebarCategoryName;
        this.sidebarCategorylabel = sidebarCategorylabel;
    }

    public Integer getSidebarCategoryId() {
        return sidebarCategoryId;
    }

    public void setSidebarCategoryId(Integer sidebarCategoryId) {
        this.sidebarCategoryId = sidebarCategoryId;
    }

    public String getSidebarCategoryName() {
        return sidebarCategoryName;
    }

    public void setSidebarCategoryName(String sidebarCategoryName) {
        this.sidebarCategoryName = sidebarCategoryName;
    }

    public String getSidebarCategorylabel() {
        return sidebarCategorylabel;
    }

    public void setSidebarCategorylabel(String sidebarCategorylabel) {
        this.sidebarCategorylabel = sidebarCategorylabel;
    }
}
