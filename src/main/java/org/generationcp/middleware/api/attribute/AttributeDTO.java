package org.generationcp.middleware.api.attribute;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class AttributeDTO {

	private String code;
	private Integer id;
	private String name;
	private String value;

	public String getCode() {
		return this.code;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}
}
