package org.generationcp.middleware.domain.germplasm;

import org.generationcp.middleware.domain.shared.AttributeDto;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class GermplasmAttributeDto extends AttributeDto {

	private Integer gid;

	private Integer cValueId;

	public Integer getGid() {
		return gid;
	}

	public void setGid(Integer gid) {
		this.gid = gid;
	}

	public Integer getcValueId() {
		return this.cValueId;
	}

	public void setcValueId(final Integer cValueId) {
		this.cValueId = cValueId;
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
