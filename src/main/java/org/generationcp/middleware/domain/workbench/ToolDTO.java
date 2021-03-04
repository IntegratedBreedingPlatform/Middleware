package org.generationcp.middleware.domain.workbench;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.List;

@AutoProperty
public class ToolDTO {

	private String name;
	private List<ToolLinkDTO> children;

	public ToolDTO() {
	}

	public ToolDTO(final String name, final List<ToolLinkDTO> children) {
		this.name = name;
		this.children = children;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public List<ToolLinkDTO> getChildren() {
		return children;
	}

	public void setChildren(final List<ToolLinkDTO> children) {
		this.children = children;
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
