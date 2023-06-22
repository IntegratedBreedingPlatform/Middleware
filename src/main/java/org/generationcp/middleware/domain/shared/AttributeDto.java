package org.generationcp.middleware.domain.shared;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class AttributeDto extends AttributeRequestDto {

	private Integer id;

	private Integer gid;

	private Integer cValueId;

	private String variableName;

	private String variableDescription;

	private String variableTypeName;

	private String locationName;

	private Boolean hasFiles;

	public AttributeDto() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public Integer getGid() {
		return this.gid;
	}

	public void setGid(final Integer gid) {
		this.gid = gid;
	}

	public Integer getcValueId() {
		return this.cValueId;
	}

	public void setcValueId(final Integer cValueId) {
		this.cValueId = cValueId;
	}

	public String getVariableDescription() {
		return this.variableDescription;
	}

	public void setVariableDescription(final String variableDescription) {
		this.variableDescription = variableDescription;
	}

	public String getLocationName() {
		return this.locationName;
	}

	public void setLocationName(final String locationName) {
		this.locationName = locationName;
	}

	public String getVariableTypeName() {
		return this.variableTypeName;
	}

	public void setVariableTypeName(final String variableTypeName) {
		this.variableTypeName = variableTypeName;
	}

	public String getVariableName() {
		return this.variableName;
	}

	public void setVariableName(final String variableName) {
		this.variableName = variableName;
	}

	public Boolean getHasFiles() {
		return hasFiles;
	}

	public void setHasFiles(final Boolean hasFiles) {
		this.hasFiles = hasFiles;
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
