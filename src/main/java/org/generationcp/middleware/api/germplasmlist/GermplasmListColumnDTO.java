package org.generationcp.middleware.api.germplasmlist;

import org.generationcp.middleware.pojos.GermplasmListColumnCategory;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class GermplasmListColumnDTO {

	private int id;
	private String name;
	private Integer typeId;
	private GermplasmListColumnCategory category;

	public GermplasmListColumnDTO() {
	}

	public GermplasmListColumnDTO(final int id, final String name, final GermplasmListColumnCategory category) {
		this.id = id;
		this.name = name;
		this.category = category;
	}

	public GermplasmListColumnDTO(final int id, final String name, final Integer typeId,
		final GermplasmListColumnCategory category) {
		this.id = id;
		this.name = name;
		this.typeId = typeId;
		this.category = category;
	}

	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(final Integer typeId) {
		this.typeId = typeId;
	}

	public GermplasmListColumnCategory getCategory() {
		return category;
	}

	public void setCategory(final GermplasmListColumnCategory category) {
		this.category = category;
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
