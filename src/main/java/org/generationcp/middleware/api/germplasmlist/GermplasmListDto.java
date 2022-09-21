package org.generationcp.middleware.api.germplasmlist;

import org.generationcp.middleware.pojos.GermplasmList;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class GermplasmListDto extends GermplasmListBasicInfoDTO {

	private Boolean locked;
	private Integer ownerId;
	private Integer generationLevel;

	public GermplasmListDto() {

	}

	public GermplasmListDto(final GermplasmList list) {
		this.setListId(list.getId());
		this.setListName(list.getName());
	}

	public Boolean isLocked() {
		return this.locked;
	}

	public void setLocked(final Boolean locked) {
		this.locked = locked;
	}

	public Integer getOwnerId() {
		return this.ownerId;
	}

	public void setOwnerId(final Integer ownerId) {
		this.ownerId = ownerId;
	}

	public Integer getGenerationLevel() {
		return generationLevel;
	}

	public void setGenerationLevel(final Integer generationLevel) {
		this.generationLevel = generationLevel;
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
