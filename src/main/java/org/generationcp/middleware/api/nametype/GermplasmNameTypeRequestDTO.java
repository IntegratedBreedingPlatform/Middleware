package org.generationcp.middleware.api.nametype;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class GermplasmNameTypeRequestDTO {

	private String code;
	private String name;
	private String description;

	public GermplasmNameTypeRequestDTO() {

	}

	public GermplasmNameTypeRequestDTO(final String code, final String name, final String description) {
		this.code = code;
		this.name = name;
		this.setDescription(description);

	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
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
