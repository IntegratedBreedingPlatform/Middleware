package org.generationcp.middleware.domain.workbench;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.generationcp.middleware.pojos.workbench.Permission;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
public class PermissionDto {

	private Integer id;

	private String name;

	private String description;

	private Integer parentId;

	@JsonIgnore
	private Integer workbenchCategoryLinkId;

	private List<PermissionDto> children;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Boolean selectable;

	public PermissionDto() {
	}

	public PermissionDto(final Permission permission) {
		this.id = permission.getPermissionId();
		this.description = permission.getDescription();
		this.name = permission.getName();
	}

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(final Integer parentId) {
		this.parentId = parentId;
	}

	public Integer getWorkbenchCategoryLinkId() {
		return workbenchCategoryLinkId;
	}

	public void setWorkbenchCategoryLinkId(final Integer workbenchCategoryLinkId) {
		this.workbenchCategoryLinkId = workbenchCategoryLinkId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public List<PermissionDto> getChildren() {
		return children;
	}

	public void setChildren(final List<PermissionDto> children) {
		this.children = children;
	}

	public Boolean getSelectable() {
		return selectable;
	}

	public void setSelectable(final Boolean selectable) {
		this.selectable = selectable;
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}
}
