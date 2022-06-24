package org.generationcp.middleware.api.brapi.v2.germplasm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class PedigreeNodeReferenceDTO {

	private String germplasmDbId;
	private String germplasmName;
	private String parentType;

	@JsonIgnore
	private Integer gid;

	public PedigreeNodeReferenceDTO() {

	}

	public PedigreeNodeReferenceDTO(final String germplasmDbId, final String germplasmName, final String parentType) {
		this.germplasmDbId = germplasmDbId;
		this.germplasmName = germplasmName;
		this.parentType = parentType;
	}

	public String getGermplasmDbId() {
		return this.germplasmDbId;
	}

	public void setGermplasmDbId(final String germplasmDbId) {
		this.germplasmDbId = germplasmDbId;
	}

	public String getGermplasmName() {
		return this.germplasmName;
	}

	public void setGermplasmName(final String germplasmName) {
		this.germplasmName = germplasmName;
	}

	public String getParentType() {
		return this.parentType;
	}

	public void setParentType(final String parentType) {
		this.parentType = parentType;
	}

	public Integer getGid() {
		return gid;
	}

	public void setGid(Integer gid) {
		this.gid = gid;
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
