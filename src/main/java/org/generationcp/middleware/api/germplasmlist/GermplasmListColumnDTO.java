package org.generationcp.middleware.api.germplasmlist;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class GermplasmListColumnDTO {

	private int id;
	private String name;
	private Integer typeId;
	private GermplasmListColumnType columnType;

	public GermplasmListColumnDTO() {
	}

	public GermplasmListColumnDTO(final int id, final String name, final GermplasmListColumnType columnType) {
		this.id = id;
		this.name = name;
		this.columnType = columnType;
	}

	public GermplasmListColumnDTO(final int id, final String name, final Integer typeId,
		final GermplasmListColumnType columnType) {
		this.id = id;
		this.name = name;
		this.typeId = typeId;
		this.columnType = columnType;
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

	public GermplasmListColumnType getColumnType() {
		return columnType;
	}

	public void setColumnType(final GermplasmListColumnType columnType) {
		this.columnType = columnType;
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
