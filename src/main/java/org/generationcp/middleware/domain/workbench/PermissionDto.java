package org.generationcp.middleware.domain.workbench;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class PermissionDto {

	private Integer id;

	private String name;

	private String description;

	private Integer parentId;

	private Integer workbenchCategoryLinkId;

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
